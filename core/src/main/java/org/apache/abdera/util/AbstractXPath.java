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
package org.apache.abdera.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.model.Base;
import org.apache.abdera.xpath.XPath;
import org.apache.abdera.xpath.XPathException;

public abstract class AbstractXPath 
  implements XPath {

  private Map<String,String> namespaces = null;
  
  public Map<String, String> getDefaultNamespaces() {
    if (namespaces == null) 
      namespaces = new HashMap<String,String>();
    namespaces.put(Constants.PREFIX, Constants.ATOM_NS);
    namespaces.put(Constants.APP_PREFIX, Constants.APP_NS);
    namespaces.put(Constants.CONTROL_PREFIX, Constants.CONTROL_NS);
    namespaces.put(Constants.THR_PREFIX, Constants.THR_NS);
    return namespaces;
  }

  public void setDefaultNamespaces(Map<String, String> namespaces) {
    this.namespaces = namespaces;
  }

  public List selectNodes(String path, Base base) throws XPathException {
    return selectNodes(path, base, getDefaultNamespaces());
  }

  public Object selectSingleNode(String path, Base base) throws XPathException {
    return selectSingleNode(path, base, getDefaultNamespaces());
  }

  public Object evaluate(String path, Base base) throws XPathException {
    return evaluate(path, base, getDefaultNamespaces());
  }

  public String valueOf(String path, Base base) throws XPathException {
    return valueOf(path, base, getDefaultNamespaces());
  }

  public boolean isTrue(String path, Base base) throws XPathException {
    return isTrue(path, base, getDefaultNamespaces());
  }

  public Number numericValueOf(String path, Base base) throws XPathException {
    return numericValueOf(path, base, getDefaultNamespaces());
  }

}
