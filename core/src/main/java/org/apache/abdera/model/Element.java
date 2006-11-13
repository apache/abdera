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

import javax.xml.namespace.QName;

import org.apache.abdera.util.iri.IRI;
import org.apache.abdera.util.iri.IRISyntaxException;

/**
 * Root interface for all elements in the Feed Object Model
 */
public interface Element 
  extends Base {
  
  /**
   * Return this Element's parent element or document
   * @return The parent
   */
  <T extends Base>T getParentElement();
  
  /**
   * Set this Element's parent element
   * @param parent The parent element
   */
  void setParentElement(Element parent);
  
  /**
   * Get the element preceding this one
   * @return The preceding sibling
   */
  <T extends Element>T getPreviousSibling();
  
  /**
   * Get the element following this one
   * @return The following sibling
   */
  <T extends Element>T getNextSibling();
  
  /**
   * Get the first child element
   * @return The first child
   */
  <T extends Element>T getFirstChild();
  
  /**
   * Get the first previous sibling with the specified QName
   * @param qname The XML QName of the sibling to find
   * @return The matching element
   */
  <T extends Element>T getPreviousSibling(QName qname);

  /**
   * Get the first following sibling with the specified QName
   * @param qname The XML QName of the sibling to find
   * @return The matching element
   */
  <T extends Element>T getNextSibling(QName qname);
  
  /**
   * Get the first child element with the given QName
   * @param qname The XML QName of the sibling to find
   * @return The matching element
   */
  <T extends Element>T getFirstChild(QName qname);  
  
  /**
   * Return the XML QName of this element
   * @return The QName of the element
   */
  QName getQName();
  
  /**
   * Returns the value of this elements <code>xml:lang</code> attribute or 
   * null if <code>xml:lang</code> is undefined.
   * @return The xml:lang value
   */
  String getLanguage();
  
  /**
   * Returns a Locale object created from the <code>xml:lang</code> attribute
   * @return A Locale appropriate for the Language (xml:lang)
   */
  Locale getLocale();
  
  /**
   * Sets the value of this elements <code>xml:lang</code> attribute.
   * @param language the value of the xml:lang element
   */
  void setLanguage(String language);
  
  /**
   * Returns the value of this element's <code>xml:base</code> attribute or
   * null if <code>xml:base</code> is undefined.
   * @return The Base URI
   * @throws IRISyntaxException if the Base URI is malformed
   */
  IRI getBaseUri() throws IRISyntaxException;
  
  /**
   * Returns the current in-scope, fully qualified Base URI for this element.
   * @throws IRISyntaxException if the Base URI is malformed
   */
  IRI getResolvedBaseUri() throws IRISyntaxException;
  
  /**
   * Sets the value of this element's <code>xml:base</code> attribute.
   * @param base The IRI base value
   */
  void setBaseUri(IRI base);
  
  /**
   * Sets the value of this element's <code>xml:base</code> attribute.
   * @param base The Base IRI
   * @throws IRISyntaxException if the base URI is malformed 
   */
  void setBaseUri(String base) throws IRISyntaxException;
  
  /**
   * Returns the document to which this element belongs
   * @return The Document to which this element belongs
   */
  <T extends Element>Document<T> getDocument();

  /**
   * Returns the value of the named attribute
   * @param name The name of the attribute
   * @return The value of the attribute
   */
  String getAttributeValue(String name);

  /**
   * Returns the value of the named attribute
   * @param qname The XML QName of the attribute
   * @return The value of the attribute
   */
  String getAttributeValue(QName qname);

  /**
   * Returns a listing of all attributes on this element
   * @return The listing of attributes for this element
   */
  List<QName> getAttributes();
  
  /**
   * Returns a listing of extension attributes on this element
   * (extension attributes are attributes whose namespace URI
   * is different than the elements)
   * @return The listing non-Atom attributes
   */
  List<QName> getExtensionAttributes();
  
  /**
   * Remove the named Attribute
   * @param qname The XML QName of the attribute to remove
   */
  void removeAttribute(QName qname);
  
  /**
   * Sets the value of the named attribute
   * @param name The name of the attribute
   * @param value The value of the attribute
   */
  void setAttributeValue(String name, String value);

  /**
   * Sets the value of the named attribute
   * @param qname The XML QName of the attribute
   * @param value The value of the attribute
   */
  void setAttributeValue(QName qname, String value);
  
  /**
   * Removes this element from its current document
   */
  void discard();
  
  /**
   * Returns the Text value of this element
   * @return The text value
   */
  String getText();
  
  /**
   * Set the Text value of this element
   * @param text The text value
   */
  void setText(String text);
  
  /**
   * Declare a namespace
   */
  void declareNS(String uri, String prefix);
  
}
