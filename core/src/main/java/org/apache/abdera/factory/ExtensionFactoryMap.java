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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;

public class ExtensionFactoryMap 
  implements ExtensionFactory {

  private final List<ExtensionFactory> factories;
  private final Map<Element,Element> wrappers;
  
  public ExtensionFactoryMap(List<ExtensionFactory> factories) {
    this.factories = factories;
    this.wrappers = new WeakHashMap<Element,Element>();
  }

  @SuppressWarnings("unchecked")
  public <T extends Element> T getElementWrapper(Element internal) {
    if (internal == null) return null;
    T t = (T)wrappers.get(internal);
    if (t == null) {
      for (ExtensionFactory factory : factories) {
        t = factory.getElementWrapper(internal);
        if (t != internal) {
          wrappers.put(internal, t);
          return t;
        }
      }
      t = (T) internal;
    }
    return t;
  }
  
  public void setElementWrapper(Element internal, Element wrapper) {
    wrappers.put(internal, wrapper);
  }

  public List<String> getNamespaces() {
    List<String> ns = new ArrayList<String>();
    for (ExtensionFactory factory : factories) {
      ns.addAll(factory.getNamespaces());
    }
    return ns;
  }

  public boolean handlesNamespace(String namespace) {
    for (ExtensionFactory factory : factories) {
      if (factory.handlesNamespace(namespace)) return true;
    }
    return false;
  }

  public <T extends Element> T newExtensionElement(
    QName qname, 
    Base parent, 
    Factory factory) {
      return null;
  }

  public List<ExtensionFactory> getFactories() {
    return factories;
  }
}
