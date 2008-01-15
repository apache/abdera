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
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents an Atom service which is backed by various workspaces and CollectionProviders.
 * This class can be extended to provide a dynamic list of workspaces or ServiceProvider can
 * be used to use a static list.
 * 
 */
public abstract class AbstractServiceProvider extends AbstractProvider implements Resolver<Target> {
  private static final Log log = LogFactory.getLog(AbstractServiceProvider.class);
  public static final String COLLECTION_PROVIDER_ATTRIBUTE = "collectionProvider";
  public static final String URI_PARAMETER_ATTRIBUTE_PREFIX = "uriParameter";
  
    private Pattern servicesPattern = Pattern.compile("^/");
    
    protected AbstractServiceProvider(int count) {
      super(count);
    }

    public Target resolve(Request request) {
      RequestContext context = (RequestContext) request;
      String uri = context.getTargetPath();
      
      if (servicesPattern == null) {
        throw new RuntimeException("You must set the servicesPattern property on the ServiceProvider.");
      }
      
      Matcher uriMatcher = servicesPattern.matcher(uri);
      TargetType tt = null;
      if (uriMatcher.matches()) {
        tt = TargetType.TYPE_SERVICE;
      } else {
        uriMatcher.reset();
        if (uriMatcher.find()) {
          String path = uri.substring(uriMatcher.end());
          int q = path.indexOf("?");
          if (q != -1) {
            path = path.substring(0, q);
          }
          
          path = UrlEncoding.decode(path);
  
          CollectionProvider provider = null;
          String providerHref = null;
          for (WorkspaceInfo wi : getWorkspaces()) {
            for (Map.Entry<String, CollectionProvider> e : wi.getCollectionProviders().entrySet()) {
              if (path.startsWith(e.getKey())) {
                provider = e.getValue();
                providerHref = e.getKey();
                break;
              }
            }
          }
          
          if (provider != null) {
            context.setAttribute(Scope.REQUEST, COLLECTION_PROVIDER_ATTRIBUTE, provider);
            
            if (providerHref.equals(path)) {
              tt = TargetType.TYPE_COLLECTION;
            } else {
              tt = getOtherTargetType(context, path, providerHref, provider);
            }
          }
        } 
      }
      
      if (tt == null) {
        tt = TargetType.TYPE_UNKNOWN;
      } else {
        for (int i = 1; i <= uriMatcher.groupCount(); i++) {
          if (uriMatcher.group(i) != null) {
            context.setAttribute(
              Scope.REQUEST, 
              URI_PARAMETER_ATTRIBUTE_PREFIX + Integer.toString(i), 
              uriMatcher.group(i));
          }
        }
      }
      
      return new DefaultTarget(tt, context);
    }

    @SuppressWarnings("unchecked")
    protected TargetType getOtherTargetType(RequestContext context, 
                                            String path, 
                                            String providerHref, 
                                            CollectionProvider provider) {
      String baseMedia = null;
      if (provider instanceof AbstractCollectionProvider) {
        baseMedia = ((AbstractCollectionProvider) provider).getBaseMediaIri();
      }
      
      if (providerHref.startsWith("/")) {
        providerHref = providerHref.substring(1);
      }
      
      if (providerHref.startsWith(baseMedia)) {
        return TargetType.TYPE_MEDIA;
      } else {
        return TargetType.TYPE_ENTRY;
      }
    }

    public ResponseContext getService(RequestContext request) {
      Abdera abdera = request.getAbdera();
      AbstractResponseContext rc = getServicesDocument(abdera, request);
      rc.setStatus(200);
      return rc;
    }

    private String getEncoding(RequestContext request) {
      return "utf-8";
    }

    private AbstractResponseContext getServicesDocument(final Abdera abdera, 
                                                        final RequestContext request) {
      final String enc = getEncoding(request);
      
      return new StreamWriterResponseContext(abdera) {

        protected void writeTo(StreamWriter sw) throws IOException {
          sw.startDocument().startService();
  
          for (WorkspaceInfo wp : getWorkspaces()) {
            sw.startWorkspace().writeTitle(wp.getName());
            Set<Map.Entry<String, CollectionProvider>> entrySet = (Set<Map.Entry<String, CollectionProvider>>)(wp
              .getCollectionProviders().entrySet());
  
            for (Map.Entry<String, CollectionProvider> entry : entrySet) {
              CollectionProvider cp = entry.getValue();
  
              String href;
              try {
                href = UrlEncoding.encode(entry.getKey(), enc, Profile.PATH.filter());
              } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
              }
  
              try {
                sw.startCollection(href)
                  .writeTitle(cp.getTitle(request))
                  .writeAcceptsEntry()
                  .endCollection();
              } catch (RuntimeException e) {
              }
            }
  
            sw.endWorkspace();
          }
  
          sw.endService().endDocument();
  
        }
      };
    }
    
    public abstract java.util.Collection<WorkspaceInfo> getWorkspaces();

    public ResponseContext getFeed(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext res = null;
      try {
        provider = getCollectionProvider(request);
        
        provider.begin(request);
        
        res = provider.getFeed(request);
      } catch (ResponseContextException e) {
        res = createErrorResponse(e);
      } finally {
        end(provider, request, res);
      }
      return res;
    }

    private CollectionProvider getCollectionProvider(RequestContext request) {
      return (CollectionProvider) request.getAttribute(Scope.REQUEST, COLLECTION_PROVIDER_ATTRIBUTE);
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

    public ResponseContext createEntry(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        provider = getCollectionProvider(request);
        provider.begin(request);
        
        response =  provider.createEntry(request);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
      } finally {
        end(provider, request, response);
      }
      return response;
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
        provider = getCollectionProvider(request);
        provider.begin(request);
        
        response = provider.getMedia(request);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
      } finally {
        end(provider, request, response);
      }
      return response;
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
        provider = getCollectionProvider(request);
      
        response = provider.deleteEntry(request);
      } finally {
        end(provider, request, response);
      }
      return response;
    }

    
    public ResponseContext getEntry(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        IRI entryBaseIri = resolveBase(request).resolve("./");
        provider = getCollectionProvider(request);
        provider.begin(request);
        
        response = provider.getEntry(request, entryBaseIri);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
      } finally {
        end(provider, request, response);
      }
      return response;
    }

    public ResponseContext updateEntry(RequestContext request) {
      CollectionProvider provider = null;
      ResponseContext response = null;
      try {
        IRI entryBaseIri = resolveBase(request).resolve("./");
        provider = getCollectionProvider(request);
        provider.begin(request);
        
        response = provider.updateEntry(request, entryBaseIri);
      } catch (ResponseContextException e) {
        response = createErrorResponse(e);
      } finally {
        end(provider, request, response);
      }
      return response;
    }

    public String getServicesPattern() {
      return servicesPattern.pattern();
    }

    public void setServicesPattern(String servicesPattern) {
      this.servicesPattern = Pattern.compile(servicesPattern);
    }

}
