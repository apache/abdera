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
package org.apache.abdera.examples.html;

import java.io.StringReader;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.xpath.XPath;

/**
 * The optional HTML Parser extension allows Abdera to parse and work with HTML
 */
public class HtmlExample {

    @SuppressWarnings("unchecked")
    public static void main(String... args) throws Exception {

        String html = "<html><body><p>this is <i>html</i></body></html>";
        Abdera abdera = Abdera.getInstance();
        Parser parser = abdera.getParserFactory().getParser("html");
        Document<Element> doc = parser.parse(new StringReader(html));
        Element root = doc.getRoot();
        root.writeTo(System.out);
        System.out.println();

        XPath xpath = abdera.getXPath();
        List<Element> list = xpath.selectNodes("//i", doc.getRoot());
        for (Element element : list)
            System.out.println(element);
    }

}
