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

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.parser.stax.util.FOMElementIteratorWrapper;
import org.apache.abdera.parser.stax.util.FOMExtensionIterator;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMExtensibleElement 
  extends FOMElement 
  implements ExtensibleElement {

  private static final long serialVersionUID = -1652430686161947531L;

  public FOMExtensibleElement(QName qname) {
    super(qname);
  }
  
  protected FOMExtensibleElement(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  protected FOMExtensibleElement(
    QName qname, 
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(qname, parent, factory);
  }

  protected FOMExtensibleElement(
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }

  @SuppressWarnings("unchecked")
  public List<Element> getExtensions() {
    return new FOMList<Element>(new FOMExtensionIterator(this));
  }

  @SuppressWarnings("unchecked")
  public List<Element> getExtensions(String uri) {
    return new FOMList<Element>(new FOMExtensionIterator(this, uri));
  }

  @SuppressWarnings("unchecked")
  public <T extends Element>List<T> getExtensions(QName qname) {
    FOMFactory factory = (FOMFactory) this.getFactory();
    return new FOMList<T>(
      new FOMElementIteratorWrapper(
        factory,getChildrenWithName(qname)));
  }

  @SuppressWarnings("unchecked")
  public <T extends Element>T getExtension(QName qname) {
    FOMFactory factory = (FOMFactory) getFactory();
    T t = (T) this.getFirstChildWithName(qname);
    return (T) ((t != null) ? factory.getElementWrapper(t) : null);
  }
  
  public void addExtension(Element extension) {
    if (extension instanceof ElementWrapper) {
      ElementWrapper wrapper = (ElementWrapper) extension;
      extension = wrapper.getInternal();
    }
    QName qname = extension.getQName();
    String prefix = qname.getPrefix();
    if (prefix != null) {
      declareNS(qname.getNamespaceURI(), prefix);
    }
    addChild((OMElement)extension);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>T addExtension(QName qname) {
    FOMFactory fomfactory = (FOMFactory) factory;
    String prefix = qname.getPrefix();
    if (prefix != null) {
      declareNS(qname.getNamespaceURI(),prefix);
    }
    return (T)fomfactory.newExtensionElement(qname, this);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>T addExtension(String namespace, String localpart, String prefix) {
    if (prefix != null) {
      declareNS(namespace, prefix);
    }
    return (T)addExtension(new QName(namespace, localpart, prefix));
  }

  public Element addSimpleExtension(QName qname, String value) {
    FOMFactory fomfactory = (FOMFactory) factory;
    Element el = fomfactory.newElement(qname, this);
    el.setText(value);
    String prefix = qname.getPrefix();
    if (prefix != null) {
      declareNS(qname.getNamespaceURI(),prefix);
    }
    return el;
  }
  
  public Element addSimpleExtension(
    String namespace, 
    String localPart, 
    String prefix, 
    String value) {
      if (prefix != null) {
        declareNS(namespace,prefix);
      }
      return addSimpleExtension(
        new QName(
          namespace, 
          localPart, 
          prefix), 
        value);
  }
  
  public String getSimpleExtension(QName qname) {
    Element el  = getExtension(qname);
    return (el != null) ? el.getText() : null;
  }
  
  public String getSimpleExtension(
    String namespace, 
    String localPart, 
    String prefix) {
      return getSimpleExtension(
        new QName(
          namespace, 
          localPart, 
          prefix));
  }
  
  public void addExtensions(List<Element> extensions) {
    for (Element e : extensions) {
      addExtension(e);
    }
  }
  
  /**
   * Trick using Generics to find an extension element without 
   * having to pass in it's QName
   */ 
  @SuppressWarnings("unchecked")
  public <T extends Element> T getExtension(Class<T> _class) {
    T t = null;
    List<Element> extensions = getExtensions();
    for (Element ext : extensions) {
      if (_class.isAssignableFrom(ext.getClass())) {
        t = (T)ext;
        break;
      }
    }
    return t;
  }
}
