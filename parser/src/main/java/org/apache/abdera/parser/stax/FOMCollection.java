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

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMCollection 
  extends FOMExtensibleElement 
  implements Collection {

  private static final long serialVersionUID = -5291734055253987136L;

  public FOMCollection() {
    super(Constants.COLLECTION, null, (OMFactory)Factory.INSTANCE);
  }
  
  public FOMCollection(
    String title, 
    URI href, 
    String[] accepts) {
      this();
      setTitle(title);
      setHref(href);
      setAccept(accepts);
  }
  
  public FOMCollection(
    String title, 
    String href, 
    String[] accepts) 
      throws URISyntaxException {
    this();
    setTitle(title);
    setHref(href);
    setAccept(accepts);
  }
  
  public FOMCollection(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMCollection(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMCollection(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  public FOMCollection(
    OMContainer parent,
    OMFactory factory) {
      super(COLLECTION, parent, factory);
  }

  public FOMCollection(
    OMContainer parent,
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(COLLECTION, parent, factory, builder);
  }
  
  public String getTitle() {
    return _getAttributeValue(ATITLE);
  }

  public void setTitle(String title) {
    if (title != null)
      _setAttributeValue(ATITLE, title);
    else 
      _removeAttribute(ATITLE);
  }

  public URI getHref() throws URISyntaxException {
    return _getUriValue(_getAttributeValue(HREF));
  }

  public URI getResolvedHref() throws URISyntaxException {
    return _resolve(getResolvedBaseUri(), getHref());
  }
  
  public void setHref(URI href) {
    if (href != null)
      _setAttributeValue(HREF, _getStringValue(href));
    else 
      _removeAttribute(HREF);
  }

  public void setHref(String href) throws URISyntaxException {
    setHref((href != null) ? new URI(href) : null);
  }
  
  public String[] getAccept(){
    String accept = _getElementValue(ACCEPT);
    String[] list = accept.split(",");
    for (int n = 0; n < list.length; n++) {
      list[n] = list[n].trim();
    }
    return list;
  }

  public void setAccept(String[] mediaRanges) {
    if (mediaRanges != null) {
      String value = "";
      for (String type : mediaRanges) {
        if (value.length() > 0) value+=",";
        value += type;
      }
      _setElementValue(ACCEPT, value);
    } else {
      _removeElement(ACCEPT, false);
    }
  }

  public boolean accepts(String mediaType) {
    String[] accept = getAccept();
    for (String a : accept) {
      if (MimeTypeHelper.isMatch(a, mediaType)) return true;
    }
    return false;
  }

  public boolean accepts(MimeType mediaType) {
    return accepts(mediaType.toString());
  }

}
