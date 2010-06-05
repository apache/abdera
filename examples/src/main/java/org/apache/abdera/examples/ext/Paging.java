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
import org.apache.abdera.ext.history.FeedPagingHelper;
import org.apache.abdera.model.Feed;

public class Paging {

    public static void main(String... args) throws Exception {

        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();

        // Set/Get the paging links
        FeedPagingHelper.setCurrent(feed, "feed");
        FeedPagingHelper.setNext(feed, "feed?page=3");
        FeedPagingHelper.setPrevious(feed, "feed?page=1");
        FeedPagingHelper.setFirst(feed, "feed");
        FeedPagingHelper.setLast(feed, "feed?page=10");
        FeedPagingHelper.setNextArchive(feed, "feed?page=3");
        FeedPagingHelper.setPreviousArchive(feed, "feed?page=1");

        System.out.println(FeedPagingHelper.getCurrent(feed));
        System.out.println(FeedPagingHelper.getNext(feed));
        System.out.println(FeedPagingHelper.getPrevious(feed));
        System.out.println(FeedPagingHelper.getFirst(feed));
        System.out.println(FeedPagingHelper.getLast(feed));
        System.out.println(FeedPagingHelper.getNextArchive(feed));
        System.out.println(FeedPagingHelper.getPreviousArchive(feed));

        // Set/Get the archive flag
        FeedPagingHelper.setArchive(feed, true);
        if (FeedPagingHelper.isArchive(feed))
            System.out.println("archive feed!");

        // Set/Get the complete flag
        FeedPagingHelper.setComplete(feed, true);
        if (FeedPagingHelper.isComplete(feed))
            System.out.println("complete feed!");

    }

}
