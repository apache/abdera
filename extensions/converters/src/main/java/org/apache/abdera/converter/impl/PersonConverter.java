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

import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.Email;
import org.apache.abdera.converter.annotation.Name;
import org.apache.abdera.converter.annotation.URI;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Person;
import org.apache.abdera.util.Constants;

public class PersonConverter 
  extends QNameConverter<Person> {

  protected PersonConverter(QName qname) {
    super(qname);
  }

  @Override 
  protected Person create(
    ConversionContext context) {
      return context.getAbdera().getFactory()
        .newPerson(getQName(), null);
  }

  @Override 
  protected void finish(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Person dest) {
      String name = dest.getName();
      if (name == null) {
        StringConverter c = new StringConverter();
        StringBuffer v = c.convert(source, objectContext, context);
        if (v != null) dest.setName(v.toString());
      }
  }

  @Override 
  protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Conventions conventions, 
    Person dest,
    AccessibleObject accessor) {
      if (accessor.isAnnotationPresent(Name.class) || 
          Name.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value);
        StringConverter c = new StringConverter();
        StringBuffer v = c.convert(value, valueContext, context);
        if (v != null) dest.setName(v.toString());
      }

      if (accessor.isAnnotationPresent(Email.class) || 
          Email.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value);
        StringConverter c = new StringConverter();
        StringBuffer v = c.convert(value, valueContext, context);
        if (v != null) dest.setEmail(v.toString());
      }
      
      if (accessor.isAnnotationPresent(URI.class) || 
          URI.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value);
        IRIConverter c = new IRIConverter(Constants.URI);
        IRIElement iri = c.convert(value, valueContext, context);
        if (iri != null) dest.setUriElement(iri);
      }
  }

}
