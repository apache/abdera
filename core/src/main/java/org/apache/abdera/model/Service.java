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

/**
 * <p>Represents the root of an Atom Publishing Protocol Introspection Document.</p>
 * 
 * <p>Per APP Draft-08:</p>
 * 
 * <pre>
 *  The root of an introspection document is the "app:service" element.
 *
 *  The "app:service" element is the container for introspection
 *  information associated with one or more workspaces.  An app:service
 *  element MUST contain one or more app:workspace elements.
 *  
 *  appService =
 *     element app:service {
 *        appCommonAttributes,
 *       ( appWorkspace+
 *          &amp; extensionElement* )
 *     }
 * </pre>
 */
public interface Service
  extends ExtensibleElement {

  /**
   * Return the complete set of workspaces
   */
  List<Workspace> getWorkspaces();
  
  /**
   * Return the named workspace
   */
  Workspace getWorkspace(String title);
  
  /**
   * Add an individual workspace
   */
  void addWorkspace(Workspace workspace);

  /**
   * Add an individual workspace
   */
  Workspace addWorkspace(String title);
  
  /**
   * Returns the named collection
   */
  Collection getCollection(String workspace, String collection);
  
}
