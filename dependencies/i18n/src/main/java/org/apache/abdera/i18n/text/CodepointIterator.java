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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Provides an iterator over Unicode Codepoints
 */
public abstract class CodepointIterator implements Iterator<Codepoint> {

    /**
     * Get a CodepointIterator for the specified char array
     */
    public static CodepointIterator forCharArray(char[] array) {
        return new CharArrayCodepointIterator(array);
    }

    /**
     * Get a CodepointIterator for the specified CharSequence
     */
    public static CodepointIterator forCharSequence(CharSequence seq) {
        return new CharSequenceCodepointIterator(seq);
    }

    /**
     * Get a CodepointIterator for the specified byte array, using the default charset
     */
    public static CodepointIterator forByteArray(byte[] array) {
        return new ByteArrayCodepointIterator(array);
    }

    /**
     * Get a CodepointIterator for the specified byte array, using the specified charset
     */
    public static CodepointIterator forByteArray(byte[] array, String charset) {
        return new ByteArrayCodepointIterator(array, charset);
    }

    /**
     * Get a CodepointIterator for the specified CharBuffer
     */
    public static CodepointIterator forCharBuffer(CharBuffer buffer) {
        return new CharBufferCodepointIterator(buffer);
    }

    /**
     * Get a CodepointIterator for the specified ReadableByteChannel
     */
    public static CodepointIterator forReadableByteChannel(ReadableByteChannel channel) {
        return new ReadableByteChannelCodepointIterator(channel);
    }

    /**
     * Get a CodepointIterator for the specified ReadableByteChannel
     */
    public static CodepointIterator forReadableByteChannel(ReadableByteChannel channel, String charset) {
        return new ReadableByteChannelCodepointIterator(channel, charset);
    }

    /**
     * Get a CodepointIterator for the specified InputStream
     */
    public static CodepointIterator forInputStream(InputStream in) {
        return new ReadableByteChannelCodepointIterator(Channels.newChannel(in));
    }

    /**
     * Get a CodepointIterator for the specified InputStream using the specified charset
     */
    public static CodepointIterator forInputStream(InputStream in, String charset) {
        return new ReadableByteChannelCodepointIterator(Channels.newChannel(in), charset);
    }

    /**
     * Get a CodepointIterator for the specified Reader
     */
    public static CodepointIterator forReader(Reader in) {
        return new ReaderCodepointIterator(in);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter) {
        return new RestrictedCodepointIterator(ci, filter, false);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter, boolean scanning) {
        return new RestrictedCodepointIterator(ci, filter, scanning);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter, boolean scanning, boolean invert) {
        return new RestrictedCodepointIterator(ci, filter, scanning, invert);
    }

    protected int position = -1;
    protected int limit = -1;

    public CodepointIterator restrict(Filter filter) {
        return restrict(this, filter);
    }

    public CodepointIterator restrict(Filter filter, boolean scanning) {
        return restrict(this, filter, scanning);
    }

    public CodepointIterator restrict(Filter filter, boolean scanning, boolean invert) {
        return restrict(this, filter, scanning, invert);
    }

    /**
     * Get the next char
     */
    protected abstract char get();

    /**
     * Get the specified char
     */
    protected abstract char get(int index);

    /**
     * True if there are codepoints remaining
     */
    public boolean hasNext() {
        return remaining() > 0;
    }

    /**
     * Return the final index position
     */
    public int lastPosition() {
        int p = position();
        return (p > -1) ? (p >= limit()) ? p : p - 1 : -1;
    }

    /**
     * Return the next chars. If the codepoint is not supplemental, the char array will have a single member. If the
     * codepoint is supplemental, the char array will have two members, representing the high and low surrogate chars
     */
    public char[] nextChars() throws InvalidCharacterException {
        if (hasNext()) {
            if (isNextSurrogate()) {
                char c1 = get();
                if (CharUtils.isHighSurrogate(c1) && position() < limit()) {
                    char c2 = get();
                    if (CharUtils.isLowSurrogate(c2)) {
                        return new char[] {c1, c2};
                    } else {
                        throw new InvalidCharacterException(c2);
                    }
                } else if (CharUtils.isLowSurrogate(c1) && position() > 0) {
                    char c2 = get(position() - 2);
                    if (CharUtils.isHighSurrogate(c2)) {
                        return new char[] {c1, c2};
                    } else {
                        throw new InvalidCharacterException(c2);
                    }
                }
            }
            return new char[] {get()};
        }
        return null;
    }

