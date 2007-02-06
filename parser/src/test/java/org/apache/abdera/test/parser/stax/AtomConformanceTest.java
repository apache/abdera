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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.iri.IRI;
import org.apache.axiom.om.OMElement;

public class AtomConformanceTest extends BaseParserTestCase {
  
  private static Document<Feed> get(IRI uri) {
    try {
      return getParser().parse(uri.toURL().openStream(), uri.toString());
    } catch (Exception e) {}
    return null;
  }

  /**
   * Test to make sure that the parser properly detects the various kinds of
   * extended content types allowed by Atom
   */
  public static void testContentTypes() throws Exception {
    IRI uri = new IRI("http://www.snellspace.com/public/contentsummary.xml");
    Document<Feed> doc = parse(uri);
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
      "http://www.snellspace.com/public/nondefaultnamespace.xml",
      "http://www.snellspace.com/public/nondefaultnamespace2.xml",
      "http://www.snellspace.com/public/nondefaultnamespace3.xml"
    };
    int n = 1;
    for (String test : tests) {
      IRI uri = new IRI(test);
      Document<Feed> doc = parse(uri);
      assertNotNull(doc);
      Feed feed = doc.getRoot();
      Entry entry = feed.getEntries().get(0);
      switch(n) {
        case 1:
          assertNotNull(entry.getTitleElement());
          assertEquals(entry.getIdElement().getValue(), 
              new IRI("tag:example.org,2007:bar"));
          Text summary = entry.getSummaryElement();
          assertNotNull(summary);
          assertEquals(summary.getTextType(), Text.Type.XHTML);
          OMElement element = (OMElement)summary;
          OMElement div = 
            element.getFirstChildWithName(
              new QName("http://www.w3.org/1999/xhtml", "div"));
          assertNotNull(div);
          break;
        case 2:
          assertNotNull(entry.getTitleElement());
          assertEquals(entry.getIdElement().getValue(), 
              new IRI("tag:example.org,2007:bar"));
          summary = entry.getSummaryElement();
          assertNotNull(summary);
          assertEquals(summary.getTextType(), Text.Type.XHTML);
          element = (OMElement)summary;
          div = 
            element.getFirstChildWithName(
              new QName("http://www.w3.org/1999/xhtml", "div"));
          assertNotNull(div);
          break;
        case 3:
          assertNotNull(entry.getTitleElement());
          assertEquals(entry.getIdElement().getValue(), 
              new IRI("tag:example.org,2007:bar"));
          summary = entry.getSummaryElement();
          assertNotNull(summary);
          assertEquals(summary.getTextType(), Text.Type.XHTML);
          element = (OMElement)summary;
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
    IRI uri = new IRI("http://www.snellspace.com/public/xmlbase.xml");
    Document<Feed> doc = parse(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertEquals(feed.getBaseUri(), new IRI("http://www.snellspace.com/public/xmlbase.xml"));
    assertEquals(feed.getLogoElement().getResolvedValue(), new IRI("http://www.snellspace.com/public/atom-logo.png"));
    assertEquals(feed.getIconElement().getResolvedValue(),new IRI("http://www.snellspace.com/public/atom-icon.png"));
    
    Entry entry = feed.getEntries().get(0);
    assertEquals(entry.getAlternateLinkResolvedHref().toString(), "http://www.snellspace.com/wp");
  }
  
  
  /**
   * This tests the parsers ability to properly ignore the ordering of elements
   * in the Atom feed/entry.  The parser should be able to properly select the
   * requested elements regardless of the order in which they appear
   */
  public static void testOrder() throws Exception {
    //http://www.snellspace.com/public/ordertest.xml
    IRI uri = new IRI("http://www.snellspace.com/public/ordertest.xml");
    Document<Feed> doc = parse(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    List<Entry> entries = feed.getEntries();
    int n = 1;
    for (Entry entry : entries ) {
      switch(n) {
        case 1:
          assertEquals(entry.getIdElement().getValue(), new IRI("tag:example.org,2006:atom/conformance/element_order/1"));
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          assertEquals(entry.getSummaryType(), Text.Type.TEXT);
          assertNotNull(entry.getUpdatedElement().getValue());
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          break;
        case 2:
          assertEquals(entry.getIdElement().getValue(), new IRI("tag:example.org,2006:atom/conformance/element_order/2"));
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          assertEquals(entry.getSummaryType(), Text.Type.TEXT);
          assertNotNull(entry.getUpdatedElement().getValue());
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          break;
        case 3:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),2);
          assertEquals(
            entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
            new IRI("http://www.snellspace.com/public/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(1).getHref(), 
              new IRI("http://www.snellspace.com/public/alternate2"));
          break;
        case 4:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/alternate"));
          break;
        case 5:
          Text title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          String value = title.getValue();
          assertEquals(value, "Entry with a source first");
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/alternate"));          
          break;
        case 6:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Entry with a source last");
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/alternate"));
          break;
        case 7:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Entry with a source in the middle");
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/alternate"));          
          break;
        case 8:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Atom elements in an extension element");
          assertEquals(
            entry.getIdElement().getValue(), 
            new IRI("tag:example.org,2006:atom/conformance/element_order/8"));
          break;
        case 9:
          title = entry.getTitleElement();
          assertEquals(entry.getTitleType(), Text.Type.TEXT);
          value = title.getValue();
          assertEquals(value, "Atom elements in an extension element");
          assertEquals(
            entry.getIdElement().getValue(), 
            new IRI("tag:example.org,2006:atom/conformance/element_order/9"));
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
    IRI uri = new IRI("http://www.snellspace.com/public/linktests.xml");
    Document<Feed> doc = parse(uri);
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
            new IRI("http://www.snellspace.com/public/linktests/alternate"));
          break;
        case 2:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),4);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(1).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/alternate"));          
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(2).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/alternate2"));
          break;
        case 3:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(entry.getLinks(Link.REL_ENCLOSURE).size(),1);
          assertEquals(entry.getLinks(Link.REL_RELATED).size(),1);
          assertEquals(entry.getLinks(Link.REL_SELF).size(),1);
          assertEquals(entry.getLinks(Link.REL_VIA).size(),1);    
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_ENCLOSURE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/enclosure"));
          assertEquals(
              entry.getLinks(Link.REL_RELATED).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/related"));
          assertEquals(
              entry.getLinks(Link.REL_SELF).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/self"));
          assertEquals(
              entry.getLinks(Link.REL_VIA).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/via"));
          break;
        case 4:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),2);
          assertEquals(entry.getLinks(Link.REL_ENCLOSURE).size(),1);
          assertEquals(entry.getLinks(Link.REL_RELATED).size(),1);
          assertEquals(entry.getLinks(Link.REL_SELF).size(),1);
          assertEquals(entry.getLinks(Link.REL_VIA).size(),1);          
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(1).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/alternate2"));
          assertEquals(
              entry.getLinks(Link.REL_ENCLOSURE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/enclosure"));
          assertEquals(
              entry.getLinks(Link.REL_RELATED).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/related"));
          assertEquals(
              entry.getLinks(Link.REL_SELF).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/self"));
          assertEquals(
              entry.getLinks(Link.REL_VIA).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/via"));
          break;
        case 5:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(entry.getLinks(Link.REL_LICENSE).size(),1);  
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks(Link.REL_LICENSE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/license"));
          break;
        case 6:
          assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),1);
          assertEquals(entry.getLinks("http://example.org").size(),1);
          assertEquals(
              entry.getLinks(Link.REL_ALTERNATE).get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/alternate"));
          assertEquals(
              entry.getLinks("http://example.org").get(0).getHref(), 
              new IRI("http://www.snellspace.com/public/linktests/example"));
          break;
      }
      n++;
    }
  }
}
