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
package org.apache.abdera.parser.stax.util;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

@SuppressWarnings("unchecked")
public class FOMElementIterator extends OMFilterIterator {

    /**
     * Field givenQName
     */
    protected QName attribute = null;
    protected String value = null;
    protected String defaultValue = null;
    protected Class _class = null;

    /**
     * Constructor OMChildrenQNameIterator.
     * 
     * @param parent
     * @param givenQName
     */
    public FOMElementIterator(Element parent, Class _class) {
        super(((OMElement)parent).getChildren());
        this._class = _class;
    }

    public FOMElementIterator(Element parent, Class _class, QName attribute, String value, String defaultValue) {
        this(parent, _class);
        this.attribute = attribute;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    @Override
    protected boolean matches(OMNode node) {
        return ((_class != null && _class.isAssignableFrom(node.getClass())) || _class == null) && isMatch((Element)node);
    }

    protected boolean isMatch(Element el) {
        if (attribute != null) {
            String val = el.getAttributeValue(attribute);
            return ((val == null && value == null) || (val == null && value != null && value.equals(defaultValue)) || (val != null && val
                .equals(value)));
        }
        return true;
    }
}
