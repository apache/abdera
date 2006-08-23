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
package org.apache.abdera.test.client;

import javax.servlet.http.HttpServlet;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.jetty.servlet.ServletHandler;

public class JettyUtil {

  private static final String PORT_PROP = "abdera.test.client.port";
  
  private static int PORT = 8080;
  private static Server server = null;
  
  public static int getPort() {
    if (System.getProperty(PORT_PROP) != null) {
      PORT = Integer.parseInt(System.getProperty(PORT_PROP));  
    }
    return PORT;
  }
  
  private static void initServer() throws Exception {
    server = new Server();
    Connector connector = new SocketConnector();
    connector.setPort(getPort());
    server.setConnectors(new Connector[]{connector});
    server.setHandler(new HandlerWrapper());
    server.start();
  }
  
  public static void addHandler(ServletHandler handler) throws Exception {
    if (server == null) initServer();
    if (hasHandler(handler)) return;
    server.addHandler(handler);
  }
  
  private static boolean hasHandler(ServletHandler handler) throws Exception {
    if (server == null || server.getHandlers() == null) return false;
    for (Handler h : server.getHandlers()) {
      if (h.equals(handler)) return true;
    }
    return false;
  }
  
  public static void removeHandler(ServletHandler handler) throws Exception {
    if (server == null) return;
    if (handler.isRunning()) handler.stop();
    server.removeHandler(handler);
    if (server.getHandlers().length == 1) {
      server.removeHandler(server.getHandler());
      server.stop();
      server = null;
    }
  }
  
  public static boolean isRunning() {
    return (server != null);
  }
  
  public static void main(String[] args) throws Exception {
    
    ServletHandler handler1 = new ServletHandler();
    handler1.addServletWithMapping(TestServlet.class, "/foo");
    System.out.println(JettyUtil.isRunning());
    JettyUtil.addHandler(handler1);
    System.out.println(JettyUtil.isRunning());
    
    ServletHandler handler2 = new ServletHandler();
    handler2.addServletWithMapping(TestServlet.class, "/bar");
    System.out.println(JettyUtil.isRunning());
    JettyUtil.addHandler(handler2);
    System.out.println(JettyUtil.isRunning());
    
    JettyUtil.removeHandler(handler1);
    System.out.println(JettyUtil.isRunning());
    JettyUtil.removeHandler(handler2);
    System.out.println(JettyUtil.isRunning());
  }
  
  @SuppressWarnings("serial")
  private static class TestServlet extends HttpServlet {}
}
