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
package org.apache.abdera.factory;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;

import java.util.List;

/**
 * <p>
 *   Extension Factories are used to provide a means of dynamically resolving
 *   builders for namespaced extension elements
 * </p>
 *
 * <p>There are four ways of supporting extension elements.</p>
 * 
 * <ol>
 *   <li>Implement your own Factory (hard)</li>
 *   <li>Subclass the default Axiom-based Factory (also somewhat difficult)</li>
 *   <li>Implement and register an ExtensionFactory (wonderfully simple)</li>
 *   <li>Use the Feed Object Model's dynamic support for extensions (also very simple)</li>
 * </ol>
 * 
 * <p>
 *   Registering an Extension Factory requires generally nothing more than 
 *   implementing ExtensionFactory and then creating the file 
 *   META-INF/services/org.apache.abdera.factory.ExtensionFactory and listing
 *   the class names of each ExtensionFactory you wish to register.
 * </p>
 * 
 * <p>
 *   Note that at this time, ExtensionFactories are specific to the parser 
 *   implementation used.  That is, if you're using the default StAX-based 
 *   FOMParser and FOMFactory implementation, your ExtensionFactories will 
 *   need to also implement FOMExtensionFactory.
 * </p>
 */
public interface ExtensionFactory {

  /**
   * Returns true if this extension factory handles the specified namespace
   */
  boolean handlesNamespace(String namespace);

  /**
   * Returns the Namespace URIs handled by this Extension Factory.
   *
   * @return A List of Namespace URIs Supported by this Extension
   */
  List<String> getNamespaces();

  /**
   * Called by the Factory implementaton to create an instance of the 
   * extension element.  If parent is not null, the new element will 
   * be automatically added as a child of the parent.
   * 
   * @param qname the QName of the extension element
   * @param parent the Parent of the extension element
   * @param factory the Factory
   * @return ExtensionElement The created ExtensionElement
   */
  <T extends Element>T newExtensionElement(QName qname, Base parent, Factory factory);

}
