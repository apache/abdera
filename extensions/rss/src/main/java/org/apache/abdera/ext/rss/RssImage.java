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

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;

public class RssImage extends ExtensibleElementWrapper implements IRIElement {

    public RssImage(Element internal) {
        super(internal);
    }

    public RssImage(Factory factory, QName qname) {
        super(factory, qname);
    }

    public IRI getResolvedValue() {
        return getValue();
    }

    public IRI getValue() {
        IRIElement iri = getExtension(RssConstants.QNAME_URL);
        if (iri == null) {
            iri = getExtension(RssConstants.QNAME_RDF_URL);
        }
        if (iri == null) {
            String t = getAttributeValue(RssConstants.QNAME_RDF_ABOUT);
            if (t != null)
                return new IRI(t);
        }
        return (iri != null) ? iri.getValue() : null;
    }

    public IRIElement setNormalizedValue(String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public IRIElement setValue(String iri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    public String getTitle() {
        Text text = getExtension(RssConstants.QNAME_TITLE);
        if (text == null)
            text = getExtension(RssConstants.QNAME_RDF_TITLE);
        return (text != null) ? text.getValue() : null;
    }

    public String getDescription() {
        Text text = getExtension(RssConstants.QNAME_DESCRIPTION);
        if (text == null)
            text = getExtension(RssConstants.QNAME_RDF_DESCRIPTION);
        return (text != null) ? text.getValue() : null;
    }

    public IRI getLink() {
        Link link = getExtension(RssConstants.QNAME_LINK);
        if (link == null)
            link = getExtension(RssConstants.QNAME_RDF_LINK);
        return (link != null) ? link.getHref() : null;
    }

    public int getWidth() {
        String w = getSimpleExtension(RssConstants.QNAME_WIDTH);
        return (w != null) ? Integer.parseInt(w) : -1;
    }

    public int getHeight() {
        String w = getSimpleExtension(RssConstants.QNAME_HEIGHT);
        return (w != null) ? Integer.parseInt(w) : -1;
    }
}
