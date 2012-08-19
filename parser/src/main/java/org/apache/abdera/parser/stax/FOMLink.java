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
package org.apache.abdera.parser.stax;

import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Link;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMLink extends FOMExtensibleElement implements Link {

    private static final long serialVersionUID = 2239772197929910635L;

    protected FOMLink(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMLink(OMContainer parent, OMFactory factory) throws OMException {
        super(LINK, parent, factory);
    }

    protected FOMLink(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMLink(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder)
        throws OMException {
        super(localName, parent, factory, builder);
    }

    public IRI getHref() {
        return _getUriValue(getAttributeValue(HREF));
    }

    public IRI getResolvedHref() {
        return _resolve(getResolvedBaseUri(), getHref());
    }

    public Link setHref(String href) {
        complete();
        if (href != null)
            setAttributeValue(HREF, (new IRI(href)).toString());
        else
            removeAttribute(HREF);
        return this;
    }

    public String getRel() {
        return getAttributeValue(REL);
    }

    public Link setRel(String rel) {
        complete();
        setAttributeValue(REL, rel);
        return this;
    }

    public MimeType getMimeType() {
        try {
            String type = getAttributeValue(TYPE);
            return (type != null) ? new MimeType(type) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public void setMimeType(MimeType type) {
        complete();
        setAttributeValue(TYPE, (type != null) ? type.toString() : null);
    }

    public Link setMimeType(String type) {
        complete();
        try {
            if (type != null)
                setAttributeValue(TYPE, (new MimeType(type)).toString());
            else
                removeAttribute(TYPE);
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
        return this;
    }

    public String getHrefLang() {
        return getAttributeValue(HREFLANG);
    }

    public Link setHrefLang(String lang) {
        complete();
        if (lang != null)
            setAttributeValue(HREFLANG, lang);
        else
            removeAttribute(HREFLANG);
        return this;
    }

    public String getTitle() {
        return getAttributeValue(ATITLE);
    }

    public Link setTitle(String title) {
        complete();
        if (title != null)
            setAttributeValue(ATITLE, title);
        else
            removeAttribute(ATITLE);
        return this;
    }

    public long getLength() {
        String l = getAttributeValue(LENGTH);
        return (l != null) ? Long.valueOf(l) : -1;
    }

    public Link setLength(long length) {
        complete();
        if (length > -1)
            setAttributeValue(LENGTH, (length >= 0) ? String.valueOf(length) : "0");
        else
            removeAttribute(LENGTH);
        return this;
    }

    private static final Map<String, String> REL_EQUIVS = new HashMap<String, String>();
    static {
        REL_EQUIVS.put(REL_ALTERNATE_IANA, REL_ALTERNATE);
        REL_EQUIVS.put(REL_CURRENT_IANA, REL_CURRENT);
        REL_EQUIVS.put(REL_ENCLOSURE_IANA, REL_ENCLOSURE);
        REL_EQUIVS.put(REL_FIRST_IANA, REL_FIRST);
        REL_EQUIVS.put(REL_LAST_IANA, REL_LAST);
        REL_EQUIVS.put(REL_NEXT_IANA, REL_NEXT);
        REL_EQUIVS.put(REL_PAYMENT_IANA, REL_PAYMENT);
        REL_EQUIVS.put(REL_PREVIOUS_IANA, REL_PREVIOUS);
        REL_EQUIVS.put(REL_RELATED_IANA, REL_RELATED);
        REL_EQUIVS.put(REL_SELF_IANA, REL_SELF);
        REL_EQUIVS.put(REL_VIA_IANA, REL_VIA);
        REL_EQUIVS.put(REL_REPLIES_IANA, REL_REPLIES);
        REL_EQUIVS.put(REL_LICENSE_IANA, REL_LICENSE);
        REL_EQUIVS.put(REL_EDIT_IANA, REL_EDIT);
        REL_EQUIVS.put(REL_EDIT_MEDIA_IANA, REL_EDIT_MEDIA);
        REL_EQUIVS.put(REL_SERVICE_IANA, REL_SERVICE);
    }

    public static final String getRelEquiv(String val) {
        try {
            val = IRI.normalizeString(val);
        } catch (Exception e) {
        }
        String rel = REL_EQUIVS.get(val);
        return (rel != null) ? rel : val;
    }

    public String getValue() {
        return getText();
    }

    public void setValue(String value) {
        complete();
        if (value != null)
            ((Element)this).setText(value);
        else
            _removeAllChildren();
    }

}
