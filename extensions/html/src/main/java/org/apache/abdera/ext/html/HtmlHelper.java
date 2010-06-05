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
package org.apache.abdera.ext.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.util.XmlRestrictedCharReader;

public class HtmlHelper {

    private HtmlHelper() {
    }

    public static Div parse(String value) {
        return parse(Abdera.getInstance(), value);
    }

    public static Div parse(InputStream in) {
        return parse(Abdera.getInstance(), in);
    }

    public static Div parse(InputStream in, String charset) {
        return parse(Abdera.getInstance(), in, charset);
    }

    public static Div parse(Reader in) {
        return parse(Abdera.getInstance(), in);
    }

    public static Div parse(Abdera abdera, String value) {
        return parse(abdera, new StringReader(value));
    }

    public static Div parse(Abdera abdera, InputStream in) {
        return parse(abdera, in, "UTF-8");
    }

    public static Div parse(Abdera abdera, InputStream in, String charset) {
        try {
            return parse(abdera, new InputStreamReader(in, charset));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Div parse(Abdera abdera, Reader in) {
        String result = null;
        Div div = abdera.getFactory().newDiv();
        try {
            div.setValue(HtmlCleaner.parse(in, true));
            return div;
        } catch (Exception e) {
            // this is a temporary hack. some html really
            // can't be parsed successfully. in that case,
            // we produce something that will likely render
            // rather ugly. but there's not much else we
            // can do
            if (result != null)
                div.setText(result);
            return div;
        }
    }

    public static Document<Element> parseDocument(Reader in) {
        return parseDocument(Abdera.getInstance(), in);
    }

    public static Document<Element> parseDocument(Abdera abdera, Reader in) {
        return abdera.getParser().parse(new StringReader(HtmlCleaner.parse(in, false)));
    }

    /**
     * This will search the element tree for elements named "link" with a rel attribute containing the value of rel and
     * a type attribute containg the value of type.
     */
    public static List<Element> discoverLinks(Element base, String type, String... rel) {
        List<Element> results = new ArrayList<Element>();
        walkElementForLinks(results, base, rel, type);
        return results;
    }

    private static void walkElementForLinks(List<Element> results, Element base, String[] rel, String type) {
        if (checkElementForLink(base, rel, type))
            results.add(base);
        for (Element child : base.getElements())
            walkElementForLinks(results, child, rel, type);
    }

    private static boolean checkElementForLink(Element base, String[] relvals, String type) {
        if (base.getQName().getLocalPart().equalsIgnoreCase("link")) {
            String relattr = base.getAttributeValue("rel");
            String typeattr = base.getAttributeValue("type");
            if (relattr != null) {
                String[] rels = relattr.split("\\s+");
                Arrays.sort(rels);
                for (String rel : relvals) {
                    if (Arrays.binarySearch(rels, rel) < 0)
                        return false;
                }
            }
            if (type != null && typeattr == null)
                return false;
            if (type == null && typeattr != null)
                return true; // assume possible match
            if (MimeTypeHelper.isMatch(type, typeattr))
                return true;
        }
        return false;
    }

    public static List<Element> discoverLinks(String uri, String type, String... rel) throws IOException {
        return discoverLinks(Abdera.getInstance(), uri, type, rel);
    }

    public static List<Element> discoverLinks(Abdera abdera, String uri, String type, String... rel) throws IOException {
        AbderaClient client = new AbderaClient(abdera);
        ClientResponse resp = client.get(uri);
        InputStream in = resp.getInputStream();
        InputStreamReader r = new InputStreamReader(in);
        XmlRestrictedCharReader x = new XmlRestrictedCharReader(r);
        Document<Element> doc = HtmlHelper.parseDocument(x);
        List<Element> list = discoverLinks(doc.getRoot(), type, rel);
        return list;
    }
}
