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
package org.apache.abdera.protocol.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.cache.Cache;
import org.apache.abdera.protocol.client.cache.CacheDisposition;
import org.apache.abdera.protocol.client.cache.CachedResponse;
import org.apache.abdera.protocol.client.util.MethodHelper;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.util.Version;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 * The primary Abdera HTTP Client
 */
public class CommonsClient extends Client {

  private static final String DEFAULT_USER_AGENT = 
    Version.APP_NAME + "/" + Version.VERSION;
  
  private final HttpClient client;
  
  /**
   * Initialize the Commons Client using the default Abdera instance and User agent
   */
  public CommonsClient() {
    this(DEFAULT_USER_AGENT);
  }
  
  /**
   * Initialize the Commons Client using the specified Abdera instance and default user agent
   */
  public CommonsClient(Abdera abdera) {
    this(DEFAULT_USER_AGENT, abdera);
  }
  
  /**
   * Initialize the Commons Client using the default Abdera instance and specified user agent
   */
  public CommonsClient(String userAgent) {
    this(userAgent, new Abdera());
  }
  
  /**
   * Initialize the Commons Client using the specified Abdera instance and user agent
   */
  public CommonsClient(String userAgent,Abdera abdera) {
    super(abdera);
    MultiThreadedHttpConnectionManager connManager = 
      new MultiThreadedHttpConnectionManager();
    client = new HttpClient(connManager);
    client.getParams().setParameter(
      HttpClientParams.USER_AGENT, 
      userAgent);
    client.getParams().setBooleanParameter(
      HttpClientParams.USE_EXPECT_CONTINUE, true);  
    client.getParams().setCookiePolicy(
      CookiePolicy.BROWSER_COMPATIBILITY);
  }
  
  /**
   * Configure the client to use preemptive authentication (HTTP Basic Authentication only)
   */
  public void usePreemptiveAuthentication(boolean val) {
    client.getParams().setAuthenticationPreemptive(val);
  }
  
  private boolean useCache(
    String method, 
    RequestOptions options) {
      return (CacheControlUtil.isIdempotent(method)) &&
        !options.isNoCache() &&
        !options.isNoStore() &&
        options.getUseLocalCache();
  }
  
  private boolean mustRevalidate(RequestOptions options, CachedResponse response) {
    if (options.getRevalidateWithAuth()) {
      if (options.getAuthorization() != null) return true;
      if (client.getParams().getBooleanParameter(
        HttpClientParams.PREEMPTIVE_AUTHENTICATION, false)) return true;
      if (response != null) {
        if (response.isPublic()) return false;
      }
    }
    return false;
  }
  
  @Override
  public ClientResponse execute(
    String method, 
    String uri, 
    RequestEntity entity,
    RequestOptions options) {
      try {
        if (options == null) options = getDefaultRequestOptions();
        Cache cache = getCache();
        CacheDisposition disp = 
          (useCache(method,options)) ? 
            cache.getDisposition(uri, options) : 
            CacheDisposition.TRANSPARENT;
        CachedResponse cached_response = cache.get(uri, options);
        disp = (!disp.equals(CacheDisposition.TRANSPARENT) && 
                mustRevalidate(options, cached_response)) ? 
                  CacheDisposition.STALE : 
                  disp;
        switch(disp) {
          case FRESH:                                                            // CACHE HIT: FRESH
            if (cached_response != null)
              return cached_response;
          case STALE:                                                            // CACHE HIT: STALE
            // revalidate the cached entry
            if (cached_response != null && cached_response.getEntityTag() != null) {
              options.setIfModifiedSince(cached_response.getLastModified());
              options.setIfNoneMatch(cached_response.getEntityTag().toString());
            } else {
              disp = CacheDisposition.TRANSPARENT;
            }
          default:                                                               // CACHE MISS
            HttpMethod httpMethod = 
              MethodHelper.createMethod(
                method, uri, entity, options);
            client.executeMethod(httpMethod);
            ClientResponse response = new CommonsResponse(abdera,httpMethod);
            return (options.getUseLocalCache()) ?
              response = cache.update(options, response, cached_response) : 
              response;
        }
      } catch (Throwable t) {
        throw new ClientException(t);
      }
  }

  @Override
  public RequestOptions getDefaultRequestOptions() {
    return MethodHelper.createDefaultRequestOptions();
  }
  
  @Override
  public void addCredentials(
    String target,
    String realm,
    String scheme,
    Credentials credentials) 
      throws URISyntaxException {
    URI uri = new URI(target);
    AuthScope scope = 
      new AuthScope(
        uri.getHost(), 
        uri.getPort(), 
        realm, scheme);
    client.getState().setCredentials(
      scope, credentials);
  }

  public void setAuthenticationSchemePriority(String... scheme) {
    List authPrefs = java.util.Arrays.asList(scheme);
    client.getParams().setParameter(
      AuthPolicy.AUTH_SCHEME_PRIORITY, 
      authPrefs);
  }
  
  /**
   * <p>Per http://jakarta.apache.org/commons/httpclient/performance.html</p>
   * <blockquote>
   *   Generally it is recommended to have a single instance of HttpClient 
   *   per communication component or even per application. However, if the 
   *   application makes use of HttpClient only very infrequently, and keeping 
   *   an idle instance of HttpClient in memory is not warranted, it is highly 
   *   recommended to explicitly  shut down the multithreaded connection manager 
   *   prior to disposing the HttpClient instance. This will ensure proper 
   *   closure of all HTTP connections in the connection pool.
   * </blockquote>
   */
  public void teardown() {
    ((MultiThreadedHttpConnectionManager)
      client.getHttpConnectionManager()).shutdown();
  }
}
