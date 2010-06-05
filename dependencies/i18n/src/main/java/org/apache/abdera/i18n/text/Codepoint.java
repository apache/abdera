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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Represents a single Unicode Codepoint
 */
public class Codepoint implements Serializable, Cloneable, Comparable<Codepoint> {

    private static final long serialVersionUID = 140337939131905483L;

    private static final String DEFAULT_ENCODING = "UTF-8";
    private final int value;

    /**
     * Create a Codepoint from a byte array using the default encoding (UTF-8)
     */
    public Codepoint(byte[] bytes) {
        try {
            this.value = valueFromCharSequence(new String(bytes, DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a Codepoint from a byte array with the specified charset encoding. Length must equal 1
     */
    public Codepoint(byte[] bytes, String encoding) throws UnsupportedEncodingException {
        this.value = valueFromCharSequence(new String(bytes, encoding));
    }

    /**
     * Create a Codepoint from a CharSequence. Length must equal 1
     */
    public Codepoint(CharSequence value) {
        this(valueFromCharSequence(value));
    }

    private static int valueFromCharSequence(CharSequence s) {
        if (s.length() == 1) {
            return (int)s.charAt(0);
        } else if (s.length() > 2) {
            throw new IllegalArgumentException("Too many chars");
        } else {
            char high = s.charAt(0);
            char low = s.charAt(1);
            return CharUtils.toSupplementary(high, low).getValue();
        }
    }

    /**
     * Create a codepoint from a single char
     */
    public Codepoint(char value) {
        this((int)value);
    }

    /**
     * Create a codepoint from a surrogate pair
     */
    public Codepoint(char high, char low) {
        this(CharUtils.toSupplementary(high, low).getValue());
    }

    /**
     * Create a codepoint as a copy of another codepoint
     */
    public Codepoint(Codepoint codepoint) {
        this(codepoint.value);
    }

    /**
     * Create a codepoint from a specific integer value
     */
    public Codepoint(int value) {
        if (value < 0)
            throw new IllegalArgumentException("Invalid Codepoint");
        this.value = value;
    }

    /**
     * The codepoint value
     */
    public int getValue() {
        return value;
    }

    /**
     * True if this codepoint is supplementary
     */
    public boolean isSupplementary() {
        return CharUtils.isSupplementary(value);
    }

    /**
     * True if this codepoint is a low surrogate
     */
    public boolean isLowSurrogate() {
        return CharUtils.isLowSurrogate((char)value);
    }

    /**
     * True if this codepoint is a high surrogate
     */
    public boolean isHighSurrogate() {
        return CharUtils.isHighSurrogate((char)value);
    }

    /**
     * Get the high surrogate of this Codepoint
     */
    public char getHighSurrogate() {
        return CharUtils.getHighSurrogate(value);
    }

    /**
     * Get the low surrogate of this Codepoint
     */
    public char getLowSurrogate() {
        return CharUtils.getLowSurrogate(value);
    }

    /**
     * True if this Codepoint is a bidi control char
     */
    public boolean isBidi() {
        return CharUtils.isBidi(value);
    }

    public boolean isDigit() {
        return CharUtils.isDigit(value);
    }

    public boolean isAlpha() {
        return CharUtils.isAlpha(value);
    }

    public boolean isAlphaDigit() {
        return CharUtils.isAlpha(value);
    }

    public int compareTo(Codepoint o) {
        return value < o.value ? -1 : value == o.value ? 0 : 1;
    }

    public String toString() {
        return CharUtils.toString(value);
    }

    public char[] toChars() {
        return toString().toCharArray();
    }

    /**
     * Get the number of chars necessary to represent this codepoint. Returns 2 if this is a supplementary codepoint
     */
    public int getCharCount() {
        return toChars().length;
    }

    public byte[] toBytes() {
        try {
            return toBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] toBytes(String encoding) throws UnsupportedEncodingException {
        return toString().getBytes(encoding);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Codepoint other = (Codepoint)obj;
        if (value != other.value)
            return false;
        return true;
    }

    /**
     * Plane 0 (0000–FFFF): Basic Multilingual Plane (BMP). This is the plane containing most of the character
     * assignments so far. A primary objective for the BMP is to support the unification of prior character sets as well
     * as characters for writing systems in current use. Plane 1 (10000–1FFFF): Supplementary Multilingual Plane
     * (SMP). Plane 2 (20000–2FFFF): Supplementary Ideographic Plane (SIP) Planes 3 to 13 (30000–DFFFF) are
     * unassigned Plane 14 (E0000–EFFFF): Supplementary Special-purpose Plane (SSP) Plane 15 (F0000–FFFFF) reserved
     * for the Private Use Area (PUA) Plane 16 (100000–10FFFF), reserved for the Private Use Area (PUA)
     **/
    public int getPlane() {
        return value / (0xFFFF + 1);
    }

    public Codepoint clone() {
        try {
            return (Codepoint)super.clone();
        } catch (CloneNotSupportedException e) {
            return new Codepoint(value);
        }
    }

    /**
     * Get the next codepoint
     */
    public Codepoint next() {
        if (value == 0x10ffff)
            throw new IndexOutOfBoundsException();
        return new Codepoint(value + 1);
    }

    /**
     * Get the previous codepoint
     */
    public Codepoint previous() {
        if (value == 0)
            throw new IndexOutOfBoundsException();
        return new Codepoint(value - 1);
    }
}
