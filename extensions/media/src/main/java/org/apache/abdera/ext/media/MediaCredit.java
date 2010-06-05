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

public class MediaCredit extends ElementWrapper {

    public MediaCredit(Element internal) {
        super(internal);
    }

    public MediaCredit(Factory factory) {
        super(factory, MediaConstants.CREDIT);
    }

    public IRI getScheme() {
        String scheme = getAttributeValue("scheme");
        return (scheme != null) ? new IRI(scheme) : null;
    }

    public void setScheme(String scheme) {
        if (scheme != null)
            setAttributeValue("scheme", (new IRI(scheme)).toString());
        else
            removeAttribute(new QName("scheme"));
    }

    public String getRole() {
        return getAttributeValue("role");
    }

    public void setRole(String role) {
        if (role != null)
            setAttributeValue("role", role);
        else
            removeAttribute(new QName("role"));
    }

}
