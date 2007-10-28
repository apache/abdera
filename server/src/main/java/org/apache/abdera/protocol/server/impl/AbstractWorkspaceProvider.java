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

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.Constants;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.util.EntityTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractWorkspaceProvider extends AbstractProvider {
  private final static Log log = LogFactory.getLog(AbstractWorkspaceProvider.class);
  
    private EntityTag service_etag = new EntityTag("simple");
    
    protected AbstractWorkspaceProvider(int count) {
      super(count);
    }

    public ResponseContext getService(RequestContext request) {
      Abdera abdera = request.getAbdera();
      Document<Service> service = getServicesDocument(abdera, getEncoding(request));
      AbstractResponseContext rc = new BaseResponseContext<Document<Service>>(service);
      rc.setEntityTag(service_etag);
      return rc;
    }

    private String getEncoding(RequestContext request) {
      return "utf-8";
    }
    
    @SuppressWarnings("unchecked")
    private Document<Service> getServicesDocument(Abdera abdera, String enc) {          
      if (enc == null) {
        enc = "utf-8";
      }
      
      Factory factory = abdera.getFactory();
      Service service = factory.newService();
      for (WorkspaceInfo wp : getWorkspaces()) {
        Workspace workspace = service.addWorkspace(wp.getName());
        Set<Map.Entry<String, CollectionProvider>> entrySet = 
          (Set<Map.Entry<String, CollectionProvider>>) (wp.getCollectionProviders().entrySet());
        for (Map.Entry<String, CollectionProvider> entry : entrySet) {
          CollectionProvider cp = entry.getValue();

          String id;
          String workspaceKey;
          try {
            id = Escaping.encode(wp.getId(), enc, Constants.PATH);
            workspaceKey = Escaping.encode(entry.getKey(), enc, Constants.PATH);
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
          }
          Collection collection = workspace.addCollection(cp.getTitle(), 
                                                          id + "/" + workspaceKey);
          collection.setAccept("entry");
          // collection.addCategories().setFixed(false);
        }
      }
      return service.getDocument();
    }

    public abstract java.util.Collection<WorkspaceInfo> getWorkspaces();

    public ResponseContext getFeed(RequestContext request) {
      try {
        CollectionProvider provider = getCollectionProvider(resolveBase(request), request);
        
        return provider.getFeed(request);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
    }

    @SuppressWarnings("unchecked")
    private CollectionProvider getCollectionProvider(IRI resolveBase, 
                                                     RequestContext request) throws ResponseContextException {
      String path = resolveBase.getPath();
      String[] paths = path.split("/");
      if (paths.length < 1) {
        // TODO:
        throw new RuntimeException();
      } else if (paths.length == 1) {
        WorkspaceInfo wp = getWorkspaceInfo("");
        if (wp == null) {
          // TODO: 404
          throw new RuntimeException();
        }
        return wp.getCollectionProvider(paths[0]);
      } else {    
        WorkspaceInfo wp = getWorkspaceInfo(paths[paths.length - 2]);
        if (wp == null) {
          // TODO: 404
          throw new RuntimeException();
        }
        return wp.getCollectionProvider(paths[paths.length - 1]);
      }
    }

    /**
     * Create a ResponseContext (or take it from the Exception) for an
     * exception that occurred in the application.
     * @param e
     * @return
     */
    protected ResponseContext createErrorResponse(ResponseContextException e) {
      if (log.isDebugEnabled()) {
        log.debug("A ResponseException was thrown.", e);
      } else if (e.getResponseContext() instanceof EmptyResponseContext 
        && ((EmptyResponseContext) e.getResponseContext()).getStatus() >= 500) {
        log.warn("A ResponseException was thrown.", e);
      }
      
      return e.getResponseContext();
    }
    protected abstract WorkspaceInfo getWorkspaceInfo(String string);

    public ResponseContext createEntry(RequestContext request) {
      try {
        CollectionProvider provider = getCollectionProvider(request.getUri(), request);
        
        return provider.createEntry(request);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
    }

    @Override
    public ResponseContext getMedia(RequestContext request) {
      try {
        IRI entryBaseIri = resolveBase(request).resolve("../");
        CollectionProvider provider = getCollectionProvider(entryBaseIri, request);
  
        return provider.getMedia(request);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
    }


    @Override
    public ResponseContext updateMedia(RequestContext request) {
      // TODO Auto-generated method stub
      return super.updateMedia(request);
    }

    public ResponseContext deleteEntry(RequestContext request) {
      try {
        CollectionProvider provider = getCollectionProvider(resolveBase(request).resolve("./"), request);
      
        return provider.deleteEntry(request);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
    }

    
    public ResponseContext getEntry(RequestContext request) {
      try {
        IRI entryBaseIri = resolveBase(request).resolve("./");
        CollectionProvider provider = getCollectionProvider(entryBaseIri, request);
  
        return provider.getEntry(request, entryBaseIri);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
    }

    @SuppressWarnings("unchecked")
    public ResponseContext updateEntry(RequestContext request) {
      try {
        IRI entryBaseIri = resolveBase(request).resolve("./");
        CollectionProvider provider = getCollectionProvider(entryBaseIri, request);
  
        return provider.updateEntry(request, entryBaseIri);
      } catch (ResponseContextException e) {
        return createErrorResponse(e);
      }
    }


}
