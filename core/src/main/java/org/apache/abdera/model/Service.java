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

/**
 * <p>
 * Represents the root of an Atom Publishing Protocol Introspection Document.
 * </p>
 * <p>
 * Per APP Draft-08:
 * </p>
 * 
 * <pre>
 *  The root of an introspection document is the "app:service" element.
 * 
 *  The "app:service" element is the container for introspection
 *  information associated with one or more workspaces.  An app:service
 *  element MUST contain one or more app:workspace elements.
 *  
 *  appService =
 *     element app:service {
 *        appCommonAttributes,
 *       ( appWorkspace+
 *          &amp; extensionElement* )
 *     }
 * </pre>
 */
public interface Service extends ExtensibleElement {

    /**
     * Return the complete set of workspaces
     * 
     * @return A listing of app:workspaces elements
     */
    List<Workspace> getWorkspaces();

    /**
     * Return the named workspace
     * 
     * @param title The workspace title
     * @return A matching app:workspace
     */
    Workspace getWorkspace(String title);

    /**
     * Add an individual workspace
     * 
     * @param workspace a app:workspace element
     */
    Service addWorkspace(Workspace workspace);

    /**
     * Add an individual workspace
     * 
     * @param title The workspace title
     * @return The newly created app:workspace
     */
    Workspace addWorkspace(String title);

    /**
     * Returns the named collection
     * 
     * @param workspace The workspace title
     * @param collection The collection title
     * @return A matching app:collection element
     */
    Collection getCollection(String workspace, String collection);

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
