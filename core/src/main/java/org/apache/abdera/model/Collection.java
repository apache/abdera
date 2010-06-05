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

import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;

/**
 * <p>
 * Represents an collection element in an Atom Publishing Protocol introspection document.
 * </p>
 * 
 * <pre>
 *  The "app:collection" describes an Atom Protocol collection.  One
 *  child element is defined here for app:collection: "app:member-type".
 * 
 *  appCollection =
 *     element app:collection {
 *        appCommonAttributes,
 *        attribute href { text },
 *        ( atomTitle
 *          &amp; appAccept
 *          &amp; extensionElement* )
 *     }
 * </pre>
 */
public interface Collection extends ExtensibleElement {

    /**
     * The text value of the collections atom:title element
     * 
     * @return The atom:title value
     */
    String getTitle();

    /**
     * Set the value of the collections atom:title element using type="text"
     * 
     * @param title The value of the atom:title
     * @return The newly created title element
     */
    Text setTitle(String title);

    /**
     * Set the value of the collections atom:title element using type="html". Special characters in the value will be
     * automatically escaped (e.g. & will become &amp;
     * 
     * @param title The value of the atom:title
     * @return The newly created title element
     */
    Text setTitleAsHtml(String title);

    /**
     * Set the value of the collections atom:title element using type="xhtml". The title text will be wrapped in a
     * xhtml:div and parsed to ensure that it is welformed XML. A ParseException (RuntimeException) could be thrown
     * 
     * @param title The value of the atom:title
     * @return The newly created title element
     */
    Text setTitleAsXHtml(String title);

    /**
     * Return the title element
     * 
     * @return The title element
     */
    Text getTitleElement();

    /**
     * Return the value of the app:collection elements href attribute
     * 
     * @return The href attribute IRI value
     * @throws IRISyntaxException if the value of the href attribute is malformed
     */
    IRI getHref();

    /**
     * Return the href attribute resolved against the in-scope Base URI
     * 
     * @return The href attribute IRI value
     * @throws IRISyntaxException if the value of the href attribute is malformed
     */
    IRI getResolvedHref();

    /**
     * Set the value of the href attribute
     * 
     * @param href The value of href attribute
     * @throws IRISyntaxException if the href attribute is malformed
     */
    Collection setHref(String href);

    /**
     * Returns the listing of media-ranges allowed for this collection
     * 
     * @return An array listing the media-ranges allowed for this collection
     */
    String[] getAccept();

    /**
     * Set the listing of media-ranges allowed for this collection. The special value "entry" is used to indicate Atom
     * Entry Documents.
     * 
     * @param mediaRanges a listing of media-ranges
     * @throws MimeTypeParseException
     */
    Collection setAccept(String... mediaRanges);

    /**
     * Returns true if the collection accepts the given media-type
     * 
     * @param mediaType The media-type to check
     * @return True if the media-type is acceptable
     */
    boolean accepts(String mediaType);

    /**
     * Returns true if the collection accepts Atom entry documents (equivalent to calling
     * accepts("application/atom+xml;type=entry");)
     */
    boolean acceptsEntry();

    /**
     * Returns true if the collection accepts nothing (i.e. there is an empty accept element)
     */
    boolean acceptsNothing();

    /**
     * Sets the appropriate accept element to indicate that entries are accepted (equivalent to calling
     * setAccept("application/atom+xml;type=entry");)
     */
    Collection setAcceptsEntry();

    /**
     * Sets the collection so that nothing is accepted (equivalent to calling setAccept(""); )
     */
    Collection setAcceptsNothing();

    /**
     * Adds a new accept element to the collection
     */
    Collection addAccepts(String mediaRange);

    /**
     * Adds new accept elements to the collection
     */
    Collection addAccepts(String... mediaRanges);

    /**
     * Same as setAcceptsEntry except the existing accepts are not discarded
     */
    Collection addAcceptsEntry();

    /**
     * Returns true if the collection accepts the given media-type
     * 
     * @param mediaType The media-type to check
     * @return True if the media-type is acceptable
     */
    boolean accepts(MimeType mediaType);

    /**
     * Returns the app:categories element
     * 
     * @return The app:categories element
     */
    List<Categories> getCategories();

    /**
     * Add an app:categories element
     * 
     * @return The newly created app:categories element
     */
    Categories addCategories();

    /**
     * Add an app:categories element that links to an external Category Document
     * 
     * @param href The IRI of the external Category Document
     * @return The newly created app:categories element
     */
    Categories addCategories(String href);

    /**
     * Add the app:categories element to the collection
     * 
     * @param categories The app:categories element
     */
    Collection addCategories(Categories categories);

    /**
     * Add a listing of categories to the collection
     * 
     * @param categories The listing of categories to add
     * @param fixed True if the listing of categories should be fixed
     * @param scheme The default IRI scheme for the categories listing
     * @return The newly created app:categories element
     */
    Categories addCategories(List<Category> categories, boolean fixed, String scheme);

}
