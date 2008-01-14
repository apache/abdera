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

import java.util.Map;

import org.apache.abdera.protocol.server.impl.ResponseContextException;

public interface WorkspaceInfo {

  String getName();

  /**
   * A map of CollectionProviders keyed by URI (in relation to the services document) - i.e.
   * if the services document was at "/services" and the collection at "/services/workspace/feed",
   * then the key would be "workspace/feed".
   * @return
   */
  Map<String, CollectionProvider> getCollectionProviders();

  CollectionProvider getCollectionProvider(String href);

}