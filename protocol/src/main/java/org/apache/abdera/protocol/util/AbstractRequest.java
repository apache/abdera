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
package org.apache.abdera.protocol.util;

import java.util.Date;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.protocol.Request;

public abstract class AbstractRequest implements Request {

  protected int flags = 0;
  protected long max_age = -1;
  protected long max_stale = -1;
  protected long min_fresh = -1;
  
  public String getAccept() {
    return getHeader("Accept");
  }

  public String getAcceptCharset() {
    return getHeader("Accept-Charset");
  }

  public String getAcceptEncoding() {
    return getHeader("Accept-Encoding");
  }

  public String getAcceptLanguage() {
    return getHeader("Accept-Language");
  }

  public String getAuthorization() {
    return getHeader("Authorization");
  }

  public String getCacheControl() {
    return getHeader("Cache-Control");
  }

  public MimeType getContentType() throws MimeTypeParseException {
    String value = getHeader("Content-Type");
    return (value != null) ? new MimeType(value) : null;
  }

  public String getIfMatch() {
    return getHeader("If-Match");
  }

  public Date getIfModifiedSince() {
    return getDateHeader("If-Modified-Since");
  }

  public String getIfNoneMatch() {
    return getHeader("If-None-Match");
  }

  public Date getIfUnmodifiedSince() {
    return getDateHeader("If-Unmodified-Since");
  }

  public long getMaxAge() {
    return max_age;
  }

  public long getMaxStale() {
    return max_stale;
  }

  public long getMinFresh() {
    return min_fresh;
  }

  public boolean isNoCache() {
    return (flags & NOCACHE) == NOCACHE; 
  }

  public boolean isNoStore() {
    return (flags & NOSTORE) == NOSTORE;
  }

  public boolean isNoTransform() {
    return (flags & NOTRANSFORM) == NOTRANSFORM;
  }

  public boolean isOnlyIfCached() {
    return (flags & ONLYIFCACHED) == ONLYIFCACHED;
  }

  public void setMaxAge(long max_age) {
    this.max_age = max_age;
  }
  
  public void setMaxStale(long max_stale) {
    this.max_stale = max_stale;
  }
  
  public void setMinFresh(long min_fresh) {
    this.min_fresh = min_fresh;
  }
  
  public void setNoCache(boolean val) {
    if (val) flags |= NOCACHE;
    else if (isNoCache()) flags ^= NOCACHE;
  }
  
  public void setNoStore(boolean val) {
    if (val) flags |= NOSTORE;
    else if (isNoStore()) flags ^= NOSTORE;
  }
  
  public void setNoTransform(boolean val) {
    if (val) flags |= NOTRANSFORM;
    else if (isNoTransform()) flags ^= NOTRANSFORM;
  }
  
  public void setOnlyIfCached(boolean val) {
    if (val) flags |= ONLYIFCACHED;
    else if (isOnlyIfCached()) flags ^= ONLYIFCACHED;
  }
}
