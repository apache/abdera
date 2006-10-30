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

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Element;

public class OpenSearchExtensionFactory 
  implements ExtensionFactory {

  @SuppressWarnings("unchecked")
  public <T extends Element> T getElementWrapper(Element internal) {
    QName qname = internal.getQName();
    if (qname.equals(OpenSearchConstants.QUERY) ||
        qname.equals(OpenSearchConstants.QUERY_V10)) {
      return (T)new Query(internal);
    } else if (qname.equals(OpenSearchConstants.ITEMS_PER_PAGE) ||
               qname.equals(OpenSearchConstants.START_INDEX) ||
               qname.equals(OpenSearchConstants.TOTAL_RESULTS) ||
               qname.equals(OpenSearchConstants.ITEMS_PER_PAGE_V10) ||
               qname.equals(OpenSearchConstants.START_INDEX_V10) ||
               qname.equals(OpenSearchConstants.TOTAL_RESULTS_V10)) {
      return (T)new IntegerElement(internal);
    } else {
      return (T)internal;
    }
  }

  public List<String> getNamespaces() {
    return java.util.Arrays.asList(
      new String[] {
        OpenSearchConstants.OPENSEARCH_NS, 
        OpenSearchConstants.OPENSEARCH_V10_NS});
  }

  public boolean handlesNamespace(String namespace) {
    return OpenSearchConstants.OPENSEARCH_NS.equals(namespace) ||
           OpenSearchConstants.OPENSEARCH_V10_NS.equals(namespace);
  }

}
