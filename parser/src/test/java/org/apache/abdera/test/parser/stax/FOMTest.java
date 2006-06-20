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
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.filter.TextFilter;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.InReplyTo;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.StringElement;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Total;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.FOMEntry;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.util.AbderaSource;
import org.apache.abdera.util.BlackListParseFilter;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.URIHelper;
import org.apache.abdera.util.Version;
import org.apache.abdera.util.WhiteListParseFilter;
import org.apache.abdera.xpath.XPath;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


import junit.framework.TestCase;

public class FOMTest extends TestCase   {

  public static void testMinimalConfiguration() {
    assertNotNull(Factory.INSTANCE);
    assertNotNull(Parser.INSTANCE);
    assertNotNull(XPath.INSTANCE);
  }
  
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
        Base elparent = parent.getParentElement();
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
  
  public void testFactory() throws Exception {
    Factory factory = Factory.INSTANCE;
    Person author = factory.newAuthor(null);
    assertNotNull(author);
    author = factory.newAuthor("a", "b", "c", null);
    assertNotNull(author);
    assertEquals(author.getName(),"a");
    assertEquals(author.getEmail(), "b");
    assertEquals(author.getUri().toString(), "c");
    author = factory.newAuthor("a", "b", new URI("c"), null);
    assertNotNull(author);
    assertEquals(author.getName(),"a");
    assertEquals(author.getEmail(), "b");
    assertEquals(author.getUri().toString(), "c");
    Category category = factory.newCategory(null);
    assertNotNull(category);
    category = factory.newCategory(new URI("a"), "b", "c", null);
    assertNotNull(category);
    assertEquals(category.getScheme().toString(), "a");
    assertEquals(category.getTerm(), "b");
    assertEquals(category.getLabel(), "c");
    Collection collection = factory.newCollection(null);
    assertNotNull(collection);
    Content content = factory.newContent(Content.Type.TEXT, null);
    assertNotNull(content);
    assertEquals(content.getContentType(), Content.Type.TEXT);
    content = factory.newContent(Content.Type.HTML, null);
    assertEquals(content.getContentType(), Content.Type.HTML);
    content = factory.newContent(Content.Type.XHTML, null);
    assertEquals(content.getContentType(), Content.Type.XHTML);
    content = factory.newContent(Content.Type.MEDIA, null);
    assertEquals(content.getContentType(), Content.Type.MEDIA);
    content = factory.newContent(Content.Type.XML, null);
    assertEquals(content.getContentType(), Content.Type.XML);
    content = factory.newContent(Content.Type.MEDIA, new MimeType("text/foo"), null);
    assertEquals(content.getContentType(), Content.Type.MEDIA);
    assertEquals(content.getMimeType().toString(), "text/foo");
    Person contributor = factory.newContributor(null);
    assertNotNull(contributor);
    contributor = factory.newContributor("a", "b", "c", null);
    assertNotNull(contributor);
    assertEquals(contributor.getName(),"a");
    assertEquals(contributor.getEmail(), "b");
    assertEquals(contributor.getUri().toString(), "c");
    contributor = factory.newContributor("a", "b", new URI("c"), null);
    assertNotNull(contributor);
    assertEquals(contributor.getName(),"a");
    assertEquals(contributor.getEmail(), "b");
    assertEquals(contributor.getUri().toString(), "c");
    Control control = factory.newControl(null);
    assertNotNull(control);
    control = factory.newControl(true, null);
    assertTrue(control.isDraft());
    Date now = new Date();
    DateTime dateTime = factory.newDateTime(Constants.UPDATED, AtomDate.valueOf(now), null);
    assertEquals(dateTime.getDate(), now);
    Calendar cal = Calendar.getInstance();
    dateTime = factory.newDateTime(Constants.UPDATED, cal, null);
    assertEquals(dateTime.getCalendar(), cal);
    dateTime = factory.newDateTime(Constants.UPDATED, now, null);
    assertEquals(dateTime.getDate(), now);
    dateTime = factory.newDateTime(Constants.UPDATED, null);
    assertNotNull(dateTime);
    dateTime = factory.newDateTime(Constants.UPDATED, now.getTime(), null);
    assertEquals(dateTime.getTime(), now.getTime());
    dateTime = factory.newDateTime(Constants.UPDATED, AtomDate.format(now), null);
    assertEquals(dateTime.getString(), AtomDate.format(now));
    assertEquals(dateTime.getDate(), now);
    Generator generator = factory.newDefaultGenerator(null);
    assertNotNull(generator);
    assertEquals(generator.getValue(), Version.APP_NAME);
    assertEquals(generator.getVersion(), Version.VERSION);
    assertEquals(generator.getUri().toString(), Version.URI);
    Div div = factory.newDiv(null);
    assertNotNull(div);
    Document doc = factory.newDocument();
    assertNotNull(doc);
    StringElement el = factory.newEmail(null);
    assertNotNull(el);
    el = factory.newEmail("a", null);
    assertEquals(el.getValue(), "a");
    Entry entry = factory.newEntry();
    assertNotNull(entry);
    entry = factory.newEntry(null);
    assertNotNull(entry);
    ExtensionElement ee = factory.newExtensionElement(new QName("urn:foo", "bar", "b"), null);
    assertNotNull(ee);
    assertEquals(ee.getQName(), new QName("urn:foo", "bar", "b"));
    Feed feed = factory.newFeed();
    assertNotNull(feed);
    feed = factory.newFeed(null);
    assertNotNull(feed);
    generator = factory.newGenerator(null);
    assertNotNull(generator);
    generator = factory.newGenerator(new URI(Version.URI), Version.VERSION, Version.APP_NAME, null);
    assertNotNull(generator);
    assertEquals(generator.getValue(), Version.APP_NAME);
    assertEquals(generator.getVersion(), Version.VERSION);
    assertEquals(generator.getUri().toString(), Version.URI);
    content = factory.newHtmlContent("a", null);
    assertNotNull(content);
    assertEquals(content.getValue(), "a");
    assertEquals(content.getContentType(), Content.Type.HTML);
    Text text = factory.newHtmlRights("a", null);
    assertNotNull(text);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    text = factory.newHtmlSubtitle("a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    text = factory.newHtmlSummary("a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    text = factory.newHtmlText(Constants.TITLE, "a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    assertEquals(text.getQName(), Constants.TITLE);
    text = factory.newHtmlTitle("a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    IRI iri = factory.newIcon(null);
    assertNotNull(iri);
    iri = factory.newIcon("http://example.org/foo", null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newIcon(new URI("http://example.org/foo"), null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newID(null);
    assertNotNull(iri);
    iri = factory.newID("http://example.org/foo", null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newID(new URI("http://example.org/foo"), null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newIRIElement(Constants.ID, null);
    assertNotNull(iri);
    iri = factory.newIRIElement(Constants.ID, "http://example.org/foo", null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newIRIElement(Constants.ID, new URI("http://example.org/foo"), null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    InReplyTo inreplyto = factory.newInReplyTo(null);
    assertNotNull(inreplyto);
    inreplyto = factory.newInReplyTo("http://example.org/foo", null);
    assertNotNull(inreplyto);
    assertEquals(inreplyto.getRef().toString(), "http://example.org/foo");
    inreplyto = factory.newInReplyTo(new URI("http://example.org/foo"), null);
    assertEquals(inreplyto.getRef().toString(), "http://example.org/foo");
    inreplyto = factory.newInReplyTo("http://example.org/foo", "http://example.org/foo", "http://example.org/foo", "text/foo", null);
    assertEquals(inreplyto.getRef().toString(), "http://example.org/foo");
    assertEquals(inreplyto.getSource().toString(), "http://example.org/foo");
    assertEquals(inreplyto.getHref().toString(), "http://example.org/foo");
    assertEquals(inreplyto.getMimeType().toString(), "text/foo");
    inreplyto = factory.newInReplyTo(new URI("http://example.org/foo"), new URI("http://example.org/foo"), new URI("http://example.org/foo"), new MimeType("text/foo"), null);
    assertEquals(inreplyto.getRef().toString(), "http://example.org/foo");
    assertEquals(inreplyto.getSource().toString(), "http://example.org/foo");
    assertEquals(inreplyto.getHref().toString(), "http://example.org/foo");
    assertEquals(inreplyto.getMimeType().toString(), "text/foo");
    Link link = factory.newLink(null);
    assertNotNull(link);
    link = factory.newLink("http://example.org/foo", "a", new MimeType("text/foo"), "b", "en", 10, null);
    assertEquals(link.getHref().toString(), "http://example.org/foo");
    assertEquals(link.getRel(), "a");
    assertEquals(link.getMimeType().toString(), "text/foo");
    assertEquals(link.getTitle(), "b");
    assertEquals(link.getHrefLang(), "en");
    assertEquals(link.getLength(), 10);
    link = factory.newLink(new URI("http://example.org/foo"), "a", new MimeType("text/foo"), "b", "en", 10, null);
    assertEquals(link.getHref().toString(), "http://example.org/foo");
    assertEquals(link.getRel(), "a");
    assertEquals(link.getMimeType().toString(), "text/foo");
    assertEquals(link.getTitle(), "b");
    assertEquals(link.getHrefLang(), "en");
    assertEquals(link.getLength(), 10);
    iri = factory.newLogo(null);
    assertNotNull(iri);
    iri = factory.newLogo("http://example.org/foo", null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newLogo(new URI("http://example.org/foo"), null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    content = factory.newMediaContent(new MimeType("text/foo"), new URI("foo"), null, null);
    assertNotNull(content);
    assertEquals(content.getMimeType().toString(), "text/foo");
    assertEquals(content.getSrc().toString(), "foo");
    content = factory.newMediaContent(new MimeType("text/foo"), null, new DataHandler(new ByteArrayDataSource("foo".getBytes())), null);
    assertEquals(content.getValue(), "Zm9v");
    assertEquals(content.getContentType(), Content.Type.MEDIA);
    el = factory.newName(null);
    assertNotNull(el);
    el = factory.newName("a", null);
    assertEquals(el.getValue(), "a");
    Parser parser = factory.newParser();
    assertNotNull(parser);
    Person person = factory.newPerson(Constants.AUTHOR, null);
    assertNotNull(person);
    assertEquals(person.getQName(), Constants.AUTHOR);
    person = factory.newPerson(Constants.AUTHOR, "a", "b", "c", null);
    assertEquals(person.getName(),"a");
    assertEquals(person.getEmail(), "b");
    assertEquals(person.getUri().toString(), "c");
    person = factory.newPerson(Constants.AUTHOR, "a", "b", new URI("c"), null);
    assertEquals(person.getName(),"a");
    assertEquals(person.getEmail(), "b");
    assertEquals(person.getUri().toString(), "c");
    now = new Date();
    dateTime = factory.newPublished(AtomDate.valueOf(now), null);
    assertEquals(dateTime.getDate(), now);
    cal = Calendar.getInstance();
    dateTime = factory.newPublished(cal, null);
    assertEquals(dateTime.getCalendar(), cal);
    dateTime = factory.newPublished(now, null);
    assertEquals(dateTime.getDate(), now);
    dateTime = factory.newPublished(null);
    assertNotNull(dateTime);
    dateTime = factory.newPublished(now.getTime(), null);
    assertEquals(dateTime.getTime(), now.getTime());
    dateTime = factory.newPublished(AtomDate.format(now), null);
    assertEquals(dateTime.getString(), AtomDate.format(now));
    assertEquals(dateTime.getDate(), now);
    Service service = factory.newService();
    assertNotNull(service);
    service = factory.newService(null);
    assertNotNull(service);
    Source source = factory.newSource(null);
    assertNotNull(source);
    el = factory.newStringElement(Constants.NAME, (Element)null);
    assertNotNull(el);
    assertEquals(el.getQName(), Constants.NAME);
    el = factory.newStringElement(Constants.NAME, "a", (Element)null);
    assertNotNull(el);
    assertEquals(el.getQName(), Constants.NAME);
    assertEquals(el.getValue(), "a");
    text = factory.newText(Constants.TITLE, Text.Type.TEXT, null);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newTextRights("a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newTextSubtitle("a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newTextSummary("a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newTextText(Constants.TITLE, "a", null);
    assertEquals(text.getQName(), Constants.TITLE);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newTextTitle("a", null);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    content = factory.newTextContent("a", null);
    assertEquals(content.getValue(), "a");
    assertEquals(content.getContentType(), Content.Type.TEXT);
    Total total = factory.newTotal(null);
    assertNotNull(total);
    total = factory.newTotal(10, null);
    assertEquals(total.getValue(), 10);
    now = new Date();
    dateTime = factory.newUpdated(AtomDate.valueOf(now), null);
    assertEquals(dateTime.getDate(), now);
    cal = Calendar.getInstance();
    dateTime = factory.newUpdated(cal, null);
    assertEquals(dateTime.getCalendar(), cal);
    dateTime = factory.newUpdated(now, null);
    assertEquals(dateTime.getDate(), now);
    dateTime = factory.newUpdated(null);
    assertNotNull(dateTime);
    dateTime = factory.newUpdated(now.getTime(), null);
    assertEquals(dateTime.getTime(), now.getTime());
    dateTime = factory.newUpdated(AtomDate.format(now), null);
    assertEquals(dateTime.getString(), AtomDate.format(now));
    assertEquals(dateTime.getDate(), now);
    iri = factory.newUri(null);
    assertNotNull(iri);
    iri = factory.newUri("http://example.org/foo", null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newUri(new URI("http://example.org/foo"), null);
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    Workspace workspace = factory.newWorkspace(null);
    assertNotNull(workspace);
    div = factory.newDiv(null);
    content = factory.newXhtmlContent(div, null);
    assertNotNull(content);
    assertEquals(content.getContentType(), Content.Type.XHTML);
    assertNotNull(content.getValueElement());
    assertEquals(content.getValueElement(), div);
    content = factory.newXmlContent(new MimeType("application/xml"), null, div, null);
    assertNotNull(content);
    assertEquals(content.getContentType(), Content.Type.XML);
    assertNotNull(content.getValueElement());
    assertEquals(content.getValueElement(), div);
    text = factory.newXhtmlRights(div, null);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newXhtmlSubtitle(div, null);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newXhtmlSummary(div, null);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newXhtmlText(Constants.TITLE, div, null);
    assertNotNull(text);
    assertEquals(text.getQName(), Constants.TITLE);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newXhtmlTitle(div, null);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    QName qname = new QName("urn:foo", "bar");
    factory.registerAsSimpleExtension(qname);
    el = (StringElement) factory.newExtensionElement(qname, null);
    assertEquals(el.getQName(), qname);
  }
  
  public void testRoundtrip() throws Exception {

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
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    feed.getDocument().writeTo(out);
    
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Document<Feed> doc = Parser.INSTANCE.parse(in);
    feed = doc.getRoot();
    
    assertEquals(feed.getLanguage(), "en-US");
    assertEquals(feed.getBaseUri().toString(), "http://example.org");
    assertEquals(feed.getTitle(), "Example Feed");
    assertEquals(feed.getAlternateLink().getHref().toString(), "http://example.org/");
    assertEquals(feed.getAuthor().getName(), "John Doe");
    assertEquals(feed.getId().toString(), "urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
    assertEquals(feed.getContributors().get(0).getName(), "Bob Jones");
    assertEquals(feed.getCategories().get(0).getTerm(), "example");
    
    assertEquals(feed.getEntries().size(), 2);
    entry = feed.getFirstChild(Constants.ENTRY);
    assertNotNull(entry);
    assertEquals(entry.getTitle(), "re: Atom-Powered Robots Run Amok");
    assertEquals(entry.getAlternateLink().getHref().toString(), "/2003/12/13/atom03/1");
    assertEquals(entry.getAlternateLink().getResolvedHref().toString(), "http://example.org/2003/12/13/atom03/1");
    assertEquals(entry.getId().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b");
    assertEquals(entry.getSummary(), "A response");
    assertNotNull(entry.getInReplyTo());
    assertEquals(entry.getInReplyTo().getRef().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    
    entry = entry.getNextSibling(Constants.ENTRY);
    assertNotNull(entry);
    assertEquals(entry.getTitle(), "Atom-Powered Robots Run Amok");
    assertEquals(entry.getAlternateLink().getHref().toString(), "http://example.org/2003/12/13/atom03");
    assertEquals(entry.getId().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    assertEquals(entry.getSummary(), "Some text.");
  }
  
  public void testAlternatives() throws Exception {
     Factory factory = new MyFactory();
     Entry entry = factory.newEntry();
     assertTrue(entry instanceof MyEntry);
     
     ParserOptions options = Parser.INSTANCE.getDefaultParserOptions();
     options.setFactory(factory);
     InputStream in = FOMTest.class.getResourceAsStream("/simple.xml");
     Document<Feed> doc = Parser.INSTANCE.parse(in, (URI)null, options);
     Feed feed = doc.getRoot();
     entry = feed.getEntries().get(0);
     assertTrue(entry instanceof MyEntry);
  }
  
  public void testSourceResult() throws Exception {
    try {
      // Apply an XSLT transform to the entire Feed
      TransformerFactory factory = TransformerFactory.newInstance();
      Document xslt = Parser.INSTANCE.parse(FOMTest.class.getResourceAsStream("/test.xslt"));
      AbderaSource xsltSource = new AbderaSource(xslt);
      Transformer transformer = factory.newTransformer(xsltSource);
      Document<Feed> feed = Parser.INSTANCE.parse(FOMTest.class.getResourceAsStream("/simple.xml"));
      AbderaSource feedSource = new AbderaSource(feed);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Result result = new StreamResult(out);
      transformer.transform(feedSource, result);
      assertEquals(out.toString(), "This is a test urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
      
      // Apply an XSLT transform to XML in the content element
      xslt = Parser.INSTANCE.parse(FOMTest.class.getResourceAsStream("/content.xslt"));
      xsltSource = new AbderaSource(xslt);
      transformer = factory.newTransformer(xsltSource);
      feed = Parser.INSTANCE.parse(FOMTest.class.getResourceAsStream("/xmlcontent.xml"));
      Entry entry = feed.getRoot().getEntries().get(0);
      Content content = entry.getContentElement();
      AbderaSource contentSource = new AbderaSource(content.getValueElement());
      out = new ByteArrayOutputStream();
      result = new StreamResult(out);
      transformer.transform(contentSource, result);
      assertEquals(out.toString(), "This is a test test");
    } catch (Exception exception) {
      // TrAX is likely not configured, skip the test
    }
  }
  
  public static class MyFactory extends FOMFactory {
    public MyFactory() {
      registerAlternative(FOMEntry.class, MyEntry.class);
    }
  }
  
  @SuppressWarnings("serial")
  public static class MyEntry extends FOMEntry{

    public MyEntry(
      OMContainer parent, 
      OMFactory factory, 
      OMXMLParserWrapper builder) 
        throws OMException {
      super(parent, factory, builder);
    }

    public MyEntry(
      OMContainer parent, 
      OMFactory factory) 
        throws OMException {
      super(parent, factory);
    }

    public MyEntry(
      QName qname, 
      OMContainer parent, 
      OMFactory factory, 
      OMXMLParserWrapper builder) {
        super(qname, parent, factory, builder);
    }

    public MyEntry(
      QName qname, 
      OMContainer parent, 
      OMFactory factory) {
        super(qname, parent, factory);
    }

    public MyEntry(
      String name, 
      OMNamespace namespace, 
      OMContainer parent, 
      OMFactory factory) 
        throws OMException {
      super(name, namespace, parent, factory);
    }
    
  }
}
