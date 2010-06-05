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
package org.apache.abdera.test.ext.sharing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.sharing.Conflicts;
import org.apache.abdera.ext.sharing.History;
import org.apache.abdera.ext.sharing.Related;
import org.apache.abdera.ext.sharing.Sharing;
import org.apache.abdera.ext.sharing.SharingHelper;
import org.apache.abdera.ext.sharing.Sync;
import org.apache.abdera.ext.sharing.Unpublished;
import org.apache.abdera.ext.sharing.SharingHelper.ConflictResolver;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.junit.Test;

public class SharingTest {

    @Test
    public void testSharingFactory() throws Exception {
        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();
        Conflicts conflicts = factory.newElement(SharingHelper.SSE_CONFLICTS);
        assertNotNull(conflicts);
        History history = factory.newElement(SharingHelper.SSE_HISTORY);
        assertNotNull(history);
        Related related = factory.newElement(SharingHelper.SSE_RELATED);
        assertNotNull(related);
        Sharing sharing = factory.newElement(SharingHelper.SSE_SHARING);
        assertNotNull(sharing);
        Sync sync = factory.newElement(SharingHelper.SSE_SYNC);
        assertNotNull(sync);
        Unpublished unpub = factory.newElement(SharingHelper.SSE_UNPUBLISHED);
        assertNotNull(unpub);
    }

    @Test
    public void testSimpleExample() throws Exception {

        String ex =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<feed xmlns=\"http://www.w3.org/2005/Atom\" "
                + "xmlns:sx=\"http://feedsync.org/2007/feedsync\">"
                + "<title>To Do List</title>"
                + "<subtitle>A list of items to do</subtitle>"
                + "<link rel=\"self\" href=\"http://example.com/partial.xml\"/>"
                + "<author>"
                + "<name>Ray Ozzie</name>"
                + "</author>"
                + "<updated>2005-05-21T11:43:33Z</updated>"
                + "<id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0aaa</id>"
                + "<sx:sharing since=\"2005-02-13T18:30:02Z\" "
                + "until=\"2005-05-23T18:30:02Z\" >"
                + "<sx:related link=\"http://example.com/all.xml\" type=\"complete\" />"
                + "<sx:related link=\"http://example.com/B.xml\" type=\"aggregated\" "
                + "title=\"To Do List (Jacks Copy)\" />"
                + "</sx:sharing>"
                + "<entry>"
                + "<title>Buy groceries</title>"
                + "<content>Get milk, eggs, butter and bread</content>"
                + "<id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0aa0</id>"
                + "<author>"
                + "<name>Ray Ozzie</name>"
                + "</author>"
                + "<updated>2005-05-21T11:43:33Z</updated>"
                + "<sx:sync id=\"item 1_myapp_2005-05-21T11:43:33Z\" updates=\"3\">"
                + "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"
                + "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"
                + "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"
                + "</sx:sync>"
                + "</entry></feed>";

        StringReader rdr = new StringReader(ex);
        Abdera abdera = new Abdera();
        Document<Feed> doc = abdera.getParser().parse(rdr);
        Feed feed = doc.getRoot();
        assertTrue(SharingHelper.hasSharing(feed));

        Sharing sharing = SharingHelper.getSharing(feed, false);
        assertNotNull(sharing);

        Date since = AtomDate.parse("2005-02-13T18:30:02Z");
        Date until = AtomDate.parse("2005-05-23T18:30:02Z");

        assertEquals(since, sharing.getSince());
        assertEquals(until, sharing.getUntil());

        assertEquals(2, sharing.getRelated().size());

        Related rel = sharing.getRelated().get(0);
        assertEquals("http://example.com/all.xml", rel.getLink().toString());
        assertEquals(Related.Type.COMPLETE, rel.getType());

        rel = sharing.getRelated().get(1);
        assertEquals("http://example.com/B.xml", rel.getLink().toString());
        assertEquals(Related.Type.AGGREGATED, rel.getType());

        Entry entry = feed.getEntries().get(0);
        Sync sync = SharingHelper.getSync(entry, false);
        assertNotNull(sync);
        assertEquals("item 1_myapp_2005-05-21T11:43:33Z", sync.getId());
        assertEquals(3, sync.getUpdates());

        assertEquals(3, sync.getHistory().size());

        Date d1 = AtomDate.parse("2005-05-21T11:43:33Z");
        Date d2 = AtomDate.parse("2005-05-21T10:43:33Z");
        Date d3 = AtomDate.parse("2005-05-21T09:43:33Z");

        History history = sync.getHistory().get(0);
        assertEquals(3, history.getSequence());
        assertEquals(d1, history.getWhen());
        assertEquals("JEO2000", history.getBy());

        history = sync.getHistory().get(1);
        assertEquals(2, history.getSequence());
        assertEquals(d2, history.getWhen());
        assertEquals("REO1750", history.getBy());

        history = sync.getHistory().get(2);
        assertEquals(1, history.getSequence());
        assertEquals(d3, history.getWhen());
        assertEquals("REO1750", history.getBy());
    }

