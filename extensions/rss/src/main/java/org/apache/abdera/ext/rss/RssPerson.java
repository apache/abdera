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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.PersonWrapper;

public class RssPerson extends PersonWrapper implements Person {

    static String EMAIL_PATTERN =
        "(([a-zA-Z0-9\\_\\-\\.\\+]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?))(\\?subject=\\S+)?";

    private String email = null;
    private String name = null;

    public RssPerson(Element internal) {
        super(internal);

        Pattern p = Pattern.compile(EMAIL_PATTERN);

        String t = getText();
        Matcher m = p.matcher(t);
        if (m.find()) {
            email = m.group(0);
            name =
                t.replaceAll(email, "").replaceAll("[\\(\\)\\<\\>]", "").replaceAll("mailto:", "")
                    .replaceAll("\\&lt\\;", "").replaceAll("\\&gt\\;", "").trim();
        }

    }

    public RssPerson(Factory factory, QName qname) {
        super(factory, qname);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Element getEmailElement() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Element getNameElement() {
        return null;
    }

    @Override
    public IRI getUri() {
        return null;
    }

    @Override
    public IRIElement getUriElement() {
        return null;
    }

    @Override
    public Element setEmail(String email) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public Person setEmailElement(Element element) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public Element setName(String name) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public Person setNameElement(Element element) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public IRIElement setUri(String uri) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

    @Override
    public Person setUriElement(IRIElement element) {
        throw new UnsupportedOperationException("Modifications are not allowed");
    }

}
