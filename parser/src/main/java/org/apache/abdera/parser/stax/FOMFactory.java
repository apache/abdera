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
import java.util.List;

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
  
  public Parser newParser() {
    return new FOMParser();
  }
  
  public <T extends Element>Document<T> newDocument() {
    return new FOMDocument<T>(this);
  }
  
  public <T extends Element>Document<T> newDocument(
    OMXMLParserWrapper parserWrapper) {
      return new FOMDocument<T>(parserWrapper, this);
  }
  
  public <T extends Element>Document<T> newDocument(
    T root, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMDocument<T>((OMElement)root, parserWrapper, this);
  }
  
  public Service newService(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMService(qname,parent, this, parserWrapper);
  }

  public Service newService(
    Base parent) {
    return new FOMService((OMContainer)parent, this);
  }

  public Workspace newWorkspace(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMWorkspace(qname,parent, this, parserWrapper);
  }
  
  public Workspace newWorkspace(
    Element parent) {
    return new FOMWorkspace((OMContainer)parent, this);
  }

  public Collection newCollection(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMCollection(qname, parent, this, parserWrapper);
  }
  
  public Collection newCollection(
    Element parent) {
    return new FOMCollection((OMContainer)parent, this);
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
      return new FOMFeed(qname,parent, this, parserWrapper);
  }
  
  public Feed newFeed(
    Base parent) {
    return new FOMFeed((OMContainer)parent, this);
  }

  public Entry newEntry(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMEntry(qname,parent, this, parserWrapper);
  }
  
  public Entry newEntry(
    Base parent) {
      return new FOMEntry((OMContainer)parent, this);
  }

  public Category newCategory(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMCategory(qname,parent, this, parserWrapper);
  }
  
  public Category newCategory(
    Element parent) {
      return new FOMCategory((OMContainer)parent, this);
  }

  public Category newCategory(
    URI scheme, 
    String term, 
    String label, 
    Element parent) {
    Category category = new FOMCategory((OMContainer)parent, this);
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
      Content content = null;
      if (Type.TEXT.equals(type)) {
        content = new FOMContent(qname,Content.Type.TEXT, parent, this, parserWrapper);
      } else if (Type.HTML.equals(type)) {
        content = new FOMContent(qname,Content.Type.HTML, parent, this, parserWrapper);
      } else if (Type.XHTML.equals(type)) {
        content = new FOMContent(qname,Content.Type.XHTML, parent, this, parserWrapper);
      } else if (Type.XML.equals(type)) {
        content = new FOMContent(qname,Content.Type.XML, parent, this, parserWrapper);
      } else if (Type.MEDIA.equals(type)) {
        content = new FOMContent(qname,Content.Type.MEDIA, parent, this, parserWrapper);
      }
      return content;
  }
  
  public Content newContent(
    Type type, 
    Element parent) {
      Content content = null;
      if (Type.TEXT.equals(type)) {
        content = new FOMContent(Content.Type.TEXT, (OMContainer)parent, this);
      } else if (Type.HTML.equals(type)) {
        content = new FOMContent(Content.Type.HTML, (OMContainer)parent, this);
      } else if (Type.XHTML.equals(type)) {
        content = new FOMContent(Content.Type.XHTML, (OMContainer)parent, this);
      } else if (Type.XML.equals(type)) {
        content = new FOMContent(Content.Type.XML, (OMContainer)parent, this);
      } else if (Type.MEDIA.equals(type)) {
        content = new FOMContent(Content.Type.MEDIA, (OMContainer)parent, this);
      }
      return content;
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
      return new FOMDateTime(
          qname, parent, this, parserWrapper);
  }
  
  public DateTime newDateTime(
    QName qname, 
    Element parent) {
    return new FOMDateTime(qname, (OMContainer)parent, this);
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
      return new FOMGenerator(qname,parent, this, parserWrapper);
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
    return new FOMGenerator((OMContainer)parent, this);
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
      return new FOMIRI(qname, parent, this, parserWrapper);
  }
  
  public IRI newID(
    Element parent) {
      return new FOMIRI(Constants.ID, (OMContainer)parent, this);
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
      return new FOMIRI(qname, parent, this, parserWrapper);
  }
  
  public IRI newIRIElement(
    QName qname, 
    Element parent) {
    return new FOMIRI(qname, (OMContainer)parent, this);
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
      return new FOMLink(qname,parent, this, parserWrapper);
  }
  
  public Link newLink(
    Element parent) {
    return new FOMLink((OMContainer)parent, this);
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
      return new FOMPerson(qname, parent, this, parserWrapper);
  }
  
  public Person newPerson(
    QName qname, 
    Element parent) {
      return new FOMPerson(qname, (OMContainer)parent, this);
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
      return new FOMSource(qname, parent, this, parserWrapper);
  }
  
  public Source newSource(
      Element parent) {
    return new FOMSource((OMContainer)parent, this);
  }

  public Text newText(
    QName qname,
    Text.Type type,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
    Text text = null;
    if (Text.Type.TEXT.equals(type)) {
      text = new FOMText(Text.Type.TEXT, qname, parent, this, parserWrapper);
    } else if (Text.Type.HTML.equals(type)) {
      text = new FOMText(Text.Type.HTML, qname, parent, this, parserWrapper);
    } else if (Text.Type.XHTML.equals(type)) {
      text = new FOMText(Text.Type.XHTML, qname, parent, this, parserWrapper);
    }
    return text;  }
  
  public Text newText(
    QName qname, 
    Text.Type type, 
    Element parent) {
    Text text = null;
    if (Text.Type.TEXT.equals(type)) {
      text = new FOMText(Text.Type.TEXT, qname, (OMContainer)parent, this);
    } else if (Text.Type.HTML.equals(type)) {
      text = new FOMText(Text.Type.HTML, qname, (OMContainer)parent, this);
    } else if (Text.Type.XHTML.equals(type)) {
      text = new FOMText(Text.Type.XHTML, qname, (OMContainer)parent, this);
    }
    return text;
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
      return new FOMStringElement(qname, parent, this, parserWrapper);
  }
  
  public StringElement newStringElement(
    QName qname, 
    Element parent) {
    return new FOMStringElement(qname, (OMContainer)parent, this);
  }

  public StringElement newStringElement(
    QName qname, 
    Document parent) {
      return new FOMStringElement(qname, (OMContainer)parent, this);
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
            new FOMExtensionElement(
              qname, 
              (OMElement)parent, 
              this);
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
        new FOMExtensionElement(qname, parent, this, parserWrapper);
    }
    return element;
  }
  
  public Control newControl(Element parent) {
    return new FOMControl((OMElement)parent, this);
  }
  
  public Control newControl(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
    return new FOMControl(qname, parent, this, parserWrapper);
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
    return new FOMDiv(Constants.DIV, (OMContainer)parent, this);
  }
  
  public Div newDiv(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMDiv(qname, parent, this, parserWrapper);
  }
  
  public Element newElement(
    QName qname,
    OMContainer parent,
    OMXMLParserWrapper parserWrapper) {
      return new FOMElement(qname, parent, this, parserWrapper);
  }

  public Total newTotal(Element parent) {
    return new FOMTotal((OMContainer)parent, this);
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
    return new FOMTotal(qname, parent, this, parserWrapper);
  }
  
  public InReplyTo newInReplyTo(
    QName qname,
    OMContainer parent, 
    OMXMLParserWrapper parserWrapper) {
      return new FOMInReplyTo(qname, parent, this, parserWrapper);
  }
  
  public InReplyTo newInReplyTo(Element parent) {
    return new FOMInReplyTo((OMContainer)parent, this);
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
        element = (OMElement) new FOMFeed(qname.getLocalPart(), namespace, parent, factory);
      } else if (SERVICE.equals(qname)) {
        element = (OMElement) new FOMService(qname.getLocalPart(), namespace, parent, factory);
      } else if (ENTRY.equals(qname)) {
        element = (OMElement) new FOMEntry(qname.getLocalPart(), namespace, parent, factory);
      } else if (AUTHOR.equals(qname)) {
        element = (OMElement) new FOMPerson(qname.getLocalPart(), namespace, parent, factory);
      } else if (CATEGORY.equals(qname)) {
        element = (OMElement) new FOMCategory(qname.getLocalPart(), namespace, parent, factory);
      } else if (CONTENT.equals(qname)) {
        Content.Type type = (Content.Type) objecttype;
        element = (OMElement) new FOMContent(qname.getLocalPart(), namespace, type, parent, factory);
      } else if (CONTRIBUTOR.equals(qname)) {
        element = (OMElement) new FOMPerson(qname.getLocalPart(), namespace, parent, factory);
      } else if (GENERATOR.equals(qname)) {
        element = (OMElement) new FOMGenerator(qname.getLocalPart(), namespace, parent, factory);
      } else if (ICON.equals(qname)) {
        element = (OMElement) new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
      } else if (ID.equals(qname)) {
        element = (OMElement) new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
      } else if (LOGO.equals(qname)) {
        element = (OMElement) new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
      } else if (LINK.equals(qname)) {
        element = (OMElement) new FOMLink(qname.getLocalPart(), namespace, parent, factory);
      } else if (PUBLISHED.equals(qname)) {
        element = (OMElement) new FOMDateTime(qname.getLocalPart(), namespace, parent, factory);
      } else if (RIGHTS.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
      } else if (SOURCE.equals(qname)) {
        element = (OMElement) new FOMSource(qname.getLocalPart(), namespace, parent, factory);
      } else if (SUBTITLE.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
      } else if (SUMMARY.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
      } else if (TITLE.equals(qname)) {
        Text.Type type = (Text.Type) objecttype;
        element = (OMElement) new FOMText(type, qname.getLocalPart(), namespace, parent, factory);
      } else if (UPDATED.equals(qname)) {
        element = (OMElement) new FOMDateTime(qname.getLocalPart(), namespace, parent, factory);          
      } else if (WORKSPACE.equals(qname)) {
        element = (OMElement) new FOMWorkspace(qname.getLocalPart(), namespace, parent, factory);
      } else if (COLLECTION.equals(qname)) {
        element = (OMElement) new FOMCollection(qname.getLocalPart(), namespace, parent, factory);
      } else if (NAME.equals(qname)) {
        element = (OMElement) new FOMStringElement(qname.getLocalPart(), namespace, parent, factory);
      } else if (EMAIL.equals(qname)) {
        element = (OMElement) new FOMStringElement(qname.getLocalPart(), namespace, parent, factory);
      } else if (URI.equals(qname)) {
        element = (OMElement) new FOMIRI(qname.getLocalPart(), namespace, parent, factory);
      } else if (CONTROL.equals(qname)) {
        element = (OMElement) new FOMControl(qname.getLocalPart(), namespace, parent, factory);
      } else if (DIV.equals(qname)) {
        element = (OMElement) new FOMDiv(qname.getLocalPart(), namespace, parent, factory);
      } else if (IN_REPLY_TO.equals(qname)) {
        element = (OMElement) new FOMInReplyTo(qname.getLocalPart(), namespace, parent, factory);
      } else if (THRTOTAL.equals(qname)) {
        element = (OMElement) new FOMTotal(qname.getLocalPart(), namespace, parent, factory);
      } else if (parent instanceof ExtensibleElement || parent instanceof Document) {
        if (isSimpleExtension(qname)) {
          element = (OMElement) new FOMStringElement(qname.getLocalPart(), namespace, parent, factory);
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

}
