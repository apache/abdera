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

import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMCategories 
  extends FOMExtensibleElement 
  implements Categories {

  private static final long serialVersionUID = 5480273546375102411L;

  public FOMCategories() {
    super(CATEGORIES, new FOMDocument(), new FOMFactory());
    init();
  }
  
  public FOMCategories(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
    init();
  }
  
  public FOMCategories(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
      init();
  }
  
  public FOMCategories(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  public FOMCategories(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(CATEGORIES, parent, factory);
    init();
  }

  public FOMCategories(
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(CATEGORIES, parent, factory, builder);
  }

  private void init() {
    this.declareNamespace(ATOM_NS, "atom");
  }
  
  public void addCategory(Category category) {
    addChild((OMElement)category);
  }

  public Category addCategory(String term) {
    FOMFactory factory = (FOMFactory) this.factory;
    Category category = factory.newCategory(this);
    category.setTerm(term);
    return category;
  }

  public Category addCategory(
    String scheme, 
    String term, 
    String label)
      throws URISyntaxException {
    FOMFactory factory = (FOMFactory) this.factory;
    Category category = factory.newCategory(this);
    category.setTerm(term);
    category.setScheme(scheme);
    category.setLabel(label);
    return category;    

  }

  public List<Category> getCategories() {
    return _getChildrenAsSet(CATEGORY);
  }

  public List<Category> getCategories(String scheme) throws URISyntaxException {
    return FOMHelper.getCategories(this, scheme);
  }

  public java.net.URI getScheme() throws URISyntaxException {
    String value = getAttributeValue(SCHEME);
    return (value != null) ? new URI(value) : null;
  }

  public boolean isFixed() {
    String value = getAttributeValue(FIXED);
    return (value != null && value.equals(YES));
  }

  public void setFixed(boolean fixed) {
    if (fixed && !isFixed())
      setAttributeValue(FIXED, YES);
    else if (!fixed && isFixed())
      removeAttribute(FIXED);
  }

  public void setScheme(String scheme) throws URISyntaxException {
    if (scheme != null)
      setAttributeValue(SCHEME, new URI(scheme).toString());
    else 
      removeAttribute(SCHEME);
  }
  
  public URI getHref() throws URISyntaxException {
    return _getUriValue(getAttributeValue(HREF));
  }

  public URI getResolvedHref() throws URISyntaxException {
    return _resolve(getResolvedBaseUri(), getHref());
  }
  
  public void setHref(String href) throws URISyntaxException {
    if (href != null)
      setAttributeValue(HREF, (new URI(href)).toString());
    else 
      removeAttribute(HREF);
  }
  


}
