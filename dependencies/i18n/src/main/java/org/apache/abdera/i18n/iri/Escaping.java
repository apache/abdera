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
package org.apache.abdera.i18n.iri;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;

import org.apache.abdera.i18n.io.CharUtils;
import org.apache.abdera.i18n.io.CharUtils.Profile;


/**
 * Performs URL Percent Encoding
 */
public final class Escaping {

  public final static char[] HEX = {
    '0','1','2','3','4','5','6','7',
    '8','9','A','B','C','D','E','F'
  };
  
  private Escaping() {}
  
  private static void encode(Appendable sb, byte... bytes) {
    encode(sb,0,bytes.length,bytes);
  }
  
  private static void encode(Appendable sb, int offset, int length, byte... bytes) {
    try {
      for (int n = offset, i = 0; n < bytes.length && i < length; n++, i++) {
        byte c = bytes[n];
        sb.append("%");
        sb.append(HEX[(c >> 4) & 0x0f]);
        sb.append(HEX[(c >> 0) & 0x0f]);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static String encode(char... chars) {
    return encode(chars,0,chars.length,"UTF-8",new Profile[0]);
  }
  
  public static String encode(char[] chars, Profile profile) {
    return encode(chars,0,chars.length,"UTF-8",new Profile[] {profile});
  }
  
  public static String encode(char[] chars, Profile... profiles) {
    return encode(chars,0,chars.length,"UTF-8",profiles);
  }

  public static String encode(char[] chars, String enc) {
    return encode(chars,0,chars.length,enc,new Profile[0]);
  }
  
  public static String encode(char[] chars, String enc, Profile profile) {
    return encode(chars,0,chars.length,enc,new Profile[] {profile});
  }
  
  public static String encode(char[] chars, String enc, Profile... profiles) {
    return encode(chars,0,chars.length,enc,profiles);
  }
  
  public static String encode(char[] chars, int offset, int length) {
    return encode(chars,offset,length,"UTF-8",new Profile[0]);
  }

  public static String encode(char[] chars, int offset, int length, String enc) {
    return encode(chars,offset,length,enc,new Profile[0]);
  }
  
  public static String encode(char[] chars, int offset, int length, Profile profile) {
    return encode(chars,offset,length,"UTF-8",new Profile[] {profile});
  }
  
  public static String encode(char[] chars, int offset, int length, Profile... profiles) {
    return encode(chars,offset,length,"UTF-8",profiles);
  }
  
  public static String encode(char[] chars, int offset, int length, String enc, Profile profile) {
    return encode(chars,offset,length,enc,new Profile[] {profile});    
  }
  
  public static String encode(char[] chars, int offset, int length, String enc, Profile... profiles) {
    try {
      return encode((CharSequence)CharBuffer.wrap(chars,offset,length),enc,profiles);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static String encode(InputStream in) throws IOException {
    StringBuilder buf = new StringBuilder();
    byte[] chunk = new byte[1024];
    int r = -1;
    while((r = in.read(chunk)) > -1)
      encode(buf,0,r,chunk);
    return buf.toString();
  }
  
  public static String encode(
    InputStream in, 
    String charset) throws IOException {
      return encode(in,charset,"UTF-8",new Profile[0]);
  }

  public static String encode(
    InputStream in, 
    String charset,
    Profile profile) 
      throws IOException {
    return encode(in,charset,"UTF-8",new Profile[] {profile});
  }

  public static String encode(
    InputStream in, 
    String charset,
    String enc) throws IOException {
      return encode(in,charset,enc,new Profile[0]);
  }

  public static String encode(
    InputStream in, 
    String charset,
    String enc,
    Profile profile) 
      throws IOException {
    return encode(in,charset,enc,new Profile[] {profile});
  }
  
  public static String encode(
    InputStream in,
    String charset,
    String enc,
    Profile... profiles) 
      throws IOException {
    return encode(new InputStreamReader(in,charset),enc,profiles);
  }

  public static String encode(
    InputStream in,
    String charset,
    Profile... profiles) 
      throws IOException {
    return encode(new InputStreamReader(in,charset),"UTF-8",profiles);
  }
  
  public static String encode(
    Reader reader) 
      throws IOException {
    return encode(reader,"UTF-8", new Profile[0]);
  }

  public static String encode(
    Readable readable) 
      throws IOException {
    return encode(readable,"UTF-8", new Profile[0]);
  }
  
  public static String encode(
    Reader reader, 
    String enc) 
      throws IOException {
    return encode(reader, enc, new Profile[0]);
  }

  public static String encode(
    Readable readable, 
    String enc) 
      throws IOException {
    return encode(readable, enc, new Profile[0]);
  }
  
  public static String encode(
    Reader reader, 
    String enc, 
    Profile profile)
      throws IOException {
    return encode(reader,enc,new Profile[] {profile});
  }

  public static String encode(
    Reader reader,  
    Profile profile)
      throws IOException {
    return encode(reader,"UTF-8",new Profile[] {profile});
  }

  public static String encode(
    Reader reader,  
    Profile... profiles)
      throws IOException {
    return encode(reader,"UTF-8",profiles);
  }
  
  public static String encode(
    Readable readable, 
    String enc, 
    Profile profile)
      throws IOException {
    return encode(readable,enc,new Profile[] {profile});
  }

  public static String encode(
    Readable readable,  
    Profile profile)
      throws IOException {
    return encode(readable,"UTF-8",new Profile[] {profile});
  }

  public static String encode(
    Readable readable,  
    Profile... profiles)
      throws IOException {
    return encode(readable,"UTF-8",profiles);
  }
  
  private static void processChars(
    StringBuilder sb,
    CharBuffer chars, 
    String enc, 
    Profile... profiles) 
      throws IOException {
    for (int n = 0; n < chars.length(); n++) {
      char c = chars.charAt(n);
      if (!CharUtils.isHighSurrogate(c) && check(c,profiles)) {
        encode(sb,String.valueOf(c).getBytes(enc));
      } else if (CharUtils.isHighSurrogate(c)) {
        if (check(c,profiles)) {
          StringBuilder buf = new StringBuilder();
          buf.append(c);
          buf.append(chars.charAt(++n));
          byte[] b = buf.toString().getBytes(enc);
          encode(sb,b);
        } else {
          sb.append(c);
          sb.append(chars.charAt(++n));
        }
      } else {
        sb.append(c);
      }
    }
  }
  
  public static String encode(
    Readable readable,
    String enc,
    Profile... profiles) 
      throws IOException {
    StringBuilder sb = new StringBuilder();
    CharBuffer chars = CharBuffer.allocate(1024);
    while (readable.read(chars) > -1) {
      chars.flip();
      processChars(sb, chars, enc, profiles);
    }
    return sb.toString();
  }
  
  public static String encode(
    Reader reader,
    String enc,
    Profile... profiles) 
      throws IOException {
    StringBuilder sb = new StringBuilder();
    char[] chunk = new char[1024];
    int r = -1;
    while ((r = reader.read(chunk)) > -1)
      processChars(
        sb, CharBuffer.wrap(chunk, 0, r), 
        enc, profiles);
    return sb.toString();
  }
  
  public static String encode(byte... bytes) {
    StringBuilder buf = new StringBuilder();
    encode(buf,bytes);
    return buf.toString();
  }
  
  public static String encode(byte[] bytes, int off, int len) {
    StringBuilder buf = new StringBuilder();
    encode(buf,off,len,bytes);
    return buf.toString();
  }
  
  public static String encode(CharSequence s) {
    return encode(s,Profile.NONE);
  }
  
  public static String encode(CharSequence s, Profile profile) {
    return encode(s, new Profile[] {profile});
  }
  
  public static String encode(CharSequence s, Profile... profiles) {
    try {
      if (s == null) return null;
      return encode(s,"utf-8",profiles);
    } catch (UnsupportedEncodingException e) {
      return null; // shouldn't happen
    }
  }
  
  public static String encode(CharSequence s, int offset, int length) {
    return encode(s,offset,length,Profile.NONE);
  }
  
  public static String encode(CharSequence s, int offset, int length, Profile profile) {
    return encode(s,offset,length, new Profile[] {profile});
  }
  
  public static String encode(CharSequence s, int offset, int length, Profile... profiles) {
    try {
      if (s == null) return null;
      return encode(s,offset,length,"utf-8",profiles);
    } catch (UnsupportedEncodingException e) {
      return null; // shouldn't happen
    }
  }
  
  private static boolean check(int codepoint, Profile... profiles) {
    for (Profile profile : profiles) {
      if (!profile.check(codepoint)) return false;
    }
    return true;
  }

  public static String encode(
      CharSequence s,
      int offset,
      int length,
      String enc, 
      Profile... profiles) 
        throws UnsupportedEncodingException {
    int end = Math.min(s.length(), offset+length);
    CharSequence seq = s.subSequence(offset, end);
    return encode(seq,enc,profiles);
  }
  
  public static String encode(
    CharSequence s, 
    String enc, 
    Profile... profiles) 
      throws UnsupportedEncodingException {
    if (s == null) return s.toString();
    StringBuilder sb = new StringBuilder();

    for (int n = 0; n < s.length(); n++) {
        char c = s.charAt(n);
      if (!CharUtils.isHighSurrogate(c) && check(c,profiles)) {
        encode(sb,String.valueOf(c).getBytes(enc));
      } else if (CharUtils.isHighSurrogate(c)) {
        if (check(c,profiles)) {
          StringBuilder buf = new StringBuilder();
          buf.append(c);
          buf.append(s.charAt(++n));
          byte[] b = buf.toString().getBytes(enc);
          encode(sb,b);
        } else {
          sb.append(c);
          sb.append(s.charAt(++n));
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
  
  public static class EncodingOutputStream 
    extends FilterOutputStream {

    public EncodingOutputStream(OutputStream out) {
      super(out);
    }
    @Override 
    public void write(byte[] b, int off, int len) throws IOException {
      String enc = encode(b,off,len);
      out.write(enc.getBytes("UTF-8"));
    }
    @Override 
    public void write(byte[] b) throws IOException {
      String enc = encode(b);
      out.write(enc.getBytes("UTF-8"));
    }
    @Override 
    public void write(int b) throws IOException {
      String enc = encode((byte)b);
      out.write(enc.getBytes("UTF-8"));
    }
  }

  public static class EncodingWriter
    extends FilterWriter {
    private final Profile[] profiles;
    public EncodingWriter(OutputStream out) {
      this(new OutputStreamWriter(out));
    }
    public EncodingWriter(OutputStream out,Profile profile) {
      this(new OutputStreamWriter(out),profile);
    }    
    public EncodingWriter(OutputStream out,Profile... profiles) {
      this(new OutputStreamWriter(out),profiles);
    }    
    public EncodingWriter(Writer out) {
      this(out,new Profile[0]);
    }
    public EncodingWriter(Writer out, Profile profile) {
      this(out,new Profile[] {profile});
    }
    public EncodingWriter(Writer out, Profile... profiles) {
      super(out);
      this.profiles = profiles;
    }
    @Override 
    public void write(char[] b, int off, int len) throws IOException {
      String enc = encode(b,off,len,profiles);
      out.write(enc.toCharArray());
    }
    @Override 
    public void write(char[] b) throws IOException {
      String enc = encode(b,profiles);
      out.write(enc.toCharArray());
    }
    @Override 
    public void write(int b) throws IOException {
      String enc = encode(new char[] {(char)b},profiles);
      out.write(enc.toCharArray());
    }
    @Override 
    public void write(
      String str, 
      int off, 
      int len)
        throws IOException {
      String enc = encode(str,off,len,profiles);
      out.write(enc.toCharArray());
    }
  }

  public static class DecodingInputStream 
    extends FilterInputStream {
    public DecodingInputStream(InputStream in) {
      super(in);
    }
    public DecodingInputStream(byte[] in) {
      super(new ByteArrayInputStream(in));
    }
    public int read() throws IOException {
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
    public synchronized int read(byte[] b, int off, int len) throws IOException {
      int n = off;
      int i = -1;
      while ((i = read()) != -1 && n < off+len) {
        b[n++] = (byte)i;
      }
      return n - off;
    }
    @Override 
    public int read(byte[] b) throws IOException {
      return read(b,0,b.length);
    }
    @Override 
    public long skip(long n) throws IOException {
      long i = 0;
      for (; i < n; i++) read();
      return i;
    }
    
  }
  
  public static class DecodingReader 
    extends FilterReader {
    public DecodingReader(byte[] buf) 
      throws UnsupportedEncodingException {
        this(new ByteArrayInputStream(buf));
    }
    public DecodingReader(
      byte[] buf,String enc) 
        throws UnsupportedEncodingException {
      this(new ByteArrayInputStream(buf),enc);
    }
    public DecodingReader(
      InputStream in) 
        throws UnsupportedEncodingException {
      this(in, "UTF-8");
    }
    public DecodingReader(
      InputStream in, 
      String enc) 
        throws UnsupportedEncodingException {
      this(new InputStreamReader(in,enc));
    }
    public DecodingReader(Reader in) {
      super(in);
    }
    public int read() throws IOException {
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
    public synchronized int read(char[] b, int off, int len) throws IOException {
      int n = off;
      int i = -1;
      while ((i = read()) != -1 && n < off+len) {
        b[n++] = (char)i;
      }
      return n - off;
    }
    @Override 
    public int read(char[] b) throws IOException {
      return read(b,0,b.length);
    }
    @Override 
    public long skip(long n) throws IOException {
      long i = 0;
      for (; i < n; i++) read();
      return i;
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
