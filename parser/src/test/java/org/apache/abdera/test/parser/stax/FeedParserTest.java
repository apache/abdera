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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.junit.BeforeClass;
import org.junit.Test;

public class FeedParserTest extends BaseParserTestCase {

  static IRI baseURI;

  @BeforeClass
  public static void setUp() throws Exception {
    baseURI = new IRI("http://www.feedparser.org/tests/wellformed/atom10/");
  }

  @Test public void testAtom10Namespace() throws Exception {
    Document doc = parse(baseURI.resolve("atom10_namespace.xml"));
    assertNotNull(doc);
  }
  
  @Test public void testEntryAuthorEmail() throws Exception {
    Document<Feed> doc = parse(baseURI.resolve("entry_author_email.xml"));
    Feed feed = doc.getRoot();
    Entry entry = feed.getEntries().get(0);
    Person person = entry.getAuthor();
    assertEquals(person.getEmail(), "me@example.com");
  }
  
  @Test public void testEntryAuthorName() throws Exception {
    Document doc = parse(baseURI.resolve("entry_author_name.xml"));
    Feed feed = (Feed) doc.getRoot();
    Entry entry = feed.getEntries().get(0);
    Person person = entry.getAuthor();
    assertEquals(person.getName(), "Example author");    
  }
  
  @Test public void testEntryContentBase64() throws Exception {
    Document doc = parse(baseURI.resolve("entry_content_base64.xml"));
    Feed feed = (Feed)doc.getRoot();
    Entry entry = feed.getEntries().get(0);
    Content mediaContent = entry.getContentElement();
    assertEquals(mediaContent.getMimeType().toString(), "application/octet-stream");
    DataHandler dataHandler = mediaContent.getDataHandler();
    InputStream in = (ByteArrayInputStream) dataHandler.getContent();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int n = -1;
    while ((n = in.read()) > -1) { baos.write(n); }
    assertEquals(baos.toString(), "Example <b>Atom</b>");
  }
  
  @Test public void testEntryContentBase642() throws Exception {
    Document doc = parse(baseURI.resolve("entry_content_base64_2.xml"));
    Feed feed = (Feed)doc.getRoot();
    Entry entry = feed.getEntries().get(0);
    Content mediaContent = entry.getContentElement();
    assertEquals(mediaContent.getMimeType().toString(), "application/octet-stream");
    DataHandler dataHandler = mediaContent.getDataHandler();
    InputStream in = (ByteArrayInputStream) dataHandler.getContent();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int n = -1;
    while ((n = in.read()) > -1) { baos.write(n); }
    assertEquals(baos.toString(), "<p>History of the &lt;blink&gt; tag</p>");
  }
  
  
}
