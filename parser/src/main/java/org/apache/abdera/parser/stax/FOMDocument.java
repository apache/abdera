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
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;

import javax.activation.MimeType;
import javax.xml.stream.XMLStreamException;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.util.StAXUtils;


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
    writeTo(new OutputStreamWriter(out));
  }

  public void writeTo(java.io.Writer writer) throws IOException {
    try {      
      OMOutputFormat outputFormat = new OMOutputFormat();
      if (this.getCharsetEncoding() != null)
        outputFormat.setCharSetEncoding(this.getCharsetEncoding());
      MTOMXMLStreamWriter omwriter = 
        new MTOMXMLStreamWriter(
          StAXUtils.createXMLStreamWriter(writer));
      omwriter.setOutputFormat(outputFormat);
      this.internalSerialize(omwriter);
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
    Document<T> doc = ((FOMFactory)factory).newDocument();
    OMDocument omdoc = (OMDocument) doc;
    for (Iterator i = getChildren(); i.hasNext();) {
      OMNode node = (OMNode) i.next();
      switch(node.getType()) {
        case OMNode.COMMENT_NODE:
          OMComment comment = (OMComment) node;
          factory.createOMComment(omdoc, comment.getValue());
          break;
        case OMNode.DTD_NODE:
          OMDocType doctype = (OMDocType) node;
          factory.createOMDocType(omdoc, doctype.getValue());
          break;
        case OMNode.ELEMENT_NODE:
          Element el = (Element) node;
          omdoc.addChild((OMNode) el.clone());
          break;
        case OMNode.PI_NODE:
          OMProcessingInstruction pi = (OMProcessingInstruction) node;
          factory.createOMProcessingInstruction(omdoc, pi.getTarget(), pi.getValue());
          break;
      }
    }
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
