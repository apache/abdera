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

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.IRI;
import org.apache.abdera.model.Person;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMPerson
  extends FOMExtensibleElement 
  implements Person {

  private static final long serialVersionUID = 2147684807662492625L; 
  
  public FOMPerson(QName qname) { 
    super(qname, null, (OMFactory)Factory.INSTANCE);
  }
  
  public FOMPerson(
    QName qname, 
    String name) {
      this(qname);
      setName(name);
  }
  
  public FOMPerson(
    QName qname, 
    String name, 
    String email, 
    URI uri) {
      this(qname);
      setName(name);
      setEmail(email);
      setUri(uri);
  }

  public FOMPerson(
    QName qname, 
    String name, 
    String email, 
    String uri) 
      throws URISyntaxException {
    this(qname);
    setName(name);
    setEmail(email);
    setUri(uri);
  }
  
  public FOMPerson(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMPerson(
    QName qname, 
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(qname, parent, factory);
  }

  public FOMPerson(
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
    setBuilder(builder);
    done = false;
  }

  public Element getNameElement() {
    return (Element)getFirstChildWithName(NAME);
  }

  public void setNameElement(Element element) {
    if (element != null)
      _setChild(NAME, (OMElement)element);
    else 
      _removeElement(NAME, false);
  }

  public Element setName(String name) {
    if (name != null) {
      FOMFactory fomfactory = (FOMFactory) factory;
      Element el = fomfactory.newName(name, null);
      _setChild(NAME, (OMElement)el);
      return el;
    } else {
      _removeElement(NAME, false);
      return null;
    }
  }
  
  public String getName() {
    Element name = getNameElement();
    return (name != null) ? name.getText() : null;
  }

  public Element getEmailElement() {
    return (Element)getFirstChildWithName(EMAIL);
  }

  public void setEmailElement(Element element) {
    if (element != null)
      _setChild(EMAIL, (OMElement)element);
    else 
      _removeElement(EMAIL, false);
  }

  public Element setEmail(String email) {
    if (email != null) {
      FOMFactory fomfactory = (FOMFactory) factory;
      Element el = fomfactory.newEmail(email, null);
      _setChild(EMAIL, (OMElement)el);
      return el;
    } else {
      _removeElement(EMAIL, false);
      return null;
    }
  }
  
  public String getEmail() {
    Element email = getEmailElement();
    return (email != null) ? email.getText() : null;
  }

  public IRI getUriElement() {
    return (IRI)getFirstChildWithName(URI);
  }

  public void setUriElement(IRI uri) {
    if (uri != null) 
      _setChild(URI, (OMElement)uri);
    else 
      _removeElement(URI, false);
  }

  public IRI setUri(URI uri) {
    if (uri != null) {
      FOMFactory fomfactory = (FOMFactory) factory;
      IRI el = fomfactory.newUri(uri, null);
      _setChild(URI, (OMElement)el);
      return el;
    } else {
      _removeElement(URI, false);
      return null;
    }
  }
  
  public IRI setUri(String uri) throws URISyntaxException {
    if (uri != null) {
      FOMFactory fomfactory = (FOMFactory) factory;
      IRI el = fomfactory.newUri(uri, null);
      _setChild(URI, (OMElement)el);
      return el;
    } else {
      _removeElement(URI, false);
      return null;
    }
  }
  
  public URI getUri() throws URISyntaxException {
    IRI iri = getUriElement();
    return (iri != null) ? iri.getValue() : null;
  }
}
