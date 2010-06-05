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
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.zip.DeflaterOutputStream;

import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterOptions;

@SuppressWarnings("unchecked")
public abstract class AbstractWriter implements Writer {

    protected WriterOptions options;

    public WriterOptions getDefaultWriterOptions() {
        if (options == null)
            options = initDefaultWriterOptions();

        // Make a copy of the options, so that changes to it don't result in
        // changes to the Parser's defaults. Also, this allows us to remain
        // thread safe without having to make ParseOptions implementations
        // synchronized.

        try {
            return (WriterOptions)options.clone();
        } catch (CloneNotSupportedException cnse) {
            // This shouldn't actually happen
            throw new RuntimeException(cnse);
        }
    }

    protected abstract WriterOptions initDefaultWriterOptions();

    public synchronized Writer setDefaultWriterOptions(WriterOptions options) {
        // Ok, we need to make a defensive copy of the options, since otherwise
        // the caller still has access to the object, which means our access to
        // it isn't certain to be thread safe.

        try {
            this.options = (options != null) ? (WriterOptions)options.clone() : initDefaultWriterOptions();
        } catch (CloneNotSupportedException cnse) {
            // This shouldn't actually happen
            throw new RuntimeException(cnse);
        }
        return this;
    }

    public Object write(Base base) throws IOException {
        return write(base, getDefaultWriterOptions());
    }

    public void writeTo(Base base, OutputStream out) throws IOException {
        writeTo(base, out, getDefaultWriterOptions());
    }

    public void writeTo(Base base, java.io.Writer out) throws IOException {
        writeTo(base, out, getDefaultWriterOptions());
    }

    protected OutputStream getCompressedOutputStream(OutputStream out, WriterOptions options) throws IOException {
        if (options.getCompressionCodecs() != null) {
            out = CompressionUtil.getEncodedOutputStream(out, options.getCompressionCodecs());
        }
        return out;
    }

    protected void finishCompressedOutputStream(OutputStream out, WriterOptions options) throws IOException {
        if (options.getCompressionCodecs() != null) {
            ((DeflaterOutputStream)out).finish();
        }
    }

    public void writeTo(Base base, WritableByteChannel out, WriterOptions options) throws IOException {
        String charset = options.getCharset();
        if (charset == null) {
            Document doc = null;
            if (base instanceof Document)
                doc = (Document)base;
            else if (base instanceof Element) {
                doc = ((Element)base).getDocument();
            }
            charset = doc != null ? doc.getCharset() : null;
        }
        writeTo(base, Channels.newWriter(out, charset != null ? charset : "utf-8"), options);
    }

    public void writeTo(Base base, WritableByteChannel out) throws IOException {
        writeTo(base, out, getDefaultWriterOptions());
    }

}
