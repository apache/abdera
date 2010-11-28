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
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.WorkspaceManager;

/**
 * {@link org.apache.abdera.protocol.server.RequestProcessor} implementation which processes requests for entry
 * documents.
 */
public class EntryRequestProcessor implements RequestProcessor {

    public ResponseContext process(RequestContext context,
                                   WorkspaceManager workspaceManager,
                                   CollectionAdapter collectionAdapter) {
        if (collectionAdapter == null) {
            return ProviderHelper.notfound(context);
        } else {
            return this.processEntry(context, collectionAdapter);
        }
    }

    protected ResponseContext processEntry(RequestContext context, CollectionAdapter adapter) {
        String method = context.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            return adapter.getEntry(context);
        } else if (method.equalsIgnoreCase("POST")) {
            return adapter.postEntry(context);
        } else if (method.equalsIgnoreCase("PUT")) {
            return adapter.putEntry(context);
        } else if (method.equalsIgnoreCase("DELETE")) {
            return adapter.deleteEntry(context);
        } else if (method.equalsIgnoreCase("HEAD")) {
            return adapter.headEntry(context);
        } else if (method.equalsIgnoreCase("OPTIONS")) {
            return adapter.optionsEntry(context);
        } else {
            return null;
        }
    }
}
