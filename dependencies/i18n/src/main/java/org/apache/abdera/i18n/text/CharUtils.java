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

import java.io.IOException;

/**
 * General utilities for dealing with Unicode characters
 */
public final class CharUtils {

    private CharUtils() {
    }

    /**
     * True if the character is a valid unicode codepoint
     */
    public static boolean isValid(int c) {
        return c >= 0x000000 && c <= 0x10ffff;
    }

    /**
     * True if the character is a valid unicode codepoint
     */
    public static boolean isValid(Codepoint c) {
        return isValid(c.getValue());
    }

    /**
     * True if all the characters in chars are within the set [low,high]
     */
    public static boolean inRange(char[] chars, char low, char high) {
        for (int i = 0; i < chars.length; i++)
            if (chars[i] < low || chars[i] > high)
                return false;
        return true;
    }

    /**
     * True if all the characters in chars are within the set [low,high]
     */
    public static boolean inRange(char[] chars, int low, int high) {
        for (int i = 0; i < chars.length; i++) {
            char n = chars[i];
            Codepoint cp =
                (isHighSurrogate(n) && i + 1 < chars.length && isLowSurrogate(chars[i + 1]))
                    ? toSupplementary(n, chars[i++]) : new Codepoint(n);
            int c = cp.getValue();
            if (c < low || c > high)
                return false;
        }
        return true;
    }

    /**
     * True if the codepoint is within the set [low,high]
     */
    public static boolean inRange(int codepoint, int low, int high) {
        return codepoint >= low && codepoint <= high;
    }

    /**
     * Append the specified codepoint to the buffer, automatically handling surrogate pairs
     */
    public static void append(Appendable buf, Codepoint c) {
        append(buf, c.getValue());
    }

