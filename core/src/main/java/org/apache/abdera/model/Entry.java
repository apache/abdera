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
import java.util.Calendar;
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
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface Entry 
  extends ExtensibleElement, ExtensionElement {

  /**
   * Returns the first author listed for the entry
   */
  Person getAuthor();
  
  /**
   * Returns the complete set of authors listed for the entry 
   */
  List<Person> getAuthors();
  
  /**
   * Sets the complete set of authors for the entry
   */
  void setAuthors(List<Person> people);
  
  /**
   * Adds an individual author to the entry
   */
  void addAuthor(Person person);

  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addAuthor(String name);

  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addAuthor(String name, String email, String uri) throws URISyntaxException;
  
  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addAuthor(String name, String email, URI uri) throws URISyntaxException;
  
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
   * Lists the complete set of categories using the specified scheme
   */
  List<Category> getCategories(URI scheme);
  
  /**
   * Sets the complete set of categories
   */
  void setCategories(List<Category> categories);
  
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
   */
  Category addCategory(URI scheme, String term, String label);
  
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
  Content setContentAsText(String value);
  
  /**
   * Sets the content for this entry as @type="html".
   * @param value The unescaped HTML string to set as the content
   * @param baseUri The value of the content element's xml:base. Null if none. 
   */
  Content setContentAsHtml(String value, URI baseUri);

  /**
   * Sets the content for this entry as @type="html".
   * @param value The unescaped HTML string to set as the content
   * @param baseUri The value of the content element's xml:base. Null if none. 
   */
  Content setContentAsHtml(String value);
  
  /**
   * Sets the content for this entry as @type="html".
   * @param value The unescaped HTML string to set as the content
   * @param baseUri The value of the content element's xml:base. Null if none. 
   * @throws URISyntaxException 
   */
  Content setContentAsHtml(String value, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the content for this entry as @type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value A string containing the XHTML.
   * @param baseUri The value of the content element's xml:base. Null if none.
   */
  Content setContentAsXhtml(String value);
  
  /**
   * Sets the content for this entry as @type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value A string containing the XHTML.
   * @param baseUri The value of the content element's xml:base. Null if none.
   */
  Content setContentAsXhtml(String value, URI baseUri);
  
  /**
   * Sets the content for this entry as @type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value A string containing the XHTML.
   * @param baseUri The value of the content element's xml:base. Null if none.
   */
  Content setContentAsXhtml(String value, String baseUri) throws URISyntaxException;

  /**
   * Sets the content for this entry as @type="xhtml". The value is passed 
   * in as a parsed org.apache.abdera.model.Div
   * @param value A Div containing the XHTML
   * @param baseUri The value of the content element's xml:base. Null if none
   */
  Content setContentAsXhtml(Div value);
  
  /**
   * Sets the content for this entry as @type="xhtml". The value is passed 
   * in as a parsed org.apache.abdera.model.Div
   * @param value A Div containing the XHTML
   * @param baseUri The value of the content element's xml:base. Null if none
   */
  Content setContentAsXhtml(Div value, URI baseUri);

  /**
   * Sets the content for this entry as @type="xhtml". The value is passed 
   * in as a parsed org.apache.abdera.model.Div
   * @param value A Div containing the XHTML
   * @param baseUri The value of the content element's xml:base. Null if none
   */
  Content setContentAsXhtml(Div value, String baseUri) throws URISyntaxException;
  
  /**
   * Returns the text of the content element
   */
  String getContent();
  
  /**
   * Returns the content type
   */
  Content.Type getContentType();

  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(String value, MimeType type);
  
  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(String value, String type) throws MimeTypeParseException;
  
  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(String value, MimeType type, URI baseUri);

  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(String value, MimeType type, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContentAsXml(String value, String type, URI baseUri) throws MimeTypeParseException;  

  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContentAsXml(String value, String type, String baseUri) throws MimeTypeParseException, URISyntaxException;  

  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(ExtensionElement value, MimeType type);

  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(ExtensionElement value, String type) throws MimeTypeParseException;
  
  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(ExtensionElement value, MimeType type, URI baseUri);

  /**
   * Sets the content for this entry
   */
  Content setContentAsXml(ExtensionElement value, MimeType type, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContentAsXml(ExtensionElement value, String type, URI baseUri) throws MimeTypeParseException;

  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContentAsXml(ExtensionElement value, String type, String baseUri) throws MimeTypeParseException, URISyntaxException;  
  
  /**
   * Sets the content for this entry
   */
  Content setContentAsMedia(MimeType type, URI src, DataHandler dataHandler);

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   */
  Content setContentAsMedia(MimeType type, String src, DataHandler dataHandler) throws URISyntaxException;
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContentAsMedia(String type, URI src, DataHandler dataHandler) throws MimeTypeParseException;

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   * @throws MimeTypeParseException 
   */
  Content setContentAsMedia(String type, String src, DataHandler dataHandler) throws MimeTypeParseException, URISyntaxException;
  
  /**
   * Sets the content for this entry
   */
  Content setContentAsMedia(MimeType type, URI src, String value);

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   */
  Content setContentAsMedia(MimeType type, String src, String value) throws URISyntaxException;
  
  /**
   * Sets the content for this entry
   * @throws MimeTypeParseException 
   */
  Content setContentAsMedia(String type, URI src, String value) throws MimeTypeParseException;

  /**
   * Sets the content for this entry
   * @throws URISyntaxException 
   * @throws MimeTypeParseException 
   */
  Content setContentAsMedia(String type, String src, String value) throws MimeTypeParseException, URISyntaxException;

  
  /**
   * Lists the complete set of contributors for this entry
   */
  List<Person> getContributors();
  
  /**
   * Sets the complete list of contributors for this entry
   */
  void setContributors(List<Person> people);
  
  /**
   * Adds an individual contributor to this entry
   */
  void addContributor(Person person);
  
  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addContributor(String name);

  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addContributor(String name, String email, String uri) throws URISyntaxException;
  
  /**
   * Adds an author
   * @throws URISyntaxException 
   */
  Person addContributor(String name, String email, URI uri) throws URISyntaxException;

  
  /**
   * Returns the universally unique identifier for this entry
   */
  IRI getIdElement();
  
  /**
   * Sets the universally unique identifier for this entry
   */
  void setIdElement(IRI id);
  
  /**
   * Returns the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  URI getId() throws URISyntaxException;

  /**
   * Sets the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  IRI setId(URI id) throws URISyntaxException;

  /**
   * Sets the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  IRI setId(String id) throws URISyntaxException;
  
  /**
   * Sets the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  IRI setId(URI id, boolean normalize) throws URISyntaxException;

  /**
   * Sets the universally unique identifier for this entry
   * @throws URISyntaxException 
   */
  IRI setId(String id, boolean normalize) throws URISyntaxException;
  
  /**
   * Lists the complete set of links for this entry
   */
  List<Link> getLinks();
  
  /**
   * Lists the complete set of links using the specified rel attribute value
   */
  List<Link> getLinks(String rel);
  
  /**
   * Sets the complete set of links for the entry
   */
  void setLinks(List<Link> links);
  
  /**
   * Adds an individual link to the entry
   */
  void addLink(Link link);
  
  Link addLink(String href) throws URISyntaxException;
  
  Link addLink(String href, String rel) throws URISyntaxException;
  
  Link addLink(URI href);
  
  Link addLink(URI href, String rel);
  
  Link addLink(URI href, String rel, MimeType type, String title, String hreflang, long length);
  
  Link addLink(String href, String rel, MimeType type, String title, String hreflang, long length) throws URISyntaxException;
  
  Link addLink(URI href, String rel, String type, String title, String hreflang, long length) throws MimeTypeParseException;
  
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
  
  String getPublishedString();
  
  Date getPublished();
  
  DateTime setPublished(Date value);
  
  DateTime setPublished(Calendar value);
  
  DateTime setPublished(long value);
  
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
   * Sets the value of the rights element as type="text"
   */
  Text setRightsAsText(String value);
  
  /**
   * Sets the value of the rights element as type="html"
   * @param value The string containing the unescaped HTML
   * @param baseUri The content element's xml:base. Null if none
   */
  Text setRightsAsHtml(String value);

  /**
   * Sets the rights for this entry as @type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The right element's xml:base. Null if none
   */
  Text setRightsAsXhtml(String value);
  
  /**
   * Sets the value of the rights element as type="xhtml"
   * @param value The XHTML Div
   * @param baseUri The right element's xml:base. Null if none
   */
  Text setRightsAsXhtml(Div value);

  
  /**
   * Sets the value of the rights element as type="html"
   * @param value The string containing the unescaped HTML
   * @param baseUri The content element's xml:base. Null if none
   */
  Text setRightsAsHtml(String value, URI baseUri);

  /**
   * Sets the rights for this entry as @type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The right element's xml:base. Null if none
   */
  Text setRightsAsXhtml(String value, URI baseUri);
  
  /**
   * Sets the value of the rights element as type="xhtml"
   * @param value The XHTML Div
   * @param baseUri The right element's xml:base. Null if none
   */
  Text setRightsAsXhtml(Div value, URI baseUri);
  
  /**
   * Sets the value of the rights element as type="html"
   * @param value The string containing the unescaped HTML
   * @param baseUri The content element's xml:base. Null if none
   * @throws URISyntaxException 
   */
  Text setRightsAsHtml(String value, String baseUri) throws URISyntaxException;

  /**
   * Sets the rights for this entry as @type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The right element's xml:base. Null if none
   */
  Text setRightsAsXhtml(String value, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the value of the rights element as type="xhtml"
   * @param value The XHTML Div
   * @param baseUri The right element's xml:base. Null if none
   */
  Text setRightsAsXhtml(Div value, String baseUri) throws URISyntaxException;
  
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
   * Sets the summary of this entry as type="text"
   */
  Text setSummaryAsText(String value);
  
  /**
   * Sets the summary of this entry as type="html"
   */
  Text setSummaryAsHtml(String value);
  
  /**
   * Sets the summary for this entry as type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The summary element's xml:base. Null if none
   */
  Text setSummaryAsXhtml(String value);
  
  /**
   * Sets the summary for this entr as type="xhtml"
   */
  Text setSummaryAsXhtml(Div value);
  
  /**
   * Sets the summary of this entry as type="html"
   */
  Text setSummaryAsHtml(String value, URI baseUri);
  
  /**
   * Sets the summary for this entry as type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The summary element's xml:base. Null if none
   */
  Text setSummaryAsXhtml(String value, URI baseUri);
  
  /**
   * Sets the summary for this entr as type="xhtml"
   */
  Text setSummaryAsXhtml(Div value, URI baseUri);
    
  /**
   * Sets the summary of this entry as type="html"
   */
  Text setSummaryAsHtml(String value, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the summary for this entry as type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The summary element's xml:base. Null if none
   */
  Text setSummaryAsXhtml(String value, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the summary for this entr as type="xhtml"
   */
  Text setSummaryAsXhtml(Div value, String baseUri) throws URISyntaxException;
  
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
   * Sets the value of atom:title as type="text"
   */
  Text setTitleAsText(String value);
  
  /**
   * Sets the value of atom:title as type="html"
   */
  Text setTitleAsHtml(String value);
  
  /**
   * Sets the title for this entry as type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The title element's xml:base. Null if none
   */
  Text setTitleAsXhtml(String value);
  
  /**
   * Sets the title for this entry as type="xhtml".
   * @param value The parsed XHTML Div
   * @param baseUri The title element's xml:base. Null if none
   */
  Text setTitleAsXhtml(Div value);
  
  /**
   * Sets the value of atom:title as type="html"
   */
  Text setTitleAsHtml(String value, URI baseUri);
  
  /**
   * Sets the title for this entry as type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The title element's xml:base. Null if none
   */
  Text setTitleAsXhtml(String value, URI baseUri);
  
  /**
   * Sets the title for this entry as type="xhtml".
   * @param value The parsed XHTML Div
   * @param baseUri The title element's xml:base. Null if none
   */
  Text setTitleAsXhtml(Div value, URI baseUri);
  
  /**
   * Sets the value of atom:title as type="html"
   */
  Text setTitleAsHtml(String value, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the title for this entry as type="xhtml".  The value is passed in
   * as a string containing unparsed XHTML with a &lt;div&lt; as the root.
   * For example, "&lt;div xmlns="http://www.w3.org/1999/xhtml">foo&lt;/div>"
   * @param value The string containing an unparsed XHTML div
   * @param baseUri The title element's xml:base. Null if none
   */
  Text setTitleAsXhtml(String value, String baseUri) throws URISyntaxException;
  
  /**
   * Sets the title for this entry as type="xhtml".
   * @param value The parsed XHTML Div
   * @param baseUri The title element's xml:base. Null if none
   */
  Text setTitleAsXhtml(Div value, String baseUri) throws URISyntaxException;
  
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
  
  String getUpdatedString();
  
  Date getUpdated();
  
  DateTime setUpdated(Date value);
  
  DateTime setUpdated(Calendar value);
  
  DateTime setUpdated(long value);
  
  DateTime setUpdated(String value);
  
  /**
   * Returns this entries Atom Publishing Protocol control element
   */
  Control getControl();
  
  /**
   * Sets this entries Atom Publishing Protocol control element
   */
  void setControl(Control control);
  
  /**
   * Returns the first link with the specified rel attribute value
   */
  Link getLink(String rel);
  
  /**
   * Returns this entries first alternate link
   */
  Link getAlternateLink();
  
  /**
   * Returns this entries first enclosure link
   */
  Link getEnclosureLink();
  
  /**
   * Returns this entries first edit link
   */
  Link getEditLink();
  
  void addInReplyTo(InReplyTo replyTo);
  
  InReplyTo addInReplyTo();
  
  InReplyTo addInReplyTo(Entry ref);
  
  InReplyTo addInReplyTo(URI ref);
  
  InReplyTo addInReplyTo(String ref) throws URISyntaxException;
  
  InReplyTo addInReplyTo(URI ref, URI source, URI href, MimeType type);
  
  InReplyTo addInReplyTo(String ref, String source, String href, String type) throws URISyntaxException, MimeTypeParseException;
  
  InReplyTo getInReplyTo();
  
  List<InReplyTo> getInReplyTos();
  
}
