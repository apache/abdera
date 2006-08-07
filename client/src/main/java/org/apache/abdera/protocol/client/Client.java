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
import java.net.URISyntaxException;

import org.apache.abdera.model.Base;
import org.apache.abdera.protocol.cache.Cache;
import org.apache.abdera.protocol.cache.CacheFactory;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;

public abstract class Client {

  protected Cache cache = null;

  public abstract RequestOptions getDefaultRequestOptions();
  
  public abstract void addCredentials(
    String target, 
    String realm,
    String scheme,
    Credentials credentials) 
      throws URISyntaxException;
  
  public abstract void setAuthenticationSchemePriority(String... scheme);
  
  public abstract void usePreemptiveAuthentication(boolean val);
  
  public abstract void init(String userAgent);
  
  public Cache getCache() {
    if (cache == null) {
      CacheFactory factory = CacheFactory.INSTANCE;
      if (factory != null)
        cache = factory.getCache();
    }
    return cache;
  }
  
  public Response head(
    String uri, 
    RequestOptions options) {
      return execute("HEAD", uri, null, options);
  }
  
  public Response get(
    String uri, 
    RequestOptions options) {
      return execute("GET", uri, null, options);
  }
  
  public Response post(
    String uri, 
    RequestEntity entity, 
    RequestOptions options) {
      return execute("POST", uri, entity, options);
  }

  public Response post(
    String uri, 
    InputStream in, 
    RequestOptions options) {
      return execute("POST", uri, new InputStreamRequestEntity(in), options);
  }

  public Response post(
    String uri, 
    Base base, 
    RequestOptions options) {
      return execute("POST", uri, new BaseRequestEntity(base), options);
  }
    
  public Response put(
    String uri, 
    RequestEntity entity, 
    RequestOptions options) {
      return execute("PUT", uri, entity, options);
  }

  public Response put(
    String uri, 
    InputStream in, 
    RequestOptions options) {
      return execute("PUT", uri, new InputStreamRequestEntity(in), options);
  }

  public Response put(
      String uri, 
      Base base, 
      RequestOptions options) {
    return execute("PUT", uri, new BaseRequestEntity(base), options);
  }
      
  public Response delete(
    String uri, 
    RequestOptions options) {
      return execute("DELETE", uri, null, options);
  }
  
  public Response head(String uri) {
    return head(uri, getDefaultRequestOptions());
  }
  
  public Response get(String uri) {
    return get(uri, getDefaultRequestOptions());
  }
    
  public Response post(
    String uri, 
    RequestEntity entity) {
    return post(uri, entity, getDefaultRequestOptions());
  }
  
  public Response post(
    String uri, 
    InputStream in) {
      return post(uri, in, getDefaultRequestOptions());
  }
  
  public Response post(
    String uri, 
    Base base) {
      return post(uri, base, getDefaultRequestOptions());
  }

  public Response put(
    String uri, 
    RequestEntity entity) {
      return put(uri, entity, getDefaultRequestOptions());
  }
  
  public Response put(
    String uri, 
    InputStream in) {
      return put(uri, in, getDefaultRequestOptions());
  }
  
  public Response put(
    String uri, 
    Base base) {
      return put(uri, base, getDefaultRequestOptions());
  }

  public Response delete(
    String uri) {
      return delete(uri, getDefaultRequestOptions());
  }
  
  public abstract Response execute(
    String method, 
    String uri, 
    RequestEntity entity, 
    RequestOptions options);
  
  public static void registerScheme(String name, Class<AuthScheme> scheme) {
    AuthPolicy.registerAuthScheme(name, scheme);
  }
}
