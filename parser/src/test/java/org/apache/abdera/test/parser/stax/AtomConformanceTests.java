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

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.Parser;
import org.apache.axiom.om.OMElement;


import junit.framework.TestCase;

public class AtomConformanceTests extends TestCase {

  private static Document<Feed> get(URI uri) {
    try {
      return Parser.INSTANCE.parse(uri.toURL().openStream(), uri);
    } catch (Exception e) {}
    return null;
  }
  
  /**
   * Test to make sure that the parser properly detects the various kinds of
   * extended content types allowed by Atom
   */
  public static void testContentTypes() throws Exception {
    URI uri = new URI("http://www.snellspace.com/public/contentsummary.xml");
    Document<Feed> doc = get(uri);
    Feed feed = doc.getRoot();
    int n = 1;
    for (Entry entry : feed.getEntries()) {
      Content content = entry.getContentElement();
      Text summary = entry.getSummaryElement();
      switch (n) {
        case 1:
          // XML Content Type
          assertEquals(Content.Type.XML, content.getContentType());
          assertTrue(content.getMimeType().match("application/xml"));
          assertEquals(Text.Type.TEXT, summary.getTextType());
          break;
        case 2:
          // XML Content Type by src reference
          assertEquals(Content.Type.XML, content.getContentType());
          assertNotNull(content.getResolvedSrc());
          assertEquals(Text.Type.TEXT, summary.getTextType());
          break;
        case 3:
          // Text Content Type. This is really an order test,
          // to determine how a reader selects which text to show
          assertEquals(Content.Type.TEXT, content.getContentType());
          assertEquals(Text.Type.TEXT, summary.getTextType());
          break;
        case 4:
          // Text Content Type. This is really an order test,
          // to determine how a reader selects which text to show
          assertEquals(Content.Type.TEXT, content.getContentType());
          assertEquals(Text.Type.TEXT, summary.getTextType());
          break;
        case 5:
          // Embedded iCalendar
          assertEquals(Content.Type.MEDIA, content.getContentType());
          assertTrue(content.getMimeType().match("text/calendar"));
          assertEquals(Text.Type.TEXT, summary.getTextType());
          break;
        case 6:
          // Embedded Base64 encoded GIF
          assertEquals(Content.Type.MEDIA, content.getContentType());
          assertTrue(content.getMimeType().match("image/gif"));
          assertEquals(Text.Type.TEXT, summary.getTextType());
          break;
      }
      n++;
    }
  }
  
  /**
   * Tests the parsers support for various XML Namespace options 
   */
  public static void testXmlNamespace() throws Exception {
    String[] tests = {
      "http://plasmasturm.org/attic/atom-tests/nondefaultnamespace.atom",
      "http://plasmasturm.org/attic/atom-tests/nondefaultnamespace-xhtml.atom",
      "http://hsivonen.iki.fi/test/unknown-namespace.atom",
      "http://plasmasturm.org/attic/atom-tests/nondefaultnamespace-baseline.atom"
    };
    int n = 1;
    for (String test : tests) {
      URI uri = new URI(test);
      Document<Feed> doc = get(uri);
      assertNotNull(doc);
      Feed feed = doc.getRoot();
      Entry entry = feed.getEntries().get(0);
      switch(n) {
        case 1:
          assertNotNull(entry.getTitleElement());
          assertEquals(entry.getIdElement().getValue(), 
              new URI("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"));
          Content content = entry.getContentElement();
          assertNotNull(content);
          assertEquals(content.getContentType(), Content.Type.XHTML);
          OMElement element = (OMElement)content;
          OMElement div = 
            element.getFirstChildWithName(
              new QName("http://www.w3.org/1999/xhtml", "div"));
          assertNotNull(div);
          break;
        case 2:
          assertNotNull(entry.getTitleElement());
          assertEquals(entry.getIdElement().getValue(), 
              new URI("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"));
          content = entry.getContentElement();
          assertNotNull(content);
          assertEquals(content.getContentType(), Content.Type.XHTML);
          element = (OMElement)content;
          div = 
            element.getFirstChildWithName(
              new QName("http://www.w3.org/1999/xhtml", "div"));
          assertNotNull(div);
          break;
        case 3:
          assertNotNull(entry.getTitleElement());
          assertEquals(entry.getIdElement().getValue(), 
              new URI("http://hsivonen.iki.fi/test/unknown-namespace.atom/entry"));
          content = entry.getContentElement();
          assertNotNull(content);
          assertEquals(content.getContentType(), Content.Type.XHTML);
          element = (OMElement)content;
          div = 
            element.getFirstChildWithName(
              new QName("http://www.w3.org/1999/xhtml", "div"));
          assertNotNull(div);
          break;
        case 4:
          assertNotNull(entry.getTitleElement());
          assertEquals(entry.getIdElement().getValue(), 
              new URI("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"));
          content = entry.getContentElement();
          assertNotNull(content);
          assertEquals(content.getContentType(), Content.Type.XHTML);
          element = (OMElement)content;
          div = 
            element.getFirstChildWithName(
              new QName("http://www.w3.org/1999/xhtml", "div"));
          assertNotNull(div);
          break;
      }
      n++;
    }
  }
  
