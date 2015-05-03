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

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.URLDataSource;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.util.Constants;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.util.base64.Base64Utils;

@SuppressWarnings("unchecked")
public class FOMContent extends FOMExtensibleElement implements Content {

    private static final long serialVersionUID = -5499917654824498563L;
    protected Type type = Type.TEXT;

    protected FOMContent(String name, OMNamespace namespace, Type type, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
        init(type);
    }

    protected FOMContent(QName qname, Type type, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
        init(type);
    }

    protected FOMContent(String localName, Type type, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
        init(type);
    }

    protected FOMContent(Type type, OMContainer parent, OMFactory factory) throws OMException {
        super(CONTENT, parent, factory);
        init(type);
    }

    private void init(Type type) {
        this.type = type;
        if (Type.TEXT.equals(type))
            setAttributeValue(TYPE, "text");
        else if (Type.HTML.equals(type))
            setAttributeValue(TYPE, "html");
        else if (Type.XHTML.equals(type))
            setAttributeValue(TYPE, "xhtml");
        else if (Type.XML.equals(type))
            setAttributeValue(TYPE, "application/xml");
        else {
            removeAttribute(TYPE);
        }
    }

    public final Type getContentType() {
        return type;
    }

    public Content setContentType(Type type) {
        complete();
        init(type);
        return this;
    }

    public <T extends Element> T getValueElement() {
        FOMFactory factory = (FOMFactory)getFactory();
        return (T)factory.getElementWrapper((Element)this.getFirstElement());
    }

    public <T extends Element> Content setValueElement(T value) {
        complete();
        if (value != null) {
            if (this.getFirstElement() != null)
                this.getFirstElement().discard();

            MimeType mtype = this.getMimeType();
            if (mtype == null) {
                String mt = getFactory().getMimeType(value);
                if (mt != null) {
                    setMimeType(mt);
                    mtype = getMimeType();
                }
            }

            if (value instanceof Div && !type.equals(Content.Type.XML))
                init(Content.Type.XHTML);
            else {
                if (mtype == null) {
                    init(Content.Type.XML);
                }
            }
            OMElement el = (OMElement)(value instanceof ElementWrapper ? ((ElementWrapper)value).getInternal() : value);
            removeChildren();
            addChild(el);
        } else {
            _removeAllChildren();
        }
        return this;
    }

    public MimeType getMimeType() {
        MimeType type = null;
        String mimeType = getAttributeValue(TYPE);
        if (mimeType != null) {
            try {
                type = new MimeType(mimeType);
            } catch (Exception e) {
            }
        }
        return type;
    }

