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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.TrustManager;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.EntityProvider;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.cache.Cache;
import org.apache.abdera.protocol.client.cache.CacheFactory;
import org.apache.abdera.protocol.client.cache.CachedResponse;
import org.apache.abdera.protocol.client.cache.LRUCache;
import org.apache.abdera.protocol.client.cache.Cache.Disposition;
import org.apache.abdera.protocol.client.util.BaseRequestEntity;
import org.apache.abdera.protocol.client.util.EntityProviderRequestEntity;
import org.apache.abdera.protocol.client.util.MethodHelper;
import org.apache.abdera.protocol.client.util.MultipartRelatedRequestEntity;
import org.apache.abdera.protocol.client.util.SimpleSSLProtocolSocketFactory;
import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.protocol.error.ProtocolException;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.Version;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
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
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/**
 * An Atom Publishing Protocol client.
 */
@SuppressWarnings( {"unchecked", "deprecation"})
public class AbderaClient {

    public static final String DEFAULT_USER_AGENT = Version.APP_NAME + "/" + Version.VERSION;

    public static int DEFAULT_MAX_REDIRECTS = 10;

    protected final Abdera abdera;
    protected final Cache cache;
    private final HttpClient client;

    public AbderaClient() {
        this(new Abdera(), DEFAULT_USER_AGENT);
    }

    /**
     * Create an AbderaClient instance using the specified useragent name
     * 
     * @param useragent
     */
    public AbderaClient(String useragent) {
        this(new Abdera(), useragent);
    }

    /**
     * Create an AbderaClient instance using the specified Abdera instance and useragent name
     * 
     * @param abdera
     * @param useragent
     */
    public AbderaClient(Abdera abdera, String useragent) {
        this(abdera, useragent, initCache(abdera));
    }

    /**
     * Create an AbderaClient instance using the specified Abdera instance and useragent name
     * 
     * @param abdera
     * @param useragent
     */
    public AbderaClient(Abdera abdera, String useragent, Cache cache) {
        this.abdera = abdera;
        this.cache = cache;
        MultiThreadedHttpConnectionManager connManager = new MultiThreadedHttpConnectionManager();
        client = new HttpClient(connManager);
        client.getParams().setParameter(HttpClientParams.USER_AGENT, useragent);
        client.getParams().setBooleanParameter(HttpClientParams.USE_EXPECT_CONTINUE, true);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        setAuthenticationSchemeDefaults();
        setMaximumRedirects(DEFAULT_MAX_REDIRECTS);
    }

    /**
     * Create an Abdera using a preconfigured HttpClient object
     * 
     * @param client An Apache HttpClient object
     */
    public AbderaClient(HttpClient client) {
        this(new Abdera(), client);
    }

    /**
     * Create an Abdera using a preconfigured HttpClient object
     * 
     * @param abdera
     * @param client An Apache HttpClient object
     */
    public AbderaClient(Abdera abdera, HttpClient client) {
        this(abdera, client, initCache(abdera));
    }

    /**
     * Create an Abdera using a preconfigured HttpClient object
     * 
     * @param abdera
     * @param client An Apache HttpClient object
     */
    public AbderaClient(Abdera abdera, HttpClient client, Cache cache) {
        this.abdera = abdera;
        this.cache = cache;
        this.client = client;
        setAuthenticationSchemeDefaults();
        setMaximumRedirects(DEFAULT_MAX_REDIRECTS);
    }

    /**
     * Create an AbderaClient instance using the specified Abdera instance
     * 
     * @param abdera
     */
    public AbderaClient(Abdera abdera) {
        this(abdera, DEFAULT_USER_AGENT);
    }

    /**
     * Create an AbderaClient instance using the specified Abdera instance
     * 
     * @param abdera
     */
    public AbderaClient(Abdera abdera, Cache cache) {
        this(abdera, DEFAULT_USER_AGENT, cache);
    }

    /**
     * Returns the client HTTP cache instance
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * @deprecated The CacheFactory interface is no longer used.
     */
    public Cache initCache(CacheFactory factory) {
        return initCache(abdera);
    }

