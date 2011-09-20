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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;


import org.apache.abdera2.common.Localizer;
import org.apache.abdera2.common.text.Codec;
import org.apache.abdera2.common.text.UrlEncoding;
import org.apache.abdera2.common.text.CharUtils.Profile;
import org.apache.abdera2.common.http.CacheControl;
import org.apache.abdera2.common.http.EntityTag;
import org.apache.abdera2.common.http.Preference;
import org.apache.abdera2.common.http.WebLink;

@SuppressWarnings("unchecked")
public abstract class AbstractResponseContext extends AbstractResponse implements ResponseContext {

    protected static final String[] EMPTY = new String[0];

    protected int status = 0;
    protected String status_text = null;
    protected boolean binary = false;

    protected Map<String, Iterable<Object>> headers = null;

    public <T extends ResponseContext>T setBinary(boolean binary) {
        this.binary = binary;
        return (T)this;
    }

    public boolean isBinary() {
        return binary;
    }

    public <T extends ResponseContext>T setCacheControl(CacheControl cc) {
      return setCacheControl(cc.toString());
    }
    
    public <T extends ResponseContext>T setCacheControl(String cc) {
      return this.setHeader("Cache-Control", cc);
    }
    
    public <T extends ResponseContext>T removeHeader(String name) {
        getHeaders().remove(name);
        return (T)this;
    }

    public <T extends ResponseContext>T setEncodedHeader(String name, String charset, String value) {
        return setHeader(name, Codec.encode(value, charset));
    }

    public <T extends ResponseContext>T setEncodedHeader(String name, String charset, String... vals) {
        Object[] evals = new Object[vals.length];
        for (int n = 0; n < vals.length; n++) {
            evals[n] = Codec.encode(vals[n], charset);
        }
        return setHeader(name, evals);
    }

    public <T extends ResponseContext>T setEscapedHeader(String name, Profile profile, String value) {
        return setHeader(name, UrlEncoding.encode(value, profile));
    }

    public <T extends ResponseContext>T setHeader(String name, Object value) {
        return setHeader(name, new Object[] {value});
    }

    public <T extends ResponseContext>T setHeader(String name, Object... vals) {
        Map<String, Iterable<Object>> headers = getHeaders();
        Set<Object> values = new HashSet<Object>();
        for (Object value : vals)
            values.add(value);
        headers.put(name, values);
        return (T)this;
    }

    public <T extends ResponseContext>T addEncodedHeader(String name, String charset, String value) {
        return addHeader(name, Codec.encode(value, charset));
    }

    public <T extends ResponseContext>T addEncodedHeaders(String name, String charset, String... vals) {
        for (String value : vals) {
            addHeader(name, Codec.encode(value, charset));
        }
        return (T)this;
    }

    public <T extends ResponseContext>T addHeader(String name, Object value) {
        return addHeaders(name, new Object[] {value});
    }

    public <T extends ResponseContext>T addHeaders(String name, Object... vals) {
        Map<String, Iterable<Object>> headers = getHeaders();
        Iterable<Object> values = headers.get(name);
        Set<Object> l = null;
        if (values == null)
            l = new HashSet<Object>();
        else
            l = (Set<Object>)values;
        for (Object value : vals) {
            l.add(value);
        }
        headers.put(name, l);
        return (T)this;
    }

    public Map<String, Iterable<Object>> getHeaders() {
        if (headers == null)
            headers = new HashMap<String, Iterable<Object>>();
        return headers;
    }

    public Date getDateHeader(String name) {
        Map<String, Iterable<Object>> headers = getHeaders();
        Iterable<Object> values = headers.get(name);
        if (values != null) {
            for (Object value : values) {
                if (value instanceof Date)
                    return (Date)value;
            }
        }
        return null;
    }

    public String getHeader(String name) {
        Map<String, Iterable<Object>> headers = getHeaders();
        Iterable<Object> values = headers.get(name);
        if (values != null)
          for (Object val : values)
            return val.toString();
        return null;
    }

