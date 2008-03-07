package org.apache.abdera.examples.appserver.employee;

import javax.servlet.http.HttpServlet;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
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
    ServletHolder servletHolder = new ServletHolder(initServlet());
    context.addServlet(servletHolder, "/*");
    server.start();
    server.join();
  }
  
  private static HttpServlet initServlet() {
    EmployeeCollectionAdapter ca = new EmployeeCollectionAdapter();
    ca.setHref("employee");    
    
    SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
    wi.setTitle("Employee Directory Workspace");
    wi.addCollection(ca);    
    
    final DefaultProvider p = new DefaultProvider("/");  
    p.addWorkspace(wi);    
    
    return new AbderaServlet() {
      private static final long serialVersionUID = 0L;
      protected Provider createProvider() {
        p.init(getAbdera(), null);
        return p;
      }
    };
  }
}
