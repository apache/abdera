package org.apache.abdera.server;

import org.apache.abdera.util.ServiceUtil;

public interface RequestHandlerFactory {
  
  public static final String HANDLER_FACTORY = "org.apache.abdera.server.RequestHandlerFactory";

  public static final RequestHandlerFactory INSTANCE = (RequestHandlerFactory) 
  	ServiceUtil.newInstance(HANDLER_FACTORY, "");
  
  RequestHandler newRequestHandler(
    RequestContext requestContext) 
      throws AbderaServerException;
  
}
