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
package org.apache.abdera.protocol.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.SubjectResolver;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetResolver;
import org.apache.abdera.protocol.server.util.ServerConstants;
import org.apache.abdera.protocol.util.AbstractRequest;
import org.apache.abdera.protocol.util.CacheControlUtil;

public class ServletRequestContext 
  extends AbstractRequest
  implements RequestContext, ServerConstants {
  
  private final Target target;
  private final AbderaServer abderaServer;
  private final Subject subject;
  private final HttpServletRequest servletRequest;
  private final String method;
  private final URI uri;
  private final URI baseUri;
  private final URI pathInfo;
    
  public ServletRequestContext(
    AbderaServer abdera,
    HttpServletRequest request) {
      this.abderaServer = abdera;
      this.servletRequest = request;
      this.uri = initUri();
      this.baseUri = initBaseUri();
      this.pathInfo = initPathInfo();
      this.method = request.getMethod();
      CacheControlUtil.parseCacheControl(getCacheControl(), this);
      TargetResolver targetResolver = abdera.getTargetResolver();
      this.target = (targetResolver != null) ?
        abdera.getTargetResolver().resolve(getUri().toString()) : null;
      SubjectResolver subjectResolver = abdera.getSubjectResolver();
      this.subject = (subjectResolver != null) ?
        subjectResolver.resolve(request.getUserPrincipal()) : null;
  }
  
  public AbderaServer getServer() {
    return abderaServer;
  }
  
  public Abdera getAbdera() {
    return getServer().getAbdera();
  }
  
  public Subject getSubject() {
    return subject;
  }
  
  public Target getTarget() {
    return target;
  }
  
  public String getMethod() {
    return method;
  }
  
  public URI getUri() {
    return uri;
  }
  
  private URI initUri() {
    URI uri = null;
    try {
      StringBuffer buf = 
        new StringBuffer(
          servletRequest.getRequestURI());
      String qs = servletRequest.getQueryString();
      if (qs != null && qs.length() != 0)
        buf.append("?" + servletRequest.getQueryString());
      uri = new URI(buf.toString());
    } catch (URISyntaxException e) {}
    return uri;
  }
  
  private String getHost() {
    String host = getAbdera().getConfiguration().getConfigurationOption(
      "org.apache.abdera.protocol.server.Host");
    return (host != null) ? 
      host : 
      servletRequest.getServerName();
  }
  
  private int getPort() {
    String port = getAbdera().getConfiguration().getConfigurationOption(
      "org.apache.abdera.protocol.server.Port");
    return (port != null) ? 
      Integer.parseInt(port) : 
      servletRequest.getLocalPort();
  }
  
  public URI getBaseUri() {
    return baseUri;
  }
  
  private URI initBaseUri() {
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
  public String[] getHeaderNames() {
    List<String> list = Collections.list(servletRequest.getHeaderNames());
    return list.toArray(new String[list.size()]);
  }
    
  public URI getPathInfo() {
    return pathInfo;
  }
  
  private URI initPathInfo() {
    try {
      String pathInfo = servletRequest.getPathInfo();
      if (pathInfo == null)  {
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

  public Date getDateHeader(String name) {
    return new Date(servletRequest.getDateHeader(name));
  }
  
}
