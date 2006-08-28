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
package org.apache.abdera.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.util.CacheControlParser;
import org.apache.abdera.server.RequestContext;
import org.apache.abdera.server.ServerConstants;

public class ServletRequestContext 
  implements RequestContext, ServerConstants {
  
  private Abdera abdera = null;
  private HttpServletRequest servletRequest = null;
  private long maxage = -1;
  private long maxstale = -1;
  private long minfresh = -1;
  private boolean nocache = false;
  private boolean nostore = false;
  private boolean notransform = false;
  private String method = null;
    
  public ServletRequestContext(
    Abdera abdera,
    HttpServletRequest request) {
      this.abdera = abdera;
      this.servletRequest = request;
      get_ccp();
  }
  
  public String getMethod() {
    if (method == null) {
      String o = getHeader(X_OVERRIDE_HEADER);
      method = (o == null) ? servletRequest.getMethod() : o;
    }
    return method;
  }
  
  public URI getUri() {
    URI uri = null;
    try {
      StringBuffer buf = 
        new StringBuffer(
          servletRequest.getRequestURI());
      if (servletRequest.getQueryString() != null)
        buf.append("?" + servletRequest.getQueryString());
      uri = new URI(buf.toString());
    } catch (URISyntaxException e) {}
    return uri;
  }
  
  private String getHost() {
    String host = abdera.getConfiguration().getConfigurationOption(
      "org.apache.abdera.protocol.server.Host");
    return (host != null) ? 
      host : 
      servletRequest.getServerName();
  }
  
  private int getPort() {
    String port = abdera.getConfiguration().getConfigurationOption(
      "org.apache.abdera.protocol.server.Port");
    return (port != null) ? 
      Integer.parseInt(port) : 
      servletRequest.getLocalPort();
  }
  
  public URI getBaseUri() {
    StringBuffer buffer = 
      new StringBuffer(
        (servletRequest.isSecure())?
          "https":"http");
    buffer.append(getHost());
    int port = getPort();
    if (port != 80) {
      buffer.append(":");
      buffer.append(port);
    }
    buffer.append(servletRequest.getServletPath());
    
    // So that .resolve() works appropriately.
    buffer.append("/");
    try {
      return new URI(buffer.toString());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
  
  public String getHeader(String name) {
    return servletRequest.getHeader(name);
  }

  @SuppressWarnings("unchecked")
  public List<String> getHeaders(String name) {
    return Collections.list(servletRequest.getHeaders(name));
  }

  @SuppressWarnings("unchecked")
  public List<String> getHeaderNames() {
    return  Collections.list(servletRequest.getHeaderNames());
  }
    
  public URI getPathInfo() {
    try {
      String pathInfo = servletRequest.getPathInfo();
      if(pathInfo == null)  {
        pathInfo = "";
      }
      return new URI(pathInfo);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
  
  public String getParameter(String name) {
    return  servletRequest.getParameter(name);
  }
    
  public List<String> getParameters(String name) {
   return Arrays.asList(servletRequest.getParameterValues(name));  
  }

  @SuppressWarnings("unchecked") 
  public List<String> getParameterNames() { 
    return Collections.list(servletRequest.getParameterNames());
  }
        
  public InputStream getInputStream() throws IOException {
    return servletRequest.getInputStream();
  }

  public String getAccept() {
    return servletRequest.getHeader("Accept");
  }

  public String getAcceptCharset() {
    return servletRequest.getHeader("Accept-Charset");
  }

  public String getAcceptEncoding() {
    return servletRequest.getHeader("Accept-Encoding");
  }

  public String getAcceptLanguage() {
    return servletRequest.getHeader("Accept-Language");
  }

  public String getAuthorization() {
    return servletRequest.getHeader("Authorization");
  }

  public String getCacheControl() {
    return servletRequest.getHeader("Cache-Control");
  }

  public String getContentType() {
    return servletRequest.getHeader("Content-Type");
  }

  public Date getDateHeader(String name) {
    return new Date(servletRequest.getDateHeader(name));
  }

  public String getIfMatch() {
    return servletRequest.getHeader("If-Match");
  }

  public Date getIfModifiedSince() {
    return getDateHeader("If-Modified-Since");
  }

  public String getIfNoneMatch() {
    return servletRequest.getHeader("If-None-Match");
  }

  public Date getIfUnmodifiedSince() {
    return getDateHeader("If-Unmodified-Since");
  }

  private void get_ccp() {
    String cc = getCacheControl();
    if (cc != null) {
      CacheControlParser ccparser = 
        new CacheControlParser(getCacheControl());
      for(String directive : ccparser) {
        directive = directive.toLowerCase();
        if (directive.equals("max-age")) {
          maxage = Long.parseLong(ccparser.getValue(directive));
        } else if (directive.equals("max-stale")) {
          maxstale = Long.parseLong(ccparser.getValue(directive));
        } else if (directive.equals("min-fresh")) {
          minfresh = Long.parseLong(ccparser.getValue(directive));
        } else if (directive.equals("no-cache")) {
          nocache = true;
        } else if (directive.equals("no-store")) {
          nostore = true;
        } else if (directive.equals("no-transform")) {
          notransform = true;
        }
      }
    }
  }
  
  public long getMaxAge() {
    return maxage;
  }

  public long getMaxStale() {
    return maxstale;
  }

  public long getMinFresh() {
    return minfresh;
  }

  public boolean getNoCache() {
    return nocache;
  }

  public boolean getNoStore() {
    return nostore;
  }

  public boolean getNoTransform() {
    return notransform;
  }

}
