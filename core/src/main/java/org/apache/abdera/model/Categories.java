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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public interface Categories 
  extends ExtensibleElement {

  /**
   * Returns the value of the href attribute
   * @throws URISyntaxException 
   */
  URI getHref() throws URISyntaxException;
  
  /**
   * Returns the fully resolved href
   * @throws URISyntaxException 
   */
  URI getResolvedHref() throws URISyntaxException;
  
  /**
   * Sets the value of the href attribute
   * @throws URISyntaxException 
   */
  void setHref(String href) throws URISyntaxException;
  
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
   * @throws URISyntaxException 
   */
  URI getScheme() throws URISyntaxException;
  
  /**
   * Sets the common scheme for this listing of categories
   * @throws URISyntaxException 
   */
  void setScheme(String scheme) throws URISyntaxException;

  /**
   * Lists the complete set of categories listed for the entry
   */
  List<Category> getCategories();
  
  /**
   * Lists the complete set of categories using the specified scheme
   * @throws URISyntaxException 
   */
  List<Category> getCategories(String scheme) throws URISyntaxException;
  
  /**
   * Adds an individual category to the entry
   */
  void addCategory(Category category);

  /**
   * Adds a category to the feed
   */
  Category addCategory(String term);

  /**
   * Adds a category to the feed
   * @throws URISyntaxException 
   */
  Category addCategory(String scheme, String term, String label) throws URISyntaxException;
    
}

