package org.apache.abdera.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.abdera.util.XmlUtil.XMLVersion;

/**
 * A reader implementation that filters out characters that are not allowed
 * in XML 1.0 or XML 1.1 documents.  The default xMLVersion is to assume XML 1.0.
 * 
 * By default, invalid characters are simply removed from the stream.  
 * Alternatively, a replacement character can be provided so long as it
 * is a valid XML character itself.
 */
public class XmlRestrictedCharReader 
  extends FilterReader {

  /**
   * The XMLVersion determines which set of restrictions to apply depending 
   * on the XML version being parsed
   */
  private final XMLVersion version;
  private final char replacement;
  
  public XmlRestrictedCharReader(InputStream in) {
    this(new InputStreamReader(in));
  }
  
  public XmlRestrictedCharReader(
    InputStream in, 
    String charset) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset));
  }
  
  public XmlRestrictedCharReader(
    InputStream in, 
    XMLVersion version) {
      this(new InputStreamReader(in),version);
  }
  
  public XmlRestrictedCharReader(
    InputStream in, 
    String charset, 
    XMLVersion version) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset),version);
  }
  
  public XmlRestrictedCharReader(
    InputStream in, 
    char replacement) {
      this(new InputStreamReader(in),replacement);
  }
  
  public XmlRestrictedCharReader(
    InputStream in, 
    String charset,
    char replacement) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset),replacement);
  }
  
  public XmlRestrictedCharReader(
    InputStream in, 
    XMLVersion version, 
    char replacement) {
      this(new InputStreamReader(in),version, replacement);
  }
  
  public XmlRestrictedCharReader(
    InputStream in, 
    String charset, 
    XMLVersion version,
    char replacement) 
      throws UnsupportedEncodingException {
    this(new InputStreamReader(in,charset),version,replacement);
  }
  
  public XmlRestrictedCharReader(
    Reader in) {
      this(in,XMLVersion.XML10,(char)0);
  }
  
  public XmlRestrictedCharReader(
    Reader in, 
    XMLVersion version) {
      this(in,version,(char)0);
  }
  
  public XmlRestrictedCharReader(
    Reader in, 
    char replacement) {
      this(in,XMLVersion.XML10,replacement);
  }
  
  public XmlRestrictedCharReader(
    Reader in, 
    XMLVersion version, 
    char replacement) {
      super(in);
      this.version = version;
      this.replacement = replacement;
      if (replacement != 0 && 
          ((!Character.isValidCodePoint(replacement)) || 
          XmlUtil.restricted(version,replacement))) 
            throw new IllegalArgumentException();
  }

  @Override
  public int read() throws IOException {
    int c = -1;
    if (replacement == 0) {
      while(((c = super.read()) != -1 && XmlUtil.restricted(version,c))) {}
    } else {
      c = super.read();
      if (c != -1 && XmlUtil.restricted(version,c)) c = replacement;
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

}
