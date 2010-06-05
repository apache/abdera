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
package org.apache.abdera.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.abdera.model.Base;

/**
 * Writers are used to serialize Abdera objects
 */
public interface Writer {

    /**
     * Serialized the given Abdera Base to the given outputstream
     */
    void writeTo(Base base, OutputStream out) throws IOException;

    /**
     * Serialized the given Abdera Base to the given writer
     */
    void writeTo(Base base, java.io.Writer out) throws IOException;

    /**
     * Return the serialized form of the Abdera Base
     */
    Object write(Base base) throws IOException;

    /**
     * Serialized the given Abdera Base to the given outputstream
     */
    void writeTo(Base base, OutputStream out, WriterOptions options) throws IOException;

    /**
     * Serialized the given Abdera Base to the given writer
     */
    void writeTo(Base base, java.io.Writer out, WriterOptions options) throws IOException;

    /**
     * Return the serialized form of the Abdera Base
     */
    Object write(Base base, WriterOptions options) throws IOException;

    void writeTo(Base base, WritableByteChannel out) throws IOException;

    void writeTo(Base base, WritableByteChannel out, WriterOptions options) throws IOException;

    WriterOptions getDefaultWriterOptions();

    Writer setDefaultWriterOptions(WriterOptions options);
}
