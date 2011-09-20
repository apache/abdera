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
import java.io.InputStream;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.common.http.ResponseType;
import org.apache.abdera2.common.io.Compression;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.parser.ParserOptions;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

class AbderaResponseHandler 
  implements ResponseHandler<Document<? extends Element>> {

  private final Abdera abdera;
  private final ParserOptions options;
  
  public AbderaResponseHandler() {
    this(Abdera.getInstance());
  }
  
  public AbderaResponseHandler(Abdera abdera) {
    this(Abdera.getInstance(),null);
  }
  
  public AbderaResponseHandler(ParserOptions options) {
    this(Abdera.getInstance(),options);
  }
  
  public AbderaResponseHandler(Abdera abdera, ParserOptions options) {
    this.abdera = abdera;
    this.options = null;
  }
  
  public Document<? extends Element> handleResponse(HttpResponse response) throws ClientProtocolException,
      IOException {
    ResponseType type = ResponseType.select(response.getStatusLine().getStatusCode());
    switch(type) {
      case SUCCESSFUL:
        return options == null ? 
            abdera.getParser().parse(getInputStream(response)) :
            abdera.getParser().parse(getInputStream(response), options);
      default:
        EntityUtils.consume(response.getEntity());
        StatusLine status = response.getStatusLine();
        throw new HttpResponseException(status.getStatusCode(),status.getReasonPhrase());
    }
    
  }

  private static InputStream getInputStream(HttpResponse response) throws IOException {
    InputStream in = null;
    Header he = response.getFirstHeader("Content-Encoding");
    String ce = he != null ? he.getValue() : null;
    BufferedHttpEntity buffer = 
      new BufferedHttpEntity(response.getEntity());
    in = buffer.getContent();
    if (ce != null && in != null)
        in = Compression.wrap(in, ce);
    return in;
  }
}
