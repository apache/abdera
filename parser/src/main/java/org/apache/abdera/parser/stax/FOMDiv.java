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

import javax.xml.namespace.QName;

import org.apache.abdera.model.Div;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMDiv 
  extends FOMExtensibleElement 
  implements Div {

  private static final long serialVersionUID = -2319449893405850433L;

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
    String _class = _getAttributeValue(CLASS);
    String[] classes = null;
    if (_class != null) {
      classes = _class.split(" ");
    }
    return classes;
  }

  public String getId() {
    return _getAttributeValue(AID);
  }

  public String getTitle() {
    return _getAttributeValue(ATITLE);
  }

  public void setId(String id) {
    if (id != null)
      _setAttributeValue(AID, id);
    else 
      _removeAttribute(AID);
  }
  
  public void setTitle(String title) {
    if (title != null)
      _setAttributeValue(ATITLE, title);
    else 
      _removeAttribute(ATITLE);
  }
  
  public void setXhtmlClass(String[] classes) {
    if (classes != null) {
      String val = "";
      for (String s : classes) {
        if (s.length() > 0) val += " ";
        val += s;
      }
      _setAttributeValue(CLASS, val);
    } else _removeAttribute(CLASS);
  }

  public String getValue() {
    return getText();
  }

  public void setValue(String value) {
    if (value != null)
      setText(value);
    else 
      _removeAllChildren();
  }

}
