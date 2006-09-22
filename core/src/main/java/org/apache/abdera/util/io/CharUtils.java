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
package org.apache.abdera.util.io;

import java.util.BitSet;

/**
 * General utilities for dealing with Unicode characters
 */
public final class CharUtils {

  private CharUtils() {}
 
  public static int scanNot(CodepointIterator ci, BitSet set) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,true,true);
    while (rci.hasNext()) rci.next();
    return rci.position;
  }
  
  public static int scanNot(char[] array, BitSet set) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,true,true);
    while (rci.hasNext()) rci.next();
    return rci.position;
  }
  
  public static int scan(CodepointIterator ci, BitSet set) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,true);
    while (rci.hasNext()) rci.next();
    return rci.position();
  }
  
  public static int scan(char[] array, BitSet set) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,true);
    while (rci.hasNext()) rci.next();
    return rci.position();
  }
  
  public static int scan(String s, BitSet set) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharSequence(s);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,true);
    while (rci.hasNext()) rci.next();
    return rci.position;
  }
  
  public static void verifyNot(CodepointIterator ci, BitSet set) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,false,true);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verifyNot(char[] array, BitSet set) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,false,true);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verify(CodepointIterator ci, BitSet set) throws InvalidCharacterException {
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,false);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verify(char[] array, BitSet set) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharArray(array);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,false);
    while (rci.hasNext()) rci.next();
  }
  
  public static void verify(String s, BitSet set) throws InvalidCharacterException {
    CodepointIterator ci = CodepointIterator.forCharSequence(s);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,false);
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
  
  public static boolean isSet(int n, BitSet... sets) {
    if (n == -1) return false;
    BitSet set = new BitSet();
    for (BitSet s : sets) set.or(s);
    return set.get(n);
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
  
}
