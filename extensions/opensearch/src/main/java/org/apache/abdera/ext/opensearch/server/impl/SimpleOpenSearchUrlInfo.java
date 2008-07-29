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
package org.apache.abdera.ext.opensearch.server.impl;

import org.apache.abdera.ext.opensearch.model.Url;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlAdapter;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlInfo;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlParameterInfo;
import org.apache.abdera.protocol.server.RequestContext;

/**
 * Simple {@link org.apache.abdera.ext.opensearch.server.OpenSearchUrlInfo} implementation.
 */
public class SimpleOpenSearchUrlInfo implements OpenSearchUrlInfo {

    private static final String ATOM_CONTENT_TYPE = "application/atom+xml";
    private String searchPath;
    private OpenSearchUrlParameterInfo[] searchParameters;
    private OpenSearchUrlAdapter openSearchUrlAdapter;

    public String getType() {
        return ATOM_CONTENT_TYPE;
    }

    public String getSearchPath() {
        return this.searchPath;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    public OpenSearchUrlParameterInfo[] getSearchParameters() {
        return this.searchParameters;
    }

    public void setSearchParameters(OpenSearchUrlParameterInfo... searchParameters) {
        this.searchParameters = searchParameters;
    }

    public OpenSearchUrlAdapter getOpenSearchUrlAdapter() {
        return this.openSearchUrlAdapter;
    }

    public void setOpenSearchUrlAdapter(OpenSearchUrlAdapter openSearchUrlAdapter) {
        this.openSearchUrlAdapter = openSearchUrlAdapter;
    }

    public Url asUrlElement(RequestContext request) {
        Url element = new Url(request.getAbdera());
        element.setType(ATOM_CONTENT_TYPE);

        StringBuilder template = new StringBuilder();
        template.append(this.getTemplateUrl(request));
        if (this.searchParameters != null) {
            template.append("?");
            for (int i = 0; i < this.searchParameters.length; i++) {
                OpenSearchUrlParameterInfo param = this.searchParameters[i];
                template.append(param.getName()).append("=").append("{").append(param.getValue());
                if (param.isOptional()) {
                    template.append("?");
                }
                template.append("}");
                if (i < this.searchParameters.length - 1) {
                    template.append("&");
                }
            }
        }
        element.setTemplate(template.toString());

        return element;
    }

    private String getTemplateUrl(RequestContext request) {
        String base = request.getBaseUri().toString();
        String path = this.searchPath;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return base + "/" + path;
    }
}
