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
package org.apache.abdera.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.model.Base;
import org.apache.abdera.xpath.XPath;
import org.apache.abdera.xpath.XPathException;

/**
 * Abstract base implementation of XPath
 */
public abstract class AbstractXPath implements XPath {

    private final Map<String, String> namespaces;

    protected AbstractXPath() {
        this(null);
    }

    protected AbstractXPath(Map<String, String> defaultNamespaces) {
        namespaces = (defaultNamespaces != null) ? defaultNamespaces : initDefaultNamespaces();
    }

    protected Map<String, String> initDefaultNamespaces() {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("a", Constants.ATOM_NS);
        namespaces.put("app", Constants.APP_NS);
        namespaces.put("xhtml", Constants.XHTML_NS);
        return namespaces;
    }

    public Map<String, String> getDefaultNamespaces() {
        return new HashMap<String, String>(namespaces);
    }

    @SuppressWarnings("unchecked")
    public List selectNodes(String path, Base base) throws XPathException {
        return selectNodes(path, base, getDefaultNamespaces());
    }

    public Object selectSingleNode(String path, Base base) throws XPathException {
        return selectSingleNode(path, base, getDefaultNamespaces());
    }

    public Object evaluate(String path, Base base) throws XPathException {
        return evaluate(path, base, getDefaultNamespaces());
    }

    public String valueOf(String path, Base base) throws XPathException {
        return valueOf(path, base, getDefaultNamespaces());
    }

    public boolean booleanValueOf(String path, Base base) throws XPathException {
        return booleanValueOf(path, base, getDefaultNamespaces());
    }

    public Number numericValueOf(String path, Base base) throws XPathException {
        return numericValueOf(path, base, getDefaultNamespaces());
    }

}
