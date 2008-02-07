package org.apache.abdera.protocol.server.impl;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.RequestContext.Scope;


public class DefaultWorkspaceManager extends AbstractWorkspaceManager {

  public static final String COLLECTION_ADAPTER_ATTRIBUTE = "collectionProvider";

  public CollectionAdapter getCollectionAdapter(RequestContext request) {
    String path = request.getTargetBasePath() + request.getTargetPath();
    
    // Typically this happens when a Resolver wants to override the CollectionAdapter being used
    CollectionAdapter ca = (CollectionAdapter) request.getAttribute(Scope.REQUEST, COLLECTION_ADAPTER_ATTRIBUTE);
    if (ca != null) {
      return ca;
    }
    
    for (WorkspaceInfo wi : workspaces) {
      for (CollectionInfo ci : wi.getCollections(request)) {
        String href = ci.getHref(request);
        if (href != null && path.startsWith(href)) {
          return (CollectionAdapter) ci;
        }
      }
    }
    
    return null; 
  }

}
