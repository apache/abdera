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

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.AbstractStreamWriter;
import org.apache.axiom.om.util.StAXUtils;

public class StaxStreamWriter 
  extends AbstractStreamWriter {
  
  private static final String NAME = "default";
  
  private XMLStreamWriter writer;
  
  public StaxStreamWriter() {
    super(NAME);
  }
  
  public StaxStreamWriter(
    Writer writer) {
    super(NAME);
    setWriter(writer);
  }

  public StaxStreamWriter(
    OutputStream out) {
      super(NAME);
      setOutputStream(out);
  }
  
  public StaxStreamWriter(
    OutputStream out, 
    String charset) {
    super(NAME);
    setOutputStream(out,charset);
  }

  public void setWriter(java.io.Writer writer) {
    try {
      this.writer = StAXUtils.createXMLStreamWriter(writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  public void setOutputStream(java.io.OutputStream out) {
    try {
      this.writer = StAXUtils.createXMLStreamWriter(out,"UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  public void setOutputStream(java.io.OutputStream out, String charset) {
    try {
      this.writer = StAXUtils.createXMLStreamWriter(out,charset);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public void startDocument(String xmlversion, String charset) {
    try {
      writer.writeStartDocument(xmlversion,charset);
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }    
  }
  
  public void startDocument(String xmlversion) {
    try {
      writer.writeStartDocument(xmlversion);
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }
  
  public void endDocument() {
    try {
      writer.writeEndDocument();
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }
 
  public void endElement() {
    try {
      pop();
      writer.writeEndElement();
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void writeNamespace(QName qname, boolean attr) throws XMLStreamException {
    String prefix = qname.getPrefix();
    String namespace = qname.getNamespaceURI();
    prefix = prefix != null ? prefix : "";
    if (!declared(prefix,namespace)) {
      if (attr && (namespace == null || "".equals(namespace))) return;
      if (prefix != null)
        writer.writeNamespace(prefix,namespace);
      else
        writer.writeDefaultNamespace(namespace);
      declare(prefix,namespace);
    }
  }
  
  public void startElement(QName qname, Map<QName, String> attributes) {
    try {
      push();
      writer.writeStartElement(qname.getPrefix(),qname.getLocalPart(),qname.getNamespaceURI());
      writeNamespace(qname,false);
      if (attributes != null) {
        for (QName attr : attributes.keySet()) 
          writeNamespace(attr,true);
        for (QName attr : attributes.keySet()) {
          writer.writeAttribute(
            attr.getPrefix(), 
            attr.getNamespaceURI(),
            attr.getLocalPart(),
            attributes.get(attr));
        }
      }
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }
  
  public void writeElementText(String value) {
    try {
      writer.writeCharacters(value);
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  public void writeComment(String value) {
    try {
      writer.writeComment(value);
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  public void writePI(String value) {
    try {
      writer.writeProcessingInstruction(value);
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  public void writePI(String value, String target) {
    try {
      writer.writeProcessingInstruction(value,target);
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }
  
  private final Stack<Map<String,String>> namespaces = new Stack<Map<String,String>>();
  
  private void push() {
    namespaces.push(new HashMap<String,String>());
  }
  
  private void pop() {
    if (!namespaces.isEmpty()) namespaces.pop();
  }
  
  private boolean declared(String prefix, String namespace) {
    for (int n = namespaces.size() - 1; n >= 0; n--) {
      Map<String,String> frame = namespaces.get(n);
      String chk = frame.get(prefix);
      if (chk == null && namespace == null) return true;
      if (chk == null && namespace != null) continue;
      if (chk != null && namespace == null) continue;
      if (chk.equals(namespace)) return true;
    }
    return false;
  }
  
  private void declare(String prefix, String namespace) {
    if (namespaces.isEmpty()) return;
    Map<String,String> frame = namespaces.peek();
    frame.put(prefix, namespace);
  }

  public void writeId(Map<QName,String> attributes) {
    writeId(FOMHelper.generateUuid(),attributes);
  }
    
}
