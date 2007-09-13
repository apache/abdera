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
package org.apache.abdera.test.converter;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.converter.ConventionConversionContext;
import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.ID;
import org.apache.abdera.converter.annotation.Title;

public class ConventionsTest extends TestCase {

  /**
   * Tests the convention matching mechanism
   */
  public static void testConventions() throws Exception {
    
    Abdera abdera = new Abdera();
    ConventionConversionContext context = 
      new ConventionConversionContext(abdera);
    Conventions conventions = context.getConventions();
    Foo foo = new Foo();
    ObjectContext fooContext = new ObjectContext(foo);
    
    AccessibleObject[] accessors = fooContext.getAccessors();
    for (AccessibleObject accessor : accessors) {
      Match match = accessor.getAnnotation(Match.class);
      Class<? extends Annotation> matchingAnnotation = 
        conventions.matchConvention(accessor);
      if (match != null) {
        assertEquals(match.value(),matchingAnnotation);
      } else {
        assertNull(matchingAnnotation);
      }
    }
  }
  
  public static class Foo {
    @Match(ID.class)
    public String getId() {
      return null;
    }
    public String getIdentifier() {
      return null;
    }
    @Match(Title.class)
    public String getTitle() {
      return null;
    }
  }
  
  @Retention(RUNTIME)
  @Target({METHOD,FIELD})
  public @interface Match {
    Class<? extends Annotation> value();
  }
  
}
