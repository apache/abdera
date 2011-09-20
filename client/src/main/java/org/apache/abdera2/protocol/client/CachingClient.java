package org.apache.abdera2.protocol.client;

public interface CachingClient extends Client {

  long cacheHits();

  long cacheMisses();

  long cacheUpdates();

}