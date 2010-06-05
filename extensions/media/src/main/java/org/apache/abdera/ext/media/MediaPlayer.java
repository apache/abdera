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
package org.apache.abdera.ext.media;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.i18n.iri.IRI;

public class MediaPlayer extends ElementWrapper {

    public MediaPlayer(Element internal) {
        super(internal);
    }

    public MediaPlayer(Factory factory) {
        super(factory, MediaConstants.PLAYER);
    }

    public IRI getUrl() {
        String url = getAttributeValue("url");
        return (url != null) ? new IRI(url) : null;
    }

    public void setUrl(String url) {
        if (url != null) {
            setAttributeValue("url", (new IRI(url)).toString());
        } else {
            removeAttribute(new QName("url"));
        }
    }

    public int getWidth() {
        String width = getAttributeValue("width");
        return (width != null) ? Integer.parseInt(width) : -1;
    }

    public void setWidth(int width) {
        if (width > -1) {
            setAttributeValue("width", String.valueOf(width));
        } else {
            removeAttribute(new QName("width"));
        }
    }

    public int getHeight() {
        String height = getAttributeValue("height");
        return (height != null) ? Integer.parseInt(height) : -1;
    }

    public void setHeight(int height) {
        if (height > -1) {
            setAttributeValue("height", String.valueOf(height));
        } else {
            removeAttribute(new QName("height"));
        }
    }
}
