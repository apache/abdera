package org.apache.abdera.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public interface RequestContext {

  public static enum Method {
    GET, POST, PUT, DELETE, OPTIONS, HEAD
  }
  
  public Method getMethod();
  
  public URI getRequestUri();
  
  public URI getBaseUri();
  
  public URI getPathInfo();
  
  public String getParameter(String name);
  
  public List<String> getParameters(String name);

  public List<String> getParameterNames();
  
  public String getHeader(String name);
  
  public List<String> getHeaders(String name);
  
  public List<String> getHeaderNames();
  
  public InputStream getInputStream() throws IOException;
  
}
