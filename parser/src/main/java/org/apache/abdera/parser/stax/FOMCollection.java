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

import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMCollection 
  extends FOMExtensibleElement 
  implements Collection {

  private static final String[] EMPTY = new String[0];
  private static final String[] ENTRY = new String[] {"entry"};
  
  private static final long serialVersionUID = -5291734055253987136L;

  public FOMCollection() {
    super(Constants.COLLECTION);
  }
  
  public FOMCollection(
    String title, 
    String href, 
    String[] accepts) 
      throws IRISyntaxException {
    this();
    setTitle(title);
    setHref(href);
    setAccept(accepts);
  }
  
  protected FOMCollection(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  protected FOMCollection(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  protected FOMCollection(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  protected FOMCollection(
    OMContainer parent,
    OMFactory factory) {
      super(COLLECTION, parent, factory);
  }

  protected FOMCollection(
    OMContainer parent,
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(COLLECTION, parent, factory, builder);
  }
  
  public String getTitle() {
    Text title = this.getFirstChild(TITLE);
    return (title != null) ? title.getValue() : null;
  }

  private Text setTitle(String title, Text.Type type) {
    FOMFactory fomfactory = (FOMFactory) factory;
    Text text = fomfactory.newText(PREFIXED_TITLE,type);
    text.setValue(title);
    this._setChild(PREFIXED_TITLE, (OMElement) text);
    return text;
  }
  
  public Text setTitle(String title) {
    return setTitle(title, Text.Type.TEXT);
  }

  public Text setTitleAsHtml(String title) {
    return setTitle(title, Text.Type.HTML);
  }
  
  public Text setTitleAsXHtml(String title) {
    return setTitle(title, Text.Type.XHTML);
  }
  
  public Text getTitleElement() {
    return getFirstChild(TITLE);
  }
  
  public IRI getHref() throws IRISyntaxException {
    return _getUriValue(getAttributeValue(HREF));
  }

  public IRI getResolvedHref() throws IRISyntaxException {
    return _resolve(getResolvedBaseUri(), getHref());
  }
  
  public void setHref(String href) throws IRISyntaxException {
    if (href != null)
      setAttributeValue(HREF, (new IRI(href).toString()));
    else 
      removeAttribute(HREF);
  }
  
  public Element getAcceptElement() {
    return getFirstChild(ACCEPT);
  }
  
  public String[] getAccept(){
    String accept = _getElementValue(ACCEPT);
    if (accept == null) return ENTRY;
    else accept = accept.trim();
    if (accept.length() == 0) return EMPTY;
    String[] list = accept.split("\\s*,\\s*(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))");
    return MimeTypeHelper.condense(list);
  }

  public void setAccept(String... mediaRanges) {
    if (mediaRanges != null) {
      mediaRanges = MimeTypeHelper.condense(mediaRanges);
      StringBuffer value = new StringBuffer();
      for (String type : mediaRanges) {
        if (value.length() > 0)
          value.append(",");
        value.append(type);
      }
      _setElementValue(ACCEPT, value.toString());
    } else {
      _removeChildren(ACCEPT, false);
    }
  }

  public boolean accepts(String mediaType) {
    String[] accept = getAccept();
    for (String a : accept) {
      if (mediaType.equalsIgnoreCase("entry") && 
          a.equalsIgnoreCase("entry")) return true;
      if (MimeTypeHelper.isMatch(a, mediaType)) return true;
    }
    return false;
  }

  public boolean accepts(MimeType mediaType) {
    return accepts(mediaType.toString());
  }

  public Categories addCategories() {
    return ((FOMFactory)factory).newCategories(this);
  }
  
  public void addCategories(Categories categories) {
    addChild((OMElement)categories);
  }

  public Categories addCategories(
    String href) 
      throws IRISyntaxException {
    Categories cats = ((FOMFactory)factory).newCategories();
    cats.setHref(href);
    addCategories(cats);
    return cats;
  }
  
  public Categories addCategories(
    List<Category> categories, 
    boolean fixed, 
    String scheme) 
      throws IRISyntaxException {
      Categories cats = ((FOMFactory)factory).newCategories();
      cats.setFixed(fixed);
      if (scheme != null) cats.setScheme(scheme);
      if (categories != null) {
        for (Category category : categories) {
          cats.addCategory(category);
        }
      }
      addCategories(cats);
      return cats;
  }

  public List<Categories> getCategories() {
    return _getChildrenAsSet(CATEGORIES);
  }

}
