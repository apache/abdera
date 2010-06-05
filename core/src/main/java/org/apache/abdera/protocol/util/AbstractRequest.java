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

import org.apache.abdera.protocol.Request;
import org.apache.abdera.util.EntityTag;

public abstract class AbstractRequest extends AbstractMessage implements Request {

    protected long max_stale = -1;
    protected long min_fresh = -1;

    public String getAccept() {
        return getHeader("Accept");
    }

    public String getAcceptCharset() {
        return getHeader("Accept-Charset");
    }

    public String getAcceptEncoding() {
        return getHeader("Accept-Encoding");
    }

    public String getAcceptLanguage() {
        return getHeader("Accept-Language");
    }

    public String getAuthorization() {
        return getHeader("Authorization");
    }

    public EntityTag[] getIfMatch() {
        return EntityTag.parseTags(getHeader("If-Match"));
    }

    public Date getIfModifiedSince() {
        return getDateHeader("If-Modified-Since");
    }

    public EntityTag[] getIfNoneMatch() {
        return EntityTag.parseTags(getHeader("If-None-Match"));
    }

    public Date getIfUnmodifiedSince() {
        return getDateHeader("If-Unmodified-Since");
    }

    public long getMaxStale() {
        return max_stale;
    }

    public long getMinFresh() {
        return min_fresh;
    }

    public boolean isOnlyIfCached() {
        return check(ONLYIFCACHED);
    }

    public AbstractRequest setMaxAge(long max_age) {
        this.max_age = max_age;
        return this;
    }

    public AbstractRequest setMaxStale(long max_stale) {
        this.max_stale = max_stale;
        return this;
    }

    public AbstractRequest setMinFresh(long min_fresh) {
        this.min_fresh = min_fresh;
        return this;
    }

    public AbstractRequest setNoCache(boolean val) {
        toggle(val, NOCACHE);
        return this;
    }

    public AbstractRequest setNoStore(boolean val) {
        toggle(val, NOSTORE);
        return this;
    }

    public AbstractRequest setNoTransform(boolean val) {
        toggle(val, NOTRANSFORM);
        return this;
    }

    public AbstractRequest setOnlyIfCached(boolean val) {
        toggle(val, ONLYIFCACHED);
        return this;
    }

}
