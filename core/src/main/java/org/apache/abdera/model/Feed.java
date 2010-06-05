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

import java.util.Comparator;
import java.util.List;

import org.apache.abdera.i18n.iri.IRISyntaxException;

/**
 * <p>
 * Represents an Atom Feed Element
 * </p>
 * <p>
 * Per RFC4287:
 * </p>
 * 
 * <pre>
 * The "atom:feed" element is the document (i.e., top-level) element of
 * an Atom Feed Document, acting as a container for metadata and data
 * associated with the feed.  Its element children consist of metadata
 * elements followed by zero or more atom:entry child elements.
 * 
 * atomFeed =
 *   element atom:feed {
 *       atomCommonAttributes,
 *       (atomAuthor*
 *        &amp; atomCategory*
 *        &amp; atomContributor*
 *        &amp; atomGenerator?
 *        &amp; atomIcon?
 *        &amp; atomId
 *        &amp; atomLink*
 *        &amp; atomLogo?
 *        &amp; atomRights?
 *        &amp; atomSubtitle?
 *        &amp; atomTitle
 *        &amp; atomUpdated
 *        &amp; extensionElement*),
 *       atomEntry*
 *   }
 * 
 * This specification assigns no significance to the order of atom:entry
 * elements within the feed.
 * 
 * The following child elements are defined by this specification (note
 * that the presence of some of these elements is required):
 * 
 * o  atom:feed elements MUST contain one or more atom:author elements,
 *    unless all of the atom:feed element's child atom:entry elements
 *    contain at least one atom:author element.
 * o  atom:feed elements MAY contain any number of atom:category
 *    elements.
 * o  atom:feed elements MAY contain any number of atom:contributor
 *    elements.
 * o  atom:feed elements MUST NOT contain more than one atom:generator
 *    element.
 * o  atom:feed elements MUST NOT contain more than one atom:icon
 *    element.
 * o  atom:feed elements MUST NOT contain more than one atom:logo
 *    element.
 * o  atom:feed elements MUST contain exactly one atom:id element.
 * o  atom:feed elements SHOULD contain one atom:link element with a rel
 *    attribute value of "self".  This is the preferred URI for
 *    retrieving Atom Feed Documents representing this Atom feed.
 * o  atom:feed elements MUST NOT contain more than one atom:link
 *    element with a rel attribute value of "alternate" that has the
 *    same combination of type and hreflang attribute values.
 * o  atom:feed elements MAY contain additional atom:link elements
 *    beyond those described above.
 * o  atom:feed elements MUST NOT contain more than one atom:rights
 *    element.
 * o  atom:feed elements MUST NOT contain more than one atom:subtitle
 *    element.
 * o  atom:feed elements MUST contain exactly one atom:title element.
 * o  atom:feed elements MUST contain exactly one atom:updated element.
 * 
 * If multiple atom:entry elements with the same atom:id value appear in
 * an Atom Feed Document, they represent the same entry.  Their
 * atom:updated timestamps SHOULD be different.  If an Atom Feed
 * Document contains multiple entries with the same atom:id, Atom
 * Processors MAY choose to display all of them or some subset of them.
 * One typical behavior would be to display only the entry with the
 * latest atom:updated timestamp.
 * </pre>
 */
public interface Feed extends Source {

    /**
     * Returns the complete set of entries contained in this feed
     * 
     * @return A listing of atom:entry elements
     */
    List<Entry> getEntries();

    /**
     * Adds a new Entry to the <i>end</i> of the Feeds collection of entries
     * 
     * @param entry Add an entry
     */
    Feed addEntry(Entry entry);

    /**
     * Adds a new Entry to the <i>end</i> of the Feeds collection of entries
     * 
     * @return A newly created atom:entry
     */
    Entry addEntry();

    /**
     * Adds a new Entry to the <i>start</i> of the Feeds collection of entries
     * 
     * @param entry An atom:entry to insert
     */
    Feed insertEntry(Entry entry);

    /**
     * Adds a new Entry to the <i>start</i> of the Feeds collection of entries
     * 
     * @return A newly created atom:entry
     */
    Entry insertEntry();

    /**
     * Creates a Source element from this Feed
     * 
     * @return Returns a copy of this atom:feed as a atom:source element
     */
    Source getAsSource();

    /**
     * Sorts entries by the atom:updated property
     * 
     * @param new_first If true, entries with newer atom:updated values will come first
     */
    Feed sortEntriesByUpdated(boolean new_first);

    /**
     * Sorts entries by the app:edited property. if app:edited is null, use app:updated
     */
    Feed sortEntriesByEdited(boolean new_first);

    /**
     * Sorts entries using the given comparator
     * 
     * @param comparator Sort the entries using the comparator
     */
    Feed sortEntries(Comparator<Entry> comparator);

    /**
     * Retrieves the first entry in the feed with the given atom:id value
     * 
     * @param id The id to retrieve
     * @return The matching atom:entry
     * @throws IRISyntaxException if the id is malformed
     */
    Entry getEntry(String id);

}
