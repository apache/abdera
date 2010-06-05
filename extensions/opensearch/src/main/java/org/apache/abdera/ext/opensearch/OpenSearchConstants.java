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
package org.apache.abdera.ext.opensearch;

import javax.xml.namespace.QName;

/**
 * Simple container of Open Search XML model constants.
 */
public interface OpenSearchConstants {

    public static final String OPENSEARCH_DESCRIPTION_CONTENT_TYPE = "application/opensearchdescription+xml";
    public static final String OPENSEARCH_NS = "http://a9.com/-/spec/opensearch/1.1/";
    public static final String OS_PREFIX = "os";
    public static final String TOTAL_RESULTS_LN = "totalResults";
    public static final String ITEMS_PER_PAGE_LN = "itemsPerPage";
    public static final String START_INDEX_LN = "startIndex";
    public static final String QUERY_LN = "Query";
    public static final String QUERY_ROLE_LN = "role";
    public static final String QUERY_TITLE_LN = "title";
    public static final String QUERY_TOTALRESULTS_LN = "totalResult";
    public static final String QUERY_SEARCHTERMS_LN = "searchTerms";
    public static final String QUERY_COUNT_LN = "count";
    public static final String QUERY_STARTINDEX_LN = "startIndex";
    public static final String QUERY_STARTPAGE_LN = "startPage";
    public static final String QUERY_LANGUAGE_LN = "language";
    public static final String QUERY_INPUTENCODING_LN = "inputEncoding";
    public static final String QUERY_OUTPUTENCODING_LN = "outputEncoding";
    public static final QName TOTAL_RESULTS = new QName(OPENSEARCH_NS, TOTAL_RESULTS_LN, OS_PREFIX);
    public static final QName ITEMS_PER_PAGE = new QName(OPENSEARCH_NS, ITEMS_PER_PAGE_LN, OS_PREFIX);
    public static final QName START_INDEX = new QName(OPENSEARCH_NS, START_INDEX_LN, OS_PREFIX);
    public static final QName QUERY = new QName(OPENSEARCH_NS, QUERY_LN, OS_PREFIX);
    public static final String OPENSEARCH_DESCRIPTION_LN = "OpenSearchDescription";
    public static final String DESCRIPTION_LN = "Description";
    public static final String SHORT_NAME_LN = "ShortName";
    public static final String TAGS_LN = "Tags";
    public static final String URL_LN = "Url";
    public static final String URL_TYPE_LN = "type";
    public static final String URL_TEMPLATE_LN = "template";
    public static final String URL_INDEXOFFSET_LN = "indexOffset";
    public static final String URL_PAGEOFFSET_LN = "pageOffset";
    public static final QName OPENSEARCH_DESCRIPTION = new QName(OPENSEARCH_NS, OPENSEARCH_DESCRIPTION_LN, OS_PREFIX);
    public static final QName DESCRIPTION = new QName(OPENSEARCH_NS, DESCRIPTION_LN, OS_PREFIX);
    public static final QName SHORT_NAME = new QName(OPENSEARCH_NS, SHORT_NAME_LN, OS_PREFIX);
    public static final QName TAGS = new QName(OPENSEARCH_NS, TAGS_LN, OS_PREFIX);
    public static final QName URL = new QName(OPENSEARCH_NS, URL_LN, OS_PREFIX);
}
