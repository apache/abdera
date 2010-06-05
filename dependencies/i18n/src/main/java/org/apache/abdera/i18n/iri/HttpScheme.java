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
package org.apache.abdera.i18n.iri;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;

class HttpScheme extends AbstractScheme {

    static final String NAME = "http";
    static final int DEFAULT_PORT = 80;

    public HttpScheme() {
        super(NAME, DEFAULT_PORT);
    }

    protected HttpScheme(String name, int port) {
        super(name, port);
    }

    @Override
    public IRI normalize(IRI iri) {
        StringBuilder buf = new StringBuilder();
        int port = (iri.getPort() == getDefaultPort()) ? -1 : iri.getPort();
        String host = iri.getHost();
        if (host != null)
            host = host.toLowerCase();
        String ui = iri.getUserInfo();
        iri.buildAuthority(buf, ui, host, port);
        String authority = buf.toString();
        return new IRI(iri._scheme, iri.getScheme(), authority, ui, host, port, IRI.normalize(iri.getPath()),
                       UrlEncoding.encode(UrlEncoding.decode(iri.getQuery()), Profile.IQUERY.filter()), UrlEncoding
                           .encode(UrlEncoding.decode(iri.getFragment()), Profile.IFRAGMENT.filter()));
    }

    // use the path normalization coded into the IRI class
    public String normalizePath(String path) {
        return null;
    }

}
