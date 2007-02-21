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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CacheControlUtil {

  public static boolean isIdempotent(String method) {
    return (method.equalsIgnoreCase("GET") ||
            method.equalsIgnoreCase("HEAD") ||
            method.equalsIgnoreCase("OPTIONS"));
  }
  
  private static long value(String val) {
    return (val != null) ? Long.parseLong(val) : -1; 
  }
  
  private static void append(StringBuffer buf, String value) {
    if (buf.length() > 0) buf.append(", ");
    buf.append(value);
  }

  public static String buildCacheControl(AbstractRequest request) {
    StringBuffer buf = new StringBuffer();
    if (request.isNoCache()) append(buf,"no-cache");
    if (request.isNoStore()) append(buf,"no-store");
    if (request.isNoTransform()) append(buf, "no-transform");
    if (request.isOnlyIfCached()) append(buf, "only-if-cached");
    if (request.getMaxAge() != -1) append(buf, "max-age=" + request.getMaxAge());
    if (request.getMaxStale() != -1) append(buf, "max-stale=" + request.getMaxStale());
    if (request.getMinFresh() != -1) append(buf, "min-fresh=" + request.getMinFresh());
    return buf.toString();
  }
  
  public static void parseCacheControl(
    String cc, 
    AbstractRequest request) {
      if (cc == null || cc.length() == 0) return;
      CacheControlParser parser = new CacheControlParser(cc);
      request.setNoCache(false);
      request.setNoStore(false);
      request.setNoTransform(false);
      request.setOnlyIfCached(false);
      request.setMaxAge(-1);
      request.setMaxStale(-1);
      request.setMinFresh(-1);
      for (Directive directive : parser) {
        switch(directive) {
          case NOCACHE:      request.setNoCache(true); break;
          case NOSTORE:      request.setNoStore(true); break;
          case NOTRANSFORM:  request.setNoTransform(true); break;
          case ONLYIFCACHED: request.setOnlyIfCached(true); break;
          case MAXAGE:       request.setMaxAge(value(parser.getValue(directive))); break;
          case MAXSTALE:     request.setMaxStale(value(parser.getValue(directive))); break;
          case MINFRESH:     request.setMinFresh(value(parser.getValue(directive))); break;
        }
      }
  }
  
  public static void parseCacheControl(
    String cc, 
    AbstractResponse response) {
      if (cc == null) return;
      CacheControlParser parser = new CacheControlParser(cc);
      response.setNoCache(false);
      response.setNoStore(false);
      response.setNoTransform(false);
      response.setMustRevalidate(false);
      response.setPrivate(false);
      response.setPublic(false);
      response.setMaxAge(-1);
      for (Directive directive : parser) {
        switch(directive) {
          case NOCACHE:
            response.setNoCache(true);
            response.setNoCacheHeaders(parser.getValues(directive));
            break;
          case NOSTORE:
            response.setNoStore(true); break;
          case NOTRANSFORM:
            response.setNoTransform(true); break;
          case MUSTREVALIDATE:
            response.setMustRevalidate(true); break;
          case PUBLIC:
            response.setPublic(true); break;
          case PRIVATE:
            response.setPrivate(true);
            response.setPrivateHeaders(parser.getValues(directive));
            break;
          case MAXAGE:
            response.setMaxAge(value(parser.getValue(directive)));
            break;
        }
      }
  }

  
  public enum Directive {
    MAXAGE, MAXSTALE, MINFRESH, NOCACHE, NOSTORE, NOTRANSFORM, ONLYIFCACHED,
    MUSTREVALIDATE, PRIVATE, PROXYREVALIDATE, PUBLIC, SMAXAGE, UNKNOWN;
    
    public static Directive select(String d) {
      try {
        d = d.toUpperCase().replaceAll("-", "");
        return Directive.valueOf(d);
      } catch (Exception e) {}
      return UNKNOWN;
    }
  }
  
  public static class CacheControlParser 
    implements Iterable<Directive> {
        
    private static final String REGEX = 
      "\\s*([\\w\\-]+)\\s*(=)?\\s*(\\d+|\\\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)+\\\")?\\s*";

    private static final Pattern pattern = Pattern.compile(REGEX);

    private HashMap<Directive, String> values = new HashMap<Directive, String>();

    public CacheControlParser(String value) {
      Matcher matcher = pattern.matcher(value);
      while (matcher.find()) {
        String d = matcher.group(1);
        Directive directive = Directive.select(d);
        if (directive != Directive.UNKNOWN) {
          values.put(directive, matcher.group(3));
        }
      }
    }

    public Map<Directive,String> getValues() {
      return values;
    }

    public String getValue(Directive directive) {
      return values.get(directive);
    }
    
    public Iterator<Directive> iterator() {
      return values.keySet().iterator();
    }
    
    public String[] getValues(Directive directive) {
      String value = getValue(directive);
      if (value != null) {
        return splitAndTrim(value, ",", true);
      }
      return null;
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
}
