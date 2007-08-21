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
package org.apache.abdera.ext.sharing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Source;

public class SharingHelper {

  public static final String SSENS = "http://www.microsoft.com/schemas/sse";
  public static final String SSEPFX = "sx";
  
  public static final QName SSE_SHARING = new QName(SSENS, "sharing", SSEPFX);
  public static final QName SSE_RELATED = new QName(SSENS, "related", SSEPFX);
  public static final QName SSE_CONFLICTS = new QName(SSENS, "conflicts", SSEPFX);
  public static final QName SSE_HISTORY = new QName(SSENS, "history", SSEPFX);
  public static final QName SSE_SYNC = new QName(SSENS, "sync", SSEPFX);
  public static final QName SSE_UNPUBLISHED = new QName(SSENS, "unpublished", SSEPFX);
  
  
  protected static boolean isTrue(String value) {
    return value.equalsIgnoreCase("true") || 
           value.equals("1") || 
           value.equals("-1") || 
           value.equals("yes");
  }
  
  public static <T extends Source>Sharing getSharing(T source) {
    return getSharing(source,false);
  }
  
  public static <T extends Source>Sharing getSharing(T source, boolean create) {
    Sharing sharing = source.getExtension(SSE_SHARING);
    if (sharing == null && create) sharing = source.addExtension(SSE_SHARING);
    return sharing;
  }
  
  public static <T extends Source>boolean hasSharing(T source) {
    return getSharing(source) != null;
  }
  
  public static Unpublished getUnpublished(Feed feed) {
    return getUnpublished(feed,false);
  }
  
  public static Unpublished getUnpublished(Feed feed, boolean create) {
    Unpublished unpub = feed.getExtension(SSE_UNPUBLISHED);
    if (unpub == null && create) unpub = feed.addExtension(SSE_UNPUBLISHED);
    return unpub;
  }
  
  public static Sync getSync(Entry entry) {
    return getSync(entry,false);
  }
  
  public static Sync getSync(Entry entry, boolean create) {
    Sync sync = entry.getExtension(SSE_SYNC);
    if (sync == null && create) sync = entry.addExtension(SSE_SYNC);
    return sync;
  }

  public static boolean hasSync(Entry entry) {
    return getSync(entry,false) != null;
  }
  
  public static Entry createEntry(Abdera abdera, String by) {
    return createEntry(abdera, by, null);
  }
  
  public static Entry createEntry(Abdera abdera, String by, Feed feed) {
    Entry entry = feed != null ? feed.addEntry() : abdera.newEntry();
    entry.newId();
    Sync sync = getSync(entry,true);
    sync.setId(entry.getId().toString());
    sync.setUpdates(1);
    History history = sync.addHistory();
    history.setSequence(sync.getUpdates());
    history.setWhen(new Date());
    history.setBy(by);
    return entry;
  }
  
  public static void deleteEntry(Entry entry, String by) {
    Sync sync = getSync(entry,true);
    sync.incrementUpdates();
    sync.setDeleted(true);
    History history = sync.addHistory();
    history.setSequence(sync.getUpdates());
    history.setWhen(new Date());
    history.setBy(by);
  }
  
  public static void updateEntry(Entry entry, String by) {
    Sync sync = getSync(entry,true);
    sync.incrementUpdates();
    History history = sync.addHistory();
    history.setSequence(sync.getUpdates());
    history.setWhen(new Date());
    history.setBy(by);
  }
  
  public static Map<String,Entry> getSyncIdMap(Feed feed) {
    Map<String,Entry> entries = new HashMap<String,Entry>();
    for (Entry entry : feed.getEntries()) {
      Sync sync = getSync(entry,false);
      if (sync != null) {
        String id = sync.getId();
        if (id != null) {
          entries.put(id,entry);
        }
      }
    }
    return entries;
  }
  
  public static boolean isSubsumed(Sync s1, Sync s2) {
    if (s1 == null && s2 == null) return false;
    if (s1 == null && s2 != null) return true;
    if (s1 != null && s2 == null) return false;
    if (s1.equals(s2)) return false;
    if (!s1.getId().equals(s2.getId())) return false;
    History h1 = s1.getFirstChild(SSE_HISTORY);
    for (History h2 : s2.getHistory()) {
      if (isSubsumed(h1,h2)) return true;
    }
    return false;
  }
  
  public static boolean isSubsumed(History h1, History h2) {
    if (h1 == null && h2 == null) return false;
    if (h1 == null && h2 != null) return true;
    if (h1 != null && h2 == null) return false;
    if (h1.equals(h2)) return false;
    String h1by = h1.getBy();
    String h2by = h2.getBy();
    if (h1by != null) {
      if (h2by != null && 
          h1by.equals(h2by) && 
          h2.getSequence() >= h1.getSequence()) 
            return true;
    } else {
      if (h2by == null && 
          h1.getWhen().equals(h2.getWhen()) && 
          h1.getSequence() == h2.getSequence()) 
            return true;
    }
    return false;
  }
  
  public static Sync pickWinner(Sync s1, Sync s2) {
    if (s1 == null && s2 == null) return null;
    if (s1 == null && s2 != null) return s2;
    if (s1 != null && s2 == null) return s1;
    if (s1.equals(s2)) return s1;
    if (!s1.getId().equals(s2.getId())) return null;
    if (s1.getUpdates() > s2.getUpdates()) return s1;
    if (s1.getUpdates() == s2.getUpdates()) {
      History h1 = s1.getTopmostHistory();
      History h2 = s2.getTopmostHistory();
      Date d1 = h1.getWhen();
      Date d2 = h2.getWhen();
      if (d1 != null && d2 == null) return s1;
      if (d1.after(d2)) return s1;
      if (d1.equals(d2)) {
        String b1 = h1.getBy();
        String b2 = h2.getBy();
        if (b1 != null && b2 == null) return s1;
        if (b1.compareTo(b2) > 0) return s1;
      }
    }
    return s2;
  }
  
