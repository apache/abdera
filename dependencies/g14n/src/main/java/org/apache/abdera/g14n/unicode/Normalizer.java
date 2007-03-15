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
package org.apache.abdera.g14n.unicode;

import java.io.IOException;

import org.apache.abdera.g14n.io.CharUtils;
import org.apache.abdera.g14n.io.CodepointIterator;
import org.apache.abdera.g14n.unicode.UnicodeCharacterDatabase;


/**
 * Performs Unicode Normalization (Form D,C,KD and KC)
 */
public final class Normalizer {

  public enum Mask {
    NONE,
    COMPATIBILITY,
    COMPOSITION
  }
  
  public enum Form { 
    D, 
    C(Mask.COMPOSITION), 
    KD(Mask.COMPATIBILITY), 
    KC(Mask.COMPATIBILITY,Mask.COMPOSITION);
    
    private int mask = 0;

    Form(Mask... masks) {
      for (Mask mask : masks) {
        this.mask |= (mask.ordinal());
      }
    }
    
    public boolean isCompatibility() {
      return (mask & (Mask.COMPATIBILITY.ordinal())) != 0;
    }
    
    public boolean isCanonical() {
      return !isCompatibility();
    }
    
    public boolean isComposition() {
      return (mask & (Mask.COMPOSITION.ordinal())) != 0;
    }
  }
  
  private Normalizer() {}
  
  /**
   * Normalize the string using NFKC
   */
  public static StringBuffer normalize(String source) throws IOException {
    return normalize(source, Form.KC);
  }
  
  /**
   * Normalize the string using the specified Form
   */
  public static StringBuffer normalize(
    String source, 
    Form form) 
      throws IOException {
    return normalize(source, form, new StringBuffer());
  }
  
  /**
   * Normalize the string into the given StringBuffer using the given Form
   */
  public static StringBuffer normalize(
    String source, 
    Form form, 
    StringBuffer buf) 
      throws IOException {
      UnicodeCharacterDatabase ucd = UnicodeCharacterDatabase.getInstance();
      if (source.length() != 0 && ucd != null) {
        decompose(ucd, source, form, buf);
        compose(ucd, form, buf);
      }
      return buf;
  }
  
  private static void decompose(
    UnicodeCharacterDatabase ucd,
    String source, 
    Form form, 
    StringBuffer buf) 
      throws IOException {
      StringBuffer internal = new StringBuffer();
      CodepointIterator ci = CodepointIterator.forCharSequence(source);
      boolean canonical = form.isCanonical();
      while (ci.hasNext()) {
        int c = ci.next();
        internal.setLength(0);
        ucd.decompose(c, canonical, internal);
        CodepointIterator ii = CodepointIterator.forCharSequence(internal);
        while(ii.hasNext()) {
          int ch = ii.next();
          int i = findInsertionPoint(ucd, buf, ch);
          buf.insert(i,CharUtils.toString(ch));
        }
      }
    
  }
  
  private static int findInsertionPoint(
    UnicodeCharacterDatabase ucd, 
    StringBuffer buf, int c) {
    int cc = ucd.getCanonicalClass(c);
    int i = buf.length();
    if (cc != 0) {
      int ch;
      for (; i > 0; i -= CharUtils.size(c)) {
        ch = CharUtils.charAt(buf, i-1);
        if (ucd.getCanonicalClass(ch) <= cc) break;
      }
    }
    return i;
  }
  
  private static void compose(
    UnicodeCharacterDatabase ucd,
    Form form, 
    StringBuffer buf) 
      throws IOException {
    if (!form.isComposition()) return;
    int pos = 0;
    int lc = CharUtils.charAt(buf, pos);
    int cpos = CharUtils.size(lc);    
    int lcc = ucd.getCanonicalClass(lc);
    if (lcc != 0) lcc = 256;
    int len = buf.length();
    int c;
    for (int dpos = cpos; dpos < buf.length(); dpos += CharUtils.size(c)) {
      c = CharUtils.charAt(buf,dpos);
      int cc = ucd.getCanonicalClass(c);
      int composite = ucd.getPairComposition(lc, c);
      if (composite != '\uFFFF' && (lcc < cc || lcc == 0)) {
        CharUtils.setChar(buf, pos, composite);
        lc = composite;
      } else {
        if (cc == 0) {
          pos = cpos;
          lc = c;
        }
        lcc = cc;
        CharUtils.setChar(buf,cpos,c);
        if (buf.length() != len) {
          dpos += buf.length() - len;
          len = buf.length();
        }
        cpos += CharUtils.size(c);
      }
    }
    buf.setLength(cpos);
  }
  
  public static void main(String... args) throws Exception {
    
    UnicodeCharacterDatabase.main("g14n/src/main/resources/org/apache/abdera/g14n/unicode/data/ucd.res");
    
  }
}
