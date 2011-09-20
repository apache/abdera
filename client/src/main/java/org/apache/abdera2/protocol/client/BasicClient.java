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
package org.apache.abdera2.protocol.client;

import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.abdera2.common.anno.AnnoUtil;
import org.apache.abdera2.common.anno.Version;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

@Version(value="v2.0-SNAPSHOT",
    name="Abdera",
    uri="http://abdera.apache.org")
@SuppressWarnings("unchecked")
public class BasicClient implements Client {

  protected HttpClient client;
  
  public BasicClient() {
    this(DEFAULT_USER_AGENT);
  }
  
  public BasicClient(String useragent) {
      this.client = initClient(useragent);
  }
  
  public BasicClient(DefaultHttpClient client) {
    this.client = client;
  }
    
  /**
   * Default initialization of the Scheme Registry, subclasses
   * may overload this to customize the scheme registry 
   * configuration
   */
  protected SchemeRegistry initSchemeRegistry() {
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(
      new Scheme(
        "http", 
        80, 
        PlainSocketFactory.getSocketFactory()));
    schemeRegistry.register(
      new Scheme(
        "https", 
        443, 
        SSLSocketFactory.getSocketFactory()));
    return schemeRegistry;
  }
  
  /**
   * Default initialization of the Client Connection Manager,
   * subclasses may overload this to customize the connection
   * manager configuration
   */
  protected ClientConnectionManager initConnectionManager(SchemeRegistry sr) {
    ThreadSafeClientConnManager cm = 
      new ThreadSafeClientConnManager(sr);
    cm.setDefaultMaxPerRoute(initDefaultMaxConnectionsPerRoute());
    cm.setMaxTotal(initDefaultMaxTotalConnections());    
    return cm;
  }
  
  protected int initDefaultMaxConnectionsPerRoute() {
    return DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
  }
  
  protected int initDefaultMaxTotalConnections() {
    return DEFAULT_MAX_TOTAL_CONNECTIONS;
  }
  
  protected void initDefaultParameters(HttpParams params) {
    params.setParameter(
      CoreProtocolPNames.USE_EXPECT_CONTINUE, 
      Boolean.TRUE);
    params.setParameter(
      ClientPNames.MAX_REDIRECTS, 
      DEFAULT_MAX_REDIRECTS);
    params.setParameter(
      ClientPNames.COOKIE_POLICY, 
      CookiePolicy.BROWSER_COMPATIBILITY); 
  }
  
  protected HttpClient initClient(String useragent) {
    SchemeRegistry schemeRegistry = 
      initSchemeRegistry();
    ClientConnectionManager cm = 
      initConnectionManager(schemeRegistry);
    DefaultHttpClient client = new DefaultHttpClient(cm);
    HttpParams params = client.getParams();
    params.setParameter(CoreProtocolPNames.USER_AGENT, useragent);
    initDefaultParameters(params);
    return client;
  }
  
  public <T extends Client>T addRequestInterceptor(
    HttpRequestInterceptor i, 
    int index) {
    if (index == -1)
      getDefaultHttpClient().addRequestInterceptor(i);
    else
      getDefaultHttpClient().addRequestInterceptor(i, index);
    return (T)this;
  }
  
  public <T extends Client>T addResponseInterceptor(
    HttpResponseInterceptor i, 
    int index) {
    if (index == -1)
      getDefaultHttpClient().addResponseInterceptor(i);
    else
      getDefaultHttpClient().addResponseInterceptor(i, index);
    return (T)this;
  }
  
  public <T extends Client>T clearRequestInterceptors() {
    getDefaultHttpClient().clearRequestInterceptors();
    return (T)this;
  }
  
  public <T extends Client>T clearResponseInterceptors() {
    getDefaultHttpClient().clearResponseInterceptors();
    return (T)this;
  }
  
  public <T extends Client>T registerScheme(
    String scheme, 
    int port, 
    SchemeSocketFactory factory) {
    SchemeRegistry sr = 
      getClient().getConnectionManager().getSchemeRegistry();
    sr.register(new Scheme(scheme,port,factory));
    return (T)this;
  }
  
  public HttpClient getClient() {
    return client;
  }
  
  public DefaultHttpClient getDefaultHttpClient() {
    return (DefaultHttpClient)client;
  }
  
  /**
   * Add authentication credentials
   */
  public <T extends Client>T addCredentials(
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
    HttpHost targetHost = new HttpHost(host,port,scheme);
    getDefaultHttpClient().getCredentialsProvider().setCredentials(
        new AuthScope(
            targetHost.getHostName(), 
            targetHost.getPort(), 
            realm != null ? realm : AuthScope.ANY_REALM, 
            scheme != null ? scheme : AuthScope.ANY_SCHEME), 
        credentials);
     return (T)this;
  }

