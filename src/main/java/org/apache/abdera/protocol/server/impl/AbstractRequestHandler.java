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
package org.apache.abdera.protocol.server.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.protocol.ItemManager;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.util.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractRequestHandler 
  implements RequestHandler {

  private static final Log log = LogFactory.getLog(AbstractRequestHandler.class);
  
  public void process(
    ServiceContext context, 
    RequestContext request,
    HttpServletResponse response) 
      throws IOException {
    
    log.debug(Messages.get("PROCESSING.REQUEST"));
    ItemManager<Provider> manager = context.getProviderManager();
    Provider provider = manager.get(request);
    log.debug(Messages.format("USING.PROVIDER",provider));
    try {
      if (preconditions(provider, request, response)) {
        output(request, response,provider.request(request));
      }
    } catch (Throwable e) {
      log.error(Messages.get("OUTPUT.ERROR"), e);
      try {
        output(request,response,new EmptyResponseContext(500));
      } catch (Exception ex) {
        log.error(Messages.get("OUTPUT.ERROR"), ex);
        response.sendError(500);
      }
    } finally {
      log.debug(Messages.format("RELEASING.PROVIDER", provider));
      if (provider != null) manager.release(provider);
    }
  }
  
  protected boolean preconditions(
    Provider provider, 
    RequestContext request, 
    HttpServletResponse response)
      throws IOException {
    // Check The Provider    
    if (provider == null) { 
      noprovider(response); 
      return false;
    }
    // Check The Target
    Target target = request.getTarget();
    if (target == null) { 
      notfound(response); 
      return false;
    }
    // Check The Method
    if (!checkMethod(provider,request)) {
      notallowed(
        response, 
        request.getMethod(), 
        provider.getAllowedMethods(target.getType()));
      return false;
    }
    return true;
  }
  
  protected void output(
    RequestContext request,
    HttpServletResponse response, 
    ResponseContext context) 
      throws IOException, ServletException {
    if (context != null) {
      response.setStatus(context.getStatus());
      long cl = context.getContentLength();
      String cc = context.getCacheControl();
      if (cl > -1) response.setHeader("Content-Length", Long.toString(cl));
      if (cc != null && cc.length() > 0) response.setHeader("Cache-Control",cc);
      try {
        MimeType ct = context.getContentType();
        if (ct != null) response.setContentType(ct.toString());
      } catch (Exception e) {}
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
      if (!request.getMethod().equals("HEAD") && context.hasEntity()) {
        OutputStream out = response.getOutputStream();
        context.writeTo(out);
        out.close();
      }  
    } else {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
  protected boolean checkMethod(
    Provider provider,
    RequestContext context) 
      throws IOException {
    String method = context.getMethod();
    Target target = context.getTarget();
    String[] methods = provider.getAllowedMethods(target.getType());
    java.util.Arrays.sort(methods);
    return (java.util.Arrays.binarySearch(methods, method) >= 0);
  }
    
  protected void noprovider(HttpServletResponse response) throws IOException {
    response.sendError(500, Messages.get("NO.PROVIDER"));
  }
  
  protected void notfound(HttpServletResponse response) throws IOException {
    response.sendError(404, Messages.get("NOT.FOUND"));
  }
  
  protected void notallowed(
    HttpServletResponse response,
    String method, 
    String[] methods) 
      throws IOException {
    response.sendError(405, Messages.format("METHOD.NOT.ALLOWED", method));
    response.setHeader("Allow", combine(methods));;
  }
  
  protected String combine(String... vals) {
    StringBuffer buf = new StringBuffer();
    for(String val : vals) {
      if (buf.length() > 0) buf.append(", ");
      buf.append(val);
    }
    return buf.toString();
  }

}
