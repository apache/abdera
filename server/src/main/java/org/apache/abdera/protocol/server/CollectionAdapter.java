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
 * The CollectionAdapter interface is the component that provides the business
 * logic of an Atompub server.  The Provider will use it's WorkspaceManager to
 * determine which CollectionAdapter to dispatch a request to.  Once an adapter
 * is selected, the Provider will determine what kind of request is being made
 * and will forward the request on to the appropriate CollectionAdapter method.
 */
public interface CollectionAdapter {

  ResponseContext postEntry(RequestContext request);
  
  ResponseContext deleteEntry(RequestContext request);
  
  ResponseContext getEntry(RequestContext request);
  
  ResponseContext putEntry(RequestContext request);

  ResponseContext getFeed(RequestContext request);

  ResponseContext getCategories(RequestContext request);

  /**
   * Any request that is not covered by the postEntry, deleteEntry, etc
   * methods will be passed on to the extensionRequest method.  This provides
   * an Adapter with the ability to support Atompub protocol extensions.
   */
  ResponseContext extensionRequest(RequestContext request);
  
}
