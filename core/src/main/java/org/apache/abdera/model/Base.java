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
package org.apache.abdera.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.writer.WriterOptions;

/**
 * The Base interface provides the basis for the Feed Object Model API and defines the operations common to both the
 * Element and Document interfaces. Classes implementing Base MUST NOT be assumed to be thread safe. Developers wishing
 * to allow multiple threads to perform concurrent modifications to a Base MUST provide their own synchronization.
 */
public interface Base extends Cloneable {

    /**
     * Get the default WriterOptions for this object
     */
    WriterOptions getDefaultWriterOptions();

    /**
     * Serializes the model component out to the specified stream
     * 
     * @param out The target output stream
     * @param options The WriterOptions to use
     */
    void writeTo(OutputStream out, WriterOptions options) throws IOException;

    /**
     * Serializes the model component out to the specified java.io.Writer
     * 
     * @param out The target output writer
     * @param options The WriterOptions to use
     */
    void writeTo(Writer out, WriterOptions options) throws IOException;

    /**
     * Serializes the model component out to the specified stream using the given Abdera writer
     * 
     * @param writer The Abdera writer to use
     * @param out The target output stream
     */
    void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out) throws IOException;

    /**
     * Serializes the model component out to the specified java.io.Writer using the given Abdera writer
     * 
     * @param writer The Abdera writer to use
     * @param out The target output writer
     */
    void writeTo(org.apache.abdera.writer.Writer writer, Writer out) throws IOException;

    /**
     * Serializes the model component out to the specified stream using the given Abdera writer
     * 
     * @param writer The Abdera writer to use
     * @param out The target output stream
     */
    void writeTo(String writer, OutputStream out) throws IOException;

    /**
     * Serializes the model component out to the specified java.io.Writer using the given Abdera writer
     * 
     * @param writer The Abdera writer to use
     * @param out The target output writer
     */
    void writeTo(String writer, Writer out) throws IOException;

    /**
     * Serializes the model component out to the specified stream using the given abdera writer
     * 
     * @param writer The Abdera writer to use
     * @param out The target output stream
     * @param options The WriterOptions to use
     */
    void writeTo(org.apache.abdera.writer.Writer writer, OutputStream out, WriterOptions options) throws IOException;

    /**
     * Serializes the model component out to the specified java.io.Writer using the given abdera writer
     * 
     * @param writer The Abdera writer to use
     * @param out The target output writer
     * @param options The WriterOptions to use
     */
    void writeTo(org.apache.abdera.writer.Writer writer, Writer out, WriterOptions options) throws IOException;

    /**
     * Serializes the model component out to the specified stream using the given abdera writer
     * 
     * @param writer The name of the Abdera writer to use
     * @param out The target output stream
     * @param options The WriterOptions to use
     */
    void writeTo(String writer, OutputStream out, WriterOptions options) throws IOException;

    /**
     * Serializes the model component out to the specified java.io.Writer using the given abdera writer
     * 
     * @param writer The name of the Abdera writer to use
     * @param out The target output writer
     * @param options The WriterOptions to use
     */
    void writeTo(String writer, Writer out, WriterOptions options) throws IOException;

    /**
     * Serializes the model component out to the specified stream
     * 
     * @param out The java.io.OutputStream to use when serializing the Base. The charset encoding specified for the
     *            document will be used
     */
    void writeTo(OutputStream out) throws IOException;

    /**
     * Serializes the model component out to the specified writer
     * 
     * @param writer The java.io.Writer to use when serializing the Base
     */
    void writeTo(Writer writer) throws IOException;

    /**
     * Clone this Base
     */
    Object clone();

    /**
     * Get the Factory used to create this Base
     * 
     * @return The Factory used to create this object
     */
    Factory getFactory();

    /**
     * Add an XML comment to this Base
     * 
     * @param value The text value of the comment
     */
    <T extends Base> T addComment(String value);

    /**
     * Ensure that the underlying streams are fully parsed. Calling complete on an Element does not necessarily mean
     * that the underlying stream is fully consumed, only that that particular element has been completely parsed.
     */
    <T extends Base> T complete();

}
