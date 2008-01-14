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
package org.apache.abdera.i18n.test.iri;

import java.util.Locale;

import junit.framework.TestCase;

import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.rfc4646.Range;

public class TestLang extends TestCase {

  @SuppressWarnings("deprecation") 
  public static void testLang() throws Exception {
    
    org.apache.abdera.i18n.lang.Lang lang = 
      new org.apache.abdera.i18n.lang.Lang("en-US-ca");
    Locale testLocale = new Locale("en", "US", "ca");
        
    assertEquals(lang.getPrimary(),"en");
    assertEquals(lang.getSubtag(0),"US");
    assertEquals(lang.getSubtag(1),"ca");
    
    assertEquals( testLocale, lang.getLocale() );
    assertEquals(testLocale.toString(), lang.getLocale().toString());
    assertEquals(testLocale.getDisplayCountry(), lang.getLocale().getDisplayCountry());
    assertEquals(testLocale.getDisplayLanguage(), lang.getLocale().getDisplayLanguage());
    assertEquals( testLocale.getDisplayVariant(), lang.getLocale().getDisplayVariant());
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
      lang = new org.apache.abdera.i18n.lang.Lang("en_US");
    } catch (org.apache.abdera.i18n.lang.InvalidLangTagSyntax ex) {
      e = ex;
    }
    assertNotNull(e);
  }
  
  public static void test4646Lang() throws Exception {
    Lang lang = new Lang("en-Latn-US-valencia");
    assertEquals(lang.getLanguage().toString(),"en");
    assertEquals(lang.getRegion().toString(), "US");
    assertEquals(lang.getScript().toString(), "Latn");
    assertEquals(lang.getVariant().toString(), "valencia");
    assertNull(lang.getExtLang());
    assertNull(lang.getExtension());
    assertNull(lang.getPrivateUse());
    assertTrue(lang.isValid());
    Locale locale = lang.getLocale();
    assertEquals(locale.getCountry(),"US");
    assertEquals(locale.getLanguage(),"en");
    assertEquals(locale.getVariant(),"valencia");
  }
  
  public static void test4647Matching() throws Exception {
    Lang lang = new Lang("en-Latn-US-valencia");
    Range range1 = new Range("*",true);
    Range range2 = new Range("en-*",true);
    Range range3 = new Range("en-Latn-*",true);
    Range range4 = new Range("en-US-*",true);
    Range range5 = new Range("en-*-US-*",true);
    Range range6 = new Range("*-US",true);
    Range range7 = new Range("*-valencia",true);
    Range range8 = new Range("*-FR",true);
    assertTrue(range1.matches(lang,true));
    assertTrue(range2.matches(lang,true));
    assertTrue(range3.matches(lang,true));
    assertTrue(range4.matches(lang,true));
    assertTrue(range5.matches(lang,true));
    assertTrue(range6.matches(lang,true));
    assertTrue(range7.matches(lang,true));
    assertFalse(range8.matches(lang,true));
  }
}
