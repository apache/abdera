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

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.net.ssl.TrustManager;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.cache.Cache;
import org.apache.abdera.protocol.client.cache.CacheDisposition;
import org.apache.abdera.protocol.client.cache.CacheFactory;
import org.apache.abdera.protocol.client.cache.CachedResponse;
import org.apache.abdera.protocol.client.cache.lru.LRUCache;
import org.apache.abdera.protocol.client.util.BaseRequestEntity;
import org.apache.abdera.protocol.client.util.MethodHelper;
import org.apache.abdera.protocol.client.util.SimpleSSLProtocolSocketFactory;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.util.ServiceUtil;
import org.apache.abdera.util.Version;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/**
 * An Atom Publishing Protocol client.
 */
public class AbderaClient {

  public static final String DEFAULT_USER_AGENT = 
    Version.APP_NAME + "/" + Version.VERSION;
  
  protected final Abdera abdera;
  protected final Cache cache;
  private final HttpClient client;

  public AbderaClient() {
    this(new Abdera(),DEFAULT_USER_AGENT);
  }
  
  public AbderaClient(String useragent) {
    this(new Abdera(), useragent);
  }
  
  public AbderaClient(Abdera abdera, String useragent) {
    this.abdera = abdera;
    this.cache = initCache(initCacheFactory());
    MultiThreadedHttpConnectionManager connManager = 
      new MultiThreadedHttpConnectionManager();
    client = new HttpClient(connManager);
    client.getParams().setParameter(
      HttpClientParams.USER_AGENT, 
      useragent);
    client.getParams().setBooleanParameter(
      HttpClientParams.USE_EXPECT_CONTINUE, true);  
    client.getParams().setCookiePolicy(
      CookiePolicy.BROWSER_COMPATIBILITY);
    setAuthenticationSchemeDefaults();
  }
  
  public AbderaClient(Abdera abdera) {
    this(abdera,DEFAULT_USER_AGENT);
  }
    
  private CacheFactory initCacheFactory() {
    CacheFactory cacheFactory = 
      (CacheFactory)ServiceUtil.newInstance(
        "org.apache.abdera.protocol.cache.CacheFactory",
        "org.apache.abdera.protocol.cache.lru.LRUCacheFactory", 
        abdera);
    return cacheFactory;
  }
  
  public Cache getCache() {
    return cache;
  }
  
  public Cache initCache(CacheFactory factory) {
    Cache cache = null;
    if (factory != null) cache = factory.getCache(abdera);
    return (cache != null) ? cache : new LRUCache(abdera);
  }
  
  public ClientResponse head(
    String uri, 
    RequestOptions options) {
      return execute("HEAD", uri, (RequestEntity)null, options);
  }
  
  public ClientResponse get(
    String uri, 
    RequestOptions options) {
      return execute("GET", uri, (RequestEntity)null, options);
  }
  
  public ClientResponse post(
    String uri, 
    RequestEntity entity, 
    RequestOptions options) {
      return execute("POST", uri, entity, options);
  }

  public ClientResponse post(
    String uri, 
    InputStream in, 
    RequestOptions options) {
      return execute("POST", uri, new InputStreamRequestEntity(in), options);
  }

  public ClientResponse post(
    String uri, 
    Base base, 
    RequestOptions options) {
      if (base instanceof Document) {
        Document d = (Document) base;
        if (options.getSlug() == null && 
            d.getSlug() != null) 
          options.setSlug(d.getSlug());
      }
      return execute("POST", uri, new BaseRequestEntity(base, options.isUseChunked()), options);
  }
    
  public ClientResponse put(
    String uri, 
    RequestEntity entity, 
    RequestOptions options) {
      return execute("PUT", uri, entity, options);
  }

  public ClientResponse put(
    String uri, 
    InputStream in, 
    RequestOptions options) {
      return execute("PUT", uri, new InputStreamRequestEntity(in), options);
  }

  public ClientResponse put(
      String uri, 
      Base base, 
      RequestOptions options) {
    if (base instanceof Document) {
      Document d = (Document) base;
      if (options.getSlug() == null && 
          d.getSlug() != null) 
        options.setSlug(d.getSlug());
    }
    return execute("PUT", uri, new BaseRequestEntity(base, options.isUseChunked()), options);
  }
      
