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
package org.apache.abdera.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * ParseFilter's determine which elements and attributes are acceptable
 * within a parsed document.  They are set via the ParserOptions.setParseFilter
 * method.
 */
public abstract class ParseFilter implements Cloneable {
  
  private List<QName> qnames = null;
  private Map<QName,List<QName>> attributes = null;
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  public void add(QName qname) {
    if (qnames == null) qnames = new ArrayList<QName>();
    if (!contains(qname)) qnames.add(qname);
  }

  public boolean contains(QName qname) {
    if (qnames == null) qnames = new ArrayList<QName>();
    return qnames.contains(qname);
  }

  public void addAttribute(QName parent, QName attribute) {
    if (attributes == null) attributes = new HashMap<QName,List<QName>>();
    if (attributes.containsKey(parent)) {
      List<QName> attrs = attributes.get(parent);
      if (!attrs.contains(attribute)) attrs.add(attribute);
    } else {
      List<QName> attrs = new ArrayList<QName>();
      attrs.add(attribute);
      attributes.put(parent, attrs);
    }
  }

  public boolean containsAttribute(QName qname, QName attribute) {
    if (attributes == null) attributes = new HashMap<QName,List<QName>>();
    if (attributes.containsKey(qname)) {
      List<QName> attrs = attributes.get(qname);
      return attrs.contains(attribute);
    } else {
      return false;
    }
  }

  public abstract boolean acceptable(QName qname);
  public abstract boolean acceptableAttribute(QName qname, QName attribute);
}
