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
package org.apache.abdera.test.ext.opensearch.server;

import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.Query;
import org.apache.abdera.ext.opensearch.server.OpenSearchInfo;
import org.apache.abdera.ext.opensearch.server.impl.SimpleOpenSearchInfo;
import org.apache.abdera.ext.opensearch.server.impl.SimpleOpenSearchQueryInfo;
import org.apache.abdera.ext.opensearch.server.impl.SimpleOpenSearchUrlInfo;
import org.apache.abdera.ext.opensearch.server.impl.SimpleOpenSearchUrlParameterInfo;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

public abstract class AbstractOpenSearchServerTest extends XMLAssert {

    protected static final String DESCRIPTION = "This is a description";
    protected static final String SHORT_NAME = "This is a short name";
    protected static final String TAG1 = "FirstTag";
    protected static final String TAG2 = "SecondTag";
    protected static final String TAGS = TAG1 + " " + TAG2;
    protected static final String SEARCH_PATH_1 = "/search1";
    protected static final String SEARCH_PATH_2 = "/search2";
    protected static final String TEMPLATE_PARAM_1_NAME = "q";
    protected static final String TEMPLATE_PARAM_1_VALUE = "searchTerms";
    protected static final String TEMPLATE_PARAM_2_NAME = "c";
    protected static final String TEMPLATE_PARAM_2_VALUE = "count";
    protected static final String QUERY_TERMS = "term1 term2";

    static {
        Map<String, String> nsContext = new HashMap<String, String>();
        nsContext.put(OpenSearchConstants.OS_PREFIX, OpenSearchConstants.OPENSEARCH_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(nsContext));
    }

    protected OpenSearchInfo createOpenSearchInfo() throws Exception {
        SimpleOpenSearchInfo osInfo = new SimpleOpenSearchInfo();
        SimpleOpenSearchQueryInfo osQueryInfo = new SimpleOpenSearchQueryInfo();
        SimpleOpenSearchUrlInfo osUrlInfo1 = new SimpleOpenSearchUrlInfo();
        SimpleOpenSearchUrlInfo osUrlInfo2 = new SimpleOpenSearchUrlInfo();
        SimpleOpenSearchUrlParameterInfo osParamInfo1 = new SimpleOpenSearchUrlParameterInfo();
        SimpleOpenSearchUrlParameterInfo osParamInfo2 = new SimpleOpenSearchUrlParameterInfo();

        osInfo.setShortName(SHORT_NAME);
        osInfo.setDescription(DESCRIPTION);
        osInfo.setTags(TAG1, TAG2);

        osQueryInfo.setRole(Query.Role.EXAMPLE);
        osQueryInfo.setSearchTerms(QUERY_TERMS);

        osInfo.setQueries(osQueryInfo);

        osUrlInfo1.setSearchPath(SEARCH_PATH_1);
        osUrlInfo2.setSearchPath(SEARCH_PATH_2);

        osParamInfo1.setName(TEMPLATE_PARAM_1_NAME);
        osParamInfo1.setValue(TEMPLATE_PARAM_1_VALUE);

        osParamInfo2.setName(TEMPLATE_PARAM_2_NAME);
        osParamInfo2.setValue(TEMPLATE_PARAM_2_VALUE);
        osParamInfo2.setOptional(true);

        osUrlInfo1.setSearchParameters(osParamInfo1, osParamInfo2);
        osUrlInfo2.setSearchParameters(osParamInfo1, osParamInfo2);

        osInfo.setUrls(osUrlInfo1, osUrlInfo2);

        return osInfo;
    }
}
