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
package org.apache.abdera.protocol.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;

import org.apache.abdera.i18n.io.CharUtils.Profile;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.util.AbstractResponse;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.Messages;
import org.apache.abdera.writer.Writer;

public abstract class AbstractResponseContext
  extends AbstractResponse
  implements ResponseContext {

  protected static final String[] EMPTY = new String[0];
  
  protected int status = 0;
  protected String status_text = null;
  protected Writer writer = null;
  
  protected Map<String,Object[]> headers = null;

  public void removeHeader(String name) {
    Map<String,Object[]> headers = getHeaders();
    headers.remove(name);
  }
  
  public void setEncodedHeader(String name, String charset, String value) {
    setHeader(name, EncodingUtil.encode(value, charset));
  }
  
  public void setEncodedHeader(String name, String charset, String... vals) {
    Object[] evals = new Object[vals.length];
    for (int n = 0; n < vals.length; n++) {
      evals[n] = EncodingUtil.encode(vals[n], charset);
    }
    setHeader(name, evals);
  }
  
  public void setEscapedHeader(String name, Profile profile, String value) {
    setHeader(name,Escaping.encode(value, profile));
  }
  
  public void setHeader(String name, Object value) {
    setHeader(name, new Object[] {value});
  }
  
  public void setHeader(String name, Object... vals) {
    Map<String,Object[]> headers = getHeaders();
    List<Object> values = new ArrayList<Object>();
    for (Object value : vals) {
      values.add(value);
    }
    headers.put(name, values.toArray(new Object[values.size()]));
  }
  
  public void addEncodedHeader(String name, String charset, String value) {
    addHeader(name, EncodingUtil.encode(value, charset));
  }
  
  public void addEncodedHeaders(String name, String charset, String... vals) {
    for (String value : vals) {
      addHeader(name,EncodingUtil.encode(value, charset));
    }
  }
  
  public void addHeader(String name, Object value) {
    addHeader(name, new Object[] {value});
  }
  
  public void addHeaders(String name, Object... vals) {
    Map<String,Object[]> headers = getHeaders();
    Object[] values = headers.get(name);
    List<Object> l = null;
    if (values == null) {
      l = new ArrayList<Object>();
    } else {
      l = Arrays.asList(values);
    }
    for (Object value : vals) {
      l.add(value);
    }
    headers.put(name, l.toArray(new Object[l.size()]));
  }
  
  public Map<String, Object[]> getHeaders() {
    if (headers == null)
      headers = new HashMap<String,Object[]>();
    return headers;
  }
    
  public Date getDateHeader(String name) {
    Map<String,Object[]> headers = getHeaders();
    Object[] values = headers.get(name);
    if (values != null) {
      for (Object value : values) {
        if (value instanceof Date) 
          return (Date)value;
      }
    }
    return null;
  }
  
  public String getHeader(String name) {
    Map<String,Object[]> headers = getHeaders();
    Object[] values = headers.get(name);
    if (values != null && values.length > 0) 
      return values[0].toString();
    return null;
  }

  public Object[] getHeaders(String name) {
    Map<String,Object[]> headers = getHeaders();
    return headers.get(name);
  }

  public String[] getHeaderNames() {
    Map<String,Object[]> headers = getHeaders();
    return headers.keySet().toArray(new String[headers.size()]);
  }
  
  private void append(StringBuffer buf, String value) {
    if (buf.length() > 0) buf.append(", ");
    buf.append(value);
  }
    
  public String getCacheControl() {
    StringBuffer buf = new StringBuffer();
    if (isPublic()) append(buf,"public");
    if (isPrivate()) append(buf,"private");
    if (private_headers != null && private_headers.length > 0) {
      buf.append("=\"");
      for (String header : private_headers) {
        append(buf,header);
      }
      buf.append("\"");
    }
    if (isNoCache()) append(buf,"no-cache");
    if (nocache_headers != null && nocache_headers.length > 0) {
      buf.append("=\"");
      for (String header : nocache_headers) {
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

  public void setAge(long age) {
    if (age == -1) {
      removeHeader("Age"); 
      return;
    }
    setHeader("Age", String.valueOf(age));
  }
  
  public void setContentLanguage(String language) {
    if (language == null) {
      removeHeader("Content-Language");
      return;
    }
    setHeader("Content-Language", language);
  }

  public void setContentLength(long length) {
    if (length == -1) {
      removeHeader("Content-Length");
      return;
    }
    setHeader("Content-Length", String.valueOf(length));
  }

  public void setContentLocation(String uri) {
    if (uri == null) {
      removeHeader("Content-Location");
      return;
    }
    setHeader("Content-Location", uri);
  }
  
  public void setSlug(String slug) {
    if (slug == null) {
      removeHeader("Slug");
      return;
    }
    if (slug.indexOf((char)10) > -1 ||
        slug.indexOf((char)13) > -1)
      throw new IllegalArgumentException(
        Messages.get("SLUG.BAD.CHARACTERS"));
    setEscapedHeader("Slug", Profile.ASCIISANSCRLF, slug);
  }
  
  public void setContentType(String type) {
    setContentType(type,null);
  }
  
  public void setContentType(String type, String charset) {
    if (type == null) {
      removeHeader("Content-Type");
      return;
    }
    try {
      MimeType mimeType = new MimeType(type);
      if (charset != null) mimeType.setParameter("charset", charset);
      setHeader("Content-Type", mimeType.toString());
    } catch (Exception e) {}
  }
  
  public void setEntityTag(String etag) {
    setEntityTag(new EntityTag(etag));
  }
  
  public void setEntityTag(EntityTag etag) {
    if (etag == null) {
      removeHeader("ETag");
      return;
    }
    setHeader("ETag", etag.toString());
  }

  public void setExpires(Date date) {
    if (date == null) {
      removeHeader("Expires");
      return;
    }
    setHeader("Expires", date);
  }
  
  public void setLastModified(Date date) {
    if (date == null) {
      removeHeader("Last-Modified");
      return;
    }
    setHeader("Last-Modified", date);
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
  
  public void setWriter(Writer writer) {
    this.writer = writer;
  }
  
}
