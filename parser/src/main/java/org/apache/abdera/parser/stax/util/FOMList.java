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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Implements an ElementSet around an internal buffered iterator. Here's the rationale: Axiom parses incrementally.
 * Using the iterators provided by Axiom, we can walk a set of elements while preserving the incremental parsing model,
 * however, if we went with just java.util.Iterator, we'd lose the ability to do things like feed.getEntries().get(0),
 * or use the new Java5 style iterators for (Entry e : feed.getEntries()). However, using a regular java.util.List also
 * isn't a great option because it means we have to iterate through all of the elements before returning back to the
 * caller. This gives us a hybrid approach. We create an internal iterator, then create a List from that, the iterator
 * is consumed as the list is used. The List itself is unmodifiable.
 */
@SuppressWarnings("unchecked")
public class FOMList<T> extends java.util.AbstractCollection<T> implements List<T> {

    private final Iterator<T> i;
    private final List<T> buffer = new ArrayList<T>();

    public FOMList(Iterator<T> i) {
        this.i = i;
    }

    public List<T> getAsList() {
        buffer(-1);
        return java.util.Collections.unmodifiableList(buffer);
    }

    private boolean finished() {
        return !i.hasNext();
    }

    private int buffered() {
        return buffer.size() - 1;
    }

    private int buffer(int n) {
        if (i.hasNext()) {
            int read = 0;
            while (i.hasNext() && (read++ < n || n == -1)) {
                buffer.add(i.next());
            }
        }
        return buffered();
    }

    public T get(int index) {
        int n = buffered();
        if (index > n && (index > buffer(index - n)))
            throw new ArrayIndexOutOfBoundsException(index);
        return (T)buffer.get(index);
    }

    public int size() {
        return buffer(-1) + 1;
    }

    public Iterator<T> iterator() {
        return new BufferIterator<T>(this);
    }

    private Iterator<T> iterator(int index) {
        return new BufferIterator<T>(this, index);
    }

    public boolean add(T o) {
        throw new UnsupportedOperationException();
    }

    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {
        buffer(-1);
        return buffer.contains(o);
    }

    public boolean containsAll(Collection c) {
        for (Object o : c)
            if (contains(o))
                return true;
        return false;
    }

    public int indexOf(Object o) {
        buffer(-1);
        return buffer.indexOf(o);
    }

    public boolean isEmpty() {
        buffer(-1);
        return buffer.isEmpty();
    }

    public int lastIndexOf(Object o) {
        buffer(-1);
        return buffer.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return (ListIterator<T>)iterator();
    }

    public ListIterator<T> listIterator(int index) {
        return (ListIterator<T>)iterator(index);
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        buffer(-1);
        return Collections.unmodifiableList(buffer.subList(fromIndex, toIndex));
    }

    public Object[] toArray() {
        buffer(-1);
        return buffer.toArray();
    }

    public Object[] toArray(Object[] a) {
        buffer(-1);
        return buffer.toArray(a);
    }

    private class BufferIterator<M> implements ListIterator<M> {

        private FOMList set = null;
        private int counter = 0;

        BufferIterator(FOMList set) {
            this.set = set;
        }

        BufferIterator(FOMList set, int index) {
            this.set = set;
            this.counter = index;
        }

        public boolean hasNext() {
            return (!set.finished()) || (set.finished() && counter < buffer.size());
        }

        public M next() {
            return (M)set.get(counter++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void add(M o) {
            throw new UnsupportedOperationException();
        }

        public boolean hasPrevious() {
            return counter > 0;
        }

        public int nextIndex() {
            if (hasNext())
                return counter + 1;
            else
                return buffer.size();
        }

        public M previous() {
            return (M)set.get(--counter);
        }

        public int previousIndex() {
            if (hasPrevious())
                return counter - 1;
            else
                return -1;
        }

        public void set(M o) {
            throw new UnsupportedOperationException();
        }

    }
}
