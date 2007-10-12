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
package org.apache.abdera.protocol.server.content;

import java.util.Collection;
import java.util.Map;

public class SimpleWorkspaceInfo implements WorkspaceInfo {
    private Map<String, CollectionProvider> collectionProviders;
    private String name;
    private String id;
    
    /* (non-Javadoc)
     * @see org.apache.abdera.protocol.server.content.WorkspaceInfo#getId()
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CollectionProvider getCollectionProvider(String id) {
      return collectionProviders.get(id);
    }

    public Map<String, CollectionProvider> getCollectionProviders() {
        return collectionProviders;
    }

    public void setCollectionProviders(Map<String, CollectionProvider> contentProviders) {
        this.collectionProviders = contentProviders;
    }

    
}
