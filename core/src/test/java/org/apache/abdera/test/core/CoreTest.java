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
package org.apache.abdera.test.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.lang.Lang;
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
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.util.AbderaConfiguration;
import org.apache.abdera.util.CompressionUtil;
import org.apache.abdera.util.Configuration;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.URIHelper;
import org.apache.abdera.util.XmlRestrictedCharReader;
import org.apache.abdera.writer.WriterOptions;

public class CoreTest extends TestCase {

  public static void testDefaultConfigurationProperties() {
    Configuration config = new AbderaConfiguration();
    assertEquals(
      config.getDefaultFactory(), 
      "org.apache.abdera.parser.stax.FOMFactory");
    assertEquals(
      config.getDefaultParser(),
      "org.apache.abdera.parser.stax.FOMParser");
    assertEquals(
      config.getDefaultXPath(),
      "org.apache.abdera.parser.stax.FOMXPath");
  }
  
  public static void testUriNormalization() {
    try {
      assertEquals(
        URIHelper.normalize(
          "HTTP://www.EXAMPLE.org:80/foo/../Bar/%3f/./foo/."), 
        "http://www.example.org/Bar/%3F/foo/");
      assertEquals(
        URIHelper.normalize(
          "HTTPs://www.EXAMPLE.org:443/foo/../Bar/%3f/./foo/."), 
        "https://www.example.org/Bar/%3F/foo/");
      assertEquals(
        URIHelper.normalize(
          "HTTP://www.EXAMPLE.org:81/foo/../Bar/%3f/./foo/."), 
        "http://www.example.org:81/Bar/%3F/foo/");
      assertEquals(
        URIHelper.normalize(
          "HTTPs://www.EXAMPLE.org:444/foo/../Bar/%3f/./foo/."), 
        "https://www.example.org:444/Bar/%3F/foo/");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void testAtomDate() {
    Date now = new Date();
    AtomDate atomNow = AtomDate.valueOf(now);
    String rfc3339 = atomNow.getValue();
    atomNow = AtomDate.valueOf(rfc3339);
    Date parsed = atomNow.getDate();
    assertEquals(now, parsed);
  }
  
  public static void testXmlRestrictedCharReader() throws Exception {
    String s = "\u0001abcdefghijklmnop\u0002";
    StringReader r = new StringReader(s);
    XmlRestrictedCharReader x = new XmlRestrictedCharReader(r);
    char[] chars = new char[s.length()+1];
    int n = x.read(chars);  // the first and last characters should never show up
    assertEquals(s.length()-2,n);
    assertEquals("abcdefghijklmnop",new String(chars,0,n));
  }
  
  public static void testMimeTypeHelper() throws Exception {
    assertTrue(MimeTypeHelper.isApp("application/atomsvc+xml"));
    assertFalse(MimeTypeHelper.isApp("application/atomserv+xml"));
    assertTrue(MimeTypeHelper.isAtom("application/atom+xml"));
    assertTrue(MimeTypeHelper.isAtom("application/atom+xml;type=\"entry\""));
    assertTrue(MimeTypeHelper.isAtom("application/atom+xml;type=\"feed\""));
    assertTrue(MimeTypeHelper.isEntry("application/atom+xml;type=\"entry\""));
    assertTrue(MimeTypeHelper.isFeed("application/atom+xml;type=\"feed\""));
    assertTrue(MimeTypeHelper.isText("text/plain"));
    assertTrue(MimeTypeHelper.isXml("application/xml"));
    
    String[] types = MimeTypeHelper.condense("image/png","image/gif","image/png","image/*");
    assertEquals(1, types.length);
    assertEquals("image/*",types[0]);
    
    assertTrue(MimeTypeHelper.isEntry(MimeTypeHelper.getMimeType(new EmptyEntry())));
    assertTrue(MimeTypeHelper.isFeed(MimeTypeHelper.getMimeType(new EmptyFeed())));
  }
  
  public static void testCompressionUtil() throws Exception {
    String s = "abcdefg";
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputStream cout = CompressionUtil.getEncodedOutputStream(
      out, CompressionUtil.CompressionCodec.GZIP);
    cout.write(s.getBytes("UTF-8"));
    cout.flush(); cout.close();
    byte[] bytes = out.toByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    InputStream cin = CompressionUtil.getDecodingInputStream(
      in, CompressionUtil.CompressionCodec.GZIP);
    byte[] buffer = new byte[20];
    int r = cin.read(buffer);
    String t = new String(buffer, 0, r, "UTF-8");
    assertEquals(s,t);
  }
  
  /** dummy classes **/
  private static class EmptyFeed implements Feed {

    public Object clone() {
      return null;
    }
    
    public void addEntry(Entry entry) {
    }

    public Entry addEntry() {
      return null;
    }

    public Source getAsSource() {
      return null;
    }

    public List<Entry> getEntries() {
      return null;
    }

    public Entry getEntry(String id) {
      return null;
    }

    public void insertEntry(Entry entry) {
    }

    public Entry insertEntry() {
      return null;
    }

    public void sortEntries(Comparator<Entry> comparator) {
    }

    public void sortEntriesByEdited(boolean new_first) {
    }

    public void sortEntriesByUpdated(boolean new_first) {
    }

    public void addAuthor(Person person) {
    }

    public Person addAuthor(String name) {
      return null;
    }

    public Person addAuthor(String name, String email, String iri) {
      return null;
    }

    public void addCategory(Category category) {
    }

    public Category addCategory(String term) {
      return null;
    }

    public Category addCategory(String scheme, String term, String label) {
      return null;
    }

    public void addContributor(Person person) {
    }

    public Person addContributor(String name) {
      return null;
    }

    public Person addContributor(String name, String email, String iri) {
      return null;
    }

    public void addLink(Link link) {
    }

    public Link addLink(String href) {
      return null;
    }

    public Link addLink(String href, String rel) {
      return null;
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length){
      return null;
    }

    public Link getAlternateLink() {
      return null;
    }

    public Link getAlternateLink(String type, String hreflang){
      return null;
    }

    public IRI getAlternateLinkResolvedHref() {
      return null;
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang){
      return null;
    }

    public Feed getAsFeed() {
      return null;
    }

    public Person getAuthor() {
      return null;
    }

    public List<Person> getAuthors() {
      return null;
    }

    public List<Category> getCategories() {
      return null;
    }

    public List<Category> getCategories(String scheme) {
      return null;
    }

    public Collection getCollection() {
      return null;
    }

    public List<Person> getContributors() {
      return null;
    }

    public Generator getGenerator() {
      return null;
    }

    public IRI getIcon() {
      return null;
    }

    public IRIElement getIconElement() {
      return null;
    }

    public IRI getId() {
      return null;
    }

    public IRIElement getIdElement() {
      return null;
    }

    public Link getLink(String rel) {
      return null;
    }

    public IRI getLinkResolvedHref(String rel) {
      return null;
    }

    public List<Link> getLinks() {
      return null;
    }

    public List<Link> getLinks(String rel) {
      return null;
    }

    public List<Link> getLinks(String... rel) {
      return null;
    }

    public IRI getLogo() {
      return null;
    }

    public IRIElement getLogoElement() {
      return null;
    }

    public String getRights() {
      return null;
    }

    public Text getRightsElement() {
      return null;
    }

    public org.apache.abdera.model.Text.Type getRightsType() {
      return null;
    }

    public Link getSelfLink() {
      return null;
    }

    public IRI getSelfLinkResolvedHref() {
      return null;
    }

    public String getSubtitle() {
      return null;
    }

    public Text getSubtitleElement() {
      return null;
    }

    public org.apache.abdera.model.Text.Type getSubtitleType() {
      return null;
    }

    public String getTitle() {
      return null;
    }

    public Text getTitleElement() {
      return null;
    }

    public org.apache.abdera.model.Text.Type getTitleType() {
      return null;
    }

    public Date getUpdated() {
      return null;
    }

    public DateTime getUpdatedElement() {
      return null;
    }

    public String getUpdatedString() {
      return null;
    }

    public IRIElement newId() {
      return null;
    }

    public void setCollection(Collection collection) {
    }

    public void setGenerator(Generator generator) {
    }

    public Generator setGenerator(String iri, String version, String value) {
      return null;
    }

    public IRIElement setIcon(String iri) {
      return null;
    }

    public void setIconElement(IRIElement iri) {
    }

    public IRIElement setId(String id) {
      return null;
    }

    public IRIElement setId(String id, boolean normalize) {
      return null;
    }

    public void setIdElement(IRIElement id) {
    }

    public IRIElement setLogo(String iri) {
      return null;
    }

    public void setLogoElement(IRIElement iri) {
    }

    public Text setRights(String value) {
      return null;
    }

    public Text setRights(String value, org.apache.abdera.model.Text.Type type) {
      return null;
    }

    public Text setRights(Div value) {
      return null;
    }

    public Text setRightsAsHtml(String value) {
      return null;
    }

    public Text setRightsAsXhtml(String value) {
      return null;
    }

    public void setRightsElement(Text text) {
    }

    public Text setSubtitle(String value) {
      return null;
    }

    public Text setSubtitle(String value, org.apache.abdera.model.Text.Type type) {
      return null;
    }

    public Text setSubtitle(Div value) {
      return null;
    }

    public Text setSubtitleAsHtml(String value) {
      return null;
    }

    public Text setSubtitleAsXhtml(String value) {
      return null;
    }

    public void setSubtitleElement(Text text) {
    }

    public Text setTitle(String value) {
      return null;
    }

    public Text setTitle(String value, org.apache.abdera.model.Text.Type type) {
      return null;
    }

    public Text setTitle(Div value) {
      return null;
    }

    public Text setTitleAsHtml(String value) {
      return null;
    }

    public Text setTitleAsXhtml(String value) {
      return null;
    }

    public void setTitleElement(Text text) {
    }

    public DateTime setUpdated(Date value) {
      return null;
    }

    public DateTime setUpdated(String value) {
      return null;
    }

    public void setUpdatedElement(DateTime dateTime) {
    }

    public void addExtension(Element extension) {
    }

    public <T extends Element> T addExtension(QName qname) {
      return null;
    }

    public <T extends Element> T addExtension(String namespace, String localPart, String prefix) {
      return null;
    }

    public Element addSimpleExtension(QName qname, String value) {
      return null;
    }

    public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
      return null;
    }

    public <T extends Element> T getExtension(QName qname) {
      return null;
    }

    public <T extends Element> T getExtension(Class<T> _class) {
      return null;
    }

    public List<Element> getExtensions() {
      return null;
    }

    public List<Element> getExtensions(String uri) {
      return null;
    }

    public <T extends Element> List<T> getExtensions(QName qname) {
      return null;
    }

    public String getSimpleExtension(QName qname) {
      return null;
    }

    public String getSimpleExtension(String namespace, String localPart, String prefix) {
      return null;
    }

    public void declareNS(String uri, String prefix) {
    }

    public void discard() {
    }

    public String getAttributeValue(String name) {
      return null;
    }

    public String getAttributeValue(QName qname) {
      return null;
    }

    public List<QName> getAttributes() {
      return null;
    }

    public IRI getBaseUri() {
      return null;
    }

    public <T extends Element> Document<T> getDocument() {
      return null;
    }

    public <T extends Element> List<T> getElements() {
      return null;
    }

    public List<QName> getExtensionAttributes() {
      return null;
    }

    public <T extends Element> T getFirstChild() {
      return null;
    }

    public <T extends Element> T getFirstChild(QName qname) {
      return null;
    }

    public String getLanguage() {
      return null;
    }

    public Lang getLanguageTag() {
      return null;
    }

    public Locale getLocale() {
      return null;
    }

    public boolean getMustPreserveWhitespace() {
      return false;
    }

    public Map<String, String> getNamespaces() {
      return null;
    }

    public <T extends Element> T getNextSibling() {
      return null;
    }

    public <T extends Element> T getNextSibling(QName qname) {
      return null;
    }

    public <T extends Base> T getParentElement() {
      return null;
    }

    public <T extends Element> T getPreviousSibling() {
      return null;
    }

    public <T extends Element> T getPreviousSibling(QName qname) {
      return null;
    }

    public QName getQName() {
      return null;
    }

    public IRI getResolvedBaseUri() {
      return null;
    }

    public String getText() {
      return null;
    }

    public void removeAttribute(QName qname) {
    }

    public void setAttributeValue(String name, String value) {
    }

    public void setAttributeValue(QName qname, String value) {
    }

    public void setBaseUri(IRI base) {
    }

    public void setBaseUri(String base) {
    }

    public void setLanguage(String language) {
    }

    public void setMustPreserveWhitespace(boolean preserve) {
    }

    public void setParentElement(Element parent) {
    }

    public void setText(String text) {
    }

    public void setText(DataHandler dataHandler) {
    }

    public void addComment(String value) {
    }

    public WriterOptions getDefaultWriterOptions() {
      return null;
    }

    public Factory getFactory() {
      return null;
    }

    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
    }

    public void writeTo(Writer out, WriterOptions options) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out, WriterOptions options) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out, WriterOptions options) throws IOException {
    }

    public void writeTo(OutputStream out) throws IOException {
    }

    public void writeTo(Writer writer) throws IOException {
    }

    public void addExtension(Element extension, Element before) {}

    public <T extends Element> T addExtension(QName qname, QName before) {
      return null;
    }

    public void complete() {}

    public Iterator<Element> iterator() {
      return null;
    }

    public void writeTo(String writer, OutputStream out) throws IOException {}

    public void writeTo(String writer, Writer out) throws IOException {}

    public void writeTo(String writer, OutputStream out, WriterOptions options)
        throws IOException {}

    public void writeTo(String writer, Writer out, WriterOptions options)
        throws IOException {}
    
  }
  
  private static class EmptyEntry implements Entry {

    public Object clone() {
      return null;
    }
    
    public void addAuthor(Person person) {
    }

    public Person addAuthor(String name) {
      return null;
    }

    public Person addAuthor(String name, String email, String uri) {
      return null;
    }

    public void addCategory(Category category) {
    }

    public Category addCategory(String term) {
      return null;
    }

    public Category addCategory(String scheme, String term, String label) {
      return null;
    }

    public void addContributor(Person person) {
    }

    public Person addContributor(String name) {
      return null;
    }

    public Person addContributor(String name, String email, String uri) {
      return null;
    }

    public void addLink(Link link) {
    }

    public Link addLink(String href) {
      return null;
    }

    public Link addLink(String href, String rel) {
      return null;
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length){
      return null;
    }

    public Link getAlternateLink() {
      return null;
    }

    public Link getAlternateLink(String type, String hreflang){
      return null;
    }

    public IRI getAlternateLinkResolvedHref() {
      return null;
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang){
      return null;
    }

    public Person getAuthor() {
      return null;
    }

    public List<Person> getAuthors() {
      return null;
    }

    public List<Category> getCategories() {
      return null;
    }

    public List<Category> getCategories(String scheme) {
      return null;
    }

    public String getContent() {
      return null;
    }

    public Content getContentElement() {
      return null;
    }

    public MimeType getContentMimeType() {
      return null;
    }

    public IRI getContentSrc() {
      return null;
    }

    public InputStream getContentStream() throws IOException {
      return null;
    }

    public Type getContentType() {
      return null;
    }

    public List<Person> getContributors() {
      return null;
    }

    public Control getControl(boolean create) {
      return null;
    }

    public Control getControl() {
      return null;
    }

    public Link getEditLink() {
      return null;
    }

    public IRI getEditLinkResolvedHref() {
      return null;
    }

    public Link getEditMediaLink() {
      return null;
    }

    public Link getEditMediaLink(String type, String hreflang){
      return null;
    }

    public IRI getEditMediaLinkResolvedHref() {
      return null;
    }

    public IRI getEditMediaLinkResolvedHref(String type, String hreflang){
      return null;
    }

    public Date getEdited() {
      return null;
    }

    public DateTime getEditedElement() {
      return null;
    }

    public Link getEnclosureLink() {
      return null;
    }

    public IRI getEnclosureLinkResolvedHref() {
      return null;
    }

    public IRI getId() {
      return null;
    }

    public IRIElement getIdElement() {
      return null;
    }

    public Link getLink(String rel) {
      return null;
    }

    public IRI getLinkResolvedHref(String rel) {
      return null;
    }

    public List<Link> getLinks() {
      return null;
    }

    public List<Link> getLinks(String rel) {
      return null;
    }

    public List<Link> getLinks(String... rel) {
      return null;
    }

    public Date getPublished() {
      return null;
    }

    public DateTime getPublishedElement() {
      return null;
    }

    public String getRights() {
      return null;
    }

    public Text getRightsElement() {
      return null;
    }

    public org.apache.abdera.model.Text.Type getRightsType() {
      return null;
    }

    public Link getSelfLink() {
      return null;
    }

    public IRI getSelfLinkResolvedHref() {
      return null;
    }

    public Source getSource() {
      return null;
    }

    public String getSummary() {
      return null;
    }

    public Text getSummaryElement() {
      return null;
    }

    public org.apache.abdera.model.Text.Type getSummaryType() {
      return null;
    }

    public String getTitle() {
      return null;
    }

    public Text getTitleElement() {
      return null;
    }

    public org.apache.abdera.model.Text.Type getTitleType() {
      return null;
    }

    public Date getUpdated() {
      return null;
    }

    public DateTime getUpdatedElement() {
      return null;
    }

    public boolean isDraft() {
      return false;
    }

    public IRIElement newId() {
      return null;
    }

    public Content setContent(String value) {
      return null;
    }

    public Content setContent(String value, Type type) {
      return null;
    }

    public Content setContent(Element value) {
      return null;
    }

    public Content setContent(Element element, String mediaType){
      return null;
    }

    public Content setContent(DataHandler dataHandler){
      return null;
    }

    public Content setContent(DataHandler dataHandler, String mediatype){
      return null;
    }

    public Content setContent(InputStream inputStream) {
      return null;
    }

    public Content setContent(InputStream inputStream, String mediatype){
      return null;
    }

    public Content setContent(String value, String mediatype){
      return null;
    }

    public Content setContent(IRI uri, String mediatype){
      return null;
    }

    public Content setContentAsHtml(String value) {
      return null;
    }

    public Content setContentAsXhtml(String value) {
      return null;
    }

    public void setContentElement(Content content) {
    }

    public void setControl(Control control) {
    }

    public void setDraft(boolean draft) {
    }

    public DateTime setEdited(Date value) {
      return null;
    }

    public DateTime setEdited(String value) {
      return null;
    }

    public void setEditedElement(DateTime modified) {
    }

    public IRIElement setId(String id) {
      return null;
    }

    public IRIElement setId(String id, boolean normalize) {
      return null;
    }

    public void setIdElement(IRIElement id) {
    }

    public DateTime setPublished(Date value) {
      return null;
    }

    public DateTime setPublished(String value) {
      return null;
    }

    public void setPublishedElement(DateTime dateTime) {
    }

    public Text setRights(String value) {
      return null;
    }

    public Text setRights(String value, org.apache.abdera.model.Text.Type type) {
      return null;
    }

    public Text setRights(Div value) {
      return null;
    }

    public Text setRightsAsHtml(String value) {
      return null;
    }

    public Text setRightsAsXhtml(String value) {
      return null;
    }

    public void setRightsElement(Text text) {
    }

    public void setSource(Source source) {
    }

    public Text setSummary(String value) {
      return null;
    }

    public Text setSummary(String value, org.apache.abdera.model.Text.Type type) {
      return null;
    }

    public Text setSummary(Div value) {
      return null;
    }

    public Text setSummaryAsHtml(String value) {
      return null;
    }

    public Text setSummaryAsXhtml(String value) {
      return null;
    }

    public void setSummaryElement(Text text) {
    }

    public Text setTitle(String value) {
      return null;
    }

    public Text setTitle(String value, org.apache.abdera.model.Text.Type type) {
      return null;
    }

    public Text setTitle(Div value) {
      return null;
    }

    public Text setTitleAsHtml(String value) {
      return null;
    }

    public Text setTitleAsXhtml(String value) {
      return null;
    }

    public void setTitleElement(Text title) {
    }

    public DateTime setUpdated(Date value) {
      return null;
    }

    public DateTime setUpdated(String value) {
      return null;
    }

    public void setUpdatedElement(DateTime updated) {
    }

    public void addExtension(Element extension) {
    }

    public <T extends Element> T addExtension(QName qname) {
      return null;
    }

    public <T extends Element> T addExtension(String namespace, String localPart, String prefix) {
      return null;
    }

    public Element addSimpleExtension(QName qname, String value) {
      return null;
    }

    public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
      return null;
    }

    public <T extends Element> T getExtension(QName qname) {
      return null;
    }

    public <T extends Element> T getExtension(Class<T> _class) {
      return null;
    }

    public List<Element> getExtensions() {
      return null;
    }

    public List<Element> getExtensions(String uri) {
      return null;
    }

    public <T extends Element> List<T> getExtensions(QName qname) {
      return null;
    }

    public String getSimpleExtension(QName qname) {
      return null;
    }

    public String getSimpleExtension(String namespace, String localPart, String prefix) {
      return null;
    }

    public void declareNS(String uri, String prefix) {
    }

    public void discard() {
    }

    public String getAttributeValue(String name) {
      return null;
    }

    public String getAttributeValue(QName qname) {
      return null;
    }

    public List<QName> getAttributes() {
      return null;
    }

    public IRI getBaseUri() {
      return null;
    }

    public <T extends Element> Document<T> getDocument() {
      return null;
    }

    public <T extends Element> List<T> getElements() {
      return null;
    }

    public List<QName> getExtensionAttributes() {
      return null;
    }

    public <T extends Element> T getFirstChild() {
      return null;
    }

    public <T extends Element> T getFirstChild(QName qname) {
      return null;
    }

    public String getLanguage() {
      return null;
    }

    public Lang getLanguageTag() {
      return null;
    }

    public Locale getLocale() {
      return null;
    }

    public boolean getMustPreserveWhitespace() {
      return false;
    }

    public Map<String, String> getNamespaces() {
      return null;
    }

    public <T extends Element> T getNextSibling() {
      return null;
    }

    public <T extends Element> T getNextSibling(QName qname) {
      return null;
    }

    public <T extends Base> T getParentElement() {
      return null;
    }

    public <T extends Element> T getPreviousSibling() {
      return null;
    }

    public <T extends Element> T getPreviousSibling(QName qname) {
      return null;
    }

    public QName getQName() {
      return null;
    }

    public IRI getResolvedBaseUri() {
      return null;
    }

    public String getText() {
      return null;
    }

    public void removeAttribute(QName qname) {
    }

    public void setAttributeValue(String name, String value) {
    }

    public void setAttributeValue(QName qname, String value) {
    }

    public void setBaseUri(IRI base) {
    }

    public void setBaseUri(String base) {
    }

    public void setLanguage(String language) {
    }

    public void setMustPreserveWhitespace(boolean preserve) {
    }

    public void setParentElement(Element parent) {
    }

    public void setText(String text) {
    }

    public void setText(DataHandler dataHandler) {
    }

    public void addComment(String value) {
    }

    public WriterOptions getDefaultWriterOptions() {
      return null;
    }

    public Factory getFactory() {
      return null;
    }

    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
    }

    public void writeTo(Writer out, WriterOptions options) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out, WriterOptions options) throws IOException {
    }

    public void writeTo(org.apache.abdera.writer.Writer writer, Writer out, WriterOptions options) throws IOException {
    }

    public void writeTo(OutputStream out) throws IOException {
    }

    public void writeTo(Writer writer) throws IOException {
    }

    public void addExtension(Element extension, Element before) {}

    public <T extends Element> T addExtension(QName qname, QName before) {
      return null;
    }

    public void complete() {}

    public Iterator<Element> iterator() {
      return null;
    }

    public void writeTo(String writer, OutputStream out) throws IOException {}

    public void writeTo(String writer, Writer out) throws IOException {}

    public void writeTo(String writer, OutputStream out, WriterOptions options)
        throws IOException {}

    public void writeTo(String writer, Writer out, WriterOptions options)
        throws IOException {}
    
  }
}
