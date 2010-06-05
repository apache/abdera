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

import org.apache.abdera.i18n.iri.IRI;

/**
 * The Atom Publishing Protocol introduces the notion of a "Category Document" and the app:categories element. These are
 * used to provide a listing of atom:category elements that may be used with the members of an Atom Publishing Protocol
 * collection.
 */
public interface Categories extends ExtensibleElement {

    /**
     * When contained within an app:collection element, the app:categories element can have an href attribute whose
     * value MUST point to an Atompub Categories Document.
     * 
     * @return The href attribute value
     */
    IRI getHref();

    /**
     * Returns the value of the href attribute resolved against the in-scope Base URI
     * 
     * @return The fully resolved href attribute value
     */
    IRI getResolvedHref();

    /**
     * Sets the value of the href attribute.
     * 
     * @param href The location of an Atompub Categories Document
     */
    Categories setHref(String href);

    /**
     * If an app:categories element is marked as fixed, then the set of atom:category elements is considered to be a
     * closed set. That is, Atom Publishing Protocol clients SHOULD only use the atom:category elements listed. The
     * default is false (fixed="no")
     * 
     * @return True if the categories listing is fixed
     */
    boolean isFixed();

    /**
     * Sets whether or not this is a fixed listing of categories. If set to false, the fixed attribute will be removed
     * from the app:categories element.
     * 
     * @param fixed True if the app:categories listing is fixed
     */
    Categories setFixed(boolean fixed);

    /**
     * The app:categories element may specify a default scheme attribute for listed atom:category elements that do not
     * have their own scheme attribute.
     * 
     * @return The scheme IRI
     */
    IRI getScheme();

    /**
     * Sets the default scheme for this listing of categories
     * 
     * @param scheme The default scheme used for this listing of categories
     */
    Categories setScheme(String scheme);

    /**
     * Lists the complete set of categories
     * 
     * @return This app:categories listing of atom:category elements
     */
    List<Category> getCategories();

    /**
     * Lists the complete set of categories that use the specified scheme
     * 
     * @param scheme The IRI of an atom:category scheme
     * @return A listing of atom:category elements that use the specified scheme
     */
    List<Category> getCategories(String scheme);

    /**
     * Returns a copy of the complete set of categories with the scheme attribute set
     * 
     * @return A listing of atom:category elements using the default scheme specified by the app:categories scheme
     *         attribute
     */
    List<Category> getCategoriesWithScheme();

    /**
     * Returns a copy of the complete set of categories with the scheme attribute set as specified in 7.2.1. (child
     * categories that do not have a scheme attribute inherit the scheme attribute of the parent)
     * 
     * @param scheme A scheme IRI
     * @return A listing of atom:category elements
     */
    List<Category> getCategoriesWithScheme(String scheme);

    /**
     * Add an atom:category to the listing
     * 
     * @param category The atom:category to add to the listing
     */
    Categories addCategory(Category category);

    /**
     * Create and add an atom:category to the listing
     * 
     * @param term The string term
     * @return The newly created atom:category
     */
    Category addCategory(String term);

    /**
     * Create an add an atom:category to the listing
     * 
     * @param scheme The scheme IRI for the newly created category
     * @param term The string term
     * @param label The human readable label for the category
     * @return The newly created atom:category
     */
    Category addCategory(String scheme, String term, String label);

    /**
     * Returns true if this app:categories listing contains a category with the specified term
     * 
     * @param term The term to look for
     * @return True if the term is found
     */
    boolean contains(String term);

    /**
     * Returns true if this app:categories listing contains a category with the specified term and scheme
     * 
     * @param term The term to look for
     * @param scheme The IRI scheme
     * @return True if the term and scheme are found
     */
    boolean contains(String term, String scheme);

    /**
     * Returns true if the href attribute is set
     */
    boolean isOutOfLine();

}
