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

import org.apache.abdera.protocol.cache.Cache;
import org.apache.abdera.protocol.cache.CacheDisposition;
import org.apache.abdera.protocol.cache.CachedResponse;
import org.apache.abdera.protocol.util.CacheControlUtil;
import org.apache.abdera.protocol.util.ExtensionMethod;
import org.apache.abdera.util.Version;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

public class CommonsClient extends Client {

  private HttpClient client = null;
  
  public CommonsClient() {
    this(Version.APP_NAME + "/" + Version.VERSION);
  }
  
  public CommonsClient(String userAgent) {
    init(userAgent);
  }
  
  public void usePreemptiveAuthentication(boolean val) {
    client.getParams().setAuthenticationPreemptive(val);
  }
  
  @Override
  public void init(String userAgent) {
    client = new HttpClient();
    client.getParams().setParameter(
      HttpClientParams.USER_AGENT, 
      userAgent);
    client.getParams().setBooleanParameter(
      HttpClientParams.USE_EXPECT_CONTINUE, true);    
  }
  
  @Override
  public Response execute(
    String method, 
    String uri, 
    RequestEntity entity,
    RequestOptions options) {
      try {
        if (options == null) options = getDefaultRequestOptions();
        Response response = null;
        CachedResponse cached_response = null;
        Cache cache = getCache();
        CacheDisposition disp = CacheDisposition.TRANSPARENT;
        if (CacheControlUtil.isIdempotent(method) &&
            cache != null && 
            options.getNoCache() == false && 
            options.getNoStore() == false &&
            options.getUseLocalCache()) {
          disp = cache.getDisposition(uri,options);
          cached_response = cache.get(uri, options);
          switch(disp) {
            case FRESH:
              //System.out.println("____ CACHE HIT: FRESH");
              response = cached_response;
              break;
            case STALE:
              //System.out.println("____ CACHE HIT: STALE, Need to revalidate");
              if (options == null) 
                options = getDefaultRequestOptions();
              if (cached_response.getLastModified() != null)
                options.setIfModifiedSince(cached_response.getLastModified());
              if (cached_response.getEntityTag() != null)
                options.setIfNoneMatch(cached_response.getEntityTag());
              break;
            default:
              //System.out.println("____ CACHE MISS: TRANSPARENT");
          }          
        }
        if (response == null) {
          HttpMethod httpMethod = createMethod(method, uri, entity);
          String[] headers = options.getHeaderNames();
          for (String header : headers) {
            String[] values = options.getHeaders(header);
            for (String value : values) {
              httpMethod.addRequestHeader(header, value);
            }
          }
          String cc = options.getCacheControl();
          if (cc != null && cc.length() != 0)
            httpMethod.setRequestHeader("Cache-Control", cc);
          int n = client.executeMethod(httpMethod);
          if (n == 304 || n == 412 && 
              cached_response != null &&
              disp.equals(CacheDisposition.STALE)) {
            response = cached_response;
          } else {
            response = new CommonsResponse(httpMethod);
            if (cache != null) 
              response = cache.update(
                method, uri, options, response);
          }
        }
        return response;
      } catch (Throwable t) {
        throw new ClientException(t);
      }
  }

  private HttpMethod createMethod(
    String method, 
    String uri,
    RequestEntity entity) {
      if (method == null) return null;
      if (method.equalsIgnoreCase("GET")) {
        return new GetMethod(uri);
      } else if (method.equalsIgnoreCase("POST")) {
        EntityEnclosingMethod m = new PostMethod(uri);
        if (entity != null)
          m.setRequestEntity(entity);
        return m;
      } else if (method.equalsIgnoreCase("PUT")) {
        EntityEnclosingMethod m = new PutMethod(uri);
        if (entity != null)
          m.setRequestEntity(entity);
        return m;
      } else if (method.equalsIgnoreCase("DELETE")) {
        return new DeleteMethod(uri);
      } else if (method.equalsIgnoreCase("HEAD")) {
        return new HeadMethod(uri);
      } else if (method.equalsIgnoreCase("OPTIONS")) {
        return new OptionsMethod(uri);
      } else if (method.equalsIgnoreCase("TRACE")) {
        return new TraceMethod(uri);
      } else {
        EntityEnclosingMethod m = new ExtensionMethod(method.toUpperCase());
        if (entity != null)
          m.setRequestEntity(entity);
        return m;
      }
  }
  
  @Override
  public RequestOptions getDefaultRequestOptions() {
    RequestOptions options = new RequestOptions();
    options.setAcceptEncoding(
      "gzip;q=1.0", 
      "deflate;q=1.0", 
      "zip;q=0.5");
    options.setAccept(
      "application/atom+xml",
      "application/atomserv+xml",
      "application/xml;q=0.8",
      "text/xml;q=0.5",
      "*/*;q=0.1");
    options.setAcceptCharset(
      "utf-8", "*;q=0.5");
    return options;
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
}
