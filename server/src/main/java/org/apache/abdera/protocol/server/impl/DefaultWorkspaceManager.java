package org.apache.abdera.protocol.server.impl;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;


public class DefaultWorkspaceManager extends AbstractWorkspaceManager {

  public CollectionAdapter getCollectionAdapter(RequestContext request) {
    return (CollectionAdapter) request.getAttribute(Scope.REQUEST, StructuredTargetResolver.COLLECTION_PROVIDER_ATTRIBUTE);
  }

}
