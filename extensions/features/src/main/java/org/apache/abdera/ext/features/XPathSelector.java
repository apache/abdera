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
package org.apache.abdera.ext.features;

import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Collection;
import org.apache.abdera.xpath.XPath;

/**
 * Selects a collection based on a boolean XPath expression
 */
public class XPathSelector extends AbstractSelector implements Selector {

    private static final long serialVersionUID = 7751803876821166591L;

    private final XPath xpath;
    private final Map<String, String> namespaces;
    private final String path;

    public XPathSelector(String path) {
        this(path, (new Abdera()).getXPath());
    }

    public XPathSelector(String path, XPath xpath) {
        this(path, xpath, xpath.getDefaultNamespaces());
    }

    public XPathSelector(String path, XPath xpath, Map<String, String> namespaces) {
        this.path = path;
        this.xpath = xpath;
        this.namespaces = namespaces;
        if (!this.namespaces.containsValue(FeaturesHelper.FNS)) {
            int c = 0;
            String p = "f";
            if (!this.namespaces.containsKey(p)) {
                this.namespaces.put(p, FeaturesHelper.FNS);
            } else {
                String s = p + c;
                while (this.namespaces.containsKey(s)) {
                    c++;
                    s = p + c;
                }
                this.namespaces.put(s, FeaturesHelper.FNS);
            }
        }
    }

    public String getFeaturesPrefix() {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getValue().equals(FeaturesHelper.FNS))
                return entry.getKey();
        }
        return null;
    }

    public boolean select(Collection collection) {
        if (xpath.booleanValueOf(path, collection, namespaces)) {
            return true;
        }
        return false;
    }

    public void addNamespace(String prefix, String uri) {
        namespaces.put(prefix, uri);
    }
}
