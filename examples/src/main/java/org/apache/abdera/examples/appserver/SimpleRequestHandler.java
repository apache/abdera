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

import java.util.Date;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException;
import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.protocol.server.target.Target;
import org.apache.abdera.protocol.server.util.AbstractRequestHandler;
import org.apache.abdera.protocol.server.util.BaseResponseContext;
import org.apache.abdera.protocol.server.util.EmptyResponseContext;
import org.apache.abdera.protocol.server.util.ResourceType;

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
  
  public SimpleRequestHandler(AbderaServer abderaServer, Provider provider) {
    super(provider);
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
          return getServiceDocument(requestContext);
      } else if (type == ResourceType.COLLECTION) {
          if (method.equals("GET")) {
            if (!target.getValue(1).equals("foo")) 
              throw new AbderaServerException(
                AbderaServerException.Code.NOTFOUND, 
                "Not Found", "");
            return getFeedDocument(requestContext);
          } else if (method.equals("POST")) {
            try {
              Entry entry = getProvider().addEntry(requestContext);
              if (entry != null) {
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
          if (method.equals("GET")) {
            Entry e = getProvider().getEntry(requestContext);
            return new BaseResponseContext(e.getDocument());
          } else if (method.equals("PUT")) {
            try {
              Entry entry = getProvider().updateEntry(requestContext);
              if (entry == null) 
                throw new AbderaServerException(
                  AbderaServerException.Code.UNSUPPORTEDMEDIATYPE, 
                  "Unsupported Media Type", "");
              return new EmptyResponseContext(204);
            } catch (AbderaServerException ase) {
              throw ase;
            } catch (Exception e) {
              throw new AbderaServerException(e);
            }
          } else if (method.equals("DELETE")) {
            getProvider().deleteEntry(requestContext);
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
  
  // The service doc, collection, etc
  
  private BaseResponseContext<Document<Entry>> getEntryDocument(Entry entry) {
    Document<Entry> entry_doc = entry.getDocument();
    BaseResponseContext<Document<Entry>> rc =
      new BaseResponseContext<Document<Entry>>(entry_doc);
    rc.setContentType("application/atom+xml");
    return rc;
  }
  
  private BaseResponseContext<Document<Service>> getServiceDocument(
    RequestContext context) 
      throws AbderaServerException {
    Document<Service> service_doc = 
      getProvider().getService(context).getDocument();
    BaseResponseContext<Document<Service>> rc = 
      new BaseResponseContext<Document<Service>>(service_doc);
    rc.setEntityTag("\"service\"");
    rc.setLastModified(new Date());
    rc.setContentType("application/atomserv+xml");
    return rc;
  }
  
  private BaseResponseContext<Document<Feed>> getFeedDocument(
    RequestContext context) 
      throws AbderaServerException {
    Document<Feed> feed_doc = 
      getProvider().getFeed(context).getDocument();
    BaseResponseContext<Document<Feed>> rc =
      new BaseResponseContext<Document<Feed>>(feed_doc);
    rc.setContentType("application/atom+xml");
    return rc;
  }
  
}
