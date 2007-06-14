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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.URIHelper;
import org.apache.abdera.i18n.io.InputStreamDataSource;
import org.apache.abdera.i18n.iri.IRI;
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

  public FOMEntry() {
    super(Constants.ENTRY, new FOMDocument(), new FOMFactory());
  }
  
  protected FOMEntry(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  protected FOMEntry(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  protected FOMEntry(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  protected FOMEntry(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(ENTRY, parent, factory);
  }

  protected FOMEntry(
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

  public void addAuthor(Person person) {
    addChild((OMElement)person);
  }
  
  public Person addAuthor(String name) {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    Person person = fomfactory.newAuthor(this);
    person.setName(name);
    return person;
  }

  public Person addAuthor(String name, String email, String uri) {
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

  public List<Category> getCategories(String scheme) {
    return FOMHelper.getCategories(this, scheme);
  }

  public void addCategory(Category category) {
    Element el = category.getParentElement();
    if (el != null && el instanceof Categories) {
      Categories cats = category.getParentElement();
      category = (Category) category.clone();
      try {
        if (category.getScheme() == null && cats.getScheme() != null) 
          category.setScheme(cats.getScheme().toString());
      } catch (Exception e) {
        // Do nothing, shouldn't happen
      }
    }
    addChild((OMElement)category);
  }

  public Category addCategory(String term) {
    FOMFactory factory = (FOMFactory) this.factory;
    Category category = factory.newCategory(this);
    category.setTerm(term);
    return category;
  }

  public Category addCategory(String scheme, String term, String label) {
    FOMFactory factory = (FOMFactory) this.factory;
    Category category = factory.newCategory(this);
    category.setTerm(term);
    category.setScheme(scheme);
    category.setLabel(label);
    return category;    
  }
  
  @SuppressWarnings("unchecked")
  public Content getContentElement() {
    return (Content)getFirstChildWithName(CONTENT);
  }

  public void setContentElement(Content content) {
    if (content != null) {
      _setChild(CONTENT, (OMElement)content);
    } else {
      _removeChildren(CONTENT, false);
    }
  }
  
  /**
   * Sets the content for this entry as @type="text"
   */
  public Content setContent(String value) {
    FOMFactory factory = (FOMFactory) this.factory;
    Content content = factory.newContent();
    content.setValue(value);
    setContentElement(content);
    return content;
  }
  
  public Content setContentAsHtml(String value) {
    return setContent(value, Content.Type.HTML);
  }
  
  public Content setContentAsXhtml(String value) {
    return setContent(value, Content.Type.XHTML);
  }
  
  /**
   * Sets the content for this entry
   */
  public Content setContent(String value, Content.Type type) {
    FOMFactory factory = (FOMFactory) this.factory;
    Content content = factory.newContent(type);
    content.setValue(value);
    setContentElement(content);
    return content;
  }
  
  /**
   * Sets the content for this entry
   */
  public Content setContent(Element value) {
    FOMFactory factory = (FOMFactory) this.factory;
    Content content = factory.newContent(Content.Type.XHTML);
    content.setValueElement(value);
    setContentElement(content);
    return content;
  } 
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  public Content setContent(Element element, String mediaType) throws MimeTypeParseException {
    FOMFactory factory = (FOMFactory) this.factory;
    Content content = factory.newContent(new MimeType(mediaType));
    content.setValueElement(element);
    setContentElement(content);
    return content;
  }
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  public Content setContent(DataHandler dataHandler) throws MimeTypeParseException {
     return setContent(dataHandler, dataHandler.getContentType());
  }  
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  public Content setContent(DataHandler dataHandler, String mediatype) throws MimeTypeParseException {
    FOMFactory factory = (FOMFactory) this.factory;
    Content content = factory.newContent(new MimeType(mediatype));
    content.setDataHandler(dataHandler);
    setContentElement(content);
    return content;
  }
  
  /**
   * Sets the content for this entry
   */
  public Content setContent(InputStream in) {
    InputStreamDataSource ds = new InputStreamDataSource(in);
    DataHandler dh = new DataHandler(ds);
    Content content = null;
    try {
      content = setContent(dh);
    } catch (MimeTypeParseException e) {
      // should not happen
    }
    return content;
  }
  
  /**
   * Sets the content for this entry
   */
  public Content setContent(InputStream in, String mediatype) throws MimeTypeParseException {
    InputStreamDataSource ds = new InputStreamDataSource(in, mediatype);
    DataHandler dh = new DataHandler(ds);
    return setContent(dh, mediatype);
  }
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  public Content setContent(String value, String mediatype) throws MimeTypeParseException {
    FOMFactory factory = (FOMFactory) this.factory;
    Content content = factory.newContent(new MimeType(mediatype));
    content.setValue(value);
    setContentElement(content);
    return content;
  }
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
 * @throws IRISyntaxException 
   */
  public Content setContent(IRI uri, String mediatype) throws MimeTypeParseException {
    FOMFactory factory = (FOMFactory) this.factory;
    Content content = factory.newContent(new MimeType(mediatype));
    content.setSrc(uri.toString());
    setContentElement(content);
    return content;
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

  public Person addContributor(
    String name, 
    String email, 
    String uri) 
      {
    FOMFactory fomfactory = (FOMFactory) this.factory;
    Person person = fomfactory.newContributor(this);
    person.setName(name);
    person.setEmail(email);
    person.setUri(uri);
    return person;
  }
  
  public IRIElement getIdElement() {
    return (IRIElement)getFirstChildWithName(ID);
  }

  public void setIdElement(IRIElement id) {
    if (id != null)
      _setChild(ID, (OMElement)id);
    else
      _removeChildren(ID, false);
  }

  public IRI getId() {
    IRIElement id = getIdElement();
    return (id != null) ? id.getValue() : null;
  }
  
  public IRIElement setId(String value) {
    return setId(value, false);
  }
  
  public IRIElement newId() {
    return setId(this.getFactory().newUuidUri(), false);
  }
  
  public IRIElement setId(String value, boolean normalize) {
    if (value == null) {
      _removeChildren(ID, false);
      return null;
    }
    IRIElement id = getIdElement();
    if (id != null) {
      if (normalize) id.setNormalizedValue(value);
      else id.setValue(value);
      return id;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      IRIElement iri = fomfactory.newID(this);
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
  
  public List<Link> getLinks(String... rels) {
    return FOMHelper.getLinks(this, rels);
  }

  public void addLink(Link link) {
    addChild((OMElement)link);
  }

  public Link addLink(String href) {
    return addLink(href, null);
  }
  
  public Link addLink(String href, String rel) {
    FOMFactory fomfactory = (FOMFactory) factory;
    Link link = fomfactory.newLink(this);
    link.setHref(href);
    if (rel != null) link.setRel(rel);
    return link;    
  }
  
  public Link addLink(
    String href, 
    String rel, 
    String type, 
    String title, 
    String hreflang, 
    long length) 
      throws MimeTypeParseException {
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
  
  public DateTime getPublishedElement() {
    return (DateTime)getFirstChildWithName(PUBLISHED);
  }

  public void setPublishedElement(DateTime dateTime) {
    if (dateTime != null)
      _setChild(PUBLISHED, (OMElement)dateTime);
    else
      _removeChildren(PUBLISHED, false);
  }

  public Date getPublished() {
    DateTime dte = getPublishedElement();
    return (dte != null) ? dte.getDate() : null;
  }
  
  private DateTime setPublished(AtomDate value) {
    if (value == null) {
      _removeChildren(PUBLISHED, false);
      return null;
    }
    DateTime dte = getPublishedElement();
    if (dte != null) {
      dte.setValue(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      DateTime dt = fomfactory.newPublished(this);
      dt.setValue(value);
      return dt;
    }
  }
  
  public DateTime setPublished(Date value) {
    return setPublished((value != null) ? AtomDate.valueOf(value) : null);
  }
  
  public DateTime setPublished(String value) {
    return setPublished((value != null) ? AtomDate.valueOf(value) : null);
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
  
  public Source getSource() {
    return (Source)getFirstChildWithName(SOURCE);
  }

  public void setSource(Source source) {
    if (source != null) {
      if (source instanceof Feed)
        source = ((Feed)source).getAsSource();
      _setChild(SOURCE, (OMElement)source);
    } else {
      _removeChildren(SOURCE, false);
    }
  }

  @SuppressWarnings("unchecked")
  public Text getSummaryElement() {
    return getTextElement(SUMMARY);
  }

  public void setSummaryElement(Text text) {
    setTextElement(SUMMARY, text, false);
  }
  
  public Text setSummary(String value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newSummary();
    text.setValue(value);
    setSummaryElement(text);
    return text;
  }
  
  public Text setSummaryAsHtml(String value) {
    return setSummary(value, Text.Type.HTML);
  }
  
  public Text setSummaryAsXhtml(String value) {
    return setSummary(value, Text.Type.XHTML);
  }
  
  public Text setSummary(String value, Text.Type type) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newSummary(type);
    text.setValue(value);
    setSummaryElement(text);
    return text;
  }
  
  public Text setSummary(Div value) {
    FOMFactory factory = (FOMFactory)this.factory;
    Text text = factory.newSummary(value);
    setSummaryElement(text);
    return text;
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

  public DateTime getEditedElement() {
    DateTime dt =  (DateTime)getFirstChildWithName(EDITED);
    if (dt == null) dt = (DateTime)getFirstChildWithName(PRE_RFC_EDITED);
    return dt;
  }

  public void setEditedElement(DateTime updated) {
    declareNamespace(APP_NS, "app");
    _removeChildren(PRE_RFC_EDITED,false);
    if (updated != null)
      _setChild(EDITED, (OMElement)updated);
    else
      _removeChildren(EDITED, false);
  }

  public Date getEdited() {
    DateTime dte = getEditedElement();
    return (dte != null) ? dte.getDate() : null;
  }
  
  private DateTime setEdited(AtomDate value) {
    declareNamespace(APP_NS, "app");
    if (value == null) {
      _removeChildren(PRE_RFC_EDITED, false);
      _removeChildren(EDITED, false);
      return null;
    }
    DateTime dte = getEditedElement();
    if (dte != null) {
      dte.setValue(value);
      return dte;
    } else {
      FOMFactory fomfactory = (FOMFactory) factory;
      DateTime dt = fomfactory.newEdited(this);
      dt.setValue(value);
      return dt;
    }
  }
  
  public DateTime setEdited(Date value) {
    return setEdited((value != null) ? AtomDate.valueOf(value) : null);
  }
  
  public DateTime setEdited(String value) {
    return setUpdated((value != null) ? AtomDate.valueOf(value) : null);
  }

  public Control getControl(boolean create) {
    Control control = getControl();
    if (control == null && create) {
      control = getFactory().newControl();
      setControl(control);
    }
    return control;
  }
  
  public Control getControl() {
    Control control = (Control)getFirstChildWithName(CONTROL);
    if (control == null) control = (Control)getFirstChildWithName(PRE_RFC_CONTROL);
    return control;
  }

  public void setControl(Control control) {
    _removeChildren(PRE_RFC_CONTROL,true);
    if (control != null) 
      _setChild(CONTROL, (OMElement)control);
    else 
      _removeChildren(CONTROL, false);
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
  
  public Link getSelfLink() {
    return getLink(Link.REL_SELF);
  }
  
  public Link getEditMediaLink() {
    return getLink(Link.REL_EDIT_MEDIA);
  }
  
  public IRI getLinkResolvedHref(String rel) {
    Link link = getLink(rel);
    return (link != null) ? link.getResolvedHref() : null;
  }
  public IRI getAlternateLinkResolvedHref() {
    Link link = getAlternateLink();
    return (link != null) ? link.getResolvedHref() : null;
  }
  public IRI getEnclosureLinkResolvedHref() {
    Link link = getEnclosureLink();
    return (link != null) ? link.getResolvedHref() : null;
  }
  public IRI getEditLinkResolvedHref() {
    Link link = getEditLink();
    return (link != null) ? link.getResolvedHref() : null;
  }
  public IRI getEditMediaLinkResolvedHref() {
    Link link = getEditMediaLink();
    return (link != null) ? link.getResolvedHref() : null;
  }
  public IRI getSelfLinkResolvedHref() {
    Link link = getSelfLink();
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public String getContent() {
    Content content = getContentElement();
    return (content != null) ? content.getValue() : null;
  }
  
  public InputStream getContentStream() throws IOException {
    Content content = getContentElement();
    DataHandler dh = content.getDataHandler();
    return dh.getInputStream();
  }
  
  public IRI getContentSrc() {
    Content content = getContentElement();
    return (content != null) ? content.getResolvedSrc() : null;
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

  public MimeType getContentMimeType() {
    Content content = getContentElement();
    return (content != null) ? content.getMimeType() : null;
  }

  public Link getAlternateLink(
    String type, 
    String hreflang) 
      throws MimeTypeParseException {
    return selectLink(getLinks(Link.REL_ALTERNATE), type, hreflang);
  }

  public IRI getAlternateLinkResolvedHref(
    String type, 
    String hreflang) 
      throws MimeTypeParseException {
    Link link = getAlternateLink(type, hreflang);
    return (link != null) ? link.getResolvedHref() : null;
  }

  public Link getEditMediaLink(
    String type, 
    String hreflang) 
      throws MimeTypeParseException {
    return selectLink(getLinks(Link.REL_EDIT_MEDIA), type, hreflang);
  }

  public IRI getEditMediaLinkResolvedHref(
    String type, 
    String hreflang) 
      throws MimeTypeParseException {
    Link link = getEditMediaLink(type, hreflang);
    return (link != null) ? link.getResolvedHref() : null;
  }
  
  public void setDraft(boolean draft) {
    Control control = getControl();
    if (control == null && draft) {
      control = ((FOMFactory)factory).newControl(this);
    }
    if (control != null) control.setDraft(draft);
  }
  
  /**
   * Returns true if this entry is a draft
   */
  public boolean isDraft() {
    Control control = getControl();
    return (control != null) ? control.isDraft() : false;
  }
}
