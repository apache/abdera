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
package org.apache.abdera.ext.features;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.util.MimeTypeHelper;

public class Feature extends ExtensibleElementWrapper {

    public Feature(Element internal) {
        super(internal);
    }

    public Feature(Factory factory) {
        super(factory, FeaturesHelper.FEATURE);
    }

    public IRI getRef() {
        String ref = getAttributeValue("ref");
        return (ref != null) ? new IRI(ref) : null;
    }

    public IRI getHref() {
        String href = getAttributeValue("href");
        return (href != null) ? new IRI(href) : null;
    }

    public String getLabel() {
        return getAttributeValue("label");
    }

    public void setRef(String ref) {
        if (ref == null)
            throw new IllegalArgumentException();
        setAttributeValue("ref", (new IRI(ref)).toString());
    }

    public void setHref(String href) {
        if (href != null)
            setAttributeValue("href", (new IRI(href)).toString());
        else
            removeAttribute(new QName("href"));
    }

    public void setLabel(String label) {
        if (label != null)
            setAttributeValue("label", label);
        else
            removeAttribute(new QName("label"));
    }

    public void addType(String mediaRange) {
        addType(new String[] {mediaRange});
    }

    public void addType(String... mediaRanges) {
        mediaRanges = MimeTypeHelper.condense(mediaRanges);
        for (String mediaRange : mediaRanges) {
            try {
                addSimpleExtension(FeaturesHelper.TYPE, new MimeType(mediaRange).toString());
            } catch (MimeTypeParseException e) {
            }
        }
    }

    public String[] getTypes() {
        List<String> list = new ArrayList<String>();
        for (Element type : getExtensions(FeaturesHelper.TYPE)) {
            String value = type.getText();
            if (value != null) {
                value = value.trim();
                try {
                    list.add(new MimeType(value).toString());
                } catch (MimeTypeParseException e) {
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

}
