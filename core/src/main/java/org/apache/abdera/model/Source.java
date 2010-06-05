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

import java.util.Date;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;

/**
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 *  If an atom:entry is copied from one feed into another feed, then the
 *  source atom:feed's metadata (all child elements of atom:feed other
 *  than the atom:entry elements) MAY be preserved within the copied
 *  entry by adding an atom:source child element, if it is not already
 *  present in the entry, and including some or all of the source feed's
 *  Metadata elements as the atom:source element's children.  Such
 *  metadata SHOULD be preserved if the source atom:feed contains any of
 *  the child elements atom:author, atom:contributor, atom:rights, or
 *  atom:category and those child elements are not present in the source
 *  atom:entry.
 * 
 *  atomSource =
 *     element atom:source {
 *        atomCommonAttributes,
 *        (atomAuthor*
 *         &amp; atomCategory*
 *         &amp; atomContributor*
 *         &amp; atomGenerator?
 *         &amp; atomIcon?
 *         &amp; atomId?
 *         &amp; atomLink*
 *         &amp; atomLogo?
 *         &amp; atomRights?
 *         &amp; atomSubtitle?
 *         &amp; atomTitle?
 *         &amp; atomUpdated?
 *         &amp; extensionElement*)
 *     }
 * 
 *  The atom:source element is designed to allow the aggregation of
 *  entries from different feeds while retaining information about an
 *  entry's source feed.  For this reason, Atom Processors that are
 *  performing such aggregation SHOULD include at least the required
 *  feed-level Metadata elements (atom:id, atom:title, and atom:updated)
 *  in the atom:source element.
 * </pre>
 */
public interface Source extends ExtensibleElement {
    /**
     * Returns the first author listed for the entry
     * 
     * @return This feed's author
     */
    Person getAuthor();

    /**
     * Returns the complete set of authors listed for the entry
     * 
     * @return This feeds list of authors
     */
    List<Person> getAuthors();

    /**
     * Adds an individual author to the entry
     * 
     * @param person an atom:author element
     */
    <T extends Source> T addAuthor(Person person);

    /**
     * Adds an author
     * 
     * @param name The author name
     * @return The newly created atom:author element
     */
    Person addAuthor(String name);

    /**
     * Adds an author
     * 
     * @param name The author name
     * @param email The author email
     * @param iri The author iri
     * @return The newly created atom:author element
     * @throws IRISyntaxException if the iri is malformed
     */
    Person addAuthor(String name, String email, String iri);

    /**
     * Lists the complete set of categories listed for the entry
     * 
     * @return A listing of app:category elements
     */
    List<Category> getCategories();

    /**
     * Lists the complete set of categories using the specified scheme
     * 
     * @param scheme A Scheme IRI
     * @return The listing of app:category elements
     * @throws IRISyntaxException if the scheme is malformed
     */
    List<Category> getCategories(String scheme);

    /**
     * Adds an individual category to the entry
     * 
     * @param category A atom:category element
     */
    <T extends Source> T addCategory(Category category);

    /**
     * Adds a category to the feed
     * 
     * @param term A category term
     * @return The newly created atom:category element
     */
    Category addCategory(String term);

    /**
     * Adds a category to the feed
     * 
     * @param scheme A category scheme
     * @param term A category term
     * @param label The human readable label
     * @return the newly created atom:category element
     * @throws IRISyntaxException if the scheme is malformed
     */
    Category addCategory(String scheme, String term, String label);

    /**
     * Lists the complete set of contributors for this entry
     * 
     * @return A listing of atom:contributor elements
     */
    List<Person> getContributors();

    /**
     * Adds an individual contributor to this entry
     * 
     * @param person a atom:contributor element
     */
    <T extends Source> T addContributor(Person person);

    /**
     * Adds a contributor
     * 
     * @param name The name of a contributor
     * @return The newly created atom:contributor element
     */
    Person addContributor(String name);

    /**
     * Adds a contributor
     * 
     * @param name The contributor name
     * @param email The contributor email
     * @param iri The contributor uri
     * @return The atom:contributor element
     * @throws IRISyntaxException if the iri is malformed
     */
    Person addContributor(String name, String email, String iri);

