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

import org.apache.abdera.i18n.iri.Constants;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.util.AbstractRequest;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.apache.abdera.util.Messages;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

public class RequestOptions
  extends AbstractRequest 
  implements Request {

  private boolean noLocalCache = false;
  private boolean revalidateAuth = false;
  private boolean useChunked = false;
  private boolean usePostOverride = false;
  private boolean requestException4xx = false;
  private boolean requestException5xx = false;
  private boolean useExpectContinue = true;
  
  private final Map<String,List<String>> headers;  
  
  public RequestOptions() {
    headers = new HashMap<String,List<String>>();
  }

  public RequestOptions(Date ifModifiedSince) {
    this();
    setIfModifiedSince(ifModifiedSince);
  }
  
  public RequestOptions(String ifNoneMatch) {
    this();
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(String... ifNoneMatch) {
    this();
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(Date ifModifiedSince, String ifNoneMatch) {
    this();
    setIfModifiedSince(ifModifiedSince);
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(Date ifModifiedSince, String... ifNoneMatch) {
    this();
    setIfModifiedSince(ifModifiedSince);
    setIfNoneMatch(ifNoneMatch);
  }
  
  public RequestOptions(boolean no_cache) {
    this();
    setNoCache(no_cache);
  }
  
  private Map<String,List<String>> getHeaders() {
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
  
  /**
   * True if the local client cache should be used
   */
  public void setUseLocalCache(boolean use_cache) {
    this.noLocalCache = !use_cache;
  }
  
  /**
   * Set the value of the HTTP Content-Type header
   */
  public void setContentType(String value) {
    setHeader("Content-Type", value);
  }
  
  /**
   * Set the value of the HTTP Content-Type header
   */
  public void setContentType(MimeType value) {
    setHeader("Content-Type", value.toString());
  }
  
  /**
   * Set the value of the HTTP Authorization header
   */
  public void setAuthorization(String auth) {
    setHeader("Authorization", auth);
  }
  
  /**
   * Set the value of a header using proper encoding of non-ascii characters
   */
  public void setEncodedHeader(String header, String charset, String value) {
    setHeader(header, EncodingUtil.encode(value,charset));
  }
  
  /**
   * Set the values of a header using proper encoding of non-ascii characters
   */
  public void setEncodedHeader(String header, String charset, String... values) {
    if (values != null && values.length > 0) {
      for (int n = 0; n < values.length; n++) {
        values[n] = EncodingUtil.encode(values[n], charset);
      }
      List<String> list = Arrays.asList(new String[] {combine(values)});
      getHeaders().put(header, list);
    } else {
      removeHeaders(header);
    }
  }
  
  /**
   * Set the value of the specified HTTP header
   */
  public void setHeader(String header, String value) {
    if (value != null)
      setHeader(header, new String[] {value});
    else
      removeHeaders(header);
  }

  /**
   * Set the value of the specified HTTP header
   */
  public void setHeader(String header, String... values) {
    if (values != null && values.length > 0) {
      List<String> list = Arrays.asList(new String[] {combine(values)});
      getHeaders().put(header, list);
    } else {
      removeHeaders(header);
    }
  }
  
  /**
   * Set the date value of the specified HTTP header
   */
  public void setDateHeader(String header, Date value) {
    if (value != null) 
      setHeader(header, DateUtil.formatDate(value));
    else 
      removeHeaders(header);
  }
  
  /**
   * Similar to setEncodedHeader, but allows for multiple instances of the
   * specified header
   */
  public void addEncodedHeader(String header, String charset, String value) {
    addHeader(header, EncodingUtil.encode(value, charset));
  }
  
  /**
   * Similar to setEncodedHeader, but allows for multiple instances of the
   * specified header
   */
  public void addEncodedHeader(String header, String charset, String... values) {
    if (values == null || values.length == 0) return;
    for (int n = 0; n < values.length; n++) {
      values[n] = EncodingUtil.encode(values[n], charset);
    }
    List<String> list = getHeaders().get(header);
    String value = combine(values);
    if (list != null) {
      if (!list.contains(value)) 
        list.add(value);
    } else {
      setHeader(header, new String[] {value});
    }
  }
  
  /**
   * Similar to setHeader but allows for multiple instances of the specified header
   */
  public void addHeader(String header, String value) {
    if (value != null)
      addHeader(header, new String[] {value});
  }
  
  /**
   * Similar to setHeader but allows for multiple instances of the specified header
   */
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

  /**
   * Similar to setDateHeader but allows for multiple instances of the specified header
   */
  public void addDateHeader(String header, Date value) {
    if (value == null) return;
    addHeader(header, DateUtil.formatDate(value));
  }
  
  /**
   * Returns the text value of the specified header
   */
  public String getHeader(String header) {
    List<String> list = getHeaders().get(header);
    return (list != null) ? list.get(0) : null;
  }
  
  /**
   * Return a listing of text values for the specified header
   */
  public List<String> getHeaders(String header) {
    return getHeaders().get(header);
  }
  
  /**
   * Returns the date value of the specified header
   */
  public Date getDateHeader(String header) {
    String val = getHeader(header);
    try {
      return (val != null) ? DateUtil.parseDate(val) : null;
    } catch (DateParseException e) {
      throw new ClientException(e);
    }
  }

  /**
   * Returns a listing of header names
   */
  public String[] getHeaderNames() {
    Set<String> names = getHeaders().keySet(); 
    return names.toArray(new String[names.size()]);
  }
  
  /**
   * Sets the value of the HTTP If-Match header
   */
  public void setIfMatch(String entity_tag) {
    setHeader("If-Match", entity_tag);
  }
  
  /**
   * Sets the value of the HTTP If-Match header
   */
  public void setIfMatch(String... entity_tags) {
    setHeader("If-Match", entity_tags);
  }
  
  /**
   * Sets the value of the HTTP If-None-Match header
   */
  public void setIfNoneMatch(String entity_tag) {
    setHeader("If-None-Match", entity_tag);
  }
  
  /**
   * Sets the value of the HTTP If-None-Match header
   */
  public void setIfNoneMatch(String... entity_tags) {
    setHeader("If-None-Match", entity_tags);
  }
  
  /**
   * Sets the value of the HTTP If-Modified-Since header
   */
  public void setIfModifiedSince(Date date) {
    setDateHeader("If-Modified-Since", date);
  }
  
  /**
   * Sets the value of the HTTP If-Unmodified-Since header
   */
  public void setIfUnmodifiedSince(Date date) {
    setDateHeader("If-Unmodified-Since", date);
  }
  
  /**
   * Sets the value of the HTTP Accept header
   */
  public void setAccept(String accept) {
    setAccept(new String[] {accept});
  }
  
  /**
   * Sets the value of the HTTP Accept header
   */
  public void setAccept(String... accept) {
    setHeader("Accept", combine(accept));
  }
  
  /**
   * Sets the value of the HTTP Accept-Language header
   */
  public void setAcceptLanguage(String accept) {
    setAcceptLanguage(new String[] {accept});
  }
  
  /**
   * Sets the value of the HTTP Accept-Language header
   */
  public void setAcceptLanguage(String... accept) {
    setHeader("Accept-Language", combine(accept));
  }
  
  /**
   * Sets the value of the HTTP Accept-Charset header
   */
  public void setAcceptCharset(String accept) {
    setAcceptCharset(new String[] {accept});
  }
  
  /**
   * Sets the value of the HTTP Accept-Charset header
   */
  public void setAcceptCharset(String... accept) {
    setHeader("Accept-Charset", combine(accept));
  }
  
  /**
   * Sets the value of the HTTP Accept-Encoding header
   */
  public void setAcceptEncoding(String accept) {
    setAcceptEncoding(new String[] {accept});
  }
  
  /**
   * Sets the value of the HTTP Accept-Encoding header
   */
  public void setAcceptEncoding(String... accept) {
    setHeader("Accept-Encoding", combine(accept));
  }
  
  /**
   * Sets the value of the Atom Publishing Protocol Slug header
   */
  public void setSlug(String slug) {
    if (slug.indexOf((char)10) > -1 ||
        slug.indexOf((char)13) > -1)
      throw new IllegalArgumentException(
        Messages.get("SLUG.BAD.CHARACTERS"));
    setHeader("Slug", Escaping.encode(slug,Constants.ASCIISANSCRLF));
  }
  
  /**
   * Sets the value of the HTTP Cache-Control header
   */
  public void setCacheControl(String cc) {
    CacheControlUtil.parseCacheControl(cc, this);
  }
  
  /**
   * Remove the specified HTTP header
   */
  public void removeHeaders(String name) {
    getHeaders().remove(name);
  }
 
  /**
   * Return the value of the Cache-Control header
   */ 
  public String getCacheControl() {
    return CacheControlUtil.buildCacheControl(this);
  }

  /**
   * Configure the AbderaClient Side cache to revalidate when using Authorization
   */
  public boolean getRevalidateWithAuth() {
    return revalidateAuth;
  }
  
  /**
   * Configure the AbderaClient Side cache to revalidate when using Authorization
   */
  public void setRevalidateWithAuth(boolean revalidateAuth) {
    this.revalidateAuth= revalidateAuth;
  }
  
  /**
   * Should the request use chunked encoding?
   */
  public boolean isUseChunked() {
    return useChunked;
  }
  
  /**
   * Set whether the request should use chunked encoding.
   */
  public void setUseChunked(boolean useChunked) {
    this.useChunked = useChunked;
  }

  /**
   * Set whether the request should use the X-HTTP-Method-Override option
   */
  public void setUsePostOverride(boolean useOverride) {
    this.usePostOverride = useOverride;
  }
  
  /**
   * Return whether the request should use the X-HTTP-Method-Override option
   */
  public boolean isUsePostOverride() {
    return this.usePostOverride;
  }
  
  /**
   * Set whether or not to throw a RequestExeption on 4xx responses
   */
  public void set4xxRequestException(boolean v) {
    this.requestException4xx = v;
  }
  
  /**
   * Return true if a RequestException should be thrown on 4xx responses
   */
  public boolean is4xxRequestException() {
    return this.requestException4xx;
  }
  
  /**
   * Set whether or not to throw a RequestExeption on 5xx responses
   */
  public void set5xxRequestException(boolean v) {
    this.requestException5xx = v;
  }
  
  /**
   * Return true if a RequestException should be thrown on 5xx responses
   */
  public boolean is5xxRequestException() {
    return this.requestException5xx;
  }
  
  /**
   * Set whether or not to use the HTTP Expect-Continue mechanism
   * (enabled by default)
   */
  public void setUseExpectContinue(boolean useExpect) {
    this.useExpectContinue = useExpect;
  }
  
  /**
   * Return true if Expect-Continue should be used
   */
  public boolean isUseExpectContinue() {
    return this.useExpectContinue;
  }
}
