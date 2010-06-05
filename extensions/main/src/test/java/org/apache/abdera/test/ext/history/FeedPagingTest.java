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
package org.apache.abdera.test.ext.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.history.FeedPagingHelper;
import org.apache.abdera.model.Feed;
import org.junit.Test;

public class FeedPagingTest {

    @Test
    public void testHistory() throws Exception {

        Abdera abdera = new Abdera();

        Feed feed = abdera.newFeed();

        FeedPagingHelper.setComplete(feed, true);
        FeedPagingHelper.setArchive(feed, true);

        FeedPagingHelper.setNext(feed, "http://example.org/foo");
        FeedPagingHelper.setNext(feed, "http://example.org/bar");

        FeedPagingHelper.setPrevious(feed, "http://example.org/foo");
        FeedPagingHelper.setPrevious(feed, "http://example.org/bar");

        FeedPagingHelper.setLast(feed, "http://example.org/foo");
        FeedPagingHelper.setLast(feed, "http://example.org/bar");

        FeedPagingHelper.setFirst(feed, "http://example.org/foo");
        FeedPagingHelper.setFirst(feed, "http://example.org/bar");

        FeedPagingHelper.setPreviousArchive(feed, "http://example.org/foo");
        FeedPagingHelper.setPreviousArchive(feed, "http://example.org/bar");

        FeedPagingHelper.setNextArchive(feed, "http://example.org/foo");
        FeedPagingHelper.setNextArchive(feed, "http://example.org/bar");

        FeedPagingHelper.setCurrent(feed, "http://example.org/foo");
        FeedPagingHelper.setCurrent(feed, "http://example.org/bar");

        assertTrue(FeedPagingHelper.isPaged(feed));
        assertTrue(FeedPagingHelper.isComplete(feed));
        assertTrue(FeedPagingHelper.isArchive(feed));

        assertNotNull(FeedPagingHelper.getNext(feed));
        assertNotNull(FeedPagingHelper.getPrevious(feed));
        assertNotNull(FeedPagingHelper.getLast(feed));
        assertNotNull(FeedPagingHelper.getFirst(feed));
        assertNotNull(FeedPagingHelper.getNextArchive(feed));
        assertNotNull(FeedPagingHelper.getPreviousArchive(feed));
        assertNotNull(FeedPagingHelper.getCurrent(feed));

        assertEquals("http://example.org/bar", FeedPagingHelper.getNext(feed).toString());
    }

    @Test
    public void testHistory2() throws Exception {
        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();
        QName complete = new QName(FeedPagingHelper.FHNS, "complete", "x");
        feed.addExtension(complete);
        assertTrue(FeedPagingHelper.isComplete(feed));
    }
}
