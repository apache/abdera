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
package org.apache.abdera.examples.ext;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.sharing.Conflicts;
import org.apache.abdera.ext.sharing.SharingHelper;
import org.apache.abdera.ext.sharing.Sync;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

/**
 * Basic Simple Sharing Extensions support
 */
public class Sharing {

    public static void main(String... args) throws Exception {

        Abdera abdera = new Abdera();

        // Create two feeds
        Feed f1 = abdera.newFeed();
        Feed f2 = abdera.newFeed();

        // Create a couple of sharing-enabled entries
        Entry e1 = SharingHelper.createEntry(abdera, "jms", f1);
        e1.newId();

        Entry e2 = SharingHelper.createEntry(abdera, "jms", f1);
        e2.newId();

        Entry e3 = (Entry)e2.clone();
        f2.addEntry(e3);

        // concurrent modification of the same entry by two different users in two different feeds
        SharingHelper.updateEntry(e2, "bob");
        SharingHelper.updateEntry(e3, "joe");

        // prepare a third feed for merging
        Feed f3 = (Feed)f2.clone();

        // merge f1 with f2 to produce f3
        SharingHelper.mergeFeeds(f1, f3);

        // there will be two entries in f3, one of which shows a conflict
        for (Entry entry : f3.getEntries()) {
            System.out.println(entry.getId());
            if (SharingHelper.hasConflicts(entry)) {
                Sync sync = SharingHelper.getSync(entry);
                Conflicts conflicts = sync.getConflicts();
                System.out.println("\tNumber of conflicts: " + conflicts.getEntries().size());
            }
        }

    }

}
