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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.ItemManager;
import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.impl.HttpServletRequestContext;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple Atompub servlet.
 */
@SuppressWarnings("unchecked") 
public class AbderaServlet 
  extends HttpServlet {

  private static final long serialVersionUID = 2393643907128535158L;
  
  private final static Log log = LogFactory.getLog(AbderaServlet.class);
  
  protected ServiceManager manager;
  protected ServiceContext context;
  
  public void init() throws ServletException {
    log.debug("Initialing Abdera Servlet");
    manager = createServiceManager();
    context = createServiceContext();
    if (context == null) {
      log.debug("Cannot create service context");
      throw new ServletException("Cannot create service context");
    }
  }

  public Abdera getAbdera() {
     return ServiceManager.getAbdera();
  }
  
  public ServiceContext getServiceContext() {
    return context;
  }

  public ServiceManager getServiceManager() {
    return manager;
  }

  protected ServiceContext createServiceContext() {
    return manager.newServiceContext(
        getProperties(
          getServletConfig()));
  }

  protected ServiceManager createServiceManager() {
    return ServiceManager.getInstance();
  }
  
  @Override
  protected void service(
    HttpServletRequest request, 
    HttpServletResponse response) 
      throws ServletException, IOException {
    RequestContext reqcontext = new HttpServletRequestContext(context, request);
    ItemManager<RequestHandler> manager = context.getRequestHandlerManager();
    log.debug("Processing request");
    RequestHandler handler = manager.get(reqcontext);
    log.debug("Handler - " + handler);
    try {
      handler.process(context, reqcontext, new HttpResponseServletAdapter(response));
    } catch (Throwable t) {
      error("Error servicing request", t, response);
      return;
    } finally {
      log.debug("Releasing handler - " + handler);
      manager.release(handler);
    }
    log.debug("Request complete");
  }
  
  private void error(
    String message, 
    Throwable t, 
    HttpServletResponse response) 
      throws IOException {
    if (response.isCommitted()) response.reset();
    if (t != null) log.error(message, t);
    else log.error(message);
    response.setStatus(500);
    StreamWriter sw = 
      getAbdera().newStreamWriter()
                 .setOutputStream(
                   response.getOutputStream(),
                   "UTF-8");
    Error.create(sw, 500, message,t);
    sw.close();
  }
  
  protected Map<String,String> getProperties(ServletConfig config) {
    Map<String,String> properties = new HashMap<String,String>();
    Enumeration<String> e = config.getInitParameterNames();
    while(e.hasMoreElements()) {
      String key = (String) e.nextElement();
      String val = config.getInitParameter(key);
      properties.put(key, val);
    }
    return properties;
  }
  
}
