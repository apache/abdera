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

import org.apache.axiom.testutils.PortAllocator;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;

public class JettyUtil {

    private static final String PORT_PROP = "abdera.test.client.port";

    private static int PORT = PortAllocator.allocatePort();
    private static Server server = null;
    private static ServletHandler handler = null;

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
        server.setConnectors(new Connector[] {connector});
        handler = new ServletHandler();
        server.setHandler(handler);
    }

    public static void addServlet(String _class, String path) {
        try {
            if (server == null)
                initServer();
        } catch (Exception e) {
        }
        handler.addServletWithMapping(_class, path);
    }

    public static void start() throws Exception {
        if (server == null)
            initServer();
        if (server.isRunning())
            return;
        server.start();
    }

    public static void stop() throws Exception {
        if (server == null)
            return;
        server.stop();
        server = null;
    }

    public static boolean isRunning() {
        return (server != null);
    }

}