  public ClientResponse delete(
    String uri, 
    RequestOptions options) {
      return execute("DELETE", uri, (RequestEntity)null, options);
  }
  
  public ClientResponse head(String uri) {
    return head(uri, getDefaultRequestOptions());
  }
  
  public ClientResponse get(String uri) {
    return get(uri, getDefaultRequestOptions());
  }
    
  public ClientResponse post(
    String uri, 
    RequestEntity entity) {
    return post(uri, entity, getDefaultRequestOptions());
  }
  
  public ClientResponse post(
    String uri, 
    InputStream in) {
      return post(uri, in, getDefaultRequestOptions());
  }
  
  public ClientResponse post(
    String uri, 
    Base base) {
      return post(uri, base, getDefaultRequestOptions());
  }

  public ClientResponse put(
    String uri, 
    RequestEntity entity) {
      return put(uri, entity, getDefaultRequestOptions());
  }
  
  public ClientResponse put(
    String uri, 
    InputStream in) {
      return put(uri, in, getDefaultRequestOptions());
  }
  
  public ClientResponse put(
    String uri, 
    Base base) {
      return put(uri, base, getDefaultRequestOptions());
  }

  public ClientResponse delete(
    String uri) {
      return delete(uri, getDefaultRequestOptions());
  }
  
  /**
   * Register a new authentication scheme.
   * 
   * @param name
   * @param scheme
   */
  public static <T extends AuthScheme>void registerScheme(
    String name, 
    Class<T> scheme) {
      AuthPolicy.registerAuthScheme(name, scheme);
  }
  
  public static void registerTrustManager(
    TrustManager trustManager) {
      registerTrustManager(trustManager,443);
  }
  
  public static void registerTrustManager() {
    registerTrustManager(443);
  }
  
  public static void registerTrustManager(
    TrustManager trustManager, 
    int port) {
      SimpleSSLProtocolSocketFactory f = 
        new SimpleSSLProtocolSocketFactory(trustManager);
      registerFactory(f,port);
  }
  
  public static void registerTrustManager(int port) {
    SimpleSSLProtocolSocketFactory f = 
      new SimpleSSLProtocolSocketFactory();
    registerFactory(f,port);
  }
  
  public static void registerFactory(
    SecureProtocolSocketFactory factory, 
    int port) {
      Protocol.registerProtocol(
        "https",
        new Protocol(
          "https", 
          (ProtocolSocketFactory)factory, port));
  }
  
