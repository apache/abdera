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
package org.apache.abdera.ext.opensearch.server;

import org.apache.abdera.ext.opensearch.model.Url;
import org.apache.abdera.protocol.server.RequestContext;

/**
 * Metadata interface holding information about the Open Search URLs.
 */
public interface OpenSearchUrlInfo {

    /**
     * Get the URL content type.
     */
    String getType();

    /**
     * Get the URL search path as appear after the servlet context path, and without the query string.
     */
    String getSearchPath();

    /**
     * Get the URL search parameters.
     */
    OpenSearchUrlParameterInfo[] getSearchParameters();

    /**
     * Get the {@link OpenSearchUrlAdapter} which will implement the actual search operation.
     */
    OpenSearchUrlAdapter getOpenSearchUrlAdapter();

    /**
     * Create the related {@link org.apache.abdera.ext.opensearch.model.Url} element.
     */
    Url asUrlElement(RequestContext request);
}
