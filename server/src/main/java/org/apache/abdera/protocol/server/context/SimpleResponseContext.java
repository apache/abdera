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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * A simple base implementation of AbstractResponseContext that makes it a bit easier to create custom ResponseContext
 * implementations e.g. new SimpleResponseContext() { public boolean hasEntity() { return true; } public void
 * writeEntity(Writer writer) { ... } }
 */
public abstract class SimpleResponseContext extends AbstractResponseContext {

    protected String encoding = "UTF-8";

    protected SimpleResponseContext() {
        this(null);
    }

    protected SimpleResponseContext(String encoding) {
        if (encoding != null) {
            this.encoding = encoding;
        }
    }

    protected SimpleResponseContext setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    protected String getEncoding() {
        return this.encoding;
    }

    public void writeTo(OutputStream out) throws IOException {
        if (hasEntity()) {
            OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
            writeTo(writer);
            writer.flush();
        }
    }

    public void writeTo(Writer writer) throws IOException {
        if (hasEntity())
            writeEntity(writer);
    }

    protected abstract void writeEntity(Writer writer) throws IOException;

    public void writeTo(OutputStream out, org.apache.abdera.writer.Writer writer) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeTo(Writer javaWriter, org.apache.abdera.writer.Writer abderaWriter) throws IOException {
        throw new UnsupportedOperationException();
    }

}
