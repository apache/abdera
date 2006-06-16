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

import org.apache.abdera.model.Generator;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMGenerator 
  extends FOMStringElement 
  implements Generator {
  
  private static final long serialVersionUID = -8441971633807437976L;

  public FOMGenerator(
      String name,
      OMNamespace namespace,
      OMContainer parent,
      OMFactory factory)
        throws OMException {
      super(name, namespace, parent, factory);
    }
  
  public FOMGenerator(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMGenerator(
    QName qname,
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(qname, parent, factory, builder);
  }
  
  public FOMGenerator(
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(GENERATOR, parent, factory);
  }

  public FOMGenerator(
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(GENERATOR, parent, factory, builder);
  }
  
  public URI getUri() throws URISyntaxException {
    String value = _getAttributeValue(AURI);
    return (value != null) ? new URI(value) : null;
}

  public URI getResolvedUri() throws URISyntaxException {
    return _resolve(getResolvedBaseUri(), getUri());
  }
  
  public void setUri(URI uri) {
    _setAttributeValue(AURI, (uri != null) ? uri.toString() : null);
  }

  public void setUri(String uri) throws URISyntaxException {
    setUri((uri != null) ? new URI(uri) : null);
  }
  
  public String getVersion() {
    return _getAttributeValue(VERSION);
  }

  public void setVersion(String version) {
    _setAttributeValue(VERSION, version);
  }

}
