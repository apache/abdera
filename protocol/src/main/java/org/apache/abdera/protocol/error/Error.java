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
package org.apache.abdera.protocol.error;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;

/**
 * Abdera protocol error element
 */
public class Error
  extends ExtensibleElementWrapper {

  public static final String NS        = "http://incubator.apache.org/abdera";
  public static final QName ERROR         = new QName(NS, "error");
  public static final QName CODE          = new QName(NS, "code");
  public static final QName MESSAGE       = new QName(NS, "message");
  
  public Error(Element internal) {
    super(internal);
  }

  public Error(Factory factory, QName qname) {
    super(factory, qname);
  }
  
  public int getCode() {
    String code = getSimpleExtension(CODE);
    return code != null ? Integer.parseInt(code) : -1;
  }
  
  public void setCode(int code) {
    if (code > -1) {
      Element element = getExtension(CODE);
      if (element != null) {
        element.setText(Integer.toString(code));
      } else {
        addSimpleExtension(CODE,Integer.toString(code));
      }
    } else {
      Element element = getExtension(CODE);
      if (element != null) element.discard();
    }
  }
 
  public String getMessage() {
    return getSimpleExtension(MESSAGE);
  }
  
  public void setMessage(String message) {
    if (message != null) {
      Element element = getExtension(MESSAGE);
      if (element != null) {
        element.setText(message);
      } else {
        addSimpleExtension(MESSAGE,message);
      }
    } else {
      Element element = getExtension(MESSAGE);
      if (element != null) element.discard();
    }
  }
  
  public void throwException() {
    throw new ProtocolException(this);
  }
  
  public static Error create(Abdera abdera, int code, String message) {
    Document<Error> doc = abdera.getFactory().newDocument();
    Error error = abdera.getFactory().newElement(ERROR,doc);
    error.setCode(code);
    error.setMessage(message);
    return error;
  }
}
