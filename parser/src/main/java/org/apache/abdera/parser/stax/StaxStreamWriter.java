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
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.abdera.Abdera;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.AbstractStreamWriter;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.om.util.StAXWriterConfiguration;
import org.apache.axiom.util.stax.dialect.StAXDialect;

public class StaxStreamWriter extends AbstractStreamWriter {
    private static final StAXWriterConfiguration ABDERA_WRITER_CONFIGURATION = new StAXWriterConfiguration() {
        public XMLOutputFactory configure(XMLOutputFactory factory, StAXDialect dialect) {
            factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
            return factory;
        }
        
        // This is used in log statements inside Axiom
        @Override
        public String toString() {
            return "ABDERA";
        }
    }; 

    private static final String NAME = "default";

    private XMLStreamWriter writer;
    private int depth = 0;
    private int textwritten = 0;

    public StaxStreamWriter(Abdera abdera) {
        super(abdera, NAME);
    }

    public StaxStreamWriter(Abdera abdera, Writer writer) {
        super(abdera, NAME);
        setWriter(writer);
    }

    public StaxStreamWriter(Abdera abdera, OutputStream out) {
        super(abdera, NAME);
        setOutputStream(out);
    }

    public StaxStreamWriter(Abdera abdera, OutputStream out, String charset) {
        super(abdera, NAME);
        setOutputStream(out, charset);
    }

    public StreamWriter setWriter(java.io.Writer writer) {
        try {
            this.writer = createXMLStreamWriter(writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private static XMLStreamWriter createXMLStreamWriter(Writer out) throws XMLStreamException {
        XMLOutputFactory outputFactory = StAXUtils.getXMLOutputFactory(ABDERA_WRITER_CONFIGURATION);
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
        return writer;
    }

    public StreamWriter setOutputStream(java.io.OutputStream out) {
        try {
            this.writer = createXMLStreamWriter(out, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) throws XMLStreamException {
        XMLOutputFactory outputFactory = StAXUtils.getXMLOutputFactory(ABDERA_WRITER_CONFIGURATION);
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out, encoding);
        return writer;
    }

    public StreamWriter setOutputStream(java.io.OutputStream out, String charset) {
        try {
            this.writer = createXMLStreamWriter(out, charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter startDocument(String xmlversion, String charset) {
        try {
            writer.writeStartDocument(xmlversion, charset);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter startDocument(String xmlversion) {
        try {
            writer.writeStartDocument(xmlversion);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter endDocument() {
        try {
            writer.writeEndDocument();
            writer.flush();
            if (autoclose)
                writer.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter endElement() {
        try {
            if (autoindent && textwritten == 0) {
                pop();
                indent();
            } else
                pop();
            writer.writeEndElement();
            if (autoflush)
                writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void writeNamespace(String prefix, String namespace, boolean attr) throws XMLStreamException {
        prefix = prefix != null ? prefix : "";
        if (!declared(prefix, namespace)) {
            if (attr && (namespace == null || "".equals(namespace)))
                return;
            if (prefix != null)
                writer.writeNamespace(prefix, namespace);
            else
                writer.writeDefaultNamespace(namespace);
            declare(prefix, namespace);
            if (autoflush)
                writer.flush();
        }
    }

    private boolean needToWriteNamespace(String prefix, String namespace) {
        NamespaceContext nc = writer.getNamespaceContext();
        String uri = nc.getNamespaceURI(prefix != null ? prefix : "");
        return uri != null ? !uri.equals(namespace) : true;
    }

    public StreamWriter startElement(String name, String namespace, String prefix) {
        try {
            if (prefix == null || prefix.equals(""))
                prefix = writer.getPrefix(namespace);
            if (autoindent && textwritten == 0)
                indent();
            push();
            if (prefix != null && !prefix.equals("")) {
                writer.writeStartElement(prefix, name, namespace);
                if (needToWriteNamespace(prefix, namespace))
                    writeNamespace(prefix, namespace, false);
            } else if (namespace != null) {
                writer.writeStartElement("", name, namespace);
                if (needToWriteNamespace(prefix, namespace))
                    writeNamespace(prefix, namespace, false);
            } else {
                writer.writeStartElement("", name, "");
                writer.writeDefaultNamespace("");
            }
            if (autoflush)
                writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeElementText(String value) {
        try {
            textwritten++;
            writer.writeCharacters(value);
            if (autoflush)
                writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeComment(String value) {
        try {
            if (autoindent)
                indent();
            writer.writeComment(value);
            if (autoflush)
                writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writePI(String value) {
        try {
            if (autoindent)
                indent();
            writer.writeProcessingInstruction(value);
            if (autoflush)
                writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writePI(String value, String target) {
        try {
            if (autoindent)
                indent();
            writer.writeProcessingInstruction(value, target);
            if (autoflush)
                writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeId() {
        return writeId(FOMHelper.generateUuid());
    }

    public StreamWriter writeDefaultNamespace(String uri) {
        try {
            writer.writeDefaultNamespace(uri);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeNamespace(String prefix, String uri) {
        try {
            writer.writeNamespace(prefix, uri);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, String value) {
        if (value == null)
            return this;
        try {
            if (prefix != null) {
                if (!prefix.equals("xml"))
                    writeNamespace(prefix, namespace, true);
                writer.writeAttribute(prefix, namespace, name, value);
            } else if (namespace != null) {
                if (!namespace.equals(Constants.XML_NS))
                    ;
                writeNamespace(prefix, namespace, true);
                writer.writeAttribute(namespace, name, value);
            } else {
                writer.writeAttribute(name, value);
            }
            if (autoflush)
                writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private final Stack<Map<String, String>> namespaces = new Stack<Map<String, String>>();

    private void push() {
        namespaces.push(new HashMap<String, String>());
        depth++;
    }

    private void pop() {
        depth--;
        if (textwritten > 0)
            textwritten--;
        if (!namespaces.isEmpty())
            namespaces.pop();
    }

    private void declare(String prefix, String namespace) {
        if (namespaces.isEmpty())
            return;
        Map<String, String> frame = namespaces.peek();
        frame.put(prefix, namespace);
    }

    private boolean declared(String prefix, String namespace) {
        for (int n = namespaces.size() - 1; n >= 0; n--) {
            Map<String, String> frame = namespaces.get(n);
            String chk = frame.get(prefix);
            if (chk == null && namespace == null)
                return true;
            if (chk == null && namespace != null)
                continue;
            if (chk != null && namespace == null)
                continue;
            if (chk.equals(namespace))
                return true;
        }
        return false;
    }

    public StreamWriter flush() {
        try {
            writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter indent() {
        try {
            char[] indent = new char[depth * 2];
            Arrays.fill(indent, ' ');
            writer.writeCharacters("\n");
            writer.writeCharacters(indent, 0, indent.length);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public void close() throws IOException {
        try {
            writer.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public StreamWriter setPrefix(String prefix, String uri) {
        try {
            writer.setPrefix(prefix, uri);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

}
