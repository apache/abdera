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
package org.apache.abdera.parser.stax;
 
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
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
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.StringElement;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.Version;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;


public class FOMFactory 
  extends OMLinkedListImplFactory 
  implements Factory, Constants {

  private final static Map<QName,Class> qclasses = new HashMap<QName,Class>();
  static {
    qclasses.put(FEED, FOMFeed.class);
    qclasses.put(SERVICE, FOMService.class);
    qclasses.put(ENTRY, FOMEntry.class);
    qclasses.put(AUTHOR, FOMPerson.class);
    qclasses.put(CATEGORY, FOMCategory.class);
    qclasses.put(CONTENT, FOMContent.class);
    qclasses.put(CONTRIBUTOR, FOMPerson.class);
    qclasses.put(GENERATOR, FOMGenerator.class);
    qclasses.put(ICON, FOMIRI.class);
    qclasses.put(ID, FOMIRI.class);
    qclasses.put(LOGO, FOMIRI.class);
    qclasses.put(LINK, FOMLink.class);
    qclasses.put(PUBLISHED, FOMDateTime.class);
    qclasses.put(RIGHTS, FOMText.class);
    qclasses.put(SOURCE, FOMSource.class);
    qclasses.put(SUBTITLE, FOMText.class);
    qclasses.put(SUMMARY, FOMText.class);
    qclasses.put(TITLE, FOMText.class);
    qclasses.put(UPDATED, FOMDateTime.class);
    qclasses.put(WORKSPACE, FOMWorkspace.class);
    qclasses.put(COLLECTION, FOMCollection.class);
    qclasses.put(NAME, FOMStringElement.class);
    qclasses.put(EMAIL, FOMStringElement.class);
    qclasses.put(URI, FOMIRI.class);
    qclasses.put(CONTROL, FOMControl.class);
    qclasses.put(DIV, FOMDiv.class);
  }
  
  private List<QName> simpleExtensions = null;
  private Map<Class,Class> alternatives = null;
  
  public Parser newParser() {
    return new FOMParser();
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> newDocument() {
    return _newInstance(FOMDocument.class);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> newDocument(
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMDocument.class, parserWrapper);
  }
  
  public <T extends Element>Document<T> newDocument(
    T root, 
    OMXMLParserWrapper parserWrapper) {
      FOMDocument<T> doc = (FOMDocument<T>) newDocument(parserWrapper);
      doc.setRoot(root);
      return doc;
  }
  
  public Service newService(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMService.class, qname, parent, parserWrapper);
  }

  public Service newService(
    Base parent) {
      return _newInstance(FOMService.class, (OMContainer)parent);
  }

  public Workspace newWorkspace(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMWorkspace.class, qname, parent, parserWrapper);
  }
  
  public Workspace newWorkspace() {
    return newWorkspace(null);
  }
  
  public Workspace newWorkspace(
    Element parent) {
      return _newInstance(FOMWorkspace.class, (OMContainer)parent);
  }

  public Collection newCollection(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMCollection.class, qname, parent, parserWrapper);
  }
  
  public Collection newCollection() {
    return newCollection(null);
  }
  
  public Collection newCollection(
    Element parent) {
      return _newInstance(FOMCollection.class, (OMContainer)parent);
  }

  public Feed newFeed() {
    Document<Feed> doc = newDocument();
    return newFeed(doc);
  }
  
  public Entry newEntry() {
    Document<Entry> doc = newDocument();
    return newEntry(doc);
  }
  
  public Service newService() {
    Document<Service> doc = newDocument();
    return newService(doc);
  }
  
  public Feed newFeed(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMFeed.class, qname, parent, parserWrapper);
  }
  
  public Feed newFeed(
    Base parent) {
      return _newInstance(FOMFeed.class, (OMContainer)parent);
  }

  public Entry newEntry(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMEntry.class, qname, parent, parserWrapper);
  }
  
  public Entry newEntry(
    Base parent) {
      return _newInstance(FOMEntry.class, (OMContainer)parent);
  }

  public Category newCategory(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMCategory.class, qname, parent, parserWrapper);
  }
  
  public Category newCategory() {
    return newCategory((Element)null);
  }
  
  public Category newCategory(
    Element parent) {
      return _newInstance(FOMCategory.class, (OMContainer)parent);
  }

  public Category newCategory(String term) {
    return newCategory(term, null);
  }
  
  public Category newCategory(String term, Element parent) {
    return newCategory(term, null, null, parent);
  }
  
  public Category newCategory(
    String term, 
    URI scheme, 
    String label) {
      return newCategory(term, scheme, label, null);
  }
  
  public Category newCategory(
    String term, 
    URI scheme, 
    String label, 
    Element parent) {
    Category category = newCategory(parent);
    if (scheme != null)
      category.setScheme(scheme);
    if (term != null)
      category.setTerm(term);
    if (label != null)
    category.setLabel(label);
    return category;
  }

  public Content newContent(
    QName qname,
    Type type, 
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      if (type == null) type = Content.Type.TEXT;
      return _newInstance(FOMContent.class, qname, type, parent, parserWrapper);
  }
  
  public Content newContent() {
    return newContent(Content.Type.TEXT);
  }
  
  public Content newContent(String value) {
    return newContent(value, Content.Type.TEXT);
  }
  
  public Content newContent(Type type) {
    if (type == null) type = Content.Type.TEXT;
    return newContent(type, (Element)null);
  }
  
  public Content newContent(
    Type type, 
    Element parent) {
      if (type == null) type = Content.Type.TEXT;
      Content content = 
        _newInstance(
          FOMContent.class, type, (OMContainer)parent);
      if (type.equals(Content.Type.XML))
        content.setMimeType("application/xml");
      return content;
  }
  
  public Content newContent(MimeType mediaType) {
    return newContent(mediaType, (Element)null);
  }
  
  public Content newContent(String value, Content.Type type) {
    return newContent(value, type, null);
  }

  public Content newContent(String value, Content.Type type, Element parent) {
    Content content = newContent(type, parent);
    content.setValue(value);
    return content;
  }
  
  public Content newContent(Content.Type type, ExtensionElement value) {
    return newContent(value, type, null);
  }
  
  public Content newContent(ExtensionElement value, Content.Type type, Element parent) {
    Content content = newContent(type, parent);
    content.setValueElement(value);
    return content;
  }
  
  public Content newContent(
    String value, 
    MimeType mediaType) {
      return newContent(value, mediaType, null);
  }
  
  public Content newContent(
    String value,
    MimeType mediaType,
    Element parent) {
    Content content = newContent(mediaType, parent);
    content.setValue(value);
    return content;
  }
  
  public Content newContent(
    DataHandler dataHandler, 
    MimeType mediaType) { 
      return newContent(dataHandler, mediaType, null);
  }
  
  public Content newContent(
    DataHandler dataHandler, 
    MimeType mediaType, 
    Element parent) {
      Content content = newContent(mediaType, parent);
      if (dataHandler != null)
        content.setDataHandler(dataHandler);
      if (mediaType != null)
        content.setMimeType(mediaType);
      return content;
  }

  public Content newContent(
    ExtensionElement value, 
    MimeType mediaType) {
      return newContent(value, mediaType, null);
  }
  
  public Content newContent(
    ExtensionElement value, 
    MimeType mediaType, 
    Element parent) {
      Content content = 
        newContent(
          mediaType, 
          parent);
      content.setValueElement(value);
      return content;
  }

  public Content newContent(URI src, MimeType mediaType)  {
    return newContent(src, mediaType, (Element)null);
  }
  
  public Content newContent(URI src, MimeType mediaType, Element parent) {
    Content content = 
      newContent(
        mediaType, 
        parent);
    content.setSrc(src);
    return content;
  }
  
  public Content newContent(
    MimeType mediaType, 
    Element parent) {
    Content.Type type = 
      (MimeTypeHelper.isXml(mediaType.toString())) ? 
         Content.Type.XML : Content.Type.MEDIA;
    Content content = newContent(type, parent);
    content.setMimeType(mediaType);
    return content;
  }

  public DateTime newDateTimeElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMDateTime.class, qname, parent, parserWrapper);
  }
  
  public DateTime newDateTime(
    QName qname, 
    Element parent) {
      return _newInstance(FOMDateTime.class, qname, (OMContainer)parent);
  }

  public DateTime newDateTime(
    QName qname, 
    AtomDate dateTime, 
    Element parent) {
    DateTime dt = newDateTime(qname, parent);
    dt.setValue(dateTime);
    return dt;
  }

  public DateTime newDateTime(
    QName qname, 
    Date date, 
    Element parent) {
    DateTime dt = newDateTime(qname, parent);
    dt.setValue(AtomDate.valueOf(date));
    return dt;
  }

  public DateTime newDateTime(
    QName qname, 
    String date, 
    Element parent) {
    DateTime dt = newDateTime(qname, parent);
    dt.setValue(AtomDate.valueOf(date));
    return dt;
  }

  public DateTime newDateTime(
    QName qname, 
    Calendar date, 
    Element parent) {
    DateTime dt = newDateTime(qname, parent);
    dt.setValue(AtomDate.valueOf(date));
    return dt;
  }

  public DateTime newDateTime(
    QName qname, 
    long date, 
    Element parent) {
    DateTime dt = newDateTime(qname, parent);
    dt.setValue(AtomDate.valueOf(date));
    return dt;
  }

  public Generator newGenerator(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMGenerator.class, qname, parent, parserWrapper);
  }
  
  public Generator newDefaultGenerator() {
    return newDefaultGenerator(null);
  }
  
  public Generator newDefaultGenerator(
    Element parent) {
      Generator generator = newGenerator(parent);
      generator.setVersion(Version.VERSION);
      generator.setValue(Version.APP_NAME);
      try {
        generator.setUri(Version.URI);
      } catch (Exception e) {}
      return generator;
  }
  
  public Generator newGenerator() {
    return newGenerator(null);
  }
  
  public Generator newGenerator(
      Element parent) {
    return _newInstance(FOMGenerator.class, (OMContainer)parent);
  }

  public Generator newGenerator(
    URI uri, 
    String version, 
    String value) {  
      return newGenerator(uri, version, value, null);
  }
  
  public Generator newGenerator(
    URI uri, 
    String version, 
    String value, 
    Element parent) {
    Generator generator = newGenerator(parent);
    if (uri != null)
      generator.setUri(uri);
    if (version != null)
      generator.setVersion(version);
    if (value != null)
      generator.setValue(value);
    return generator;
  }

  public IRI newID(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMIRI.class, qname, parent, parserWrapper);
  }
  
  public IRI newID() {
    return newID((Element)null);
  }
  
  public IRI newID(String id) throws URISyntaxException {
    return newID(id, null);
  }
  
  public IRI newID(URI id) {
    return newID(id, null);
  }
  
  public IRI newID(
    Element parent) {
      return _newInstance(FOMIRI.class, Constants.ID, (OMContainer)parent);
  }

  public IRI newID(
    String id, 
    Element parent) 
      throws URISyntaxException {
    IRI _id = newID(parent);
    if (id != null)
      _id.setValue(new URI(id));
    return _id;
  }

  public IRI newID(
    URI id, 
    Element parent) {
      IRI _id = newID(parent);
      if (id != null)
      _id.setValue(id);
      return _id;
  }

  public IRI newURIElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMIRI.class, qname, parent, parserWrapper);
  }
  
  public IRI newIRIElement(
    QName qname, 
    Element parent) {
      return _newInstance(FOMIRI.class, qname, (OMContainer)parent);
  }

  public IRI newIRIElement(
    QName qname, 
    URI uri, 
    Element parent) {
    IRI iri = newIRIElement(qname, parent);
    if (uri != null)
      iri.setValue(uri);
    return iri;
  }

  public IRI newIRIElement(
    QName qname, 
    String URI, 
    Element parent) 
      throws URISyntaxException {
    IRI iri = newIRIElement(qname, parent);
    if (URI != null)
      iri.setValue(new URI(URI));
    return iri;
  }

  public Link newLink(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMLink.class, qname, parent, parserWrapper);
  }
  
  public Link newLink() {
    return newLink((Element)null);
  }
  
  public Link newLink(
    String href, 
    String rel, 
    MimeType type, 
    String title, 
    String hreflang, 
    long length) 
      throws URISyntaxException {
    return newLink(href, rel, type, title, hreflang, length, null);
  }
  
  public Link newLink(
    URI href, 
    String rel, 
    MimeType type, 
    String title, 
    String hreflang, 
    long length) {
      return newLink(href, rel, type, title, hreflang, length, null);
  }
  
  public Link newLink(
    Element parent) {
      return _newInstance(FOMLink.class, (OMContainer)parent);
  }

  public Link newLink(
    String href, 
    String rel, 
    MimeType type, 
    String title,
    String hreflang, 
    long length, 
    Element parent) throws URISyntaxException {
    Link link = newLink(parent);
    if (href != null)
      link.setHref(new URI(href));
    if (rel != null)
      link.setRel(rel);
    if (title != null)
      link.setTitle(title);
    if (type != null)
      link.setMimeType(type);
    if (hreflang != null)
      link.setHrefLang(hreflang);
    if (length > -1)
      link.setLength(length);
    return link;
  }

  public Link newLink(
    URI href, 
    String rel, 
    MimeType type, 
    String title, 
    String hreflang, 
    long length, 
    Element parent) {
    Link link = newLink(parent);
    if (href != null)
      link.setHref(href);
    if (rel != null)
      link.setRel(rel);
    if (title != null)
      link.setTitle(title);
    if (type != null)
      link.setMimeType(type);
    if (hreflang != null)
      link.setHrefLang(hreflang);
    if (length > -1)
      link.setLength(length);
    return link;
  }

  public Person newPerson(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMPerson.class, qname, parent, parserWrapper);
  }
  
  public Person newPerson(
    QName qname, 
    Element parent) {
      return _newInstance(FOMPerson.class, qname, (OMContainer)parent);
  }

  public Person newPerson(
    QName qname,
    String name, 
    String email, 
    String uri, 
    Element parent) throws URISyntaxException {
    Person person = newPerson(qname, parent);
    if (name != null)
      person.setName(name);
    if (email != null)
      person.setEmail(email);
    if (uri != null)
      person.setUri(new URI(uri));
    return person;
  }

  public Person newPerson(
    QName qname, 
    String name, 
    String email,
    URI uri, 
    Element parent) {
    Person person = newPerson(qname, parent);
    if (name != null)
      person.setName(name);
    if (email != null)
      person.setEmail(email);
    if (uri != null)
      person.setUri(uri);
    return person;
  }

  public Source newSource(
    QName qname, 
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMSource.class, qname, parent, parserWrapper);
  }
  
  public Source newSource() {
    return newSource((Element)null);
  }
  
  public Source newSource(
      Element parent) {
    return _newInstance(FOMSource.class, (OMContainer)parent);
  }

  public Text newText(
    QName qname,
    Text.Type type,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
    if (type == null) type = Text.Type.TEXT;
    return _newInstance(FOMText.class, type, qname, parent, parserWrapper);
  }
  
  public Text newText(
    QName qname,
    Text.Type type) {
    return newText(qname, type, (Element)null);
  }
  
  public Text newText(
    QName qname, 
    Text.Type type, 
    Element parent) {
    if (type == null) type = Text.Type.TEXT;
    return _newInstance(FOMText.class,type, qname,(OMContainer)parent);
  }
  
  public Text newText(
    QName qname, 
    String value,
    Text.Type type) {
      return newText(qname, value, type, null);
  }
  
  public Text newText(
    QName qname, 
    String value,
    Text.Type type,
    Element parent) {
      Text text = newText(qname, type, parent);
      text.setValue(value);
      return text;
  }

  public Text newText(
    QName qname,
    Div value) {
    return newText(qname, value, null);
  }
  
  public Text newText(
    QName qname, 
    Div value,
    Element parent) {
      Text text = newText(qname, Text.Type.XHTML, parent);
      if (value != null)
        text.setValueElement(value);
      return text;
  }

  public StringElement newStringElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMStringElement.class, qname, parent, parserWrapper);
  }
  
  public StringElement newStringElement(QName qname) {
    return newStringElement(qname, (Element)null);
  }
  
  public StringElement newStringElement(QName qname, String value) {
    return newStringElement(qname, value, null);
  }
  
  public StringElement newStringElement(
    QName qname, 
    Base parent) {
      return _newInstance(FOMStringElement.class, qname, (OMContainer)parent);
  }
  
  public StringElement newStringElement(
    QName qname, 
    String value,
    Base parent) {
    StringElement el = newStringElement(qname, parent);
    if (value != null)
      el.setValue(value);
    return el;
  }

  public ExtensionElement newExtensionElement(QName qname) {
    return newExtensionElement(qname, (Base)null);
  }
  
  public ExtensionElement newExtensionElement(
    QName qname, 
    Base parent) {
    return newExtensionElement(qname, (OMContainer)parent);
  }
  
  private ExtensionElement newExtensionElement(
    QName qname, 
    OMContainer parent) {
      return newExtensionElement(qname, parent, null);
  }

  public ExtensionElement newExtensionElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
    ExtensionElement element = null;
    if (!isSimpleExtension(qname)) {
      List<ExtensionFactory> factories = 
        org.apache.abdera.util.ServiceUtil.loadExtensionFactories();
      if (factories != null) {
        for (ExtensionFactory factory : factories) {
          if (factory instanceof FOMExtensionFactory &&
              factory.handlesNamespace(qname.getNamespaceURI())) {
              if (parserWrapper != null) {
                element = ((FOMExtensionFactory)factory).newExtensionElement(
                  qname, (Base)parent, this, parserWrapper);
              } else {
                element = ((FOMExtensionFactory)factory).newExtensionElement(
                  qname, (Base)parent, this); 
              }
          }
        }
      }
      if (element == null) {
        if (parserWrapper != null) {
          element = _newInstance(FOMExtensionElement.class, qname, (OMContainer)parent, parserWrapper);
        } else {
          element = _newInstance(FOMExtensionElement.class, qname, (OMContainer)parent);
        }
      }
    } else {
      element = newStringElement(qname, (Base) parent);
    }
    return element;
  }
  
  public Control newControl() {
    return newControl((Element)null);
  }
   
  public Control newControl(boolean draft) {
    return newControl(draft, null);
  }
  
  public Control newControl(Element parent) {
    return _newInstance(FOMControl.class, (OMContainer)parent);
  }
  
  public Control newControl(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMControl.class, qname, parent, parserWrapper);
  }

  public DateTime newPublished() {
    return newPublished((Element)null);
  }
  
  public DateTime newPublished(AtomDate dateTime) {
    return newPublished(dateTime, null);
  }
  
  public DateTime newPublished(Date date) {
    return newPublished(date, null);
  }
  
  public DateTime newPublished(String date) {
    return newPublished(date, null);
  }
  
  public DateTime newPublished(Calendar date) {
    return newPublished(date, null);
  }
  
  public DateTime newPublished(long date) {
    return newPublished(date, null);
  }
  
  public DateTime newPublished(Element parent) {
    return newDateTime(Constants.PUBLISHED, parent);
  }

  public DateTime newPublished(AtomDate dateTime, Element parent) {
    return newDateTime(Constants.PUBLISHED, dateTime, parent);
  }

  public DateTime newPublished(Date date, Element parent) {
    return newDateTime(Constants.PUBLISHED, date, parent);
  }

  public DateTime newPublished(String date, Element parent) {
    return newDateTime(Constants.PUBLISHED, date, parent);
  }

  public DateTime newPublished(Calendar date, Element parent) {
    return newDateTime(Constants.PUBLISHED, date, parent);
  }

  public DateTime newPublished(long date, Element parent) {
    return newDateTime(Constants.PUBLISHED, date, parent);
  }

  public DateTime newUpdated() {
    return newUpdated((Element)null);
  }
  
  public DateTime newUpdated(AtomDate dateTime) {
    return newUpdated(dateTime, null);
  }
  
  public DateTime newUpdated(Date date) {
    return newUpdated(date, null);
  }
  
  public DateTime newUpdated(String date) {
    return newUpdated(date, null);
  }
  
  public DateTime newUpdated(Calendar date) {
    return newUpdated(date, null);
  }
  
  public DateTime newUpdated(long date) {
    return newUpdated(date, null);
  }
  
  public DateTime newUpdated(Element parent) {
    return newDateTime(Constants.UPDATED, parent);
  }

  public DateTime newUpdated(AtomDate dateTime, Element parent) {
    return newDateTime(Constants.UPDATED, dateTime, parent);
  }

  public DateTime newUpdated(Date date, Element parent) {
    return newDateTime(Constants.UPDATED, date, parent);
  }

  public DateTime newUpdated(String date, Element parent) {
    return newDateTime(Constants.UPDATED, date, parent);
  }

  public DateTime newUpdated(Calendar date, Element parent) {
    return newDateTime(Constants.UPDATED, date, parent);
  }

  public DateTime newUpdated(long date, Element parent) {
    return newDateTime(Constants.UPDATED, date, parent);
  }

  public IRI newIcon() {
    return newIcon((Element)null);
  }
  
  public IRI newIcon(String uri) throws URISyntaxException {
    return newIcon(uri, null);
  }
  
  public IRI newIcon(URI uri) {
    return newIcon(uri, null);
  }
  
  public IRI newIcon(Element parent) {
    return newIRIElement(Constants.ICON, parent);
  }

  public IRI newIcon(URI uri, Element parent) {
    return newIRIElement(Constants.ICON, uri, parent);
  }

  public IRI newIcon(String URI, Element parent) throws URISyntaxException {
    return newIRIElement(Constants.ICON, URI, parent);
  }

  public IRI newLogo() {
    return newLogo((Element)null);
  }
  
  public IRI newLogo(String uri) throws URISyntaxException {
    return newLogo(uri, null);
  }
  
  public IRI newLogo(URI uri) {
    return newLogo(uri, null);
  }
  
  public IRI newLogo(Element parent) {
    return newIRIElement(Constants.LOGO, parent);
  }

  public IRI newLogo(URI uri, Element parent) {
    return newIRIElement(Constants.LOGO, uri, parent);
  }

  public IRI newLogo(String URI, Element parent) throws URISyntaxException {
    return newIRIElement(Constants.LOGO, URI, parent);
  }

  public IRI newUri() {
    return newUri((Element)null);
  }
  
  public IRI newUri(URI uri) {
    return newUri(uri, null);
  }
  
  public IRI newUri(String uri) throws URISyntaxException {
    return newUri(uri, null);
  }
  
  public IRI newUri(Element parent) {
    return newIRIElement(Constants.URI, parent);
  }

  public IRI newUri(URI uri, Element parent) {
    return newIRIElement(Constants.URI, uri, parent);
  }

  public IRI newUri(String URI, Element parent) throws URISyntaxException {
    return newIRIElement(Constants.URI, URI, parent);
  }

  public Person newAuthor() {
    return newAuthor((Element)null);
  }
  
  public Person newAuthor(
    String name, 
    String email, 
    String uri) 
      throws URISyntaxException {
    return newAuthor(name, email, uri, null);
  }
  
  public Person newAuthor(
    String name, 
    String email, 
    URI uri) {
      return newAuthor(name, email, uri, null);
  }
  
  public Person newAuthor(Element parent) {
    return newPerson(Constants.AUTHOR, parent);
  }

  public Person newAuthor(
    String name, 
    String email, 
    String uri, 
    Element parent) 
      throws URISyntaxException {
    return newPerson(Constants.AUTHOR, name, email, uri, parent);
  }

  public Person newAuthor(
    String name, 
    String email, 
    URI uri, 
    Element parent) {
      return newPerson(Constants.AUTHOR, name, email, uri, parent);
  }

  public Person newContributor() {
    return newContributor((Element)null);
  }

  public Person newContributor(
    String name, 
    String email, 
    String uri) 
      throws URISyntaxException {
    return newContributor(name, email, uri, null);
  }
  
  public Person newContributor(
    String name, 
    String email, 
    URI uri) {
      return newContributor(name, email, uri, null);
  }
  
  public Person newContributor(
    Element parent) {
      return newPerson(Constants.CONTRIBUTOR, parent);
  }

  public Person newContributor(
    String name, 
    String email, 
    String uri, 
    Element parent) 
      throws URISyntaxException {
    return newPerson(Constants.CONTRIBUTOR, name, email, uri, parent);
  }

  public Person newContributor(
    String name, 
    String email, 
    URI uri, 
    Element parent) {
      return newPerson(Constants.CONTRIBUTOR, name, email, uri, parent);
  }

  public Text newTitle() {
    return newTitle(Text.Type.TEXT);
  }
  
  public Text newTitle(String value) {
    return newTitle(value, Text.Type.TEXT);
  }
  
  public Text newTitle(Element parent) {
    return newTitle(Text.Type.TEXT, parent);
  }
  
  public Text newTitle(Text.Type type) {
    return newTitle(type, (Element)null);
  }
  
  public Text newTitle(Text.Type type, Element parent) {
    return newText(Constants.TITLE, type, parent);
  }
  
  public Text newTitle(String value, Text.Type type) {
    return newTitle(value, type, null);
  }
  
  public Text newTitle(String value, Text.Type type, Element parent) {
    Text text = newText(Constants.TITLE, type, parent);
    text.setValue(value);
    return text;
  }
  
  public Text newTitle(Div value) {
    return newTitle(value, null);
  }
  
  public Text newTitle(
    Div value,
    Element parent) {
      return newText(Constants.TITLE, value, parent);
  }

  public Text newSubtitle() {
    return newSubtitle(Text.Type.TEXT);
  }
  
  public Text newSubtitle(String value) {
    return newSubtitle(value, Text.Type.TEXT);
  }
  
  public Text newSubtitle(Element parent) {
    return newSubtitle(Text.Type.TEXT, parent);
  }
  
  public Text newSubtitle(Text.Type type) {
    return newSubtitle(type, (Element)null);
  }
  
  public Text newSubtitle(Text.Type type, Element parent) {
    return newText(Constants.SUBTITLE, type, parent);
  }
  
  public Text newSubtitle(String value, Text.Type type) {
    return newSubtitle(value, type, null);
  }
  
  public Text newSubtitle(String value, Text.Type type, Element parent) {
    Text text = newText(Constants.SUBTITLE, type, parent);
    text.setValue(value);
    return text;
  }
  
  public Text newSubtitle(Div value) {
    return newSubtitle(value, null);
  }
  
  public Text newSubtitle(
    Div value,
    Element parent) {
      return newText(Constants.SUBTITLE, value, parent);
  }

  public Text newSummary() {
    return newSummary(Text.Type.TEXT);
  }
  
  public Text newSummary(String value) {
    return newSummary(value, Text.Type.TEXT);
  }
  
  public Text newSummary(Element parent) {
    return newSummary(Text.Type.TEXT, parent);
  }
  
  public Text newSummary(Text.Type type) {
    return newSubtitle(type, (Element)null);
  }
  
  public Text newSummary(Text.Type type, Element parent) {
    return newText(Constants.SUMMARY, type, parent);
  }
  
  public Text newSummary(String value, Text.Type type) {
    return newSummary(value, type, null);
  }
  
  public Text newSummary(String value, Text.Type type, Element parent) {
    Text text = newText(Constants.SUMMARY, type, parent);
    text.setValue(value);
    return text;
  }
  
  public Text newSummary(Div value) {
    return newSummary(value, null);
  }
  
  public Text newSummary(
    Div value,
    Element parent) {
      return newText(Constants.SUMMARY, value, parent);
  }

  public Text newRights() {
    return newRights(Text.Type.TEXT);
  }
  
  public Text newRights(String value) {
    return newRights(value, Text.Type.TEXT);
  }
  
  public Text newRights(Element parent) {
    return newRights(Text.Type.TEXT, parent);
  }
  
  public Text newRights(Text.Type type) {
    return newRights(type, (Element)null);
  }
  
  public Text newRights(Text.Type type, Element parent) {
    return newText(Constants.RIGHTS, type, parent);
  }
  
  public Text newRights(String value, Text.Type type) {
    return newRights(value, type, null);
  }
  
  public Text newRights(String value, Text.Type type, Element parent) {
    Text text = newText(Constants.RIGHTS, type, parent);
    text.setValue(value);
    return text;
  }
  
  public Text newRights(Div value) {
    return newRights(value, null);
  }
  
  public Text newRights(
    Div value,
    Element parent) {
      return newText(Constants.RIGHTS, value, parent);
  }

  public StringElement newName() {
    return newName((Element)null);
  }
  
  public StringElement newName(String value) {
    return newName(value, null);
  }
  
  public StringElement newName(Element parent) {
    return newStringElement(Constants.NAME, parent);
  }

  public StringElement newName(String value, Element parent) {
    return newStringElement(Constants.NAME, value, parent);
  }

  public StringElement newEmail() {
    return newEmail((Element)null);
  }
  
  public StringElement newEmail(String value) {
    return newEmail(value, null);
  }
  
  public StringElement newEmail(Element parent) {
    return newStringElement(Constants.EMAIL, parent);
  }

  public StringElement newEmail(String value, Element parent) {
    return newStringElement(Constants.EMAIL, value, parent);
  }

  public Control newControl(boolean draft, Element parent) {
    Control control = newControl(parent);
    control.setDraft(draft);
    return control;
  }

  public Div newDiv() {
    return newDiv(null);
  }
  
  public Div newDiv(Base parent) {
    return _newInstance(FOMDiv.class, Constants.DIV, (OMContainer)parent);
  }
  
  public Div newDiv(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMDiv.class, qname, parent, parserWrapper);
  }
  
  public Element newElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMElement.class, qname, parent, parserWrapper);
  }

  private boolean isContent(QName qname) {
    return CONTENT.equals(qname);
  }
  
  private boolean isText(QName qname) {
    return TITLE.equals(qname) ||
           SUMMARY.equals(qname) ||
           SUBTITLE.equals(qname) ||
           RIGHTS.equals(qname);
  }
  
  @SuppressWarnings("unchecked")
  protected OMElement createElement(
      QName qname, 
      OMContainer parent, 
      OMFactory factory,
      Object objecttype) {
      OMElement element = null;
      OMNamespace namespace = 
        this.createOMNamespace(
          qname.getNamespaceURI(), 
          qname.getPrefix());
      if (qclasses.containsKey(qname)) {
        if (isContent(qname)) {
          Content.Type type = (Content.Type)objecttype;
          element = (OMElement) _newInstance(
            qclasses.get(qname), type, qname.getLocalPart(), namespace, parent);
        } else if (isText(qname)) {
          Text.Type type = (Text.Type)objecttype;
          element = (OMElement) _newInstance(
            qclasses.get(qname), type, qname.getLocalPart(), namespace, parent);
        } else {
          element = (OMElement) _newInstance(
            qclasses.get(qname), qname.getLocalPart(), namespace, parent);
        }
      } else if (parent instanceof ExtensibleElement || 
                 parent instanceof Document) {
        if (isSimpleExtension(qname)) {
          element = (OMElement) _newInstance(
            FOMStringElement.class, qname.getLocalPart(), namespace, parent);
        } else {
          element = (OMElement) newExtensionElement(qname, parent);
        }
      }
      return element;
    }
  
  @SuppressWarnings("unchecked")
  protected OMElement createElement(
    QName qname, 
    OMContainer parent, 
    FOMBuilder builder) {
    OMElement element = null;    
    if (qclasses.containsKey(qname)) {
      if (isContent(qname)) {
        Content.Type type = builder.getContentType();
        element = (OMElement) _newInstance(qclasses.get(qname), qname, type, parent, builder);
      } else if (isText(qname)) {
        Text.Type type = builder.getTextType();
        element = (OMElement) _newInstance(qclasses.get(qname), type, qname, parent, builder);
      } else {
        element = (OMElement) _newInstance(qclasses.get(qname), qname, parent, builder);
      }
    } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
      if (isSimpleExtension(qname)) {
        element = (OMElement) newStringElement(qname, parent, builder);
      } else {
        element = (OMElement) newExtensionElement(qname, parent, builder);
      }
    }
    return element;
  }

  public void registerAsSimpleExtension(QName qname) {
    if (simpleExtensions == null) 
      simpleExtensions = new ArrayList<QName>();
    if (!simpleExtensions.contains(qname)) simpleExtensions.add(qname);
  }
    
  public boolean isSimpleExtension(QName qname) {
    if (simpleExtensions == null) 
      simpleExtensions = new ArrayList<QName>();
    return simpleExtensions.contains(qname);
  }

  @SuppressWarnings("unchecked")
  public <T extends Base>void registerAlternative(Class<T> base, Class<? extends T> extension) {
    if (!base.isAssignableFrom(extension))
      throw new IllegalArgumentException("The extension must extend the base");
    if (alternatives == null) alternatives = new HashMap<Class,Class>();
    if (extension != null)
      alternatives.put(base, extension);
    else if (alternatives.containsKey(base))
      alternatives.remove(base);
  }
  
  // Internals for Alternative creation
  
  private Class _getAlternative(Class _class) {
    if (alternatives == null) alternatives = new HashMap<Class,Class>();
    return (alternatives.containsKey(_class)) ? alternatives.get(_class) : _class;
  }
  
  private static final Class[] CONSTRUCTOR1 = new Class[] {
    QName.class, 
    OMContainer.class,
    OMFactory.class,
    OMXMLParserWrapper.class};
  
  private static final Class[] CONSTRUCTOR2 = new Class[] {
    OMContainer.class,
    OMFactory.class
  };
  
  private static final Class[] CONSTRUCTOR3 = new Class[] {
    QName.class,
    Content.Type.class,
    OMContainer.class,
    OMFactory.class,
    OMXMLParserWrapper.class
  };

  private static final Class[] CONSTRUCTOR4 = new Class[] {
    Content.Type.class,
    OMContainer.class,
    OMFactory.class
  }; 
  
  private static final Class[] CONSTRUCTOR5 = new Class[] {
    QName.class,
    OMContainer.class,
    OMFactory.class
  }; 
  
  private static final Class[] CONSTRUCTOR6 = new Class[] {
    Text.Type.class,
    QName.class,
    OMContainer.class,
    OMFactory.class,
    OMXMLParserWrapper.class
  };

  private static final Class[] CONSTRUCTOR7 = new Class[] {
    Text.Type.class,
    QName.class,
    OMContainer.class,
    OMFactory.class
  }; 
  
  private static final Class[] CONSTRUCTOR8 = new Class[] {
    String.class, 
    OMNamespace.class, 
    OMContainer.class, 
    OMFactory.class
  };
  
  private static final Class[] CONSTRUCTOR9 = new Class[] {
    OMFactory.class
  };
  
  private static final Class[] CONSTRUCTOR10 = new Class[] {
    OMFactory.class,
    OMXMLParserWrapper.class
  };

  private static final Class[] CONSTRUCTOR11 = new Class[] {
    Content.Type.class,
    String.class, 
    OMNamespace.class, 
    OMContainer.class, 
    OMFactory.class
  };

  private static final Class[] CONSTRUCTOR12 = new Class[] {
    Text.Type.class,
    String.class, 
    OMNamespace.class, 
    OMContainer.class, 
    OMFactory.class
  };
  
  @SuppressWarnings("unchecked")
  private <T extends Base>T _newInstance(
    Class<T> _class,
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      Object[] args = new Object[] {qname, parent, this, parserWrapper}; 
      return _newInstance(_class, CONSTRUCTOR1, args);
  }
  
  @SuppressWarnings("unused")
  private <T extends Base>T _newInstance(
    Class<T> _class,
    OMContainer parent) {
      Object[] args = new Object[] {parent, this};
      return _newInstance(_class, CONSTRUCTOR2, args);
  }
  
  private <T extends Base>T _newInstance(
    Class<T> _class,
    QName qname,
    Content.Type type,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      Object[] args = new Object[] {qname, type, parent, this, parserWrapper};
      return _newInstance(_class, CONSTRUCTOR3, args);
  }

  @SuppressWarnings("unused")
  private <T extends Base>T _newInstance(
    Class<T> _class,
    Content.Type type,
    OMContainer parent) {
      Object[] args = new Object[] {type, parent, this};
      return _newInstance(_class, CONSTRUCTOR4, args);
  }

  @SuppressWarnings("unused")
  private <T extends Base>T _newInstance(
    Class<T> _class,
    QName qname,
    OMContainer parent) {
      Object[] args = new Object[] {qname, parent, this};
      return _newInstance(_class, CONSTRUCTOR5, args);
  }
  
  private <T extends Base>T _newInstance(
    Class<T> _class,
    Text.Type type,
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      Object[] args = new Object[] {type, qname, parent, this, parserWrapper};
      return _newInstance(_class, CONSTRUCTOR6, args);
  }

  private <T extends Base>T _newInstance(
    Class<T> _class,
    Text.Type type,
    QName qname,
    OMContainer parent) {
      Object[] args = new Object[] {type, qname, parent, this};
      return _newInstance(_class, CONSTRUCTOR7, args);
  }

  private <T extends Base>T _newInstance(
    Class<T> _class,
    String localPart,
    OMNamespace namespace,
    OMContainer parent) {
      Object[] args = new Object[] {localPart, namespace, parent, this};
      return _newInstance(_class, CONSTRUCTOR8, args);
  }

  private <T extends Base>T _newInstance(
    Class<T> _class) {
      Object[] args = new Object[] {this};
      return _newInstance(_class, CONSTRUCTOR9, args);
  }

  private <T extends Base>T _newInstance(
    Class<T> _class,
    OMXMLParserWrapper parserWrapper) {
      Object[] args = new Object[] {this, parserWrapper};
      return _newInstance(_class, CONSTRUCTOR10, args);
  }
  
  private <T extends Base>T _newInstance(
    Class<T> _class,
    Content.Type type,
    String localPart,
    OMNamespace namespace,
    OMContainer parent) {
      Object[] args = new Object[] {type, localPart, namespace, parent, this};
      return _newInstance(_class, CONSTRUCTOR11, args);
  }
  
  private <T extends Base>T _newInstance(
    Class<T> _class,
    Text.Type type,
    String localPart,
    OMNamespace namespace,
    OMContainer parent) {
      Object[] args = new Object[] {type, localPart, namespace, parent, this};
      return _newInstance(_class, CONSTRUCTOR12, args);
  }
  
  /**
   * This likely has some class loader issues we need to be concerned about
   */
  @SuppressWarnings("unchecked")
  private <T extends Base>T _newInstance(
    Class<T> _class, 
    Class[] argtypes, 
    Object[] args) {
    Class _altclass = _getAlternative(_class);
    try {
      return (T) _altclass.getConstructor(argtypes).newInstance(args);
    } catch (Exception e) {
      try {
        return (T) _class.getConstructor(argtypes).newInstance(args);
      } catch (Exception ex) {}
    }
    return null;
  }
}
