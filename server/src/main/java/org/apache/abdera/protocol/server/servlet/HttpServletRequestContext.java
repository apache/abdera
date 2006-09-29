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
import java.io.Reader;
import java.security.Principal;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.auth.SubjectResolver;
import org.apache.abdera.protocol.server.provider.AbstractRequestContext;
import org.apache.abdera.protocol.server.provider.RequestContext;
import org.apache.abdera.protocol.server.provider.TargetResolver;
import org.apache.abdera.util.iri.IRI;
import org.apache.abdera.util.iri.IRISyntaxException;

public class HttpServletRequestContext 
  extends AbstractRequestContext
  implements RequestContext {

  private final HttpServletRequest request;
  private HttpSession session;
  
  public HttpServletRequestContext(
    ServiceContext context, 
    HttpServletRequest request) {
      super(
        context, 
        request.getMethod(), 
        initRequestUri(request),
        initBaseUri(context,request));
      this.request = request;
      this.session = request.getSession(false);
      
      SubjectResolver subjectResolver = context.getSubjectResolver();
      subject = (subjectResolver != null)? 
        subjectResolver.resolve((Principal)getProperty(Property.PRINCIPAL)) : null;
      
      TargetResolver targetResolver = context.getTargetResolver();
      target = (targetResolver != null) ? 
        targetResolver.resolve(this) : null;
  }
  
  public Object getProperty(Property property) {
    switch (property) {
      case SESSIONID:         return (session != null) ? session.getId() : null;
      case SESSIONCREATED:    return (session != null) ? new Date(session.getCreationTime()) : null;
      case SESSIONACCESSED:   return (session != null) ? new Date(session.getLastAccessedTime()) : null;
      case SESSIONTIMEOUT:    return (session != null) ? session.getMaxInactiveInterval() : -1;
      case CHARACTERENCODING: return request.getCharacterEncoding();
      case LOCALES:           return request.getLocales();
      case PROTOCOL:          return request.getProtocol();
      case REMOTEADDRESS:     return request.getRemoteAddr();
      case REMOTEHOST:        return request.getRemoteHost();
      case REMOTEUSER:        return request.getRemoteUser();
      case SCHEME:            return request.getScheme();
      case PRINCIPAL:         return request.getUserPrincipal();
      default:
        throw new UnsupportedOperationException("Property not supported"); 
    }
  }
  
  public Reader getReader() throws IOException {
    return request.getReader();
  }
  
  public InputStream getInputStream() throws IOException {
    return request.getInputStream();
  }
  
  private synchronized HttpSession getSession() {
    if (session == null) session = request.getSession(true);
    return session;
  }
  
  public void setAttribute(Scope scope, String name, Object value) {
    switch(scope) {
      case REQUEST: request.setAttribute(name, value); break;
      case SESSION: getSession().setAttribute(name, value); break;
    }
  }
  
  public Object getAttribute(Scope scope, String name) {
    switch(scope) {
      case REQUEST: return request.getAttribute(name);
      case SESSION: return (session != null) ? session.getAttribute(name) : null;
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public String[] getAttributeNames(Scope scope) {
    switch(scope) {
      case REQUEST: enum2array(request.getAttributeNames());
      case SESSION: return (session != null) ? enum2array(session.getAttributeNames()) : null;
    }
    return null;
  }
  
  public String getParameter(String name) {
    return request.getParameter(name);
  }
  
  @SuppressWarnings("unchecked")
  public String[] getParameterNames() {
    return enum2array(request.getParameterNames());
  }
  
  public List<String> getParameters(String name) {
    return java.util.Arrays.asList(
      request.getParameterValues(name));
  }
  
  public Date getDateHeader(String name) {
    return new Date(request.getDateHeader(name));
  }

  public String getHeader(String name) {
    return request.getHeader(name);
  }

  @SuppressWarnings("unchecked")
  public String[] getHeaderNames() {
    return enum2array(request.getHeaderNames());
  }

  @SuppressWarnings("unchecked")
  public List<String> getHeaders(String name) {
    Enumeration<String> e = request.getHeaders(name);
    return java.util.Collections.list(e);
  }

  @SuppressWarnings("unchecked")
  private static String[] enum2array (Enumeration<String> e) {
    List<String> list = java.util.Collections.list(e);
    return list.toArray(new String[list.size()]);
  }
  
  private static String getHost(
      ServiceContext context, 
      HttpServletRequest request) {
        Abdera abdera = context.getAbdera();
        String host = abdera.getConfiguration().getConfigurationOption(
          "org.apache.abdera.protocol.server.Host");
        return (host != null) ? 
          host : 
          request.getServerName();
    }
    
    private static int getPort(
      ServiceContext context, 
      HttpServletRequest request) {
      Abdera abdera = context.getAbdera();
        String port = abdera.getConfiguration().getConfigurationOption(
          "org.apache.abdera.protocol.server.Port");
        return (port != null) ? 
          Integer.parseInt(port) : 
          request.getLocalPort();
    }
    
    private static IRI initBaseUri(
      ServiceContext context, 
      HttpServletRequest request) {
        StringBuffer buffer = 
          new StringBuffer(
            (request.isSecure())?
              "https":"http");
        buffer.append("://");
        buffer.append(getHost(context,request));
        int port = getPort(context,request);
        if (port != 80) {
          buffer.append(":");
          buffer.append(port);
        }
        buffer.append(request.getContextPath());
        // So that .resolve() works appropriately.
        buffer.append("/");
        try {
          return new IRI(buffer.toString());
        } catch (IRISyntaxException e) {
          throw new RuntimeException(e);
        }
    }
    
    private static IRI initRequestUri(HttpServletRequest request) {
      IRI uri = null;
      try {
        StringBuffer buf = 
          new StringBuffer(
            request.getRequestURI());
        String qs = request.getQueryString();
        if (qs != null && qs.length() != 0)
          buf.append("?" + request.getQueryString());
        uri = new IRI(buf.toString());
      } catch (IRISyntaxException e) {}
      return uri;
    }
}
