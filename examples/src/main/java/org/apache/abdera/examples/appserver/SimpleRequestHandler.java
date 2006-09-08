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

import java.io.IOException;
import java.util.Date;

import javax.activation.MimeTypeParseException;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException;
import org.apache.abdera.protocol.server.target.Target;
import org.apache.abdera.protocol.server.util.AbstractRequestHandler;
import org.apache.abdera.protocol.server.util.BaseResponseContext;
import org.apache.abdera.protocol.server.util.EmptyResponseContext;
import org.apache.abdera.protocol.server.util.ResourceType;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;

/**
 * <p>RequestHandlers are the components that actually do the work in the APP
 * server.  That is, the RequestHandler is responsible for interfacing with
 * the back end data storage and translating the APP GET, POST, PUT, DELETE
 * operations into operations on the data store, and for parsing the Atom 
 * posts into something the datastore can understand.</p>
 * 
 * <p>The AbstractRequestHandler helper class makes things a bit easier by
 * providing hooks for checking whether or not the requested resource has
 * been modified (to support If-Modified-Since), checking to see if the resource
 * exists (to support 404 or 410 responses), validating the request (to support
 * 400 Bad Request responses), etc.</p>
 * 
 * <p>RequestHandler implementations SHOULD be thread safe</p>
 */
