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
package org.apache.abdera.protocol.util;

import java.util.Date;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.util.EntityTag;

public abstract class AbstractResponse extends AbstractMessage implements Response {

    protected String[] nocache_headers = null;
    protected String[] private_headers = null;
    protected long smax_age = -1;

    public long getAge() {
        String value = getHeader("Age");
        try {
            return (value != null) ? Long.parseLong(value) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getAllow() {
        return getHeader("Allow");
    }

    public long getContentLength() {
        String value = getHeader("Content-Length");
        try {
            return (value != null) ? Long.parseLong(value) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public EntityTag getEntityTag() {
        String etag = getHeader("ETag");
        return (etag != null) ? EntityTag.parse(getHeader("ETag")) : null;
    }

    public Date getExpires() {
        return getDateHeader("Expires");
    }

    public Date getLastModified() {
        return getDateHeader("Last-Modified");
    }

    public IRI getLocation() {
        String l = getHeader("Location");
        return l != null ? new IRI(l) : null;
    }

    public String[] getNoCacheHeaders() {
        return nocache_headers;
    }

    public String[] getPrivateHeaders() {
        return private_headers;
    }

    public long getSMaxAge() {
        return smax_age;
    }

    public ResponseType getType() {
        return ResponseType.select(getStatus());
    }

    public boolean isMustRevalidate() {
        return check(REVALIDATE);
    }

    public boolean isPrivate() {
        return check(PRIVATE);
    }

    public boolean isProxyRevalidate() {
        return check(PROXYREVALIDATE);
    }

    public boolean isPublic() {
        return check(PUBLIC);
    }

    public AbstractResponse setMaxAge(long max_age) {
        this.max_age = max_age;
        return this;
    }

    public AbstractResponse setMustRevalidate(boolean val) {
        toggle(val, REVALIDATE);
        return this;
    }

    public AbstractResponse setProxyRevalidate(boolean val) {
        toggle(val, PROXYREVALIDATE);
        return this;
    }

    public AbstractResponse setNoCache(boolean val) {
        toggle(val, NOCACHE);
        return this;
    }

    public AbstractResponse setNoStore(boolean val) {
        toggle(val, NOSTORE);
        return this;
    }

    public AbstractResponse setNoTransform(boolean val) {
        toggle(val, NOTRANSFORM);
        return this;
    }

    public AbstractResponse setPublic(boolean val) {
        toggle(val, PUBLIC);
        return this;
    }

    public AbstractResponse setPrivate(boolean val) {
        if (val)
            flags |= PRIVATE;
        else if (isPrivate())
            flags ^= PRIVATE;
        return this;
    }

    public AbstractResponse setPrivateHeaders(String... headers) {
        this.private_headers = headers;
        return this;
    }

    public AbstractResponse setNoCacheHeaders(String... headers) {
        this.nocache_headers = headers;
        return this;
    }

}
