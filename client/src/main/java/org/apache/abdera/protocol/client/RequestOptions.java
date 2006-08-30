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
package org.apache.abdera.protocol.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

public class RequestOptions {

  private byte flags = 0;
  private final static byte NOCACHE = 1;
  private final static byte NOSTORE = 2;
  private final static byte NOTRANSFORM = 4;
  private final static byte ONLYIFCACHED = 8;
  private long max_age = -1;
  private long max_stale = -1;
  private long min_fresh = -1;
  private boolean noLocalCache = false;
  private boolean revalidateAuth = false;
  
  private Map<String,List<String>> headers = null;  
  
  public RequestOptions() {}

  public RequestOptions(Date ifModifiedSince) {
    setIfModifiedSince(ifModifiedSince);
  }
  
  public RequestOptions(String ifNoneMatch) {
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(String... ifNoneMatch) {
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(Date ifModifiedSince, String ifNoneMatch) {
    setIfModifiedSince(ifModifiedSince);
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(Date ifModifiedSince, String... ifNoneMatch) {
    setIfModifiedSince(ifModifiedSince);
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(boolean no_cache) {
    setNoCache(no_cache);
  }
  
  private Map<String,List<String>> getHeaders() {
    if (headers == null)
      headers = new HashMap<String,List<String>>();
    return headers;
  }

  private String combine(String ... values) {
    StringBuffer v = new StringBuffer();
    for (String val : values) {
      if (v.length() > 0)
        v.append(", ");
      v.append(val);
    }
    return v.toString();
  }
    
  /**
   * The difference between this and getNoCache is that this
   * only disables the local cache without affecting the 
   * Cache-Control header.
   */
  public boolean getUseLocalCache() {
    return !noLocalCache;
  }
  
  public void setUseLocalCache(boolean use_cache) {
    this.noLocalCache = !use_cache;
  }
  
  public void setContentType(String value) {
    setHeader("Content-Type", value);
  }
  
  public void setContentType(MimeType value) {
    setHeader("Content-Type", value.toString());
  }
  
  public String getContentType() {
    return getHeader("Content-Type");
  }
  
  public void setAuthorization(String auth) {
    setHeader("Authorization", auth);
  }
  
  public String getAuthorization() {
    return getHeader("Authorization");
  }
  
  public void setHeader(String header, String value) {
    if (value != null)
      setHeader(header, new String[] {value});
    else
      removeHeaders(header);
  }

  public void setHeader(String header, String... values) {
    if (values != null && values.length > 0) {
      List<String> list = Arrays.asList(new String[] {combine(values)});
      getHeaders().put(header, list);
    } else {
      removeHeaders(header);
    }
  }
  
  public void setDateHeader(String header, Date value) {
    if (value != null) 
      setHeader(header, DateUtil.formatDate(value));
    removeHeaders(header);
  }
  
  public void addHeader(String header, String value) {
    if (value != null)
      addHeader(header, new String[] {value});
  }
  
  public void addHeader(String header, String... values) {
    if (values == null || values.length == 0) return;
    List<String> list = getHeaders().get(header);
    String value = combine(values);
    if (list != null) {
      if (!list.contains(value)) 
        list.add(value);
    } else {
      setHeader(header, new String[] {value});
    }
  }

  public void addDateHeader(String header, Date value) {
    if (value == null) return;
    addHeader(header, DateUtil.formatDate(value));
  }
  
  public String getHeader(String header) {
    List<String> list = getHeaders().get(header);
    return (list != null) ? list.get(0) : null;
  }
  
  public String[] getHeaders(String header) {
    List<String> list = getHeaders().get(header);
    return (list != null) ? 
      list.toArray(new String[list.size()]) : 
      new String[0];
  }
  
  public Date getDateHeader(String header) {
    String val = getHeader(header);
    try {
      return (val != null) ? DateUtil.parseDate(val) : null;
    } catch (DateParseException e) {
      throw new ClientException(e);
    }
  }

  public String[] getHeaderNames() {
    Set<String> set = getHeaders().keySet();
    return set.toArray(new String[set.size()]);
  }
  
  public String getIfMatch() {
    return getHeader("If-Match");
  }
  
  public void setIfMatch(String entity_tag) {
    setHeader("If-Match", entity_tag);
  }
  
  public void setIfMatch(String... entity_tags) {
    setHeader("If-Match", entity_tags);
  }
  
  public String getIfNoneMatch() {
    return getHeader("If-None-Match");
  }
  
  public void setIfNoneMatch(String entity_tag) {
    setHeader("If-None-Match", entity_tag);
  }
  
  public void setIfNoneMatch(String... entity_tags) {
    setHeader("If-None-Match", entity_tags);
  }
  
  public Date getIfModifiedSince() {
    return getDateHeader("If-Modified-Since");
  }
  
  public void setIfModifiedSince(Date date) {
    setDateHeader("If-Modified-Since", date);
  }
  
  public Date getIfUnmodifiedSince() {
    return getDateHeader("If-Unmodified-Since");
  }
  
  public void setIfUnmodifiedSince(Date date) {
    setDateHeader("If-Unmodified-Since", date);
  }
  
  public String getAccept() {
    return getHeader("Accept");
  }
  
  public void setAccept(String accept) {
    setAccept(new String[] {accept});
  }
  
  public void setAccept(String... accept) {
    setHeader("Accept", combine(accept));
  }
  
  public String getAcceptLanguage() {
    return getHeader("Accept-Language");
  }
  
  public void setAcceptLanguage(String accept) {
    setAcceptLanguage(new String[] {accept});
  }
  
  public void setAcceptLanguage(String... accept) {
    setHeader("Accept-Language", combine(accept));
  }
  
  public String getAcceptCharset() {
    return getHeader("Accept-Charset");
  }
  
  public void setAcceptCharset(String accept) {
    setAcceptCharset(new String[] {accept});
  }
  
  public void setAcceptCharset(String... accept) {
    setHeader("Accept-Charset", combine(accept));
  }
  
  public String getAcceptEncoding() {
    return getHeader("Accept-Encoding");
  }
  
  public void setAcceptEncoding(String accept) {
    setAcceptEncoding(new String[] {accept});
  }
  
  public void setAcceptEncoding(String... accept) {
    setHeader("Accept-Encoding", combine(accept));
  }
  
  public void setSlug(String slug) {
    setHeader("Slug", slug);
  }
  
  public String getSlug() {
    return getHeader("Slug");
  }
  
  public boolean getOnlyIfCached() {
    return (flags & ONLYIFCACHED) == ONLYIFCACHED;
  }
  
  public void setOnlyIfCached(boolean condition) {
    if (condition) flags |= ONLYIFCACHED;
    else if (getOnlyIfCached()) flags ^= ONLYIFCACHED;
  }
  
  public boolean getNoTransform() {
    return (flags & NOTRANSFORM) == NOTRANSFORM;
  }
  
  public void setNoTransform(boolean condition) {
    if (condition) flags |= NOTRANSFORM;
    else if (getNoTransform()) flags ^= NOTRANSFORM;
  }
  
  public boolean getNoCache() {
    return (flags & NOCACHE) == NOCACHE;
  }
  
  public void setNoCache(boolean condition) {
    if (condition) flags |= NOCACHE;
    else if (getNoCache()) flags ^= NOCACHE;
  }
  
  public boolean getNoStore() {
    return (flags & NOSTORE) == NOSTORE;
  }
  
  public void setNoStore(boolean condition) {
    if (condition) flags |= NOSTORE;
    else if (getNoStore()) flags ^= NOSTORE;
  }
  
  public long getMaxAge() {
    return max_age;
  }
  
  public void setMaxAge(long condition) {
    this.max_age = condition;
  }
  
  public long getMaxStale() {
    return max_stale;
  }
  
  public void setMaxStale(long condition) {
    this.max_stale = condition;
  }
  
  public long getMinFresh() {
    return this.min_fresh;
  }
  
  public void setMinFresh(long condition) {
    this.min_fresh = condition;
  }
  
  public void setCacheControl(String cc) {
    CacheControlUtil.parseCacheControl(cc, this);
  }
  
  public void removeHeaders(String name) {
    getHeaders().remove(name);
  }
  
  public String getCacheControl() {
    StringBuffer buf = new StringBuffer();
    if (getNoCache()) append(buf,"no-cache");
    if (getNoStore()) append(buf,"no-store");
    if (getNoTransform()) append(buf, "no-transform");
    if (getOnlyIfCached()) append(buf, "only-if-cached");
    if (getMaxAge() != -1) append(buf, "max-age=" + getMaxAge());
    if (getMaxStale() != -1) append(buf, "max-stale=" + getMaxStale());
    if (getMinFresh() != -1) append(buf, "min-fresh=" + getMinFresh());
    return buf.toString();
  }
  
  private void append(StringBuffer buf, String value) {
    if (buf.length() > 0) buf.append(", ");
    buf.append(value);
  }
  
  public boolean getRevalidateWithAuth() {
    return revalidateAuth;
  }
  
  public void setRevalidateWithAuth(boolean revalidateAuth) {
    this.revalidateAuth= revalidateAuth;
  }
}
