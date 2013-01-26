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
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;
import org.apache.abdera.i18n.rfc4646.Lang;

/**
 * Root interface for all elements in the Feed Object Model
 */
public interface Element extends Base, Iterable<Element> {

    /**
     * Return this Element's parent element or document
     * 
     * @return The parent
     */
    <T extends Base> T getParentElement();

    /**
     * @deprecated This method will cause corruption of the object model because the parent of an
     *             element cannot be set without adding that element as a child.
     */
    <T extends Element> T setParentElement(Element parent);

    /**
     * Get the element preceding this one
     * 
     * @return The preceding sibling
     */
    <T extends Element> T getPreviousSibling();

    /**
     * Get the element following this one
     * 
     * @return The following sibling
     */
    <T extends Element> T getNextSibling();

    /**
     * Get the first child element
     * 
     * @return The first child
     */
    <T extends Element> T getFirstChild();

    /**
     * Get the first previous sibling with the specified QName
     * 
     * @param qname The XML QName of the sibling to find
     * @return The matching element
     */
    <T extends Element> T getPreviousSibling(QName qname);

    /**
     * Get the first following sibling with the specified QName
     * 
     * @param qname The XML QName of the sibling to find
     * @return The matching element
     */
    <T extends Element> T getNextSibling(QName qname);

    /**
     * Get the first child element with the given QName
     * 
     * @param qname The XML QName of the sibling to find
     * @return The matching element
     */
    <T extends Element> T getFirstChild(QName qname);

    /**
     * Return the XML QName of this element
     * 
     * @return The QName of the element
     */
    QName getQName();

    /**
     * Returns the value of this elements <code>xml:lang</code> attribute or null if <code>xml:lang</code> is undefined.
     * 
     * @return The xml:lang value
     */
    String getLanguage();

    /**
     * Returns the value of the xml:lang attribute as a Lang object
     */
    Lang getLanguageTag();

    /**
     * Returns a Locale object created from the <code>xml:lang</code> attribute
     * 
     * @return A Locale appropriate for the Language (xml:lang)
     */
    Locale getLocale();

    /**
     * Sets the value of this elements <code>xml:lang</code> attribute.
     * 
     * @param language the value of the xml:lang element
     */
    <T extends Element> T setLanguage(String language);

    /**
     * Returns the value of this element's <code>xml:base</code> attribute or null if <code>xml:base</code> is
     * undefined.
     * 
     * @return The Base URI
     * @throws IRISyntaxException if the Base URI is malformed
     */
    IRI getBaseUri();

    /**
     * Returns the current in-scope, fully qualified Base URI for this element.
     * 
     * @throws IRISyntaxException if the Base URI is malformed
     */
    IRI getResolvedBaseUri();

    /**
     * Sets the value of this element's <code>xml:base</code> attribute.
     * 
     * @param base The IRI base value
     */
    <T extends Element> T setBaseUri(IRI base);

    /**
     * Sets the value of this element's <code>xml:base</code> attribute.
     * 
     * @param base The Base IRI
     * @throws IRISyntaxException if the base URI is malformed
     */
    <T extends Element> T setBaseUri(String base);

    /**
     * Returns the document to which this element belongs
     * 
     * @return The Document to which this element belongs
     */
    <T extends Element> Document<T> getDocument();

    /**
     * Returns the value of the named attribute
     * 
     * @param name The name of the attribute
     * @return The value of the attribute
     */
    String getAttributeValue(String name);

    /**
     * Returns the value of the named attribute
     * 
     * @param qname The XML QName of the attribute
     * @return The value of the attribute
     */
    String getAttributeValue(QName qname);

    /**
     * Returns a listing of all attributes on this element
     * 
     * @return The listing of attributes for this element
     */
    List<QName> getAttributes();

    /**
     * Returns a listing of extension attributes on this element (extension attributes are attributes whose namespace
     * URI is different than the elements)
     * 
     * @return The listing non-Atom attributes
     */
    List<QName> getExtensionAttributes();

    /**
     * Remove the named Attribute
     * 
     * @param qname The XML QName of the attribute to remove
     */
    <T extends Element> T removeAttribute(QName qname);

    /**
     * Remove the named attribute
     * 
     * @param name The name of the attribute to remove
     */
    <T extends Element> T removeAttribute(String name);

    /**
     * Sets the value of the named attribute
     * 
     * @param name The name of the attribute
     * @param value The value of the attribute
     */
    <T extends Element> T setAttributeValue(String name, String value);

    /**
     * Sets the value of the named attribute
     * 
     * @param qname The XML QName of the attribute
     * @param value The value of the attribute
     */
    <T extends Element> T setAttributeValue(QName qname, String value);

    /**
     * Removes this element from its current document
     */
    void discard();

    /**
     * Returns the Text value of this element
     * 
     * @return The text value
     */
    String getText();

    /**
     * Set the Text value of this element
     * 
     * @param text The text value
     */
    void setText(String text);

    /**
     * Set the Text value of this element using the data handler
     */
    <T extends Element> T setText(DataHandler dataHandler);

    /**
     * Declare a namespace
     */
    <T extends Element> T declareNS(String uri, String prefix);

    /**
     * Return a map listing the xml namespaces declared for this element
     */
    Map<String, String> getNamespaces();

    /**
     * Return a listing of this elements child elements
     */
    <T extends Element> List<T> getElements();

    /**
     * Return true if insignificant whitespace must be preserved
     */
    boolean getMustPreserveWhitespace();

    /**
     * Set to true to preserve insignificant whitespace
     */
    <T extends Element> T setMustPreserveWhitespace(boolean preserve);
}
