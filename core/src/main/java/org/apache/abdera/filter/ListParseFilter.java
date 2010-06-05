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
package org.apache.abdera.filter;

import javax.xml.namespace.QName;

/**
 * A ParseFilter that is based on an internal collection of QName's.
 */
public interface ListParseFilter extends ParseFilter {

    /**
     * Add an element QName to the parse filter
     */
    ListParseFilter add(QName qname);

    /**
     * Returns true if the given qname has been added to the filter
     */
    boolean contains(QName qname);

    /**
     * Adds an attribute to the parse filter
     */
    ListParseFilter add(QName parent, QName attribute);

    /**
     * Returns true if the given attribute has been added to the filter
     */
    boolean contains(QName qname, QName attribute);

}