    public Content setMimeType(String type) {
        complete();
        try {
            if (type != null)
                setAttributeValue(TYPE, (new MimeType(type)).toString());
            else
                removeAttribute(TYPE);
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
        return this;
    }

    public IRI getSrc() {
        return _getUriValue(getAttributeValue(SRC));
    }

    public IRI getResolvedSrc() {
        return _resolve(getResolvedBaseUri(), getSrc());
    }

    public Content setSrc(String src) {
        complete();
        if (src != null)
            setAttributeValue(SRC, (new IRI(src)).toString());
        else
            removeAttribute(SRC);
        return this;
    }

    public DataHandler getDataHandler() {
        if (!Type.MEDIA.equals(type))
            throw new UnsupportedOperationException(Localizer.get("DATA.HANDLER.NOT.SUPPORTED"));
        MimeType type = getMimeType();
        java.net.URL src = null;
        try {
            src = getSrc().toURL();
        } catch (Exception e) {
        }
        DataHandler dh = null;
        if (src == null) {
            dh = new DataHandler(new ByteArrayDataSource(
                    Base64Utils.decode(getText()),
                    (type != null) ? type.toString() : null));
        } else {
            dh = new DataHandler(new URLDataSource(src));
        }
        return dh;
    }

    public Content setDataHandler(DataHandler dataHandler) {
        complete();
        if (!Type.MEDIA.equals(type))
            throw new IllegalArgumentException();
        if (dataHandler.getContentType() != null) {
            try {
                setMimeType(dataHandler.getContentType());
            } catch (Exception e) {
            }
        }
        _removeAllChildren();
        addChild(getOMFactory().createOMText(dataHandler, true));
        return this;
    }

    public String getValue() {
        String val = null;
        if (Type.TEXT.equals(type)) {
            val = getText();
        } else if (Type.HTML.equals(type)) {
            val = getText();
        } else if (Type.XHTML.equals(type)) {
            FOMDiv div = (FOMDiv)this.getFirstChildWithName(Constants.DIV);
            if (div != null)
                val = div.getInternalValue();
        } else if (Type.XML.equals(type)) {
            OMElement el = this.getFirstElement();
            if (el != null)
                val = el.toString();
        } else if (Type.MEDIA.equals(type)) {
            val = getText();
        }
        return val;
    }

    public <T extends Element> T setText(Content.Type type, String value) {
        complete();
        init(type);
        if (value != null) {
            OMNode child = this.getFirstOMChild();
            while (child != null) {
                if (child.getType() == OMNode.TEXT_NODE) {
                    child.detach();
                }
                child = child.getNextOMSibling();
            }
            getOMFactory().createOMText(this, value);
        } else
            _removeAllChildren();
        return (T)this;
    }

    public <T extends Element> T setText(String value) {
        return (T)setText(Content.Type.TEXT, value);
    }

    public Content setValue(String value) {
        complete();
        if (value != null)
            removeAttribute(SRC);
        if (value != null) {
            if (Type.TEXT.equals(type)) {
                _removeAllChildren();
                setText(type, value);
            } else if (Type.HTML.equals(type)) {
                _removeAllChildren();
                setText(type, value);
            } else if (Type.XHTML.equals(type)) {
                IRI baseUri = null;
                Element element = null;
                value = "<div xmlns=\"" + XHTML_NS + "\">" + value + "</div>";
                try {
                    baseUri = getResolvedBaseUri();
                    element = _parse(value, baseUri);
                } catch (Exception e) {
                }
                if (element != null && element instanceof Div)
                    setValueElement((Div)element);
            } else if (Type.XML.equals(type)) {
                IRI baseUri = null;
                Element element = null;
                try {
                    baseUri = getResolvedBaseUri();
                    element = _parse(value, baseUri);
                } catch (Exception e) {
                }
                if (element != null)
                    setValueElement(element);
                try {
                    if (getMimeType() == null)
                        setMimeType("application/xml");
                } catch (Exception e) {
                }
            } else if (Type.MEDIA.equals(type)) {
                _removeAllChildren();
                setText(type, value);
                try {
                    if (getMimeType() == null)
                        setMimeType("text/plain");
                } catch (Exception e) {
                }
            }
        } else {
            _removeAllChildren();
        }
        return this;
    }

    public String getWrappedValue() {
        if (Type.XHTML.equals(type)) {
            return this.getFirstChildWithName(Constants.DIV).toString();
        } else {
            return getText();
        }
    }

    public Content setWrappedValue(String wrappedValue) {
        complete();
        if (Type.XHTML.equals(type)) {
            IRI baseUri = null;
            Element element = null;
            try {
                baseUri = getResolvedBaseUri();
                element = _parse(wrappedValue, baseUri);
            } catch (Exception e) {
            }
            if (element != null && element instanceof Div)
                setValueElement((Div)element);
        } else {
            ((Element)this).setText(wrappedValue);
        }
        return this;
    }

    @Override
    public IRI getBaseUri() {
        if (Type.XHTML.equals(type)) {
            Element el = getValueElement();
            if (el != null) {
                if (el.getAttributeValue(BASE) != null) {
                    if (getAttributeValue(BASE) != null)
                        return super.getBaseUri().resolve(el.getAttributeValue(BASE));
                    else
                        return _getUriValue(el.getAttributeValue(BASE));
                }
            }
        }
        return super.getBaseUri();
    }

    @Override
    public IRI getResolvedBaseUri() {
        if (Type.XHTML.equals(type)) {
            Element el = getValueElement();
            if (el != null) {
                if (el.getAttributeValue(BASE) != null) {
                    return super.getResolvedBaseUri().resolve(el.getAttributeValue(BASE));
                }
            }
        }
        return super.getResolvedBaseUri();
    }

    @Override
    public String getLanguage() {
        if (Type.XHTML.equals(type)) {
            Element el = getValueElement();
            if (el.getAttributeValue(LANG) != null)
                return el.getAttributeValue(LANG);
        }
        return super.getLanguage();
    }

    @Override
    public Object clone() {
        FOMContent content = (FOMContent)super.clone();
        content.type = this.type;
        return content;
    }

}
