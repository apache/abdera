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

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Person;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMPerson extends FOMExtensibleElement implements Person {

    private static final long serialVersionUID = 2147684807662492625L;

    protected FOMPerson(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMPerson(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMPerson(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(localName, parent, factory, builder);
    }

    public Element getNameElement() {
        return (Element)getFirstChildWithName(NAME);
    }

    public Person setNameElement(Element element) {
        complete();
        if (element != null)
            _setChild(NAME, (OMElement)element);
        else
            _removeChildren(NAME, false);
        return this;
    }

    public Element setName(String name) {
        complete();
        if (name != null) {
            FOMFactory fomfactory = (FOMFactory)getOMFactory();
            Element el = fomfactory.newName(null);
            el.setText(name);
            _setChild(NAME, (OMElement)el);
            return el;
        } else {
            _removeChildren(NAME, false);
            return null;
        }
    }

    public String getName() {
        Element name = getNameElement();
        return (name != null) ? name.getText() : null;
    }

    public Element getEmailElement() {
        return (Element)getFirstChildWithName(EMAIL);
    }

    public Person setEmailElement(Element element) {
        complete();
        if (element != null)
            _setChild(EMAIL, (OMElement)element);
        else
            _removeChildren(EMAIL, false);
        return this;
    }

    public Element setEmail(String email) {
        complete();
        if (email != null) {
            FOMFactory fomfactory = (FOMFactory)getOMFactory();
            Element el = fomfactory.newEmail(null);
            el.setText(email);
            _setChild(EMAIL, (OMElement)el);
            return el;
        } else {
            _removeChildren(EMAIL, false);
            return null;
        }
    }

    public String getEmail() {
        Element email = getEmailElement();
        return (email != null) ? email.getText() : null;
    }

    public IRIElement getUriElement() {
        return (IRIElement)getFirstChildWithName(URI);
    }

    public Person setUriElement(IRIElement uri) {
        complete();
        if (uri != null)
            _setChild(URI, (OMElement)uri);
        else
            _removeChildren(URI, false);
        return this;
    }

    public IRIElement setUri(String uri) {
        complete();
        if (uri != null) {
            FOMFactory fomfactory = (FOMFactory)getOMFactory();
            IRIElement el = fomfactory.newUri(null);
            el.setValue(uri);
            _setChild(URI, (OMElement)el);
            return el;
        } else {
            _removeChildren(URI, false);
            return null;
        }
    }

    public IRI getUri() {
        IRIElement iri = getUriElement();
        return (iri != null) ? iri.getResolvedValue() : null;
    }
}
