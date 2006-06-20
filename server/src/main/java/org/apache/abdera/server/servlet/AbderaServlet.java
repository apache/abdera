package org.apache.abdera.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.server.AbderaServerException;
import org.apache.abdera.server.CachePolicy;
import org.apache.abdera.server.RequestContext;
import org.apache.abdera.server.RequestHandler;
import org.apache.abdera.server.RequestHandlerFactory;
import org.apache.abdera.server.ResponseContext;
import org.apache.abdera.server.exceptions.MethodNotAllowed;

public class AbderaServlet 
  extends HttpServlet {

  private static final long serialVersionUID = -4273782501412352619L;

  @Override
  protected void service(
    HttpServletRequest request, 
    HttpServletResponse response) 
      throws ServletException, IOException {
    RequestContext requestContext = new ServletRequestContext(request);
    ResponseContext responseContext = null;
    RequestHandler handler = null;
    try {
      RequestHandlerFactory factory = RequestHandlerFactory.INSTANCE;            // TODO: improve this
      if (factory != null)
        handler = factory.newRequestHandler(requestContext);
      if (handler != null) {
        responseContext = handler.invoke(requestContext);
      } else {
        throw new MethodNotAllowed(request.getMethod());
      }
    } catch (AbderaServerException exception) {
      responseContext = exception;
    }
    doOutput(response, responseContext); 
  }

  private void doOutput(
    HttpServletResponse response, 
    ResponseContext context) 
      throws IOException, ServletException {
    if (context != null) {
      if (context.getStatusText() != null)
        response.sendError(context.getStatus(), context.getStatusText());
      else 
        response.setStatus(context.getStatus());
      if (context.getLastModified() != null)
        response.setDateHeader("Last-Modified", context.getLastModified().getTime());
      if (context.getContentLanguage() != null)
        response.setHeader("Content-Language", context.getContentLanguage());
      if (context.getContentLocation() != null)
        response.setHeader("Content-Location", context.getContentLocation().toString());
      if (context.getContentType() != null)
        response.setContentType(context.getContentType().toString());
      if (context.getEntityTag() != null)
        response.setHeader("ETag", context.getEntityTag());
      if (context.getLocation() != null)
        response.setHeader("Location", context.getLocation().toString());
      handleCachePolicy(response, context.getCachePolicy());
      if (context.hasOutput())
        context.writeTo(response.getOutputStream());
    } else {
      response.sendError(
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
  private void handleCachePolicy(
    HttpServletResponse response, 
    CachePolicy cachePolicy) {
      if (cachePolicy == null) return;
      //TODO
  }
}
