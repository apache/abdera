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
package org.apache.abdera.test.ext.opensearch.server;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class JettyServer {

    public static final int DEFAULT_PORT = 9002;
    private final int port;
    private Server server;

    public JettyServer() {
        this(DEFAULT_PORT);
    }

    public JettyServer(int port) {
        this.port = port;
    }

    public void start(final Provider myProvider) throws Exception {
        server = new Server(port);
        Context context = new Context(server, "/", Context.SESSIONS);
        ServletHolder servletHolder = new ServletHolder(new AbderaServlet() {

            protected Provider createProvider() {
                myProvider.init(this.getAbdera(), this.getProperties(this.getServletConfig()));
                return myProvider;
            }
        });
        context.addServlet(servletHolder, "/*");
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
