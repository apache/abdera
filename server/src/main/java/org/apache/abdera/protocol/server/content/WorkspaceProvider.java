package org.apache.abdera.protocol.server.content;

import java.util.Collection;

public class WorkspaceProvider extends AbstractWorkspaceProvider {

  private Collection<WorkspaceInfo> workspaces;
  
  public WorkspaceProvider() {
    super(10);
  }
  
  public WorkspaceInfo<?> getWorkspaceInfo(String id) {
    for (WorkspaceInfo wp : workspaces) {
      if (wp.getId().equals(id)) {
        return wp;
      }
    }
    return null;
  }

  public Collection<WorkspaceInfo> getWorkspaces() {
    return workspaces;
  }

  public void setWorkspaces(Collection<WorkspaceInfo> workspaces) {
    this.workspaces = workspaces;
  }

}