    public Iterable<Object> getHeaders(String name) {
        Map<String, Iterable<Object>> headers = getHeaders();
        return headers.get(name);
    }

    public Iterable<String> getHeaderNames() {
        Map<String, Iterable<Object>> headers = getHeaders();
        return headers.keySet();
    }

    public <T extends ResponseContext>T setAge(long age) {
        return (T)(age == -1 ? removeHeader("Age") : setHeader("Age", String.valueOf(age)));
    }

    public <T extends ResponseContext>T setContentLanguage(String language) {
        return (T)(language == null ? removeHeader("Content-Language") : setHeader("Content-Language", language));
    }

    public <T extends ResponseContext>T setContentLength(long length) {
        return (T)(length == -1 ? removeHeader("Content-Length") : setHeader("Content-Length", String.valueOf(length)));
    }

    public <T extends ResponseContext>T setContentLocation(String uri) {
        return (T)(uri == null ? removeHeader("Content-Location") : setHeader("Content-Location", uri));
    }

    public <T extends ResponseContext>T setSlug(String slug) {
        if (slug == null) {
            return removeHeader("Slug");
        }
        if (slug.indexOf((char)10) > -1 || slug.indexOf((char)13) > -1)
            throw new IllegalArgumentException(Localizer.get("SLUG.BAD.CHARACTERS"));
        return setEscapedHeader("Slug", Profile.PATHNODELIMS, slug);
    }

    public <T extends ResponseContext>T setContentType(String type) {
        return setContentType(type, null);
    }

    public <T extends ResponseContext>T setContentType(String type, String charset) {
        if (type == null) {
            return removeHeader("Content-Type");
        }
        try {
            MimeType mimeType = new MimeType(type);
            if (charset != null)
                mimeType.setParameter("charset", charset);
            return setHeader("Content-Type", mimeType.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends ResponseContext>T setEntityTag(String etag) {
        return (T)(etag != null ? setEntityTag(new EntityTag(etag)) : removeHeader("ETag"));
    }

    public <T extends ResponseContext>T setEntityTag(EntityTag etag) {
        return (T)(etag == null ? removeHeader("ETag") : setHeader("ETag", etag.toString()));
    }

    public <T extends ResponseContext>T setExpires(Date date) {
        return (T)(date == null ? removeHeader("Expires") : setHeader("Expires", date));
    }

    public <T extends ResponseContext>T setLastModified(Date date) {
        return (T)(date == null ? removeHeader("Last-Modified") : setHeader("Last-Modified", date));
    }

    public <T extends ResponseContext>T setLocation(String uri) {
        return (T)(uri == null ? removeHeader("Location") : setHeader("Location", uri));
    }

    public int getStatus() {
        return status;
    }

    public <T extends ResponseContext>T setStatus(int status) {
        this.status = status;
        return (T)this;
    }

    public String getStatusText() {
        return status_text;
    }

    public <T extends ResponseContext>T setStatusText(String text) {
        this.status_text = text;
        return (T)this;
    }

    public <T extends ResponseContext>T setAllow(String method) {
        return setHeader("Allow", method);
    }

    public <T extends ResponseContext>T setAllow(String... methods) {
        StringBuilder buf = new StringBuilder();
        for (String method : methods) {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(method);
        }
        return setAllow(buf.toString());
    }

    public <T extends ResponseContext>T setWebLinks(WebLink link, WebLink... links) {
      return setHeader("Link", WebLink.toString(link,links));
    }
    
    public <B extends ResponseContext>B setPrefer(Preference pref, Preference... prefs) {
      return setHeader("Prefer", Preference.toString(pref,prefs));
    }
    
    public <B extends ResponseContext>B setPreferApplied(Preference pref, Preference... prefs) {
      return setHeader("Preference-Applied", Preference.toString(pref,prefs));
    }
}
