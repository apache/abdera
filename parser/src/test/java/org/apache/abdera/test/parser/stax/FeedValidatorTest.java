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

public class FeedValidatorTest 
  extends BaseParserTestCase { //extends TestCase {

  private static IRI baseURI = null;
  
  private static <T extends Element> Document<T> get(IRI uri) {
    try {
      //return Parser.INSTANCE.parse(uri.toURL().openStream(), uri);
      return parse(uri);
    } catch (Exception e) {}
    return null;
  }
  

  @Override
  protected void setUp() throws Exception {
    baseURI = new IRI("http://feedvalidator.org/testcases/atom/");
    super.setUp();
  }


  public static void testSection11BriefNoError() throws Exception {
    
    // http://feedvalidator.org/testcases/atom/1.1/brief-noerror.xml
    IRI uri = baseURI.resolve("1.1/brief-noerror.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    Text title = feed.getTitleElement();
    assertNotNull(title);
    assertEquals(title.getTextType(), Text.Type.TEXT);
    String value = title.getValue();
    assertNotNull(value);
    assertEquals(value, "Example Feed");
    List<Link> links = feed.getLinks();
    assertEquals(1, links.size());
    for (Link link : links) {
      assertNull(link.getRel()); // it's an alternate link
      assertEquals(link.getHref(), new IRI("http://example.org/"));
      assertNull(link.getHrefLang());
      assertNull(link.getMimeType());
      assertNull(link.getTitle());
      assertEquals(link.getLength(),-1);
    }
    links = feed.getLinks(Link.REL_ALTERNATE);
    assertEquals(links.size(),1);
    links = feed.getLinks(Link.REL_RELATED);
    assertEquals(links.size(),0);
    assertNotNull(feed.getUpdatedElement());
    DateTime dte = feed.getUpdatedElement();
    AtomDate dt = dte.getValue();
    assertNotNull(dt);
    Calendar c = dt.getCalendar();
    AtomDate cdt = new AtomDate(c);
    assertEquals(dt.getTime(), cdt.getTime());
    Person person = feed.getAuthor();
    assertNotNull(person);
    assertEquals(person.getName(), "John Doe");
    assertNull(person.getEmail());
    assertNull(person.getUri());
    IRIElement id = feed.getIdElement();
    assertNotNull(id);
    assertEquals(id.getValue(), new IRI("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"));
    List<Entry> entries = feed.getEntries();
    assertEquals(entries.size(), 1);
    for (Entry entry : entries) {
      title = entry.getTitleElement();
      assertNotNull(title);
      assertEquals(title.getTextType(), Text.Type.TEXT);
      value = title.getValue();
      assertEquals(value, "Atom-Powered Robots Run Amok");
      links = entry.getLinks();
      assertEquals(1, links.size());
      for (Link link : links) {
        assertNull(link.getRel()); // it's an alternate link
        assertEquals(link.getHref(), new IRI("http://example.org/2003/12/13/atom03"));
        assertNull(link.getHrefLang());
        assertNull(link.getMimeType());
        assertNull(link.getTitle());
        assertEquals(link.getLength(),-1);
      }
      links = entry.getLinks(Link.REL_ALTERNATE);
      assertEquals(links.size(),1);
      links = entry.getLinks(Link.REL_RELATED);
      assertEquals(links.size(),0);
      id = entry.getIdElement();
      assertNotNull(id);
      assertEquals(id.getValue(), new IRI("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"));
      assertNotNull(entry.getUpdatedElement());
      dte = entry.getUpdatedElement();
      dt = dte.getValue();
      assertNotNull(dt);
      c = dt.getCalendar();
      cdt = new AtomDate(c);
      assertEquals(dt.getTime(), cdt.getTime());
      Text summary = entry.getSummaryElement();
      assertNotNull(summary);
      assertEquals(summary.getTextType(), Text.Type.TEXT);
      value = summary.getValue();
      assertEquals(value, "Some text.");
    }
  }
  
  public static void testSection11ExtensiveNoError() throws Exception {
    
    //http://feedvalidator.org/testcases/atom/1.1/extensive-noerror.xml
    IRI uri = baseURI.resolve("1.1/extensive-noerror.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertNotNull(feed.getTitleElement());
    assertEquals(feed.getTitleElement().getTextType(), Text.Type.TEXT);
    assertEquals(feed.getTitleElement().getValue(), "dive into mark");
    assertNotNull(feed.getSubtitleElement());
    assertEquals(feed.getTitleElement().getTextType(), Text.Type.TEXT);
    assertNotNull(feed.getSubtitleElement().getValue());
    assertNotNull(feed.getUpdatedElement());
    assertNotNull(feed.getUpdatedElement().getValue());
    assertNotNull(feed.getUpdatedElement().getValue().getDate());
    assertNotNull(feed.getIdElement());
    assertTrue(feed.getIdElement() instanceof IRIElement);
    assertEquals(feed.getIdElement().getValue(), new IRI("tag:example.org,2003:3"));
    List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
    assertEquals(links.size(), 1);
    for (Link link : links) {
      assertEquals(link.getRel(), "alternate");
      assertEquals(link.getMimeType().toString(), "text/html");
      assertEquals(link.getHrefLang(), "en");
      assertEquals(link.getHref(), new IRI("http://example.org/"));
      assertNull(link.getTitle());
      assertEquals(link.getLength(),-1);
    }
    links = feed.getLinks(Link.REL_SELF);
    assertEquals(links.size(), 1);
    for (Link link : links) {
      assertEquals(link.getRel(), "self");
      assertEquals(link.getMimeType().toString(), "application/atom+xml");
      assertEquals(link.getHref(), new IRI("http://example.org/feed.atom"));
      assertNull(link.getHrefLang());
      assertNull(link.getTitle());
      assertEquals(link.getLength(),-1);
    }
    assertNotNull(feed.getRightsElement());
    assertEquals(feed.getRightsElement().getTextType(), Text.Type.TEXT);
    assertEquals(feed.getRightsElement().getValue(), "Copyright (c) 2003, Mark Pilgrim");
    assertNotNull(feed.getGenerator());
    Generator generator = feed.getGenerator();
    assertEquals(generator.getUri(), new IRI("http://www.example.com/"));
    assertEquals(generator.getVersion(), "1.0");
    assertNotNull(generator.getText());
    assertEquals(generator.getText().trim(), "Example Toolkit");
    List<Entry> entries = feed.getEntries();
    assertNotNull(entries);
    assertEquals(entries.size(), 1);
    for (Entry entry : entries) {
      assertNotNull(entry.getTitleElement());
      assertEquals(entry.getTitleElement().getTextType(), Text.Type.TEXT);
      assertEquals(entry.getTitleElement().getValue(), "Atom draft-07 snapshot");
      links = entry.getLinks(Link.REL_ALTERNATE);
      assertEquals(links.size(), 1);
      for (Link link : links) {
        assertEquals(link.getRel(), "alternate");
        assertEquals(link.getMimeType().toString(), "text/html");
        assertEquals(link.getHref(), new IRI("http://example.org/2005/04/02/atom"));
        assertNull(link.getHrefLang());
        assertNull(link.getTitle());
        assertEquals(link.getLength(),-1);
      }
      links = entry.getLinks(Link.REL_ENCLOSURE);
      assertEquals(links.size(), 1);
      for (Link link : links) {
        assertEquals(link.getRel(), "enclosure");
        assertEquals(link.getMimeType().toString(), "audio/mpeg");
        assertEquals(link.getHref(), new IRI("http://example.org/audio/ph34r_my_podcast.mp3"));
        assertEquals(link.getLength(),1337);
        assertNull(link.getHrefLang());
        assertNull(link.getTitle());
      }
      assertNotNull(entry.getIdElement());
      assertEquals(entry.getIdElement().getValue(), new IRI("tag:example.org,2003:3.2397"));
      assertNotNull(entry.getUpdatedElement());
      assertNotNull(entry.getPublishedElement());
      Person person = entry.getAuthor();
      assertNotNull(person);
      assertEquals(person.getName(),"Mark Pilgrim");
      assertEquals(person.getEmail(), "f8dy@example.com");
      assertNotNull(person.getUriElement());
      assertEquals(person.getUriElement().getValue(), new IRI("http://example.org/"));
      List<Person> contributors = entry.getContributors();
      assertNotNull(contributors);
      assertEquals(contributors.size(),2);
      assertNotNull(entry.getContentElement());
      assertEquals(entry.getContentElement().getContentType(), Content.Type.XHTML);
      assertEquals(entry.getContentElement().getLanguage(), "en");
      assertEquals(entry.getContentElement().getBaseUri(), new IRI("http://diveintomark.org/"));
    }
  }
  
  public static void testSection12MissingNamespace() throws Exception {
    //http://feedvalidator.org/testcases/atom/1.2/missing-namespace.xml
    IRI uri = baseURI.resolve("1.2/missing-namespace.xml");
    Document doc = null;
    doc = get(uri);
    assertNotNull(doc);
    assertFalse(doc.getRoot() instanceof Feed);
  }
  
  public static void testSection12PrefixedNamespace() throws Exception {
    //http://feedvalidator.org/testcases/atom/1.2/prefixed-namespace.xml
    IRI uri = baseURI.resolve("1.2/prefixed-namespace.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assert(feed.getQName().getPrefix().equals("atom"));
  }
  
  public static void testSection12WrongNamespaceCase() throws Exception {
    //http://feedvalidator.org/testcases/atom/1.2/wrong-namespace-case.xml
    IRI uri = baseURI.resolve("1.2/wrong-namespace-case.xml");
    Document doc = null;
    doc = get(uri);
    assertNotNull(doc);
    assertFalse(doc.getRoot() instanceof Feed);
  }

  public static void testSection12WrongNamespace() throws Exception {
    //http://feedvalidator.org/testcases/atom/1.2/wrong-namespace.xml
    IRI uri = baseURI.resolve("1.2/wrong-namespace.xml");
    Document doc = null;
    doc = get(uri);
    assertNotNull(doc);
    assertFalse(doc.getRoot() instanceof Feed);
  }
  
  public static void testSection2BriefEntry() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/brief-entry-noerror.xml
    IRI uri = baseURI.resolve("2/brief-entry-noerror.xml");
    Document<Entry> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot();
    assertNotNull(entry);
    assertNotNull(entry.getTitleElement());
    assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(), 1);
    assertNotNull(entry.getIdElement());
    assertNotNull(entry.getIdElement().getValue());
    assertNotNull(entry.getUpdatedElement());
    assertNotNull(entry.getUpdatedElement().getValue());
    assertNotNull(entry.getUpdatedElement().getValue().getDate());
    assertNotNull(entry.getSummaryElement());
    assertEquals(entry.getSummaryElement().getTextType(), Text.Type.TEXT);
    assertNotNull(entry.getAuthor());
    assertEquals(entry.getAuthor().getName(), "John Doe");
  }
  
  public static void testSection2InfosetAttrOrder() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/infoset-attr-order.xml
    IRI uri = baseURI.resolve("2/infoset-attr-order.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
    assertEquals(links.size(),2);
    for (Link link : links) {
      assertEquals(link.getRel(), "alternate");
      assertNotNull(link.getHref());
    }
  }
  
  public static void testSection2InfosetCDATA() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/infoset-cdata.xml
    IRI uri = baseURI.resolve("2/infoset-cdata.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text summary = entry.getSummaryElement();
      assertNotNull(summary);
      assertEquals(summary.getTextType(), Text.Type.TEXT);
      String value = summary.getValue();
      assertEquals(value, "Some <b>bold</b> text.");
    }
  }
  
  @SuppressWarnings("deprecation")
  public static void testSection2InfosetCharRef() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/infoset-char-ref.xml
    IRI uri = baseURI.resolve("2/infoset-char-ref.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      DateTime updated = entry.getUpdatedElement();
      assertNotNull(updated);
      assertNotNull(updated.getValue());
      assertNotNull(updated.getValue().getDate());
      assertEquals(updated.getValue().getDate().getYear(), 103);
    }
  }
  
  public static void testSection2InfosetElementWhitespace() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/infoset-element-whitespace.xml
    IRI uri = baseURI.resolve("2/infoset-element-whitespace.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    Link link = feed.getAlternateLink();
    assertEquals(link.getResolvedHref(), new IRI("http://example.org/"));
    // the feed has a second alternate link that we will ignore
  }
  
  public static void testSection2InfosetEmpty1() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/infoset-empty1.xml
    IRI uri = baseURI.resolve("2/infoset-empty1.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    Entry entry = feed.getEntries().get(0);
    assertEquals(entry.getTitle(),"");
  }
  
  public static void testSection2InfosetEmpty2() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/infoset-empty2.xml
    IRI uri = baseURI.resolve("2/infoset-empty2.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    Entry entry = feed.getEntries().get(0);
    assertEquals(entry.getTitle(),"");
  }
  
  public static void testSection2InfosetSingleQuote() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/infoset-quote-single.xml
    IRI uri = baseURI.resolve("2/infoset-quote-single.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getAlternateLink().getResolvedHref(), new IRI("http://example.org/"));
  }
  
  public static void testSection2InvalidXmlBase() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/invalid-xml-base.xml
    IRI uri = baseURI.resolve("2/invalid-xml-base.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    try {
      feed.getBaseUri();
    } catch (Exception e) {
      assertTrue(e instanceof IRISyntaxException);
    }
  }
  
  public static void testSection2InvalidXmlLang() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/invalid-xml-lang.xml
    IRI uri = baseURI.resolve("2/invalid-xml-lang.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertFalse(java.util.Locale.US.equals(doc.getRoot().getLocale()));
  }
  
  public static void testSection2Iri() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/iri.xml
    IRI uri = baseURI.resolve("2/iri.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertNotNull(feed.getIdElement().getValue());
    assertNotNull(feed.getAuthor().getUriElement().getValue());
    assertNotNull(feed.getAuthor().getUriElement().getValue().toASCIIString());
  }
  
  public static void testSection2XmlBaseAmbiguous() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/xml-base-ambiguous.xml
    IRI uri = baseURI.resolve("2/xml-base-ambiguous.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getAlternateLink().getResolvedHref(), new IRI("http://example.org/"));
  }
  
  public static void testSection2XmlBaseElemEqDoc() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/xml-base-elem-eq-doc.xml
    IRI uri = baseURI.resolve("2/xml-base-elem-eq-doc.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getAlternateLink().getResolvedHref(), new IRI("http://www.feedvalidator.org/2003/12/13/atom03"));
  }
  
  public static void testSection2XmlBaseElemNeDoc() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/xml-base-elem-ne-doc.xml
    IRI uri = baseURI.resolve("2/xml-base-elem-ne-doc.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getSelfLink().getResolvedHref(),new IRI("http://www.feedvalidator.org/testcases/atom/2/xml-base-elem-ne-doc.xml"));
  }
  
  public static void xtestSection2XmlBase() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/xml-base.xml
    IRI uri = baseURI.resolve("2/xml-base.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Link> links = feed.getLinks();
    for (Link link : links) {
      assertEquals(link.getResolvedHref(), new IRI("http://example.org/index.html"));
    }
  }
  
  public static void testSection2XmlLangBlank() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/xml-lang-blank.xml
    IRI uri = baseURI.resolve("2/xml-lang-blank.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertNull(doc.getRoot().getLocale());
  }
  
  public static void testSection2XmlLang() throws Exception {
    //http://feedvalidator.org/testcases/atom/2/xml-lang.xml
    IRI uri = baseURI.resolve("2/xml-lang.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertEquals(feed.getLanguage(), "en-us");
    assertTrue(feed.getLocale().equals(java.util.Locale.US));
  }
  
  public static void testSection3WsAuthorUri() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-author-uri.xml
    IRI uri = baseURI.resolve("3/ws-author-uri.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    Person author = feed.getAuthor();
    try {
      author.getUriElement().getValue();
    } catch (Exception e) {
      assertTrue(e instanceof IRISyntaxException);
    }
  }
  
  public static void testSection3WsCategoryScheme() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-category-scheme.xml
    IRI uri = baseURI.resolve("3/ws-category-scheme.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection3WsContentSrc() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-content-src.xml
    IRI uri = baseURI.resolve("3/ws-content-src.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection3WsEntryId() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-entry-id.xml
    IRI uri = baseURI.resolve("3/ws-entry-id.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection3WsEntryPublished() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-entry-published.xml
    IRI uri = baseURI.resolve("3/ws-entry-published.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection3WsEntryUpdated() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-entry-updated.xml
    IRI uri = baseURI.resolve("3/ws-entry-updated.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection3WsFeedIcon() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-feed-icon.xml
    IRI uri = baseURI.resolve("3/ws-feed-icon.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getIconElement().getValue();
    } catch (Exception e) {
      assertTrue(e instanceof IRISyntaxException);
    }
  }

  public static void testSection3WsFeedId() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-feed-id.xml
    IRI uri = baseURI.resolve("3/ws-feed-id.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getIdElement().getValue();
    } catch (Exception e) {
      assertTrue(e instanceof IRISyntaxException);
    }
  }
  
  public static void testSection3WsFeedLogo() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-feed-logo.xml
    IRI uri = baseURI.resolve("3/ws-feed-logo.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getLogoElement().getValue();
    } catch (Exception e) {
      assertTrue(e instanceof IRISyntaxException);
    }
  }
  
  public static void testSection3WsFeedUpdated() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-feed-updated.xml
    IRI uri = baseURI.resolve("3/ws-feed-updated.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getUpdatedElement().getValue();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }  
  
  public static void testSection3WsGeneratorUri() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-generator-uri.xml
    IRI uri = baseURI.resolve("3/ws-generator-uri.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection3WsLinkHref() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-link-href.xml
    IRI uri = baseURI.resolve("3/ws-link-href.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection3WsLinkRel() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-link-rel.xml
    IRI uri = baseURI.resolve("3/ws-link-rel.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertNull(doc.getRoot().getAlternateLink());
  }
  
  public static void testSection3WsXmlBase() throws Exception {
    //http://feedvalidator.org/testcases/atom/3/ws-xml-base.xml
    IRI uri = baseURI.resolve("3/ws-xml-base.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection311SummaryTypeMime() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1/summary_type_mime.xml
    IRI uri = baseURI.resolve("3.1.1/summary_type_mime.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getEntries();
    } catch (Exception e) {
      assertTrue(e instanceof OMException);
    }
  }
  
  public static void testSection3111EscapedText() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.1/escaped_text.xml
    IRI uri = baseURI.resolve("3.1.1.1/escaped_text.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text text = entry.getSummaryElement();
      assertNotNull(text);
      assertEquals(text.getTextType(), Text.Type.TEXT);
      String value = text.getValue();
      assertEquals(value, "Some&nbsp;escaped&nbsp;html");
    }
  }
  
  public static void testSection3111ExampleTextTitle() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.1/example_text_title.xml
    IRI uri = baseURI.resolve("3.1.1.1/example_text_title.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text title = entry.getTitleElement();
      assertNotNull(title);
      assertEquals(title.getTextType(), Text.Type.TEXT);
      String value = title.getValue();
      assertEquals(value.trim(), "Less: <");
    }
  }
  
  public static void testSection3111SummaryTypeMime() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.1/summary_type_mime.xml
    IRI uri = baseURI.resolve("3.1.1.1/summary_type_mime.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getEntries();
    } catch (Exception e) {
      assertTrue(e instanceof OMException);
    }
  }
  
  public static void testSection3112ExampleHtmlTitle() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.2/example_html_title.xml
    IRI uri = baseURI.resolve("3.1.1.2/example_html_title.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text title = entry.getTitleElement();
      assertEquals(title.getTextType(), Text.Type.HTML);
      String value = title.getValue();
      assertEquals(value.trim(), "Less: <em> &lt; </em>");
    }
  }
  
  public static void testSection3112InvalidHtml() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.2/invalid_html.xml
    IRI uri = baseURI.resolve("3.1.1.2/invalid_html.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getSummary().trim(), "<a");
  }
  
  public static void testSection3112TextWithEscapedHtml() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.2/text_with_escaped_html.xml
    IRI uri = baseURI.resolve("3.1.1.2/text_with_escaped_html.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getSummary().trim(), "So I was reading <a href=\"http://example.com/\">example.com</a> the other day, it's really interesting.");
  }
  
  public static void testSection3112ValidHtml() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.2/valid_html.xml
    IRI uri = baseURI.resolve("3.1.1.2/valid_html.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getSummary().trim(), "<h3>Heading</h3>");

  }
  
  public static void testSection3113ExampleXhtmlSummary1() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.3/example_xhtml_summary1.xml
    IRI uri = baseURI.resolve("3.1.1.3/example_xhtml_summary1.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text summary = entry.getSummaryElement();
      assertNotNull(summary);
      assertEquals(summary.getTextType(), Text.Type.XHTML);
      Div div = summary.getValueElement();
      assertNotNull(div);
    }
  }
  
  public static void testSection3113ExampleXhtmlSummary2() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.3/example_xhtml_summary2.xml
    IRI uri = baseURI.resolve("3.1.1.3/example_xhtml_summary2.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text summary = entry.getSummaryElement();
      assertNotNull(summary);
      assertEquals(summary.getTextType(), Text.Type.XHTML);
      Div div = summary.getValueElement();
      assertNotNull(div);
    }
  }
  
  public static void testSection3113ExampleXhtmlSummary3() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.3/example_xhtml_summary3.xml
    IRI uri = baseURI.resolve("3.1.1.3/example_xhtml_summary3.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text summary = entry.getSummaryElement();
      assertNotNull(summary);
      assertEquals(summary.getTextType(), Text.Type.XHTML);
      Div div = summary.getValueElement();
      assertNotNull(div);
    }
  }  
  
  public static void testSection3113MissingXhtmlDiv() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.3/missing_xhtml_div.xml
    IRI uri = baseURI.resolve("3.1.1.3/missing_xhtml_div.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Text summary = entry.getSummaryElement();
      assertNotNull(summary);
      assertEquals(summary.getTextType(), Text.Type.XHTML);
      Div div = summary.getValueElement();
      assertNull(div);
    }
  }
  
  public static void testSection3113XhtmlNamedEntity() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.1.1.3/xhtml_named_entity.xml
    IRI uri = baseURI.resolve("3.1.1.3/xhtml_named_entity.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getEntries();
    } catch (Exception e) {
      assertTrue(e instanceof OMException);
    }    
  }
  
  public static void testSection321ContainsEmail() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.1/contains-email.xml
    //Note: not validating input right now
  }
  
  public static void testSection321MultipleNames() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.1/multiple-names.xml
    IRI uri = baseURI.resolve("3.2.1/multiple-names.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getContributors().get(0).getName(),"George Washington");
  }
  
  public static void testSection321NoName() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.1/no-name.xml
    IRI uri = baseURI.resolve("3.2.1/no-name.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertNull(doc.getRoot().getContributors().get(0).getName());
  }
  
  public static void testSection322InvalidUri() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.2/invalid-uri.xml
    IRI uri = baseURI.resolve("3.2.2/invalid-uri.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection322MultipleUris() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.2/multiple-uris.xml
    IRI uri = baseURI.resolve("3.2.2/multiple-uris.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getContributors().get(0).getUri(), new IRI("http://example.com/~jane/"));
  }
  
  public static void testSection322RelativeRef() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.2/relative-ref.xml
    IRI uri = baseURI.resolve("3.2.2/relative-ref.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Person> contr = feed.getContributors();
    for (Person person : contr) {
      assertEquals(person.getUriElement().getValue(), new IRI("~jane/"));
      assertEquals(person.getUriElement().getResolvedValue(), uri.resolve("~jane/"));
    }
  }
  
  public static void testSection323EmailRss20Style() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.3/email-rss20-style.xml
    IRI uri = baseURI.resolve("3.2.3/email-rss20-style.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection323EmailWithName() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.3/email-with-name.xml
    IRI uri = baseURI.resolve("3.2.3/email-with-name.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection323EmailWithPlus() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.3/email-with-plus.xml
    IRI uri = baseURI.resolve("3.2.3/email-with-plus.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Person> contr = feed.getContributors();
    for (Person person : contr) {
      new IRI(person.getEmail()); 
    }
  }

  public static void testSection323InvalidEmail() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.3/invalid-email.xml
    IRI uri = baseURI.resolve("3.2.3/invalid-email.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection323MultipleEmails() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.2.3/multiple-emails.xml
    IRI uri = baseURI.resolve("3.2.3/multiple-emails.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getContributors().get(0).getEmail(), "jane@example.com");
  }
  
  public static void testSection33DuplicateUpdated() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/duplicate-updated.xml
    IRI uri = baseURI.resolve("3.3/duplicate-updated.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-12-13T18:30:02Z");
    for (Entry entry : doc.getRoot().getEntries()) {
      Date date = entry.getUpdated();
      assertEquals(date,d);
    }
  }
  
  public static void testSection33LowercaseUpdated() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/lowercase-updated.xml
    IRI uri = baseURI.resolve("3.3/lowercase-updated.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    try {
      feed.getUpdatedElement().getValue();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }
  
  public static void testSection33PublishedBadDay() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_bad_day.xml
    IRI uri = baseURI.resolve("3.3/published_bad_day.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-07-32T15:51:30-05:00");
    assertEquals(doc.getRoot().getEntries().get(0).getPublished(),d);
  }
  
  public static void testSection33PublishedBadDay2() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_bad_day2.xml
    IRI uri = baseURI.resolve("3.3/published_bad_day2.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    // this is an invalid date, but we don't care because we're not doing
    // validation.  Better run those feeds through the feed validator :-)
    Date d = AtomDate.parse("2003-06-31T15:51:30-05:00");
    assertEquals(doc.getRoot().getEntries().get(0).getPublished(),d);
  }
  
  public static void testSection33PublishedBadHours() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_bad_hours.xml
    IRI uri = baseURI.resolve("3.3/published_bad_hours.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-07-01T25:51:30-05:00");
    assertEquals(doc.getRoot().getEntries().get(0).getPublished(),d);
  }
  
  public static void testSecton33PublishedBadMinutes() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_bad_minutes.xml
    IRI uri = baseURI.resolve("3.3/published_bad_minutes.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-07-01T01:61:30-05:00");
    assertEquals(doc.getRoot().getEntries().get(0).getPublished(),d);
  }
  
  public static void testSection33PublishedBadMonth() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_bad_month.xml
    IRI uri = baseURI.resolve("3.3/published_bad_month.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-13-01T15:51:30-05:00");
    assertEquals(doc.getRoot().getEntries().get(0).getPublished(),d);
  }
  
  public static void testSection33PublishedBadSeconds() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_bad_seconds.xml
    IRI uri = baseURI.resolve("3.3/published_bad_seconds.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-07-01T01:55:61-05:00");
    assertEquals(doc.getRoot().getEntries().get(0).getPublished(),d);
  }
  
  public static void testSection33PublishedDateOnly() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_date_only.xml
    IRI uri = baseURI.resolve("3.3/published_date_only.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedExtraSpaces() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces.xml
    IRI uri = baseURI.resolve("3.3/published_extra_spaces.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedExtraSpaces2() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces2.xml
    IRI uri = baseURI.resolve("3.3/published_extra_spaces2.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedExtraSpaces3() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces3.xml
    IRI uri = baseURI.resolve("3.3/published_extra_spaces3.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedExtraSpaces4() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces4.xml
    IRI uri = baseURI.resolve("3.3/published_extra_spaces4.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedExtraSpaces5() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_extra_spaces5.xml
    IRI uri = baseURI.resolve("3.3/published_extra_spaces5.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedFractionalSecond() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_fractional_second.xml
    IRI uri = baseURI.resolve("3.3/published_fractional_second.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      entry.getPublishedElement().getValue();
    }
  }
  
  public static void testSection33PublishedHoursMinutes() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_hours_minutes.xml
    IRI uri = baseURI.resolve("3.3/published_hours_minutes.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedNoColons() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_no_colons.xml
    IRI uri = baseURI.resolve("3.3/published_no_colons.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedNoHyphens() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_no_hyphens.xml
    IRI uri = baseURI.resolve("3.3/published_no_hyphens.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedNoT() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_no_t.xml
    IRI uri = baseURI.resolve("3.3/published_no_t.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedNoTimezoneColon() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_no_timezone_colon.xml
    IRI uri = baseURI.resolve("3.3/published_no_timezone_colon.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedNoYear() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_no_year.xml
    IRI uri = baseURI.resolve("3.3/published_no_year.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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

  public static void testSection33PublishedSeconds() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_seconds.xml
    IRI uri = baseURI.resolve("3.3/published_seconds.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      entry.getPublishedElement().getValue();
    }
  }  
  
  public static void testSection33PublishedUtc() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_utc.xml
    IRI uri = baseURI.resolve("3.3/published_utc.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      entry.getPublishedElement().getValue();
    }
  }
  
  public static void testSection33PublishedWrongFormat() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_wrong_format.xml
    IRI uri = baseURI.resolve("3.3/published_wrong_format.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedYearAndMonth() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_year_and_month.xml
    IRI uri = baseURI.resolve("3.3/published_year_and_month.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33PublishedYearOnly() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/published_year_only.xml
    IRI uri = baseURI.resolve("3.3/published_year_only.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
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
  
  public static void testSection33UpdatedExample2() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/updated-example2.xml
    IRI uri = baseURI.resolve("3.3/updated-example2.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      entry.getUpdatedElement().getValue();
    }
  }
  
  public static void testSection33UpdatedExample3() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/updated-example3.xml
    IRI uri = baseURI.resolve("3.3/updated-example3.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      entry.getUpdatedElement().getValue();
    }
  }  
  
  public static void testSection33UpdatedExample4() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/updated-example4.xml
    IRI uri = baseURI.resolve("3.3/updated-example4.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      entry.getUpdatedElement().getValue();
    }
  }  
  
  public static void testSection33UpdatedFuture() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/updated-future.xml
    IRI uri = baseURI.resolve("3.3/updated-future.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);    
    Date d = AtomDate.parse("2103-12-13T18:30:02Z");
    assertEquals(doc.getRoot().getEntries().get(0).getUpdated(),d);
  }
  
  public static void testSection33UpdatedPast() throws Exception {
    //http://feedvalidator.org/testcases/atom/3.3/updated-past.xml
    IRI uri = baseURI.resolve("3.3/updated-past.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);    
    Date d = AtomDate.parse("0103-12-13T18:30:02Z");
    assertEquals(doc.getRoot().getEntries().get(0).getUpdated(),d);
  }
  
  public static void testSection411AuthorAtEntryOnly() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/author-at-entry-only.xml
    IRI uri = baseURI.resolve("4.1.1/author-at-entry-only.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNotNull(entry.getAuthor());
    }
  }
  
  public static void testSection411AuthorAtFeedAndEntry() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/author-at-feed-and-entry.xml
    IRI uri = baseURI.resolve("4.1.1/author-at-feed-and-entry.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertNotNull(feed.getAuthor());
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNotNull(entry.getAuthor());
    }
  }
  
  public static void testSection411AuthorAtFeedOnly() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/author-at-feed-only.xml
    IRI uri = baseURI.resolve("4.1.1/author-at-feed-only.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertNotNull(feed.getAuthor());
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNull(entry.getAuthor());
    }
  }  
  
  public static void testSection411AuthorlessWithNoEntries() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/authorless-with-no-entries.xml
    IRI uri = baseURI.resolve("4.1.1/authorless-with-no-entries.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertNull(feed.getAuthor());
  }
  
  public static void testSection411AuthorlessWithOneEntry() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/authorless-with-one-entry.xml
    IRI uri = baseURI.resolve("4.1.1/authorless-with-one-entry.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertNull(feed.getAuthor());
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNull(entry.getAuthor());
    }
  }
  
  public static void testSection411DuplicateEntries() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/duplicate-entries.xml
    IRI uri = baseURI.resolve("4.1.1/duplicate-entries.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry e1 = doc.getRoot().getEntries().get(0);
    Entry e2 = doc.getRoot().getEntries().get(1);
    assertEquals(e1.getId(),e2.getId());
    assertEquals(e1.getUpdated(),e2.getUpdated());
  }
  
  public static void testSection411LinkRelFull() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/link-rel-full.xml
    IRI uri = baseURI.resolve("4.1.1/link-rel-full.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Link link = doc.getRoot().getLink("http://xmlns.com/foaf/0.1/");
    assertNotNull(link);
    assertEquals(link.getResolvedHref(), new IRI("http://example.org/foaf"));
  }
  
  public static void testSection411MisplacedMetadata() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/misplaced-metadata.xml
    IRI uri = baseURI.resolve("4.1.1/misplaced-metadata.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getId(),new IRI("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"));
  }
  
  public static void testSection411MissingId() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/missing-id.xml
    IRI uri = baseURI.resolve("4.1.1/missing-id.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertNull(doc.getRoot().getId());
  }
  
  public static void testSection411MissingSelf() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/missing-self.xml
    IRI uri = baseURI.resolve("4.1.1/missing-self.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertNull(doc.getRoot().getSelfLink());
  }  
  
  public static void testSection411MissingTitles() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/missing-titles.xml
    IRI uri = baseURI.resolve("4.1.1/missing-titles.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertNull(doc.getRoot().getTitle());
  }
  
  public static void testSection411MissingUpdated() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/missing-updated.xml
    IRI uri = baseURI.resolve("4.1.1/missing-updated.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertNull(doc.getRoot().getUpdated());
  }
  
  public static void testSection411MultipleAlternatesDiffering() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-alternates-differing.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-alternates-differing.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
    assertEquals(links.size(), 2);
  }
  
  public static void testSection411MultipleAlternatesMatching() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-alternates-matching.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-alternates-matching.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getAlternateLink().getResolvedHref(), new IRI("http://example.org/front-page.html"));
  }
  
  public static void testSection411MultipleAuthors() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-authors.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-authors.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Person> authors = feed.getAuthors();
    assertEquals(authors.size(),2);
  }
  
  public static void testSection411MultipleCategories() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-categories.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-categories.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Category> cats = feed.getCategories();
    assertEquals(cats.size(),2);
  }
  
  public static void testSection411MultipleContributors() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-contributors.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-contributors.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Person> contr = feed.getContributors();
    assertEquals(contr.size(),2);
  }  
  
  public static void testSection411MultipleGenerators() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-generators.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-generators.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Generator g = doc.getRoot().getGenerator();
    assertEquals(g.getResolvedUri(), new IRI("http://www.example.com/"));
    assertEquals(g.getText().trim(), "Example Toolkit");
  }
  
  public static void testSection411MultipleIcons() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-icons.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-icons.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getIcon(),new IRI("http://feedvalidator.org/big.icon"));
  }
  
  public static void testSection411MultipleIds() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-ids.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-ids.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getId(), new IRI("urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"));
  }  
  
  public static void testSection411MultipleLogos() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-logos.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-logos.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getLogo(),new IRI("http://feedvalidator.org/small.jpg"));
  }
  
  public static void testSection411MultipleRelatedMatching() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-related-matching.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-related-matching.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    List<Link> links = doc.getRoot().getLinks("related");
    assertEquals(links.size(),2);
    assertEquals(links.get(0).getResolvedHref(), new IRI("http://example.org/front-page.html"));
    assertEquals(links.get(1).getResolvedHref(), new IRI("http://example.org/second-page.html"));
  }
  
  public static void testSection411MultipleRights() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-rights.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-rights.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getRights(),"Public Domain");
  }
  
  public static void testSection411MultipleSubtitles() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-subtitles.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-subtitles.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getSubtitle(), "A unique feed, just like all the others");
  }
  
  public static void testSection411MultipleTitles() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-titles.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-titles.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getTitle(), "Example Feed");
  }
  
  public static void testSection411MultipleUpdateds() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/multiple-updateds.xml
    IRI uri = baseURI.resolve("4.1.1/multiple-updateds.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-12-13T18:30:02Z");
    assertEquals(doc.getRoot().getUpdated(), d);
  }
  
  public static void testSection411ZeroEntries() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1/zero-entries.xml
    IRI uri = baseURI.resolve("4.1.1/zero-entries.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    assertEquals(feed.getEntries().size(),0);
  }
  
  public static void testSection4111ContentSrc() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1.1/content-src.xml
    IRI uri = baseURI.resolve("4.1.1.1/content-src.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      Content content = entry.getContentElement();
      assertNotNull(content.getSrc());
    }
  }
  
  public static void testSection4111EmptyContent() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1.1/empty-content.xml
    IRI uri = baseURI.resolve("4.1.1.1/empty-content.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getEntries().get(0).getContent(),"");
  }
  
  public static void testSection4111EmptyTitle() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1.1/empty-title.xml
    IRI uri = baseURI.resolve("4.1.1.1/empty-title.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getEntries().get(0).getTitle(),"");
  }  
  
  public static void testSection4111NoContentOrSummary() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.1.1/no-content-or-summary.xml
    IRI uri = baseURI.resolve("4.1.1.1/no-content-or-summary.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertNull(entry.getContent());
    assertNull(entry.getSummary());
  }
  
  public static void testSection412AlternateNoContent() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/alternate-no-content.xml
    IRI uri = baseURI.resolve("4.1.2/alternate-no-content.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(), 1);
      assertNotNull(entry.getSummaryElement());
    }
  }
  
  public static void testSection412ContentBase64NoSummary() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/content-base64-no-summary.xml
    IRI uri = baseURI.resolve("4.1.2/content-base64-no-summary.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNull(entry.getSummaryElement());
      assertNotNull(entry.getContentElement());
      Content mediaContent = entry.getContentElement();
      DataHandler dataHandler = mediaContent.getDataHandler();
      InputStream in = (ByteArrayInputStream) dataHandler.getContent();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int n = -1;
      while ((n = in.read()) > -1) { baos.write(n); }
      assertEquals(baos.toString(), "Some more text.");
    }
  }
  
  public static void testSection412ContentNoAlternate() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/content-no-alternate.xml
    IRI uri = baseURI.resolve("4.1.2/content-no-alternate.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(),0);
      assertNotNull(entry.getContentElement());
    }
  }
  
  public static void testSection412ContentSrcNoSummary() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/content-src-no-summary.xml
    IRI uri = baseURI.resolve("4.1.2/content-src-no-summary.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertNull(entry.getSummary());
    assertEquals(entry.getContentElement().getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
  }
  
  public static void testSection412EntrySourceAuthor() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/entry-source-author.xml
    IRI uri = baseURI.resolve("4.1.2/entry-source-author.xml");
    Document<Entry> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot();
    assertNotNull(entry);
    assertNotNull(entry.getSource());
    assertNotNull(entry.getSource().getAuthor());
  }
  
  public static void testSection412LinkFullUri() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/link-full-uri.xml
    IRI uri = baseURI.resolve("4.1.2/link-full-uri.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      List<Link> links = entry.getLinks("http://xmlns.com/foaf/0.1/");
      assertEquals(links.size(),1);
    }
  }
  
  public static void testSection412LinkSameRelDifferentTypes() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-different-types.xml
    IRI uri = baseURI.resolve("4.1.2/link-same-rel-different-types.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      List<Link> links = entry.getLinks(Link.REL_ALTERNATE);
      assertEquals(links.size(),2);
    }
  }
  
  public static void testSection412LinkSameRelTypeDifferentHreflang() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-type-different-hreflang.xml
    IRI uri = baseURI.resolve("4.1.2/link-same-rel-type-different-hreflang.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    List<Link> links = doc.getRoot().getEntries().get(0).getLinks("alternate");
    assertEquals(links.size(),2);
    assertEquals(links.get(0).getHrefLang(), "es-es");
    assertEquals(links.get(1).getHrefLang(), "en-us");
  }
  
  public static void testSection412LinkSameRelTypeHreflang() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-type-hreflang.xml
    IRI uri = baseURI.resolve("4.1.2/link-same-rel-type-hreflang.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getAlternateLink().getResolvedHref(), new IRI("http://example.org/2003/12/13/atom02"));
  }
  
  public static void testSection412LinkSameRelTypeNoHreflang() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/link-same-rel-type-no-hreflang.xml
    IRI uri = baseURI.resolve("4.1.2/link-same-rel-type-no-hreflang.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getAlternateLink().getResolvedHref(), new IRI("http://example.org/2003/12/13/atom02"));
  }
  
  public static void testSection412MissingId() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/missing-id.xml
    IRI uri = baseURI.resolve("4.1.2/missing-id.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertNull(entry.getId());
  }  

  public static void testSection412MissingTitle() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/missing-title.xml
    IRI uri = baseURI.resolve("4.1.2/missing-title.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertNull(entry.getTitle());
  }  
  
  public static void testSection412MissingUpdated() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/missing-updated.xml
    IRI uri = baseURI.resolve("4.1.2/missing-updated.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertNull(entry.getUpdated());
  }  
  
  public static void testSection412MultiEnclosureTest() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multi-enclosure-test.xml
    IRI uri = baseURI.resolve("4.1.2/multi-enclosure-test.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      List<Link> enclosures = entry.getLinks(Link.REL_ENCLOSURE);
      assertEquals(enclosures.size(),2);
    }
  }  
  
  public static void testSection412MultipleCategories() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-categories.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-categories.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      List<Category> cats = entry.getCategories();
      assertEquals(cats.size(),2);
    }
  }   

  public static void testSection412MultipleContents() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-contents.xml
    //Note: not implemented
    IRI uri = baseURI.resolve("4.1.2/multiple-contents.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getContent(), "No big deal");
  }   
  
  public static void testSection412MultipleContributors() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-contributors.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-contributors.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      List<Person> contr = entry.getContributors();
      assertEquals(contr.size(),2);
    }
  }
  
  public static void testSection412MultipleIds() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-ids.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-ids.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    assertEquals(doc.getRoot().getEntries().get(0).getId(), new IRI("urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a"));
  }   
  
  public static void testSection412MultiplePublished() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-published.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-published.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Date d = AtomDate.parse("2003-12-11T11:13:56Z");
    assertEquals(doc.getRoot().getEntries().get(0).getPublished(), d);
  }   
  
  public static void testSection412MultipleRights() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-rights.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-rights.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getRights(), "Public Domain");
  }   
  
  public static void testSection412MultipleSources() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-sources.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-sources.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    Source source = entry.getSource();
    assertEquals(source.getId(), new IRI("urn:uuid:9b056ae0-f778-11d9-8cd6-0800200c9a66"));
  }   
  
  public static void testSection412MultipleSummaries() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-summaries.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-summaries.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getSummary(), "Some text.");
  }   
  
  public static void testSection412MultipleTitles() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-titles.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-titles.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getTitle(), "Atom-Powered Robots Run Amok");
  }   
  
  public static void testSection412MultipleUpdated() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/multiple-updated.xml
    IRI uri = baseURI.resolve("4.1.2/multiple-updated.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    Date d = AtomDate.parse("2003-12-13T18:30:02Z");
    assertEquals(entry.getUpdated(), d);
  }   
  
  public static void testSection412NoContentOrAlternate() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/no-content-or-alternate.xml
    IRI uri = baseURI.resolve("4.1.2/no-content-or-alternate.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertNull(entry.getContent());
    assertNull(entry.getAlternateLink());
  }   
  
  public static void testSection412RelatedSameRelTypeHreflang() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/related-same-rel-type-hreflang.xml
    IRI uri = baseURI.resolve("4.1.2/related-same-rel-type-hreflang.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    List<Link> links = entry.getLinks("related");
    assertEquals(links.size(),2);
    assertEquals(links.get(0).getResolvedHref(), new IRI("http://example.org/2003/12/13/atom02"));
    assertEquals(links.get(1).getResolvedHref(), new IRI("http://example.org/2003/12/13/atom03"));
  }   
      
  public static void testSection412SummaryContentBase64() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/summary-content-base64.xml
    IRI uri = baseURI.resolve("4.1.2/summary-content-base64.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNotNull(entry.getSummaryElement());
      assertEquals(entry.getSummaryElement().getTextType(), Text.Type.TEXT);
      assertNotNull(entry.getContentElement());
      assertEquals(entry.getContentElement().getContentType(), Content.Type.MEDIA);
    }
  }
  
  public static void testSection412SummaryContentSrc() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.2/summary-content-src.xml
    IRI uri = baseURI.resolve("4.1.2/summary-content-src.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNotNull(entry.getSummaryElement());
      assertEquals(entry.getSummaryElement().getTextType(), Text.Type.TEXT);
      assertNotNull(entry.getContentElement());
      assertEquals(entry.getContentElement().getContentType(), Content.Type.MEDIA);
      Content mediaContent = entry.getContentElement();
      assertNotNull(mediaContent.getSrc());
      assertEquals(mediaContent.getMimeType().toString(), "application/pdf");
    }
  }  
  
  public static void testSection4131TypeHtml() throws Exception {
   //http://feedvalidator.org/testcases/atom/4.1.3.1/type-html.xml
    IRI uri = baseURI.resolve("4.1.3.1/type-html.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    
    Feed feed = doc.getRoot();
    assertNotNull(feed);
    List<Entry> entries = feed.getEntries();
    for (Entry entry : entries) {
      assertNotNull(entry.getContentElement());
      assertEquals(entry.getContentElement().getContentType(), Content.Type.HTML);
    }
  }
  
  public static void testSection413TypeMultipartAlternative() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.3.1/type-multipart-alternative.xml
    IRI uri = baseURI.resolve("4.1.3.1/type-multipart-alternative.xml");
    Document<Feed> doc = get(uri);
    assertNotNull(doc);
    Entry entry = doc.getRoot().getEntries().get(0);
    assertEquals(entry.getContentElement().getMimeType().toString(), "multipart/alternative");
  }
  
  public static void testSection4131TypeTextHtml() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.3.1/type-text-html.xml
     IRI uri = baseURI.resolve("4.1.3.1/type-text-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       assertNotNull(entry.getContentElement());
       assertEquals(entry.getContentElement().getContentType(), Content.Type.MEDIA);
       Content mediaContent = entry.getContentElement();
       assertEquals(mediaContent.getMimeType().toString(), "text/html");
     }
   }  
  
  public static void testSection4131TypeText() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.3.1/type-text.xml
     IRI uri = baseURI.resolve("4.1.3.1/type-text.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       assertNotNull(entry.getContentElement());
       assertEquals(entry.getContentElement().getContentType(), Content.Type.TEXT);
     }
   }
  
  public static void testSection4131TypeXhtml() throws Exception {
    //http://feedvalidator.org/testcases/atom/4.1.3.1/type-xhtml.xml
     IRI uri = baseURI.resolve("4.1.3.1/type-xhtml.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       assertNotNull(entry.getContentElement());
       assertEquals(entry.getContentElement().getContentType(), Content.Type.XHTML);
     }
   }
  
   public static void testSection413TypeXml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.1/type-xhtml.xml
     IRI uri = baseURI.resolve("4.1.3.1/type-xml.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     try {
       feed.getEntries();
     } catch (Exception e) {
       assertTrue(e instanceof OMException);
     }
   }
   
   public static void testSection4132ContentSrcExtraChild() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-extra-child.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-extra-child.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Content content = entry.getContentElement();
     assertEquals(content.getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
     assertEquals(entry.getContent().trim(), "extraneous text");
   }
   
   public static void testSection4132ContentSrcExtraText() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-extra-text.xml
     try {
       IRI uri = baseURI.resolve("4.1.3.2/content-src-extra-text.xml");
       Document<Feed> doc = get(uri);
       assertNotNull(doc);
       Entry entry = doc.getRoot().getEntries().get(0);
       Content content = entry.getContentElement();
       assertEquals(content.getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
     } catch (Exception e) {}
   }
   
   public static void testSection4132ContentSrcInvalidIri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-invalid-iri.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-invalid-iri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
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
   
   public static void testSection4132ContentSrcNoTypeNoError() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-no-type-no-error.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-no-type-no-error.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Content content = entry.getContentElement();
     assertEquals(content.getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
     assertNull(content.getMimeType());
   }
   
   public static void testSection4132ContentSrcNoType() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-no-type.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-no-type.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Content content = entry.getContentElement();
     assertEquals(content.getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
     assertNull(content.getMimeType());
   }
   
   public static void testSection4132ContentSrcRelativeRef() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-relative-ref.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-relative-ref.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       Content content = entry.getContentElement();
       assertNotNull(content);
       assertEquals(content.getContentType(), Content.Type.MEDIA);
       assertEquals(content.getResolvedSrc(), uri.resolve("2003/12/12/atom03.pdf"));
     }
   }
   
   public static void testSection4132ContentSrcTypeHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-html.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-type-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContent(),"");
     assertEquals(entry.getContentType(), Content.Type.HTML);
     assertEquals(entry.getContentElement().getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
   }
   
   public static void testSection4132ContentSrcTypeTextHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-text-html.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-type-text-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContent(),"");
     assertEquals(entry.getContentType(), Content.Type.MEDIA);
     assertEquals(entry.getContentElement().getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
   }
   
   public static void testSection4132ContentSrcTypeText() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-text.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-type-text.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContent(),"");
     assertEquals(entry.getContentType(), Content.Type.TEXT);
     assertEquals(entry.getContentElement().getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
   }
   
   public static void testSection4132ContentSrcTypeXhtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.2/content-src-type-xhtml.xml
     IRI uri = baseURI.resolve("4.1.3.2/content-src-type-xhtml.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertNull(entry.getContent());
     assertEquals(entry.getContentType(), Content.Type.XHTML);
     assertEquals(entry.getContentElement().getResolvedSrc(), new IRI("http://example.org/2003/12/13/atom03"));
   }
   
   public static void testSection4133ContentApplicationXhtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-application-xthml.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-application-xthml.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       Content content = entry.getContentElement();
       assertNotNull(content);
       assertEquals(entry.getContentElement().getContentType(), Content.Type.XML);
     }
   }
   
   public static void testSection4133ContentHtmlWithChildren() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-html-with-children.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-html-with-children.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContent(), "Some  text.");
     assertEquals(entry.getContentType(), Content.Type.HTML);
   }
   
   public static void testSection4133ContentJpegInvalidBase64() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-jpeg-invalid-base64.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-jpeg-invalid-base64.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.MEDIA);
     assertEquals(entry.getContent(),"insert image here");
   }
   
   public static void testSection4133ContentJpegValidBase64() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-jpeg-valid-base64.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-jpeg-valid-base64.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.MEDIA);
     DataHandler dh = entry.getContentElement().getDataHandler();
     ByteArrayInputStream in = (ByteArrayInputStream) dh.getContent();
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     int n = -1;
     while ((n = in.read()) != -1) { out.write(n); }
     out.flush();
     assertEquals(out.toByteArray().length,1538);
     assertEquals(dh.getContentType(), "image/jpeg");
   }
   
   public static void testSection4133ContentNoTypeEscapedHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-no-type-escaped-html.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-no-type-escaped-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContent(), "Some <b>bold</b> text.");
     assertEquals(entry.getContentType(), Content.Type.TEXT);
   }
   
   public static void testSection4133ContentNoTypeWithChildren() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-no-type-with-children.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-no-type-with-children.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContent().trim(), "Some  text");
     assertEquals(entry.getContentType(), Content.Type.TEXT);
   }   
   
   public static void testSection4133ContentPlainWithChildren() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-plain-with-children.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-plain-with-children.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContent().trim(), "Some  text.");
     assertEquals(entry.getContentType(), Content.Type.MEDIA);
   }   
   
   public static void testSection4133ContentSvgMixed() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-svg-mixed.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-svg-mixed.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Content content = entry.getContentElement();
     assertNotNull(content.getValueElement()); // we're pretty forgiving
     assertEquals(content.getContentType(), Content.Type.XML);
   }
   
   public static void testSection4133ContentSvg() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-svg.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-svg.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       Content content = entry.getContentElement();
       assertNotNull(content);
       assertEquals(entry.getContentElement().getContentType(), Content.Type.XML);
     }
   }
   
   public static void testSection4133ContentTextHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-text-html.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-text-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.MEDIA);
   }
   
   public static void testSection4133ContentTextWithChildren() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-text-with-children.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-text-with-children.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.TEXT);
     assertEquals(entry.getContent().trim(), "Some  text");
   }
   
   public static void testSection4133ContentXhtmlEscaped() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-escaped.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-escaped.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.XHTML);
     String c = entry.getContent().trim();
     c = c.replaceAll(">", "&gt;");
     assertEquals(c, "Some &lt;b&gt;bold&lt;/b&gt; text.");
   }
   
   public static void testSection4133ContentXhtmlMixed() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-mixed.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-mixed.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.XHTML);
     String c = entry.getContent().trim();
     c = c.replaceAll("Some &lt;b>bold&lt;/b>", "Some &lt;b&gt;bold&lt;/b&gt;");
     assertEquals(c, "<b xmlns=\"http://www.w3.org/1999/xhtml\">Example:</b> Some &lt;b&gt;bold&lt;/b&gt; text.");
   }
   
   public static void testSection4133ContentXhtmlNoXhtmlDiv() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-no-xhtml-div.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-no-xhtml-div.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.XHTML);
     assertNull(entry.getContent());
   }
   
   public static void testSection4133ContentXhtmlNotmarkup() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-notmarkup.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-notmarkup.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.XHTML);
     String c = entry.getContent();
     c = c.replaceAll(">", "&gt;");
     assertEquals(c,"Some &lt;x&gt;bold&lt;/x&gt; text.");
   }
   
   public static void testSection4133ContentXhtmlTextChildren() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.1.3.3/content-xhtml-text-children.xml
     IRI uri = baseURI.resolve("4.1.3.3/content-xhtml-text-children.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getContentType(), Content.Type.XHTML);
     assertNull(entry.getContent());
   }
   
   public static void testSection4221CategoryNoTerm() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.2.1/category-no-term.xml
     IRI uri = baseURI.resolve("4.2.2.1/category-no-term.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     List<Category> cats = entry.getCategories();
     assertEquals(cats.size(),1);
     assertNull(cats.get(0).getTerm());
   }
   
   public static void testSection4222CategoryNoScheme() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.2.2/category-no-scheme.xml
     IRI uri = baseURI.resolve("4.2.2.2/category-no-scheme.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     List<Category> cats = entry.getCategories();
     assertEquals(cats.size(),1);
     assertNull(cats.get(0).getScheme());
   }
   
   public static void testSection4222CategorySchemeInvalidIri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.2.2/category-scheme-invalid-iri.xml
     IRI uri = baseURI.resolve("4.2.2.2/category-scheme-invalid-iri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
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
   
   public static void testSection4222CategorySchemeRelIri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.2.2/category-scheme-rel-iri.xml
     IRI uri = baseURI.resolve("4.2.2.2/category-scheme-rel-iri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Category cat = entry.getCategories().get(0);
     assertEquals(cat.getScheme(), new IRI("mine"));
   }
   
   public static void testSection4223CategoryLabelEscapedHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.2.3/category-label-escaped-html.xml
     IRI uri = baseURI.resolve("4.2.2.3/category-label-escaped-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Category cat = entry.getCategories().get(0);
     assertEquals(cat.getLabel(), "<b>business</b>");
   }
   
   public static void testSection4223CategoryNoLabel() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.2.3/category-no-label.xml
     IRI uri = baseURI.resolve("4.2.2.3/category-no-label.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Category cat = entry.getCategories().get(0);
     assertNull(cat.getLabel());
   }
   
   public static void testSection424GeneratorEscapedHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.4/generator-escaped-html.xml
     IRI uri = baseURI.resolve("4.2.4/generator-escaped-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     Generator generator = feed.getGenerator();
     assertEquals(generator.getText(),"<b>The</b> generator");
   }
   
   public static void testSection424GeneratorInvalidIri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.4/generator-invalid-iri.xml
     IRI uri = baseURI.resolve("4.2.4/generator-invalid-iri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
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
   
   public static void testSection424GeneratorNoText() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.4/generator-no-text.xml
     IRI uri = baseURI.resolve("4.2.4/generator-no-text.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     Generator generator = feed.getGenerator();
     assertEquals(generator.getText(),"");
   }
   
   public static void testSection424GeneratorWithChild() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.4/generator-with-child.xml
     IRI uri = baseURI.resolve("4.2.4/generator-with-child.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     Generator generator = feed.getGenerator();
     assertEquals(generator.getText(),"");
   }
   
   public static void testSection424GeneratorRelativeRef() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.4/generator_relative_ref.xml
     IRI uri = baseURI.resolve("4.2.4/generator_relative_ref.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     Generator generator = feed.getGenerator();
     assertNotNull(generator);
     assertEquals(generator.getResolvedUri(), uri.resolve("misc/Colophon"));
   }
   
   public static void testSection425IconInvalidUri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.5/icon_invalid_uri.xml
     IRI uri = baseURI.resolve("4.2.5/icon_invalid_uri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     assertNotNull(feed.getIconElement());
     try {
       feed.getIconElement().getValue();
     } catch (Exception e) {
       assertTrue(e instanceof IRISyntaxException);
     }
   }
   
   public static void testSection425IconRelativeRef() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.5/icon_relative_ref.xml
     IRI uri = baseURI.resolve("4.2.5/icon_relative_ref.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertNotNull(feed);
     assertNotNull(feed.getIconElement());
     assertEquals(feed.getIconElement().getResolvedValue(), uri.resolve("favicon.ico"));
   }
   
   public static void testSection426IdDotSegments() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-dot-segments.xml
     IRI uri = baseURI.resolve("4.2.6/id-dot-segments.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(), new IRI("http://example.org/./id/1234"));
     assertEquals(IRI.normalize(doc.getRoot().getId()), new IRI("http://example.org/id/1234"));
   }
   
   public static void testSection426IdEmptyFragmentId() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-empty-fragment-id.xml
     IRI uri = baseURI.resolve("4.2.6/id-empty-fragment-id.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     feed.getIdElement().getValue();
   }
   
   public static void testSection426IdEmptyPath() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-empty-path.xml
     IRI uri = baseURI.resolve("4.2.6/id-empty-path.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("http://example.org"));
   }
   
   public static void testSection426IdEmptyQuery() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-empty-query.xml
     IRI uri = baseURI.resolve("4.2.6/id-empty-query.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("http://example.org/id/1234?"));
   }
   
   public static void testSection426IdExplicitAuthority() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-explicit-authority.xml
     IRI uri = baseURI.resolve("4.2.6/id-explicit-authority.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("http://:@example.org/id/1234"));
   }
   
   public static void testSection426IdExplicitDefaultPort() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-explicit-default-port.xml
     IRI uri = baseURI.resolve("4.2.6/id-explicit-default-port.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("http://example.org:80/id/1234"));
   }
   
   public static void testSection426IdHostUppercase() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-host-uppercase.xml
     IRI uri = baseURI.resolve("4.2.6/id-host-uppercase.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("http://Example.org/id/1234"));
   }
   
   public static void testSection426IdNotUri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-not-uri.xml
     IRI uri = baseURI.resolve("4.2.6/id-not-uri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     try {
       feed.getIdElement().getValue();
     } catch (Exception e) {
       assertTrue(e instanceof IRISyntaxException);
     }
   }
   
   public static void testSection426IdPercentEncodedLower() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-percent-encoded-lower.xml
     IRI uri = baseURI.resolve("4.2.6/id-percent-encoded-lower.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("http://example.org/id/1234?q=%5c"));
   }
   
   public static void testSection426IdPercentEncoded() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-percent-encoded.xml
     IRI uri = baseURI.resolve("4.2.6/id-percent-encoded.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("http://example.org/%69%64/1234"));     
   }
   
   public static void testSection426IdRelativeUri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-relative-uri.xml
     IRI uri = baseURI.resolve("4.2.6/id-relative-uri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("/id/1234"));
   }
   
   public static void testSection426IdUppercaseScheme() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-uppercase-scheme.xml
     IRI uri = baseURI.resolve("4.2.6/id-uppercase-scheme.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getId(),new IRI("Http://example.org/id/1234"));
   }
   
   public static void testSection426IdValidTagUris() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.6/id-valid-tag-uris.xml
     IRI uri = baseURI.resolve("4.2.6/id-valid-tag-uris.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     // we don't care that they're invalid, at least for now
     assertEquals(doc.getRoot().getId(),new IRI("tag:example.com,2000:"));
   }
   
   public static void testSection4271LinkHrefInvalid() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.1/link-href-invalid.xml
     IRI uri = baseURI.resolve("4.2.7.1/link-href-invalid.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
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
   
   public static void testSection427LinkHrefRelative() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.1/link-href-relative.xml
     IRI uri = baseURI.resolve("4.2.7.1/link-href-relative.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       List<Link> links = entry.getLinks();
       for (Link link : links) {
         assertEquals(link.getResolvedHref(), uri.resolve("/2003/12/13/atom03"));
       }
     }
   }
   
   public static void testSection427LinkNoHref() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.1/link-no-href.xml
     IRI uri = baseURI.resolve("4.2.7.1/link-no-href.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Link link = entry.getLinks().get(0);
     assertNull(link.getHref());
   }
   
   public static void testSection4272AbsoluteRel() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/absolute_rel.xml
     IRI uri = baseURI.resolve("4.2.7.2/absolute_rel.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertEquals(feed.getLinks(Link.REL_ALTERNATE).size(), 1);
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       assertEquals(entry.getLinks(Link.REL_ALTERNATE).size(), 1);
     }
   }
   
   public static void testSection4272EmptyPath() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/empty-path.xml
     IRI uri = baseURI.resolve("4.2.7.2/empty-path.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     List<Link> links = feed.getLinks(Link.REL_ALTERNATE);
     for (Link link : links) {
       assertEquals(link.getResolvedHref(), uri);
     }
   }
   
   public static void testSection4272LinkRelIsegmentNzNc() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-isegment-nz-nc.xml
     IRI uri = baseURI.resolve("4.2.7.2/link-rel-isegment-nz-nc.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertNotNull(entry.getAlternateLink());
   }
   
   public static void testSection4272LinkRelRelative() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-relative.xml
     IRI uri = baseURI.resolve("4.2.7.2/link-rel-relative.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Link link = entry.getLink("/foo");
     assertNotNull(link);  // we don't care that it's invalid
   }
   
   public static void testSection4272LinkRelSelfMatch() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-match.xml
     IRI uri = baseURI.resolve("4.2.7.2/link-rel-self-match.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getSelfLink().getResolvedHref(), new IRI("http://www.feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-match.xml"));
   }
   
   public static void testSection4272LinkRelSelfNoMatch() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-nomatch.xml
     IRI uri = baseURI.resolve("4.2.7.2/link-rel-self-nomatch.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getSelfLink().getResolvedHref(), new IRI("http://www.feedvalidator.org/testcases/atom/4.2.7.2/link-rel-self-match.xml"));
   }
   
   public static void testSection4272SelfVsAlternate() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/self-vs-alternate.xml
     IRI uri = baseURI.resolve("4.2.7.2/self-vs-alternate.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertNull(entry.getAlternateLink());
     Link self = entry.getLink("self");
     assertEquals(self.getMimeType().toString(), "text/html");
   }
   
   public static void testSection4272UnregisteredRel() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.2/unregistered-rel.xml
     IRI uri = baseURI.resolve("4.2.7.2/unregistered-rel.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertNotNull(doc.getRoot().getLink("service.post"));
   }
   
   public static void testSection4273LinkTypeInvalidMime() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.3/link-type-invalid-mime.xml
     IRI uri = baseURI.resolve("4.2.7.3/link-type-invalid-mime.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
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
   
   public static void testSection4273LinkTypeParameters() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.3/link-type-parameters.xml
     IRI uri = baseURI.resolve("4.2.7.3/link-type-parameters.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       List<Link> links = entry.getLinks();
       for (Link link : links) {
         assertEquals(link.getMimeType().getBaseType(), "text/html");
         assertEquals(link.getMimeType().getParameter("charset"), "utf-8");
       }
     }
   }
   
   public static void testSection4274LinkHreflangInvalidLanguage() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.4/link-hreflang-invalid-language.xml
     IRI uri = baseURI.resolve("4.2.7.4/link-hreflang-invalid-language.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Link link = entry.getAlternateLink();
     assertEquals(link.getHrefLang(), "insert language here");
   }
   
   public static void testSection4275LinkTitleWithBadchars() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.5/link-title-with-badchars.xml
     IRI uri = baseURI.resolve("4.2.7.5/link-title-with-badchars.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Link link = entry.getAlternateLink();
     assertEquals(link.getTitle(),"This is a \u00A3\u0093test.\u0094");
   }
   
   public static void testSection4275LinkTitleWithHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.5/link-title-with-html.xml
     IRI uri = baseURI.resolve("4.2.7.5/link-title-with-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Link link = entry.getAlternateLink();
     assertEquals(link.getTitle(),"very, <b>very</b>, scary indeed");
   }
   
   public static void testSection4276LinkLengthNotPositive() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.7.6/link-length-not-positive.xml
     IRI uri = baseURI.resolve("4.2.7.6/link-length-not-positive.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Link link = entry.getAlternateLink();
     assertEquals(link.getLength(),0);
   }
   
   public static void testSection428LogoInvalidUri() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.8/logo-invalid-uri.xml
     IRI uri = baseURI.resolve("4.2.8/logo-invalid-uri.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Feed feed = doc.getRoot();
     assertNotNull(feed.getLogoElement());
     try {
       feed.getLogoElement().getValue();
     } catch (Exception e) {
       assertTrue(e instanceof IRISyntaxException);
     }
   }
   
   public static void testSection428LogoRelativeRef() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.8/logo_relative_ref.xml
     IRI uri = baseURI.resolve("4.2.8/logo_relative_ref.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     assertNotNull(feed.getLogoElement());
     assertEquals(feed.getLogoElement().getResolvedValue(), uri.resolve("atomlogo.png"));
   }
   
   public static void testSection429PublishedInvalidDate() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.9/published-invalid-date.xml
     IRI uri = baseURI.resolve("4.2.9/published-invalid-date.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
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
   
   public static void testSection4210RightsInvalidType() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.10/rights-invalid-type.xml
     try {
       IRI uri = baseURI.resolve("4.2.10/rights-invalid-type.xml");
       Document<Feed> doc = get(uri);
       assertNotNull(doc);
       doc.getRoot().getRights();
     } catch (Exception e) {}
   }
   
   public static void testSection4210RightsTextWithEscapedHtml() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.10/rights-text-with-escaped-html.xml
     IRI uri = baseURI.resolve("4.2.10/rights-text-with-escaped-html.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getRights(),"Copyright &copy; 2005");
   }
   
   public static void testSection4210RightsXhtmlNoXmldiv() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.10/rights-xhtml-no-xmldiv.xml
     IRI uri = baseURI.resolve("4.2.10/rights-xhtml-no-xmldiv.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertNull(doc.getRoot().getRights());
   }
   
   public static void testSection4211MissingId() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/missing-id.xml
     IRI uri = baseURI.resolve("4.2.11/missing-id.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Source source = entry.getSource();
     assertNull(source.getId());
   }
   
   public static void testSection4211MissingTitle() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/missing-title.xml
     IRI uri = baseURI.resolve("4.2.11/missing-title.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Source source = entry.getSource();
     assertNull(source.getTitle());
   }
   
   public static void testSection4211MissingUpdated() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/missing-updated.xml
     IRI uri = baseURI.resolve("4.2.11/missing-updated.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Source source = entry.getSource();
     assertNull(source.getUpdated());
   }
   
   public static void testSection4211MultipleAlternatesDiffering() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-alternates-differing.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-alternates-differing.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Source source = entry.getSource();
     List<Link> links = source.getLinks("alternate");
     assertEquals(links.size(),2);
     assertEquals(links.get(0).getResolvedHref(), new IRI("http://example.org/"));
     assertEquals(links.get(1).getResolvedHref(), new IRI("http://example.es/"));
   }
   
   public static void testSection4211MultipleAlternatesMatching() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-alternates-matching.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-alternates-matching.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Source source = entry.getSource();
     assertEquals(source.getAlternateLink().getResolvedHref(), new IRI("http://example.org/front-page.html"));
   }   
   
   public static void testSection4211MultipleAuthors() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-authors.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-authors.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       Source source = entry.getSource();
       assertNotNull(source);
       assertEquals(source.getAuthors().size(),2);
     }
   }
   
   public static void testSection4211MultipleCategories() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-categories.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-categories.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       Source source = entry.getSource();
       assertNotNull(source);
       assertEquals(source.getCategories().size(),2);
     }
   }   
   
   public static void testSection4211MultipleContributors() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-contributors.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-contributors.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     
     Feed feed = doc.getRoot();
     List<Entry> entries = feed.getEntries();
     for (Entry entry : entries) {
       Source source = entry.getSource();
       assertNotNull(source);
       assertEquals(source.getContributors().size(),2);
     }
   }
   
   public static void testSection4211MultipleGenerators() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-generators.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-generators.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Generator g = entry.getSource().getGenerator();
     assertEquals(g.getResolvedUri(), new IRI("http://www.example.com/"));
   }
   
   public static void testSection4211MultipleIcons() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-icons.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-icons.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getSource().getIcon(), new IRI("http://feedvalidator.org/big.icon"));
   }
   
   public static void testSection4211MultipleIds() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-ids.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-ids.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getSource().getId(), new IRI("urn:uuid:28213c50-f84c-11d9-8cd6-0800200c9a66"));
   }   
   
   public static void testSection4211MultipleLogos() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-logos.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-logos.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getSource().getLogo(), new IRI("http://feedvalidator.org/small.jpg"));
   }
   
   public static void testSection4211MultipleRights() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-rights.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-rights.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getSource().getRights().trim(), "Public Domain");
   }
   
   public static void testSection4211MultipleSubtitles() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-subtitles.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-subtitles.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getSource().getSubtitle().trim(), "A unique feed, just like all the others");
   }
   
   public static void testSection4211MultipleTitles() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-titles.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-titles.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     assertEquals(entry.getSource().getTitle().trim(), "Source of all knowledge");
   }
   
   public static void testSection4211MultipleUpdateds() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/multiple-updateds.xml
     IRI uri = baseURI.resolve("4.2.11/multiple-updateds.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Date d = AtomDate.parse("2003-12-13T17:46:27Z");
     assertEquals(entry.getSource().getUpdated(), d);
   }
   
   public static void testSection4211SourceEntry() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.11/source-entry.xml
     IRI uri = baseURI.resolve("4.2.11/source-entry.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     Entry entry = doc.getRoot().getEntries().get(0);
     Source source = entry.getSource();
     assertNotNull(source);
   }
   
   public static void testSection4212SubtitleBlank() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.12/subtitle-blank.xml
     IRI uri = baseURI.resolve("4.2.12/subtitle-blank.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getSubtitle(),"");
   }
   
   public static void testSection4214TitleBlank() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.14/title-blank.xml
     IRI uri = baseURI.resolve("4.2.14/title-blank.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
     assertEquals(doc.getRoot().getTitle(),"");
   }
   
   public static void testSection4215UpdatedInvalidDate() throws Exception {
     //http://feedvalidator.org/testcases/atom/4.2.15/updated-invalid-date.xml
     IRI uri = baseURI.resolve("4.2.15/updated-invalid-date.xml");
     Document<Feed> doc = get(uri);
     assertNotNull(doc);
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
