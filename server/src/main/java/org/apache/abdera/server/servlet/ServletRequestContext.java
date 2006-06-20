package org.apache.abdera.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
  
  public URI getBaseUri() {
	// TODO: this should be done from properties
	StringBuffer buffer = new StringBuffer("http://");
	buffer.append(servletRequest.getServerName());
	if (servletRequest.getLocalPort() != 80) {
		buffer.append(":");
		buffer.append(servletRequest.getLocalPort());
	}
	buffer.append(servletRequest.getServletPath());
	try {
		return new URI(buffer.toString());
	} catch (URISyntaxException e) {
		throw new RuntimeException(e);
	}
  }
  
  public String getHeader(String name) {
	  return servletRequest.getHeader(name);
  }

  public List<String> getHeaders(String name) {
	  return Collections.list(servletRequest.getHeaders(name));
  }
  
  public List<String> getHeaderNames() {
	  return Collections.list(servletRequest.getHeaderNames());
  }
  
  public URI getPathInfo() {
	try {
		return new URI(servletRequest.getPathInfo());
	} catch (URISyntaxException e) {
		throw new RuntimeException(e);
	}
  }
  
  public String getParameter(String name) {
	  return servletRequest.getParameter(name);
  }
  
  public List<String> getParameters(String name) {
	  return Arrays.asList(servletRequest.getParameterValues(name)); 
  }
  
  public List<String> getParameterNames() {
	  return Collections.list(servletRequest.getParameterNames());
  }
    
  public InputStream getInputStream() throws IOException {
	  return servletRequest.getInputStream();
  }

}
