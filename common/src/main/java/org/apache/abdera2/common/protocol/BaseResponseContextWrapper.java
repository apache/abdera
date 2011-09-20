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

import javax.activation.MimeType;

import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.common.text.CharUtils.Profile;
import org.apache.abdera2.common.http.Authentication;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.ResponseType;
import org.apache.abdera2.common.http.WebLink;

@SuppressWarnings("unchecked")
public class BaseResponseContextWrapper implements ResponseContext {

    protected final ResponseContext response;

    public BaseResponseContextWrapper(ResponseContext response) {
        this.response = response;
    }

    public <T extends ResponseContext>T addEncodedHeader(String name, String charset, String value) {
        response.addEncodedHeader(name, charset, value);
        return (T)this;
    }

    public <T extends ResponseContext>T addEncodedHeaders(String name, String charset, String... vals) {
        response.addEncodedHeaders(name, charset, vals);
        return (T)this;
    }

    public <T extends ResponseContext>T addHeader(String name, Object value) {
        response.addHeader(name, value);
        return (T)this;
    }

    public <T extends ResponseContext>T addHeaders(String name, Object... vals) {
        response.addHeaders(name, vals);
        return (T)this;
    }

    public boolean hasEntity() {
        return response.hasEntity();
    }

    public <T extends ResponseContext>T removeHeader(String name) {
        response.removeHeader(name);
        return (T)this;
    }

    public <T extends ResponseContext>T setAge(long age) {
        response.setAge(age);
        return (T)this;
    }

    public <T extends ResponseContext>T setAllow(String method) {
        response.setAllow(method);
        return (T)this;
    }

    public <T extends ResponseContext>T setAllow(String... methods) {
        response.setAllow(methods);
        return (T)this;
    }

    public <T extends ResponseContext>T setContentLanguage(String language) {
        response.setContentLanguage(language);
        return (T)this;
    }

    public <T extends ResponseContext>T setContentLength(long length) {
        response.setContentLength(length);
        return (T)this;
    }

    public <T extends ResponseContext>T setContentLocation(String uri) {
        response.setContentLocation(uri);
        return (T)this;
    }

    public <T extends ResponseContext>T setContentType(String type) {
        response.setContentType(type);
        return (T)this;
    }

    public <T extends ResponseContext>T setContentType(String type, String charset) {
        response.setContentType(type, charset);
        return (T)this;
    }

    public <T extends ResponseContext>T setEncodedHeader(String name, String charset, String value) {
        response.setEncodedHeader(name, charset, value);
        return (T)this;
    }

    public <T extends ResponseContext>T setEncodedHeader(String name, String charset, String... vals) {
        response.setEncodedHeader(name, charset, vals);
        return (T)this;
    }

    public <T extends ResponseContext>T setEntityTag(String etag) {
        response.setEntityTag(etag);
        return (T)this;
    }

    public <T extends ResponseContext>T setEntityTag(EntityTag etag) {
        response.setEntityTag(etag);
        return (T)this;
    }

    public <T extends ResponseContext>T setEscapedHeader(String name, Profile profile, String value) {
        response.setEscapedHeader(name, profile, value);
        return (T)this;
    }

    public <T extends ResponseContext>T setExpires(Date date) {
        response.setExpires(date);
        return (T)this;
    }

    public <T extends ResponseContext>T setHeader(String name, Object value) {
        response.setHeader(name, value);
        return (T)this;
    }

    public <T extends ResponseContext>T setHeader(String name, Object... vals) {
        response.setHeader(name, vals);
        return (T)this;
    }

    public <T extends ResponseContext>T setLastModified(Date date) {
        response.setLastModified(date);
        return (T)this;
    }

    public <T extends ResponseContext>T setLocation(String uri) {
        response.setLocation(uri);
        return (T)this;
    }

    public <T extends ResponseContext>T setSlug(String slug) {
        response.setSlug(slug);
        return (T)this;
    }

    public <T extends ResponseContext>T setStatus(int status) {
        response.setStatus(status);
        return (T)this;
    }

    public <T extends ResponseContext>T setStatusText(String text) {
        response.setStatusText(text);
        return (T)this;
    }

    public void writeTo(OutputStream out) throws IOException {
        response.writeTo(out);
    }

    public void writeTo(java.io.Writer javaWriter) throws IOException {
        response.writeTo(javaWriter);
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

    public int getStatus() {
        return response.getStatus();
    }

    public String getStatusText() {
        return response.getStatusText();
    }

    public ResponseType getType() {
        return response.getType();
    }

    public CacheControl getCacheControl() {
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

    public Iterable<String> getDecodedHeaders(String name) {
        return response.getDecodedHeaders(name);
    }
    
    public Iterable<Authentication> getAuthentication() {
      return response.getAuthentication();
    }

    public String getHeader(String name) {
        return response.getHeader(name);
    }

    public Iterable<String> getHeaderNames() {
        return response.getHeaderNames();
    }

    public Iterable<Object> getHeaders(String name) {
        return response.getHeaders(name);
    }

    public String getSlug() {
        return response.getSlug();
    }

    public boolean isBinary() {
        return response.isBinary();
    }

    public <T extends ResponseContext>T setBinary(boolean binary) {
        response.setBinary(true);
        return (T)this;
    }

    public <T extends ResponseContext>T setCacheControl(CacheControl cc) {
      response.setCacheControl(cc);
      return (T)this;
    }

    public <T extends ResponseContext>T setCacheControl(String cc) {
      response.setCacheControl(cc);
      return (T)this;
    }

    public Iterable<WebLink> getWebLinks() {
      return response.getWebLinks();
    }

    public Iterable<Preference> getPrefer() {
      return response.getPrefer();
    }
    
    public Iterable<Preference> getPreferApplied() {
      return response.getPreferApplied();
    }

    public <T extends ResponseContext> T setWebLinks(WebLink link,
        WebLink... links) {
      response.setWebLinks(link, links);
      return (T)this;
    }

    public <T extends ResponseContext> T setPrefer(Preference pref,
        Preference... prefs) {
      response.setPrefer(pref, prefs);
      return (T)this;
    }

    public <T extends ResponseContext> T setPreferApplied(Preference pref,
        Preference... prefs) {
      response.setPreferApplied(pref, prefs);
      return (T)this;
    }

}
