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
package org.apache.abdera.ext.rss;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.Link;

public class RssLink extends ExtensibleElementWrapper implements Link {

    public RssLink(Element internal) {
        super(internal);
    }

    public RssLink(Factory factory, QName qname) {
        super(factory, qname);
    }

    public IRI getHref() {
        String txt = getText();
        return (txt != null) ? new IRI(txt) : null;
    }

    public String getHrefLang() {
        return null;
    }

    public long getLength() {
        return -1;
    }

    public MimeType getMimeType() {
        return null;
    }

    public String getRel() {
        QName qname = getQName();
        if (qname.equals(RssConstants.QNAME_DOCS)) {
            return "docs";
        } else if (qname.equals(RssConstants.QNAME_COMMENTS)) {
            return "comments";
        } else {
            return Link.REL_ALTERNATE;
        }
    }

    public IRI getResolvedHref() {
        return getHref();
    }

    public String getTitle() {
        return null;
    }

    public Link setHref(String href) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link setHrefLang(String lang) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link setLength(long length) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link setMimeType(String type) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link setRel(String rel) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public Link setTitle(String title) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

}
