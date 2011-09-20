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

@SuppressWarnings("unchecked")
public abstract class ClientWrapper 
  implements Client {

  private final Client internal;
  
  protected ClientWrapper(Client client) {
    this.internal = client;
  }
  
  public <T extends Client>T  addRequestInterceptor(HttpRequestInterceptor i, int index) {
    internal.addRequestInterceptor(i, index);
    return (T)this;
  }

  public <T extends Client>T  addResponseInterceptor(HttpResponseInterceptor i, int index) {
    internal.addResponseInterceptor(i,index);
    return (T)this;
  }

  public <T extends Client>T  clearRequestInterceptors() {
    internal.clearRequestInterceptors();
    return (T)this;
  }

  public <T extends Client>T  clearResponseInterceptors() {
    internal.clearResponseInterceptors();
    return (T)this;
  }

  public <T extends Client>T  registerScheme(String scheme, int port,
      SchemeSocketFactory factory) {
    internal.registerScheme(scheme, port, factory);
    return (T)this;
  }

  public HttpClient getClient() {
    return internal.getClient();
  }

  public DefaultHttpClient getDefaultHttpClient() {
    return internal.getDefaultHttpClient();
  }

  public <T extends Client>T  addCredentials(String target, String realm, String scheme,
      Credentials credentials) throws URISyntaxException {
    internal.addCredentials(target, realm, scheme, credentials);
    return (T)this;
  }

  public <T extends Client>T  setAuthenticationSchemePriority(String... scheme) {
    internal.setAuthenticationSchemePriority(scheme);
    return (T)this;
  }

  public String[] getAuthenticationSchemePriority() {
    return internal.getAuthenticationSchemePriority();
  }

  public <T extends Client>T  setMaxConnectionsPerHost(int max) {
    internal.setMaxConnectionsPerHost(max);
    return (T)this;
  }

  public int getMaxConnectionsPerHost() {
    return internal.getMaxConnectionsPerHost();
  }

  public <T extends Client>T  setMaxConnectionsTotal(int max) {
    internal.setMaxConnectionsTotal(max);
    return (T)this;
  }

  public int getMaxConnectionsTotal() {
    return internal.getMaxConnectionsTotal();
  }

  public <T extends Client>T  setProxy(String host, int port) {
    internal.setProxy(host,port);
    return (T)this;
  }

  public <T extends Client>T  setStandardProxy() {
    internal.setStandardProxy();
    return (T)this;
  }

  public CookieStore getCookieStore() {
    return internal.getCookieStore();
  }

  public CookieStore getCookieStore(boolean create) {
    return internal.getCookieStore(create);
  }

  public <T extends Client>T  setCookieStore(CookieStore cookieStore) {
    internal.setCookieStore(cookieStore);
    return (T)this;
  }

  public <T extends Client>T  addCookie(String domain, String name, String value) {
    internal.addCookie(domain, name, value);
    return (T)this;
  }

  public <T extends Client>T  addCookie(String domain, String name, String value,
      String path, Date expires, boolean secure) {
    internal.addCookie(domain, name, value, path, expires, secure);
    return (T)this;
  }

  public <T extends Client>T  addCookies(Cookie cookie) {
    internal.addCookies(cookie);
    return (T)this;
  }

  public <T extends Client>T  addCookies(Cookie... cookies) {
    internal.addCookies(cookies);
    return (T)this;
  }

  public Iterable<Cookie> getCookies() {
    return internal.getCookies();
  }

  public Iterable<Cookie> getCookies(String domain, String path) {
    return internal.getCookies(domain, path);
  }

  public Iterable<Cookie> getCookies(String domain) {
    return internal.getCookies(domain);
  }

  public <T extends Client>T  clearCookies() {
    internal.clearCookies();
    return (T)this;
  }

  public <T extends Client>T  clearExpiredCookies() {
    internal.clearExpiredCookies();
    return (T)this;
  }

  public <T extends Client>T  setConnectionTimeout(int timeout) {
    internal.setConnectionTimeout(timeout);
    return (T)this;
  }

  public <T extends Client>T  setSocketTimeout(int timeout) {
    internal.setSocketTimeout(timeout);
    return (T)this;
  }

  public int getConnectionTimeout() {
    return internal.getConnectionTimeout();
  }

  public int getSocketTimeout() {
    return internal.getSocketTimeout();
  }

  public void setTcpNoDelay(boolean enable) {
    internal.setTcpNoDelay(enable);
  }

  public boolean getTcpNoDelay() {
    return internal.getTcpNoDelay();
  }

  public HttpParams getHttpConnectionManagerParams() {
   return internal.getHttpConnectionManagerParams();
  }

  public <T extends Client>T  setMaximumRedirects(int redirects) {
    internal.setMaximumRedirects(redirects);
    return (T)this;
  }

  public int getMaximumRedirects() {
    return internal.getMaximumRedirects();
  }

  public <T extends Client>T  clearCredentials() {
    internal.clearCredentials();
    return (T)this;
  }

  public <T extends Session>T newSession() {
    return (T)new Session(this);
  }

  public void shutdown() {
    internal.shutdown();
  }

}
