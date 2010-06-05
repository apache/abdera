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
package org.apache.abdera.model;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;

/**
 * ElementWrapper implementation that implements the ExtensibleElement interface. This should be used to create static
 * extension elements that support extensions
 */
@SuppressWarnings("unchecked")
public abstract class ExtensibleElementWrapper extends ElementWrapper implements ExtensibleElement {

    protected ExtensibleElementWrapper(Element internal) {
        super(internal);
    }

    public ExtensibleElementWrapper(Factory factory, QName qname) {
        super(factory, qname);
    }

    protected ExtensibleElement getExtInternal() {
        return (ExtensibleElement)getInternal();
    }

    public <T extends ExtensibleElement> T addExtension(Element extension) {
        getExtInternal().addExtension(extension);
        return (T)this;
    }

    public <T extends Element> T addExtension(QName qname) {
        return (T)getExtInternal().addExtension(qname);
    }

    public <T extends Element> T addExtension(String namespace, String localPart, String prefix) {
        return (T)getExtInternal().addExtension(namespace, localPart, prefix);
    }

    public Element addSimpleExtension(QName qname, String value) {
        return getExtInternal().addSimpleExtension(qname, value);
    }

    public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
        return getExtInternal().addSimpleExtension(namespace, localPart, prefix, value);
    }

    public <T extends Element> T getExtension(QName qname) {
        return (T)getExtInternal().getExtension(qname);
    }

    public <T extends Element> T getExtension(Class<T> _class) {
        return (T)getExtInternal().getExtension(_class);
    }

    public List<Element> getExtensions() {
        return getExtInternal().getExtensions();
    }

    public List<Element> getExtensions(String uri) {
        return getExtInternal().getExtensions(uri);
    }

    public <T extends Element> List<T> getExtensions(QName qname) {
        return getExtInternal().getExtensions(qname);
    }

    public String getSimpleExtension(QName qname) {
        return getExtInternal().getSimpleExtension(qname);
    }

    public String getSimpleExtension(String namespace, String localPart, String prefix) {
        return getExtInternal().getSimpleExtension(namespace, localPart, prefix);
    }

    public boolean getMustPreserveWhitespace() {
        return getExtInternal().getMustPreserveWhitespace();
    }

    public <T extends Element> T setMustPreserveWhitespace(boolean preserve) {
        getExtInternal().setMustPreserveWhitespace(preserve);
        return (T)this;
    }

    public <T extends ExtensibleElement> T addExtension(Element extension, Element before) {
        getExtInternal().addExtension(extension, before);
        return (T)this;
    }

    public <T extends Element> T addExtension(QName qname, QName before) {
        return (T)getExtInternal().addExtension(qname, before);
    }

}
