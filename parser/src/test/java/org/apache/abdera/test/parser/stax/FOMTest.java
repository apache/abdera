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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ListParseFilter;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
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
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.AbderaSource;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.Version;
import org.apache.abdera.util.filter.BlackListParseFilter;
import org.apache.abdera.util.filter.WhiteListParseFilter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.junit.Test;

public class FOMTest {

    private static Abdera abdera = new Abdera();

    private static Parser getParser() {
        return abdera.getParser();
    }

    private static Factory getFactory() {
        return abdera.getFactory();
    }

    private static XPath getXPath() {
        return abdera.getXPath();
    }

    private static WriterFactory getWriterFactory() {
        return abdera.getWriterFactory();
    }

    private static ParserFactory getParserFactory() {
        return abdera.getParserFactory();
    }

    private static Writer getWriter() {
        return abdera.getWriter();
    }

    @Test
    public void testMinimalConfiguration() {
        assertNotNull(getFactory());
        assertNotNull(getParser());
        assertNotNull(getXPath());
        assertNotNull(getWriterFactory());
        assertNotNull(getParserFactory());
        assertNotNull(getWriter());
    }

    @Test
    public void testParser() throws Exception {

        InputStream in = FOMTest.class.getResourceAsStream("/simple.xml");
        Document<Feed> doc = getParser().parse(in);
        Feed feed = doc.getRoot();

        assertEquals("Example Feed", feed.getTitle());
        assertEquals(Text.Type.TEXT, feed.getTitleType());
        assertEquals("http://example.org/", feed.getAlternateLink().getResolvedHref().toString());
        assertNotNull(feed.getUpdated());
        assertEquals("John Doe", feed.getAuthor().getName());
        assertEquals("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", feed.getId().toString());

        Entry entry = feed.getEntries().get(0);

        assertEquals("Atom-Powered Robots Run Amok", entry.getTitle());
        assertEquals(Text.Type.TEXT, entry.getTitleType());
        assertEquals("http://example.org/2003/12/13/atom03", entry.getAlternateLink().getResolvedHref().toString());
        assertEquals("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", entry.getId().toString());
        assertNotNull(entry.getUpdated());
        assertEquals("Some text.", entry.getSummary());
        assertEquals(Text.Type.TEXT, entry.getSummaryType());

    }

    @Test
    public void testCreate() throws Exception {
        Feed feed = getFactory().newFeed();
        feed.setLanguage("en-US");
        feed.setBaseUri("http://example.org");

        feed.setTitle("Example Feed");
        feed.addLink("http://example.org/");
        feed.addAuthor("John Doe");
        feed.setId("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", false);
        feed.addContributor("Bob Jones");
        feed.addCategory("example");

        Entry entry = feed.insertEntry();
        entry.setTitle("Atom-Powered Robots Run Amok");
        entry.addLink("http://example.org/2003/12/13/atom03");
        entry.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", false);
        entry.setSummary("Some text.");

        Entry entry2 = feed.insertEntry();
        entry2.setTitle("re: Atom-Powered Robots Run Amok");
        entry2.addLink("/2003/12/13/atom03/1");
        entry2.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b", false);
        entry2.setSummary("A response");

        assertEquals("urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b", feed.getEntries().get(0).getId().toString());
        assertEquals("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", feed.getEntries().get(1).getId().toString());

    }

