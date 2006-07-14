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
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.stax.FOMLink;

public class LinkIterator extends ElementIterator {

  public LinkIterator(Element parent, Class _class, QName attribute, String value, String defaultValue) {
    super(parent, _class, attribute, (value != null) ? FOMLink.getRelEquiv(value) : Link.REL_ALTERNATE, defaultValue);
  }

  public LinkIterator(Element parent, Class _class) {
    super(parent, _class);
  }

  protected boolean isMatch(Element el) {
    if (attribute != null) {
      String val = FOMLink.getRelEquiv(el.getAttributeValue(attribute));
      return ((val == null && value == null) ||
             (val == null && value != null && value.equalsIgnoreCase(defaultValue)) ||
             (val != null && val.equalsIgnoreCase(value)));
    }
    return true;
  }  
}
