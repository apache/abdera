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

import java.util.ArrayList;
import java.util.List;
import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;
import org.apache.abdera.ext.opensearch.model.Query;
import org.apache.abdera.ext.opensearch.model.Url;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.ext.opensearch.server.OpenSearchQueryInfo;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlInfo;
import org.apache.abdera.protocol.server.RequestContext;

/**
 * Simple {@link org.apache.abdera.ext.opensearch.server.OpenSearchInfo} implementation.
 */
public class SimpleOpenSearchInfo implements OpenSearchInfo {

    private String shortName;
    private String description;
    private String[] tags;
    private OpenSearchQueryInfo[] queries;
    private OpenSearchUrlInfo[] urls;

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setTags(String... tags) {
        this.tags = tags;
    }

    public OpenSearchQueryInfo[] getQueries() {
        return this.queries;
    }

    public void setQueries(OpenSearchQueryInfo... queries) {
        this.queries = queries;
    }

    public OpenSearchUrlInfo[] getUrls() {
        return this.urls;
    }

    public void setUrls(OpenSearchUrlInfo... urls) {
        this.urls = urls;
    }

    public OpenSearchDescription asOpenSearchDescriptionElement(RequestContext request) {
        OpenSearchDescription document = new OpenSearchDescription(request.getAbdera());
        document.setShortName(this.shortName);
        document.setDescription(this.description);
        document.setTags(this.tags);

        if (this.urls != null) {
            List<Url> urlElements = new ArrayList<Url>(this.urls.length);
            for (OpenSearchUrlInfo urlInfo : this.urls) {
                urlElements.add(urlInfo.asUrlElement(request));
            }
            document.addUrls(urlElements.toArray(new Url[this.urls.length]));
        }

        if (this.queries != null) {
            List<Query> queryElements = new ArrayList<Query>(this.queries.length);
            for (OpenSearchQueryInfo queryInfo : this.queries) {
                queryElements.add(queryInfo.asQueryElement(request));
            }
            document.addQueries(queryElements.toArray(new Query[this.queries.length]));
        }

        return document;
    }
}
