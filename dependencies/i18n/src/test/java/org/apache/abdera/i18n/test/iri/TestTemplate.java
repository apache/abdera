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
package org.apache.abdera.i18n.test.iri;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.Template;
import org.apache.abdera.i18n.templates.URITemplate;
import org.junit.Test;

public final class TestTemplate extends TestBase {

    @Test
    public void test1() throws Exception {
        String t = "http://bitworking.org/news/{entry}";
        String e = "http://bitworking.org/news/RESTLog_Specification";
        HashMapContext c = new HashMapContext();
        c.put("entry", "RESTLog_Specification");
        eval(t, e, c);
    }

    @Test
    public void test2() throws Exception {
        String t = "http://example.org/wiki/{entry=FrontPage}";
        String e = "http://example.org/wiki/FrontPage";
        HashMapContext c = new HashMapContext();
        eval(t, e, c);
    }

    @Test
    public void test3() throws Exception {
        String t = "http://bitworking.org/news/{-list|/|entry}";
        String e = "http://bitworking.org/news/240/Newsqueak";
        HashMapContext c = new HashMapContext();
        c.put("entry", new String[] {"240", "Newsqueak"});
        eval(t, e, c);
    }

    @Test
    public void test4() throws Exception {
        String t = "http://technorati.com/search/{term}";
        String e = "http://technorati.com/search/240%2FNewsqueak";
        HashMapContext c = new HashMapContext();
        c.put("term", "240/Newsqueak");
        eval(t, e, c);
    }

    @Test
    public void test5() throws Exception {
        String t = "http://example.org/{fruit=orange}/";
        String e = "http://example.org/apple/";
        String f = "http://example.org/orange/";
        HashMapContext c = new HashMapContext();
        c.put("fruit", "apple");
        eval(t, e, c);
        c.remove("fruit");
        eval(t, f, c);
    }

    @Test
    public void test6() throws Exception {
        String t = "bar{-prefix|/|var}/";
        String e = "bar/foo/";
        HashMapContext c = new HashMapContext();
        c.put("var", "foo");
        eval(t, e, c);
    }

    @Test
    public void test7() throws Exception {
        String t = "bar/{-suffix|#home|var}";
        String e = "bar/foo#home";
        HashMapContext c = new HashMapContext();
        c.put("var", "foo");
        eval(t, e, c);
    }

    @Test
    public void test8() throws Exception {
        String t = "{-join|&|name,location,age}";
        String e = "name=joe&location=NYC";
        HashMapContext c = new HashMapContext();
        c.put("name", "joe");
        c.put("location", "NYC");
        eval(t, e, c);
    }

    @Test
    public void test9() throws Exception {
        String t = "{-list|/|segments}";
        String e = "a/b/c";
        HashMapContext c = new HashMapContext();
        c.put("segments", new String[] {"a", "b", "c"});
        eval(t, e, c);
    }

    @Test
    public void test10() throws Exception {
        String t = "{-opt|/|segments}";
        String e = "/";
        HashMapContext c = new HashMapContext();
        c.put("segments", new String[] {"a", "b", "c"});
        eval(t, e, c);
    }

    @Test
    public void test11() throws Exception {
        String t = "{-neg|/|segments}";
        String e = "";
        HashMapContext c = new HashMapContext();
        c.put("segments", new String[] {"a", "b", "c"});
        eval(t, e, c);
    }

    @Test
    public void test12() throws Exception {
        String t = "http://www.google.com/search?q={term}";
        String e = "http://www.google.com/search?q=ben%26jerrys";
        HashMapContext c = new HashMapContext();
        c.put("term", "ben&jerrys");
        eval(t, e, c);
        t = "http://www.google.com/search?q={term}";
        e = "http://www.google.com/search?q=2%2B2%3D5";
        c = new HashMapContext();
        c.put("term", "2+2=5");
        eval(t, e, c);
        t = "http://www.google.com/base/feeds/snippets/?{-join|&|author}";
        e = "http://www.google.com/base/feeds/snippets/?author=test%40example.com";
        c = new HashMapContext();
        c.put("author", "test@example.com");
        eval(t, e, c);
    }

    @Test
    public void test13() throws Exception {
        String t = "http://www.google.com/search?q={term}";
        String e =
            "http://www.google.com/search?q=%C3%8E%C3%B1%C5%A3%C3%A9r%C3%B1%C3%A5%C5%A3%C3%AE%C3%B6%C3%B1%C3%A5%C4%BC%C3%AE%C5%BE%C3%A5%C5%A3%C3%AE%C3%B6%C3%B1";
        HashMapContext c = new HashMapContext();
        c
            .put("term",
                 "\u00ce\u00f1\u0163\u00e9\u0072\u00f1\u00e5\u0163\u00ee\u00f6\u00f1\u00e5\u013c\u00ee\u017e\u00e5\u0163\u00ee\u00f6\u00f1");
        eval(t, e, c);
    }

