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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.cache.CachedResponse;
import org.apache.abdera.test.client.JettyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These cache tests were originally based on Mark Nottingham's javascript cache tests, available at:
 * http://www.mnot.net/javascript/xmlhttprequest/cache.html They have since been modified to use an embedded Jetty
 * server instead of going off over the internet to hit Mark's server, since there are too many things that can get in
 * the way of those sort things (proxies, intermediate caches, etc) if you try to talk to a remote server.
 */
@SuppressWarnings("serial")
public class CacheTest {

    private static String CHECK_CACHE_INVALIDATE;
    private static String CHECK_NO_CACHE;
    // private static String CHECK_AUTH;
    private static String CHECK_MUST_REVALIDATE;

    public CacheTest() {
        String base = getBase();
        CHECK_CACHE_INVALIDATE = base + "/check_cache_invalidate";
        CHECK_NO_CACHE = base + "/no_cache";
        // CHECK_AUTH = base + "/auth";
        CHECK_MUST_REVALIDATE = base + "/must_revalidate";
    }

    protected static void getServletHandler(String... servletMappings) {
        for (int n = 0; n < servletMappings.length; n = n + 2) {
            String name = servletMappings[n];
            String root = servletMappings[n + 1];
            JettyUtil.addServlet(name, root);
        }
    }

    protected String getBase() {
        return "http://localhost:" + JettyUtil.getPort();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        getServletHandler();
        JettyUtil.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JettyUtil.stop();
    }

    protected static void getServletHandler() {
        getServletHandler("org.apache.abdera.test.client.cache.CacheTest$CheckCacheInvalidateServlet",
                          "/check_cache_invalidate",
                          "org.apache.abdera.test.client.cache.CacheTest$NoCacheServlet",
                          "/no_cache",
                          "org.apache.abdera.test.client.cache.CacheTest$AuthServlet",
                          "/auth",
                          "org.apache.abdera.test.client.cache.CacheTest$CheckMustRevalidateServlet",
                          "/must_revalidate");
    }

