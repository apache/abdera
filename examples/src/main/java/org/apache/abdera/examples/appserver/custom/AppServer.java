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
package org.apache.abdera.examples.appserver.custom;

import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class AppServer {

    public static void main(String... args) throws Exception {
        int port = 9002;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : 9002;
        } catch (Exception e) {
        }
        Server server = new Server(port);
        Context context = new Context(server, "/", Context.SESSIONS);
        ServletHolder servletHolder = new ServletHolder(new AbderaServlet());
        servletHolder.setInitParameter(ServiceManager.PROVIDER, CustomProvider.class.getName());
        context.addServlet(servletHolder, "/*");
        server.start();
        server.join();
    }

}
