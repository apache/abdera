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
package org.apache.abdera.examples.uritemplates;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.templates.Template;
import org.apache.abdera.i18n.templates.URITemplate;
import org.apache.abdera.i18n.templates.VarName;

public class AtomLinkTemplate {

    public static void main(String... args) throws Exception {

        Abdera abdera = Abdera.getInstance();
        abdera.newStreamWriter().setOutputStream(System.out).startDocument().startFeed()
            .writeBase("http://example.org").writeLink(getPage("entries", 1, 10), "current")
            .writeLink(getPage("entries", 2, 10), "self").writeLink(getPage("entries", 1, 10), "previous")
            .writeLink(getPage("entries", 3, 10), "next").writeLink(getPage("entries", 1, 10), "first")
            .writeLink(getPage("entries", 10, 10), "last").endFeed().endDocument().flush();

    }

    private static String getPage(String view, int page, int count) {
        return Template.expandAnnotated(new PagingLink(view, page, count));
    }

    @URITemplate("/{view}?{-join|&|page,count}")
    public static class PagingLink {
        private final String view;
        private final int page;
        private final int count;

        public PagingLink(String view, int page, int count) {
            this.view = view;
            this.page = page;
            this.count = count;
        }

        public String getView() {
            return view;
        }

        public int getPage() {
            return page;
        }

        @VarName("count")
        public int getPageSize() {
            return count;
        }
    }

}
