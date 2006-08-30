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
package org.apache.abdera.protocol.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.protocol.client.ClientException;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.protocol.util.MethodHelper;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

public class InMemoryCachedResponse 
  extends CachedResponseBase
  implements CachedResponse {

  private String method = null;
  private int status = 0;
  private String status_text = null;
  private String uri = null;
  private Map<String,List<Object>> headers = null;
  private byte[] buf = null;
  
  public InMemoryCachedResponse(
    Cache cache,
    CacheKey key,
    ClientResponse response) 
      throws IOException {
    super(key,cache);
    this.method = response.getMethod();
    this.status = response.getStatus();
    this.status_text = response.getStatusText();
    this.uri = response.getUri();
    this.headers = MethodHelper.getCacheableHeaders(response);
    CacheControlUtil.parseCacheControl(this.getHeader("Cache-Control"), this);
    getServerDate();
    getInitialAge();
    cacheStream(response.getInputStream());
    response.setInputStream(getInputStream());
  }

  /**
   * This is terribly inefficient, but it is an in-memory cache
   * that is being used by parsers that incrementally consume 
   * InputStreams at different rates.  There's really no other
   * way to do it.
   */
  private void cacheStream(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
    byte[] buf = new byte[1024];
    int m = -1;
    while((m = in.read(buf)) != -1) {
      out.write(buf,0,m);
    }
    this.buf = out.toByteArray();
  }
  
  public Map<String,List<Object>> getHeaders() {
    if (headers == null)
      headers = new HashMap<String,List<Object>>();
    return headers;
  }
  
  public String getMethod() {
    return method;
  }
  
  public String getHeader(String header) {
    List<Object> values = getHeaders().get(header);
    return (values != null) ? (String)values.get(0) : null;
  }

  public String[] getHeaderNames() {
    return getHeaders().keySet().toArray(new String[getHeaders().size()]);
  }

  public List<Object> getHeaders(String header) {
    return getHeaders().get(header);
  }

  public int getStatus() {
    return status;
  }

  public String getStatusText() {
    return status_text;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(this.buf);
  }

  @Override
  public void setInputStream(InputStream in) {
    throw new UnsupportedOperationException();
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
}
