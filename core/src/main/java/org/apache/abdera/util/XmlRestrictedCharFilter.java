package org.apache.abdera.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.abdera.i18n.ChainableBitSet;

/**
 * A reader implementation that filters out characters that are not allowed
 * in XML 1.0 or XML 1.1 documents.  The default mode is to assume XML 1.0.
 * 
 * By default, invalid characters are simply removed from the stream.  
 * Alternatively, a replacement character can be provided so long as it
 * is a valid XML character itself.
 */
public class XmlRestrictedCharFilter 
  extends FilterReader {

  /**
   * The mode determines which set of restrictions to apply depending 
   * on the XML version being parsed
   */
  public enum Mode { XML10, XML11 };
  
  private final ChainableBitSet set;
  private final char replacement;
  
  protected XmlRestrictedCharFilter(InputStream in) {
    this(new InputStreamReader(in));
  }
  
  protected XmlRestrictedCharFilter(
    InputStream in, 
    String charset) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset));
  }
  
  protected XmlRestrictedCharFilter(
    InputStream in, 
    Mode mode) {
      this(new InputStreamReader(in),mode);
  }
  
  protected XmlRestrictedCharFilter(
    InputStream in, 
    String charset, 
    Mode mode) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset),mode);
  }
  
  protected XmlRestrictedCharFilter(
    InputStream in, 
    char replacement) {
      this(new InputStreamReader(in),replacement);
  }
  
  protected XmlRestrictedCharFilter(
    InputStream in, 
    String charset,
    char replacement) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset),replacement);
  }
  
  protected XmlRestrictedCharFilter(
    InputStream in, 
    Mode mode, 
    char replacement) {
      this(new InputStreamReader(in),mode, replacement);
  }
  
  protected XmlRestrictedCharFilter(
    InputStream in, 
    String charset, 
    Mode mode,
    char replacement) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset),mode,replacement);
  }
  
  
  protected XmlRestrictedCharFilter(
    Reader in) {
      this(in,Mode.XML10,(char)0);
  }
  
  protected XmlRestrictedCharFilter(
    Reader in, 
    Mode mode) {
      this(in,mode,(char)0);
  }
  
  protected XmlRestrictedCharFilter(
    Reader in, 
    char replacement) {
      this(in,Mode.XML10,replacement);
  }
  
  protected XmlRestrictedCharFilter(
    Reader in, 
    Mode mode, 
    char replacement) {
      super(in);
      this.set = mode == Mode.XML10 ? restrictedchar10 : restrictedchar11;
      this.replacement = replacement;
      if (replacement != 0 && 
          ((!Character.isValidCodePoint(replacement)) || 
          set.get(replacement))) 
            throw new IllegalArgumentException();
  }

  @Override
  public int read() throws IOException {
    int c = -1;
    if (replacement == 0) {
      while(((c = super.read()) != -1 && set.get(c))) {}
    } else {
      c = super.read();
      if (c != -1 && set.get(c)) c = replacement;
    }
    return c;
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int n = off;
    for (; n < Math.min(len,cbuf.length-off); n++) {
      int r = read();
      if (r != -1) cbuf[n] = (char)r;
      else break;
    }
    return n - off;
  }

  private final ChainableBitSet restrictedchar10 =
    new ChainableBitSet().set2(0, 8)
                         .set2(11, 12)
                         .set2(14, 31)
                         .set2(55296, 57343)
                         .set2(65534, 65535);

  private final ChainableBitSet restrictedchar11 = 
    new ChainableBitSet().set2(0, 8)
                         .set2(11, 12)
                         .set2(14, 31)
                         .set2(127, 159)
                         .set2(55296, 57343)
                         .set2(65534, 65535);
}
