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

import javax.xml.namespace.QName;

import org.apache.abdera.ext.thread.impl.FOMInReplyTo;
import org.apache.abdera.ext.thread.impl.FOMTotal;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.ExtensionElement;
import org.apache.abdera.parser.stax.FOMExtensionFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

public class ThreadExtensionFactory 
  implements ExtensionFactory,
    FOMExtensionFactory {

  public boolean handlesNamespace(String namespace) {
    return (ThreadConstants.THR_NS.equals(namespace));
  }

  public String getNamespace() {
    return ThreadConstants.THR_NS;
  }

  @SuppressWarnings("unchecked")
  public <T extends ExtensionElement> T newExtensionElement(
    QName qname,
    Base parent, 
    Factory factory) {
      if (ThreadConstants.IN_REPLY_TO.equals(qname)) 
        return (T)new FOMInReplyTo(
          qname, 
          (OMContainer)parent, 
          (OMFactory)factory);
      else if (ThreadConstants.THRTOTAL.equals(qname))
        return (T)new FOMTotal(
          qname, 
          (OMContainer)parent, 
          (OMFactory)factory);
      else return null;
  }

  @SuppressWarnings("unchecked")
  public <T extends ExtensionElement> T newExtensionElement(
    QName qname,
    Base parent, 
    Factory factory, 
    OMXMLParserWrapper parserWrapper) {
      if (ThreadConstants.IN_REPLY_TO.equals(qname)) 
        return (T)new FOMInReplyTo(
          qname, 
          (OMContainer)parent, 
          (OMFactory)factory,
          parserWrapper);
      else if (ThreadConstants.THRTOTAL.equals(qname))
        return (T)new FOMTotal(
          qname, 
          (OMContainer)parent, 
          (OMFactory)factory,
          parserWrapper);
      else return null;
  }

}
