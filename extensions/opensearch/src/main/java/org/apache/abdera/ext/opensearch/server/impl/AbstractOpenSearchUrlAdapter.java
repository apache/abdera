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
package org.apache.abdera.ext.opensearch.server.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.IntegerElement;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlAdapter;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;

/**
 * Abstract {@link org.apache.abdera.ext.opensearch.server.OpenSearchUrlAdapter} providing explicit methods to implement
 * and/or override for executing the actual search and creating the Atom feed containing search results.
 * 
 * @param <T> The generic object type representing a search result.
 */
public abstract class AbstractOpenSearchUrlAdapter<T> implements OpenSearchUrlAdapter {

    public ResponseContext search(RequestContext request, Map<String, String> parameters) {
        List<T> searchResults = this.doSearch(request, parameters);
        Feed feed = this.createFeed(request, parameters, searchResults);
        Document<Feed> document = feed.getDocument();
        ResponseContext response = new BaseResponseContext<Document<Feed>>(document);
        return response;
    }

    /**
     * Get the identifier of the feed containing this search results.
     */
    protected abstract String getOpenSearchFeedId(RequestContext request);

    /**
     * Get the title of the feed containing this search results.
     */
    protected abstract String getOpenSearchFeedTitle(RequestContext request);

    /**
     * Get the author of the feed containing this search results.
     */
    protected abstract Person getOpenSearchFeedAuthor(RequestContext request);

    /**
     * Get the update date of the feed containing this search results.
     */
    protected abstract Date getOpenSearchFeedUpdatedDate(RequestContext request);

    /**
     * Do the actual search, returning a list of search results as generic objects.
     */
    protected abstract List<T> doSearch(RequestContext request, Map<String, String> parameters);

    /**
     * Fill the given empty Atom entry from the given search result object.<br>
     * This method is called once for every search result returned by the {#doSearch(RequestContext, Map<String,
     * String>)} method.
     */
    protected abstract void fillEntry(Entry entry, T entity);

    /**
     * Get the total number of results of this search.<br>
     * By default, it's equal to the number of search results: override to provide a different behavior.<br>
     * This element can be explicitly omitted from the feed by returning a negative value.
     */
    protected int getOpenSearchFeedTotalResults(RequestContext request,
                                                Map<String, String> parameters,
                                                List<T> searchResults) {
        return searchResults.size();
    }

    /**
     * Get the number of items (entries) contained into this page (feed).<br>
     * By default, it's equal to the number of search results: override to provide a different behavior.<br>
     * This element can be explicitly omitted from the feed by returning a negative value.
     */
    protected int getOpenSearchFeedItemsPerPage(RequestContext request,
                                                Map<String, String> parameters,
                                                List<T> searchResults) {
        return searchResults.size();
    }

    /**
     * Get the index number of the first result contained into this feed.<br>
     * By default, this element is omitted: override to provide a different behavior.<br>
     * This element can be explicitly omitted from the feed by returning a negative value.
     */
    protected int getOpenSearchFeedStartIndex(RequestContext request,
                                              Map<String, String> parameters,
                                              List<T> searchResults) {
        return -1;
    }

    /**
     * Post process this feed in order to make custom modifications.<br>
     * By default, this method does nothing: override to provide a different behavior.
     */
    protected void postProcess(Feed feed, RequestContext request, Map<String, String> parameters, List<T> searchResults) {
    }

    private Feed createFeed(RequestContext searchRequest, Map<String, String> parameters, List<T> searchResults) {
        Factory factory = searchRequest.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        feed.setId(this.getOpenSearchFeedId(searchRequest));
        feed.setTitle(this.getOpenSearchFeedTitle(searchRequest));
        feed.addAuthor(this.getOpenSearchFeedAuthor(searchRequest));
        feed.setUpdated(this.getOpenSearchFeedUpdatedDate(searchRequest));
        feed.addLink(searchRequest.getUri().toString(), "self");
        int totalResults = this.getOpenSearchFeedTotalResults(searchRequest, parameters, searchResults);
        if (totalResults > -1) {
            ((IntegerElement)feed.addExtension(OpenSearchConstants.TOTAL_RESULTS)).setValue(totalResults);
        }
        int itemsPerPage = this.getOpenSearchFeedItemsPerPage(searchRequest, parameters, searchResults);
        if (itemsPerPage > -1) {
            ((IntegerElement)feed.addExtension(OpenSearchConstants.ITEMS_PER_PAGE)).setValue(itemsPerPage);
        }
        int startIndex = this.getOpenSearchFeedStartIndex(searchRequest, parameters, searchResults);
        if (startIndex > -1) {
            ((IntegerElement)feed.addExtension(OpenSearchConstants.START_INDEX)).setValue(startIndex);
        }
        for (T entity : searchResults) {
            Entry entry = factory.newEntry();
            this.fillEntry(entry, entity);
            feed.addEntry(entry);
        }
        this.postProcess(feed, searchRequest, parameters, searchResults);
        return feed;
    }
}
