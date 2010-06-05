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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpMethod;

public final class AutoReleasingInputStream extends FilterInputStream {

    private final HttpMethod method;

    public AutoReleasingInputStream(HttpMethod method, InputStream in) {
        super(in);
        this.method = method;
    }

    @Override
    public int read() throws IOException {
        if (this.in == null)
            return -1;
        try {
            int r = super.read();
            if (r == -1) {
                method.releaseConnection();
            }
            return r;
        } catch (IOException e) {
            if (method != null)
                method.releaseConnection();
            throw e;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.in == null)
            return -1;
        try {
            int r = super.read(b, off, len);
            if (r == -1) {
                method.releaseConnection();
            }
            return r;
        } catch (IOException e) {
            if (method != null)
                method.releaseConnection();
            throw e;
        }
    }

}
