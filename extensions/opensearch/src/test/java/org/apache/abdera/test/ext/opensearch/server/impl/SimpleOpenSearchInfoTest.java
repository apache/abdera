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

import java.io.StringWriter;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;
import org.apache.abdera.ext.opensearch.model.Query;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.test.ext.opensearch.server.AbstractOpenSearchServerTest;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class SimpleOpenSearchInfoTest extends AbstractOpenSearchServerTest {

    private OpenSearchInfo osInfo;

    @Before
    public void setUp() throws Exception {
        this.osInfo = this.createOpenSearchInfo();
    }

    @Test
    public void testOpenSearchDescriptionCreationFromOpenSearchInfo() throws Exception {
        RequestContext requestMock = createMock(RequestContext.class);
        expect(requestMock.getAbdera()).andReturn(Abdera.getInstance()).anyTimes();
        expect(requestMock.getBaseUri()).andReturn(new IRI("http://www.acme.org/")).anyTimes();

        replay(requestMock);

        OpenSearchDescription description = this.osInfo.asOpenSearchDescriptionElement(requestMock);
        StringWriter writer = new StringWriter();
        description.writeTo(writer);

        String result = writer.toString();
        System.out.print(result);

        assertXpathEvaluatesTo(SHORT_NAME, "/os:OpenSearchDescription/os:ShortName", result);
        assertXpathEvaluatesTo(DESCRIPTION, "/os:OpenSearchDescription/os:Description", result);
        assertXpathEvaluatesTo(TAGS, "/os:OpenSearchDescription/os:Tags", result);
        assertXpathEvaluatesTo(Query.Role.EXAMPLE.toString().toLowerCase(),
                               "/os:OpenSearchDescription/os:Query/@role",
                               result);
        assertXpathEvaluatesTo(QUERY_TERMS, "/os:OpenSearchDescription/os:Query/@searchTerms", result);
        assertXpathEvaluatesTo("application/atom+xml", "/os:OpenSearchDescription/os:Url[1]/@type", result);
        assertXpathEvaluatesTo("http://www.acme.org/search1?q={searchTerms}&c={count?}",
                               "/os:OpenSearchDescription/os:Url[1]/@template",
                               result);
        assertXpathEvaluatesTo("application/atom+xml", "/os:OpenSearchDescription/os:Url[2]/@type", result);
        assertXpathEvaluatesTo("http://www.acme.org/search2?q={searchTerms}&c={count?}",
                               "/os:OpenSearchDescription/os:Url[2]/@template",
                               result);
    }
}
