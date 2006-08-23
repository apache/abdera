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
import java.util.Date;
import java.util.List;

import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Generator;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.Constants;
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
  
  public FOMSource() {
    super(Constants.SOURCE);
  }
  
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
    Person person = fomfactory.newAuthor(this);
    person.setName(name);
    person.setEmail(email);
    person.setUri(uri);
    return person;
  }
  
  public List<Category> getCategories() {
    return _getChildrenAsSet(CATEGORY);
  }

  public List<Category> getCategories(String scheme) throws URISyntaxException {
    return FOMHelper.getCategories(this, scheme);
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

  public Category addCategory(String scheme, String term, String label) throws URISyntaxException {
    FOMFactory factory = (FOMFactory) this.factory;
    Category category = factory.newCategory(this);
    category.setTerm(term);
    category.setScheme(scheme);
    category.setLabel(label);
    return category;    
  }
  
  public List<Person> getContributors() {
    return _getChildrenAsSet(CONTRIBUTOR);
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
    Person person = fomfactory.newContributor(this);
    person.setName(name);
    person.setEmail(email);
    person.setUri(uri);
    return person;
  }
  
  public IRI getIdElement() {
    return (IRI)getFirstChildWithName(ID);
  }

  public void setIdElement(IRI id) {
    if (id != null)
      _setChild(ID, (OMElement)id);
    else 
      _removeChildren(ID, false);
  }

  public URI getId() throws URISyntaxException {
    IRI id = getIdElement();
    return (id != null) ? id.getValue() : null;
  }
  
  public IRI setId(String value) throws URISyntaxException {
    return setId(value, false);
  }
    
  public IRI setId(String value, boolean normalize) throws URISyntaxException {
    if (value == null) {
      _removeChildren(ID, false);
      return null;
    }
    IRI id = getIdElement();
    if (id != null) {
      if (normalize) id.setNormalizedValue(value);
      else id.setValue(value);
      return id;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      IRI iri = fomfactory.newID(this);
      iri.setValue((normalize) ? URIHelper.normalize(value) : value);
      return iri;
    }
  }
  
  public List<Link> getLinks() {
    return _getChildrenAsSet(LINK);
  }
  
  public List<Link> getLinks(String rel) {
    return FOMHelper.getLinks(this, rel);
  }

  public void addLink(Link link) {
    addChild((OMElement)link);
  }

  public Link addLink(String href) throws URISyntaxException {
    return addLink(href, null);
  }
  
  public Link addLink(String href, String rel) throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) factory;
    Link link = fomfactory.newLink(this);
    link.setHref(href);
    if (rel != null) link.setRel(rel);
    return link;    
  }
  
  public Link addLink(String href, String rel, String type, String title, String hreflang, long length) throws URISyntaxException, MimeTypeParseException {
    FOMFactory fomfactory = (FOMFactory) factory;
    Link link = fomfactory.newLink(this);
    link.setHref(href);
    link.setRel(rel);
    link.setMimeType(type);
    link.setTitle(title);
    link.setHrefLang(hreflang);
    link.setLength(length);
    return link;
  }
    
  @SuppressWarnings("unchecked")
  public Text getRightsElement() {
    return getTextElement(RIGHTS);
  }

  public void setRightsElement(Text text) {
    setTextElement(RIGHTS, text, false);
  }

  public Text setRights(String value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newRights();
    text.setValue(value);
    setRightsElement(text);
    return text;
  }
  
  public Text setRightsAsHtml(String value) {
    return setRights(value, Text.Type.HTML);
  }
  
  public Text setRightsAsXhtml(String value) {
    return setRights(value, Text.Type.XHTML);
  }
  
  public Text setRights(String value, Text.Type type) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newRights(type);
    text.setValue(value);
    setRightsElement(text);
    return text;
  }
  
  public Text setRights(Div value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newRights(value);
    setRightsElement(text);
    return text;
  }

  public String getRights() {
    return getText(RIGHTS);
  }
  
  public Text getSubtitleElement() {
    return getTextElement(SUBTITLE);
  }

  public void setSubtitleElement(Text text) {
    setTextElement(SUBTITLE, text, false);
  }

  public Text setSubtitle(String value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newSubtitle();
    text.setValue(value);
    setSubtitleElement(text);
    return text;
  }
  
  public Text setSubtitleAsHtml(String value) {
    return setSubtitle(value, Text.Type.HTML);
  }
  
  public Text setSubtitleAsXhtml(String value) {
    return setSubtitle(value, Text.Type.XHTML);
  }
  
  public Text setSubtitle(String value, Text.Type type) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newSubtitle(type);
    text.setValue(value);
    setSubtitleElement(text);
    return text;
  }
  
  public Text setSubtitle(Div value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newSubtitle(value);
    setSubtitleElement(text);
    return text;
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

  public Text setTitle(String value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newTitle();
    text.setValue(value);
    setTitleElement(text);
    return text;
  }
  
  public Text setTitleAsHtml(String value) {
    return setTitle(value, Text.Type.HTML);
  }
  
  public Text setTitleAsXhtml(String value) {
    return setTitle(value, Text.Type.XHTML);
  }
  
  public Text setTitle(String value, Text.Type type) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newTitle(type);
    text.setValue(value);
    setTitleElement(text);
    return text;
  }
  
  public Text setTitle(Div value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newTitle(value);
    setTitleElement(text);
    return text;
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
      _removeChildren(UPDATED, false);
  }

  public String getUpdatedString() {
    DateTime dte = getUpdatedElement();
    return (dte != null) ? dte.getString() : null;
  }

  public Date getUpdated() {
    DateTime dte = getUpdatedElement();
    return (dte != null) ? dte.getDate() : null;
  }
  
  private DateTime setUpdated(AtomDate value) {
    if (value == null) {
      _removeChildren(UPDATED, false);
      return null;
    }
    DateTime dte = getUpdatedElement();
    if (dte != null) {
      dte.setValue(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      DateTime dt = fomfactory.newUpdated(this);
      dt.setValue(value);
      return dt;
    }
  }
  
  public DateTime setUpdated(Date value) {
    return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
  }
  
  public DateTime setUpdated(String value) {
    return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
  }
  
  public Generator getGenerator() {
    return (Generator)getFirstChildWithName(GENERATOR);
  }

  public void setGenerator(Generator generator) {
    if (generator != null)
      _setChild(GENERATOR, (OMElement) generator);
    else 
      _removeChildren(GENERATOR, false);
  }

  public Generator setGenerator(
    String uri, 
    String version, 
    String value) 
      throws URISyntaxException {
    FOMFactory fomfactory = (FOMFactory) factory;
    Generator generator = fomfactory.newGenerator(this);
    if (uri != null) generator.setUri(uri);
    if (version != null) generator.setVersion(version);
    if (value != null) generator.setText(value);
    return generator;    
  }
  
  public IRI getIconElement() {
    return (IRI)getFirstChildWithName(ICON);
  }

  public void setIconElement(IRI iri) {
    if (iri != null)
      _setChild(ICON, (OMElement) iri);
    else 
      _removeChildren(ICON, false);
  }

  public IRI setIcon(String value) throws URISyntaxException {
    if (value == null) {
      _removeChildren(ICON, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    IRI iri = fomfactory.newIcon(this);
    iri.setValue(value);
    return iri;
  }
  
  public URI getIcon() throws URISyntaxException {
    IRI iri = getIconElement();
    return (iri != null) ? iri.getResolvedValue() : null;
  }

  public IRI getLogoElement() {
    return (IRI)getFirstChildWithName(LOGO);
  }

  public void setLogoElement(IRI iri) {
    if (iri != null)
      _setChild(LOGO, (OMElement)iri);
    else 
      _removeChildren(LOGO, false);
  }

  public IRI setLogo(String value) throws URISyntaxException {
    if (value == null) {
      _removeChildren(LOGO, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    IRI iri = fomfactory.newLogo(this);
    iri.setValue(value);
    return iri;
  }
  
  public URI getLogo() throws URISyntaxException {
    IRI iri = getLogoElement();
    return (iri != null) ? iri.getResolvedValue() : null;
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

  public URI getLinkResolvedHref(String rel) throws URISyntaxException {
    Link link = getLink(rel);
    return (link != null) ? link.getResolvedHref() : null;
  }
  public URI getSelfLinkResolvedHref() throws URISyntaxException {
    Link link = getSelfLink();
    return (link != null) ? link.getResolvedHref() : null;
  }
  public URI getAlternateLinkResolvedHref() throws URISyntaxException {
    Link link = getAlternateLink();
    return (link != null) ? link.getResolvedHref() : null;
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

}
