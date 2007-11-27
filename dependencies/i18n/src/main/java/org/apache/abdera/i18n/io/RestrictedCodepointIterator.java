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

import org.apache.abdera.i18n.io.CharUtils.Profile;



/**
 * A CodepointIterator implementation that checks output against a CharUTils.Profile.
 * If the iterator is set to "scanning only", the iterator will return -1
 * upon encountering a codepoint not in the set, otherwise the iterator 
 * will throw an InvalidCharacterException
 */
public class RestrictedCodepointIterator 
  extends FilterCodepointIterator {

  private final Profile profile;
  private final boolean scanningOnly;
  private final boolean notset;

  protected RestrictedCodepointIterator(
    CodepointIterator internal, 
    Profile profile) {
      this(internal,profile,false);
  }

  protected RestrictedCodepointIterator(
    CodepointIterator internal, 
    Profile profile,
    boolean scanningOnly) {
      this(internal, profile, scanningOnly, false);
  }
  
  protected RestrictedCodepointIterator(
    CodepointIterator internal, 
    Profile profile,
    boolean scanningOnly,
    boolean notset) {
      super(internal);
      this.profile = profile;
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
    boolean answer = !profile.check(cp);
    return (!notset) ? !answer : answer;
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
 
}
