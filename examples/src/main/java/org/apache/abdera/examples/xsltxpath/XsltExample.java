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
package org.apache.abdera.examples.xsltxpath;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.util.AbderaSource;

public class XsltExample {

    public static void main(String[] args) {

        Parser parser = Abdera.getNewParser();

        try {

            // Apply an XSLT transform to the entire Feed
            TransformerFactory factory = TransformerFactory.newInstance();

            // Abdera is capable of parsing any well-formed XML document, even XSLT
            Document<Element> xslt = parser.parse(XsltExample.class.getResourceAsStream("/test.xslt"));
            AbderaSource xsltSource = new AbderaSource(xslt);
            Transformer transformer = factory.newTransformer(xsltSource);

            // Now let's get the feed we're going to transform
            Document<Feed> feed = parser.parse(XsltExample.class.getResourceAsStream("/simple.xml"));
            AbderaSource feedSource = new AbderaSource(feed);

            // Prepare the output
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Result result = new StreamResult(out);
            transformer.transform(feedSource, result);
            System.out.println(out); // "This is a test urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"

            // Apply an XSLT transform to XML in the content element
            xslt = parser.parse(XsltExample.class.getResourceAsStream("/content.xslt"));
            xsltSource = new AbderaSource(xslt);
            transformer = factory.newTransformer(xsltSource);

            feed = parser.parse(XsltExample.class.getResourceAsStream("/xmlcontent.xml"));
            Entry entry = feed.getRoot().getEntries().get(0);
            Content content = entry.getContentElement();
            AbderaSource contentSource = new AbderaSource(content.getValueElement());

            // Note that the AbderaSource is set to the value element of atom:content!!

            out = new ByteArrayOutputStream();
            result = new StreamResult(out);
            transformer.transform(contentSource, result);
            System.out.println(out); // "This is a test test"

        } catch (Exception exception) {
            // TrAX is likely not configured, skip the test
        }

    }

}
