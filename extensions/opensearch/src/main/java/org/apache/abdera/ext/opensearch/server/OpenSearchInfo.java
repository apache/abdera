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

import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;
import org.apache.abdera.protocol.server.RequestContext;

/**
 * Metadata interface holding information about the Open Search Description document.
 */
public interface OpenSearchInfo {
    
    /**
     * Get the Open Search document short name.
     */
    String getShortName();
    
    /**
     * Get the Open Search document description.
     */
    String getDescription();
    
    /**
     * Get the Open Search document tags.
     */
    String[] getTags();
    
    /**
     * Get the Open Search queries.
     */
    OpenSearchQueryInfo[] getQueries();
    
    /**
     * Get the Open Search URLs metadata.
     */
    OpenSearchUrlInfo[] getUrls();
    
    /**
     * Create the related {@link org.apache.abdera.ext.opensearch.model.OpenSearchDescription} element.
     */
    OpenSearchDescription asOpenSearchDescriptionElement(RequestContext request);
}
