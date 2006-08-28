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
package org.apache.abdera.server.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.server.ResponseContext;

public abstract class BaseResponseContext 
  implements ResponseContext {

  protected final static byte NOCACHE = 1;
  protected final static byte NOSTORE = 2;
  protected final static byte NOTRANSFORM = 4;
  protected final static byte MUSTREVALIDATE = 8;
  protected final static byte PROXYREVALIDATE = 16;
  protected final static byte PUBLIC = 32;
  protected final static byte PRIVATE = 64;
  protected static final String[] EMPTY = new String[0];
  
  protected int status = 0;
  protected String status_text = null;
  
  protected Map<String,List<Object>> headers = null;
  protected byte flags = 0;
  protected long max_age = -1;
  protected long smax_age = -1;
  protected List<String> no_cache_headers = null;
  protected List<String> private_headers = null;

  public void removeHeader(String name) {
    Map<String,List<Object>> headers = getHeaders();
    headers.remove(name);
  }
  
  public void setHeader(String name, Object value) {
    Map<String,List<Object>> headers = getHeaders();
    List<Object> values = new ArrayList<Object>();
    values.add(value);
    headers.put(name, values);
  }
  
  public void setHeader(String name, Object... vals) {
    Map<String,List<Object>> headers = getHeaders();
    List<Object> values = new ArrayList<Object>();
    for (Object value : vals) {
      values.add(value);
    }
    headers.put(name, values);
  }
  
  public void addHeader(String name, Object value) {
    Map<String,List<Object>> headers = getHeaders();
    List<Object> values = new ArrayList<Object>();
    if (values == null) {
      values = new ArrayList<Object>();
      headers.put(name, values);
    } 
    values.add(value);
  }
  
  public void addHeaders(String name, Object... vals) {
    Map<String,List<Object>> headers = getHeaders();
    List<Object> values = new ArrayList<Object>();
    if (values == null) {
      values = new ArrayList<Object>();
      headers.put(name,values);
    }
    for (Object value : vals) {
      values.add(value);
    }
  }
  
  public Map<String, List<Object>> getHeaders() {
    if (headers == null)
      headers = new HashMap<String,List<Object>>();
    return headers;
  }
    
  public Date getDateHeader(String name) {
    Map<String,List<Object>> headers = getHeaders();
    List<Object> values = headers.get(name);
    if (values != null) {
      for (Object value : values) {
        if (value instanceof Date) 
          return (Date)value;
      }
    }
    return null;
  }
  
  public String getHeader(String name) {
    Map<String,List<Object>> headers = getHeaders();
    List<Object> values = headers.get(name);
    if (values != null && values.size() > 0) 
      return values.get(0).toString();
    return null;
  }

  public List<Object> getHeaders(String name) {
    Map<String,List<Object>> headers = getHeaders();
    return headers.get(name);
  }

  private void append(StringBuffer buf, String value) {
    if (buf.length() > 0) buf.append(", ");
    buf.append(value);
  }
    
  public String getCacheControl() {
    StringBuffer buf = new StringBuffer();
    if (isPublic()) append(buf,"public");
    if (isPrivate()) append(buf,"private");
    if (private_headers != null && private_headers.size() > 0) {
      buf.append("=\"");
      for (String header : private_headers) {
        append(buf,header);
      }
      buf.append("\"");
    }
    if (isNoCache()) append(buf,"no-cache");
    if (no_cache_headers != null && no_cache_headers.size() > 0) {
      buf.append("=\"");
      for (String header : no_cache_headers) {
        append(buf,header);
      }
      buf.append("\"");
    }
    if (isNoStore()) append(buf,"no-store");
    if (isMustRevalidate()) append(buf,"must-revalidate");
    if (isNoTransform()) append(buf, "no-transform");
    if (getMaxAge() != -1) append(buf, "max-age=" + getMaxAge());
    if (getSMaxAge() != -1) append(buf, "smax-age=" + getMaxAge());
    return buf.toString();
  }

  public long getAge() {
    String value = getHeader("Age");
    return (value != null) ? Long.parseLong(value) : -1;
  }
  
  public void setAge(long age) {
    if (age == -1) {
      removeHeader("Age"); 
      return;
    }
    setHeader("Age", String.valueOf(age));
  }
  
  public String getContentLanguage() {
    return getHeader("Content-Language");
  }
  
  public void setContentLanguage(String language) {
    if (language == null) {
      removeHeader("Content-Language");
      return;
    }
    setHeader("Content-Language", language);
  }

  public long getContentLength() {
    String value = getHeader("Content-Length");
    return (value != null) ? Long.parseLong(value) : -1;
  }
  
  public void setContentLength(long length) {
    if (length == -1) {
      removeHeader("Content-Length");
      return;
    }
    setHeader("Content-Length", String.valueOf(length));
  }

  public URI getContentLocation() throws URISyntaxException {
    String value = getHeader("Content-Location");
    return (value != null) ? new URI(value) : null;
  }

  public void setContentLocation(String uri) {
    if (uri == null) {
      removeHeader("Content-Location");
      return;
    }
    setHeader("Content-Location", uri);
  }
  
  public MimeType getContentType() throws MimeTypeParseException {
    String value = getHeader("Content-Type");
    return (value != null) ? new MimeType(value) : null;
  }
  
  public void setContentType(String type) {
    if (type == null) {
      removeHeader("Content-Type");
      return;
    }
    setHeader("Content-Type", type);
  }

  public String getEntityTag() {
    return getHeader("ETag");
  }
  
  public void setEntityTag(String etag) {
    if (etag == null) {
      removeHeader("ETag");
      return;
    }
    setHeader("ETag", etag);
  }

  public Date getExpires() {
    return getDateHeader("Expires");
  }

  public void setExpires(Date date) {
    if (date == null) {
      removeHeader("Expires");
      return;
    }
    setHeader("Expires", date);
  }
  
  public Date getLastModified() {
    return getDateHeader("Last-Modified");
  }
  
  public void setLastModified(Date date) {
    if (date == null) {
      removeHeader("Last-Modified");
      return;
    }
    setHeader("Last-Modified", date);
  }

  public URI getLocation() throws URISyntaxException {
    String value = getHeader("Location");
    return (value != null) ? new URI(value) : null;
  }

  public void setLocation(String uri) {
    if (uri == null) {
      removeHeader("Location");
      return;
    }
    setHeader("Location", uri);
  }
  
  public int getStatus() {
    return status;
  }
  
  public void setStatus(int status) {
    this.status = status;
  }

  public String getStatusText() {
    return status_text;
  }
  
  public void setStatusText(String text) {
    this.status_text = text;
  }

  public long getMaxAge() {
    return max_age;
  }
  
  public void setMaxAge(long max_age) {
    this.max_age = max_age;
  }

  public String[] getNoCacheHeaders() {
    if (no_cache_headers == null) return EMPTY;
    return no_cache_headers.toArray(new String[no_cache_headers.size()]);
  }
  
  public void setNoCacheHeaders(String header) {
    if (no_cache_headers == null) no_cache_headers = new ArrayList<String>();
    no_cache_headers.add(header);
  }
  
  public void setNoCacheHeaders(String... headers) {
    if (no_cache_headers == null) no_cache_headers = new ArrayList<String>();
    for (String header : headers) {
      no_cache_headers.add(header);
    }
  }

  public String[] getPrivateHeaders() {
    if (private_headers == null) return EMPTY;
    return private_headers.toArray(new String[private_headers.size()]);
  }

  public void setPrivateHeaders(String header) {
    if (private_headers == null) private_headers = new ArrayList<String>();
    private_headers.add(header);
  }
  
  public void setPrivateHeaders(String... headers) {
    if (private_headers == null) private_headers = new ArrayList<String>();
    for (String header : headers) {
      private_headers.add(header);
    }
  }
  
  public long getSMaxAge() {
    return smax_age;
  }
  
  public void setSMaxAge(long smax_age) {
    this.smax_age = smax_age;
  }
  
  public boolean isMustRevalidate() {
    return (flags & MUSTREVALIDATE) == MUSTREVALIDATE;
  }
  
  public void setMustRevalidate(boolean val) {
    if (isMustRevalidate() && !val)
      flags ^= MUSTREVALIDATE;
    else if (!isMustRevalidate() && val)
      flags |= MUSTREVALIDATE;
  }

  public boolean isNoCache() {
    return (flags & NOCACHE) == NOCACHE;
  }

  public void setNoCache(boolean val) {
    if (isNoCache() && !val)
      flags ^= NOCACHE;
    else if (!isNoCache() && val)
      flags |= NOCACHE;
  }
  
  public boolean isNoStore() {
    return (flags & NOSTORE) == NOSTORE;
  }

  public void setNoStore(boolean val) {
    if (isNoStore() && !val)
      flags ^= NOSTORE;
    else if (!isNoStore() && val)
      flags |= NOSTORE;
  }
  
  public boolean isNoTransform() {
    return (flags & NOTRANSFORM) == NOTRANSFORM;
  }

  public void setNoTransform(boolean val) {
    if (isNoTransform() && !val)
      flags ^= NOTRANSFORM;
    else if (!isNoTransform() && val)
      flags |= NOTRANSFORM;
  }
  
  public boolean isPrivate() {
    return (flags & PRIVATE) == PRIVATE;
  }

  public void setPrivate(boolean val) {
    if (isPrivate() && !val)
      flags ^= PRIVATE;
    else if (!isPrivate() && val)
      flags |= PRIVATE;
  }
  
  public boolean isProxyRevalidate() {
    return (flags & PROXYREVALIDATE) == PROXYREVALIDATE;
  }
  
  public void setProxyRevalidate(boolean val) {
    if (isProxyRevalidate() && !val)
      flags ^= PROXYREVALIDATE;
    else if (!isProxyRevalidate() && val)
      flags |= PROXYREVALIDATE;
  }

  public boolean isPublic() {
    return (flags & PUBLIC) == PUBLIC;
  }

  public void setPublic(boolean val) {
    if (isPublic() && !val)
      flags ^= PUBLIC;
    else if (!isPublic() && val)
      flags |= PUBLIC;
  }  
  
  public String getAllow() {
    return getHeader("Allow");
  }
  
  public void setAllow(String method) {
    setHeader("Allow", method);
  }
  
  public void setAllow(String... methods) {
    StringBuffer buf = new StringBuffer();
    for(String method : methods) {
      if (buf.length() > 0) buf.append(", ");
      buf.append(method);
    }
    setAllow(buf.toString());
  }
}