    @Test
    public void testCreateOperation() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = SharingHelper.createEntry(abdera, "jms");
        Sync sync = SharingHelper.getSync(entry, false);
        assertNotNull(sync);
        assertNotNull(sync.getId());
        assertEquals(1, sync.getUpdates());
        assertEquals(1, sync.getHistory().size());
        History history = sync.getTopmostHistory();
        assertEquals(1, history.getSequence());
        assertEquals("jms", history.getBy());
    }

    @Test
    public void testUpdateOperation() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = SharingHelper.createEntry(abdera, "jms");
        SharingHelper.updateEntry(entry, "jms");
        Sync sync = SharingHelper.getSync(entry, false);
        assertNotNull(sync);
        assertNotNull(sync.getId());
        assertEquals(2, sync.getUpdates());
        assertEquals(2, sync.getHistory().size());
        History history = sync.getTopmostHistory();
        assertEquals(2, history.getSequence());
        assertEquals("jms", history.getBy());
    }

    @Test
    public void testDeleteOperation() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = SharingHelper.createEntry(abdera, "jms");
        Sync sync = SharingHelper.getSync(entry, false);
        assertNotNull(sync);
        assertFalse(sync.isDeleted());
        SharingHelper.deleteEntry(entry, "jms");
        sync = SharingHelper.getSync(entry, false);
        assertNotNull(sync);
        assertTrue(sync.isDeleted());
        assertNotNull(sync.getId());
        assertEquals(2, sync.getUpdates());
        assertEquals(2, sync.getHistory().size());
        History history = sync.getTopmostHistory();
        assertEquals(2, history.getSequence());
        assertEquals("jms", history.getBy());
    }

    @Test
    public void testConflict() throws Exception {

        Abdera abdera = new Abdera();
        Feed f1 = abdera.newFeed();
        Feed f2 = abdera.newFeed();
        Entry e1 = SharingHelper.createEntry(abdera, "jms", f1);
        Entry e2 = SharingHelper.createEntry(abdera, "jms", f2);
        Sync s1 = SharingHelper.getSync(e1, false);
        Sync s2 = SharingHelper.getSync(e2, false);
        s2.setId(s1.getId());
        SharingHelper.updateEntry(e1, "bob");
        SharingHelper.updateEntry(e2, "jms");

        SharingHelper.mergeFeeds(f1, f2);

        assertEquals(1, f2.getEntries().size());
        Entry entry = f2.getEntries().get(0);
        Sync sync = SharingHelper.getSync(entry);
        Conflicts conflicts = sync.getConflicts();
        assertNotNull(conflicts);
        assertEquals(1, conflicts.getEntries().size());
        Entry conflict = conflicts.getEntries().get(0);
        assertNotNull(conflict);

        ConflictResolver r = new ConflictResolver() {
            public Entry resolve(Entry entry, List<Entry> conflicts) {
                Sync sync = SharingHelper.getSync(entry, false);
                Conflicts c = sync.getConflicts(false);
                if (c != null)
                    c.discard();
                return entry; // take the latest
            }
        };

        entry = SharingHelper.resolveConflicts(entry, r, "jms");
        sync = SharingHelper.getSync(entry);
        conflicts = sync.getConflicts();
        assertNull(conflicts);
        assertEquals(4, sync.getHistory().size());

    }

    @Test
    public void testUnpublish() throws Exception {

        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();
        Entry entry = feed.addEntry();
        assertEquals(1, feed.getEntries().size());
        entry = SharingHelper.unpublish(entry);
        assertEquals(0, feed.getEntries().size());
        Unpublished unpub = SharingHelper.getUnpublished(feed);
        assertEquals(1, unpub.getEntries().size());
        SharingHelper.republish(entry);
        unpub = SharingHelper.getUnpublished(feed);
        assertEquals(0, unpub.getEntries().size());
        assertEquals(1, feed.getEntries().size());
    }
}
