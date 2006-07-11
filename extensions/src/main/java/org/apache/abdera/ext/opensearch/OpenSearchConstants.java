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

public final class OpenSearchConstants {
  private OpenSearchConstants() {}

  public static final String OPENSEARCH_NS = "http://a9.com/-/spec/opensearchrss/1.0/";

  public static final String TOTAL_RESULTS_LN  = "totalResults";
  public static final String ITEMS_PER_PAGE_LN = "itemsPerPage";
  public static final String START_INDEX_LN    = "startIndex";

  public static final QName TOTAL_RESULTS  = new QName(OPENSEARCH_NS, TOTAL_RESULTS_LN);
  public static final QName ITEMS_PER_PAGE = new QName(OPENSEARCH_NS, ITEMS_PER_PAGE_LN);
  public static final QName START_INDEX    = new QName(OPENSEARCH_NS, START_INDEX_LN);
}
