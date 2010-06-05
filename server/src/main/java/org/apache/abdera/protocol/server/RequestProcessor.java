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
package org.apache.abdera.protocol.server;

/**
 * Request processors implement the actual business logic for handling requests to the Atompub server and producing the
 * related response.
 */
public interface RequestProcessor {

    /**
     * Provide the actual request processing logic.
     * 
     * @param requestContext The {@link RequestContext} object, holding information about the request to process.
     * @param workspaceManager The {@link WorkspaceManager} object, holding information useful for request processing.
     * @param collectionAdapter The {@link CollectionAdapter} object, holding information useful for request processing;
     *            may be null if not needed.
     * @return A {@link ResponseContext} object, as resulted from the request processing.
     */
    ResponseContext process(RequestContext requestContext,
                            WorkspaceManager workspaceManager,
                            CollectionAdapter collectionAdapter);
}
