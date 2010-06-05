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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;

import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.i18n.text.Rfc2047Helper;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.util.AbstractResponse;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.Writer;

public abstract class AbstractResponseContext extends AbstractResponse implements ResponseContext {

    protected static final String[] EMPTY = new String[0];

    protected int status = 0;
    protected String status_text = null;
    protected Writer writer = null;
    protected boolean binary = false;

    protected Map<String, Object[]> headers = null;

    public ResponseContext setBinary(boolean binary) {
        this.binary = binary;
        return this;
    }

    public boolean isBinary() {
        return binary;
    }

    public ResponseContext removeHeader(String name) {
        Map<String, Object[]> headers = getHeaders();
        headers.remove(name);
        return this;
    }

    public ResponseContext setEncodedHeader(String name, String charset, String value) {
        return setHeader(name, Rfc2047Helper.encode(value, charset));
    }

    public ResponseContext setEncodedHeader(String name, String charset, String... vals) {
        Object[] evals = new Object[vals.length];
        for (int n = 0; n < vals.length; n++) {
            evals[n] = Rfc2047Helper.encode(vals[n], charset);
        }
        return setHeader(name, evals);
    }

    public ResponseContext setEscapedHeader(String name, Profile profile, String value) {
        return setHeader(name, UrlEncoding.encode(value, profile.filter()));
    }

    public ResponseContext setHeader(String name, Object value) {
        return setHeader(name, new Object[] {value});
    }

    public ResponseContext setHeader(String name, Object... vals) {
        Map<String, Object[]> headers = getHeaders();
        List<Object> values = new ArrayList<Object>();
        for (Object value : vals) {
            values.add(value);
        }
        headers.put(name, values.toArray(new Object[values.size()]));
        return this;
    }

    public ResponseContext addEncodedHeader(String name, String charset, String value) {
        return addHeader(name, Rfc2047Helper.encode(value, charset));
    }

    public ResponseContext addEncodedHeaders(String name, String charset, String... vals) {
        for (String value : vals) {
            addHeader(name, Rfc2047Helper.encode(value, charset));
        }
        return this;
    }

    public ResponseContext addHeader(String name, Object value) {
        return addHeaders(name, new Object[] {value});
    }

    public ResponseContext addHeaders(String name, Object... vals) {
        Map<String, Object[]> headers = getHeaders();
        Object[] values = headers.get(name);
        List<Object> l = null;
        if (values == null) {
            l = new ArrayList<Object>();
        } else {
            l = Arrays.asList(values);
        }
        for (Object value : vals) {
            l.add(value);
        }
        headers.put(name, l.toArray(new Object[l.size()]));
        return this;
    }

    public Map<String, Object[]> getHeaders() {
        if (headers == null)
            headers = new HashMap<String, Object[]>();
        return headers;
    }

    public Date getDateHeader(String name) {
        Map<String, Object[]> headers = getHeaders();
        Object[] values = headers.get(name);
        if (values != null) {
            for (Object value : values) {
                if (value instanceof Date)
                    return (Date)value;
            }
        }
        return null;
    }

    public String getHeader(String name) {
        Map<String, Object[]> headers = getHeaders();
        Object[] values = headers.get(name);
        if (values != null && values.length > 0)
            return values[0].toString();
        return null;
    }

    public Object[] getHeaders(String name) {
        Map<String, Object[]> headers = getHeaders();
        return headers.get(name);
    }

    public String[] getHeaderNames() {
        Map<String, Object[]> headers = getHeaders();
        return headers.keySet().toArray(new String[headers.size()]);
    }

    private void append(StringBuilder buf, String value) {
        if (buf.length() > 0)
            buf.append(", ");
        buf.append(value);
    }

    public String getCacheControl() {
        StringBuilder buf = new StringBuilder();
        if (isPublic())
            append(buf, "public");
        if (isPrivate())
            append(buf, "private");
        if (private_headers != null && private_headers.length > 0) {
            buf.append("=\"");
            for (String header : private_headers) {
                append(buf, header);
            }
            buf.append("\"");
        }
        if (isNoCache())
            append(buf, "no-cache");
        if (nocache_headers != null && nocache_headers.length > 0) {
            buf.append("=\"");
            for (String header : nocache_headers) {
                append(buf, header);
            }
            buf.append("\"");
        }
        if (isNoStore())
            append(buf, "no-store");
        if (isMustRevalidate())
            append(buf, "must-revalidate");
        if (isNoTransform())
            append(buf, "no-transform");
        if (getMaxAge() != -1)
            append(buf, "max-age=" + getMaxAge());
        if (getSMaxAge() != -1)
            append(buf, "smax-age=" + getMaxAge());
        return buf.toString();
    }

    public ResponseContext setAge(long age) {
        return age == -1 ? removeHeader("Age") : setHeader("Age", String.valueOf(age));
    }

    public ResponseContext setContentLanguage(String language) {
        return language == null ? removeHeader("Content-Language") : setHeader("Content-Language", language);
    }

    public ResponseContext setContentLength(long length) {
        return length == -1 ? removeHeader("Content-Length") : setHeader("Content-Length", String.valueOf(length));
    }

    public ResponseContext setContentLocation(String uri) {
        return uri == null ? removeHeader("Content-Location") : setHeader("Content-Location", uri);
    }

    public ResponseContext setSlug(String slug) {
        if (slug == null) {
            return removeHeader("Slug");
        }
        if (slug.indexOf((char)10) > -1 || slug.indexOf((char)13) > -1)
            throw new IllegalArgumentException(Localizer.get("SLUG.BAD.CHARACTERS"));
        return setEscapedHeader("Slug", Profile.ASCIISANSCRLF, slug);
    }

    public ResponseContext setContentType(String type) {
        return setContentType(type, null);
    }

    public ResponseContext setContentType(String type, String charset) {
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

    public ResponseContext setEntityTag(String etag) {
        return etag != null ? setEntityTag(new EntityTag(etag)) : removeHeader("ETag");
    }

    public ResponseContext setEntityTag(EntityTag etag) {
        return etag == null ? removeHeader("ETag") : setHeader("ETag", etag.toString());
    }

    public ResponseContext setExpires(Date date) {
        return date == null ? removeHeader("Expires") : setHeader("Expires", date);
    }

    public ResponseContext setLastModified(Date date) {
        return date == null ? removeHeader("Last-Modified") : setHeader("Last-Modified", date);
    }

    public ResponseContext setLocation(String uri) {
        return uri == null ? removeHeader("Location") : setHeader("Location", uri);
    }

    public int getStatus() {
        return status;
    }

    public ResponseContext setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getStatusText() {
        return status_text;
    }

    public ResponseContext setStatusText(String text) {
        this.status_text = text;
        return this;
    }

    public ResponseContext setAllow(String method) {
        return setHeader("Allow", method);
    }

    public ResponseContext setAllow(String... methods) {
        StringBuilder buf = new StringBuilder();
        for (String method : methods) {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(method);
        }
        return setAllow(buf.toString());
    }

    public ResponseContext setWriter(Writer writer) {
        this.writer = writer;
        return this;
    }

}
