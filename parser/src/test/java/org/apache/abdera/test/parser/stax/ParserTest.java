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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.Parser;

public class ParserTest extends TestCase {

  private static Abdera abdera = new Abdera();
  
  private static Parser getParser() {
    return abdera.getParser();
  }
  
  public static void testParse() throws Exception {
    
    Document<Feed> feedDoc = getParser().parse(ParserTest.class.getResourceAsStream("/simpleFeed.xml"));
    assertTrue(feedDoc.getRoot() instanceof Feed);
    assertEquals(feedDoc.getCharset(), "utf-8");
    
    Document<Entry> entryDoc = getParser().parse(ParserTest.class.getResourceAsStream("/simpleEntry.xml"));
    assertTrue(entryDoc.getRoot() instanceof Entry);
    assertEquals(entryDoc.getCharset(), "utf-8");
    
    Document<Service> serviceDoc = getParser().parse(ParserTest.class.getResourceAsStream("/simpleService.xml"));
    assertTrue(serviceDoc.getRoot() instanceof Service);
    assertEquals(serviceDoc.getCharset(), "utf-8");
    
  }
  
  public static void testParseReader() throws Exception {

    InputStream is = ParserTest.class.getResourceAsStream("/simpleFeed.xml");
    Document<Feed> feedDoc = getParser().parse(new InputStreamReader(is), 
        ParserTest.class.getResource("/simpleEntry.xml").toExternalForm());
    assertTrue(feedDoc.getRoot() instanceof Feed);
    assertEquals(feedDoc.getCharset(), "utf-8");

    is = ParserTest.class.getResourceAsStream("/simpleEntry.xml");
    Document<Entry> entryDoc = getParser().parse(new InputStreamReader(is),
        ParserTest.class.getResource("/simpleEntry.xml").toExternalForm());
    assertTrue(entryDoc.getRoot() instanceof Entry);
    assertEquals(entryDoc.getCharset(), "utf-8");

    is = ParserTest.class.getResourceAsStream("/simpleService.xml");
    Document<Service> serviceDoc = getParser().parse(new InputStreamReader(is),
        ParserTest.class.getResource("/simpleEntry.xml").toExternalForm());
    assertTrue(serviceDoc.getRoot() instanceof Service);
    assertEquals(serviceDoc.getCharset(), "utf-8");
    
  }
  
  /**
   * Test for ABDERA-22.
   * 
   * @see https://issues.apache.org/jira/browse/ABDERA-22
   */
  public static void testParseReaderNoBase() throws Exception {

    InputStream is = ParserTest.class.getResourceAsStream("/simpleEntry.xml");
    Reader reader = new InputStreamReader(is);
    Document<Entry> entryDoc = getParser().parse(reader);
    assertTrue(entryDoc.getRoot() instanceof Entry);
    assertEquals(entryDoc.getCharset(), "utf-8");
    
  }
  
  //TODO: need lots more unit tests
}
