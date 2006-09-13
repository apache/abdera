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

import javax.activation.MimeTypeParseException;

/**
 * <p>Per RFC4287:</p>
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
public interface Source 
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
   * Adds a category to the feed
   */
  Category addCategory(String term);

  /**
   * Adds a category to the feed
   * @throws URISyntaxException 
   */
  Category addCategory(String scheme, String term, String label) throws URISyntaxException;
  
  /**
   * Lists the complete set of contributors for this entry
   */
  List<Person> getContributors();
  
  /**
   * Adds an individual contributor to this entry
   */
  void addContributor(Person person);
  
  /**
   * Adds an author
   */
  Person addContributor(String name);

  /**
   * Adds a contributor
   * @throws URISyntaxException 
   */
  Person addContributor(String name, String email, String uri) throws URISyntaxException;
  
  /**
   * RFC4287: The "atom:generator" element's content identifies the 
   * agent used to generate a feed, for debugging and other purposes.
   */
  Generator getGenerator();

  /**
   * RFC4287: The "atom:generator" element's content identifies the 
   * agent used to generate a feed, for debugging and other purposes.
   */
  void setGenerator(Generator generator);

  /**
   * RFC4287: The "atom:generator" element's content identifies the 
   * agent used to generate a feed, for debugging and other purposes.
   */
  Generator setGenerator(
    String uri, 
    String version, 
    String value) 
      throws URISyntaxException;
  
  /** 
   * RFC4287: The "atom:icon" element's content is an IRI reference 
   * [RFC3987] that identifies an image that provides iconic visual
   *  identification for a feed... The image SHOULD have an aspect ratio 
   *  of one (horizontal) to one (vertical) and SHOULD be suitable for 
   *  presentation at a small size.
   */
  IRI getIconElement();
  
  /** 
   * RFC4287: The "atom:icon" element's content is an IRI reference 
   * [RFC3987] that identifies an image that provides iconic visual
   *  identification for a feed... The image SHOULD have an aspect ratio 
   *  of one (horizontal) to one (vertical) and SHOULD be suitable for 
   *  presentation at a small size.
   */
  void setIconElement(IRI iri);
  
  /** 
   * RFC4287: The "atom:icon" element's content is an IRI reference 
   * [RFC3987] that identifies an image that provides iconic visual
   *  identification for a feed... The image SHOULD have an aspect ratio 
   *  of one (horizontal) to one (vertical) and SHOULD be suitable for 
   *  presentation at a small size.
   * @throws URISyntaxException 
   */
  IRI setIcon(String iri) throws URISyntaxException;

  /** 
   * RFC4287: The "atom:icon" element's content is an IRI reference 
   * [RFC3987] that identifies an image that provides iconic visual
   *  identification for a feed... The image SHOULD have an aspect ratio 
   *  of one (horizontal) to one (vertical) and SHOULD be suitable for 
   *  presentation at a small size.
   * @throws URISyntaxException 
   */
  URI getIcon() throws URISyntaxException;
  
  /**
   * RFC4287: The "atom:id" element conveys a permanent, universally unique
   * identifier for an entry or feed.
   */
  IRI getIdElement();
  
  /**
   * RFC4287: The "atom:id" element conveys a permanent, universally unique
   * identifier for an entry or feed.
   */
  void setIdElement(IRI id);
  
  /**
   * Returns the universally unique identifier for this feed
   * @throws URISyntaxException 
   */
  URI getId() throws URISyntaxException;

  /**
   * Sets the universally unique identifier for this feed
   * @throws URISyntaxException 
   */
  IRI setId(String id) throws URISyntaxException;
  
  /**
   * Sets the universally unique identifier for this feed
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
   * Adds an individual link to the entry
   */
  void addLink(Link link);
  
  Link addLink(String href) throws URISyntaxException;
  
  Link addLink(String href, String rel) throws URISyntaxException;
  
  Link addLink(String href, String rel, String type, String title, String hreflang, long length) throws URISyntaxException, MimeTypeParseException;
   
  /**
   * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] 
   * that identifies an image that provides visual identification for a feed.
   * The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
   */
  IRI getLogoElement();
  
  /**
   * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] 
   * that identifies an image that provides visual identification for a feed.
   * The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
   */
  void setLogoElement(IRI iri);
  
  /**
   * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] 
   * that identifies an image that provides visual identification for a feed.
   * The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
   * @throws URISyntaxException 
   */
  IRI setLogo(String iri) throws URISyntaxException;

  /**
   * RFC4287: The "atom:logo" element's content is an IRI reference [RFC3987] 
   * that identifies an image that provides visual identification for a feed.
   * The image SHOULD have an aspect ratio of 2 (horizontal) to 1 (vertical).
   * @throws URISyntaxException 
   */
  URI getLogo() throws URISyntaxException;
  
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
   * Sets the value of the rights as @type="xhtml"
   */
  Text setRights(Div value);
  
  /**
   * Returns the text of atom:rights
   */
  String getRights();
  
  /**
   * Returns the type of atom:rights
   */
  Text.Type getRightsType();
  
  /**
   * RFC4287: The "atom:subtitle" element is a Text construct that 
   * conveys a human-readable description or subtitle for a feed.
   */
  Text getSubtitleElement();

  /**
   * RFC4287: The "atom:subtitle" element is a Text construct that 
   * conveys a human-readable description or subtitle for a feed.
   */  
  void setSubtitleElement(Text text);
  
  /**
   * Sets the value of the subtitle as @type="text"
   */
  Text setSubtitle(String value);

  /**
   * Sets the value of the subtitle as @type="html"
   */
  Text setSubtitleAsHtml(String value);

  /**
   * Sets the value of the subtitle as @type="xhtml"
   */
  Text setSubtitleAsXhtml(String value);
  
  /**
   * Sets the value of the subtitle
   */
  Text setSubtitle(String value, Text.Type type);
  
  /**
   * Sets the value of the subtitle as @type="xhtml"
   */
  Text setSubtitle(Div value);
  
  /**
   * Returns the text value of atom:subtitle
   */
  String getSubtitle();
  
  /**
   * Returns the atom:subtitle type
   */
  Text.Type getSubtitleType();

  /**
   * RFC4287: The "atom:title" element is a Text construct that conveys a 
   * human-readable title for an entry or feed.
   */
  Text getTitleElement();
  
  /**
   * RFC4287: The "atom:title" element is a Text construct that conveys a 
   * human-readable title for an entry or feed.
   */
  void setTitleElement(Text text);
  
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
   * Returns the text of atom:title
   */
  String getTitle();

  /**
   * Returns the type of atom:title
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
  void setUpdatedElement(DateTime dateTime);
  
  String getUpdatedString();
  
  Date getUpdated();
  
  DateTime setUpdated(Date value);
  
  DateTime setUpdated(String value);
  
  /**
   * Returns the first link with the specified rel attribute value
   */
  Link getLink(String rel);
  
  /**
   * Returns the first link using the rel attribute value "self"
   */
  Link getSelfLink();
  
  /**
   * Returns this entries first alternate link
   */
  Link getAlternateLink();
  
  URI getLinkResolvedHref(String rel) throws URISyntaxException;
  URI getSelfLinkResolvedHref() throws URISyntaxException;
  URI getAlternateLinkResolvedHref() throws URISyntaxException;
  
  Collection getCollection();
  
  void setCollection(Collection collection);
  
}
