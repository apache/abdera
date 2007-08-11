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

import org.apache.abdera.protocol.Response;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.i18n.iri.IRI;

public abstract class AbstractResponse 
  implements Response {

  protected int flags = 0;
  protected String[] nocache_headers = null;
  protected String[] private_headers = null;
  protected long max_age = -1;
  protected long smax_age = -1;
  
  public long getAge() {
    String value = getHeader("Age");
    try {
      return (value != null) ? Long.parseLong(value) : -1;
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  public String getAllow() {
    return getHeader("Allow");
  }

  public String getCacheControl() {
    return getHeader("Cache-Control");
  }

  public String getContentLanguage() {
    return getHeader("Content-Language");
  }

  public long getContentLength() {
    String value = getHeader("Content-Length");
    try {
      return (value != null) ? Long.parseLong(value) : -1;
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  public IRI getContentLocation() {
    return getUriHeader("Content-Location");
  }
  
  public String getSlug() {
    return getDecodedHeader("Slug");
  }

  public MimeType getContentType() {
    try {
      String value = getHeader("Content-Type");
      return (value != null) ? new MimeType(value) : null;
    } catch (javax.activation.MimeTypeParseException e) {
      throw new org.apache.abdera.util.MimeTypeParseException(e);
    }
  }

  public EntityTag getEntityTag() {
    String etag = getHeader("ETag");
    return (etag != null) ? EntityTag.parse(getHeader("ETag")) : null;
  }

  public Date getExpires() {
    return getDateHeader("Expires");
  }

  public Date getLastModified() {
    return getDateHeader("Last-Modified");
  }

  public IRI getLocation() {
    return getUriHeader("Location");
  }

  public long getMaxAge() {
    return max_age;
  }

  public String[] getNoCacheHeaders() {
    return nocache_headers;
  }

  public String[] getPrivateHeaders() {
    return private_headers;
  }

  public long getSMaxAge() {
    return smax_age;
  }

  public ResponseType getType() {
    return ResponseType.select(getStatus());
  }

  public IRI getUriHeader(String name) {
    String value = getHeader(name);
    return (value != null) ? new IRI(value) : null;
  }

  public boolean isMustRevalidate() {
    return check(REVALIDATE);
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

  public boolean isPrivate() {
    return check(PRIVATE);
  }

  public boolean isProxyRevalidate() {
    return check(PROXYREVALIDATE);
  }

  public boolean isPublic() {
    return check(PUBLIC);
  }

  public void setMaxAge(long max_age) {
    this.max_age = max_age;
  }
  
  public void setMustRevalidate(boolean val) {
    toggle(val, REVALIDATE);
  }
  
  public void setProxyRevalidate(boolean val) {
    toggle(val, PROXYREVALIDATE);
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
  
  public void setPublic(boolean val) {
    toggle(val, PUBLIC);
  }
  
  public void setPrivate(boolean val) {
    if (val) flags |= PRIVATE;
    else if (isPrivate()) flags ^= PRIVATE;
  }
  
  public void setPrivateHeaders(String... headers) {
    this.private_headers = headers;
  }
  
  public void setNoCacheHeaders(String... headers) {
    this.nocache_headers = headers;
  }

  private boolean check(int flag) {
    return (flags & flag) == flag;
  }
  
  private void toggle(boolean val, int flag) {
    if (val) flags |= flag;
    else flags &= ~flag;
  }
  
  public String getDecodedHeader(String header) {
    return Escaping.decode(EncodingUtil.decode(getHeader(header)));
  }
  
}
