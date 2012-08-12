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

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.filter.ParseFilter;
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
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.util.OMSerializerUtil;

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
        if (parserOptions == null)
            return true;
        ParseFilter filter = parserOptions.getParseFilter();
        return (filter != null) ? (!attribute) ? filter.acceptable(qname) : filter.acceptable(parser.getName(), qname)
            : true;
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

    private QName getAlias(QName qname) {
        Map<QName, QName> map = parserOptions.getQNameAliasMap();
        if (map == null)
            return qname;
        QName alias = map.get(qname);
        return alias != null ? alias : qname;
    }

    @Override
    protected OMElement constructNode(OMContainer parent, String name) {
        OMElement element = null;
        if (!indoc) {
            parent = document;
            indoc = true;
        }
        QName qname = parser.getName();
        if (parserOptions.isQNameAliasMappingEnabled()) {
            qname = getAlias(qname);
        }
        element = fomfactory.createElement(qname, parent, this);
        if (element == null) {
            element = new FOMElement(qname, parent, fomfactory, this);
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
                String value = parser.getAttributeValue(i);
                node.addAttribute(parser.getAttributeLocalName(i), value, namespace);
            }
        }
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
