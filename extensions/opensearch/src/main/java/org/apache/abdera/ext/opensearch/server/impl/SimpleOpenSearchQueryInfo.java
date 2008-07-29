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

import org.apache.abdera.ext.opensearch.model.Query;
import org.apache.abdera.ext.opensearch.model.Query.Role;
import org.apache.abdera.ext.opensearch.server.OpenSearchQueryInfo;
import org.apache.abdera.protocol.server.RequestContext;

/**
 * Simple {@link org.apache.abdera.ext.opensearch.server.OpenSearchQueryInfo} implementation.
 */
public class SimpleOpenSearchQueryInfo implements OpenSearchQueryInfo {

    private Query.Role role;
    private String searchTerms;

    public void setRole(Role role) {
        this.role = role;
    }

    public Query.Role getRole() {
        return this.role;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getSearchTerms() {
        return this.searchTerms;
    }

    public Query asQueryElement(RequestContext request) {
        Query query = new Query(request.getAbdera());
        query.setRole(this.role);
        query.setSearchTerms(this.searchTerms);
        return query;
    }
}
