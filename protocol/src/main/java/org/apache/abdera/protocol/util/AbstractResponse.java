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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.protocol.Response;

public abstract class AbstractResponse 
  implements Response {

  protected int flags = 0;
  protected String[] nocache_headers = null;
  protected String[] private_headers = null;
  protected long max_age = -1;
  protected long smax_age = -1;
  
  public long getAge() {
    String value = getHeader("Age");
    return (value != null) ? Long.parseLong(value) : -1;
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
    return (value != null) ? Long.parseLong(value) : -1;
  }

  public URI getContentLocation() throws URISyntaxException {
    return getUriHeader("Content-Location");
  }

  public MimeType getContentType() throws MimeTypeParseException {
    String value = getHeader("Content-Type");
    return (value != null) ? new MimeType(value) : null;
  }

  public String getEntityTag() {
    return getHeader("ETag");
  }

  public Date getExpires() {
    return getDateHeader("Expires");
  }

  public Date getLastModified() {
    return getDateHeader("Last-Modified");
  }

  public URI getLocation() throws URISyntaxException {
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
    int status = getStatus();
    if (status >= 200 && status < 300) return ResponseType.SUCCESS;
    if (status >= 300 && status < 400) return ResponseType.REDIRECTION;
    if (status >= 400 && status < 500) return ResponseType.CLIENT_ERROR;
    if (status >= 500 && status < 600) return ResponseType.SERVER_ERROR;
    return ResponseType.UNKNOWN;
  }

  public URI getUriHeader(String name) throws URISyntaxException {
    String value = getHeader(name);
    return (value != null) ? new URI(value) : null;
  }

  public boolean isMustRevalidate() {
    return (flags & REVALIDATE) == REVALIDATE;
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

  public boolean isPrivate() {
    return (flags & PRIVATE) == PRIVATE;
  }

  public boolean isProxyRevalidate() {
    return (flags & PROXYREVALIDATE) == PROXYREVALIDATE;
  }

  public boolean isPublic() {
    return (flags & PUBLIC) == PUBLIC;
  }

  public void setMaxAge(long max_age) {
    this.max_age = max_age;
  }
  
  public void setMustRevalidate(boolean val) {
    if (val) flags |= REVALIDATE;
    else if (isMustRevalidate()) flags ^= REVALIDATE;
  }
  
  public void setProxyRevalidate(boolean val) {
    if (val) flags |= PROXYREVALIDATE;
    else if (isProxyRevalidate()) flags ^= PROXYREVALIDATE;
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
  
  public void setPublic(boolean val) {
    if (val) flags |= PUBLIC;
    else if (isPublic()) flags ^= PUBLIC;
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

}
