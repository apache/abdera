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
package org.apache.abdera.protocol.client.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.EntityProvider;
import org.apache.commons.httpclient.methods.RequestEntity;

public class EntityProviderRequestEntity implements RequestEntity {

    private final Abdera abdera;
    private final EntityProvider provider;
    private byte[] buf = null;
    private boolean use_chunked = true;
    private boolean auto_indent = false;
    private String encoding = "UTF-8";

    public EntityProviderRequestEntity(Abdera abdera, EntityProvider provider, boolean use_chunked) {
        this.abdera = abdera;
        this.use_chunked = use_chunked;
        this.provider = provider;
    }

    private void write(OutputStream out) {
        provider.writeTo(abdera.newStreamWriter().setOutputStream(out, encoding).setAutoIndent(auto_indent));
    }

    public long getContentLength() {
        if (use_chunked)
            return -1; // chunk the response
        else {
            // this is ugly, but some proxies and server configurations (e.g. gdata)
            // require that requests contain the Content-Length header. The only
            // way to get that is to serialize the document into a byte array, which
            // we buffer into memory.
            if (buf == null) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    write(out);
                    buf = out.toByteArray();
                } catch (Exception e) {
                }
            }
            return buf.length;
        }
    }

    public String getContentType() {
        return provider.getContentType();
    }

    public boolean isRepeatable() {
        return provider.isRepeatable();
    }

    public void writeRequest(OutputStream out) throws IOException {
        if (use_chunked)
            write(out);
        else {
            // if we're not using chunked requests, the getContentLength method
            // has likely already been called and we want to just go ahead and
            // use the buffered output rather than reserialize
            if (buf == null)
                getContentLength(); // ensures that the content is buffered
            out.write(buf);
            out.flush();
        }
    }

    public boolean isAutoIndent() {
        return auto_indent;
    }

    public void setAutoIndent(boolean auto_indent) {
        this.auto_indent = auto_indent;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
