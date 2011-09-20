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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.protocol.RequestContext;
import org.apache.abdera2.common.protocol.ResponseContext;
import org.apache.abdera2.common.protocol.FilterChain;
import org.apache.abdera2.common.protocol.Provider;
import org.apache.abdera2.common.protocol.ServiceManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractAbderaServlet
  extends HttpServlet {

  private static final long serialVersionUID = 2722733242417632126L;

    private final static Log log = LogFactory.getLog(AbstractAbderaServlet.class);

    protected ServiceManager<Provider> manager;
    protected Provider provider;

    @Override
    public void init() throws ServletException {
      log.debug("Initialing Abdera Servlet");
        manager = createServiceManager();
        provider = createProvider();
        log.debug("Using manager - " + manager);
        log.debug("Using provider - " + provider);
    }

    protected ServiceManager<Provider> getServiceManager() {
        return manager;
    }

    @SuppressWarnings("unchecked")
    protected ServiceManager<Provider> createServiceManager() {
        String prop = this.getInitParameter(ServiceManager.class.getName());
        return prop != null ? 
          ServiceManager.Factory.getInstance(prop) :  
          ServiceManager.Factory.getInstance();
    }

    protected Provider createProvider() {
        return manager.newProvider(getProperties(getServletConfig()));
    }

    protected void process(
      HttpServletRequest request,
      HttpServletResponse response,
      ServletContext context) {
      RequestContext reqcontext = new ServletRequestContext(provider, request, context);
      FilterChain chain = 
        new FilterChain(provider, reqcontext);
      try {
          output(request, response, chain.next(reqcontext));
      } catch (Throwable t) {
          error("Error servicing request", t, response);
          return;
      }
      log.debug("Request complete");
    }
    
    protected void output(HttpServletRequest request, HttpServletResponse response, ResponseContext context)
        throws IOException {
        if (context != null) {
            response.setStatus(context.getStatus());
            long cl = context.getContentLength();
            CacheControl cc = context.getCacheControl();
            if (cl > -1)
                response.setHeader("Content-Length", Long.toString(cl));
            if (cc != null)
                response.setHeader("Cache-Control", cc.toString());
            try {
                MimeType ct = context.getContentType();
                if (ct != null)
                    response.setContentType(ct.toString());
            } catch (Exception e) {
            }
            Iterable<String> names = context.getHeaderNames();
            for (String name : names) {
                Iterable<Object> headers = context.getHeaders(name);
                for (Object value : headers) {
                    if (value instanceof Date)
                        response.addDateHeader(name, ((Date)value).getTime());
                    else
                        response.addHeader(name, value.toString());
                }
            }
            if (!request.getMethod().equals("HEAD") && context.hasEntity()) {
                context.writeTo(response.getOutputStream());
            }
        } else {
            error("Internal Server Error", null, response);
        }
    }

    protected void error(String message, Throwable t, HttpServletResponse response) {
      try {
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
        log.error("Error writing to output stream",e);
      }
    }

    protected Map<String, String> getProperties(ServletConfig config) {
        Map<String, String> properties = new HashMap<String, String>();
        Enumeration<String> e = config.getInitParameterNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            String val = config.getInitParameter(key);
            properties.put(key, val);
        }
        return properties;
    }

}
