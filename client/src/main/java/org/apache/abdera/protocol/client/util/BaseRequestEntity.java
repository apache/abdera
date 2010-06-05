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

import org.apache.abdera.model.Base;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * Required for the Apache Commons HTTP AbderaClient.
 */
public class BaseRequestEntity implements RequestEntity {

    private final Base base;
    private byte[] buf = null;
    private boolean use_chunked = true;

    public BaseRequestEntity(Base base) {
        this.base = base;
    }

    public BaseRequestEntity(Base base, boolean use_chunked) {
        this(base);
        this.use_chunked = use_chunked;
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(OutputStream out) throws IOException {
        if (use_chunked)
            base.writeTo(out);
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
                    base.writeTo(out);
                    buf = out.toByteArray();
                } catch (Exception e) {
                }
            }
            return buf.length;
        }
    }

    public String getContentType() {
        return MimeTypeHelper.getMimeType(base);
    }

}
