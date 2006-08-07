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

import org.apache.abdera.protocol.client.Client;
import org.apache.abdera.protocol.client.CommonsClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.Response;

import junit.framework.TestCase;

/**
 * For now these are hitting Mark Nottingham's set of caching tests.  We'll
 * need to set up our own server.  The hard part about testing caching is
 * that we need a server endpoint to test against.  Also, the test results 
 * can be affected by proxies and intermediate caches.  If we want to run
 * these tests in an offline configuration, we'll need to provide instructions
 * on how to set up a local server and have a portable suite of tests. 
 */
public class CacheTests extends TestCase {
  
  private final static String CHECK_CACHE_INVALIDATE = 
    "http://www.mnot.net/javascript/xmlhttprequest/check_cache_invalidate.s";
  
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
