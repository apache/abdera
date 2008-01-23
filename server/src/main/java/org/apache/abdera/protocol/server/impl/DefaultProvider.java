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

import javax.security.auth.Subject;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;

public class DefaultProvider 
  extends AbstractProvider {
  
  private WorkspaceManager workspaceManager;
  private Resolver<Target> targetResolver;
  private Resolver<Subject> subjectResolver;
  
  public DefaultProvider() {
    workspaceManager = new DefaultWorkspaceManager();
    targetResolver = new StructuredTargetResolver(workspaceManager);
  }
  
  public DefaultProvider(String base) {
    workspaceManager = new DefaultWorkspaceManager();
    targetResolver = new StructuredTargetResolver(workspaceManager, base);
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
  
  protected WorkspaceManager getWorkspaceManager(
    RequestContext request) {
      return getWorkspaceManager();
  }
  
  public WorkspaceManager getWorkspaceManager() {
    return workspaceManager;
  }

  public void setWorkspaceManager(WorkspaceManager workspaceManager) {
    this.workspaceManager = workspaceManager;
  }
  
  public void addWorkspace(WorkspaceInfo workspace) {
    ((DefaultWorkspaceManager) getWorkspaceManager()).addWorkspace(workspace);
  }
}
