/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.abdera.ext.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.history.FeedPagingHelper;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.Client;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.CommonsClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.util.EntityTag;

/**
 * A rudimentary, and currently incomplete editor client.  Encapsulates the
 * use of the client module apis to interact with APP servers. 
 */
public class Editor {

  private final Abdera abdera;
  private final Service service;
  private final Client client;
  private Workspace workspace;
  private Collection collection;
  private Document<Feed> feeddoc;
  
  public Editor(
    String serviceDocument) {
    this(new Abdera(), serviceDocument);
  }
  
  public Editor(
    Service service) {
      this(
        service.getFactory().getAbdera(), 
        service);
  }
  
  public Editor(
    Abdera abdera,
    String serviceDocument) {
    this.abdera = abdera;
    this.client = new CommonsClient(abdera);
    Document<Service> servicedoc = 
      getDocument(
        client, 
        abdera, 
        serviceDocument);
    this.service = servicedoc.getRoot();
  }
  
  public Editor(
    Abdera abdera,
    Service service) {
      this.abdera = abdera;
      this.service = service;
      this.client = new CommonsClient(abdera);
  }
  
  private static <T extends Element>Document<T> getDocument(
    Client client,
    Abdera abdera, 
    String document) {
    
    ClientResponse resp = client.get(document);
    switch(resp.getType()) {
      case SUCCESS:
        Document<T> doc = resp.getDocument();
        doc.setLastModified(resp.getLastModified());
        doc.setEntityTag(resp.getEntityTag());
        try {
          doc.setContentType(resp.getContentType().toString());
        } catch (Exception e) {}
        return doc;
      case REDIRECTION:
        throw new RedirectionException(resp.getStatus(),resp.getLocation());
      case CLIENT_ERROR:
        throw new ClientException(resp.getStatus(),resp.getStatusText());
      case SERVER_ERROR:
        throw new ServerException(resp.getStatus(),resp.getStatusText());
    }
    
    return null;
  }
  
  public Abdera getAbdera() {
    return abdera;
  }
  
  public Service getService() {
    return service;
  }
  
  public Client getClient() {
    return client;
  }
  
  public List<Workspace> getWorkspaces() {
    return service.getWorkspaces();
  }
  
  public void setCurrentWorkspace(Workspace workspace) {
    this.workspace = workspace;
  }
  
  public Workspace getCurrentWorkspace() {
    return this.workspace;
  }
  
  public List<Collection> getCollections() {
    if (this.workspace == null)
      throw new IllegalStateException("Current workspace is not set");
    return this.workspace.getCollections();
  }
  
  public void setCurrentCollection(Collection collection) {
    this.collection = collection;
  }
  
  public Collection getCurrentCollection() {
    return collection;
  }
  
  public Feed getFeed()  {
      Collection collection = getCurrentCollection();
      if (collection == null)
        throw new IllegalStateException(
          "Current collection is not set");
      if (this.feeddoc == null) {
        this.feeddoc = 
          getDocument(
            client, 
            abdera, 
            collection.getResolvedHref().toString());
      }
      return this.feeddoc.getRoot();
  }
  
  public boolean hasNextPage() {
    return getNextPage() != null;
  }
  
  public IRI getNextPage() {
    IRI nextPage = null;
    try {
      Feed feed = getFeed();
      if (feed != null) {
        nextPage = FeedPagingHelper.getNext(feed);
      }
    } catch (Exception e) {}
    return nextPage;
  }
  
  public void nextPage()  {
      IRI np = getNextPage();
      if (np != null) {
        this.feeddoc = 
          getDocument(
            client, 
            abdera, 
            np.toString());
      }
  }
  
  public boolean hasPreviousPage() {
    return getPreviousPage() != null;
  }
  
  public IRI getPreviousPage() {
    IRI prevPage = null;
    try {
      Feed feed = getFeed();
      if (feed != null) {
        prevPage = FeedPagingHelper.getPrevious(feed);
      }
    } catch (Exception e) {}
    return prevPage;
  }
  
  public void previousPage()  {
      IRI np = getPreviousPage();
      if (np != null) {
        this.feeddoc = 
          getDocument(
            client, 
            abdera, 
            np.toString());
      }
  }
  
  public List<Entry> getEntries() {
      Feed feed = getFeed();
      if (feed == null) 
        throw new IllegalStateException(
          "Current collection is not set");
      return feed.getEntries();
  }
  
  public EntryEditor editEntry(
    Entry entry) {
    return new EntryEditor(entry);
  }
  
  public class MediaEditor {
    
    private final IRI editMediaLink;
    private InputStream media;
    private EntityTag etag;
    private Date lastModified;
    private MimeType contentType;
    
    public MediaEditor(IRI editMediaLink) 
      throws IOException {
      this.editMediaLink = editMediaLink;
      getMedia(this.editMediaLink);
    }
    
    private void getMedia(IRI editMediaLink) 
      throws IOException {
      ClientResponse resp = client.get(editMediaLink.toString());
      switch(resp.getType()) {
        case SUCCESS:
          this.media = resp.getInputStream();
          this.etag = resp.getEntityTag();
          this.lastModified = resp.getLastModified();
          try {
            this.contentType = resp.getContentType();
          } catch (Exception e) {
            try {
              this.contentType = new MimeType("application/octet-stream");
            } catch (Exception ex) {}
          }
          break;
        case REDIRECTION:
          throw new RedirectionException(resp.getStatus(),resp.getLocation());
        case CLIENT_ERROR:
          throw new ClientException(resp.getStatus(),resp.getStatusText());
        case SERVER_ERROR:
          throw new ServerException(resp.getStatus(),resp.getStatusText());
      }
    }
    
