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
package org.apache.abdera.examples.simple;

import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.StreamWriter;

/**
 * Demonstrates the use of the Abdera StreamWriter interface
 */
public class StreamWriterExample {

    public static void main(String... args) {

        Abdera abdera = Abdera.getInstance();

        StreamWriter out =
            abdera.newStreamWriter().setOutputStream(System.out, "UTF-8").setAutoflush(false).setAutoIndent(true)
                .startDocument().startFeed().writeBase("http://example.org").writeLanguage("en-US")
                .writeId("http://example.org").writeTitle("<Testing 123>").writeSubtitle("Foo").writeAuthor("James",
                                                                                                            null,
                                                                                                            null)
                .writeUpdated(new Date()).writeLink("http://example.org/foo").writeLink("http://example.org/bar",
                                                                                        "self").writeCategory("foo")
                .writeCategory("bar").writeLogo("logo").writeIcon("icon").writeGenerator("1.0",
                                                                                         "http://example.org",
                                                                                         "foo").flush();

        for (int n = 0; n < 100; n++) {
            out.startEntry().writeId("http://example.org/" + n).writeTitle("Entry #" + n).writeUpdated(new Date())
                .writePublished(new Date()).writeEdited(new Date()).writeSummary("This is text summary")
                .writeAuthor("James", null, null).writeContributor("Joe", null, null).startContent("application/xml")
                .startElement("a", "b", "c").startElement("x", "y", "z").writeElementText("This is a test")
                .startElement("a").writeElementText("foo").endElement().startElement("b", "bar")
                .writeAttribute("foo", new Date()).writeAttribute("bar", 123L).writeElementText(123.123).endElement()
                .endElement().endElement().endContent().endEntry().flush();
        }

        out.endFeed().endDocument().flush();

    }

}
