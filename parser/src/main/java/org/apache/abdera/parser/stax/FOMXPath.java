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
package org.apache.abdera.parser.stax;

import java.util.List;
import java.util.Map;

import org.apache.abdera.model.Base;
import org.apache.abdera.util.AbstractXPath;
import org.apache.abdera.xpath.XPathException;
import org.apache.axiom.om.xpath.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

public class FOMXPath extends AbstractXPath {

  public static XPath getXPath(String path) throws JaxenException {
    return getXPath(path, null);
  }
  
  public static XPath getXPath(
    String path, 
    Map<String,String> namespaces) 
      throws JaxenException {
    DocumentNavigator nav = new DocumentNavigator();
    XPath contextpath = new BaseXPath(path, nav);
    if (namespaces != null) {
      for (String key : namespaces.keySet()) {
        String value = namespaces.get(key);
        contextpath.addNamespace(key, value);
      }
    }
    
    return contextpath;
  }

  public List selectNodes(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException {
    try {
      XPath xpath = getXPath(path, namespaces);
      return xpath.selectNodes(base);
    } catch (JaxenException e) {
      throw new XPathException(e);
    }
  }
  
  public Object selectSingleNode(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException {
    try {
      XPath xpath = getXPath(path, namespaces);
      return xpath.selectSingleNode(base);
    } catch (JaxenException e) {
      throw new XPathException(e);
    }
  }
  
  public Object evaluate(
    String path, 
    Base base, 
    Map<String,String> namespaces) throws XPathException {
    try {
      XPath xpath = getXPath(path, namespaces);
      return xpath.evaluate(base);
    } catch (JaxenException e) {
      throw new XPathException(e);
    }
  }
  
  public String valueOf(
    String path, 
    Base base, 
    Map<String,String> namespaces) 
      throws XPathException {
    try {
      XPath xpath = getXPath(path, namespaces);
      return xpath.stringValueOf(base);
    } catch (JaxenException e) {
      throw new XPathException(e);
    }
  }
  
  public boolean isTrue(
    String path, 
    Base base, 
    Map<String,String> namespaces) 
      throws XPathException {
    try {
      XPath xpath = getXPath(path, namespaces);
      return xpath.booleanValueOf(base);
    } catch (JaxenException e) {
      throw new XPathException(e);
    }
  }

  public Number numericValueOf(
    String path, 
    Base base, 
    Map<String,String>namespaces) 
      throws XPathException {
    try {
      XPath xpath = getXPath(path, namespaces);
      return xpath.numberValueOf(base);
    } catch (JaxenException e) {
      throw new XPathException(e);
    }
  }
  
}
