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

import java.util.Date;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.util.ConfigProperties;
import org.apache.abdera.util.URIHelper;
import org.apache.abdera.xpath.XPath;

import junit.framework.TestCase;

public class CoreTest extends TestCase {

  public static void testDefaultConfigurationProperties() {
    assertEquals(
      ConfigProperties.getDefaultFactory(), 
      "org.apache.abdera.parser.stax.FOMFactory");
    assertEquals(
      ConfigProperties.getDefaultParser(),
      "org.apache.abdera.parser.stax.FOMParser");
    assertEquals(
      ConfigProperties.getDefaultXPath(),
      "org.apache.abdera.parser.stax.FOMXPath");
  }
  
  public static void testMinimalConfiguration() {
    assertNotNull(Factory.INSTANCE);
    assertNotNull(Parser.INSTANCE);
    assertNotNull(XPath.INSTANCE);
  }
  
  public static void testUriNormalization() {
    try {
      assertEquals(
        URIHelper.normalize(
          "HTTP://www.EXAMPLE.org:80/foo/../Bar/%3f/./foo/."), 
        "http://www.example.org/Bar/%3F/foo/");
      assertEquals(
        URIHelper.normalize(
          "HTTPs://www.EXAMPLE.org:443/foo/../Bar/%3f/./foo/."), 
        "https://www.example.org/Bar/%3F/foo/");
      assertEquals(
        URIHelper.normalize(
          "HTTP://www.EXAMPLE.org:81/foo/../Bar/%3f/./foo/."), 
        "http://www.example.org:81/Bar/%3F/foo/");
      assertEquals(
        URIHelper.normalize(
          "HTTPs://www.EXAMPLE.org:444/foo/../Bar/%3f/./foo/."), 
        "https://www.example.org:444/Bar/%3F/foo/");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void testAtomDate() {
    Date now = new Date();
    AtomDate atomNow = AtomDate.valueOf(now);
    String rfc3339 = atomNow.getValue();
    atomNow = AtomDate.valueOf(rfc3339);
    Date parsed = atomNow.getDate();
    assertEquals(now, parsed);
  }
}
