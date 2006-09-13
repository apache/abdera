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

import javax.activation.MimeType;

/**
 * <p>Represents an collection element in an Atom Publishing Protocol 
 * introspection document.</p>
 * 
 * <pre>
 *  The "app:collection" describes an Atom Protocol collection.  One
 *  child element is defined here for app:collection: "app:member-type".
 *
 *  appCollection =
 *     element app:collection {
 *        appCommonAttributes,
 *        attribute href { text },
 *        ( atomTitle
 *          &amp; appAccept
 *          &amp; extensionElement* )
 *     }
 * </pre>
 */
public interface Collection 
  extends ExtensibleElement {

  String getTitle();

  Text setTitle(String title);

  Text setTitleAsHtml(String title);
  
  Text setTitleAsXHtml(String title);
  
  Text getTitleElement();

  URI getHref() throws URISyntaxException;

  URI getResolvedHref() throws URISyntaxException;
  
  void setHref(String href) throws URISyntaxException;

  Element getAcceptElement();
  
  String[] getAccept() ;

  void setAccept(String... mediaRanges);
  
  boolean accepts(String mediaType);
  
  boolean accepts(MimeType mediaType);
  
  List<Categories> getCategories();
  
  Categories addCategories();
  
  Categories addCategories(String href) throws URISyntaxException;
  
  void addCategories(Categories categories);
  
  Categories addCategories(List<Category> categories, boolean fixed, String scheme) throws URISyntaxException;
  
}
