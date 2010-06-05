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
package org.apache.abdera.xpath;

import java.util.List;
import java.util.Map;

import org.apache.abdera.model.Base;

/**
 * Used to execute XPath queries over Feed Object Model instances.
 */
@SuppressWarnings("unchecked")
public interface XPath {

    /**
     * Return the default mapping of Prefixes to XML Namespaces
     */
    Map<String, String> getDefaultNamespaces();

    /**
     * Return a listing of nodes matching the specified Path
     */
    List selectNodes(String path, Base base) throws XPathException;

    /**
     * Return the first node matching the specified Path
     */
    Object selectSingleNode(String path, Base base) throws XPathException;

    /**
     * Evaluate the specified XPath and return it's value
     */
    Object evaluate(String path, Base base) throws XPathException;

    /**
     * Return the text value of the specified Path
     */
    String valueOf(String path, Base base) throws XPathException;

    /**
     * Return a boolean representation of the specified Path
     */
    boolean booleanValueOf(String path, Base base) throws XPathException;

    /**
     * Return a numeric representation of the specified Path
     */
    Number numericValueOf(String path, Base base) throws XPathException;

    /**
     * Return a listing of nodes matching the specified Path using the specified Namespaces mapping
     */
    List selectNodes(String path, Base base, Map<String, String> namespaces) throws XPathException;

    /**
     * Return a the first node matching the specified Path using the specified Namespaces mapping
     */
    Object selectSingleNode(String path, Base base, Map<String, String> namespaces) throws XPathException;

    /**
     * Evaluate the specified XPath and return it's value using the specified Namespaces mapping
     */
    Object evaluate(String path, Base base, Map<String, String> namespaces) throws XPathException;

    /**
     * Return the text value of the specified Path using the specified Namespaces mapping
     */
    String valueOf(String path, Base base, Map<String, String> namespaces) throws XPathException;

    /**
     * Return a boolean representation of the specified Path using the specified Namespaces mapping
     */
    boolean booleanValueOf(String path, Base base, Map<String, String> namespaces) throws XPathException;

    /**
     * Return a numeric representation of the specified Path using the specified Namespaces mapping
     */
    Number numericValueOf(String path, Base base, Map<String, String> namespaces) throws XPathException;

}
