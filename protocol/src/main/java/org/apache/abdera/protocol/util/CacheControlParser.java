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

public class CacheControlParser implements Iterable<CacheControlParser.Directive> {
  
  public enum Directive {
    MAXAGE, MAXSTALE, MINFRESH, NOCACHE, NOSTORE, NOTRANSFORM, ONLYIFCACHED,
    MUSTREVALIDATE, PRIVATE, PROXYREVALIDATE, PUBLIC, SMAXAGE, UNKNOWN;
    
    public static Directive select(String d) {
      try {
        d = d.toUpperCase().replace("-", "");
        return Directive.valueOf(d);
      } catch (Exception e) {}
      return UNKNOWN;
    }
    
  }
  
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
    
  private static String[] splitAndTrim(String value, String delim, boolean unquote) {
    String[] headers = (unquote) ? unquote(value).split(delim) : value.split(delim);
    for (int n = 0; n < headers.length; n++) {
      headers[n] = headers[n].trim();
    }
    return headers;
  }

}