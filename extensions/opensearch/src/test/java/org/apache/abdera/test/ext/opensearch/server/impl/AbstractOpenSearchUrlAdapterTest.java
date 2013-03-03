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
package org.apache.abdera.test.ext.opensearch.server.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.IntegerElement;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.ext.opensearch.server.impl.AbstractOpenSearchUrlAdapter;
import org.apache.abdera.ext.opensearch.server.impl.SimpleOpenSearchUrlInfo;
import org.apache.abdera.ext.opensearch.server.processors.OpenSearchTargetTypes;
import org.apache.abdera.ext.opensearch.server.processors.OpenSearchUrlRequestProcessor;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.test.ext.opensearch.server.AbstractOpenSearchServerTest;
import org.apache.abdera.test.ext.opensearch.server.JettyServer;
import org.apache.axiom.testutils.PortAllocator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractOpenSearchUrlAdapterTest extends AbstractOpenSearchServerTest {

    private int port;
    private JettyServer server;
    private OpenSearchInfo osInfo;
    private OpenSearchUrlRequestProcessor osUrlProcessor;

    @Before
    public void setUp() throws Exception {
        port = PortAllocator.allocatePort();
        server = new JettyServer(port);
        
        this.osInfo = this.createOpenSearchInfo();
        ((SimpleOpenSearchUrlInfo)osInfo.getUrls()[0]).setOpenSearchUrlAdapter(new TestingOpenSearchUrlAdapter());

        this.osUrlProcessor = new OpenSearchUrlRequestProcessor();
        this.osUrlProcessor.setOpenSearchInfo(this.osInfo);

        Map<TargetType, RequestProcessor> processors = new HashMap<TargetType, RequestProcessor>();
        processors.put(OpenSearchTargetTypes.OPENSEARCH_URL, osUrlProcessor);

        DefaultWorkspaceManager wsManager = new DefaultWorkspaceManager();
        wsManager.setWorkspaces(new LinkedList<WorkspaceInfo>());

        RouteManager routeManager = new RouteManager();
        routeManager.addRoute("service", "/", TargetType.TYPE_SERVICE).addRoute("feed",
                                                                                "/atom/:collection",
                                                                                TargetType.TYPE_COLLECTION)
            .addRoute("entry", "/atom/:collection/:entry", TargetType.TYPE_ENTRY)
            .addRoute("categories", "/atom/:collection/:entry;categories", TargetType.TYPE_CATEGORIES)
            .addRoute("osSearch1", "/search1", OpenSearchTargetTypes.OPENSEARCH_URL);

        DefaultProvider provider = new DefaultProvider("/");
        provider.setWorkspaceManager(wsManager);
        provider.setTargetResolver(routeManager);
        provider.setTargetBuilder(routeManager);
        provider.addRequestProcessors(processors);

        this.server.start(provider);
    }

    @After
    public void tearDown() throws Exception {
        this.server.stop();
    }

    @Test
    public void testOpenSearchFeedResponse() throws Exception {
        AbderaClient client = new AbderaClient();
        ClientResponse response = client.get("http://localhost:" + port + "/search1?q=test1&c=1");
        assertEquals(200, response.getStatus());

        Document<Feed> feedDoc = response.getDocument();
        Feed feed = feedDoc.getRoot();
        assertEquals(TestingOpenSearchUrlAdapter.OS_FEED_ID, feed.getId().toString());
        assertEquals(TestingOpenSearchUrlAdapter.OS_FEED_TITLE, feed.getTitle());
        assertEquals(TestingOpenSearchUrlAdapter.OS_FEED_AUTHOR, feed.getAuthor().getName());
        assertEquals(2, ((IntegerElement)feed.getExtension(OpenSearchConstants.TOTAL_RESULTS)).getValue());
        assertEquals(2, feed.getEntries().size());
        assertNotNull(feed.getEntry(TestingOpenSearchUrlAdapter.SEARCH_RESULT_1_ID));
        assertNotNull(feed.getEntry(TestingOpenSearchUrlAdapter.SEARCH_RESULT_2_ID));
        assertEquals(TestingOpenSearchUrlAdapter.SEARCH_RESULT_1_TITLE, feed
            .getEntry(TestingOpenSearchUrlAdapter.SEARCH_RESULT_1_ID).getTitle());
        assertEquals(TestingOpenSearchUrlAdapter.SEARCH_RESULT_1_DESC, feed
            .getEntry(TestingOpenSearchUrlAdapter.SEARCH_RESULT_1_ID).getContent());
        assertEquals(TestingOpenSearchUrlAdapter.SEARCH_RESULT_2_TITLE, feed
            .getEntry(TestingOpenSearchUrlAdapter.SEARCH_RESULT_2_ID).getTitle());
        assertEquals(TestingOpenSearchUrlAdapter.SEARCH_RESULT_2_DESC, feed
            .getEntry(TestingOpenSearchUrlAdapter.SEARCH_RESULT_2_ID).getContent());
    }

    private class TestingOpenSearchUrlAdapter extends AbstractOpenSearchUrlAdapter<SearchResult> {

        public static final String OS_FEED_AUTHOR = "Sergio Bossa";
        public static final String OS_FEED_ID = "http://www.acme.org/feed/id";
        public static final String OS_FEED_TITLE = "Feed Title";
        public static final String SEARCH_RESULT_1_DESC = "Search Result 1 description";
        public static final String SEARCH_RESULT_1_ID = "urn:1";
        public static final String SEARCH_RESULT_1_TITLE = "Search Result 1 title";
        public static final String SEARCH_RESULT_2_DESC = "Search Result 2 description";
        public static final String SEARCH_RESULT_2_ID = "urn:2";
        public static final String SEARCH_RESULT_2_TITLE = "Search Result 2 title";

        protected String getOpenSearchFeedId(RequestContext request) {
            return OS_FEED_ID;
        }

        protected String getOpenSearchFeedTitle(RequestContext request) {
            return OS_FEED_TITLE;
        }

        protected Person getOpenSearchFeedAuthor(RequestContext request) {
            Person p = request.getAbdera().getFactory().newAuthor();
            p.setName(OS_FEED_AUTHOR);
            return p;
        }

        protected Date getOpenSearchFeedUpdatedDate(RequestContext request) {
            return new Date();
        }

        protected List<SearchResult> doSearch(RequestContext request, Map<String, String> parameters) {
            List<SearchResult> result = new LinkedList<SearchResult>();
            SearchResult sr1 = new SearchResult(SEARCH_RESULT_1_ID, SEARCH_RESULT_1_TITLE, SEARCH_RESULT_1_DESC);
            SearchResult sr2 = new SearchResult(SEARCH_RESULT_2_ID, SEARCH_RESULT_2_TITLE, SEARCH_RESULT_2_DESC);
            result.add(sr1);
            result.add(sr2);
            return result;
        }

        protected void fillEntry(Entry entry, SearchResult entity) {
            entry.setId(entity.getId());
            entry.setTitle(entity.getTitle());
            entry.setContent(entity.getDescription());
        }
    }

    private class SearchResult {

        private String id;
        private String title;
        private String description;

        public SearchResult(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
