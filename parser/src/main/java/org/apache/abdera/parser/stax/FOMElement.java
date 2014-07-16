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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRIHelper;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.parser.stax.util.FOMElementIteratorWrapper;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterOptions;
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

@SuppressWarnings("unchecked")
public class FOMElement extends OMElementImpl implements Element, OMElement, Constants {

    private static final long serialVersionUID = 8024257594220911953L;

    protected FOMElement(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(parent, name, namespace, null, factory, true);
    }

    protected FOMElement(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(parent, qname.getLocalPart(), getOrCreateNamespace(qname, parent, factory), null, factory, true);
    }

    protected FOMElement(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(parent, localName, null, builder, factory, false);
    }

    private static OMNamespace getOrCreateNamespace(QName qname, OMContainer parent, OMFactory factory) {
        String namespace = qname.getNamespaceURI();
        String prefix = qname.getPrefix();
        if (parent != null && parent instanceof OMElement) {
            OMNamespace ns = ((OMElement)parent).findNamespace(namespace, prefix);
            if (ns != null)
                return ns;
        }
        return factory.createOMNamespace(qname.getNamespaceURI(), qname.getPrefix());
    }

    protected Element getWrapped(Element internal) {
        if (internal == null)
            return null;
        FOMFactory factory = (FOMFactory)getFactory();
        return factory.getElementWrapper(internal);
    }

    public <T extends Base> T getParentElement() {
        T parent = (T)super.getParent();
        return (T)((parent instanceof Element) ? getWrapped((Element)parent) : parent);
    }

    protected void setParentDocument(Document parent) {
        ((OMContainer)parent).addChild(this);
    }

    public <T extends Element> T setParentElement(Element parent) {
        if (parent instanceof ElementWrapper) {
            parent = ((ElementWrapper)parent).getInternal();
        }
        ((FOMElement)parent).addChild(this);
        return (T)this;
    }

    public <T extends Element> T getPreviousSibling() {
        OMNode el = this.getPreviousOMSibling();
        while (el != null) {
            if (el instanceof Element)
                return (T)getWrapped((Element)el);
            else
                el = el.getPreviousOMSibling();
        }
        return null;
    }

    public <T extends Element> T getNextSibling() {
        OMNode el = this.getNextOMSibling();
        while (el != null) {
            if (el instanceof Element)
                return (T)getWrapped((Element)el);
            else
                el = el.getNextOMSibling();
        }
        return null;
    }

    public <T extends Element> T getFirstChild() {
        return (T)getWrapped((Element)this.getFirstElement());
    }

    public <T extends Element> T getPreviousSibling(QName qname) {
        Element el = getPreviousSibling();
        while (el != null) {
            OMElement omel = (OMElement)el;
            if (omel.getQName().equals(qname))
                return (T)getWrapped((Element)omel);
            el = el.getPreviousSibling();
        }
        return null;
    }

    public <T extends Element> T getNextSibling(QName qname) {
        Element el = getNextSibling();
        while (el != null) {
            OMElement omel = (OMElement)el;
            if (omel.getQName().equals(qname))
                return (T)getWrapped((Element)omel);
            el = el.getNextSibling();
        }
        return null;
    }

    public <T extends Element> T getFirstChild(QName qname) {
        return (T)getWrapped((Element)this.getFirstChildWithName(qname));
    }

    public Lang getLanguageTag() {
        String lang = getLanguage();
        return (lang != null) ? new Lang(lang) : null;
    }

    public String getLanguage() {
        String lang = getAttributeValue(LANG);
        Base parent = this.getParentElement();
        return (lang != null) ? lang : (parent != null && parent instanceof Element) ? ((Element)parent).getLanguage()
            : (parent != null && parent instanceof Document) ? ((Document)parent).getLanguage() : null;
    }

    public <T extends Element> T setLanguage(String language) {
        setAttributeValue(LANG, language);
        return (T)this;
    }

