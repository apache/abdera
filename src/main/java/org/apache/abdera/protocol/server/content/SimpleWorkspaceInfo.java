package org.apache.abdera.protocol.server.content;

import java.util.Collection;
import java.util.Map;

public class SimpleWorkspaceInfo implements WorkspaceInfo {
    private Map<String, CollectionProvider> collectionProviders;
    private String name;
    private String id;
    
    /* (non-Javadoc)
     * @see org.apache.abdera.protocol.server.content.WorkspaceInfo#getId()
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CollectionProvider getCollectionProvider(String id) {
      return collectionProviders.get(id);
    }

    public Map<String, CollectionProvider> getCollectionProviders() {
        return collectionProviders;
    }

    public void setCollectionProviders(Map<String, CollectionProvider> contentProviders) {
        this.collectionProviders = contentProviders;
    }

    
}
