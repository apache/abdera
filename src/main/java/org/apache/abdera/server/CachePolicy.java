package org.apache.abdera.server;

import java.util.Date;

public interface CachePolicy {

  boolean isPublic();
  
  boolean isCacheable();
  
  boolean isStorable();
  
  boolean isTransformable();
  
  boolean mustRevalidate();
  
  boolean proxyMustRevalidate();
  
  int getMaxAge();
  
  int getSharedMaxAge();
  
  Date getExpires();
  
}