    /**
     * Peek the next chars in the iterator. If the codepoint is not supplemental, the char array will have a single
     * member. If the codepoint is supplemental, the char array will have two members, representing the high and low
     * surrogate chars
     */
    public char[] peekChars() throws InvalidCharacterException {
        return peekChars(position());
    }

    /**
     * Peek the specified chars in the iterator. If the codepoint is not supplemental, the char array will have a single
     * member. If the codepoint is supplemental, the char array will have two members, representing the high and low
     * surrogate chars
     */
    private char[] peekChars(int pos) throws InvalidCharacterException {
        if (pos < 0 || pos >= limit())
            return null;
        char c1 = get(pos);
        if (CharUtils.isHighSurrogate(c1) && pos < limit()) {
            char c2 = get(pos + 1);
            if (CharUtils.isLowSurrogate(c2)) {
                return new char[] {c1, c2};
            } else {
                throw new InvalidCharacterException(c2);
            }
        } else if (CharUtils.isLowSurrogate(c1) && pos > 1) {
            char c2 = get(pos - 1);
            if (CharUtils.isHighSurrogate(c2)) {
                return new char[] {c2, c1};
            } else {
                throw new InvalidCharacterException(c2);
            }
        } else
            return new char[] {c1};
    }

    /**
     * Return the next codepoint
     */
    public Codepoint next() throws InvalidCharacterException {
        return toCodepoint(nextChars());
    }

    /**
     * Peek the next codepoint
     */
    public Codepoint peek() throws InvalidCharacterException {
        return toCodepoint(peekChars());
    }

    /**
     * Peek the specified codepoint
     */
    public Codepoint peek(int index) throws InvalidCharacterException {
        return toCodepoint(peekChars(index));
    }

    private Codepoint toCodepoint(char[] chars) {
        return (chars == null) ? null : (chars.length == 1) ? new Codepoint(chars[0]) : CharUtils
            .toSupplementary(chars[0], chars[1]);
    }

    /**
     * Set the iterator position
     */
    public void position(int n) {
        if (n < 0 || n > limit())
            throw new ArrayIndexOutOfBoundsException(n);
        position = n;
    }

    /**
     * Get the iterator position
     */
    public int position() {
        return position;
    }

    /**
     * Return the iterator limit
     */
    public int limit() {
        return limit;
    }

    /**
     * Return the remaining iterator size
     */
    public int remaining() {
        return limit - position();
    }

    private boolean isNextSurrogate() {
        if (!hasNext())
            return false;
        char c = get(position());
        return CharUtils.isHighSurrogate(c) || CharUtils.isLowSurrogate(c);
    }

    /**
     * Returns true if the char at the specified index is a high surrogate
     */
    public boolean isHigh(int index) {
        if (index < 0 || index > limit())
            throw new ArrayIndexOutOfBoundsException(index);
        return CharUtils.isHighSurrogate(get(index));
    }

