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
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.activation.MimeType;
import javax.security.auth.Subject;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.WebLink;

@SuppressWarnings("unchecked")
public class BaseRequestContextWrapper
  implements RequestContext{

    protected final RequestContext request;

    public BaseRequestContextWrapper(RequestContext request) {
        this.request = request;
    }

    public Object getAttribute(Scope scope, String name) {
        return request.getAttribute(scope, name);
    }

    public Iterable<String> getAttributeNames(Scope scope) {
        return request.getAttributeNames(scope);
    }

    public IRI getBaseUri() {
        return request.getBaseUri();
    }

    public String getContextPath() {
        return request.getContextPath();
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

    public Iterable<String> getParameterNames() {
        return request.getParameterNames();
    }

    public List<String> getParameters(String name) {
        return request.getParameters(name);
    }

    public Locale getPreferredLocale() {
        return request.getPreferredLocale();
    }

    public Iterable<Locale> getPreferredLocales() {
        return request.getPreferredLocales();
    }

    public Principal getPrincipal() {
        return request.getPrincipal();
    }

    public <T>T getProperty(Property property) {
        return request.getProperty(property);
    }

    public <P extends Provider>P getProvider() {
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

    public <T extends RequestContext>T setAttribute(Scope scope, String name, Object value) {
        request.setAttribute(scope, name, value);
        return (T)this;
    }

    public <T extends RequestContext>T setAttribute(String name, Object value) {
        request.setAttribute(name, value);
        return (T)this;
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

    public Iterable<Authentication> getAuthentication() {
        return request.getAuthentication();
    }

    public Iterable<EntityTag> getIfMatch() {
        return request.getIfMatch();
    }

    public Date getIfModifiedSince() {
        return request.getIfModifiedSince();
    }

    public Iterable<EntityTag> getIfNoneMatch() {
        return request.getIfNoneMatch();
    }

    public Date getIfUnmodifiedSince() {
        return request.getIfUnmodifiedSince();
    }

    public CacheControl getCacheControl() {
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

    public Iterable<String> getDecodedHeaders(String name) {
        return request.getDecodedHeaders(name);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public Iterable<String> getHeaderNames() {
        return request.getHeaderNames();
    }

    public Iterable<Object> getHeaders(String name) {
        return request.getHeaders(name);
    }

    public String getSlug() {
        return request.getSlug();
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

    public Iterator<Property> iterator() {
      return request.iterator();
    }

    public Iterable<WebLink> getWebLinks() {
      return request.getWebLinks();
    }

    public Iterable<Preference> getPrefer() {
      return request.getPrefer();
    }
    
    public Iterable<Preference> getPreferApplied() {
      return request.getPreferApplied();
    }

}
