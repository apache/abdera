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
package org.apache.abdera.model;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * An abstract element that can be extended with namespaced child elements
 */
public interface ExtensibleElement extends Element {

    /**
     * Returns the complete set of extension elements
     * 
     * @return a listing of extensions
     */
    List<Element> getExtensions();

    /**
     * Returns the complete set of extension elements using the specified XML Namespace URI
     * 
     * @param uri A namespace URI
     * @return A listing of extensions using the specified XML namespace
     */
    List<Element> getExtensions(String uri);

    /**
     * Returns the complete set of extension elements using the specified XML qualified name
     * 
     * @param qname An XML QName
     * @return A listing of extensions with the specified QName
     */
    <T extends Element> List<T> getExtensions(QName qname);

    /**
     * Returns the first extension element with the XML qualified name
     * 
     * @param qname An XML QName
     * @return An extension with the specified qname
     */
    <T extends Element> T getExtension(QName qname);

    /**
     * Adds an individual extension element
     * 
     * @param extension An extension element to add
     */
    <T extends ExtensibleElement> T addExtension(Element extension);

    /**
     * Adds an individual extension element before the specified element
     */
    <T extends ExtensibleElement> T addExtension(Element extension, Element before);

    /**
     * Adds an individual extension element
     * 
     * @param qname An extension element to create
     * @return The newly created extension element
     */
    <T extends Element> T addExtension(QName qname);

    /**
     * Adds an individual extension element
     * 
     * @param qname An extension element to create
     * @return The newly created extension element
     */
    <T extends Element> T addExtension(QName qname, QName before);

    /**
     * Adds an individual extension element
     * 
     * @param namespace An XML namespace
     * @param localPart A localname
     * @param prefix A XML namespace prefix
     * @return The newly creatd extension element
     */
    <T extends Element> T addExtension(String namespace, String localPart, String prefix);

    /**
     * Adds a simple extension (text content only)
     * 
     * @param qname An XML QName
     * @param value The simple text value of the element
     * @return The newly created extension element
     */
    Element addSimpleExtension(QName qname, String value);

    /**
     * Adds a simple extension (text content only)
     * 
     * @param namespace An XML namespace
     * @param localPart A local name
     * @param prefix A namespace prefix
     * @param value The simple text value
     * @return The newly created extension element
     */
    Element addSimpleExtension(String namespace, String localPart, String prefix, String value);

    /**
     * Gets the value of a simple extension
     * 
     * @param qname An XML QName
     * @return The string value of the extension
     */
    String getSimpleExtension(QName qname);

    /**
     * Gets the value of a simple extension
     * 
     * @param namespace An XML namespace
     * @param localPart A localname
     * @param prefix A namespace prefix
     * @return The string value of the extension
     */
    String getSimpleExtension(String namespace, String localPart, String prefix);

    /**
     * Find an extension by Class rather than QName
     * 
     * @param _class The implementation class of the extension
     * @return The extension element
     */
    <T extends Element> T getExtension(Class<T> _class);
}
