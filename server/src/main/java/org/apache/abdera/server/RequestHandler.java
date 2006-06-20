package org.apache.abdera.server;

public interface RequestHandler {

  ResponseContext invoke(
    RequestContext requestContext) 
      throws AbderaServerException;
  
}
