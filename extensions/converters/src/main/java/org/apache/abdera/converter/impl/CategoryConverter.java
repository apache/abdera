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
import org.apache.abdera.converter.annotation.Label;
import org.apache.abdera.converter.annotation.Scheme;
import org.apache.abdera.converter.annotation.Term;
import org.apache.abdera.model.Category;

public class CategoryConverter 
  extends BaseConverter<Category> {

  @Override 
  protected Category create(
    ConversionContext context) {
      return context.getAbdera().getFactory()
        .newCategory(null);
  }

  @Override 
  protected void finish(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Category dest) {
      String term = dest.getTerm();
      if (term == null) {
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(source, objectContext, context);
        if (v != null) dest.setTerm(v.toString());
      }
      
      if (dest.getScheme() == null) {
        Scheme ra = objectContext.getAnnotation(Scheme.class);
        if (ra != null && !ra.value().equals("##default")) {
          dest.setScheme(ra.value());
        }
      }

  }

  @Override 
  protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Conventions conventions, 
    Category dest,
    AccessibleObject accessor) {
      if (accessor.isAnnotationPresent(Term.class) || 
          Term.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setTerm(v.toString());
      }
      
      if (accessor.isAnnotationPresent(Scheme.class) || 
          Scheme.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setScheme(v.toString());
      }
      
      if (accessor.isAnnotationPresent(Label.class) || 
          Label.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setLabel(v.toString());
      }
  }

}
