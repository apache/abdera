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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.RequestHandlerFactory;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException;
import org.apache.abdera.protocol.server.target.TargetResolver;
import org.apache.abdera.protocol.server.util.ServerConstants;

public class AbderaServlet 
  extends HttpServlet 
  implements ServerConstants {

  private static final long serialVersionUID = -4273782501412352619L;

  private final Abdera abdera;
  private final AbderaServer abderaServer;
  
  public AbderaServlet() {
    this.abdera = new Abdera();
    this.abderaServer = new AbderaServer(abdera);
  }
  
  @Override
  public void init() throws ServletException {}

  /**
   * The RequestContext will either be set on the HttpServletRequest by 
   * some filter or servlet earlier in the invocation chain or will need
   * to be created and set on the request 
   */
  private RequestContext getRequestContext(HttpServletRequest request) {
    RequestContext context = 
      (RequestContext) request.getAttribute(REQUESTCONTEXT);
    TargetResolver resolver = getTargetResolver();
    if (context == null) {
      context = new ServletRequestContext(abdera,resolver,request);
      request.setAttribute(REQUESTCONTEXT, context);
    }
    return context;
  }
  
  
  private RequestHandlerFactory getRequestHandlerFactory() {
    ServletContext context = getServletContext();
    synchronized(context) {
      RequestHandlerFactory factory = 
        (RequestHandlerFactory) context.getAttribute(
        HANDLER_FACTORY);
      if (factory == null) {
        String s = getServletConfig().getInitParameter(HANDLER_FACTORY);
        factory = abderaServer.newRequestHandlerFactory(s);
        context.setAttribute(HANDLER_FACTORY, factory);
      }
      return factory;
    }
  }
  
  private TargetResolver getTargetResolver() {
    ServletContext context = getServletContext();
    synchronized(context) {
      TargetResolver resolver = 
        (TargetResolver) context.getAttribute(
          TARGET_RESOLVER);
      if (resolver == null) {
        String s = getServletConfig().getInitParameter(TARGET_RESOLVER);
        resolver = abderaServer.newTargetResolver(s);
        context.setAttribute(TARGET_RESOLVER, resolver);
      }
      return resolver;
    }
  }
  
  @Override
  protected void service(
    HttpServletRequest request, 
    HttpServletResponse response) 
      throws ServletException, IOException {
    RequestContext requestContext = getRequestContext(request);
    ResponseContext responseContext = null;
    RequestHandler handler = null;
    try {
      RequestHandlerFactory factory = getRequestHandlerFactory();
      if (factory != null)
        handler = factory.newRequestHandler(abderaServer,requestContext);
      if (handler != null) {
        responseContext = handler.invoke(requestContext);
      } else {
        throw new AbderaServerException(
          AbderaServerException.Code.NOTFOUND, 
          "Handler Not Found", "");
      }
    } catch (AbderaServerException exception) {
      responseContext = exception;
    } catch (Throwable t) {
      responseContext = new AbderaServerException(t);
    }
    doOutput(response, responseContext); 
  }

  private void doOutput(
    HttpServletResponse response, 
    ResponseContext context) 
      throws IOException, ServletException {
    if (context != null) {
      response.setStatus(context.getStatus());
      long cl = context.getContentLength();
      String cc = context.getCacheControl();
      if (cl > -1) response.setHeader("Content-Length", Long.toString(cl));
      if (cc != null) response.setHeader("Cache-Control",cc);
      Map<String, List<Object>> headers = context.getHeaders();
      if (headers != null) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
          List<Object> values = entry.getValue();
          if (values == null) 
            continue;          
          for (Object value : values) {
            if (value instanceof Date)
              response.setDateHeader(entry.getKey(), ((Date)value).getTime());
            else
              response.setHeader(entry.getKey(), value.toString());
          }
        }
      }
      
      if (context.hasEntity())
        context.writeTo(response.getOutputStream());
    } else {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
}
