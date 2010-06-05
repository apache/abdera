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

import java.util.Arrays;
import java.util.Iterator;

/**
 * Iterator implementation that wraps multiple iterators and invokes them in sequence.
 */
public final class MultiIterator<T> implements Iterator<T> {

    private Iterator<Iterator<T>> iterators;
    private Iterator<T> current;

    public MultiIterator(Iterable<Iterator<T>> i) {
        this(i.iterator());
    }

    public MultiIterator(Iterator<T>... iterators) {
        this(Arrays.asList(iterators).iterator());
    }

    public MultiIterator(Iterator<Iterator<T>> iterators) {
        this.iterators = iterators;
        current = selectCurrent();
    }

    private Iterator<T> selectCurrent() {
        if (current == null) {
            if (iterators.hasNext())
                current = iterators.next();
        } else if (!current.hasNext() && iterators.hasNext()) {
            current = iterators.next();
        }
        return current;
    }

    public boolean hasNext() {
        Iterator<T> c = selectCurrent();
        return c != null ? c.hasNext() : false;
    }

    public T next() {
        if (hasNext())
            return selectCurrent().next();
        else
            return null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
