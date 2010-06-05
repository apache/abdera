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
package org.apache.abdera.protocol.server.test.route;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.Route;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.protocol.server.impl.RouteManager.RouteTarget;
import org.junit.Test;

public class RouteTest {
    @Test
    public void testSimpleRoute() throws Exception {
        Route route = new Route("feed", "/:collection");

        HashMapContext ctx = new HashMapContext();
        ctx.put("collection", "test");
        assertEquals("/test", route.expand(ctx));

        assertTrue(route.match("/foo"));
        assertFalse(route.match("/foo/test"));
        assertFalse(route.match("foo"));

        Map<String, String> vars = route.parse("/test");
        assertEquals("test", vars.get("collection"));
    }

    @Test
    public void testStaticRoute() throws Exception {
        Route route = new Route("feed", "/feed");

        HashMapContext ctx = new HashMapContext();
        assertEquals("/feed", route.expand(ctx));

        assertTrue(route.match("/feed"));
        assertFalse(route.match("/feed/test"));
        assertFalse(route.match("feed"));

        Map<String, String> vars = route.parse("/test");
        assertEquals(0, vars.size());
    }

    @Test
    public void testTwoPathRoute() throws Exception {
        Route route = new Route("entry", "/:collection/:entry");

        HashMapContext ctx = new HashMapContext();
        ctx.put("collection", "c");
        ctx.put("entry", "e");
        assertEquals("/c/e", route.expand(ctx));

        assertFalse(route.match("/foo"));
        assertTrue(route.match("/foo/test"));
        assertFalse(route.match("foo"));
        assertFalse(route.match("/foo/test/bar"));

        Map<String, String> vars = route.parse("/1/2");
        assertEquals("1", vars.get("collection"));
        assertEquals("2", vars.get("entry"));

        vars = route.parse("/1/");
        assertEquals("1", vars.get("collection"));
        assertNull(vars.get("entry"));
    }

    @Test
    public void testTwoPathRouteWithSubstitution() throws Exception {
        Map<String, String> defaults = new HashMap<String, String>();
        defaults.put("collection", "c");
        Route route = new Route("entry", "/:collection/:entry", defaults, null);

        HashMapContext ctx = new HashMapContext();
        ctx.put("entry", "e");
        assertEquals("/c/e", route.expand(ctx));
    }

    @Test
    public void testDashedRoute() throws Exception {
        Route route = new Route("entry", ":collection/:entry-:foo");

        HashMapContext ctx = new HashMapContext();
        ctx.put("collection", "c");
        ctx.put("entry", "e");
        ctx.put("foo", "f");
        assertEquals("c/e-f", route.expand(ctx));

        assertTrue(route.match("1/2-3"));
        assertFalse(route.match("1/2-"));
        assertFalse(route.match("1/-"));
    }

    @Test
    public void testDashedRouteWithSubstitution() throws Exception {
        Map<String, String> defaults = new HashMap<String, String>();
        defaults.put("foo", "f");
        Route route = new Route("entry", ":collection/:entry-:foo", defaults, null);

        HashMapContext ctx = new HashMapContext();
        ctx.put("collection", "c");
        ctx.put("entry", "e");
        assertEquals("c/e-f", route.expand(ctx));

        assertTrue(route.match("1/2-3"));
        assertFalse(route.match("1/2-"));
        assertFalse(route.match("1/-"));
    }

    @Test
    public void testBaseURI() throws Exception {
        Route route = new Route("entry", "/base/:collection/:entry");

        assertTrue(route.match("/base/test/123"));
        assertFalse(route.match("/base/test"));
        assertFalse(route.match("base/test"));

        Map<String, String> vars = route.parse("/base/1/2");
        assertEquals("1", vars.get("collection"));
        assertEquals("2", vars.get("entry"));
    }

    @Test
    public void testNonVariablesAtBothEnds() throws Exception {
        Route route = new Route("entry", "/base/:collection/:entry;categories");

        assertTrue(route.match("/base/test/123;categories"));
        assertFalse(route.match("/base/test/123"));

        Map<String, String> vars = route.parse("/base/1/2;categories");
        assertEquals("1", vars.get("collection"));
        assertEquals("2", vars.get("entry"));
    }

    @Test
    public void testSubDelims() throws Exception {
        Route route = new Route("entry", "/base/:collection/:entry");

        assertTrue(route.match("/base/test/123"));
        assertFalse(route.match("/base/test/123;categories"));

        Map<String, String> vars = route.parse("/base/test/123");
        assertEquals("test", vars.get("collection"));
        assertEquals("123", vars.get("entry"));
    }

    @Test
    public void testGenDelims() throws Exception {
        Route route = new Route("entry", "/base/:collection/");

        assertTrue(route.match("/base/test/"));
        assertFalse(route.match("/base/test/123/"));

        Map<String, String> vars = route.parse("/base/test/");
        assertEquals("test", vars.get("collection"));
    }

    @Test
    @SuppressWarnings("serial")
    public void testUrlFor() throws Exception {
        Map<String, String> context = new HashMap<String, String>() {
            {
                put("entry", "123");
            }
        };
        runTestUrlFor("/app/base/123", context);
    }

    @Test
    @SuppressWarnings("serial")
    public void testUrlForSupportsParamsWithColon() throws Exception {
        Map<String, String> context = new HashMap<String, String>() {
            {
                put(":entry", null);
            }
        };
        runTestUrlFor("/app/base/", context);
    }

    @Test
    @SuppressWarnings("serial")
    public void testUrlForEmptyContext() throws Exception {
        runTestUrlFor("/app/base/", null);
    }

    private void runTestUrlFor(String expected, Map<String, String> context) {
        RouteManager manager = new RouteManager().addRoute(new Route("entry", "/base/:entry"));

        RequestContext request = createMock(RequestContext.class);
        expect(request.getContextPath()).andReturn("/app");
        replay(request);

        assertEquals(expected, manager.urlFor(request, "entry", context));
    }

    @Test
    public void testStaticResolve() throws Exception {
        RouteManager manager = new RouteManager();
        manager.addRoute(new Route("collection", "/:coll"), TargetType.TYPE_COLLECTION)
            .addRoute(new Route("openSearch", "/openSearch.xml"), TargetType.TYPE_ENTRY);

        RequestContext request = createMock(RequestContext.class);
        expect(request.getTargetPath()).andReturn("/openSearch.xml");
        replay(request);

        RouteTarget target = (RouteTarget)manager.resolve(request);
        assertEquals("/openSearch.xml", target.getRoute().getPattern());
    }

    @Test
    public void testRouteRequirementsMatches() {
        Route route = getRouteWithRequirements();
        assertTrue(route.match("/base/collection/1234"));
    }

    @Test
    public void testRouteRequirementsNotMatch() throws Exception {
        Route route = getRouteWithRequirements();
        assertFalse(route.match("/base/collection/entry"));
    }

    @SuppressWarnings("serial")
    private Route getRouteWithRequirements() {
        Map<String, String> requirements = new HashMap<String, String>() {
            {
                put("entry", "\\d+");
            }
        };
        return new Route("entry", "/base/:collection/:entry", null, requirements);
    }
}