  /**
   * Test to ensure that the parser properly resolves relative URI
   */
  public static void testXmlBase() throws Exception {
    //http://tbray.org/ongoing/ongoing.atom
    URI uri = new URI("http://www.tbray.org/ongoing/ongoing.atom");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertEquals(feed.getBaseUri(), new URI("http://www.tbray.org/ongoing/ongoing.atom"));
    assertEquals(feed.getLogoElement().getResolvedValue(), new URI("http://www.tbray.org/ongoing/rsslogo.jpg"));
    assertEquals(feed.getIconElement().getResolvedValue(),new URI("http://www.tbray.org/favicon.ico"));    
  }
  
  /**
   * Test to ensure that the parser properly resolves relative URI
   */
  public static void testXmlBase2() throws Exception {
    //http://plasmasturm.org/attic/atom-tests/xmlbase.atom
    URI uri = new URI("http://plasmasturm.org/attic/atom-tests/xmlbase.atom");
    URI result = new URI("http://example.org/tests/base/result.html");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    int n = 1;
    for (Entry entry : feed.getEntries()) {
      switch(n) {
        case 1:
          assertEquals(entry.getAlternateLink().getResolvedHref(), result);
          break;
        case 2:
          assertEquals(entry.getAlternateLink().getResolvedHref(), result);
          break;
        case 3:
          assertEquals(entry.getAlternateLink().getResolvedHref(), result);
          break;
        case 4:
          assertEquals(entry.getAlternateLink().getResolvedHref(), result);
          break;
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 16:
          // content tests... skipping
          // we defer the proper handling of xml:base in the content to the
          // application using the parser. 
          break;
      }
      n++;
    }
  }
  
  public static void testUpdated() throws Exception {
    //http://intertwingly.net/testcase/updated.atom
    //Note: This test determines whether or not applications properly
    //      detect content changes in an atom entry based on the value
    //      of the atom:updated.  This isn't really relevant at this 
    //      level.  The responsibility for properly detecting modifications
    //      belongs to the application.
  }
  
  /**
   * Test support for markup in Text constructs
   */
  public static void testTitle() throws Exception {
    String[] tests = {
      "html-cdata.atom",
      "html-entity.atom",
      "html-ncr.atom",
      "text-cdata.atom",
      "text-entity.atom",
      "text-ncr.atom",
      "xhtml-entity.atom",
      "xhtml-ncr.atom"};
    URI baseUri = new URI("http://atomtests.philringnalda.com/tests/item/title/");
    int n = 1;
    for (String test : tests) {
      Document<Feed> doc = get(baseUri.resolve(test));
      assertNotNull(doc);
      Feed feed = doc.getRoot();
      Entry entry = feed.getEntries().get(0);
      assertNotNull(entry);
      Text title = entry.getTitleElement();
      assertNotNull(title);
      switch(n) {
        case 1:
          // The parser passes escaped HTML back up to the application.
          // is the applications responsibility to properly display it
          String value = title.getValue();
          assertEquals(value, "&lt;title>");
          break;
        case 2:
          // The parser passes escaped HTML back up to the application.
          // is the applications responsibility to properly display it
          value = title.getValue();
          assertEquals(value, "&lt;title>");
          break;
        case 3:
          // The parser passes escaped HTML back up to the application.
          // is the applications responsibility to properly display it
          value = title.getValue();
          assertEquals(value, "&lt;title>");          
          break;
        case 4:
          value = title.getValue();
          assertEquals(value, "<title>");          
          break;
        case 5:
          value = title.getValue();
          assertEquals(value, "<title>");
          break;
        case 6:
         
          value = title.getValue();
          assertEquals(value, "<title>");
          break;
        case 7:
          Div div = title.getValueElement();
          assertEquals(div.getValue(), "<title>");
          break;
        case 8:
          div = title.getValueElement();
          assertEquals(div.getValue(), "<title>");
          break;
      }
      n++;
    }
  }
  
