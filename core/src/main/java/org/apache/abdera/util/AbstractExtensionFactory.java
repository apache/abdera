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
package org.apache.abdera.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

/**
 * <p>
 * Provides a base implementation for ExtensionFactory instances. By extending this, specific extension factories need
 * only to associate a QName with an implementation class, e.g.,
 * </p>
 * 
 * <pre>
 *  public class MyExtensionFactory
 *    extends AbstractExtensionFactory {
 * 
 *    private String NS = "http://example.org/foo/ns"; 
 *    private QName FOO = new QName(NS, "foo");
 * 
 *    public MyExtensionFactory() {
 *      super(NS);
 *      addImpl(FOO, Foo.class);
 *    }
 *  }
 *  
 *  public class Foo extends ElementWrapper { ... }
 * 
 * </pre>
 */
public abstract class AbstractExtensionFactory implements ExtensionFactory {

    private final List<String> namespaces = new ArrayList<String>();
    private final Map<QName, String> mimetypes = new HashMap<QName, String>();
    private final Map<QName, Class<? extends ElementWrapper>> impls =
        new HashMap<QName, Class<? extends ElementWrapper>>();

    protected AbstractExtensionFactory(String... namespaces) {
        for (String ns : namespaces)
            this.namespaces.add(ns);
    }

    @SuppressWarnings("unchecked")
    public <T extends Element> T getElementWrapper(Element internal) {
        T t = null;
        QName qname = internal.getQName();
        Class<? extends ElementWrapper> impl = impls.get(qname);
        if (impl != null) {
            try {
                t = (T)impl.getConstructor(new Class[] {Element.class}).newInstance(new Object[] {internal});
            } catch (Exception e) {
            }
        }
        return t != null ? t : (T)internal;
    }

    /**
     * Associate a MIME media type for the specific QName
     */
    protected AbstractExtensionFactory addMimeType(QName qname, String mimetype) {
        mimetypes.put(qname, mimetype);
        return this;
    }

    /**
     * Associate a QName with an implementation class
     */
    protected AbstractExtensionFactory addImpl(QName qname, Class<? extends ElementWrapper> impl) {
        impls.put(qname, impl);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Base> String getMimeType(T base) {
        Element element =
            base instanceof Element ? (Element)base : base instanceof Document ? ((Document)base).getRoot() : null;
        QName qname = element != null ? element.getQName() : null;
        return element != null && qname != null ? mimetypes.get(qname) : null;
    }

    public String[] getNamespaces() {
        return namespaces.toArray(new String[namespaces.size()]);
    }

    public boolean handlesNamespace(String namespace) {
        return namespaces.contains(namespace);
    }

}
