package org.apache.abdera2.protocol.client;

import java.net.URISyntaxException;
import java.util.Date;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public interface Client {

  public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
  public static final int DEFAULT_MAX_REDIRECTS = 10;
  public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
  public static final String DEFAULT_USER_AGENT = BasicClient.getDefaultUserAgent();

  <T extends Client>T addRequestInterceptor(HttpRequestInterceptor i,
      int index);

  <T extends Client>T addResponseInterceptor(HttpResponseInterceptor i,
      int index);

  <T extends Client>T clearRequestInterceptors();

  <T extends Client>T clearResponseInterceptors();

  <T extends Client>T registerScheme(String scheme, int port,
      SchemeSocketFactory factory);

  HttpClient getClient();

  DefaultHttpClient getDefaultHttpClient();

  /**
   * Add authentication credentials
   */
  <T extends Client>T addCredentials(String target, String realm,
      String scheme, Credentials credentials) throws URISyntaxException;

  /**
   * When multiple authentication schemes are supported by a server, the client will automatically select a scheme
   * based on the configured priority. For instance, to tell the client to prefer "digest" over "basic", set the
   * priority by calling setAuthenticationSchemePriority("digest","basic")
   */
  <T extends Client>T setAuthenticationSchemePriority(String... scheme);

  /**
   * Returns the current listing of preferred authentication schemes, in order of preference
   * 
   * @see setAuthenticationSchemePriority
   */
  String[] getAuthenticationSchemePriority();

  /**
   * Set the maximum number of connections allowed for a single host. This 
   * is only effective if the Connection Manager implementation used is
   * a ThreadSafeClientConnManager, otherwise, an IllegalStateException is
   * thrown. Subclasses can override this method if a different Client
   * Connection Manager implementation is used that supports setting the
   * max connections per host.
   */
  <T extends Client>T setMaxConnectionsPerHost(int max);

  /**
   * Return the maximum number of connections allowed for a single host. This
   * only returns a value if the Connection Manager implementation is a
   * ThreadSafeClientConnManager instance, otherwise it returns -1. Subclasses
   * can override this behavior if a different Connection Manager implementation
   * is used.
   */
  int getMaxConnectionsPerHost();

  /**
   * Return the maximum number of connections allowed for the client
   */
  <T extends Client>T setMaxConnectionsTotal(int max);

  /**
   * Return the maximum number of connections allowed for the client
   */
  int getMaxConnectionsTotal();

  /**
   * Configure the client to use the specified proxy
   */
  <T extends Client>T setProxy(String host, int port);

  /**
   * Configure the client to use the proxy configured for the JVM
   * @return
   */
  <T extends Client>T setStandardProxy();

  /**
   * Get the underlying HTTP Client's Cookie Store, creating it automatically
   * if it does not already exist
   */
  CookieStore getCookieStore();

  /**
   * Get the underlying HTTP Client's Cookie Store, optionally creating it
   * if it does not already exist
   */
  CookieStore getCookieStore(boolean create);

  /**
   * Set the underlying HTTP Client Cookie Store implementation
   */
  <T extends Client>T setCookieStore(CookieStore cookieStore);

  /**
   * Manually add cookies
   */
  <T extends Client>T addCookie(String domain, String name, String value);

  /**
   * Manually add cookies
   */
  <T extends Client>T addCookie(String domain, String name, String value,
      String path, Date expires, boolean secure);

  /**
   * Manually add cookies
   */
  <T extends Client>T addCookies(Cookie cookie);

  /**
   * Manually add cookies
   */
  <T extends Client>T addCookies(Cookie... cookies);

  /**
   * Get all the cookies
   */
  Iterable<Cookie> getCookies();

  /**
   * Get the cookies for a specific domain and path
   */
  Iterable<Cookie> getCookies(String domain, String path);

  /**
   * Get the cookies for a specific domain
   */
  Iterable<Cookie> getCookies(String domain);

  /**
   * Clear the cookies
   */
  <T extends Client>T clearCookies();

  <T extends Client>T clearExpiredCookies();

  /**
   * Sets the timeout until a connection is etablished. A value of zero means the timeout is not used. The default
   * value is zero.
   */
  <T extends Client>T setConnectionTimeout(int timeout);

  /**
   * Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data. A timeout
   * value of zero is interpreted as an infinite timeout.
   */
  <T extends Client>T setSocketTimeout(int timeout);

  /**
   * Return the timeout until a connection is etablished, in milliseconds. A value of zero means the timeout is not
   * used. The default value is zero.
   */
  int getConnectionTimeout();

  /**
   * Return the socket timeout for the connection in milliseconds A timeout value of zero is interpreted as an
   * infinite timeout.
   */
  int getSocketTimeout();

  /**
   * Determines whether Nagle's algorithm is to be used. The Nagle's algorithm tries to conserve bandwidth by
   * minimizing the number of segments that are sent. When applications wish to decrease network latency and increase
   * performance, they can disable Nagle's algorithm (that is enable TCP_NODELAY). Data will be sent earlier, at the
   * cost of an increase in bandwidth consumption.
   */
  void setTcpNoDelay(boolean enable);

  /**
   * Tests if Nagle's algorithm is to be used.
   */
  boolean getTcpNoDelay();

  /**
   * Return the HttpConnectionManagerParams object of the underlying HttpClient. This enables you to configure options
   * not explicitly exposed by the AbderaClient
   */
  HttpParams getHttpConnectionManagerParams();

  /**
   * Set the maximum number of redirects
   */
  <T extends Client>T setMaximumRedirects(int redirects);

  /**
   * Get the maximum number of redirects
   */
  int getMaximumRedirects();

  /**
   * Clear all credentials (including proxy credentials)
   */
  <T extends Client>T clearCredentials();

  <T extends Session>T newSession();

  void shutdown();

}