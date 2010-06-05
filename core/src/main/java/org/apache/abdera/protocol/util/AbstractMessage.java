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

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Rfc2047Helper;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.Message;

/**
 * Root impl for Message interface impls. This is provided solely as a way of keeping the interface and impl's
 * consistent across the Request and Response objects.
 */
public abstract class AbstractMessage implements Message {

    protected int flags = 0;
    protected long max_age = -1;

    public String getCacheControl() {
        return getHeader("Cache-Control");
    }

    public String getContentLanguage() {
        return getHeader("Content-Language");
    }

    public IRI getContentLocation() {
        String value = getHeader("Content-Location");
        return (value != null) ? new IRI(value) : null;
    }

    public MimeType getContentType() {
        try {
            String value = getHeader("Content-Type");
            return (value != null) ? new MimeType(value) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public String getDecodedHeader(String header) {
        return UrlEncoding.decode(Rfc2047Helper.decode(getHeader(header)));
    }

    public String[] getDecodedHeaders(String header) {
        Object[] headers = getHeaders(header);
        for (int n = 0; n < headers.length; n++) {
            headers[n] = UrlEncoding.decode(Rfc2047Helper.decode(headers[n].toString()));
        }
        return (String[])headers;
    }

    public String getSlug() {
        return getDecodedHeader("Slug");
    }

    protected boolean check(int flag) {
        return (flags & flag) == flag;
    }

    protected void toggle(boolean val, int flag) {
        if (val)
            flags |= flag;
        else
            flags &= ~flag;
    }

    public boolean isNoCache() {
        if (check(NOCACHE))
            return true;
        Object[] pragma = getHeaders("Pragma");
        if (pragma != null) {
            for (Object o : pragma) {
                String s = (String)o;
                if (s.equalsIgnoreCase("no-cache")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNoStore() {
        return check(NOSTORE);
    }

    public boolean isNoTransform() {
        return check(NOTRANSFORM);
    }

    public long getMaxAge() {
        return max_age;
    }
}
