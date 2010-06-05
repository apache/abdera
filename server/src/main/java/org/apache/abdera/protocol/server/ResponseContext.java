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
package org.apache.abdera.protocol.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.Writer;

/**
 * The ResponseContext encapsulates a server response
 */
public interface ResponseContext extends Response {

    /**
     * True if the response contains a binary entity as opposed to a character based entity. Default is false. If true,
     * the AbderaServlet will pass in the OutputStream for writing out, if false, the AbderaServlet will pass in the
     * Writer.
     */
    boolean isBinary();

    /**
     * True if the response contains a binary entity as opposed to a character based entity. Default is false. If true,
     * the AbderaServlet will pass in the OutputStream for writing out, if false, the AbderaServlet will pass in the
     * Writer.
     */
    ResponseContext setBinary(boolean binary);

    /**
     * True if the response contains an entity
     */
    boolean hasEntity();

    /**
     * Write the response out to the specified OutputStream
     */
    void writeTo(OutputStream out) throws IOException;

    /**
     * Write the response out to the specified Writer
     */
    void writeTo(java.io.Writer javaWriter) throws IOException;

    /**
     * Write the response out to the specified OutputStream
     */
    void writeTo(OutputStream out, Writer writer) throws IOException;

    /**
     * Write the response out to the specified Writer
     */
    void writeTo(java.io.Writer javaWriter, Writer abderaWriter) throws IOException;

    /**
     * Set the Abdera Writer for this response. This can be used to customize the serialization of the response
     */
    ResponseContext setWriter(Writer writer);

    /**
     * Remove the specified header from the response
     */
    ResponseContext removeHeader(String name);

    /**
     * Set an RFC 2047 encoded header in the response
     */
    ResponseContext setEncodedHeader(String name, String charset, String value);

    /**
     * Set an RFC 2047 encoded header in the response
     */
    ResponseContext setEncodedHeader(String name, String charset, String... vals);

    /**
     * Set a pct-encoded header in the response
     */
    ResponseContext setEscapedHeader(String name, Profile profile, String value);

    /**
     * Set the value of a header in the response
     */
    ResponseContext setHeader(String name, Object value);

    /**
     * Set the value of a header in the response
     */
    ResponseContext setHeader(String name, Object... vals);

    /**
     * Add an RFC 2047 encoded header in the response
     */
    ResponseContext addEncodedHeader(String name, String charset, String value);

    /**
     * Add an RFC 2047 encoded header in the response
     */
    ResponseContext addEncodedHeaders(String name, String charset, String... vals);

    /**
     * Add a header to the response
     */
    ResponseContext addHeader(String name, Object value);

    /**
     * Add a header to the response
     */
    ResponseContext addHeaders(String name, Object... vals);

    /**
     * Set the value of the Age header
     */
    ResponseContext setAge(long age);

    /**
     * Set the value of the Content-Language header
     */
    ResponseContext setContentLanguage(String language);

    /**
     * Set the value of the Content-Length header
     */
    ResponseContext setContentLength(long length);

    /**
     * Set the value of the Content-Location header
     */
    ResponseContext setContentLocation(String uri);

    /**
     * Set the value of the Slug header
     */
    ResponseContext setSlug(String slug);

    /**
     * Set the value of the Content-Type header
     */
    ResponseContext setContentType(String type);

    /**
     * Set the value of the Content-Type header
     */
    ResponseContext setContentType(String type, String charset);

    /**
     * Set the value of the ETag header
     */
    ResponseContext setEntityTag(String etag);

    /**
     * Set the value of the ETag header
     */
    ResponseContext setEntityTag(EntityTag etag);

    /**
     * Set the value of the Expires header
     */
    ResponseContext setExpires(Date date);

    /**
     * Set the value of the Last-Modified header
     */
    ResponseContext setLastModified(Date date);

    /**
     * Set the value of the Location header
     */
    ResponseContext setLocation(String uri);

    /**
     * Set the response status code
     */
    ResponseContext setStatus(int status);

    /**
     * Set the response status text
     */
    ResponseContext setStatusText(String text);

    /**
     * Specify the HTTP methods allowed
     */
    ResponseContext setAllow(String method);

    /**
     * Specify the HTTP methods allowed
     */
    ResponseContext setAllow(String... methods);

}
