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
package org.apache.abdera.server.impl;

import org.apache.abdera.server.RequestContext;
import org.apache.abdera.server.RequestHandler;
import org.apache.abdera.server.ResponseContext;
import org.apache.abdera.server.cache.Cache;
import org.apache.abdera.server.cache.CacheEntry;
import org.apache.abdera.server.exceptions.AbderaServerException;
import org.apache.abdera.server.exceptions.ExistenceException;
import org.apache.abdera.server.exceptions.MethodNotAllowedException;
import org.apache.abdera.server.exceptions.NotFoundException;
import org.apache.abdera.server.exceptions.NotModifiedException;
import org.apache.abdera.server.exceptions.RequestException;

public abstract class BaseRequestHandler 
  implements RequestHandler {

  public enum Type {
    UNKNOWN, INTROSPECTION, COLLECTION, ENTRY, ENTRY_EDIT, MEDIA, MEDIA_EDIT
  };
  
  protected Type resourceType = Type.UNKNOWN;
  
  public BaseRequestHandler() {}
  
  public ResponseContext invoke(
    RequestContext requestContext)
      throws AbderaServerException {
    try {
      ResponseContext response = createResponseContext();
      checkExists(requestContext);
      checkMethod(requestContext);
      checkModified(requestContext);
      checkRequest(requestContext);
      checkCache(requestContext,response,getCacheKey());
      internalInvoke(requestContext, response);
      if (response != null)
        return response;
      else
        return new NotFoundException();
    } catch (AbderaServerException ase) {
      throw ase;
    } catch (Throwable t) {
      String message = t.getMessage();
      if (message == null || message.length() == 0)
        message = "Unknown Server Exception";
      throw new AbderaServerException(500,message);
    }
  }

  protected abstract ResponseContext createResponseContext();
  
  /**
   * Returns this handlers cache (if any)
   */
  protected abstract Cache getCache();
  
  /**
   * Return a cache key for this request
   */
  protected abstract String getCacheKey();
  
  protected abstract boolean useCache();
  
  protected CacheEntry.CacheDisposition checkCache(
    RequestContext requestContext,
    ResponseContext responseContext, 
    String cacheKey) {
      if (useCache()) {
        Cache cache = getCache();
        if (cache != null) {
          return cache.process(requestContext, responseContext, cacheKey);
        }
      } 
      return CacheEntry.CacheDisposition.TRANSPARENT;
  }
  
  /**
   * Returns the type of the requested resource
   */
  protected abstract Type getResourceType(RequestContext requestContext);
  
  /**
   * Check that the requested resource exists.  If not, the method
   * MUST throw an appropriate ExistenceException (e.g. not found,
   * gone, moved, etc)
   */
  protected void checkExists(RequestContext requestContext) throws ExistenceException{}
  
  /**
   * Check the request method.  If the method is not supported, 
   * a MethodNotAllowedException MUST be thrown, otherwise the 
   * method should return with no exceptions
   */
  protected void checkMethod(RequestContext requestContext) throws MethodNotAllowedException {
    String method = requestContext.getMethod();
    String[] methods = getAllowedMethods(getResourceType(requestContext));
    java.util.Arrays.sort(methods);
    if (java.util.Arrays.binarySearch(methods, method) < 0)
      notAllowed(requestContext);
  }
  
  /**
   * Check to see if the requested resource has been modified.  If not,
   * a NotModifiedException MUST be thrown, otherwise the method
   * should return with no exceptions
   */
  protected void checkModified(RequestContext requestContext) throws NotModifiedException {}
  
  /**
   * Check to see if the request is valid. If not, throw an appropriate
   * RequestException
   */
  protected void checkRequest(RequestContext requestContext) throws RequestException {}
  
  /**
   * Handle the request
   */
  protected abstract ResponseContext internalInvoke(
    RequestContext requestContext,
    ResponseContext responseContext) 
      throws AbderaServerException;
  
  /**
   * Implementations should override this to specify customizations to the
   * allowable methods on a particular type of resource
   */
  protected String[] getAllowedMethods(Type type) {
    switch (type) {
      case COLLECTION:    return new String[] { "GET", "POST" };
      case ENTRY:         return new String[] { "GET" };
      case ENTRY_EDIT:    return new String[] { "GET", "DELETE", "PUT" };
      case INTROSPECTION: return new String[] { "GET" };
      default:            return new String[] { "GET" };
    }
  }
  
  /**
   * Utility method for reporting MethodNotAllowedExceptions properly
   */
  protected void notAllowed(RequestContext requestContext) throws MethodNotAllowedException {
    MethodNotAllowedException notallowed = new MethodNotAllowedException();
    notallowed.setAllow(getAllowedMethods(getResourceType(requestContext)));
    throw notallowed;
  }
  
  
}
