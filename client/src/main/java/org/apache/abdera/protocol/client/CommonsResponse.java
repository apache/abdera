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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.util.ContentEncodingUtil;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

public class CommonsResponse 
  extends AbstractClientResponse
  implements ClientResponse {

  private final HttpMethod method;
    
  protected CommonsResponse(Abdera abdera, HttpMethod method) {
    super(abdera);
    if (method.isRequestSent()) 
      this.method = method;
    else throw new IllegalStateException();
    parse_cc();
    getServerDate();
  }

  public HttpMethod getHttpMethod() {
    return method;
  }
  
  public String getMethod() {
    return method.getName();
  }
  
  public int getStatus() {
    return method.getStatusCode();
  }
  
  public String getStatusText() {
    return method.getStatusText();
  }
  
  public void release() {
    method.releaseConnection();
  }
  
  public String getHeader(String header) {
    Header h = method.getResponseHeader(header);
    if (h != null) 
      return h.getValue();
    else return null;
  }

  public List<Object> getHeaders(String header) {
    Header[] headers = method.getResponseHeaders(header);
    List<Object> values = new ArrayList<Object>();
    for (Header h : headers) {
      values.add(h.getValue());
    }
    return java.util.Collections.unmodifiableList(values);
  }
  
  public Map<String,List<Object>> getHeaders() {
    Header[] headers = method.getResponseHeaders();
    Map<String,List<Object>> map = new HashMap<String,List<Object>>();
    for (Header header : headers) {
      List<Object> values = map.get(header.getName());
      if (values == null) {
        values = new ArrayList<Object>();
        map.put(header.getName(),values);
      }
      values.add(header.getValue());
    }
    return java.util.Collections.unmodifiableMap(map);
  }
  
  public String[] getHeaderNames() {
    Header[] headers = method.getResponseHeaders();
    List<String> list = new ArrayList<String>();
    for (Header h : headers) {
      String name = h.getName();
      if (!list.contains(name))
        list.add(name);
    }
    return list.toArray(new String[list.size()]);
  }

  public String getUri() {
    try {
      return method.getURI().toString();
    } catch (URIException e) {}
    return null; // shouldn't happen
  }

  public InputStream getInputStream() throws IOException {
    if (in == null) {
      String ce = getHeader("Content-Encoding");
      in = method.getResponseBodyAsStream();
      if (ce != null)
        in = ContentEncodingUtil.getDecodingInputStream(in, ce);
    }
    return super.getInputStream();
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
