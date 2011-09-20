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
package org.apache.abdera2.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.abdera2.common.text.CharUtils;

public class Compression {

    public enum CompressionCodec {
        GZIP, XGZIP, DEFLATE;

        public static CompressionCodec value(String encoding) {
            if (encoding == null)
                throw new IllegalArgumentException();
            return valueOf(encoding.toUpperCase().replaceAll("-", ""));
        }

        public OutputStream wrap(OutputStream out) throws IOException {
          switch (this) {
            case XGZIP:
            case GZIP:
              return new GZIPOutputStream(out);
            case DEFLATE:
              return new DeflaterOutputStream(out);
            default: throw new IllegalArgumentException(
              "Unknown Compression Codec");
          }          
        }
     
        public InputStream wrap(InputStream in) throws IOException {
          switch (this) {
            case GZIP:
            case XGZIP:
                return new GZIPInputStream(in);
            case DEFLATE:
                return new InflaterInputStream(in);
            default: throw new IllegalArgumentException(
              "Unknown Compression Codec");
          }
        }
    }

    public static CompressionCodec getCodec(String name) {
        CompressionCodec codec = null;
        if (name == null)
            return null;
        try {
            codec = CompressionCodec.valueOf(name.toUpperCase().trim());
        } catch (Exception e) {}
        return codec;
    }

    public static OutputStream wrap(
        OutputStream out, 
        CompressionCodec... codecs)
        throws IOException {
      if (out == null)
        throw new IllegalArgumentException(
            "OutputStream must not be null");
      if (codecs.length == 0)
        throw new IllegalArgumentException(
            "At least one codec must be specified");
      for (int n = codecs.length - 1; n >= 0; n--)
        out = codecs[n].wrap(out);
      return out;      
    }
    
    public static OutputStream wrap(
        OutputStream out, 
        CompressionCodec codec,
        CompressionCodec... codecs)
        throws IOException {
        if (out == null)
          throw new IllegalArgumentException(
              "OutputStream must not be null");
        if (codec == null)
          throw new IllegalArgumentException(
              "At least one codec must be specified");
        for (int n = codecs.length - 1; n >= 0; n--)
          out = codecs[n].wrap(out);
        out = codec.wrap(out);
        return out;
    }

    public static InputStream wrap(
      InputStream in, 
      CompressionCodec... codecs)
    throws IOException {
      if (in == null)
        throw new IllegalArgumentException(
            "InputStream must not be null");
      if (codecs.length == 0)
        throw new IllegalArgumentException(
            "At least one codec must be specified");
      for (int n = codecs.length - 1; n >= 0; n--)
        in = codecs[n].wrap(in);
      return in;
    }
    
    public static InputStream wrap(
        InputStream in, 
        CompressionCodec codec,
        CompressionCodec... codecs) 
          throws IOException {
        if (in == null)
          throw new IllegalArgumentException(
              "InputStream must not be null");
        if (codec == null)
          throw new IllegalArgumentException(
              "At least one codec must be specified");
        for (int n = codecs.length - 1; n >= 0; n--)
          in = codecs[n].wrap(in);
        in = codec.wrap(in);
        return in;
    }

    public static InputStream wrap(
        InputStream in, 
        String ce) 
          throws IOException {
        if (in == null)
          throw new IllegalArgumentException(
              "InputStream must not be null");
        String[] encodings = CharUtils.splitAndTrim(ce);
        if (encodings.length == 0) 
          throw new IllegalArgumentException(
            "At least one codec must be specified");
        for (int n = encodings.length - 1; n >= 0; n--) {
            CompressionCodec encoding = 
              getCodec(encodings[n]);
            if (encoding == null) 
              throw new IllegalArgumentException(
                "Invalid Compression Codec");
            in = encoding.wrap(in);
        }
        return in;
    }

    public static String describe(
        CompressionCodec codec, 
        CompressionCodec... codecs) {
        if (codec == null)
          throw new IllegalArgumentException(
            "At least one codec must be specified");
        StringBuilder buf = new StringBuilder("\"");
        buf.append(codec.name().toLowerCase());
        for (int n = codecs.length - 1; n >= 0; n--)
          buf.append(',')
             .append(codecs[n].name().toLowerCase());
        buf.append('"');
        return buf.toString();
    }
}
