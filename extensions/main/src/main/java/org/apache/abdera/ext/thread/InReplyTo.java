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
package org.apache.abdera.ext.thread;

import javax.activation.MimeType;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.i18n.iri.IRI;

public class InReplyTo extends ElementWrapper {

    public InReplyTo(Element internal) {
        super(internal);
    }

    public InReplyTo(Factory factory) {
        super(factory, ThreadConstants.IN_REPLY_TO);
    }

    public IRI getHref() {
        String href = getAttributeValue("href");
        return (href != null) ? new IRI(href) : null;
    }

    public MimeType getMimeType() {
        try {
            String type = getAttributeValue("type");
            return (type != null) ? new MimeType(type) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public IRI getRef() {
        String ref = getAttributeValue("ref");
        return (ref != null) ? new IRI(ref) : null;
    }

    public IRI getResolvedHref() {
        IRI href = getHref();
        IRI base = getBaseUri();
        return (base == null) ? href : (href != null) ? base.resolve(href) : null;
    }

    public IRI getResolvedSource() {
        IRI href = getSource();
        IRI base = getBaseUri();
        return (base == null) ? href : (href != null) ? base.resolve(href) : null;
    }

    public IRI getSource() {
        String source = getAttributeValue("source");
        return (source != null) ? new IRI(source) : null;
    }

    public void setHref(IRI ref) {
        setAttributeValue("href", ref.toString());
    }

    public void setHref(String ref) {
        setAttributeValue("href", ref);
    }

    public void setMimeType(MimeType mimeType) {
        setAttributeValue("type", mimeType.toString());
    }

    public void setMimeType(String mimeType) {
        setAttributeValue("type", mimeType);
    }

    public void setRef(IRI ref) {
        setAttributeValue("ref", ref.toString());
    }

    public void setRef(String ref) {
        setAttributeValue("ref", ref);
    }

    public void setSource(IRI source) {
        setAttributeValue("source", source.toString());
    }

    public void setSource(String source) {
        setAttributeValue("source", source);
    }

}
