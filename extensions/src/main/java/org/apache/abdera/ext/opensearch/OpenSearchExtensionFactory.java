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

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.stax.FOMExtensionFactory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Base;
import org.apache.abdera.ext.opensearch.impl.FOMTotalResults;
import org.apache.abdera.ext.opensearch.impl.FOMItemsPerPage;
import org.apache.abdera.ext.opensearch.impl.FOMStartIndex;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

public class OpenSearchExtensionFactory implements ExtensionFactory, FOMExtensionFactory {
  public boolean handlesNamespace(String ns)
  {
    return OpenSearchConstants.OPENSEARCH_NS.equals(ns);
  }

  public List<String> getNamespaces()
  {
    List<String> lst = new ArrayList<String>();
    lst.add(OpenSearchConstants.OPENSEARCH_NS);
    return lst;
  }

  @SuppressWarnings("unchecked")
  public <T extends Element> T newExtensionElement(QName qname,
                                                   Base base,
                                                   Factory factory)
  {
    return (T) newExtensionElement(qname, base, factory, null);
  }

  @SuppressWarnings("unchecked")
  public <T extends Element> T newExtensionElement(QName qname,
                                                   Base base,
                                                   Factory factory,
                                                   OMXMLParserWrapper parserWrapper)
  {
    OMContainer cbase = (OMContainer) base;
    OMFactory cfactory = (OMFactory) factory;
    if (OpenSearchConstants.TOTAL_RESULTS.equals(qname)) {
      return (parserWrapper != null) ?
        (T) new FOMTotalResults(qname, cbase, cfactory, parserWrapper) :
        (T) new FOMTotalResults(qname, cbase, cfactory);
    }
    else if (OpenSearchConstants.ITEMS_PER_PAGE.equals(qname)) {
      return (parserWrapper != null) ?
        (T) new FOMItemsPerPage(qname, cbase, cfactory, parserWrapper) :
        (T) new FOMItemsPerPage(qname, cbase, cfactory);
    }
    else if (OpenSearchConstants.START_INDEX.equals(qname)) {
      return (parserWrapper != null) ?
        (T) new FOMStartIndex(qname, cbase, cfactory, parserWrapper) :
        (T) new FOMStartIndex(qname, cbase, cfactory);
    }
    return null;
  }
}
