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
package org.apache.abdera2.common.protocol.servlet.async;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.activation.MimeType;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContext;
import org.apache.abdera2.common.protocol.FilterChain;
import org.apache.abdera2.common.protocol.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbderaTask {

  private final static Log log = LogFactory.getLog(AbderaTask.class);
  
  private final String id;
  private final AsyncContext context;
  private final RequestContext requestContext;
  private final Provider provider;
  
  AbderaTask(AsyncContext context, Provider provider, RequestContext requestContext) {
    this.context = context;
    this.provider = provider;
    this.requestContext = requestContext;
    this.id = UUID.randomUUID().toString();
  }
    
  public String getId() {
    return id;
  }
  
  public void cancel() {
    cancel(context,getId());
  }
  
  public static void cancel(AsyncContext context, String id) {
    try {
      HttpServletResponse resp = 
        (HttpServletResponse) context.getResponse();
      if (!resp.isCommitted())
        resp.reset();
      resp.sendError(
        HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
        "Server is shutting down. Unable to process request");
      resp.flushBuffer();
    } catch (Throwable t) {
      log.error(String.format("Unrecoverable error canceling Abdera Task (%s)",id), t);
    } finally {
      context.complete();
    }
  }
  
  public void invoke() {
    try {
      log.debug(String.format("Invoking Abdera Task (%s)",getId()));
      HttpServletRequest req = 
        (HttpServletRequest) context.getRequest();
      HttpServletResponse resp = 
        (HttpServletResponse) context.getResponse();
      process(provider,req,resp);
      resp.flushBuffer();
    } catch (Throwable t) {
      log.error(String.format("Unrecoverable error processing Abdera Task (%s)",getId()), t);
    } finally {
      context.complete();
    }
  }
  
  protected void process(
    Provider provider,
    HttpServletRequest request,
    HttpServletResponse response) {
    FilterChain chain = 
      new FilterChain(provider, requestContext);
    try {
        log.debug(String.format("Using RequestContext: %s",requestContext.getClass().getName()));
        output(request, response, chain.next(requestContext), provider);
    } catch (Throwable t) {
        error("Error servicing request", t, response, provider);
        return;
    }
  }
    
    protected void output(HttpServletRequest request, HttpServletResponse response, ResponseContext context, Provider provider)
        throws IOException {
        log.debug(String.format("Received ResponseContext: %s", context));
        if (context != null) {
          log.debug(String.format("Status: %d",context.getStatus()));
          response.setStatus(context.getStatus());
            long cl = context.getContentLength();
            CacheControl cc = context.getCacheControl();
            if (cl > -1)
                response.setHeader("Content-Length", Long.toString(cl));
            if (cc != null)
                response.setHeader("Cache-Control", cc.toString());
            try {
                MimeType ct = context.getContentType();
                if (ct != null) {
                  log.debug(String.format("Content-Type: %s",ct.toString()));
                    response.setContentType(ct.toString());
                }
            } catch (Exception e) {
            }
            Iterable<String> names = context.getHeaderNames();
            for (String name : names) {
                Iterable<Object> headers = context.getHeaders(name);
                for (Object value : headers) {
                  log.debug(String.format("Header [%s]: %s", name, value.toString()));
                    if (value instanceof Date)
                        response.addDateHeader(name, ((Date)value).getTime());
                    else
                        response.addHeader(name, value.toString());
                }
            }
            if (!request.getMethod().equals("HEAD") && context.hasEntity()) {
                log.debug("Writing entity...");
                context.writeTo(response.getOutputStream());
            } else {
              log.debug("No entity to write...");
            }
        } else {
            error("Internal Server Error", null, response, provider);
        }
    }

    protected void error(String message, Throwable t, HttpServletResponse response, Provider provider) {
      try {
        message = 
          String.format(
            "Error in Abdera Task (%s): %s", 
            getId(), message);
        if (t != null)
            log.error(message, t);
        else
            log.error(message);

        if (response.isCommitted()) {
            log.error("Could not write an error message as the headers & HTTP status were already committed!");
        } else {
          ResponseContext resp = 
            provider.createErrorResponse(500, message, t);
          response.setStatus(500);
          response.setCharacterEncoding("UTF-8");
          resp.writeTo(response.getOutputStream());
        }
      } catch (IOException e) {
        log.error(String.format("Error writing to output stream (%s)",getId()),e);
      }
    }
}
