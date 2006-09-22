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

import java.net.URI;

import org.apache.abdera.util.iri.IRI;
import org.apache.abdera.util.unicode.Normalizer;


import junit.framework.TestCase;

public class TestIRI extends TestCase {

  public static void testSimple() throws Exception {
    IRI iri = new IRI("http://validator.w3.org/check?uri=http%3A%2F%2Fr\u00E9sum\u00E9.example.org");
    assertEquals(iri.toString(),"http://validator.w3.org/check?uri=http%3A%2F%2Frésumé.example.org");
    assertEquals(iri.toURI().toString(),"http://validator.w3.org/check?uri=http://r%C3%A9sum%C3%A9.example.org");   
  }

  public static void testSimple2() throws Exception {
    IRI iri = new IRI("http://www.example.org/red%09ros\u00E9#red");
    assertEquals(iri.toURI().toString(),"http://www.example.org/red%09ros%C3%A9#red");
  }
  
  
  public static void testNotSoSimple() throws Exception {
    IRI iri = new IRI("http://example.com/\uD800\uDF00\uD800\uDF01\uD800\uDF02");
    assertEquals(iri.toURI().toString(),"http://example.com/%F0%90%8C%80%F0%90%8C%81%F0%90%8C%82");
  }
    
  public static void testURItoIRI() throws Exception {
    URI uri = new URI("http://www.example.org/D%C3%BCrst");
    IRI iri = new IRI(uri);
    assertEquals(iri.toString(),"http://www.example.org/D\u00FCrst");
  }
  
  public static void testURItoIRI2() throws Exception {
    URI uri = new URI("http://www.example.org/D%FCrst");
    IRI iri = new IRI(uri);
    assertEquals(iri.toString(),"http://www.example.org/D%FCrst");
  }

  public static void testURItoIRI3() throws Exception {
    URI uri = new URI("http://xn--99zt52a.example.org/%e2%80%ae");
    IRI iri = new IRI(uri);
    assertEquals(iri.toString(),"http://xn--99zt52a.example.org/%E2%80%AE");
  }

  public static void testIRItoURI() throws Exception {
    IRI iri = new IRI("http://\u7D0D\u8C46.example.org/%E2%80%AE");
    URI uri = iri.toURI();
    assertEquals(uri.toString(),"http://xn--99zt52a.example.org/%E2%80%AE");
  }
  

  public static void testComparison() throws Exception {
    IRI iri1 = new IRI("http://www.example.org/");
    IRI iri2 = new IRI("http://www.example.org/..");
    IRI iri3 = new IRI("http://www.Example.org:80");
    
    assertFalse(iri1.equals(iri2)); // false
    assertFalse(iri1.equals(iri3)); // false
    assertFalse(iri2.equals(iri1)); // false
    assertFalse(iri2.equals(iri3)); // false
    assertFalse(iri3.equals(iri1)); // false
    assertFalse(iri3.equals(iri2)); // false
    
    assertTrue(iri1.normalize().equals(iri2.normalize()));  //true
    assertFalse(iri1.normalize().equals(iri3.normalize()));  //false
    assertTrue(iri2.normalize().equals(iri1.normalize()));  //true
    assertFalse(iri2.normalize().equals(iri3.normalize()));  //false
    assertFalse(iri3.normalize().equals(iri1.normalize()));  //false
    assertFalse(iri3.normalize().equals(iri2.normalize()));  //false
    
    assertTrue(iri1.equivalent(iri2)); // true
    assertTrue(iri1.equivalent(iri3)); // true
    assertTrue(iri2.equivalent(iri1)); // true
    assertTrue(iri2.equivalent(iri3)); // true
    assertTrue(iri3.equivalent(iri1)); // true
    assertTrue(iri3.equivalent(iri2)); // true
  }
  
  
  public static void testUCN() throws Exception {
    IRI iri1 = new IRI("http://www.example.org/r\u00E9sum\u00E9.html");
    IRI iri2 = new IRI("http://www.example.org/re\u0301sume\u0301.html", Normalizer.Form.C);
    assertEquals(iri1,iri2);
  }
  
  public static void testPercent() throws Exception {
      IRI iri1 = new IRI("http://example.org/~user");
      IRI iri2 = new IRI("http://example.org/%7euser");
      IRI iri3 = new IRI("http://example.org/%7Euser");
      assertTrue(iri1.normalize().equals(iri2.normalize()));
      assertTrue(iri1.normalize().equals(iri3.normalize()));
  }
  
  public static void testIDN() throws Exception {
    IRI iri1 = new IRI("http://r\u00E9sum\u00E9.example.org");
    IRI iri2 = new IRI("http://xn--rsum-bpad.example.org");
    assertTrue(iri1.equivalent(iri2));
  }
  
  public static void testMixedEncodings() throws Exception {
    IRI iri = new IRI("http://www.example.org/r%E9sum%E9.xml#r%C3%A9sum%C3%A9");
    assertEquals(iri.normalize().toString(),"http://www.example.org/r%E9sum%E9.xml#r\u00E9sum\u00E9");
  }
  
  public static void testRelative() throws Exception{
    IRI base = new IRI("http://example.org/foo/");
    
    assertEquals(base.resolve("/").toString(),"http://example.org/");
    assertEquals(base.resolve("/test").toString(),"http://example.org/test");
    assertEquals(base.resolve("test").toString(),"http://example.org/foo/test");
    assertEquals(base.resolve("../test").toString(),"http://example.org/test");
    assertEquals(base.resolve("./test").toString(),"http://example.org/foo/test");
    assertEquals(base.resolve("test/test/../../").toString(),"http://example.org/foo/");
    assertEquals(base.resolve("?test").toString(),"http://example.org/foo/?test");
    assertEquals(base.resolve("#test").toString(),"http://example.org/foo/#test");
    assertEquals(base.resolve(".").toString(),"http://example.org/foo/");
  }

  
  
  
}
