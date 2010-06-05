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
import java.util.Collections;
import java.util.List;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

/**
 * A utility implementation of ExtensionFactory used internally by Abdera. It maintains the collection ExtensionFactory
 * instances discovered on the classpath and a cache of Internal-Wrapper mappings.
 */
public class ExtensionFactoryMap implements ExtensionFactory {

    private final List<ExtensionFactory> factories;

    public ExtensionFactoryMap(List<ExtensionFactory> factories) {
        this.factories = Collections.synchronizedList(factories);
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> T getElementWrapper(Element internal) {
        if (internal == null)
            return null;
        T t = null;
        synchronized (factories) {
            for (ExtensionFactory factory : factories) {
                t = (T)factory.getElementWrapper(internal);
                if (t != null && t != internal) {
                    return t;
                }
            }
        }
        return (t != null) ? t : (T)internal;
    }

    public String[] getNamespaces() {
        List<String> ns = new ArrayList<String>();
        synchronized (factories) {
            for (ExtensionFactory factory : factories) {
                String[] namespaces = factory.getNamespaces();
                for (String uri : namespaces) {
                    if (!ns.contains(uri))
                        ns.add(uri);
                }
            }
        }
        return ns.toArray(new String[ns.size()]);
    }

    public boolean handlesNamespace(String namespace) {
        synchronized (factories) {
            for (ExtensionFactory factory : factories) {
                if (factory.handlesNamespace(namespace))
                    return true;
            }
        }
        return false;
    }

    public ExtensionFactoryMap addFactory(ExtensionFactory factory) {
        if (!factories.contains(factory))
            factories.add(factory);
        return this;
    }

    public <T extends Base> String getMimeType(T base) {
        Element element = base instanceof Element ? (Element)base : ((Document<?>)base).getRoot();
        String namespace = element.getQName().getNamespaceURI();
        synchronized (factories) {
            for (ExtensionFactory factory : factories) {
                if (factory.handlesNamespace(namespace))
                    return factory.getMimeType(base);
            }
        }
        return null;
    }

    public String[] listExtensionFactories() {
        List<String> names = new ArrayList<String>();
        synchronized (factories) {
            for (ExtensionFactory factory : factories) {
                String name = factory.getClass().getName();
                if (!names.contains(name))
                    names.add(name);
            }
        }
        return names.toArray(new String[names.size()]);
    }
}
