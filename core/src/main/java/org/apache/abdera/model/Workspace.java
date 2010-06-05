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

import org.apache.abdera.i18n.iri.IRISyntaxException;

/**
 * <p>
 * An Atom Publishing Protocol Introspection Document workspace element.
 * </p>
 * <p>
 * Per APP Draft-08
 * </p>
 * 
 * <pre>
 *  The "app:workspace" element contains information elements about the
 *  collections of resources available for editing.  The app:workspace
 *  element MUST contain one or more app:collection elements.
 * 
 *  appWorkspace =
 *     element app:workspace {
 *        appCommonAttributes,
 *        ( atomTitle
 *          &amp; appCollection*
 *          &amp; extensionElement* )
 *     }
 * 
 * </pre>
 */
public interface Workspace extends ExtensibleElement {

    /**
     * Return the workspace title
     * 
     * @return The atom:title value
     */
    String getTitle();

    /**
     * Set the workspace title
     * 
     * @param title The atom:title value
     * @return The newly created atom:title
     */
    Text setTitle(String title);

    /**
     * Set the workspace title as escaped HTML
     * 
     * @param title The atom:title value
     * @return The newly created atom:title
     */
    Text setTitleAsHtml(String title);

    /**
     * Set the workspace title as XHTML
     * 
     * @param title The atom:title value
     * @return the newly created atom:title
     */
    Text setTitleAsXHtml(String title);

    /**
     * Return the atom:title
     * 
     * @return The atom:title element
     */
    Text getTitleElement();

    /**
     * Returns the full set of collections in this workspace
     * 
     * @return A listing of app:collection elements
     */
    List<Collection> getCollections();

    /**
     * Returns the named collection
     * 
     * @param title A collection title
     * @return A matching app:collection
     */
    Collection getCollection(String title);

    /**
     * Adds an individual collection to this workspace
     * 
     * @param collection The collection to add
     */
    Workspace addCollection(Collection collection);

    /**
     * Adds an individual collection to this workspace
     * 
     * @param title The collection title
     * @param href The collection HREF
     * @return The newly created app:collection
     * @throws IRISyntaxException if the href is malformed
     */
    Collection addCollection(String title, String href);

    /**
     * Adds a multipart collection to this workspace
     * 
     * @param title The collection title
     * @param href The collection HREF
     * @return The newly created app:collection
     * @throws IRISyntaxException if the href is malformed
     */
    public Collection addMultipartCollection(String title, String href);

    /**
     * Returns a collection that accepts the specified media types
     * 
     * @param a listing of media types the collection must accept
     * @return A matching app:collection element
     */
    Collection getCollectionThatAccepts(MimeType... type);

    /**
     * Returns a collection that accepts the specified media types
     * 
     * @param a listing of media types the collection must accept
     * @return A matching app:collection element
     */
    Collection getCollectionThatAccepts(String... type);

    /**
     * Returns collections that accept the specified media types
     * 
     * @param a listing of media types the collection must accept
     * @return A listing matching app:collection elements
     */
    List<Collection> getCollectionsThatAccept(MimeType... type);

    /**
     * Returns collections that accept the specified media types
     * 
     * @param a listing of media types the collection must accept
     * @return A listing of matching app:collection elements
     */
    List<Collection> getCollectionsThatAccept(String... type);

}
