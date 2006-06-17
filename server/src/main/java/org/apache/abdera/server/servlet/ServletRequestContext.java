package org.apache.abdera.server.servlet;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.abdera.server.RequestContext;

public class ServletRequestContext 
  implements RequestContext {

  private HttpServletRequest servletRequest = null;
  
  public ServletRequestContext(HttpServletRequest request) {
    this.servletRequest = request;
  }
  
  public Method getMethod() {
    return Method.valueOf(servletRequest.getMethod());
  }

  public URI getRequestUri() {
    URI uri = null;
    try {
      StringBuffer buf = 
        new StringBuffer(
          servletRequest.getRequestURI());
      if (servletRequest.getQueryString() != null)
        buf.append("?" + servletRequest.getQueryString());
      uri = new URI(buf.toString());
    } catch (URISyntaxException e) {}
    return uri;
  }

}
