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
 
import javax.activation.MimeTypeParseException;

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
import org.apache.abdera.model.InReplyTo;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.StringElement;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Total;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.util.Constants;
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
  
  public Category newCategory(
    Element parent) {
      return _newInstance(FOMCategory.class, (OMContainer)parent);
  }

  public Category newCategory(
    URI scheme, 
    String term, 
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
  
  public Content newContent(
    Type type, 
    Element parent) {
      if (type == null) type = Content.Type.TEXT;
      return _newInstance(FOMContent.class, type, (OMContainer)parent);
  }
  
  public Content newHtmlContent(
    String value, 
    Element parent) {
      Content content = newContent(Content.Type.HTML, parent);
      content.setValue(value);
      return content;
  }

  public Content newMediaContent(
    MimeType mediaType, 
    URI src, 
    DataHandler dataHandler, 
    Element parent) {
      Content content = newContent(Content.Type.MEDIA, mediaType, parent);
      if (src != null) content.setSrc(src);
      if (dataHandler != null) content.setDataHandler(dataHandler);
      return content;
  }

  public Content newTextContent(
    String value, 
    Element parent) {
      Content content = newContent(Content.Type.TEXT, parent);
      content.setValue(value);
      return content;
  }

  public Content newXhtmlContent(
    Div value, Element parent) {
      Content content = 
        newContent(
          Content.Type.XHTML, 
          parent);
      if (value != null)
        content.setValueElement(value);
      return content;
  }

  public Content newXmlContent(
    MimeType mediaType, 
    URI src, 
    ExtensionElement value, Element parent) {
      Content content = 
        newContent(
          Content.Type.XML, 
          mediaType, 
          parent);
      if (src != null) content.setSrc(src);
      if (value != null) content.setValueElement(value);
      return content;
  }

  public Content newContent(
    Type type, 
    MimeType mediaType, 
    Element parent) {
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
  
  public Generator newGenerator(
      Element parent) {
    return _newInstance(FOMGenerator.class, (OMContainer)parent);
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
    Text.Type type, 
    Element parent) {
    if (type == null) type = Text.Type.TEXT;
    return _newInstance(FOMText.class,type, qname,(OMContainer)parent);
  }
  
  public Text newHtmlText(
    QName qname, 
    String value, 
    Element parent) {
      Text text = newText(qname, Text.Type.HTML, parent);
      text.setValue(value);
      return text;
  }

  public Text newTextText(
    QName qname, 
    String value, 
    Element parent) {
      Text text = newText(qname, Text.Type.TEXT, parent);
      text.setValue(value);
      return text;
  }

  public Text newXhtmlText(
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
  
  public StringElement newStringElement(
    QName qname, 
    Element parent) {
      return _newInstance(FOMStringElement.class, qname, (OMContainer)parent);
  }

  public StringElement newStringElement(
    QName qname, 
    Document parent) {
      return _newInstance(FOMStringElement.class, qname, (OMContainer)parent);
  }
  
  public StringElement newStringElement(
    QName qname, 
    String value,
    Element parent) {
    StringElement el = newStringElement(qname, parent);
    if (value != null)
      el.setValue(value);
    return el;
  }

  public StringElement newStringElement(
    QName qname, 
    String value,
    Document parent) {
    StringElement el = newStringElement(qname, parent);
    if (value != null)
      el.setValue(value);
    return el;
  }
  
  public ExtensionElement newExtensionElement(
    QName qname, 
    Base parent) {
    return newExtensionElement(qname, (OMContainer)parent);
  }
  
  private ExtensionElement newExtensionElement(
    QName qname, 
    OMContainer parent) {
      ExtensionElement element = null;
      if (!isSimpleExtension(qname)) {
        List<ExtensionFactory> factories = 
          org.apache.abdera.util.ServiceUtil.loadExtensionFactories(
            "org.apache.abdera.factory.ExtensionFactory");
        if (factories != null) {
          for (ExtensionFactory factory : factories) {
            if ((factory.getNamespace() == null) ? 
                  qname.getNamespaceURI() == null : 
                    factory.getNamespace().equals(
                      qname.getNamespaceURI())) {
              if (parent instanceof Element)
                element = factory.newExtensionElement(qname, (Element)parent, this);
              else if (parent instanceof Document)
                element = factory.newExtensionElement(qname, (Document)parent, this);
            }
          }
        }
        if (element == null) {
          element = 
            _newInstance(FOMExtensionElement.class, qname, (OMContainer)parent);
        }
      } else {
        if (parent instanceof Element)
          element = newStringElement(qname, (Element)parent);
        else if (parent instanceof Document)
          element = newStringElement(qname, (Document)parent);
        else 
          element = newStringElement(qname, (Element)null);
      }
      return element;
  }

  public ExtensionElement newExtensionElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
    ExtensionElement element = null;
    List<ExtensionFactory> factories = 
      org.apache.abdera.util.ServiceUtil.loadExtensionFactories(
        "org.apache.abdera.factory.ExtensionFactory");
    if (factories != null) {
      for (ExtensionFactory factory : factories) {
        if (factory instanceof FOMExtensionFactory &&
            (factory.getNamespace() == null) ? 
              qname.getNamespaceURI() == null : 
                factory.getNamespace().equals(
                  qname.getNamespaceURI())) {
          if (parent instanceof Element)
            element = 
              ((FOMExtensionFactory)factory).newExtensionElement(
                qname, (Element)parent, this, parserWrapper);
          else if (parent instanceof Document)
            element = 
              ((FOMExtensionFactory)factory).newExtensionElement(
                qname, (Document)parent, this, parserWrapper);
        }
      }
    }
    if (element == null) {
      element = 
        _newInstance(FOMExtensionElement.class, qname, (OMContainer)parent, parserWrapper);
    }
    return element;
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

  public IRI newIcon(Element parent) {
    return newIRIElement(Constants.ICON, parent);
  }

  public IRI newIcon(URI uri, Element parent) {
    return newIRIElement(Constants.ICON, uri, parent);
  }

  public IRI newIcon(String URI, Element parent) throws URISyntaxException {
    return newIRIElement(Constants.ICON, URI, parent);
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

  public IRI newUri(Element parent) {
    return newIRIElement(Constants.URI, parent);
  }

  public IRI newUri(URI uri, Element parent) {
    return newIRIElement(Constants.URI, uri, parent);
  }

  public IRI newUri(String URI, Element parent) throws URISyntaxException {
    return newIRIElement(Constants.URI, URI, parent);
  }

  public Person newAuthor(Element parent) {
    return newPerson(Constants.AUTHOR, parent);
  }

  public Person newAuthor(String name, String email, String uri, Element parent) throws URISyntaxException {
    return newPerson(Constants.AUTHOR, name, email, uri, parent);
  }

  public Person newAuthor(String name, String email, URI uri, Element parent) {
    return newPerson(Constants.AUTHOR, name, email, uri, parent);
  }

  public Person newContributor(Element parent) {
    return newPerson(Constants.CONTRIBUTOR, parent);
  }

  public Person newContributor(String name, String email, String uri, Element parent) throws URISyntaxException {
    return newPerson(Constants.CONTRIBUTOR, name, email, uri, parent);
  }

  public Person newContributor(String name, String email, URI uri, Element parent) {
    return newPerson(Constants.CONTRIBUTOR, name, email, uri, parent);
  }

  public Text newTextTitle(String value, Element parent) {
    return newTextText(Constants.TITLE, value, parent);
  }

  public Text newHtmlTitle(String value, Element parent) {
    return newHtmlText(Constants.TITLE, value, parent);
  }

  public Text newXhtmlTitle(
    Div value,
    Element parent) {
      return newXhtmlText(Constants.TITLE, value, parent);
  }

  public Text newTextSubtitle(String value, Element parent) {
    return newTextText(Constants.SUBTITLE, value, parent);
  }

  public Text newHtmlSubtitle(String value, Element parent) {
    return newHtmlText(Constants.SUBTITLE, value, parent);
  }

  public Text newXhtmlSubtitle(Div value, Element parent) {
    return newXhtmlText(Constants.SUBTITLE, value, parent);
  }

  public Text newTextSummary(String value, Element parent) {
    return newTextText(Constants.SUMMARY, value, parent);
  }

  public Text newHtmlSummary(String value, Element parent) {
    return newHtmlText(Constants.SUMMARY, value, parent);
  }

  public Text newXhtmlSummary(Div value, Element parent) {
    return newXhtmlText(Constants.SUMMARY, value, parent);
  }

  public Text newTextRights(String value, Element parent) {
    return newTextText(Constants.RIGHTS, value, parent);
  }

  public Text newHtmlRights(String value, Element parent) {
    return newHtmlText(Constants.RIGHTS, value, parent);
  }

  public Text newXhtmlRights(Div value, Element parent) {
    return newXhtmlText(Constants.RIGHTS, value, parent);
  }

  public StringElement newName(Element parent) {
    return newStringElement(Constants.NAME, parent);
  }

  public StringElement newName(String value, Element parent) {
    return newStringElement(Constants.NAME, value, parent);
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

  public Total newTotal(Element parent) {
    return _newInstance(FOMTotal.class, (OMContainer)parent);
  }
  
  public Total newTotal(int totalResponse, Element parent) {
    Total total = newTotal(parent);
    total.setValue(totalResponse);
    return total;
  }
  
  public Total newTotal(
      QName qname, 
      OMContainer parent, 
      OMXMLParserWrapper parserWrapper) {
    return _newInstance(FOMTotal.class, qname, parent, parserWrapper);
  }
  
  public InReplyTo newInReplyTo(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return _newInstance(FOMInReplyTo.class, qname, parent, parserWrapper);
  }
  
  public InReplyTo newInReplyTo(Element parent) {
    return _newInstance(FOMInReplyTo.class, (OMContainer)parent);
  }

  public InReplyTo newInReplyTo(URI ref, Element parent) {
    InReplyTo replyTo = newInReplyTo(parent);
    if (ref != null) replyTo.setRef(ref);
    return replyTo;
  }

  public InReplyTo newInReplyTo(String ref, Element parent) throws URISyntaxException {
    return (ref != null) ? newInReplyTo(new URI(ref), parent) : newInReplyTo(parent);
  }

  public InReplyTo newInReplyTo(
    URI ref, 
    URI source, 
    URI href, 
    MimeType type, 
    Element parent) {
      InReplyTo replyTo = newInReplyTo(parent);
      if (ref != null) replyTo.setRef(ref);
      if (source != null) replyTo.setSource(source);
      if (href != null) replyTo.setHref(href);
      if (type != null) replyTo.setMimeType(type);
      return replyTo;
  }

  public InReplyTo newInReplyTo(
    String ref, 
    String source, 
    String href, 
    String type, 
    Element parent) 
      throws URISyntaxException, 
             MimeTypeParseException {
    return newInReplyTo(
      (ref != null) ? new URI(ref) : null,
      (source != null) ? new URI(source) : null,
      (href != null) ? new URI(href) : null,
      (type != null) ? new MimeType(type) : null,
      parent
    );
  }

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
      if (FEED.equals(qname)) {
        element = (OMElement) _newInstance(FOMFeed.class, qname.getLocalPart(), namespace, parent);
      } else if (SERVICE.equals(qname)) {
        element = (OMElement) _newInstance(FOMService.class, qname.getLocalPart(), namespace, parent);
      } else if (ENTRY.equals(qname)) {
        element = (OMElement) _newInstance(FOMEntry.class, qname.getLocalPart(), namespace, parent);
      } else if (AUTHOR.equals(qname)) {
        element = (OMElement) _newInstance(FOMPerson.class, qname.getLocalPart(), namespace, parent);
      } else if (CATEGORY.equals(qname)) {
        element = (OMElement) _newInstance(FOMCategory.class, qname.getLocalPart(), namespace, parent);
      } else if (CONTENT.equals(qname)) {
        Content.Type type = (Content.Type) objecttype;
        element = (OMElement) _newInstance(FOMContent.class, type, qname.getLocalPart(), namespace, parent);
      } else if (CONTRIBUTOR.equals(qname)) {
        element = (OMElement) _newInstance(FOMPerson.class, qname.getLocalPart(), namespace, parent);
      } else if (GENERATOR.equals(qname)) {
        element = (OMElement) _newInstance(FOMGenerator.class, qname.getLocalPart(), namespace, parent);
      } else if (ICON.equals(qname)) {
        element = (OMElement) _newInstance(FOMIRI.class, qname.getLocalPart(), namespace, parent);
      } else if (ID.equals(qname)) {
        element = (OMElement) _newInstance(FOMIRI.class, qname.getLocalPart(), namespace, parent);
      } else if (LOGO.equals(qname)) {
        element = (OMElement) _newInstance(FOMIRI.class, qname.getLocalPart(), namespace, parent);
      } else if (LINK.equals(qname)) {
        element = (OMElement) _newInstance(FOMLink.class, qname.getLocalPart(), namespace, parent);
      } else if (PUBLISHED.equals(qname)) {
        element = (OMElement) _newInstance(FOMDateTime.class, qname.getLocalPart(), namespace, parent);
      } else if (RIGHTS.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) _newInstance(FOMText.class, type, qname.getLocalPart(), namespace, parent);
      } else if (SOURCE.equals(qname)) {
        element = (OMElement) _newInstance(FOMSource.class, qname.getLocalPart(), namespace, parent);
      } else if (SUBTITLE.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) _newInstance(FOMText.class, type, qname.getLocalPart(), namespace, parent);
      } else if (SUMMARY.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) _newInstance(FOMText.class, type, qname.getLocalPart(), namespace, parent);
      } else if (TITLE.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) _newInstance(FOMText.class, type, qname.getLocalPart(), namespace, parent);
      } else if (UPDATED.equals(qname)) {
        element = (OMElement) _newInstance(FOMDateTime.class, qname.getLocalPart(), namespace, parent);          
      } else if (WORKSPACE.equals(qname)) {
        element = (OMElement) _newInstance(FOMWorkspace.class, qname.getLocalPart(), namespace, parent);
      } else if (COLLECTION.equals(qname)) {
        element = (OMElement) _newInstance(FOMCollection.class, qname.getLocalPart(), namespace, parent);
      } else if (NAME.equals(qname)) {
        element = (OMElement) _newInstance(FOMStringElement.class, qname.getLocalPart(), namespace, parent);
      } else if (EMAIL.equals(qname)) {
        element = (OMElement) _newInstance(FOMStringElement.class, qname.getLocalPart(), namespace, parent);
      } else if (URI.equals(qname)) {
        element = (OMElement) _newInstance(FOMIRI.class, qname.getLocalPart(), namespace, parent);
      } else if (CONTROL.equals(qname)) {
        element = (OMElement) _newInstance(FOMControl.class, qname.getLocalPart(), namespace, parent);
      } else if (DIV.equals(qname)) {
        element = (OMElement) _newInstance(FOMDiv.class, qname.getLocalPart(), namespace, parent);
      } else if (IN_REPLY_TO.equals(qname)) {
        element = (OMElement) _newInstance(FOMInReplyTo.class, qname.getLocalPart(), namespace, parent);
      } else if (THRTOTAL.equals(qname)) {
        element = (OMElement) _newInstance(FOMTotal.class, qname.getLocalPart(), namespace, parent);
      } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
        if (isSimpleExtension(qname)) {
          element = (OMElement) _newInstance(FOMStringElement.class, qname.getLocalPart(), namespace, parent);
        } else {
          element = (OMElement) newExtensionElement(qname, parent);
        }
      }
      return element;
    }
  
  protected OMElement createElement(
    QName qname, 
    OMContainer parent, 
    FOMBuilder builder) {
    OMElement element = null;
    if (FEED.equals(qname)) {
      element = (OMElement) newFeed(qname, parent, builder);
    } else if (SERVICE.equals(qname)) {
      element = (OMElement) newService(qname, parent, builder);
    } else if (ENTRY.equals(qname)) {
      element = (OMElement) newEntry(qname, parent, builder);
    } else if (AUTHOR.equals(qname)) {
      element = (OMElement) newPerson(qname, parent, builder);
    } else if (CATEGORY.equals(qname)) {
      element = (OMElement) newCategory(qname, parent, builder);
    } else if (CONTENT.equals(qname)) {
      Content.Type type = builder.getContentType();
      element = (OMElement) newContent(qname, type, parent, builder);
    } else if (CONTRIBUTOR.equals(qname)) {
      element = (OMElement) newPerson(qname, parent, builder);
    } else if (GENERATOR.equals(qname)) {
      element = (OMElement) newGenerator(qname, parent, builder);
    } else if (ICON.equals(qname)) {
      element = (OMElement) newURIElement(qname, parent, builder);
    } else if (ID.equals(qname)) {
      element = (OMElement) newID(qname, parent, builder);
    } else if (LOGO.equals(qname)) {
      element = (OMElement) newURIElement(qname, parent, builder);
    } else if (LINK.equals(qname)) {
      element = (OMElement) newLink(qname, parent, builder);
    } else if (PUBLISHED.equals(qname)) {
      element = (OMElement) newDateTimeElement(qname, parent, builder);
    } else if (RIGHTS.equals(qname)) {
      Text.Type type = builder.getTextType();
      element = (OMElement) newText(qname, type, parent, builder);
    } else if (SOURCE.equals(qname)) {
      element = (OMElement) newSource(qname, parent, builder);
    } else if (SUBTITLE.equals(qname)) {
      Text.Type type = builder.getTextType();
      element = (OMElement) newText(qname, type, parent, builder);
    } else if (SUMMARY.equals(qname)) {
      Text.Type type = builder.getTextType();
      element = (OMElement) newText(qname, type, parent, builder);
    } else if (TITLE.equals(qname)) {
      Text.Type type = builder.getTextType();
      element = (OMElement) newText(qname, type, parent, builder);
    } else if (UPDATED.equals(qname)) {
      element = (OMElement) newDateTimeElement(qname, parent, builder);          
    } else if (WORKSPACE.equals(qname)) {
      element = (OMElement) newWorkspace(qname, parent, builder);
    } else if (COLLECTION.equals(qname)) {
      element = (OMElement) newCollection(qname, parent, builder);
    } else if (NAME.equals(qname)) {
      element = (OMElement) newStringElement(qname, parent, builder);
    } else if (EMAIL.equals(qname)) {
      element = (OMElement) newStringElement(qname, parent, builder);
    } else if (URI.equals(qname)) {
      element = (OMElement) newURIElement(qname, parent, builder);
    } else if (CONTROL.equals(qname)) {
      element = (OMElement) newControl(qname, parent, builder);
    } else if (DIV.equals(qname)) {
      element = (OMElement) newDiv(qname, parent, builder);
    } else if (IN_REPLY_TO.equals(qname)) {
      element = (OMElement) newInReplyTo(qname, parent, builder);
    } else if (THRTOTAL.equals(qname)) {
      element = (OMElement) newTotal(qname, parent, builder);
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
    Content.Type.class,
    String.class, 
    OMNamespace.class, 
    OMContainer.class, 
    OMFactory.class
  };

  private static final Class[] CONSTRUCTOR10 = new Class[] {
    Text.Type.class,
    String.class, 
    OMNamespace.class, 
    OMContainer.class, 
    OMFactory.class
  };
  
  private static final Class[] CONSTRUCTOR11 = new Class[] {
    OMFactory.class
  };
  
  private static final Class[] CONSTRUCTOR12 = new Class[] {
    OMFactory.class,
    OMXMLParserWrapper.class
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
    Class<T> _class,
    Content.Type type,
    String localPart,
    OMNamespace namespace,
    OMContainer parent) {
      Object[] args = new Object[] {type, localPart, namespace, parent, this};
      return _newInstance(_class, CONSTRUCTOR9, args);
  }
  
  private <T extends Base>T _newInstance(
    Class<T> _class,
    Text.Type type,
    String localPart,
    OMNamespace namespace,
    OMContainer parent) {
      Object[] args = new Object[] {type, localPart, namespace, parent, this};
      return _newInstance(_class, CONSTRUCTOR10, args);
  }
  
  private <T extends Base>T _newInstance(
    Class<T> _class) {
      Object[] args = new Object[] {this};
      return _newInstance(_class, CONSTRUCTOR11, args);
  }

  private <T extends Base>T _newInstance(
    Class<T> _class,
    OMXMLParserWrapper parserWrapper) {
      Object[] args = new Object[] {this, parserWrapper};
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
