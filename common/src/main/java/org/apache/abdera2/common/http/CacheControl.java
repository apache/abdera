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
package org.apache.abdera2.common.http;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class CacheControl implements Serializable {

  private static final long serialVersionUID = 3554586802963893228L;
  public final static int NOCACHE = 1,
                          NOSTORE = 2,
                          NOTRANSFORM = 4,
                          PUBLIC = 8,
                          PRIVATE = 16,
                          REVALIDATE = 32,
                          PROXYREVALIDATE = 64,
                          ONLYIFCACHED = 128;
  protected int flags = 0;
  protected String[] nocache_headers = null,
                     private_headers = null;
  protected long max_age = -1,
                 max_stale = -1,
                 min_fresh = -1,
                 smax_age = -1,
                 staleiferror = -1,
                 stalewhilerevalidate = -1;
  protected HashMap<String,Object> exts =
    new HashMap<String,Object>();
  
  public CacheControl() {}
  
  public CacheControl(String cc) {
    CacheControlUtil.parseCacheControl(cc, this);
  }
  
  public CacheControl setExtensions(Map<String,Object> exts) {
    this.exts.putAll(exts);
    return this;
  }
  
  public CacheControl setExtension(String name, Object value) {
    exts.put(name,value);
    return this;
  }
  
  public Object getExtension(String name) {
    return exts.get(name);
  }
  
  public String[] listExtensions() {
    return exts.keySet().toArray(new String[exts.size()]);
  }
  
  protected boolean check(int flag) {
    return (flags & flag) == flag;
  }

  protected void toggle(boolean val, int flag) {
      if (val)
          flags |= flag;
      else
          flags &= ~flag;
  }
  
  public void setDefaults() {
    setNoCache(false);
    setNoStore(false);
    setNoTransform(false);
    setOnlyIfCached(false);
    setMaxAge(-1);
    setMaxStale(-1);
    setMinFresh(-1);
    setStaleIfError(-1);
    setStaleWhileRevalidate(-1);
    setMustRevalidate(false);
    setPrivate(false);
    setPublic(false);
    setMaxAge(-1);
  }
  
  public boolean isNoCache() {
    return check(NOCACHE);
  }
  
  public boolean isNoStore() {
      return check(NOSTORE);
  }
  
  public boolean isNoTransform() {
      return check(NOTRANSFORM);
  }
  
  public long getMaxAge() {
      return max_age;
  }
  
  public String[] getNoCacheHeaders() {
    return isNoCache() ? nocache_headers : null;
  }

  public String[] getPrivateHeaders() {
    return isPrivate() ? private_headers : null;
  }

  public long getSMaxAge() {
      return smax_age;
  }

  public boolean isMustRevalidate() {
    return check(REVALIDATE);
  }

  public boolean isPrivate() {
    return check(PRIVATE);
  }

  public boolean isProxyRevalidate() {
    return check(PROXYREVALIDATE);
  }

  public boolean isPublic() {
    return check(PUBLIC);
  }

  public long getStaleIfError() {
    return staleiferror;
  }
  
  public long getStaleWhileRevalidate() {
    return stalewhilerevalidate;
  }
  
  public CacheControl setStaleIfError(long delta) {
    this.staleiferror = Math.max(-1,delta);
    return this;
  }
  
  public CacheControl setStaleWhileRevalidate(long delta) {
    this.stalewhilerevalidate = Math.max(-1,delta);
    return this;
  }
  
  public CacheControl setMaxAge(long max_age) {
    this.max_age = Math.max(-1,max_age);
    return this;
  }

  public CacheControl setMustRevalidate(boolean val) {
    toggle(val,REVALIDATE);
    return this;
  }

  public CacheControl setProxyRevalidate(boolean val) {
    toggle(val,PROXYREVALIDATE);
    return this;
  }

  public CacheControl setNoCache(boolean val) {
    toggle(val,NOCACHE);
    return this;
  }

  public CacheControl setNoStore(boolean val) {
    toggle(val,NOSTORE);
    return this;
  }

  public CacheControl setNoTransform(boolean val) {
    toggle(val,NOTRANSFORM);
    return this;
  }
  
  public CacheControl setPublic(boolean val) {
    toggle(val,PUBLIC);
    return this;
  }

  public CacheControl setPrivate(boolean val) {
    toggle(val,PRIVATE);
    return this;
  }

  public CacheControl setPrivateHeaders(String... headers) {
    this.private_headers = headers;
    return this;
  }

  public CacheControl setNoCacheHeaders(String... headers) {
    this.nocache_headers = headers;
    return this;
  }
  
  public boolean isOnlyIfCached() {
    return check(ONLYIFCACHED);
  }
  
  public long getMaxStale() {
    return max_stale;
  }

  public long getMinFresh() {
    return min_fresh;
  }
  
  public CacheControl setMaxStale(long max_stale) {
    this.max_stale = max_stale;
    return this;
  }

  public CacheControl setMinFresh(long min_fresh) {
    this.min_fresh = min_fresh;
    return this;
  }
  
  public CacheControl setOnlyIfCached(boolean val) {
    toggle(val,ONLYIFCACHED);
    return this;
  }
  
  public String toString() {
    return CacheControlUtil.buildCacheControl(this);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + flags;
    result = prime * result + (int) (max_age ^ (max_age >>> 32));
    result = prime * result + (int) (max_stale ^ (max_stale >>> 32));
    result = prime * result + (int) (min_fresh ^ (min_fresh >>> 32));
    result = prime * result + Arrays.hashCode(nocache_headers);
    result = prime * result + Arrays.hashCode(private_headers);
    result = prime * result + (int) (smax_age ^ (smax_age >>> 32));
    result = prime * result + (int) (staleiferror ^ (staleiferror >>> 32));
    result = prime * result
        + (int) (stalewhilerevalidate ^ (stalewhilerevalidate >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CacheControl other = (CacheControl) obj;
    if (flags != other.flags)
      return false;
    if (max_age != other.max_age)
      return false;
    if (max_stale != other.max_stale)
      return false;
    if (min_fresh != other.min_fresh)
      return false;
    if (!Arrays.equals(nocache_headers, other.nocache_headers))
      return false;
    if (!Arrays.equals(private_headers, other.private_headers))
      return false;
    if (smax_age != other.smax_age)
      return false;
    if (staleiferror != other.staleiferror)
      return false;
    if (stalewhilerevalidate != other.stalewhilerevalidate)
      return false;
    return true;
  }
  
  public static CacheControl NOCACHE() {
    CacheControl cc = new CacheControl();
    cc.setNoCache(true);
    return cc;
  }
  
  public static CacheControl NOSTORE() {
    CacheControl cc = new CacheControl();
    cc.setNoStore(true);
    return cc;
  }
  
  public static CacheControl MAXAGE(long age) {
    CacheControl cc = new CacheControl();
    cc.setMaxAge(age);
    return cc;
  }
  
  public static CacheControl PUBLIC() {
    CacheControl cc = new CacheControl();
    cc.setPublic(true);
    return cc;
  }
  
  public static CacheControl PRIVATE() {
    CacheControl cc = new CacheControl();
    cc.setPrivate(true);
    return cc;
  }
  
}
