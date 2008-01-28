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

import org.apache.abdera.converter.BaseConverter;
import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.URI;
import org.apache.abdera.converter.annotation.Value;
import org.apache.abdera.converter.annotation.Version;
import org.apache.abdera.model.Generator;

public class GeneratorConverter 
  extends BaseConverter<Generator> {

  @Override 
  protected Generator create(
    ConversionContext context) {
      return context.getAbdera().getFactory()
        .newGenerator(null);
  }

  @Override 
  protected void finish(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Generator dest) {
      String text = dest.getText();
      if (text == null || text.length() == 0) {
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(source, objectContext, context);
        if (v != null) dest.setText(v.toString());
      }
      
      org.apache.abdera.converter.annotation.Generator gen =
        objectContext.getAnnotation(org.apache.abdera.converter.annotation.Generator.class);
      if (dest.getVersion() == null && gen != null) {
        if (!gen.version().equals("##default"))
          dest.setVersion(gen.version());
      }
      if (dest.getUri() == null && gen != null) {
        if (!gen.uri().equals("##default"))
          dest.setUri(gen.uri());
      }

  }

  @Override 
  protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Conventions conventions, 
    Generator dest,
    AccessibleObject accessor) {
      if (accessor.isAnnotationPresent(Value.class) || 
          Value.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setText(v.toString());
      }
      
      if (accessor.isAnnotationPresent(Version.class) || 
          Version.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setVersion(v.toString());
      }
      
      if (accessor.isAnnotationPresent(URI.class) || 
          URI.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setUri(v.toString());
      }
  }

}
