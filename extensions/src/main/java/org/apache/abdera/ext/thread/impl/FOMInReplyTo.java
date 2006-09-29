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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.ext.thread.InReplyTo;
import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.util.iri.IRI;
import org.apache.abdera.util.iri.IRISyntaxException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public class FOMInReplyTo 
  extends FOMElement 
  implements InReplyTo {

  private static final long serialVersionUID = 7805672826003392693L;

  public FOMInReplyTo() {
    super(ThreadConstants.IN_REPLY_TO);
  }
  
  public FOMInReplyTo(IRI ref) {
    this();
    setRef(ref);
  }
  
  public FOMInReplyTo(String ref) throws IRISyntaxException {
    this();
    setRef(ref);
  }
  
  public FOMInReplyTo(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMInReplyTo(
    QName qname,
    OMContainer parent,
    OMFactory factory) {
      super(qname, parent, factory);
  }
  
  public FOMInReplyTo( 
    OMContainer parent, 
    OMFactory factory)
      throws OMException {
    super(ThreadConstants.IN_REPLY_TO, parent, factory);
  }

  public FOMInReplyTo(
    QName qname, 
    OMContainer parent, 
    OMFactory factory,
    OMXMLParserWrapper builder) 
      throws OMException {
    super(qname, parent, factory, builder);
  }

  public IRI getRef() throws IRISyntaxException {
    return _getUriValue(getAttributeValue(ThreadConstants.THRREF));
  }

  public void setRef(IRI ref) {
    this.setAttributeValue(ThreadConstants.THRREF, ref.toString());
  }
  
  public void setRef(String ref) throws IRISyntaxException {
    setRef(new IRI(ref));
  }

  public IRI getResolvedHref() throws IRISyntaxException {
    return _resolve(getResolvedBaseUri(), getHref());
  }
  
  public IRI getHref() throws IRISyntaxException {
    return _getUriValue(getAttributeValue(HREF));
  }

  public void setHref(IRI ref) {
    this.setAttributeValue(HREF, ref.toString());
  }

  public void setHref(String ref) throws IRISyntaxException {
    setHref(new IRI(ref));
  }
  
  public MimeType getMimeType() throws MimeTypeParseException {
    MimeType type = null;
    String value = getAttributeValue(TYPE);
    if (value != null) {
      type = new MimeType(value);
    }
    return type;
  }

  public void setMimeType(MimeType mimeType) {
    this.setAttributeValue(TYPE, mimeType.toString());
  }
  
  public void setMimeType(String mimeType) throws MimeTypeParseException {
    setMimeType(new MimeType(mimeType));
  }
  
  public IRI getResolvedSource() throws IRISyntaxException {
    return _resolve(getResolvedBaseUri(), getSource());
  }
  
  public IRI getSource() throws IRISyntaxException {
    return _getUriValue(getAttributeValue(ThreadConstants.THRSOURCE));
  }

  public void setSource(IRI source) {
    this.setAttributeValue(ThreadConstants.THRSOURCE, source.toString());
  }
  
  public void setSource(String source) throws IRISyntaxException {
    setSource(new IRI(source));
  }

}
