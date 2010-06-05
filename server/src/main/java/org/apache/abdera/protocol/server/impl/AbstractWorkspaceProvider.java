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
package org.apache.abdera.protocol.server.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;

/**
 * An abstract base Provider implementation that implements the WorkspaceManager interface. This is intended to be used
 * by Provider's that do not wish to use a separate WorkspaceManager object.
 */
public abstract class AbstractWorkspaceProvider extends AbstractProvider implements WorkspaceManager {

    protected Resolver<Target> targetResolver;
    protected TargetBuilder targetBuilder;
    protected Collection<WorkspaceInfo> workspaces;

    protected WorkspaceManager getWorkspaceManager(RequestContext request) {
        return this;
    }

    protected Resolver<Target> getTargetResolver(RequestContext request) {
        return targetResolver;
    }

    protected TargetBuilder getTargetBuilder(RequestContext request) {
        return targetBuilder;
    }

    protected void setTargetBuilder(TargetBuilder targetBuilder) {
        this.targetBuilder = targetBuilder;
    }

    protected void setTargetResolver(Resolver<Target> targetResolver) {
        this.targetResolver = targetResolver;
    }

    public Collection<WorkspaceInfo> getWorkspaces(RequestContext request) {
        return workspaces;
    }

    public void addWorkspace(WorkspaceInfo workspace) {
        if (workspaces == null) {
            workspaces = new HashSet<WorkspaceInfo>();
        }
        workspaces.add(workspace);
    }

}
