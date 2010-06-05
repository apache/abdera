package org.apache.abdera.parser.stax.util;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.util.AbstractNamedWriter;
import org.apache.abdera.util.AbstractWriterOptions;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.WriterOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.StAXUtils;

@SuppressWarnings("unchecked")
public class PrettyWriter extends AbstractNamedWriter implements NamedWriter {

    private static final String[] FORMATS = {"application/atom+xml", "application/atomserv+xml", "application/xml"};

    public PrettyWriter() {
        super("PrettyXML", FORMATS);
    }

    public Object write(Base base, WriterOptions options) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeTo(base, out, options);
        return out.toString();
    }

    public void writeTo(Base base, OutputStream out, WriterOptions options) throws IOException {
        out = getCompressedOutputStream(out, options);
        String charset = options.getCharset() != null ? options.getCharset() : "UTF-8";
        writeTo(base, new OutputStreamWriter(out, charset), options);
        finishCompressedOutputStream(out, options);
        if (options.getAutoClose())
            out.close();
    }

    public void writeTo(Base base, Writer out, WriterOptions options) throws IOException {
        try {
            XMLStreamWriter w = StAXUtils.createXMLStreamWriter(out);
            XMLStreamWriter pw = new PrettyStreamWriter(w);
            OMElement om = (base instanceof Document) ? getOMElement(((Document)base).getRoot()) : (OMElement)base;
            String charset = options.getCharset();
            if (om.getParent() != null && om.getParent() instanceof OMDocument) {
                OMDocument doc = (OMDocument)om.getParent();
                pw.writeStartDocument(charset != null ? charset : doc.getCharsetEncoding(), doc.getXMLVersion());
            }
            om.serialize(pw);
            pw.writeEndDocument();
            if (options.getAutoClose())
                out.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private OMElement getOMElement(Element el) {
        if (el instanceof ElementWrapper) {
            return getOMElement(((ElementWrapper)el).getInternal());
        } else
            return (OMElement)el;
    }

    private static class PrettyStreamWriter implements XMLStreamWriter {

        private static final int INDENT = 2;

        private XMLStreamWriter internal = null;
        private int depth = 0;
        private boolean prev_was_end_element = false;

        public PrettyStreamWriter(XMLStreamWriter writer) {
            this.internal = writer;
        }

        public void close() throws XMLStreamException {
            internal.close();
        }

        public void flush() throws XMLStreamException {
            internal.flush();
        }

        public NamespaceContext getNamespaceContext() {
            return internal.getNamespaceContext();
        }

        public String getPrefix(String arg0) throws XMLStreamException {
            return internal.getPrefix(arg0);
        }

        public Object getProperty(String arg0) throws IllegalArgumentException {
            return internal.getProperty(arg0);
        }

        public void setDefaultNamespace(String arg0) throws XMLStreamException {
            internal.setDefaultNamespace(arg0);
        }

        public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException {
            internal.setNamespaceContext(arg0);
        }

        public void setPrefix(String arg0, String arg1) throws XMLStreamException {
            internal.setPrefix(arg0, arg1);
        }

        public void writeAttribute(String arg0, String arg1) throws XMLStreamException {
            internal.writeAttribute(arg0, arg1);
            prev_was_end_element = false;
        }

        public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException {
            internal.writeAttribute(arg0, arg1, arg2);
            prev_was_end_element = false;
        }

        public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException {
            internal.writeAttribute(arg0, arg1, arg2, arg3);
            prev_was_end_element = false;
        }

        public void writeCData(String arg0) throws XMLStreamException {
            internal.writeCData(arg0);
            prev_was_end_element = false;
        }

        public void writeCharacters(String arg0) throws XMLStreamException {
            internal.writeCharacters(arg0);
            prev_was_end_element = false;
        }

        public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
            internal.writeCharacters(arg0, arg1, arg2);
            prev_was_end_element = false;
        }

        public void writeComment(String arg0) throws XMLStreamException {
            writeIndent();
            internal.writeComment(arg0);
            prev_was_end_element = true;
        }

        public void writeDTD(String arg0) throws XMLStreamException {
            internal.writeDTD(arg0);
            prev_was_end_element = true;
        }

        public void writeDefaultNamespace(String arg0) throws XMLStreamException {
            internal.writeDefaultNamespace(arg0);
            prev_was_end_element = false;
        }

        public void writeEmptyElement(String arg0) throws XMLStreamException {
            writeIndent();
            internal.writeEmptyElement(arg0);
            prev_was_end_element = true;
        }

        public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
            writeIndent();
            internal.writeEmptyElement(arg0, arg1);
            prev_was_end_element = true;
        }

        public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            writeIndent();
            internal.writeEmptyElement(arg0, arg1, arg2);
            prev_was_end_element = true;
        }

        public void writeEndDocument() throws XMLStreamException {
            internal.writeEndDocument();
            prev_was_end_element = false;
        }

        public void writeEndElement() throws XMLStreamException {
            depth--;
            if (prev_was_end_element)
                writeIndent();
            internal.writeEndElement();
            prev_was_end_element = true;
        }

        public void writeEntityRef(String arg0) throws XMLStreamException {
            internal.writeEntityRef(arg0);
            prev_was_end_element = false;
        }

        public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
            internal.writeNamespace(arg0, arg1);
            prev_was_end_element = false;
        }

        public void writeProcessingInstruction(String arg0) throws XMLStreamException {
            writeIndent();
            internal.writeProcessingInstruction(arg0);
            prev_was_end_element = true;
        }

        public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
            writeIndent();
            internal.writeProcessingInstruction(arg0, arg1);
            prev_was_end_element = true;
        }

        public void writeStartDocument() throws XMLStreamException {
            internal.writeStartDocument();
            prev_was_end_element = false;
        }

        public void writeStartDocument(String arg0) throws XMLStreamException {
            internal.writeStartDocument(arg0);
            prev_was_end_element = false;
        }

        public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
            internal.writeStartDocument(arg0, arg1);
            prev_was_end_element = false;
        }

        public void writeStartElement(String arg0) throws XMLStreamException {
            writeIndent();
            depth++;
            internal.writeStartElement(arg0);
            prev_was_end_element = false;
        }

        public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
            writeIndent();
            depth++;
            internal.writeStartElement(arg0, arg1);
            prev_was_end_element = false;
        }

        public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            writeIndent();
            depth++;
            internal.writeStartElement(arg0, arg1, arg2);
            prev_was_end_element = false;
        }

        private void writeIndent() throws XMLStreamException {
            internal.writeCharacters("\n");
            char[] spaces = getSpaces();
            internal.writeCharacters(spaces, 0, spaces.length);
        }

        private char[] getSpaces() {
            char[] spaces = new char[INDENT * depth];
            java.util.Arrays.fill(spaces, ' ');
            return spaces;
        }
    }

    @Override
    protected WriterOptions initDefaultWriterOptions() {
        return new AbstractWriterOptions() {
        };
    }

}
