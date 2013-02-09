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
package org.apache.abdera.test.ext.opensearch.server.processors;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.ext.opensearch.server.OpenSearchUrlAdapter;
import org.apache.abdera.ext.opensearch.server.impl.SimpleOpenSearchUrlInfo;
import org.apache.abdera.ext.opensearch.server.processors.OpenSearchTargetTypes;
import org.apache.abdera.ext.opensearch.server.processors.OpenSearchUrlRequestProcessor;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.context.SimpleResponseContext;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.test.ext.opensearch.server.AbstractOpenSearchServerTest;
import org.apache.abdera.test.ext.opensearch.server.JettyServer;
import org.apache.axiom.testutils.PortAllocator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OpenSearchUrlRequestProcessorTest extends AbstractOpenSearchServerTest {

    private int port;
    private JettyServer server;
    private OpenSearchInfo osInfo;
    private OpenSearchUrlRequestProcessor osUrlProcessor;

    @Before
    public void setUp() throws Exception {
        port = PortAllocator.allocatePort();
        server = new JettyServer(port);
        
        this.osInfo = this.createOpenSearchInfo();
        ((SimpleOpenSearchUrlInfo)osInfo.getUrls()[0]).setOpenSearchUrlAdapter(new TestingOpenSearchUrlAdapter1());
        ((SimpleOpenSearchUrlInfo)osInfo.getUrls()[1]).setOpenSearchUrlAdapter(new TestingOpenSearchUrlAdapter2());

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
            .addRoute("osSearch1", "/search1", OpenSearchTargetTypes.OPENSEARCH_URL)
            .addRoute("osSearch2", "/search2", OpenSearchTargetTypes.OPENSEARCH_URL)
            .addRoute("osSearch3", "/search3", OpenSearchTargetTypes.OPENSEARCH_URL);

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
    public void testProcessSuccessfully() throws Exception {
        AbderaClient client = new AbderaClient();
        ClientResponse response = null;
        // Test with first adapter:
        response = client.get("http://localhost:" + port + "/search1?q=test1&c=1");
        assertEquals(200, response.getStatus());
        // Test with second adapter:
        client.get("http://localhost:" + port + "/search2?q=test2&c=1");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testProcessFailsBecauseOfNoAdapterFound() throws Exception {
        AbderaClient client = new AbderaClient();
        ClientResponse response = null;
        // No adapter found for this Open Search url:
        response = client.get("http://localhost:" + port + "/search3?q=test1&c=1");
        assertEquals(404, response.getStatus());
    }

    private class TestingOpenSearchUrlAdapter1 implements OpenSearchUrlAdapter {

        public ResponseContext search(RequestContext request, Map<String, String> parameters) {
            assertEquals(SEARCH_PATH_1, request.getTargetPath().substring(0, request.getTargetPath().indexOf("?")));
            assertNotNull(parameters.get(TEMPLATE_PARAM_1_NAME));
            assertEquals("test1", parameters.get(TEMPLATE_PARAM_1_NAME));
            assertNotNull(parameters.get(TEMPLATE_PARAM_2_NAME));
            assertEquals("1", parameters.get(TEMPLATE_PARAM_2_NAME));
            return new SimpleResponseContext() {

                protected void writeEntity(Writer writer) throws IOException {
                }

                public boolean hasEntity() {
                    return false;
                }
            }.setStatus(200);
        }
    }

    private class TestingOpenSearchUrlAdapter2 implements OpenSearchUrlAdapter {

        public ResponseContext search(RequestContext request, Map<String, String> parameters) {
            assertEquals(SEARCH_PATH_2, request.getTargetPath().substring(0, request.getTargetPath().indexOf("?")));
            assertNotNull(parameters.get(TEMPLATE_PARAM_1_NAME));
            assertEquals("test2", parameters.get(TEMPLATE_PARAM_1_NAME));
            assertNotNull(parameters.get(TEMPLATE_PARAM_2_NAME));
            assertEquals("1", parameters.get(TEMPLATE_PARAM_2_NAME));
            return new SimpleResponseContext() {

                protected void writeEntity(Writer writer) throws IOException {
                }

                public boolean hasEntity() {
                    return false;
                }
            }.setStatus(200);
        }
    }
}
