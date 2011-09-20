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
package org.apache.abdera2.common.protocol.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.protocol.AbstractBaseRequestContext;
import org.apache.abdera2.common.protocol.Provider;

@SuppressWarnings({ "unchecked" })
public class ServletRequestContext 
  extends AbstractBaseRequestContext {

    private final HttpServletRequest request;
    private final ServletContext servletContext;
    private HttpSession session;

    public ServletRequestContext(Provider provider, HttpServletRequest request, ServletContext servletContext) {
        super(provider, request.getMethod(), initRequestUri(request), initBaseUri(provider, request));
        this.request = request;
        this.servletContext = servletContext;
        this.session = request.getSession(false);
        this.principal = request.getUserPrincipal();
        this.subject = provider.resolveSubject(this);
        this.target = initTarget();
    }

    public Reader getReader() throws IOException {
        return request.getReader();
    }

    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public synchronized HttpSession getSession() {
        return getSession(false);
    }

    public synchronized HttpSession getSession(boolean create) {
        if (session == null)
            session = request.getSession(create);
        return session;
    }

    public ServletRequestContext setAttribute(Scope scope, String name, Object value) {
        switch (scope) {
            case REQUEST:
                request.setAttribute(name, value);
                break;
            case SESSION:
                getSession(true).setAttribute(name, value);
                break;
            case CONTAINER: {
                ServletContext scontext = getServletContext();
                if (scontext != null)
                    scontext.setAttribute(name, value);
            }
        }
        return this;
    }

    public Object getAttribute(Scope scope, String name) {
        switch (scope) {
            case REQUEST:
                return request.getAttribute(name);
            case SESSION:
                return (session != null) ? session.getAttribute(name) : null;
            case CONTAINER: {
                ServletContext scontext = getServletContext();
                return scontext != null ? scontext.getAttribute(name) : null;
            }
        }
        return null;
    }

    public Iterable<String> getAttributeNames(Scope scope) {
        switch (scope) {
            case REQUEST:
                return enum2array(request.getAttributeNames());
            case SESSION:
                return (session != null) ? enum2array(session.getAttributeNames()) : null;
            case CONTAINER: {
                ServletContext scontext = getServletContext();
                return scontext != null ? enum2array(scontext.getAttributeNames()) : null;
            }
        }
        return null;
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }

    public Iterable<String> getParameterNames() {
        return enum2array(request.getParameterNames());
    }

    public List<String> getParameters(String name) {
        String[] values = request.getParameterValues(name);
        return values != null ? java.util.Arrays.asList(values) : null;
    }

    public Date getDateHeader(String name) {
        long value = request.getDateHeader(name);
        return value != -1 ? new Date(value) : null;
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public Iterable<String> getHeaderNames() {
        return enum2array(request.getHeaderNames());
    }

    public Iterable<Object> getHeaders(String name) {
        Enumeration<?> e = request.getHeaders(name);
        List<Object> list = new ArrayList<Object>();
        while(e.hasMoreElements())
          list.add(e.nextElement());
        return list;
    }

    private static Iterable<String> enum2array(Enumeration<String> e) {
        return java.util.Collections.list(e);
    }

    private static String getHost(Provider provider, HttpServletRequest request) {
        String host = provider.getProperty("org.apache.abdera.protocol.server.Host");
        return (host != null) ? host : request.getServerName();
    }

    private static int getPort(Provider provider, HttpServletRequest request) {
        String port = provider.getProperty("org.apache.abdera.protocol.server.Port");
        return (port != null) ? Integer.parseInt(port) : request.getServerPort();
    }

    private static IRI initBaseUri(Provider provider, HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder((request.isSecure()) ? "https" : "http");
        buffer.append("://");
        buffer.append(getHost(provider, request));
        int port = getPort(provider, request);
        if ((port != 80) && (port != 443)) {
            buffer.append(":");
            buffer.append(port);
        }
        buffer.append(request.getContextPath());
        // So that .resolve() works appropriately.
        buffer.append("/");
        return new IRI(buffer.toString());
    }

    private static IRI initRequestUri(HttpServletRequest request) {
        IRI uri;
        StringBuilder buf = new StringBuilder(request.getRequestURI());
        String qs = request.getQueryString();
        if (qs != null && qs.length() != 0)
            buf.append("?").append(request.getQueryString());
        uri = new IRI(buf.toString());
        return uri;
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public Locale getPreferredLocale() {
        return request.getLocale();
    }

    public Iterable<Locale> getPreferredLocales() {
        return Collections.list(request.getLocales());
    }

    public String getTargetBasePath() {
        return request.getContextPath() + request.getServletPath();
    }

    public Object getProperty(Property property) {
      switch (property) {
      case SESSIONID:
          return (session != null) ? session.getId() : null;
      case SESSIONCREATED:
          return (session != null) ? new Date(session.getCreationTime()) : null;
      case SESSIONACCESSED:
          return (session != null) ? new Date(session.getLastAccessedTime()) : null;
      case SESSIONTIMEOUT:
          return (session != null) ? new Integer(session.getMaxInactiveInterval()) : new Integer((-1));
      case CHARACTERENCODING:
          return request.getCharacterEncoding();
      case LOCALES:
          return request.getLocales();
      case PROTOCOL:
          return request.getProtocol();
      case REMOTEADDRESS:
          return request.getRemoteAddr();
      case REMOTEHOST:
          return request.getRemoteHost();
      case REMOTEUSER:
          return request.getRemoteUser();
      case SCHEME:
          return request.getScheme();
      case PRINCIPAL:
          return request.getUserPrincipal();
      case AUTHTYPE:
          return request.getAuthType();
      case CONTENTLENGTH:
          return new Integer(request.getContentLength());
      case CONTENTTYPE:
          return request.getContentType();
      case CONTEXTPATH:
          return request.getContextPath();
      case LOCALADDR:
          return request.getLocalAddr();
      case LOCALNAME:
          return request.getLocalName();
      case SERVERNAME:
          return request.getServerName();
      case SERVERPORT:
          return new Integer(request.getServerPort());
      case SECURE:
          return (Boolean)request.isSecure();
      case PARTS: {
        try {
          return request.getParts();
        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      }
      default:
          throw new UnsupportedOperationException(Localizer.get("PROPERTY.NOT.SUPPORTED"));
  }
    }
}
