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

import javax.activation.MimeType;

import org.apache.abdera.converter.BaseConverter;
import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.Href;
import org.apache.abdera.converter.annotation.HrefLanguage;
import org.apache.abdera.converter.annotation.Length;
import org.apache.abdera.converter.annotation.MediaType;
import org.apache.abdera.converter.annotation.Rel;
import org.apache.abdera.converter.annotation.Title;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Link;

public class LinkConverter 
  extends BaseConverter<Link> {

  @Override 
  protected Link create(
    ConversionContext context) {
      return context.getAbdera().getFactory()
        .newLink(null);
  }

  @Override 
  protected void finish(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Link dest) {
      IRI href = dest.getHref();
      if (href == null) {
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(source, objectContext, context);
        if (v != null) dest.setHref(v.toString());
      }
      
      if (dest.getRel() == null) {
        Rel ra = objectContext.getAnnotation(Rel.class);
        if (ra != null && !ra.value().equals("##default")) {
          dest.setRel(ra.value());
        }
      }
      
      if (dest.getMimeType() == null) {
        MediaType mt = objectContext.getAnnotation(MediaType.class);
        if (mt != null && !mt.value().equals("##default")) {
          dest.setMimeType(mt.value());
        }
      }
  }

  @Override 
  protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Conventions conventions, 
    Link dest,
    AccessibleObject accessor) {
      if (accessor.isAnnotationPresent(Href.class) || 
          Href.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setHref(v.toString());
      }

      if (accessor.isAnnotationPresent(MediaType.class) || 
          MediaType.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        if (value != null && value instanceof MimeType) {
          dest.setMimeType(value.toString());
        } else {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          StringConverter s = new StringConverter();
          StringBuffer v = s.convert(value, valueContext, context);
          if (v != null) dest.setMimeType(v.toString());
        }
      }
      
      if (accessor.isAnnotationPresent(Title.class) || 
          Title.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setTitle(v.toString());
      }

      if (accessor.isAnnotationPresent(HrefLanguage.class) || 
          HrefLanguage.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setHrefLang(v.toString());
      }

      if (accessor.isAnnotationPresent(Rel.class) || 
          Rel.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        StringConverter s = new StringConverter();
        StringBuffer v = s.convert(value, valueContext, context);
        if (v != null) dest.setRel(v.toString());
      }

      if (accessor.isAnnotationPresent(Length.class) || 
          Length.class.equals(conventions.matchConvention(accessor))) {
        Object value = eval(accessor, source);
        if (value instanceof Long) {
          dest.setLength((Long)value);
        } else if (value instanceof Integer) {
          Integer i = (Integer)value;
          dest.setLength(i.intValue());
        } else if (value instanceof Short) {
          Short s = (Short)value;
          dest.setLength(s.shortValue());
        } else {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          StringConverter s = new StringConverter();
          StringBuffer v = s.convert(value, valueContext, context);
          if (v != null) dest.setLength(Long.parseLong(v.toString()));
        }
      }

  }

}
