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

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * ParseFilter's determine which elements and attributes are acceptable within a parsed document. They are set via the
 * ParserOptions.setParseFilter method.
 */
public interface ParseFilter extends Cloneable, Serializable {

    /**
     * Clone this ParseFilter
     */
    Object clone() throws CloneNotSupportedException;

    /**
     * Returns true if elements with the given QName are acceptable
     */
    boolean acceptable(QName qname);

    /**
     * Returns true if attributes with the given qname appearing on elements with the given qname are acceptable
     */
    boolean acceptable(QName qname, QName attribute);

    /**
     * Return true if the parser should ignore comments
     */
    boolean getIgnoreComments();

    /**
     * Return true if the parser should ignore insignificant whitespace
     */
    boolean getIgnoreWhitespace();

    /**
     * Return true if the parser should ignore processing instructions
     */
    boolean getIgnoreProcessingInstructions();

    /**
     * True if the parser should ignore comments
     */
    ParseFilter setIgnoreComments(boolean ignore);

    /**
     * True if the parser should ignore insignificant whitespace
     */
    ParseFilter setIgnoreWhitespace(boolean ignore);

    /**
     * True if the parser should ignore processing instructions
     */
    ParseFilter setIgnoreProcessingInstructions(boolean ignore);
}
