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

import org.apache.abdera.util.iri.IRI;
import org.apache.abdera.util.iri.IRISyntaxException;

public interface Categories 
  extends ExtensibleElement {

  /**
   * Returns the value of the href attribute
   * @throws IOException 
   * @throws URISyntaxException 
   */
  IRI getHref() throws IRISyntaxException;
  
  /**
   * Returns the fully resolved href
   * @throws IOException 
   * @throws URISyntaxException 
   */
  IRI getResolvedHref() throws IRISyntaxException;
  
  /**
   * Sets the value of the href attribute
   * @throws IOException 
   * @throws URISyntaxException 
   */
  void setHref(String href) throws IRISyntaxException;
  
  /**
   * Specifies whether or not this is a fixed listing of categories
   */
  boolean isFixed();
  
  /**
   * Sets whether or not this is a fixed listing of categories
   */
  void setFixed(boolean fixed);
  
  /**
   * Returns the common scheme for this listing of categories
   * @throws IOException 
   * @throws IRISyntaxException 
   * @throws URISyntaxException 
   */
  IRI getScheme() throws IRISyntaxException;
  
  /**
   * Sets the common scheme for this listing of categories
   * @throws IOException 
   * @throws URISyntaxException 
   */
  void setScheme(String scheme) throws IRISyntaxException;

  /**
   * Lists the complete set of categories
   */
  List<Category> getCategories();
  
  /**
   * Lists the complete set of categories using the specified scheme
   * @throws URISyntaxException 
   */
  List<Category> getCategories(String scheme) throws IRISyntaxException;
  
  /**
   * Returns a copy of the complete set of categories with the scheme attribute set
   * as specified in 7.2.1. (child categories that do not have a scheme
   * attribute inherit the scheme attribute of the parent)
   * @throws IOException 
   * @throws URISyntaxException 
   */
  List<Category> getCategoriesWithScheme() throws IRISyntaxException;

  /**
   * Returns a copy of the complete set of categories with the scheme 
   * attribute set as specified in 7.2.1. (child categories that do not have a 
   * scheme attribute inherit the scheme attribute of the parent)
   * @throws IRISyntaxException 
   */
  List<Category> getCategoriesWithScheme(String scheme) throws IRISyntaxException;
  
  void addCategory(Category category);

  Category addCategory(String term);

  Category addCategory(String scheme, String term, String label) throws IRISyntaxException;
    
  boolean contains(String term) throws IRISyntaxException;
  
  boolean contains(String term, String scheme) throws IRISyntaxException;
  
}