  private static List<Entry> getConflicts(Entry entry) {
    List<Entry> list = new ArrayList<Entry>();
    Sync sync = getSync(entry,false);
    if (sync != null) {
      Conflicts conflicts = sync.getConflicts(false);
      if (conflicts != null) {
        list.addAll(conflicts.getEntries());
      }
    }
    list.add(entry);
    return list;
  }
  
  private static Entry compareConflicts(
    Entry w,
    List<Entry> outerList, 
    List<Entry> innerList, 
    List<Entry> results) {
      Entry[] outer = outerList.toArray(new Entry[outerList.size()]);
      Entry[] inner = innerList.toArray(new Entry[innerList.size()]);
      for (Entry x : outer) {
        Sync s1 = getSync(x,false);
        boolean ok = true;
        for (Entry y : inner) {
          Sync s2 = getSync(y,false);
          if (isSubsumed(s1,s2)) {
            outerList.remove(s1);
            ok = false;
            break;
          }
        }
        if (ok) {
          results.add(x);
          if (w == null) w = x;
          else {
            Sync s2 = getSync(w);
            if (pickWinner(s1,s2) == s1) w = x;
          }
        }
      }
      return w;
  }
  
  public static void mergeFeeds(Feed source, Feed dest) {
    Map<String,Entry> destentries = getSyncIdMap(dest);
    for (Entry entry : source.getEntries()) {
      Sync s2 = getSync(entry,false);
      if (s2 != null) {
        String id = s2.getId();
        if (id != null) {
          Entry existing = destentries.get(id);
          if (existing == null) {
            dest.addEntry((Entry)entry.clone());
          } else {
            Sync s1 = getSync(existing,false);
            List<Entry> c1 = getConflicts(existing);
            List<Entry> c2 = getConflicts(entry);
            List<Entry> m = new ArrayList<Entry>();
            Entry w = null;
            w = compareConflicts(w, c1,c2,m);
            w = compareConflicts(w, c2,c1,m);
            if (w != null) dest.addEntry(w);
            if (s1.isNoConflicts()) return;
            if (m.size() > 0) {
              Sync sync = getSync(w,true);
              sync.setConflicts(null);
              Conflicts conflicts = sync.getConflicts(true);
              for (Entry e : m) {
                if (e != w) conflicts.addEntry(e);
              }
            }
          }
        }
      } // otherwise skip the entry
    }
  }
  
  private static void mergeConflictItems(Entry entry, List<Entry> conflicts) {
    Sync sync = getSync(entry,true);
    for (Entry x : conflicts) {
      Sync xsync = getSync(x,false);
      if (xsync != null) {
        for (History h1 : xsync.getHistory()) {
          boolean ok = true;
          for (History h2 : sync.getHistory()) {
            if (isSubsumed(h1,h2)) {
              ok = false;
              break;
            }
          }
          if (ok) sync.addHistory(h1);
        }
      }
    }
  }
  
  public static Entry resolveConflicts(Entry entry, ConflictResolver resolver, String by) {
    List<Entry> conflicts = getConflicts(entry);
    entry = resolver.resolve(entry, conflicts);
    updateEntry(entry, by);
    mergeConflictItems(entry,conflicts);
    return entry;
  }
  
  public static interface ConflictResolver {
    Entry resolve(Entry entry, List<Entry> conflicts);
  }
  
  public static Entry unpublish(Entry entry) {
    if (entry == null) return null;
    Base base = entry.getParentElement();
    if (base == null || !(base instanceof Feed)) return null;
    Feed feed = (Feed) base;
    Unpublished unpub = getUnpublished(feed, true);
    Entry newentry = (Entry)entry.clone();
    newentry.setParentElement(unpub);
    unpub.addEntry(newentry);
    entry.discard();
    return newentry;
  }
  
  public static Entry republish(Entry entry) {
    if (entry == null) return null;
    Base base = entry.getParentElement();
    if (base == null || !(base instanceof Unpublished)) return null;
    Unpublished unpub = (Unpublished)base;
    Feed feed = unpub.getParentElement();
    Entry newentry = (Entry)entry.clone();
    newentry.setParentElement(feed);
    feed.addEntry(newentry);
    entry.discard();
    return newentry;
  }
  
  public static void publish(Feed feed, Date expires, boolean initial) {
    if (initial) {
      Sharing sharing = getSharing(feed,true);
      Date since = getMin(feed);
      Date until = getMax(feed);
      sharing.setSince(since);
      sharing.setUntil(until);
      sharing.setExpires(expires);
    } else {
      Sharing sharing = getSharing(feed,false);
      if (sharing != null) {
        sharing.setExpires(expires);
      } else {
        publish(feed, expires, true);
      }
    }
  }
  
  private static Date getMax(Feed feed) {
    Date d = null;
    for (Entry entry : feed.getEntries()) {
      Date updated = entry.getUpdated();
      if (d == null) d = updated;
      if (updated.after(d)) d = updated;
    }
    return d;
  }
  
  private static Date getMin(Feed feed) {
    Date d = null;
    for (Entry entry : feed.getEntries()) {
      Date updated = entry.getUpdated();
      if (d == null) d = updated;
      if (updated.before(d)) d = updated;
    }
    return d;
  }
  
  public boolean hasConflicts(Entry entry) {
    Sync sync = getSync(entry);
    if (sync != null) {
      Conflicts conflicts = sync.getConflicts();
      if (conflicts != null) {
        if (conflicts.getEntries().size() > 0) {
          return true;
        }
      }
    }
    return false;
  }
}
