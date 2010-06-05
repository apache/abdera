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
package org.apache.abdera.i18n.text.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public final class InputStreamDataSource implements DataSource {

    public static final String DEFAULT_TYPE = "application/octet-stream";

    private final InputStream in;
    private final String ctype;

    public InputStreamDataSource(InputStream in) {
        this(in, null);
    }

    public InputStreamDataSource(InputStream in, String ctype) {
        this.in = in;
        this.ctype = (ctype != null) ? ctype : DEFAULT_TYPE;
    }

    public String getContentType() {
        return ctype;
    }

    public String getName() {
        return null;
    }

    public InputStream getInputStream() throws IOException {
        return in;
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }

}
