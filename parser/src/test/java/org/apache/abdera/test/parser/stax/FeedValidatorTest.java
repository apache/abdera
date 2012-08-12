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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.MimeTypeParseException;
import org.apache.axiom.om.OMException;
import org.junit.BeforeClass;
import org.junit.Test;

//@Ignore("ABDERA-256")
public class FeedValidatorTest extends BaseParserTestCase {

    private static IRI baseURI = null;

    @BeforeClass
    public static void setUp() throws Exception {
        baseURI = new IRI("http://feedvalidator.org/testcases/atom/");
    }

    @Test
    public void testSection11BriefNoError() throws Exception {

        // http://feedvalidator.org/testcases/atom/1.1/brief-noerror.xml
        IRI uri = baseURI.resolve("1.1/brief-noerror.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Text title = feed.getTitleElement();
        assertNotNull(title);
        assertEquals(Text.Type.TEXT, title.getTextType());
        String value = title.getValue();
        assertNotNull(value);
        assertEquals("Example Feed", value);
        List<Link> links = feed.getLinks();
        assertEquals(1, links.size());
        for (Link link : links) {
            assertNull(link.getRel()); // it's an alternate link
            assertEquals(new IRI("http://example.org/"), link.getHref());
            assertNull(link.getHrefLang());
            assertNull(link.getMimeType());
            assertNull(link.getTitle());
            assertEquals(-1, link.getLength());
        }
        links = feed.getLinks(Link.REL_ALTERNATE);
        assertEquals(1, links.size());
        links = feed.getLinks(Link.REL_RELATED);
        assertEquals(0, links.size());
        assertNotNull(feed.getUpdatedElement());
        DateTime dte = feed.getUpdatedElement();
        AtomDate dt = dte.getValue();
        assertNotNull(dt);
        Calendar c = dt.getCalendar();
        AtomDate cdt = new AtomDate(c);
        assertEquals(dt.getTime(), cdt.getTime());
        Person person = feed.getAuthor();
        assertNotNull(person);
        assertEquals("John Doe", person.getName());
        assertNull(person.getEmail());
        assertNull(person.getUri());
        IRIElement id = feed.getIdElement();
        assertNotNull(id);
        assertEquals(new IRI("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"), id.getValue());
        List<Entry> entries = feed.getEntries();
        assertEquals(1, entries.size());
        for (Entry entry : entries) {
            title = entry.getTitleElement();
            assertNotNull(title);
            assertEquals(Text.Type.TEXT, title.getTextType());
            value = title.getValue();
            assertEquals("Atom-Powered Robots Run Amok", value);
            links = entry.getLinks();
            assertEquals(1, links.size());
            for (Link link : links) {
                assertNull(link.getRel()); // it's an alternate link
                assertEquals(new IRI("http://example.org/2003/12/13/atom03"), link.getHref());
                assertNull(link.getHrefLang());
                assertNull(link.getMimeType());
                assertNull(link.getTitle());
                assertEquals(-1, link.getLength());
            }
            links = entry.getLinks(Link.REL_ALTERNATE);
            assertEquals(1, links.size());
            links = entry.getLinks(Link.REL_RELATED);
            assertEquals(0, links.size());
            id = entry.getIdElement();
            assertNotNull(id);
            assertEquals(new IRI("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"), id.getValue());
            assertNotNull(entry.getUpdatedElement());
            dte = entry.getUpdatedElement();
            dt = dte.getValue();
            assertNotNull(dt);
            c = dt.getCalendar();
            cdt = new AtomDate(c);
            assertEquals(cdt.getTime(), dt.getTime());
            Text summary = entry.getSummaryElement();
            assertNotNull(summary);
            assertEquals(Text.Type.TEXT, summary.getTextType());
            value = summary.getValue();
            assertEquals("Some text.", value);
        }
    }

    @Test
    public void testSection11ExtensiveNoError() throws Exception {

        // http://feedvalidator.org/testcases/atom/1.1/extensive-noerror.xml
        IRI uri = baseURI.resolve("1.1/extensive-noerror.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNotNull(feed.getTitleElement());
        assertEquals(Text.Type.TEXT, feed.getTitleElement().getTextType());
        assertEquals("dive into mark", feed.getTitleElement().getValue());
        assertNotNull(feed.getSubtitleElement());
        assertEquals(Text.Type.TEXT, feed.getTitleElement().getTextType());
        assertNotNull(feed.getSubtitleElement().getValue());
        assertNotNull(feed.getUpdatedElement());
        assertNotNull(feed.getUpdatedElement().getValue());
        assertNotNull(feed.getUpdatedElement().getValue().getDate());
        assertNotNull(feed.getIdElement());
        assertTrue(feed.getIdElement() instanceof IRIElement);
        assertEquals(new IRI("tag:example.org,2003:3"), feed.getIdElement().getValue());
        List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
        assertEquals(1, links.size());
        for (Link link : links) {
            assertEquals("alternate", link.getRel());
            assertEquals("text/html", link.getMimeType().toString());
            assertEquals("en", link.getHrefLang());
            assertEquals(new IRI("http://example.org/"), link.getHref());
            assertNull(link.getTitle());
            assertEquals(-1, link.getLength());
        }
        links = feed.getLinks(Link.REL_SELF);
        assertEquals(1, links.size());
        for (Link link : links) {
            assertEquals("self", link.getRel());
            assertEquals("application/atom+xml", link.getMimeType().toString());
            assertEquals(new IRI("http://example.org/feed.atom"), link.getHref());
            assertNull(link.getHrefLang());
            assertNull(link.getTitle());
            assertEquals(-1, link.getLength());
        }
        assertNotNull(feed.getRightsElement());
        assertEquals(Text.Type.TEXT, feed.getRightsElement().getTextType());
        assertEquals("Copyright (c) 2003, Mark Pilgrim", feed.getRightsElement().getValue());
        assertNotNull(feed.getGenerator());
        Generator generator = feed.getGenerator();
        assertEquals(new IRI("http://www.example.com/"), generator.getUri());
        assertEquals("1.0", generator.getVersion());
        assertNotNull(generator.getText());
        assertEquals("Example Toolkit", generator.getText().trim());
        List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        assertEquals(1, entries.size());
        for (Entry entry : entries) {
            assertNotNull(entry.getTitleElement());
            assertEquals(Text.Type.TEXT, entry.getTitleElement().getTextType());
            assertEquals("Atom draft-07 snapshot", entry.getTitleElement().getValue());
            links = entry.getLinks(Link.REL_ALTERNATE);
            assertEquals(1, links.size());
            for (Link link : links) {
                assertEquals("alternate", link.getRel());
                assertEquals("text/html", link.getMimeType().toString());
                assertEquals(new IRI("http://example.org/2005/04/02/atom"), link.getHref());
                assertNull(link.getHrefLang());
                assertNull(link.getTitle());
                assertEquals(-1, link.getLength());
            }
            links = entry.getLinks(Link.REL_ENCLOSURE);
            assertEquals(1, links.size());
            for (Link link : links) {
                assertEquals("enclosure", link.getRel());
                assertEquals("audio/mpeg", link.getMimeType().toString());
                assertEquals(new IRI("http://example.org/audio/ph34r_my_podcast.mp3"), link.getHref());
                assertEquals(1337, link.getLength());
                assertNull(link.getHrefLang());
                assertNull(link.getTitle());
            }
            assertNotNull(entry.getIdElement());
            assertEquals(new IRI("tag:example.org,2003:3.2397"), entry.getIdElement().getValue());
            assertNotNull(entry.getUpdatedElement());
            assertNotNull(entry.getPublishedElement());
            Person person = entry.getAuthor();
            assertNotNull(person);
            assertEquals("Mark Pilgrim", person.getName());
            assertEquals("f8dy@example.com", person.getEmail());
            assertNotNull(person.getUriElement());
            assertEquals(new IRI("http://example.org/"), person.getUriElement().getValue());
            List<Person> contributors = entry.getContributors();
            assertNotNull(contributors);
            assertEquals(2, contributors.size());
            assertNotNull(entry.getContentElement());
            assertEquals(Content.Type.XHTML, entry.getContentElement().getContentType());
            assertEquals("en", entry.getContentElement().getLanguage());
            assertEquals(new IRI("http://diveintomark.org/"), entry.getContentElement().getBaseUri());
        }
    }

    @Test
    public void testSection12MissingNamespace() throws Exception {
        // http://feedvalidator.org/testcases/atom/1.2/missing-namespace.xml
        IRI uri = baseURI.resolve("1.2/missing-namespace.xml");
        Document<?> doc = parse(uri);
        assertFalse(doc.getRoot() instanceof Feed);
    }

    @Test
    public void testSection12PrefixedNamespace() throws Exception {
        // http://feedvalidator.org/testcases/atom/1.2/prefixed-namespace.xml
        IRI uri = baseURI.resolve("1.2/prefixed-namespace.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assert (feed.getQName().getPrefix().equals("atom"));
    }

    @Test
    public void testSection12WrongNamespaceCase() throws Exception {
        // http://feedvalidator.org/testcases/atom/1.2/wrong-namespace-case.xml
        IRI uri = baseURI.resolve("1.2/wrong-namespace-case.xml");
        Document<?> doc = parse(uri);
        assertFalse(doc.getRoot() instanceof Feed);
    }

    @Test
    public void testSection12WrongNamespace() throws Exception {
        // http://feedvalidator.org/testcases/atom/1.2/wrong-namespace.xml
        IRI uri = baseURI.resolve("1.2/wrong-namespace.xml");
        Document<?> doc = parse(uri);
        assertFalse(doc.getRoot() instanceof Feed);
    }

    @Test
    public void testSection2BriefEntry() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/brief-entry-noerror.xml
        IRI uri = baseURI.resolve("2/brief-entry-noerror.xml");
        Document<Entry> doc = parse(uri);
        Entry entry = doc.getRoot();
        assertNotNull(entry);
        assertNotNull(entry.getTitleElement());
        assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
        assertNotNull(entry.getIdElement());
        assertNotNull(entry.getIdElement().getValue());
        assertNotNull(entry.getUpdatedElement());
        assertNotNull(entry.getUpdatedElement().getValue());
        assertNotNull(entry.getUpdatedElement().getValue().getDate());
        assertNotNull(entry.getSummaryElement());
        assertEquals(Text.Type.TEXT, entry.getSummaryElement().getTextType());
        assertNotNull(entry.getAuthor());
        assertEquals("John Doe", entry.getAuthor().getName());
    }

    @Test
    public void testSection2InfosetAttrOrder() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/infoset-attr-order.xml
        IRI uri = baseURI.resolve("2/infoset-attr-order.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
        assertEquals(2, links.size());
        for (Link link : links) {
            assertEquals("alternate", link.getRel());
            assertNotNull(link.getHref());
        }
    }

    @Test
    public void testSection2InfosetCDATA() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/infoset-cdata.xml
        IRI uri = baseURI.resolve("2/infoset-cdata.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text summary = entry.getSummaryElement();
            assertNotNull(summary);
            assertEquals(Text.Type.TEXT, summary.getTextType());
            String value = summary.getValue();
            assertEquals("Some <b>bold</b> text.", value);
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSection2InfosetCharRef() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/infoset-char-ref.xml
        IRI uri = baseURI.resolve("2/infoset-char-ref.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            DateTime updated = entry.getUpdatedElement();
            assertNotNull(updated);
            assertNotNull(updated.getValue());
            assertNotNull(updated.getValue().getDate());
            assertEquals(103, updated.getValue().getDate().getYear());
        }
    }

    @Test
    public void testSection2InfosetElementWhitespace() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/infoset-element-whitespace.xml
        IRI uri = baseURI.resolve("2/infoset-element-whitespace.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Link link = feed.getAlternateLink();
        assertEquals(new IRI("http://example.org/"), link.getResolvedHref());
        // the feed has a second alternate link that we will ignore
    }

    @Test
    public void testSection2InfosetEmpty1() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/infoset-empty1.xml
        IRI uri = baseURI.resolve("2/infoset-empty1.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Entry entry = feed.getEntries().get(0);
        assertEquals("", entry.getTitle());
    }

    @Test
    public void testSection2InfosetEmpty2() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/infoset-empty2.xml
        IRI uri = baseURI.resolve("2/infoset-empty2.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Entry entry = feed.getEntries().get(0);
        assertEquals("", entry.getTitle());
    }

    @Test
    public void testSection2InfosetSingleQuote() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/infoset-quote-single.xml
        IRI uri = baseURI.resolve("2/infoset-quote-single.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org/"), doc.getRoot().getAlternateLink().getResolvedHref());
    }

