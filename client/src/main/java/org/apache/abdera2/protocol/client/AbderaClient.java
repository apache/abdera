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

import java.io.IOException;
import org.apache.abdera2.Abdera;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.parser.ParserOptions;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * The default Abdera Client.
 */
public class AbderaClient extends ClientWrapper {

  protected Abdera abdera;
  
  public AbderaClient() {
    this(
      new BasicClient(), 
      Abdera.getInstance(), 
      DEFAULT_USER_AGENT);
  }
  
  public AbderaClient(Client client) {
    this(
      client, 
      Abdera.getInstance(), 
      DEFAULT_USER_AGENT);
  }
  
  public AbderaClient(String useragent) {
    this(
      new BasicClient(), 
      Abdera.getInstance(), 
      useragent);
  }
  
  public AbderaClient(
    Client client, 
    String useragent) {
      this(client, Abdera.getInstance(), useragent);
  }
  
  public AbderaClient(
      Client client, 
      Abdera abdera, 
      String useragent) {
        super(client);
        this.abdera = abdera;
  }
  
  public AbderaClient(DefaultHttpClient client) {
    this(new BasicClient(client), Abdera.getInstance(), DEFAULT_USER_AGENT);
  }
  
  public AbderaClient(Abdera abdera, DefaultHttpClient client) {
    this(new BasicClient(client), abdera, DEFAULT_USER_AGENT);
  }
  
  public AbderaClient(Abdera abdera) {
    this(new BasicClient(), abdera, DEFAULT_USER_AGENT);
  }

  public Abdera getAbdera() {
    return abdera;
  }
    
  /**
   * Convenience method for performing a session-less get on a given URI.
   */
  public <T extends Element>Document<T> get(String uri) 
    throws ClientProtocolException, IOException {
      return get(uri,null,null);
  }

  /**
   * Convenience method for performing a session-less get on a given URI.
   */
  public <T extends Element>Document<T> get(String uri, ParserOptions options) 
    throws ClientProtocolException, IOException {
      return get(uri,null,options);
  }
  
  /**
   * Convenience method for performing a session-less get on a given URI.
   */
  public <T extends Element>Document<T> get(
    String uri, 
    RequestOptions options) {
      return get(uri,options,null);
  }
  
  /**
   * Convenience method for performing a session-less get on a given URI.
   */
  @SuppressWarnings("unchecked")
  public <T extends Element>Document<T> get(
    String uri, 
    RequestOptions options, 
    ParserOptions parserOptions) {
    try {
      HttpUriRequest request = 
        RequestHelper.createRequest(
          "GET", uri, null, options);
      AbderaResponseHandler h = 
        parserOptions != null ?
        new AbderaResponseHandler(abdera,parserOptions) :
        new AbderaResponseHandler(abdera);
      return (Document<T>) getClient().execute(request,h);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Session>T newSession() {
    return (T)new AbderaSession(this);
  }

  
}
