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

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;

/**
 * An attribute. Returned by the Abdera XPath implementation
 * when querying for Attribute nodes.
 */
public interface Attribute {

  /**
   * Get the QName of the attribute
   * 
   * @return The attribute QName
   */
  QName getQName();
  
  /**
   * Return the text value of the attribute
   * 
   * @return The attribute value
   */
  String getText();
  
  /**
   * Set the text value of the attribute. The value will be automatically 
   * escaped for proper serialization to XML
   * 
   * @param text The attribute value
   */
  void setText(String text);
  
  /**
   * The Abdera Factory
   */
  Factory getFactory();
  
}
