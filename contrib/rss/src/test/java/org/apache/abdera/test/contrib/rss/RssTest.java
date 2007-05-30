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
package org.apache.abdera.test.contrib.rss;

import java.io.InputStream;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

import junit.framework.TestCase;

public class RssTest extends TestCase {

  public static void testRSS1() {
    
    Abdera abdera = new Abdera();
    
    InputStream in = RssTest.class.getResourceAsStream("/rss1.rdf");
    
    Document<Feed> doc = abdera.getParser().parse(in);
    
    Feed feed = doc.getRoot();
    
    assertEquals("XML.com",feed.getTitle());
    assertEquals("http://xml.com/pub", feed.getAlternateLinkResolvedHref().toASCIIString());
    assertEquals("XML.com features a rich mix of information and services \n      for the XML community.", feed.getSubtitle().trim());
    List<Entry> entries = feed.getEntries();
    assertEquals(2, entries.size());
    
    // TODO: finish this test as the RSS 1.0 impl is completed
  }
  
}
