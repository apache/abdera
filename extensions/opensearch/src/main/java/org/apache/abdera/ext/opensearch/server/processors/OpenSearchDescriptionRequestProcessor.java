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

import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.context.BaseResponseContext;

/**
 * {@link org.apache.abdera.protocol.server.RequestProcessor} implementation for processing Open Search Description
 * document GET requests.<br>
 * This request processor needs an {@link org.apache.abdera.ext.opensearch.server.OpenSearchInfo} in order to provide
 * information about the Open Search service.
 * 
 * @see {@link #setOpenSearchInfo(OpenSearchInfo)}
 */
public class OpenSearchDescriptionRequestProcessor implements RequestProcessor {

    private OpenSearchInfo openSearchInfo;

    public ResponseContext process(RequestContext requestContext,
                                   WorkspaceManager workspaceManager,
                                   CollectionAdapter collectionAdapter) {
        String method = requestContext.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            OpenSearchDescription description = this.openSearchInfo.asOpenSearchDescriptionElement(requestContext);
            ResponseContext response = new BaseResponseContext(description);
            response.setContentType(OpenSearchConstants.OPENSEARCH_DESCRIPTION_CONTENT_TYPE);
            return response;
        } else {
            return null;
        }
    }

    public void setOpenSearchInfo(OpenSearchInfo openSearchInfo) {
        this.openSearchInfo = openSearchInfo;
    }
}
