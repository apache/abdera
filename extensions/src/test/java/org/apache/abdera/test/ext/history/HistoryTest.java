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

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.history.FeedHistoryHelper;
import org.apache.abdera.model.Feed;

import junit.framework.TestCase;

public class HistoryTest extends TestCase {

  public static void testHistory() throws Exception {
    
    Abdera abdera = new Abdera();
    
    Feed feed = abdera.getFactory().newFeed();

    FeedHistoryHelper.setComplete(feed, true);
    FeedHistoryHelper.setArchive(feed, true);

    FeedHistoryHelper.setNext(feed, "http://example.org/foo");
    FeedHistoryHelper.setNext(feed, "http://example.org/bar");
    
    FeedHistoryHelper.setPrevious(feed, "http://example.org/foo");
    FeedHistoryHelper.setPrevious(feed, "http://example.org/bar");
    
    FeedHistoryHelper.setLast(feed, "http://example.org/foo");
    FeedHistoryHelper.setLast(feed, "http://example.org/bar");

    FeedHistoryHelper.setFirst(feed, "http://example.org/foo");
    FeedHistoryHelper.setFirst(feed, "http://example.org/bar");

    FeedHistoryHelper.setPreviousArchive(feed, "http://example.org/foo");
    FeedHistoryHelper.setPreviousArchive(feed, "http://example.org/bar");
    
    FeedHistoryHelper.setNextArchive(feed, "http://example.org/foo");
    FeedHistoryHelper.setNextArchive(feed, "http://example.org/bar");

    FeedHistoryHelper.setCurrent(feed, "http://example.org/foo");
    FeedHistoryHelper.setCurrent(feed, "http://example.org/bar");
    
    assertTrue(FeedHistoryHelper.isPaged(feed));
    assertTrue(FeedHistoryHelper.isComplete(feed));
    assertTrue(FeedHistoryHelper.isArchive(feed));
    
    assertNotNull(FeedHistoryHelper.getNext(feed));
    assertNotNull(FeedHistoryHelper.getPrevious(feed));
    assertNotNull(FeedHistoryHelper.getLast(feed));
    assertNotNull(FeedHistoryHelper.getFirst(feed));
    assertNotNull(FeedHistoryHelper.getNextArchive(feed));
    assertNotNull(FeedHistoryHelper.getPreviousArchive(feed));
    assertNotNull(FeedHistoryHelper.getCurrent(feed));
    
    assertEquals(FeedHistoryHelper.getNext(feed).toString(),"http://example.org/bar");
  }
  
}