    /**
     * Returns true if the char at the specified index is a low surrogate
     */
    public boolean isLow(int index) {
        if (index < 0 || index > limit())
            throw new ArrayIndexOutOfBoundsException(index);
        return CharUtils.isLowSurrogate(get(index));
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    static class ByteArrayCodepointIterator extends CharArrayCodepointIterator {
        public ByteArrayCodepointIterator(byte[] bytes) {
            this(bytes, Charset.defaultCharset());
        }

        public ByteArrayCodepointIterator(byte[] bytes, String charset) {
            this(bytes, Charset.forName(charset));
        }

        public ByteArrayCodepointIterator(byte[] bytes, Charset charset) {
            CharBuffer cb = charset.decode(ByteBuffer.wrap(bytes));
            buffer = cb.array();
            position = cb.position();
            limit = cb.limit();
        }
    }

    static class CharArrayCodepointIterator extends CodepointIterator {
        protected char[] buffer;

        protected CharArrayCodepointIterator() {
        }

        public CharArrayCodepointIterator(char[] buffer) {
            this(buffer, 0, buffer.length);
        }

        public CharArrayCodepointIterator(char[] buffer, int n, int e) {
            this.buffer = buffer;
            this.position = n;
            this.limit = Math.min(buffer.length - n, e);
        }

        protected char get() {
            return (position < limit) ? buffer[position++] : (char)-1;
        }

        protected char get(int index) {
            if (index < 0 || index >= limit)
                throw new ArrayIndexOutOfBoundsException(index);
            return buffer[index];
        }
    }

    static class CharBufferCodepointIterator extends CharArrayCodepointIterator {
        public CharBufferCodepointIterator(CharBuffer cb) {
            buffer = cb.array();
            position = cb.position();
            limit = cb.limit();
        }
    }

    static class CharSequenceCodepointIterator extends CodepointIterator {
        private CharSequence buffer;

        public CharSequenceCodepointIterator(CharSequence buffer) {
            this(buffer, 0, buffer.length());
        }

        public CharSequenceCodepointIterator(CharSequence buffer, int n, int e) {
            this.buffer = buffer;
            this.position = n;
            this.limit = Math.min(buffer.length() - n, e);
        }

        protected char get() {
            return buffer.charAt(position++);
        }

        protected char get(int index) {
            return buffer.charAt(index);
        }
    }

    static class ReadableByteChannelCodepointIterator extends CharArrayCodepointIterator {
        public ReadableByteChannelCodepointIterator(ReadableByteChannel channel) {
            this(channel, Charset.defaultCharset());
        }

        public ReadableByteChannelCodepointIterator(ReadableByteChannel channel, String charset) {
            this(channel, Charset.forName(charset));
        }

        public ReadableByteChannelCodepointIterator(ReadableByteChannel channel, Charset charset) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(1024);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                WritableByteChannel outc = Channels.newChannel(out);
                while (channel.read(buf) > 0) {
                    buf.flip();
                    outc.write(buf);
                }
                CharBuffer cb = charset.decode(ByteBuffer.wrap(out.toByteArray()));
                buffer = cb.array();
                position = cb.position();
                limit = cb.limit();
            } catch (Exception e) {
            }
        }
    }

    static class ReaderCodepointIterator extends CharArrayCodepointIterator {
        public ReaderCodepointIterator(Reader reader) {
            try {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[1024];
                int n = -1;
                while ((n = reader.read(buf)) > -1) {
                    sb.append(buf, 0, n);
                }
                buffer = new char[sb.length()];
                sb.getChars(0, sb.length(), buffer, 0);
                position = 0;
                limit = buffer.length;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class RestrictedCodepointIterator extends DelegatingCodepointIterator {

        private final Filter filter;
        private final boolean scanningOnly;
        private final boolean notset;

        protected RestrictedCodepointIterator(CodepointIterator internal, Filter filter) {
            this(internal, filter, false);
        }

        protected RestrictedCodepointIterator(CodepointIterator internal, Filter filter, boolean scanningOnly) {
            this(internal, filter, scanningOnly, false);
        }

        protected RestrictedCodepointIterator(CodepointIterator internal,
                                              Filter filter,
                                              boolean scanningOnly,
                                              boolean notset) {
            super(internal);
            this.filter = filter;
            this.scanningOnly = scanningOnly;
            this.notset = notset;
        }

        public boolean hasNext() {
            boolean b = super.hasNext();
            if (scanningOnly) {
                try {
                    int cp = peek(position()).getValue();
                    if (b && cp != -1 && check(cp))
                        return false;
                } catch (InvalidCharacterException e) {
                    return false;
                }
            }
            return b;
        }

        @Override
        public Codepoint next() throws InvalidCharacterException {
            Codepoint cp = super.next();
            int v = cp.getValue();
            if (v != -1 && check(v)) {
                if (scanningOnly) {
                    position(position() - 1);
                    return null;
                } else
                    throw new InvalidCharacterException(v);
            }
            return cp;
        }

        private boolean check(int cp) {
            boolean answer = !filter.accept(cp);
            return (!notset) ? !answer : answer;
        }

        @Override
        public char[] nextChars() throws InvalidCharacterException {
            char[] chars = super.nextChars();
            if (chars != null && chars.length > 0) {
                if (chars.length == 1 && check(chars[0])) {
                    if (scanningOnly) {
                        position(position() - 1);
                        return null;
                    } else
                        throw new InvalidCharacterException(chars[0]);
                } else if (chars.length == 2) {
                    int cp = CharUtils.toSupplementary(chars[0], chars[1]).getValue();
                    if (check(cp)) {
                        if (scanningOnly) {
                            position(position() - 2);
                            return null;
                        } else
                            throw new InvalidCharacterException(cp);
                    }
                }
            }
            return chars;
        }

    }

}