    public IRI getBaseUri() {
        IRI uri = _getUriValue(getAttributeValue(BASE));
        if (IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri)) {
            uri = null;
        }
        if (uri == null) {
            OMContainer parent = getParent();
            if (parent instanceof Element) {
                uri = ((Element)parent).getBaseUri();
            } else if (parent instanceof Document) {
                uri = ((Document)parent).getBaseUri();
            }
        }
        return uri;
    }

    public IRI getResolvedBaseUri() {
        IRI baseUri = null;
        IRI uri = _getUriValue(getAttributeValue(BASE));
        if (IRIHelper.isJavascriptUri(uri) || IRIHelper.isMailtoUri(uri)) {
            uri = null;
        }
        OMContainer parent = getParent();
        if (parent instanceof Element)
            baseUri = ((Element)parent).getResolvedBaseUri();
        else if (parent instanceof Document)
            baseUri = ((Document)parent).getBaseUri();
        if (uri != null && baseUri != null) {
            uri = baseUri.resolve(uri);
        } else if (uri == null) {
            uri = baseUri;
        }
        return uri;
    }

    public <T extends Element> T setBaseUri(IRI base) {
        complete();
        setAttributeValue(BASE, _getStringValue(base));
        return (T)this;
    }

    public <T extends Element> T setBaseUri(String base) {
        setBaseUri((base != null) ? new IRI(base) : null);
        return (T)this;
    }

    public String getAttributeValue(QName qname) {
        OMAttribute attr = getAttribute(qname);
        String value = (attr != null) ? attr.getAttributeValue() : null;
        return getMustPreserveWhitespace() || value == null ? value : value.trim();
    }

    public <T extends Element> T setAttributeValue(QName qname, String value) {
        OMAttribute attr = this.getAttribute(qname);
        if (attr != null && value != null) {
            attr.setAttributeValue(value);
        } else {
            if (value != null) {
                String uri = qname.getNamespaceURI();
                String prefix = qname.getPrefix();
                OMFactory factory = getOMFactory();
                if (uri != null) {
                    OMNamespace ns = findNamespace(uri, prefix);
                    if (ns == null)
                        ns = factory.createOMNamespace(uri, prefix);
                    attr = factory.createOMAttribute(qname.getLocalPart(), ns, value);
                } else {
                    attr = factory.createOMAttribute(qname.getLocalPart(), null, value);
                }
                if (attr != null)
                    addAttribute(attr);
            } else if (attr != null) {
                removeAttribute(attr);
            }
        }
        return (T)this;
    }

    protected <E extends Element> List<E> _getChildrenAsSet(QName qname) {
        FOMFactory factory = (FOMFactory)getFactory();
        return new FOMList(new FOMElementIteratorWrapper(factory, getChildrenWithName(qname)));
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

    protected IRI _getUriValue(String v) {
        return (v != null) ? new IRI(v) : null;
    }

    protected String _getStringValue(IRI uri) {
        return (uri != null) ? uri.toString() : null;
    }

    protected IRI _resolve(IRI base, IRI value) {
        return base != null ? base.resolve(value) : value;
    }

    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
        Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo(this, out, options);
    }

    public void writeTo(java.io.Writer out, WriterOptions options) throws IOException {
        Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo(this, out, options);
    }

    public void writeTo(Writer writer, OutputStream out) throws IOException {
        writer.writeTo(this, out);
    }

    public void writeTo(Writer writer, java.io.Writer out) throws IOException {
        writer.writeTo(this, out);
    }

    public void writeTo(Writer writer, OutputStream out, WriterOptions options) throws IOException {
        writer.writeTo(this, out, options);
    }

    public void writeTo(Writer writer, java.io.Writer out, WriterOptions options) throws IOException {
        writer.writeTo(this, out, options);
    }

    public void writeTo(OutputStream out) throws IOException {
        Document doc = getDocument();
        String charset = doc != null ? doc.getCharset() : "UTF-8";
        Writer writer = this.getFactory().getAbdera().getWriter();
        writeTo(writer, new OutputStreamWriter(out, charset));
    }

    public void writeTo(java.io.Writer writer) throws IOException {
        Writer out = getFactory().getAbdera().getWriter();
        if (!(out instanceof FOMWriter)) {
            out.writeTo(this, writer);
        } else {
            try {
                OMOutputFormat outputFormat = new OMOutputFormat();
                if (getDocument() != null && getDocument().getCharset() != null)
                    outputFormat.setCharSetEncoding(getDocument().getCharset());
                serialize(writer, outputFormat);
            } catch (XMLStreamException e) {
                throw new FOMException(e);
            }
        }
    }

    public <T extends Element> Document<T> getDocument() {
        Document<T> document = null;
        OMContainer parent = getParent();
        if (parent != null) {
            if (parent instanceof Element) {
                document = ((Element)parent).getDocument();
            } else if (parent instanceof Document) {
                document = (Document<T>)parent;
            }
        }
        return document;
    }

    public String getAttributeValue(String name) {
        return getAttributeValue(new QName(name));
    }

    public <T extends Element> T setAttributeValue(String name, String value) {
        setAttributeValue(new QName(name), value);
        return (T)this;
    }

    protected void _setElementValue(QName qname, String value) {
        complete();
        OMElement element = this.getFirstChildWithName(qname);
        if (element != null && value != null) {
            element.setText(value);
        } else if (element != null && value == null) {
            for (Iterator i = element.getChildren(); i.hasNext();) {
                OMNode node = (OMNode)i.next();
                node.discard();
            }
        } else if (element == null && value != null) {
            element = getOMFactory().createOMElement(qname, this);
            element.setText(value);
            this.addChild(element);
        }
    }

    protected String _getElementValue(QName qname) {
        String value = null;
        OMElement element = this.getFirstChildWithName(qname);
        if (element != null)
            value = element.getText();
        return getMustPreserveWhitespace() || value == null ? value : value.trim();
    }

    protected <T extends Text> T getTextElement(QName qname) {
        return (T)getFirstChildWithName(qname);
    }

    protected <T extends Text> void setTextElement(QName qname, T text, boolean many) {
        complete();
        if (text != null) {
            _setChild(qname, (OMElement)text);
        } else
            _removeChildren(qname, false);
    }

    protected Text setTextText(QName qname, String value) {
        if (value == null) {
            setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Text text = fomfactory.newText(qname, Text.Type.TEXT);
        text.setValue(value);
        setTextElement(qname, text, false);
        return text;
    }

    protected Text setHtmlText(QName qname, String value, IRI baseUri) {
        if (value == null) {
            setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Text text = fomfactory.newText(qname, Text.Type.HTML);
        if (baseUri != null)
            text.setBaseUri(baseUri);
        text.setValue(value);
        setTextElement(qname, text, false);
        return text;
    }

    protected Text setXhtmlText(QName qname, String value, IRI baseUri) {
        if (value == null) {
            setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Text text = fomfactory.newText(qname, Text.Type.XHTML);
        if (baseUri != null)
            text.setBaseUri(baseUri);
        text.setValue(value);
        setTextElement(qname, text, false);
        return text;
    }

    protected Text setXhtmlText(QName qname, Div value, IRI baseUri) {
        if (value == null) {
            setTextElement(qname, null, false);
            return null;
        }
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Text text = fomfactory.newText(qname, Text.Type.XHTML);
        if (baseUri != null)
            text.setBaseUri(baseUri);
        text.setValueElement(value);
        setTextElement(qname, text, false);
        return text;
    }

    public void setText(String text) {
        complete();
        if (text != null) {
            OMNode child = this.getFirstOMChild();
            while (child != null) {
                if (child.getType() == OMNode.TEXT_NODE) {
                    child.detach();
                }
                child = child.getNextOMSibling();
            }
            getOMFactory().createOMText(this, text);
        } else
            _removeAllChildren();
        // return (T)this;
    }

    public String getText() {
        StringBuilder buf = new StringBuilder();
        Iterator i = getChildren();
        while (i.hasNext()) {
            OMNode node = (OMNode)i.next();
            if (node instanceof OMText) {
                buf.append(((OMText)node).getText());
            } else {
                // for now, let's ignore other elements. eventually, we
                // should make this work like innerHTML in browsers... stripping
                // out all markup but leaving all text, even in child nodes
            }
        }
        String value = buf.toString();
        return getMustPreserveWhitespace() || value == null ? value : value.trim();
    }

    protected String getText(QName qname) {
        Text text = getTextElement(qname);
        return (text != null) ? text.getValue() : null;
    }

    public List<QName> getAttributes() {
        List<QName> list = new ArrayList<QName>();
        for (Iterator i = getAllAttributes(); i.hasNext();) {
            OMAttribute attr = (OMAttribute)i.next();
            list.add(attr.getQName());
        }
        return Collections.unmodifiableList(list);
    }

    public List<QName> getExtensionAttributes() {
        List<QName> list = new ArrayList<QName>();
        for (Iterator i = getAllAttributes(); i.hasNext();) {
            OMAttribute attr = (OMAttribute)i.next();
            String namespace = (attr.getNamespace() != null) ? attr.getNamespace().getNamespaceURI() : "";
            if (!namespace.equals(getNamespace().getNamespaceURI()) && !namespace.equals(""))
                list.add(attr.getQName());
        }
        return Collections.unmodifiableList(list);
    }

    protected Element _parse(String value, IRI baseUri) throws ParseException, UnsupportedEncodingException {
        if (value == null)
            return null;
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Parser parser = fomfactory.newParser();
        ParserOptions options = parser.getDefaultParserOptions();
        options.setFactory(fomfactory);
        Document doc = parser.parse(new StringReader(value), (baseUri != null) ? baseUri.toString() : null, options);
        return doc.getRoot();
    }

    public <T extends Element> T removeAttribute(QName qname) {
        OMAttribute attr = getAttribute(qname);
        if (attr != null)
            removeAttribute(attr);
        return (T)this;
    }

    public <T extends Element> T removeAttribute(String name) {
        OMAttribute attr = getAttribute(new QName(name));
        if (attr != null)
            getAttribute(new QName(name));
        return (T)this;
    }

    protected void _removeChildren(QName qname, boolean many) {
        complete();
        if (many) {
            for (Iterator i = getChildrenWithName(qname); i.hasNext();) {
                OMElement element = (OMElement)i.next();
                element.discard();
            }
        } else {
            OMElement element = getFirstChildWithName(qname);
            if (element != null)
                element.discard();
        }
    }

    protected void _removeAllChildren() {
        complete();
        for (Iterator i = getChildren(); i.hasNext();) {
            OMNode node = (OMNode)i.next();
            node.discard();
        }
    }

    public Object clone() {
        OMElement el = _create(this);
        _copyElement(this, el);
        return el;

    }

    protected OMElement _copyElement(OMElement src, OMElement dest) {
        OMFactory factory = getOMFactory();
        for (Iterator i = src.getAllAttributes(); i.hasNext();) {
            OMAttribute attr = (OMAttribute)i.next();
            dest.addAttribute(attr);
            dest.addAttribute(factory.createOMAttribute(attr.getLocalName(), attr.getNamespace(), attr
                .getAttributeValue()));
        }
        for (Iterator i = src.getChildren(); i.hasNext();) {
            OMNode node = (OMNode)i.next();
            if (node.getType() == OMNode.ELEMENT_NODE) {
                OMElement element = (OMElement)node;
                OMElement child = _create(element);
                if (child != null) {
                    _copyElement(element, child);
                    dest.addChild(child);
                }
            } else if (node.getType() == OMNode.CDATA_SECTION_NODE) {
                OMText text = (OMText)node;
                factory.createOMText(dest, text.getText(), OMNode.CDATA_SECTION_NODE);
            } else if (node.getType() == OMNode.TEXT_NODE) {
                OMText text = (OMText)node;
                factory.createOMText(dest, text.getText());
            } else if (node.getType() == OMNode.COMMENT_NODE) {
                OMComment comment = (OMComment)node;
                factory.createOMComment(dest, comment.getValue());
            } else if (node.getType() == OMNode.PI_NODE) {
                OMProcessingInstruction pi = (OMProcessingInstruction)node;
                factory.createOMProcessingInstruction(dest, pi.getTarget(), pi.getValue());
            } else if (node.getType() == OMNode.SPACE_NODE) {
                OMText text = (OMText)node;
                factory.createOMText(dest, text.getText(), OMNode.SPACE_NODE);
            } else if (node.getType() == OMNode.ENTITY_REFERENCE_NODE) {
                OMText text = (OMText)node;
                factory.createOMText(dest, text.getText(), OMNode.ENTITY_REFERENCE_NODE);
            }
        }
        return dest;
    }

    protected OMElement _create(OMElement src) {
        OMElement el = null;

        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Object obj = null;
        if (src instanceof Content)
            obj = ((Content)src).getContentType();
        if (src instanceof Text)
            obj = ((Text)src).getTextType();
        el = fomfactory.createElement(src.getQName(), (OMContainer)fomfactory.newDocument(), fomfactory, obj);

        return el;
    }

    public Factory getFactory() {
        return (Factory)this.getOMFactory();
    }

    // This appears to no longer be necessary with Axiom 1.2
    //
    // @Override
    // protected void internalSerialize(
    // XMLStreamWriter writer,
    // boolean bool) throws XMLStreamException {
    // if (this.getNamespace() != null) {
    // this.declareNamespace(this.getNamespace());
    // }
    // Iterator i = this.getAllAttributes();
    // while (i.hasNext()) {
    // OMAttribute attr = (OMAttribute) i.next();
    // if (attr.getNamespace() != null)
    // this.declareNamespace(attr.getNamespace());
    // }
    // super.internalSerialize(writer, bool);
    // }

    public <T extends Base> T addComment(String value) {
        getOMFactory().createOMComment(this, value);
        return (T)this;
    }

    public Locale getLocale() {
        String tag = getLanguage();
        if (tag == null || tag.length() == 0)
            return null;
        String[] tokens = tag.split("-");
        Locale locale = null;
        switch (tokens.length) {
            case 0:
                break;
            case 1:
                locale = new Locale(tokens[0]);
                break;
            case 2:
                locale = new Locale(tokens[0], tokens[1]);
                break;
            default:
                locale = new Locale(tokens[0], tokens[1], tokens[2]);
                break;
        }
        return locale;
    }

    protected Link selectLink(List<Link> links, String type, String hreflang) {
        for (Link link : links) {
            MimeType mt = link.getMimeType();
            boolean typematch = MimeTypeHelper.isMatch((mt != null) ? mt.toString() : null, type);
            boolean langmatch =
                "*".equals(hreflang) || ((hreflang != null) ? hreflang.equals(link.getHrefLang())
                    : link.getHrefLang() == null);
            if (typematch && langmatch)
                return link;
        }
        return null;
    }

    public <T extends Element> T declareNS(String uri, String prefix) {
        if (!isDeclared(uri, prefix)) {
            super.declareNamespace(uri, prefix);
        }
        return (T)this;
    }

    protected boolean isDeclared(String ns, String prefix) {
        for (Iterator i = this.getAllDeclaredNamespaces(); i.hasNext();) {
            OMNamespace omn = (OMNamespace)i.next();
            if (omn.getNamespaceURI().equals(ns) && (omn.getPrefix() != null && omn.getPrefix().equals(prefix)))
                return true;
        }
        Base parent = this.getParentElement();
        if (parent != null && parent instanceof FOMElement) {
            return ((FOMElement)parent).isDeclared(ns, prefix);
        } else
            return false;
    }

    protected void declareIfNecessary(String ns, String prefix) {
        if (prefix != null && !prefix.equals("") && !isDeclared(ns, prefix)) {
            declareNS(ns, prefix);
        }
    }

    public Map<String, String> getNamespaces() {
        Map<String, String> namespaces = new HashMap<String, String>();
        OMElement current = this;
        while (current != null) {
            Iterator i = current.getAllDeclaredNamespaces();
            while (i.hasNext()) {
                OMNamespace ns = (OMNamespace)i.next();
                String prefix = ns.getPrefix();
                String uri = ns.getNamespaceURI();
                if (!namespaces.containsKey(prefix))
                    namespaces.put(prefix, uri);
            }
            OMContainer parent = current.getParent();
            current = (OMElement)((parent != null && parent instanceof OMElement) ? parent : null);
        }
        return Collections.unmodifiableMap(namespaces);
    }

    public <T extends Element> List<T> getElements() {
        return new FOMList<T>(new FOMElementIteratorWrapper((FOMFactory)getOMFactory(), getChildElements()));
    }

    public boolean getMustPreserveWhitespace() {
        OMAttribute attr = getAttribute(SPACE);
        String space = (attr != null) ? attr.getAttributeValue() : null;
        Base parent = this.getParentElement();

        return space != null && space.equalsIgnoreCase("preserve") ? true
            : (parent != null && parent instanceof Element) ? ((Element)parent).getMustPreserveWhitespace()
                : (parent != null && parent instanceof Document) ? ((Document)parent).getMustPreserveWhitespace()
                    : true;
    }

    public <T extends Element> T setMustPreserveWhitespace(boolean preserve) {
        if (preserve && !getMustPreserveWhitespace()) {
            setAttributeValue(SPACE, "preserve");
        } else if (!preserve && getMustPreserveWhitespace()) {
            setAttributeValue(SPACE, "default");
        }
        return (T)this;
    }

    public <T extends Element> T setText(DataHandler handler) {
        _removeAllChildren();
        addChild(getOMFactory().createOMText(handler, true));
        return (T)this;
    }

    public WriterOptions getDefaultWriterOptions() {
        return new FOMWriter().getDefaultWriterOptions();
    }

    /**
     * Ensure that the underlying streams are fully parsed. We might eventually need to find a more efficient way of
     * doing this, but for now, calling toString() will ensure that this particular object is fully parsed and ready to
     * be modified. Calling complete on an Element does not necessarily mean that the underlying stream is fully
     * consumed, only that that particular element has been completely parsed.
     */
    public <T extends Base> T complete() {
        if (!isComplete() && builder != null)
            super.build();
        return (T)this;
    }

    /**
     * Iterate over all child elements
     */
    public Iterator<Element> iterator() {
        return getElements().iterator();
    }

    public void writeTo(String writer, OutputStream out) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    public void writeTo(String writer, java.io.Writer out) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    public void writeTo(String writer, OutputStream out, WriterOptions options) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    public void writeTo(String writer, java.io.Writer out, WriterOptions options) throws IOException {
        writeTo(getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    public String toFormattedString() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeTo("prettyxml", out);
            return new String(out.toByteArray(), "UTF-8");
        } catch (Exception e) {
            return toString();
        }
    }
}
