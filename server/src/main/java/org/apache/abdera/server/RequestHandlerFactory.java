package org.apache.abdera.server;

public interface RequestHandlerFactory {
  
  public static final RequestHandlerFactory INSTANCE = null;
  
  RequestHandler newRequestHandler(
    RequestContext requestContext) 
      throws AbderaServerException;
  
}
