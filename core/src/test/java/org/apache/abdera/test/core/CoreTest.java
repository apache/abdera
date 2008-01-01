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
import org.apache.abdera.model.ExtensibleElement;
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
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.XmlRestrictedCharReader;
import org.apache.abdera.writer.WriterOptions;

public class CoreTest extends TestCase implements Constants {

  public static void testDefaultConfigurationProperties() {
    Configuration config = new AbderaConfiguration();
    assertEquals(
      config.getConfigurationOption(CONFIG_FACTORY, DEFAULT_FACTORY), 
      "org.apache.abdera.parser.stax.FOMFactory");
    assertEquals(
      config.getConfigurationOption(CONFIG_PARSER, DEFAULT_PARSER),
      "org.apache.abdera.parser.stax.FOMParser");
    assertEquals(
      config.getConfigurationOption(CONFIG_XPATH, DEFAULT_XPATH),
      "org.apache.abdera.parser.stax.FOMXPath");
  }
  
  public static void testUriNormalization() {
    try {
      assertEquals(
        IRI.normalizeString(
          "HTTP://www.EXAMPLE.org:80/foo/../Bar/%3f/./foo/."), 
        "http://www.example.org/Bar/%3F/foo/");
      assertEquals(
          IRI.normalizeString(
          "HTTPs://www.EXAMPLE.org:443/foo/../Bar/%3f/./foo/."), 
        "https://www.example.org/Bar/%3F/foo/");
      assertEquals(
        IRI.normalizeString(
          "HTTP://www.EXAMPLE.org:81/foo/../Bar/%3f/./foo/."), 
        "http://www.example.org:81/Bar/%3F/foo/");
      assertEquals(
        IRI.normalizeString(
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
    
    public Feed addEntry(Entry entry) {
      return this;
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

    public Feed insertEntry(Entry entry) {
      return this;
    }

    public Entry insertEntry() {
      return null;
    }

    public Feed sortEntries(Comparator<Entry> comparator) {
      return null;
    }

    public Feed sortEntriesByEdited(boolean new_first) {
      return null;
    }

    public Feed sortEntriesByUpdated(boolean new_first) {
      return null;
    }

    public <T extends Source>T addAuthor(Person person) {
      return (T)this;
    }

    public Person addAuthor(String name) {
      return null;
    }

    public Person addAuthor(String name, String email, String iri) {
      return null;
    }

    public <T extends Source>T addCategory(Category category) {
      return (T)this;
    }

    public Category addCategory(String term) {
      return null;
    }

    public Category addCategory(String scheme, String term, String label) {
      return null;
    }

    public <T extends Source>T addContributor(Person person) {
      return (T)this;
    }

    public Person addContributor(String name) {
      return null;
    }

    public Person addContributor(String name, String email, String iri) {
      return null;
    }

    public <T extends Source>T addLink(Link link) {
      return (T)this;
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

    public <T extends Source>T setCollection(Collection collection) {
      return (T)this;
    }

    public <T extends Source>T setGenerator(Generator generator) {
      return (T)this;
    }

    public Generator setGenerator(String iri, String version, String value) {
      return null;
    }

    public IRIElement setIcon(String iri) {
      return null;
    }

    public <T extends Source>T setIconElement(IRIElement iri) {
      return (T)this;
    }

    public IRIElement setId(String id) {
      return null;
    }

    public IRIElement setId(String id, boolean normalize) {
      return null;
    }

    public <T extends Source>T setIdElement(IRIElement id) {
      return (T)this;
    }

    public IRIElement setLogo(String iri) {
      return null;
    }

    public <T extends Source>T setLogoElement(IRIElement iri) {
      return (T)this;
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

    public <T extends Source>T setRightsElement(Text text) {
      return (T)this;
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

    public <T extends Source>T setSubtitleElement(Text text) {
      return (T)this;
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

    public <T extends Source>T setTitleElement(Text text) {
      return (T)this;
    }

    public DateTime setUpdated(Date value) {
      return null;
    }

    public DateTime setUpdated(String value) {
      return null;
    }

    public <T extends Source>T setUpdatedElement(DateTime dateTime) {
      return (T)this;
    }

    public <T extends ExtensibleElement>T addExtension(Element extension) {
      return (T)this;
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

    public Feed declareNS(String uri, String prefix) {
      return null;
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

    public Feed removeAttribute(QName qname) {
      return null;
    }

    public Feed setAttributeValue(String name, String value) {
      return null;
    }

    public Feed setAttributeValue(QName qname, String value) {
      return null;
    }

    public Feed setBaseUri(IRI base) {
      return null;
    }

    public Feed setBaseUri(String base) {
      return null;
    }

    public Feed setLanguage(String language) {
      return null;
    }

    public Feed setMustPreserveWhitespace(boolean preserve) {
      return null;
    }

    public Feed setParentElement(Element parent) {
      return null;
    }

    public Feed setText(String text) {
      return null;
    }

    public Feed setText(DataHandler dataHandler) {
      return null;
    }

    public Feed addComment(String value) {
      return null;
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

    public <T extends ExtensibleElement>T addExtension(Element extension, Element before) {
      return (T)this;
    }

    public <T extends Element> T addExtension(QName qname, QName before) {
      return null;
    }

    public Feed complete() {
      return null;
    }

    public Iterator<Element> iterator() {
      return null;
    }

    public void writeTo(String writer, OutputStream out) throws IOException {}

    public void writeTo(String writer, Writer out) throws IOException {}

    public void writeTo(String writer, OutputStream out, WriterOptions options)
        throws IOException {}

    public void writeTo(String writer, Writer out, WriterOptions options)
        throws IOException {}

    public Feed removeAttribute(String name) {
      return null;
    }
    
  }
  
  private static class EmptyEntry implements Entry {

    public Object clone() {
      return null;
    }
    
    public Entry addAuthor(Person person) {
      return this;
    }

    public Person addAuthor(String name) {
      return null;
    }

    public Person addAuthor(String name, String email, String uri) {
      return null;
    }

    public Entry addCategory(Category category) {
      return this;
    }

    public Category addCategory(String term) {
      return null;
    }

    public Category addCategory(String scheme, String term, String label) {
      return null;
    }

    public Entry addContributor(Person person) {
      return this;
    }

    public Person addContributor(String name) {
      return null;
    }

    public Person addContributor(String name, String email, String uri) {
      return null;
    }

    public Entry addLink(Link link) {
      return this;
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

    public Entry setContentElement(Content content) {
      return this;
    }

    public Entry setControl(Control control) {
      return this;
    }

    public Entry setDraft(boolean draft) {
      return this;
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

    public Entry setIdElement(IRIElement id) {
      return this;
    }

    public DateTime setPublished(Date value) {
      return null;
    }

    public DateTime setPublished(String value) {
      return null;
    }

    public Entry setPublishedElement(DateTime dateTime) {
      return this;
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

    public Entry setRightsElement(Text text) {
      return this;
    }

    public Entry setSource(Source source) {
      return this;
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

    public Entry setSummaryElement(Text text) {
      return this;
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

    public Entry setTitleElement(Text title) {
      return this;
    }

    public DateTime setUpdated(Date value) {
      return null;
    }

    public DateTime setUpdated(String value) {
      return null;
    }

    public Entry setUpdatedElement(DateTime updated) {
      return this;
    }

    public <T extends ExtensibleElement>T addExtension(Element extension) {
      return (T)this;
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

    public Entry declareNS(String uri, String prefix) {
      return null;
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

    public Entry removeAttribute(QName qname) {
      return null;
    }

    public Entry setAttributeValue(String name, String value) {
      return null;
    }

    public Entry setAttributeValue(QName qname, String value) {
      return null;
    }

    public Entry setBaseUri(IRI base) {
      return null;
    }

    public Entry setBaseUri(String base) {
      return null;
    }

    public Entry setLanguage(String language) {
      return null;
    }

    public Entry setMustPreserveWhitespace(boolean preserve) {
      return null;
    }

    public Entry setParentElement(Element parent) {
      return null;
    }

    public Entry setText(String text) {
      return null;
    }

    public Entry setText(DataHandler dataHandler) {
      return null;
    }

    public Entry addComment(String value) {
      return null;
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

    public <T extends ExtensibleElement>T addExtension(Element extension, Element before) {
      return (T)this;
    }

    public <T extends Element> T addExtension(QName qname, QName before) {
      return null;
    }

    public Entry complete() {
      return null;
    }

    public Iterator<Element> iterator() {
      return null;
    }

    public void writeTo(String writer, OutputStream out) throws IOException {}

    public void writeTo(String writer, Writer out) throws IOException {}

    public void writeTo(String writer, OutputStream out, WriterOptions options)
        throws IOException {}

    public void writeTo(String writer, Writer out, WriterOptions options)
        throws IOException {}

    public Entry removeAttribute(String name) {
      return null;
    }
    
  }
}
