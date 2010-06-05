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
package org.apache.abdera.protocol.server.impl;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.RequestContext.Scope;

/**
 * The DefaultWorkspaceManager is used by the DefaultProvider
 */
public class DefaultWorkspaceManager extends AbstractWorkspaceManager {
    // URI reserved delimiter characters (gen-delims) from RFC 3986 section 2.2
    private static final String URI_GEN_DELIMS = ":/?#[]@";
    public static final String COLLECTION_ADAPTER_ATTRIBUTE = "collectionProvider";

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        String path = request.getContextPath() + request.getTargetPath();

        // Typically this happens when a Resolver wants to override the CollectionAdapter being used
        CollectionAdapter ca = (CollectionAdapter)request.getAttribute(Scope.REQUEST, COLLECTION_ADAPTER_ATTRIBUTE);
        if (ca != null) {
            return ca;
        }
        for (WorkspaceInfo wi : workspaces) {
            for (CollectionInfo ci : wi.getCollections(request)) {
                String href = ci.getHref(request);
                if (path.equals(href) || (href != null && path.startsWith(href) && URI_GEN_DELIMS.contains(path
                    .substring(href.length(), href.length() + 1)))) {
                    return (CollectionAdapter)ci;
                }
            }
        }

        return null;
    }

}
