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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMWorkspace
  extends FOMExtensibleElement 
  implements Workspace {

  private static final long serialVersionUID = -421749865550509424L; 

  public FOMWorkspace() {
    super(Constants.WORKSPACE, null, (OMFactory)Factory.INSTANCE);
  }
  
  public FOMWorkspace(String title) {
    this();
    setTitle(title);
  }
  
  public FOMWorkspace(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMWorkspace(
    QName qname,
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(qname, parent, factory);
  }

  public FOMWorkspace(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }
  
  public FOMWorkspace(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(WORKSPACE, parent, factory);
  }

  public FOMWorkspace(
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(WORKSPACE, parent, factory, builder);
  }
  
  public String getTitle() {
    return _getAttributeValue(ATITLE);
  }

  public void setTitle(String title) {
    _setAttributeValue(ATITLE, title);
  }

  public List<Collection> getCollections() {
    return _getChildrenAsSet(COLLECTION);
  }

  public Collection getCollection(String title) {
    List<Collection> cols = getCollections();
    Collection col = null;
    for (Collection c : cols) {
      if (c.getTitle().equals(title)) {
        col = c;
        break;
      }
    }
    return col;
  }

  public void setCollection(List<Collection> collections) {
    _setChildrenFromSet(COLLECTION, collections);
  }

  public void addCollection(Collection collection) {
    addChild((OMElement)collection);
  }

  public Collection addCollection(String title, URI href) {
    FOMFactory fomfactory = (FOMFactory) factory;
    Collection collection = fomfactory.newCollection(this);
    collection.setTitle(title);
    collection.setHref(href);
    return collection;
  }
  
  public Collection addCollection(String title, String href) throws URISyntaxException {
    return addCollection(title, (href != null) ? new URI(href) : null);
  }
}
