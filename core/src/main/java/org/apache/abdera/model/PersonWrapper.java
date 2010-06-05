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

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.util.Constants;

/**
 * ElementWrapper implementation that implements the Person interface. Used to create static extensions based on the
 * Atom Person Construct
 */
public abstract class PersonWrapper extends ExtensibleElementWrapper implements Person, Constants {

    protected PersonWrapper(Element internal) {
        super(internal);
    }

    public PersonWrapper(Factory factory, QName qname) {
        super(factory, qname);
    }

    public String getEmail() {
        Element email = getEmailElement();
        return (email != null) ? email.getText() : null;
    }

    public Element getEmailElement() {
        return getInternal().getFirstChild(EMAIL);
    }

    public String getName() {
        Element name = getNameElement();
        return (name != null) ? name.getText() : null;
    }

    public Element getNameElement() {
        return getInternal().getFirstChild(NAME);
    }

    public IRI getUri() {
        IRIElement iri = getUriElement();
        return (iri != null) ? iri.getResolvedValue() : null;
    }

    public IRIElement getUriElement() {
        return getInternal().getFirstChild(URI);
    }

    public Element setEmail(String email) {
        ExtensibleElement internal = getExtInternal();
        Element el = getEmailElement();
        if (email != null) {
            if (el == null)
                el = internal.getFactory().newEmail(internal);
            el.setText(email);
            return el;
        } else {
            if (el != null)
                el.discard();
            return null;
        }
    }

    public Person setEmailElement(Element element) {
        ExtensibleElement internal = getExtInternal();
        Element el = getEmailElement();
        if (el != null)
            el.discard();
        if (element != null)
            internal.addExtension(element);
        return this;
    }

    public Element setName(String name) {
        ExtensibleElement internal = getExtInternal();
        Element el = getNameElement();
        if (name != null) {
            if (el == null)
                el = internal.getFactory().newName(internal);
            el.setText(name);
            return el;
        } else {
            if (el != null)
                el.discard();
            return null;
        }
    }

    public Person setNameElement(Element element) {
        ExtensibleElement internal = getExtInternal();
        Element el = getNameElement();
        if (el != null)
            el.discard();
        if (element != null)
            internal.addExtension(element);
        return this;
    }

    public IRIElement setUri(String uri) {
        ExtensibleElement internal = getExtInternal();
        IRIElement el = getUriElement();
        if (uri != null) {
            if (el == null)
                el = internal.getFactory().newUri(internal);
            el.setText(uri.toString());
            return el;
        } else {
            if (el != null)
                el.discard();
            return null;
        }
    }

    public Person setUriElement(IRIElement element) {
        ExtensibleElement internal = getExtInternal();
        Element el = getUriElement();
        if (el != null)
            el.discard();
        if (element != null)
            internal.addExtension(element);
        return this;
    }

}
