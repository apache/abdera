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
package org.apache.abdera.protocol.server.filters;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.i18n.text.io.CompressionUtil.CompressionCodec;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextWrapper;
import org.apache.abdera.writer.Writer;

/**
 * Abdera Filter implementation that selectively applies compression to the response payload
 */
public class CompressionFilter implements Filter {

    public ResponseContext filter(RequestContext request, FilterChain chain) {
        String encoding = request.getHeader("Accept-Encoding");
        String[] encodings = encoding != null ? ProviderHelper.orderByQ(encoding) : new String[0];
        for (String enc : encodings) {
            try {
                CompressionCodec codec = CompressionCodec.valueOf(enc.toUpperCase());
                return new CompressingResponseContextWrapper(chain.next(request), codec);
            } catch (Exception e) {
            }
        }
        return chain.next(request);
    }

    /**
     * A HttpServletResponseWrapper implementation that applies GZip or Deflate compression to response output.
     */
    public static class CompressingResponseContextWrapper extends ResponseContextWrapper {

        private final CompressionCodec codec;

        public CompressingResponseContextWrapper(ResponseContext response, CompressionCodec codec) {
            super(response);
            this.codec = codec;
        }

        private OutputStream wrap(OutputStream out) {
            return new CompressingOutputStream(codec, out);
        }

        public void writeTo(OutputStream out, Writer writer) throws IOException {
            out = wrap(out);
            super.writeTo(out, writer);
            out.flush();
        }

        public void writeTo(OutputStream out) throws IOException {
            out = wrap(out);
            super.writeTo(out);
            out.flush();
        }
    }

    public static class CompressingOutputStream extends FilterOutputStream {

        public CompressingOutputStream(CompressionCodec codec, OutputStream out) {
            super(initStream(codec, out));
        }

        public CompressingOutputStream(DeflaterOutputStream dout) {
            super(dout);
        }

        private static OutputStream initStream(CompressionCodec codec, OutputStream out) {
            try {
                return CompressionUtil.getEncodedOutputStream(out, codec);
            } catch (Exception e) {
                return out;
            }
        }

    }

}
