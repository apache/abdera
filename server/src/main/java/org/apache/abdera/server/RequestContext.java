package org.apache.abdera.server;

import java.net.URI;

public interface RequestContext {

  public static enum Method {
    GET, POST, PUT, DELETE, OPTIONS, HEAD
  }
  
  Method getMethod();
  
  URI getRequestUri();
  
  //TODO: finish
  
}
