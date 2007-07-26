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
package org.apache.abdera.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public class CompressionUtil {

  public enum CompressionCodec { GZIP, XGZIP, DEFLATE }
  
  public static OutputStream getEncodedOutputStream(
    OutputStream out, 
    CompressionCodec encoding) 
      throws IOException {
    return getEncodedOutputStream(
      out, new CompressionCodec[] {encoding});
  }
  
  public static OutputStream getEncodedOutputStream(
    OutputStream out, 
    CompressionCodec... encodings) 
      throws IOException {
    for (CompressionCodec encoding:encodings) {
      switch(encoding) {
        case GZIP:
          out = new GZIPOutputStream(out); break;
        case DEFLATE:
          out = new DeflaterOutputStream(out); break;
      }
    }
    return out;
  }
  
  public static InputStream getDecodingInputStream(
    InputStream in, 
    CompressionCodec encoding) 
      throws IOException {
    switch(encoding) {
      case GZIP:
      case XGZIP:
        in = new GZIPInputStream(in); break;
      case DEFLATE:
        in = new InflaterInputStream(in); break;
    }
    return in;
  }
  
  public static InputStream getDecodingInputStream(
    InputStream in, 
    CompressionCodec... encoding) 
      throws IOException {
    for (CompressionCodec codec : encoding) {
      in = getDecodingInputStream(in, codec);
    }
    return in;
  }
}
