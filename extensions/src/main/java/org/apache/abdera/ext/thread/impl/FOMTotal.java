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
package org.apache.abdera.ext.thread.impl;

import javax.xml.namespace.QName;

import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.ext.thread.Total;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMTotal extends FOMElement implements Total {

  private static final long serialVersionUID = 241599118592917827L;

  public FOMTotal() {
    super(ThreadConstants.THRTOTAL, null, (OMFactory)Factory.INSTANCE);
  }
  
  public FOMTotal(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMTotal(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMTotal( 
    OMContainer parent, 
    OMFactory factory)
      throws OMException {
    super(ThreadConstants.THRTOTAL, parent, factory);
  }

  public FOMTotal(
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }
  
  public int getValue() {
    return Integer.parseInt(getText());
  }

  public void setValue(int value) {
    setText(String.valueOf(value));
  }

}
