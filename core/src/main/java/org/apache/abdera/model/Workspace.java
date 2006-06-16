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

/**
 * <p>An Atom Publishing Protocol Introspection Document workspace element.</p>
 * 
 * <p>Per APP Draft-08</p>
 * 
 * <pre>
 *  The "app:workspace" element contains information elements about the
 *  collections of resources available for editing.  The app:workspace
 *  element MUST contain one or more app:collection elements.
 *
 *  appWorkspace =
 *     element app:workspace {
 *        appCommonAttributes,
 *        attribute title { text },
 *        ( appCollection+
 *          &amp; extensionElement* )
 *     }
 *
 * </pre>
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface Workspace 
  extends ExtensibleElement, ExtensionElement {

  /**
   * APP Draft-08: The app:workspace element MUST contain a "title" 
   * attribute, which gives a human-readable name for the workspace.  
   * This attribute is Language-Sensitive.
   */
  String getTitle();
  
  /**
   * APP Draft-08: The app:workspace element MUST contain a "title" 
   * attribute, which gives a human-readable name for the workspace.  
   * This attribute is Language-Sensitive.
   */
  void setTitle(String title);
  
  /**
   * Returns the full set of collections in this workspace 
   */
  List<Collection> getCollections();
  
  /**
   * Returns the named collection
   */
  Collection getCollection(String title);
  
  /**
   * Sets the full set of collections for this workspace
   */
  void setCollection(List<Collection> collections);
  
  /**
   * Adds an individual collection to this workspace
   */
  void addCollection(Collection collection);

  /**
   * Adds an individual collection to this workspace
   */
  Collection addCollection(
    String title, 
    URI href);
  
  /**
   * Adds an individual collection to this workspace
   */
  Collection addCollection(
    String title, 
    String href) 
      throws URISyntaxException;
  
}
