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
package org.apache.abdera.test.ext.opensearch.model;

import java.io.InputStream;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.IntegerElement;
import org.apache.abdera.ext.opensearch.model.Query;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

public class OpenSearchAtomTest extends XMLAssert {

    private static final int TOTAL_RESULTS = 47;
    private static final int START_INDEX = 1;
    private static final int ITEMS_PER_PAGE = 1;
    private static final String QUERY_TERMS = "some content";

    static {
        Map<String, String> nsContext = new HashMap<String, String>();
        nsContext.put(OpenSearchConstants.OS_PREFIX, OpenSearchConstants.OPENSEARCH_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(nsContext));
    }

    @Test
    public void testAtomResponseCreation() throws Exception {
        Feed feed = Abdera.getInstance().getFactory().newFeed();

        feed.setId("http://example.com/opensearch+example");
        feed.setTitle("An OpenSearch Example");
        feed.setUpdated(new Date());

        Query query = feed.addExtension(OpenSearchConstants.QUERY);
        query.setRole(Query.Role.REQUEST);
        query.setSearchTerms(QUERY_TERMS);

        IntegerElement totalResults = feed.addExtension(OpenSearchConstants.TOTAL_RESULTS);
        totalResults.setValue(TOTAL_RESULTS);
        IntegerElement itemsPerPage = feed.addExtension(OpenSearchConstants.ITEMS_PER_PAGE);
        itemsPerPage.setValue(ITEMS_PER_PAGE);
        IntegerElement startIndex = feed.addExtension(OpenSearchConstants.START_INDEX);
        startIndex.setValue(START_INDEX);

        StringWriter writer = new StringWriter();
        feed.writeTo(writer);

        String result = writer.toString();

        System.out.print(result);

        assertXpathEvaluatesTo(String.valueOf(TOTAL_RESULTS), "//os:totalResults", result);
        assertXpathEvaluatesTo(String.valueOf(ITEMS_PER_PAGE), "//os:itemsPerPage", result);
        assertXpathEvaluatesTo(String.valueOf(START_INDEX), "//os:startIndex", result);
        assertXpathEvaluatesTo(Query.Role.REQUEST.toString().toLowerCase(), "//os:Query/@role", result);
        assertXpathEvaluatesTo(QUERY_TERMS, "//os:Query/@searchTerms", result);
    }

    @Test
    public void testAtomResponseParsing() throws Exception {
        Parser parser = Abdera.getNewParser();

        InputStream stream = OpenSearchAtomTest.class.getResourceAsStream("/atomResponse.xml");
        Document<Element> doc = parser.parse(stream);

        IntegerElement tr = doc.getRoot().getFirstChild(OpenSearchConstants.TOTAL_RESULTS);
        assertNotNull(tr);
        assertEquals(47, tr.getValue());

        IntegerElement ipp = doc.getRoot().getFirstChild(OpenSearchConstants.ITEMS_PER_PAGE);
        assertNotNull(ipp);
        assertEquals(1, ipp.getValue());

        IntegerElement si = doc.getRoot().getFirstChild(OpenSearchConstants.START_INDEX);
        assertNotNull(si);
        assertEquals(1, si.getValue());

        Query q = doc.getRoot().getFirstChild(OpenSearchConstants.QUERY);
        assertNotNull(q);
        assertEquals(Query.Role.REQUEST, q.getRole());
        assertEquals(QUERY_TERMS, q.getSearchTerms());
    }

    @Test
    public void testFeedSimpleExtension() throws Exception {
        Feed feed = Abdera.getInstance().getFactory().newFeed();

        feed.setId("http://example.com/opensearch+example");
        feed.setTitle("An OpenSearch Example");
        feed.setUpdated(new Date());

        feed.addSimpleExtension(OpenSearchConstants.TOTAL_RESULTS, String.valueOf(TOTAL_RESULTS));
        feed.addSimpleExtension(OpenSearchConstants.ITEMS_PER_PAGE, String.valueOf(ITEMS_PER_PAGE));

        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String result = writer.toString();

        assertXpathEvaluatesTo(String.valueOf(TOTAL_RESULTS), "//os:totalResults", result);
        assertXpathEvaluatesTo(String.valueOf(ITEMS_PER_PAGE), "//os:itemsPerPage", result);

    }
}
