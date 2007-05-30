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
package org.apache.abdera.contrib.rss;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.parser.stax.util.FOMElementIterator;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.abdera.util.Constants;

public class RssItem 
  extends ExtensibleElementWrapper 
  implements Entry {

  public RssItem(Element internal) {
    super(internal);
  }

  public RssItem(Factory factory, QName qname) {
    super(factory, qname);
  }

  public void addAuthor(Person person) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Person addAuthor(String name) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Person addAuthor(String name, String email, String uri) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void addCategory(Category category) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Category addCategory(String term) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Category addCategory(String scheme, String term, String label) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void addContributor(Person person) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Person addContributor(String name) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Person addContributor(String name, String email, String uri) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void addLink(Link link) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link addLink(String href) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link addLink(String href, String rel) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link addLink(String href, String rel, String type, String title,
      String hreflang, long length) throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link getAlternateLink() {
    RssGuid guid = (RssGuid) getIdElement();
    if (guid != null && guid.isPermalink())
      return guid;
    return getExtension(RssConstants.QNAME_LINK);
  }

  public Link getAlternateLink(String type, String hreflang)
      throws MimeTypeParseException {
    return getAlternateLink();
  }

  public IRI getAlternateLinkResolvedHref() {
    Link link = getAlternateLink();
    return (link != null) ? link.getResolvedHref() : null;
  }

  public IRI getAlternateLinkResolvedHref(String type, String hreflang)
      throws MimeTypeParseException {
    return getAlternateLinkResolvedHref();
  }

  public Person getAuthor() {
    return getExtension(RssConstants.QNAME_AUTHOR);
  }

  public List<Person> getAuthors() {
    return getExtensions(RssConstants.QNAME_AUTHOR);
  }

  public List<Category> getCategories() {
    return getExtensions(RssConstants.QNAME_CATEGORY);
  }

  @SuppressWarnings("unchecked")
  public List<Category> getCategories(String scheme) {
    Iterator i = new FOMElementIterator(getInternal(), RssCategory.class, new QName("domain"), scheme, null);
    return new FOMList<Category>(i);
  }

  public String getContent() {
    Content content = getContentElement();
    if (content == null) return getSummary();
    return content.getValue();
  }

  public Content getContentElement() {
    Content content = getExtension(RssConstants.QNAME_CONTENT_ENCODED);
    // what else to return other than content:encoded and possibly atom:content?
    if (content == null) {
      content = getExtension(Constants.CONTENT);
    }
    return content;
  }

  public MimeType getContentMimeType() {
    return null;
  }

  public IRI getContentSrc() {
    throw new UnsupportedOperationException("RSS does not support content by reference");
  }

  public InputStream getContentStream() throws IOException {
    throw new UnsupportedOperationException("RSS does not support encoded binary content");
  }

  public Type getContentType() {
    Content content = getContentElement();
    if (content == null) {
      Text text = getSummaryElement();
      switch (text.getTextType()) {
        case TEXT: return Content.Type.TEXT;
        case HTML: return Content.Type.HTML;
        case XHTML: return Content.Type.XHTML;
        default: return Content.Type.HTML;
      }
    } else {
      return content.getContentType();
    }
  }

  public List<Person> getContributors() {
    return null;
  }

  public Control getControl(boolean create) {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public Control getControl() {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public Link getEditLink() {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public IRI getEditLinkResolvedHref() {
    @SuppressWarnings("unused")
    Link link = getEditLink();
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public Link getEditMediaLink() {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public Link getEditMediaLink(String type, String hreflang)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public IRI getEditMediaLinkResolvedHref() {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public IRI getEditMediaLinkResolvedHref(String type, String hreflang)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public Date getEdited() {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public DateTime getEditedElement() {
    throw new UnsupportedOperationException("RSS does not support APP features");
  }

  public Link getEnclosureLink() {
    return getExtension(RssConstants.QNAME_ENCLOSURE);
  }

  public IRI getEnclosureLinkResolvedHref() {
    Link link = getEnclosureLink();
    return (link != null) ? link.getHref() : null;
  }

  public IRI getId() {
    IRIElement iri = getIdElement();
    return (iri != null) ? iri.getValue() : null;
  }

  public IRIElement getIdElement() {
    return getExtension(RssConstants.QNAME_GUID);
  }

  public Link getLink(String rel) {
    if (rel.equals(Link.REL_ALTERNATE) || rel.equals(Link.REL_ALTERNATE_IANA)) {
      RssGuid guid = (RssGuid) getIdElement();
      if (guid != null && guid.isPermalink())
        return guid;
    }
    List<Link> links = FOMHelper.getLinks(getInternal(), rel);
    return (links != null && links.size() > 0) ? links.get(0) : null;
  }

  public IRI getLinkResolvedHref(String rel) {
    Link link = getLink(rel);
    return (link != null) ? link.getResolvedHref() : null;
  }

  public List<Link> getLinks() {
    return getExtensions(Constants.LINK);
  }

  public List<Link> getLinks(String rel) {
    return FOMHelper.getLinks(getInternal(), rel);
  }

  public List<Link> getLinks(String... rel) {
    return FOMHelper.getLinks(getInternal(), rel);
  }

  public Date getPublished() {
    DateTime dt = getPublishedElement();
    return (dt != null) ? dt.getDate() : null;
  }

  public DateTime getPublishedElement() {
    DateTime dt = getExtension(RssConstants.QNAME_PUBDATE);
    if (dt == null) dt = getExtension(RssConstants.QNAME_PUBDATE2);
    return dt;
  }

  public String getRights() {
    Text text = getRightsElement();
    return (text != null) ? text.getValue() : null;
  }

  public Text getRightsElement() {
    Element el = getParentElement();
    if (el instanceof RssChannel)
      return ((RssChannel)el).getRightsElement();
    else if (el instanceof RssFeed) 
      return ((RssFeed)el).getRightsElement();
    return null;
  }

  public org.apache.abdera.model.Text.Type getRightsType() {
    Text text = getRightsElement();
    return (text != null) ? text.getTextType() : null;
  }

  public Link getSelfLink() {
    return getLink("self");
  }

  public IRI getSelfLinkResolvedHref() {
    Link link = getSelfLink();
    return (link != null) ? link.getResolvedHref() : null;
  }

  public Source getSource() {
    return getExtension(RssConstants.QNAME_SOURCE);
  }

  public String getSummary() {
    Text text = getSummaryElement();
    return (text != null) ? text.getValue() : null;
  }

  public Text getSummaryElement() {
    return getExtension(RssConstants.QNAME_DESCRIPTION);
  }

  public org.apache.abdera.model.Text.Type getSummaryType() {
    Text text = getSummaryElement();
    return (text != null) ? text.getTextType() : null;
  }

  public String getTitle() {
    Text text = getTitleElement();
    return (text != null) ? text.getValue() : null;
  }

  public Text getTitleElement() {
    return getExtension(RssConstants.QNAME_TITLE);
  }

  public org.apache.abdera.model.Text.Type getTitleType() {
    Text text = getTitleElement();
    return (text != null) ? text.getTextType() : null;
  }

  public Date getUpdated() {
    return getPublished();
  }

  public DateTime getUpdatedElement() {
    return getPublishedElement();
  }

  public boolean isDraft() {
    return false;
  }

  public IRIElement newId() {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(String value, Type type) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(Element value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(Element element, String mediaType)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(DataHandler dataHandler)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(DataHandler dataHandler, String mediatype)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(InputStream inputStream) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(InputStream inputStream, String mediatype)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(String value, String mediatype)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContent(IRI uri, String mediatype)
      throws MimeTypeParseException {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContentAsHtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Content setContentAsXhtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setContentElement(Content content) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setControl(Control control) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setDraft(boolean draft) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public DateTime setEdited(Date value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public DateTime setEdited(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setEditedElement(DateTime modified) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public IRIElement setId(String id) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public IRIElement setId(String id, boolean normalize) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setIdElement(IRIElement id) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public DateTime setPublished(Date value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public DateTime setPublished(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setPublishedElement(DateTime dateTime) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setRights(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setRights(String value, org.apache.abdera.model.Text.Type type) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setRights(Div value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setRightsAsHtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setRightsAsXhtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setRightsElement(Text text) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setSource(Source source) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setSummary(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setSummary(String value, org.apache.abdera.model.Text.Type type) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setSummary(Div value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setSummaryAsHtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setSummaryAsXhtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setSummaryElement(Text text) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setTitle(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setTitle(String value, org.apache.abdera.model.Text.Type type) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setTitle(Div value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setTitleAsHtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Text setTitleAsXhtml(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setTitleElement(Text title) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public DateTime setUpdated(Date value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public DateTime setUpdated(String value) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public void setUpdatedElement(DateTime updated) {
    throw new UnsupportedOperationException("Modifications are not allowed");
  }

  public Link getComments() {
    return getExtension(RssConstants.QNAME_COMMENTS);
  }
}
