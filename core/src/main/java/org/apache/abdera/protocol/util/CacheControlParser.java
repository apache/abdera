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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CacheControlParser implements Iterable<String> {

  public static final HashSet<String> cacheRequestDirectives = 
    new HashSet<String>(
      Arrays.asList(
        new String[] { 
          "no-cache", 
          "no-store", 
          "max-age", 
          "max-stale", 
          "min-fresh", 
          "no-transform", 
          "only-if-cached" }));

  public static final HashSet<String> cacheResponseDirectives = 
    new HashSet<String>(
      Arrays.asList(
        new String[] { 
          "public", 
          "private", 
          "no-cache", 
          "no-store", 
          "no-transform", 
          "must-revalidate", 
          "proxy-revalidate",
          "max-age", 
          "s-maxage" }));

  private static final String REGEX = 
    "\\s*([\\w\\-]+)\\s*(=)?\\s*(\\d+|\\\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)+\\\")?\\s*";

  private static final Pattern pattern = Pattern.compile(REGEX);

  private HashMap<String, String> values = new HashMap<String, String>();

  public CacheControlParser(String value) {
    Matcher matcher = pattern.matcher(value);
    while (matcher.find()) {
      String directive = matcher.group(1);
      if (isDirective(directive)) {
        values.put(directive, matcher.group(3));
      }
    }
  }

  private boolean isDirective(String directive) {
    return cacheRequestDirectives.contains(directive) || 
           cacheResponseDirectives.contains(directive);
  }

  public Map<String,String> getValues() {
    return values;
  }

  public String getValue(String directive) {
    return values.get(directive);
  }
  
  public Iterator<String> iterator() {
    return values.keySet().iterator();
  }
  
}