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
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface ExtensibleElement extends Element {

  /**
   * Returns the complete set of extension elements
   */
  List<ExtensionElement> getExtensions();
  
  /**
   * Returns the complete set of extension elements using the specified 
   * XML Namespace URI
   */
  List<ExtensionElement> getExtensions(String uri);
  
  /**
   * Returns the complete set of extension elements using the specified
   * XML qualified name
   */
  List<ExtensionElement> getExtensions(QName qname);
  
  /**
   * Returns the first extension element with the XML qualified name
   */
  ExtensionElement getExtension(QName qname);
  
  /**
   * Adds an individual extension element
   */
  void addExtension(ExtensionElement extension);

  /**
   * Adds an individual extension element
   */
  ExtensionElement addExtension(QName qname);
  
  /**
   * Adds an individual extension element
   */
  ExtensionElement addExtension(
    String namespace, 
    String localPart,
    String prefix);
  
  /**
   * Adds a simple extension (text content only)
   */
  StringElement addSimpleExtension(
    QName qname, 
    String value);
  
  /**
   * Adds a simple extension (text content only)
   */
  StringElement addSimpleExtension(
    String namespace, 
    String localPart, 
    String prefix, 
    String value);
  
  /**
   * Adds a list of extensions
   */
  void addExtensions(List<ExtensionElement> extensions);
  
  /**
   * Find an extension by Class rather than QName
   */
  <T extends ExtensionElement> T getExtension(Class<T> _class);
}
