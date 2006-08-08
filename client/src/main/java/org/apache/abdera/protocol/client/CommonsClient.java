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
import org.apache.abdera.protocol.util.MethodHelper;
import org.apache.abdera.util.Version;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.RequestEntity;
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
  
  private boolean useCache(
    String method, 
    RequestOptions options) {
      return (CacheControlUtil.isIdempotent(method)) &&
        !options.getNoCache() &&
        !options.getNoStore() &&
        options.getUseLocalCache();
  }
  
  @Override
  public Response execute(
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
        switch(disp) {
          case FRESH:                                                            // CACHE HIT: FRESH
            if (cached_response != null)
              return cached_response;
          case STALE:                                                            // CACHE HIT: STALE
            // revalidate the cached entry
            options.setIfModifiedSince(cached_response.getLastModified());
            options.setIfNoneMatch(cached_response.getEntityTag());
          default:                                                               // CACHE MISS
            HttpMethod httpMethod = 
              MethodHelper.createMethod(
                method, uri, entity, options);
            client.executeMethod(httpMethod);
            Response response = new CommonsResponse(httpMethod);
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
}