    @Test
    public void testSection2InvalidXmlBase() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/invalid-xml-base.xml
        IRI uri = baseURI.resolve("2/invalid-xml-base.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        try {
            feed.getBaseUri();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection2InvalidXmlLang() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/invalid-xml-lang.xml
        IRI uri = baseURI.resolve("2/invalid-xml-lang.xml");
        Document<Feed> doc = parse(uri);
        assertFalse(java.util.Locale.US.equals(doc.getRoot().getLocale()));
    }

    @Test
    public void testSection2Iri() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/iri.xml
        IRI uri = baseURI.resolve("2/iri.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNotNull(feed.getIdElement().getValue());
        assertNotNull(feed.getAuthor().getUriElement().getValue());
        assertNotNull(feed.getAuthor().getUriElement().getValue().toASCIIString());
    }

    @Test
    public void testSection2XmlBaseAmbiguous() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/xml-base-ambiguous.xml
        IRI uri = baseURI.resolve("2/xml-base-ambiguous.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org/"), doc.getRoot().getAlternateLink().getResolvedHref());
    }

    @Test
    public void testSection2XmlBaseElemEqDoc() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/xml-base-elem-eq-doc.xml
        IRI uri = baseURI.resolve("2/xml-base-elem-eq-doc.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(new IRI("http://www.feedvalidator.org/2003/12/13/atom03"), entry.getAlternateLink()
            .getResolvedHref());
    }

    @Test
    public void testSection2XmlBaseElemNeDoc() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/xml-base-elem-ne-doc.xml
        IRI uri = baseURI.resolve("2/xml-base-elem-ne-doc.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://www.feedvalidator.org/testcases/atom/2/xml-base-elem-ne-doc.xml"), doc.getRoot()
            .getSelfLink().getResolvedHref());
    }

    @Test
    public void xtestSection2XmlBase() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/xml-base.xml
        IRI uri = baseURI.resolve("2/xml-base.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Link> links = feed.getLinks();
        for (Link link : links) {
            assertEquals(new IRI("http://example.org/index.html"), link.getResolvedHref());
        }
    }

    @Test
    public void testSection2XmlLangBlank() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/xml-lang-blank.xml
        IRI uri = baseURI.resolve("2/xml-lang-blank.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getLocale());
    }

    @Test
    public void testSection2XmlLang() throws Exception {
        // http://feedvalidator.org/testcases/atom/2/xml-lang.xml
        IRI uri = baseURI.resolve("2/xml-lang.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertEquals("en-us", feed.getLanguage());
        assertTrue(feed.getLocale().equals(java.util.Locale.US));
    }

    @Test
    public void testSection3WsAuthorUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-author-uri.xml
        IRI uri = baseURI.resolve("3/ws-author-uri.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Person author = feed.getAuthor();
        try {
            author.getUriElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection3WsCategoryScheme() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-category-scheme.xml
        IRI uri = baseURI.resolve("3/ws-category-scheme.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        for (Entry entry : entries) {
            List<Category> cats = entry.getCategories();
            for (Category cat : cats) {
                try {
                    cat.getScheme();
                } catch (Exception e) {
                    assertTrue(e instanceof IRISyntaxException);
                }
            }
        }
    }

    @Test
    public void testSection3WsContentSrc() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-content-src.xml
        IRI uri = baseURI.resolve("3/ws-content-src.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        for (Entry entry : entries) {
            Content content = entry.getContentElement();
            assertNotNull(content);
            try {
                content.getSrc();
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection3WsEntryId() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-entry-id.xml
        IRI uri = baseURI.resolve("3/ws-entry-id.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        for (Entry entry : entries) {
            IRIElement id = entry.getIdElement();
            assertNotNull(id);
            try {
                id.getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection3WsEntryPublished() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-entry-published.xml
        IRI uri = baseURI.resolve("3/ws-entry-published.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException); // bad date
            }
        }
    }

    @Test
    public void testSection3WsEntryUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-entry-updated.xml
        IRI uri = baseURI.resolve("3/ws-entry-updated.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        for (Entry entry : entries) {
            try {
                entry.getUpdatedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException); // bad date
            }
        }
    }

    @Test
    public void testSection3WsFeedIcon() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-feed-icon.xml
        IRI uri = baseURI.resolve("3/ws-feed-icon.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getIconElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection3WsFeedId() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-feed-id.xml
        IRI uri = baseURI.resolve("3/ws-feed-id.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getIdElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection3WsFeedLogo() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-feed-logo.xml
        IRI uri = baseURI.resolve("3/ws-feed-logo.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getLogoElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection3WsFeedUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-feed-updated.xml
        IRI uri = baseURI.resolve("3/ws-feed-updated.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getUpdatedElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testSection3WsGeneratorUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-generator-uri.xml
        IRI uri = baseURI.resolve("3/ws-generator-uri.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Generator gen = feed.getGenerator();
        assertNotNull(gen);
        try {
            gen.getUri();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection3WsLinkHref() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-link-href.xml
        IRI uri = baseURI.resolve("3/ws-link-href.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Link> links = feed.getLinks();
        for (Link link : links) {
            try {
                link.getHref();
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection3WsLinkRel() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-link-rel.xml
        IRI uri = baseURI.resolve("3/ws-link-rel.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getAlternateLink());
    }

    @Test
    public void testSection3WsXmlBase() throws Exception {
        // http://feedvalidator.org/testcases/atom/3/ws-xml-base.xml
        IRI uri = baseURI.resolve("3/ws-xml-base.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getBaseUri();
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection311SummaryTypeMime() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1/summary_type_mime.xml
        IRI uri = baseURI.resolve("3.1.1/summary_type_mime.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getEntries();
        } catch (Exception e) {
            assertTrue(e instanceof OMException);
        }
    }

    @Test
    public void testSection3111EscapedText() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.1/escaped_text.xml
        IRI uri = baseURI.resolve("3.1.1.1/escaped_text.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text text = entry.getSummaryElement();
            assertNotNull(text);
            assertEquals(Text.Type.TEXT, text.getTextType());
            String value = text.getValue();
            assertEquals("Some&nbsp;escaped&nbsp;html", value);
        }
    }

    @Test
    public void testSection3111ExampleTextTitle() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.1/example_text_title.xml
        IRI uri = baseURI.resolve("3.1.1.1/example_text_title.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text title = entry.getTitleElement();
            assertNotNull(title);
            assertEquals(Text.Type.TEXT, title.getTextType());
            String value = title.getValue();
            assertEquals("Less: <", value.trim());
        }
    }

    @Test
    public void testSection3111SummaryTypeMime() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.1/summary_type_mime.xml
        IRI uri = baseURI.resolve("3.1.1.1/summary_type_mime.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getEntries();
        } catch (Exception e) {
            assertTrue(e instanceof OMException);
        }
    }

    @Test
    public void testSection3112ExampleHtmlTitle() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.2/example_html_title.xml
        IRI uri = baseURI.resolve("3.1.1.2/example_html_title.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text title = entry.getTitleElement();
            assertEquals(Text.Type.HTML, title.getTextType());
            String value = title.getValue();
            assertEquals("Less: <em> &lt; </em>", value.trim());
        }
    }

    @Test
    public void testSection3112InvalidHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.2/invalid_html.xml
        IRI uri = baseURI.resolve("3.1.1.2/invalid_html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("<a", entry.getSummary().trim());
    }

    @Test
    public void testSection3112TextWithEscapedHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.2/text_with_escaped_html.xml
        IRI uri = baseURI.resolve("3.1.1.2/text_with_escaped_html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("So I was reading <a href=\"http://example.com/\">example.com</a> the other day, it's really interesting.",
                     entry.getSummary().trim());
    }

    @Test
    public void testSection3112ValidHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.2/valid_html.xml
        IRI uri = baseURI.resolve("3.1.1.2/valid_html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("<h3>Heading</h3>", entry.getSummary().trim());

    }

    @Test
    public void testSection3113ExampleXhtmlSummary1() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.3/example_xhtml_summary1.xml
        IRI uri = baseURI.resolve("3.1.1.3/example_xhtml_summary1.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text summary = entry.getSummaryElement();
            assertNotNull(summary);
            assertEquals(Text.Type.XHTML, summary.getTextType());
            Div div = summary.getValueElement();
            assertNotNull(div);
        }
    }

    @Test
    public void testSection3113ExampleXhtmlSummary2() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.3/example_xhtml_summary2.xml
        IRI uri = baseURI.resolve("3.1.1.3/example_xhtml_summary2.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text summary = entry.getSummaryElement();
            assertNotNull(summary);
            assertEquals(Text.Type.XHTML, summary.getTextType());
            Div div = summary.getValueElement();
            assertNotNull(div);
        }
    }

    @Test
    public void testSection3113ExampleXhtmlSummary3() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.3/example_xhtml_summary3.xml
        IRI uri = baseURI.resolve("3.1.1.3/example_xhtml_summary3.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text summary = entry.getSummaryElement();
            assertNotNull(summary);
            assertEquals(Text.Type.XHTML, summary.getTextType());
            Div div = summary.getValueElement();
            assertNotNull(div);
        }
    }

    @Test
    public void testSection3113MissingXhtmlDiv() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.3/missing_xhtml_div.xml
        IRI uri = baseURI.resolve("3.1.1.3/missing_xhtml_div.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Text summary = entry.getSummaryElement();
            assertNotNull(summary);
            assertEquals(Text.Type.XHTML, summary.getTextType());
            Div div = summary.getValueElement();
            assertNull(div);
        }
    }

    @Test
    public void testSection3113XhtmlNamedEntity() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.1.1.3/xhtml_named_entity.xml
        IRI uri = baseURI.resolve("3.1.1.3/xhtml_named_entity.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getEntries();
        } catch (Exception e) {
            assertTrue(e instanceof OMException);
        }
    }

    @Test
    public void testSection321ContainsEmail() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.1/contains-email.xml
        // Note: not validating input right now
    }

    @Test
    public void testSection321MultipleNames() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.1/multiple-names.xml
        IRI uri = baseURI.resolve("3.2.1/multiple-names.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("George Washington", doc.getRoot().getContributors().get(0).getName());
    }

    @Test
    public void testSection321NoName() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.1/no-name.xml
        IRI uri = baseURI.resolve("3.2.1/no-name.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getContributors().get(0).getName());
    }

    @Test
    public void testSection322InvalidUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.2/invalid-uri.xml
        IRI uri = baseURI.resolve("3.2.2/invalid-uri.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> contr = feed.getContributors();
        for (Person person : contr) {
            try {
                person.getUriElement();
            } catch (Exception e) {
                assertFalse(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection322MultipleUris() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.2/multiple-uris.xml
        IRI uri = baseURI.resolve("3.2.2/multiple-uris.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.com/~jane/"), doc.getRoot().getContributors().get(0).getUri());
    }

    @Test
    public void testSection322RelativeRef() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.2/relative-ref.xml
        IRI uri = baseURI.resolve("3.2.2/relative-ref.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> contr = feed.getContributors();
        for (Person person : contr) {
            assertEquals(new IRI("~jane/"), person.getUriElement().getValue());
            assertEquals(uri.resolve("~jane/"), person.getUriElement().getResolvedValue());
        }
    }

    @Test
    public void testSection323EmailRss20Style() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.3/email-rss20-style.xml
        IRI uri = baseURI.resolve("3.2.3/email-rss20-style.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> contr = feed.getContributors();
        for (Person person : contr) {
            try {
                new IRI(person.getEmail());
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection323EmailWithName() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.3/email-with-name.xml
        IRI uri = baseURI.resolve("3.2.3/email-with-name.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> contr = feed.getContributors();
        for (Person person : contr) {
            try {
                new IRI(person.getEmail());
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection323EmailWithPlus() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.3/email-with-plus.xml
        IRI uri = baseURI.resolve("3.2.3/email-with-plus.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> contr = feed.getContributors();
        for (Person person : contr) {
            new IRI(person.getEmail());
        }
    }

    @Test
    public void testSection323InvalidEmail() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.3/invalid-email.xml
        IRI uri = baseURI.resolve("3.2.3/invalid-email.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> contr = feed.getContributors();
        for (Person person : contr) {
            try {
                new IRI(person.getEmail());
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection323MultipleEmails() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.2.3/multiple-emails.xml
        IRI uri = baseURI.resolve("3.2.3/multiple-emails.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("jane@example.com", doc.getRoot().getContributors().get(0).getEmail());
    }

    @Test
    public void testSection33DuplicateUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/duplicate-updated.xml
        IRI uri = baseURI.resolve("3.3/duplicate-updated.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-12-13T18:30:02Z");
        for (Entry entry : doc.getRoot().getEntries()) {
            Date date = entry.getUpdated();
            assertEquals(d, date);
        }
    }

    @Test
    public void testSection33LowercaseUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/lowercase-updated.xml
        IRI uri = baseURI.resolve("3.3/lowercase-updated.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getUpdatedElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testSection33PublishedBadDay() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_bad_day.xml
        IRI uri = baseURI.resolve("3.3/published_bad_day.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-07-32T15:51:30-05:00");
        assertEquals(d, doc.getRoot().getEntries().get(0).getPublished());
    }

    @Test
    public void testSection33PublishedBadDay2() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_bad_day2.xml
        IRI uri = baseURI.resolve("3.3/published_bad_day2.xml");
        Document<Feed> doc = parse(uri);
        // this is an invalid date, but we don't care because we're not doing
        // validation. Better run those feeds through the feed validator :-)
        Date d = AtomDate.parse("2003-06-31T15:51:30-05:00");
        assertEquals(d, doc.getRoot().getEntries().get(0).getPublished());
    }

    @Test
    public void testSection33PublishedBadHours() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_bad_hours.xml
        IRI uri = baseURI.resolve("3.3/published_bad_hours.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-07-01T25:51:30-05:00");
        assertEquals(d, doc.getRoot().getEntries().get(0).getPublished());
    }

    @Test
    public void testSecton33PublishedBadMinutes() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_bad_minutes.xml
        IRI uri = baseURI.resolve("3.3/published_bad_minutes.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-07-01T01:61:30-05:00");
        assertEquals(d, doc.getRoot().getEntries().get(0).getPublished());
    }

    @Test
    public void testSection33PublishedBadMonth() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_bad_month.xml
        IRI uri = baseURI.resolve("3.3/published_bad_month.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-13-01T15:51:30-05:00");
        assertEquals(d, doc.getRoot().getEntries().get(0).getPublished());
    }

    @Test
    public void testSection33PublishedBadSeconds() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_bad_seconds.xml
        IRI uri = baseURI.resolve("3.3/published_bad_seconds.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-07-01T01:55:61-05:00");
        assertEquals(d, doc.getRoot().getEntries().get(0).getPublished());
    }

    @Test
    public void testSection33PublishedDateOnly() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_date_only.xml
        IRI uri = baseURI.resolve("3.3/published_date_only.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedExtraSpaces() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces.xml
        IRI uri = baseURI.resolve("3.3/published_extra_spaces.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedExtraSpaces2() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces2.xml
        IRI uri = baseURI.resolve("3.3/published_extra_spaces2.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedExtraSpaces3() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces3.xml
        IRI uri = baseURI.resolve("3.3/published_extra_spaces3.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedExtraSpaces4() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces4.xml
        IRI uri = baseURI.resolve("3.3/published_extra_spaces4.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedExtraSpaces5() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces5.xml
        IRI uri = baseURI.resolve("3.3/published_extra_spaces5.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedFractionalSecond() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_fractional_second.xml
        IRI uri = baseURI.resolve("3.3/published_fractional_second.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            entry.getPublishedElement().getValue();
        }
    }

    @Test
    public void testSection33PublishedHoursMinutes() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_hours_minutes.xml
        IRI uri = baseURI.resolve("3.3/published_hours_minutes.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedNoColons() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_no_colons.xml
        IRI uri = baseURI.resolve("3.3/published_no_colons.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedNoHyphens() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_no_hyphens.xml
        IRI uri = baseURI.resolve("3.3/published_no_hyphens.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedNoT() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_no_t.xml
        IRI uri = baseURI.resolve("3.3/published_no_t.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedNoTimezoneColon() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_no_timezone_colon.xml
        IRI uri = baseURI.resolve("3.3/published_no_timezone_colon.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedNoYear() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_no_year.xml
        IRI uri = baseURI.resolve("3.3/published_no_year.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedSeconds() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_seconds.xml
        IRI uri = baseURI.resolve("3.3/published_seconds.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            entry.getPublishedElement().getValue();
        }
    }

    @Test
    public void testSection33PublishedUtc() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_utc.xml
        IRI uri = baseURI.resolve("3.3/published_utc.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            entry.getPublishedElement().getValue();
        }
    }

    @Test
    public void testSection33PublishedWrongFormat() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_wrong_format.xml
        IRI uri = baseURI.resolve("3.3/published_wrong_format.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedYearAndMonth() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_year_and_month.xml
        IRI uri = baseURI.resolve("3.3/published_year_and_month.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33PublishedYearOnly() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/published_year_only.xml
        IRI uri = baseURI.resolve("3.3/published_year_only.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection33UpdatedExample2() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/updated-example2.xml
        IRI uri = baseURI.resolve("3.3/updated-example2.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            entry.getUpdatedElement().getValue();
        }
    }

    @Test
    public void testSection33UpdatedExample3() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/updated-example3.xml
        IRI uri = baseURI.resolve("3.3/updated-example3.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            entry.getUpdatedElement().getValue();
        }
    }

    @Test
    public void testSection33UpdatedExample4() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/updated-example4.xml
        IRI uri = baseURI.resolve("3.3/updated-example4.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            entry.getUpdatedElement().getValue();
        }
    }

    @Test
    public void testSection33UpdatedFuture() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/updated-future.xml
        IRI uri = baseURI.resolve("3.3/updated-future.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2103-12-13T18:30:02Z");
        assertEquals(d, doc.getRoot().getEntries().get(0).getUpdated());
    }

    @Test
    public void testSection33UpdatedPast() throws Exception {
        // http://feedvalidator.org/testcases/atom/3.3/updated-past.xml
        IRI uri = baseURI.resolve("3.3/updated-past.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("0103-12-13T18:30:02Z");
        assertEquals(d, doc.getRoot().getEntries().get(0).getUpdated());
    }

    @Test
    public void testSection411AuthorAtEntryOnly() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/author-at-entry-only.xml
        IRI uri = baseURI.resolve("4.1.1/author-at-entry-only.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getAuthor());
        }
    }

    @Test
    public void testSection411AuthorAtFeedAndEntry() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/author-at-feed-and-entry.xml
        IRI uri = baseURI.resolve("4.1.1/author-at-feed-and-entry.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNotNull(feed.getAuthor());
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getAuthor());
        }
    }

    @Test
    public void testSection411AuthorAtFeedOnly() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/author-at-feed-only.xml
        IRI uri = baseURI.resolve("4.1.1/author-at-feed-only.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNotNull(feed.getAuthor());
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNull(entry.getAuthor());
        }
    }

    @Test
    public void testSection411AuthorlessWithNoEntries() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/authorless-with-no-entries.xml
        IRI uri = baseURI.resolve("4.1.1/authorless-with-no-entries.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNull(feed.getAuthor());
    }

    @Test
    public void testSection411AuthorlessWithOneEntry() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/authorless-with-one-entry.xml
        IRI uri = baseURI.resolve("4.1.1/authorless-with-one-entry.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNull(feed.getAuthor());
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNull(entry.getAuthor());
        }
    }

    @Test
    public void testSection411DuplicateEntries() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/duplicate-entries.xml
        IRI uri = baseURI.resolve("4.1.1/duplicate-entries.xml");
        Document<Feed> doc = parse(uri);
        Entry e1 = doc.getRoot().getEntries().get(0);
        Entry e2 = doc.getRoot().getEntries().get(1);
        assertEquals(e1.getId(), e2.getId());
        assertEquals(e1.getUpdated(), e2.getUpdated());
    }

    @Test
    public void testSection411LinkRelFull() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/link-rel-full.xml
        IRI uri = baseURI.resolve("4.1.1/link-rel-full.xml");
        Document<Feed> doc = parse(uri);
        Link link = doc.getRoot().getLink("http://xmlns.com/foaf/0.1/");
        assertNotNull(link);
        assertEquals(new IRI("http://example.org/foaf"), link.getResolvedHref());
    }

    @Test
    public void testSection411MisplacedMetadata() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/misplaced-metadata.xml
        IRI uri = baseURI.resolve("4.1.1/misplaced-metadata.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"), doc.getRoot().getId());
    }

    @Test
    public void testSection411MissingId() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/missing-id.xml
        IRI uri = baseURI.resolve("4.1.1/missing-id.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getId());
    }

    @Test
    public void testSection411MissingSelf() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/missing-self.xml
        IRI uri = baseURI.resolve("4.1.1/missing-self.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getSelfLink());
    }

    @Test
    public void testSection411MissingTitles() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/missing-titles.xml
        IRI uri = baseURI.resolve("4.1.1/missing-titles.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getTitle());
    }

    @Test
    public void testSection411MissingUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/missing-updated.xml
        IRI uri = baseURI.resolve("4.1.1/missing-updated.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getUpdated());
    }

    @Test
    public void testSection411MultipleAlternatesDiffering() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-alternates-differing.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-alternates-differing.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
        assertEquals(2, links.size());
    }

    @Test
    public void testSection411MultipleAlternatesMatching() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-alternates-matching.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-alternates-matching.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org/front-page.html"), doc.getRoot().getAlternateLink().getResolvedHref());
    }

    @Test
    public void testSection411MultipleAuthors() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-authors.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-authors.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> authors = feed.getAuthors();
        assertEquals(2, authors.size());
    }

    @Test
    public void testSection411MultipleCategories() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-categories.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-categories.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Category> cats = feed.getCategories();
        assertEquals(2, cats.size());
    }

    @Test
    public void testSection411MultipleContributors() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-contributors.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-contributors.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Person> contr = feed.getContributors();
        assertEquals(2, contr.size());
    }

    @Test
    public void testSection411MultipleGenerators() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-generators.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-generators.xml");
        Document<Feed> doc = parse(uri);
        Generator g = doc.getRoot().getGenerator();
        assertEquals(new IRI("http://www.example.com/"), g.getResolvedUri());
        assertEquals("Example Toolkit", g.getText().trim());
    }

    @Test
    public void testSection411MultipleIcons() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-icons.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-icons.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://feedvalidator.org/big.icon"), doc.getRoot().getIcon());
    }

    @Test
    public void testSection411MultipleIds() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-ids.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-ids.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"), doc.getRoot().getId());
    }

    @Test
    public void testSection411MultipleLogos() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-logos.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-logos.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://feedvalidator.org/small.jpg"), doc.getRoot().getLogo());
    }

    @Test
    public void testSection411MultipleRelatedMatching() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-related-matching.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-related-matching.xml");
        Document<Feed> doc = parse(uri);
        List<Link> links = doc.getRoot().getLinks("related");
        assertEquals(2, links.size());
        assertEquals(new IRI("http://example.org/front-page.html"), links.get(0).getResolvedHref());
        assertEquals(new IRI("http://example.org/second-page.html"), links.get(1).getResolvedHref());
    }

    @Test
    public void testSection411MultipleRights() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-rights.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-rights.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("Public Domain", doc.getRoot().getRights());
    }

    @Test
    public void testSection411MultipleSubtitles() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-subtitles.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-subtitles.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("A unique feed, just like all the others", doc.getRoot().getSubtitle());
    }

    @Test
    public void testSection411MultipleTitles() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-titles.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-titles.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("Example Feed", doc.getRoot().getTitle());
    }

    @Test
    public void testSection411MultipleUpdateds() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/multiple-updateds.xml
        IRI uri = baseURI.resolve("4.1.1/multiple-updateds.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-12-13T18:30:02Z");
        assertEquals(d, doc.getRoot().getUpdated());
    }

    @Test
    public void testSection411ZeroEntries() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1/zero-entries.xml
        IRI uri = baseURI.resolve("4.1.1/zero-entries.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertEquals(0, feed.getEntries().size());
    }

    @Test
    public void testSection4111ContentSrc() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1.1/content-src.xml
        IRI uri = baseURI.resolve("4.1.1.1/content-src.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Content content = entry.getContentElement();
            assertNotNull(content.getSrc());
        }
    }

    @Test
    public void testSection4111EmptyContent() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1.1/empty-content.xml
        IRI uri = baseURI.resolve("4.1.1.1/empty-content.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("", doc.getRoot().getEntries().get(0).getContent());
    }

    @Test
    public void testSection4111EmptyTitle() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1.1/empty-title.xml
        IRI uri = baseURI.resolve("4.1.1.1/empty-title.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("", doc.getRoot().getEntries().get(0).getTitle());
    }

    @Test
    public void testSection4111NoContentOrSummary() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.1.1/no-content-or-summary.xml
        IRI uri = baseURI.resolve("4.1.1.1/no-content-or-summary.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getContent());
        assertNull(entry.getSummary());
    }

    @Test
    public void testSection412AlternateNoContent() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/alternate-no-content.xml
        IRI uri = baseURI.resolve("4.1.2/alternate-no-content.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
            assertNotNull(entry.getSummaryElement());
        }
    }

    @Test
    public void testSection412ContentBase64NoSummary() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/content-base64-no-summary.xml
        IRI uri = baseURI.resolve("4.1.2/content-base64-no-summary.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNull(entry.getSummaryElement());
            assertNotNull(entry.getContentElement());
            Content mediaContent = entry.getContentElement();
            DataHandler dataHandler = mediaContent.getDataHandler();
            InputStream in = (ByteArrayInputStream)dataHandler.getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n = -1;
            while ((n = in.read()) > -1) {
                baos.write(n);
            }
            assertEquals("Some more text.", baos.toString());
        }
    }

    @Test
    public void testSection412ContentNoAlternate() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/content-no-alternate.xml
        IRI uri = baseURI.resolve("4.1.2/content-no-alternate.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertEquals(0, entry.getLinks(Link.REL_ALTERNATE).size());
            assertNotNull(entry.getContentElement());
        }
    }

    @Test
    public void testSection412ContentSrcNoSummary() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/content-src-no-summary.xml
        IRI uri = baseURI.resolve("4.1.2/content-src-no-summary.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getSummary());
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), entry.getContentElement().getResolvedSrc());
    }

    @Test
    public void testSection412EntrySourceAuthor() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/entry-source-author.xml
        IRI uri = baseURI.resolve("4.1.2/entry-source-author.xml");
        Document<Entry> doc = parse(uri);
        Entry entry = doc.getRoot();
        assertNotNull(entry);
        assertNotNull(entry.getSource());
        assertNotNull(entry.getSource().getAuthor());
    }

    @Test
    public void testSection412LinkFullUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/link-full-uri.xml
        IRI uri = baseURI.resolve("4.1.2/link-full-uri.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Link> links = entry.getLinks("http://xmlns.com/foaf/0.1/");
            assertEquals(1, links.size());
        }
    }

    @Test
    public void testSection412LinkSameRelDifferentTypes() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-different-types.xml
        IRI uri = baseURI.resolve("4.1.2/link-same-rel-different-types.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Link> links = entry.getLinks(Link.REL_ALTERNATE);
            assertEquals(2, links.size());
        }
    }

    @Test
    public void testSection412LinkSameRelTypeDifferentHreflang() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-type-different-hreflang.xml
        IRI uri = baseURI.resolve("4.1.2/link-same-rel-type-different-hreflang.xml");
        Document<Feed> doc = parse(uri);
        List<Link> links = doc.getRoot().getEntries().get(0).getLinks("alternate");
        assertEquals(2, links.size());
        assertEquals("es-es", links.get(0).getHrefLang());
        assertEquals("en-us", links.get(1).getHrefLang());
    }

    @Test
    public void testSection412LinkSameRelTypeHreflang() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-type-hreflang.xml
        IRI uri = baseURI.resolve("4.1.2/link-same-rel-type-hreflang.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(new IRI("http://example.org/2003/12/13/atom02"), entry.getAlternateLink().getResolvedHref());
    }

    @Test
    public void testSection412LinkSameRelTypeNoHreflang() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-type-no-hreflang.xml
        IRI uri = baseURI.resolve("4.1.2/link-same-rel-type-no-hreflang.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(new IRI("http://example.org/2003/12/13/atom02"), entry.getAlternateLink().getResolvedHref());
    }

    @Test
    public void testSection412MissingId() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/missing-id.xml
        IRI uri = baseURI.resolve("4.1.2/missing-id.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getId());
    }

    @Test
    public void testSection412MissingTitle() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/missing-title.xml
        IRI uri = baseURI.resolve("4.1.2/missing-title.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getTitle());
    }

    @Test
    public void testSection412MissingUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/missing-updated.xml
        IRI uri = baseURI.resolve("4.1.2/missing-updated.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getUpdated());
    }

    @Test
    public void testSection412MultiEnclosureTest() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multi-enclosure-test.xml
        IRI uri = baseURI.resolve("4.1.2/multi-enclosure-test.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Link> enclosures = entry.getLinks(Link.REL_ENCLOSURE);
            assertEquals(2, enclosures.size());
        }
    }

    @Test
    public void testSection412MultipleCategories() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-categories.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-categories.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Category> cats = entry.getCategories();
            assertEquals(2, cats.size());
        }
    }

    @Test
    public void testSection412MultipleContents() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-contents.xml
        // Note: not implemented
        IRI uri = baseURI.resolve("4.1.2/multiple-contents.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("No big deal", entry.getContent());
    }

    @Test
    public void testSection412MultipleContributors() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-contributors.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-contributors.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Person> contr = entry.getContributors();
            assertEquals(2, contr.size());
        }
    }

    @Test
    public void testSection412MultipleIds() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-ids.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-ids.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"), doc.getRoot().getEntries().get(0)
            .getId());
    }

    @Test
    public void testSection412MultiplePublished() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-published.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-published.xml");
        Document<Feed> doc = parse(uri);
        Date d = AtomDate.parse("2003-12-11T11:13:56Z");
        assertEquals(d, doc.getRoot().getEntries().get(0).getPublished());
    }

    @Test
    public void testSection412MultipleRights() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-rights.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-rights.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Public Domain", entry.getRights());
    }

    @Test
    public void testSection412MultipleSources() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-sources.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-sources.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Source source = entry.getSource();
        assertEquals(new IRI("urn:uuid:9b056ae0-f778-11d9-8cd6-0800200c9a66"), source.getId());
    }

    @Test
    public void testSection412MultipleSummaries() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-summaries.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-summaries.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Some text.", entry.getSummary());
    }

    @Test
    public void testSection412MultipleTitles() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-titles.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-titles.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Atom-Powered Robots Run Amok", entry.getTitle());
    }

    @Test
    public void testSection412MultipleUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/multiple-updated.xml
        IRI uri = baseURI.resolve("4.1.2/multiple-updated.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Date d = AtomDate.parse("2003-12-13T18:30:02Z");
        assertEquals(d, entry.getUpdated());
    }

    @Test
    public void testSection412NoContentOrAlternate() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/no-content-or-alternate.xml
        IRI uri = baseURI.resolve("4.1.2/no-content-or-alternate.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getContent());
        assertNull(entry.getAlternateLink());
    }

    @Test
    public void testSection412RelatedSameRelTypeHreflang() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/related-same-rel-type-hreflang.xml
        IRI uri = baseURI.resolve("4.1.2/related-same-rel-type-hreflang.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        List<Link> links = entry.getLinks("related");
        assertEquals(2, links.size());
        assertEquals(new IRI("http://example.org/2003/12/13/atom02"), links.get(0).getResolvedHref());
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), links.get(1).getResolvedHref());
    }

    @Test
    public void testSection412SummaryContentBase64() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/summary-content-base64.xml
        IRI uri = baseURI.resolve("4.1.2/summary-content-base64.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getSummaryElement());
            assertEquals(Text.Type.TEXT, entry.getSummaryElement().getTextType());
            assertNotNull(entry.getContentElement());
            assertEquals(Content.Type.MEDIA, entry.getContentElement().getContentType());
        }
    }

    @Test
    public void testSection412SummaryContentSrc() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.2/summary-content-src.xml
        IRI uri = baseURI.resolve("4.1.2/summary-content-src.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getSummaryElement());
            assertEquals(Text.Type.TEXT, entry.getSummaryElement().getTextType());
            assertNotNull(entry.getContentElement());
            assertEquals(Content.Type.MEDIA, entry.getContentElement().getContentType());
            Content mediaContent = entry.getContentElement();
            assertNotNull(mediaContent.getSrc());
            assertEquals("application/pdf", mediaContent.getMimeType().toString());
        }
    }

    @Test
    public void testSection4131TypeHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.1/type-html.xml
        IRI uri = baseURI.resolve("4.1.3.1/type-html.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getContentElement());
            assertEquals(Content.Type.HTML, entry.getContentElement().getContentType());
        }
    }

    @Test
    public void testSection413TypeMultipartAlternative() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.1/type-multipart-alternative.xml
        IRI uri = baseURI.resolve("4.1.3.1/type-multipart-alternative.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("multipart/alternative", entry.getContentElement().getMimeType().toString());
    }

    @Test
    public void testSection4131TypeTextHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.1/type-text-html.xml
        IRI uri = baseURI.resolve("4.1.3.1/type-text-html.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getContentElement());
            assertEquals(Content.Type.MEDIA, entry.getContentElement().getContentType());
            Content mediaContent = entry.getContentElement();
            assertEquals("text/html", mediaContent.getMimeType().toString());
        }
    }

    @Test
    public void testSection4131TypeText() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.1/type-text.xml
        IRI uri = baseURI.resolve("4.1.3.1/type-text.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getContentElement());
            assertEquals(Content.Type.TEXT, entry.getContentElement().getContentType());
        }
    }

    @Test
    public void testSection4131TypeXhtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.1/type-xhtml.xml
        IRI uri = baseURI.resolve("4.1.3.1/type-xhtml.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertNotNull(entry.getContentElement());
            assertEquals(Content.Type.XHTML, entry.getContentElement().getContentType());
        }
    }

    @Test
    public void testSection413TypeXml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.1/type-xhtml.xml
        IRI uri = baseURI.resolve("4.1.3.1/type-xml.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        try {
            feed.getEntries();
        } catch (Exception e) {
            assertTrue(e instanceof OMException);
        }
    }

    @Test
    public void testSection4132ContentSrcExtraChild() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-extra-child.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-extra-child.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Content content = entry.getContentElement();
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), content.getResolvedSrc());
        assertEquals("extraneous text", entry.getContent().trim());
    }

    @Test
    public void testSection4132ContentSrcExtraText() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-extra-text.xml
        try {
            IRI uri = baseURI.resolve("4.1.3.2/content-src-extra-text.xml");
            Document<Feed> doc = parse(uri);
            Entry entry = doc.getRoot().getEntries().get(0);
            Content content = entry.getContentElement();
            assertEquals(new IRI("http://example.org/2003/12/13/atom03"), content.getResolvedSrc());
        } catch (Exception e) {
        }
    }

    @Test
    public void testSection4132ContentSrcInvalidIri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-invalid-iri.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-invalid-iri.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Content content = entry.getContentElement();
            assertNotNull(content);
            try {
                content.getSrc();
            } catch (Exception e) {
                assertTrue(e instanceof IRISyntaxException);
            }
        }
    }

    @Test
    public void testSection4132ContentSrcNoTypeNoError() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-no-type-no-error.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-no-type-no-error.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Content content = entry.getContentElement();
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), content.getResolvedSrc());
        assertNull(content.getMimeType());
    }

    @Test
    public void testSection4132ContentSrcNoType() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-no-type.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-no-type.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Content content = entry.getContentElement();
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), content.getResolvedSrc());
        assertNull(content.getMimeType());
    }

    @Test
    public void testSection4132ContentSrcRelativeRef() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-relative-ref.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-relative-ref.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Content content = entry.getContentElement();
            assertNotNull(content);
            assertEquals(Content.Type.MEDIA, content.getContentType());
            assertEquals(uri.resolve("2003/12/12/atom03.pdf"), content.getResolvedSrc());
        }
    }

    @Test
    public void testSection4132ContentSrcTypeHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-html.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-type-html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("", entry.getContent());
        assertEquals(Content.Type.HTML, entry.getContentType());
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), entry.getContentElement().getResolvedSrc());
    }

    @Test
    public void testSection4132ContentSrcTypeTextHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-text-html.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-type-text-html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("", entry.getContent());
        assertEquals(Content.Type.MEDIA, entry.getContentType());
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), entry.getContentElement().getResolvedSrc());
    }

    @Test
    public void testSection4132ContentSrcTypeText() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-text.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-type-text.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("", entry.getContent());
        assertEquals(Content.Type.TEXT, entry.getContentType());
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), entry.getContentElement().getResolvedSrc());
    }

    @Test
    public void testSection4132ContentSrcTypeXhtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-xhtml.xml
        IRI uri = baseURI.resolve("4.1.3.2/content-src-type-xhtml.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getContent());
        assertEquals(Content.Type.XHTML, entry.getContentType());
        assertEquals(new IRI("http://example.org/2003/12/13/atom03"), entry.getContentElement().getResolvedSrc());
    }

    @Test
    public void testSection4133ContentApplicationXhtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-application-xthml.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-application-xthml.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Content content = entry.getContentElement();
            assertNotNull(content);
            assertEquals(Content.Type.XML, entry.getContentElement().getContentType());
        }
    }

    @Test
    public void testSection4133ContentHtmlWithChildren() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-html-with-children.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-html-with-children.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Some  text.", entry.getContent());
        assertEquals(Content.Type.HTML, entry.getContentType());
    }

    @Test
    public void testSection4133ContentJpegInvalidBase64() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-jpeg-invalid-base64.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-jpeg-invalid-base64.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.MEDIA, entry.getContentType());
        assertEquals("insert image here", entry.getContent());
    }

    @Test
    public void testSection4133ContentJpegValidBase64() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-jpeg-valid-base64.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-jpeg-valid-base64.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.MEDIA, entry.getContentType());
        DataHandler dh = entry.getContentElement().getDataHandler();
        ByteArrayInputStream in = (ByteArrayInputStream)dh.getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int n = -1;
        while ((n = in.read()) != -1) {
            out.write(n);
        }
        out.flush();
        assertEquals(1538, out.toByteArray().length);
        assertEquals("image/jpeg", dh.getContentType());
    }

    @Test
    public void testSection4133ContentNoTypeEscapedHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-no-type-escaped-html.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-no-type-escaped-html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Some <b>bold</b> text.", entry.getContent());
        assertEquals(Content.Type.TEXT, entry.getContentType());
    }

    @Test
    public void testSection4133ContentNoTypeWithChildren() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-no-type-with-children.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-no-type-with-children.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Some  text", entry.getContent().trim());
        assertEquals(Content.Type.TEXT, entry.getContentType());
    }

    @Test
    public void testSection4133ContentPlainWithChildren() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-plain-with-children.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-plain-with-children.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Some  text.", entry.getContent().trim());
        assertEquals(Content.Type.MEDIA, entry.getContentType());
    }

    @Test
    public void testSection4133ContentSvgMixed() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-svg-mixed.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-svg-mixed.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Content content = entry.getContentElement();
        assertNotNull(content.getValueElement()); // we're pretty forgiving
        assertEquals(Content.Type.XML, content.getContentType());
    }

    @Test
    public void testSection4133ContentSvg() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-svg.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-svg.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Content content = entry.getContentElement();
            assertNotNull(content);
            assertEquals(Content.Type.XML, entry.getContentElement().getContentType());
        }
    }

    @Test
    public void testSection4133ContentTextHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-text-html.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-text-html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.MEDIA, entry.getContentType());
    }

    @Test
    public void testSection4133ContentTextWithChildren() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-text-with-children.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-text-with-children.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.TEXT, entry.getContentType());
        assertEquals("Some  text", entry.getContent().trim());
    }

    @Test
    public void testSection4133ContentXhtmlEscaped() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-escaped.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-escaped.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.XHTML, entry.getContentType());
        String c = entry.getContent().trim();
        c = c.replaceAll(">", "&gt;");
        assertEquals("Some &lt;b&gt;bold&lt;/b&gt; text.", c);
    }

    @Test
    public void testSection4133ContentXhtmlMixed() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-mixed.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-mixed.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.XHTML, entry.getContentType());
        String c = entry.getContent().trim();
        c = c.replaceAll("Some &lt;b>bold&lt;/b>", "Some &lt;b&gt;bold&lt;/b&gt;");
        assertEquals("<b xmlns=\"http://www.w3.org/1999/xhtml\">Example:</b> Some &lt;b&gt;bold&lt;/b&gt; text.", c);
    }

    @Test
    public void testSection4133ContentXhtmlNoXhtmlDiv() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-no-xhtml-div.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-no-xhtml-div.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.XHTML, entry.getContentType());
        assertNull(entry.getContent());
    }

    @Test
    public void testSection4133ContentXhtmlNotmarkup() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-notmarkup.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-notmarkup.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.XHTML, entry.getContentType());
        String c = entry.getContent();
        c = c.replaceAll(">", "&gt;");
        assertEquals("Some &lt;x&gt;bold&lt;/x&gt; text.", c);
    }

    @Test
    public void testSection4133ContentXhtmlTextChildren() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-text-children.xml
        IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-text-children.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(Content.Type.XHTML, entry.getContentType());
        assertNull(entry.getContent());
    }

    @Test
    public void testSection4221CategoryNoTerm() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.2.1/category-no-term.xml
        IRI uri = baseURI.resolve("4.2.2.1/category-no-term.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        List<Category> cats = entry.getCategories();
        assertEquals(1, cats.size());
        assertNull(cats.get(0).getTerm());
    }

    @Test
    public void testSection4222CategoryNoScheme() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.2.2/category-no-scheme.xml
        IRI uri = baseURI.resolve("4.2.2.2/category-no-scheme.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        List<Category> cats = entry.getCategories();
        assertEquals(1, cats.size());
        assertNull(cats.get(0).getScheme());
    }

    @Test
    public void testSection4222CategorySchemeInvalidIri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.2.2/category-scheme-invalid-iri.xml
        IRI uri = baseURI.resolve("4.2.2.2/category-scheme-invalid-iri.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed);
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Category> cats = entry.getCategories();
            for (Category cat : cats) {
                try {
                    cat.getScheme();
                } catch (Exception e) {
                    assertTrue(e instanceof IRISyntaxException);
                }
            }
        }
    }

    @Test
    public void testSection4222CategorySchemeRelIri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.2.2/category-scheme-rel-iri.xml
        IRI uri = baseURI.resolve("4.2.2.2/category-scheme-rel-iri.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Category cat = entry.getCategories().get(0);
        assertEquals(new IRI("mine"), cat.getScheme());
    }

    @Test
    public void testSection4223CategoryLabelEscapedHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.2.3/category-label-escaped-html.xml
        IRI uri = baseURI.resolve("4.2.2.3/category-label-escaped-html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Category cat = entry.getCategories().get(0);
        assertEquals("<b>business</b>", cat.getLabel());
    }

    @Test
    public void testSection4223CategoryNoLabel() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.2.3/category-no-label.xml
        IRI uri = baseURI.resolve("4.2.2.3/category-no-label.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Category cat = entry.getCategories().get(0);
        assertNull(cat.getLabel());
    }

    @Test
    public void testSection424GeneratorEscapedHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.4/generator-escaped-html.xml
        IRI uri = baseURI.resolve("4.2.4/generator-escaped-html.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Generator generator = feed.getGenerator();
        assertEquals("<b>The</b> generator", generator.getText());
    }

    @Test
    public void testSection424GeneratorInvalidIri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.4/generator-invalid-iri.xml
        IRI uri = baseURI.resolve("4.2.4/generator-invalid-iri.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Generator generator = feed.getGenerator();
        assertNotNull(generator);
        try {
            generator.getUri();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection424GeneratorNoText() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.4/generator-no-text.xml
        IRI uri = baseURI.resolve("4.2.4/generator-no-text.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Generator generator = feed.getGenerator();
        assertEquals("", generator.getText());
    }

    @Test
    public void testSection424GeneratorWithChild() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.4/generator-with-child.xml
        IRI uri = baseURI.resolve("4.2.4/generator-with-child.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Generator generator = feed.getGenerator();
        assertEquals("", generator.getText());
    }

    @Test
    public void testSection424GeneratorRelativeRef() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.4/generator_relative_ref.xml
        IRI uri = baseURI.resolve("4.2.4/generator_relative_ref.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        Generator generator = feed.getGenerator();
        assertNotNull(generator);
        assertEquals(uri.resolve("misc/Colophon"), generator.getResolvedUri());
    }

    @Test
    public void testSection425IconInvalidUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.5/icon_invalid_uri.xml
        IRI uri = baseURI.resolve("4.2.5/icon_invalid_uri.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNotNull(feed.getIconElement());
        try {
            feed.getIconElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection425IconRelativeRef() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.5/icon_relative_ref.xml
        IRI uri = baseURI.resolve("4.2.5/icon_relative_ref.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed);
        assertNotNull(feed.getIconElement());
        assertEquals(uri.resolve("favicon.ico"), feed.getIconElement().getResolvedValue());
    }

    @Test
    public void testSection426IdDotSegments() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-dot-segments.xml
        IRI uri = baseURI.resolve("4.2.6/id-dot-segments.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org/./id/1234"), doc.getRoot().getId());
        assertEquals(new IRI("http://example.org/id/1234"), IRI.normalize(doc.getRoot().getId()));
    }

    @Test
    public void testSection426IdEmptyFragmentId() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-empty-fragment-id.xml
        IRI uri = baseURI.resolve("4.2.6/id-empty-fragment-id.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        feed.getIdElement().getValue();
    }

    @Test
    public void testSection426IdEmptyPath() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-empty-path.xml
        IRI uri = baseURI.resolve("4.2.6/id-empty-path.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdEmptyQuery() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-empty-query.xml
        IRI uri = baseURI.resolve("4.2.6/id-empty-query.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org/id/1234?"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdExplicitAuthority() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-explicit-authority.xml
        IRI uri = baseURI.resolve("4.2.6/id-explicit-authority.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://:@example.org/id/1234"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdExplicitDefaultPort() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-explicit-default-port.xml
        IRI uri = baseURI.resolve("4.2.6/id-explicit-default-port.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org:80/id/1234"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdHostUppercase() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-host-uppercase.xml
        IRI uri = baseURI.resolve("4.2.6/id-host-uppercase.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://Example.org/id/1234"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdNotUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-not-uri.xml
        IRI uri = baseURI.resolve("4.2.6/id-not-uri.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        try {
            feed.getIdElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection426IdPercentEncodedLower() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-percent-encoded-lower.xml
        IRI uri = baseURI.resolve("4.2.6/id-percent-encoded-lower.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org/id/1234?q=%5c"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdPercentEncoded() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-percent-encoded.xml
        IRI uri = baseURI.resolve("4.2.6/id-percent-encoded.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://example.org/%69%64/1234"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdRelativeUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-relative-uri.xml
        IRI uri = baseURI.resolve("4.2.6/id-relative-uri.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("/id/1234"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdUppercaseScheme() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-uppercase-scheme.xml
        IRI uri = baseURI.resolve("4.2.6/id-uppercase-scheme.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("Http://example.org/id/1234"), doc.getRoot().getId());
    }

    @Test
    public void testSection426IdValidTagUris() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.6/id-valid-tag-uris.xml
        IRI uri = baseURI.resolve("4.2.6/id-valid-tag-uris.xml");
        Document<Feed> doc = parse(uri);
        // we don't care that they're invalid, at least for now
        assertEquals(new IRI("tag:example.com,2000:"), doc.getRoot().getId());
    }

    @Test
    public void testSection4271LinkHrefInvalid() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.1/link-href-invalid.xml
        IRI uri = baseURI.resolve("4.2.7.1/link-href-invalid.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Link> links = entry.getLinks();
            for (Link link : links) {
                try {
                    link.getHref();
                } catch (Exception e) {
                    assertTrue(e instanceof IRISyntaxException);
                }
            }
        }
    }

    @Test
    public void testSection427LinkHrefRelative() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.1/link-href-relative.xml
        IRI uri = baseURI.resolve("4.2.7.1/link-href-relative.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Link> links = entry.getLinks();
            for (Link link : links) {
                assertEquals(uri.resolve("/2003/12/13/atom03"), link.getResolvedHref());
            }
        }
    }

    @Test
    public void testSection427LinkNoHref() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.1/link-no-href.xml
        IRI uri = baseURI.resolve("4.2.7.1/link-no-href.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Link link = entry.getLinks().get(0);
        assertNull(link.getHref());
    }

    @Test
    public void testSection4272AbsoluteRel() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/absolute_rel.xml
        IRI uri = baseURI.resolve("4.2.7.2/absolute_rel.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertEquals(1, feed.getLinks(Link.REL_ALTERNATE).size());
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            assertEquals(1, entry.getLinks(Link.REL_ALTERNATE).size());
        }
    }

    @Test
    public void testSection4272EmptyPath() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/empty-path.xml
        IRI uri = baseURI.resolve("4.2.7.2/empty-path.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
        for (Link link : links) {
            assertEquals(uri, link.getResolvedHref());
        }
    }

    @Test
    public void testSection4272LinkRelIsegmentNzNc() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-isegment-nz-nc.xml
        IRI uri = baseURI.resolve("4.2.7.2/link-rel-isegment-nz-nc.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNotNull(entry.getAlternateLink());
    }

    @Test
    public void testSection4272LinkRelRelative() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-relative.xml
        IRI uri = baseURI.resolve("4.2.7.2/link-rel-relative.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Link link = entry.getLink("/foo");
        assertNotNull(link); // we don't care that it's invalid
    }

    @Test
    public void testSection4272LinkRelSelfMatch() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-match.xml
        IRI uri = baseURI.resolve("4.2.7.2/link-rel-self-match.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://www.feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-match.xml"), doc
            .getRoot().getSelfLink().getResolvedHref());
    }

    @Test
    public void testSection4272LinkRelSelfNoMatch() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-nomatch.xml
        IRI uri = baseURI.resolve("4.2.7.2/link-rel-self-nomatch.xml");
        Document<Feed> doc = parse(uri);
        assertEquals(new IRI("http://www.feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-match.xml"), doc
            .getRoot().getSelfLink().getResolvedHref());
    }

    @Test
    public void testSection4272SelfVsAlternate() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/self-vs-alternate.xml
        IRI uri = baseURI.resolve("4.2.7.2/self-vs-alternate.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getAlternateLink());
        Link self = entry.getLink("self");
        assertEquals("text/html", self.getMimeType().toString());
    }

    @Test
    public void testSection4272UnregisteredRel() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.2/unregistered-rel.xml
        IRI uri = baseURI.resolve("4.2.7.2/unregistered-rel.xml");
        Document<Feed> doc = parse(uri);
        assertNotNull(doc.getRoot().getLink("service.post"));
    }

    @Test
    public void testSection4273LinkTypeInvalidMime() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.3/link-type-invalid-mime.xml
        IRI uri = baseURI.resolve("4.2.7.3/link-type-invalid-mime.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Link> links = entry.getLinks();
            for (Link link : links) {
                try {
                    link.getMimeType();
                } catch (Exception e) {
                    assertTrue(e instanceof MimeTypeParseException);
                }
            }
        }
    }

    @Test
    public void testSection4273LinkTypeParameters() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.3/link-type-parameters.xml
        IRI uri = baseURI.resolve("4.2.7.3/link-type-parameters.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            List<Link> links = entry.getLinks();
            for (Link link : links) {
                assertEquals("text/html", link.getMimeType().getBaseType());
                assertEquals("utf-8", link.getMimeType().getParameter("charset"));
            }
        }
    }

    @Test
    public void testSection4274LinkHreflangInvalidLanguage() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.4/link-hreflang-invalid-language.xml
        IRI uri = baseURI.resolve("4.2.7.4/link-hreflang-invalid-language.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Link link = entry.getAlternateLink();
        assertEquals("insert language here", link.getHrefLang());
    }

    @Test
    public void testSection4275LinkTitleWithBadchars() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.5/link-title-with-badchars.xml
        IRI uri = baseURI.resolve("4.2.7.5/link-title-with-badchars.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Link link = entry.getAlternateLink();
        assertEquals("This is a \u00A3\u0093test.\u0094", link.getTitle());
    }

    @Test
    public void testSection4275LinkTitleWithHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.5/link-title-with-html.xml
        IRI uri = baseURI.resolve("4.2.7.5/link-title-with-html.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Link link = entry.getAlternateLink();
        assertEquals("very, <b>very</b>, scary indeed", link.getTitle());
    }

    @Test
    public void testSection4276LinkLengthNotPositive() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.7.6/link-length-not-positive.xml
        IRI uri = baseURI.resolve("4.2.7.6/link-length-not-positive.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Link link = entry.getAlternateLink();
        assertEquals(-1, link.getLength());
    }

    @Test
    public void testSection428LogoInvalidUri() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.8/logo-invalid-uri.xml
        IRI uri = baseURI.resolve("4.2.8/logo-invalid-uri.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        assertNotNull(feed.getLogoElement());
        try {
            feed.getLogoElement().getValue();
        } catch (Exception e) {
            assertTrue(e instanceof IRISyntaxException);
        }
    }

    @Test
    public void testSection428LogoRelativeRef() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.8/logo_relative_ref.xml
        IRI uri = baseURI.resolve("4.2.8/logo_relative_ref.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        assertNotNull(feed.getLogoElement());
        assertEquals(uri.resolve("atomlogo.png"), feed.getLogoElement().getResolvedValue());
    }

    @Test
    public void testSection429PublishedInvalidDate() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.9/published-invalid-date.xml
        IRI uri = baseURI.resolve("4.2.9/published-invalid-date.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getPublishedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void testSection4210RightsInvalidType() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.10/rights-invalid-type.xml
        try {
            IRI uri = baseURI.resolve("4.2.10/rights-invalid-type.xml");
            Document<Feed> doc = parse(uri);
            doc.getRoot().getRights();
        } catch (Exception e) {
        }
    }

    @Test
    public void testSection4210RightsTextWithEscapedHtml() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.10/rights-text-with-escaped-html.xml
        IRI uri = baseURI.resolve("4.2.10/rights-text-with-escaped-html.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("Copyright &copy; 2005", doc.getRoot().getRights());
    }

    @Test
    public void testSection4210RightsXhtmlNoXmldiv() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.10/rights-xhtml-no-xmldiv.xml
        IRI uri = baseURI.resolve("4.2.10/rights-xhtml-no-xmldiv.xml");
        Document<Feed> doc = parse(uri);
        assertNull(doc.getRoot().getRights());
    }

    @Test
    public void testSection4211MissingId() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/missing-id.xml
        IRI uri = baseURI.resolve("4.2.11/missing-id.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Source source = entry.getSource();
        assertNull(source.getId());
    }

    @Test
    public void testSection4211MissingTitle() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/missing-title.xml
        IRI uri = baseURI.resolve("4.2.11/missing-title.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Source source = entry.getSource();
        assertNull(source.getTitle());
    }

    @Test
    public void testSection4211MissingUpdated() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/missing-updated.xml
        IRI uri = baseURI.resolve("4.2.11/missing-updated.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Source source = entry.getSource();
        assertNull(source.getUpdated());
    }

    @Test
    public void testSection4211MultipleAlternatesDiffering() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-alternates-differing.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-alternates-differing.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Source source = entry.getSource();
        List<Link> links = source.getLinks("alternate");
        assertEquals(2, links.size());
        assertEquals(new IRI("http://example.org/"), links.get(0).getResolvedHref());
        assertEquals(new IRI("http://example.es/"), links.get(1).getResolvedHref());
    }

    @Test
    public void testSection4211MultipleAlternatesMatching() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-alternates-matching.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-alternates-matching.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Source source = entry.getSource();
        assertEquals(new IRI("http://example.org/front-page.html"), source.getAlternateLink().getResolvedHref());
    }

    @Test
    public void testSection4211MultipleAuthors() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-authors.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-authors.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Source source = entry.getSource();
            assertNotNull(source);
            assertEquals(2, source.getAuthors().size());
        }
    }

    @Test
    public void testSection4211MultipleCategories() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-categories.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-categories.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Source source = entry.getSource();
            assertNotNull(source);
            assertEquals(2, source.getCategories().size());
        }
    }

    @Test
    public void testSection4211MultipleContributors() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-contributors.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-contributors.xml");
        Document<Feed> doc = parse(uri);

        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            Source source = entry.getSource();
            assertNotNull(source);
            assertEquals(2, source.getContributors().size());
        }
    }

    @Test
    public void testSection4211MultipleGenerators() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-generators.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-generators.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Generator g = entry.getSource().getGenerator();
        assertEquals(new IRI("http://www.example.com/"), g.getResolvedUri());
    }

    @Test
    public void testSection4211MultipleIcons() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-icons.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-icons.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(new IRI("http://feedvalidator.org/big.icon"), entry.getSource().getIcon());
    }

    @Test
    public void testSection4211MultipleIds() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-ids.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-ids.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(new IRI("urn:uuid:28213c50-f84c-11d9-8cd6-0800200c9a66"), entry.getSource().getId());
    }

    @Test
    public void testSection4211MultipleLogos() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-logos.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-logos.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals(new IRI("http://feedvalidator.org/small.jpg"), entry.getSource().getLogo());
    }

    @Test
    public void testSection4211MultipleRights() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-rights.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-rights.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Public Domain", entry.getSource().getRights().trim());
    }

    @Test
    public void testSection4211MultipleSubtitles() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-subtitles.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-subtitles.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("A unique feed, just like all the others", entry.getSource().getSubtitle().trim());
    }

    @Test
    public void testSection4211MultipleTitles() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-titles.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-titles.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertEquals("Source of all knowledge", entry.getSource().getTitle().trim());
    }

    @Test
    public void testSection4211MultipleUpdateds() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/multiple-updateds.xml
        IRI uri = baseURI.resolve("4.2.11/multiple-updateds.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Date d = AtomDate.parse("2003-12-13T17:46:27Z");
        assertEquals(d, entry.getSource().getUpdated());
    }

    @Test
    public void testSection4211SourceEntry() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.11/source-entry.xml
        IRI uri = baseURI.resolve("4.2.11/source-entry.xml");
        Document<Feed> doc = parse(uri);
        Entry entry = doc.getRoot().getEntries().get(0);
        Source source = entry.getSource();
        assertNotNull(source);
    }

    @Test
    public void testSection4212SubtitleBlank() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.12/subtitle-blank.xml
        IRI uri = baseURI.resolve("4.2.12/subtitle-blank.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("", doc.getRoot().getSubtitle());
    }

    @Test
    public void testSection4214TitleBlank() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.14/title-blank.xml
        IRI uri = baseURI.resolve("4.2.14/title-blank.xml");
        Document<Feed> doc = parse(uri);
        assertEquals("", doc.getRoot().getTitle());
    }

    @Test
    public void testSection4215UpdatedInvalidDate() throws Exception {
        // http://feedvalidator.org/testcases/atom/4.2.15/updated-invalid-date.xml
        IRI uri = baseURI.resolve("4.2.15/updated-invalid-date.xml");
        Document<Feed> doc = parse(uri);
        Feed feed = doc.getRoot();
        List<Entry> entries = feed.getEntries();
        for (Entry entry : entries) {
            try {
                entry.getUpdatedElement().getValue();
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

}
