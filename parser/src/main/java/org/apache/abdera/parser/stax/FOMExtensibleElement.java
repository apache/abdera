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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.model.StringElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;


public abstract class FOMExtensibleElement 
  extends FOMElement 
  implements ExtensibleElement {

  public FOMExtensibleElement(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMExtensibleElement(
    QName qname, 
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(qname, parent, factory);
  }

  public FOMExtensibleElement(
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }

  @SuppressWarnings("unchecked")
  protected <E extends Element>List<E> _getExtensionChildrenAsList() {
    List<E> set = new ArrayList<E>();
    for (Iterator i = getChildren();i.hasNext();) {
      OMNode e = (OMNode) i.next();
      if (e instanceof ExtensionElement &&
          !((OMElement)e).getQName().getNamespaceURI().equals(
            this.getQName().getNamespaceURI())) {
        set.add((E) e);
      }
    }
    return set;
  }
  
  public List<ExtensionElement> getExtensions() {
    return _getExtensionChildrenAsList();
  }

  public List<ExtensionElement> getExtensions(String uri) {
    List<ExtensionElement> matching = new ArrayList<ExtensionElement>();
    for (Iterator i = this.getChildElements(); i.hasNext();) {
      OMElement e = (OMElement) i.next();
      if ((uri == null)?
            e.getQName().getNamespaceURI() == null: 
            e.getQName().getNamespaceURI().equals(uri)) {
        matching.add((ExtensionElement) e);
      }
    }
    return matching;
  }

  public List<ExtensionElement> getExtensions(QName qname) {
    List<ExtensionElement> matching = new ArrayList<ExtensionElement>();
    for (Iterator i = this.getChildrenWithName(qname); i.hasNext();) {
      matching.add((ExtensionElement) i.next());
    }
    return matching;
  }

  public ExtensionElement getExtension(QName qname) {
    return (ExtensionElement) this.getFirstChildWithName(qname);
  }
  
  public void addExtension(ExtensionElement extension) {
    addChild((OMElement)extension);
  }
  
  public ExtensionElement addExtension(QName qname) {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newExtensionElement(qname, this);
  }
  
  public ExtensionElement addExtension(String namespace, String localpart, String prefix) {
    return addExtension(new QName(namespace, localpart, prefix));
  }

  public StringElement addSimpleExtension(QName qname, String value) {
    FOMFactory fomfactory = (FOMFactory) factory;
    return fomfactory.newStringElement(qname, value, this);
  }
  
  public StringElement addSimpleExtension(
    String namespace, 
    String localPart, 
    String prefix, 
    String value) {
      return addSimpleExtension(
        new QName(
          namespace, 
          localPart, 
          prefix), 
        value);
  }
  
  public void addExtensions(List<ExtensionElement> extensions) {
    for (ExtensionElement e : extensions) {
      addExtension(e);
    }
  }
  
  /**
   * Trick using Generics to find an extension element without 
   * having to pass in it's QName
   */ 
  @SuppressWarnings("unchecked")
  public <T extends ExtensionElement> T getExtension(Class<T> _class) {
    T t = null;
    List<ExtensionElement> extensions = getExtensions();
    for (ExtensionElement ext : extensions) {
      if (_class.isAssignableFrom(ext.getClass())) {
        t = (T)ext;
        break;
      }
    }
    return t;
  }
}