    @Test
    public void testWhiteListParseFilter() throws Exception {

        ListParseFilter filter = new WhiteListParseFilter();
        filter.add(Constants.FEED);
        filter.add(Constants.ENTRY);
        filter.add(Constants.TITLE);
        filter.add(Constants.ID);
        ParserOptions options = getParser().getDefaultParserOptions();
        options.setParseFilter(filter);

        URL url = FOMTest.class.getResource("/simple.xml");
        InputStream in = url.openStream();

        Document<Feed> doc = getParser().parse(in, url.toString().replaceAll(" ", "%20"), options);
        Feed feed = doc.getRoot();

        assertEquals("Example Feed", feed.getTitle());
        assertEquals(Text.Type.TEXT, feed.getTitleType());
        assertNull(feed.getAlternateLink());
        assertNull(feed.getUpdated());
        assertNull(feed.getAuthor());
        assertEquals("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", feed.getId().toString());

        Entry entry = feed.getEntries().get(0);

        assertEquals("Atom-Powered Robots Run Amok", entry.getTitle());
        assertEquals(Text.Type.TEXT, entry.getTitleType());
        assertNull(entry.getAlternateLink());
        assertEquals("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", entry.getId().toString());
        assertNull(entry.getUpdated());
        assertNull(entry.getSummary());
        assertNull(entry.getSummaryType());
    }

    @Test
    public void testBlackListParseFilter() throws Exception {

        ListParseFilter filter = new BlackListParseFilter();
        filter.add(Constants.UPDATED);
        ParserOptions options = getParser().getDefaultParserOptions();
        options.setParseFilter(filter);

        URL url = FOMTest.class.getResource("/simple.xml");
        InputStream in = url.openStream();

        Document<Feed> doc = getParser().parse(in, url.toString().replaceAll(" ", "%20"), options);
        Feed feed = doc.getRoot();

        assertEquals("Example Feed", feed.getTitle());
        assertEquals(Text.Type.TEXT, feed.getTitleType());
        assertEquals("http://example.org/", feed.getAlternateLink().getResolvedHref().toString());
        assertNull(feed.getUpdated());
        assertEquals("John Doe", feed.getAuthor().getName());
        assertEquals("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", feed.getId().toString());

        Entry entry = feed.getEntries().get(0);

        assertEquals("Atom-Powered Robots Run Amok", entry.getTitle());
        assertEquals(Text.Type.TEXT, entry.getTitleType());
        assertEquals("http://example.org/2003/12/13/atom03", entry.getAlternateLink().getResolvedHref().toString());
        assertEquals("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", entry.getId().toString());
        assertNull(entry.getUpdated());
        assertEquals("Some text.", entry.getSummary());
        assertEquals(Text.Type.TEXT, entry.getSummaryType());

    }

    @Test
    public void testXPath() throws Exception {

        InputStream in = FOMTest.class.getResourceAsStream("/simple.xml");
        Document<Feed> doc = getParser().parse(in);
        Feed feed = doc.getRoot();
        XPath xpath = getXPath();
        assertEquals(1.0d, xpath.evaluate("count(/a:feed)", feed));
        assertTrue(xpath.booleanValueOf("/a:feed/a:entry", feed));
        assertEquals(1.0d, xpath.numericValueOf("count(/a:feed)", feed));
        assertEquals("Atom-Powered Robots Run Amok", xpath.valueOf("/a:feed/a:entry/a:title", feed));
        assertEquals(1, xpath.selectNodes("/a:feed/a:entry", feed).size());
        assertTrue(xpath.selectSingleNode("/a:feed", feed) instanceof Feed);
        assertEquals(feed, xpath.selectSingleNode("..", feed.getTitleElement()));
        assertEquals(feed, xpath.selectSingleNode("ancestor::*", feed.getEntries().get(0)));
        assertEquals("The feed is is urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", xpath
            .valueOf("concat('The feed is is ',/a:feed/a:id)", feed));

    }

    @Test
    public void testUriNormalization() throws Exception {
        String s1 = "HTTP://www.Example.ORG:80/./foo/%2d/../%2d/./foo";
        String s2 = "HTTP://www.Example.ORG:81/./foo/%2d/../%2d/./foo";
        assertEquals("http://www.example.org/foo/-/foo", IRI.normalizeString(s1));
        assertEquals("http://www.example.org:81/foo/-/foo", IRI.normalizeString(s2));
    }

