package org.apache.abdera.protocol.server.content;

import java.util.Map;

public interface WorkspaceInfo<T> {

  String getId();

  String getName();

  Map<String, CollectionProvider<T>> getCollectionProviders();

  CollectionProvider<T> getCollectionProvider(String id);

}
