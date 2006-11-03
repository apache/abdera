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
import org.apache.abdera.util.URIHelper;
import org.apache.abdera.util.Version;
import org.apache.abdera.util.filter.BlackListParseFilter;
import org.apache.abdera.util.filter.WhiteListParseFilter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;
import org.apache.axiom.attachments.ByteArrayDataSource;

import junit.framework.TestCase;

public class FOMTest extends TestCase   {

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
  
  public static void testMinimalConfiguration() {
    assertNotNull(getFactory());
    assertNotNull(getParser());
    assertNotNull(getXPath());
    assertNotNull(getWriterFactory());
    assertNotNull(getParserFactory());
    assertNotNull(getWriter());
  }
  
  public void testParser() throws Exception {
    
    InputStream in = FOMTest.class.getResourceAsStream("/simple.xml");
    Document<Feed> doc = getParser().parse(in);
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
    
    //TODO: we can't compare the serializations.  different 
    // stax impls serialize with slight variances
    //String compare = "<?xml version='1.0' encoding='UTF-8'?><a:feed xmlns:a=\"http://www.w3.org/2005/Atom\" xml:base=\"http://example.org\" xml:lang=\"en-US\"><a:title type=\"text\">Example Feed</a:title><a:link href=\"http://example.org/\" /><a:author><a:name>John Doe</a:name></a:author><a:id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</a:id><a:contributor><a:name>Bob Jones</a:name></a:contributor><a:category term=\"example\" /><a:entry><a:title type=\"text\">re: Atom-Powered Robots Run Amok</a:title><a:link href=\"/2003/12/13/atom03/1\" /><a:id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b</a:id><a:summary type=\"text\">A response</a:summary></a:entry><a:entry><a:title type=\"text\">Atom-Powered Robots Run Amok</a:title><a:link href=\"http://example.org/2003/12/13/atom03\" /><a:id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</a:id><a:summary type=\"text\">Some text.</a:summary></a:entry></a:feed>";
    
    //ByteArrayOutputStream out = new ByteArrayOutputStream(512);
    //feed.getDocument().writeTo(out);
    //String actual = out.toString();
    
    //assertEquals(actual, compare);
    
    assertEquals(feed.getEntries().get(0).getId().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80cb323feb5b");
    assertEquals(feed.getEntries().get(1).getId().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    
  }

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

    Document<Feed> doc = getParser().parse(in, url.toString(), options);
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
    
    ListParseFilter filter = new BlackListParseFilter();
    filter.add(Constants.UPDATED);
    ParserOptions options = getParser().getDefaultParserOptions();
    options.setParseFilter(filter);
    
    URL url = FOMTest.class.getResource("/simple.xml");
    InputStream in = url.openStream();

    Document<Feed> doc = getParser().parse(in, url.toString(), options);
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
  
  public void testXPath() throws Exception {
    
    InputStream in = FOMTest.class.getResourceAsStream("/simple.xml");
    Document<Feed> doc = getParser().parse(in);
    Feed feed = doc.getRoot();
    XPath xpath = getXPath();
    assertEquals(xpath.evaluate("count(/a:feed)", feed), 1.0d);
    assertTrue(xpath.booleanValueOf("/a:feed/a:entry", feed));
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
    Factory factory = getFactory();
    Person author = factory.newAuthor();
    assertNotNull(author);
    author = factory.newAuthor();
    author.setName("a");
    author.setEmail("b");
    author.setUri("c");
    assertNotNull(author);
    assertEquals(author.getName(),"a");
    assertEquals(author.getEmail(), "b");
    assertEquals(author.getUri().toString(), "c");
    author = factory.newAuthor();
    author.setName("a");
    author.setEmail("b");
    author.setUri("c");
    assertNotNull(author);
    assertEquals(author.getName(),"a");
    assertEquals(author.getEmail(), "b");
    assertEquals(author.getUri().toString(), "c");
    Category category = factory.newCategory();
    assertNotNull(category);
    category = factory.newCategory();
    category.setScheme("a");
    category.setTerm("b");
    category.setLabel("c");
    assertNotNull(category);
    assertEquals(category.getScheme().toString(), "a");
    assertEquals(category.getTerm(), "b");
    assertEquals(category.getLabel(), "c");
    Collection collection = factory.newCollection();
    assertNotNull(collection);
    Content content = factory.newContent(Content.Type.TEXT);
    assertNotNull(content);
    assertEquals(content.getContentType(), Content.Type.TEXT);
    content = factory.newContent(Content.Type.HTML);
    assertEquals(content.getContentType(), Content.Type.HTML);
    content = factory.newContent(Content.Type.XHTML);
    assertEquals(content.getContentType(), Content.Type.XHTML);
    content = factory.newContent(Content.Type.MEDIA);
    assertEquals(content.getContentType(), Content.Type.MEDIA);
    content = factory.newContent(Content.Type.XML);
    assertEquals(content.getContentType(), Content.Type.XML);
    content = factory.newContent(new MimeType("text/foo"));
    assertEquals(content.getContentType(), Content.Type.MEDIA);
    assertEquals(content.getMimeType().toString(), "text/foo");
    Person contributor = factory.newContributor();
    assertNotNull(contributor);
    contributor = factory.newContributor();
    contributor.setName("a");
    contributor.setEmail("b");
    contributor.setUri("c");
    assertNotNull(contributor);
    assertEquals(contributor.getName(),"a");
    assertEquals(contributor.getEmail(), "b");
    assertEquals(contributor.getUri().toString(), "c");
    contributor = factory.newContributor();
    contributor.setName("a");
    contributor.setEmail("b");
    contributor.setUri("c");
    assertNotNull(contributor);
    assertEquals(contributor.getName(),"a");
    assertEquals(contributor.getEmail(), "b");
    assertEquals(contributor.getUri().toString(), "c");
    Control control = factory.newControl();
    assertNotNull(control);
    control = factory.newControl();
    control.setDraft(true);
    assertTrue(control.isDraft());
    Date now = new Date();
    DateTime dateTime = factory.newDateTime(Constants.UPDATED, null);
    dateTime.setValue(AtomDate.valueOf(now));
    assertEquals(dateTime.getDate(), now);
    Calendar cal = Calendar.getInstance();
    dateTime = factory.newDateTime(Constants.UPDATED, null);
    dateTime.setCalendar(cal);
    assertEquals(dateTime.getCalendar(), cal);
    dateTime = factory.newDateTime(Constants.UPDATED, null);
    dateTime.setDate(now);
    assertEquals(dateTime.getDate(), now);
    dateTime = factory.newDateTime(Constants.UPDATED, null);
    assertNotNull(dateTime);
    dateTime = factory.newDateTime(Constants.UPDATED, null);
    dateTime.setTime(now.getTime());
    assertEquals(dateTime.getTime(), now.getTime());
    dateTime = factory.newDateTime(Constants.UPDATED, null);
    dateTime.setString(AtomDate.format(now));
    assertEquals(dateTime.getString(), AtomDate.format(now));
    assertEquals(dateTime.getDate(), now);
    Generator generator = factory.newDefaultGenerator();
    assertNotNull(generator);
    assertEquals(generator.getText(), Version.APP_NAME);
    assertEquals(generator.getVersion(), Version.VERSION);
    assertEquals(generator.getUri().toString(), Version.URI);
    Div div = factory.newDiv();
    assertNotNull(div);
    Document doc = factory.newDocument();
    assertNotNull(doc);
    Element el = factory.newEmail();
    assertNotNull(el);
    el = factory.newEmail();
    el.setText("a");
    assertEquals(el.getText(), "a");
    Entry entry = factory.newEntry();
    assertNotNull(entry);
    entry = factory.newEntry();
    assertNotNull(entry);
    Element ee = factory.newExtensionElement(new QName("urn:foo", "bar", "b"));
    assertNotNull(ee);
    assertEquals(ee.getQName(), new QName("urn:foo", "bar", "b"));
    Feed feed = factory.newFeed();
    assertNotNull(feed);
    generator = factory.newGenerator();
    assertNotNull(generator);
    generator = factory.newGenerator();
    generator.setUri(Version.URI);
    generator.setVersion(Version.VERSION);
    generator.setText(Version.APP_NAME);
    assertNotNull(generator);
    assertEquals(generator.getText(), Version.APP_NAME);
    assertEquals(generator.getVersion(), Version.VERSION);
    assertEquals(generator.getUri().toString(), Version.URI);
    content = factory.newContent(Content.Type.HTML);
    content.setValue("a");
    assertNotNull(content);
    assertEquals(content.getValue(), "a");
    assertEquals(content.getContentType(), Content.Type.HTML);
    Text text = factory.newRights(Text.Type.HTML);
    text.setValue("a");
    assertNotNull(text);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    text = factory.newSubtitle(Text.Type.HTML);
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    text = factory.newSummary(Text.Type.HTML);
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    text = factory.newText(Constants.TITLE,Text.Type.HTML, null);
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    assertEquals(text.getQName(), Constants.TITLE);
    text = factory.newTitle(Text.Type.HTML);
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.HTML);
    IRIElement iri = factory.newIcon();
    assertNotNull(iri);
    iri = factory.newIcon();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newIcon();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newID();
    assertNotNull(iri);
    iri = factory.newID();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newID();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newIRIElement(Constants.ID, null);
    assertNotNull(iri);
    iri = factory.newIRIElement(Constants.ID, null);
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newIRIElement(Constants.ID, null);
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    Link link = factory.newLink();
    assertNotNull(link);
    link = factory.newLink();
    link.setHref("http://example.org/foo");
    link.setRel("a");
    link.setMimeType("text/foo");
    link.setTitle("b");
    link.setHrefLang("en");
    link.setLength(10);
    assertEquals(link.getHref().toString(), "http://example.org/foo");
    assertEquals(link.getRel(), "a");
    assertEquals(link.getMimeType().toString(), "text/foo");
    assertEquals(link.getTitle(), "b");
    assertEquals(link.getHrefLang(), "en");
    assertEquals(link.getLength(), 10);
    link = factory.newLink();
    link.setHref("http://example.org/foo");
    link.setRel("a");
    link.setMimeType("text/foo");
    link.setTitle("b");
    link.setHrefLang("en");
    link.setLength(10);
    assertEquals(link.getHref().toString(), "http://example.org/foo");
    assertEquals(link.getRel(), "a");
    assertEquals(link.getMimeType().toString(), "text/foo");
    assertEquals(link.getTitle(), "b");
    assertEquals(link.getHrefLang(), "en");
    assertEquals(link.getLength(), 10);
    iri = factory.newLogo();
    assertNotNull(iri);
    iri = factory.newLogo();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newLogo();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    content = factory.newContent(new MimeType("text/foo"));
    content.setSrc("foo");
    assertNotNull(content);
    assertEquals(content.getMimeType().toString(), "text/foo");
    assertEquals(content.getSrc().toString(), "foo");
    content = factory.newContent(new MimeType("text/foo"));
    content.setDataHandler(new DataHandler(new ByteArrayDataSource("foo".getBytes())));
    assertEquals(content.getValue(), "Zm9v");
    assertEquals(content.getContentType(), Content.Type.MEDIA);
    el = factory.newName();
    assertNotNull(el);
    el = factory.newName();
    el.setText("a");
    assertEquals(el.getText(), "a");
    Parser parser = factory.newParser();
    assertNotNull(parser);
    Person person = factory.newPerson(Constants.AUTHOR, null);
    assertNotNull(person);
    assertEquals(person.getQName(), Constants.AUTHOR);
    person = factory.newPerson(Constants.AUTHOR, null);
    person.setName("a");
    person.setEmail("b");
    person.setUri("c");
    assertEquals(person.getName(),"a");
    assertEquals(person.getEmail(), "b");
    assertEquals(person.getUri().toString(), "c");
    person = factory.newPerson(Constants.AUTHOR, null);
    person.setName("a");
    person.setEmail("b");
    person.setUri("c");
    assertEquals(person.getName(),"a");
    assertEquals(person.getEmail(), "b");
    assertEquals(person.getUri().toString(), "c");
    now = new Date();
    dateTime = factory.newPublished();
    dateTime.setValue(AtomDate.valueOf(now));
    assertEquals(dateTime.getDate(), now);
    cal = Calendar.getInstance();
    dateTime = factory.newPublished();
    dateTime.setCalendar(cal);
    assertEquals(dateTime.getCalendar(), cal);
    dateTime = factory.newPublished();
    dateTime.setDate(now);
    assertEquals(dateTime.getDate(), now);
    dateTime = factory.newPublished();
    assertNotNull(dateTime);
    dateTime = factory.newPublished();
    dateTime.setTime(now.getTime());
    assertEquals(dateTime.getTime(), now.getTime());
    dateTime = factory.newPublished();
    dateTime.setString(AtomDate.format(now));
    assertEquals(dateTime.getString(), AtomDate.format(now));
    assertEquals(dateTime.getDate(), now);
    Service service = factory.newService();
    assertNotNull(service);
    Source source = factory.newSource();
    assertNotNull(source);
    el = factory.newElement(Constants.NAME);
    assertNotNull(el);
    assertEquals(el.getQName(), Constants.NAME);
    el = factory.newElement(Constants.NAME);
    el.setText("a");
    assertNotNull(el);
    assertEquals(el.getQName(), Constants.NAME);
    assertEquals(el.getText(), "a");
    text = factory.newText(Constants.TITLE, Text.Type.TEXT);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newRights();
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newSubtitle();
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newSummary();
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newText(Constants.TITLE, Text.Type.TEXT,null);
    text.setValue("a");
    assertEquals(text.getQName(), Constants.TITLE);
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    text = factory.newTitle();
    text.setValue("a");
    assertEquals(text.getValue(), "a");
    assertEquals(text.getTextType(), Text.Type.TEXT);
    content = factory.newContent(Content.Type.TEXT);
    content.setValue("a");
    assertEquals(content.getValue(), "a");
    assertEquals(content.getContentType(), Content.Type.TEXT);
    now = new Date();
    dateTime = factory.newUpdated();
    dateTime.setValue(AtomDate.valueOf(now));
    assertEquals(dateTime.getDate(), now);
    cal = Calendar.getInstance();
    dateTime = factory.newUpdated();
    dateTime.setCalendar(cal);
    assertEquals(dateTime.getCalendar(), cal);
    dateTime = factory.newUpdated();
    dateTime.setDate(now);
    assertEquals(dateTime.getDate(), now);
    dateTime = factory.newUpdated();
    assertNotNull(dateTime);
    dateTime = factory.newUpdated();
    dateTime.setTime(now.getTime());
    assertEquals(dateTime.getTime(), now.getTime());
    dateTime = factory.newUpdated();
    dateTime.setString(AtomDate.format(now));
    assertEquals(dateTime.getString(), AtomDate.format(now));
    assertEquals(dateTime.getDate(), now);
    iri = factory.newUri();
    assertNotNull(iri);
    iri = factory.newUri();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    iri = factory.newUri();
    iri.setValue("http://example.org/foo");
    assertEquals(iri.getValue().toString(), "http://example.org/foo");
    Workspace workspace = factory.newWorkspace();
    assertNotNull(workspace);
    div = factory.newDiv();
    content = factory.newContent(Content.Type.XHTML);
    content.setValueElement(div);
    assertNotNull(content);
    assertEquals(content.getContentType(), Content.Type.XHTML);
    assertNotNull(content.getValueElement());
    assertEquals(content.getValueElement(), div);
    content = factory.newContent(new MimeType("application/xml"));
    content.setValueElement(div);
    assertNotNull(content);
    assertEquals(content.getContentType(), Content.Type.XML);
    assertNotNull(content.getValueElement());
    assertEquals(content.getValueElement(), div);
    text = factory.newRights();
    text.setValueElement(div);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newSubtitle();
    text.setValueElement(div);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newSummary();
    text.setValueElement(div);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newText(Constants.TITLE, null);
    text.setValueElement(div);
    assertNotNull(text);
    assertEquals(text.getQName(), Constants.TITLE);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
    text = factory.newTitle();
    text.setValueElement(div);
    assertNotNull(text);
    assertEquals(text.getTextType(), Text.Type.XHTML);
    assertEquals(text.getValueElement(), div);
  }
  
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
    
    entry = entry.getNextSibling(Constants.ENTRY);
    assertNotNull(entry);
    assertEquals(entry.getTitle(), "Atom-Powered Robots Run Amok");
    assertEquals(entry.getAlternateLink().getHref().toString(), "http://example.org/2003/12/13/atom03");
    assertEquals(entry.getId().toString(), "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a");
    assertEquals(entry.getSummary(), "Some text.");
  }
  
  public void testSourceResult() throws Exception {
    try {
      // Apply an XSLT transform to the entire Feed
      TransformerFactory factory = TransformerFactory.newInstance();
      Document xslt = getParser().parse(FOMTest.class.getResourceAsStream("/test.xslt"));
      AbderaSource xsltSource = new AbderaSource(xslt);
      Transformer transformer = factory.newTransformer(xsltSource);
      Document<Feed> feed = getParser().parse(FOMTest.class.getResourceAsStream("/simple.xml"));
      AbderaSource feedSource = new AbderaSource(feed);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Result result = new StreamResult(out);
      transformer.transform(feedSource, result);
      assertEquals(out.toString(), "This is a test urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6");
      
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
      assertEquals(out.toString(), "This is a test test");
    } catch (Exception exception) {
      // TrAX is likely not configured, skip the test
    }
  }
  
}
