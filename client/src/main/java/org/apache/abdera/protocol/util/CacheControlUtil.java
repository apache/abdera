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
package org.apache.abdera.protocol.util;

import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.ResponseBase;

public class CacheControlUtil {

  public static boolean isIdempotent(String method) {
    return (method.equalsIgnoreCase("GET") ||
            method.equalsIgnoreCase("HEAD") ||
            method.equalsIgnoreCase("OPTIONS"));
  }
  
  private static long value(String val) {
    return (val != null) ? Long.parseLong(val) : -1; 
  }
  
  public static void parseCacheControl(
    String cc, 
    RequestOptions options) {
      CacheControlParser parser = new CacheControlParser(cc);
      options.setNoCache(false);
      options.setNoStore(false);
      options.setNoTransform(false);
      options.setOnlyIfCached(false);
      options.setMaxAge(-1);
      options.setMaxStale(-1);
      options.setMinFresh(-1);
      for (String directive : parser) {
        if (directive.equalsIgnoreCase("no-cache")) {
          options.setNoCache(true);
        } else if (directive.equalsIgnoreCase("no-store"))
          options.setNoStore(true);
        else if (directive.equalsIgnoreCase("no-transform"))
          options.setNoTransform(true);
        else if (directive.equalsIgnoreCase("only-if-cached"))
          options.setOnlyIfCached(true);
        else if (directive.equalsIgnoreCase("max-age"))
          options.setMaxAge(value(parser.getValue(directive)));
        else if (directive.equalsIgnoreCase("max-stale"))
          options.setMaxStale(value(parser.getValue(directive)));
        else if (directive.equalsIgnoreCase("min-fresh"))
          options.setMinFresh(value(parser.getValue(directive)));
      }
  }
  
  public static void parseCacheControl(
    String cc, 
    ResponseBase response) {
      if (cc == null) return;
      CacheControlParser parser = new CacheControlParser(cc);
      response.setNoCache(false);
      response.setNoStore(false);
      response.setNoTransform(false);
      response.setMustRevalidate(false);
      response.setPrivate(false);
      response.setPublic(false);
      response.setMaxAge(-1);
      for (String directive : parser) {
        if (directive.equalsIgnoreCase("no-cache")) {
          response.setNoCache(true);
          String value = parser.getValue(directive);
          if (value != null) {
            String[] headers = splitAndTrim(value, ",", true);
            response.setNoCacheHeaders(headers);
          }
        } else if (directive.equalsIgnoreCase("no-store"))
          response.setNoStore(true);
        else if (directive.equalsIgnoreCase("no-transform"))
          response.setNoTransform(true);
        else if (directive.equalsIgnoreCase("must-revalidate"))
          response.setMustRevalidate(true);
        else if (directive.equalsIgnoreCase("public"))
          response.setPublic(true);
        else if (directive.equalsIgnoreCase("private")) {
          response.setPrivate(true);
          String value = parser.getValue(directive);
          if (value != null) {
            String[] headers = splitAndTrim(value, ",", true);
            response.setPrivateHeaders(headers);
          }
        } else if (directive.equalsIgnoreCase("max-age"))
          response.setMaxAge(value(parser.getValue(directive)));
      }
  }
  
  private static String unquote(String s) {
    if (s == null || s.length() == 0) return s;
    if (s.startsWith("\"")) s = s.substring(1);
    if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);
    return s;
  }
  
  public static String[] splitAndTrim(String value, String delim, boolean unquote) {
    String[] headers = (unquote) ? unquote(value).split(delim) : value.split(delim);
    for (int n = 0; n < headers.length; n++) {
      headers[n] = headers[n].trim();
    }
    return headers;
  }
}
