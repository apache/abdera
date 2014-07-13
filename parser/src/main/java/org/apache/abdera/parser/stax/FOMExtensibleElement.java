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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.parser.stax.util.FOMElementIteratorWrapper;
import org.apache.abdera.parser.stax.util.FOMExtensionIterator;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

@SuppressWarnings("unchecked")
public class FOMExtensibleElement extends FOMElement implements ExtensibleElement {

    private static final long serialVersionUID = -1652430686161947531L;

    protected FOMExtensibleElement(String name, OMNamespace namespace, OMContainer parent, OMFactory factory)
        throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMExtensibleElement(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMExtensibleElement(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(localName, parent, factory, builder);
    }

    public List<Element> getExtensions() {
        return new FOMList<Element>(new FOMExtensionIterator(this));
    }

    public List<Element> getExtensions(String uri) {
        return new FOMList<Element>(new FOMExtensionIterator(this, uri));
    }

    public <T extends Element> List<T> getExtensions(QName qname) {
        FOMFactory factory = (FOMFactory)this.getFactory();
        return new FOMList<T>(new FOMElementIteratorWrapper(factory, getChildrenWithName(qname)));
    }

    public <T extends Element> T getExtension(QName qname) {
        FOMFactory factory = (FOMFactory)getFactory();
        T t = (T)this.getFirstChildWithName(qname);
        return (T)((t != null) ? factory.getElementWrapper(t) : null);
    }

    public <T extends ExtensibleElement> T addExtension(Element extension) {
        complete();
        if (extension instanceof ElementWrapper) {
            ElementWrapper wrapper = (ElementWrapper)extension;
            extension = wrapper.getInternal();
        }
        QName qname = extension.getQName();
        String prefix = qname.getPrefix();
        declareIfNecessary(qname.getNamespaceURI(), prefix);
        addChild((OMElement)extension);
        return (T)this;
    }

    public <T extends Element> T addExtension(QName qname) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        String prefix = qname.getPrefix();
        declareIfNecessary(qname.getNamespaceURI(), prefix);
        return (T)fomfactory.newExtensionElement(qname, this);
    }

    public <T extends Element> T addExtension(String namespace, String localpart, String prefix) {
        complete();
        declareIfNecessary(namespace, prefix);
        return (prefix != null) ? (T)addExtension(new QName(namespace, localpart, prefix))
            : (T)addExtension(new QName(namespace, localpart, ""));
    }

    public Element addSimpleExtension(QName qname, String value) {
        complete();
        FOMFactory fomfactory = (FOMFactory)getOMFactory();
        Element el = fomfactory.newElement(qname, this);
        el.setText(value);
        String prefix = qname.getPrefix();
        declareIfNecessary(qname.getNamespaceURI(), prefix);
        return el;
    }

    public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
        complete();
        declareIfNecessary(namespace, prefix);
        return addSimpleExtension((prefix != null) ? new QName(namespace, localPart, prefix) : new QName(namespace,
                                                                                                         localPart),
                                  value);
    }

    public String getSimpleExtension(QName qname) {
        Element el = getExtension(qname);
        return (el != null) ? el.getText() : null;
    }

    public String getSimpleExtension(String namespace, String localPart, String prefix) {
        return getSimpleExtension(new QName(namespace, localPart, prefix));
    }

    public void addExtensions(List<Element> extensions) {
        for (Element e : extensions) {
            addExtension(e);
        }
    }

    /**
     * Trick using Generics to find an extension element without having to pass in it's QName
     */
    public <T extends Element> T getExtension(Class<T> _class) {
        T t = null;
        List<Element> extensions = getExtensions();
        for (Element ext : extensions) {
            if (_class.isAssignableFrom(ext.getClass())) {
                t = (T)ext;
                break;
            }
        }
        return t;
    }

    private Element getInternal(Element element) {
        if (element instanceof ElementWrapper) {
            ElementWrapper wrapper = (ElementWrapper)element;
            element = wrapper.getInternal();
        }
        return element;
    }

    public <T extends ExtensibleElement> T addExtension(Element extension, Element before) {
        complete();
        extension = getInternal(extension);
        before = getInternal(before);
        if (before instanceof ElementWrapper) {
            ElementWrapper wrapper = (ElementWrapper)before;
            before = wrapper.getInternal();
        }
        if (before == null) {
            addExtension(extension);
        } else {
            ((OMElement)before).insertSiblingBefore((OMElement)extension);
        }
        return (T)this;
    }

    public <T extends Element> T addExtension(QName qname, QName before) {
        complete();
        OMElement el = getFirstChildWithName(before);
        T element = (T)getFactory().newElement(qname);
        if (el == null) {
            addExtension(element);
        } else {
            el.insertSiblingBefore((OMElement)getInternal(element));
        }
        return (T)element;
    }
}
