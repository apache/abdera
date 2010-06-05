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
package org.apache.abdera.i18n.text;

/**
 * Base implementation of a CodepointIterator that filters the output of another CodpointIterator
 */
public abstract class DelegatingCodepointIterator extends CodepointIterator {

    private CodepointIterator internal;

    protected DelegatingCodepointIterator(CodepointIterator internal) {
        this.internal = internal;
    }

    @Override
    protected char get() {
        return internal.get();
    }

    @Override
    protected char get(int index) {
        return internal.get(index);
    }

    @Override
    public boolean hasNext() {
        return internal.hasNext();
    }

    @Override
    public boolean isHigh(int index) {
        return internal.isHigh(index);
    }

    @Override
    public boolean isLow(int index) {
        return internal.isLow(index);
    }

    @Override
    public int limit() {
        return internal.limit();
    }

    @Override
    public Codepoint next() {
        return internal.next();
    }

    @Override
    public char[] nextChars() {
        return internal.nextChars();
    }

    @Override
    public Codepoint peek() {
        return internal.peek();
    }

    @Override
    public Codepoint peek(int index) {
        return internal.peek(index);
    }

    @Override
    public char[] peekChars() {
        return internal.peekChars();
    }

    @Override
    public int position() {
        return internal.position();
    }

    @Override
    public int remaining() {
        return internal.remaining();
    }

    @Override
    public void position(int position) {
        internal.position(position);
    }

}
