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
package org.apache.abdera2.protocol.client;

import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;

/**
 * Alternative implementation of the Abdera Client that uses client-side 
 * Caching. This is intended to be used as a drop-in replacement to the 
 * base Client when caching capabilities are desired.
 */
public class BasicCachingClient extends BasicClient implements CachingClient {

  private DefaultHttpClient inner;
  private HttpCacheStorage store;
    
  public BasicCachingClient(HttpCacheStorage store) {
    this(DEFAULT_USER_AGENT, store);
  }
  
  public BasicCachingClient(String useragent, HttpCacheStorage store) {
      this.store = store;
      this.client = initClient(useragent);
  }
  
  public BasicCachingClient(DefaultHttpClient client, HttpCacheStorage store) {
      this.store = store;
      this.inner = client;
      this.client = initClient(DEFAULT_USER_AGENT); // TODO:
  }
  
  public BasicCachingClient() {
    super();
  }

  public BasicCachingClient(DefaultHttpClient client) {
    this.inner = client;
    this.client = initClient(DEFAULT_USER_AGENT);
  }

  public BasicCachingClient(String useragent) {
    super(useragent);
  }

  protected HttpClient initClient(String useragent) {
    return initClient(useragent,inner);
  }
  
  protected HttpClient initClient(String useragent, DefaultHttpClient client) {
    inner = client != null? client : (DefaultHttpClient) super.initClient(useragent);
    CacheConfig cacheConfig = new CacheConfig();  
    cacheConfig.setMaxCacheEntries(1000);
    cacheConfig.setMaxObjectSizeBytes(8192);
    cacheConfig.setHeuristicCachingEnabled(true);

    return store != null ? 
        new CachingHttpClient(inner,store,cacheConfig):
        new CachingHttpClient(inner, cacheConfig);
  }
  
  public DefaultHttpClient getDefaultHttpClient() {
    return inner;
  }
  
  public long cacheHits() {
    return ((CachingHttpClient)getClient()).getCacheHits();
  }
  
  public long cacheMisses() {
    return ((CachingHttpClient)getClient()).getCacheMisses();
  }
  
  public long cacheUpdates() {
    return ((CachingHttpClient)getClient()).getCacheUpdates();
  }
}