    /**
     * Append the specified codepoint to the buffer, automatically handling surrogate pairs
     */
    public static void append(Appendable buf, int c) {
        try {
            if (isSupplementary(c)) {
                buf.append(getHighSurrogate(c));
                buf.append(getLowSurrogate(c));
            } else
                buf.append((char)c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the high surrogate for a particular unicode codepoint
     */
    public static char getHighSurrogate(int c) {
        return (c >= 0x10000) ? (char)((0xD800 - (0x10000 >> 10)) + (c >> 10)) : 0;
    }

    /**
     * Get the low surrogate for a particular unicode codepoint
     */
    public static char getLowSurrogate(int c) {
        return (c >= 0x10000) ? (char)(0xDC00 + (c & 0x3FF)) : (char)c;
    }

    /**
     * True if the specified char is a high surrogate
     */
    public static boolean isHighSurrogate(char c) {
        return c <= '\uDBFF' && c >= '\uD800';
    }

    /**
     * True if the specified char is a low surrogate
     */
    public static boolean isLowSurrogate(char c) {
        return c <= '\uDFFF' && c >= '\uDC00';
    }

    /**
     * True if the specified character is supplemental
     */
    public static boolean isSupplementary(int c) {
        return c <= 0x10ffff && c >= 0x010000;
    }

    /**
     * True if the two chars represent a surrogate pair
     */
    public static boolean isSurrogatePair(char high, char low) {
        return isHighSurrogate(high) && isLowSurrogate(low);
    }

    /**
     * Converts the high and low surrogate into a supplementary codepoint
     */
    public static Codepoint toSupplementary(char high, char low) {
        if (!isHighSurrogate(high))
            throw new IllegalArgumentException("Invalid High Surrogate");
        if (!isLowSurrogate(low))
            throw new IllegalArgumentException("Invalid Low Surrogate");
        return new Codepoint(((high - '\uD800') << 10) + (low - '\uDC00') + 0x010000);
    }

    /**
     * Return the codepoint at the given location, automatically dealing with surrogate pairs
     */
    public static Codepoint codepointAt(String s, int i) {
        char c = s.charAt(i);
        if (c < 0xD800 || c > 0xDFFF)
            return new Codepoint(c);
        if (isHighSurrogate(c)) {
            if (s.length() != i) {
                char low = s.charAt(i + 1);
                if (isLowSurrogate(low))
                    return toSupplementary(c, low);
            }
        } else if (isLowSurrogate(c)) {
            if (i >= 1) {
                char high = s.charAt(i - 1);
                if (isHighSurrogate(high))
                    return toSupplementary(high, c);
            }
        }
        return new Codepoint(c);
    }

    /**
     * Return the codepoint at the given location, automatically dealing with surrogate pairs
     */
    public static Codepoint codepointAt(CharSequence s, int i) {
        char c = s.charAt(i);
        if (c < 0xD800 || c > 0xDFFF)
            return new Codepoint(c);
        if (isHighSurrogate(c)) {
            if (s.length() != i) {
                char low = s.charAt(i + 1);
                if (isLowSurrogate(low))
                    return toSupplementary(c, low);
            }
        } else if (isLowSurrogate(c)) {
            if (i >= 1) {
                char high = s.charAt(i - 1);
                if (isHighSurrogate(high))
                    return toSupplementary(high, c);
            }
        }
        return new Codepoint(c);
    }

    /**
     * Insert a codepoint into the buffer, automatically dealing with surrogate pairs
     */
    public static void insert(CharSequence s, int i, Codepoint c) {
        insert(s, i, c.getValue());
    }

    /**
     * Insert a codepoint into the buffer, automatically dealing with surrogate pairs
     */
    public static void insert(CharSequence s, int i, int c) {
        if (!(s instanceof StringBuilder) && !(s instanceof StringBuffer)) {
            insert(new StringBuilder(s), i, c);
        } else {
            if (i > 0 && i < s.length()) {
                char ch = s.charAt(i);
                boolean low = isLowSurrogate(ch);
                if (low) {
                    if (low && isHighSurrogate(s.charAt(i - 1))) {
                        i--;
                    }
                }
            }
            if (s instanceof StringBuffer)
                ((StringBuffer)s).insert(i, toString(c));
            else if (s instanceof StringBuilder)
                ((StringBuilder)s).insert(i, toString(c));
        }
    }

    /**
     * Set the character at a given location, automatically dealing with surrogate pairs
     */
    public static void setChar(CharSequence s, int i, Codepoint c) {
        setChar(s, i, c.getValue());
    }

    /**
     * Set the character at a given location, automatically dealing with surrogate pairs
     */
    public static void setChar(CharSequence s, int i, int c) {
        if (!(s instanceof StringBuilder) && !(s instanceof StringBuffer)) {
            setChar(new StringBuilder(s), i, c);
        } else {
            int l = 1;
            char ch = s.charAt(i);
            boolean high = isHighSurrogate(ch);
            boolean low = isLowSurrogate(ch);
            if (high || low) {
                if (high && (i + 1) < s.length() && isLowSurrogate(s.charAt(i + 1)))
                    l++;
                else {
                    if (low && i > 0 && isHighSurrogate(s.charAt(i - 1))) {
                        i--;
                        l++;
                    }
                }
            }
            if (s instanceof StringBuffer)
                ((StringBuffer)s).replace(i, i + l, toString(c));
            else if (s instanceof StringBuilder)
                ((StringBuilder)s).replace(i, i + l, toString(c));
        }
    }

    /**
     * Return the number of characters used to represent the codepoint (will return 1 or 2)
     */
    public static int length(Codepoint c) {
        return c.getCharCount();
    }

    /**
     * Return the number of characters used to represent the codepoint (will return 1 or 2)
     */
    public static int length(int c) {
        return new Codepoint(c).getCharCount();
    }

    /**
     * Return the total number of codepoints in the buffer. Each surrogate pair counts as a single codepoint
     */
    public static int length(CharSequence c) {
        return length(CodepointIterator.forCharSequence(c));
    }

    /**
     * Return the total number of codepoints in the buffer. Each surrogate pair counts as a single codepoint
     */
    public static int length(char[] c) {
        return length(CodepointIterator.forCharArray(c));
    }

    private static int length(CodepointIterator ci) {
        int n = 0;
        while (ci.hasNext()) {
            ci.next();
            n++;
        }
        return n;
    }

    private static String supplementaryToString(int c) {
        StringBuilder buf = new StringBuilder();
        buf.append((char)getHighSurrogate(c));
        buf.append((char)getLowSurrogate(c));
        return buf.toString();
    }

    /**
     * Return the String representation of the codepoint, automatically dealing with surrogate pairs
     */
    public static String toString(int c) {
        return (isSupplementary(c)) ? supplementaryToString(c) : String.valueOf((char)c);
    }

    public static final char LRE = 0x202A;
    public static final char RLE = 0x202B;
    public static final char LRO = 0x202D;
    public static final char RLO = 0x202E;
    public static final char LRM = 0x200E;
    public static final char RLM = 0x200F;
    public static final char PDF = 0x202C;

    /**
     * Removes leading and trailing bidi controls from the string
     */
    public static String stripBidi(String s) {
        if (s == null || s.length() <= 1)
            return s;
        if (isBidi(s.charAt(0)))
            s = s.substring(1);
        if (isBidi(s.charAt(s.length() - 1)))
            s = s.substring(0, s.length() - 1);
        return s;
    }

    /**
     * Removes bidi controls from within a string
     */
    public static String stripBidiInternal(String s) {
        return s.replaceAll("[\u202A\u202B\u202D\u202E\u200E\u200F\u202C]", "");
    }

    private static String wrap(String s, char c1, char c2) {
        StringBuilder buf = new StringBuilder(s);
        if (buf.length() > 1) {
            if (buf.charAt(0) != c1)
                buf.insert(0, c1);
            if (buf.charAt(buf.length() - 1) != c2)
                buf.append(c2);
        }
        return buf.toString();
    }

    /**
     * Wrap the string with the specified bidi control
     */
    public static String wrapBidi(String s, char c) {
        switch (c) {
            case RLE:
                return wrap(s, RLE, PDF);
            case RLO:
                return wrap(s, RLO, PDF);
            case LRE:
                return wrap(s, LRE, PDF);
            case LRO:
                return wrap(s, LRO, PDF);
            case RLM:
                return wrap(s, RLM, RLM);
            case LRM:
                return wrap(s, LRM, LRM);
            default:
                return s;
        }
    }

    /**
     * True if the codepoint is a digit
     */
    public static boolean isDigit(Codepoint codepoint) {
        return isDigit(codepoint.getValue());
    }

    /**
     * True if the codepoint is a digit
     */
    public static boolean isDigit(int codepoint) {
        return CharUtils.inRange(codepoint, '0', '9');
    }

    /**
     * True if the codepoint is part of the ASCII alphabet (a-z, A-Z)
     */
    public static boolean isAlpha(Codepoint codepoint) {
        return isAlpha(codepoint.getValue());
    }

    /**
     * True if the codepoint is part of the ASCII alphabet (a-z, A-Z)
     */
    public static boolean isAlpha(int codepoint) {
        return CharUtils.inRange(codepoint, 'A', 'Z') || CharUtils.inRange(codepoint, 'a', 'z');
    }

    /**
     * True if isAlpha and isDigit both return true
     */
    public static boolean isAlphaDigit(Codepoint codepoint) {
        return isAlphaDigit(codepoint.getValue());
    }

    /**
     * True if isAlpha and isDigit both return true
     */
    public static boolean isAlphaDigit(int codepoint) {
        return isDigit(codepoint) || isAlpha(codepoint);
    }
    
    public static boolean isHex (int codepoint){
        return isDigit(codepoint) || CharUtils.inRange(codepoint, 'a', 'f') || CharUtils.inRange(codepoint, 'A', 'F');
    }

    /**
     * True if the codepoint is a bidi control character
     */
    public static boolean isBidi(Codepoint codepoint) {
        return isBidi(codepoint.getValue());
    }

    /**
     * True if the codepoint is a bidi control character
     */
    public static boolean isBidi(int codepoint) {
        return codepoint == LRM || // Left-to-right mark
        codepoint == RLM
            || // Right-to-left mark
            codepoint == LRE
            || // Left-to-right embedding
            codepoint == RLE
            || // Right-to-left embedding
            codepoint == LRO
            || // Left-to-right override
            codepoint == RLO
            || // Right-to-left override
            codepoint == PDF; // Pop directional formatting
    }

    public static int get_index(int[] set, int value) {
        int s = 0, e = set.length;
        while (e - s > 8) {
            int i = (e + s) >> 1;
            s = set[i] <= value ? i : s;
            e = set[i] > value ? i : e;
        }
        while (s < e) {
            if (value < set[s])
                break;
            s++;
        }
        return s == e ? -1 : s - 1;
    }

    /**
     * Treats the specified int array as an Inversion Set and returns true if the value is located within the set. This
     * will only work correctly if the values in the int array are monotonically increasing
     */
    public static boolean invset_contains(int[] set, int value) {
        int s = 0, e = set.length;
        while (e - s > 8) {
            int i = (e + s) >> 1;
            s = set[i] <= value ? i : s;
            e = set[i] > value ? i : e;
        }
        while (s < e) {
            if (value < set[s])
                break;
            s++;
        }
        return ((s - 1) & 1) == 0;
    }

    public static enum Profile {
        NONE(new Filter() {
            public boolean accept(int codepoint) {
                return true;
            }
        }), ALPHA(new Filter() {
            public boolean accept(int codepoint) {
                return !isAlpha(codepoint);
            }
        }), ALPHANUM(new Filter() {
            public boolean accept(int codepoint) {
                return !isAlphaDigit(codepoint);
            }
        }), FRAGMENT(new Filter() {
            public boolean accept(int codepoint) {
                return !isFragment(codepoint);
            }
        }), IFRAGMENT(new Filter() {
            public boolean accept(int codepoint) {
                return !is_ifragment(codepoint);
            }
        }), PATH(new Filter() {
            public boolean accept(int codepoint) {
                return !isPath(codepoint);
            }
        }), IPATH(new Filter() {
            public boolean accept(int codepoint) {
                return !is_ipath(codepoint);
            }
        }), IUSERINFO(new Filter() {
            public boolean accept(int codepoint) {
                return !is_iuserinfo(codepoint);
            }
        }), USERINFO(new Filter() {
            public boolean accept(int codepoint) {
                return !isUserInfo(codepoint);
            }
        }), QUERY(new Filter() {
            public boolean accept(int codepoint) {
                return !isQuery(codepoint);
            }
        }), IQUERY(new Filter() {
            public boolean accept(int codepoint) {
                return !is_iquery(codepoint);
            }
        }), SCHEME(new Filter() {
            public boolean accept(int codepoint) {
                return !isScheme(codepoint);
            }
        }), PATHNODELIMS(new Filter() {
            public boolean accept(int codepoint) {
                return !isPathNoDelims(codepoint);
            }
        }), IPATHNODELIMS(new Filter() {
            public boolean accept(int codepoint) {
                return !is_ipathnodelims(codepoint);
            }
        }), IPATHNODELIMS_SEG(new Filter() {
            public boolean accept(int codepoint) {
                return !is_ipathnodelims(codepoint) && codepoint != '@' && codepoint != ':';
            }
        }), IREGNAME(new Filter() {
            public boolean accept(int codepoint) {
                return !is_iregname(codepoint);
            }
        }), IHOST (new Filter(){
            public boolean accept(int codepoint){
                return !is_ihost(codepoint);
            }
        }), IPRIVATE(new Filter() {
            public boolean accept(int codepoint) {
                return !is_iprivate(codepoint);
            }
        }), RESERVED(new Filter() {
            public boolean accept(int codepoint) {
                return !isReserved(codepoint);
            }
        }), IUNRESERVED(new Filter() {
            public boolean accept(int codepoint) {
                return !is_iunreserved(codepoint);
            }
        }), UNRESERVED(new Filter() {
            public boolean accept(int codepoint) {
                return !isUnreserved(codepoint);
            }
        }), SCHEMESPECIFICPART(new Filter() {
            public boolean accept(int codepoint) {
                return !is_iunreserved(codepoint) && !isReserved(codepoint)
                    && !is_iprivate(codepoint)
                    && !isPctEnc(codepoint)
                    && codepoint != '#';
            }
        }), AUTHORITY(new Filter() {
            public boolean accept(int codepoint) {
                return !is_regname(codepoint) && !isUserInfo(codepoint) && !isGenDelim(codepoint);
            }
        }), ASCIISANSCRLF(new Filter() {
            public boolean accept(int codepoint) {
                return !CharUtils.inRange(codepoint, 1, 9) && !CharUtils.inRange(codepoint, 14, 127);
            }
        }), PCT(new Filter() {
            public boolean accept(int codepoint) {
                return !CharUtils.isPctEnc(codepoint);
            }
        }), STD3ASCIIRULES(new Filter() {
            public boolean accept(int codepoint) {
                return !CharUtils.inRange(codepoint, 0x0000, 0x002C) 
                	&& !CharUtils.inRange(codepoint, 0x002E, 0x002F)
                    && !CharUtils.inRange(codepoint, 0x003A, 0x0040)
                    && !CharUtils.inRange(codepoint, 0x005B, 0x005E)
                    && !CharUtils.inRange(codepoint, 0x0060, 0x0060)
                    && !CharUtils.inRange(codepoint, 0x007B, 0x007F);
            }
        });
        private final Filter filter;

        Profile(Filter filter) {
            this.filter = filter;
        }

        public Filter filter() {
            return filter;
        }

        public boolean check(int codepoint) {
            return filter.accept(codepoint);
        }
    }

    public static boolean isPctEnc(int codepoint) {
        return codepoint == '%' || isDigit(codepoint)
            || CharUtils.inRange(codepoint, 'A', 'F')
            || CharUtils.inRange(codepoint, 'a', 'f');
    }

    public static boolean isMark(int codepoint) {
        return codepoint == '-' || codepoint == '_'
            || codepoint == '.'
            || codepoint == '!'
            || codepoint == '~'
            || codepoint == '*'
            || codepoint == '\\'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')';
    }

    public static boolean isUnreserved(int codepoint) {
        return isAlphaDigit(codepoint) || codepoint == '-' || codepoint == '.' || codepoint == '_' || codepoint == '~';
    }

    public static boolean isReserved(int codepoint) {
        return codepoint == '$' || codepoint == '&'
            || codepoint == '+'
            || codepoint == ','
            || codepoint == '/'
            || codepoint == ':'
            || codepoint == ';'
            || codepoint == '='
            || codepoint == '?'
            || codepoint == '@'
            || codepoint == '['
            || codepoint == ']';
    }

    public static boolean isGenDelim(int codepoint) {
        return codepoint == '#' || codepoint == '/'
            || codepoint == ':'
            || codepoint == '?'
            || codepoint == '@'
            || codepoint == '['
            || codepoint == ']';
    }

    public static boolean isSubDelim(int codepoint) {
        return codepoint == '!' || codepoint == '$'
            || codepoint == '&'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')'
            || codepoint == '*'
            || codepoint == '+'
            || codepoint == ','
            || codepoint == ';'
            || codepoint == '='
            || codepoint == '\\';
    }

    public static boolean isPchar(int codepoint) {
        return isUnreserved(codepoint) || codepoint == ':'
            || codepoint == '@'
            || codepoint == '&'
            || codepoint == '='
            || codepoint == '+'
            || codepoint == '$'
            || codepoint == ',';
    }

    public static boolean isPath(int codepoint) {
        return isPchar(codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '%' || codepoint == ',';
    }

    public static boolean isPathNoDelims(int codepoint) {
        return isPath(codepoint) && !isGenDelim(codepoint);
    }

    public static boolean isScheme(int codepoint) {
        return isAlphaDigit(codepoint) || codepoint == '+' || codepoint == '-' || codepoint == '.';
    }

    public static boolean isUserInfo(int codepoint) {
        return isUnreserved(codepoint) || isSubDelim(codepoint) || isPctEnc(codepoint);
    }

    public static boolean isQuery(int codepoint) {
        return isPchar(codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '?' || codepoint == '%';
    }

    public static boolean isFragment(int codepoint) {
        return isPchar(codepoint) || codepoint == '/' || codepoint == '?' || codepoint == '%';
    }

    public static boolean is_ucschar(int codepoint) {
        return CharUtils.inRange(codepoint, '\u00A0', '\uD7FF') || CharUtils.inRange(codepoint, '\uF900', '\uFDCF')
            || CharUtils.inRange(codepoint, '\uFDF0', '\uFFEF')
            || CharUtils.inRange(codepoint, 0x10000, 0x1FFFD)
            || CharUtils.inRange(codepoint, 0x20000, 0x2FFFD)
            || CharUtils.inRange(codepoint, 0x30000, 0x3FFFD)
            || CharUtils.inRange(codepoint, 0x40000, 0x4FFFD)
            || CharUtils.inRange(codepoint, 0x50000, 0x5FFFD)
            || CharUtils.inRange(codepoint, 0x60000, 0x6FFFD)
            || CharUtils.inRange(codepoint, 0x70000, 0x7FFFD)
            || CharUtils.inRange(codepoint, 0x80000, 0x8FFFD)
            || CharUtils.inRange(codepoint, 0x90000, 0x9FFFD)
            || CharUtils.inRange(codepoint, 0xA0000, 0xAFFFD)
            || CharUtils.inRange(codepoint, 0xB0000, 0xBFFFD)
            || CharUtils.inRange(codepoint, 0xC0000, 0xCFFFD)
            || CharUtils.inRange(codepoint, 0xD0000, 0xDFFFD)
            || CharUtils.inRange(codepoint, 0xE1000, 0xEFFFD);
    }

    public static boolean is_iprivate(int codepoint) {
        return CharUtils.inRange(codepoint, '\uE000', '\uF8FF') || CharUtils.inRange(codepoint, 0xF0000, 0xFFFFD)
            || CharUtils.inRange(codepoint, 0x100000, 0x10FFFD);
    }

    public static boolean is_iunreserved(int codepoint) {
        return isAlphaDigit(codepoint) || isMark(codepoint) || is_ucschar(codepoint);
    }

    public static boolean is_ipchar(int codepoint) {
        return is_iunreserved(codepoint) || isSubDelim(codepoint)
            || codepoint == ':'
            || codepoint == '@'
            || codepoint == '&'
            || codepoint == '='
            || codepoint == '+'
            || codepoint == '$';
    }

    public static boolean is_ipath(int codepoint) {
        return is_ipchar(codepoint) || codepoint == ';' || codepoint == '/' || codepoint == '%' || codepoint == ',';
    }

    public static boolean is_ipathnodelims(int codepoint) {
        return is_ipath(codepoint) && !isGenDelim(codepoint);
    }

    public static boolean is_iquery(int codepoint) {
        return is_ipchar(codepoint) || is_iprivate(codepoint)
            || codepoint == ';'
            || codepoint == '/'
            || codepoint == '?'
            || codepoint == '%';
    }

    public static boolean is_ifragment(int codepoint) {
        return is_ipchar(codepoint) || is_iprivate(codepoint)
            || codepoint == '/'
            || codepoint == '?'
            || codepoint == '%';
    }

    public static boolean is_iregname(int codepoint) {
        return is_iunreserved(codepoint) || codepoint == '!'
            || codepoint == '$'
            || codepoint == '&'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')'
            || codepoint == '*'
            || codepoint == '+'
            || codepoint == ','
            || codepoint == ';'
            || codepoint == '='
            || codepoint == '"';
    }
    
    public static boolean is_ipliteral (int codepoint){
        return isHex(codepoint) || codepoint==':' 
            || codepoint =='[' 
            || codepoint==']';
    }
    
    public static boolean is_ihost (int codepoint){
        return is_iregname(codepoint) || is_ipliteral(codepoint);
    }

    public static boolean is_regname(int codepoint) {
        return isUnreserved(codepoint) || codepoint == '!'
            || codepoint == '$'
            || codepoint == '&'
            || codepoint == '\''
            || codepoint == '('
            || codepoint == ')'
            || codepoint == '*'
            || codepoint == '+'
            || codepoint == ','
            || codepoint == ';'
            || codepoint == '='
            || codepoint == '"';
    }

    public static boolean is_iuserinfo(int codepoint) {
        return is_iunreserved(codepoint) || codepoint == ';'
            || codepoint == ':'
            || codepoint == '&'
            || codepoint == '='
            || codepoint == '+'
            || codepoint == '$'
            || codepoint == ',';
    }

    public static boolean is_iserver(int codepoint) {
        return is_iuserinfo(codepoint) || is_iregname(codepoint)
            || isAlphaDigit(codepoint)
            || codepoint == '.'
            || codepoint == ':'
            || codepoint == '@'
            || codepoint == '['
            || codepoint == ']'
            || codepoint == '%'
            || codepoint == '-';
    }

    /**
     * Verifies a sequence of codepoints using the specified filter
     */
    public static void verify(CodepointIterator ci, Filter filter) throws InvalidCharacterException {
        CodepointIterator rci = CodepointIterator.restrict(ci, filter);
        while (rci.hasNext())
            rci.next();
    }

    /**
     * Verifies a sequence of codepoints using the specified filter
     */
    public static void verify(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
        CodepointIterator rci = CodepointIterator.restrict(ci, profile.filter());
        while (rci.hasNext())
            rci.next();
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verify(char[] s, Profile profile) throws InvalidCharacterException {
        if (s == null)
            return;
        verify(CodepointIterator.forCharArray(s), profile);
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verify(String s, Profile profile) throws InvalidCharacterException {
        if (s == null)
            return;
        verify(CodepointIterator.forCharSequence(s), profile);
    }

    /**
     * Verifies a sequence of codepoints using the specified filter
     */
    public static void verifyNot(CodepointIterator ci, Filter filter) throws InvalidCharacterException {
        CodepointIterator rci = ci.restrict(filter, false, true);
        while (rci.hasNext())
            rci.next();
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verifyNot(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
        CodepointIterator rci = ci.restrict(profile.filter(), false, true);
        while (rci.hasNext())
            rci.next();
    }

    /**
     * Verifies a sequence of codepoints using the specified profile
     */
    public static void verifyNot(char[] array, Profile profile) throws InvalidCharacterException {
        CodepointIterator rci = CodepointIterator.forCharArray(array).restrict(profile.filter(), false, true);
        while (rci.hasNext())
            rci.next();
    }

}
