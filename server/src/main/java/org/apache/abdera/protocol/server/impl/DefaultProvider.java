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

import javax.security.auth.Subject;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;

/**
 * The DefaultProvider is the default Provider implementation for Abdera. It supports multiple collections and assumes a
 * simple http://.../{collection}/{entry} URL structure. Media-link entries are not supported.
 */
public class DefaultProvider extends AbstractProvider {

    protected WorkspaceManager workspaceManager;
    protected Resolver<Target> targetResolver;
    protected Resolver<Subject> subjectResolver;
    protected TargetBuilder targetBuilder;
    protected RouteManager routeManager;

    public DefaultProvider() {
        this("/");
    }

    public DefaultProvider(String base) {
        if (base == null) {
            base = "/";
        }
        workspaceManager = new DefaultWorkspaceManager();
        routeManager =
            new RouteManager().addRoute("service", base, TargetType.TYPE_SERVICE).addRoute("feed",
                                                                                           base + ":collection",
                                                                                           TargetType.TYPE_COLLECTION)
                .addRoute("entry", base + ":collection/:entry", TargetType.TYPE_ENTRY)
                .addRoute("categories", base + ":collection/:entry;categories", TargetType.TYPE_CATEGORIES);

        targetBuilder = routeManager;
        targetResolver = routeManager;
    }

    public RouteManager getRouteManager() {
        return routeManager;
    }

    protected Resolver<Target> getTargetResolver(RequestContext request) {
        return targetResolver;
    }

    public void setTargetResolver(Resolver<Target> targetResolver) {
        this.targetResolver = targetResolver;
    }

    protected Resolver<Subject> getSubjectResolver(RequestContext request) {
        return subjectResolver;
    }

    public void setSubjectResolver(Resolver<Subject> subjectResolver) {
        this.subjectResolver = subjectResolver;
    }

    public Resolver<Target> getTargetResolver() {
        return targetResolver;
    }

    public Resolver<Subject> getSubjectResolver() {
        return subjectResolver;
    }

    protected WorkspaceManager getWorkspaceManager(RequestContext request) {
        return getWorkspaceManager();
    }

    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

    public void setWorkspaceManager(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    public TargetBuilder getTargetBuilder() {
        return targetBuilder;
    }

    public void setTargetBuilder(TargetBuilder targetBuilder) {
        this.targetBuilder = targetBuilder;
    }

    public void addWorkspace(WorkspaceInfo workspace) {
        ((DefaultWorkspaceManager)getWorkspaceManager()).addWorkspace(workspace);
    }

    public void addWorkspaces(Collection<WorkspaceInfo> workspaces) {
        for (WorkspaceInfo w : workspaces) {
            ((DefaultWorkspaceManager)getWorkspaceManager()).addWorkspace(w);
        }
    }

    protected TargetBuilder getTargetBuilder(RequestContext request) {
        return (TargetBuilder)targetBuilder;
    }
}
