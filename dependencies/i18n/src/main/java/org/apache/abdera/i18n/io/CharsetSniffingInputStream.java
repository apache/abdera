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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Will attempt to autodetect the character encoding from the stream
 * This will preserve the BOM if it exists
 */
public class CharsetSniffingInputStream 
  extends FilterInputStream {
  
  private static final byte[] UTF32be  = new byte[] {0x00,0x00,0xFFFFFFFE,0xFFFFFFFF};
  private static final byte[] UTF32le  = new byte[] {0xFFFFFFFF,0xFFFFFFFE,0x00,0x00};
  private static final byte[] INVALID1 = new byte[] {0xFFFFFFFE,0xFFFFFFFF,0x00,0x00};
  private static final byte[] INVALID2 = new byte[] {0x00,0x00,0xFFFFFFFF,0xFFFFFFFE};
  private static final byte[] UTF16be  = new byte[] {0xFFFFFFFE,0xFFFFFFFF};
  private static final byte[] UTF16le  = new byte[] {0xFFFFFFFF,0xFFFFFFFE};
  private static final byte[] UTF8     = new byte[] {0xFFFFFFEF,0xFFFFFFBB,0xFFFFFFBF};
  private static final byte[] UTF32be2 = new byte[] {0x00,0x00,0x00,0x3C};
  private static final byte[] UTF32le2 = new byte[] {0x3C,0x00,0x00,0x00};
  private static final byte[] UTF16be2 = new byte[] {0x00,0x3C,0x00,0x3F};
  private static final byte[] UTF16le2 = new byte[] {0x3C,0x00,0x3F,0x00};
  
  protected String encoding;
  protected boolean bomset = false;
  
  public CharsetSniffingInputStream(InputStream in) {
    super(
      !(in instanceof PeekAheadInputStream) ? 
        new PeekAheadInputStream(in,4) : in);
    try {
      encoding = detectEncoding();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isBomSet() {
    return bomset;
  }
  
  public String getEncoding() {
    return encoding;
  }
  
  protected PeekAheadInputStream getInternal() {
    return (PeekAheadInputStream)in;
  }
  
  private static boolean equals(byte[] a1, int len, byte[] a2) {
    for (int n = 0, i = 0; n < len; n++, i++) {  
      if (a1[n] != a2[i]) return false;
    }
    return true;
  }
  
  protected String detectEncoding() throws IOException {
    PeekAheadInputStream pin = (PeekAheadInputStream) this.in;
    byte[] bom = new byte[4];
    pin.peek(bom);
    String charset = null;
    if (equals(bom,4,UTF32be)) {
      bomset = true;
      return "utf-32";
    } else if (equals(bom,4,UTF32le)) {
      bomset = true;
      return "utf-32";
    } else if ((equals(bom,4,INVALID1)) ||
               (equals(bom,4,INVALID2))) {
      bomset = true;
      return null;
    } else if (equals(bom,2,UTF16be)) {
      bomset = true;
      return "utf-16";
    } else if (equals(bom,2,UTF16le)) {
      bomset = true;
      return "utf-16";
    } else if (equals(bom,3,UTF8))  {
      bomset = true;
      return "utf-8";
    } else if (equals(bom,4,UTF32be2)) {
      charset = "utf-32be";
    } else if (equals(bom,4,UTF32le2)) {
      charset = "utf-32le";
    } else if (equals(bom,4,UTF16be2)) {
      charset = "utf-16be";
    } else if (equals(bom,4,UTF16le2)) {
      charset = "utf-16le";
    }
    bomset = false;
    return charset;
  }
  
}
