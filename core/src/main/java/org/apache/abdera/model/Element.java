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

import java.io.IOException;
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
  
  <T extends Base>T getParentElement();
  
  void setParentElement(Element parent);
  
  <T extends Element>T getPreviousSibling();
  
  <T extends Element>T getNextSibling();
  
  <T extends Element>T getFirstChild();
  
  <T extends Element>T getPreviousSibling(QName qname);
  
  <T extends Element>T getNextSibling(QName qname);
  
  <T extends Element>T getFirstChild(QName qname);  
  
  QName getQName();
  
  /**
   * Returns the value of this elements <code>xml:lang</code> attribute or 
   * null if <code>xml:lang</code> is undefined.
   */
  String getLanguage();
  
  /**
   * Returns a Locale object created from the <code>xml:lang</code> attribute
   */
  Locale getLocale();
  
  /**
   * Sets the value of this elements <code>xml:lang</code> attribute.
   */
  void setLanguage(String language);
  
  /**
   * Returns the value of this element's <code>xml:base</code> attribute or
   * null if <code>xml:base</code> is undefined.
   * @throws IOException 
   * @throws IRISyntaxException 
   */
  IRI getBaseUri() throws IRISyntaxException;
  
  /**
   * Returns the current in-scope, fully qualified Base URI for this element.
   * @throws IOException 
   */
  IRI getResolvedBaseUri() throws IRISyntaxException;
  
  /**
   * Sets the value of this element's <code>xml:base</code> attribute.
   */
  void setBaseUri(IRI base);
  
  /**
   * Sets the value of this element's <code>xml:base</code> attribute.
   * @throws IOException 
   * @throws URISyntaxException 
   */
  void setBaseUri(String base) throws IRISyntaxException;
  
  /**
   * Returns the document to which this element belongs
   */
  <T extends Element>Document<T> getDocument();

  /**
   * Returns the value of the named attribute
   */
  String getAttributeValue(String name);

  /**
   * Returns the value of the named attribute
   */

  String getAttributeValue(QName qname);

  /**
   * Returns a listing of all attributes on this element
   */
  List<QName> getAttributes();
  
  /**
   * Returns a listing of extension attributes on this element
   * (extension attributes are attributes whose namespace URI
   * is different than the elements)
   */
  List<QName> getExtensionAttributes();
  
  void removeAttribute(QName qname);
  
  /**
   * Sets the value of the named attribute
   */
  void setAttributeValue(String name, String value);

  /**
   * Sets the value of the named attribute
   */
  void setAttributeValue(QName qname, String value);
  
  /**
   * Removes this element from its current document
   */
  void discard();
  
  String getText();
  
  void setText(String text);
}
