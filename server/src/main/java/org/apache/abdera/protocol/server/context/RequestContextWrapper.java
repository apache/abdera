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
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.activation.MimeType;
import javax.security.auth.Subject;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.util.EntityTag;

public class RequestContextWrapper implements RequestContext {

    protected final RequestContext request;

    public RequestContextWrapper(RequestContext request) {
        this.request = request;
    }

    public Abdera getAbdera() {
        return request.getAbdera();
    }

    public Object getAttribute(Scope scope, String name) {
        return request.getAttribute(scope, name);
    }

    public String[] getAttributeNames(Scope scope) {
        return request.getAttributeNames(scope);
    }

    public IRI getBaseUri() {
        return request.getBaseUri();
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public <T extends Element> Document<T> getDocument() throws ParseException, IOException {
        return request.getDocument();
    }

    public <T extends Element> Document<T> getDocument(Parser parser) throws ParseException, IOException {
        return request.getDocument(parser);
    }

    public <T extends Element> Document<T> getDocument(Parser parser, ParserOptions options) throws ParseException,
        IOException {
        return request.getDocument(parser, options);
    }

    public <T extends Element> Document<T> getDocument(ParserOptions options) throws ParseException, IOException {
        return request.getDocument(options);
    }

    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }

    public String[] getParameterNames() {
        return request.getParameterNames();
    }

    public List<String> getParameters(String name) {
        return request.getParameters(name);
    }

    public Locale getPreferredLocale() {
        return request.getPreferredLocale();
    }

    public Locale[] getPreferredLocales() {
        return request.getPreferredLocales();
    }

    public Principal getPrincipal() {
        return request.getPrincipal();
    }

    public Object getProperty(Property property) {
        return request.getProperty(property);
    }

    public Provider getProvider() {
        return request.getProvider();
    }

    public Reader getReader() throws IOException {
        return request.getReader();
    }

    public IRI getResolvedUri() {
        return request.getResolvedUri();
    }

    public Subject getSubject() {
        return request.getSubject();
    }

    public Target getTarget() {
        return request.getTarget();
    }

    public String getTargetPath() {
        return request.getTargetPath();
    }

    public IRI getUri() {
        return request.getUri();
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public RequestContext setAttribute(Scope scope, String name, Object value) {
        request.setAttribute(scope, name, value);
        return this;
    }

    public RequestContext setAttribute(String name, Object value) {
        request.setAttribute(name, value);
        return this;
    }

    public String getAccept() {
        return request.getAccept();
    }

    public String getAcceptCharset() {
        return request.getAcceptCharset();
    }

    public String getAcceptEncoding() {
        return request.getAcceptEncoding();
    }

    public String getAcceptLanguage() {
        return request.getAcceptLanguage();
    }

    public String getAuthorization() {
        return request.getAuthorization();
    }

    public EntityTag[] getIfMatch() {
        return request.getIfMatch();
    }

    public Date getIfModifiedSince() {
        return request.getIfModifiedSince();
    }

    public EntityTag[] getIfNoneMatch() {
        return request.getIfNoneMatch();
    }

    public Date getIfUnmodifiedSince() {
        return request.getIfUnmodifiedSince();
    }

    public long getMaxStale() {
        return request.getMaxStale();
    }

    public long getMinFresh() {
        return request.getMinFresh();
    }

    public boolean isOnlyIfCached() {
        return request.isOnlyIfCached();
    }

    public String getCacheControl() {
        return request.getCacheControl();
    }

    public String getContentLanguage() {
        return request.getContentLanguage();
    }

    public IRI getContentLocation() {
        return request.getContentLocation();
    }

    public MimeType getContentType() {
        return request.getContentType();
    }

    public Date getDateHeader(String name) {
        return request.getDateHeader(name);
    }

    public String getDecodedHeader(String name) {
        return request.getDecodedHeader(name);
    }

    public String[] getDecodedHeaders(String name) {
        return request.getDecodedHeaders(name);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public String[] getHeaderNames() {
        return request.getHeaderNames();
    }

    public Object[] getHeaders(String name) {
        return request.getHeaders(name);
    }

    public long getMaxAge() {
        return request.getMaxAge();
    }

    public String getSlug() {
        return request.getSlug();
    }

    public boolean isNoCache() {
        return request.isNoCache();
    }

    public boolean isNoStore() {
        return request.isNoStore();
    }

    public boolean isNoTransform() {
        return request.isNoTransform();
    }

    public String urlFor(Object key, Object param) {
        return getProvider().urlFor(this, key, param);
    }

    public String getTargetBasePath() {
        return request.getTargetBasePath();
    }

    public String absoluteUrlFor(Object key, Object param) {
        return request.getResolvedUri().resolve(urlFor(key, param)).toString();
    }

    public boolean isAtom() {
        return request.isAtom();
    }

}
