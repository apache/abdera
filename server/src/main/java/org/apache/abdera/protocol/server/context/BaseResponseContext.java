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
package org.apache.abdera.protocol.server.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.activation.MimeType;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.Writer;

public class BaseResponseContext<T extends Base> extends AbstractResponseContext {

    private final T base;
    private final boolean chunked;

    public BaseResponseContext(T base) {
        this(base, true);
    }

    public BaseResponseContext(T base, boolean chunked) {
        this.base = base;
        setStatus(200);
        setStatusText("OK");
        this.chunked = chunked;
        try {
            MimeType type = getContentType();
            String charset = type.getParameter("charset");
            if (charset == null)
                charset = getCharsetFromBase(base);
            if (charset == null)
                charset = "UTF-8";
            type.setParameter("charset", charset);
            setContentType(type.toString());
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    private String getCharsetFromBase(Base base) {
        if (base == null)
            return null;
        if (base instanceof Document) {
            return ((Document)base).getCharset();
        } else if (base instanceof Element) {
            return getCharsetFromBase(((Element)base).getDocument());
        }
        return null;
    }

    public T getBase() {
        return base;
    }

    public boolean hasEntity() {
        return (base != null);
    }

    public void writeTo(java.io.Writer javaWriter) throws IOException {
        if (hasEntity()) {
            if (writer == null)
                base.writeTo(javaWriter);
            else
                writeTo(javaWriter, writer);
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        if (hasEntity()) {
            if (writer == null)
                base.writeTo(out);
            else
                writeTo(out, writer);
        }
    }

    @Override
    public MimeType getContentType() {
        try {
            MimeType t = super.getContentType();
            if (t == null) {
                String type = MimeTypeHelper.getMimeType(base);
                if (type != null)
                    t = new MimeType(type);
            }
            return t;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    @Override
    public long getContentLength() {
        long len = super.getContentLength();
        if (hasEntity() && len == -1 && !chunked) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                base.writeTo(out);
                len = out.size();
                super.setContentLength(len);
            } catch (Exception e) {
            }
        }
        return len;
    }

    public void writeTo(OutputStream out, Writer writer) throws IOException {
        writer.writeTo(base, out);
    }

    public void writeTo(java.io.Writer javaWriter, Writer abderaWriter) throws IOException {
        abderaWriter.writeTo(base, javaWriter);
    }

}
