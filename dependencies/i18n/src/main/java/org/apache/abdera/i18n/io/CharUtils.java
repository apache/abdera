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
package org.apache.abdera.i18n.io;

import java.util.Arrays;

/**
 * General utilities for dealing with Unicode characters
 */
public final class CharUtils {

  private CharUtils() {}
 
  public static boolean isValidCodepoint(int d) {
    return d >= 0x000000 && d <= 0x10ffff;
  }
  
  public static int scanNot(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,true,true);
    while (rci.hasNext()) rci.next();
    return rci.position;
  }
  
  public static int scanNot(char[] array, Profile profile) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,true,true);
    while (rci.hasNext()) rci.next();
    return rci.position;
  }
  
  public static int scan(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,true);
    while (rci.hasNext()) rci.next();
    return rci.position();
  }
  
  public static int scan(char[] array, Profile profile) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,true);
    while (rci.hasNext()) rci.next();
    return rci.position();
  }
  
  public static int scan(String s, Profile profile) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharSequence(s);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,true);
    while (rci.hasNext()) rci.next();
    return rci.position;
  }
  
  public static void verifyNot(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,false,true);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verifyNot(char[] array, Profile profile) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,false,true);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verify(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,false);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verify(char[] array, Profile profile) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,false);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verify(String s, Profile profile) throws InvalidCharacterException {
    if (s == null) return;
    CodepointIterator ci = CodepointIterator.forCharSequence(s);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,profile,false);
    while (rci.hasNext()) rci.next();
  }
  
  public static boolean inRange(char[] chars, char low, char high) {
    for (int i = 0; i < chars.length; i++)
      if (chars[i] < low || chars[i] > high) return false;
    return true;
  }

  public static boolean inRange(char[] chars, int low, int high) {
    for (int i = 0; i < chars.length; i++) {
      char n = chars[i];
      int c = (isHighSurrogate(n) && 
               i + 1 < chars.length && 
               isLowSurrogate(chars[i+1])) ? toCodePoint(n,chars[i++]) : n;
      if (c < low || c > high) return false;
    }
    return true;
  }
  
  public static boolean inRange(int codepoint, int low, int high) {
    return codepoint >= low && codepoint <= high;
  }
  
  public static boolean isSet(int n, int[]... sets) {
    if (n == -1) return false;
    return contains(n,sets);
  }
  
  public static void append(StringBuffer buf, int c) {
    if (isSupplementary(c)) {
      buf.append(getHighSurrogate(c));
      buf.append(getLowSurrogate(c));
    } else buf.append((char)c);
  }
  
  public static char getHighSurrogate(int c) {
    return (c >= 0x10000) ?
       (char)((0xD800 - (0x10000 >> 10)) + (c >> 10)) : 0;
  }

  public static char getLowSurrogate(int c) {    
    return (c >= 0x10000) ?
        (char)(0xDC00 + (c & 0x3FF)) : (char)c;
  }
  
  public static boolean isHighSurrogate(char c) {
    return c <= '\uDBFF' && c >= '\uD800';
  }

  public static boolean isLowSurrogate(char c) {
    return c <= '\uDFFF' && c >= '\uDC00';
  }
  
  public static boolean isSupplementary(int c) {
    return c <= 0x10ffff && c >= 0x010000;
  }
  
  public static boolean isSurrogatePair(char high, char low) {
    return isHighSurrogate(high) && isLowSurrogate(low);
  }
  
  public static int toCodePoint(char[] chars) {
    return toCodePoint(chars[0],chars[1]);
  }
  
  public static int toCodePoint(char high, char low) {
    return ((high - '\uD800') << 10) + (low - '\uDC00') + 0x010000;    
  }

  public static int charAt(String s, int i) {
    char c = s.charAt(i);
    if (c < 0xD800 || c > 0xDFFF) return c;
    if (isHighSurrogate(c)) {
      if (s.length() != i) {
        char low = s.charAt(i+1);
        if (isLowSurrogate(low)) return toCodePoint(c,low);
      }
    } else if (isLowSurrogate(c)) {
      if (i >= 1) {
        char high = s.charAt(i-1);
        if (isHighSurrogate(high)) return toCodePoint(high,c);
      }
    }
    return c;
  }
  
  public static int charAt(StringBuffer s, int i) {
    char c = s.charAt(i);
    if (c < 0xD800 || c > 0xDFFF) return c;
    if (isHighSurrogate(c)) {
      if (s.length() != i) {
        char low = s.charAt(i+1);
        if (isLowSurrogate(low)) return toCodePoint(c,low);
      }
    } else if (isLowSurrogate(c)) {
      if (i >= 1) {
        char high = s.charAt(i-1);
        if (isHighSurrogate(high)) return toCodePoint(high,c);
      }
    }
    return c;
  }
  
  public static void insert(StringBuffer s, int i, int c) {
    if (i > 0 && i < s.length()) {
      char ch = s.charAt(i);
      boolean low = isLowSurrogate(ch);
      if (low) {
        if (low && isHighSurrogate(s.charAt(i-1))) {
          i--;
        }
      }
    }
    s.insert(i, toString(c));
  }
  
  public static void setChar(StringBuffer s, int i, int c) {
    int l = 1;
    char ch = s.charAt(i);
    boolean high = isHighSurrogate(ch);
    boolean low = isLowSurrogate(ch);
    if (high || low) {
      if (high && (i+1) < s.length() && isLowSurrogate(s.charAt(i+1))) l++;
      else {
        if (low && i > 0 && isHighSurrogate(s.charAt(i-1))) {
          i--; l++;
        }
      }
    }
    s.replace(i, i+l, toString(c));
  }
  
  public static int size(int c) {
    return (isSupplementary(c)) ? 2 : 1;
  }
  
  private static String supplementaryToString(int c) {
    StringBuffer buf = new StringBuffer();
    buf.append((char)getHighSurrogate(c));
    buf.append((char)getLowSurrogate(c));
    return buf.toString();
  }
  
  public static String toString(int c) {
    return (isSupplementary(c)) ? 
      supplementaryToString(c) : 
      String.valueOf((char)c);
  }
  
  

  private static final char LRE = 0x202A; 
  private static final char RLE = 0x202B; 
  private static final char LRO = 0x202D; 
  private static final char RLO = 0x202E; 
  private static final char LRM = 0x200E; 
  private static final char RLM = 0x200F;
  private static final char PDF = 0x202C;
  
  /**
   * Removes leading and trailing bidi controls from the string
   */
  public static String stripBidi(String s) {
    if (s == null || s.length() <= 1) return s;
    if (charIsBidiControl(s.charAt(0)))
      s = s.substring(1);
    if (charIsBidiControl(s.charAt(s.length()-1)))
      s = s.substring(0,s.length()-1);
    return s;
  }
  
  /**
   * Returns true if the character is a bidi control 
   */
  public static boolean charIsBidiControl(char c) {
    return c == 0x202A ||
    c == LRE ||
    c == RLE ||
    c == LRO ||
    c == RLO ||
    c == RLM ||
    c == LRM || 
    c == PDF;
  }
  
  private static String wrap(String s, char c1, char c2) {
    StringBuffer buf = new StringBuffer(s);
    if (buf.length() > 1) {
      if (buf.charAt(0) != c1) buf.insert(0, c1);
      if (buf.charAt(buf.length()-1) != c2) buf.append(c2);
    }
    return buf.toString();
  }
  
  /**
   * Wrap the string with Bidi Right-to-Left embed
   */
  public static String bidiRLE(String s) {
    return wrap(s,RLE,PDF);
  }
  
  /**
   * Wrap the string with Bidi Right-to-Left override 
   */
  public static String bidiRLO(String s) {
    return wrap(s,RLO,PDF);
  }
  
  /**
   * Wrap the string with Bidi Left-to-Right embed
   */
  public static String bidiLRE(String s) {
    return wrap(s,LRE,PDF);
  }
  
  /**
   * Wrap the string with Bidi Left-to-Right override
   */
  public static String bidiLRO(String s) {
    return wrap(s,LRO,PDF);
  }
  
  /**
   * Wrap the string with Bidi RML marks
   */
  public static String bidiRLM(String s) {
    return wrap(s,RLM,RLM);
  }
  
  /**
   * Wrap the string with Bidi LRM marks
   */
  public static String bidiLRM(String s) {
    return wrap(s,LRM,LRM);
  }
  
  public static boolean contains(int v, int[]... ranges) {
    for (int[] range : ranges) 
      if (Arrays.binarySearch(range, v) > -1) return true;
    return false;
  }
  
  public static interface Check {
    boolean escape(int codepoint);
  }
  
  public static enum Profile {
    NONE(
      new Check() {
        public boolean escape(int codepoint) {
          return true;
        }
      }
    ),
    ALPHA(
      new Check() {
        public boolean escape(int codepoint) {
          return !isAlpha(codepoint);
        }
      }
    ),
    ALPHANUM(
      new Check() {
        public boolean escape(int codepoint) {
          return !isAlphaNum(codepoint);
        }
      }
    ),
    FRAGMENT(
      new Check() {
        public boolean escape(int codepoint) {
          return !isFragment(codepoint);
        }
      }
    ),
    IFRAGMENT(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_ifragment(codepoint);
        }
      }
    ),
    PATH(
      new Check() {
        public boolean escape(int codepoint) {
          return !isPath(codepoint);
        }
      }
    ),
    IPATH(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_ipath(codepoint);
        }
      }
    ),
    IUSERINFO(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_iuserinfo(codepoint);
        }
      }
    ),
    USERINFO(
      new Check() {
        public boolean escape(int codepoint) {
          return !isUserInfo(codepoint);
        }
      }
    ),
    QUERY(
        new Check() {
        public boolean escape(int codepoint) {
          return !isQuery(codepoint);
        }
      }
    ),
    IQUERY(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_iquery(codepoint);
        }
      }
    ),
    SCHEME(
      new Check() {
        public boolean escape(int codepoint) {
          return !isScheme(codepoint);
        }
      }
    ),
    PATHNODELIMS(
      new Check() {
        public boolean escape(int codepoint) {
          return !isPathNoDelims(codepoint);
        }
      }
    ),
    IPATHNODELIMS(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_ipathnodelims(codepoint);
        }
      }
    ),
    IREGNAME(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_iregname(codepoint);
        }
      }
    ),
    IPRIVATE(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_iprivate(codepoint);
        }
      }
    ),
    RESERVED(
      new Check() {
        public boolean escape(int codepoint) {
          return !isReserved(codepoint);
        }
      }
    ),
    IUNRESERVED(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_iunreserved(codepoint);
        }
      }
    ),
    SCHEMESPECIFICPART(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_iunreserved(codepoint) && 
                 !isReserved(codepoint) && 
                 !is_iprivate(codepoint) && 
                 !isPctEnc(codepoint) && 
                 codepoint != '#';
        }
      }
    ),
    AUTHORITY(
      new Check() {
        public boolean escape(int codepoint) {
          return !is_regname(codepoint) &&
                 !isUserInfo(codepoint) && 
                 !isGenDelim(codepoint);
        }
      }
    ),
    ASCIISANSCRLF(
      new Check() {
        public boolean escape(int codepoint) {  
          return !CharUtils.inRange(codepoint,1,9) &&   
                 !CharUtils.inRange(codepoint,14,127);
        }        
      }
    ),
    STD3ASCIIRULES(
      new Check() {
        public boolean escape(int codepoint) {
          return !CharUtils.inRange(codepoint,0x0000,0x002C) &&
                 !CharUtils.inRange(codepoint,0x002E,0x002F) &&
                 !CharUtils.inRange(codepoint,0x003A,0x0040) &&
                 !CharUtils.inRange(codepoint,0x005B,0x0060) &&
                 !CharUtils.inRange(codepoint,0x007B,0x007F);          
        }
      }
    )
    ;
    private final Check check;
    Profile(Check check) {
      this.check = check;
    }
    public boolean check(int codepoint) {
      return check.escape(codepoint);
    }
  }
  
  public static boolean isDigit(int codepoint) {
    return CharUtils.inRange(codepoint, '0', '9');
  }
  
  public static boolean isAlpha(int codepoint) {
    return CharUtils.inRange(codepoint, 'A', 'Z') ||
           CharUtils.inRange(codepoint, 'a', 'z');
  }

  public static boolean isAlphaNum(int codepoint) {
    return isDigit(codepoint) || isAlpha(codepoint);
  }

  public static boolean isPctEnc(int codepoint) {
    return codepoint == '%' ||
           isDigit(codepoint) ||
           CharUtils.inRange(codepoint,'A','F') ||
           CharUtils.inRange(codepoint,'a','f');
  }
  
  public static boolean isMark(int codepoint) {
    return codepoint == '-'  ||
           codepoint == '_'  ||
           codepoint == '.'  ||
           codepoint == '!'  ||
           codepoint == '~'  ||
           codepoint == '*'  ||
           codepoint == '\\' ||
           codepoint == '\'' ||
           codepoint == '('  ||
           codepoint == ')';
  }
  
  public static boolean isUnreserved(int codepoint) {
    return isAlphaNum(codepoint) || isMark(codepoint);
  }

  public static boolean isReserved(int codepoint) {
    return codepoint == '$' ||
           codepoint == '&' ||
           codepoint == '+' ||
           codepoint == ',' ||
           codepoint == '/' ||
           codepoint == ':' ||
           codepoint == ';' ||
           codepoint == '=' ||
           codepoint == '?' ||
           codepoint == '@' ||
           codepoint == '[' ||
           codepoint == ']';
  }
  
  public static boolean isGenDelim(int codepoint) {
    return codepoint == '#' ||
           codepoint == '/' ||
           codepoint == ':' ||
           codepoint == '?' ||
           codepoint == '@' ||
           codepoint == '[' ||
           codepoint == ']';
  }
  
  public static boolean isSubDelim(int codepoint) {
    return codepoint == '!' || 
           codepoint == '$' || 
           codepoint == '&' || 
           codepoint == '\'' || 
           codepoint == '(' || 
           codepoint == ')' || 
           codepoint == '*' || 
           codepoint == '+' || 
           codepoint == ',' || 
           codepoint == ';' || 
           codepoint == '=' || 
           codepoint == '\\';
  }
  
  public static boolean isPchar(int codepoint) {
    return isUnreserved(codepoint) ||  
           codepoint == ':' || 
           codepoint == '@' || 
           codepoint == '&' || 
           codepoint == '=' || 
           codepoint == '+' || 
           codepoint == '$' || 
           codepoint == ',';
  }

  public static boolean isPath(int codepoint) {
    return isPchar(codepoint) || 
           codepoint == ';' ||
           codepoint == '/' ||
           codepoint == '%' || 
           codepoint == ',';
  }
  
  public static boolean isPathNoDelims(int codepoint) {
    return isPath(codepoint) && !isGenDelim(codepoint);
  }

  public static boolean isScheme(int codepoint) {
    return isAlphaNum(codepoint) || 
           codepoint == '+' || 
           codepoint == '-' ||
           codepoint == '.';
  }
  

  public static boolean isUserInfo(int codepoint) {
    return isUnreserved(codepoint) || 
           isSubDelim(codepoint) ||
           isPctEnc(codepoint);
  }
  
  public static boolean isQuery(int codepoint) {
    return isPchar(codepoint) ||
           codepoint == ';' || 
           codepoint == '/' ||
           codepoint == '?' ||
           codepoint == '%';
  }

  public static boolean isFragment(int codepoint) {
    return isPchar(codepoint) ||
           codepoint == '/' ||
           codepoint == '?' ||
           codepoint == '%';
  }
  
  public static boolean isBidi(int codepoint) {
    return  codepoint == '\u200E' || // Left-to-right mark
            codepoint == '\u200F' || // Right-to-left mark
            codepoint == '\u202A' || // Left-to-right embedding
            codepoint == '\u202B' || // Right-to-left embedding
            codepoint == '\u202D' || // Left-to-right override
            codepoint == '\u202E' || // Right-to-left override
            codepoint == '\u202C';   // Pop directional formatting
  }
  
  public static boolean is_ucschar(int codepoint) {    
    return 
        CharUtils.inRange(codepoint,'\u00A0', '\uD7FF') ||
        CharUtils.inRange(codepoint,'\uF900','\uFDCF') ||
        CharUtils.inRange(codepoint,'\uFDF0','\uFFEF') ||
        CharUtils.inRange(codepoint,0x10000,0x1FFFD) ||
        CharUtils.inRange(codepoint,0x20000,0x2FFFD) ||
        CharUtils.inRange(codepoint,0x30000,0x3FFFD) ||
        CharUtils.inRange(codepoint,0x40000,0x4FFFD) ||
        CharUtils.inRange(codepoint,0x50000,0x5FFFD) ||
        CharUtils.inRange(codepoint,0x60000,0x6FFFD) ||
        CharUtils.inRange(codepoint,0x70000,0x7FFFD) ||
        CharUtils.inRange(codepoint,0x80000,0x8FFFD) ||
        CharUtils.inRange(codepoint,0x90000,0x9FFFD) ||
        CharUtils.inRange(codepoint,0xA0000,0xAFFFD) ||
        CharUtils.inRange(codepoint,0xB0000,0xBFFFD) ||
        CharUtils.inRange(codepoint,0xC0000,0xCFFFD) ||
        CharUtils.inRange(codepoint,0xD0000,0xDFFFD) ||
        CharUtils.inRange(codepoint,0xE1000,0xEFFFD);
  }

  public static boolean is_iprivate(int codepoint) {
    return
      CharUtils.inRange(codepoint,'\uE000', '\uF8FF') ||
      CharUtils.inRange(codepoint, 0xF0000,0xFFFFD) ||
      CharUtils.inRange(codepoint, 0x100000,0x10FFFD);
  }
  
  public static boolean is_iunreserved(int codepoint) {
    return isAlphaNum(codepoint) || isMark(codepoint) || is_ucschar(codepoint);
  }

  public static boolean is_ipchar(int codepoint) {
    return is_iunreserved(codepoint) || 
           codepoint == ':' || 
           codepoint == '@' || 
           codepoint == '&' || 
           codepoint == '=' || 
           codepoint == '+' || 
           codepoint == '$';
  }

  public static boolean is_ipath(int codepoint) {
    return is_ipchar(codepoint) || 
           codepoint == ';' ||
           codepoint == '/' ||
           codepoint == '%' || 
           codepoint == ',';
  }
  
  public static boolean is_ipathnodelims(int codepoint) {
    return is_ipath(codepoint) && !isGenDelim(codepoint);
  }

  public static boolean is_iquery(int codepoint) {
    return is_ipchar(codepoint) || 
           is_iprivate(codepoint) || 
           codepoint == ';' ||
           codepoint == '/' ||
           codepoint == '?' ||
           codepoint == '%';
  }

  public static boolean is_ifragment(int codepoint) {
    return is_ipchar(codepoint) || 
           is_iprivate(codepoint) || 
           codepoint == '/' ||
           codepoint == '?' ||
           codepoint == '%';
  }  
  
  public static boolean is_iregname(int codepoint) {
    return is_iunreserved(codepoint) || 
           codepoint == '!'  || 
           codepoint == '$'  || 
           codepoint == '&'  || 
           codepoint == '\'' || 
           codepoint == '('  || 
           codepoint == ')'  || 
           codepoint == '*'  || 
           codepoint == '+'  || 
           codepoint == ','  || 
           codepoint == ';'  || 
           codepoint == '='  || 
           codepoint == '"';
  }
  
  public static boolean is_regname(int codepoint) {
    return isUnreserved(codepoint) || 
           codepoint == '!'  || 
           codepoint == '$'  || 
           codepoint == '&'  || 
           codepoint == '\'' || 
           codepoint == '('  || 
           codepoint == ')'  || 
           codepoint == '*'  || 
           codepoint == '+'  || 
           codepoint == ','  || 
           codepoint == ';'  || 
           codepoint == '='  || 
           codepoint == '"';
  }
  
  public static boolean is_iuserinfo(int codepoint) {
    return is_iunreserved(codepoint) || 
           codepoint == ';' || 
           codepoint == ':' || 
           codepoint == '&' || 
           codepoint == '=' || 
           codepoint == '+' || 
           codepoint == '$' || 
           codepoint == ',';
  }
  
  public static boolean is_iserver(int codepoint) {
    return is_iuserinfo(codepoint) || 
           is_iregname(codepoint) || 
           isAlphaNum(codepoint) || 
           codepoint == '.' || 
           codepoint == ':' || 
           codepoint == '@' || 
           codepoint == '[' || 
           codepoint == ']' || 
           codepoint == '%' || 
           codepoint == '-';
  }

}

