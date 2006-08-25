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

import org.apache.abdera.model.Element;

/**
 * Text filter provides a means of filtering unacceptable text
 * from elements in the Atom feed, including unwanted text and
 * markup in HTML entries.
 */
public interface TextFilter extends Cloneable {

  public Object clone() throws CloneNotSupportedException;
  
  /**
   * Applies the text filter to the specified element text, 
   * returns the filtered text
   */
  public String applyFilter(
    char[] c, 
    int start, 
    int len, 
    Element parent);
  
  /**
   * Applies the text filter to the specified attribute text, 
   * returns the filtered text
   */
  public String applyFilter(
    String value, 
    Element parent,
    QName attribute);
  
}
