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
package org.apache.abdera.examples.appserver;

import java.security.MessageDigest;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.EntityTag;
import org.apache.abdera.protocol.server.provider.AbstractResponseContext;
import org.apache.abdera.protocol.server.provider.BaseResponseContext;
import org.apache.abdera.protocol.server.provider.EmptyResponseContext;
import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.protocol.server.provider.RequestContext;
import org.apache.abdera.protocol.server.provider.ResponseContext;
import org.apache.abdera.protocol.server.provider.TargetType;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.iri.IRI;
import org.apache.axiom.om.util.Base64;

public class SimpleProvider 
  implements Provider {

  private EntityTag service_etag = new EntityTag("simple");
  private Document<Service> service_doc;
  private Document<Feed> feed_doc;
  
  private Document<Service> init_service_doc(Abdera abdera) {
    Factory factory = abdera.getFactory();
    Service service = factory.newService();
    Workspace workspace = service.addWorkspace("Simple");
    try {
      Collection collection = workspace.addCollection("Simple", "atom/feed");
      collection.setAccept("entry");
      collection.addCategories().setFixed(false);
    } catch (Exception e) {}
    return service.getDocument();
  }
  
  private Document<Feed> init_feed_doc(Abdera abdera) {
    Factory factory = abdera.getFactory();
    Feed feed = factory.newFeed();
    try {
      feed.setId("tag:example.org,2006:feed");
      feed.setTitle("Simple");
      feed.setUpdated(new Date());
      feed.addLink("");
      feed.addLink("","self");
      feed.addAuthor("Simple");
    } catch (Exception e) {}
    return feed.getDocument();
  }
  
  private synchronized Document<Service> get_service_doc(Abdera abdera) {
    if (service_doc == null) {
      service_doc = init_service_doc(abdera);
    }
    return service_doc;
  }
  
  private synchronized Document<Feed> get_feed_doc(Abdera abdera) {
    if (feed_doc == null) {
      feed_doc = init_feed_doc(abdera);
    }
    return feed_doc;
  }
  
  public ResponseContext getService(
    RequestContext request) {
      Abdera abdera = request.getServiceContext().getAbdera();
      Document<Service> service = get_service_doc(abdera);
      AbstractResponseContext rc; 
      rc = new BaseResponseContext<Document<Service>>(service); 
      rc.setEntityTag(service_etag);
      return rc;
  }
  
  public ResponseContext getFeed(
    RequestContext request) {
      Abdera abdera = request.getServiceContext().getAbdera();
      Document<Feed> feed = get_feed_doc(abdera);
      AbstractResponseContext rc; 
      rc = new BaseResponseContext<Document<Feed>>(feed);
      rc.setEntityTag(calculateEntityTag(feed.getRoot()));
      return rc;
  }

  @SuppressWarnings("unchecked")
  public ResponseContext createEntry(
    RequestContext request) {
      Abdera abdera = request.getServiceContext().getAbdera();
      Factory factory = abdera.getFactory();
      Parser parser = abdera.getParser();
      try {
        MimeType contentType = request.getContentType();
        String ctype = (contentType != null) ? contentType.toString() : null;
        if (ctype != null && !MimeTypeHelper.isAtom(ctype) && !MimeTypeHelper.isXml(ctype))
          return new EmptyResponseContext(415);

        Document<Entry> entry_doc = 
          (Document<Entry>) request.getDocument(parser).clone();
        if (entry_doc != null) {
          Entry entry = entry_doc.getRoot();
          if (!isValidEntry(entry))
            return new EmptyResponseContext(400);
          entry.setUpdated(new Date());
          entry.getIdElement().setValue(factory.newUuidUri());
          entry.addLink("feed/" + entry.getId().toString(), "edit");
          Feed feed = get_feed_doc(abdera).getRoot();
          feed.insertEntry(entry);
          feed.setUpdated(new Date());
          BaseResponseContext rc = new BaseResponseContext(entry);
          IRI baseUri = resolveBase(request);
          rc.setLocation(baseUri.resolve(entry.getEditLinkResolvedHref()).toString());
          rc.setContentLocation(rc.getLocation().toString());
          rc.setEntityTag(calculateEntityTag(entry));
          rc.setStatus(201);
          return rc;
        } else {
          return new EmptyResponseContext(400);
        }
      } catch (ParseException pe) {
        return new EmptyResponseContext(415);
      } catch (ClassCastException cce) {
        return new EmptyResponseContext(415);
      } catch (Exception e) {
        return new EmptyResponseContext(400);
      }
  }
  
  private IRI resolveBase(RequestContext request) {
    return request.getBaseUri().resolve(request.getUri());
  }
  
  public ResponseContext deleteEntry(
    RequestContext request) {
      Entry entry = getAbderaEntry(request);
      if (entry != null)
        entry.discard();
      return new EmptyResponseContext(204);
  }

  public ResponseContext getEntry(
    RequestContext request) {
      Entry entry = (Entry) getAbderaEntry(request);
      if (entry != null) {
        Feed feed = entry.getParentElement();
        entry = (Entry) entry.clone();
        entry.setSource(feed.getAsSource());
        Document<Entry> entry_doc = entry.getDocument();
        AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(entry_doc);
        rc.setEntityTag(calculateEntityTag(entry));
        return rc;
      } else {
        return new EmptyResponseContext(404);
      }
  }

  @SuppressWarnings("unchecked")
  public ResponseContext updateEntry(
    RequestContext request) {
      Abdera abdera = request.getServiceContext().getAbdera();
      Parser parser = abdera.getParser();
      Factory factory = abdera.getFactory();
      Entry orig_entry = getAbderaEntry(request);
      if (orig_entry != null) {
        try {
          MimeType contentType = request.getContentType();
          if (contentType != null && !MimeTypeHelper.isAtom(contentType.toString()))
            return new EmptyResponseContext(415);
          
          Document<Entry> entry_doc = 
            (Document<Entry>) request.getDocument(parser).clone();
          if (entry_doc != null) {
            Entry entry = entry_doc.getRoot();
            if (!entry.getId().equals(orig_entry.getId()))
              return new EmptyResponseContext(409);
            if (!isValidEntry(entry))
              return new EmptyResponseContext(400);
            entry.setUpdated(new Date());
            entry.getIdElement().setValue(factory.newUuidUri());
            entry.addLink("atom/feed/" + entry.getId().toString(), "edit");
            orig_entry.discard();
            Feed feed = get_feed_doc(abdera).getRoot();
            feed.insertEntry(entry);
            feed.setUpdated(new Date());
            return new EmptyResponseContext(204);
          } else {
            return new EmptyResponseContext(400);
          }
        } catch (ParseException pe) {
          return new EmptyResponseContext(415);
        } catch (ClassCastException cce) {
          return new EmptyResponseContext(415);
        } catch (Exception e) {
          return new EmptyResponseContext(400);
        }
      } else {
        return new EmptyResponseContext(404);
      }
  }

  private EntityTag calculateEntityTag(Base base) {
    try {
      String id = null;
      String modified = null;
      if (base instanceof Entry) {
        id = ((Entry)base).getId().toString();
        modified = ((Entry)base).getUpdatedElement().getText();
      } else if (base instanceof Feed) {
        id = ((Feed)base).getId().toString();
        modified = ((Feed)base).getUpdatedElement().getText();
      }
      String tag = id + ":" + modified;
      byte[] digest = MessageDigest.getInstance("sha1").digest(tag.getBytes());
      String etag = Base64.encode(digest);
      return new EntityTag(etag);
    } catch (Exception e) {
      // Not going to happen
    }
    return null;
  }
  
  private Entry getAbderaEntry(RequestContext request) {
    Abdera abdera = request.getServiceContext().getAbdera();
    String entry_id = getEntryID(request);
    Document<Feed> feed = get_feed_doc(abdera);
    try { 
      return feed.getRoot().getEntry(entry_id); 
    } catch (Exception e) {}
    return null;
  }
  
  private String getEntryID(RequestContext request) {
    if (request.getTarget().getType() != TargetType.TYPE_ENTRY) 
      return null;
    String path = request.getUri().toString();
    String[] segments = path.split("/");
    return segments[segments.length-1];
  }
  
  public ResponseContext entryPost(
    RequestContext request) {
      return new EmptyResponseContext(403);
  }
  
  public ResponseContext deleteMedia(
    RequestContext request) {
      throw new UnsupportedOperationException();
  }
  
  public ResponseContext mediaPost(
    RequestContext request) {
      throw new UnsupportedOperationException();
  }
  
  public ResponseContext getMedia(
    RequestContext request) {
      throw new UnsupportedOperationException();
  }
  
  public ResponseContext updateMedia(
    RequestContext request) {
      throw new UnsupportedOperationException();
  }

  private boolean isValidEntry(Entry entry) {
    try {
      if (entry.getId() == null || 
          entry.getId().toString().length() == 0) return false;
      if (entry.getTitle() == null) return false;
      if (entry.getAuthor() == null) return false;
      if (entry.getUpdated() == null) return false;
      if (entry.getContent() == null) {
        if (entry.getAlternateLink() == null) return false;
        if (entry.getSummary() == null) return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  public ResponseContext getCategories(RequestContext request) {
    return null;
  }

}
