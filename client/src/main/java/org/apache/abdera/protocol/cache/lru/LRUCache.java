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
package org.apache.abdera.protocol.cache.lru;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.abdera.protocol.cache.Cache;
import org.apache.abdera.protocol.cache.CacheKey;
import org.apache.abdera.protocol.cache.CachedResponse;
import org.apache.abdera.protocol.cache.InMemoryCache;

@SuppressWarnings("serial")
public class LRUCache
  extends InMemoryCache
  implements Cache {

  private final static int DEFAULT_SIZE = 10;
  
  public LRUCache() {
    this(DEFAULT_SIZE);
  }
  
  public LRUCache(final int size) {
    setMap(
      new LinkedHashMap<CacheKey,CachedResponse>(size,0.75f,true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<CacheKey,
                                                      CachedResponse> eldest)
        {
          return size() > size;
        }
      }
    );
  }
  
}
