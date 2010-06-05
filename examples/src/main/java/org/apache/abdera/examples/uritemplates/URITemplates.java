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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.abdera.i18n.templates.CachingContext;
import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.Template;

@SuppressWarnings("unchecked")
public final class URITemplates {

    private static final Template template =
        new Template(
                     "http://example.org{-opt|/~|user}{user}{-opt|/-/|categories}{-listjoin|/|categories}{-opt|?|foo,bar}{-join|&|foo,bar}");

    public static void main(String... args) throws Exception {

        // two examples of resolving the template
        exampleWithObject();
        exampleIRIWithObject();
        exampleWithMap();
        exampleWithHashMapContext();
        exampleWithCustomContext();

        // explain the template
        System.out.println(template);
    }

    // Using a Java object
    private static void exampleWithObject() {
        MyObject myObject = new MyObject();
        System.out.println(template.expand(myObject));
    }

    // Using a Java object
    private static void exampleIRIWithObject() {
        MyObject myObject = new MyObject();
        System.out.println(template.expand(myObject, true));
    }

    // Using a Map
    private static void exampleWithMap() {
        Map<String, Object> map = new HashMap();
        map.put("user", "james");
        map.put("categories", new String[] {"a", "b", "c"});
        map.put("foo", "abc");
        map.put("bar", "xyz");
        System.out.println(template.expand(map));
    }

    // Using a HashMap based context
    private static void exampleWithHashMapContext() {
        HashMapContext context = new HashMapContext();
        context.put("user", "james");
        context.put("categories", new String[] {"a", "b", "c"});
        context.put("foo", "abc");
        context.put("bar", "xyz");
        System.out.println(template.expand(context));
    }

    // Using a custom context implementation
    private static void exampleWithCustomContext() {
        CachingContext context = new CachingContext() {
            private static final long serialVersionUID = 4896250661828139020L;

            protected <T> T resolveActual(String var) {
                if (var.equals("user"))
                    return (T)"james";
                else if (var.equals("categories"))
                    return (T)new String[] {"a", "b", "c"};
                else if (var.equals("foo"))
                    return (T)"abc";
                else if (var.equals("bar"))
                    return (T)"xyz";
                else
                    return null;
            }

            public Iterator<String> iterator() {
                return Arrays.asList(new String[] {"user", "categories", "foo", "bar"}).iterator();
            }
        };
        System.out.println(template.expand(context));
    }

    public static class MyObject {
        public String user = "james";

        public List getCategories() {
            List<String> list = new ArrayList<String>();
            list.add("a");
            list.add("b");
            list.add("c");
            return list;
        }

        public Foo[] getFoo() {
            return new Foo[] {new Foo(), new Foo()};
        }

        public String getBar() {
            return "xyz";
        }
    }

    private static class Foo {
        public String toString() {
            return "abcæ";
        }
    }
}
