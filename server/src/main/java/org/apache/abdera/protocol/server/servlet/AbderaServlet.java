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
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Sample APP servlet.
 * 
 * @version $Id$
 */
public class AbderaServlet 
  extends HttpServlet {

  private static final long serialVersionUID = 2393643907128535158L;
  
  private final static Log logger = LogFactory.getLog(AbderaServlet.class);
  
  protected ServiceManager serviceManager;
  
  public void init() throws ServletException {
    serviceManager = ServiceManager.getInstance();
  }
  
  @Override
  protected void service(
    HttpServletRequest request, 
    HttpServletResponse response) 
      throws ServletException, IOException {
    ServiceContext context = 
      serviceManager.newServiceContext(
        getProperties(getServletConfig()));
    RequestHandlerManager manager = context.getRequestHandlerManager();
    RequestHandler handler = manager.getRequestHandler();
    try {
      handler.process(context, request, response);
    } catch (Throwable t) {
      logger.error("Error servicing request", t);
      response.setContentType("text/plain");
      PrintWriter out = response.getWriter();
      out.println(t);
      t.printStackTrace(out);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
      manager.release(handler);
    }
  }
  
  private Map<String,String> getProperties(ServletConfig config) {
    Map<String,String> properties = new HashMap<String,String>();
    Enumeration e = config.getInitParameterNames();
    while(e.hasMoreElements()) {
      String key = (String) e.nextElement();
      String val = config.getInitParameter(key);
      properties.put(key, val);
    }
    return properties;
  }
  
}
