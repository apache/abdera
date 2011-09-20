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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.abdera2.common.http.EntityTag;

/**
 * Base implementation for WorkspaceManager implementations
 */
public abstract class AbstractWorkspaceManager 
  implements WorkspaceManager {

    protected Collection<WorkspaceInfo> workspaces;
    public static final String COLLECTION_ADAPTER_ATTRIBUTE = "collectionProvider";

    public Collection<WorkspaceInfo> getWorkspaces(RequestContext request) {
        return workspaces;
    }

    public void setWorkspaces(Collection<WorkspaceInfo> workspaces) {
        this.workspaces = workspaces;
    }

    public void addWorkspace(WorkspaceInfo workspace) {
        if (workspaces == null) {
            workspaces = new HashSet<WorkspaceInfo>();
        }
        workspaces.add(workspace);
    }
    
    public Date getLastModified() {
      return new Date();
    }
    
    public EntityTag getEntityTag() { 
      return null;
    }

}
