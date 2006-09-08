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
package org.apache.abdera.protocol.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.protocol.server.target.Target;
import org.apache.abdera.protocol.server.target.TargetResolver;

/**
 * <p>Provides a utility class helpful for determining which type of resource
 * the client is requesting.  Each resource type (e.g. service doc, collection,
 * entry, edit uri, media resource, etc) is assigned a regex pattern.  Given
 * the request URI (path and querystring), this will determine which resource
 * was selected and return an appropriate TargetMatcher.  TargetMatcher is 
 * essentially just a simplified version of the java.util.regex.Matcher that
 * also specifies the Resource Type.</p>
 * 
 * <pre>
 *  RegexTargetResolver tr = new RegexTargetResolver();
 *  tr.addPattern(ResourceType.INTROSPECTION, "/atom");
 *  tr.addPattern(ResourceType.COLLECTION, "/atom/([^/#?]+)");
 *  tr.addPattern(ResourceType.ENTRY, "/atom/([^/#?]+)/([^/#?]+)");
 *  tr.addPattern(ResourceType.ENTRY_EDIT, "/atom/([^/#?]+)/([^/#?]+)\\?edit");
 *  tr.addPattern(ResourceType.MEDIA,"/atom/([^/#?]+)/([^/#?]+)\\?media");
 *  tr.addPattern(ResourceType.MEDIA_EDIT,"/atom/([^/#?]+)/([^/#?]+)\\?edit-media");
 *  
 *  Target target = tr.resolve("/atom/foo");
 *  System.out.println(target.getResourceType());
 *  System.out.println(targer.getValue(1));  // foo
 * </pre>
 *  
 */
public class RegexTargetResolver implements TargetResolver {

  private final Map<ResourceType,Pattern> patterns;
  
  public RegexTargetResolver() {
    this.patterns = new HashMap<ResourceType,Pattern>();
  }
  
  public RegexTargetResolver(Map<ResourceType,String> patterns) {
    this.patterns = new HashMap<ResourceType,Pattern>();
    for (ResourceType type : patterns.keySet()) {
      String p = patterns.get(type);
      Pattern pattern = Pattern.compile(p);
      this.patterns.put(type, pattern);
    }
  }
  
  public synchronized void setPattern(ResourceType type, String pattern) {
    Pattern p = Pattern.compile(pattern);
    this.patterns.put(type, p);
  }
  
  public Target resolve(String path_info) {
    for (ResourceType type : patterns.keySet()) {
      Pattern pattern = patterns.get(type);
      Matcher matcher = pattern.matcher(path_info);
      if (matcher.matches()) return new RegexTarget(type, matcher);
    }
    return null;
  }
  
  public static class RegexTarget implements Target {
    
    final Matcher matcher;
    final ResourceType type;
    
    RegexTarget(ResourceType type, Matcher matcher) {
      this.type = type;
      this.matcher = matcher;
    }
    
    public ResourceType getResourceType() {
      return this.type;
    }
    
    public String getValue(int token) {
      try {
        return this.matcher.group(token);
      } catch (IndexOutOfBoundsException e) {
        return null;
      }
    }
    
    public boolean hasValue(int token) {
      return getValue(token) != null;
    }
  }
  
}
