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
package org.apache.abdera.protocol.client.cache;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.ClientResponse;

public abstract class InMemoryCache 
  extends CacheBase {

  protected transient Map<CacheKey,CachedResponse> cache;
  protected transient final Map<String,SimpleCacheKey> keycache = 
    new LRUMap<String,SimpleCacheKey>(20,0.75f,true);

  protected InMemoryCache(Abdera abdera) {
    super(abdera);
  }
  
  protected void setMap(Map<CacheKey,CachedResponse> map) {
    cache = Collections.synchronizedMap(map);
  }
  
  @Override
  protected CachedResponse createCachedResponse(
    ClientResponse response, 
    CacheKey key) 
      throws IOException {
    return new InMemoryCachedResponse(abdera, this, key, response);
  }

  public void clear() {
    cache.clear();
  }

  public CachedResponse get(
    CacheKey key) {   
      return cache.get(key);
  }

  public CacheKey getCacheKey(
    String uri, 
    RequestOptions options) {
      return getCacheKey(uri,options,null);
  }
  
  public CacheKey getCacheKey(
    String uri,
    RequestOptions options,
    ClientResponse response) {
      //TODO: We need a complete solution that takes the Vary header into account
    SimpleCacheKey key = keycache.get(uri);
    if (key == null) {
      key = new SimpleCacheKey(uri);
      keycache.put(uri, key);
    } 
    return key;
  }

  public void remove(
    CacheKey key) {
      cache.remove(key);
  }

  protected void add(
    CacheKey key, 
    CachedResponse response) {
      cache.put(key, response);
  }

}
