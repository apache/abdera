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
package org.apache.abdera.i18n.templates;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked") 
public final class ObjectContext 
  extends CachingContext {
  
  private static final long serialVersionUID = -1387599933658718221L;
  private final Object target;
  private final Map<String,AccessibleObject> accessors = 
    new HashMap<String,AccessibleObject>();
  
  public ObjectContext(Object object) {
    this(object,false);
  }
  
  public ObjectContext(Object object, boolean isiri) {
    if (object == null) throw new IllegalArgumentException();
    this.target = object;
    setIri(isiri);
    initMethods();    
  }
  
  private void initMethods() {
    Class _class = target.getClass();
    if (_class.isAnnotation() || 
        _class.isArray() || 
        _class.isEnum() || 
        _class.isPrimitive())
      throw new IllegalArgumentException();
    if (!_class.isInterface()) {
      Field[] fields = _class.getFields();
      for (Field field : fields) {
        if (!Modifier.isPrivate(field.getModifiers())) {
          accessors.put(field.getName().toLowerCase(), field);
        }
      }
    }
    Method[] methods = _class.getMethods();
    for (Method method : methods) {
      String name = method.getName();
      if (!Modifier.isPrivate(method.getModifiers()) &&
          method.getParameterTypes().length == 0 && 
          !method.getReturnType().equals(Void.class) && 
          !isReserved(name)) {
        name = name.toLowerCase();
        if (name.startsWith("get")) name = name.substring(3);
        else if (name.startsWith("is")) name = name.substring(2);
        accessors.put(name, method);
      }
    }
  }
  
  private boolean isReserved(String name) {
    return (name.equals("toString") || 
            name.equals("hashCode") || 
            name.equals("notify") || 
            name.equals("notifyAll") || 
            name.equals("getClass") || 
            name.equals("wait"));
  }
  
  @Override 
  protected <T> T resolveActual(String var) {
    try {
      var = var.toLowerCase();
      AccessibleObject accessor = accessors.get(var);
      if (accessor == null) return null;
      if (accessor instanceof Method) {
        Method method = (Method) accessor;
        return (T)method.invoke(target);
      } else if (accessor instanceof Field) {
        Field field = (Field) accessor;
        return (T)field.get(target);
      } else return null;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
  
  public Iterator<String> iterator() {
    return accessors.keySet().iterator();
  }

  @Override 
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((target == null) ? 0 : target.hashCode());
    return result;
  }

  @Override 
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final ObjectContext other = (ObjectContext) obj;
    if (target == null) {
      if (other.target != null) return false;
    } else if (!target.equals(other.target)) return false;
    return true;
  }
    
}