    /**
     * RFC4287: The "atom:generator" element's content identifies the agent used to generate a feed, for debugging and
     * other purposes.
     * 
     * @return The atom:generator
     */
    Generator getGenerator();

    /**
     * RFC4287: The "atom:generator" element's content identifies the agent used to generate a feed, for debugging and
     * other purposes.
     * 
     * @param generator A atom:generator element
     */
    <T extends Source> T setGenerator(Generator generator);

    /**
     * RFC4287: The "atom:generator" element's content identifies the agent used to generate a feed, for debugging and
     * other purposes.
     * 
     * @param iri The iri attribute
     * @param version The version attribute
     * @param value The value attribute
     * @return A newly created atom:generator element
     * @throws IRISyntaxException if the iri is malformed
     */
    Generator setGenerator(String iri, String version, String value);

    /**
     * RFC4287: The "atom:icon" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * iconic visual identification for a feed... The image SHOULD have an aspect ratio of one (horizontal) to one
     * (vertical) and SHOULD be suitable for presentation at a small size.
     * 
     * @return the atom:icon element
     */
    IRIElement getIconElement();

    /**
     * RFC4287: The "atom:icon" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * iconic visual identification for a feed... The image SHOULD have an aspect ratio of one (horizontal) to one
     * (vertical) and SHOULD be suitable for presentation at a small size.
     * 
     * @param iri The atom:icon element
     */
    <T extends Source> T setIconElement(IRIElement iri);

    /**
     * RFC4287: The "atom:icon" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * iconic visual identification for a feed... The image SHOULD have an aspect ratio of one (horizontal) to one
     * (vertical) and SHOULD be suitable for presentation at a small size.
     * 
     * @param iri The atom:icon IRI value
     * @throws IRISyntaxException if the iri is malformed
     */
    IRIElement setIcon(String iri);

    /**
     * RFC4287: The "atom:icon" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * iconic visual identification for a feed... The image SHOULD have an aspect ratio of one (horizontal) to one
     * (vertical) and SHOULD be suitable for presentation at a small size.
     * 
     * @return The atom:icon value
     * @throws IRISyntaxException if the atom:icon value is malformed
     */
    IRI getIcon();

    /**
     * RFC4287: The "atom:id" element conveys a permanent, universally unique identifier for an entry or feed.
     * 
     * @return The atom:id element
     */
    IRIElement getIdElement();

    /**
     * RFC4287: The "atom:id" element conveys a permanent, universally unique identifier for an entry or feed.
     * 
     * @param id A atom:id element
     */
    <T extends Source> T setIdElement(IRIElement id);

    /**
     * Returns the universally unique identifier for this feed
     * 
     * @return The atom:id value
     * @throws IRISyntaxException if the atom:id is malformed
     */
    IRI getId();

    /**
     * Sets the universally unique identifier for this feed
     * 
     * @param id The atom:id value
     * @return The newly created atom:id element
     * @throws IRISyntaxException if the id is malformed
     */
    IRIElement setId(String id);

    /**
     * Creates a new randomized atom:id for the entry
     */
    IRIElement newId();

    /**
     * Sets the universally unique identifier for this feed
     * 
     * @param id The atom:id value
     * @param normalize True if the atom:id value should be normalized
     * @return The newly created atom:id element
     * @throws IRISyntaxException if the id is malformed
     */
    IRIElement setId(String id, boolean normalize);

    /**
     * Lists the complete set of links for this entry
     * 
     * @return returns a listing of atom:link elements
     */
    List<Link> getLinks();

    /**
     * Lists the complete set of links using the specified rel attribute value
     * 
     * @param rel A link relation
     * @return A listing of atom:link elements
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
     * @param link A atom:link element
     */
    <T extends Source> T addLink(Link link);

    /**
     * Adds an individual link element
     * 
     * @param href The href IRI of the link
     * @return The newly created atom:link
     * @throws IRISyntaxException if the href is malformed
     */
    Link addLink(String href);

    /**
     * Adds an individual link element
     * 
     * @param href The href IRI of the link
     * @param rel The link rel attribute
     * @return The newly created atom:link
     * @throws IRISyntaxException if the href is malformed
     */
    Link addLink(String href, String rel);

