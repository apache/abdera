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
import java.io.Writer;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.StreamWriter;

/**
 * Abstract base class for creating ResponseContext implementations that use the StreamWriter interface. Using the
 * StreamWriter to write out documents is significantly faster than using the object model but requires developers to
 * know more about proper Atom syntax.
 */
public abstract class StreamWriterResponseContext extends SimpleResponseContext {

    private final Abdera abdera;
    private final String sw;
    private boolean autoindent;

    /**
     * Create a new StreamWriterResponseContext
     * 
     * @param abdera The Abdera instance
     */
    protected StreamWriterResponseContext(Abdera abdera) {
        this(abdera, null, null);
    }

    /**
     * Create a new StreamWriterResponseContext
     * 
     * @param abdera The Abdera instance
     * @param encoding The charset encoding
     */
    protected StreamWriterResponseContext(Abdera abdera, String encoding) {
        this(abdera, encoding, null);
    }

    /**
     * Create a new StreamWriterResponseContext
     * 
     * @param abdera The Abdera instance
     * @param encoding The charset encoding
     * @param sw The name of the Named StreamWriter to use
     */
    protected StreamWriterResponseContext(Abdera abdera, String encoding, String sw) {
        super(encoding);
        this.abdera = abdera;
        this.sw = sw;
    }

    /**
     * Get the Abdera instance
     */
    protected final Abdera getAbdera() {
        return abdera;
    }

    /**
     * Create a new StreamWriter instance. If the sw property was set, the specified Named StreamWriter will be returned
     */
    protected StreamWriter newStreamWriter() {
        return sw == null ? abdera.newStreamWriter() : abdera.getWriterFactory().newStreamWriter(sw);
    }

    protected void writeEntity(Writer writer) throws IOException {
        writeTo(newStreamWriter().setWriter(writer).setAutoIndent(autoindent));
    }

    /**
     * Write to the specified StreamWriter. Subclasses of this class must implement this method.
     */
    protected abstract void writeTo(StreamWriter sw) throws IOException;

    /**
     * True to enable automatic indenting on the StreamWriter
     */
    public StreamWriterResponseContext setAutoIndent(boolean autoindent) {
        this.autoindent = autoindent;
        return this;
    }

    /**
     * True if automatic indenting is enabled on the StreamWriter
     */
    public boolean getAutoIndent() {
        return this.autoindent;
    }

    public boolean hasEntity() {
        return true;
    }

}
