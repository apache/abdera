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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Feed;

public class OpenSearchV11Helper {

  public static final String OPENSEARCH_NS = "http://a9.com/-/spec/opensearchrss/1.1/";

  public static final String TOTAL_RESULTS_LN  = "totalResults";
  public static final String ITEMS_PER_PAGE_LN = "itemsPerPage";
  public static final String START_INDEX_LN    = "startIndex";
  public static final String QUERY_LN          = "Query";

  public static final String OS_PREFIX = "os";
  
  public static final QName TOTAL_RESULTS  = new QName(OPENSEARCH_NS, TOTAL_RESULTS_LN, OS_PREFIX);
  public static final QName ITEMS_PER_PAGE = new QName(OPENSEARCH_NS, ITEMS_PER_PAGE_LN, OS_PREFIX);
  public static final QName START_INDEX    = new QName(OPENSEARCH_NS, START_INDEX_LN, OS_PREFIX);
  public static final QName QUERY          = new QName(OPENSEARCH_NS, QUERY_LN, OS_PREFIX);
  
  public static void setTotalResults(Feed feed, int totalResults) {
    if (totalResults < 0) throw new IllegalArgumentException();
    feed.declareNS(OpenSearchV11Helper.OPENSEARCH_NS, OS_PREFIX);
    Element el = feed.getExtension(TOTAL_RESULTS);
    if (totalResults != -1) {
      if (el != null) el.setText(String.valueOf(totalResults));
      else feed.addSimpleExtension(
        TOTAL_RESULTS, 
        String.valueOf(totalResults));
    } else {
      if (el != null) el.discard();
    }
  }
  
  public static int getTotalResults(Feed feed) {
    String val = feed.getSimpleExtension(TOTAL_RESULTS);
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public static void setItemsPerPage(Feed feed, int items) {
    if (items < 0) throw new IllegalArgumentException();
    feed.declareNS(OpenSearchV11Helper.OPENSEARCH_NS, OS_PREFIX);
    Element el = feed.getExtension(ITEMS_PER_PAGE);
    if (items != -1) {
      if (el != null) el.setText(String.valueOf(items));
      else feed.addSimpleExtension(
        ITEMS_PER_PAGE, 
        String.valueOf(items));
    } else {
      if (el != null) el.discard();
    }
  }
  
  public static int getItemsPerPage(Feed feed) {
    String val = feed.getSimpleExtension(ITEMS_PER_PAGE);
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public static void setStartIndex(Feed feed, int startIndex) {
    if (startIndex < 0) throw new IllegalArgumentException();
    feed.declareNS(OpenSearchV11Helper.OPENSEARCH_NS, OS_PREFIX);
    Element el = feed.getExtension(START_INDEX);
    if (startIndex != -1) {
      if (el != null) el.setText(String.valueOf(startIndex));
      else feed.addSimpleExtension(
        START_INDEX, 
        String.valueOf(startIndex));
    } else {
      if (el != null) el.discard();
    }
  }
  
  public static int getStartIndex(Feed feed) {
    String val = feed.getSimpleExtension(START_INDEX);
    return (val != null) ? Integer.parseInt(val) : -1;
  }
  
  public static Query addQuery(Feed feed) {
    return new Query(feed);
  }
  
  public static Query[] getQueries(Feed feed) {
    List<Element> els = feed.getExtensions(QUERY);
    Query[] queries = new Query[els.size()];
    int n = 0;
    for (Element el : els) {
      queries[n++] = new Query(el);
    }
    return queries;
  }
}
