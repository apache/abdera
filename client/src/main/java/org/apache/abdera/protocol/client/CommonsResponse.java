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
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;

import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

public class CommonsResponse 
  extends AbstractClientResponse
  implements ClientResponse {

  private HttpMethod method = null;
    
  protected CommonsResponse(HttpMethod method) {
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
      in = method.getResponseBodyAsStream();
      String ce = getHeader("Content-Encoding");
      if (ce != null) {
        // multiple encodings may be applied, they're listed in the order
        // they were applied, so we need to walk the list backwards
        String[] encodings = CacheControlUtil.splitAndTrim(ce, ",", false);
        for (int n = encodings.length -1; n >= 0; n--) {
          if ("gzip".equalsIgnoreCase(encodings[n]) ||
              "x-gzip".equalsIgnoreCase(encodings[n])) {
            in = new GZIPInputStream(in);
          } else if ("deflate".equalsIgnoreCase(encodings[n])) {
            in = new InflaterInputStream(in);
          } else if ("zip".equalsIgnoreCase(encodings[n])) {
            in = new ZipInputStream(in);
          } else {
            throw new IOException("Unsupported Content-Encoding");
          }
        }
      } 
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
