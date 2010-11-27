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
package org.apache.abdera.security.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.security.SecurityOptions;

@SuppressWarnings("unchecked")
public abstract class SecurityBase {

    protected final Abdera abdera;

    protected SecurityBase(Abdera abdera) {
        this.abdera = abdera;
    }

    protected Abdera getAbdera() {
        return abdera;
    }

    protected org.w3c.dom.Document fomToDom(Document doc, SecurityOptions options) {
        org.w3c.dom.Document dom = null;
        if (doc != null) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                doc.writeTo(out);
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setValidating(false);
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                dom = db.parse(in);
            } catch (Exception e) {
            }
        }
        return dom;
    }

    protected Document domToFom(org.w3c.dom.Document dom, SecurityOptions options) {
        Document doc = null;
        if (dom != null) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                t.transform(new DOMSource(dom), new StreamResult(out));
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                doc = options.getParser().parse(in);
            } catch (Exception e) {
            }
        }
        return doc;
    }

    protected org.w3c.dom.Element fomToDom(Element element, SecurityOptions options) {
        org.w3c.dom.Element dom = null;
        if (element != null) {
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(element.toString().getBytes());
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setValidating(false);
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                dom = db.parse(in).getDocumentElement();
            } catch (Exception e) {
            }
        }
        return dom;
    }

    protected Element domToFom(org.w3c.dom.Element element, SecurityOptions options) {
        Element el = null;
        if (element != null) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                t.transform(new DOMSource(element), new StreamResult(out));
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                el = options.getParser().parse(in).getRoot();
            } catch (Exception e) {
            }
        }
        return el;
    }
}
