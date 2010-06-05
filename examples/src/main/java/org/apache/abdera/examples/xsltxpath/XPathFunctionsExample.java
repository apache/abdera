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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;
import org.apache.abdera.parser.stax.FOMXPath;
import org.apache.abdera.xpath.XPath;
import org.apache.axiom.om.OMNode;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

public class XPathFunctionsExample {

    @SuppressWarnings("unchecked")
    public static void main(String... args) throws Exception {

        Abdera abdera = Abdera.getInstance();
        Feed feed = abdera.newFeed();
        feed.setBaseUri("http://example.org/");
        // add additional feed metadata
        Entry entry = feed.addEntry();
        // add additional feed metadata
        entry.addLink("alternate.xml"); // relative URI

        XPath xpath = abdera.getXPath();
        System.out.println(xpath.valueOf("abdera:resolve(/a:feed/a:entry/a:link/@href)", feed));

        // You can add your own xpath functions.
        FOMXPath fxpath = (FOMXPath)xpath;
        Map<QName, Function> functions = fxpath.getDefaultFunctions();
        functions.put(AlternateLinkFunction.QNAME, new AlternateLinkFunction());
        fxpath.setDefaultFunctions(functions);
        List<Link> links = fxpath.selectNodes("abdera:altlinks(/a:feed/a:entry)", feed);
        System.out.println(links);
    }

    public static class AlternateLinkFunction implements Function {

        public static final QName QNAME = new QName("http://abdera.apache.org", "altlinks");

        @SuppressWarnings("unchecked")
        public Object call(Context context, List args) throws FunctionCallException {
            List<Link> results = new ArrayList<Link>();
            if (args.isEmpty())
                return null;
            for (Object obj : args) {
                if (obj instanceof List) {
                    for (Object o : (List)obj) {
                        try {
                            if (o instanceof OMNode) {
                                OMNode node = (OMNode)o;
                                List<Link> links = null;
                                if (node instanceof Source) {
                                    Source source = (Source)node;
                                    links = source.getLinks("alternate");
                                } else if (node instanceof Entry) {
                                    Entry entry = (Entry)node;
                                    links = entry.getLinks("alternate");
                                }
                                if (links != null)
                                    results.addAll(links);
                            }
                        } catch (Exception e) {
                        }
                    }
                } else {
                    // nothing to do
                }
            }
            return results;
        }

    }
}
