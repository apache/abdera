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

import java.util.BitSet;

import org.apache.abdera.i18n.ChainableBitSet;
import org.apache.abdera.i18n.io.CharUtils;
import org.apache.abdera.i18n.io.CodepointIterator;
import org.apache.abdera.i18n.io.FilterCodepointIterator;
import org.apache.abdera.i18n.io.InvalidCharacterException;
import org.apache.abdera.i18n.io.RestrictedCodepointIterator;


/**
 * A CodepointIterator implementation that checks output against a BitSet.
 * If the iterator is set to "scanning only", the iterator will return -1
 * upon encountering a codepoint not in the set, otherwise the iterator 
 * will throw an InvalidCharacterException
 */
public class RestrictedCodepointIterator 
  extends FilterCodepointIterator {

  private BitSet bitset;
  private boolean scanningOnly = false;
  private boolean notset = false;

  protected RestrictedCodepointIterator(
    CodepointIterator internal, 
    BitSet bitset) {
      this(internal,bitset,false);
  }

  protected RestrictedCodepointIterator(
    CodepointIterator internal, 
    BitSet bitset,
    boolean scanningOnly) {
      this(internal, bitset, scanningOnly, false);
  }
  
  protected RestrictedCodepointIterator(
      CodepointIterator internal, 
      BitSet bitset,
      boolean scanningOnly,
      boolean notset) {
      super(internal);
      this.bitset = bitset;
      this.scanningOnly = scanningOnly;
      this.notset = notset;
    }

  public boolean hasNext() {
    boolean b = super.hasNext();
    if (scanningOnly) {
      try {
        int cp = peek(position());
        if (b && cp != -1 && check(cp)) return false;
      } catch (InvalidCharacterException e) { return false; }
    } 
    return b;
  }
  
  @Override
  public int next() throws InvalidCharacterException {
    int cp = super.next();
    if (cp != -1 && check(cp)) {
      if (scanningOnly) {
        position(position()-1);
        return -1;
      }
      else throw new InvalidCharacterException(cp);
    }
    return cp;
  }

  private boolean check(int cp) {
    return (!notset) ? !bitset.get(cp) : bitset.get(cp);
  }
  
  @Override
  public char[] nextChars() throws InvalidCharacterException {
    char[] chars = super.nextChars();
    if (chars != null && chars.length > 0) {
      if (chars.length == 1 && check(chars[0])) {
        if (scanningOnly) {
          position(position()-1);
          return null;
        }
        else throw new InvalidCharacterException(chars[0]);
      } else if (chars.length == 2) {
        int cp = CharUtils.toCodePoint(chars);
        if (check(cp)) {
          if (scanningOnly) {
            position(position()-2);
            return null; 
          }
          else throw new InvalidCharacterException(cp);
        }
      }
    }
    return chars;
  }
 
  public static void main(String... args) throws Exception {
    
    ChainableBitSet set = new ChainableBitSet().set2('a','b','c');
    char[] c = {'a','b','c',CharUtils.getHighSurrogate(0x10000),CharUtils.getLowSurrogate(0x10000)};
    
    CodepointIterator ci = CodepointIterator.forCharArray(c);
    RestrictedCodepointIterator rci = new RestrictedCodepointIterator(ci,set,false,true);
    while(rci.hasNext()) System.out.println(rci.next());
  }
}
