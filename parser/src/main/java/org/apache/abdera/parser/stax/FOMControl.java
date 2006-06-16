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

import org.apache.abdera.model.Control;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMControl
  extends FOMExtensibleElement
  implements Control {

  private static final long serialVersionUID = -3816493378953555206L;

  public FOMControl(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMControl(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMControl(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  public FOMControl(
    OMContainer parent, 
    OMFactory factory, 
    OMXMLParserWrapper builder) 
      throws OMException {
    super(CONTROL, parent, factory, builder);
  }

  public FOMControl(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(CONTROL, parent, factory);
  }

  public boolean isDraft() {
    String value = _getElementValue(DRAFT);
    return (value != null && YES.equalsIgnoreCase(value));
  }

  public void setDraft(boolean draft) {
    _setElementValue(DRAFT, (draft) ? YES:NO);
  }
  
  public void unsetDraft() {
    OMElement el = getFirstChildWithName(DRAFT);
    if (el != null)
      el.discard();
  }

}
