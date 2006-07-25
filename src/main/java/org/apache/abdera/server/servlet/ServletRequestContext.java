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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.abdera.server.RequestContext;

public class ServletRequestContext 
  implements RequestContext {

  public static final String X_OVERRIDE_HEADER = "X-Method-Override";
  
  private HttpServletRequest servletRequest = null;
  
  public ServletRequestContext(
    HttpServletRequest request) {
      this.servletRequest = request;
  }
  
  public String getMethod() {
    return servletRequest.getMethod();
  }
  
  public URI getRequestUri() {
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
  
  public URI getBaseUri() {
    // TODO: this should be done from properties
    StringBuffer buffer = new StringBuffer("http://");
    buffer.append(servletRequest.getServerName());
    if (servletRequest.getLocalPort() != 80) {
      buffer.append(":");
      buffer.append(servletRequest.getLocalPort());
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

}
