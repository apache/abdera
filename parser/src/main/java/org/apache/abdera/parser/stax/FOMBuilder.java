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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

@SuppressWarnings( {"unchecked", "deprecation"})
public class FOMBuilder extends StAXOMBuilder implements Constants {

    private final FOMFactory fomfactory;
    private final ParserOptions parserOptions;
    private boolean indoc = false;

    public FOMBuilder(FOMFactory factory, XMLStreamReader parser, ParserOptions parserOptions) {
        super(factory, new FOMStAXFilter(parser, parserOptions));
        this.document = (OMDocument)factory.newDocument();
        this.parserOptions = parserOptions;
        this.fomfactory = factory;
        String enc = parser.getCharacterEncodingScheme();
        document.setCharsetEncoding(enc != null ? enc : "utf-8");
        document.setXMLVersion(parser.getVersion() != null ? parser.getVersion() : "1.0");
    }

    public ParserOptions getParserOptions() {
        return parserOptions;
    }

    @Override
    protected OMNode createOMElement() throws OMException {
        OMElement node;
        String elementName = parser.getLocalName();
        if (lastNode == null) {
            node = constructNode(null, elementName);
        } else if (lastNode.isComplete()) {
            node = constructNode((OMContainer)lastNode.getParent(), elementName);
            ((OMNodeEx)lastNode).setNextOMSibling(node);
            ((OMNodeEx)node).setPreviousOMSibling(lastNode);
        } else {
            OMElement e = (OMElement)lastNode;
            node = constructNode((OMElement)lastNode, elementName);
            ((OMContainerEx)e).setFirstChild(node);
        }
        this.processNamespaceData(node);
        processAttributes(node);
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

    /**
     * Method next.
     * 
     * @return Returns int.
     * @throws OMException
     */
    public int next() throws OMException {
        try {
            return super.next();
        } catch (OMException e) {
            // TODO: transforming the OMException here is not ideal!
            throw new ParseException(e);
        }
    }

    protected OMElement constructNode(OMContainer parent, String name) {
        OMElement element = null;
        if (!indoc) {
            parent = document;
            indoc = true;
        }
        QName qname = parser.getName();
        element = fomfactory.createElement(qname, parent, this);
        if (element == null) {
            element = new FOMElement(qname, parent, fomfactory, this);
        }
        return element;
    }

    public <T extends Element> Document<T> getFomDocument() {
        while (!indoc && !done) {
            next();
        }
        return (Document<T>)document;
    }

    public OMDocument getDocument() {
        return (OMDocument)getFomDocument();
    }

    public FOMFactory getFactory() {
        return fomfactory;
    }
}
