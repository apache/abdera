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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;

import org.apache.abdera.protocol.client.Client;
import org.apache.abdera.protocol.client.CommonsClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.Response;

import junit.framework.TestCase;

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
public class CacheTest extends TestCase {

  private static final String PORT_PROP = "abdera.test.client.cache.port";
  private static String CHECK_CACHE_INVALIDATE;
  private static int PORT;
  
  private static Server server;

  private static int NUM_TESTS = 3;
  private static int testsRun  = 0;

  static {
    if (System.getProperty(PORT_PROP) != null) {
      PORT = Integer.parseInt(System.getProperty(PORT_PROP));  
    } else {
      PORT = 8080;
    }
    
    CHECK_CACHE_INVALIDATE = "http://localhost:" + PORT + "/";

    server = new Server();

    Connector connector = new SocketConnector();

    connector.setPort(PORT);

    server.setConnectors(new Connector[]{connector});

    ServletHandler handler = new ServletHandler();

    server.setHandler(handler);

    handler.addServletWithMapping(
      "org.apache.abdera.test.client.cache.CacheTest$Servlet",
      "/"
    );

    try {
      server.start();
    } catch (Exception e) {
      // Nothing...
    }
  }

  public void tearDown() throws Exception {
    if (++testsRun == NUM_TESTS)
      server.stop();
  }

  public static class Servlet extends HttpServlet {
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

  public static void testRequestNoStore() throws Exception {
    
      Client client = new CommonsClient();
      RequestOptions options = client.getDefaultRequestOptions();
      options.setHeader("x-reqnum", "1");
      Response response = client.get(CHECK_CACHE_INVALIDATE, options);
    
      String resp1 = getResponse(response.getInputStream());
      assertEquals(resp1, "1");
      
      // Should not use the cache
      options.setHeader("x-reqnum", "2");
      options.setNoStore(true);
      response = client.get(CHECK_CACHE_INVALIDATE, options);
    
      String resp2 = getResponse(response.getInputStream());
      assertEquals(resp2, "2");
      
      // Should use the cache
      options.setHeader("x-reqnum", "3");
      options.setNoStore(false);
      response = client.get(CHECK_CACHE_INVALIDATE, options);
    
      String resp3 = getResponse(response.getInputStream());
      assertEquals(resp3, "2");
  }

  public static void testRequestNoCache() throws Exception {
    
    Client client = new CommonsClient();
    RequestOptions options = client.getDefaultRequestOptions();
    options.setHeader("x-reqnum", "1");
    Response response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "1");
    
    // Should not use the cache
    options.setHeader("x-reqnum", "2");
    options.setNoCache(true);
    response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp2 = getResponse(response.getInputStream());
    assertEquals(resp2, "2");
    
    // Should use the cache
    options.setHeader("x-reqnum", "3");
    options.setNoCache(false);
    response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp3 = getResponse(response.getInputStream());
    assertEquals(resp3, "2");
  }
  
  public static void testRequestMaxAge() throws Exception {
    
    Client client = new CommonsClient();
    RequestOptions options = client.getDefaultRequestOptions();
    options.setHeader("x-reqnum", "1");
    Response response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp1 = getResponse(response.getInputStream());
    assertEquals(resp1, "1");
    
    // Should not use the cache
    options.setHeader("x-reqnum", "2");
    options.setMaxAge(0);
    response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp2 = getResponse(response.getInputStream());
    assertEquals(resp2, "2");
    
    // Should use the cache
    options.setHeader("x-reqnum", "3");
    options.setMaxAge(60);
    response = client.get(CHECK_CACHE_INVALIDATE, options);
  
    String resp3 = getResponse(response.getInputStream());
    assertEquals(resp3, "2");
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
