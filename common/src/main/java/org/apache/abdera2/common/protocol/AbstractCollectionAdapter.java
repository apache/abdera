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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera2.common.text.UrlEncoding;
import org.apache.abdera2.common.date.DateTime;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base CollectionAdapter implementation that provides a number of helper utility methods for adapter implementations.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCollectionAdapter 
  implements CollectionAdapter, 
             MediaCollectionAdapter, 
             Transactional,
             CollectionInfo {

    private final static Log log = LogFactory.getLog(AbstractCollectionAdapter.class);

    private String href;
    private Map<String, Object> hrefParams = new HashMap<String, Object>();

    public AbstractCollectionAdapter() {
        super();
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
        hrefParams.put("collection", href);
    }

    public String getHref(RequestContext request) {
        return request.urlFor("feed", hrefParams);
    }

    public void compensate(RequestContext request, Throwable t) {
    }

    public void end(RequestContext request, ResponseContext response) {
    }

    public void start(RequestContext request) {
    }

    public <S extends ResponseContext>S deleteMedia(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public <S extends ResponseContext>S getMedia(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public <S extends ResponseContext>S headMedia(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public <S extends ResponseContext>S optionsMedia(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public <S extends ResponseContext>S putMedia(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public <S extends ResponseContext>S postMedia(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public <S extends ResponseContext>S headItem(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public <S extends ResponseContext>S optionsItem(RequestContext request) {
        return (S)ProviderHelper.notallowed(request);
    }

    public abstract String getAuthor(RequestContext request) throws ResponseContextException;

    public abstract String getId(RequestContext request);

    /**
     * Creates the ResponseContext for a HEAD entry request. By default, an EmptyResponseContext is returned. The Etag
     * header will be set.
     */
    protected <S extends ResponseContext>S buildHeadEntryResponse(RequestContext request, String id, Date updated)
        throws ResponseContextException {
        EmptyResponseContext rc = new EmptyResponseContext(200);
        rc.setEntityTag(EntityTag.generate(id, DateTime.format(updated)));
        return (S)rc;
    }

    /**
     * Create a ResponseContext (or take it from the Exception) for an exception that occurred in the application.
     * 
     * @param e
     * @return
     */
    protected <S extends ResponseContext>S createErrorResponse(ResponseContextException e) {
        if (log.isDebugEnabled()) {
            log.debug("A ResponseException was thrown.", e);
        } else if (e.getResponseContext() instanceof EmptyResponseContext && ((EmptyResponseContext)e
            .getResponseContext()).getStatus() >= 500) {
            log.warn("A ResponseException was thrown.", e);
        }

        return e.getResponseContext();
    }

    /**
     * Get's the name of the specific resource requested
     */
    protected String getResourceName(RequestContext request) {
        String path = request.getTargetPath();
        int q = path.indexOf("?");
        if (q != -1) {
            path = path.substring(0, q);
        }
        String[] segments = path.split("/");
        String id = segments[segments.length - 1];
        return UrlEncoding.decode(id);
    }

    public <S extends ResponseContext>S extensionRequest(RequestContext request) {
        return (S)ProviderHelper.notallowed(request, getMethods(request));
    }

    private String[] getMethods(RequestContext request) {
        return ProviderHelper.getDefaultMethods(request);
    }

}
