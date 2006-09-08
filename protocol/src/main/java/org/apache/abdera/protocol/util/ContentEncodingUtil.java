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

public class ContentEncodingUtil {

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
    String[] encodings = CacheControlParser.splitAndTrim(ce, ",", false);
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
