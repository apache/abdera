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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMElementImpl;

public class FOMElement 
  extends OMElementImpl 
  implements Element, 
             OMElement, 
             Constants {

  private static final long serialVersionUID = 8024257594220911953L;
  
  public FOMElement(QName qname) {
    super(qname, null, (OMFactory)Factory.INSTANCE);
  }
  
  public FOMElement(
    String name,
    OMNamespace namespace,
    OMContainer parent,
    OMFactory factory)
      throws OMException {
    super(name, namespace, parent, factory);
  }
  
  public FOMElement(
    QName qname, 
    OMContainer parent, 
    OMFactory factory) 
      throws OMException {
    super(qname, parent, factory);
  }

  public FOMElement(
    QName qname,
    OMContainer parent,
    OMFactory factory,
    OMXMLParserWrapper builder) {
      super(
        qname.getLocalPart(), 
        factory.createOMNamespace(
          qname.getNamespaceURI(), 
          qname.getPrefix()),
        parent,
        builder,
        factory);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Base>T getParentElement() {
    return (T)super.getParent();
  }
  
  public void setParentElement(Element parent) {
    super.setParent((FOMElement)parent);
  }

  @SuppressWarnings("unchecked")
  public <T extends Element>T getPreviousSibling() {
    OMNode el = this.getPreviousOMSibling();
    while (el != null) {
      if (el instanceof Element) return (T)el;
      else el = el.getPreviousOMSibling();
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>T getNextSibling() {
    OMNode el = this.getNextOMSibling();
    while (el != null) {
      if (el instanceof Element) return (T) el;
      else el = el.getNextOMSibling();
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>T getFirstChild() {
    return (T)this.getFirstElement();
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>T getPreviousSibling(QName qname) {
    Element el = getPreviousSibling();
    while (el != null) {
      OMElement omel = (OMElement) el;
      if (omel.getQName().equals(qname))
        return (T)omel;
      el = el.getPreviousSibling();
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>T getNextSibling(QName qname) {
    Element el = getNextSibling();
    while (el != null) {
      OMElement omel = (OMElement) el;
      if (omel.getQName().equals(qname))
        return (T)omel;
      el = el.getNextSibling();
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>T getFirstChild(QName qname) {
    return (T)this.getFirstChildWithName(qname);
  }
  
  public String getLanguage() {
    return getAttributeValue(LANG);
  }

  public void setLanguage(String language) {
    setAttributeValue(LANG,language);
  }

  public URI getBaseUri() throws URISyntaxException {
    URI uri = _getUriValue(getAttributeValue(BASE));
    if (uri == null) {
      if (parent instanceof Element) {
        uri = ((Element)parent).getBaseUri();
      } else if (parent instanceof Document) {
        uri = ((Document)parent).getBaseUri();
      }
    }
    return uri;
  }

  public URI getResolvedBaseUri() throws URISyntaxException {
    URI baseUri = null;
    URI uri = _getUriValue(getAttributeValue(BASE));
    if (parent instanceof Element) 
      baseUri = ((Element)parent).getResolvedBaseUri();
    else if (parent instanceof Document)
      baseUri = ((Document)parent).getBaseUri();
    if (uri != null && baseUri != null) {
      if (baseUri != null) {
        uri = baseUri.resolve(uri);
      } else {
        return getBaseUri();
      }
    } else if (uri == null) {
      uri = baseUri;
    }
    return uri;    
  }
  
  public void setBaseUri(URI base) {
    setAttributeValue(BASE,_getStringValue(base));
  }
  
  public void setBaseUri(String base) throws URISyntaxException {
    setBaseUri((base != null) ? new URI(base) : null);
  }
  
  public String getAttributeValue(QName qname) {
    OMAttribute attr = getAttribute(qname);
    return (attr != null) ? attr.getAttributeValue() : null;    
  }
  
  public void setAttributeValue(QName qname, String value) {
    OMAttribute attr = this.getAttribute(qname);
    if (attr != null && value != null) {
      attr.setAttributeValue(value);
    } else {
      if (value != null) {
        if (qname.getNamespaceURI() != null) {
          attr = factory.createOMAttribute(
            qname.getLocalPart(), 
            factory.createOMNamespace(
              qname.getNamespaceURI(), 
              qname.getPrefix()), 
            value);
        } else {
          attr = factory.createOMAttribute(
            qname.getLocalPart(), 
            null, 
            value);
        }
        addAttribute(attr);
      } else {
        removeAttribute(attr);
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  protected <E extends Element>List<E> _getChildrenAsSet(QName qname) {
    return new FOMList(getChildrenWithName(qname));
  }
  
  protected void _setChild(QName qname, OMElement element) {
    OMElement e = getFirstChildWithName(qname);
    if (e == null && element != null) {
      addChild(element);
    } else if (e != null && element != null) {
      e.insertSiblingBefore(element);
      e.discard();
    } else if (e != null && element == null) {
      e.discard();
    }
  }
  
  protected URI _getUriValue(String v) throws URISyntaxException {
    return (v != null) ? new URI(v) : null;
  }
  
  protected String _getStringValue(URI uri) {
    return (uri != null) ? uri.toString() : null;
  }
  
  protected URI _resolve(URI base, URI value) {
    if (value == null) return null;
    if ("".equals(value.toString()) || 
        "#".equals(value.toString()) ||
        ".".equals(value.toString()) ||
        "./".equals(value.toString())) return base;
    if (base == null) return value;
    if ("".equals(base.getPath())) base = base.resolve("/");
    URI resolved = (base != null) ? base.resolve(value) : value;
    return resolved;
  }

  public void writeTo(OutputStream out) throws IOException {
    writeTo(new OutputStreamWriter(out));
  }

  public void writeTo(java.io.Writer writer) throws IOException {
    try { 
      OMOutputFormat outputFormat = new OMOutputFormat();
      if (getDocument() != null && getDocument().getCharset() != null)
        outputFormat.setCharSetEncoding(getDocument().getCharset());
      serializeAndConsume(writer, outputFormat);
    } catch (XMLStreamException e) {
      throw new FOMException(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> getDocument() {
    Document<T> document = null;
    if (parent != null) {
      if (parent instanceof Element) {
        document = ((Element)parent).getDocument();
      } else if (parent instanceof Document) {
        document = (Document<T>) parent;
      }
    }
    return document;
  }

  public String getAttributeValue(String name) {
    return getAttributeValue(new QName(name));
  }
  
  public void setAttributeValue(String name, String value) {
    setAttributeValue(new QName(name), value);
  }
  
  protected void _setElementValue(QName qname, String value) {
    OMElement element = this.getFirstChildWithName(qname);
    if (element != null && value != null) {
      element.setText(value);
    } else if (element != null && value == null) {
      for (Iterator i = element.getChildren(); i.hasNext();) {
        OMNode node = (OMNode) i.next();
        node.discard();
      }
    } else if (element == null && value != null ) {
      element = factory.createOMElement(qname, this);
      element.setText(value);
      this.addChild(element);
    }
  }
  
  protected String _getElementValue(QName qname) {
    String value = null;
    OMElement element = this.getFirstChildWithName(qname);
    if (element != null)
      value = element.getText();
    return value;
  }

  @SuppressWarnings("unchecked")
  protected <T extends Text>T getTextElement(QName qname) {
    return (T)getFirstChildWithName(qname);
  }

  protected <T extends Text>void setTextElement(QName qname, T text, boolean many) {
    if (text != null) {
      if (!many) {
        OMElement el = getFirstChildWithName(qname);
        if (el != null) el.discard();
      }
      _setChild(qname, (OMElement)text);
    } else _removeChildren(qname, false);
  }

  protected Text setTextText(QName qname, String value) {
    if (value == null) {
      setTextElement(qname, null, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Text text = fomfactory.newText(qname, Text.Type.TEXT);
    text.setValue(value);
    setTextElement(qname, text, false);
    return text;
  }

  protected Text setHtmlText(QName qname, String value, URI baseUri) {
    if (value == null) {
      setTextElement(qname, null, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Text text = fomfactory.newText(qname, Text.Type.HTML);
    if (baseUri != null) text.setBaseUri(baseUri);
    text.setValue(value);
    setTextElement(qname, text, false);
    return text;
  }
  
  protected Text setXhtmlText(QName qname, String value, URI baseUri) {
    if (value == null) {
      setTextElement(qname, null, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Text text = fomfactory.newText(qname, Text.Type.XHTML);
    if (baseUri != null) text.setBaseUri(baseUri);
    text.setValue(value);
    setTextElement(qname, text, false);
    return text;
  }

  protected Text setXhtmlText(QName qname, Div value, URI baseUri) {
    if (value == null) {
      setTextElement(qname, null, false);
      return null;
    }
    FOMFactory fomfactory = (FOMFactory) factory;
    Text text = fomfactory.newText(qname, Text.Type.XHTML);
    if (baseUri != null) text.setBaseUri(baseUri);
    text.setValueElement(value);
    setTextElement(qname, text, false);
    return text;
  }
  
  protected String getText(QName qname) {
    Text text = getTextElement(qname);
    return (text != null) ? text.getValue() : null;
  }
  
  public List<QName> getAttributes() {
    List<QName> list = new ArrayList<QName>();
    for (Iterator i = getAllAttributes(); i.hasNext();) {
      OMAttribute attr = (OMAttribute) i.next();
      list.add(attr.getQName());
    }
    return Collections.unmodifiableList(list);
  }
  
  public List<QName> getExtensionAttributes() {
    List<QName> list = new ArrayList<QName>();
    for (Iterator i = getAllAttributes(); i.hasNext();) {
      OMAttribute attr = (OMAttribute) i.next();
      String namespace = 
        (attr.getNamespace() != null) ? 
          attr.getNamespace().getName() : "";
      if (!namespace.equals(getNamespace().getName()) &&
          !namespace.equals(""))
        list.add(attr.getQName());
    }
    return Collections.unmodifiableList(list);
  }
  
  
  protected Element _parse(String value, URI baseUri) {
    if (value == null) return null;
    FOMFactory fomfactory = (FOMFactory) factory;
    Parser parser = fomfactory.newParser();
    ByteArrayInputStream bais = new ByteArrayInputStream(value.getBytes());
    ParserOptions options = parser.getDefaultParserOptions();
    options.setCharset(getXMLStreamReader().getCharacterEncodingScheme());
    options.setFactory(fomfactory);
    Document doc = parser.parse(bais, baseUri, options);
    return doc.getRoot();
  }

  public void removeAttribute(QName qname) {
    OMAttribute attr = getAttribute(qname);
    if (attr != null) removeAttribute(attr);
  }
  
  public void removeAttribute(String name) {
    removeAttribute(getAttribute(new QName(name)));
  }
  
  protected void _removeChildren(QName qname, boolean many) {
    if (many) {
      for (Iterator i = getChildrenWithName(qname); i.hasNext();) {
        OMElement element = (OMElement) i.next();
        element.discard();
      }
    } else {
      OMElement element = getFirstChildWithName(qname);
      if (element != null) element.discard();
    }
  }
  
  protected void _removeAllChildren() {
    for (Iterator i = getChildren(); i.hasNext();) {
      OMNode node = (OMNode) i.next();
      node.discard();
    }
  }
  
  @SuppressWarnings("unchecked")
  public Object clone() {
    OMElement el = _create(this);
    _copyElement(this, el);
    return el;

  }
  
  protected OMElement _copyElement(OMElement src, OMElement dest) {
    for (Iterator i = src.getAllAttributes(); i.hasNext();) {
      OMAttribute attr = (OMAttribute) i.next();
      dest.addAttribute(
        attr.getLocalName(), 
        attr.getAttributeValue(), 
        (attr.getNamespace() != null) ? 
          dest.declareNamespace(attr.getNamespace()) : null);
    }
    for (Iterator i = src.getChildren(); i.hasNext();) {
      OMNode node = (OMNode) i.next();
      if (node.getType() == OMNode.ELEMENT_NODE) {
        OMElement element = (OMElement) node;
        OMElement child = _create(element);
        if (child != null) {
          _copyElement(element, child);
          dest.addChild(child);
        }
      } else if (node.getType() == OMNode.CDATA_SECTION_NODE) {
        OMText text = (OMText) node;
        factory.createOMText(dest,text.getText(), OMNode.CDATA_SECTION_NODE);
      } else if (node.getType() == OMNode.TEXT_NODE) {
        OMText text = (OMText) node;
        factory.createOMText(dest,text.getText());
      } else if (node.getType() == OMNode.COMMENT_NODE) {
        OMComment comment = (OMComment) node;
        factory.createOMComment(dest, comment.getValue());
      } else if (node.getType() == OMNode.PI_NODE) {
        OMProcessingInstruction pi = (OMProcessingInstruction) node;
        factory.createOMProcessingInstruction(dest, pi.getTarget(), pi.getValue());
      } else if (node.getType() == OMNode.SPACE_NODE) {
        OMText text = (OMText) node;
        factory.createOMText(dest, text.getText(), OMNode.SPACE_NODE);
      } else if (node.getType() == OMNode.ENTITY_REFERENCE_NODE) {
        OMText text = (OMText) node;
        factory.createOMText(dest, text.getText(), OMNode.ENTITY_REFERENCE_NODE);
      }
    }
    return dest;
  }
  
  protected OMElement _create(OMElement src) {
    OMElement el = null;
        
    FOMFactory fomfactory = (FOMFactory)factory;
    Object obj = null;
    if (src instanceof Content) obj = ((Content)src).getContentType();
    if (src instanceof Text) obj = ((Text)src).getTextType();
    el = fomfactory.createElement(src.getQName(), null, factory, obj);
    
    return el;
  }

  
  public Factory getFactory() {
    return (Factory) this.factory;
  }
}
