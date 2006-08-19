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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.server.RequestContext;
import org.apache.abdera.server.RequestHandler;
import org.apache.abdera.server.RequestHandlerFactory;
import org.apache.abdera.server.ResponseContext;
import org.apache.abdera.server.cache.CachePolicy;
import org.apache.abdera.server.exceptions.AbderaServerException;
import org.apache.abdera.server.exceptions.MethodNotAllowedException;

public class AbderaServlet 
  extends HttpServlet {

  private static final long serialVersionUID = -4273782501412352619L;

  @Override
  protected void service(
    HttpServletRequest request, 
    HttpServletResponse response) 
      throws ServletException, IOException {
    RequestContext requestContext = new ServletRequestContext(request);
    ResponseContext responseContext = null;
    RequestHandler handler = null;
    try {
      RequestHandlerFactory factory = RequestHandlerFactory.INSTANCE;            // TODO: improve this
      if (factory != null)
        handler = factory.newRequestHandler(requestContext);
      if (handler != null) {
        responseContext = handler.invoke(requestContext);
      } else {
        throw new MethodNotAllowedException(request.getMethod());
      }
    } catch (AbderaServerException exception) {
      responseContext = exception;
    }
    doOutput(response, responseContext); 
  }

  private void doOutput(
    HttpServletResponse response, 
    ResponseContext context) 
      throws IOException, ServletException {
    if (context != null) {
      if (context.getStatusText() != null)
        response.sendError(context.getStatus(), context.getStatusText());
      else 
        response.setStatus(context.getStatus());
      if (context.getLastModified() != null)
        response.setDateHeader("Last-Modified", context.getLastModified().getTime());
      if (context.getContentLanguage() != null)
        response.setHeader("Content-Language", context.getContentLanguage());
      if (context.getContentLocation() != null)
        response.setHeader("Content-Location", context.getContentLocation().toString());
      if (context.getContentType() != null)
        response.setContentType(context.getContentType().toString());
      if (context.getEntityTag() != null)
        response.setHeader("ETag", context.getEntityTag());
      if (context.getLocation() != null)
        response.setHeader("Location", context.getLocation().toString());
      if (context.getContentLength() > -1) {
        response.setHeader("Content-Length", Long.toString(context.getContentLength()));
      }
      handleCachePolicy(response, context.getCachePolicy());
      
      // Add any custom headers after we've set the known ones,
      // giving the developer an option to replace or set multiple
      // headers. If they want to skip the ones above, they simply
      // don't set them.
      Map<String, List<String>> headers = context.getHeaders();
      if (headers != null) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
          List<String> values = entry.getValue();
          if (values == null) 
            continue;          
          for (String value : values) {
            response.setHeader(entry.getKey(), value);
          }
        }
      }
      
      if (context.hasEntity())
        context.writeTo(response.getOutputStream());
    } else {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
  private void handleCachePolicy(
    HttpServletResponse response, 
    CachePolicy cachePolicy) {
      if (cachePolicy == null) return;
      //TODO
  }
}
