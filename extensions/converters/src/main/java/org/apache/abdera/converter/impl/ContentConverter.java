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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.AccessibleObject;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.converter.BaseConverter;
import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.ContentType;
import org.apache.abdera.converter.annotation.MediaType;
import org.apache.abdera.converter.annotation.Value;
import org.apache.abdera.i18n.io.InputStreamDataSource;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

@SuppressWarnings("unchecked")
public class ContentConverter 
  extends BaseConverter<Content> {
  
  @Override 
  protected Content create(
    ConversionContext context) {
      return context.getAbdera().getFactory()
        .newContent((Content.Type)null, null);
  }

  @Override
  protected void init(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Content dest) {
      ContentType ct = objectContext.getAnnotation(ContentType.class);
      MediaType mt = objectContext.getAnnotation(MediaType.class);
      if (ct != null) dest.setContentType(ct.value());
      setValue(source,dest,false,null,null);
      if (mt != null) dest.setMimeType(mt.value());
  }

  @Override 
  protected void finish(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Content dest) {
    String set = dest.getAttributeValue("set");
    dest.removeAttribute(new QName("set"));
    if ("no".equals(set)) setValue(source,dest,true,objectContext,context);
  }

  @Override 
  protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Conventions conventions, 
    Content dest,
    AccessibleObject accessor) {
      
    String set = dest.getAttributeValue("set");
    if (!"no".equals(set)) return;
    
    if (accessor.isAnnotationPresent(Value.class) || 
        Value.class.equals(conventions.matchConvention(accessor))) {
      Object value = eval(accessor, source);
      ObjectContext valueContext = new ObjectContext(value,source,accessor);
      setValue(value, dest, true, valueContext, context);
    } else if (accessor.isAnnotationPresent(ContentType.class) || 
        ContentType.class.equals(conventions.matchConvention(accessor))) {
      Object value = eval(accessor, source);
      ContentType ct = objectContext.getAnnotation(ContentType.class);
      if (value == null || ct != null) return;
      if (value instanceof Content.Type) {
        dest.setContentType((Content.Type)value);
      } else {
        Content.Type type = Content.Type.valueOf(value.toString().toUpperCase());
        dest.setContentType(type);
      }
    }
    
  }

  private void setValue(
    Object source, 
    Content dest, 
    boolean convertOther, 
    ObjectContext sourceContext,
    ConversionContext context) {
    if (source == null) return;
    try {
      if (source instanceof String) {
        dest.setValue(source.toString());
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof StringBuffer) {
        dest.setValue(source.toString());
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof DataHandler) {
        DataHandler dh = (DataHandler) source;
        dest.setText(dh);
        dest.setMimeType(dh.getContentType());
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof InputStream) {
        DataHandler dh = new DataHandler(new InputStreamDataSource((InputStream)source));
        dest.setText(dh);
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof Reader) {
        Reader rdr = (Reader)source;
        StringBuffer buf = new StringBuffer();
        char[] chars = new char[100];
        int r = -1;
        while ((r = rdr.read(chars)) != -1) buf.append(chars, 0, r);
        dest.setValue(buf.toString());
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof ReadableByteChannel) {
        InputStream in = Channels.newInputStream((ReadableByteChannel) source);
        DataHandler dh = new DataHandler(new InputStreamDataSource(in));
        dest.setText(dh);
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof Div) {
        dest.setValueElement((Div)source);
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof Element) {
        dest.setValueElement((Element)source);
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof Document) {
        Element root = ((Document)source).getRoot();
        if (root != null) dest.setValueElement(root);
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof URI) {
        dest.setSrc(source.toString());
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof URL) {
        dest.setSrc(source.toString());
        dest.removeAttribute(new QName("set"));
      } else if (source instanceof IRI) {
        dest.setSrc(source.toString());
        dest.removeAttribute(new QName("set"));
      } else {
        if (convertOther) {
          StringConverter c = new StringConverter();
          StringBuffer buf = c.convert(source, sourceContext, context);
          if (buf != null) dest.setValue(buf.toString());
          dest.removeAttribute(new QName("set"));
        } else dest.setAttributeValue("set", "no");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