  /**
   * This tests the parsers ability to properly ignore the ordering of elements
   * in the Atom feed/entry.  The parser should be able to properly select the
   * requested elements regardless of the order in which they appear
   */
  public static void testOrder() throws Exception {
    //http://www.snellspace.com/public/ordertest.xml
    URI uri = new URI("http://www.snellspace.com/public/ordertest.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    List<Entry> entries = feed.getEntries();
    int n = 1;
    for (Entry entry : entries ) {
      switch(n) {
        case 1:
          assertEquals(entry.getIdElement().getValue(), new URI("tag:example.org,2006:atom/conformance/element_order/1"));
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          assertEquals(entry.getSummaryType(), Text.Type.TEXT);
          assertNotNull(entry.getUpdatedElement().getValue());
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          break;
        case 2:
          assertEquals(entry.getIdElement().getValue(), new URI("tag:example.org,2006:atom/conformance/element_order/2"));
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          assertEquals(entry.getSummaryType(), Text.Type.TEXT);
          assertNotNull(entry.getUpdatedElement().getValue());
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          break;
        case 3:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),2);
          assertEquals(
            entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
            new URI("http://www.snellspace.com/public/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(1).getHref(), 
              new URI("http://www.snellspace.com/public/alternate2"));
          break;
        case 4:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/alternate"));
          break;
        case 5:
          Text title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          String value = title.getValue();
          assertEquals(value, "Entry with a source first");
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/alternate"));          
          break;
        case 6:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Entry with a source last");
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/alternate"));
          break;
        case 7:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Entry with a source in the middle");
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/alternate"));          
          break;
        case 8:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Atom elements in an extension element");
          assertEquals(
            entry.getIdElement().getValue(), 
            new URI("tag:example.org,2006:atom/conformance/element_order/8"));
          break;
        case 9:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Atom elements in an extension element");
          assertEquals(
            entry.getIdElement().getValue(), 
            new URI("tag:example.org,2006:atom/conformance/element_order/9"));
          break;
      }
      n++;
    }
  }
  
  /**
   * Tests the parsers support for the various link relation types
   */
  public static void testLink() throws Exception {
    //http://www.snellspace.com/public/linktests.xml
    URI uri = new URI("http://www.snellspace.com/public/linktests.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    List<Entry> entries = feed.getEntries();
    int n = 1;
    for (Entry entry : entries) {
      switch(n) {
        case 1:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
            entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
            new URI("http://www.snellspace.com/public/linktests/alternate"));
          break;
        case 2:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),4);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(1).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/alternate"));          
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(2).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/alternate2"));
          break;
        case 3:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(entry.getLinks(Link.REL_ENCLOSURE).size(),1);
          assertEquals(entry.getLinks(Link.REL_RELATED).size(),1);
          assertEquals(entry.getLinks(Link.REL_SELF).size(),1);
          assertEquals(entry.getLinks(Link.REL_VIA).size(),1);    
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_ENCLOSURE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/enclosure"));
          assertEquals(
              entry.getLinks(Link.REL_RELATED).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/related"));
          assertEquals(
              entry.getLinks(Link.REL_SELF).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/self"));
          assertEquals(
              entry.getLinks(Link.REL_VIA).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/via"));
          break;
        case 4:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),2);
          assertEquals(entry.getLinks(Link.REL_ENCLOSURE).size(),1);
          assertEquals(entry.getLinks(Link.REL_RELATED).size(),1);
          assertEquals(entry.getLinks(Link.REL_SELF).size(),1);
          assertEquals(entry.getLinks(Link.REL_VIA).size(),1);          
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(1).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/alternate2"));
          assertEquals(
              entry.getLinks(Link.REL_ENCLOSURE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/enclosure"));
          assertEquals(
              entry.getLinks(Link.REL_RELATED).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/related"));
          assertEquals(
              entry.getLinks(Link.REL_SELF).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/self"));
          assertEquals(
              entry.getLinks(Link.REL_VIA).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/via"));
          break;
        case 5:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(entry.getLinks(Link.REL_LICENSE).size(),1);  
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_LICENSE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/license"));
          break;
        case 6:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(entry.getLinks("http://example.org").size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks("http://example.org").get(0).getHref(), 
              new URI("http://www.snellspace.com/public/linktests/example"));
          break;
      }
      n++;
    }
  }
}
