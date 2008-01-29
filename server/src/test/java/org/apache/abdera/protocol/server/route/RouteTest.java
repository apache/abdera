package org.apache.abdera.protocol.server.route;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.Route;
import org.junit.Test;

import junit.framework.Assert;

public class RouteTest extends Assert {
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

    Map<String, String> vars = route.parse("/1/2");
    assertEquals("1", vars.get("collection"));
    assertEquals("2", vars.get("entry"));
    
    vars = route.parse("/1/");
    assertEquals("1", vars.get("collection"));
    assertNull(vars.get("entry"));
  }

  @Test
  public void testTwoPathRouteWithSubstitution() throws Exception {
    Map<String, String> defaults = new HashMap<String,String>();
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
    Map<String, String> defaults = new HashMap<String,String>();
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
}
