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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;

/**
 * <p>
 * Represents an Atom Entry element.
 * </p>
 * <p>
 * Per RFC4287:
 * </p>
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
public interface Entry extends ExtensibleElement {

    /**
     * Returns the first author listed for the entry
     * 
     * @return The atom:author
     */
    Person getAuthor();

    /**
     * Returns the complete set of authors listed for the entry
     * 
     * @return The listing of atom:author elements
     */
    List<Person> getAuthors();

    /**
     * Adds an individual author to the entry
     * 
     * @param person The person to add
     */
    Entry addAuthor(Person person);

    /**
     * Adds an author
     * 
     * @param name The name of the author
     * @return The newly created atom:author element
     */
    Person addAuthor(String name);

    /**
     * Adds an author
     * 
     * @param name The name of the author
     * @param email The author's email address
     * @param uri A URI belonging to the author
     * @return The newly created atom:author element
     * @throws IRISyntaxException if the URI is malformed
     */
    Person addAuthor(String name, String email, String uri);

    /**
     * Lists the complete set of categories listed for the entry
     * 
     * @return The listing of atom:category elements
     */
    List<Category> getCategories();

    /**
     * Lists the complete set of categories using the specified scheme A listing of atom:category elements using the
     * specified scheme
     * 
     * @throws IRISyntaxException if the scheme is malformed
     */
    List<Category> getCategories(String scheme);

    /**
     * Adds an individual category to the entry
     * 
     * @param category The atom:category element to add
     */
    Entry addCategory(Category category);

    /**
     * Adds a category to the entry
     * 
     * @param term The category term to add
     * @return The newly created atom:category
     */
    Category addCategory(String term);

    /**
     * Adds a category to the entry
     * 
     * @param scheme The category scheme
     * @param term The category term
     * @param label The human readable label
     * @return The newly create atom:category
     * @throws IRISyntaxException if the scheme is malformed
     */
    Category addCategory(String scheme, String term, String label);

    /**
     * Returns the content for this entry
     * 
     * @return The atom:content element
     */
    Content getContentElement();

    /**
     * Sets the content for this entry
     * 
     * @param content The atom:content element
     */
    Entry setContentElement(Content content);

    /**
     * Sets the content for this entry as @type="text"
     * 
     * @param value The text value of the content
     * @return The newly created atom:content
     */
    Content setContent(String value);

    /**
     * Sets the content for this entry as @type="html"
     * 
     * @param value The text value of the content. Special characters will be escaped (e.g. &amp; will become &amp;amp;)
     * @return The newly created atom:content
     */
    Content setContentAsHtml(String value);

    /**
     * Sets the content for this entry as @type="xhtml"
     * 
     * @param value The text value of the content. The text will be parsed as XHTML
     * @return The newly created atom:content
     */
    Content setContentAsXhtml(String value);

    /**
     * Sets the content for this entry
     * 
     * @param value The text value of the content
     * @param type The Content Type of the text
     * @return The newly created atom:content
     */
    Content setContent(String value, Content.Type type);

    /**
     * Sets the content for this entry
     * 
     * @param value The content element value. If the value is a Div, the the type attribute will be set to
     *            type="xhtml", otherwise type="application/xml"
     * @return The newly create atom:content
     */
    Content setContent(Element value);

    /**
     * Sets the content for this entry
     * 
     * @param element The element value
     * @param mediaType The media type of the element
     * @throws MimeTypeParseException if the mediaType is malformed
     */
    Content setContent(Element element, String mediaType);

    /**
     * Sets the content for this entry
     * 
     * @param dataHandler The Data Handler containing the binary content needing Base64 encoding.
     * @throws MimeTypeParseException if the media Type specified by the dataHandler is malformed
     */
    Content setContent(DataHandler dataHandler);

    /**
     * Sets the content for this entry
     * 
     * @param dataHandler The Data Handler containing the binary content needing Base64 encoding.
     * @param mediaType The mediatype of the binary content
     * @return The created content element
     * @throws MimeTypeParseException if the media type specified is malformed
     */
    Content setContent(DataHandler dataHandler, String mediatype);

    /**
     * Sets the content for this entry
     * 
     * @param inputStream An inputstream providing binary content
     * @return The created content element
     */
    Content setContent(InputStream inputStream);

    /**
     * Sets the content for this entry
     * 
     * @param inputStream An inputstream providing binary content
     * @param mediaType The mediatype of the binary content
     * @return The created content element
     * @throws MimeTypeParseException if the media type specified is malformed
     */
    Content setContent(InputStream inputStream, String mediatype);

    /**
     * Sets the content for this entry
     * 
     * @param value the string value of the content
     * @param mediatype the media type for the content
     * @return The newly created atom:content
     * @throws MimeTypeParseException if the media type is malformed
     */
    Content setContent(String value, String mediatype);

    /**
     * Sets the content for this entry as out of line.
     * 
     * @param uri URI of the content (value of the "src" attribute).
     * @param mediatype Type of the content.
     * @return The new content element.
     * @throws MimeTypeParseException if the mime type is invalid.
     * @throws IRISyntaxException if the URI is invalid.
     */
    Content setContent(IRI uri, String mediatype);

    /**
     * Returns the text of the content element
     * 
     * @return text content
     */
    String getContent();

    /**
     * Returns an input stream from the content element value. This is particularly useful when dealing with Base64
     * binary content.
     * 
     * @throws IOException
     */
    InputStream getContentStream() throws IOException;

    /**
     * Returns the content/@src attribute, if any
     * 
     * @return The src IRI
     * @throws IRISyntaxException if the src attribute is invalid
     */
    IRI getContentSrc();

    /**
     * Returns the content type
     * 
     * @return The content type
     */
    Content.Type getContentType();

    /**
     * Returns the media type of the content type or null if type equals 'text', 'html' or 'xhtml'
     * 
     * @return The content media type
     */
    MimeType getContentMimeType();

    /**
     * Lists the complete set of contributors for this entry
     * 
     * @return The listing of atom:contributor elements
     */
    List<Person> getContributors();

    /**
     * Adds an individual contributor to this entry
     * 
     * @param person The atom:contributor element
     */
    Entry addContributor(Person person);

    /**
     * Adds a contributor
     * 
     * @param name The contributor name
     * @return The newly created atom:contributor
     */
    Person addContributor(String name);

    /**
     * Adds an author
     * 
     * @param name The contributor name
     * @param email The contributor's email address
     * @param uri The contributor's URI
     * @return The newly created atom:contributor
     * @throws IRISyntaxException if the uri is malformed
     */
    Person addContributor(String name, String email, String uri);

    /**
     * Returns the universally unique identifier for this entry
     * 
     * @return The atom:id element
     */
    IRIElement getIdElement();

    /**
     * Sets the universally unique identifier for this entry
     * 
     * @param id The atom:id element
     */
    Entry setIdElement(IRIElement id);

    /**
     * Returns the universally unique identifier for this entry
     * 
     * @throws IRISyntaxException if the atom:id value is malformed
     */
    IRI getId();

    /**
     * Sets the universally unique identifier for this entry
     * 
     * @param id The atom:id value
     * @return The newly created atom:id element
     * @throws IRISyntaxException if the atom:id value is malformed
     */
    IRIElement setId(String id);

    /**
     * Creates a new randomized atom:id for the entry
     */
    IRIElement newId();

    /**
     * Sets the universally unique identifier for this entry
     * 
     * @param id The atom:id value
     * @param normalize true if the atom:id value should be normalized as called for by RFC4287
     * @return The newly created atom:id element
     * @throws IRISyntaxException if the atom:id value is malformed
     */
    IRIElement setId(String id, boolean normalize);

    /**
     * Lists the complete set of links for this entry
     * 
     * @return The listing of atom:link elements
     */
    List<Link> getLinks();

    /**
     * Lists the complete set of links using the specified rel attribute value
     * 
     * @param rel The rel attribute value to look for
     * @return The listing of atom:link element's with the rel attribute
     */
    List<Link> getLinks(String rel);

    /**
     * Lists the complete set of links using the specified rel attributes values
     * 
     * @param rels A listing of link relations
     * @return A listof atom:link elements
     */
    List<Link> getLinks(String... rel);

    /**
     * Adds an individual link to the entry
     * 
     * @param link the atom:link to add
     */
    Entry addLink(Link link);

    /**
     * Add a link to the entry
     * 
     * @param href The IRI of the link
     * @return The newly created atom:link
     * @throws IRISyntaxException if the href is malformed
     */
    Link addLink(String href);

    /**
     * Add a link to the entry
     * 
     * @param href The IRI of the link
     * @param rel The link rel attribute
     * @return The newly created atom:link
     * @throws IRISyntaxException if the href is malformed
     */
    Link addLink(String href, String rel);

    /**
     * Add a link to the entry
     * 
     * @param href The IRI of the link
     * @param rel The link rel attribute
     * @param type The media type of the link
     * @param hreflang The language of the target
     * @param length The length of the resource
     * @return The newly created atom:link
     * @throws IRISyntaxException if the href is malformed
     */
    Link addLink(String href, String rel, String type, String title, String hreflang, long length);

    /**
     * RFC4287: The "atom:published" element is a Date construct indicating an instant in time associated with an event
     * early in the life cycle of the entry... Typically, atom:published will be associated with the initial creation or
     * first availability of the resource.
     * 
     * @return The atom:published element
     */
    DateTime getPublishedElement();

    /**
     * RFC4287: The "atom:published" element is a Date construct indicating an instant in time associated with an event
     * early in the life cycle of the entry... Typically, atom:published will be associated with the initial creation or
     * first availability of the resource.
     * 
     * @param dateTime the atom:published element
     */
    Entry setPublishedElement(DateTime dateTime);

    /**
     * Return the value of the atom:published element
     * 
     * @return a java.util.Date for the atom:published value
     */
    Date getPublished();

    /**
     * Set the value of the atom:published element
     * 
     * @param value The java.util.Date
     * @return The newly created atom:published element
     */
    DateTime setPublished(Date value);

    /**
     * Set the value of the atom:published element using the serialized string value
     * 
     * @param value The serialized date
     * @return The newly created atom:published element
     */
    DateTime setPublished(String value);

    /**
     * <p>
     * The rights element is typically used to convey a human readable copyright (e.g. "&lt;atom:rights>Copyright (c),
     * 2006&lt;/atom:rights>).
     * </p>
     * <p>
     * RFC4287: The "atom:rights" element is a Text construct that conveys information about rights held in and over an
     * entry or feed.
     * </p>
     * 
     * @return The atom:rights element
     */
    Text getRightsElement();

    /**
     * <p>
     * The rights element is typically used to convey a human readable copyright (e.g. "&lt;atom:rights>Copyright (c),
     * 2006&lt;/atom:rights>).
     * </p>
     * <p>
     * RFC4287: The "atom:rights" element is a Text construct that conveys information about rights held in and over an
     * entry or feed.
     * </p>
     * 
     * @param text The atom:rights element
     */
    Entry setRightsElement(Text text);

    /**
     * Sets the value of the rights as @type="text"
     * 
     * @param value The text value of the atom:rights element
     * @return The newly created atom:rights element
     */
    Text setRights(String value);

    /**
     * Sets the value of the rights as @type="html". Special characters like & will be automatically escaped
     * 
     * @param value The text value of the atom:rights element.
     * @return The newly created atom:rights element
     */
    Text setRightsAsHtml(String value);

    /**
     * Sets the value of the rights as @type="xhtml"
     * 
     * @param value The text value of the atom:rights element
     * @return The newly created atom:rights element
     */
    Text setRightsAsXhtml(String value);

    /**
     * Sets the value of the rights
     * 
     * @param value The text value of the atom:rights element
     * @param type The text type
     * @return The newly create atom:rights element
     */
    Text setRights(String value, Text.Type type);

    /**
     * Sets the value of the right as @type="xhtml"
     * 
     * @param value The XHTML div for the atom:rights element
     * @return The newly created atom:rights element
     */
    Text setRights(Div value);

    /**
     * Return the String value of the atom:rights element
     * 
     * @return The text value of the atom:rights element
     */
    String getRights();

    /**
     * Return the @type of the atom:rights element
     * 
     * @return The Text.Type of the atom:rights element
     */
    Text.Type getRightsType();

    /**
     * <p>
     * Returns the source element for this entry.
     * </p>
     * <p>
     * RFC4287: If an atom:entry is copied from one feed into another feed, then the source atom:feed's metadata (all
     * child elements of atom:feed other than the atom:entry elements) MAY be preserved within the copied entry by
     * adding an atom:source child element, if it is not already present in the entry, and including some or all of the
     * source feed's Metadata elements as the atom:source element's children. Such metadata SHOULD be preserved if the
     * source atom:feed contains any of the child elements atom:author, atom:contributor, atom:rights, or atom:category
     * and those child elements are not present in the source atom:entry.
     * </p>
     * 
     * @return The atom:source element
     */
    Source getSource();

    /**
     * <p>
     * Returns the source element for this entry.
     * </p>
     * <p>
     * RFC4287: If an atom:entry is copied from one feed into another feed, then the source atom:feed's metadata (all
     * child elements of atom:feed other than the atom:entry elements) MAY be preserved within the copied entry by
     * adding an atom:source child element, if it is not already present in the entry, and including some or all of the
     * source feed's Metadata elements as the atom:source element's children. Such metadata SHOULD be preserved if the
     * source atom:feed contains any of the child elements atom:author, atom:contributor, atom:rights, or atom:category
     * and those child elements are not present in the source atom:entry.
     * </p>
     * 
     * @param source The atom:source element
     */
    Entry setSource(Source source);

    /**
     * RFC4287: The "atom:summary" element is a Text construct that conveys a short summary, abstract, or excerpt of an
     * entry... It is not advisable for the atom:summary element to duplicate atom:title or atom:content because Atom
     * Processors might assume there is a useful summary when there is none.
     * 
     * @return The atom:summary element
     */
    Text getSummaryElement();

    /**
     * RFC4287: The "atom:summary" element is a Text construct that conveys a short summary, abstract, or excerpt of an
     * entry... It is not advisable for the atom:summary element to duplicate atom:title or atom:content because Atom
     * Processors might assume there is a useful summary when there is none.
     * 
     * @param text The atom:summary element
     */
    Entry setSummaryElement(Text text);

    /**
     * Sets the value of the summary as @type="text"
     * 
     * @param value The text value of the atom:summary element
     * @return the newly created atom:summary element
     */
    Text setSummary(String value);

    /**
     * Sets the value of the summary as @type="html"
     * 
     * @param value The text value of the atom:summary element
     * @return the newly created atom:summary element
     */
    Text setSummaryAsHtml(String value);

    /**
     * Sets the value of the summary as @type="xhtml"
     * 
     * @param value The text value of the atom:summary element
     * @return the newly created atom:summary element
     */
    Text setSummaryAsXhtml(String value);

    /**
     * Sets the value of the summary
     * 
     * @param value The text value of the atom:summary element
     * @param type The Text.Type of the atom:summary element
     * @return The newly created atom:summary element
     */
    Text setSummary(String value, Text.Type type);

    /**
     * Sets the value of the summary as @type="xhtml"
     * 
     * @param value The XHTML div
     * @return the newly created atom:summary element
     */
    Text setSummary(Div value);

    /**
     * Returns the text string value of this summary
     * 
     * @return the text value of the atom:summary
     */
    String getSummary();

    /**
     * Returns the summary type
     * 
     * @return the Text.Type of the atom:summary
     */
    Text.Type getSummaryType();

    /**
     * RFC4287: The "atom:title" element is a Text construct that conveys a human-readable title for an entry or feed.
     * 
     * @return the atom:title element
     */
    Text getTitleElement();

    /**
     * RFC4287: The "atom:title" element is a Text construct that conveys a human-readable title for an entry or feed.
     * 
     * @param title the atom:title element
     */
    Entry setTitleElement(Text title);

    /**
     * Sets the value of the title as @type="text"
     * 
     * @param value The title value
     * @return The newly created atom:title element
     */
    Text setTitle(String value);

    /**
     * Sets the value of the title as @type="html"
     * 
     * @param value The title value
     * @return The newly created atom:title element
     */
    Text setTitleAsHtml(String value);

    /**
     * Sets the value of the title as @type="xhtml"
     * 
     * @param value The title value
     * @return The newly created atom:title element
     */
    Text setTitleAsXhtml(String value);

    /**
     * Sets the value of the title
     * 
     * @param value The title value
     * @param type The Text.Type of the title
     * @return the newly created atom:title element
     */
    Text setTitle(String value, Text.Type type);

    /**
     * Sets the value of the title as @type="xhtml"
     * 
     * @param value The XHTML div
     * @return the newly created atom:title element
     */
    Text setTitle(Div value);

    /**
     * Returns the text string value of the title element
     * 
     * @return text value of the atom:title
     */
    String getTitle();

    /**
     * Returns the @type of this entries title
     * 
     * @return the Text.Type of the atom:title
     */
    Text.Type getTitleType();

    /**
     * RFC4287: The "atom:updated" element is a Date construct indicating the most recent instant in time when an entry
     * or feed was modified in a way the publisher considers significant. Therefore, not all modifications necessarily
     * result in a changed atom:updated value.
     * 
     * @return the atom:updated element
     */
    DateTime getUpdatedElement();

    /**
     * RFC4287: The "atom:updated" element is a Date construct indicating the most recent instant in time when an entry
     * or feed was modified in a way the publisher considers significant. Therefore, not all modifications necessarily
     * result in a changed atom:updated value.
     * 
     * @param updated the atom:updated element.
     */
    Entry setUpdatedElement(DateTime updated);

    /**
     * Return atom:updated
     * 
     * @return A java.util.Date value
     */
    Date getUpdated();

    /**
     * Set the atom:updated value
     * 
     * @param value The new value
     * @return The newly created atom:updated element
     */
    DateTime setUpdated(Date value);

    /**
     * Set the atom:updated value
     * 
     * @param value The new value
     * @return The newly created atom:updated element
     */
    DateTime setUpdated(String value);

    /**
     * APP Introduces a new app:edited element whose value changes every time the entry is updated
     * 
     * @return the app:edited element
     */
    DateTime getEditedElement();

    /**
     * Set the app:edited element
     * 
     * @param modified The app:edited element
     */
    void setEditedElement(DateTime modified);

    /**
     * Return the value of app:edited
     * 
     * @return app:edited
     */
    Date getEdited();

    /**
     * Set the value of app:edited
     * 
     * @param value The java.util.Date value
     * @return The newly created app:edited element
     */
    DateTime setEdited(Date value);

    /**
     * Set the value of app:edited
     * 
     * @param value the serialized string value for app:edited
     * @return The newly created app:edited element
     */
    DateTime setEdited(String value);

    /**
     * Returns this entries Atom Publishing Protocol control element. A new control element will be created if one
     * currently does not exist
     * 
     * @return The app:control element
     */
    Control getControl(boolean create);

    /**
     * Returns this entries Atom Publishing Protocol control element
     * 
     * @return The app:control element
     */
    Control getControl();

    /**
     * Sets this entries Atom Publishing Protocol control element
     * 
     * @param control The app:contorl element
     */
    Entry setControl(Control control);

    /**
     * Sets whether or not this entry is a draft
     * 
     * @param draft true if this entry should be marked as a draft
     */
    Entry setDraft(boolean draft);

    /**
     * Returns true if this entry is a draft
     * 
     * @return True if this entry is a date
     */
    boolean isDraft();

    /**
     * Returns the first link with the specified rel attribute value
     * 
     * @param rel The link rel
     * @return a Link with the specified rel attribute
     */
    Link getLink(String rel);

    /**
     * Returns this entries first alternate link
     * 
     * @return the Alternate link
     */
    Link getAlternateLink();

    /**
     * Returns the first alternate link matching the specified type and hreflang
     * 
     * @throws MimeTypeParseException
     * @param type The link media type
     * @param hreflang The link target language
     * @return The matching atom:link
     * @throws MimeTypeParseException if the type is malformed
     */
    Link getAlternateLink(String type, String hreflang);

    /**
     * Returns this entries first enclosure link
     * 
     * @return the Enclosure link
     */
    Link getEnclosureLink();

    /**
     * Returns this entries first edit link
     * 
     * @return the Edit Link
     */
    Link getEditLink();

    /**
     * Returns this entries first edit-media link (if any)
     * 
     * @return the Edit Media Link
     */
    Link getEditMediaLink();

    /**
     * Returns the first edit-media link matching the specified type and hreflang
     * 
     * @param type a media type
     * @param hreflang a target language
     * @return A matching atom:link element
     * @throws MimeTypeParseException
     */
    Link getEditMediaLink(String type, String hreflang);

    /**
     * Returns this entries first self link
     * 
     * @return the Self Link
     */
    Link getSelfLink();

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @param rel The rel attribute value
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     */
    IRI getLinkResolvedHref(String rel);

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     */
    IRI getAlternateLinkResolvedHref();

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @param type A target type
     * @param hreflang A target language
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     */
    IRI getAlternateLinkResolvedHref(String type, String hreflang);

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     */
    IRI getEnclosureLinkResolvedHref();

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     */
    IRI getEditLinkResolvedHref();

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     */
    IRI getEditMediaLinkResolvedHref();

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @param type A target type
     * @param hreflang A target language
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     * @throws MimeTypeParseException if the type is malformed
     */
    IRI getEditMediaLinkResolvedHref(String type, String hreflang);

    /**
     * Return a link href resolved against the in-scope Base URI
     * 
     * @return The resolved IRI
     * @throws IRISyntaxException if the href attribute is malformed
     */
    IRI getSelfLinkResolvedHref();

    Control addControl();
}
