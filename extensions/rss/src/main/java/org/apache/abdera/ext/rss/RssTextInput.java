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
import org.apache.abdera.model.Text;

public class RssTextInput extends ExtensibleElementWrapper {

    public RssTextInput(Element internal) {
        super(internal);
    }

    public RssTextInput(Factory factory, QName qname) {
        super(factory, qname);
    }

    public String getTitle() {
        Text text = getExtension(RssConstants.QNAME_TITLE);
        return (text != null) ? text.getValue() : null;
    }

    public String getDescription() {
        Text text = getExtension(RssConstants.QNAME_DESCRIPTION);
        return (text != null) ? text.getValue() : null;
    }

    public String getName() {
        Element el = getExtension(RssConstants.QNAME_NAME);
        return (el != null) ? el.getText() : null;
    }

    public IRI getLink() {
        IRIElement iri = getExtension(RssConstants.QNAME_LINK);
        return (iri != null) ? iri.getValue() : null;
    }
}
