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
import org.apache.abdera.model.Entry;

/**
 * This example demonstrates the use of URI Templates to create an atom:id value from an annotated Java object.
 */
public class AtomIDTemplate {

    public static void main(String... args) throws Exception {

        ID id = new ID("myblog", "entries", "abc123xyz");

        Abdera abdera = Abdera.getInstance();
        Entry entry = abdera.newEntry();
        entry.setId(Template.expandAnnotated(id));
        entry.setTitle("This is a test");
        entry.writeTo("prettyxml", System.out);

    }

    @URITemplate("tag:example.org,2007:/{collection}/{view}/{id}")
    public static class ID {
        private final String collection;
        private final String view;
        private final String id;

        public ID(String collection, String view, String id) {
            this.collection = collection;
            this.view = view;
            this.id = id;
        }

        public String getCollection() {
            return collection;
        }

        public String getView() {
            return view;
        }

        public String getId() {
            return id;
        }
    }
}
