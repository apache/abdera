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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.activation.MimeType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;


public class FOMDocument<T extends Element> 
  extends OMDocumentImpl 
  implements Document<T> {

  private static final long serialVersionUID = -3255339511063344662L;
  protected URI base = null;
  protected MimeType contentType = null;
  protected Date lastModified = null;

  public FOMDocument() {
    super();
  }

  public FOMDocument(
    OMElement documentElement, 
    OMXMLParserWrapper parserWrapper, 
    OMFactory factory) {
      super(documentElement, parserWrapper, factory);
  }

  public FOMDocument(
    OMElement documentElement, 
    OMXMLParserWrapper parserWrapper) {
      super(documentElement, parserWrapper);
  }

  public FOMDocument(
    OMFactory factory) {
      super(factory);
  }

  public FOMDocument(
    OMXMLParserWrapper parserWrapper, 
    OMFactory factory) {
      super(parserWrapper, factory);
  }

  public FOMDocument(
    OMXMLParserWrapper parserWrapper) {
      super(parserWrapper);
  }

  @SuppressWarnings("unchecked")
  public T getRoot() {  
    return (T)this.getOMDocumentElement();
  }

  public void setRoot(T root) {
    this.setOMDocumentElement((OMElement) root);
  }

  public URI getBaseUri() {
    return base;
  }

  public void setBaseUri(URI base) {
    this.base = base;
  }
  
  public void setBaseUri(String base) throws URISyntaxException {
    setBaseUri((base != null) ? new URI(base) : null);
  }

  public void writeTo(OutputStream out) throws IOException {
    try {    
      OMOutputFormat outputFormat = new OMOutputFormat();
      outputFormat.setCharSetEncoding(this.getCharsetEncoding());
      MTOMXMLStreamWriter omwriter = new MTOMXMLStreamWriter(out, outputFormat);
      internalSerialize(omwriter, true);
      omwriter.flush();
    } catch (XMLStreamException e) {
      throw new FOMException(e);
    }
  }

  public void writeTo(java.io.Writer writer) throws IOException {
    try {
      setComplete(true);      
      OMOutputFormat outputFormat = new OMOutputFormat();
      outputFormat.setCharSetEncoding(this.getCharsetEncoding());
      XMLStreamWriter streamwriter = 
        XMLOutputFactory.newInstance().createXMLStreamWriter(
          writer);
      MTOMXMLStreamWriter omwriter = new MTOMXMLStreamWriter(streamwriter);
      omwriter.setOutputFormat(outputFormat);
      this.internalSerializeAndConsume(omwriter);
      omwriter.flush();
    } catch (XMLStreamException e) {
      throw new FOMException(e);
    }
  }
  
  public MimeType getContentType() {
    return contentType;
  }
  
  public void setContentType(MimeType contentType) {
    this.contentType = contentType;
  }
  
  public Date getLastModified() {
    return this.lastModified;
  }
  
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  
  @SuppressWarnings("unchecked")
  public Object clone() {
    T rootClone = (T)getRoot().clone();
    Document<T> doc = ((FOMFactory)factory).newDocument();
    doc.setRoot(rootClone);
    return doc;
  }

  public String getCharset() {
    return this.getCharsetEncoding();
  }

  public void setCharset(String charset) {
    this.setCharsetEncoding(charset);
  }

  public Factory getFactory() {
    return (Factory) this.factory;
  }
  
}
