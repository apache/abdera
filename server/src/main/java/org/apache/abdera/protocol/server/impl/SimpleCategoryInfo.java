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
package org.apache.abdera.protocol.server.impl;

import java.io.Serializable;

import org.apache.abdera.model.Category;
import org.apache.abdera.protocol.server.CategoryInfo;
import org.apache.abdera.protocol.server.RequestContext;

public class SimpleCategoryInfo implements CategoryInfo, Serializable {

    private static final long serialVersionUID = -4013333222147077975L;
    private final String label;
    private final String term;
    private final String scheme;

    public SimpleCategoryInfo(String term) {
        this(term, null, null);
    }

    public SimpleCategoryInfo(String term, String scheme) {
        this(term, scheme, null);
    }

    public SimpleCategoryInfo(String term, String scheme, String label) {
        this.term = term;
        this.scheme = scheme;
        this.label = label;
    }

    public String getLabel(RequestContext request) {
        return label;
    }

    public String getScheme(RequestContext request) {
        return scheme;
    }

    public String getTerm(RequestContext request) {
        return term;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
        result = prime * result + ((term == null) ? 0 : term.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SimpleCategoryInfo other = (SimpleCategoryInfo)obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (scheme == null) {
            if (other.scheme != null)
                return false;
        } else if (!scheme.equals(other.scheme))
            return false;
        if (term == null) {
            if (other.term != null)
                return false;
        } else if (!term.equals(other.term))
            return false;
        return true;
    }

    public Category asCategoryElement(RequestContext request) {
        Category cat = request.getAbdera().getFactory().newCategory();
        cat.setTerm(term);
        if (scheme != null)
            cat.setScheme(scheme);
        if (label != null)
            cat.setLabel(label);
        return cat;
    }
}
