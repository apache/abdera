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
package org.apache.abdera.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.abdera.server.target.Target;

public interface RequestContext {
  
  Target getTarget();
  
  /**
   * Returns the method 
   */
  String getMethod();
  
  /**
   * Returns the complete Request URI
   */
  URI getUri();
  
  /**
   * Returns the server base URI
   */
  URI getBaseUri();
  
  /**
   * Returns the relative URI (without scheme, host, or port)
   */
  URI getPathInfo();
  
  /**
   * Returns a request parameter (e.g. query string param)
   */
  String getParameter(String name);
  
  /**
   * Returns a list of request parameter values (e.g. query string params)
   */
  List<String> getParameters(String name);

  /**
   * Returns a listing of request parameter names
   */
  List<String> getParameterNames();
  
  /**
   * Returns a request header
   */
  String getHeader(String name);
  
  /**
   * Returns a list of request header values
   */
  List<String> getHeaders(String name);
  
  /**
   * Returns a list of request header names
   */
  List<String> getHeaderNames();
  
  /**
   * Returns the input stream for the request
   */
  InputStream getInputStream() throws IOException;
  
  String getAccept();
  
  String getAcceptCharset();
  
  String getAcceptEncoding();
  
  String getAcceptLanguage();
  
  String getAuthorization();
  
  String getCacheControl();
  
  String getContentType();
  
  Date getDateHeader(String name);
  
  String getIfMatch();
  
  Date getIfModifiedSince();
  
  String getIfNoneMatch();
  
  Date getIfUnmodifiedSince();
  
  long getMaxAge();
  
  long getMaxStale();
  
  long getMinFresh();
  
  boolean getNoCache();
  
  boolean getNoStore();
  
  boolean getNoTransform();
  
}
