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
package org.apache.abdera2.common.protocol;


/**
 * {@link org.apache.AtompubRequestProcessor.protocol.server.RequestProcessor} implementation which processes requests for entry
 * documents.
 */
public class EntryRequestProcessor
    implements RequestProcessor {
  
    @SuppressWarnings("unchecked")
    public <S extends ResponseContext>S process(RequestContext context,
                     WorkspaceManager workspaceManager,
                     CollectionAdapter collectionAdapter) {
        if (collectionAdapter == null) {
            return (S)ProviderHelper.notfound(context);
        } else {
            return this.processEntry(context, collectionAdapter);
        }
    }

    public <S extends ResponseContext>S processEntry(
        RequestContext context, 
        CollectionAdapter adapter) {
        String method = context.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            return adapter.getItem(context);
        } else if (method.equalsIgnoreCase("POST")) {
            return adapter.postItem(context);
        } else if (method.equalsIgnoreCase("PUT")) {
            return adapter.putItem(context);
        } else if (method.equalsIgnoreCase("DELETE")) {
            return adapter.deleteItem(context);
        } else if (method.equalsIgnoreCase("HEAD")) {
            return adapter.headItem(context);
        } else if (method.equalsIgnoreCase("OPTIONS")) {
            return adapter.optionsItem(context);
        } else if (method.equalsIgnoreCase("PATCH") && adapter instanceof PatchAdapter) {
            return ((PatchAdapter)adapter).patchItem(context);
        } else {
            return null;
        }
    }
}
