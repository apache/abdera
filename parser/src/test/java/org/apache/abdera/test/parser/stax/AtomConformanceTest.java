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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;
import org.apache.axiom.om.OMElement;
import org.junit.Test;

public class AtomConformanceTest extends BaseParserTestCase {

    /**
     * Test to make sure that the parser properly detects the various kinds of extended content types allowed by Atom
     */
    @Test
    public void testContentTypes() throws Exception {
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
    @Test
    public void testXmlNamespace() throws Exception {
        String[] tests =
            {"http://www.snellspace.com/public/nondefaultnamespace.xml",
             "http://www.snellspace.com/public/nondefaultnamespace2.xml",
             "http://www.snellspace.com/public/nondefaultnamespace3.xml"};
        int n = 1;
        for (String test : tests) {
            IRI uri = new IRI(test);
            Document<Feed> doc = parse(uri);
            assertNotNull(doc);
            Feed feed = doc.getRoot();
            Entry entry = feed.getEntries().get(0);
            switch (n) {
                case 1:
                    assertNotNull(entry.getTitleElement());
                    assertEquals(new IRI("tag:example.org,2007:bar"), entry.getIdElement().getValue());
                    Text summary = entry.getSummaryElement();
                    assertNotNull(summary);
                    assertEquals(Text.Type.XHTML, summary.getTextType());
                    OMElement element = (OMElement)summary;
                    OMElement div = element.getFirstChildWithName(new QName("http://www.w3.org/1999/xhtml", "div"));
                    assertNotNull(div);
                    break;
                case 2:
                    assertNotNull(entry.getTitleElement());
                    assertEquals(new IRI("tag:example.org,2007:bar"), entry.getIdElement().getValue());
                    summary = entry.getSummaryElement();
                    assertNotNull(summary);
                    assertEquals(Text.Type.XHTML, summary.getTextType());
                    element = (OMElement)summary;
                    div = element.getFirstChildWithName(new QName("http://www.w3.org/1999/xhtml", "div"));
                    assertNotNull(div);
                    break;
                case 3:
                    assertNotNull(entry.getTitleElement());
                    assertEquals(new IRI("tag:example.org,2007:bar"), entry.getIdElement().getValue());
                    summary = entry.getSummaryElement();
                    assertNotNull(summary);
                    assertEquals(Text.Type.XHTML, summary.getTextType());
                    element = (OMElement)summary;
                    div = element.getFirstChildWithName(new QName("http://www.w3.org/1999/xhtml", "div"));
                    assertNotNull(div);
                    break;
            }
            n++;
        }
    }

    /**
     * Test to ensure that the parser properly resolves relative URI
     */
    @Test
    public void testXmlBase() throws Exception {
        IRI uri = new IRI("http://www.snellspace.com/public/xmlbase.xml");
        Document<Feed> doc = parse(uri);
        assertNotNull(doc);
        Feed feed = doc.getRoot();
        assertEquals(new IRI("http://www.snellspace.com/public/xmlbase.xml"), feed.getBaseUri());
        assertEquals(new IRI("http://www.snellspace.com/public/atom-logo.png"), feed.getLogoElement()
            .getResolvedValue());
        assertEquals(new IRI("http://www.snellspace.com/public/atom-icon.png"), feed.getIconElement()
            .getResolvedValue());

        Entry entry = feed.getEntries().get(0);
        assertEquals("http://www.snellspace.com/wp", entry.getAlternateLinkResolvedHref().toString());
    }

    /**
     * This tests the parsers ability to properly ignore the ordering of elements in the Atom feed/entry. The parser
     * should be able to properly select the requested elements regardless of the order in which they appear
     */
    @Test
    public void testOrder() throws Exception {
        // http://www.snellspace.com/public/ordertest.xml
        IRI uri = new IRI("http://www.snellspace.com/public/ordertest.xml");
        Document<Feed> doc = parse(uri);
        assertNotNull(doc);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        int n = 1;
        for (Entry entry : entries) {
            switch (n) {
                case 1:
                    assertEquals(new IRI("tag:example.org,2006:atom/conformance/element_order/1"), entry.getIdElement()
                        .getValue());
                    assertEquals(Text.Type.TEXT, entry.getTitleType());
                    assertEquals(Text.Type.TEXT, entry.getSummaryType());
                    assertNotNull(entry.getUpdatedElement().getValue());
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    break;
                case 2:
                    assertEquals(new IRI("tag:example.org,2006:atom/conformance/element_order/2"), entry.getIdElement()
                        .getValue());
                    assertEquals(Text.Type.TEXT, entry.getTitleType());
                    assertEquals(Text.Type.TEXT, entry.getSummaryType());
                    assertNotNull(entry.getUpdatedElement().getValue());
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    break;
                case 3:
                    assertEquals(2, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/alternate2"), entry
                        .getLinks(Link.REL_ALTERNATE).get(1).getHref());
                    break;
                case 4:
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    break;
                case 5:
                    Text title = entry.getTitleElement();
                    assertEquals(Text.Type.TEXT, entry.getTitleType());
                    String value = title.getValue();
                    assertEquals("Entry with a source first", value);
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    break;
                case 6:
                    title = entry.getTitleElement();
                    assertEquals(Text.Type.TEXT, entry.getTitleType());
                    value = title.getValue();
                    assertEquals("Entry with a source last", value);
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    break;
                case 7:
                    title = entry.getTitleElement();
                    assertEquals(Text.Type.TEXT, entry.getTitleType());
                    value = title.getValue();
                    assertEquals("Entry with a source in the middle", value);
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    break;
                case 8:
                    title = entry.getTitleElement();
                    assertEquals(Text.Type.TEXT, entry.getTitleType());
                    value = title.getValue();
                    assertEquals("Atom elements in an extension element", value);
                    assertEquals(new IRI("tag:example.org,2006:atom/conformance/element_order/8"), entry.getIdElement()
                        .getValue());
                    break;
                case 9:
                    title = entry.getTitleElement();
                    assertEquals(Text.Type.TEXT, entry.getTitleType());
                    value = title.getValue();
                    assertEquals("Atom elements in an extension element", value);
                    assertEquals(new IRI("tag:example.org,2006:atom/conformance/element_order/9"), entry.getIdElement()
                        .getValue());
                    break;
            }
            n++;
        }
    }

    /**
     * Tests the parsers support for the various link relation types
     */
    @Test
    public void testLink() throws Exception {
        // http://www.snellspace.com/public/linktests.xml
        IRI uri = new IRI("http://www.snellspace.com/public/linktests.xml");
        Document<Feed> doc = parse(uri);
        assertNotNull(doc);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        int n = 1;
        for (Entry entry : entries) {
            switch (n) {
                case 1:
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    break;
                case 2:
                    assertEquals(4, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(1).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate2"), entry
                        .getLinks(Link.REL_ALTERNATE).get(2).getHref());
                    break;
                case 3:
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(1, entry.getLinks(Link.REL_ENCLOSURE).size());
                    assertEquals(1, entry.getLinks(Link.REL_RELATED).size());
                    assertEquals(1, entry.getLinks(Link.REL_SELF).size());
                    assertEquals(1, entry.getLinks(Link.REL_VIA).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/enclosure"), entry
                        .getLinks(Link.REL_ENCLOSURE).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/related"), entry
                        .getLinks(Link.REL_RELATED).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/self"), entry
                        .getLinks(Link.REL_SELF).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/via"), entry
                        .getLinks(Link.REL_VIA).get(0).getHref());
                    break;
                case 4:
                    assertEquals(2, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(1, entry.getLinks(Link.REL_ENCLOSURE).size());
                    assertEquals(1, entry.getLinks(Link.REL_RELATED).size());
                    assertEquals(1, entry.getLinks(Link.REL_SELF).size());
                    assertEquals(1, entry.getLinks(Link.REL_VIA).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate2"), entry
                        .getLinks(Link.REL_ALTERNATE).get(1).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/enclosure"), entry
                        .getLinks(Link.REL_ENCLOSURE).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/related"), entry
                        .getLinks(Link.REL_RELATED).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/self"), entry
                        .getLinks(Link.REL_SELF).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/via"), entry
                        .getLinks(Link.REL_VIA).get(0).getHref());
                    break;
                case 5:
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(1, entry.getLinks(Link.REL_LICENSE).size());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/license"), entry
                        .getLinks(Link.REL_LICENSE).get(0).getHref());
                    break;
                case 6:
                    assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
                    assertEquals(1, entry.getLinks("http://example.org").size());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/alternate"), entry
                        .getLinks(Link.REL_ALTERNATE).get(0).getHref());
                    assertEquals(new IRI("http://www.snellspace.com/public/linktests/example"), entry
                        .getLinks("http://example.org").get(0).getHref());
                    break;
            }
            n++;
        }
    }
}
