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
package org.apache.abdera.i18n.rfc4646;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

abstract class SubtagSet implements Serializable, Cloneable, Iterable<Subtag>, Comparable<SubtagSet> {

    protected final Subtag primary;

    protected SubtagSet(Subtag primary) {
        this.primary = primary;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Subtag subtag : this) {
            if (buf.length() > 0)
                buf.append('-');
            buf.append(subtag.getName());
        }
        return buf.toString();
    }

    public Iterator<Subtag> iterator() {
        return new SubtagIterator(primary);
    }

    public boolean contains(Subtag subtag) {
        for (Subtag tag : this)
            if (tag.equals(subtag))
                return true;
        return false;
    }

    public boolean contains(String tag) {
        return contains(tag, Subtag.Type.SIMPLE);
    }

    public boolean contains(String tag, Subtag.Type type) {
        return contains(new Subtag(type, tag));
    }

    public int length() {
        return toString().length();
    }

    public boolean isValid() {
        for (Subtag subtag : this)
            if (!subtag.isValid())
                return false;
        return true;
    }

    @SuppressWarnings("unused")
    public int count() {
        int n = 0;
        for (Subtag tag : this)
            n++;
        return n;
    }

    public Subtag get(int index) {
        if (index < 0 || index > count())
            throw new IndexOutOfBoundsException();
        Subtag tag = primary;
        for (int n = 1; n <= index; n++)
            tag = tag.getNext();
        return tag;
    }

    static class SubtagIterator implements Iterator<Subtag> {
        private Subtag current;

        SubtagIterator(Subtag current) {
            this.current = current;
        }

        public boolean hasNext() {
            return current != null;
        }

        public Subtag next() {
            Subtag tag = current;
            current = tag.getNext();
            return tag;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (Subtag tag : this)
            result = prime * result + tag.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Lang other = (Lang)obj;
        return hashCode() == other.hashCode();
    }

    public Subtag[] toArray() {
        List<Subtag> tags = new LinkedList<Subtag>();
        for (Subtag tag : this)
            tags.add(tag);
        return tags.toArray(new Subtag[tags.size()]);
    }

    public List<Subtag> asList() {
        return Arrays.asList(toArray());
    }

    public int compareTo(SubtagSet o) {
        Iterator<Subtag> i = iterator();
        Iterator<Subtag> e = o.iterator();
        for (; i.hasNext() && e.hasNext();) {
            Subtag inext = i.next();
            Subtag enext = e.next();
            int c = inext.compareTo(enext);
            if (c != 0)
                return c;
        }
        if (e.hasNext() && !i.hasNext())
            return -1;
        if (i.hasNext() && !e.hasNext())
            return 1;
        return 0;
    }

}
