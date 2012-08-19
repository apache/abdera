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
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMCategory extends FOMExtensibleElement implements Category {

    private static final long serialVersionUID = -4313042828936786803L;

    protected FOMCategory(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMCategory(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    protected FOMCategory(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(localName, parent, factory, builder);
    }

    protected FOMCategory(OMContainer parent, OMFactory factory) {
        super(CATEGORY, parent, factory);
    }

    public String getTerm() {
        return getAttributeValue(TERM);
    }

    public Category setTerm(String term) {
        complete();
        if (term != null)
            setAttributeValue(TERM, term);
        else
            removeAttribute(TERM);
        return this;
    }

    public IRI getScheme() {
        String value = getAttributeValue(SCHEME);
        return (value != null) ? new IRI(value) : null;
    }

    public Category setScheme(String scheme) {
        complete();
        if (scheme != null)
            setAttributeValue(SCHEME, new IRI(scheme).toString());
        else
            removeAttribute(SCHEME);
        return this;
    }

    public String getLabel() {
        return getAttributeValue(LABEL);
    }

    public Category setLabel(String label) {
        complete();
        if (label != null)
            setAttributeValue(LABEL, label);
        else
            removeAttribute(LABEL);
        return this;
    }

    public String getValue() {
        return getText();
    }

    public void setValue(String value) {
        complete();
        if (value != null)
            ((Element)this).setText(value);
        else
            _removeAllChildren();
    }

}
