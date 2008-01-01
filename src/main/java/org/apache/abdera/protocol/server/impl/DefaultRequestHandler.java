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
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.activation.MimeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.protocol.ItemManager;
import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.protocol.server.HttpResponse;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultRequestHandler 
  implements RequestHandler {

  private static final Log log = LogFactory.getLog(DefaultRequestHandler.class);
  
  public void process(
    ServiceContext context, 
    RequestContext request,
    HttpResponse response) 
      throws IOException {
    
    log.debug(Localizer.get("PROCESSING.REQUEST"));
    ItemManager<Provider> manager = context.getProviderManager();
    Provider provider = manager.get(request);
    log.debug(Localizer.sprintf("USING.PROVIDER",provider));
    try {
      if (preconditions(provider, request, response)) {
        response(request, response,provider.request(request));
      }
    } catch (Throwable e) {
      log.error(Localizer.get("OUTPUT.ERROR"), e);
      try {
        response(request,response,new EmptyResponseContext(500));
      } catch (Exception ex) {
        log.error(Localizer.get("OUTPUT.ERROR"), ex);
        internalServerError(request, response);
      }
    } finally {
      log.debug(Localizer.sprintf("RELEASING.PROVIDER", provider));
      if (provider != null) manager.release(provider);
    }
  }
  
  protected boolean preconditions(
    Provider provider, 
    RequestContext request, 
    HttpResponse response)
      throws IOException {
    // Check The Provider    
    if (provider == null) { 
      noprovider(request, response); 
      return false;
    }
    // Check The Target
    Target target = request.getTarget();
    if (target == null) { 
      notfound(request, response); 
      return false;
    }
    // Check The Method
    if (!checkMethod(provider,request)) {
      notallowed(
        request, 
        response, 
        request.getMethod(), 
        provider.getAllowedMethods(target.getType()));
      return false;
    }
    return true;
  }
  
  protected void response(
    RequestContext request,
    HttpResponse response, 
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
      String[] names = context.getHeaderNames();
      for (String name : names) {
        Object[] headers = context.getHeaders(name);
        for (Object value : headers) {          
          if (value instanceof Date)
            response.setDateHeader(name, ((Date)value).getTime());
          else
            response.setHeader(name, value.toString());
        }
      }
      
      if (!request.getMethod().equals("HEAD") && context.hasEntity()) {
        OutputStream out = response.getOutputStream();
        context.writeTo(out);
        out.close();
      }  
    } else {
      internalServerError(request, response);
    }
  }

  private void internalServerError(RequestContext request, HttpResponse response)
    throws UnsupportedEncodingException, IOException {
    sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "There was an error fulfilling your request.");
  }

  private void sendError(
    RequestContext request, 
    HttpResponse response, int code, 
    String message) 
      throws UnsupportedEncodingException,
             IOException {
    Abdera abdera = request.getAbdera();
    response.setStatus(code);
    response.setContentType("application/xml");
    StreamWriter sw = 
      abdera.newStreamWriter()
            .setOutputStream(
              response.getOutputStream(),
              "UTF-8");
    Error.create(sw,code,message,null);
    sw.close();
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
    
  protected void noprovider(RequestContext request, HttpResponse response) throws IOException {
    sendError(request, response, 500, Localizer.get("NO.PROVIDER"));
  }
  
  protected void notfound(RequestContext request, HttpResponse response) throws IOException {
    sendError(request, response, 404,
              Localizer.get("NOT.FOUND"));
  }
  
  protected void notallowed(
    RequestContext request, 
    HttpResponse response,
    String method, 
    String[] methods) 
      throws IOException {
    sendError(request, response, 405, Localizer.sprintf("METHOD.NOT.ALLOWED", method));
    response.setHeader("Allow", combine(methods));;
  }
  
  protected String combine(String... vals) {
    StringBuilder buf = new StringBuilder();
    for(String val : vals) {
      if (buf.length() > 0) buf.append(", ");
      buf.append(val);
    }
    return buf.toString();
  }

}
