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
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.Value;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.DateTime;

public class DateTimeConverter 
  extends QNameConverter<DateTime> {

  protected DateTimeConverter(QName qname) {
    super(qname);
  }

  @Override 
  protected DateTime create(
    ConversionContext context) {
      return context.getAbdera().getFactory()
        .newDateTime(getQName(), null);
  }

  @Override 
  protected void finish(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    DateTime dest) {
      String text = dest.getText();
      if (text == null || text.trim().length() == 0) {
        setValue(source, objectContext, context, dest);
      }
  }

  @Override 
  protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Conventions conventions, 
    DateTime dest,
    AccessibleObject accessor) {
      if (accessor.isAnnotationPresent(Value.class) || 
          Value.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value);
        setValue(source, valueContext, context, dest);
      }
  }

  private void setValue(
    Object source, 
    ObjectContext objectContext, 
    ConversionContext context, 
    DateTime dest) {
      if (source == null) return;
      if (source instanceof Date) {
        dest.setDate((Date)source);
      } else if (source instanceof Calendar) {
        dest.setCalendar((Calendar)source);
      } else if (source instanceof Long) {
        dest.setTime((Long)source);
      } else if (source instanceof String) {
        dest.setValue(AtomDate.valueOf((String)source));
      } else {
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(source, objectContext, context);
        if (v != null) dest.setValue(AtomDate.valueOf(v.toString()));
      }
  }
}
