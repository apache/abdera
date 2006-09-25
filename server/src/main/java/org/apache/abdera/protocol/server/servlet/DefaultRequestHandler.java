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
package org.apache.abdera.protocol.server.servlet;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.server.provider.AbstractResponseContext;
import org.apache.abdera.protocol.server.provider.EmptyResponseContext;
import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.protocol.server.provider.RequestContext;
import org.apache.abdera.protocol.server.provider.ResponseContext;
import org.apache.abdera.protocol.server.provider.TargetType;

public class DefaultRequestHandler 
  extends AbstractRequestHandler
  implements RequestHandler {

  public static interface TypeHandler {
    ResponseContext process(Provider provider, RequestContext request);
  }
  
  private static class TypeMethod {
    private TargetType type;
    private String method;
    public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((method == null) ? 0 : method.hashCode());
      result = PRIME * result + ((type == null) ? 0 : type.hashCode());
      return result;
    }
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final TypeMethod other = (TypeMethod) obj;
      if (method == null) {
        if (other.method != null)
          return false;
      } else if (!method.equals(other.method))
        return false;
      if (type == null) {
        if (other.type != null)
          return false;
      } else if (!type.equals(other.type))
        return false;
      return true;
    }
  }
  
  private final Map<TargetType,TypeHandler> typehandlers = new HashMap<TargetType,TypeHandler>();
  private final Map<TypeMethod,TypeHandler> typemethodhandlers = new HashMap<TypeMethod,TypeHandler>();
  
  private TypeMethod getTypeMethod(TargetType type, String method) {
    TypeMethod tm = new TypeMethod();
    tm.type = type;
    tm.method = method;
    return tm;
  }
  
  protected synchronized void setTypeMethodHandler(TargetType type, String method, TypeHandler handler) {
    typemethodhandlers.put(getTypeMethod(type,method), handler);
  }
  
  protected synchronized void setTypeHandler(TargetType type, TypeHandler handler) {
    typehandlers.put(type,handler);
  }
  
  private TypeHandler getHandler(TargetType type) {
    return typehandlers.get(type);
  }
  
  private TypeHandler getMethodHandler(TargetType type, String method) {
    return typemethodhandlers.get(getTypeMethod(type, method));
  }
  
  protected ResponseContext process(
    Provider provider, 
    RequestContext request) {
      
      TargetType type = request.getTarget().getType();    
      TypeHandler handler = getHandler(type);
      if (handler == null) {
        String method = request.getMethod().intern();
        
        handler = getMethodHandler(type,method);
        
        if (handler == null) {
          if (method == "GET") {
            if (type == TargetType.TYPE_SERVICE) {
              return provider.getService(request, true);
            }
            if (type == TargetType.TYPE_COLLECTION) {
              return provider.getFeed(request, true);
            }
            if (type == TargetType.TYPE_ENTRY) {
              return provider.getEntry(request, true);
            }
            if (type == TargetType.TYPE_MEDIA) {
              return provider.getMedia(request, true);
            }
            if (type == TargetType.TYPE_CATEGORIES) {
              return provider.getCategories(request, true);
            }
          }
          else if (method == "HEAD") {
            if (type == TargetType.TYPE_SERVICE) {
              return provider.getService(request, false);
            }
            if (type == TargetType.TYPE_COLLECTION) {
              return provider.getFeed(request, false);
            }
            if (type == TargetType.TYPE_ENTRY) {
              return provider.getEntry(request, false);
            }
            if (type == TargetType.TYPE_MEDIA) {
              return provider.getMedia(request, false);
            }
            if (type == TargetType.TYPE_CATEGORIES) {
              return provider.getCategories(request, false);
            }
          }
          else if (method == "POST") {
            if (type == TargetType.TYPE_COLLECTION) {
              return provider.createEntry(request);
            }
            if (type == TargetType.TYPE_ENTRY) {
              return provider.entryPost(request);
            }
            if (type == TargetType.TYPE_MEDIA) {
              return provider.mediaPost(request);
            }
          }
          else if (method == "PUT") {
            if (type == TargetType.TYPE_ENTRY) {
              return provider.updateEntry(request);
            }
            if (type == TargetType.TYPE_MEDIA) {
              return provider.updateMedia(request);
            }
          }
          else if (method == "DELETE") {
            if (type == TargetType.TYPE_ENTRY) {
              return provider.deleteEntry(request);
            }
            if (type == TargetType.TYPE_MEDIA) {
              return provider.deleteMedia(request);
            }
          } 
          else if (method == "OPTIONS") {
            AbstractResponseContext rc = new EmptyResponseContext(200);
            rc.addHeader("Allow", combine(getAllowedMethods(type)));
            return rc;
          }
        } else {
          return handler.process(provider, request);
        }
        return null;
      } else {
        return handler.process(provider, request);
      }
  }
  
  protected String[] getAllowedMethods(TargetType type) {
    if (type == null)                       return new String[0];
    if (type == TargetType.TYPE_COLLECTION) return new String[] { "GET", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_CATEGORIES) return new String[] { "GET", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_ENTRY)      return new String[] { "GET", "DELETE", "PUT", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_MEDIA)      return new String[] { "GET", "DELETE", "PUT", "POST", "HEAD", "OPTIONS" };
    if (type == TargetType.TYPE_SERVICE)    return new String[] { "GET", "HEAD", "OPTIONS" };
    return new String[] { "GET", "HEAD", "OPTIONS" };
  }
  
}
