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

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera2.common.http.Method;
import org.apache.abdera2.common.http.ResponseType;
import org.apache.abdera2.common.protocol.ProtocolException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * A client session. Session's MUST NOT be used by more
 * than one Thread of execution as a time as multiple threads would stomp 
 * all over the shared session context. It is critical to completely
 * consume each ClientResponse before executing an additional request on 
 * the same session.
 */
public class Session {

    protected final Client client;
    protected final HttpContext localContext;

    protected Session(Client client) {
        this.client = client;
        this.localContext = 
          new BasicHttpContext();
    }
    
    private HttpClient getClient() {
      return client.getClient();
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends ClientResponse>T wrap(ClientResponse resp) {
      return (T)resp;
    }
    
    /**
     * Sends an HTTP HEAD request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public <T extends ClientResponse>T head(String uri, RequestOptions options) {
        return wrap(execute("HEAD", uri, (HttpEntity)null, options));
    }
    
    /**
     * Sends an HTTP GET request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public <T extends ClientResponse>T get(String uri, RequestOptions options) {
        return wrap(execute("GET", uri, (HttpEntity)null, options));
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T post(String uri, HttpEntity entity, RequestOptions options) {
        return wrap(execute("POST", uri, entity, options));
    }

    /**
     * Sends an HTTP POST request to the specified URI.
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T post(String uri, InputStream in, RequestOptions options) {
        return wrap(execute("POST", uri, new InputStreamEntity(in,-1), options));
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T put(String uri, HttpEntity entity, RequestOptions options) {
        return wrap(execute("PUT", uri, entity, options));
    }

    /**
     * Sends an HTTP PUT request to the specified URI.
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     * @param options The request options
     */
    public <T extends ClientResponse>T put(String uri, InputStream in, RequestOptions options) {
        return wrap(execute("PUT", uri, new InputStreamEntity(in,-1), options));
    }

    /**
     * Sends an HTTP DELETE request to the specified URI.
     * 
     * @param uri The request URI
     * @param options The request options
     */
    public <T extends ClientResponse>T delete(String uri, RequestOptions options) {
        return wrap(execute("DELETE", uri, (HttpEntity)null, options));
    }

    /**
     * Sends an HTTP HEAD request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public <T extends ClientResponse>T head(String uri) {
        return wrap(head(uri, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP GET request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public <T extends ClientResponse>T get(String uri) {
        return wrap(get(uri, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     */
    public <T extends ClientResponse>T post(String uri, HttpEntity entity) {
        return wrap(post(uri, entity, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP POST request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     */
    public <T extends ClientResponse>T post(String uri, InputStream in) {
        return wrap(post(uri, in, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param entity A RequestEntity object providing the payload of the request
     */
    public <T extends ClientResponse>T put(String uri, HttpEntity entity) {
        return wrap(put(uri, entity, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP PUT request to the specified URI using the default options
     * 
     * @param uri The request URI
     * @param in An InputStream providing the payload of the request
     */
    public <T extends ClientResponse>T put(String uri, InputStream in) {
        return wrap(put(uri, in, getDefaultRequestOptions()));
    }

    /**
     * Sends an HTTP DELETE request to the specified URI using the default options
     * 
     * @param uri The request URI
     */
    public <T extends ClientResponse>T delete(String uri) {
        return wrap(delete(uri, getDefaultRequestOptions()));
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
    public <T extends ClientResponse>T execute(
        String method, 
        String uri, 
        InputStream in, 
        RequestOptions options) {
        if (options == null)
          options = getDefaultRequestOptions();
        InputStreamEntity re = 
          new InputStreamEntity(in, -1);
        re.setContentType(
          options.getContentType().toString());
        return wrap(execute(
          method, uri, re, options));
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
    public <T extends ClientResponse>T execute(
        Method method, 
        String uri, 
        InputStream in, 
        RequestOptions options) {
        return wrap(execute(method.name(),uri,in,options));
    }

    public <T extends ClientResponse>T execute(
        Method method, 
        String uri, 
        HttpEntity entity, 
        RequestOptions options) {
      return wrap(execute(method.name(),uri,entity,options));
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
    public <T extends ClientResponse>T execute(
        String method, 
        String uri, 
        HttpEntity entity, 
        RequestOptions options) {
        options =
          options != null ? 
            options : 
            getDefaultRequestOptions();
        try {
            HttpUriRequest request = 
              RequestHelper.createRequest(
                  method, uri, entity, options);
            HttpResponse response = 
              getClient().execute(request, localContext);
            ClientResponse resp = 
              wrap(new ClientResponseImpl(
                this, response, method, localContext));
            return checkRequestException(resp, options);
        } catch (RuntimeException r) {
            throw r;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends ClientResponse>T checkRequestException(ClientResponse response, RequestOptions options) {
      if (response == null)
          return (T)response;
      ResponseType type = response.getType();
      if ((type.equals(ResponseType.CLIENT_ERROR) && options.is4xxRequestException()) || (type
          .equals(ResponseType.SERVER_ERROR) && options.is5xxRequestException())) {
        throw new ProtocolException(response.getStatus(),response.getStatusText());
      }
      return (T)response;
  }
    
    /**
     * Get a copy of the default request options
     */
    public RequestOptions getDefaultRequestOptions() {
        return RequestHelper.createDefaultRequestOptions();
    }

    public void usePreemptiveAuthentication(String target, String realm) throws URISyntaxException {
        AuthCache cache = (AuthCache) localContext.getAttribute(ClientContext.AUTH_CACHE);
        if (cache == null) {
          String host = AuthScope.ANY_HOST;
          int port = AuthScope.ANY_PORT;
          if (target != null) {
              URI uri = new URI(target);
              host = uri.getHost();
              port = uri.getPort();
          }
          BasicScheme basicAuth = new BasicScheme();
          HttpHost targetHost = 
            new HttpHost(host,port,basicAuth.getSchemeName());
          cache = new BasicAuthCache();
          cache.put(targetHost, basicAuth);
          localContext.setAttribute(ClientContext.AUTH_CACHE, cache);
        }
    }
    
    public void doFormLogin(String uri, String userid, String password) {
      doFormLogin(uri, "j_username", userid, "j_password", password);
    }
    
    public void doFormLogin(String uri, String userfield, String userid, String passfield, String password) {
      try {
        HttpPost httpost = new HttpPost(uri);
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair(userfield, userid));
        nvps.add(new BasicNameValuePair(passfield, password));
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = getClient().execute(httpost,localContext);
        HttpEntity entity = response.getEntity();
        EntityUtils.consume(entity);
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }
}
