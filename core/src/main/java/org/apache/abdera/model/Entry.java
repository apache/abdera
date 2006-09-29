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
package org.apache.abdera.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * <p>Represents an Atom Entry element.</p>
 * 
 * <p>Per RFC4287:</p>
 * 
 * <pre>
  * The "atom:entry" element represents an individual entry, acting as a
  * container for metadata and data associated with the entry.  This
  * element can appear as a child of the atom:feed element, or it can
  * appear as the document (i.e., top-level) element of a stand-alone
  * Atom Entry Document.
  *
  * atomEntry =
  *    element atom:entry {
  *       atomCommonAttributes,
  *       (atomAuthor*
  *        &amp; atomCategory*
  *        &amp; atomContent?
  *        &amp; atomContributor*
  *        &amp; atomId
  *        &amp; atomLink*
  *        &amp; atomPublished?
  *        &amp; atomRights?
  *        &amp; atomSource?
  *        &amp; atomSummary?
  *        &amp; atomTitle
  *        &amp; atomUpdated
  *        &amp; extensionElement*)
  *    }
  *
  * This specification assigns no significance to the order of appearance
  * of the child elements of atom:entry.
  *
  * The following child elements are defined by this specification (note
  * that it requires the presence of some of these elements):
  *
  * o  atom:entry elements MUST contain one or more atom:author elements,
  *    unless the atom:entry contains an atom:source element that
  *    contains an atom:author element or, in an Atom Feed Document, the
  *    atom:feed element contains an atom:author element itself.
  * o  atom:entry elements MAY contain any number of atom:category
  *    elements.
  * o  atom:entry elements MUST NOT contain more than one atom:content
  *    element.
  * o  atom:entry elements MAY contain any number of atom:contributor
  *    elements.
  * o  atom:entry elements MUST contain exactly one atom:id element.
  * o  atom:entry elements that contain no child atom:content element
  *    MUST contain at least one atom:link element with a rel attribute
  *    value of "alternate".
  * o  atom:entry elements MUST NOT contain more than one atom:link
  *    element with a rel attribute value of "alternate" that has the
  *    same combination of type and hreflang attribute values.
  * o  atom:entry elements MAY contain additional atom:link elements
  *    beyond those described above.
  * o  atom:entry elements MUST NOT contain more than one atom:published
  *    element.
  * o  atom:entry elements MUST NOT contain more than one atom:rights
  *    element.
  * o  atom:entry elements MUST NOT contain more than one atom:source
  *    element.
  * o  atom:entry elements MUST contain an atom:summary element in either
  *    of the following cases:
  *    *  the atom:entry contains an atom:content that has a "src"
  *       attribute (and is thus empty).
  *    *  the atom:entry contains content that is encoded in Base64;
  *       i.e., the "type" attribute of atom:content is a MIME media type
  *       [MIMEREG], but is not an XML media type [RFC3023], does not
  *       begin with "text/", and does not end with "/xml" or "+xml".
  * o  atom:entry elements MUST NOT contain more than one atom:summary
  *    element.
  * o  atom:entry elements MUST contain exactly one atom:title element.
  * o  atom:entry elements MUST contain exactly one atom:updated element.
 * </pre>
 */
