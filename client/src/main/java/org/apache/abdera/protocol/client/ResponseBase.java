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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

public abstract class ResponseBase implements Response {

  protected int flags = 0;
  protected final static int NOCACHE = 1;
  protected final static int NOSTORE = 2;
  protected final static int NOTRANSFORM = 4;
  protected final static int PUBLIC = 8;
  protected final static int PRIVATE = 16;
  protected final static int REVALIDATE = 32;
  protected String[] nocache_headers = null;
  protected String[] private_headers = null;
  protected long max_age = -1;
  protected InputStream in = null;
  protected Date response_date = null;
  protected Date now = new Date(); 
  
  public InputStream getInputStream() throws IOException {
    return in;
  }

  public void setInputStream(InputStream in) {
    this.in = in;
  }

  public static ResponseType getResponseClass(int status) {
    if (status >= 200 && status < 300) return ResponseType.SUCCESS;
    if (status >= 300 && status < 400) return ResponseType.REDIRECTION;
    if (status >= 400 && status < 500) return ResponseType.CLIENT_ERROR;
    if (status >= 500 && status < 600) return ResponseType.SERVER_ERROR;
    return ResponseType.UNKNOWN;
  }
  
  public ResponseType getResponseClass() {
    return getResponseClass(getStatus());
  }
  
  public Date getDateHeader(String header) {
    try {
      String value = getHeader(header);
      if (value != null)
        return DateUtil.parseDate(value);
      else return null;
    } catch (DateParseException e) {
      throw new ClientException(e); // server likely returned a bad date format
    }
  }
  
  public MimeType getContentType() {
    try {
      String type = getHeader("Content-Type");
      if (type != null) 
        return new MimeType(type);
    } catch (Exception e) {}
    return null;  // shouldn't happen
  } 
  
  public String getEntityTag() {
    return getHeader("ETag");
  }

  public Date getLastModified() {
    return getDateHeader("Last-Modified");
  }

  public Date getServerDate() {
    if (response_date == null) {
      Date date = getDateHeader("Date");
      response_date = (date != null) ? date : now;
    }
    return response_date;
  }
  
  public String getContentLocation() {
    return getHeader("Content-Location");
  }

  public String getLocation() {
    return getHeader("Location");
  }

  public long getAge() {
    String age = getHeader("Age");
    return (age != null) ? Long.parseLong(age) : -1;
  }

  public Date getExpires() {
    return getDateHeader("Expires");
  }

  public long getMaxAge() {
    return max_age;
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

  public boolean isPublic() {
    return (flags & PUBLIC) == PUBLIC;
  }
  
  public String[] getNoCacheHeaders() {
    return nocache_headers;
  }
  
  public String[] getPrivateHeaders() {
    return private_headers;
  }
  
  public void setMaxAge(long max_age) {
    this.max_age = max_age;
  }
  
  public void setMustRevalidate(boolean val) {
    if (val) flags |= REVALIDATE;
    else if (isMustRevalidate()) flags ^= REVALIDATE;
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
  
  protected void parse_cc() {
    String cc = getHeader("Cache-Control");
    if (cc != null)
      CacheControlUtil.parseCacheControl(cc, this);
  }

}
