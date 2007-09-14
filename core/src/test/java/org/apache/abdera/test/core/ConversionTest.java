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
package org.apache.abdera.test.core;

import junit.framework.TestCase;

import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.Converter;
import org.apache.abdera.converter.DefaultConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public class ConversionTest extends TestCase {

  public static void testConverter() 
    throws Exception {
    
    ConversionContext context = new DefaultConversionContext();
    context.setConverter(Foo.class, new FooConverter());
    Foo foo = new Foo();
    
    ObjectContext fooContext = new ObjectContext(foo);
    Converter<Element> converter = context.getConverter(fooContext);
    
    assertNotNull(converter);
    assertTrue(converter instanceof FooConverter);
    
    Element fooEl = context.convert(foo,fooContext);
    
    assertNotNull(fooEl);
    
  }
  
  public static void testConverterProvider()
    throws Exception {
    
    ConversionContext context = new DefaultConversionContext();
    Bar bar = new Bar();
    
    ObjectContext barContext = new ObjectContext(bar);
    Converter<Element> converter = context.getConverter(barContext);
    
    assertNotNull(converter);
    assertTrue(converter instanceof FooConverter);
    
    Element barEl = context.convert(bar);
    
    assertNotNull(barEl);
    
  }
  
  public static void testConverterAnnotation() 
    throws Exception {
    
    ConversionContext context = new DefaultConversionContext();
    Baz baz = new Baz();
    
    ObjectContext bazContext = new ObjectContext(baz);
    Converter<Element> converter = context.getConverter(bazContext);
    
    assertNotNull(converter);
    assertTrue(converter instanceof FooConverter);
    
    Element bazEl = context.convert(baz);
    
    assertNotNull(bazEl);
    
  }
  
  public static void testNoConverter() 
    throws Exception {

    ConversionContext context = new DefaultConversionContext();
    Foo foo = new Foo();
    
    ObjectContext fooContext = new ObjectContext(foo);
    Converter<Element> converter = context.getConverter(fooContext);
    
    assertNull(converter);
    
    try {
      context.convert(foo);
      fail("Expected error not thrown");
    } catch (Exception e) {}
   
  }
  
  
  public static class Foo {}
  
  public static class Bar {}
  
  @org.apache.abdera.converter.annotation.Converter(FooConverter.class) 
  public static class Baz {}
  
  public static class FooConverter 
    extends Converter<Element> {
      public Element convert(
        Object source,
        ObjectContext objectContext, 
        ConversionContext context) {
          Element el = new ElementWrapper(null) {};
          return el;
    }
  }
}
