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
package org.apache.abdera.g14n.iri;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import org.apache.abdera.g14n.io.CharUtils;
import org.apache.abdera.g14n.iri.Constants;


/**
 * Performs URL Percent Encoding
 */
public final class Escaping {

  private Escaping() {}
  
  private static void encode(StringBuffer sb, byte... bytes) {
    for (byte c : bytes) {
      sb.append("%");
      sb.append(Constants.hex[(c >> 4) & 0x0f]);
      sb.append(Constants.hex[(c >> 0) & 0x0f]);
    }
  }
  
  public static String encode(String s, BitSet... maps) {
    try {
      if (s == null) return null;
      return encode(s,"utf-8",maps);
    } catch (UnsupportedEncodingException e) {
      return null; // shouldn't happen
    }
  }
  
  public static String encode(
    String s, 
    String enc, 
    BitSet... maps) 
      throws UnsupportedEncodingException {
    if (s == null) return s;
    StringBuffer sb = new StringBuffer();
    char[] chars = s.toCharArray();
    for (int n = 0; n < chars.length; n++) {
      char c = (char) chars[n];
      if (!CharUtils.isSet(c,maps) && !CharUtils.isHighSurrogate(c)) {
        encode(sb,String.valueOf(c).getBytes(enc));
      } else if (CharUtils.isHighSurrogate(c)) {
        if (!CharUtils.isSet(c,maps)) {
          StringBuffer buf = new StringBuffer();
          buf.append(c);
          buf.append(chars[++n]);
          byte[] b = buf.toString().getBytes(enc);
          encode(sb,b);
        } else {
          sb.append(c);
          sb.append(chars[++n]);
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
  public static String decode(String e, String enc) 
    throws UnsupportedEncodingException {
      DecodingReader r = new DecodingReader(e.getBytes(enc),enc);
      char[] buf = new char[e.length()];
      try {
        int l = r.read(buf);
        e = new String(buf,0,l);
      } catch (Exception ex) {}
      return e;
  }
  
  public static String decode(String e) {
    try {
      return decode(e,"utf-8");
    } catch (Exception ex) {
      return e;
    }
  }
  
  public static class DecodingInputStream 
    extends ByteArrayInputStream {

    DecodingInputStream(byte[] buf) {
      super(buf);
    }
    public int read() {
      int c = super.read();
      if (c == '%') {
        int c1 = super.read();
        int c2 = super.read();
        return decode((char)c1,(char)c2);
      } else {
        return c;
      }
    }
    @Override
    public synchronized int read(byte[] b, int off, int len) {
      int n = off;
      int i = -1;
      while ((i = read()) != -1 && n < off+len) {
        b[n++] = (byte)i;
      }
      return n - off;
    }
  }
  
  public static class DecodingReader 
    extends InputStreamReader {
      public DecodingReader(byte[] buf) {
        super(new DecodingInputStream(buf));
      }
      public DecodingReader(
        byte[] buf, 
        String encoding) 
          throws UnsupportedEncodingException {
        super(new DecodingInputStream(buf),encoding);
      }
  }
  
  private static byte decode(char c, int shift) {
    return (byte)((((c >= '0' && c <= '9') ?
      c - '0' :
      (c >= 'A' && c <= 'F') ? c - 'A' + 10 : 
        (c >= 'a' && c<= 'f') ? c - 'a' + 10 :-1)
          & 0xf) << shift);
  }
  
  private static byte decode(char c1, char c2) {
    return (byte)(decode(c1,4) | decode(c2,0));
  }
  
}
