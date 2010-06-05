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
package org.apache.abdera.protocol.server.test.custom;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.RequestContextWrapper;
import org.apache.abdera.protocol.server.filters.OpenSearchFilter;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.impl.TemplateTargetBuilder;

public class CustomProvider extends AbstractWorkspaceProvider {

    private final SimpleAdapter adapter;

    public CustomProvider() {

        this.adapter = new SimpleAdapter();

        setTargetResolver(new RegexTargetResolver().setPattern("/atom(\\?[^#]*)?", TargetType.TYPE_SERVICE)
            .setPattern("/atom/([^/#?]+);categories", TargetType.TYPE_CATEGORIES, "collection")
            .setPattern("/atom/([^/#?;]+)(\\?[^#]*)?", TargetType.TYPE_COLLECTION, "collection")
            .setPattern("/atom/([^/#?]+)/([^/#?]+)(\\?[^#]*)?", TargetType.TYPE_ENTRY, "collection", "entry")
            .setPattern("/search", OpenSearchFilter.TYPE_OPENSEARCH_DESCRIPTION));

        setTargetBuilder(new TemplateTargetBuilder().setTemplate(TargetType.TYPE_SERVICE, "{target_base}/atom")
            .setTemplate(TargetType.TYPE_COLLECTION,
                         "{target_base}/atom/{collection}{-opt|?|q,c,s,p,l,i,o}{-join|&|q,c,s,p,l,i,o}")
            .setTemplate(TargetType.TYPE_CATEGORIES, "{target_base}/atom/{collection};categories")
            .setTemplate(TargetType.TYPE_ENTRY, "{target_base}/atom/{collection}/{entry}")
            .setTemplate(OpenSearchFilter.TYPE_OPENSEARCH_DESCRIPTION, "{target_base}/search"));

        SimpleWorkspaceInfo workspace = new SimpleWorkspaceInfo();
        workspace.setTitle("A Simple Workspace");
        workspace.addCollection(adapter);
        addWorkspace(workspace);

        addFilter(new SimpleFilter());
        addFilter(new OpenSearchFilter()
            .setShortName("My OpenSearch")
            .setDescription("My Description")
            .setTags("test", "example", "opensearch")
            .setContact("john.doe@example.org")
            .setTemplate("http://localhost:8080/atom/feed?q={searchTerms}&c={count?}&s={startIndex?}&p={startPage?}&l={language?}&i={indexEncoding?}&o={outputEncoding?}")
            .mapTargetParameter("q", "searchTerms").mapTargetParameter("c", "count").mapTargetParameter("s",
                                                                                                        "startIndex")
            .mapTargetParameter("p", "startPage").mapTargetParameter("l", "language")
            .mapTargetParameter("i", "inputEncoding").mapTargetParameter("o", "outputEncoding"));
    }

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        return adapter;
    }

    public class SimpleFilter implements Filter {
        public ResponseContext filter(RequestContext request, FilterChain chain) {
            RequestContextWrapper rcw = new RequestContextWrapper(request);
            rcw.setAttribute("offset", 10);
            rcw.setAttribute("count", 10);
            return chain.next(rcw);
        }
    }

}
