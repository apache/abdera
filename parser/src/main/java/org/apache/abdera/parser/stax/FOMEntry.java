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
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.InReplyTo;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.util.URIHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMEntry
  extends FOMExtensibleElement 
  implements Entry {

  private static final long serialVersionUID = 1L;

  public FOMEntry(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMEntry(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMEntry(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  public FOMEntry(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(ENTRY, parent, factory);
  }

  public FOMEntry(
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(ENTRY, parent, factory, builder);
  }
  
  public Person getAuthor() {
    return (Person) getFirstChildWithName(AUTHOR);
  }
  
  public List<Person> getAuthors() {
    return _getChildrenAsSet(AUTHOR);
  }

  public void setAuthors(List<Person> people) {
    _setChildrenFromSet(AUTHOR, people);
  }

  public void addAuthor(Person person) {
    addChild((OMElement)person);
  }
  
  public Person addAuthor(String name) {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    Person person = fomfactory.newAuthor(this);
    person.setName(name);
    return person;
  }

  public Person addAuthor(String name, String email, String uri) throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    return fomfactory.newAuthor(name, email, uri, this);
  }
  
  public Person addAuthor(String name, String email, URI uri) {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    return fomfactory.newAuthor(name, email, uri, this);    
  }

  public List<Category> getCategories() {
    return _getChildrenAsSet(CATEGORY);
  }

  public List<Category> getCategories(URI scheme) {
    List<Category> categories = getCategories();
    List<Category> matching = new ArrayList<Category>();
    for (Category category : categories) {
      try {
        URI uri = category.getScheme();
        if ((uri != null && uri.equals(scheme)) ||
            (uri == null && scheme == null)) {
          matching.add(category);
        }
      } catch (Exception e) {}
    }
    return matching;
  }
  
  public List<Category> getCategories(String scheme) throws URISyntaxException {
    return getCategories((scheme != null) ? new URI(scheme) : null);
  }

  public void setCategories(List<Category> categories) {
    _setChildrenFromSet(CATEGORY, categories);
  }

  public void addCategory(Category category) {
    addChild((OMElement)category);
  }

  public Category addCategory(String term) {
    FOMFactory factory = (FOMFactory) this.factory;
    Category category = factory.newCategory(this);
    category.setTerm(term);
    return category;
  }

  public Category addCategory(URI scheme, String term, String label) {
    FOMFactory factory = (FOMFactory) this.factory;
    return factory.newCategory(scheme, term, label, this);
  }
  
  public Category addCategory(String scheme, String term, String label) throws URISyntaxException {
    FOMFactory factory = (FOMFactory) this.factory;
    return factory.newCategory(new URI(scheme), term, label, this);    
  }
  
  @SuppressWarnings("unchecked")
  public Content getContentElement() {
    return (Content)getFirstChildWithName(CONTENT);
  }

  public void setContentElement(Content content) {
    if (content != null) {
      Content element = getContentElement();
      if (element != null) element.discard();
      _setChild(CONTENT, (OMElement)content);
    } else {
      _removeElement(CONTENT, false);
    }
  }

  public Content setContentAsText(String value) {
    if (value == null) {
      setContentElement(null);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Content content = fomfactory.newTextContent(value, null);
    setContentElement(content);
    return content;
  }

  public Content setContentAsHtml(String value, URI baseUri) {
    if (value == null) {
      setContentElement(null);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Content content = fomfactory.newHtmlContent(value, null);
    if (baseUri != null) content.setBaseUri(baseUri);
    setContentElement(content);
    return content;
  }
  
  public Content setContentAsXhtml(String value, URI baseUri) {
    if (value == null) {
      setContentElement(null);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Content content = 
      (Content) fomfactory.newContent(
        Content.Type.XHTML, null);
    if (baseUri != null) content.setBaseUri(baseUri);
    content.setValue(value);
    setContentElement(content);
    return content;
  }
  
  public Content setContentAsXhtml(Div value, URI baseUri) {
    if (value == null) {
      setContentElement(null);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Content content = fomfactory.newXhtmlContent(value, null);
    if (baseUri != null) content.setBaseUri(baseUri);
    setContentElement(content);
    return content;
  }

  public Content setContentAsXml(
    String value, 
    MimeType type, 
    URI baseUri) {
      if (value == null) {
        setContentElement(null);
        return null;
      }
      FOMFactory fomfactory = (FOMFactory) factory;
      Content content = 
        (Content) fomfactory.newContent(Content.Type.XML, null);
      if (type != null) content.setMimeType(type);
      if (baseUri != null) content.setBaseUri(baseUri);
      content.setValue(value);
      setContentElement(content);
      return content;    
  }

  public Content setContentAsXml(
    String value, 
    String type, 
    URI baseUri) 
      throws MimeTypeParseException {
    return setContentAsXml(
      value, 
      (type != null) ? new MimeType(type) : null, 
       baseUri);
  }
  
  public Content setContentAsXml(
    ExtensionElement value, 
    MimeType type, 
    URI baseUri) {
      if (value == null) {
        setContentElement(null);
        return null;
      }
      FOMFactory fomfactory = (FOMFactory) factory;
      Content content = 
        fomfactory.newXmlContent(
          type, null, value, null);
      if (baseUri != null) content.setBaseUri(baseUri);
      setContentElement(content);
      return content;    
  }

  public Content setContentAsXml(
    ExtensionElement value, 
    String type, 
    URI baseUri) 
      throws MimeTypeParseException {
    return setContentAsXml(
      value, 
      (type != null) ? new MimeType(type) : null, 
      baseUri);
  }

  /**
   * Sets the content for this entry
   */
  public Content setContentAsMedia(
    MimeType type, 
    URI src, 
    DataHandler dataHandler) {
      if (dataHandler == null) {
        setContentElement(null);
        return null;
      }
      FOMFactory fomfactory = (FOMFactory) factory;
      Content content = fomfactory.newMediaContent(type, src, dataHandler, null);
      setContentElement(content);
      return content;
  }

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   */
  public Content setContentAsMedia(
    MimeType type, 
    String src, 
    DataHandler dataHandler) 
      throws URISyntaxException {
    return setContentAsMedia(
      type, (src != null) ? new URI(src):null,
      dataHandler);
  }
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  public Content setContentAsMedia(
    String type, 
    URI src, 
    DataHandler dataHandler) 
      throws MimeTypeParseException {
    return setContentAsMedia(
      (type != null) ? new MimeType(type) : null, 
      src,dataHandler);
  }

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   * @throws MimeTypeParseException 
   */
  public Content setContentAsMedia(
    String type, 
    String src, 
    DataHandler dataHandler) 
      throws MimeTypeParseException, URISyntaxException {
    return setContentAsMedia(
      (type != null) ? new MimeType(type) : null, 
      (src != null) ? new URI(src) : null,
      dataHandler);
  }
  
  /**
   * Sets the content for this entry
   */
  public Content setContentAsMedia(
    MimeType type, 
    URI src, 
    String value) {
      if (value == null) {
        setContentElement(null);
        return null;
      }
      FOMFactory fomfactory = (FOMFactory) factory;
      Content content = fomfactory.newMediaContent(type, src, null, null);
      content.setValue(value);
      setContentElement(content);
      return content;
  }

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   */
  public Content setContentAsMedia(
    MimeType type, 
    String src, 
    String value) 
      throws URISyntaxException {
    return setContentAsMedia(
      type, (src != null) ? new URI(src):null,
      value);
  }
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  public Content setContentAsMedia(
    String type, 
    URI src, 
    String value) 
      throws MimeTypeParseException {
    return setContentAsMedia(
      (type != null) ? new MimeType(type) : null, 
      src,value);
  }

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   * @throws MimeTypeParseException 
   */
  public Content setContentAsMedia(
    String type, 
    String src, 
    String value) 
      throws MimeTypeParseException, URISyntaxException {
    return setContentAsMedia(
      (type != null) ? new MimeType(type) : null, 
      (src != null) ? new URI(src) : null,
      value);
  }
  
  public List<Person> getContributors() {
    return _getChildrenAsSet(CONTRIBUTOR);
  }

  public void setContributors(List<Person> people) {
    _setChildrenFromSet(CONTRIBUTOR, people);
  }

  public void addContributor(Person person) {
    addChild((OMElement)person);
  }

  public Person addContributor(String name) {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    Person person = fomfactory.newContributor(this);
    person.setName(name);
    return person;
  }

  public Person addContributor(
    String name, 
    String email, 
    String uri) 
      throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    return fomfactory.newContributor(name, email, uri, this);
  }
  
  public Person addContributor(
    String name, 
    String email, 
    URI uri) {
      FOMFactory fomfactory = (FOMFactory) this.factory;
      return fomfactory.newContributor(name, email, uri, this);    
  }
  
  public IRI getIdElement() {
    return (IRI)getFirstChildWithName(ID);
  }

  public void setIdElement(IRI id) {
    if (id != null)
      _setChild(ID, (OMElement)id);
    else
      _removeElement(ID, false);
  }

  public URI getId() throws URISyntaxException {
    IRI id = getIdElement();
    return (id != null) ? id.getValue() : null;
  }
  
  public IRI setId(URI value) throws URISyntaxException {
    return setId(value, false);
  }
  
  public IRI setId(String value) throws URISyntaxException {
    return setId(value, false);
  }
  
  public IRI setId(URI value, boolean normalize) throws URISyntaxException {
    if (value == null) {
      _removeElement(ID, false);
      return null;
    }
    IRI id = getIdElement();
    if (id != null) {
      if (normalize) id.setNormalizedValue(value);
      else id.setValue(value);
      return id;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newID(
        (normalize) ? URIHelper.normalize(value) : value, this);
    }
  }
  
  public IRI setId(String value, boolean normalize) throws URISyntaxException {
    return setId((value != null) ? new URI(value) : null, normalize);
  }
  
  public List<Link> getLinks() {
    return _getChildrenAsSet(LINK);
  }

  public List<Link> getLinks(String rel) {
    if (rel == null) return getLinks();
    List<Link> links = getLinks();
    List<Link> matching = new ArrayList<Link>();
    for (Link link : links) {
      String value = FOMLink.getRelEquiv(link.getRel());
      rel = FOMLink.getRelEquiv(rel);
      if (rel.equalsIgnoreCase(Link.REL_ALTERNATE) && 
          (value == null || 
            value.equalsIgnoreCase(Link.REL_ALTERNATE))) {
        matching.add(link);
      } else if (rel.equalsIgnoreCase(value)) {
        matching.add(link);
      }
    }
    return matching;
  }

  public void setLinks(List<Link> links) {
    _setChildrenFromSet(LINK, links);
  }

  public void addLink(Link link) {
    addChild((OMElement)link);
  }

  public Link addLink(String href) throws URISyntaxException {
    return addLink(href, null);
  }
  
  public Link addLink(String href, String rel) throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(href, rel, null, null, null, -1, this);    
  }
  
  public Link addLink(URI href) {
    return addLink(href, null);
  }
  
  public Link addLink(URI href, String rel) {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(href, rel, null, null, null, -1, this);
  }
  
  public Link addLink(
    URI href, 
    String rel, 
    MimeType type, 
    String title, 
    String hreflang, 
    long length) {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(
      href, rel, type, title, hreflang, length, this);
  }
  
  public Link addLink(
    String href, 
    String rel, 
    MimeType type, 
    String title, 
    String hreflang, 
    long length) 
      throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(
      href, rel, type, title, hreflang, length, this);
  }
  
  public Link addLink(
    URI href, 
    String rel, 
    String type, 
    String title, 
    String hreflang, 
    long length) 
      throws MimeTypeParseException {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(
      href, rel, new MimeType(type), title, hreflang, length, this);
  }
  
  public Link addLink(
    String href, 
    String rel, 
    String type, 
    String title, 
    String hreflang, 
    long length) 
      throws URISyntaxException, MimeTypeParseException {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(
      href, rel, new MimeType(type), title, hreflang, length, this);
  }
  
  public DateTime getPublishedElement() {
    return (DateTime)getFirstChildWithName(PUBLISHED);
  }

  public void setPublishedElement(DateTime dateTime) {
    if (dateTime != null)
      _setChild(PUBLISHED, (OMElement)dateTime);
    else
      _removeElement(PUBLISHED, false);
  }

  public String getPublishedString() {
    DateTime dte = getPublishedElement();
    return (dte != null) ? dte.getString() : null;
  }
  
  public Date getPublished() {
    DateTime dte = getPublishedElement();
    return (dte != null) ? dte.getDate() : null;
  }
  
  public DateTime setPublished(Date value) {
    if (value == null) {
      _removeElement(PUBLISHED, false);
      return null;
    }
    DateTime dte = getPublishedElement();
    if (dte != null) {
      dte.setDate(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newPublished(value, this);
    }
  }
  
  public DateTime setPublished(Calendar value) {
    if (value == null) {
      _removeElement(PUBLISHED, false);
      return null;
    }
    DateTime dte = getPublishedElement();
    if (dte != null) {
      dte.setCalendar(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newPublished(value, this);
    }
  }
  
  public DateTime setPublished(long value) {
    DateTime dte = getPublishedElement();
    if (dte != null) {
      dte.setTime(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newPublished(value, this);
    }
  }
  
  public DateTime setPublished(String value) {
    if (value == null) {
      _removeElement(PUBLISHED, false);
      return null;
    }
    DateTime dte = getPublishedElement();
    if (dte != null) {
      dte.setString(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newPublished(value, this);
    }
  }
  
  @SuppressWarnings("unchecked")
  public Text getRightsElement() {
    return getTextElement(RIGHTS);
  }

  public void setRightsElement(Text text) {
    setTextElement(RIGHTS, text, false);
  }

  public Text setRightsAsText(String value) {
    return setTextText(RIGHTS, value);
  }  
  
  public Text setRightsAsHtml(String value, URI baseUri) {
    return setHtmlText(RIGHTS, value, baseUri);
  }
  
  public Text setRightsAsXhtml(String value, URI baseUri) {
    return setXhtmlText(RIGHTS, value, baseUri);
  }
  
  public Text setRightsAsXhtml(Div value, URI baseUri) {
    return setXhtmlText(RIGHTS, value, baseUri);
  }
  
  public String getRights() {
    return getText(RIGHTS);
  }
  
  public Source getSource() {
    return (Source)getFirstChildWithName(SOURCE);
  }

  public void setSource(Source source) {
    if (source != null)
      _setChild(SOURCE, (OMElement)source);
    else
      _removeElement(SOURCE, false);
  }

  @SuppressWarnings("unchecked")
  public Text getSummaryElement() {
    return getTextElement(SUMMARY);
  }

  public void setSummaryElement(Text text) {
    setTextElement(SUMMARY, text, false);
  }
  
  public Text setSummaryAsText(String value) {
    return setTextText(SUMMARY, value);
  }  
  
  public Text setSummaryAsHtml(String value, URI baseUri) {
    return setHtmlText(SUMMARY, value, baseUri);
  }
  
  public Text setSummaryAsXhtml(String value, URI baseUri) {
    return setXhtmlText(SUMMARY, value, baseUri);
  }
  
  public Text setSummaryAsXhtml(Div value, URI baseUri) {
    return setXhtmlText(SUMMARY, value, baseUri);
  }
    
  public String getSummary() {
    return getText(SUMMARY);
  }

  @SuppressWarnings("unchecked")
  public Text getTitleElement() {
    return getTextElement(TITLE);
  }

  public void setTitleElement(Text title) {
    setTextElement(TITLE, title, false);
  }

  public Text setTitleAsText(String value) {
    return setTextText(TITLE, value);
  }  
  
  public Text setTitleAsHtml(String value, URI baseUri) {
    return setHtmlText(TITLE, value, baseUri);
  }
  
  public Text setTitleAsXhtml(String value, URI baseUri) {
    return setXhtmlText(TITLE, value, baseUri);
  }
  
  public Text setTitleAsXhtml(Div value, URI baseUri) {
    return setXhtmlText(TITLE, value, baseUri);
  }
  
  public String getTitle() {
    return getText(TITLE);
  }
    
  public DateTime getUpdatedElement() {
    return (DateTime)getFirstChildWithName(UPDATED);
  }

  public void setUpdatedElement(DateTime updated) {
    if (updated != null)
      _setChild(UPDATED, (OMElement)updated);
    else 
      _removeElement(UPDATED, false);
  }

  public String getUpdatedString() {
    DateTime dte = getUpdatedElement();
    return (dte != null) ? dte.getString() : null;
  }
  
  public Date getUpdated() {
    DateTime dte = getUpdatedElement();
    return (dte != null) ? dte.getDate() : null;
  }
  
  public DateTime setUpdated(Date value) {
    if (value == null) {
      _removeElement(UPDATED, false);
      return null;
    }
    DateTime dte = getUpdatedElement();
    if (dte != null) {
      dte.setDate(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newUpdated(value, this);
    }
  }
  
  public DateTime setUpdated(Calendar value) {
    if (value == null) {
      _removeElement(UPDATED, false);
      return null;
    }
    DateTime dte = getUpdatedElement();
    if (dte != null) {
      dte.setCalendar(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newUpdated(value, this);
    }
  }
  
  public DateTime setUpdated(long value) {
    DateTime dte = getUpdatedElement();
    if (dte != null) {
      dte.setTime(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newUpdated(value, this);
    }
  }
  
  public DateTime setUpdated(String value) {
    if (value == null) {
      _removeElement(UPDATED, false);
      return null;
    }
    DateTime dte = getUpdatedElement();
    if (dte != null) {
      dte.setString(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      return fomfactory.newUpdated(value, this);
    }
  }

  public Control getControl() {
    return (Control)getFirstChildWithName(CONTROL);
  }

  public void setControl(Control control) {
    if (control != null) 
      _setChild(CONTROL, (OMElement)control);
    else 
      _removeElement(CONTROL, false);
  }

  public Link getLink(String rel) {
    List<Link> links = getLinks(rel);
    Link link = null;
    if (links.size() > 0) link = links.get(0);
    return link;
  }

  public Link getAlternateLink() {
    return getLink(Link.REL_ALTERNATE);
  }

  public Link getEnclosureLink() {
    return getLink(Link.REL_ENCLOSURE);
  }
  
  public Link getEditLink() {
    return getLink(Link.REL_EDIT);
  }
  
  public String getContent() {
    Content content = getContentElement();
    return (content != null) ? content.getValue() : null;
  }
  
  public Type getContentType() {
    Content content = getContentElement();
    return (content != null) ? content.getContentType() : null;
  }

  public Text.Type getRightsType() {
    Text text = getRightsElement();
    return (text != null) ? text.getTextType() : null;
  }

  public Text.Type getSummaryType() {
    Text text = getSummaryElement();
    return (text != null) ? text.getTextType() : null;
  }

  public Text.Type getTitleType() {
    Text text = getTitleElement();
    return (text != null) ? text.getTextType() : null;
  }

  public Content setContentAsHtml(String value, String baseUri) throws URISyntaxException {
    return setContentAsHtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Content setContentAsXhtml(String value, String baseUri) throws URISyntaxException {
    return setContentAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Content setContentAsXhtml(Div value, String baseUri) throws URISyntaxException {
    return setContentAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Content setContentAsXml(String value, MimeType type, String baseUri) throws URISyntaxException {
    return setContentAsXml(value, type, (baseUri != null) ? new URI(baseUri): null);
  }

  public Content setContentAsXml(String value, String type, String baseUri) throws MimeTypeParseException, URISyntaxException {
    return setContentAsXml(value, type, (baseUri != null) ? new URI(baseUri): null);
  }

  public Content setContentAsXml(ExtensionElement value, MimeType type, String baseUri) throws URISyntaxException {
    return setContentAsXml(value, type, (baseUri != null) ? new URI(baseUri): null);
  }

  public Content setContentAsXml(ExtensionElement value, String type, String baseUri) throws MimeTypeParseException, URISyntaxException {
    return setContentAsXml(value, type, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setRightsAsHtml(String value, String baseUri) throws URISyntaxException {
    return setRightsAsHtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setRightsAsXhtml(String value, String baseUri) throws URISyntaxException {
    return setRightsAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setRightsAsXhtml(Div value, String baseUri) throws URISyntaxException {
    return setRightsAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setSummaryAsHtml(String value, String baseUri) throws URISyntaxException {
    return setSummaryAsHtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setSummaryAsXhtml(String value, String baseUri) throws URISyntaxException {
    return setSummaryAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setSummaryAsXhtml(Div value, String baseUri) throws URISyntaxException {
    return setSummaryAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setTitleAsHtml(String value, String baseUri) throws URISyntaxException {
    return setTitleAsHtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setTitleAsXhtml(String value, String baseUri) throws URISyntaxException {
    return setTitleAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setTitleAsXhtml(Div value, String baseUri) throws URISyntaxException {
    return setTitleAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Content setContentAsHtml(String value) {
    return setContentAsHtml(value, (URI)null);
  }

  public Content setContentAsXhtml(String value) {
    return setContentAsXhtml(value, (URI)null);
  }

  public Content setContentAsXhtml(Div value) {
    return setContentAsXhtml(value, (URI)null);
  }

  public Content setContentAsXml(
    String value, 
    MimeType type) {
      return setContentAsXml(value, type, (URI)null);
  }

  public Content setContentAsXml(
    String value, 
    String type) 
      throws MimeTypeParseException {
    return setContentAsXml(value, type, (URI)null);
  }

  public Content setContentAsXml(
    ExtensionElement value, 
    MimeType type) {
      return setContentAsXml(value, type, (URI)null);
  }

  public Content setContentAsXml(
    ExtensionElement value, 
    String type) 
      throws MimeTypeParseException {
    return setContentAsXml(value, type, (URI)null);
  }

  public Text setRightsAsHtml(String value) {
    return setRightsAsHtml(value, (URI)null);
  }

  public Text setRightsAsXhtml(String value) {
    return setRightsAsXhtml(value, (URI)null);
  }

  public Text setRightsAsXhtml(Div value) {
    return setRightsAsXhtml(value, (URI)null);
  }

  public Text setSummaryAsHtml(String value) {
    return setSummaryAsHtml(value, (URI)null);
  }

  public Text setSummaryAsXhtml(String value) {
    return setSummaryAsXhtml(value, (URI)null);
  }

  public Text setSummaryAsXhtml(Div value) {
    return setSummaryAsXhtml(value, (URI)null);
  }

  public Text setTitleAsHtml(String value) {
    return setTitleAsHtml(value, (URI)null);
  }

  public Text setTitleAsXhtml(String value) {
    return setTitleAsXhtml(value, (URI)null);
  }

  public Text setTitleAsXhtml(Div value) {
    return setTitleAsXhtml(value, (URI)null);
  }

  public void addInReplyTo(InReplyTo replyTo) {
    addChild((OMElement)replyTo);
  }
  
  public InReplyTo addInReplyTo() {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    InReplyTo replyTo = fomfactory.newInReplyTo(this);
    return replyTo;
  }

  public InReplyTo addInReplyTo(Entry ref) {
    if (ref.equals(this)) return null;
    InReplyTo irt = addInReplyTo(); 
    try {
      irt.setRef(ref.getId());
      Link altlink = ref.getAlternateLink();
      if (altlink != null) {
        irt.setHref(altlink.getResolvedHref());
        if (altlink.getMimeType() != null) 
          irt.setMimeType(altlink.getMimeType());
      }
      Source src = ref.getSource();
      if (src != null) {
        Link selflink = src.getSelfLink();
        if (selflink != null)
          irt.setSource(selflink.getResolvedHref());
      }
    } catch (Exception e) {}
    return irt;
  }

  public InReplyTo addInReplyTo(URI ref) {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    return fomfactory.newInReplyTo(ref, this);
  }

  public InReplyTo addInReplyTo(String ref) throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    return fomfactory.newInReplyTo(ref, this);
  }

  public InReplyTo addInReplyTo(
    URI ref, 
    URI source, 
    URI href, 
    MimeType type) {
      FOMFactory fomfactory = (FOMFactory) this.factory;
      return fomfactory.newInReplyTo(ref, source, href, type, this);
  }

  public InReplyTo addInReplyTo(
    String ref, 
    String source, 
    String href, 
    String type) 
      throws URISyntaxException, 
             MimeTypeParseException {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    return fomfactory.newInReplyTo(ref, source, href, type, this);
  }

  public InReplyTo getInReplyTo() {
    return (InReplyTo) getFirstChildWithName(IN_REPLY_TO);
  }

  public List<InReplyTo> getInReplyTos() {
    return _getChildrenAsSet(IN_REPLY_TO);
  }

}