public interface Entry 
  extends ExtensibleElement {

  /**
   * Returns the first author listed for the entry
   */
  Person getAuthor();
  
  /**
   * Returns the complete set of authors listed for the entry 
   */
  List<Person> getAuthors();
  
  /**
   * Adds an individual author to the entry
   */
  void addAuthor(Person person);

  /**
   * Adds an author
   */
  Person addAuthor(String name);

  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addAuthor(String name, String email, String uri) throws URISyntaxException;
  
  /**
   * Lists the complete set of categories listed for the entry
   */
  List<Category> getCategories();
  
  /**
   * Lists the complete set of categories using the specified scheme
   * @throws URISyntaxException 
   */
  List<Category> getCategories(String scheme) throws URISyntaxException;

  /**
   * Adds an individual category to the entry
   */
  void addCategory(Category category);
  
  /**
   * Adds a category to the entry
   */
  Category addCategory(String term);

  /**
   * Adds a category to the entry
   * @throws URISyntaxException 
   */
  Category addCategory(String scheme, String term, String label) throws URISyntaxException;
  
  /**
   * Returns the content for this entry
   */
  Content getContentElement();
  
  /**
   * Sets the content for this entry
   */
  void setContentElement(Content content);

  /**
   * Sets the content for this entry as @type="text"
   */
  Content setContent(String value);

  /**
   * Sets the content for this entry as @type="html"
   */
  Content setContentAsHtml(String value);

  /**
   * Sets the content for this entry as @type="xhtml"
   */
  Content setContentAsXhtml(String value);
  
  /**
   * Sets the content for this entry
   */
  Content setContent(String value, Content.Type type);
  
  /**
   * Sets the content for this entry
   */
  Content setContent(Element value);  
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContent(Element element, String mediaType) throws MimeTypeParseException;
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContent(DataHandler dataHandler) throws MimeTypeParseException;
  
  /**
   * Sets the content for this entry
   */
  Content setContent(DataHandler dataHandler, String mediatype) throws MimeTypeParseException;
  
  /**
   * Sets the content for this entry
   */
  Content setContent(String value, String mediatype) throws MimeTypeParseException;
  
  /**
   * Sets the content for this entry as out of line.
   * 
   * @param uri URI of the content (value of the "src" attribute).
   * @param mediatype Type of the content.
   * @return The new content element.
   * @throws MimeTypeParseException if the mime type is invalid.
   * @throws URISyntaxException if the URI is invalid.
   */
  Content setContent(URI uri, String mediatype) throws MimeTypeParseException, URISyntaxException;
  
  /**
   * Returns the text of the content element
   */
  String getContent();
  
  /**
   * Returns the content/@src attribute, if any
   * @throws URISyntaxException 
   */
  URI getContentSrc() throws URISyntaxException;
  
  /**
   * Returns the content type
   */
  Content.Type getContentType();
  
  /**
   * Returns the media type of the content type or null if type equals 'text',
   * 'html' or 'xhtml'
   */
  MimeType getContentMimeType();
  
  /**
   * Lists the complete set of contributors for this entry
   */
  List<Person> getContributors();
  
  /**
   * Adds an individual contributor to this entry
   */
  void addContributor(Person person);
  
  /**
   * Adds a contributor 
   */
  Person addContributor(String name);

  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addContributor(String name, String email, String uri) throws URISyntaxException;
  
  /**
   * Returns the universally unique identifier for this entry
   */
  IRIElement getIdElement();
  
  /**
   * Sets the universally unique identifier for this entry
   */
  void setIdElement(IRIElement id);
  
  /**
   * Returns the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  URI getId() throws URISyntaxException;

  /**
   * Sets the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  IRIElement setId(String id) throws URISyntaxException;
  
  /**
   * Sets the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  IRIElement setId(String id, boolean normalize) throws URISyntaxException;
  
  /**
   * Lists the complete set of links for this entry
   */
  List<Link> getLinks();
  
  /**
   * Lists the complete set of links using the specified rel attribute value
   */
  List<Link> getLinks(String rel);
  
  /**
   * Adds an individual link to the entry
   */
  void addLink(Link link);
  
  Link addLink(String href) throws URISyntaxException;
  
  Link addLink(String href, String rel) throws URISyntaxException;
  
  Link addLink(String href, String rel, String type, String title, String hreflang, long length) throws URISyntaxException, MimeTypeParseException;
  
  /**
   * RFC4287: The "atom:published" element is a Date construct indicating an
   * instant in time associated with an event early in the life cycle of
   * the entry... Typically, atom:published will be associated with the initial
   * creation or first availability of the resource.
   */
  DateTime getPublishedElement();
  
  /**
   * RFC4287: The "atom:published" element is a Date construct indicating an
   * instant in time associated with an event early in the life cycle of
   * the entry... Typically, atom:published will be associated with the initial
   * creation or first availability of the resource.
   */
  void setPublishedElement(DateTime dateTime);
  
  Date getPublished();
  
  DateTime setPublished(Date value);
  
  DateTime setPublished(String value);
  
  /**
   * <p>The rights element is typically used to convey a human readable 
   * copyright (e.g. "&lt;atom:rights>Copyright (c), 2006&lt;/atom:rights>).</p>
   * 
   * <p>RFC4287: The "atom:rights" element is a Text construct that conveys 
   * information about rights held in and over an entry or feed.</p>
   */
  Text getRightsElement();

  /**
   * <p>The rights element is typically used to convey a human readable 
   * copyright (e.g. "&lt;atom:rights>Copyright (c), 2006&lt;/atom:rights>).</p>
   * 
   * <p>RFC4287: The "atom:rights" element is a Text construct that conveys 
   * information about rights held in and over an entry or feed.</p>
   */
  void setRightsElement(Text text);
  
  /**
   * Sets the value of the rights as @type="text"
   */
  Text setRights(String value);

  /**
   * Sets the value of the rights as @type="html"
   */
  Text setRightsAsHtml(String value);
  
  /**
   * Sets the value of the rights as @type="xhtml"
   */
  Text setRightsAsXhtml(String value);
  
  /**
   * Sets the value of the rights
   */
  Text setRights(String value, Text.Type type);
  
  /**
   * Sets the value of the right as @type="xhtml"
   */
  Text setRights(Div value);

  /**
   * Return the String value of the atom:rights element
   */
  String getRights();
  
  /**
   * Return the @type of the atom:rights element
   */
  Text.Type getRightsType();
  
  /**
   * <p>Returns the source element for this entry.</p>
   * 
   * <p>RFC4287: If an atom:entry is copied from one feed into another feed, 
   * then the source atom:feed's metadata (all child elements of atom:feed 
   * other than the atom:entry elements) MAY be preserved within the copied
   * entry by adding an atom:source child element, if it is not already
   * present in the entry, and including some or all of the source feed's
   * Metadata elements as the atom:source element's children.  Such
   * metadata SHOULD be preserved if the source atom:feed contains any of
   * the child elements atom:author, atom:contributor, atom:rights, or
   * atom:category and those child elements are not present in the source
   * atom:entry.</p>
   */
  Source getSource();

  /**
   * <p>Returns the source element for this entry.</p>
   * 
   * <p>RFC4287: If an atom:entry is copied from one feed into another feed, 
   * then the source atom:feed's metadata (all child elements of atom:feed 
   * other than the atom:entry elements) MAY be preserved within the copied
   * entry by adding an atom:source child element, if it is not already
   * present in the entry, and including some or all of the source feed's
   * Metadata elements as the atom:source element's children.  Such
   * metadata SHOULD be preserved if the source atom:feed contains any of
   * the child elements atom:author, atom:contributor, atom:rights, or
   * atom:category and those child elements are not present in the source
   * atom:entry.</p>
   */
  void setSource(Source source);
  
  /**
   * RFC4287: The "atom:summary" element is a Text construct that conveys 
   * a short summary, abstract, or excerpt of an entry... It is not advisable 
   * for the atom:summary element to duplicate atom:title or atom:content 
   * because Atom Processors might assume there is a useful summary when there 
   * is none.
   */
  Text getSummaryElement();

  /**
   * RFC4287: The "atom:summary" element is a Text construct that conveys 
   * a short summary, abstract, or excerpt of an entry... It is not advisable 
   * for the atom:summary element to duplicate atom:title or atom:content 
   * because Atom Processors might assume there is a useful summary when there 
   * is none.
   */
  void setSummaryElement(Text text);
  
  /**
   * Sets the value of the summary as @type="text"
   */
  Text setSummary(String value);

  /**
   * Sets the value of the summary as @type="html"
   */
  Text setSummaryAsHtml(String value);
  
  /**
   * Sets the value of the summary as @type="xhtml"
   */
  Text setSummaryAsXhtml(String value);
  
  /**
   * Sets the value of the summary
   */
  Text setSummary(String value, Text.Type type);
  
  /**
   * Sets the value of the summary as @type="xhtml"
   */
  Text setSummary(Div value);
  
  /**
   * Returns the text string value of this summary
   */
  String getSummary();
  
  /**
   * Returns the summary type
   */
  Text.Type getSummaryType();
  
  /**
   * RFC4287: The "atom:title" element is a Text construct that conveys a 
   * human-readable title for an entry or feed.
   */
  Text getTitleElement();

  /**
   * RFC4287: The "atom:title" element is a Text construct that conveys a 
   * human-readable title for an entry or feed.
   */
  void setTitleElement(Text title);
  
  /**
   * Sets the value of the title as @type="text"
   */
  Text setTitle(String value);
  
  /**
   * Sets the value of the title as @type="html"
   */
  Text setTitleAsHtml(String value);
  
  /**
   * Sets the value of the title as @type="xhtml"
   */
  Text setTitleAsXhtml(String value);
  
  /**
   * Sets the value of the title
   */
  Text setTitle(String value, Text.Type type);
  
  /**
   * Sets the value of the title as @type="xhtml"
   */
  Text setTitle(Div value);
  
  /**
   * Returns the text string value of the title element
   */
  String getTitle();
  
  /**
   * Returns the @type of this entries title
   */
  Text.Type getTitleType();
  
  /**
   * RFC4287: The "atom:updated" element is a Date construct indicating 
   * the most recent instant in time when an entry or feed was modified 
   * in a way the publisher considers significant.  Therefore, not all
   * modifications necessarily result in a changed atom:updated value.
   */
  DateTime getUpdatedElement();

  /**
   * RFC4287: The "atom:updated" element is a Date construct indicating 
   * the most recent instant in time when an entry or feed was modified 
   * in a way the publisher considers significant.  Therefore, not all
   * modifications necessarily result in a changed atom:updated value.
   */
  void setUpdatedElement(DateTime updated);
  
  Date getUpdated();
  
  DateTime setUpdated(Date value);
  
  DateTime setUpdated(String value);
  
  /**
   * APP Introduces a new app:modified element whose value changes 
   * every time the entry is updated
   */
  DateTime getModifiedElement();
  
  void setModifiedElement(DateTime modified);
  
  Date getModified();
  
  DateTime setModified(Date value);
  
  DateTime setModified(String value);
  
  /**
   * Returns this entries Atom Publishing Protocol control element
   */
  Control getControl();
  
  /**
   * Sets this entries Atom Publishing Protocol control element
   */
  void setControl(Control control);
  
  /**
   * Sets whether or not this entry is a draft
   */
  void setDraft(boolean draft);
  
  /**
   * Returns true if this entry is a draft
   */
  boolean isDraft();
  
  /**
   * Returns the first link with the specified rel attribute value
   */
  Link getLink(String rel);
  
  /**
   * Returns this entries first alternate link
   */
  Link getAlternateLink();
  
  /**
   * Returns the first alternate link matching the specified type and hreflang
   * @throws MimeTypeParseException 
   */
  Link getAlternateLink(String type, String hreflang) throws MimeTypeParseException;
  
  /**
   * Returns this entries first enclosure link
   */
  Link getEnclosureLink();
  
  /**
   * Returns this entries first edit link
   */
  Link getEditLink();
  
  /**
   * Returns this entries first edit-media link (if any)
   */
  Link getEditMediaLink();
  
  /**
   * Returns the first edit-media link matching the specified type and hreflang
   * @throws MimeTypeParseException 
   */
  Link getEditMediaLink(String type, String hreflang) throws MimeTypeParseException;
  
  /**
   * Returns this entries first self link
   */
  Link getSelfLink();
 
  URI getLinkResolvedHref(String rel) throws URISyntaxException;
  URI getAlternateLinkResolvedHref() throws URISyntaxException;
  URI getAlternateLinkResolvedHref(String type, String hreflang) throws URISyntaxException, MimeTypeParseException;
  URI getEnclosureLinkResolvedHref() throws URISyntaxException;
  URI getEditLinkResolvedHref() throws URISyntaxException;
  URI getEditMediaLinkResolvedHref() throws URISyntaxException;
  URI getEditMediaLinkResolvedHref(String type, String hreflang) throws URISyntaxException, MimeTypeParseException;
  URI getSelfLinkResolvedHref() throws URISyntaxException;
}
