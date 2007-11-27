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

import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Provides an iterator over Unicode Codepoints
 */
public abstract class CodepointIterator {

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
    return new ByteArrayCodepointIterator(array,charset);
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
    return new ReadableByteChannelCodepointIterator(channel,charset);
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
    return new ReadableByteChannelCodepointIterator(Channels.newChannel(in),charset);
  }
  
  /**
   * Get a CodepointIterator for the specified Reader
   */
  public static CodepointIterator forReader(Reader in) {
    return new ReaderCodepointIterator(in);
  }

  protected int position = -1;
  protected int limit = -1;

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
   * Return the last char in the iterator
   */
  public int last() {
    return (position() > 0) ? get(position() - 1) : -1;
  }
  
  /**
   * Return the final index position
   */
  public int lastPosition() {
    int p = position();
    return (p > -1) ? 
      (p >= limit()) ? p : p - 1 : -1;
  }
  
  /**
   * Return the next chars.  If the codepoint is not supplemental,
   * the char array will have a single member.  If the codepoint is 
   * supplemental, the char array will have two members, representing
   * the high and low surrogate chars
   */
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

  /**
   * Peek the next chars in the iterator. If the codepoint is not supplemental,
   * the char array will have a single member.  If the codepoint is 
   * supplemental, the char array will have two members, representing
   * the high and low surrogate chars
   */
  public char[] peekChars() throws InvalidCharacterException {
    return peekChars(position());
  }
  
  /**
   * Peek the specified chars in the iterator. If the codepoint is not supplemental,
   * the char array will have a single member.  If the codepoint is 
   * supplemental, the char array will have two members, representing
   * the high and low surrogate chars
   */
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
  
  /**
   * Return the next codepoint
   */
  public int next() throws InvalidCharacterException {
    char[] chars = nextChars();
    return (chars == null) ? -1 :
      (chars.length == 1) ? chars[0] :
      CharUtils.toCodePoint(chars[0], chars[1]);
  }

  /**
   * Peek the next codepoint
   */
  public int peek() throws InvalidCharacterException {
    char[] chars = peekChars();
    return (chars == null) ? -1 :
      (chars.length == 1) ? chars[0] :
      CharUtils.toCodePoint(chars[0], chars[1]);
  }
  
  /**
   * Peek the specified codepoint
   */
  public int peek(int index) throws InvalidCharacterException {
    char[] chars = peekChars(index);
    return (chars == null) ? -1 :
      (chars.length == 1) ? chars[0] :
      CharUtils.toCodePoint(chars[0], chars[1]);
  }
  
  /**
   * Set the iterator position
   */
  public void position(int n) {
    if (n < 0 || n > limit()) throw new ArrayIndexOutOfBoundsException(n);
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
    if (!hasNext()) return false;
    char c = get(position());
    return CharUtils.isHighSurrogate(c) || CharUtils.isLowSurrogate(c);
  }

  /**
   * Returns true if the char at the specified index is a high surrogate
   */
  public boolean isHigh(int index) {
    if (index < 0 || index > limit()) throw new ArrayIndexOutOfBoundsException(index);
    return CharUtils.isHighSurrogate(get(index));
  }

  /**
   * Returns true if the char at the specified index is a low surrogate
   */
  public boolean isLow(int index) {
    if (index < 0 || index > limit()) throw new ArrayIndexOutOfBoundsException(index);
    return CharUtils.isLowSurrogate(get(index));
  }
  
}
