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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.Query;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.ext.opensearch.server.processors.OpenSearchDescriptionRequestProcessor;
import org.apache.abdera.ext.opensearch.server.processors.OpenSearchTargetTypes;
import org.apache.abdera.model.Document;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.server.RequestProcessor;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.test.ext.opensearch.server.AbstractOpenSearchServerTest;
import org.apache.abdera.test.ext.opensearch.server.JettyServer;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.testutils.PortAllocator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OpenSearchDescriptionRequestProcessorTest extends AbstractOpenSearchServerTest {

    private int port;
    private JettyServer server;
    private OpenSearchInfo osInfo;
    private OpenSearchDescriptionRequestProcessor osRequestProcessor;

    @Before
    public void setUp() throws Exception {
        port = PortAllocator.allocatePort();
        server = new JettyServer(port);
        
        this.osInfo = this.createOpenSearchInfo();
        this.osRequestProcessor = new OpenSearchDescriptionRequestProcessor();
        this.osRequestProcessor.setOpenSearchInfo(osInfo);

        Map<TargetType, RequestProcessor> processors = new HashMap<TargetType, RequestProcessor>();
        processors.put(OpenSearchTargetTypes.OPENSEARCH_DESCRIPTION, osRequestProcessor);

        DefaultWorkspaceManager wsManager = new DefaultWorkspaceManager();
        wsManager.setWorkspaces(new LinkedList<WorkspaceInfo>());

        RouteManager routeManager = new RouteManager();
        routeManager.addRoute("service", "/", TargetType.TYPE_SERVICE).addRoute("feed",
                                                                                "/atom/:collection",
                                                                                TargetType.TYPE_COLLECTION)
            .addRoute("entry", "/atom/:collection/:entry", TargetType.TYPE_ENTRY)
            .addRoute("categories", "/atom/:collection/:entry;categories", TargetType.TYPE_CATEGORIES)
            .addRoute("osDescription", "/search", OpenSearchTargetTypes.OPENSEARCH_DESCRIPTION);

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
    public void testOpenSearchDescriptionRequestProcessorOutput() throws Exception {
        AbderaClient client = new AbderaClient();
        ClientResponse resp = client.get("http://localhost:" + port + "/search");

        assertNotNull(resp);
        assertEquals(ResponseType.SUCCESS, resp.getType());
        assertTrue(MimeTypeHelper.isMatch(resp.getContentType().toString(),
                                          OpenSearchConstants.OPENSEARCH_DESCRIPTION_CONTENT_TYPE));

        Document doc = resp.getDocument();
        StringWriter writer = new StringWriter();
        doc.writeTo(writer);

        String result = writer.toString();
        System.out.println(result);

        assertXpathEvaluatesTo(SHORT_NAME, "/os:OpenSearchDescription/os:ShortName", result);
        assertXpathEvaluatesTo(DESCRIPTION, "/os:OpenSearchDescription/os:Description", result);
        assertXpathEvaluatesTo(TAGS, "/os:OpenSearchDescription/os:Tags", result);
        assertXpathEvaluatesTo(Query.Role.EXAMPLE.toString().toLowerCase(),
                               "/os:OpenSearchDescription/os:Query/@role",
                               result);
        assertXpathEvaluatesTo(QUERY_TERMS, "/os:OpenSearchDescription/os:Query/@searchTerms", result);
        assertXpathEvaluatesTo("application/atom+xml", "/os:OpenSearchDescription/os:Url[1]/@type", result);
        assertXpathEvaluatesTo("http://localhost:" + port + "/search1?q={searchTerms}&c={count?}",
                               "/os:OpenSearchDescription/os:Url[1]/@template",
                               result);
        assertXpathEvaluatesTo("application/atom+xml", "/os:OpenSearchDescription/os:Url[2]/@type", result);
        assertXpathEvaluatesTo("http://localhost:" + port + "/search2?q={searchTerms}&c={count?}",
                               "/os:OpenSearchDescription/os:Url[2]/@template",
                               result);
    }
}
