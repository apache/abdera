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

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.filter.ListParseFilter;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.filter.WhiteListParseFilter;

/**
 * Illustrates the use of optimized-parsing using the WhiteListParseFilter. Using this mechanism, only the elements
 * added to the ParseFilter will be parsed and added to the Feed Object Model instance. The resulting savings in memory
 * and CPU costs is significant.
 */
public class PrintTitles {
    public static void main(String args[]) {
        InputStream input;

        Parser parser = Abdera.getNewParser();

        try {
            input = new URL(args[0]).openStream();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ParserOptions opts = parser.getDefaultParserOptions();

        ListParseFilter filter = new WhiteListParseFilter();
        filter.add(Constants.FEED);
        filter.add(Constants.ENTRY);
        filter.add(Constants.TITLE);
        opts.setParseFilter(filter);

        Document<Feed> doc;

        try {
            doc = parser.parse(input, "", opts);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Feed feed = doc.getRoot();

        List<Entry> entries = feed.getEntries();

        for (Entry e : entries) {
            System.out.println(e.getTitle());
        }
    }
}
