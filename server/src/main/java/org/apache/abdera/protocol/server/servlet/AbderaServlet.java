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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple Atompub servlet.
 */
@SuppressWarnings("unchecked")
public class AbderaServlet extends HttpServlet {

    private static final long serialVersionUID = 2393643907128535158L;

    private final static Log log = LogFactory.getLog(AbderaServlet.class);

    protected ServiceManager manager;
    protected Provider provider;

    public void init() throws ServletException {
        log.debug("Initialing Abdera Servlet");
        manager = createServiceManager();
        provider = createProvider();
        log.debug("Using provider - " + provider);
    }

    public Abdera getAbdera() {
        return ServiceManager.getAbdera();
    }

    public ServiceManager getServiceManager() {
        return manager;
    }

    protected ServiceManager createServiceManager() {
        return ServiceManager.getInstance();
    }

    protected Provider createProvider() {
        return manager.newProvider(getProperties(getServletConfig()));
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        RequestContext reqcontext = new ServletRequestContext(provider, request, getServletContext());
        FilterChain chain = new FilterChain(provider, reqcontext);
        try {
            output(request, response, chain.next(reqcontext));
        } catch (Throwable t) {
            error("Error servicing request", t, response);
            return;
        }
        log.debug("Request complete");
    }

    private void output(HttpServletRequest request, HttpServletResponse response, ResponseContext context)
        throws IOException {
        if (context != null) {
            response.setStatus(context.getStatus());
            long cl = context.getContentLength();
            String cc = context.getCacheControl();
            if (cl > -1)
                response.setHeader("Content-Length", Long.toString(cl));
            if (cc != null && cc.length() > 0)
                response.setHeader("Cache-Control", cc);
            try {
                MimeType ct = context.getContentType();
                if (ct != null)
                    response.setContentType(ct.toString());
            } catch (Exception e) {
            }
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
                context.writeTo(response.getOutputStream());
            }
        } else {
            error("Internal Server Error", null, response);
        }
    }

    private void error(String message, Throwable t, HttpServletResponse response) throws IOException {
        if (t != null)
            log.error(message, t);
        else
            log.error(message);

        if (response.isCommitted()) {
            log.error("Could not write an error message as the headers & HTTP status were already committed!");
        } else {
            response.setStatus(500);
            StreamWriter sw = getAbdera().newStreamWriter().setOutputStream(response.getOutputStream(), "UTF-8");
            Error.create(sw, 500, message, t);
            sw.close();
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
