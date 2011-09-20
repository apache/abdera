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
package org.apache.abdera2.common.protocol;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import org.apache.abdera2.common.misc.Resolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base Provider implementation that provides the core implementation details for all Providers. This class provides the
 * basic request routing logic.
 */
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class BaseProvider
  implements Provider {

    private final static Log log = LogFactory.getLog(BaseProvider.class);
    protected Map<String, String> properties;
    protected Set<Filter> filters = 
      new LinkedHashSet<Filter>();
    protected Map<TargetType, RequestProcessor> requestProcessors = 
      new HashMap<TargetType, RequestProcessor>();

    public void init(Map<String,String> properties) {
      this.properties = properties != null ? properties : new HashMap<String,String>();
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public Iterable<String> getPropertyNames() {
        return properties.keySet();
    }

    public Subject resolveSubject(RequestContext request) {
        Resolver<Subject,Request> subjectResolver = getSubjectResolver(request);
        return subjectResolver != null ? subjectResolver.resolve(request) : null;
    }

    public Target resolveTarget(RequestContext request) {
        Resolver<Target,RequestContext> targetResolver = getTargetResolver(request);
        return targetResolver != null ? targetResolver.resolve(request) : null;
    }

    public String urlFor(Request request, Object key, Object param) {
        TargetBuilder tm = getTargetBuilder(request);
        return tm != null ? tm.urlFor(request, key, param) : null;
    }

    protected Resolver<Subject,Request> getSubjectResolver(RequestContext request) {
        return new SimpleSubjectResolver();
    }

    protected abstract TargetBuilder getTargetBuilder(Request request);

    protected abstract Resolver<Target, RequestContext> getTargetResolver(RequestContext request);

    public <S extends ResponseContext>S process(RequestContext request) {
        Target target = request.getTarget();
        if (target == null || target.getType() == TargetType.TYPE_NOT_FOUND) {
            return (S)ProviderHelper.notfound(request);
        }

        TargetType type = target.getType();
        log.debug(String.format("Processing [%s] request for Target [%s] of Type [%s]",request.getMethod(),target.getIdentity(),type.toString()));
        RequestProcessor processor = 
          (RequestProcessor) this.requestProcessors.get(type);
        if (processor == null) {
            return (S)ProviderHelper.notfound(request);
        }

        WorkspaceManager wm = getWorkspaceManager(request);
        CollectionAdapter adapter = wm.getCollectionAdapter(request);
        Transactional transaction = 
          adapter instanceof Transactional ? (Transactional)adapter : null;
        S response = null;
        try {
            transactionStart(transaction, request);
            response = (S)processor.process(request, wm, adapter);
            response = (S)(response != null ? response : processExtensionRequest(request, adapter));
        } catch (Throwable e) {
            if (e instanceof ResponseContextException) {
                ResponseContextException rce = (ResponseContextException)e;
                if (rce.getStatusCode() >= 400 && rce.getStatusCode() < 500) {
                    // don't report routine 4xx HTTP errors
                    log.info(e);
                } else {
                    log.error(e);
                }
            } else {
                log.error(e);
            }
            transactionCompensate(transaction, request, e);
            response = (S)createErrorResponse(request, e);
            return response;
        } finally {
            transactionEnd(transaction, request, response);
        }
        return (S)(response != null ? response : ProviderHelper.badrequest(request));
    }

    /**
     * Subclass to customize the kind of error response to return
     */
    protected ResponseContext createErrorResponse(RequestContext request, Throwable e) {
        return ProviderHelper.servererror(request, e);
    }

    protected void transactionCompensate(Transactional transactional, RequestContext request, Throwable e) {
        if (transactional != null) {
            transactional.compensate(request, e);
        }
    }

    protected void transactionEnd(Transactional transactional, RequestContext request, ResponseContext response) {
        if (transactional != null) {
            transactional.end(request, response);
        }
    }

    protected void transactionStart(Transactional transactional, RequestContext request)
        throws ResponseContextException {
        if (transactional != null) {
            transactional.start(request);
        }
    }

    protected ResponseContext processExtensionRequest(RequestContext context, CollectionAdapter adapter) {
        return adapter.extensionRequest(context);
    }

    protected abstract WorkspaceManager getWorkspaceManager(RequestContext request);

    public void setFilters(Collection<Filter> filters) {
        this.filters = new LinkedHashSet<Filter>(filters);
    }

    public Iterable<Filter> getFilters(RequestContext request) {
        return filters;
    }

    public void addFilter(Filter... filters) {
        for (Filter filter : filters) {
            this.filters.add(filter);
        }
    }

    public void setRequestProcessors(Map<TargetType, RequestProcessor> requestProcessors) {
        this.requestProcessors.clear();
        this.requestProcessors.putAll(requestProcessors);
    }

    public void addRequestProcessors(Map<TargetType, RequestProcessor> requestProcessors) {
        this.requestProcessors.putAll(requestProcessors);
    }

    public Map<TargetType, RequestProcessor> getRequestProcessors() {
        return Collections.unmodifiableMap(this.requestProcessors);
    }
    
}
