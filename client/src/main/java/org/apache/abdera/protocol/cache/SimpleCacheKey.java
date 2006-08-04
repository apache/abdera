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

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Simple cache key based on an md5 hash of the URI.
 * A more complete implementation should take the Vary header into account
 */
public class SimpleCacheKey implements CacheKey {

  private static final long serialVersionUID = 8757289485580165536L;
  private byte[] key = null;
  
  private MessageDigest md = null;
  
  public SimpleCacheKey(String uri) {
    try {
      md = MessageDigest.getInstance("md5");
      key = md.digest(uri.getBytes());
    } catch (Exception e) {}
  }
  
  public byte[] getKey() {
    return key;
  }

  public boolean isMatch(CacheKey cacheKey) {
    return (cacheKey instanceof SimpleCacheKey) ?
      MessageDigest.isEqual(
        key, ((SimpleCacheKey)cacheKey).key) : false;
  }
  
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof CacheKey) ? isMatch((CacheKey)obj) : false;
  }
  
  @Override
  public int hashCode() {
    return 31 + Arrays.hashCode(key);
  }

}
