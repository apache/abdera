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
package org.apache.abdera.ext.history;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;

/**
 * Initial support for Mark Nottingham's Feed Paging and Archiving draft
 * (http://ietfreport.isoc.org/all-ids/draft-nottingham-atompub-feed-history-11.txt)
 */
public final class FeedPagingHelper {

    public static final String FH_PREFIX = "fh";
    public static final String FHNS = "http://purl.org/syndication/history/1.0";
    public static final QName COMPLETE = new QName(FHNS, "complete", FH_PREFIX);
    public static final QName ARCHIVE = new QName(FHNS, "archive", FH_PREFIX);

    FeedPagingHelper() {
    }

    /**
     * Returns true if the feed is "complete". According to the Feed Paging and Archiving specification, in a complete
     * feed, "any entry not actually in the feed document SHOULD NOT be considered to be part of that feed."
     * 
     * @param feed The feed to check
     */
    public static boolean isComplete(Source feed) {
        return (feed.getExtension(COMPLETE) != null);
    }

    /**
     * Flag the feed as being complete. According to the Feed Paging and Archiving specification, in a complete feed,
     * "any entry not actually in the feed document SHOULD NOT be considered to be part of that feed."
     * 
     * @param feed The Feed to mark as complete
     * @param complete True if the feed is complete
     */
    public static void setComplete(Source feed, boolean complete) {
        if (complete) {
            if (!isComplete(feed))
                feed.addExtension(COMPLETE);
        } else {
            if (isComplete(feed)) {
                Element ext = feed.getExtension(COMPLETE);
                ext.discard();
            }
        }
    }

    /**
     * Flag the feed as being an archive.
     * 
     * @param feed The Feed to mark as an archive
     * @param archive True if the feed is an archive
     */
    public static void setArchive(Source feed, boolean archive) {
        if (archive) {
            if (!isArchive(feed))
                feed.addExtension(ARCHIVE);
        } else {
            if (isArchive(feed)) {
                Element ext = feed.getExtension(ARCHIVE);
                ext.discard();
            }
        }
    }

    /**
     * Return true if the feed has been marked as an archive
     * 
     * @param feed The feed to check
     */
    public static boolean isArchive(Source feed) {
        return feed.getExtension(ARCHIVE) != null;
    }

    /**
     * Return true if the feed contains any next, previous, first or last paging link relations
     * 
     * @param feed The feed to check
     */
    public static boolean isPaged(Source feed) {
        return feed.getLink("next") != null || feed.getLink("previous") != null
            || feed.getLink("first") != null
            || feed.getLink("last") != null;
    }

    /**
     * Adds a next link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the next feed document
     * @return The newly created Link
     */
    public static Link setNext(Source feed, String iri) {
        Link link = feed.getLink("next");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "next");
        }
        return link;
    }

    /**
     * Adds a previous link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the previous feed document
     * @return The newly created Link
     */
    public static Link setPrevious(Source feed, String iri) {
        Link link = feed.getLink("previous");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "previous");
        }
        return link;
    }

    /**
     * Adds a first link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the first feed document
     * @return The newly created Link
     */
    public static Link setFirst(Source feed, String iri) {
        Link link = feed.getLink("first");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "first");
        }
        return link;
    }

    /**
     * Adds a last link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the last feed document
     * @return The newly created Link
     */
    public static Link setLast(Source feed, String iri) {
        Link link = feed.getLink("last");
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "last");
        }
        return link;
    }

    /**
     * Adds a next-archive link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the next archive feed document
     * @return The newly created Link
     */
    public static Link setNextArchive(Source feed, String iri) {
        Link link = feed.getLink("next-archive");
        if (link == null) { // try the full IANA URI version
            link = feed.getLink(Link.IANA_BASE + "next-archive");
        }
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "next-archive");
        }
        return link;
    }

    /**
     * Adds a prev-archive link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the previous archive feed document
     * @return The newly created Link
     */
    public static Link setPreviousArchive(Source feed, String iri) {
        Link link = feed.getLink("prev-archive");
        if (link == null) { // try the full IANA URI version
            link = feed.getLink(Link.IANA_BASE + "prev-archive");
        }
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "prev-archive");
        }
        return link;
    }

    /**
     * Adds a current link relation to the feed
     * 
     * @param feed The feed
     * @param iri The IRI of the current feed document
     * @return The newly created Link
     */
    public static Link setCurrent(Source feed, String iri) {
        Link link = feed.getLink("current");
        if (link == null) { // try the full IANA URI version
            link = feed.getLink(Link.IANA_BASE + "current");
        }
        if (link != null) {
            link.setHref(iri);
        } else {
            link = feed.addLink(iri, "current");
        }
        return link;
    }

    /**
     * Returns the IRI of the next link relation
     */
    public static IRI getNext(Source feed) {
        Link link = feed.getLink("next");
        return (link != null) ? link.getResolvedHref() : null;
    }

    /**
     * Returns the IRI of the previous link relation
     */
    public static IRI getPrevious(Source feed) {
        Link link = feed.getLink("previous");
        return (link != null) ? link.getResolvedHref() : null;
    }

    /**
     * Returns the IRI of the first link relation
     */
    public static IRI getFirst(Source feed) {
        Link link = feed.getLink("first");
        return (link != null) ? link.getResolvedHref() : null;
    }

    /**
     * Returns the IRI of the last link relation
     */
    public static IRI getLast(Source feed) {
        Link link = feed.getLink("last");
        return (link != null) ? link.getResolvedHref() : null;
    }

    /**
     * Returns the IRI of the prev-archive link relation
     */
    public static IRI getPreviousArchive(Source feed) {
        Link link = feed.getLink("prev-archive");
        if (link == null) { // try the full IANA URI version
            link = feed.getLink(Link.IANA_BASE + "prev-archive");
        }
        return (link != null) ? link.getResolvedHref() : null;
    }

    /**
     * Returns the IRI of the next-archive link relation
     */
    public static IRI getNextArchive(Source feed) {
        Link link = feed.getLink("next-archive");
        if (link == null) { // try the full IANA URI version
            link = feed.getLink(Link.IANA_BASE + "next-archive");
        }
        return (link != null) ? link.getResolvedHref() : null;
    }

    /**
     * Returns the IRI of the current link relation
     */
    public static IRI getCurrent(Source feed) {
        Link link = feed.getLink("current");
        if (link == null) { // try the full IANA URI version
            link = feed.getLink(Link.IANA_BASE + "current");
        }
        return (link != null) ? link.getResolvedHref() : null;
    }
}
