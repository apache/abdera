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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.protocol.client.Response;
import org.apache.abdera.protocol.util.CachingInputStream;

public class InMemoryCachedResponse 
  extends CachedResponseBase
  implements CachedResponse {

  private int status = 0;
  private String status_text = null;
  private String uri = null;
  private Map<String,List<String>> headers = null;
  private ByteArrayOutputStream out;
  private InputStream in = null;
  
  public InMemoryCachedResponse(
    Cache cache,
    CacheKey key,
    Response response) 
      throws IOException {
    super(key,cache);
    this.status = response.getStatus();
    this.status_text = response.getStatusText();
    this.uri = response.getUri();
    String[] headers = response.getHeaderNames();
    for (String header : headers) {
      if (!isNoCacheOrPrivate(header, response)) {
        String[] values = response.getHeaders(header);
        List<String> list = Arrays.asList(values);
        getHeaders().put(header, list);
      }
    }
    getServerDate();
    getInitialAge();
    out = new ByteArrayOutputStream();
    in = new CachingInputStream(response.getInputStream(),out,true);
    response.setInputStream(in);
  }

  private Map<String,List<String>> getHeaders() {
    if (headers == null)
      headers = new HashMap<String,List<String>>();
    return headers;
  }
  
  private boolean isNoCacheOrPrivate(
    String header, 
    Response response) {
      String[] no_cache_headers = response.getNoCacheHeaders();
      String[] private_headers = response.getPrivateHeaders();
      return contains(no_cache_headers,header) ||
             contains(private_headers,header);
  }

  private boolean contains(String[] headers, String header) {
    if (headers != null) {
      for (String h : headers) {
        if (h.equals(header)) return true;
      }
    } 
    return false;
  }
  
  public String getHeader(String header) {
    List<String> values = getHeaders().get(header);
    return (values != null) ? values.get(0) : null;
  }

  public String[] getHeaderNames() {
    return getHeaders().keySet().toArray(new String[getHeaders().size()]);
  }

  public String[] getHeaders(String header) {
    List<String> values = getHeaders().get(header);
    return (values != null) ?
      values.toArray(new String[values.size()]) :
      new String[0];
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
    return new ByteArrayInputStream(out.toByteArray());
  }

  @Override
  public void setInputStream(InputStream in) {
    throw new UnsupportedOperationException();
  }
    
}
