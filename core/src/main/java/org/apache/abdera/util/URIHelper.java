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
package org.apache.abdera.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class URIHelper {

  private static final Pattern javascript = 
    Pattern.compile(
      "\\s*j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:.*");
  private static final Pattern mailto = 
    Pattern.compile(
      "\\s*m\\s*a\\s*i\\s*l\\s*t\\s*o\\s*:.*");
  
  public static boolean isJavascriptUri(URI uri) {
    if (uri == null) return false;
    return javascript.matcher(uri.toString()).matches();
  }
  
  public static boolean isMailtoUri(URI uri) {
    if (uri == null) return false;
    return mailto.matcher(uri.toString()).matches();
  }
  
  /**
   * Normalize a URI as specified by RFC4287 Section 4.2.6
   */
  public static URI normalize(
    URI uri) 
      throws URISyntaxException {
    if (uri == null) return null;
    // the normalization built into the URI class only gets us part of the way
    uri = uri.normalize();
    String scheme = uri.getScheme().toLowerCase();
    String host = 
      (uri.getHost() != null) ? 
        uri.getHost().toLowerCase() : "";
    int port = -1;
    if ("http".equalsIgnoreCase(scheme) && uri.getPort() != 80) 
      port = uri.getPort();
    else if ("https".equalsIgnoreCase(scheme) && uri.getPort() != 443) 
      port = uri.getPort();
    uri = new URI(
      scheme, 
      uri.getUserInfo(), 
      host, port, 
      uri.getPath(), 
      uri.getQuery(), 
      uri.getFragment());
    return uri;
  }
  
  public static String normalize(String uri) throws URISyntaxException {
    return normalize(new URI(uri)).toString();
  }
}
