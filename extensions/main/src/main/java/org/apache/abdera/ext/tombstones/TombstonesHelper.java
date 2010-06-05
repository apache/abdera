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
package org.apache.abdera.ext.tombstones;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

/**
 * Note this is an experimental implementation of the Tombstones specification. The tombstone spec is a work-in-progress
 * and will continue to evolve. This extension should only be used for experimentation or prototype development.
 * http://www.ietf.org/internet-drafts/draft-snell-atompub-tombstones-04.txt
 */
public class TombstonesHelper {

    public static final String TNS = "http://purl.org/atompub/tombstones/1.0";

    public static final QName DELETED_ENTRY = new QName(TNS, "deleted-entry");
    public static final QName BY = new QName(TNS, "by");
    public static final QName COMMENT = new QName(TNS, "comment");

    public static List<Tombstone> getTombstones(Feed source) {
        return source.getExtensions(DELETED_ENTRY);
    }

    public static void addTombstone(Feed source, Tombstone tombstone) {
        source.addExtension(tombstone);
    }

    public static Tombstone addTombstone(Feed source, Entry entry) {
        Tombstone tombstone = source.getFactory().newExtensionElement(DELETED_ENTRY, source);
        tombstone.setRef(entry.getId());
        Base parent = entry.getParentElement();
        if (parent != null && parent.equals(source))
            entry.discard();
        return tombstone;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, Date when, String by, String comment) {
        Tombstone ts = addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, String when, String by, String comment) {
        Tombstone ts = addTombstone(source, entry);
        ts.setWhen(when);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, Calendar when, String by, String comment) {
        Tombstone ts = addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, long when, String by, String comment) {
        Tombstone ts = addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, Entry entry, AtomDate when, String by, String comment) {
        Tombstone ts = addTombstone(source, entry);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;

    }

    public static boolean hasTombstones(Feed source) {
        return source.getExtension(DELETED_ENTRY) != null;
    }

    public static Tombstone addTombstone(Feed source, String id) {
        Tombstone tombstone = source.getFactory().newExtensionElement(DELETED_ENTRY);
        tombstone.setRef(id);
        return tombstone;
    }

    public static Tombstone addTombstone(Feed source, String id, Date when, String by, String comment) {
        Tombstone ts = addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, String when, String by, String comment) {
        Tombstone ts = addTombstone(source, id);
        ts.setWhen(when);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, Calendar when, String by, String comment) {
        Tombstone ts = addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, long when, String by, String comment) {
        Tombstone ts = addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

    public static Tombstone addTombstone(Feed source, String id, AtomDate when, String by, String comment) {
        Tombstone ts = addTombstone(source, id);
        ts.setWhen(when);
        ts.setBy(by);
        ts.setComment(comment);
        return ts;
    }

}
