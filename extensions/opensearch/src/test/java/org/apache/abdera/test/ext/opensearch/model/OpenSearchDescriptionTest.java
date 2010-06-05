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
import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;
import org.apache.abdera.ext.opensearch.model.Query;
import org.apache.abdera.ext.opensearch.model.StringElement;
import org.apache.abdera.ext.opensearch.model.Url;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.Parser;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

public class OpenSearchDescriptionTest extends XMLAssert {

    private static final String DESCRIPTION = "This is a description";
    private static final String SHORT_NAME = "This is a short name";
    private static final String TAG1 = "FirstTag";
    private static final String TAG2 = "SecondTag";
    private static final String TAGS = TAG1 + " " + TAG2;
    private static final String URL_TEMPLATE = "http://example.com/?q={searchTerms}";
    private static final String URL_TYPE = "application/atom+xml";
    private static final String QUERY_TERMS = "term1 term2";

    static {
        Map<String, String> nsContext = new HashMap<String, String>();
        nsContext.put(OpenSearchConstants.OS_PREFIX, OpenSearchConstants.OPENSEARCH_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(nsContext));
    }

    @Test
    public void testOpenSearchDescriptionDocumentCreation() throws Exception {
        OpenSearchDescription document = new OpenSearchDescription(Abdera.getInstance());

        document.setShortName(SHORT_NAME);
        document.setDescription(DESCRIPTION);
        document.setTags(TAG1, TAG2);

        Url url = new Url(Abdera.getInstance());
        url.setType(URL_TYPE);
        url.setTemplate(URL_TEMPLATE);

        Query query = new Query(Abdera.getInstance());
        query.setRole(Query.Role.EXAMPLE);
        query.setSearchTerms(QUERY_TERMS);

        document.addUrls(url);
        document.addQueries(query);

        StringWriter writer = new StringWriter();
        document.writeTo(writer);

        String result = writer.toString();

        System.out.print(result);

        assertXpathEvaluatesTo(SHORT_NAME, "/os:OpenSearchDescription/os:ShortName", result);
        assertXpathEvaluatesTo(DESCRIPTION, "/os:OpenSearchDescription/os:Description", result);
        assertXpathEvaluatesTo(TAGS, "/os:OpenSearchDescription/os:Tags", result);
        assertXpathEvaluatesTo(URL_TYPE, "/os:OpenSearchDescription/os:Url/@type", result);
        assertXpathEvaluatesTo(URL_TEMPLATE, "/os:OpenSearchDescription/os:Url/@template", result);
        assertXpathEvaluatesTo(Query.Role.EXAMPLE.toString().toLowerCase(),
                               "/os:OpenSearchDescription/os:Query/@role",
                               result);
        assertXpathEvaluatesTo(QUERY_TERMS, "/os:OpenSearchDescription/os:Query/@searchTerms", result);
        assertXpathEvaluatesTo(new Integer(1).toString(), "/os:OpenSearchDescription/os:Url/@indexOffset", result);
        assertXpathEvaluatesTo(new Integer(1).toString(), "/os:OpenSearchDescription/os:Url/@pageOffset", result);
    }

    @Test
    public void testOpenSearchDescriptionDocumentParsing() throws Exception {
        Parser parser = Abdera.getNewParser();

        InputStream stream = OpenSearchAtomTest.class.getResourceAsStream("/opensearchDescription.xml");
        Document<Element> doc = parser.parse(stream);

        StringElement shortName = doc.getRoot().getFirstChild(OpenSearchConstants.SHORT_NAME);
        assertNotNull(shortName);
        assertEquals(SHORT_NAME, shortName.getValue());

        StringElement description = doc.getRoot().getFirstChild(OpenSearchConstants.DESCRIPTION);
        assertNotNull(description);
        assertEquals(DESCRIPTION, description.getValue());

        StringElement tags = doc.getRoot().getFirstChild(OpenSearchConstants.TAGS);
        assertNotNull(tags);
        assertEquals(TAGS, tags.getValue());

        Query q = doc.getRoot().getFirstChild(OpenSearchConstants.QUERY);
        assertNotNull(q);
        assertEquals(Query.Role.EXAMPLE, q.getRole());
        assertEquals(QUERY_TERMS, q.getSearchTerms());

        Url u = doc.getRoot().getFirstChild(OpenSearchConstants.URL);
        assertNotNull(u);
        assertEquals(URL_TYPE, u.getType());
        assertEquals(URL_TEMPLATE, u.getTemplate());
    }
}
