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
package org.apache.abdera.ext.opensearch.impl;

import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.StartIndex;

import org.apache.abdera.parser.stax.FOMElement;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;

import javax.xml.namespace.QName;

public class FOMStartIndex extends FOMElement implements StartIndex {
  private static final long serialVersionUID = -8365709284324867565L;

  public FOMStartIndex()
  {
    super(OpenSearchConstants.START_INDEX);
  }

  public FOMStartIndex(String name,
                       OMNamespace namespace,
                       OMContainer parent,
                       OMFactory factory)
    throws OMException
  {
    super(name, namespace, parent, factory);
  }

  public FOMStartIndex(QName qname,
                       OMContainer parent,
                       OMFactory factory)
  {
    super(qname, parent, factory);
  }

  public FOMStartIndex(OMContainer parent,
                       OMFactory factory)
    throws OMException
  {
    super(OpenSearchConstants.START_INDEX, parent, factory);
  }

  public FOMStartIndex(QName qname,
                       OMContainer parent,
                       OMFactory factory,
                       OMXMLParserWrapper builder)
    throws OMException
  {
    super(qname, parent, factory, builder);
  }

  public int getIndex()
  {
    return Integer.parseInt(getText());
  }

  public void setIndex(int idx)
  {
    setText(String.valueOf(idx));
  }
}
