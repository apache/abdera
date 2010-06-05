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

import java.io.InputStream;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.xpath.XPath;

public class XPathExample {

    public static void main(String[] args) throws Exception {

        Abdera abdera = new Abdera();
        Parser parser = abdera.getParser();
        XPath xpath = abdera.getXPath();

        InputStream in = XPathExample.class.getResourceAsStream("/simple.xml");
        Document<Feed> doc = parser.parse(in);
        Feed feed = doc.getRoot();

        System.out.println(xpath.evaluate("count(/a:feed)", feed)); // 1.0
        System.out.println(xpath.numericValueOf("count(/a:feed)", feed)); // 1.0
        System.out.println(xpath.booleanValueOf("/a:feed/a:entry", feed)); // true (the feed has an entry)
        System.out.println(xpath.valueOf("/a:feed/a:entry/a:title", feed)); // Atom-Powered Robots Run Amok
        System.out.println(xpath.selectNodes("/a:feed/a:entry", feed)); // every entry
        System.out.println(xpath.selectSingleNode("/a:feed", feed));
        System.out.println(xpath.selectSingleNode("..", feed.getTitleElement()));
        System.out.println(xpath.selectSingleNode("ancestor::*", feed.getEntries().get(0)));
        System.out.println(xpath.valueOf("concat('The feed is is ',/a:feed/a:id)", feed)); // "The feed is is urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6"

    }

}