    @Test
    public void testFactory() throws Exception {
        Factory factory = getFactory();
        Person author = factory.newAuthor();
        assertNotNull(author);
        author = factory.newAuthor();
        author.setName("a");
        author.setEmail("b");
        author.setUri("c");
        assertNotNull(author);
        assertEquals("a", author.getName());
        assertEquals("b", author.getEmail());
        assertEquals("c", author.getUri().toString());
        author = factory.newAuthor();
        author.setName("a");
        author.setEmail("b");
        author.setUri("c");
        assertNotNull(author);
        assertEquals("a", author.getName());
        assertEquals("b", author.getEmail());
        assertEquals("c", author.getUri().toString());
        Category category = factory.newCategory();
        assertNotNull(category);
        category = factory.newCategory();
        category.setScheme("a");
        category.setTerm("b");
        category.setLabel("c");
        assertNotNull(category);
        assertEquals("a", category.getScheme().toString());
        assertEquals("b", category.getTerm());
        assertEquals("c", category.getLabel());
        Collection collection = factory.newCollection();
        assertNotNull(collection);
        Content content = factory.newContent(Content.Type.TEXT);
        assertNotNull(content);
        assertEquals(Content.Type.TEXT, content.getContentType());
        content = factory.newContent(Content.Type.HTML);
        assertEquals(Content.Type.HTML, content.getContentType());
        content = factory.newContent(Content.Type.XHTML);
        assertEquals(Content.Type.XHTML, content.getContentType());
        content = factory.newContent(Content.Type.MEDIA);
        assertEquals(Content.Type.MEDIA, content.getContentType());
        content = factory.newContent(Content.Type.XML);
        assertEquals(Content.Type.XML, content.getContentType());
        content = factory.newContent(new MimeType("text/foo"));
        assertEquals(Content.Type.MEDIA, content.getContentType());
        assertEquals("text/foo", content.getMimeType().toString());
        Person contributor = factory.newContributor();
        assertNotNull(contributor);
        contributor = factory.newContributor();
        contributor.setName("a");
        contributor.setEmail("b");
        contributor.setUri("c");
        assertNotNull(contributor);
        assertEquals("a", contributor.getName());
        assertEquals("b", contributor.getEmail());
        assertEquals("c", contributor.getUri().toString());
        contributor = factory.newContributor();
        contributor.setName("a");
        contributor.setEmail("b");
        contributor.setUri("c");
        assertNotNull(contributor);
        assertEquals("a", contributor.getName());
        assertEquals("b", contributor.getEmail());
        assertEquals("c", contributor.getUri().toString());
        Control control = factory.newControl();
        assertNotNull(control);
        control = factory.newControl();
        control.setDraft(true);
        assertTrue(control.isDraft());
        Date now = new Date();
        DateTime dateTime = factory.newDateTime(Constants.UPDATED, null);
        dateTime.setValue(AtomDate.valueOf(now));
        assertEquals(now, dateTime.getDate());
        Calendar cal = Calendar.getInstance();
        dateTime = factory.newDateTime(Constants.UPDATED, null);
        dateTime.setCalendar(cal);
        assertEquals(cal, dateTime.getCalendar());
        dateTime = factory.newDateTime(Constants.UPDATED, null);
        dateTime.setDate(now);
        assertEquals(now, dateTime.getDate());
        dateTime = factory.newDateTime(Constants.UPDATED, null);
        assertNotNull(dateTime);
        dateTime = factory.newDateTime(Constants.UPDATED, null);
        dateTime.setTime(now.getTime());
        assertEquals(now.getTime(), dateTime.getTime());
        dateTime = factory.newDateTime(Constants.UPDATED, null);
        dateTime.setString(AtomDate.format(now));
        assertEquals(AtomDate.format(now), dateTime.getString());
        assertEquals(now, dateTime.getDate());
        Generator generator = factory.newDefaultGenerator();
        assertNotNull(generator);
        assertEquals(Version.APP_NAME, generator.getText());
        assertEquals(Version.VERSION, generator.getVersion());
        assertEquals(Version.URI, generator.getUri().toString());
        Div div = factory.newDiv();
        assertNotNull(div);
        Document<?> doc = factory.newDocument();
        assertNotNull(doc);
        Element el = factory.newEmail();
        assertNotNull(el);
        el = factory.newEmail();
        el.setText("a");
        assertEquals("a", el.getText());
        Entry entry = factory.newEntry();
        assertNotNull(entry);
        entry = factory.newEntry();
        assertNotNull(entry);
        Element ee = factory.newExtensionElement(new QName("urn:foo", "bar", "b"));
        assertNotNull(ee);
        assertEquals(new QName("urn:foo", "bar", "b"), ee.getQName());
        Feed feed = factory.newFeed();
        assertNotNull(feed);
        generator = factory.newGenerator();
        assertNotNull(generator);
        generator = factory.newGenerator();
        generator.setUri(Version.URI);
        generator.setVersion(Version.VERSION);
        generator.setText(Version.APP_NAME);
        assertNotNull(generator);
        assertEquals(Version.APP_NAME, generator.getText());
        assertEquals(Version.VERSION, generator.getVersion());
        assertEquals(Version.URI, generator.getUri().toString());
        content = factory.newContent(Content.Type.HTML);
        content.setValue("a");
        assertNotNull(content);
        assertEquals("a", content.getValue());
        assertEquals(Content.Type.HTML, content.getContentType());
        Text text = factory.newRights(Text.Type.HTML);
        text.setValue("a");
        assertNotNull(text);
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.HTML, text.getTextType());
        text = factory.newSubtitle(Text.Type.HTML);
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.HTML, text.getTextType());
        text = factory.newSummary(Text.Type.HTML);
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.HTML, text.getTextType());
        text = factory.newText(Constants.TITLE, Text.Type.HTML, null);
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.HTML, text.getTextType());
        assertEquals(Constants.TITLE, text.getQName());
        text = factory.newTitle(Text.Type.HTML);
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.HTML, text.getTextType());
        IRIElement iri = factory.newIcon();
        assertNotNull(iri);
        iri = factory.newIcon();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        iri = factory.newIcon();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        iri = factory.newID();
        assertNotNull(iri);
        iri = factory.newID();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        iri = factory.newID();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        iri = factory.newIRIElement(Constants.ID, null);
        assertNotNull(iri);
        iri = factory.newIRIElement(Constants.ID, null);
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        iri = factory.newIRIElement(Constants.ID, null);
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        Link link = factory.newLink();
        assertNotNull(link);
        link = factory.newLink();
        link.setHref("http://example.org/foo");
        link.setRel("a");
        link.setMimeType("text/foo");
        link.setTitle("b");
        link.setHrefLang("en");
        link.setLength(10);
        assertEquals("http://example.org/foo", link.getHref().toString());
        assertEquals("a", link.getRel());
        assertEquals("text/foo", link.getMimeType().toString());
        assertEquals("b", link.getTitle());
        assertEquals("en", link.getHrefLang());
        assertEquals(10, link.getLength());
        link = factory.newLink();
        link.setHref("http://example.org/foo");
        link.setRel("a");
        link.setMimeType("text/foo");
        link.setTitle("b");
        link.setHrefLang("en");
        link.setLength(10);
        assertEquals("http://example.org/foo", link.getHref().toString());
        assertEquals("a", link.getRel());
        assertEquals("text/foo", link.getMimeType().toString());
        assertEquals("b", link.getTitle());
        assertEquals("en", link.getHrefLang());
        assertEquals(10, link.getLength());
        iri = factory.newLogo();
        assertNotNull(iri);
        iri = factory.newLogo();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        iri = factory.newLogo();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        content = factory.newContent(new MimeType("text/foo"));
        content.setSrc("foo");
        assertNotNull(content);
        assertEquals("text/foo", content.getMimeType().toString());
        assertEquals("foo", content.getSrc().toString());
        content = factory.newContent(new MimeType("text/foo"));
        content.setDataHandler(new DataHandler(new ByteArrayDataSource("foo".getBytes())));
        assertEquals("Zm9v", content.getValue());
        assertEquals(Content.Type.MEDIA, content.getContentType());
        el = factory.newName();
        assertNotNull(el);
        el = factory.newName();
        el.setText("a");
        assertEquals("a", el.getText());
        Parser parser = factory.newParser();
        assertNotNull(parser);
        Person person = factory.newPerson(Constants.AUTHOR, null);
        assertNotNull(person);
        assertEquals(Constants.AUTHOR, person.getQName());
        person = factory.newPerson(Constants.AUTHOR, null);
        person.setName("a");
        person.setEmail("b");
        person.setUri("c");
        assertEquals("a", person.getName());
        assertEquals("b", person.getEmail());
        assertEquals("c", person.getUri().toString());
        person = factory.newPerson(Constants.AUTHOR, null);
        person.setName("a");
        person.setEmail("b");
        person.setUri("c");
        assertEquals("a", person.getName());
        assertEquals("b", person.getEmail());
        assertEquals("c", person.getUri().toString());
        now = new Date();
        dateTime = factory.newPublished();
        dateTime.setValue(AtomDate.valueOf(now));
        assertEquals(now, dateTime.getDate());
        cal = Calendar.getInstance();
        dateTime = factory.newPublished();
        dateTime.setCalendar(cal);
        assertEquals(cal, dateTime.getCalendar());
        dateTime = factory.newPublished();
        dateTime.setDate(now);
        assertEquals(now, dateTime.getDate());
        dateTime = factory.newPublished();
        assertNotNull(dateTime);
        dateTime = factory.newPublished();
        dateTime.setTime(now.getTime());
        assertEquals(now.getTime(), dateTime.getTime());
        dateTime = factory.newPublished();
        dateTime.setString(AtomDate.format(now));
        assertEquals(AtomDate.format(now), dateTime.getString());
        assertEquals(now, dateTime.getDate());
        Service service = factory.newService();
        assertNotNull(service);
        Source source = factory.newSource();
        assertNotNull(source);
        el = factory.newElement(Constants.NAME);
        assertNotNull(el);
        assertEquals(Constants.NAME, el.getQName());
        el = factory.newElement(Constants.NAME);
        el.setText("a");
        assertNotNull(el);
        assertEquals(Constants.NAME, el.getQName());
        assertEquals("a", el.getText());
        text = factory.newText(Constants.TITLE, Text.Type.TEXT);
        assertNotNull(text);
        assertEquals(Text.Type.TEXT, text.getTextType());
        text = factory.newRights();
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.TEXT, text.getTextType());
        text = factory.newSubtitle();
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.TEXT, text.getTextType());
        text = factory.newSummary();
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.TEXT, text.getTextType());
        text = factory.newText(Constants.TITLE, Text.Type.TEXT, null);
        text.setValue("a");
        assertEquals(Constants.TITLE, text.getQName());
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.TEXT, text.getTextType());
        text = factory.newTitle();
        text.setValue("a");
        assertEquals("a", text.getValue());
        assertEquals(Text.Type.TEXT, text.getTextType());
        content = factory.newContent(Content.Type.TEXT);
        content.setValue("a");
        assertEquals("a", content.getValue());
        assertEquals(Content.Type.TEXT, content.getContentType());
        now = new Date();
        dateTime = factory.newUpdated();
        dateTime.setValue(AtomDate.valueOf(now));
        assertEquals(now, dateTime.getDate());
        cal = Calendar.getInstance();
        dateTime = factory.newUpdated();
        dateTime.setCalendar(cal);
        assertEquals(cal, dateTime.getCalendar());
        dateTime = factory.newUpdated();
        dateTime.setDate(now);
        assertEquals(now, dateTime.getDate());
        dateTime = factory.newUpdated();
        assertNotNull(dateTime);
        dateTime = factory.newUpdated();
        dateTime.setTime(now.getTime());
        assertEquals(now.getTime(), dateTime.getTime());
        dateTime = factory.newUpdated();
        dateTime.setString(AtomDate.format(now));
        assertEquals(AtomDate.format(now), dateTime.getString());
        assertEquals(now, dateTime.getDate());
        iri = factory.newUri();
        assertNotNull(iri);
        iri = factory.newUri();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        iri = factory.newUri();
        iri.setValue("http://example.org/foo");
        assertEquals("http://example.org/foo", iri.getValue().toString());
        Workspace workspace = factory.newWorkspace();
        assertNotNull(workspace);
        div = factory.newDiv();
        content = factory.newContent(Content.Type.XHTML);
        content.setValueElement(div);
        assertNotNull(content);
        assertEquals(Content.Type.XHTML, content.getContentType());
        assertNotNull(content.getValueElement());
        assertEquals(div, content.getValueElement());
        content = factory.newContent(new MimeType("application/xml"));
        content.setValueElement(div);
        assertNotNull(content);
        assertEquals(Content.Type.XML, content.getContentType());
        assertNotNull(content.getValueElement());
        assertEquals(div, content.getValueElement());
        text = factory.newRights();
        text.setValueElement(div);
        assertNotNull(text);
        assertEquals(Text.Type.XHTML, text.getTextType());
        assertEquals(div, text.getValueElement());
        text = factory.newSubtitle();
        text.setValueElement(div);
        assertNotNull(text);
        assertEquals(Text.Type.XHTML, text.getTextType());
        assertEquals(div, text.getValueElement());
        text = factory.newSummary();
        text.setValueElement(div);
        assertNotNull(text);
        assertEquals(Text.Type.XHTML, text.getTextType());
        assertEquals(div, text.getValueElement());
        text = factory.newText(Constants.TITLE, null);
        text.setValueElement(div);
        assertNotNull(text);
        assertEquals(Constants.TITLE, text.getQName());
        assertEquals(Text.Type.XHTML, text.getTextType());
        assertEquals(div, text.getValueElement());
        text = factory.newTitle();
        text.setValueElement(div);
        assertNotNull(text);
        assertEquals(Text.Type.XHTML, text.getTextType());
        assertEquals(div, text.getValueElement());
    }

    @Test
    public void testRoundtrip() throws Exception {

        Feed feed = getFactory().newFeed();
        feed.setLanguage("en-US");
        feed.setBaseUri("http://example.org");

        feed.setTitle("Example Feed");
        feed.addLink("http://example.org/");
        feed.addAuthor("John Doe");
        feed.setId("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", false);
        feed.addContributor("Bob Jones");
        feed.addCategory("example");

        Entry entry = feed.insertEntry();
        entry.setTitle("Atom-Powered Robots Run Amok");
        entry.addLink("http://example.org/2003/12/13/atom03");
        entry.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", false);
        entry.setSummary("Some text.");

        Entry entry2 = feed.insertEntry();
        entry2.setTitle("re: Atom-Powered Robots Run Amok");
        entry2.addLink("/2003/12/13/atom03/1");
        entry2.setId("urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b", false);
        entry2.setSummary("A response");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        feed.getDocument().writeTo(out);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Document<Feed> doc = getParser().parse(in);
        feed = doc.getRoot();

        assertEquals("en-US", feed.getLanguage());
        assertEquals("http://example.org", feed.getBaseUri().toString());
        assertEquals("Example Feed", feed.getTitle());
        assertEquals("http://example.org/", feed.getAlternateLink().getHref().toString());
        assertEquals("John Doe", feed.getAuthor().getName());
        assertEquals("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", feed.getId().toString());
        assertEquals("Bob Jones", feed.getContributors().get(0).getName());
        assertEquals("example", feed.getCategories().get(0).getTerm());

        assertEquals(2, feed.getEntries().size());
        entry = feed.getFirstChild(Constants.ENTRY);
        assertNotNull(entry);
        assertEquals("re: Atom-Powered Robots Run Amok", entry.getTitle());
        assertEquals("/2003/12/13/atom03/1", entry.getAlternateLink().getHref().toString());
        assertEquals("http://example.org/2003/12/13/atom03/1", entry.getAlternateLink().getResolvedHref().toString());
        assertEquals("urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b", entry.getId().toString());
        assertEquals("A response", entry.getSummary());

        entry = entry.getNextSibling(Constants.ENTRY);
        assertNotNull(entry);
        assertEquals("Atom-Powered Robots Run Amok", entry.getTitle());
        assertEquals("http://example.org/2003/12/13/atom03", entry.getAlternateLink().getHref().toString());
        assertEquals("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a", entry.getId().toString());
        assertEquals("Some text.", entry.getSummary());
    }

    @Test
    public void testSourceResult() throws Exception {
        try {
            // Apply an XSLT transform to the entire Feed
            TransformerFactory factory = TransformerFactory.newInstance();
            Document<Element> xslt = getParser().parse(FOMTest.class.getResourceAsStream("/test.xslt"));
            AbderaSource xsltSource = new AbderaSource(xslt);
            Transformer transformer = factory.newTransformer(xsltSource);
            Document<Feed> feed = getParser().parse(FOMTest.class.getResourceAsStream("/simple.xml"));
            AbderaSource feedSource = new AbderaSource(feed);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Result result = new StreamResult(out);
            transformer.transform(feedSource, result);
            assertEquals("This is a test urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6", out.toString());

            // Apply an XSLT transform to XML in the content element
            xslt = getParser().parse(FOMTest.class.getResourceAsStream("/content.xslt"));
            xsltSource = new AbderaSource(xslt);
            transformer = factory.newTransformer(xsltSource);
            feed = getParser().parse(FOMTest.class.getResourceAsStream("/xmlcontent.xml"));
            Entry entry = feed.getRoot().getEntries().get(0);
            Content content = entry.getContentElement();
            AbderaSource contentSource = new AbderaSource(content.getValueElement());
            out = new ByteArrayOutputStream();
            result = new StreamResult(out);
            transformer.transform(contentSource, result);
            assertEquals("This is a test test", out.toString());
        } catch (Exception exception) {
            // TrAX is likely not configured, skip the test
        }
    }

    @Test
    public void testContentClone() throws Exception {
        String s = "<entry xmlns='http://www.w3.org/2005/Atom'><content type='html'>test</content></entry>";
        ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes());
        Abdera abdera = new Abdera();
        Parser parser = abdera.getParser();
        Document<Entry> doc = parser.parse(in);
        Entry entry = (Entry)(doc.getRoot().clone());
        assertEquals(Content.Type.HTML, entry.getContentType());
    }

    @Test
    public void testSimpleExtension() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setDraft(true); // this will create an app:control element
        assertNull(entry.getControl().getSimpleExtension(new QName("urn:foo", "foo")));
    }

    @Test
    public void testLang() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setLanguage("en-US");
        assertEquals("en-US", entry.getLanguage());
        Lang lang = entry.getLanguageTag();
        assertNotNull(lang);
        assertEquals("en", lang.getLanguage().getName());
        assertEquals("US", lang.getRegion().getName());
        assertEquals(java.util.Locale.US, lang.getLocale());
    }

    @Test
    public void testSetContent() throws Exception {

        Abdera abdera = new Abdera();

        Entry entry = abdera.newEntry();
        Document<Element> foodoc = abdera.getParser().parse(new ByteArrayInputStream("<a><b><c/></b></a>".getBytes()));
        Element foo = foodoc.getRoot();
        entry.setContent(foo, "application/foo+xml");
        assertEquals(foo, entry.getContentElement().getValueElement());

    }

    @Test
    public void testSetContent2() throws Exception {

        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        InputStream in = new ByteArrayInputStream("tóst".getBytes("utf-16"));

        Document<Entry> edoc = entry.getDocument();
        entry.setContent(in, "text/plain;charset=\"utf-16\"");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter w = new OutputStreamWriter(out, "utf-16");
        edoc.writeTo(w);

        in = new ByteArrayInputStream(out.toByteArray());

        entry = (Entry)abdera.getParser().parse(in).getRoot();

        assertEquals("tóst", entry.getContent());
    }
}
