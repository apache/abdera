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
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.protocol.ResponseInfo;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.provider.EmptyResponseContext;
import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.protocol.server.provider.ProviderManager;
import org.apache.abdera.protocol.server.provider.RequestContext;
import org.apache.abdera.protocol.server.provider.ResponseContext;
import org.apache.abdera.protocol.server.provider.Target;
import org.apache.abdera.protocol.server.provider.TargetType;
import org.apache.abdera.protocol.EntityTag;

public abstract class AbstractRequestHandler 
  implements RequestHandler {

  public void process(
    ServiceContext context, 
    HttpServletRequest request,
    HttpServletResponse response) 
      throws IOException {
    
    ProviderManager manager = context.getProviderManager();
    Provider provider = manager.getProvider();
    RequestContext requestContext = getRequestContext(context,request);
    
    try {
      if (preconditions(provider, requestContext, response)) {
        output(response,process(provider, requestContext));
      }
    } catch (Throwable e) {
      try {
        output(response,new EmptyResponseContext(500));
      } catch (Exception ex) {
        response.sendError(500);
      }
    } finally {
      manager.release(provider);
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
    if (!checkMethod(request)) {
      notallowed(
        response, 
        request.getMethod(), 
        getAllowedMethods(target.getType()));
      return false;
    }
    // Check The Conditions
    ResponseInfo info = provider.getInfo(request);
    switch(checkConditions(info, request)) {
      case 412: preconditionfailed(response); return false;
      case 304: notmodified(response); return false;
    }
    return true;
  }
  
  protected abstract ResponseContext process(
    Provider provider, 
    RequestContext request);
  
  protected void output(
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
      if (context.hasEntity()) {
        OutputStream out = response.getOutputStream();
        context.writeTo(out);
        out.close();
      }  
    } else {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
  protected boolean checkMethod(
    RequestContext context) 
      throws IOException {
    String method = context.getMethod();
    Target target = context.getTarget();
    String[] methods = getAllowedMethods(target.getType());
    java.util.Arrays.sort(methods);
    return (java.util.Arrays.binarySearch(methods, method) >= 0);
  }

  protected abstract String[] getAllowedMethods(TargetType type);
  
  protected RequestContext getRequestContext(
    ServiceContext context, 
    HttpServletRequest request) {
      return new HttpServletRequestContext(context, request);
  }
  
  protected int checkConditions(
    ResponseInfo info, 
    RequestContext request) {
      EntityTag entity_tag = (info != null) ? info.getEntityTag() : null;
      Date last_mod = (info != null) ? info.getLastModified() : null;
      if (entity_tag != null) {
        String ifmatch = request.getIfMatch();
        if (ifmatch != null && 
           (entity_tag == null || 
            !EntityTag.matches(entity_tag,ifmatch))) {
          return 412;
        }
        String ifnonematch = request.getIfNoneMatch();
        if (ifnonematch != null && 
            entity_tag != null && 
            EntityTag.matches(entity_tag,ifnonematch)) {
          return 304;
        }
      }
      if (last_mod != null) {
        Date ifmodsince = request.getIfModifiedSince();
        if (ifmodsince != null && 
            last_mod.getTime() <= ifmodsince.getTime()) return 304;
        Date ifunmodsince = request.getIfUnmodifiedSince();
        if (ifunmodsince != null && 
            last_mod.getTime() > ifunmodsince.getTime()) return 412;
      }
      return 0;
  }
  
  protected void preconditionfailed(HttpServletResponse response) throws IOException {
    response.sendError(412, "Failed");
  }
  
  protected void notmodified(HttpServletResponse response) throws IOException {
    response.sendError(304, "Not Modified");
  }
  
  protected void noprovider(HttpServletResponse response) throws IOException {
    response.sendError(500, "No Provider");
  }
  
  protected void notfound(HttpServletResponse response) throws IOException {
    response.sendError(404, "Not Found");
  }
  
  protected void notallowed(
    HttpServletResponse response,
    String method, 
    String[] methods) 
      throws IOException {
    response.sendError(405, "Method '" + method + "' Not Allowed");
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
