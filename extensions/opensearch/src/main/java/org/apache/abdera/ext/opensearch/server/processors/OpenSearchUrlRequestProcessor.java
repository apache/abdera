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
package org.apache.abdera.ext.opensearch.server.processors;

import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlAdapter;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlInfo;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlParameterInfo;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceManager;

/**
 * {@link org.apache.abdera.protocol.server.RequestProcessor} implementation for processing GET requests to Open Search
 * urls and then delegating the actual search to the proper
 * {@link org.apache.abdera.ext.opensearch.server.OpenSearchUrlAdapter}.<br>
 * The proper {@link org.apache.abdera.ext.opensearch.server.OpenSearchUrlAdapter} is selected by searching the
 * configured {@link org.apache.abdera.ext.opensearch.server.OpenSearchInfo} for an
 * {@link org.apache.abdera.ext.opensearch.server.OpenSearchUrlInfo} with a matching search path.
 * 
 * @see {@link #setOpenSearchInfo(OpenSearchInfo)}
 */
public class OpenSearchUrlRequestProcessor implements RequestProcessor {

    private OpenSearchInfo openSearchInfo;

    public ResponseContext process(final RequestContext requestContext,
                                   final WorkspaceManager workspaceManager,
                                   final CollectionAdapter collectionAdapter) {
        String method = requestContext.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            OpenSearchUrlInfo urlInfo = this.getMatchingUrlInfo(requestContext);
            if (urlInfo != null) {
                OpenSearchUrlAdapter adapter = urlInfo.getOpenSearchUrlAdapter();
                if (adapter != null) {
                    Map<String, String> params = this.getUrlParametersFromRequest(requestContext, urlInfo);
                    return adapter.search(requestContext, params);
                } else {
                    return ProviderHelper.notfound(requestContext);
                }
            } else {
                return ProviderHelper.notfound(requestContext);
            }
        } else {
            return null;
        }
    }

    public void setOpenSearchInfo(OpenSearchInfo openSearchInfo) {
        this.openSearchInfo = openSearchInfo;
    }

    private OpenSearchUrlInfo getMatchingUrlInfo(RequestContext request) {
        String targetSearchPath =
            this.stripSlashes(request.getTargetPath().substring(0, request.getTargetPath().indexOf("?")));
        OpenSearchUrlInfo result = null;
        for (OpenSearchUrlInfo urlInfo : this.openSearchInfo.getUrls()) {
            String searchPath = this.stripSlashes(urlInfo.getSearchPath());
            if (searchPath.equals(targetSearchPath)) {
                result = urlInfo;
                break;
            }
        }
        return result;
    }

    private Map<String, String> getUrlParametersFromRequest(RequestContext request, OpenSearchUrlInfo urlInfo) {
        Map<String, String> result = new HashMap<String, String>();
        for (OpenSearchUrlParameterInfo paramInfo : urlInfo.getSearchParameters()) {
            // TODO : enforce mandatory parameters?
            String name = paramInfo.getName();
            String value = request.getParameter(name);
            if (value != null) {
                result.put(name, value);
            }
        }
        return result;
    }

    private String stripSlashes(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
