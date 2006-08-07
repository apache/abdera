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
package org.apache.abdera.protocol.cache;

import java.io.IOException;

import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.Response;
import org.apache.abdera.protocol.util.CacheControlUtil;

public abstract class CacheBase 
  implements Cache {

  public CachedResponse get(String uri, RequestOptions options) {
    return get(getCacheKey(uri,options));
  }

  public CacheDisposition getDisposition(
    String uri, 
    RequestOptions options) {
      CacheKey key = getCacheKey(uri, options);
      CachedResponse response = get(key);
      if (response != null) {
        String[] pragma = options.getHeaders("Pragma");
        for (String s: pragma) {
          if (s.equalsIgnoreCase("no-cache")) {
            return CacheDisposition.TRANSPARENT;
          }
        }
        if (options != null && options.getNoCache()) 
          return CacheDisposition.TRANSPARENT;
        else if (response.isNoCache())
          return CacheDisposition.STALE;
        else if (options != null && options.getOnlyIfCached())
          return CacheDisposition.FRESH;
        else if (response.isMustRevalidate())
          return CacheDisposition.STALE;
        else if (response.getCachedTime() != -1) {
          if (response.isFresh()) {
            long maxAge = (options != null) ? options.getMaxAge() : -1;
            long currentAge = response.getCurrentAge();
            if (maxAge != -1) {
              return (maxAge > currentAge) ? 
                CacheDisposition.FRESH:
                CacheDisposition.STALE;
            }
            long minFresh = (options != null) ? options.getMinFresh() : -1;
            if (minFresh != -1) {
              long lifetime = response.getFreshnessLifetime();
              long age = currentAge;
              return (lifetime < age + minFresh) ? 
                CacheDisposition.TRANSPARENT : 
                CacheDisposition.FRESH;
            }
            return CacheDisposition.FRESH;
          } else {
            long maxStale = (options != null) ? options.getMaxStale() : -1;
            if (maxStale != -1) {
              long howStale = response.getHowStale();
              return (maxStale < howStale) ? 
                CacheDisposition.STALE : 
                CacheDisposition.FRESH;
            } 
            return CacheDisposition.STALE;
          }
        }
      }
      return CacheDisposition.TRANSPARENT;

  }

  public void remove(String uri, RequestOptions options) {
    remove(getCacheKey(uri,options));
  }

  protected abstract void add(
    CacheKey key, 
    CachedResponse response);

  protected abstract CachedResponse createCachedResponse(
    Response response, CacheKey key) throws IOException;
  
  protected abstract CacheKey getCacheKey(
    String uri, 
    RequestOptions options, 
    Response response);
  
  public CachedResponse getIfFreshEnough(
    String uri, 
    RequestOptions options) {
      CacheKey key = getCacheKey(uri,options);
      CachedResponse response = get(key);
      if (!response.isFresh()) {
        // if the milk is only slightly sour, we'll still go ahead and take a drink
        long max_stale = (options != null) ? options.getMaxStale() : -1;
        if (max_stale != -1 && response.getHowStale() > max_stale)
          return null;
      } else {
        long min_fresh = (options != null) ? options.getMinFresh() : -1;
        if (min_fresh != -1 && response.getCurrentAge() < min_fresh)
          return null;
      }
      return response;
  }
  
  public Response update(
    String method,
    String uri, 
    RequestOptions options,
    Response response) {
    CacheKey key = getCacheKey(uri, options,response);
    if ((response != null && response.isNoStore()) || 
        !CacheControlUtil.isIdempotent(method)) {
// TODO: Need to get clarification on this.. if the request is no-store, can
//       the response be cached.
//    if ((response != null && response.isNoStore()) ||
//        options != null && options.getNoStore()) {
     remove(key);
   } else {
     try {
       CachedResponse cachedResponse = createCachedResponse(response, key);
       add(key, cachedResponse);
     } catch (IOException e) {
       throw new CacheException(e);
     }
   }
   return response;
  }

}
