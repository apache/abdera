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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
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
 *  tr.setPattern(ResourceType.INTROSPECTION, "/atom");
 *  tr.setPattern(ResourceType.COLLECTION, "/atom/([^/#?]+)");
 *  tr.setPattern(ResourceType.ENTRY, "/atom/([^/#?]+)/([^/#?]+)");
 *  tr.setPattern(ResourceType.ENTRY_EDIT, "/atom/([^/#?]+)/([^/#?]+)\\?edit");
 *  tr.setPattern(ResourceType.MEDIA,"/atom/([^/#?]+)/([^/#?]+)\\?media");
 *  tr.setPattern(ResourceType.MEDIA_EDIT,"/atom/([^/#?]+)/([^/#?]+)\\?edit-media");
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
    
    private static final long serialVersionUID = 165211244926064449L;
    transient Matcher matcher;
    private ResourceType type;
    
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

    public Iterator<String> iterator() {
      return new TargetIterator(this);
    }
    
    private void writeObject(ObjectOutputStream out) 
      throws IOException {
        out.defaultWriteObject();
        out.writeObject(matcher.pattern().pattern());
        out.writeObject(matcher.group(0));
    }

    private void readObject(ObjectInputStream in) 
      throws IOException, 
             ClassNotFoundException {
       in.defaultReadObject();
       String p = (String) in.readObject();
       String v = (String) in.readObject();
       Pattern pattern = Pattern.compile(p);
       matcher = pattern.matcher(v);
       matcher.matches();
    }

    @Override
    public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      String m = matcher.group(0);
      String p = matcher.pattern().pattern();
      result = PRIME * result + ((m == null) ? 0 : m.hashCode());
      result = PRIME * result + ((p == null) ? 0 : p.hashCode());
      result = PRIME * result + ((type == null) ? 0 : type.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final RegexTarget other = (RegexTarget) obj;
      String m = matcher.group(0);
      String p = matcher.pattern().pattern();
      String m2 = other.matcher.group(0);
      String p2 = other.matcher.pattern().pattern();
      if (m == null) {
        if (m2 != null)
          return false;
      } else if (!m.equals(m2))
        return false;
      if (p == null) {
        if (p2 != null)
          return false;
      } else if (!p.equals(p2))
        return false;
      if (type == null) {
        if (other.type != null)
          return false;
      } else if (!type.equals(other.type))
        return false;
      return true;
    }
    
    public String toString() {
      String m = matcher.group(0);
      String p = matcher.pattern().pattern();
      StringBuffer buf = new StringBuffer();
      buf.append("RegexTarget[");
      buf.append(p);
      buf.append(" ==> ");
      buf.append(m);
      buf.append("] = ");
      buf.append(type.toString());
      buf.append("\n");
      int n = -1;
      while(hasValue(++n)) {
        buf.append("    ");
        buf.append(n);
        buf.append(" = ");
        buf.append(getValue(n));
        buf.append("\n");
      }
      return buf.toString();
    }
  }
  
}
