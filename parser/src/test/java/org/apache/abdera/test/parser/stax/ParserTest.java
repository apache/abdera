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
package org.apache.abdera.test.parser.stax;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMParser;
import org.apache.abdera.parser.stax.FOMXPath;
import org.apache.abdera.xpath.XPath;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

  public static void testConfiguration() {
    assertTrue(Factory.INSTANCE instanceof FOMFactory);
    assertTrue(Parser.INSTANCE instanceof FOMParser);
    assertTrue(XPath.INSTANCE instanceof FOMXPath);
  }
  
  public static void testParse() {
    
    Document<Feed> feedDoc = Parser.INSTANCE.parse(ParserTest.class.getResourceAsStream("/simpleFeed.xml"));
    assertTrue(feedDoc.getRoot() instanceof Feed);
    assertEquals(feedDoc.getCharset(), "utf-8");
    
    Document<Entry> entryDoc = Parser.INSTANCE.parse(ParserTest.class.getResourceAsStream("/simpleEntry.xml"));
    assertTrue(entryDoc.getRoot() instanceof Entry);
    assertEquals(entryDoc.getCharset(), "utf-8");
    
    Document<Service> serviceDoc = Parser.INSTANCE.parse(ParserTest.class.getResourceAsStream("/simpleService.xml"));
    assertTrue(serviceDoc.getRoot() instanceof Service);
    assertEquals(serviceDoc.getCharset(), "utf-8");
    
  }
  
  //TODO: need lots more unit tests
}
