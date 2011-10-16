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

import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

import javax.xml.namespace.QName;

public class FOMExtensionIterator extends OMFilterIterator {

    /**
     * Field givenQName
     */
    private String namespace = null;
    private String extns = null;
    private FOMFactory factory = null;

    /**
     * Constructor OMChildrenQNameIterator.
     * 
     * @param parent
     * @param givenQName
     */
    public FOMExtensionIterator(OMElement parent) {
        super(parent.getChildren());
        this.namespace = parent.getQName().getNamespaceURI();
        this.factory = (FOMFactory)parent.getOMFactory();
    }

    public FOMExtensionIterator(OMElement parent, String extns) {
        this(parent);
        this.extns = extns;
    }

    @Override
    public Object next() {
        return factory.getElementWrapper((Element)super.next());
    }

    @Override
    protected boolean matches(OMNode node) {
        return (node instanceof OMElement) && (isQNamesMatch(((OMElement)node).getQName(),
                this.namespace));
    }

    private boolean isQNamesMatch(QName elementQName, String namespace) {
        String elns = elementQName == null ? "" : elementQName.getNamespaceURI();
        boolean namespaceURIMatch = (namespace == null) || (namespace == "") || elns.equals(namespace);
        if (!namespaceURIMatch && extns != null && !elns.equals(extns))
            return false;
        else
            return !namespaceURIMatch;
    }

}
