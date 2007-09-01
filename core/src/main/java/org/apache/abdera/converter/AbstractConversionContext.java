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
package org.apache.abdera.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;

@SuppressWarnings("unchecked")
public abstract class AbstractConversionContext
  implements ConversionContext {

  private transient final Abdera abdera;
  private final Map<Class,Converter> converters = 
    new HashMap<Class,Converter>();
  
  protected AbstractConversionContext() {
    this(new Abdera());
  }
  
  protected AbstractConversionContext(Abdera abdera) {
    this.abdera = abdera;
  }
  
  public Abdera getAbdera() {
    return abdera;
  }
  
  public <T> Converter<T> getConverter(ObjectContext objectContext) {
    try {
      Class type = objectContext.getObjectType();
      Converter<T> converter = converters.get(type);
      if (converter == null) converter = objectContext.getConverter();
      if (converter == null) {
        for (Annotation annotation : objectContext.getAnnotations()) {
          converter = converters.get(annotation.annotationType());
          if (converter != null) return converter;
        }
      }
      if (converter == null && !type.isAnnotation()) {
        for (Class knownType : converters.keySet()) {
          if (!knownType.isAnnotation() && 
              knownType.isAssignableFrom(type)) {
            return converters.get(knownType);
          }
        }
      }
      return converter;
    } catch (Throwable t) {
      throw new ConversionException(t);
    }
  }

  public boolean hasConverter(ObjectContext objectContext) {
    return getConverter(objectContext) != null;
  }
  
  public boolean hasConverter(Object object) {
    return hasConverter(new ObjectContext(object));
  }
  
  public boolean hasConverter(
    Object object, 
    Object parent, 
    AccessibleObject accessor) {
      return hasConverter(new ObjectContext(object,parent,accessor));
  }
  
  public boolean hasConverter(Class<?> type) {
    if (converters.containsKey(type)) return true;
    if (!type.isAnnotation()) {
      for (Class<?> t : converters.keySet()) {
        if (!t.isAnnotation() && 
            t.isAssignableFrom(type)) return true;
      }
    }
    return false;
  }

  public void setConverter(Class<?> type, Converter<?> converter) {
    converters.put(type, converter);
  }

  public <T> T convert(Object object) {
    return (T)convert(object,new ObjectContext(object));
  }

  public <T> T convert(Object object, ObjectContext objectContext) {
    if (objectContext == null) objectContext = new ObjectContext(object);
    Converter<T> converter = getConverter(objectContext);
    if (converter != null)
      return convert(object,objectContext,converter);
    else 
      throw new ConversionException(
        "No converter available for " + 
        objectContext.getObjectType());
  }

  public <T> T convert(Object object, Converter<T> converter) {
    return (T)convert(object,new ObjectContext(object),converter);
  }

  public <T> T convert(
    Object object, 
    ObjectContext objectContext, 
    Converter<T> converter) {
      if (objectContext == null) objectContext = new ObjectContext(object);
      Converter<T> overrideConverter = getConverter(objectContext);
      if (overrideConverter != null) converter = overrideConverter;
      if (converter != null) {
        return converter.convert(object, objectContext, this);
      } else {
        throw new ConversionException(
          "No converter available for " + 
          objectContext.getObjectType());
      }
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return copy();
    }
  }
  
  protected Object copy() {
    return new RuntimeException(new CloneNotSupportedException());
  }
}
