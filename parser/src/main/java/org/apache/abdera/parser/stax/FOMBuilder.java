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
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Constants;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.util.OMSerializerUtil;

@SuppressWarnings( {"unchecked", "deprecation"})
public class FOMBuilder extends StAXOMBuilder implements Constants {

    private final FOMFactory fomfactory;
    private final ParserOptions parserOptions;
    private boolean indoc = false;
    private int depthInSkipElement = 0;
    private boolean ignoreWhitespace = false;
    private boolean ignoreComments = false;
    private boolean ignorePI = false;

    public FOMBuilder(FOMFactory factory, XMLStreamReader parser, ParserOptions parserOptions) {
        super(factory, parser);
        this.document = (OMDocument)factory.newDocument();
        this.parserOptions = parserOptions;
        this.fomfactory = factory;
        String enc = parser.getCharacterEncodingScheme();
        document.setCharsetEncoding(enc != null ? enc : "utf-8");
        document.setXMLVersion(parser.getVersion() != null ? parser.getVersion() : "1.0");
        if (parserOptions != null) {
            ParseFilter parseFilter = parserOptions.getParseFilter();
            if (parseFilter != null) {
                ignoreWhitespace = parseFilter.getIgnoreWhitespace();
                ignoreComments = parseFilter.getIgnoreComments();
                ignorePI = parseFilter.getIgnoreProcessingInstructions();
            }
        }
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

    private boolean isAcceptableToParse(QName qname, boolean attribute) {
        if (parserOptions == null)
            return true;
        ParseFilter filter = parserOptions.getParseFilter();
        return (filter != null) ? (!attribute) ? filter.acceptable(qname) : filter.acceptable(parser.getName(), qname)
            : true;
    }

    private OMNode applyTextFilter(int type) {
        if (parserOptions != null) {
            ParseFilter parseFilter = parserOptions.getParseFilter();
            if (parseFilter != null) {
                if (parser.isWhiteSpace() && parseFilter.getIgnoreWhitespace())
                    return createOMText("", type);
            }
        }
        return createOMText(type);
    }

    private int getNextElementToParse() throws XMLStreamException {
        int token = parser.next();
        if (depthInSkipElement == 0 && token != XMLStreamConstants.START_ELEMENT) {
            return token;
        } else if (token == XMLStreamConstants.START_ELEMENT && isAcceptableToParse(parser.getName(), false)
            && depthInSkipElement == 0) {
            return token;
        } else if (token == XMLStreamConstants.START_ELEMENT) {
            depthInSkipElement++;
        } else if (token == XMLStreamConstants.END_ELEMENT) { // otherwise skip like crazy
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
                    document.setXMLVersion(parser.getVersion() != null ? parser.getVersion() : "1.0");
                    document.setCharsetEncoding(parser.getEncoding() != null ? parser.getEncoding() : "utf-8");
                    document.setStandalone(parser.isStandalone() ? YES : NO);
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
                    ((OMContainerEx)this.document).setComplete(true);
                    break;
                case XMLStreamConstants.SPACE:
                    if (!ignoreWhitespace)
                        lastNode = createOMText(XMLStreamConstants.SPACE);
                    break;
                case XMLStreamConstants.COMMENT:
                    if (!ignoreComments)
                        createComment();
                    break;
                case XMLStreamConstants.DTD:
                    // Current StAX cursor model implementations inconsistently handle DTDs.
                    // Woodstox, for instance, does not provide a means of getting to the complete
                    // doctype declaration (which is actually valid according to the spec, which
                    // is broken). The StAX reference impl returns the complete doctype declaration
                    // despite the fact that doing so is apparently against the spec. We can get
                    // to the complete declaration in Woodstox if we want to use their proprietary
                    // extension APIs. It's unclear how other Stax impls handle this. So.. for now,
                    // we're just going to ignore the DTD. The DTD will still be processed as far
                    // as entities are concerned, but we will not be able to reserialize the parsed
                    // document with the DTD. Since very few folks actually use DTD's in feeds
                    // right now (and we should likely be encouraging folks not to do so), this
                    // shouldn't be that big of a problem
                    // if (!parserOptions.getIgnoreDoctype())
                    // createDTD();
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    if (!ignorePI)
                        createPI();
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    String val = parserOptions.resolveEntity(super.getName());
                    if (val == null)
                        throw new ParseException("Unresolved undeclared entity: " + super.getName());
                    else
                        lastNode = createOMText(val, XMLStreamConstants.CHARACTERS);
                    break;
                default:
                    throw new ParseException();
            }
            return token;
        } catch (ParseException e) {
            throw e;
        } catch (OMException e) {
            throw new ParseException(e);
        } catch (Exception e) {
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
            node = createOMText(value, (OMElement)lastNode, textType);
        } else {
            OMContainer parent = lastNode.getParent();
            if (!(parent instanceof OMDocument)) {
                node = createOMText(value, (OMElement)parent, textType);
            }
        }
        return node;
    }

    /**
     * This method will check whether the text can be optimizable using IS_BINARY flag. If that is set then we try to
     * get the data handler.
     * 
     * @param omElement
     * @param textType
     * @return omNode
     */
    private OMNode createOMText(String value, OMElement omElement, int textType) {
        try {
            // TODO:Check on this. I'm not sure it's actually used
            // if (isDataHandlerAware && Boolean.TRUE == parser.getProperty(OMConstants.IS_BINARY)) {
            if (Boolean.TRUE == parser.getProperty(OMConstants.IS_BINARY)) {
                Object dataHandler = parser.getProperty(OMConstants.DATA_HANDLER);
                OMText text = new FOMTextValue(dataHandler, true, (OMFactory)this);
                omElement.addChild(text);
                return text;
            } else {
                return new FOMTextValue(omElement, value, textType, (OMFactory)this.fomfactory);
            }
        } catch (IllegalArgumentException e) {
            return new FOMTextValue(omElement, value, textType, (OMFactory)this.fomfactory);
        }
    }
}
