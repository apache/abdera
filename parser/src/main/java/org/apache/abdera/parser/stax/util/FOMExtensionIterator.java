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
package org.apache.abdera.parser.stax.util;

import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;

import javax.xml.namespace.QName;

/**
 * Most of the original code for this class came from the 
 * OMChildrenQNameIterator from Axiom
 */
public class FOMExtensionIterator extends OMChildrenIterator {

  /**
   * Field givenQName
   */
  private String namespace = null;
  private String extns = null;
  private FOMFactory factory = null;

  /**
   * Field needToMoveForward
   */
  private boolean needToMoveForward = true;

  /**
   * Field isMatchingNodeFound
   */
  private boolean isMatchingNodeFound = false;

  /**
   * Constructor OMChildrenQNameIterator.
   *
   * @param currentChild
   * @param givenQName
   */
  public FOMExtensionIterator(OMElement parent) {
    super(parent.getFirstOMChild());
    this.namespace = parent.getQName().getNamespaceURI();
    this.factory = (FOMFactory) parent.getOMFactory();
  }
  
  public FOMExtensionIterator(OMElement parent, String extns) {
    this(parent);
    this.extns = extns;
  }
  
  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   *
   * @return Returns <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {
      while (needToMoveForward) {
          if (currentChild != null) {

              // check the current node for the criteria
              if ((currentChild instanceof OMElement)
                      && (isQNamesMatch(
                              ((OMElement) currentChild).getQName(),
                              this.namespace))) {
                  isMatchingNodeFound = true;
                  needToMoveForward = false;
              } else {

                  // get the next named node
                  currentChild = currentChild.getNextOMSibling();
                  isMatchingNodeFound = needToMoveForward = !(currentChild
                          == null);
              }
          } else {
              needToMoveForward = false;
          }
      }
      return isMatchingNodeFound;
  }

  public Object next() {

      // reset the flags
      needToMoveForward = true;
      isMatchingNodeFound = false;
      nextCalled = true;
      removeCalled = false;
      lastChild = currentChild;
      currentChild = currentChild.getNextOMSibling();
      return factory.getElementWrapper((Element)lastChild);
  }

  private boolean isQNamesMatch(QName elementQName, String namespace) {
      String elns = elementQName == null ? "" : elementQName.getNamespaceURI();
      boolean namespaceURIMatch =
              (namespace == null)
              || (namespace == "")
              || elns.equals(namespace);
      if (!namespaceURIMatch && extns != null && !elns.equals(extns))
        return false;
      else 
        return !namespaceURIMatch;
  }

  
}
