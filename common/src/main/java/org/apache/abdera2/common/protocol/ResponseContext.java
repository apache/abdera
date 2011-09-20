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
package org.apache.abdera2.common.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.abdera2.common.text.CharUtils.Profile;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.WebLink;

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
    <B extends ResponseContext>B setBinary(boolean binary);

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
     * Remove the specified header from the response
     */
    <B extends ResponseContext>B removeHeader(String name);

    /**
     * Set an RFC 2047 encoded header in the response
     */
    <B extends ResponseContext>B setEncodedHeader(String name, String charset, String value);

    /**
     * Set an RFC 2047 encoded header in the response
     */
    <B extends ResponseContext>B setEncodedHeader(String name, String charset, String... vals);

    /**
     * Set a pct-encoded header in the response
     */
    <B extends ResponseContext>B setEscapedHeader(String name, Profile profile, String value);

    /**
     * Set the value of a header in the response
     */
    <B extends ResponseContext>B setHeader(String name, Object value);

    /**
     * Set the value of a header in the response
     */
    <B extends ResponseContext>B setHeader(String name, Object... vals);

    /**
     * Add an RFC 2047 encoded header in the response
     */
    <B extends ResponseContext>B addEncodedHeader(String name, String charset, String value);

    /**
     * Add an RFC 2047 encoded header in the response
     */
    <B extends ResponseContext>B addEncodedHeaders(String name, String charset, String... vals);

    /**
     * Add a header to the response
     */
    <B extends ResponseContext>B addHeader(String name, Object value);

    /**
     * Add a header to the response
     */
    <B extends ResponseContext>B addHeaders(String name, Object... vals);

    /**
     * Set the value of the Age header
     */
    <B extends ResponseContext>B setAge(long age);

    /**
     * Set the value of the Content-Language header
     */
    <B extends ResponseContext>B setContentLanguage(String language);

    /**
     * Set the value of the Content-Length header
     */
    <B extends ResponseContext>B setContentLength(long length);

    /**
     * Set the value of the Content-Location header
     */
    <B extends ResponseContext>B setContentLocation(String uri);

    /**
     * Set the value of the Slug header
     */
    <B extends ResponseContext>B setSlug(String slug);

    /**
     * Set the value of the Content-Type header
     */
    <B extends ResponseContext>B setContentType(String type);

    /**
     * Set the value of the Content-Type header
     */
    <B extends ResponseContext>B setContentType(String type, String charset);

    /**
     * Set the value of the ETag header
     */
    <B extends ResponseContext>B setEntityTag(String etag);

    /**
     * Set the value of the ETag header
     */
    <B extends ResponseContext>B setEntityTag(EntityTag etag);

    /**
     * Set the value of the Expires header
     */
    <B extends ResponseContext>B setExpires(Date date);

    /**
     * Set the value of the Last-Modified header
     */
    <B extends ResponseContext>B setLastModified(Date date);

    /**
     * Set the value of the Location header
     */
    <B extends ResponseContext>B setLocation(String uri);

    /**
     * Set the response status code
     */
    <B extends ResponseContext>B setStatus(int status);

    /**
     * Set the response status text
     */
    <B extends ResponseContext>B setStatusText(String text);

    /**
     * Specify the HTTP methods allowed
     */
    <B extends ResponseContext>B setAllow(String method);

    /**
     * Specify the HTTP methods allowed
     */
    <B extends ResponseContext>B setAllow(String... methods);
    
    /**
     * Specify the CacheControl header
     */
    <B extends ResponseContext>B setCacheControl(CacheControl cc);
    
    /**
     * Specify the CacheControl header
     */
    <B extends ResponseContext>B setCacheControl(String cc);

    <B extends ResponseContext>B setWebLinks(WebLink link, WebLink... links);
    
    <B extends ResponseContext>B setPrefer(Preference pref, Preference... prefs);
    
    <B extends ResponseContext>B setPreferApplied(Preference pref, Preference... prefs);
}
