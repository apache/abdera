package org.apache.abdera.protocol.server.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;

public abstract class AbstractWorkspaceProvider 
  extends AbstractProvider 
  implements WorkspaceManager {
  
  protected Resolver<Target> targetResolver;
  protected TargetBuilder targetBuilder;
  protected Collection<WorkspaceInfo> workspaces;

  protected WorkspaceManager getWorkspaceManager(
    RequestContext request) {
      return this;
  }
    
  protected Resolver<Target> getTargetResolver(
    RequestContext request) {
      return targetResolver;
  }
  
  protected TargetBuilder getTargetBuilder(
    RequestContext request) {
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