  private static void registerFactory(
    SimpleSSLProtocolSocketFactory factory, 
    int port) {
      Protocol.registerProtocol(
        "https", 
        new Protocol(
          "https", 
          (ProtocolSocketFactory)factory, port));
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
  
  public ClientResponse execute(
    String method,
    String uri,
    Base base,
    RequestOptions options) {
      RequestEntity re = new BaseRequestEntity(base);
      return execute(method,uri,re,options);
  }
    
  public ClientResponse execute(
    String method,
    String uri,
    InputStream in,
    RequestOptions options) {
      RequestEntity re = null;
      try {
        if (options.getContentType() != null) { 
          re = new InputStreamRequestEntity(
            in, options.getContentType().toString());
        } else {
          re = new InputStreamRequestEntity(in);
        }
      } catch (Exception e) {
        re = new InputStreamRequestEntity(in);
      }
      return execute(method,uri,re,options);
  }
  
  private CacheDisposition getCacheDisposition(
    boolean usecache, 
    String uri, 
    RequestOptions options,
    CachedResponse cached_response) {
    CacheDisposition disp = 
      (usecache) ? 
        cache.getDisposition(uri, options) : 
        CacheDisposition.TRANSPARENT;
    disp = (!disp.equals(CacheDisposition.TRANSPARENT) && 
            mustRevalidate(options, cached_response)) ? 
              CacheDisposition.STALE : 
              disp;
    return disp;
  }
  
  public ClientResponse execute(
    String method, 
    String uri, 
    RequestEntity entity,
    RequestOptions options) {
      boolean usecache = useCache(method,options);
      options = options != null ? options : getDefaultRequestOptions();
      try {
        Cache cache = getCache();
        CachedResponse cached_response = cache.get(uri, options);
        CacheDisposition disp = getCacheDisposition(
          usecache, uri, options, cached_response);
        switch(disp) {
          case FRESH:                                                            // CACHE HIT: FRESH
            if (cached_response != null) {
              checkRequestException(cached_response,options);
              return cached_response;
            }
          case STALE:                                                            // CACHE HIT: STALE
            // revalidate the cached entry
            if (cached_response != null) {
              if (cached_response.getEntityTag() != null)
                options.setIfNoneMatch(cached_response.getEntityTag().toString());
              else if (cached_response.getLastModified() != null) 
                options.setIfModifiedSince(cached_response.getLastModified());
              else options.setNoCache(true);
            } else {
              disp = CacheDisposition.TRANSPARENT;
            }
          default:                                                               // CACHE MISS
            HttpMethod httpMethod = 
              MethodHelper.createMethod(
                method, uri, entity, options);
            client.executeMethod(httpMethod);
            if (usecache &&
                (httpMethod.getStatusCode() == 304 || 
                 httpMethod.getStatusCode() == 412) &&
                cached_response != null) return cached_response;
            ClientResponse response = new CommonsResponse(abdera,httpMethod);
            response = usecache ?
              response = cache.update(options, response, cached_response) : 
              response;
            checkRequestException(response,options);
            return response;
        }
      } catch (Throwable t) {
        if (t instanceof ClientException) throw (ClientException)t;
        throw new ClientException(t);
      }
  }

  private void checkRequestException(
    ClientResponse response, 
    RequestOptions options) {
      if (response == null) return;
      ResponseType type = response.getType();
      if ((type.equals(ResponseType.CLIENT_ERROR) && options.is4xxRequestException()) ||
          (type.equals(ResponseType.SERVER_ERROR) && options.is5xxRequestException()))
        throw new RequestException(response);
  }
  
  public RequestOptions getDefaultRequestOptions() {
    return MethodHelper.createDefaultRequestOptions();
  }
  
  public void addCredentials(
    String target,
    String realm,
    String scheme,
    Credentials credentials) 
      throws URISyntaxException {
    String host = AuthScope.ANY_HOST;
    int port = AuthScope.ANY_PORT;
    if (target != null) {
      URI uri = new URI(target);
      host = uri.getHost();
      port = uri.getPort();
    }
    AuthScope scope = 
      new AuthScope(
        host, 
        port, 
        (realm != null) ? realm : AuthScope.ANY_REALM, 
        (scheme != null) ? scheme : AuthScope.ANY_SCHEME);
    client.getState().setCredentials(
      scope, credentials);
  }

  public void setAuthenticationSchemeDefaults() {
    List authPrefs = AuthPolicy.getDefaultAuthPrefs();
    client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
  }
  
  public void setAuthenticationSchemePriority(String... scheme) {
    List authPrefs = java.util.Arrays.asList(scheme);
    client.getParams().setParameter(
      AuthPolicy.AUTH_SCHEME_PRIORITY, 
      authPrefs);
  }
  
  public List getAuthenticationSchemePriority() {
    return (List)client.getParams().getParameter(AuthPolicy.AUTH_SCHEME_PRIORITY);
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

  /**
   * Set the maximum number of connections allowed for a single host
   */
  public void setMaxConnectionsPerHost(int max) {
    client.getHttpConnectionManager().getParams().setIntParameter(
      HttpConnectionManagerParams.MAX_HOST_CONNECTIONS, max);
  }
  
  /**
   * Return the maximum number of connections allowed for a single host
   */
  public int getMaxConnectionsPerHost() {
    return client.getHttpConnectionManager().getParams().getIntParameter(
      HttpConnectionManagerParams.MAX_HOST_CONNECTIONS,
      MultiThreadedHttpConnectionManager.DEFAULT_MAX_HOST_CONNECTIONS);
  }
  
  /**
   * Return the maximum number of connections allowed for the client
   */
  public void setMaxConnectionsTotal(int max) {
    client.getHttpConnectionManager().getParams().setIntParameter(
      HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS, max);
  }
  
  /**
   * Return the maximum number of connections allowed for the client
   */
  public int getMaxConnectionsTotal() {
    return client.getHttpConnectionManager().getParams().getIntParameter(
      HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS,
      MultiThreadedHttpConnectionManager.DEFAULT_MAX_TOTAL_CONNECTIONS);
  }
}
