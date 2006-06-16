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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.URIHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMSource
  extends FOMExtensibleElement 
  implements Source {

  private static final long serialVersionUID = 9153127297531238021L; 
  
  public FOMSource(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMSource( 
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(SOURCE, parent, factory);
  }

  public FOMSource( 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(SOURCE, parent, factory, builder);
  }

  public FOMSource( 
    QName qname,
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(qname, parent, factory);
  }

  public FOMSource(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }
  
  public Person getAuthor() {
    return (Person)getFirstChildWithName(AUTHOR);
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
    Person person = fomfactory.newAuthor(name, email, uri, this);
    addAuthor(person);
    return person;
  }
  
  public Person addAuthor(String name, String email, URI uri) {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    Person person = fomfactory.newAuthor(name, email, uri, this);
    addAuthor(person);
    return person;    
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

  public Person addContributor(String name, String email, String uri) throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    return fomfactory.newContributor(name, email, uri, this);
  }
  
  public Person addContributor(String name, String email, URI uri) {
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
            value.equalsIgnoreCase(Link.REL_ALTERNATE) ||
            value.equalsIgnoreCase(Link.REL_ALTERNATE_IANA))) {
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
  
  public Link addLink(URI href, String rel, MimeType type, String title, String hreflang, long length) {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(href, rel, type, title, hreflang, length, this);
  }
  
  public Link addLink(String href, String rel, MimeType type, String title, String hreflang, long length) throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(href, rel, type, title, hreflang, length, this);
  }
  
  public Link addLink(URI href, String rel, String type, String title, String hreflang, long length) throws MimeTypeParseException {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(href, rel, new MimeType(type), title, hreflang, length, this);
  }
  
  public Link addLink(String href, String rel, String type, String title, String hreflang, long length) throws URISyntaxException, MimeTypeParseException {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLink(href, rel, new MimeType(type), title, hreflang, length, this);
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

  public Text getSubtitleElement() {
    return getTextElement(SUBTITLE);
  }

  public void setSubtitleElement(Text text) {
    setTextElement(SUBTITLE, text, false);
  }

  public Text setSubtitleAsText(String value) {
    return setTextText(SUBTITLE, value);
  }  
  
  public Text setSubtitleAsHtml(String value, URI baseUri) {
    return setHtmlText(SUBTITLE, value, baseUri);
  }
  
  public Text setSubtitleAsXhtml(String value, URI baseUri) {
    return setXhtmlText(SUBTITLE, value, baseUri);
  }
  
  public Text setSubtitleAsXhtml(Div value, URI baseUri) {
    return setXhtmlText(SUBTITLE, value, baseUri);
  }
  
  public String getSubtitle() {
    return getText(SUBTITLE);
  }

  public Text getTitleElement() {
    return getTextElement(TITLE);
  }

  public void setTitleElement(Text text) {
    setTextElement(TITLE, text, false);
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
  
  public Generator getGenerator() {
    return (Generator)getFirstChildWithName(GENERATOR);
  }

  public void setGenerator(Generator generator) {
    if (generator != null)
      _setChild(GENERATOR, (OMElement) generator);
    else 
      _removeElement(GENERATOR, false);
  }

  public Generator setGenerator(URI uri, String version, String value) {
    FOMFactory fomfactory = (FOMFactory) factory;
    Generator generator = fomfactory.newGenerator(uri, version, value, this);
    return generator;
  }
  
  public Generator setGenerator(
    String uri, 
    String version, 
    String value) 
      throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) factory;
    Generator generator = 
      fomfactory.newGenerator(
        (uri != null) ? new URI(uri):null, 
        version, value, this);
    return generator;    
  }
  
  public IRI getIconElement() {
    return (IRI)getFirstChildWithName(ICON);
  }

  public void setIconElement(IRI iri) {
    if (iri != null)
      _setChild(ICON, (OMElement) iri);
    else 
      _removeElement(ICON, false);
  }

  public IRI setIcon(String iri) throws URISyntaxException {
    if (iri == null) {
      _removeElement(ICON, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newIcon(iri, this);
  }
  
  public IRI setIcon(URI iri) {
    if (iri == null) {
      _removeElement(ICON, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newIcon(iri, this);    
  }
  
  public URI getIcon() throws URISyntaxException {
    IRI iri = getIconElement();
    return (iri != null) ? iri.getValue() : null;
  }

  public IRI getLogoElement() {
    return (IRI)getFirstChildWithName(LOGO);
  }

  public void setLogoElement(IRI iri) {
    if (iri != null)
      _setChild(LOGO, (OMElement)iri);
    else 
      _removeElement(LOGO, false);
  }

  public IRI setLogo(String iri) throws URISyntaxException {
    if (iri == null) {
      _removeElement(LOGO, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLogo(iri, this);
  }
  
  public IRI setLogo(URI iri) {
    if (iri == null) {
      _removeElement(LOGO, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newLogo(iri, this);    
  }
  
  public URI getLogo() throws URISyntaxException {
    IRI iri = getLogoElement();
    return (iri != null) ? iri.getValue() : null;
  }
  
  public Link getLink(String rel) {
    List<Link> self = getLinks(rel);
    Link link = null;
    if (self.size() > 0) link = self.get(0);
    return link;
  }
  
  public Link getSelfLink() {
    return getLink(Link.REL_SELF);
  }

  public Link getAlternateLink() {
    return getLink(Link.REL_ALTERNATE);
  }

  public Text.Type getRightsType() {
    Text text = getRightsElement();
    return (text != null) ? text.getTextType() : null;
  }

  public Text.Type getSubtitleType() {
    Text text = getSubtitleElement();
    return (text != null) ? text.getTextType() : null;
  }

  public Text.Type getTitleType() {
    Text text = getTitleElement();
    return (text != null) ? text.getTextType() : null;
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

  public Text setSubtitleAsHtml(String value, String baseUri) throws URISyntaxException {
    return setSubtitleAsHtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setSubtitleAsXhtml(String value, String baseUri) throws URISyntaxException {
    return setSubtitleAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
  }

  public Text setSubtitleAsXhtml(Div value, String baseUri) throws URISyntaxException {
    return setSubtitleAsXhtml(value, (baseUri != null) ? new URI(baseUri): null);
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

  public Text setRightsAsHtml(String value) {
    return setRightsAsHtml(value, (URI)null);
  }

  public Text setRightsAsXhtml(String value) {
    return setRightsAsXhtml(value, (URI)null);
  }

  public Text setRightsAsXhtml(Div value) {
    return setRightsAsXhtml(value, (URI)null);
  }

  public Text setSubtitleAsHtml(String value) {
    return setSubtitleAsHtml(value, (URI)null);
  }

  public Text setSubtitleAsXhtml(String value) {
    return setSubtitleAsXhtml(value, (URI)null);
  }

  public Text setSubtitleAsXhtml(Div value) {
    return setSubtitleAsXhtml(value, (URI)null);
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

}
