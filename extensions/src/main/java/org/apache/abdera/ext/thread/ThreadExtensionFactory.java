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
package org.apache.abdera.ext.thread;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Element;

public class ThreadExtensionFactory 
  implements ExtensionFactory {

  public boolean handlesNamespace(String namespace) {
    return (ThreadConstants.THR_NS.equals(namespace));
  }

  public List<String> getNamespaces() {
    List<String> lst = new ArrayList<String>();
    lst.add(ThreadConstants.THR_NS);
    return lst;
  }

  @SuppressWarnings("unchecked")
  public <T extends Element> T getElementWrapper(Element internal) {
    QName qname = internal.getQName();
    if (ThreadConstants.IN_REPLY_TO.equals(qname)) 
      return (T)new InReplyTo(internal);
    else if (ThreadConstants.THRTOTAL.equals(qname))
      return (T)new Total(internal);
    else return (T)internal;
  }



}
