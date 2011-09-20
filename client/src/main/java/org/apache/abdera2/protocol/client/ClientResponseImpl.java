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
package org.apache.abdera2.protocol.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.MimeType;

import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Method;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.ResponseType;
import org.apache.abdera2.common.http.WebLink;
import org.apache.abdera2.common.io.Compression;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.lang.Lang;
import org.apache.abdera2.common.text.Codec;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

class ClientResponseImpl 
  implements ClientResponse {

  private final HttpResponse response;
  private final Session session;
  private final Method method;
  private final HttpContext localContext;
  private BufferedHttpEntity buffer;
  
  public ClientResponseImpl(
    Session session,
    HttpResponse response,
    String method,
    HttpContext localContext) {
    this.response = response;
    this.session = session;
    this.method = Method.get(method,true);
    this.localContext = localContext;
  }
  
  public Session getSession() {
    return session;
  }
  
  public EntityTag getEntityTag() {
    String et = getHeader("ETag");
    return et != null ? new EntityTag(et) : null;
  }

  public ResponseType getType() {
    return ResponseType.select(response.getStatusLine().getStatusCode());
  }

  public int getStatus() {
    return response.getStatusLine().getStatusCode();
  }

  public String getStatusText() {
    return response.getStatusLine().getReasonPhrase();
  }

  public Date getLastModified() {
    return getDateHeader("Last-Modified");
  }

  public long getContentLength() {
    try {
      HttpEntity entity = getEntity();
      return entity != null ? entity.getContentLength() : 0;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public String getAllow() {
    return getHeader("Allow");
  }

  public IRI getLocation() {
    String l = getHeader("Location");
    return l != null ? new IRI(l) : null;
  }

  public long getAge() {
    String a = getHeader("Age");
    return a != null ? Long.parseLong(a) : -1;
  }

  public Date getExpires() {
    return getDateHeader("Expires");
  }

  public String getHeader(String name) {
    Header header = response.getFirstHeader(name);
    return header != null ? header.getValue() : null;
  }

  public String getDecodedHeader(String name) {
    String val = getHeader(name);
    return val != null ? Codec.decode(val) : null;
  }

  @SuppressWarnings("unchecked")
  public Iterable<Object> getHeaders(String name) {
    Header[] headers = response.getHeaders(name);
    if (headers == null) return Collections.EMPTY_LIST;
    List<Object> ret = new ArrayList<Object>();
    for (int n = 0; n < headers.length; n++) 
      ret.add(headers[n].getValue());
    return ret;
  }

  public Iterable<String> getDecodedHeaders(String name) {
    Iterable<Object> headers = getHeaders(name);
    List<String> ret = new ArrayList<String>();
    for (Object h : headers)
      ret.add(Codec.decode(h.toString()));
    return ret;
  }

  public Iterable<String> getHeaderNames() {
    Header[] headers = response.getAllHeaders();
    Set<String> set = new HashSet<String>();
    for (Header header : headers)
      set.add(header.getName());
    return set;
  }

  public String getSlug() {
    return getHeader("Slug");
  }

  public MimeType getContentType() {
    try {
      String ct = getHeader("Content-Type");
      return ct != null ? new MimeType(ct) : null;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public IRI getContentLocation() {
    String ct = getHeader("Location");
    return ct != null ? new IRI(ct) : null;
  }

  public String getContentLanguage() {
    String ct = getHeader("Content-Language");
    return ct != null ? new Lang(ct).toString() : null;
  }

  public Date getDateHeader(String name) {
    try {
      String ct = getHeader(name);
      return ct != null ? DateUtils.parseDate(ct) : null;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public CacheControl getCacheControl() {
    String cc = getHeader("Cache-Control");
    return cc != null ? new CacheControl(cc) : null;
  }

  @SuppressWarnings("unchecked")
  public Iterable<Authentication> getAuthentication() {
    String cc = getHeader("WWW-Authenticate");
    return cc != null ? Authentication.parse(cc) : Collections.EMPTY_LIST;
  }

  public Method getMethod() {
    return method;
  }

  public String getUri() {
    HttpUriRequest currentReq = 
      (HttpUriRequest) localContext.getAttribute( 
        ExecutionContext.HTTP_REQUEST);
    HttpHost currentHost = (HttpHost)  localContext.getAttribute( 
        ExecutionContext.HTTP_TARGET_HOST);
    String currentUrl = currentHost.toURI() + currentReq.getURI();
    return currentUrl;
  }

  public void release() {
    try {
      EntityUtils.consume(response.getEntity());
    } catch (Throwable t) {}
  }

  private HttpEntity getEntity() throws IOException {
    if (buffer == null) {
      HttpEntity entity = response.getEntity();
      if (entity != null)
        buffer = new BufferedHttpEntity(response.getEntity());
    }
    return buffer;
  }
  
  public InputStream getInputStream() throws IOException {
    InputStream in = null;
    String ce = getHeader("Content-Encoding");
    HttpEntity entity = getEntity();
    in = entity != null ? entity.getContent() : null;
    if (ce != null && in != null)
        in = Compression.wrap(in, ce);
    return in;
  }

  public Reader getReader() throws IOException {
    return getReader(getCharacterEncoding());
  }

  public Reader getReader(String charset) throws IOException {
    InputStream in = getInputStream();
    if (in == null) return null;
    Reader reader = 
      charset != null ? 
          new InputStreamReader(getInputStream(),charset) :
          new InputStreamReader(getInputStream());
    return reader;
  }

  public Date getServerDate() {
    return getDateHeader("Date");
  }

  public String getCharacterEncoding() {
    return getHeader("Content-Encoding");
  }

  public void writeTo(OutputStream out) throws IOException {
    InputStream in = getInputStream();
    byte[] buf = new byte[1024];
    int r = -1;
    while((r = in.read(buf)) > -1)
      out.write(buf,0,r);
  }

  public Iterable<WebLink> getWebLinks() {
    List<WebLink> links = new ArrayList<WebLink>();
    Iterable<Object> headers = this.getHeaders("Link");
    for (Object obj : headers) {
      Iterable<WebLink> list = WebLink.parse(obj.toString());
      for (WebLink link : list)
        links.add(link);
    }
    return links;
  }
  
  public Iterable<Preference> getPrefer() {
    List<Preference> links = new ArrayList<Preference>();
    Iterable<Object> headers = this.getHeaders("Prefer");
    for (Object obj : headers) {
      Iterable<Preference> list = Preference.parse(obj.toString());
      for (Preference link : list)
        links.add(link);
    }
    return links;
  }
  
  public Iterable<Preference> getPreferApplied() {
    List<Preference> links = new ArrayList<Preference>();
    Iterable<Object> headers = this.getHeaders("Preference-Applied");
    for (Object obj : headers) {
      Iterable<Preference> list = Preference.parse(obj.toString());
      for (Preference link : list)
        links.add(link);
    }
    return links;
  }
}