public class SimpleRequestHandler 
  extends AbstractRequestHandler {

  private final AbderaServer abderaServer;
  
  public SimpleRequestHandler(AbderaServer abderaServer) {
    this.abderaServer = abderaServer;
  }
  
  @Override
  protected ResourceType getResourceType(RequestContext requestContext) {
    Target target = requestContext.getTarget();
    return (target != null && target.getResourceType() != null) ? 
      target.getResourceType() : ResourceType.UNKNOWN;
  }

  @Override
  protected ResponseContext createResponseContext() {
    return null;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected ResponseContext internalInvoke(
    RequestContext requestContext,
    ResponseContext responseContext) 
      throws AbderaServerException {
    Target target = requestContext.getTarget();
    String method = requestContext.getMethod();
    if (target != null) {
      ResourceType type = target.getResourceType();
      if (type == ResourceType.SERVICE) {
          try {
            if (requestContext.getIfNoneMatch().equals("\"service\"")) 
              throw new AbderaServerException(
                AbderaServerException.Code.NOTMODIFIED, 
                "Not Modified", "");
          } catch (NullPointerException npe) {}
          return getServiceDocument();
      } else if (type == ResourceType.COLLECTION) {
          if (method.equals("GET")) {
            if (!target.getValue(1).equals("foo")) 
              throw new AbderaServerException(
                AbderaServerException.Code.NOTFOUND, 
                "Not Found", "");
            return getFeedDocument();
          } else if (method.equals("POST")) {
            try {
              Document<Entry> entry_doc = 
                getEntryFromRequestContext(requestContext);
              if (entry_doc != null) {
                Entry entry = (Entry) entry_doc.getRoot().clone();
                if (!isValidEntry(entry))
                  throw new AbderaServerException(
                    AbderaServerException.Code.BADREQUEST, "Invalid Entry", "");
                entry.setUpdated(new Date());
                entry.setModified(entry.getUpdated());
                entry.getIdElement().setValue(FOMHelper.generateUuid());
                entry.addLink("foo/" + entry.getId().toString(), "edit");
                getFeed().getRoot().insertEntry((Entry) entry.clone());
                BaseResponseContext rc = getEntryDocument(entry);
                rc.setStatus(201);
                String elink = entry.getEditLink().getHref().toString();
                rc.setLocation(elink);
                rc.setContentLocation(elink);
                return rc;
              } else {
                throw new AbderaServerException(
                  AbderaServerException.Code.UNSUPPORTEDMEDIATYPE, 
                  "Unsupported Media Type", "");
              }
            } catch (AbderaServerException e) {
              throw e;
            } catch (Exception e) {
              throw new AbderaServerException(e);
            }
          }
      } else if (type == ResourceType.ENTRY_EDIT) {
          Entry entry = getEntryFromFeed(requestContext);
          if (entry == null)
            throw new AbderaServerException(
              AbderaServerException.Code.NOTFOUND, "Not Found", "");
          if (method.equals("GET")) {
            Entry e = (Entry) entry.clone();
            return new BaseResponseContext(e.getDocument());
          } else if (method.equals("PUT")) {
            try {
              Document<Entry> entry_doc = getEntryFromRequestContext(requestContext);
              if (entry_doc == null) 
                throw new AbderaServerException(
                  AbderaServerException.Code.UNSUPPORTEDMEDIATYPE, 
                  "Unsupported Media Type", "");
              Document<Feed> feed_doc = getFeed();
              Entry e = (Entry) entry_doc.getRoot().clone();
              // check to see if it's a valid atom entry
              if (!isValidEntry(entry))
                throw new AbderaServerException(
                  AbderaServerException.Code.BADREQUEST, "Invalid Entry", "");
              // check to see if the atom:id matches
              if (!e.getId().equals(entry.getId()))
                throw new AbderaServerException(
                  AbderaServerException.Code.CONFLICT, "Conflict. Cannot change atom:id", "");
              // override atom:updated 
              e.setUpdated(new Date());
              // set the app:modified date
              e.setModified(e.getUpdated());
              // make sure the edit link stays the same
              Link editLink = e.getEditLink();
              if (editLink == null) 
                e.addLink("foo/" + e.getId().toString(), "edit");
              else editLink.setHref("foo/" + e.getId().toString());
              // discard the original
              entry.discard();
              // insert the new
              feed_doc.getRoot().insertEntry((Entry) e.clone());
              return new EmptyResponseContext(204);
            } catch (Exception e) {
              throw new AbderaServerException(e);
            }
          } else if (method.equals("DELETE")) {
            entry.discard();
            return new EmptyResponseContext(204);
          }
      }
      return null;
    } else {
      throw new AbderaServerException(
        AbderaServerException.Code.NOTFOUND, 
        "Not Found", "");
    }
  }
  
  private Entry getEntryFromFeed(RequestContext context) {
    try {
      Target target = context.getTarget();
      Document<Feed> doc_feed = getFeed();
      String id = target.getValue(2);
      return doc_feed.getRoot().getEntry(id);
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private Document<Entry> getEntryFromRequestContext(
    RequestContext context) 
      throws IOException, 
             MimeTypeParseException {
    String ctype = context.getContentType().toString();
    if (ctype != null && ctype.length() != 0 &&
        (!MimeTypeHelper.isMatch(ctype, "application/atom+xml") &&
         !MimeTypeHelper.isMatch(ctype, "application/xml"))) {
      return null;
    }
    // might still be an atom entry, let's try it
    try {
      Parser parser = getParser();
      Document doc = parser.parse(context.getInputStream());
      Element root = doc.getRoot();
      if (root != null && root.getQName().equals(Constants.ENTRY)) {
        return doc;
      }
    } catch (Exception e) {}
    return null;
  }
  
  private boolean isValidEntry(Entry entry) {
    try {
      if (entry.getId() == null || entry.getId().toString().length() == 0) return false;
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
  
  // The service doc, collection, etc
  
  private static Document<Service> service_doc = null;
  private static Document<Feed> feed_doc = null;
  
  private BaseResponseContext<Document<Entry>> getEntryDocument(Entry entry) {
    Document<Entry> entry_doc = entry.getDocument();
    BaseResponseContext<Document<Entry>> rc =
      new BaseResponseContext<Document<Entry>>(entry_doc);
    rc.setContentType("application/atom+xml");
    return rc;
  }
  
  private BaseResponseContext<Document<Service>> getServiceDocument() {
    BaseResponseContext<Document<Service>> rc = 
      new BaseResponseContext<Document<Service>>(getService());
    rc.setEntityTag("\"service\"");
    rc.setLastModified(new Date());
    rc.setContentType("application/atomserv+xml");
    return rc;
  }
  
  private BaseResponseContext<Document<Feed>> getFeedDocument() {
    BaseResponseContext<Document<Feed>> rc =
      new BaseResponseContext<Document<Feed>>(getFeed());
    rc.setContentType("application/atom+xml");
    return rc;
  }
  
  private synchronized Document<Service> getService() {
    if (service_doc == null) {
      try {
        Factory factory = getFactory();
        service_doc = factory.newDocument();
        Service service = factory.newService(service_doc);
        Workspace workspace = service.addWorkspace("Simple");
        Collection entries = workspace.addCollection("foo", "atom/foo");
        entries.setAccept("entry");
      } catch (Exception e) {}
    }
    return service_doc;
  }
  
  private synchronized Document<Feed> getFeed() {
    if (feed_doc == null) {
      try {
        Factory factory = getFactory();
        feed_doc = factory.newDocument();
        Feed feed = factory.newFeed(feed_doc);
        feed.setId(FOMHelper.generateUuid());
        feed.setTitle("Foo");
        feed.setUpdated(new Date());
        feed.addLink("http://example.org");
        feed.addLink("", "self");
        feed.addAuthor("Simple Service");
      } catch (Exception e) {}
    }
    return feed_doc;
  }
  
  private AbderaServer getAbderaServer() {
    return abderaServer;
  }
  
  private Abdera getAbdera() {
    return getAbderaServer().getAbdera();
  }
  
  private Factory getFactory() {
    return getAbdera().getFactory();
  }
  
  private Parser getParser() {
    return getAbdera().getParser();
  }

}
