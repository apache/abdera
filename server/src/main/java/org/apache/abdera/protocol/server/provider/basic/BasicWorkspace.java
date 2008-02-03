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
package org.apache.abdera.protocol.server.provider.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.CollectionAdapterManager;
import org.apache.abdera.protocol.server.impl.SimpleCollectionInfo;

class BasicWorkspace 
  implements WorkspaceInfo {
  
  private final BasicProvider provider;
  
  BasicWorkspace(BasicProvider provider) {
    this.provider = provider;
  }
  
  public Collection<CollectionInfo> getCollections(RequestContext request) {
    CollectionAdapterManager cam = provider.getCollectionAdapterManager(request.getAbdera());
    List<CollectionInfo> collections = new ArrayList<CollectionInfo>();
    try {
      Map<String,Properties> map = cam.listAdapters();
      for (Map.Entry<String,Properties> entry : map.entrySet()) {
        Properties properties = entry.getValue();
        String href = properties.getProperty(BasicAdapter.PROP_NAME_FEED_URI);
        String title = properties.getProperty(BasicAdapter.PROP_NAME_TITLE);
        SimpleCollectionInfo col = new SimpleCollectionInfo(title,href,"application/atom+xml;type=entry");
        collections.add(col);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return collections;
  }

  public String getTitle(RequestContext request) {
    return "Abdera";
  }

  public Workspace asWorkspaceElement(RequestContext request) {
    Workspace workspace = request.getAbdera().getFactory().newWorkspace();
    workspace.setTitle(getTitle(null));
    for (CollectionInfo collection : getCollections(request))
      workspace.addCollection(collection.asCollectionElement(request));
    return workspace;
  }
}
