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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

/**
 * Root interface for all elements in the Feed Object Model
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface Element 
  extends Base, Serializable {
  
  <T extends Base>T getParentElement();
  
  void setParentElement(Element parent);
  
  <T extends Element>T getPreviousSibling();
  
  <T extends Element>T getNextSibling();
  
  <T extends Element>T getFirstChild();
  
  <T extends Element>T getPreviousSibling(QName qname);
  
  <T extends Element>T getNextSibling(QName qname);
  
  <T extends Element>T getFirstChild(QName qname);  
  
  /**
   * Returns the value of this elements <code>xml:lang</code> attribute or 
   * null if <code>xml:lang</code> is undefined.
   */
  String getLanguage();
  
  /**
   * Sets the value of this elements <code>xml:lang</code> attribute.
   */
  void setLanguage(String language);
  
  /**
   * Returns the value of this element's <code>xml:base</code> attribute or
   * null if <code>xml:base</code> is undefined.
   */
  URI getBaseUri() throws URISyntaxException;
  
  /**
   * Returns the current in-scope, fully qualified Base URI for this element.
   */
  URI getResolvedBaseUri() throws URISyntaxException;
  
  /**
   * Sets the value of this element's <code>xml:base</code> attribute.
   */
  void setBaseUri(URI base);
  
  /**
   * Sets the value of this element's <code>xml:base</code> attribute.
   * @throws URISyntaxException 
   */
  void setBaseUri(String base) throws URISyntaxException;
  
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
   * Returns the value of the named attribute
   */
  String getAttributeValue(String namespace, String localPart);
    
  /**
   * Sets the value of the named attribute
   */
  void setAttributeValue(String name, String value);

  /**
   * Sets the value of the named attribute
   */
  void setAttributeValue(QName qname, String value);
  
  /**
   * Sets the value of the named attribute
   */
  void setAttributeValue(String namespace, String localPart, String prefix, String value);
  
  /**
   * Removes this element from its current document
   */
  void discard();
  
}