    /**
     * Adds an individual link element
     * 
     * @param href The href IRI of the link
     * @param rel The link rel attribute
     * @param type The link type attribute
     * @param hreflang The link hreflang attribute
     * @param length The length attribute
     * @return The newly created atom:link
     * @throws IRISyntaxException if the href is malformed
     */
    Link addLink(String href, String rel, String type, String title, String hreflang, long length);

    /**
     * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * visual identification for a feed. The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
     * 
     * @return the atom:logo element
     */
    IRIElement getLogoElement();

    /**
     * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * visual identification for a feed. The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
     * 
     * @param iri The atom:logo element
     */
    <T extends Source> T setLogoElement(IRIElement iri);

    /**
     * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * visual identification for a feed. The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
     * 
     * @param iri The atom:logo value
     * @return The newly created atom:logo element
     * @throws IRISyntaxException if the iri is malformed
     */
    IRIElement setLogo(String iri);

    /**
     * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] that identifies an image that provides
     * visual identification for a feed. The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
     * 
     * @return The atom:logo element value
     * @throws IRISyntaxException if the atom:logo value is malformed
     */
    IRI getLogo();

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
    <T extends Source> T setRightsElement(Text text);

    /**
     * Sets the value of the rights as @type="text"
     * 
     * @param value The atom:rights text value
     * @return The newly created atom:rights element
     */
    Text setRights(String value);

    /**
     * Sets the value of the rights as @type="html"
     * 
     * @param value The atom:rights text value
     * @return The newly created atom:rights element
     */
    Text setRightsAsHtml(String value);

    /**
     * Sets the value of the rights as @type="xhtml"
     * 
     * @param value The atom:rights text value
     * @return The newly created atom:rights element
     */
    Text setRightsAsXhtml(String value);

    /**
     * Sets the value of the rights
     * 
     * @param value The atom:rights text value
     * @param type The atom:rights text type
     * @return The newly created atom:rights element
     */
    Text setRights(String value, Text.Type type);

    /**
     * Sets the value of the rights as @type="xhtml"
     * 
     * @param value The XHTML div element
     * @return The newly created atom:rights element
     */
    Text setRights(Div value);

    /**
     * Returns the text of atom:rights
     * 
     * @return The value of the atom:rights element
     */
    String getRights();

    /**
     * Returns the type of atom:rights
     * 
     * @return The Text.Type of the atom:rights element
     */
    Text.Type getRightsType();

    /**
     * RFC4287: The "atom:subtitle" element is a Text construct that conveys a human-readable description or subtitle
     * for a feed.
     * 
     * @return The atom:subtitle element
     */
    Text getSubtitleElement();

    /**
     * RFC4287: The "atom:subtitle" element is a Text construct that conveys a human-readable description or subtitle
     * for a feed.
     * 
     * @param text A atom:subtitle element
     */
    <T extends Source> T setSubtitleElement(Text text);

    /**
     * Sets the value of the subtitle as @type="text"
     * 
     * @param value the value of the atom:subtitle element
     * @return The atom:subtitle element
     */
    Text setSubtitle(String value);

    /**
     * Sets the value of the subtitle as @type="html"
     * 
     * @param value The value of the atom:subtitle element
     * @return The newly created atom:subtitle element
     */
    Text setSubtitleAsHtml(String value);

    /**
     * Sets the value of the subtitle as @type="xhtml"
     * 
     * @param value The value of the atom:subtitle element
     * @return The newly created atom:subtitle element
     */
    Text setSubtitleAsXhtml(String value);

    /**
     * Sets the value of the subtitle
     * 
     * @param value The value of the atom:subtitle element
     * @param type The atom:subtitle Text.Type
     * @return The newly created atom:subtitle element
     */
    Text setSubtitle(String value, Text.Type type);

    /**
     * Sets the value of the subtitle as @type="xhtml"
     * 
     * @param value The atom:subtitle element
     * @return The newly created atom:subtitle element
     */
    Text setSubtitle(Div value);