    @Test
    public void test14() throws Exception {
        String t = "{-opt|/-/|categories}{-list|/|categories}";
        String e = "/-/A%7C-B/-C";
        HashMapContext c = new HashMapContext();
        c.put("categories", new String[] {"A|-B", "-C"});
        eval(t, e, c);
    }

    @Test
    public void test15() throws Exception {
        String t =
            "http://www.google.com/notebook/feeds/{userID}{-prefix|/notebooks/|notebookID}{-opt|/-/|categories}{-list|/|categories}?{-join|&|updated-min,updated-max,alt,start-index,max-results,entryID,orderby}";
        String e = "http://www.google.com/notebook/feeds/a/notebooks/b?updated-min=c&max-results=d";
        HashMapContext c = new HashMapContext();
        c.put("userID", "a");
        c.put("notebookID", "b");
        c.put("updated-min", "c");
        c.put("max-results", "d");
        eval(t, e, c);
    }

    @Test
    public void test16() throws Exception {
        String t = "http://www.google.com/search?q={term}";
        String e =
            "http://www.google.com/search?q=\u00ce\u00f1\u0163\u00e9\u0072\u00f1\u00e5\u0163\u00ee\u00f6\u00f1\u00e5\u013c\u00ee\u017e\u00e5\u0163\u00ee\u00f6\u00f1";
        HashMapContext c = new HashMapContext();
        c.setIri(true);
        c
            .put("term",
                 "\u00ce\u00f1\u0163\u00e9\u0072\u00f1\u00e5\u0163\u00ee\u00f6\u00f1\u00e5\u013c\u00ee\u017e\u00e5\u0163\u00ee\u00f6\u00f1");
        eval(t, e, c); // use the IriEvaluator so that pct-encoding is done correctly for IRI's
    }

    @Test
    public void test17() throws Exception {
        String t = new String("bar{-prefix|/é/|var}/".getBytes("UTF-8"), "UTF-8");
        String e = new String("bar/é/foo/".getBytes("UTF-8"), "UTF-8");
        HashMapContext c = new HashMapContext();
        c.setIri(true);
        c.put("var", "foo");
        eval(t, e, c);
    }

    @Test
    public void test18() throws Exception {
        String t =
            "http://www.google.com/notebook/feeds/{userID}{-prefix|/notebooks/|notebookID}{-opt|/-/|categories}{-list|/|categories}?{-join|&|updated-min,updated-max,alt,start-index,max-results,entryID,orderby}";
        Template template = new Template(t);
        String[] variables = template.getVariables();
        assertEquals("userID", variables[0]);
        assertEquals("notebookID", variables[1]);
        assertEquals("categories", variables[2]);
        assertEquals("updated-min", variables[3]);
        assertEquals("updated-max", variables[4]);
        assertEquals("alt", variables[5]);
        assertEquals("start-index", variables[6]);
        assertEquals("max-results", variables[7]);
        assertEquals("entryID", variables[8]);
        assertEquals("orderby", variables[9]);
    }

    @Test
    public void test19() throws Exception {
        String t =
            "http://www.google.com/notebook/feeds/{userID}{-prefix|/notebooks/|notebookID}{-opt|/-/|categories}{-list|/|categories}?{-join|&|updated-min,updated-max,alt,start-index,max-results,entryID,orderby}";
        Template template = new Template(t);
        Template t2 = template.clone();
        assertEquals(t2, template);
        assertEquals(t2.hashCode(), template.hashCode());
    }

    @Test
    public void test20() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", "foo");
        map.put("b", "bar");
        map.put("data", "10,20,30");
        map.put("points", new String[] {"10", "20", "30"});
        map.put("list0", new String[0]);
        map.put("str0", "");
        map.put("reserved", ":/?#[]@!$&'()*+,;=");
        map.put("u", "\u2654\u2655");
        map.put("a_b", "baz");
        map.put("bytes", new byte[] {'a', 'b', 'c'});
        map.put("stream", new ByteArrayInputStream(new byte[] {'a', 'b', 'c'}));
        map.put("chars", new char[] {'a', 'b', 'c', '/'});
        map.put("ints", new int[] {1, 2, 3});

        Map<String, String> tests = new HashMap<String, String>();
        tests.put("http://example.org/?q={a}", "http://example.org/?q=foo");
        tests.put("http://example.org/{foo}", "http://example.org/");
        tests.put("relative/{reserved}/", "relative/%3A%2F%3F%23%5B%5D%40%21%24%26%27%28%29%2A%2B%2C%3B%3D/");
        tests.put("http://example.org/{foo=fred}", "http://example.org/fred");
        tests.put("http://example.org/{foo=%25}/", "http://example.org/%25/");
        tests.put("/{-prefix|#|foo}", "/");
        tests.put("./{-prefix|#|str0}", "./");
        tests.put("/{-suffix|/|a}{-opt|data|points}{-neg|@|a}{-prefix|#|b}", "/foo/data#bar");
        tests.put("http://example.org/q={u}", "http://example.org/q=%E2%99%94%E2%99%95");
        tests.put("http://example.org/?{-join|&|a,data}", "http://example.org/?a=foo&data=10%2C20%2C30");
        tests.put("http://example.org/?d={-list|,|points}&{-join|&|a,b}", "http://example.org/?d=10,20,30&a=foo&b=bar");
        tests.put("http://example.org/?d={-list|,|list0}&{-join|&|foo}", "http://example.org/?d=&");
        tests.put("http://example.org/?d={-list|&d=|points}", "http://example.org/?d=10&d=20&d=30");
        tests.put("http://example.org/{a}{b}/{a_b}", "http://example.org/foobar/baz");
        tests.put("http://example.org/{a}{-prefix|/-/|a}/", "http://example.org/foo/-/foo/");
        tests.put("{bytes}", "%61%62%63");
        tests.put("{stream}", "%61%62%63");
        tests.put("{chars}", "abc%2F");
        tests.put("{ints}", "1%2C2%2C3");

