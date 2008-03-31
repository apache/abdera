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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.CategoriesInfo;
import org.apache.abdera.protocol.server.CategoryInfo;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.MediaCollectionAdapter;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.Transactional;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base Provider implementation that provides the core implementation details 
 * for all Providers.  This class provides the basic request routing logic
 */
public abstract class AbstractProvider 
  implements Provider {
  
  private final static Log log = LogFactory.getLog(AbstractProvider.class);
  
  protected Abdera abdera;
  protected Map<String,String> properties;
  protected List<Filter> filters = new ArrayList<Filter>();
  
  public void init(
    Abdera abdera, 
    Map<String, String> properties) {
      this.abdera = abdera;
      this.properties = properties;
  }
  
  public String getProperty(String name) {
    return properties.get(name);
  }
  
  public String[] getPropertyNames() {
    return properties.keySet().toArray(new String[properties.size()]);
  }
  
  public Abdera getAbdera() {
    return abdera;
  }

  public Subject resolveSubject(RequestContext request) {
    Resolver<Subject> subjectResolver = getSubjectResolver(request);
    return subjectResolver != null ? 
       subjectResolver.resolve(request) : null;
  }

  public Target resolveTarget(RequestContext request) {
    Resolver<Target> targetResolver = 
      getTargetResolver(request);
    return targetResolver != null ? 
      targetResolver.resolve(request) : null;
  }

  public String urlFor(
    RequestContext request, 
    Object key, 
    Object param) {
      TargetBuilder tm = getTargetBuilder(request);
      return tm != null ? tm.urlFor(request, key, param) : null;
  }
  
  protected Resolver<Subject> getSubjectResolver(RequestContext request) {
    return new SimpleSubjectResolver();
  }

  protected abstract TargetBuilder getTargetBuilder(RequestContext request);
  
  protected abstract Resolver<Target> getTargetResolver(RequestContext request);

  public ResponseContext process(
    RequestContext request) {    
      Target target = request.getTarget();
      if (target == null || 
          target.getType() == TargetType.TYPE_NOT_FOUND)
        return ProviderHelper.notfound(request);
      String method = request.getMethod();
      TargetType type = target.getType();
      if (type == TargetType.TYPE_SERVICE && 
          method.equalsIgnoreCase("GET")) {
          return getServiceDocument(request);
      }
      WorkspaceManager wm = getWorkspaceManager(request);
      
      CollectionAdapter adapter = 
        wm.getCollectionAdapter(request);
      if (adapter == null) {
        return ProviderHelper.notfound(
          request);
      }
      
      Transactional transaction = 
        adapter instanceof Transactional ? 
          (Transactional)adapter : null;
      ResponseContext response = null;
      try {
        if (transaction != null) transaction.start(request);
        if (type == TargetType.TYPE_CATEGORIES) {
          if (method.equalsIgnoreCase("GET"))
            response = adapter.getCategories(request);
        } else if (type == TargetType.TYPE_COLLECTION) {
          if (method.equalsIgnoreCase("GET")) 
            response = adapter.getFeed(request);
          else if (method.equalsIgnoreCase("POST")) {
            response = ProviderHelper.isAtom(request) ?
              adapter.postEntry(request) :
              adapter instanceof MediaCollectionAdapter ?
                ((MediaCollectionAdapter)adapter).postMedia(request) :
                ProviderHelper.notsupported(request);
          }
        } else if (type == TargetType.TYPE_ENTRY) {
          if (method.equalsIgnoreCase("GET")) 
            response = adapter.getEntry(request);
          else if (method.equalsIgnoreCase("PUT")) 
            response = adapter.putEntry(request);
          else if (method.equalsIgnoreCase("DELETE")) 
            response = adapter.deleteEntry(request);
          else if (method.equalsIgnoreCase("HEAD")) 
            response = adapter.headEntry(request);
          else if (method.equalsIgnoreCase("OPTIONS")) 
            response = adapter.optionsEntry(request);
        } else if (type == TargetType.TYPE_MEDIA) {
          if (adapter instanceof MediaCollectionAdapter) {
            MediaCollectionAdapter mcadapter = 
              (MediaCollectionAdapter) adapter;
            if (method.equalsIgnoreCase("GET")) 
              response = mcadapter.getMedia(request);
            else if (method.equalsIgnoreCase("PUT")) 
              response = mcadapter.putMedia(request);
            else if (method.equalsIgnoreCase("DELETE")) 
              response = mcadapter.deleteMedia(request);   
            else if (method.equalsIgnoreCase("HEAD")) 
              response = mcadapter.headMedia(request);   
            else if (method.equalsIgnoreCase("OPTIONS")) 
              response = mcadapter.optionsMedia(request);        
          } else {
            response = ProviderHelper.notsupported(request);
          }
        } else if (type == TargetType.TYPE_NOT_FOUND) {
          response = ProviderHelper.notfound(request);
        } else {
          response = adapter.extensionRequest(request);
        }
        if (response == null)
          response = ProviderHelper.notsupported(request);
        return response;
      } catch (Throwable e) {
        log.error(e);
        if (transaction != null) 
          transaction.compensate(request,e);
        response = ProviderHelper.servererror(request, e);
        return response;
      } finally {
        if (transaction != null) 
          transaction.end(request, response);
      }
  }
   
  protected abstract WorkspaceManager getWorkspaceManager(
    RequestContext request);
  

  protected Service getServiceElement(RequestContext request) {
    Service service = abdera.newService();
    for (WorkspaceInfo wi : getWorkspaceManager(request).getWorkspaces(request))
      service.addWorkspace(wi.asWorkspaceElement(request));
    return service;
  }
  
  protected ResponseContext getServiceDocument(
    final RequestContext request) {
      return 
        new StreamWriterResponseContext(request.getAbdera()) {
          protected void writeTo(
            StreamWriter sw) 
              throws IOException {
            sw.startDocument()
              .startService();
            for (WorkspaceInfo wi : getWorkspaceManager(request).getWorkspaces(request)) {
              sw.startWorkspace()
                .writeTitle(wi.getTitle(request));
              for (CollectionInfo ci : wi.getCollections(request)) {
                sw.startCollection(ci.getHref(request))
                  .writeTitle(ci.getTitle(request))
                  .writeAccepts(ci.getAccepts(request));
                CategoriesInfo[] catinfos = ci.getCategoriesInfo(request);
                if (catinfos != null) {
                  for (CategoriesInfo catinfo : catinfos) {
                    String cathref = catinfo.getHref(request);
                    if (cathref != null) {
                      sw.startCategories()
                        .writeAttribute("href", request.getTargetBasePath() + cathref)
                        .endCategories();
                    } else {
                      sw.startCategories(
                        catinfo.isFixed(request), 
                        catinfo.getScheme(request));
                      for (CategoryInfo cat : catinfo) {
                        sw.writeCategory(
                          cat.getTerm(request), 
                          cat.getScheme(request), 
                          cat.getLabel(request));
                      }
                      sw.endCategories();
                    }
                  }
                }
                sw.endCollection();
              }
              sw.endWorkspace();
            }
            sw.endService()
              .endDocument();
          }
        }
        .setStatus(200)
        .setContentType(Constants.APP_MEDIA_TYPE);
  }

  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }
  
  public Filter[] getFilters(RequestContext request) {
    return filters.toArray(new Filter[filters.size()]);
  }

  public void addFilter(Filter... filters) {
    for (Filter filter : filters)
      this.filters.add(filter);
  }  
}
