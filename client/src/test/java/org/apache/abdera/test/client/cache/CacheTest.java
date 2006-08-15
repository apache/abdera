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
package org.apache.abdera.test.client.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.abdera.protocol.cache.CachedResponse;
import org.apache.abdera.protocol.client.Client;
import org.apache.abdera.protocol.client.CommonsClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.Response;
import org.apache.abdera.test.client.JettyTest;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.mortbay.jetty.servlet.ServletHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 * These cache tests were originally based on Mark Nottingham's javascript
 * cache tests, available at:
 * 
 *   http://www.mnot.net/javascript/xmlhttprequest/cache.html
 * 
 * They have since been modified to use an embedded Jetty server instead of
 * going off over the internet to hit Mark's server, since there are too many
 * things that can get in the way of those sort things (proxies, intermediate
 * caches, etc) if you try to talk to a remote server.
 */
@SuppressWarnings("serial")
public class CacheTest extends JettyTest {

  private static ServletHandler handler = 
    JettyTest.getServletHandler(
      "org.apache.abdera.test.client.cache.CacheTest$CheckCacheInvalidateServlet","/check_cache_invalidate",
      "org.apache.abdera.test.client.cache.CacheTest$NoCacheServlet", "/no_cache",
      "org.apache.abdera.test.client.cache.CacheTest$AuthServlet", "/auth",
      "org.apache.abdera.test.client.cache.CacheTest$CheckMustRevalidateServlet", "/must_revalidate"
    );
  
  private static String CHECK_CACHE_INVALIDATE;
  private static String CHECK_NO_CACHE;
  private static String CHECK_AUTH;
  private static String CHECK_MUST_REVALIDATE;
  
  public CacheTest() {
    super(11);
    String base = getBase();
    CHECK_CACHE_INVALIDATE = base + "/check_cache_invalidate";
    CHECK_NO_CACHE = base + "/no_cache";
    CHECK_AUTH = base + "/auth";
    CHECK_MUST_REVALIDATE = base + "/must_revalidate";
  }
  
  protected ServletHandler getServletHandler() {
    return CacheTest.handler;
  }
  
