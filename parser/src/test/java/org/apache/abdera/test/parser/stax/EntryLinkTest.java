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
package org.apache.abdera.test.parser.stax;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;



public class EntryLinkTest extends TestCase {

    /**
     * Link in entry disappears after adding entry to a feed.
     * 
     * @see https://issues.apache.org/jira/browse/ABDERA-70
     */
    public void testEntryLinkInFeed() throws Exception {
        Abdera abdera = new Abdera();
        Factory factory = abdera.getFactory();
        Feed feed = factory.newFeed();
        feed.setTitle("Test");
        feed.setId("http://example.com/feed");
        Parser parser = abdera.getParser();
        Document<Entry> doc = parser.parse(this.getClass().getResourceAsStream("/entry.xml"));
        Entry entry = doc.getRoot();
        // Workaround for ABDERA-70 suggested by James Snell.
        entry = (Entry) entry.clone();
        // End workaround
        Link link = factory.newLink();
        link.setHref(entry.getId().toString());
        link.setRel(Link.REL_EDIT);
        entry.addLink(link);
        assertNotNull("Link is null", entry.getLink(Link.REL_EDIT));
        feed.addEntry(entry);
        assertNotNull("Link is null", entry.getLink(Link.REL_EDIT));
        for (Entry e : feed.getEntries()) {
            assertNotNull("Link is null", e.getLink(Link.REL_EDIT));
        }
    }
}
