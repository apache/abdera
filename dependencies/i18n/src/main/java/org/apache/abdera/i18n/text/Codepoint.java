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

public class Codepoint
  implements Serializable, 
             Cloneable,
             Comparable<Codepoint>{

  private static final long serialVersionUID = 140337939131905483L;
  
  private static final String DEFAULT_ENCODING = "UTF-8";
  private final int value;

  public Codepoint(byte[] bytes) {
    try {
      this.value = valueFromCharSequence(new String(bytes,DEFAULT_ENCODING));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
  
  public Codepoint(
    byte[] bytes, 
    String encoding) 
      throws UnsupportedEncodingException {
    this.value = valueFromCharSequence(new String(bytes,encoding));
  }
  
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
  
  public Codepoint(char value) {
    this((int)value);
  }
  
  public Codepoint(char high, char low) {
    this(CharUtils.toSupplementary(high, low).getValue());
  }
  
  public Codepoint(Codepoint codepoint) {
    this(codepoint.value);
  }
  
  public Codepoint(int value) {
    if (value < 0) 
      throw new IllegalArgumentException(
        "Invalid Codepoint");
    this.value = value;
  }
  
  public int getValue() {
    return value;
  }
  
  public boolean isSupplementary() {
    return CharUtils.isSupplementary(value);
  }
  
  public boolean isLowSurrogate() {
    return CharUtils.isLowSurrogate((char)value);
  }
  
  public boolean isHighSurrogate() {
    return CharUtils.isHighSurrogate((char)value);
  }
  
  public int compareTo(Codepoint o) {
    return value < o.value ? -1 :
           value == o.value ? 0 : 1;
  }
  
  public String toString() {
    return CharUtils.toString(value);
  }
  
  public char[] toChars() {
    return toString().toCharArray();
  }
  
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
  
  public byte[] toBytes(
    String encoding) 
      throws UnsupportedEncodingException {
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
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Codepoint other = (Codepoint) obj;
    if (value != other.value) return false;
    return true;
  }
   
  /**
   * Plane 0 (0000–FFFF): Basic Multilingual Plane (BMP). This is the plane containing most of the character assignments so far. A primary objective for the BMP is to support the unification of prior character sets as well as characters for writing systems in current use.
   * Plane 1 (10000–1FFFF): Supplementary Multilingual Plane (SMP).
   * Plane 2 (20000–2FFFF): Supplementary Ideographic Plane (SIP)
   * Planes 3 to 13 (30000–DFFFF) are unassigned
   * Plane 14 (E0000–EFFFF): Supplementary Special-purpose Plane (SSP)
   * Plane 15 (F0000–FFFFF) reserved for the Private Use Area (PUA)
   * Plane 16 (100000–10FFFF), reserved for the Private Use Area (PUA)
   **/
  public int getPlane() {
    return value / (0xFFFF + 1);
  }
  
  public Codepoint clone() {
    try {
      return (Codepoint) super.clone();
    } catch (CloneNotSupportedException e) {
      return new Codepoint(value);
    }
  }
  
}
