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
package org.apache.abdera.server.cache;

import org.apache.abdera.server.RequestContext;
import org.apache.abdera.server.ResponseContext;

/**
 * Provides an interface for a server-side cache, out of which responses can
 * be served rather than always going back to the RequestHandler.
 */
public interface Cache {
  
  /**
   * Initialize the cache
   */
  void initialize();
  
  /**
   * Clear the cache
   */
  void clear();
  
  /**
   * Clear the specified entry from the cache
   */
  void clear(String cacheKey);
  
  /**
   * Retrieve a cache entry, or null 
   */
  CacheEntry get(String cacheKey);
  
  /**
   * Set a cache entry
   */
  void set(String cacheKey, CacheEntry entry);
  
  /**
   * Returns the current disposition for the cache entry
   */
  CacheEntry.CacheDisposition getDisposition(String cacheKey);
  
  /**
   * Process the request from the cache.  The method will return either
   * FRESH, STALE or TRANSPARENT.  A response of FRESH indicates that the
   * cache was able to successfully handle the request.  STALE means that 
   * the Cache Entry for the request should not be used.  TRANSPARENT means
   * that the Cache Entry either does not exist or that the cache cannot
   * process the request for a variety of reasons
   */
  CacheEntry.CacheDisposition process(
    RequestContext request, 
    ResponseContext response, 
    String cacheKey);
  
}
