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
package org.apache.abdera.parser.stax.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Link;
import org.apache.abdera.util.Constants;
import org.apache.axiom.util.UIDGenerator;

@SuppressWarnings("unchecked")
public class FOMHelper implements Constants {

    public static List<Category> getCategories(Element element, String scheme) {
        Iterator i = new FOMElementIterator(element, Category.class, SCHEME, scheme, null);
        return new FOMList<Category>(i);
    }

    public static List<Link> getLinks(Element element, String rel) {
        Iterator i = new FOMLinkIterator(element, Link.class, REL, rel, Link.REL_ALTERNATE);
        return new FOMList<Link>(i);
    }

    public static List<Link> getLinks(Element element, String... rels) {
        List<Link> links = new ArrayList<Link>();
        for (String rel : rels) {
            List<Link> l = getLinks(element, rel);
            links.addAll(l);
        }
        return links;
    }

    public static String generateUuid() {
        return UIDGenerator.generateURNString();
    }
}
