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
package org.apache.abdera2.activities.protocol;

import java.util.Collection;

import javax.security.auth.Subject;

import org.apache.abdera2.common.misc.Resolver;
import org.apache.abdera2.common.protocol.DefaultWorkspaceManager;
import org.apache.abdera2.common.protocol.Request;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.RouteManager;
import org.apache.abdera2.common.protocol.Target;
import org.apache.abdera2.common.protocol.TargetBuilder;
import org.apache.abdera2.common.protocol.TargetType;
import org.apache.abdera2.common.protocol.WorkspaceInfo;
import org.apache.abdera2.common.protocol.WorkspaceManager;

public class DefaultActivitiesProvider
  extends AbstractActivitiesProvider {

  protected WorkspaceManager workspaceManager;
  protected Resolver<Target,RequestContext> targetResolver;
  protected Resolver<Subject,Request> subjectResolver;
  protected TargetBuilder<?> targetBuilder;
  protected RouteManager<TargetType,RequestContext> routeManager;
  
  public DefaultActivitiesProvider() {
    this("/");
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public DefaultActivitiesProvider(String base) {
    if (base == null) base = "/";
    workspaceManager = new DefaultWorkspaceManager();
    routeManager =
        new RouteManager()
          .addRoute("stream", base + ":stream", TargetType.TYPE_COLLECTION)
          .addRoute("activity", base + ":stream/:activity", TargetType.TYPE_ENTRY)
        ;
    targetBuilder = routeManager;
    targetResolver = routeManager;
  }
  
  @SuppressWarnings("rawtypes")
  public RouteManager getRouteManager() {
      return routeManager;
  }

  protected Resolver<Target,RequestContext> getTargetResolver(RequestContext request) {
      return targetResolver;
  }

  public void setTargetResolver(Resolver<Target,RequestContext> targetResolver) {
      this.targetResolver = targetResolver;
  }

  protected Resolver<Subject,Request> getSubjectResolver(RequestContext request) {
      return subjectResolver;
  }

  public void setSubjectResolver(Resolver<Subject,Request> subjectResolver) {
      this.subjectResolver = subjectResolver;
  }

  public Resolver<Target,RequestContext> getTargetResolver() {
      return targetResolver;
  }

  public Resolver<Subject,Request> getSubjectResolver() {
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

  @SuppressWarnings("rawtypes")
  public TargetBuilder getTargetBuilder() {
      return (TargetBuilder)targetBuilder;
  }

  public void setTargetBuilder(TargetBuilder<?> targetBuilder) {
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

  @SuppressWarnings("rawtypes")
  protected TargetBuilder getTargetBuilder(Request request) {
      return (TargetBuilder)targetBuilder;
  }
}
