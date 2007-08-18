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
package org.apache.abdera.test.sharing;

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

import junit.framework.TestCase;

public class SharingTest
    extends TestCase {
  
  public static void testSharingFactory() throws Exception {
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

  public static void testSimpleExample() throws Exception {
    
    String ex = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
                "xmlns:sx=\"http://www.microsoft.com/schemas/sse\">" +
                "<title>To Do List</title>" +
                "<subtitle>A list of items to do</subtitle>" + 
                "<link rel=\"self\" href=\"http://example.com/partial.xml\"/>" +
                "<author>" + 
                "<name>Ray Ozzie</name>" + 
                "</author>" +
                "<updated>2005-05-21T11:43:33Z</updated>" + 
                "<id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0aaa</id>" + 
                "<sx:sharing since=\"2005-02-13T18:30:02Z\" " + 
                "until=\"2005-05-23T18:30:02Z\" >" +
                "<sx:related link=\"http://example.com/all.xml\" type=\"complete\" />" +
                "<sx:related link=\"http://example.com/B.xml\" type=\"aggregated\" " +
                "title=\"To Do List (Jacks Copy)\" />" +
                "</sx:sharing>" + 
                "<entry>" + 
                "<title>Buy groceries</title>" + 
                "<content>Get milk, eggs, butter and bread</content>" + 
                "<id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0aa0</id>" + 
                "<author>" +
                "<name>Ray Ozzie</name>" + 
                "</author>" +
                "<updated>2005-05-21T11:43:33Z</updated>" + 
                "<sx:sync id=\"item 1_myapp_2005-05-21T11:43:33Z\" updates=\"3\">" +
                "<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>" +
                "<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>" +
                "<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>" +
                "</sx:sync>" + 
                "</entry></feed>";
    
    StringReader rdr = new StringReader(ex);
    Abdera abdera = new Abdera();
    Document<Feed> doc = abdera.getParser().parse(rdr);
    Feed feed = doc.getRoot();
    assertTrue(SharingHelper.hasSharing(feed));
    
    Sharing sharing = SharingHelper.getSharing(feed, false);
    assertNotNull(sharing);
    
    Date since = AtomDate.parse("2005-02-13T18:30:02Z");
    Date until = AtomDate.parse("2005-05-23T18:30:02Z");
    
    assertEquals(sharing.getSince(),since);
    assertEquals(sharing.getUntil(),until);
    
    assertEquals(sharing.getRelated().size(),2);
    
    Related rel = sharing.getRelated().get(0);
    assertEquals(rel.getLink().toString(),"http://example.com/all.xml");
    assertEquals(rel.getType(),Related.Type.COMPLETE);
    
    rel = sharing.getRelated().get(1);
    assertEquals(rel.getLink().toString(),"http://example.com/B.xml");
    assertEquals(rel.getType(),Related.Type.AGGREGATED);
    
    Entry entry = feed.getEntries().get(0);
    Sync sync = SharingHelper.getSync(entry, false);
    assertNotNull(sync);
    assertEquals(sync.getId(),"item 1_myapp_2005-05-21T11:43:33Z");
    assertEquals(sync.getUpdates(),3);
    
    assertEquals(sync.getHistory().size(),3);
    
    Date d1 = AtomDate.parse("2005-05-21T11:43:33Z");
    Date d2 = AtomDate.parse("2005-05-21T10:43:33Z");
    Date d3 = AtomDate.parse("2005-05-21T09:43:33Z");
    
    History history = sync.getHistory().get(0);
    assertEquals(history.getSequence(),3);
    assertEquals(history.getWhen(),d1);
    assertEquals(history.getBy(),"JEO2000");
    
    history = sync.getHistory().get(1);
    assertEquals(history.getSequence(),2);
    assertEquals(history.getWhen(),d2);
    assertEquals(history.getBy(),"REO1750");
    
    history = sync.getHistory().get(2);
    assertEquals(history.getSequence(),1);
    assertEquals(history.getWhen(),d3);
    assertEquals(history.getBy(),"REO1750");
  }
  
  public void testCreateOperation() throws Exception  {
    Abdera abdera = new Abdera();
    Entry entry = SharingHelper.createEntry(abdera, "jms");
    Sync sync = SharingHelper.getSync(entry, false);
    assertNotNull(sync);
    assertNotNull(sync.getId());
    assertEquals(sync.getUpdates(),1);
    assertEquals(sync.getHistory().size(),1);
    History history = sync.getTopmostHistory();
    assertEquals(history.getSequence(),1);
    assertEquals(history.getBy(),"jms");
  }
  
  public void testUpdateOperation() throws Exception {
    Abdera abdera = new Abdera();
    Entry entry = SharingHelper.createEntry(abdera, "jms");
    SharingHelper.updateEntry(entry, "jms");
    Sync sync = SharingHelper.getSync(entry, false);
    assertNotNull(sync);
    assertNotNull(sync.getId());
    assertEquals(sync.getUpdates(),2);
    assertEquals(sync.getHistory().size(),2);
    History history = sync.getTopmostHistory();
    assertEquals(history.getSequence(),2);
    assertEquals(history.getBy(),"jms");
  }
  
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
    assertEquals(sync.getUpdates(),2);
    assertEquals(sync.getHistory().size(),2);
    History history = sync.getTopmostHistory();
    assertEquals(history.getSequence(),2);
    assertEquals(history.getBy(),"jms");
  }
  
  public void testConflict() throws Exception  {
    
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
    
    assertEquals(f2.getEntries().size(),1);
    Entry entry = f2.getEntries().get(0);
    Sync sync = SharingHelper.getSync(entry);
    Conflicts conflicts = sync.getConflicts();
    assertNotNull(conflicts);
    assertEquals(conflicts.getEntries().size(),1);
    Entry conflict = conflicts.getEntries().get(0);
    assertNotNull(conflict);
    
    ConflictResolver r = new ConflictResolver() {
      public Entry resolve(Entry entry, List<Entry> conflicts) {
        Sync sync = SharingHelper.getSync(entry,false);
        Conflicts c = sync.getConflicts(false);
        if (c != null) c.discard();
        return entry; // take the latest
      }
    };
    entry = SharingHelper.resolveConflicts(entry, r, "jms");
    sync = SharingHelper.getSync(entry);
    conflicts = sync.getConflicts();
    assertNull(conflicts);
    assertEquals(sync.getHistory().size(),4);
  }
  
  public void testUnpublish() throws Exception {
    
    Abdera abdera = new Abdera();
    Feed feed = abdera.newFeed();
    Entry entry = feed.addEntry();
    assertEquals(feed.getEntries().size(),1);
    entry = SharingHelper.unpublish(entry);
    assertEquals(feed.getEntries().size(),0);
    Unpublished unpub = SharingHelper.getUnpublished(feed);
    assertEquals(unpub.getEntries().size(),1);
    SharingHelper.republish(entry);
    unpub = SharingHelper.getUnpublished(feed);
    assertEquals(unpub.getEntries().size(),0);
    assertEquals(feed.getEntries().size(),1);
  }
}
