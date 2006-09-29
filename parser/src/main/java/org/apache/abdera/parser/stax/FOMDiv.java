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

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.abdera.model.Div;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.iri.IRI;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMDiv 
  extends FOMExtensibleElement 
  implements Div {

  private static final long serialVersionUID = -2319449893405850433L;

  public FOMDiv() {
    super(Constants.DIV);
  }
  
  public FOMDiv(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMDiv(
    QName qname, 
    OMContainer parent, 
    OMFactory factory)
      throws OMException {
    super(qname, parent, factory);
  }

  public FOMDiv(
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }

  public String[] getXhtmlClass() {
    String _class = getAttributeValue(CLASS);
    String[] classes = null;
    if (_class != null) {
      classes = _class.split(" ");
    }
    return classes;
  }

  public String getId() {
    return getAttributeValue(AID);
  }

  public String getTitle() {
    return getAttributeValue(ATITLE);
  }

  public void setId(String id) {
    if (id != null)
      setAttributeValue(AID, id);
    else 
      removeAttribute(AID);
  }
  
  public void setTitle(String title) {
    if (title != null)
      setAttributeValue(ATITLE, title);
    else 
      removeAttribute(ATITLE);
  }
  
  public void setXhtmlClass(String[] classes) {
    if (classes != null) {
      StringBuffer val = new StringBuffer();
      for (String s : classes) {
        if (s.length() > 0)
          val.append(" ");
        val.append(s);
      }
      setAttributeValue(CLASS, val.toString());
    } else removeAttribute(CLASS);
  }

  public String getValue() {
    return getInternalValue();
  }

  public void setValue(String value) {
    _removeAllChildren();
    if (value != null) {
      IRI baseUri = null;
      value = "<div xmlns=\"" + XHTML_NS + "\">" + value + "</div>";
      OMElement element = null;
      try {
        baseUri = getResolvedBaseUri();
        element = (OMElement) _parse(value, baseUri);
      } catch (Exception e) {}
      for (Iterator i = element.getChildren(); i.hasNext();) {
        this.addChild((OMNode)i.next());
      }
    }
  }

  protected String getInternalValue() {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      XMLStreamWriter writer = 
        XMLOutputFactory.newInstance().createXMLStreamWriter(out);
      writer.writeStartElement("");
      for (Iterator nodes = this.getChildren(); nodes.hasNext();) {
        OMNode node = (OMNode) nodes.next();
        node.serialize(writer);
      }
      writer.writeEndElement();
      return out.toString().substring(2);
    } catch (Exception e) {}
    return "";
  }
  
}
