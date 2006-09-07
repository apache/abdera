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
public interface XPath {
  
  Map<String,String> getDefaultNamespaces();
  
  List selectNodes(
    String path, 
    Base base) throws XPathException;
  
  Object selectSingleNode(
    String path, 
    Base base) throws XPathException;
  
  Object evaluate(
    String path, 
    Base base) throws XPathException;
  
  String valueOf(
    String path, 
    Base base) throws XPathException;
  
  boolean isTrue(
    String path, 
    Base base) throws XPathException; 
  
  <T extends Number>T numericValueOf(
    String path, 
    Base base) 
      throws XPathException;
  
  List selectNodes(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException;
  
  <T>T selectSingleNode(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException;
  
  <T>T evaluate(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException;
  
  String valueOf(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException;
  
  boolean isTrue(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException; 
  
  <T extends Number>T numericValueOf(
    String path, 
    Base base, 
    Map<String, String> namespaces) 
      throws XPathException;
  
}
