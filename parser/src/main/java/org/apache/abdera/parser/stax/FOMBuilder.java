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

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.filter.TextFilter;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.util.OMSerializerUtil;


public class FOMBuilder 
  extends StAXOMBuilder
  implements Constants {

  private FOMFactory fomfactory = null;
  private Document fomDocument = null;
  private ParserOptions parserOptions = null;
  private int depth = 0;
  private int depthInSkipElement = 0;
  
  public FOMBuilder(
    FOMFactory factory, 
    XMLStreamReader parser, 
    ParserOptions parserOptions) {
      super(factory, parser);
      this.parserOptions = parserOptions;
      fomfactory = factory;
  }

  public FOMBuilder(
      FOMFactory factory, 
      XMLStreamReader parser) {
        super(factory, parser);
        document = factory.createOMDocument(this);
        fomfactory = factory;
    }
  
  public FOMBuilder(
    String filePath, 
    ParserOptions parserOptions) 
      throws XMLStreamException, 
             FileNotFoundException {
    super(filePath);
    this.parserOptions = parserOptions;
    fomfactory = getFomFactory();
    setOMBuilderFactory(fomfactory);
  }

  public FOMBuilder(
    FOMFactory factory, 
    String filePath, 
    ParserOptions parserOptions) 
      throws XMLStreamException, 
             FileNotFoundException {
    super(filePath);
    this.parserOptions = parserOptions;
    fomfactory = factory;
    setOMBuilderFactory(fomfactory);
  }
  
  public FOMBuilder(
    InputStream inStream, 
    ParserOptions parserOptions) 
      throws XMLStreamException {
    super(inStream);
    this.parserOptions = parserOptions;
    fomfactory = getFomFactory();
    setOMBuilderFactory(fomfactory);
  }

  public FOMBuilder(
    FOMFactory factory, 
    InputStream inStream, 
    ParserOptions parserOptions) 
      throws XMLStreamException {
    super(inStream);
    this.parserOptions = parserOptions;
    fomfactory = factory;
    setOMBuilderFactory(fomfactory);
  }

  public FOMBuilder(
    XMLStreamReader parser, 
    ParserOptions parserOptions) {
      super(parser);
      this.parserOptions = parserOptions;
      fomfactory = getFomFactory();
      setOMBuilderFactory(fomfactory);
  }
  
  public FOMBuilder(
    XMLStreamReader parser, 
    FOMFactory factory, 
    ParserOptions parserOptions) {
      super(factory, parser);
      this.parserOptions = parserOptions;
      fomfactory = factory;
  }
 
  private FOMFactory getFomFactory() {
    FOMFactory factory = 
      (parserOptions != null && parserOptions.getFactory() != null) ? 
      (FOMFactory)parserOptions.getFactory() : null;
    if (factory == null)
      factory = (Factory.INSTANCE instanceof FOMFactory) ? 
          (FOMFactory)Factory.INSTANCE : new FOMFactory();
    return factory;
  }
  
  public ParserOptions getParserOptions() {
    return parserOptions;
  }
  
  @Override
  protected OMNode createOMElement() throws OMException {
    depth++;
    OMElement node;
    String elementName = parser.getLocalName();
    if (lastNode == null) {
        node = constructNode(null, elementName);
    } else if (lastNode.isComplete()) {
        node =
          constructNode(
            (OMContainer) lastNode.getParent(),
            elementName);
        if (node != null) {
          ((OMNodeEx) lastNode).setNextOMSibling(node);
          ((OMNodeEx) node).setPreviousOMSibling(lastNode);
        }
    } else {
        OMElement e = (OMElement) lastNode;
        node = constructNode((OMElement) lastNode, elementName);
        e.setFirstChild(node);
    }
    return node;
  }

  protected Text.Type getTextType() {
    Text.Type ttype = Text.Type.TEXT;
    String type = parser.getAttributeValue(null, LN_TYPE);
    if (type != null) {
      ttype = Text.Type.typeFromString(type);
      if (ttype == null)
        throw new FOMUnsupportedTextTypeException(type);
    }
    return ttype;
  }
  
  protected Content.Type getContentType() {
    Content.Type ctype = Content.Type.TEXT;
    String type = parser.getAttributeValue(null, LN_TYPE);
    String src = parser.getAttributeValue(null, LN_SRC);
    if (type != null) {
      ctype = Content.Type.typeFromString(type);
      if (ctype == null)
        throw new FOMUnsupportedContentTypeException(type);
    } else if (type == null && src != null) {
      ctype = Content.Type.MEDIA;
    }
    return ctype;
  }
  
  private boolean isAcceptableToParse(QName qname, boolean attribute) {
    if (parserOptions == null) return true;
    ParseFilter filter = parserOptions.getParseFilter();
    return (filter != null) ? 
      (!attribute) ? 
         filter.acceptable(qname) : 
         filter.acceptableAttribute(parser.getName(), qname): 
      true;
  }
  
  private OMNode applyTextFilter(int type) {
    if (parserOptions != null) { 
      TextFilter filter = parserOptions.getTextFilter();
      if (filter != null) {
        String value = parser.getText();
        if (!lastNode.isComplete())
          value = filter.filterText(value, (Element)lastNode);
        return createOMText(value, type);
      }
    }
    return createOMText(type);
  }
  
  private String applyAttributeTextFilter(String value, QName attribute, Element parent) {
    if (parserOptions != null) { 
      TextFilter filter = parserOptions.getTextFilter();
      if (filter != null) {
        return filter.filterAttributeText(value, attribute, parent);
      }
    }
    return value;
  }
  
  private int getNextElementToParse() 
    throws XMLStreamException{
      int token = parser.next();
      if (depthInSkipElement == 0 && token != XMLStreamConstants.START_ELEMENT){
        return token;         
      } else if (token == XMLStreamConstants.START_ELEMENT && isAcceptableToParse(parser.getName(), false) && depthInSkipElement == 0){
        return token;
      } else if (token == XMLStreamConstants.START_ELEMENT ){      
        depthInSkipElement++; 
      } else if (token == XMLStreamConstants.END_ELEMENT ){ // otherwise skip like crazy      
        depthInSkipElement--;
      }   
      return getNextElementToParse();
  }
  
  /**
   * Method next.
   *
   * @return Returns int.
   * @throws OMException
   */
  public int next() throws OMException {
    try {
        if (done) {
            throw new OMException();
        }
        int token = getNextElementToParse();
        if (!cache) {
            return token;
        }
        switch (token) {
            case XMLStreamConstants.START_ELEMENT:
                lastNode = createOMElement();
                break;
            case XMLStreamConstants.START_DOCUMENT:
                document.setXMLVersion(
                  parser.getVersion() != null ? 
                  parser.getVersion() : "1.0");
                document.setCharsetEncoding(
                  parser.getEncoding() != null ?
                  parser.getEncoding() : "utf-8");
                document.setStandalone(
                  parser.isStandalone() ? YES : NO);
                break;
            case XMLStreamConstants.CHARACTERS:
                lastNode = applyTextFilter(XMLStreamConstants.CHARACTERS);
                break;
            case XMLStreamConstants.CDATA:
                lastNode = applyTextFilter(XMLStreamConstants.CDATA);
                break;
            case XMLStreamConstants.END_ELEMENT:
                endElement();
                break;
            case XMLStreamConstants.END_DOCUMENT:
                done = true;
                ((OMContainerEx) this.document).setComplete(true);
                break;
            case XMLStreamConstants.SPACE:
                lastNode = createOMText(XMLStreamConstants.SPACE);
                break;
            case XMLStreamConstants.COMMENT:
                createComment();
                break;
            case XMLStreamConstants.DTD:
                createDTD();
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                createPI();
                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                lastNode = createOMText(XMLStreamConstants.ENTITY_REFERENCE);
                break;
            default :
                throw new OMException();
        }
        return token;
    } catch (OMException e) {
        throw e;
    } catch (Exception e) {
        throw new OMException(e);
    }
  }
  
  private void initDocument(String name) {
    fomDocument = fomfactory.newDocument();
    String enc = parser.getCharacterEncodingScheme();
    getDocument().setCharsetEncoding(enc != null ? enc : "utf-8");
    getDocument().setXMLVersion(
      parser.getVersion() != null ? 
        parser.getVersion() : "1.0");
  }
  
  protected OMElement constructNode(OMContainer parent, String name) {
    OMElement element = null;
    if (fomDocument == null) {
      initDocument(name);
      parent = (OMContainer) fomDocument;
    }
    QName qname = parser.getName();
    element = fomfactory.createElement(qname, parent, this);
    if (element == null) {
      element = new FOMExtensionElement(qname, parent, fomfactory, this);
    }
    if (element != null) {
      this.processNamespaceData(element);
      processAttributes(element);
    }
    return element;
  }

  @Override
  protected void processAttributes(OMElement node) {
    int attribCount = parser.getAttributeCount();
    for (int i = 0; i < attribCount; i++) {
      QName attr = parser.getAttributeName(i);
      if (isAcceptableToParse(attr, true)) {
        String uri = parser.getAttributeNamespace(i);
        String prefix = parser.getAttributePrefix(i);
        OMNamespace namespace = null;
        if (uri != null && uri.length() > 0) {
            namespace = node.findNamespace(uri, prefix);
            if (namespace == null) {
                if (prefix == null || "".equals(prefix)) {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
                namespace = node.declareNamespace(uri, prefix);
            }
        }
        String value = applyAttributeTextFilter(
          parser.getAttributeValue(i), 
          attr, (Element)node);
        node.addAttribute(parser.getAttributeLocalName(i),
                value, namespace);
      }
    }
}
  
  @Override
  protected void endElement() {
    if (lastNode != null && lastNode.isComplete()) {
      OMElement parent = (OMElement) lastNode.getParent();
      ((OMNodeEx) parent).setComplete(true);
      lastNode = parent;
    } else {
        OMNode e = lastNode;
        if (e != null)
          ((OMNodeEx) e).setComplete(true);
    }
    depth--;
  }

  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> getFomDocument() {
    while ((fomDocument == null) && !done) {
      next();
    }
    return fomDocument;
  }
  
  public OMDocument getDocument() {
    return (OMDocument) getFomDocument();
  }
  
  public FOMFactory getFactory() {
    return fomfactory;
  }
  
  /**
   * Method createOMText.
   *
   * @return Returns OMNode.
   * @throws OMException
   */
  protected OMNode createOMText(String value, int textType) throws OMException {
      OMNode node = null;
      if (lastNode == null) {
          return null;
      } else if (!lastNode.isComplete()) {
          node = createOMText(value, (OMElement) lastNode, textType);
      } else {
          OMContainer parent = lastNode.getParent();
          if (!(parent instanceof OMDocument)) {
              node = createOMText(value, (OMElement) parent, textType);
          }
      }
      return node;
  }

  /**
   * This method will check whether the text can be optimizable using IS_BINARY flag.
   * If that is set then we try to get the data handler.
   *
   * @param omElement
   * @param textType
   * @return omNode
   */
  private OMNode createOMText(String value, OMElement omElement, int textType) {
      try {
          if (isDataHandlerAware && Boolean.TRUE == parser.getProperty(OMConstants.IS_BINARY)) {
              Object dataHandler = parser.getProperty(OMConstants.DATA_HANDLER);
              OMText text = omfactory.createOMText(dataHandler, true);
              omElement.addChild(text);
              return text;
          } else {
              return omfactory.createOMText(omElement, value, textType);
          }
      } catch (IllegalArgumentException e) {
          return omfactory.createOMText(omElement, value, textType);
      }
  }
}
