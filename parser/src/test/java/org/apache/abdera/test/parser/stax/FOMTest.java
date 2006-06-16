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
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.filter.TextFilter;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.BlackListParseFilter;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.URIHelper;
import org.apache.abdera.util.WhiteListParseFilter;
import org.apache.abdera.xpath.XPath;


import junit.framework.TestCase;

public class FOMTest extends TestCase   {

  public void testParser() throws Exception {
    
    InputStream in = FOMTest.class.getResourceAsStream("/simple.xml");
    Document<Feed> doc = Parser.INSTANCE.parse(in);
    Feed feed = doc.getRoot();
    
    assertEquals(feed.getTitle(),"Example Feed");
    assertEquals(feed.getTitleType(), Text.Type.TEXT);
    assertEquals(feed.getAlternateLink().getResolvedHref().toString(), "http://example.org/");
    assertNotNull(feed.getUpdated());
    assertEquals(feed.getAuthor().getName(), "John Doe");
    assertEquals(feed.getId().toString(), "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
    
    Entry entry = feed.getEntries().get(0);

    assertEquals(entry.getTitle(),"Atom-Powered Robots Run Amok");
    assertEquals(entry.getTitleType(), Text.Type.TEXT);
    assertEquals(entry.getAlternateLink().getResolvedHref().toString(), "http://example.org/2003/12/13/atom03");
    assertEquals(entry.getId().toString(),"urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    assertNotNull(entry.getUpdated());
    assertEquals(entry.getSummary(), "Some text.");
    assertEquals(entry.getSummaryType(), Text.Type.TEXT);
    
  }

  public void testCreate() throws Exception {
    Feed feed = Factory.INSTANCE.newFeed();
    feed.setLanguage("en-US");
    feed.setBaseUri("http://example.org");
    
    feed.setTitleAsText("Example Feed");
    feed.addLink("http://example.org/");
    feed.addAuthor("John Doe");
    feed.setId("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", false);
    feed.addContributor("Bob Jones");
    feed.addCategory("example");
    
    Entry entry = feed.insertEntry();
    entry.setTitleAsText("Atom-Powered Robots Run Amok");
    entry.addLink("http://example.org/2003/12/13/atom03");
    entry.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", false);
    entry.setSummaryAsText("Some text.");
    
    Entry entry2 = feed.insertEntry();
    entry2.setTitleAsText("re: Atom-Powered Robots Run Amok");
    entry2.addLink("/2003/12/13/atom03/1");
    entry2.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b", false);
    entry2.setSummaryAsText("A response");
    entry2.addInReplyTo(entry);
    
    String compare = "<?xml version='1.0' encoding='UTF-8'?><a:feed xmlns:a=\"http://www.w3.org/2005/Atom\" xml:base=\"http://example.org\" xml:lang=\"en-US\"><a:title type=\"text\">Example Feed</a:title><a:link href=\"http://example.org/\" /><a:author><a:name>John Doe</a:name></a:author><a:id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</a:id><a:contributor><a:name>Bob Jones</a:name></a:contributor><a:category term=\"example\" /><a:entry><a:title type=\"text\">re: Atom-Powered Robots Run Amok</a:title><a:link href=\"/2003/12/13/atom03/1\" /><a:id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b</a:id><a:summary type=\"text\">A response</a:summary><thr:in-reply-to xmlns:thr=\"http://purl.org/syndication/thread/1.0\" href=\"http://example.org/2003/12/13/atom03\" ref=\"urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a\" /></a:entry><a:entry><a:title type=\"text\">Atom-Powered Robots Run Amok</a:title><a:link href=\"http://example.org/2003/12/13/atom03\" /><a:id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</a:id><a:summary type=\"text\">Some text.</a:summary></a:entry></a:feed>";
    
    ByteArrayOutputStream out = new ByteArrayOutputStream(512);
    feed.getDocument().writeTo(out);
    String actual = out.toString();
    
    assertEquals(actual, compare);
    
    assertEquals(feed.getEntries().get(0).getId().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b");
    assertEquals(feed.getEntries().get(1).getId().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    
  }

  public void testWhiteListParseFilter() throws Exception {
    
    ParseFilter filter = new WhiteListParseFilter();
    filter.add(Constants.FEED);
    filter.add(Constants.ENTRY);
    filter.add(Constants.TITLE);
    filter.add(Constants.ID);
    ParserOptions options = Parser.INSTANCE.getDefaultParserOptions();
    options.setParseFilter(filter);
    
    URL url = FOMTest.class.getResource("/simple.xml");
    InputStream in = url.openStream();

    Document<Feed> doc = Parser.INSTANCE.parse(in, url.toURI(), options);
    Feed feed = doc.getRoot();
    
    assertEquals(feed.getTitle(),"Example Feed");
    assertEquals(feed.getTitleType(), Text.Type.TEXT);
    assertNull(feed.getAlternateLink());
    assertNull(feed.getUpdated());
    assertNull(feed.getAuthor());
    assertEquals(feed.getId().toString(), "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
    
    Entry entry = feed.getEntries().get(0);

    assertEquals(entry.getTitle(),"Atom-Powered Robots Run Amok");
    assertEquals(entry.getTitleType(), Text.Type.TEXT);
    assertNull(entry.getAlternateLink());
    assertEquals(entry.getId().toString(),"urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    assertNull(entry.getUpdated());
    assertNull(entry.getSummary());
    assertNull(entry.getSummaryType());
  }
  
  public void testBlackListParseFilter() throws Exception {
    
    ParseFilter filter = new BlackListParseFilter();
    filter.add(Constants.UPDATED);
    ParserOptions options = Parser.INSTANCE.getDefaultParserOptions();
    options.setParseFilter(filter);
    
    URL url = FOMTest.class.getResource("/simple.xml");
    InputStream in = url.openStream();

    Document<Feed> doc = Parser.INSTANCE.parse(in, url.toURI(), options);
    Feed feed = doc.getRoot();
    
    assertEquals(feed.getTitle(),"Example Feed");
    assertEquals(feed.getTitleType(), Text.Type.TEXT);
    assertEquals(feed.getAlternateLink().getResolvedHref().toString(), "http://example.org/");
    assertNull(feed.getUpdated());
    assertEquals(feed.getAuthor().getName(), "John Doe");
    assertEquals(feed.getId().toString(), "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
    
    Entry entry = feed.getEntries().get(0);

    assertEquals(entry.getTitle(),"Atom-Powered Robots Run Amok");
    assertEquals(entry.getTitleType(), Text.Type.TEXT);
    assertEquals(entry.getAlternateLink().getResolvedHref().toString(), "http://example.org/2003/12/13/atom03");
    assertEquals(entry.getId().toString(),"urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    assertNull(entry.getUpdated());
    assertEquals(entry.getSummary(), "Some text.");
    assertEquals(entry.getSummaryType(), Text.Type.TEXT);

  }
  
  public void testTextFilter() throws Exception {
    
    TextFilter filter = new TextFilter() {
      @Override
      public String filterText(String text, Element parent) {
        ExtensionElement ee = (ExtensionElement) parent;
        QName qname = ee.getQName();
        Element elparent = parent.getParentElement();
        if (Constants.NAME.equals(qname)) {
          text = "Jane Doe";
        } else if (Constants.TITLE.equals(qname) && elparent instanceof Entry) {
          text = text.replaceAll("Amok", "Crazy");
        }
        return text;
      }
    };
    
    ParserOptions options = Parser.INSTANCE.getDefaultParserOptions();
    options.setTextFilter(filter);
    
    URL url = FOMTest.class.getResource("/simple.xml");
    InputStream in = url.openStream();
    Document<Feed> doc = Parser.INSTANCE.parse(in, url.toURI(), options);
    Feed feed = doc.getRoot();
    
    assertEquals(feed.getTitle(),"Example Feed");
    assertEquals(feed.getTitleType(), Text.Type.TEXT);
    assertEquals(feed.getAlternateLink().getResolvedHref().toString(), "http://example.org/");
    assertNotNull(feed.getUpdated());
    assertEquals(feed.getAuthor().getName(), "Jane Doe");
    assertEquals(feed.getId().toString(), "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
    
    Entry entry = feed.getEntries().get(0);

    assertEquals(entry.getTitle(),"Atom-Powered Robots Run Crazy");
    assertEquals(entry.getTitleType(), Text.Type.TEXT);
    assertEquals(entry.getAlternateLink().getResolvedHref().toString(), "http://example.org/2003/12/13/atom03");
    assertEquals(entry.getId().toString(),"urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    assertNotNull(entry.getUpdated());
    assertEquals(entry.getSummary(), "Some text.");
    assertEquals(entry.getSummaryType(), Text.Type.TEXT);
    
  }
  
  public void testXPath() throws Exception {
    
    InputStream in = FOMTest.class.getResourceAsStream("/simple.xml");
    Document<Feed> doc = Parser.INSTANCE.parse(in);
    Feed feed = doc.getRoot();
    XPath xpath = XPath.INSTANCE;
    assertEquals(xpath.evaluate("count(/a:feed)", feed), 1.0d);
    assertTrue(xpath.isTrue("/a:feed/a:entry", feed));
    assertEquals(xpath.numericValueOf("count(/a:feed)", feed), 1.0d);
    assertEquals(xpath.valueOf("/a:feed/a:entry/a:title", feed), "Atom-Powered Robots Run Amok");
    assertEquals(xpath.selectNodes("/a:feed/a:entry", feed).size(), 1);
    assertTrue(xpath.selectSingleNode("/a:feed", feed) instanceof Feed);
    assertEquals(xpath.selectSingleNode("..", feed.getTitleElement()), feed);
    assertEquals(xpath.selectSingleNode("ancestor::*", feed.getEntries().get(0)), feed);
    assertEquals(xpath.valueOf("concat('The feed is is ',/a:feed/a:id)", feed), "The feed is is urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
    
  }
  
  public void testUriNormalization() throws Exception {
    String s1 = "HTTP://www.Example.ORG:80/./foo/%2d/../%2d/./foo";
    String s2 = "HTTP://www.Example.ORG:81/./foo/%2d/../%2d/./foo";
    assertEquals(URIHelper.normalize(s1), "http://www.example.org/foo/-/foo");
    assertEquals(URIHelper.normalize(s2), "http://www.example.org:81/foo/-/foo");
  }
  
  
}
