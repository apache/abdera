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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.stream.XMLStreamException;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.lang.Lang;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.XmlUtil;
import org.apache.abdera.util.XmlUtil.XMLVersion;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterOptions;
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

@SuppressWarnings("unchecked")
public class FOMDocument<T extends Element> 
  extends OMDocumentImpl 
  implements Document<T> {

  private static final long serialVersionUID = -3255339511063344662L;
  protected IRI base = null;
  protected MimeType contentType = null;
  protected Date lastModified = null;
  protected EntityTag etag = null;
  protected String language = null;
  protected String slug = null;
  protected boolean preserve = true;

  public FOMDocument() {
    super();
  }

  protected FOMDocument(
    OMElement documentElement, 
    OMXMLParserWrapper parserWrapper, 
    OMFactory factory) {
      super(documentElement, parserWrapper, factory);
  }

  protected FOMDocument(
    OMElement documentElement, 
    OMXMLParserWrapper parserWrapper) {
      super(documentElement, parserWrapper);
  }

  protected FOMDocument(
    OMFactory factory) {
      super(factory);
  }

  protected FOMDocument(
    OMXMLParserWrapper parserWrapper, 
    OMFactory factory) {
      super(parserWrapper, factory);
  }

  protected FOMDocument(
    OMXMLParserWrapper parserWrapper) {
      super(parserWrapper);
  }

  public T getRoot() {  
    FOMFactory factory = (FOMFactory) getFactory();
    return (T) factory.getElementWrapper((T) this.getOMDocumentElement());
  }

  public void setRoot(T root) {
    if (root instanceof OMElement) {
      this.setOMDocumentElement((OMElement) root);
    } else if (root instanceof ElementWrapper) {
      this.setOMDocumentElement((OMElement) ((ElementWrapper)root).getInternal());
    }
  }

  public IRI getBaseUri() {
    return base;
  }

  public void setBaseUri(String base) {
    this.base = new IRI(base);
  }

  public void writeTo(
    OutputStream out, 
    WriterOptions options) 
      throws IOException {
    FOMWriter writer = new FOMWriter();
    writer.writeTo(this,out,options);
  }
  
  public void writeTo(
    java.io.Writer out, 
    WriterOptions options)
      throws IOException {
    FOMWriter writer = new FOMWriter();
    writer.writeTo(this,out,options);
  }
  
  public void writeTo(
    Writer writer, 
    OutputStream out) 
      throws IOException {
    writer.writeTo(this,out);
  }
  
  public void writeTo(
    Writer writer,
    java.io.Writer out) 
      throws IOException {
    writer.writeTo(this,out);
  }
  
  public void writeTo(
    Writer writer, 
    OutputStream out,
    WriterOptions options) 
      throws IOException {
    writer.writeTo(this,out,options);
  }
  
  public void writeTo(
    Writer writer,
    java.io.Writer out,
    WriterOptions options) 
      throws IOException {
    writer.writeTo(this,out,options);
  }
  
  public void writeTo(OutputStream out) throws IOException {
    String charset = getCharset();
    if (charset == null) charset = "UTF-8";
    writeTo(new OutputStreamWriter(out, charset));
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
  
  public void setContentType(String contentType) {
    try {
      this.contentType = new MimeType(contentType);
      if (this.contentType.getParameter("charset") != null)
        setCharset(this.contentType.getParameter("charset"));
    } catch (javax.activation.MimeTypeParseException e) {
      throw new org.apache.abdera.util.MimeTypeParseException(e);
    }
  }
  
  public Date getLastModified() {
    return this.lastModified;
  }
  
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  
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
  
  public String[] getProcessingInstruction(String target) {
    List<String> values = new ArrayList<String>();
    for (Iterator i = getChildren(); i.hasNext();) {
      OMNode node = (OMNode) i.next();
      if (node.getType() == OMNode.PI_NODE) {
        OMProcessingInstruction pi = (OMProcessingInstruction) node;
        if (pi.getTarget().equalsIgnoreCase(target))
          values.add(pi.getValue());
      }
    }
    return values.toArray(new String[values.size()]);
  }

  public void addProcessingInstruction(String target, String value) {
    OMProcessingInstruction pi = 
      this.factory.createOMProcessingInstruction(
        null, target, value);
    if (this.getOMDocumentElement() != null) {
      this.getOMDocumentElement().insertSiblingBefore(pi);
    } else {
      this.addChild(pi);
    }
  }

  public void addStylesheet(String href, String media) {
    if (media == null) {
      addProcessingInstruction(
        "xml-stylesheet", "href=\"" + href + "\"");
    } else {
      addProcessingInstruction(
        "xml-stylesheet", "href=\"" + href + 
        "\" media=\"" + media + "\"");
    }
  }

  public void addComment(String value) {
    OMComment comment =
      this.factory.createOMComment(null, value);
    if (this.getOMDocumentElement() != null) {
      this.getOMDocumentElement().insertSiblingBefore(comment);
    } else {
      this.addChild(comment);
    }
  }

  public EntityTag getEntityTag() {
    return etag;
  }

  public void setEntityTag(EntityTag tag) {
    this.etag = tag;
  }

  public void setEntityTag(String tag) {
    this.etag = new EntityTag(tag);
  }
  
  public String getLanguage() {
    return language;
  }
  
  public Lang getLanguageTag() {
    String lang = getLanguage();
    return (lang != null) ? new Lang(lang) : null;
  }
  
  public void setLanguage(String lang) {
    this.language = lang;
  }
  
  public String getSlug() {
    return slug;
  }
  
  public void setSlug(String slug) {
    this.slug = slug;
  }
  
  public boolean getMustPreserveWhitespace() {
    return preserve;
  }
  
  public void setMustPreserveWhitespace(boolean preserve) {
    this.preserve = preserve;
  }

  public XMLVersion getXmlVersion() {
    return XmlUtil.getVersion(super.getXMLVersion());
  }
  
  public WriterOptions getDefaultWriterOptions() {
    return new FOMWriter().getDefaultWriterOptions();
  }
  
  /**
   * Ensure that the underlying streams are fully parsed. 
   * We might eventually need to find a more efficient way
   * of doing this, but for now, calling toString() will
   * ensure that this particular object is fully parsed and ready 
   * to be modified.
   */
  public void complete() {
    if (!isComplete() && getRoot() != null) getRoot().complete();
  }
}
