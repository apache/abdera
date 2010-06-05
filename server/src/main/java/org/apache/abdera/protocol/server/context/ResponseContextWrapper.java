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
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.Writer;

public class ResponseContextWrapper implements ResponseContext {

    protected final ResponseContext response;

    public ResponseContextWrapper(ResponseContext response) {
        this.response = response;
    }

    public ResponseContext addEncodedHeader(String name, String charset, String value) {
        response.addEncodedHeader(name, charset, value);
        return this;
    }

    public ResponseContext addEncodedHeaders(String name, String charset, String... vals) {
        response.addEncodedHeaders(name, charset, vals);
        return this;
    }

    public ResponseContext addHeader(String name, Object value) {
        response.addHeader(name, value);
        return this;
    }

    public ResponseContext addHeaders(String name, Object... vals) {
        response.addHeaders(name, vals);
        return this;
    }

    public boolean hasEntity() {
        return response.hasEntity();
    }

    public ResponseContext removeHeader(String name) {
        response.removeHeader(name);
        return this;
    }

    public ResponseContext setAge(long age) {
        response.setAge(age);
        return this;
    }

    public ResponseContext setAllow(String method) {
        response.setAllow(method);
        return this;
    }

    public ResponseContext setAllow(String... methods) {
        response.setAllow(methods);
        return this;
    }

    public ResponseContext setContentLanguage(String language) {
        response.setContentLanguage(language);
        return this;
    }

    public ResponseContext setContentLength(long length) {
        response.setContentLength(length);
        return this;
    }

    public ResponseContext setContentLocation(String uri) {
        response.setContentLocation(uri);
        return this;
    }

    public ResponseContext setContentType(String type) {
        response.setContentType(type);
        return this;
    }

    public ResponseContext setContentType(String type, String charset) {
        response.setContentType(type, charset);
        return this;
    }

    public ResponseContext setEncodedHeader(String name, String charset, String value) {
        response.setEncodedHeader(name, charset, value);
        return this;
    }

    public ResponseContext setEncodedHeader(String name, String charset, String... vals) {
        response.setEncodedHeader(name, charset, vals);
        return this;
    }

    public ResponseContext setEntityTag(String etag) {
        response.setEntityTag(etag);
        return this;
    }

    public ResponseContext setEntityTag(EntityTag etag) {
        response.setEntityTag(etag);
        return this;
    }

    public ResponseContext setEscapedHeader(String name, Profile profile, String value) {
        response.setEscapedHeader(name, profile, value);
        return this;
    }

    public ResponseContext setExpires(Date date) {
        response.setExpires(date);
        return this;
    }

    public ResponseContext setHeader(String name, Object value) {
        response.setHeader(name, value);
        return this;
    }

    public ResponseContext setHeader(String name, Object... vals) {
        response.setHeader(name, vals);
        return this;
    }

    public ResponseContext setLastModified(Date date) {
        response.setLastModified(date);
        return this;
    }

    public ResponseContext setLocation(String uri) {
        response.setLocation(uri);
        return this;
    }

    public ResponseContext setSlug(String slug) {
        response.setSlug(slug);
        return this;
    }

    public ResponseContext setStatus(int status) {
        response.setStatus(status);
        return this;
    }

    public ResponseContext setStatusText(String text) {
        response.setStatusText(text);
        return this;
    }

    public ResponseContext setWriter(Writer writer) {
        response.setWriter(writer);
        return this;
    }

    public void writeTo(OutputStream out) throws IOException {
        response.writeTo(out);
    }

    public void writeTo(java.io.Writer javaWriter) throws IOException {
        response.writeTo(javaWriter);
    }

    public void writeTo(OutputStream out, Writer writer) throws IOException {
        response.writeTo(out, writer);
    }

    public void writeTo(java.io.Writer javaWriter, Writer abderaWriter) throws IOException {
        response.writeTo(javaWriter, abderaWriter);
    }

    public long getAge() {
        return response.getAge();
    }

    public String getAllow() {
        return response.getAllow();
    }

    public long getContentLength() {
        return response.getContentLength();
    }

    public EntityTag getEntityTag() {
        return response.getEntityTag();
    }

    public Date getExpires() {
        return response.getExpires();
    }

    public Date getLastModified() {
        return response.getLastModified();
    }

    public IRI getLocation() {
        return response.getLocation();
    }

    public String[] getNoCacheHeaders() {
        return response.getNoCacheHeaders();
    }

    public String[] getPrivateHeaders() {
        return response.getPrivateHeaders();
    }

    public long getSMaxAge() {
        return response.getSMaxAge();
    }

    public int getStatus() {
        return response.getStatus();
    }

    public String getStatusText() {
        return response.getStatusText();
    }

    public ResponseType getType() {
        return response.getType();
    }

    public boolean isMustRevalidate() {
        return response.isMustRevalidate();
    }

    public boolean isPrivate() {
        return response.isPrivate();
    }

    public boolean isProxyRevalidate() {
        return response.isProxyRevalidate();
    }

    public boolean isPublic() {
        return response.isPublic();
    }

    public String getCacheControl() {
        return response.getCacheControl();
    }

    public String getContentLanguage() {
        return response.getContentLanguage();
    }

    public IRI getContentLocation() {
        return response.getContentLocation();
    }

    public MimeType getContentType() {
        return response.getContentType();
    }

    public Date getDateHeader(String name) {
        return response.getDateHeader(name);
    }

    public String getDecodedHeader(String name) {
        return response.getDecodedHeader(name);
    }

    public String[] getDecodedHeaders(String name) {
        return response.getDecodedHeaders(name);
    }

    public String getHeader(String name) {
        return response.getHeader(name);
    }

    public String[] getHeaderNames() {
        return response.getHeaderNames();
    }

    public Object[] getHeaders(String name) {
        return response.getHeaders(name);
    }

    public long getMaxAge() {
        return response.getMaxAge();
    }

    public String getSlug() {
        return response.getSlug();
    }

    public boolean isNoCache() {
        return response.isNoCache();
    }

    public boolean isNoStore() {
        return response.isNoStore();
    }

    public boolean isNoTransform() {
        return response.isNoTransform();
    }

    public boolean isBinary() {
        return response.isBinary();
    }

    public ResponseContext setBinary(boolean binary) {
        response.setBinary(true);
        return this;
    }

}
