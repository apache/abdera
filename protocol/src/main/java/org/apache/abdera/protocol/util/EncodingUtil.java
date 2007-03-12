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
package org.apache.abdera.protocol.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.abdera.util.unicode.Normalizer;
import org.apache.commons.codec.net.QCodec;

public class EncodingUtil {

  /**
   * Used to sanitize a string.  Optionally performs Unicode Form KD normalization
   * on a string to break extended characters down, then replaces non alphanumeric
   * characters with a specified filler replacement.
   * @param slug The source string
   * @param filler The replacement string
   * @param lower True if the result should be lowercase
   * @param normalize True if the result should be Form KD normalized
   */
  public static String sanitize(String slug, String filler, boolean lower, boolean normalize) {
    if (filler == null) filler = "";
    if (slug == null) return null;
    if (normalize) {
      try {
        StringBuffer value = Normalizer.normalize(slug, Normalizer.Form.KD);
        slug = value.toString();
      } catch (Exception e) {}
    }
    slug = slug.replaceAll("[^A-Za-z0-9]",filler);
    return (lower) ? slug.toLowerCase() : slug;
  }
  
  /**
   * Used to encode a string as specified by RFC 2047
   * @param value The string to encode
   * @param charset The character set to use for the encoding
   */
  public static String encode(String value, String charset) {
    if (value == null) return null;
    try {
      return (new QCodec(charset)).encode(value);
    } catch (Exception e) {
      return value;
    }
  }
  
  /**
   * Used to decode a string as specified by RFC 2047
   * @param value The encoded string
   */
  public static String decode(String value) {
    if (value == null) return null;
    try {
      return (new QCodec()).decode(value);
    } catch (Exception e) {
      return value;
    }
  }
  
  
  public enum ContentEncoding { GZIP, XGZIP, DEFLATE }
  
  public static OutputStream getEncodedOutputStream(OutputStream out, ContentEncoding encoding) throws IOException {
    return getEncodedOutputStream(out, new ContentEncoding[] {encoding});
  }
  
  public static OutputStream getEncodedOutputStream(OutputStream out, ContentEncoding... encodings) throws IOException {
    for (ContentEncoding encoding:encodings) {
      switch(encoding) {
        case GZIP:
          out = new GZIPOutputStream(out); break;
        case DEFLATE:
          out = new DeflaterOutputStream(out); break;
      }
    }
    return out;
  }
  
  public static InputStream getDecodingInputStream(InputStream in, String ce) throws IOException {
    String[] encodings = CacheControlUtil.CacheControlParser.splitAndTrim(ce, ",", false);
    for (int n = encodings.length -1; n >= 0; n--) {
      switch(ContentEncoding.valueOf(encodings[n].toUpperCase().replaceAll("-", ""))) {
        case GZIP:
        case XGZIP:
          in = new GZIPInputStream(in); break;
        case DEFLATE:
          in = new InflaterInputStream(in); break;
      }
    }
    return in;
  }
}
