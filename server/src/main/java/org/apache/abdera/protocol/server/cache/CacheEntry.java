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
package org.apache.abdera.protocol.server.cache;

import org.apache.abdera.protocol.server.ResponseContext;

public interface CacheEntry extends ResponseContext {

  public static enum CacheDisposition {
    STALE,        // the cache entry is stale and should not be used
    FRESH,        // the cache entry is fresh and usable
    TRANSPARENT   // the cache entry either does not exist, or the request should not be processed by the cache
  }
  
  /**
   * Returns the current disposition of this cache entry
   */
  CacheDisposition getDisposition();
  
}
