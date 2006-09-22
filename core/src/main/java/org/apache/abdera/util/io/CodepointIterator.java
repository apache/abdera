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

import java.nio.CharBuffer;

/**
 * Iterate over Unicode codepoints 
 */
public abstract class CodepointIterator {

  public static CodepointIterator forCharArray(char[] array) {
    return new CharArrayCodepointIterator(array);
  }
  
  public static CodepointIterator forCharSequence(CharSequence seq) {
    return new CharSequenceCodepointIterator(seq);
  }
  
  public static CodepointIterator forByteArray(byte[] array) {
    return new ByteArrayCodepointIterator(array);
  }
  
  public static CodepointIterator forCharBuffer(CharBuffer buffer) {
    return new CharBufferCodepointIterator(buffer);
  }
  
  protected int position = -1;
  protected int limit = -1;
  
  protected abstract char get();
  
  protected abstract char get(int index);
  
  public boolean hasNext() {
    return remaining() > 0;
  }

  public int last() {
    return (position() > 0) ? get(position() - 1) : -1;
  }
  
  public int lastPosition() {
    int p = position();
    return (p > -1) ? 
      (p >= limit()) ? p : p - 1 : -1;
  }
  
  public char[] nextChars() throws InvalidCharacterException {
    if (hasNext()) {
      if (isNextSurrogate()) {
        char c1 = get();
        if (CharUtils.isHighSurrogate(c1) && position() < limit()) {
          char c2 = get();
          if (CharUtils.isLowSurrogate(c2)) {
            return new char[] {c1,c2};
          } else {
            throw new InvalidCharacterException(c2);
          }
        } else if (CharUtils.isLowSurrogate(c1) && position() > 0) {
          char c2 = get(position()-2);
          if (CharUtils.isHighSurrogate(c2)) {
            return new char[] {c1,c2};
          } else {
            throw new InvalidCharacterException(c2);
          }
        }
      }
      return new char[] {get()}; 
    } 
    return null;
  }
  
  public char[] peekChars() throws InvalidCharacterException {
    return peekChars(position());
  }
  
  private char[] peekChars(int pos) throws InvalidCharacterException {
    if (pos < 0 || pos >= limit()) return null;
    char c1 = get(pos);
    if (CharUtils.isHighSurrogate(c1) && pos < limit()) {
      char c2 = get(pos+1);
      if (CharUtils.isLowSurrogate(c2)) {
        return new char[] {c1,c2};
      } else {
        throw new InvalidCharacterException(c2);
      }
    } else if (CharUtils.isLowSurrogate(c1) && pos > 1) {
      char c2 = get(pos-1);
      if (CharUtils.isHighSurrogate(c2)) {
        return new char[] {c2,c1};
      } else {
        throw new InvalidCharacterException(c2);
      }
    } else  return new char[] {c1}; 
  }
  
  public int next() throws InvalidCharacterException {
    char[] chars = nextChars();
    return (chars == null) ? -1 :
      (chars.length == 1) ? chars[0] :
      CharUtils.toCodePoint(chars[0], chars[1]);
  }

  public int peek() throws InvalidCharacterException {
    char[] chars = peekChars();
    return (chars == null) ? -1 :
      (chars.length == 1) ? chars[0] :
      CharUtils.toCodePoint(chars[0], chars[1]);
  }
  
  public int peek(int index) throws InvalidCharacterException {
    char[] chars = peekChars(index);
    return (chars == null) ? -1 :
      (chars.length == 1) ? chars[0] :
      CharUtils.toCodePoint(chars[0], chars[1]);
  }
  
  public void position(int n) {
    if (n < 0 || n > limit()) throw new ArrayIndexOutOfBoundsException(n);
    position = n;
  }
  
  public int position() {
    return position;
  }

  public int limit() {
    return limit;
  }
  
  public int remaining() {
    return limit - position();
  }
  
  private boolean isNextSurrogate() {
    if (!hasNext()) return false;
    char c = get(position());
    return CharUtils.isHighSurrogate(c) || CharUtils.isLowSurrogate(c);
  }

  public boolean isHigh(int index) {
    if (index < 0 || index > limit()) throw new ArrayIndexOutOfBoundsException(index);
    return CharUtils.isHighSurrogate(get(index));
  }

  public boolean isLow(int index) {
    if (index < 0 || index > limit()) throw new ArrayIndexOutOfBoundsException(index);
    return CharUtils.isLowSurrogate(get(index));
  }
  
}
