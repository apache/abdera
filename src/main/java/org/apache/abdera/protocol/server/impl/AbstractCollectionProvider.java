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
package org.apache.abdera.protocol.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractCollectionProvider<T> extends ProviderSupport
  implements CollectionProvider {
  private final static Log log = LogFactory.getLog(AbstractCollectionProvider.class);
  
  private String baseMediaIri = "media/";
  
  public ResponseContext createEntry(RequestContext request) {
    try {
      MimeType contentType = request.getContentType();
      if (isMediaType(contentType))
        return createMediaEntry(request);
      else
        return createNonMediaEntry(request);
    } catch (ParseException pe) {
      return new EmptyResponseContext(415);
    } catch (ClassCastException cce) {
      return new EmptyResponseContext(415);
    } catch (IOException e) {
      log.warn(e.getMessage(), e);
      return new EmptyResponseContext(500);
    }
  }

  protected boolean isMediaType(MimeType contentType) {
    String ctype = (contentType != null) ? contentType.toString() : null;
    return ctype != null && !MimeTypeHelper.isAtom(ctype) /*&& !MimeTypeHelper.isXml(ctype)*/;
  }
  
  public abstract T createEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext request) throws ResponseContextException;
  
  public T createMediaEntry(MimeType mimeType, String slug, InputStream inputStream, RequestContext request) throws ResponseContextException {
    throw new UnsupportedOperationException();
  }
  
  public ResponseContext deleteEntry(RequestContext request) {
    String id = getEntryID(request);
    if (id != null) {

      try {
        deleteEntry(id, request);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
      
      return new EmptyResponseContext(204);
    } else {
      // TODO: is this right?
      return new EmptyResponseContext(404);
    }
  }

  public abstract void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException;

  public abstract String getAuthor() throws ResponseContextException;

  public List<Person> getAuthors(T entry, RequestContext request) throws ResponseContextException {
    return null;
  }
  
  public String getBaseMediaIri() {
    return baseMediaIri;
  }
  
  public abstract Object getContent(T entry, RequestContext request) throws ResponseContextException;
  
  // GET, POST, PUT, DELETE
  
  public String getContentType(T entry) {
    throw new UnsupportedOperationException();
  }
  
  public abstract Iterable<T> getEntries(RequestContext request) throws ResponseContextException;
  
  public ResponseContext getEntry(RequestContext request, IRI entryBaseIri) {
    try {
      Entry entry = getEntryFromCollectionProvider(entryBaseIri,
                                                   request);
      if (entry != null) {
        return buildGetEntryResponse(request, entry);
      } else {
        return new EmptyResponseContext(404);
      }
    } catch (ResponseContextException e) {
      return createErrorResponse(e);
    }
  }

  protected ResponseContext buildGetEntryResponse(RequestContext request, Entry entry) {
    Feed feed = createFeedBase(request);
    entry.setSource(feed.getAsSource());
    Document<Entry> entry_doc = entry.getDocument();
    AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(entry_doc);
    rc.setEntityTag(calculateEntityTag(entry));
    return rc;
  }
  
  public abstract T getEntry(String resourceName, RequestContext request) throws ResponseContextException;

  public ResponseContext getFeed(RequestContext request) {
    Feed feed = createFeedBase(request);
    
    return getFeed(feed, request);
  }
  
  public ResponseContext getFeed(Feed feed, RequestContext request) {
    try {
      addFeedDetails(feed, request);
      
      return buildGetFeedResponse(feed);
    } catch (ResponseContextException e) {
      return createErrorResponse(e);
    }
  }

  protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
    feed.setUpdated(new Date());

    IRI baseIri = resolveBase(request);
    IRI entryIri = getEntryBaseFromFeedIRI(baseIri);
    
    Iterable<T> entries = getEntries(request);
    if (entries != null) {
      for (T entryObj : entries) {
        Entry e = feed.addEntry();
  
        addEntryDetails(request, e, entryIri, entryObj);

        if (isMediaEntry(entryObj)) {
          addMediaContent(entryIri, e, entryObj);
        } else {
          addContent(e, entryObj, request);
        }
      }
    }
  }

  protected ResponseContext buildGetFeedResponse(Feed feed) {
    Document<Feed> document = feed.getDocument();
    AbstractResponseContext rc = new BaseResponseContext<Document<Feed>>(document);
    rc.setEntityTag(calculateEntityTag(document.getRoot()));
    return rc;
  }

  /**
   * Create a ResponseContext (or take it from the Exception) for an
   * exception that occurred in the application.
   * @param e
   * @return
   */
  protected ResponseContext createErrorResponse(ResponseContextException e) {
    if (log.isInfoEnabled()) {
      log.info("A ResponseException was thrown.", e);
    } else if (e.getResponseContext() instanceof EmptyResponseContext 
      && ((EmptyResponseContext) e.getResponseContext()).getStatus() >= 500) {
      log.warn("A ResponseException was thrown.", e);
    }
    
    return e.getResponseContext();
  }

  public abstract String getId(RequestContext request);
  
  /**
   * Gets the UUID for the specified entry.
   * @param entry
   * @return
   */
  public abstract String getId(T entry) throws ResponseContextException;

  public ResponseContext getMedia(RequestContext request) {
    try {
      String id = getEntryID(request);
      T entryObj = getEntry(id, request);

      if (entryObj == null) {
        return new EmptyResponseContext(404);
      }

      return buildGetMediaResponse(entryObj);
    } catch (ParseException pe) {
      return new EmptyResponseContext(415);
    } catch (ClassCastException cce) {
      return new EmptyResponseContext(415);
    } catch (ResponseContextException e) {
      return e.getResponseContext();
    } catch (Exception e) {
      log.warn(e.getMessage(), e);
      return new EmptyResponseContext(400);
    }
  }

  protected ResponseContext buildGetMediaResponse(T entryObj) throws ResponseContextException {
    MediaResponseContext ctx = new MediaResponseContext(getMediaStream(entryObj), 
                                                        getUpdated(entryObj), 
                                                        200);
    ctx.setContentType(getContentType(entryObj));

    return ctx;
  }
  public String getMediaName(T entry) throws ResponseContextException {
    throw new UnsupportedOperationException();
  }

  public InputStream getMediaStream(T entry) throws ResponseContextException {
    throw new UnsupportedOperationException();
  }
  
  public abstract String getName(T entry) throws ResponseContextException;

  public abstract String getTitle(RequestContext request);

  public abstract String getTitle(T entry) throws ResponseContextException;

  public abstract Date getUpdated(T entry) throws ResponseContextException;

  public boolean isMediaEntry(T entry) throws ResponseContextException {
    return false;
  }

  public void setBaseMediaIri(String baseMediaIri) {
    this.baseMediaIri = baseMediaIri;
  }

  public ResponseContext updateEntry(RequestContext request, IRI feedIri) {
    try {
      String id = getEntryID(request);
      T entryObj = getEntry(id, request);
      
      if (entryObj == null) {
        return new EmptyResponseContext(404);
      }
      
      Entry orig_entry = getEntryFromCollectionProvider(entryObj, feedIri, request);
      if (orig_entry != null) {

        MimeType contentType = request.getContentType();
        if (contentType != null && !MimeTypeHelper.isAtom(contentType.toString()))
          return new EmptyResponseContext(415);

        Entry entry = getEntryFromRequest(request);
        if (entry != null) {
          if (!entry.getId().equals(orig_entry.getId()))
            return new EmptyResponseContext(409);

          if (!isValidEntry(entry))
            return new EmptyResponseContext(400);

          updateEntry(entryObj, entry.getTitle(), new Date(), entry.getAuthors(), 
                      entry.getSummary(), entry.getContentElement(), request);
          return new EmptyResponseContext(204);
        } else {
          return new EmptyResponseContext(400);
        }
      } else {
        return new EmptyResponseContext(404);
      }
    } catch (ResponseContextException e) {
      return createErrorResponse(e);
    } catch (ParseException pe) {
      return new EmptyResponseContext(415);
    } catch (ClassCastException cce) {
      return new EmptyResponseContext(415);
    } catch (Exception e) {
      log.warn(e.getMessage(), e);
      return new EmptyResponseContext(400);
    }
    
  }

  public abstract void updateEntry(T entry, String title, Date updated, 
                                   List<Person> authors, String summary, 
                                   Content content, RequestContext request) throws ResponseContextException;

  protected void addContent(Entry e, T doc, RequestContext request) throws ResponseContextException {
    Object content = getContent(doc, request);

    if (content instanceof Content) {
      e.setContentElement((Content)content);
    } else if (content instanceof String) {
      e.setContent((String)content);
    }
  }

  protected void addEntryDetails(RequestContext request, Entry e, 
                               IRI entryBaseIri, T entryObj) throws ResponseContextException {
    IRI entryIri = entryBaseIri.resolve(getName(entryObj));
    e.addLink(entryIri.toString(), "edit");
    e.setId(getId(entryObj));
    e.setTitle(getTitle(entryObj));
    e.setUpdated(getUpdated(entryObj));
    
    List<Person> authors = getAuthors(entryObj, request);
    if (authors != null) {
      for (Person a : authors) {
        e.addAuthor(a);
      }
    }
    
    Text t = getSummary(entryObj, request);
    if (t != null) {
      e.setSummaryElement(t);
    }
  }

  public Text getSummary(T entry, RequestContext request) throws ResponseContextException {
    return null;
  }

  protected void addMediaContent(IRI entryBaseIri, Entry entry, T doc) throws ResponseContextException {
    String name = getMediaName(doc);
    IRI mediaIri = getMediaIRI(entryBaseIri, name);
    mediaIri = entryBaseIri.resolve(mediaIri);

    entry.setContent(mediaIri, getContentType(doc));
    entry.addLink(mediaIri.toString(), "edit-media");
  }

  protected EntityTag calculateEntityTag(Base base) {
    String id = null;
    String modified = null;
    if (base instanceof Entry) {
      id = ((Entry)base).getId().toString();
      modified = ((Entry)base).getUpdatedElement().getText();
    } else if (base instanceof Feed) {
      id = ((Feed)base).getId().toString();
      modified = ((Feed)base).getUpdatedElement().getText();
    }
    return EntityTag.generate(id, modified);
  }
  
  protected Feed createFeedBase(RequestContext request) {
    Factory factory = request.getAbdera().getFactory();
    Feed feed = factory.newFeed();
    try {
      feed.setId(getId(request));
      feed.setTitle(getTitle(request));
      feed.addLink("");
      feed.addLink("", "self");
      feed.addAuthor(getAuthor());
    } catch (Exception e) {
    }
    return feed;
  }

  protected ResponseContext createMediaEntry(RequestContext request) {
    try {
      T doc = createMediaEntry(request.getContentType(), request.getSlug(), 
                               request.getInputStream(), request);

      IRI baseIri = resolveBase(request);
      IRI entryIri = getEntryBaseFromFeedIRI(baseIri);

      Entry entry = request.getAbdera().getFactory().newEntry();

      addEntryDetails(request, entry, entryIri, doc);

      addMediaContent(entryIri, entry, doc);

      return buildCreateMediaEntryResponse(entryIri, entry);
    } catch (IOException e) {
      return new EmptyResponseContext(500);
    } catch (ResponseContextException e) {
      return createErrorResponse(e);
    }
  }

  protected ResponseContext buildCreateMediaEntryResponse(IRI entryIri, Entry entry) {
    return buildCreateEntryResponse(entryIri, entry);
  }

  protected ResponseContext buildCreateEntryResponse(IRI entryIri, Entry entry) {
    BaseResponseContext<Entry> rc = new BaseResponseContext<Entry>(entry);
    rc.setLocation(entryIri.resolve(entry.getEditLinkResolvedHref()).toString());
    rc.setContentLocation(rc.getLocation().toString());
    rc.setEntityTag(calculateEntityTag(entry));
    rc.setStatus(201);
    return rc;
  }

  protected ResponseContext createNonMediaEntry(RequestContext request)
    throws IOException {
    Entry entry = getEntryFromRequest(request);
    if (entry != null) {
      if (!isValidEntry(entry))
        return new EmptyResponseContext(400);

      entry.setUpdated(new Date());
      
      try {
        T entryObj = createEntry(entry.getTitle(),
                                 entry.getId(),
                                 entry.getSummary(),
                                 entry.getUpdated(),
                                 entry.getAuthors(),
                                 entry.getContentElement(), request);
        entry.getIdElement().setValue(getId(entryObj));
      
        IRI entryBaseUri = getEntryBaseFromFeedIRI(resolveBase(request));
        
        IRI entryIri = entryBaseUri.resolve(getName(entryObj));
        entry.addLink(entryIri.toString(), "edit");
  
        return buildCreateEntryResponse(entryIri, entry);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
    } else {
      return new EmptyResponseContext(400);
    }
  }

  protected IRI getEntryBaseFromFeedIRI(IRI baseIri) {
    return new IRI(baseIri.toString() + "/");
  }

  protected Entry getEntryFromCollectionProvider(IRI feedIri, RequestContext request) throws ResponseContextException {

    String id = getEntryID(request);
    T entryObj = getEntry(id, request);

    if (entryObj == null) {
      return null;
    }

    return getEntryFromCollectionProvider(entryObj, feedIri, request);
  }

  private Entry getEntryFromCollectionProvider(T entryObj, IRI feedIri, RequestContext request)
    throws ResponseContextException {
    Abdera abdera = request.getAbdera();
    Factory factory = abdera.getFactory();
    Entry entry = factory.newEntry();

    return buildEntry(entryObj, entry, feedIri, request);
  }

  private Entry buildEntry(T entryObj, Entry entry, IRI feedIri, RequestContext request)
    throws ResponseContextException {
    addEntryDetails(request, entry, feedIri, entryObj);

    if (isMediaEntry(entryObj)) {
      addMediaContent(feedIri, entry, entryObj);
    } else {
      addContent(entry, entryObj, request);
    }

    return entry;
  }

  @SuppressWarnings("unchecked")
  protected Entry getEntryFromRequest(RequestContext request) throws IOException {
    Abdera abdera = request.getAbdera();
    Parser parser = abdera.getParser();

    Document<Entry> entry_doc = (Document<Entry>)request.getDocument(parser).clone();
    if (entry_doc == null) {
      return null;
    }
    return entry_doc.getRoot();
  }

  protected String getEntryID(RequestContext request) {
    String path = request.getTargetPath();
    int q = path.indexOf("?");
    if (q != -1) {
      path = path.substring(0, q);
    }
    String[] segments = path.split("/");
    String id = segments[segments.length - 1];
    return UrlEncoding.decode(id);
  }

  protected IRI getMediaIRI(IRI entryBaseIri, String name) {
    return entryBaseIri.resolve(getBaseMediaIri()).resolve(name);
  }

  @Override
  protected IRI resolveBase(RequestContext request) {
    IRI uri = request.getUri();
    String q = uri.getQuery();
    if (q != null && !"".equals(q)) {
      String iriStr = uri.toString();
      int idx = iriStr.indexOf('?');
      uri = new IRI(iriStr.substring(0, idx));
    }
    
    IRI resolved = request.getBaseUri().resolve(uri);
    
    return resolved;
  }

  public void begin(RequestContext request) throws ResponseContextException {
    // TODO Auto-generated method stub
    
  }

  public void end(RequestContext request, ResponseContext response) {
    // TODO Auto-generated method stub
    
  }
}
