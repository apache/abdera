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

import java.net.URI;
import java.net.URISyntaxException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.ext.thread.InReplyTo;
import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.stax.FOMElement;
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
    super(ThreadConstants.IN_REPLY_TO, null, (OMFactory)Factory.INSTANCE);
  }
  
  public FOMInReplyTo(URI ref) {
    this();
    setRef(ref);
  }
  
  public FOMInReplyTo(String ref) throws URISyntaxException {
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

  public URI getRef() throws URISyntaxException {
    return _getUriValue(getAttributeValue(ThreadConstants.THRREF));
  }

  public void setRef(URI ref) {
    this.setAttributeValue(ThreadConstants.THRREF, ref.toString());
  }
  
  public void setRef(String ref) throws URISyntaxException {
    setRef(new URI(ref));
  }

  public URI getResolvedHref() throws URISyntaxException {
    return _resolve(getResolvedBaseUri(), getHref());
  }
  
  public URI getHref() throws URISyntaxException {
    return _getUriValue(getAttributeValue(HREF));
  }

  public void setHref(URI ref) {
    this.setAttributeValue(HREF, ref.toString());
  }

  public void setHref(String ref) throws URISyntaxException {
    setHref(new URI(ref));
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
  
  public URI getResolvedSource() throws URISyntaxException {
    return _resolve(getResolvedBaseUri(), getSource());
  }
  
  public URI getSource() throws URISyntaxException {
    return _getUriValue(getAttributeValue(ThreadConstants.THRSOURCE));
  }

  public void setSource(URI source) {
    this.setAttributeValue(ThreadConstants.THRSOURCE, source.toString());
  }
  
  public void setSource(String source) throws URISyntaxException {
    setSource(new URI(source));
  }

}
