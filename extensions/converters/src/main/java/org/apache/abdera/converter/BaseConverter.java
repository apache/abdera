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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.abdera.converter.annotation.BaseURI;
import org.apache.abdera.converter.annotation.Charset;
import org.apache.abdera.converter.annotation.EntityTag;
import org.apache.abdera.converter.annotation.Language;
import org.apache.abdera.converter.annotation.LastModified;
import org.apache.abdera.converter.annotation.Slug;
import org.apache.abdera.converter.impl.StringConverter;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

@SuppressWarnings("unchecked")
public abstract class BaseConverter<T> 
  extends Converter<T> {
  
  protected abstract T create(ConversionContext context);
  
  protected void init(
    Object source, 
    ObjectContext objectContext, 
    ConversionContext context, 
    T dest) {}
  
  protected void finish(
    Object source, 
    ObjectContext objectContext, 
    ConversionContext context, 
    T dest) {}

  protected void process(
    Object source, 
    ObjectContext 
    objectContext, 
    ConversionContext context,
    Conventions conventions,
    T dest,
    AccessibleObject accessor) {}
  
  @Override 
  public T convert(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context) {    
      T dest = create(context);
      init(
        source, 
        objectContext, 
        context, 
        dest);
      Conventions conventions = 
        ((ConventionConversionContext)context)
          .getConventions();
      for (AccessibleObject accessor : objectContext.getAccessors()) {
        if (dest instanceof Base && 
            accessor.isAnnotationPresent(Language.class) || 
            Language.class.equals(conventions.matchConvention(accessor))) {
          Object value = eval(accessor,source);
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          StringConverter c = new StringConverter();
          StringBuffer buf = c.convert(value, valueContext, context);
          if (buf != null) {
            if (dest instanceof Document) ((Document)dest).setLanguage(buf.toString());
            else if (dest instanceof Element) ((Element)dest).setLanguage(buf.toString());
          }
        } else if (dest instanceof Base &&
            accessor.isAnnotationPresent(BaseURI.class) || 
            BaseURI.class.equals(conventions.matchConvention(accessor))) {
          
          Object value = eval(accessor,source);
          String iri = null;
          if (value instanceof URI) {
            iri = value.toString();
          } else if (value instanceof URL) {
            iri = value.toString();
          } else if (value instanceof IRI) {
            iri = value.toString();
          } else {
            ObjectContext valueContext = new ObjectContext(value,source,accessor);
            StringConverter c = new StringConverter();
            StringBuffer buf = c.convert(value, valueContext, context);
            iri = buf.toString();
          }
          if (iri != null) {
            if (dest instanceof Document) ((Document)dest).setBaseUri(iri);
            else if (dest instanceof Element) ((Element)dest).setBaseUri(iri);
          }
        } else if (dest instanceof Document && 
          accessor.isAnnotationPresent(Slug.class) || 
          Slug.class.equals(conventions.matchConvention(accessor))) {
            Object value = eval(accessor,source);
            ObjectContext valueContext = new ObjectContext(value,source,accessor);
            StringConverter c = new StringConverter();
            StringBuffer buf = c.convert(value, valueContext, context);
            if (buf != null) ((Document)dest).setSlug(buf.toString());
        } else if (dest instanceof Document && 
            accessor.isAnnotationPresent(LastModified.class) || 
            LastModified.class.equals(conventions.matchConvention(accessor))) {
          Object value = eval(accessor,source);
          Document doc = (Document)dest;
          if (value instanceof Date) {
            doc.setLastModified((Date)value);
          } else if (value instanceof Calendar) {
            Calendar cal = (Calendar)value;
            doc.setLastModified(cal.getTime());
          } else if (value instanceof Long) {
            Long l = (Long) value;
            doc.setLastModified(new Date(l.longValue()));
          } else if (value instanceof String) {
            doc.setLastModified(AtomDate.parse((String)value));
          } else {
            StringConverter s = new StringConverter();
            StringBuffer v = s.convert(source, objectContext, context);
            if (v != null) doc.setLastModified(AtomDate.parse(v.toString()));
          }
        } else if (dest instanceof Document && 
            accessor.isAnnotationPresent(Charset.class) || 
            Charset.class.equals(conventions.matchConvention(accessor))) {
          Object value = eval(accessor,source);
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          StringConverter c = new StringConverter();
          StringBuffer buf = c.convert(value, valueContext, context);
          if (buf != null) ((Document)dest).setCharset(buf.toString());
        } else if (dest instanceof Document && 
            accessor.isAnnotationPresent(EntityTag.class) || 
            EntityTag.class.equals(conventions.matchConvention(accessor))) {
              Object value = eval(accessor,source);
              if (value instanceof org.apache.abdera.util.EntityTag) {
                ((Document)dest).setEntityTag((org.apache.abdera.util.EntityTag)value);
              } else {
                ObjectContext valueContext = new ObjectContext(value,source,accessor);
                StringConverter c = new StringConverter();
                StringBuffer buf = c.convert(value, valueContext, context);
                if (buf != null) ((Document)dest).setEntityTag(buf.toString());
              }
        } else {
          process(source, objectContext, context, conventions, dest, accessor);
        }
      }      
      finish(
        source, 
        objectContext, 
        context, 
        dest);
      return dest;      
  }
    
  protected Object eval(AccessibleObject accessor, Object parent) {
    try {
      if (accessor instanceof Field) 
        return ((Field)accessor).get(parent);
      else if (accessor instanceof Method) 
        return ((Method)accessor).invoke(parent, new Object[0]);
      else return null;
    } catch (Throwable t) {
      throw new ConversionException(t);
    }
  }
  
  protected boolean hasAnnotation(
    AnnotatedElement item, 
    Class<? extends Annotation> annotation) {
      return item.isAnnotationPresent(annotation);
  }
  
  protected Object[] toArray(Object value) {
    if (value == null) return new Object[0];
    if (value.getClass().isArray()) {
      return (Object[])value;
    } else if (value instanceof Collection) { 
      return ((Collection)value).toArray();
    } else if (value instanceof Map) {
      return ((Map)value).values().toArray();
    } else if (value instanceof Dictionary) {
      List<Object> list = new ArrayList<Object>();
      Enumeration e = ((Dictionary)value).elements();
      while(e.hasMoreElements()) list.add(e.nextElement());
      return list.toArray();
    } else if (value instanceof Iterator) { 
      List<Object> list = new ArrayList<Object>();
      Iterator i = (Iterator) value;
      while(i.hasNext()) list.add(i.next());
      return list.toArray();
    } else if (value instanceof Enumeration) {
      List<Object> list = new ArrayList<Object>();
      Enumeration e = (Enumeration) value;
      while(e.hasMoreElements()) list.add(e.nextElement());
      return list.toArray();
    } else if (value instanceof Iterable) {
      List<Object> list = new ArrayList<Object>();
      Iterable v = (Iterable) value;
      Iterator i = v.iterator();
      while(i.hasNext()) list.add(i.next());
      return list.toArray();
    } else {
      return new Object[] {value};
    }
  }
}
