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
package org.apache.abdera.test.ext.rss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.junit.Test;

public class RssTest {

    @Test
    public void testRSS1() {

        Abdera abdera = new Abdera();

        InputStream in = RssTest.class.getResourceAsStream("/rss1.rdf");

        Document<Feed> doc = abdera.getParser().parse(in);

        Feed feed = doc.getRoot();

        assertEquals("XML.com", feed.getTitle());
        assertEquals("http://xml.com/pub", feed.getAlternateLinkResolvedHref().toASCIIString());
        assertEquals("XML.com features a rich mix of information and services \n      for the XML community.", feed
            .getSubtitle().trim());
        assertNotNull(feed.getAuthor());
        assertEquals("James Snell", feed.getAuthor().getName());
        assertEquals("jasnell@example.com", feed.getAuthor().getEmail());
        assertEquals(1, feed.getCategories().size());
        assertEquals("Anything", feed.getCategories().get(0).getTerm());
        assertEquals("foo", feed.getId().toASCIIString());
        assertEquals("Copyright 2007 Foo", feed.getRights());
        assertNotNull(feed.getUpdated());
        assertEquals("en-US", feed.getLanguage());
        assertEquals(1, feed.getContributors().size());
        Person person = feed.getContributors().get(0);
        assertEquals("John Doe", person.getName());
        assertEquals("jdoe@example.org", person.getEmail());

        List<Entry> entries = feed.getEntries();
        assertEquals(2, entries.size());

        Entry entry = entries.get(0);
        assertEquals("Processing Inclusions with XSLT", entry.getTitle());
        assertEquals("http://xml.com/pub/2000/08/09/xslt/xslt.html", entry.getId().toASCIIString());
        assertEquals("http://xml.com/pub/2000/08/09/xslt/xslt.html", entry.getAlternateLinkResolvedHref()
            .toASCIIString());
        assertNotNull(entry.getSummary());
        assertEquals("testing", entry.getContent());

        person = entry.getAuthor();
        System.out.println(person.getName());
        assertEquals("Bob", person.getName());

        entry = entries.get(1);
        assertEquals("Putting RDF to Work", entry.getTitle());
        assertEquals("http://xml.com/pub/2000/08/09/rdfdb/index.html", entry.getId().toASCIIString());
        assertEquals("http://xml.com/pub/2000/08/09/rdfdb/index.html", entry.getAlternateLinkResolvedHref()
            .toASCIIString());

        assertNotNull(entry.getSummary());
        assertEquals("testing", entry.getContent());

        person = entry.getAuthor();
        assertEquals("Joe", person.getName());

    }

}