    /**
     * Returns the text value of atom:subtitle
     * 
     * @return The atom:subtitle text value
     */
    String getSubtitle();

    /**
     * Returns the atom:subtitle type
     * 
     * @return The atom:subtitle Text.Type
     */
    Text.Type getSubtitleType();

    /**
     * RFC4287: The "atom:title" element is a Text construct that conveys a human-readable title for an entry or feed.
     * 
     * @return The atom:title element
     */
    Text getTitleElement();

    /**
     * RFC4287: The "atom:title" element is a Text construct that conveys a human-readable title for an entry or feed.
     * 
     * @param text The atom:title element
     */
    <T extends Source> T setTitleElement(Text text);

    /**
     * Sets the value of the title as @type="text"
     * 
     * @param value The atom:title value
     * @return The newly created atom:title element
     */
    Text setTitle(String value);

    /**
     * Sets the value of the title as @type="html"
     * 
     * @param value The atom:title value
     * @return The newly created atom:title element
     */
    Text setTitleAsHtml(String value);

    /**
     * Sets the value of the title as @type="xhtml"
     * 
     * @param value The atom:title value
     * @return The newly created atom:title element
     */
    Text setTitleAsXhtml(String value);

    /**
     * Sets the value of the title
     * 
     * @param value The atom:title value
     * @param type The atom:title Text.Type
     * @return The newly created atom:title element
     */
    Text setTitle(String value, Text.Type type);

    /**
     * Sets the value of the title as @type="xhtml"
     * 
     * @param value The XHTML div
     * @return The newly created atom:title element
     */
    Text setTitle(Div value);

    /**
     * Returns the text of atom:title
     * 
     * @return The text value of the atom:title element
     */
    String getTitle();

    /**
     * Returns the type of atom:title
     * 
     * @return The atom:title Text.Type value
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
     * @param dateTime A atom:updated element
     */
    <T extends Source> T setUpdatedElement(DateTime dateTime);

    /**
     * Return the atom:updated value
     * 
     * @return The serialized string form value of atom:updated
     */
    String getUpdatedString();

    /**
     * Return the atom:updated value
     * 
     * @return The atom:updated as a java.util.Date
     */
    Date getUpdated();

    /**
     * Set the atom:updated value
     * 
     * @param value The java.util.Date
     * @return The newly created atom:updated element
     */
    DateTime setUpdated(Date value);

    /**
     * Set the atom:updated value
     * 
     * @param value The serialized string date
     * @return The newly created atom:updated element
     */
    DateTime setUpdated(String value);

    /**
     * Returns the first link with the specified rel attribute value
     * 
     * @param rel A link relation
     * @return The newly created atom:link element
     */
    Link getLink(String rel);

    /**
     * Returns the first link using the rel attribute value "self"
     * 
     * @return An atom:link
     */
    Link getSelfLink();

    /**
     * Returns this entries first alternate link
     * 
     * @return An atom:link
     */
    Link getAlternateLink();

    /**
     * @param type A media type
     * @param hreflang A target language
     * @return A matching atom:link
     * @throws MimeTypeParseException if the type if malformed
     */
    Link getAlternateLink(String type, String hreflang);

    /**
     * @param rel A link relation
     * @return An atom:link
     */
    IRI getLinkResolvedHref(String rel);

    /**
     * @return An atom:link
     */
    IRI getSelfLinkResolvedHref();

    /**
     * @return An atom:link
     */
    IRI getAlternateLinkResolvedHref();

    /**
     * @param type A media type
     * @param hreflang A target language
     * @return A matching atom:link
     * @throws MimeTypeParseException if the type if malformed
     */
    IRI getAlternateLinkResolvedHref(String type, String hreflang);

    /**
     * Return an app:collection element associatd with this atom:source. The Atom Publishing Protocol allows an
     * app:collection to be contained by atom:feed to specify the collection to which the feed is associated.
     * 
     * @return An app:collection element
     */
    Collection getCollection();

    /**
     * Set the app:collection element
     * 
     * @param collection An app:collection element
     */
    <T extends Source> T setCollection(Collection collection);

    /**
     * Convert the Source element into an empty Feed element
     */
    Feed getAsFeed();
}
