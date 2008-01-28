package org.apache.abdera.examples.appserver;

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
    } catch (Exception e) {}
    Server server = new Server(port);
    Context context = new Context(server, "/", Context.SESSIONS);
    ServletHolder servletHolder = new ServletHolder(new AbderaServlet());
    servletHolder.setInitParameter(ServiceManager.PROVIDER, CustomProvider.class.getName());
    context.addServlet(servletHolder, "/*");
    server.start();
    server.join();
  }
  
}