  public static class CheckMustRevalidateServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response)
          throws ServletException, IOException
      {
      String reqnum = request.getHeader("X-Reqnum");
      int req = Integer.parseInt(reqnum);
      response.setContentType("text/plain");
      if (req == 1) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Cache-Control", "must-revalidate");
        response.setDateHeader("Date", System.currentTimeMillis());      
        response.getWriter().println(reqnum);
      } else if (req == 2) {
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        response.setDateHeader("Date", System.currentTimeMillis());
        return;
      } else if (req == 3) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        response.setDateHeader("Date", System.currentTimeMillis());
        return;
      }
      }
  }

  public static class CheckCacheInvalidateServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
      throws ServletException, IOException
    {
      String reqnum = request.getHeader("X-Reqnum");

      response.setContentType("text/plain");
      response.setStatus(HttpServletResponse.SC_OK);
      response.setHeader("Cache-Control", "max-age=60");
      response.setDateHeader("Date", System.currentTimeMillis());

      response.getWriter().println(reqnum);
    }
  }

  public static class NoCacheServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
      throws ServletException, IOException
    {
      String reqnum = request.getHeader("X-Reqnum");
      int reqtest = Integer.parseInt(request.getHeader("X-Reqtest"));

      response.setContentType("text/plain");
      response.setStatus(HttpServletResponse.SC_OK);
      switch(reqtest) {
        case NOCACHE: response.setHeader("Cache-Control", "no-cache"); break;
        case NOSTORE: response.setHeader("Cache-Control", "no-store"); break;
        case MAXAGE0: response.setHeader("Cache-Control", "max-age=0"); break;
      }
      response.setDateHeader("Date", System.currentTimeMillis());

      response.getWriter().println(reqnum);
    }
  }
  
  public static class AuthServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
      throws ServletException, IOException
    {
      String reqnum = request.getHeader("X-Reqnum");
      int num = Integer.parseInt(reqnum);
      response.setContentType("text/plain");
      switch (num) {
        case 1: response.setStatus(HttpServletResponse.SC_OK); break;
        case 2: response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); break;
        default:
          response.setStatus(HttpServletResponse.SC_OK); break;
      }
      response.setDateHeader("Date", System.currentTimeMillis());
      response.getWriter().println(reqnum);
    }
  }
  
  private static final int NOCACHE = 0;
  private static final int NOSTORE = 1;
  private static final int MAXAGE0 = 2;
  private static final int POST = 3;
  private static final int DELETE = 4;
  private static final int PUT = 5;
  
  public static void testRequestNoStore() throws Exception {
    _requestCacheInvalidation(NOSTORE);
  }

  public static void testRequestNoCache() throws Exception {
    _requestCacheInvalidation(NOCACHE);    
  }
  
  public static void testRequestMaxAge0() throws Exception {
    _requestCacheInvalidation(MAXAGE0);
  }

  public static void testResponseNoStore() throws Exception {
    _responseNoCache(NOSTORE);
  }

  public static void testResponseNoCache() throws Exception {
    _responseNoCache(NOCACHE);
  }
  
  public static void testResponseMaxAge0() throws Exception {
    _responseNoCache(MAXAGE0);
  }
  
  public static void testPostInvalidates() throws Exception {
    _methodInvalidates(POST);
  }

  public static void testPutInvalidates() throws Exception {
    _methodInvalidates(PUT);
  }
  
  public static void testDeleteInvalidates() throws Exception {
    _methodInvalidates(DELETE);
  }
  
  public static void testAuthForcesRevalidation() throws Exception {
    
    // the revalidatewithauth mechanism allows us to revalidate the 
    // cache when authentication is used, meaning that we'll only
    // be served data from the cache if our authentication credentials
    // are valid.
    
    // unfortunately, this only works for preemptive auth and auth using
    // the RequestOptions.setAuthorization method.  I'm not quite sure
    // yet how to plug into httpclient's built in auth mechanisms to 
    // ensure we can invalidate the cache.    
    
    Client client = new CommonsClient();
    client.usePreemptiveAuthentication(true);
    client.addCredentials(CHECK_AUTH, null, null, new UsernamePasswordCredentials("james","snell"));
    RequestOptions options = client.getDefaultRequestOptions();
    options.setRevalidateWithAuth(true);
    options.setHeader("x-reqnum", "1");
    Response response = client.get(CHECK_AUTH, options);
  
    // first request works as expected. fills the cache
    String resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "1");

    // second request uses authentication, should force revalidation of the cache
    options.setHeader("x-reqnum", "2");
    response = client.get(CHECK_AUTH, options);
  
    resp1 = getResponse(response.getInputStream());
    assertEquals(response.getStatus(), HttpServletResponse.SC_UNAUTHORIZED);
    assertEquals(resp1, "2");

    // third request does not use authentication, but since the previous request
    // resulted in an "unauthorized" response, the cache needs to be refilled
    options.setHeader("x-reqnum", "3");
    client.usePreemptiveAuthentication(false);
    response = client.get(CHECK_AUTH, options);
  
    resp1 = getResponse(response.getInputStream());
    assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    assertEquals(resp1, "3");  

    // fourth request does not use authentication, will pull from the cache
    options.setHeader("x-reqnum", "4");
    response = client.get(CHECK_AUTH, options);
  
    resp1 = getResponse(response.getInputStream());
    assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    assertEquals(resp1, "3");  
    
    // fifth request uses authentication, will force revalidation
    options.setAuthorization("Basic amFtZXM6c25lbGw=");
    options.setHeader("x-reqnum", "5");
    response = client.get(CHECK_AUTH, options);
    
    resp1 = getResponse(response.getInputStream());
    assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    assertEquals(resp1, "5");
  }
  
  public static void testResponseMustRevalidate() throws Exception {
    Client client = new CommonsClient();
    RequestOptions options = client.getDefaultRequestOptions();
    options.setHeader("x-reqnum", "1");
    Response response = client.get(CHECK_MUST_REVALIDATE, options);
  
    String resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "1");
    
    // Should be revalidated and use the cache
    options.setHeader("x-reqnum", "2");
    response = client.get(CHECK_MUST_REVALIDATE, options);
    assertTrue(response instanceof CachedResponse);
    
    String resp2 = getResponse(response.getInputStream());
    assertEquals(resp2, "1");
    
    // Should be revalidated and return a 404
    options.setHeader("x-reqnum", "3");
    response = client.get(CHECK_MUST_REVALIDATE, options);  
    assertEquals(response.getStatus(), 404);

  }
  
  private static void _methodInvalidates(int type) throws Exception {
    
    Client client = new CommonsClient();
    RequestOptions options = client.getDefaultRequestOptions();
    options.setHeader("x-reqnum", "1");
    Response response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "1");
    
    // calling a method that could change state on the server should invalidate the cache
    options.setHeader("x-reqnum", "2");
    switch(type) {
      case POST:  
        client.post(
          CHECK_CACHE_INVALIDATE, 
          new ByteArrayInputStream("".getBytes()), 
          options);
        break;
      case PUT:
        client.put(
          CHECK_CACHE_INVALIDATE, 
          new ByteArrayInputStream("".getBytes()), 
          options);
        break;
      case DELETE:
        client.delete(
          CHECK_CACHE_INVALIDATE, 
          options);
        break;
    }
    
    options.setHeader("x-reqnum", "3");
    response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "3");
  }
  
  private static void _requestCacheInvalidation(int type) throws Exception {
    
    Client client = new CommonsClient();
    RequestOptions options = client.getDefaultRequestOptions();
    options.setHeader("x-reqnum", "1");
    Response response = client.get(CHECK_CACHE_INVALIDATE, options);  
    String resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "1");
    
    // Should not use the cache
    options.setHeader("x-reqnum", "2");
    switch(type) {
      case NOCACHE: options.setNoCache(true); break;
      case NOSTORE: options.setNoStore(true); break;
      case MAXAGE0: options.setMaxAge(0); break;
    }
    response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp2 = getResponse(response.getInputStream());
    assertEquals(resp2, "2");
    
    // Should use the cache
    options.setHeader("x-reqnum", "3");
    switch(type) {
      case NOCACHE: options.setNoCache(false); break;
      case NOSTORE: options.setNoStore(false); break;
      case MAXAGE0: options.setMaxAge(60); break;
    }
    response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp3 = getResponse(response.getInputStream());
    assertEquals(resp3, "2");
  }
  
  private static void _responseNoCache(int type) throws Exception {
    
    Client client = new CommonsClient();
    RequestOptions options = client.getDefaultRequestOptions();
    options.setHeader("x-reqnum", "1");
    options.setHeader("x-reqtest", String.valueOf(type));
    Response response = client.get(CHECK_NO_CACHE, options);
  
    String resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "1");
    
    // Should not use the cache
    options.setHeader("x-reqnum", "2");
    response = client.get(CHECK_NO_CACHE, options);
  
    String resp2 = getResponse(response.getInputStream());
    assertEquals(resp2, "2");
    
    // Should use the cache
    options.setHeader("x-reqnum", "3");
    response = client.get(CHECK_NO_CACHE, options);
  
    String resp3 = getResponse(response.getInputStream());
    assertEquals(resp3, "3");
  }
  
  private static String getResponse(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int m = -1;
    while ((m = in.read()) != -1) {
      out.write(m);
    }
    String resp = new String(out.toByteArray());
    return resp.trim();
  }
}
