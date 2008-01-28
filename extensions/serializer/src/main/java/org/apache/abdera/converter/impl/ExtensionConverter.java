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
package org.apache.abdera.converter.impl;

import java.lang.reflect.AccessibleObject;

import javax.xml.namespace.QName;

import org.apache.abdera.converter.BaseConverter;
import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.Extension;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;

public abstract class ExtensionConverter<T extends ExtensibleElement> 
  extends BaseConverter<T> {

  @Override 
  protected void process(
  Object source, 
  ObjectContext objectContext,
  ConversionContext context, 
  Conventions conventions, 
  T dest,
  AccessibleObject accessor) {
    if (accessor.isAnnotationPresent(Extension.class) || 
        Extension.class.equals(conventions.matchConvention(accessor))) {
      Object val = eval(accessor, source);
      Object[] values = toArray(val);
      for (Object value : values) {
        ObjectContext valueContext = new ObjectContext(value, source, accessor);
        Extension ext = valueContext.getAnnotation(Extension.class);
        QName qname = getQName(ext);
        if (ext.simple() && qname != null) {
          StringConverter c = new StringConverter();
          StringBuffer buf = c.convert(value, valueContext, context);
          dest.addSimpleExtension(qname, buf.toString());
        } else {
          Object converted = context.convert(value, valueContext);
          if (converted != null && converted instanceof Element) {
            dest.addExtension((Element)converted);
          }
        }
      }
    }
  }
  
  protected static boolean isUndefined(String value) {
    return value == null || "##default".equals(value);
  }
  
  protected static QName getQName(
    Extension extension) {
    QName qname = null;
    if (extension != null) {
      if (isUndefined(extension.prefix()) && 
          isUndefined(extension.ns()) && 
          isUndefined(extension.name())) {
        qname = new QName(
          extension.ns(), 
          extension.name(), 
          extension.prefix());
      } else if (
          isUndefined(extension.prefix()) && 
          !isUndefined(extension.ns()) && 
          !isUndefined(extension.name())) {
        qname = new QName(
          extension.ns(), 
          extension.name());
      } else if (!isUndefined(extension.name())) {
        qname = new QName(extension.name());
      }
    }
    return qname;
  }
}
