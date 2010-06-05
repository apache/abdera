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
package org.apache.abdera.util.filter;

import javax.xml.namespace.QName;

/**
 * WhiteList Implementation of ParseFilter. Only the QNames listed will be considered acceptable
 */
public class WhiteListParseFilter extends AbstractListParseFilter {

    private static final long serialVersionUID = -2126524829459798481L;
    private final boolean listAttributesExplicitly;

    public WhiteListParseFilter() {
        this(false);
    }

    /**
     * If listAttributesExplicity == true, attributes MUST be whitelisted independently of the elements on which they
     * appear, otherwise, all attributes will automatically be considered acceptable if the containing element is
     * considered acceptable.
     */
    public WhiteListParseFilter(boolean listAttributesExplicitly) {
        this.listAttributesExplicitly = listAttributesExplicitly;
    }

    public boolean acceptable(QName qname) {
        return contains(qname);
    }

    public boolean acceptable(QName qname, QName attribute) {
        return (listAttributesExplicitly) ? contains(qname, attribute) && acceptable(qname) : acceptable(qname);
    }

}
