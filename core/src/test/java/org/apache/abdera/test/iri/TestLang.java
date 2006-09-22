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
package org.apache.abdera.test.iri;

import org.apache.abdera.util.lang.InvalidLangTagSyntax;
import org.apache.abdera.util.lang.Lang;

import junit.framework.TestCase;

public class TestLang extends TestCase {

  public static void testLang() throws Exception {
    
    Lang lang = new Lang("en-US-ca");
    
    assertEquals(lang.getPrimary(),"en");
    assertEquals(lang.getSubtag(0),"US");
    assertEquals(lang.getSubtag(1),"ca");
    
    assertEquals(lang.getLocale().toString(), "en_US_ca");
    assertEquals(lang.getLocale().getDisplayCountry(), "United States");
    assertEquals(lang.getLocale().getDisplayLanguage(), "English");
    
    assertTrue(lang.matches("*"));
    assertTrue(lang.matches("en"));
    assertTrue(lang.matches("EN"));
    assertTrue(lang.matches("en-US"));
    assertTrue(lang.matches("en-us"));
    assertTrue(lang.matches("en-US-ca"));
    assertTrue(lang.matches("en-us-ca"));
    assertFalse(lang.matches("en-US-ca-bob"));
    assertFalse(lang.matches("en-US-fr"));
    
    Exception e = null;
    try {
      lang = new Lang("en_US");
    } catch (InvalidLangTagSyntax ex) {
      e = ex;
    }
    assertNotNull(e);
  }
  
}
