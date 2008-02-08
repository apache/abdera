package org.apache.abdera.protocol.server.impl;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.RequestContext.Scope;


public class DefaultWorkspaceManager extends AbstractWorkspaceManager {
  // URI reserved delimiter characters (gen-delims) from RFC 3986 section 2.2
  private static final String URI_GEN_DELIMS = ":/?#[]@";
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
        if (path.equals(href) ||
          (href != null && 
           path.startsWith(href) &&
           URI_GEN_DELIMS.contains(path.substring(href.length(), href.length() + 1)))) {
          return (CollectionAdapter) ci;
        }
      }
    }
    
    return null; 
  }

}