    public static class CheckMustRevalidateServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            int req = Integer.parseInt(reqnum);
            if (req == 1) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain");
                response.setHeader("Cache-Control", "must-revalidate");
                response.setDateHeader("Date", System.currentTimeMillis());
                response.getWriter().println(reqnum);
                response.getWriter().close();
            } else if (req == 2) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setContentType("text/plain");
                response.setDateHeader("Date", System.currentTimeMillis());
                return;
            } else if (req == 3) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setDateHeader("Date", System.currentTimeMillis());
                return;
            }
        }
    }

    public static class CheckCacheInvalidateServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setDateHeader("Date", System.currentTimeMillis());
            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "max-age=60");
            response.getWriter().println(reqnum);
            response.getWriter().close();
        }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        }

        protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        }

        protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        }
    }

    public static class NoCacheServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            int reqtest = Integer.parseInt(request.getHeader("X-Reqtest"));

            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);
            switch (reqtest) {
                case NOCACHE:
                    response.setHeader("Cache-Control", "no-cache");
                    break;
                case NOSTORE:
                    response.setHeader("Cache-Control", "no-store");
                    break;
                case MAXAGE0:
                    response.setHeader("Cache-Control", "max-age=0");
                    break;
            }
            response.setDateHeader("Date", System.currentTimeMillis());

            response.getWriter().println(reqnum);
            response.getWriter().close();
        }
    }

    public static class AuthServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
            String reqnum = request.getHeader("X-Reqnum");
            int num = Integer.parseInt(reqnum);
            switch (num) {
                case 1:
                    response.setStatus(HttpServletResponse.SC_OK);
                    break;
                case 2:
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_OK);
                    break;
            }
            response.setDateHeader("Date", System.currentTimeMillis());
            response.setContentType("text/plain");
            response.getWriter().println(reqnum);
            response.getWriter().close();
        }
    }

    private static final int NOCACHE = 0;
    private static final int NOSTORE = 1;
    private static final int MAXAGE0 = 2;
    private static final int POST = 3;
    private static final int DELETE = 4;
    private static final int PUT = 5;

    @Test
    public void testRequestNoStore() throws Exception {
        _requestCacheInvalidation(NOSTORE);
    }

    @Test
    public void testRequestNoCache() throws Exception {
        _requestCacheInvalidation(NOCACHE);
    }

    @Test
    public void testRequestMaxAge0() throws Exception {
        _requestCacheInvalidation(MAXAGE0);
    }

    @Test
    public void testResponseNoStore() throws Exception {
        _responseNoCache(NOSTORE);
    }

    @Test
    public void testResponseNoCache() throws Exception {
        _responseNoCache(NOCACHE);
    }

    @Test
    public void testResponseMaxAge0() throws Exception {
        _responseNoCache(MAXAGE0);
    }

    @Test
    public void testPostInvalidates() throws Exception {
        _methodInvalidates(POST);
    }

    @Test
    public void testPutInvalidates() throws Exception {
        _methodInvalidates(PUT);
    }

    @Test
    public void testDeleteInvalidates() throws Exception {
        _methodInvalidates(DELETE);
    }

    @Test
    public void testAuthForcesRevalidation() throws Exception {

        // TODO: Actually need to rethink this. Responses to authenticated requests
        // should never be cached unless the resource is explicitly marked as
        // being cacheable (e.g. using Cache-Control: public). So this test
        // was testing incorrect behavior.

        // AbderaClient client = new CommonsClient();
        // client.usePreemptiveAuthentication(true);
        // client.addCredentials(CHECK_AUTH, null, null, new UsernamePasswordCredentials("james","snell"));
        // RequestOptions options = client.getDefaultRequestOptions();
        // options.setHeader("Connection", "close");
        // options.setRevalidateWithAuth(true);
        // options.setHeader("x-reqnum", "1");
        // Response response = client.get(CHECK_AUTH, options);
        //  
        // // first request works as expected. fills the cache
        // String resp1 = getResponse(response);
        // assertEquals(resp1, "1");
        //
        // // second request uses authentication, should force revalidation of the cache
        // options.setHeader("x-reqnum", "2");
        // response = client.get(CHECK_AUTH, options);
        //  
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_UNAUTHORIZED);
        // assertEquals(resp1, "2");
        //
        // // third request does not use authentication, but since the previous request
        // // resulted in an "unauthorized" response, the cache needs to be refilled
        // options.setHeader("x-reqnum", "3");
        // client.usePreemptiveAuthentication(false);
        // response = client.get(CHECK_AUTH, options);
        //  
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        // assertEquals(resp1, "3");
        //
        // // fourth request does not use authentication, will pull from the cache
        // options = client.getDefaultRequestOptions();
        // options.setHeader("x-reqnum", "4");
        // client.usePreemptiveAuthentication(false);
        // response = client.get(CHECK_AUTH, options);
        //  
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        // assertEquals(resp1, "3");
        //    
        // // fifth request uses authentication, will force revalidation
        // options.setAuthorization("Basic amFtZXM6c25lbGw=");
        // options.setHeader("x-reqnum", "5");
        // response = client.get(CHECK_AUTH, options);
        //    
        // resp1 = getResponse(response);
        // assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        // assertEquals(resp1, "5");
    }

    @Test
    public void testResponseMustRevalidate() throws Exception {
        AbderaClient abderaClient = new AbderaClient();
        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setHeader("Connection", "close");
        options.setHeader("x-reqnum", "1");
        ClientResponse response = abderaClient.get(CHECK_MUST_REVALIDATE, options);

        String resp1 = getResponse(response);
        assertEquals("1", resp1);

        // Should be revalidated and use the cache
        options.setHeader("x-reqnum", "2");
        response = abderaClient.get(CHECK_MUST_REVALIDATE, options);
        assertTrue(response instanceof CachedResponse);

        String resp2 = getResponse(response);
        assertEquals("1", resp2);

        // Should be revalidated and return a 404
        options.setHeader("x-reqnum", "3");
        response = abderaClient.get(CHECK_MUST_REVALIDATE, options);
        assertEquals(404, response.getStatus());
        response.release();

    }

    private RequestOptions getRequestOptions(AbderaClient client, int num) {
        RequestOptions options = client.getDefaultRequestOptions();
        options.setHeader("Connection", "close");
        options.setHeader("x-reqnum", String.valueOf(num));
        options.setUseExpectContinue(false);
        return options;
    }

    private void _methodInvalidates(int type) throws Exception {

        AbderaClient abderaClient = new AbderaClient();
        RequestOptions options = getRequestOptions(abderaClient, 1);
        ClientResponse response = abderaClient.get(CHECK_CACHE_INVALIDATE, options);

        String resp1 = getResponse(response);

        response.release();
        assertEquals("1", resp1);

        // calling a method that could change state on the server should invalidate the cache
        options = getRequestOptions(abderaClient, 2);
        switch (type) {
            case POST:
                response = abderaClient.post(CHECK_CACHE_INVALIDATE, new ByteArrayInputStream("".getBytes()), options);
                break;
            case PUT:
                response = abderaClient.put(CHECK_CACHE_INVALIDATE, new ByteArrayInputStream("".getBytes()), options);
                break;
            case DELETE:
                response = abderaClient.delete(CHECK_CACHE_INVALIDATE, options);
                break;
        }
        response.release();

        options = getRequestOptions(abderaClient, 3);
        response = abderaClient.get(CHECK_CACHE_INVALIDATE, options);

        resp1 = getResponse(response);
        response.release();
        assertEquals("3", resp1);
    }

    private void _requestCacheInvalidation(int type) throws Exception {

        AbderaClient abderaClient = new AbderaClient();
        RequestOptions options = getRequestOptions(abderaClient, 1);
        ClientResponse response = abderaClient.get(CHECK_CACHE_INVALIDATE, options);
        String resp1 = getResponse(response);
        assertEquals("1", resp1);

        // Should not use the cache
        options = getRequestOptions(abderaClient, 2);
        switch (type) {
            case NOCACHE:
                options.setNoCache(true);
                break;
            case NOSTORE:
                options.setNoStore(true);
                break;
            case MAXAGE0:
                options.setMaxAge(0);
                break;
        }
        response = abderaClient.get(CHECK_CACHE_INVALIDATE, options);

        String resp2 = getResponse(response);
        assertEquals("2", resp2);

        // Should use the cache
        options = getRequestOptions(abderaClient, 3);
        switch (type) {
            case NOCACHE:
                options.setNoCache(false);
                break;
            case NOSTORE:
                options.setNoStore(false);
                break;
            case MAXAGE0:
                options.setMaxAge(60);
                break;
        }
        response = abderaClient.get(CHECK_CACHE_INVALIDATE, options);

        String resp3 = getResponse(response);
        assertEquals("2", resp3);
    }

    private void _responseNoCache(int type) throws Exception {

        AbderaClient abderaClient = new AbderaClient();
        RequestOptions options = getRequestOptions(abderaClient, 1);
        options.setHeader("x-reqtest", String.valueOf(type));
        ClientResponse response = abderaClient.get(CHECK_NO_CACHE, options);

        String resp1 = getResponse(response);
        assertEquals("1", resp1);

        // Should not use the cache

        options = getRequestOptions(abderaClient, 2);
        options.setHeader("x-reqtest", String.valueOf(type));
        response = abderaClient.get(CHECK_NO_CACHE, options);

        String resp2 = getResponse(response);
        assertEquals("2", resp2);

        // Should use the cache
        options = getRequestOptions(abderaClient, 3);
        options.setHeader("x-reqtest", String.valueOf(type));
        response = abderaClient.get(CHECK_NO_CACHE, options);

        String resp3 = getResponse(response);
        assertEquals("3", resp3);
    }

    private static String getResponse(ClientResponse response) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int m = -1;
        InputStream in = response.getInputStream();
        while ((m = in.read()) != -1) {
            out.write(m);
        }
        in.close();
        String resp = new String(out.toByteArray());
        return resp.trim();
    }

    @Test
    public void testInitCache() {
        AbderaClient client = new AbderaClient();
        assertNotNull(client.getCache());
    }
}
