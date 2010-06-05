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
package org.apache.abdera.protocol.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;

/**
 * <p>
 * Provides a utility class helpful for determining which type of resource the client is requesting. Each resource type
 * (e.g. service doc, collection, entry, edit uri, media resource, etc) is assigned a regex pattern. Given the request
 * URI (path and querystring), this will determine which resource was selected and return an appropriate TargetMatcher.
 * TargetMatcher is essentially just a simplified version of the java.util.regex.Matcher that also specifies the
 * Resource Type.
 * </p>
 * 
 * <pre>
 *  RequestContext request = ...
 *  RegexTargetResolver tr = new RegexTargetResolver();
 *  tr.setPattern("/atom",ResourceType.INTROSPECTION)
 *    .setPattern("/atom/([^/#?]+)",ResourceType.COLLECTION)
 *    .setPattern("/atom/([^/#?]+)/([^/#?]+)",ResourceType.ENTRY)
 *    .setPattern("/atom/([^/#?]+)/([^/#?]+)\\?edit",ResourceType.ENTRY_EDIT)
 *    .setPattern("/atom/([^/#?]+)/([^/#?]+)\\?media",ResourceType.MEDIA)
 *    .setPattern("/atom/([^/#?]+)/([^/#?]+)\\?edit-media",ResourceType.MEDIA_EDIT);
 *  
 *  Target target = tr.resolve(request);
 *  System.out.println(target.getType());
 *  System.out.println(targer.getParameter("foo"));
 * </pre>
 */
public class RegexTargetResolver implements Resolver<Target> {

    protected final Map<Pattern, TargetType> patterns;
    protected final Map<Pattern, String[]> fields;

    public RegexTargetResolver() {
        this.patterns = new HashMap<Pattern, TargetType>();
        this.fields = new HashMap<Pattern, String[]>();
    }

    public RegexTargetResolver(Map<String, TargetType> patterns) {
        this.patterns = new HashMap<Pattern, TargetType>();
        this.fields = new HashMap<Pattern, String[]>();
        for (String p : patterns.keySet()) {
            TargetType type = patterns.get(p);
            setPattern(p, type);
        }
    }

    public RegexTargetResolver setPattern(String pattern, TargetType type) {
        return setPattern(pattern, type, new String[0]);
    }

    public RegexTargetResolver setPattern(String pattern, TargetType type, String... fields) {
        Pattern p = Pattern.compile(pattern);
        this.patterns.put(p, type);
        this.fields.put(p, fields);
        return this;
    }

    public Target resolve(Request request) {
        RequestContext context = (RequestContext)request;
        String uri = context.getTargetPath();
        for (Pattern pattern : patterns.keySet()) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.matches()) {
                TargetType type = this.patterns.get(pattern);
                String[] fields = this.fields.get(pattern);
                return getTarget(type, context, matcher, fields);
            }
        }
        return null;
    }

    protected Target getTarget(TargetType type, RequestContext request, Matcher matcher, String[] fields) {
        return new RegexTarget(type, request, matcher, fields);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Regex Target Resolver:\n");
        for (Pattern pattern : patterns.keySet()) {
            TargetType type = this.patterns.get(pattern);
            String[] fields = this.fields.get(pattern);
            buf.append(pattern.toString() + ", Type: " + type + ", Fields: " + Arrays.toString(fields));
        }
        return buf.toString();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        result = prime * result + ((patterns == null) ? 0 : patterns.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RegexTargetResolver other = (RegexTargetResolver)obj;
        if (fields == null) {
            if (other.fields != null)
                return false;
        } else if (!fields.equals(other.fields))
            return false;
        if (patterns == null) {
            if (other.patterns != null)
                return false;
        } else if (!patterns.equals(other.patterns))
            return false;
        return true;
    }

    public static class RegexTarget extends SimpleTarget implements Target {

        private static final long serialVersionUID = 165211244926064449L;
        protected Matcher matcher;
        protected String[] fields;

        public RegexTarget(TargetType type, RequestContext context, Matcher matcher, String[] fields) {
            super(type, context);
            this.matcher = matcher;
            this.fields = fields;
        }

        public String getParameter(String name) {
            if (fields == null)
                return super.getParameter(name);
            int idx = 0;
            for (int n = 0; n < fields.length; n++)
                if (fields[n].equalsIgnoreCase(name))
                    idx = n + 1;
            return idx > 0 && idx <= matcher.groupCount() ? matcher.group(idx) : super.getParameter(name);
        }

        public String[] getParameterNames() {
            String[] names = super.getParameterNames();
            List<String> list = new ArrayList<String>();
            if (names != null)
                list.addAll(Arrays.asList(names));
            if (fields != null)
                list.addAll(Arrays.asList(fields));
            return list.toArray(new String[list.size()]);
        }

        @Override
        public <T> T getMatcher() {
            return (T)matcher.pattern();
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
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final RegexTarget other = (RegexTarget)obj;
            String m = matcher.group(0);
            String p = matcher.pattern().pattern();
            String m2 = other.matcher.group(0);
            String p2 = other.matcher.pattern().pattern();
            if (!super.equals(obj))
                return false;
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
            StringBuilder buf = new StringBuilder();
            buf.append("RegexTarget[").append(p).append(" ==> ").append(m).append("] = ").append(type.toString())
                .append("\n");
            String[] params = getParameterNames();
            for (String param : params) {
                buf.append("    ").append(param).append(" = ").append(getParameter(param)).append("\n");
            }
            return buf.toString();
        }

        public String getIdentity() {
            return context.getUri().toString();
        }

    }

}
