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
package org.apache.abdera.protocol.server.processors;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.MediaCollectionAdapter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceManager;

/**
 * {@link org.apache.abdera.protocol.server.RequestProcessor} implementation which processes requests for collection
 * documents.
 */
public class CollectionRequestProcessor implements RequestProcessor {

    public ResponseContext process(RequestContext context,
                                   WorkspaceManager workspaceManager,
                                   CollectionAdapter collectionAdapter) {
        if (collectionAdapter == null) {
            return ProviderHelper.notfound(context);
        } else {
            return this.processCollection(context, collectionAdapter);
        }
    }

    private ResponseContext processCollection(RequestContext context, CollectionAdapter adapter) {
        String method = context.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            return adapter.getFeed(context);
        } else if (method.equalsIgnoreCase("POST")) {
            return ProviderHelper.isAtom(context) ? adapter.postEntry(context)
                : adapter instanceof MediaCollectionAdapter ? ((MediaCollectionAdapter)adapter).postMedia(context)
                    : ProviderHelper.notallowed(context);
        } else {
            return null;
        }
    }
}