  /**
   * When multiple authentication schemes are supported by a server, the client will automatically select a scheme
   * based on the configured priority. For instance, to tell the client to prefer "digest" over "basic", set the
   * priority by calling setAuthenticationSchemePriority("digest","basic")
   */
  public <T extends Client>T setAuthenticationSchemePriority(String... scheme) {
      List<?> authPrefs = java.util.Arrays.asList(scheme);
      client.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authPrefs);
      return (T)this;
  }

  /**
   * Returns the current listing of preferred authentication schemes, in order of preference
   * 
   * @see setAuthenticationSchemePriority
   */
  public String[] getAuthenticationSchemePriority() {
      List<?> list = (List<?>) client.getParams().getParameter(AuthPNames.TARGET_AUTH_PREF);
      return list.toArray(new String[list.size()]);
  }

  /**
   * Set the maximum number of connections allowed for a single host. This 
   * is only effective if the Connection Manager implementation used is
   * a ThreadSafeClientConnManager, otherwise, an IllegalStateException is
   * thrown. Subclasses can override this method if a different Client
   * Connection Manager implementation is used that supports setting the
   * max connections per host.
   */
  public <T extends Client>T setMaxConnectionsPerHost(int max) {
    ClientConnectionManager ccm = 
      client.getConnectionManager();
    if (ccm instanceof ThreadSafeClientConnManager) {
      ThreadSafeClientConnManager cm = 
        (ThreadSafeClientConnManager) ccm;
      cm.setDefaultMaxPerRoute(max);
    } else {
      throw new IllegalStateException();
    }
    return (T)this;
  }

  /**
   * Return the maximum number of connections allowed for a single host. This
   * only returns a value if the Connection Manager implementation is a
   * ThreadSafeClientConnManager instance, otherwise it returns -1. Subclasses
   * can override this behavior if a different Connection Manager implementation
   * is used.
   */
  public int getMaxConnectionsPerHost() {
    ClientConnectionManager ccm = 
      client.getConnectionManager();
    if (ccm instanceof ThreadSafeClientConnManager) {
      ThreadSafeClientConnManager cm = 
        (ThreadSafeClientConnManager) client.getConnectionManager();
      return cm.getDefaultMaxPerRoute();
    } else return -1;
  }

  /**
   * Return the maximum number of connections allowed for the client
   */
  public <T extends Client>T setMaxConnectionsTotal(int max) {
    ClientConnectionManager ccm = 
      client.getConnectionManager();
    if (ccm instanceof ThreadSafeClientConnManager) {
      ThreadSafeClientConnManager cm = 
        (ThreadSafeClientConnManager) client.getConnectionManager();
      cm.setMaxTotal(max);
    } else {
      throw new IllegalStateException();
    }
    return (T)this;
  }

  /**
   * Return the maximum number of connections allowed for the client
   */
  public int getMaxConnectionsTotal() {
    ClientConnectionManager ccm = 
      client.getConnectionManager();
    if (ccm instanceof ThreadSafeClientConnManager) {
      ThreadSafeClientConnManager cm = 
        (ThreadSafeClientConnManager) client.getConnectionManager();
      return cm.getMaxTotal();
    } return -1;
  }

  /**
   * Configure the client to use the specified proxy
   */
  public <T extends Client>T setProxy(String host, int port) {
      HttpHost proxy = 
        new HttpHost(host, port);
      client.getParams().setParameter(
          ConnRoutePNames.DEFAULT_PROXY, proxy);
      return (T)this;
  }

  /**
   * Configure the client to use the proxy configured for the JVM
   * @return
   */
  public <T extends Client>T setStandardProxy() {
    ProxySelectorRoutePlanner routePlanner = 
      new ProxySelectorRoutePlanner(
        client.getConnectionManager().getSchemeRegistry(),
        ProxySelector.getDefault());  
    getDefaultHttpClient().setRoutePlanner(routePlanner);
    return (T)this;
  }
  
  /**
   * Get the underlying HTTP Client's Cookie Store, creating it automatically
   * if it does not already exist
   */
  public CookieStore getCookieStore() {
    return getCookieStore(true);
  }
  
  /**
   * Get the underlying HTTP Client's Cookie Store, optionally creating it
   * if it does not already exist
   */
  public CookieStore getCookieStore(boolean create) {
    CookieStore store = getDefaultHttpClient().getCookieStore();
    if (store == null && create) {
      synchronized(this) {
        store = new BasicCookieStore();
        getDefaultHttpClient().setCookieStore(store);
      }
    }
    return store;
  }
  
  /**
   * Set the underlying HTTP Client Cookie Store implementation
   */
  public <T extends Client>T setCookieStore(CookieStore cookieStore) {
    getDefaultHttpClient().setCookieStore(cookieStore);
    return (T)this;
  }
  
  /**
   * Manually add cookies
   */
  public <T extends Client>T addCookie(
      String domain, 
      String name, 
      String value) {
      BasicClientCookie cookie = 
        new BasicClientCookie(name,value);
      cookie.setVersion(0);
      cookie.setDomain(domain);
      getCookieStore().addCookie(cookie);
      return (T)this;
  }

  /**
   * Manually add cookies
   */
  public <T extends Client>T addCookie(
    String domain, 
    String name, 
    String value, 
    String path, 
    Date expires, 
    boolean secure) {
    BasicClientCookie cookie = 
      new BasicClientCookie(name,value);
    cookie.setVersion(0);
    cookie.setDomain(domain);
    cookie.setPath(path);
    cookie.setExpiryDate(expires);
    cookie.setSecure(secure);
    getCookieStore().addCookie(cookie);
    return (T)this;
  }

  /**
   * Manually add cookies
   */
  public <T extends Client>T addCookies(Cookie cookie) {
      getCookieStore().addCookie(cookie);
      return (T)this;
  }

  /**
   * Manually add cookies
   */
  public <T extends Client>T addCookies(Cookie... cookies) {
      for (Cookie cookie : cookies) 
        getCookieStore().addCookie(cookie);
      return (T)this;
  }

  /**
   * Get all the cookies
   */
  public Iterable<Cookie> getCookies() {
    return getCookieStore().getCookies();
  }

  /**
   * Get the cookies for a specific domain and path
   */
  public Iterable<Cookie> getCookies(String domain, String path) {
      List<Cookie> cookies = getCookieStore().getCookies();
      List<Cookie> list = new ArrayList<Cookie>();
      for (Cookie cookie : cookies) {
          String test = cookie.getDomain();
          if (test.startsWith("."))
              test = test.substring(1);
          if ((domain.endsWith(test) || test.endsWith(domain)) && 
              (path == null || cookie.getPath().startsWith(path))) {
              list.add(cookie);
          }
      }
      return list;
  }

  /**
   * Get the cookies for a specific domain
   */
  public Iterable<Cookie> getCookies(String domain) {
      return getCookies(domain, null);
  }

  /**
   * Clear the cookies
   */
  public <T extends Client>T clearCookies() {
      getCookieStore().clear();
      return (T)this;
  }
  
  public <T extends Client>T clearExpiredCookies() {
      getCookieStore().clearExpired(new Date());
      return (T)this;
  }

  /**
   * Sets the timeout until a connection is etablished. A value of zero means the timeout is not used. The default
   * value is zero.
   */
  public <T extends Client>T setConnectionTimeout(int timeout) {
      client.getParams().setIntParameter(
        CoreConnectionPNames.CONNECTION_TIMEOUT, 
        Math.max(0,timeout));
      return (T)this;
  }

  /**
   * Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data. A timeout
   * value of zero is interpreted as an infinite timeout.
   */
  public <T extends Client>T setSocketTimeout(int timeout) {
    client.getParams().setIntParameter(
      CoreConnectionPNames.SO_TIMEOUT, 
      Math.max(0,timeout));
      return (T)this;
  }

  /**
   * Return the timeout until a connection is etablished, in milliseconds. A value of zero means the timeout is not
   * used. The default value is zero.
   */
  public int getConnectionTimeout() {
      return client.getParams().getIntParameter(
        CoreConnectionPNames.CONNECTION_TIMEOUT, 0);
  }

  /**
   * Return the socket timeout for the connection in milliseconds A timeout value of zero is interpreted as an
   * infinite timeout.
   */
  public int getSocketTimeout() {
      return client.getParams().getIntParameter(
        CoreConnectionPNames.SO_TIMEOUT, 0);
  }

  /**
   * Determines whether Nagle's algorithm is to be used. The Nagle's algorithm tries to conserve bandwidth by
   * minimizing the number of segments that are sent. When applications wish to decrease network latency and increase
   * performance, they can disable Nagle's algorithm (that is enable TCP_NODELAY). Data will be sent earlier, at the
   * cost of an increase in bandwidth consumption.
   */
  public void setTcpNoDelay(boolean enable) {
      client.getParams().setBooleanParameter(
        CoreConnectionPNames.TCP_NODELAY, enable);
  }

  /**
   * Tests if Nagle's algorithm is to be used.
   */
  public boolean getTcpNoDelay() {
    return client.getParams().getBooleanParameter(
      CoreConnectionPNames.TCP_NODELAY, false);
  }

  /**
   * Return the HttpConnectionManagerParams object of the underlying HttpClient. This enables you to configure options
   * not explicitly exposed by the AbderaClient
   */
  public HttpParams getHttpConnectionManagerParams() {
      return client.getParams();
  }

  /**
   * Set the maximum number of redirects
   */
  public <T extends Client>T setMaximumRedirects(int redirects) {
      client.getParams().setIntParameter(
        ClientPNames.MAX_REDIRECTS, 
        Math.max(0, redirects));
      return (T)this;
  }

  /**
   * Get the maximum number of redirects
   */
  public int getMaximumRedirects() {
      return client.getParams().getIntParameter(
        ClientPNames.MAX_REDIRECTS, 
        DEFAULT_MAX_REDIRECTS);
  }

  /**
   * Clear all credentials (including proxy credentials)
   */
  public <T extends Client>T clearCredentials() {
      getDefaultHttpClient().getCredentialsProvider().clear();
      return (T)this;
  }

  public <T extends Session>T newSession() {
    return (T)new Session(this);
  }
  
  public void shutdown() {
    client.getConnectionManager().shutdown();
  }

  public static String getDefaultUserAgent() {
    Version version = AnnoUtil.getVersion(BasicClient.class);
    return String.format("%s/%s",version.name(),version.value());
  }
}
