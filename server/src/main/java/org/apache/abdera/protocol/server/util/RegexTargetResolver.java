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

import org.apache.abdera.protocol.server.provider.AbstractTarget;
import org.apache.abdera.protocol.server.provider.RequestContext;
import org.apache.abdera.protocol.server.provider.Target;
import org.apache.abdera.protocol.server.provider.TargetResolver;
import org.apache.abdera.protocol.server.provider.TargetType;

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
 *  RequestContext request = ...
 *  RegexTargetResolver tr = new RegexTargetResolver();
 *  tr.setPattern("/atom",ResourceType.INTROSPECTION);
 *  tr.setPattern("/atom/([^/#?]+)",ResourceType.COLLECTION);
 *  tr.setPattern("/atom/([^/#?]+)/([^/#?]+)",ResourceType.ENTRY);
 *  tr.setPattern("/atom/([^/#?]+)/([^/#?]+)\\?edit",ResourceType.ENTRY_EDIT);
 *  tr.setPattern("/atom/([^/#?]+)/([^/#?]+)\\?media",ResourceType.MEDIA);
 *  tr.setPattern("/atom/([^/#?]+)/([^/#?]+)\\?edit-media",ResourceType.MEDIA_EDIT);
 *  
 *  Target target = tr.resolve(request);
 *  System.out.println(target.getType());
 *  System.out.println(targer.getParameter("foo"));
 * </pre>
 *  
 */
public class RegexTargetResolver 
  implements TargetResolver {

  private final Map<Pattern, TargetType> patterns;
  private String contextPath;
  
  public RegexTargetResolver(String contextPath) {
    this.contextPath = contextPath;
    this.patterns = new HashMap<Pattern, TargetType>();
  }
  
  public RegexTargetResolver(String contextPath, Map<String, TargetType> patterns) {
    this.contextPath = contextPath;
    this.patterns = new HashMap<Pattern, TargetType>();
    for (String p : patterns.keySet()) {
      TargetType type = patterns.get(p);
      setPattern(p,type);
    }
  }
  
  public synchronized void setPattern(String pattern, TargetType type) {
    Pattern p = Pattern.compile(getContextPath() + pattern);
    this.patterns.put(p,type);
  }
  
  public Target resolve(RequestContext request) {
    String uri = request.getUri().toString();
    for (Pattern pattern : patterns.keySet()) {
      Matcher matcher = pattern.matcher(uri);
      if (matcher.matches()) {
        TargetType type = patterns.get(pattern);
        return getTarget(type, request, matcher);
      }
    }
    return null;
  }
  
  protected Target getTarget(
    TargetType type, 
    RequestContext request, 
    Matcher matcher) {
      return new RegexTarget(type, request, matcher);
  }
  
  public static class RegexTarget
    extends AbstractTarget
    implements Target {
    
    private static final long serialVersionUID = 165211244926064449L;
    protected transient Matcher matcher;
    
    protected RegexTarget(
      TargetType type, 
      RequestContext context, 
      Matcher matcher) {
        super(type, context);
        this.matcher = matcher;
    }
    
    @Override
    public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      String m = matcher.group(0);
      String p = matcher.pattern().pattern();
      result = PRIME * result + super.hashCode();
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
      if (!super.equals(obj)) return false;
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
      String[] params = getParameterNames();
      for (String param : params) {
        buf.append("    ");
        buf.append(param);
        buf.append(" = ");
        buf.append(getParameter(param));
        buf.append("\n");
      }
      return buf.toString();
    }
    
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }
  
  protected String getContextPath() {
    return this.contextPath;
  }
  
}
