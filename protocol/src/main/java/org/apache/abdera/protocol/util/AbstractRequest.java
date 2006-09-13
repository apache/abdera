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
import java.util.List;

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
  
  public String getSlug() {
    return EncodingUtil.decode(getHeader("Slug"));
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
    return check(NOCACHE); 
  }

  public boolean isNoStore() {
    return check(NOSTORE);
  }

  public boolean isNoTransform() {
    return check(NOTRANSFORM);
  }

  public boolean isOnlyIfCached() {
    return check(ONLYIFCACHED);
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

  private boolean check(int flag) {
    return (flags & flag) == flag;
  }
  
  private void toggle(boolean val, int flag) {
    if (val) flags |= flag;
    else flags &= ~flag;
  }
  
  public void setNoCache(boolean val) {
    toggle(val, NOCACHE);
  }
  
  public void setNoStore(boolean val) {
    toggle(val, NOSTORE);
  }
  
  public void setNoTransform(boolean val) {
    toggle(val, NOTRANSFORM);
  }
  
  public void setOnlyIfCached(boolean val) {
    toggle(val, ONLYIFCACHED);
  }

  public String getDecodedHeader(String header) {
    return EncodingUtil.decode(getHeader(header));
  }
  
  public List<String> getDecodedHeaders(String header) {
    List<String> headers = getHeaders(header);
    String[] vals = new String[headers.size()];
    for (int n = 0; n < headers.size(); n++) {
      vals[n] = EncodingUtil.decode(headers.get(n));
    }
    return java.util.Arrays.asList(vals);
  }
}
