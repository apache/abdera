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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.i18n.iri.IRI;

public class MediaRating extends ElementWrapper {

    public MediaRating(Element internal) {
        super(internal);
    }

    public MediaRating(Factory factory) {
        super(factory, MediaConstants.RATING);
    }

    public IRI getScheme() {
        String scheme = getAttributeValue("scheme");
        return (scheme != null) ? new IRI(scheme) : null;
    }

    public void setScheme(String scheme) {
        setAttributeValue("scheme", (new IRI(scheme)).toString());
    }

    public void setAdult(boolean adult) {
        try {
            setScheme("urn:simple");
            setText(adult ? "adult" : "nonadult");
        } catch (Exception e) {
        }
    }

    public boolean isAdult() {
        String scheme = getAttributeValue("scheme");
        String text = getText();
        return scheme.equals("urn:simple") && "adult".equalsIgnoreCase(text);
    }
}