    public InputStream getMediaStream() {
      return media;
    }
    
    public void setMediaStream(InputStream media) {
      this.media = media;
    }
    
    public MimeType getContentType() {
      return this.contentType;
    }
    
    public void setContentType(MimeType contentType) {
      this.contentType = contentType;
    }
    
    public boolean delete() {
      RequestOptions options = client.getDefaultRequestOptions();
      if (etag != null) {
        options.setIfMatch(etag.toString());
      }
      if (lastModified != null && etag == null) {
        options.setIfUnmodifiedSince(lastModified);
      }
      ClientResponse resp = client.delete(editMediaLink.toString(),options);
      switch(resp.getType()) {
        case SUCCESS:
          return true;
        case REDIRECTION:
          throw new RedirectionException(resp.getStatus(),resp.getLocation());
        case CLIENT_ERROR:
          throw new ClientException(resp.getStatus(),resp.getStatusText());
        case SERVER_ERROR:
          throw new ServerException(resp.getStatus(),resp.getStatusText());
        default:
          return false;
      }
    }
    
    public boolean save() {
      RequestOptions options = client.getDefaultRequestOptions();
      if (etag != null) {
        options.setIfMatch(etag.toString());
      }
      if (lastModified != null && etag == null) {
        options.setIfUnmodifiedSince(lastModified);
      }
      if (contentType != null) options.setContentType(contentType);
      ClientResponse resp = client.put(editMediaLink.toString(), media, options);
      switch(resp.getType()) {
        case SUCCESS:
          return true;
        case REDIRECTION:
          throw new RedirectionException(resp.getStatus(),resp.getLocation());
        case CLIENT_ERROR:
          throw new ClientException(resp.getStatus(),resp.getStatusText());
        case SERVER_ERROR:
          throw new ServerException(resp.getStatus(),resp.getStatusText());
        default:
          return false;
      }
    }
    
    public void refresh() 
      throws MimeTypeParseException, 
             IOException {
      getMedia(editMediaLink);
    }

  }
  
  public class EntryEditor {
    
    private final IRI editLink;
    private final IRI editMediaLink;
    private Document<Entry> entryDoc;
    
    public EntryEditor(Entry entry) {
      this.editLink = entry.getEditLinkResolvedHref();
      this.editMediaLink = entry.getEditMediaLinkResolvedHref();
      entryDoc = getDocument(client,abdera,this.editLink.toString());
    }
    
    public Entry getEntry() {
      return this.entryDoc.getRoot();
    }
    
    public boolean isMediaLinkEntry() {
      return this.editMediaLink != null;
    }
    
    public MediaEditor editMedia() 
      throws IOException {
      if (this.editMediaLink == null) 
        throw new IllegalStateException("This is not a media link entry");
      return new MediaEditor(this.editMediaLink);
    }
    
    public boolean delete() {
      RequestOptions options = client.getDefaultRequestOptions();
      EntityTag etag = entryDoc.getEntityTag();
      Date lm = entryDoc.getLastModified();
      if (etag != null) {
        options.setIfMatch(etag.toString());
      }
      if (lm != null && etag == null) {
        options.setIfUnmodifiedSince(lm);
      }
      ClientResponse resp = client.delete(editLink.toString(),options);
      switch(resp.getType()) {
        case SUCCESS:
          return true;
        case REDIRECTION:
          throw new RedirectionException(resp.getStatus(),resp.getLocation());
        case CLIENT_ERROR:
          throw new ClientException(resp.getStatus(),resp.getStatusText());
        case SERVER_ERROR:
          throw new ServerException(resp.getStatus(),resp.getStatusText());
        default:
          return false;
      }
    }
    
    public boolean save() {
      RequestOptions options = client.getDefaultRequestOptions();
      EntityTag etag = entryDoc.getEntityTag();
      Date lm = entryDoc.getLastModified();
      if (etag != null) {
        options.setIfMatch(etag.toString());
      }
      if (lm != null && etag == null) {
        options.setIfUnmodifiedSince(lm);
      }
      ClientResponse resp = client.put(editLink.toString(), entryDoc, options);
      switch(resp.getType()) {
        case SUCCESS:
          return true;
        case REDIRECTION:
          throw new RedirectionException(resp.getStatus(),resp.getLocation());
        case CLIENT_ERROR:
          throw new ClientException(resp.getStatus(),resp.getStatusText());
        case SERVER_ERROR:
          throw new ServerException(resp.getStatus(),resp.getStatusText());
        default:
          return false;
      }
    }
    
    public void refresh() {
      entryDoc = getDocument(
        client, 
        abdera, 
        editLink.toString());
    }
  }
  
  private static class RedirectionException 
    extends RuntimeException {
    private static final long serialVersionUID = -599273256698050968L;
    private final int code;
    private final IRI location;
    public RedirectionException(int code,IRI location) {
      this.code = code;
      this.location = location;
    }
    public int getCode() {
      return code;
    }
    public IRI getLocation() {
      return location;
    }
  }
  
  private static class ClientException
    extends RuntimeException {
    private static final long serialVersionUID = 8691746591711026788L;
    private final int code;
    private final String text;
    public ClientException(int code, String text) {
      this.code = code;
      this.text = text;
    }
    public int getCode() {
      return code;
    }
    public String getText() {
      return text;
    }
  }
  
  private static class ServerException
  extends RuntimeException {
  private static final long serialVersionUID = -6536219420389801294L;
  private final int code;
  private final String text;
  public ServerException(int code, String text) {
    this.code = code;
    this.text = text;
  }
  public int getCode() {
    return code;
  }
  public String getText() {
    return text;
  }
}
}
