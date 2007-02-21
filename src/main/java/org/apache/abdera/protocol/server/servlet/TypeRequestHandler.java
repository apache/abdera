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

import org.apache.abdera.protocol.server.provider.Provider;
import org.apache.abdera.protocol.server.provider.RequestContext;
import org.apache.abdera.protocol.server.provider.ResponseContext;
import org.apache.abdera.protocol.server.provider.TargetType;

/**
 * Extension of the Default Request Handler that allows subclasses
 * to plug in alternate implementations of request and method handlers.
 * This was originally in DefaultRequestHandler but it just didn't 
 * prove to be as useful as I had originally imagined it could be.  
 * Separating this out into it's own class makes it easier for us to 
 * drop it later if it proves to be completely useless :-)  
 */
public abstract class TypeRequestHandler
  extends DefaultRequestHandler 
  implements RequestHandler {

  public static interface TypeHandler {
    ResponseContext process(
      Provider provider, 
      RequestContext request);
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
  
  private final Map<TargetType,TypeHandler> typehandlers = 
    new HashMap<TargetType,TypeHandler>();
  private final Map<TypeMethod,TypeHandler> typemethodhandlers = 
    new HashMap<TypeMethod,TypeHandler>();
  
  private TypeMethod getTypeMethod(TargetType type, String method) {
    TypeMethod tm = new TypeMethod();
    tm.type = type;
    tm.method = method;
    return tm;
  }
  
  protected synchronized void setTypeMethodHandler(
    TargetType type, 
    String method, 
    TypeHandler handler) {
      typemethodhandlers.put(getTypeMethod(type,method), handler);
  }
  
  protected synchronized void setTypeHandler(
    TargetType type, 
    TypeHandler handler) {
      typehandlers.put(type,handler);
  }
  
  private TypeHandler getHandler(
    TargetType type) {
      return typehandlers.get(type);
  }
  
  private TypeHandler getMethodHandler(
    TargetType type, 
    String method) {
      return typemethodhandlers.get(getTypeMethod(type, method));
  }

  @Override
  protected ResponseContext process(
    Provider provider, 
    RequestContext request) {
      TargetType type = request.getTarget().getType();    
      TypeHandler handler = getHandler(type);
      if (handler == null) {
        String method = request.getMethod();
        handler = getMethodHandler(type,method);
        if (handler == null) {
          return super.process(provider, request);
        } else {
          return handler.process(provider, request);
        }
      } else {
        return handler.process(provider, request);
      }
  }

}
