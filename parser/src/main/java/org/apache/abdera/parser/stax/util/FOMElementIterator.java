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

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;

@SuppressWarnings("unchecked")
public class FOMElementIterator 
  extends OMChildrenIterator {

  /**
   * Field givenQName
   */
  protected QName attribute = null;
  protected String value = null;
  protected String defaultValue = null;
  protected Class _class = null;

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
  public FOMElementIterator(Element parent, Class _class) {
    super(((OMElement)parent).getFirstOMChild());
    this._class = _class;
  }
  
  public FOMElementIterator(Element parent, Class _class, QName attribute, String value, String defaultValue) {
    this(parent, _class);
    this.attribute = attribute;
    this.value = value;
    this.defaultValue = defaultValue;
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
            if (currentChild instanceof Element) {
              if (((_class != null &&
                    _class.isAssignableFrom(currentChild.getClass())) 
                 || _class == null) && isMatch((Element)currentChild)) {
                  isMatchingNodeFound = true;
                  needToMoveForward = false;
              } else {
                isMatchingNodeFound = false;
                currentChild = currentChild.getNextOMSibling();
              }
            } else {
              isMatchingNodeFound = false;
              currentChild = currentChild.getNextOMSibling();
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
      return lastChild;
  }

  protected boolean isMatch(Element el) {
    if (attribute != null) {
      String val = el.getAttributeValue(attribute);
      return ((val == null && value == null) ||
             (val == null && value != null && value.equals(defaultValue)) ||
             (val != null && val.equals(value)));
    }
    return true;
  }  
}