        for (String t : tests.keySet())
            assertEquals(tests.get(t), Template.expand(t, map));
    }

    @Test
    public void test21() throws Exception {
        String t = "http://example.org/{foo}/{bar}{-opt|/|categories}{-list|/|categories}?{-join|&|baz,tag}";
        String e = "http://example.org/abc/xyz/a/b?baz=true&tag=x&tag=y&tag=z";
        assertEquals(e, Template.expand(t, new MyObject()));
    }

    @Test
    public void test22() throws Exception {
        String e = "http://example.org/abc/xyz/a/b?baz=true&tag=x&tag=y&tag=z";
        assertEquals(e, Template.expandAnnotated(new MyObject()));
    }

    @Test
    public void test23() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("foo", "\u03d3");
        map.put("bar", "fred");
        map.put("baz", new int[] {10, 20, 30});
        map.put("qux", new String[] {"10", "20", "30"});
        map.put("corge", new String[0]);
        map.put("garply", "a/b/c");
        map.put("grault", "");
        map.put("waldo", "ben & jerrys");
        map.put("fred", new String[] {"fred", "", "wilma"});
        map.put("plugh", new String[] {"\u017F\u0307", "\u0073\u0307"});
        map.put("1-a_b.c", 200);

        String[] templates = {"http://example.org/?q={bar}", "http://example.org/?q=fred",

        "/{xyzzy}", "/",

        "http://example.org/?{-join|&|foo,bar,xyzzy,baz}", "http://example.org/?foo=%CE%8E&bar=fred&baz=10%2C20%2C30",

        "http://example.org/?d={-list|,|qux}", "http://example.org/?d=10,20,30",

        "http://example.org/?d={-list|&d=|qux}", "http://example.org/?d=10&d=20&d=30",

        "http://example.org/{bar}{bar}/{garply}", "http://example.org/fredfred/a%2Fb%2Fc",

        "http://example.org/{bar}{-prefix|/|fred}", "http://example.org/fred/fred//wilma",

        "{-neg|:|corge}{-suffix|:|plugh}", ":%E1%B9%A1:%E1%B9%A1:",

        "../{waldo}/", "../ben%20%26%20jerrys/",

        "telnet:192.0.2.16{-opt|:80|grault}", "telnet:192.0.2.16:80",

        ":{1-a_b.c}:", ":200:"};

        for (int n = 0; n < templates.length; n = n + 2) {
            assertEquals(templates[n + 1], Template.expand(templates[n], map));
        }
    }

    @Test
    public void test24() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", "fred");
        map.put("bar", new String[] {"fee", "fi", "fo", "fum"});
        map.put("baz", new String[0]);
        String[] templates =
            {"{-suffix|/|foo}", "fred/", "{-suffix|/|bar}", "fee/fi/fo/fum/", "{-suffix|/|baz}", "", "{-suffix|/|qux}",
             ""};

        for (int n = 0; n < templates.length; n = n + 2) {
            assertEquals(templates[n + 1], Template.expand(templates[n], map));
        }
    }

    @Test
    public void test25() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", "fred");
        map.put("bar", new String[] {"fee", "fi", "fo", "fum"});
        map.put("baz", new String[0]);
        String[] templates =
            {"{-prefix|/|foo}", "/fred", "{-prefix|/|bar}", "/fee/fi/fo/fum", "{-prefix|/|baz}", "", "{-prefix|/|qux}",
             ""};

        for (int n = 0; n < templates.length; n = n + 2) {
            assertEquals(templates[n + 1], Template.expand(templates[n], map));
        }
    }

    @URITemplate("http://example.org/{foo}/{bar}{-opt|/|categories}{-list|/|categories}?{-join|&|baz,tag}")
    public static class MyObject {
        public String foo = "abc";

        public String getBar() {
            return "xyz";
        }

        public boolean isBaz() {
            return true;
        }

        public String[] getCategories() {
            return new String[] {"a", "b"};
        }

        public List<String> getTag() {
            return Arrays.asList(new String[] {"x", "y", "z"});
        }
    }

    private static void eval(String t, String e, HashMapContext c) {
        assertEquals(e, Template.expand(t, c));
    }
}
