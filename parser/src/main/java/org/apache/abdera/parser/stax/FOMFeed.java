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
package org.apache.abdera.parser.stax;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Source;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMFeed 
  extends FOMSource 
  implements Feed {
  
  private static final long serialVersionUID = 4552921210185524535L;
  
  public FOMFeed(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMFeed(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMFeed(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  public FOMFeed(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(FEED, parent, factory);
  }

  public FOMFeed(
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(FEED, parent, factory, builder);
  }
  
  public List<Entry> getEntries() {
    return _getChildrenAsSet(ENTRY);
  }

  public void setEntries(List<Entry> entries) {
    _setChildrenFromSet(ENTRY, entries);
  }

  public void addEntry(Entry entry) {
    addChild((OMElement)entry);
  }

  public Entry addEntry() {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newEntry(this);
  }
  
  public void insertEntry(Entry entry) {
    OMElement el = getFirstChildWithName(ENTRY);
    if (el == null) {
      addEntry(entry);
    } else {
      el.insertSiblingBefore((OMElement)entry);
    }
  }
  
  public Entry insertEntry() {
    FOMFactory fomfactory = (FOMFactory) factory;
    Entry entry = fomfactory.newEntry((Feed)null);
    insertEntry(entry);
    return entry;
  }

  public Source getAsSource() {
    FOMSource source = (FOMSource) ((FOMFactory)factory).newSource(null);
    OMElement _source = source;
    for (Iterator i = this.getChildElements(); i.hasNext();) {
      OMElement _child = (OMElement) i.next();
      if (!_child.getQName().equals(ENTRY)) {
        _source.addChild(_child.cloneOMElement());
      }
    }
    return source;
  }
 
}
