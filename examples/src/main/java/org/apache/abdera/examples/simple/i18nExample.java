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
import org.apache.abdera.ext.bidi.BidiHelper;
import org.apache.abdera.i18n.text.Bidi.Direction;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

/**
 * Simple example demonstrating Abdera's i18n support
 */
public class i18nExample {

    public static void main(String... args) throws Exception {

        Abdera abdera = Abdera.getInstance();
        Feed feed = abdera.newFeed();
        feed.getDocument().setCharset("UTF-8");

        // Set the language context and default text direction
        feed.setLanguage("ar"); // Arabic
        BidiHelper.setDirection(Direction.RTL, feed);

        feed.setBaseUri("http://\u0645\u062b\u0627\u0644.org/ar/feed.xml");
        feed.setId("tag:\u0645\u062b\u0627\u0644.org,2007:/\u0645\u062b\u0627\u0644");
        feed.setUpdated(new Date());
        feed
            .setTitle("\u0645\u062b\u0644\u0627\u0020\u0627\u0644\u0646\u0635\u0020\u0627\u0644\u0639\u0631\u0628\u064a");
        feed.addAuthor("\u062c\u064a\u0645\u0633");
        feed.addLink("", "self");
        feed.addLink("http://\u0645\u062b\u0627\u0644.org/ar/blog");

        Entry entry = feed.addEntry();
        entry.setId("tag:\u0645\u062b\u0627\u0644.org,2007:/\u0645\u062b\u0627\u0644/1");
        entry.setTitle("\u0645\u062b\u0627\u0644\u0020\u062f\u062e\u0648\u0644");
        entry.setUpdated(new Date());
        entry.addLink("http://\u0645\u062b\u0627\u0644.org/ar/blog/1");
        entry
            .setSummaryAsXhtml("<p xml:lang=\"ar\" dir=\"rtl\">\u0648\u0647\u0630\u0627\u0020\u0645\u062b\u0627\u0644\u0020\u0639\u0644\u0649\u0020\u0648\u062c\u0648\u062f\u0020\u0041\u0074\u006f\u006d\u0020\u0031\u002e\u0030\u0020\u0052\u0054\u004c\u0020\u0627\u0644\u0627\u0639\u0644\u0627\u0641\u0020\u0627\u0644\u062a\u064a\u0020\u062a\u062d\u062a\u0648\u064a\u0020\u0639\u0644\u0649\u0020\u0627\u0644\u0646\u0635\u0020\u0627\u0644\u0639\u0631\u0628\u064a</p>");

        feed.writeTo("prettyxml", System.out);

        System.out.println();
        Element el = feed.getEntries().get(0).getSummaryElement().getValueElement().getFirstChild();
        System.out.println("Incorrect: " + el.getText());
        System.out.println("Correct: " + BidiHelper.getBidiElementText(el));
    }

}
