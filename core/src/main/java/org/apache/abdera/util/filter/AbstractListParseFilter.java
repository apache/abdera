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
package org.apache.abdera.util.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.filter.ListParseFilter;

/**
 * ParseFilter's determine which elements and attributes are acceptable within a parsed document. They are set via the
 * ParserOptions.setParseFilter method.
 */
public abstract class AbstractListParseFilter extends AbstractParseFilter implements Cloneable, ListParseFilter {

    private static final long serialVersionUID = -758691949740569208L;

    private transient List<QName> qnames = new ArrayList<QName>();
    private transient Map<QName, List<QName>> attributes = new HashMap<QName, List<QName>>();

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ListParseFilter add(QName qname) {
        synchronized (qnames) {
            if (!contains(qname))
                qnames.add(qname);
        }
        return this;
    }

    public boolean contains(QName qname) {
        synchronized (qnames) {
            return qnames.contains(qname);
        }
    }

    public ListParseFilter add(QName parent, QName attribute) {
        synchronized (attributes) {
            if (attributes.containsKey(parent)) {
                List<QName> attrs = attributes.get(parent);
                if (!attrs.contains(attribute))
                    attrs.add(attribute);
            } else {
                List<QName> attrs = new ArrayList<QName>();
                attrs.add(attribute);
                attributes.put(parent, attrs);
            }
        }
        return this;
    }

    public boolean contains(QName qname, QName attribute) {
        synchronized (attributes) {
            if (attributes.containsKey(qname)) {
                List<QName> attrs = attributes.get(qname);
                return attrs.contains(attribute);
            } else {
                return false;
            }
        }
    }

    public abstract boolean acceptable(QName qname);

    public abstract boolean acceptable(QName qname, QName attribute);

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        // qnames field
        assert qnames != null;
        out.writeInt(qnames.size());
        for (QName q : qnames) {
            out.writeObject(q);
        }

        // attributes field
        assert attributes != null;
        out.writeInt(attributes.size());
        for (Map.Entry<QName, List<QName>> e : attributes.entrySet()) {
            out.writeObject(e.getKey());
            final List<QName> v = e.getValue();
            assert v != null;
            out.writeInt(v.size());
            for (QName q : v) {
                out.writeObject(q);
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // qnames field
        final int qnamesSize = in.readInt();
        qnames = new ArrayList<QName>(qnamesSize);
        for (int i = 0; i < qnamesSize; i++) {
            qnames.add((QName)in.readObject());
        }

        // attributes field
        final int attributesSize = in.readInt();
        attributes = new HashMap<QName, List<QName>>(attributesSize);
        for (int i = 0; i < attributesSize; i++) {
            final QName k = (QName)in.readObject();
            final int vSize = in.readInt();
            final List<QName> v = new ArrayList<QName>(vSize);
            for (int j = 0; j < vSize; j++) {
                v.add((QName)in.readObject());
            }
            attributes.put(k, v);
        }
    }
}
