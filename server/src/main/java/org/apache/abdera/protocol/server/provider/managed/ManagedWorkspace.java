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
package org.apache.abdera.protocol.server.provider.managed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;

public class ManagedWorkspace implements WorkspaceInfo {

    private final ManagedProvider provider;

    private String title = "Abdera";

    public ManagedWorkspace(ManagedProvider provider) {
        this.provider = provider;
    }

    public Collection<CollectionInfo> getCollections(RequestContext request) {
        CollectionAdapterManager cam = provider.getCollectionAdapterManager(request);
        List<CollectionInfo> collections = new ArrayList<CollectionInfo>();
        try {
            Map<String, FeedConfiguration> map = cam.listAdapters();
            for (FeedConfiguration config : map.values())
                collections.add(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return collections;
    }

    public String getTitle(RequestContext request) {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Workspace asWorkspaceElement(RequestContext request) {
        Workspace workspace = request.getAbdera().getFactory().newWorkspace();
        workspace.setTitle(getTitle(null));
        for (CollectionInfo collection : getCollections(request))
            workspace.addCollection(collection.asCollectionElement(request));
        return workspace;
    }
}