    /**
     * Initializes the client HTTP cache
     */
    public static Cache initCache(Abdera abdera) {
        return new LRUCache(abdera);
    }

    /**
     * Sends an HTTP HEAD request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public ClientResponse head(String uri, RequestOptions options) {
        return execute("HEAD", uri, (RequestEntity)null, options);
    }

    /**
     * Sends an HTTP GET request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public ClientResponse get(String uri, RequestOptions options) {
        return execute("GET", uri, (RequestEntity)null, options);
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     * @param options The request options
     */
    public ClientResponse post(String uri, EntityProvider provider, RequestOptions options) {
        return post(uri, new EntityProviderRequestEntity(abdera, provider, options.isUseChunked()), options);
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     * @param options The request options
     */
    public ClientResponse post(String uri, RequestEntity entity, RequestOptions options) {
        return execute("POST", uri, entity, options);
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The request options
     */
    public ClientResponse post(String uri, InputStream in, RequestOptions options) {
        return execute("POST", uri, new InputStreamRequestEntity(in), options);
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param base An Abdera FOM Document or Element object providing the payload of the request
     * @param options The request options
     */
    public ClientResponse post(String uri, Base base, RequestOptions options) {
        if (base instanceof Document) {
            Document d = (Document)base;
            if (options.getSlug() == null && d.getSlug() != null)
                options.setSlug(d.getSlug());
        }
        return execute("POST", uri, new BaseRequestEntity(base, options.isUseChunked()), options);
    }

    /**
     * Sends an HTTP POST request to the specified URI. It uses the media and entry parameters to create a
     * multipart/related object. If the contentType is not provided this method tries to get it from the type attribute
     * of the entry content.
     * 
     * @param uri The request URI
     * @param entry The entry that will be sent as the first element of the multipart/related object
     * @param media The media object that will be sent as the second element of the multipart/related object
     */
    public ClientResponse post(String uri, Entry entry, InputStream media) {
        return post(uri, entry, media, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP POST request to the specified URI. It uses the media and entry parameters to create a
     * multipart/related object. If the contentType is not provided this method tries to get it from the type attribute
     * of the entry content.
     * 
     * @param uri The request URI
     * @param entry The entry that will be sent as the first element of the multipart/related object
     * @param media The media object that will be sent as the second element of the multipart/related object
     * @param options The request options
     */
    public ClientResponse post(String uri, Entry entry, InputStream media, RequestOptions options) {
        return post(uri, entry, media, null, options);
    }

    /**
     * Sends an HTTP POST request to the specified URI. It uses the media and entry parameters to create a
     * multipart/related object.
     * 
     * @param uri The request URI
     * @param entry The entry that will be sent as the first element of the multipart/related object
     * @param media The media object that will be sent as the second element of the multipart/related object
     * @param contentType the content type of the media object
     * @param options The request options
     */
    public ClientResponse post(String uri, Entry entry, InputStream media, String contentType, RequestOptions options) {
        return execute("POST", uri, new MultipartRelatedRequestEntity(entry, media, contentType), options);
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     * @param options The request options
     */
    public ClientResponse put(String uri, EntityProvider provider, RequestOptions options) {
        if (options == null)
            options = getDefaultRequestOptions();
        if (options.isConditionalPut()) {
            EntityTag etag = provider.getEntityTag();
            if (etag != null)
                options.setIfMatch(etag);
            else {
                Date lm = provider.getLastModified();
                if (lm != null)
                    options.setIfUnmodifiedSince(lm);
            }
        }
        return put(uri, new EntityProviderRequestEntity(abdera, provider, options.isUseChunked()), options);
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     * @param options The request options
     */
    public ClientResponse put(String uri, RequestEntity entity, RequestOptions options) {
        return execute("PUT", uri, entity, options);
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The request options
     */
    public ClientResponse put(String uri, InputStream in, RequestOptions options) {
        return execute("PUT", uri, new InputStreamRequestEntity(in), options);
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param base A FOM Document or Element providing the payload of the request
     * @param options The request options
     */
    public ClientResponse put(String uri, Base base, RequestOptions options) {
        if (options == null)
            options = getDefaultRequestOptions();
        if (base instanceof Document) {
            Document d = (Document)base;
            if (options.getSlug() == null && d.getSlug() != null)
                options.setSlug(d.getSlug());

            if (options.isConditionalPut()) {
                if (d.getEntityTag() != null)
                    options.setIfMatch(d.getEntityTag());
                else if (d.getLastModified() != null)
                    options.setIfUnmodifiedSince(d.getLastModified());
            }
        }
        return execute("PUT", uri, new BaseRequestEntity(base, options.isUseChunked()), options);
    }

    /**
     * Sends an HTTP DELETE request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public ClientResponse delete(String uri, RequestOptions options) {
        return execute("DELETE", uri, (RequestEntity)null, options);
    }

    /**
     * Sends an HTTP HEAD request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public ClientResponse head(String uri) {
        return head(uri, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP GET request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public ClientResponse get(String uri) {
        return get(uri, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload the request
     */
    public ClientResponse post(String uri, EntityProvider provider) {
        return post(uri, provider, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     */
    public ClientResponse post(String uri, RequestEntity entity) {
        return post(uri, entity, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     */
    public ClientResponse post(String uri, InputStream in) {
        return post(uri, in, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param base A FOM Document or Element providing the payload of the request
     */
    public ClientResponse post(String uri, Base base) {
        return post(uri, base, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     */
    public ClientResponse put(String uri, EntityProvider provider) {
        return put(uri, provider, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     */
    public ClientResponse put(String uri, RequestEntity entity) {
        return put(uri, entity, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     */
    public ClientResponse put(String uri, InputStream in) {
        return put(uri, in, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param base A FOM Document or Element providing the payload of the request
     */
    public ClientResponse put(String uri, Base base) {
        return put(uri, base, getDefaultRequestOptions());
    }

    /**
     * Sends an HTTP DELETE request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public ClientResponse delete(String uri) {
        return delete(uri, getDefaultRequestOptions());
    }

    /**
     * Register a new authentication scheme.
     * 
     * @param name
     * @param scheme
     */
    public static void registerScheme(String name, Class<? extends AuthScheme> scheme) {
        AuthPolicy.registerAuthScheme(name, scheme);
    }

    /**
     * Unregister a specific authentication scheme
     * 
     * @param name The name of the authentication scheme (e.g. "basic", "digest", etc)
     */
    public static void unregisterScheme(String name) {
        AuthPolicy.unregisterAuthScheme(name);
    }

    /**
     * Unregister multiple HTTP authentication schemes
     */
    public static void unregisterScheme(String... names) {
        for (String name : names)
            unregisterScheme(name);
    }

    /**
     * Register the specified TrustManager for SSL support on the default port (443)
     * 
     * @param trustManager The TrustManager implementation
     */
    public static void registerTrustManager(TrustManager trustManager) {
        registerTrustManager(trustManager, 443);
    }

    /**
     * Register the default TrustManager for SSL support on the default port (443)
     */
    public static void registerTrustManager() {
        registerTrustManager(443);
    }

    /**
     * Register the specified TrustManager for SSL support on the specified port
     * 
     * @param trustManager The TrustManager implementation
     * @param port The port
     */
    public static void registerTrustManager(TrustManager trustManager, int port) {
        SimpleSSLProtocolSocketFactory f = new SimpleSSLProtocolSocketFactory(trustManager);
        registerFactory(f, port);
    }

    /**
     * Register the default trust manager on the specified port
     * 
     * @param port The port
     */
    public static void registerTrustManager(int port) {
        SimpleSSLProtocolSocketFactory f = new SimpleSSLProtocolSocketFactory();
        registerFactory(f, port);
    }

    /**
     * Register the specified secure socket factory on the specified port
     */
    public static void registerFactory(SecureProtocolSocketFactory factory, int port) {
        Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory)factory, port));
    }

    /**
     * Configure the client to use preemptive authentication (HTTP Basic Authentication only)
     */
    public AbderaClient usePreemptiveAuthentication(boolean val) {
        client.getParams().setAuthenticationPreemptive(val);
        return this;
    }

    private boolean useCache(String method, RequestOptions options) {
        return (CacheControlUtil.isIdempotent(method)) && !options.isNoCache()
            && !options.isNoStore()
            && options.getUseLocalCache();
    }

    private boolean mustRevalidate(RequestOptions options, CachedResponse response) {
        if (options.getRevalidateWithAuth()) {
            if (options.getAuthorization() != null)
                return true;
            if (client.getParams().getBooleanParameter(HttpClientParams.PREEMPTIVE_AUTHENTICATION, false))
                return true;
            if (response != null) {
                if (response.isPublic())
                    return false;
            }
        }
        return false;
    }

    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param base A FOM Document and Element providing the payload for the request
     * @param options The Request Options
     */
    public ClientResponse execute(String method, String uri, Base base, RequestOptions options) {
        return execute(method, uri, new BaseRequestEntity(base), options);
    }

    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param provider An EntityProvider implementation providing the payload of the request
     * @param options The Request Options
     */
    public ClientResponse execute(String method, String uri, EntityProvider provider, RequestOptions options) {
        if (options == null)
            options = getDefaultRequestOptions();
        return execute(method, uri, new EntityProviderRequestEntity(abdera, provider, options.isUseChunked()), options);
    }

    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The Request Options
     */
    public ClientResponse execute(String method, String uri, InputStream in, RequestOptions options) {
        RequestEntity re = null;
        try {
            if (options.getContentType() != null) {
                re = new InputStreamRequestEntity(in, options.getContentType().toString());
            } else {
                re = new InputStreamRequestEntity(in);
            }
        } catch (Exception e) {
            re = new InputStreamRequestEntity(in);
        }
        return execute(method, uri, re, options);
    }

    private Disposition getCacheDisposition(boolean usecache,
                                            String uri,
                                            RequestOptions options,
                                            CachedResponse cached_response) {
        Disposition disp = (usecache) ? cache.disposition(uri, options) : Disposition.TRANSPARENT;
        disp =
            (!disp.equals(Disposition.TRANSPARENT) && mustRevalidate(options, cached_response)) ? Disposition.STALE
                : disp;
        return disp;
    }

    /**
     * Sends the specified method request to the specified URI. This can be used to send extension HTTP methods to a
     * server (e.g. PATCH, LOCK, etc)
     * 
     * @param method The HTTP method
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload for the request
     * @param options The Request Options
     */
    public ClientResponse execute(String method, String uri, RequestEntity entity, RequestOptions options) {
        boolean usecache = useCache(method, options);
        options = options != null ? options : getDefaultRequestOptions();
        try {
            Cache cache = getCache();
            CachedResponse cached_response = cache.get(uri);
            switch (getCacheDisposition(usecache, uri, options, cached_response)) {
                case FRESH: // CACHE HIT: FRESH
                    if (cached_response != null)
                        return checkRequestException(cached_response, options);
                case STALE: // CACHE HIT: STALE
                    // revalidate the cached entry
                    if (cached_response != null) {
                        if (cached_response.getEntityTag() != null)
                            options.setIfNoneMatch(cached_response.getEntityTag().toString());
                        else if (cached_response.getLastModified() != null)
                            options.setIfModifiedSince(cached_response.getLastModified());
                        else
                            options.setNoCache(true);
                    }
                default: // CACHE MISS
                    HttpMethod httpMethod = MethodHelper.createMethod(method, uri, entity, options);
                    client.executeMethod(httpMethod);
                    if (usecache && (httpMethod.getStatusCode() == 304 || httpMethod.getStatusCode() == 412)
                        && cached_response != null)
                        return cached_response;
                    ClientResponse response = new CommonsResponse(abdera, httpMethod);
                    response =
                        options.getUseLocalCache() ? response = cache.update(uri, options, response, cached_response)
                            : response;
                    return checkRequestException(response, options);
            }
        } catch (RuntimeException r) {
            throw r;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private ClientResponse checkRequestException(ClientResponse response, RequestOptions options) {
        if (response == null)
            return response;
        ResponseType type = response.getType();
        if ((type.equals(ResponseType.CLIENT_ERROR) && options.is4xxRequestException()) || (type
            .equals(ResponseType.SERVER_ERROR) && options.is5xxRequestException())) {
            try {
                Document<Element> doc = response.getDocument();
                org.apache.abdera.protocol.error.Error error = null;
                if (doc != null) {
                    Element root = doc.getRoot();
                    if (root instanceof Error) {
                        error = (Error)root;
                    }
                }
                if (error == null)
                    error =
                        org.apache.abdera.protocol.error.Error.create(abdera, response.getStatus(), response
                            .getStatusText());
                error.throwException();
            } catch (ProtocolException pe) {
                throw pe;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }

    /**
     * Get a copy of the default request options
     */
    public RequestOptions getDefaultRequestOptions() {
        return MethodHelper.createDefaultRequestOptions();
    }

    /**
     * Add authentication credentials
     */
    public AbderaClient addCredentials(String target, String realm, String scheme, Credentials credentials)
        throws URISyntaxException {
        String host = AuthScope.ANY_HOST;
        int port = AuthScope.ANY_PORT;
        if (target != null) {
            URI uri = new URI(target);
            host = uri.getHost();
            port = uri.getPort();
        }
        AuthScope scope =
            new AuthScope(host, port, (realm != null) ? realm : AuthScope.ANY_REALM, (scheme != null) ? scheme
                : AuthScope.ANY_SCHEME);
        client.getState().setCredentials(scope, credentials);
        return this;
    }

    /**
     * Configure the client to use the default authentication scheme settings
     */
    public AbderaClient setAuthenticationSchemeDefaults() {
        List authPrefs = AuthPolicy.getDefaultAuthPrefs();
        client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
        return this;
    }

    /**
     * When multiple authentication schemes are supported by a server, the client will automatically select a scheme
     * based on the configured priority. For instance, to tell the client to prefer "digest" over "basic", set the
     * priority by calling setAuthenticationSchemePriority("digest","basic")
     */
    public AbderaClient setAuthenticationSchemePriority(String... scheme) {
        List authPrefs = java.util.Arrays.asList(scheme);
        client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
        return this;
    }

    /**
     * Returns the current listing of preferred authentication schemes, in order of preference
     * 
     * @see setAuthenticationSchemePriority
     */
    public String[] getAuthenticationSchemePriority() {
        List list = (List)client.getParams().getParameter(AuthPolicy.AUTH_SCHEME_PRIORITY);
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * <p>
     * Per http://jakarta.apache.org/commons/httpclient/performance.html
     * </p>
     * <blockquote> Generally it is recommended to have a single instance of HttpClient per communication component or
     * even per application. However, if the application makes use of HttpClient only very infrequently, and keeping an
     * idle instance of HttpClient in memory is not warranted, it is highly recommended to explicitly shut down the
     * multithreaded connection manager prior to disposing the HttpClient instance. This will ensure proper closure of
     * all HTTP connections in the connection pool. </blockquote>
     */
    public AbderaClient teardown() {
        ((MultiThreadedHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        return this;
    }

    /**
     * Set the maximum number of connections allowed for a single host
     */
    public AbderaClient setMaxConnectionsPerHost(int max) {
        Map<HostConfiguration, Integer> m = new HashMap<HostConfiguration, Integer>();
        m.put(HostConfiguration.ANY_HOST_CONFIGURATION, max);
        client.getHttpConnectionManager().getParams().setParameter(HttpConnectionManagerParams.MAX_HOST_CONNECTIONS, m);
        return this;
    }

    /**
     * Return the maximum number of connections allowed for a single host
     */
    public int getMaxConnectionsPerHost() {
        Map<HostConfiguration, Integer> m =
            (Map<HostConfiguration, Integer>)client.getHttpConnectionManager().getParams()
                .getParameter(HttpConnectionManagerParams.MAX_HOST_CONNECTIONS);
        if (m == null)
            return MultiThreadedHttpConnectionManager.DEFAULT_MAX_HOST_CONNECTIONS;
        Integer i = m.get(HostConfiguration.ANY_HOST_CONFIGURATION);
        return i != null ? i.intValue() : MultiThreadedHttpConnectionManager.DEFAULT_MAX_HOST_CONNECTIONS;
    }

    /**
     * Return the maximum number of connections allowed for the client
     */
    public AbderaClient setMaxConnectionsTotal(int max) {
        client.getHttpConnectionManager().getParams()
            .setIntParameter(HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS, max);
        return this;
    }

    /**
     * Return the maximum number of connections allowed for the client
     */
    public int getMaxConnectionsTotal() {
        return client.getHttpConnectionManager().getParams()
            .getIntParameter(HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS,
                             MultiThreadedHttpConnectionManager.DEFAULT_MAX_TOTAL_CONNECTIONS);
    }

    /**
     * Configure the client to use the specified proxy
     */
    public AbderaClient setProxy(String host, int port) {
        client.getHostConfiguration().setProxy(host, port);
        return this;
    }

    /**
     * Specify the auth credentials for the proxy server
     */
    public AbderaClient setProxyCredentials(String host, int port, Credentials credentials) {
        setProxyCredentials(host, port, null, null, credentials);
        return this;
    }

    /**
     * Specify the auth credentials for the proxy server
     */
    public AbderaClient setProxyCredentials(String host, int port, String realm, String scheme, Credentials credentials) {
        host = host != null ? host : AuthScope.ANY_HOST;
        port = port > -1 ? port : AuthScope.ANY_PORT;
        AuthScope scope =
            new AuthScope(host, port, realm != null ? realm : AuthScope.ANY_REALM, scheme != null ? scheme
                : AuthScope.ANY_SCHEME);
        client.getState().setProxyCredentials(scope, credentials);
        return this;
    }

    /**
     * Manually add cookies
     */
    public AbderaClient addCookie(String domain, String name, String value) {
        Cookie cookie = new Cookie(domain, name, value);
        client.getState().addCookie(cookie);
        return this;
    }

    /**
     * Manually add cookies
     */
    public AbderaClient addCookie(String domain, String name, String value, String path, Date expires, boolean secure) {
        Cookie cookie = new Cookie(domain, name, value, path, expires, secure);
        client.getState().addCookie(cookie);
        return this;
    }

    /**
     * Manually add cookies
     */
    public AbderaClient addCookie(String domain, String name, String value, String path, int maxAge, boolean secure) {
        Cookie cookie = new Cookie(domain, name, value, path, maxAge, secure);
        client.getState().addCookie(cookie);
        return this;
    }

    /**
     * Manually add cookies
     */
    public AbderaClient addCookies(Cookie cookie) {
        client.getState().addCookie(cookie);
        return this;
    }

    /**
     * Manually add cookies
     */
    public AbderaClient addCookies(Cookie... cookies) {
        client.getState().addCookies(cookies);
        return this;
    }

    /**
     * Get all the cookies
     */
    public Cookie[] getCookies() {
        return client.getState().getCookies();
    }

    /**
     * Get the cookies for a specific domain and path
     */
    public Cookie[] getCookies(String domain, String path) {
        Cookie[] cookies = getCookies();
        List<Cookie> list = new ArrayList<Cookie>();
        for (Cookie cookie : cookies) {
            String test = cookie.getDomain();
            if (test.startsWith("."))
                test = test.substring(1);
            if ((domain.endsWith(test) || test.endsWith(domain)) && (path == null || cookie.getPath().startsWith(path))) {
                list.add(cookie);
            }
        }
        return list.toArray(new Cookie[list.size()]);
    }

    /**
     * Get the cookies for a specific domain
     */
    public Cookie[] getCookies(String domain) {
        return getCookies(domain, null);
    }

    /**
     * Clear the cookies
     */
    public AbderaClient clearCookies() {
        client.getState().clearCookies();
        return this;
    }

    /**
     * Sets the timeout until a connection is etablished. A value of zero means the timeout is not used. The default
     * value is zero.
     */
    public AbderaClient setConnectionTimeout(int timeout) {
        client.getHttpConnectionManager().getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, timeout);
        return this;
    }

    /**
     * Sets the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data. A timeout
     * value of zero is interpreted as an infinite timeout.
     */
    public AbderaClient setSocketTimeout(int timeout) {
        client.getParams().setSoTimeout(timeout);
        return this;
    }

    /**
     * Sets the timeout in milliseconds used when retrieving an HTTP connection from the HTTP connection manager.
     */
    public void setConnectionManagerTimeout(long timeout) {
        client.getParams().setLongParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, timeout);
    }

    /**
     * Return the timeout until a connection is etablished, in milliseconds. A value of zero means the timeout is not
     * used. The default value is zero.
     */
    public int getConnectionTimeout() {
        return client.getHttpConnectionManager().getParams()
            .getIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 0);
    }

    /**
     * Return the socket timeout for the connection in milliseconds A timeout value of zero is interpreted as an
     * infinite timeout.
     */
    public int getSocketTimeout() {
        return client.getParams().getSoTimeout();
    }

    /**
     * Returns the timeout in milliseconds used when retrieving an HTTP connection from the HTTP connection manager.
     */
    public long getConnectionManagerTimeout() {
        return client.getParams().getLongParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, 0);
    }

    /**
     * Determines whether Nagle's algorithm is to be used. The Nagle's algorithm tries to conserve bandwidth by
     * minimizing the number of segments that are sent. When applications wish to decrease network latency and increase
     * performance, they can disable Nagle's algorithm (that is enable TCP_NODELAY). Data will be sent earlier, at the
     * cost of an increase in bandwidth consumption.
     */
    public void setTcpNoDelay(boolean enable) {
        client.getHttpConnectionManager().getParams().setBooleanParameter(HttpConnectionParams.TCP_NODELAY, enable);
    }

    /**
     * Tests if Nagle's algorithm is to be used.
     */
    public boolean getTcpNoDelay() {
        return client.getHttpConnectionManager().getParams().getBooleanParameter(HttpConnectionParams.TCP_NODELAY,
                                                                                 false);
    }

    /**
     * Return the HttpConnectionManagerParams object of the underlying HttpClient. This enables you to configure options
     * not explicitly exposed by the AbderaClient
     */
    public HttpConnectionManagerParams getHttpConnectionManagerParams() {
        return client.getHttpConnectionManager().getParams();
    }

    /**
     * Return the HttpClientParams object of the underlying HttpClient. This enables you to configure options not
     * explicitly exposed by the AbderaClient
     */
    public HttpClientParams getHttpClientParams() {
        return client.getParams();
    }

    /**
     * Set the maximum number of redirects
     */
    public AbderaClient setMaximumRedirects(int redirects) {
        client.getParams().setIntParameter(HttpClientParams.MAX_REDIRECTS, redirects);
        return this;
    }

    /**
     * Get the maximum number of redirects
     */
    public int getMaximumRedirects() {
        return client.getParams().getIntParameter(HttpClientParams.MAX_REDIRECTS, DEFAULT_MAX_REDIRECTS);
    }

    /**
     * Clear all credentials (including proxy credentials)
     */
    public AbderaClient clearCredentials() {
        client.getState().clearCredentials();
        clearProxyCredentials();
        return this;
    }

    /**
     * Clear proxy credentials
     */
    public AbderaClient clearProxyCredentials() {
        client.getState().clearProxyCredentials();
        return this;
    }

    /**
     * Clear all state (cookies, credentials, etc)
     */
    public AbderaClient clearState() {
        client.getState().clear();
        return this;
    }
}
