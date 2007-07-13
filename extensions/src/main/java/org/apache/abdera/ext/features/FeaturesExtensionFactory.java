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
package org.apache.abdera.ext.features;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;

public final class FeaturesExtensionFactory 
  implements ExtensionFactory {

  public boolean handlesNamespace(String namespace) {
    return (FeaturesHelper.FNS.equals(namespace));
  }

  public List<String> getNamespaces() {
    List<String> lst = new ArrayList<String>();
    lst.add(FeaturesHelper.FNS);
    return lst;
  }

  @SuppressWarnings("unchecked")
  public <T extends Element> T getElementWrapper(Element internal) {
    QName qname = internal.getQName();
    if (FeaturesHelper.FEATURE.equals(qname)) 
      return (T)new Feature(internal);
    else return (T)internal;
  }

  public <T extends Base> String getMimeType(T base) {
    return null;
  }



}
