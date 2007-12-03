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
import java.util.Map;
import java.util.Set;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.io.CharUtils.Profile;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractWorkspaceProvider 
  extends AbstractProvider {
  private final static Log log = LogFactory.getLog(AbstractWorkspaceProvider.class);
  
    private EntityTag service_etag = new EntityTag("simple");
    
    protected AbstractWorkspaceProvider(int count) {
      super(count);
    }

    public ResponseContext getService(RequestContext request) {
      Abdera abdera = request.getAbdera();
      AbstractResponseContext rc = getServicesDocument(abdera, getEncoding(request));
      rc.setEntityTag(service_etag);
      return rc;
    }

    private String getEncoding(RequestContext request) {
      return "utf-8";
    }
    
    private AbstractResponseContext getServicesDocument(
      final Abdera abdera, 
      final String enc) {
      return new StreamWriterResponseContext(abdera) { 
        @SuppressWarnings({"serial","unchecked"}) 
        protected void writeTo(
          StreamWriter sw) 
            throws IOException {
          sw.startDocument()
            .startService();          
          for (WorkspaceInfo wp : getWorkspaces()) {
            sw.startWorkspace().writeTitle(wp.getName());
            Set<Map.Entry<String, CollectionProvider>> entrySet = 
              (Set<Map.Entry<String, CollectionProvider>>) (wp.getCollectionProviders().entrySet());
            for (Map.Entry<String, CollectionProvider> entry : entrySet) {
              CollectionProvider cp = entry.getValue();
              final String id = wp.getId();
              final String key = entry.getKey();
              try {
                sw.startCollection(
                    Escaping.encode(id,enc,Profile.PATH) + "/" + 
                    Escaping.encode(key,enc,Profile.PATH))
                  .writeTitle(cp.getTitle())
                  .writeAcceptsEntry() 
                  .endCollection();
              } catch (RuntimeException e) {}
            }
            sw.endWorkspace();    
          }          
          sw.endService()
            .endDocument();
        }
      };
    }

    public abstract java.util.Collection<WorkspaceInfo> getWorkspaces();

    public ResponseContext getFeed(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext res = null;
      try {
        provider = getCollectionProvider(resolveBase(request), request);
        
        provider.begin(request);
        
        res = provider.getFeed(request);
        return res;
      } catch (ResponseContextException e) {
        res = createErrorResponse(e);
        return res;
      } finally {
        end(provider, request, res);
      }
    }

    @SuppressWarnings("unchecked")
    private CollectionProvider getCollectionProvider(IRI resolveBase, 
                                                     RequestContext request) throws ResponseContextException {
      String path = resolveBase.getPath();
      String[] paths = path.split("/");
      String id = null;
      WorkspaceInfo wp = null;
      if (paths.length < 1) {
        // TODO:
        throw new ResponseContextException(404);
      } else if (paths.length == 1) {
        wp = getWorkspaceInfo("");
        if (wp == null) {
          // TODO: 404
          throw new ResponseContextException(404);
        }
        id = paths[0];
      } else {
        String workspaceId = paths[paths.length - 2];
        workspaceId = Escaping.decode(workspaceId);
        wp = getWorkspaceInfo(workspaceId);
        if (wp == null) {
          // TODO: 404
          throw new ResponseContextException(404);
        }
        id = paths[paths.length - 1];
      }
      
      id = Escaping.decode(id);
      
      return wp.getCollectionProvider(id);
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
    protected abstract WorkspaceInfo getWorkspaceInfo(String string);

    public ResponseContext createEntry(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        provider = getCollectionProvider(request.getUri(), request);
        provider.begin(request);
        
        return provider.createEntry(request);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
        return response;
      } finally {
        end(provider, request, response);
      }
    }

    protected void end(CollectionProvider provider, RequestContext request, ResponseContext response) {
      if (provider != null) {
        try {
          provider.end(request, response);
        } catch (Throwable t) {
          log.warn("Could not end() CollectionProvider.", t);
        }
      }
    }

    @Override
    public ResponseContext getMedia(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        IRI entryBaseIri = resolveBase(request).resolve("../");
        provider = getCollectionProvider(entryBaseIri, request);
        provider.begin(request);
        
        return provider.getMedia(request);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
        return response;
      } finally {
        end(provider, request, response);
      }
    }


    @Override
    public ResponseContext updateMedia(RequestContext request) {
      // TODO Auto-generated method stub
      return super.updateMedia(request);
    }

    public ResponseContext deleteEntry(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        provider = getCollectionProvider(resolveBase(request).resolve("./"), request);
      
        return provider.deleteEntry(request);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
        return response;
      } finally {
        end(provider, request, response);
      }
    }

    
    public ResponseContext getEntry(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        IRI entryBaseIri = resolveBase(request).resolve("./");
        provider = getCollectionProvider(entryBaseIri, request);
        provider.begin(request);
        
        return provider.getEntry(request, entryBaseIri);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
        return response;
      } finally {
        end(provider, request, response);
      }
    }

    @SuppressWarnings("unchecked")
    public ResponseContext updateEntry(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        IRI entryBaseIri = resolveBase(request).resolve("./");
        provider = getCollectionProvider(entryBaseIri, request);
        provider.begin(request);
        
        return provider.updateEntry(request, entryBaseIri);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
        return response;
      } finally {
        end(provider, request, response);
      }
    }


}
