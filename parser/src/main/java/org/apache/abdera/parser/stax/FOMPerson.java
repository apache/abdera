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

import org.apache.abdera.model.IRI;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.StringElement;
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

  public StringElement getNameElement() {
    return (StringElement)getFirstChildWithName(NAME);
  }

  public void setNameElement(StringElement element) {
    if (element != null)
      _setChild(NAME, (OMElement)element);
    else 
      _removeElement(NAME, false);
  }

  public StringElement setName(String name) {
    if (name != null) {
      FOMFactory fomfactory = (FOMFactory) factory;
      StringElement el = fomfactory.newName(name, null);
      _setChild(NAME, (OMElement)el);
      return el;
    } else {
      _removeElement(NAME, false);
      return null;
    }
  }
  
  public String getName() {
    StringElement name = getNameElement();
    return (name != null) ? name.getValue() : null;
  }

  public StringElement getEmailElement() {
    return (StringElement)getFirstChildWithName(EMAIL);
  }

  public void setEmailElement(StringElement element) {
    if (element != null)
      _setChild(EMAIL, (OMElement)element);
    else 
      _removeElement(EMAIL, false);
  }

  public StringElement setEmail(String email) {
    if (email != null) {
      FOMFactory fomfactory = (FOMFactory) factory;
      StringElement el = fomfactory.newEmail(email, null);
      _setChild(EMAIL, (OMElement)el);
      return el;
    } else {
      _removeElement(EMAIL, false);
      return null;
    }
  }
  
  public String getEmail() {
    StringElement email = getEmailElement();
    return (email != null) ? email.getValue() : null;
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
