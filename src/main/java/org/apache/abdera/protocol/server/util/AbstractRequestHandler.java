/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.abdera.protocol.server.util;

import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.ProviderManager;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.RequestHandlerManager;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException;

public abstract class AbstractRequestHandler 
  implements RequestHandler, ServerConstants {
  
  protected final RequestHandlerManager manager;
  
  protected AbstractRequestHandler(RequestHandlerManager manager) {
    this.manager = manager;
  }
  
  public RequestHandlerManager getRequestHandlerManager() {
    return manager;
  }
  
  public ResponseContext invoke(
    RequestContext requestContext)
      throws AbderaServerException {
    AbderaServer server = requestContext.getServer();
    ProviderManager providerManager =  server.getProviderManager();
    Provider provider = null;
    try {
      provider = (providerManager != null) ? 
        providerManager.newProvider(server) : null;
      checkMethod(requestContext);
      checkExists(requestContext, provider);
      checkModified(requestContext, provider);
      ResponseContext response = internalInvoke(requestContext, provider);
      if (response != null) return response;
      else throw new AbderaServerException(AbderaServerException.Code.NOTFOUND);
    } catch (Throwable t) {
      t.printStackTrace();
      String message = t.getMessage();
      if (message == null || message.length() == 0)
        message = "Unknown Server Exception";
      throw new AbderaServerException(500,message);
    } finally {
      if (provider != null)
        providerManager.releaseProvider(provider);
    }
  }
  
  /**
   * Check that the requested resource exists.  If not, the method
   * MUST throw an appropriate ExistenceException (e.g. not found,
   * gone, moved, etc)
   */
  protected void checkExists(
    RequestContext requestContext, 
    Provider provider) 
      throws AbderaServerException{
    if (provider != null) provider.checkExists(requestContext);
  }
  
  /**
   * Check the request method.  If the method is not supported, 
   * a MethodNotAllowedException MUST be thrown, otherwise the 
   * method should return with no exceptions
   */
  protected void checkMethod(RequestContext requestContext) throws AbderaServerException {
    String method = requestContext.getMethod();
    Target target = requestContext.getTarget();
    String[] methods = getAllowedMethods(target.getResourceType());
    java.util.Arrays.sort(methods);
    if (java.util.Arrays.binarySearch(methods, method) < 0)
      notAllowed(requestContext);
  }
  
  /**
   * Check to see if the requested resource has been modified.  If not,
   * a NotModifiedException MUST be thrown, otherwise the method
   * should return with no exceptions
   * @throws AbderaServerException 
   */
  protected void checkModified(
    RequestContext requestContext, 
    Provider provider) 
      throws AbderaServerException {
    if (provider != null) provider.checkModified(requestContext);
  }
  
  /**
   * Handle the request
   */
  protected abstract ResponseContext internalInvoke(
    RequestContext requestContext,
    Provider provider) 
      throws AbderaServerException;
  
  /**
   * Implementations should override this to specify customizations to the
   * allowable methods on a particular type of resource
   */
  protected String[] getAllowedMethods(ResourceType type) {
    if (type == null) return EMPTY;
    switch (type.ordinal()) {
      case ResourceType.COLLECTION_ORDINAL:    return new String[] { "GET", "POST", "HEAD", "OPTIONS" };
      case ResourceType.ENTRY_ORDINAL:         return new String[] { "GET", "HEAD", "OPTIONS" };
      case ResourceType.ENTRY_EDIT_ORDINAL:    return new String[] { "GET", "DELETE", "PUT", "HEAD", "OPTIONS" };
      case ResourceType.MEDIA_EDIT_ORDINAL:    return new String[] { "GET", "DELETE", "PUT", "HEAD", "OPTIONS" };
      case ResourceType.SERVICE_ORDINAL:       return new String[] { "GET", "HEAD", "OPTIONS" };
      default:            return new String[] { "GET", "HEAD", "OPTIONS" };
    }
  }
  
  /**
   * Utility method for reporting MethodNotAllowedExceptions properly
   */
  protected void notAllowed(RequestContext requestContext) throws AbderaServerException {
    Target target = requestContext.getTarget();
    AbderaServerException notallowed = 
      new AbderaServerException(
        AbderaServerException.Code.METHODNOTALLOWED);
    notallowed.setAllow(getAllowedMethods(target.getResourceType()));
    throw notallowed;
  }
  
}
