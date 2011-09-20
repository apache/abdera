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
package org.apache.abdera2.common.templates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.anno.URIRoute;

/**
 * A type of URI Template loosely based on Ruby on Rails style Routes. Example: Route feed_route = new
 * Route("feed",":feed/:entry");
 */
@SuppressWarnings("unchecked")
public class Route<T> implements Iterable<String>, Cloneable, Serializable {

    private static final long serialVersionUID = -8979172281494208841L;

    private static final Pattern VARIABLE = Pattern.compile("[\\*\\:](?:\\()?[0-9a-zA-Z]+(?:\\))?");
    private static final String VARIABLE_CONTENT_MATCH = "([^:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;\\=]+)";
    private static final String VARIABLE_CONTENT_PARSE = "([^:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;\\=]*)";
    private final T key;
    private final String pattern;
    private final String[] tokens;
    private final String[] variables;
    private final Pattern regexMatch;
    private final Pattern regexParse;

    private Map<String, String> requirements;

    private MapContext defaultValues;

    public Route(T key, String pattern) {
        this(key, pattern, null, null);
    }

    public Route(T key, String pattern, Map<String, Object> defaultValues, Map<String, String> requirements) {
        this.key = key;
        this.pattern = pattern;
        this.tokens = initTokens();
        this.variables = initVariables();
        this.defaultValues = defaultValues != null ? new MapContext(defaultValues,true) : null;
        this.requirements = requirements;
        this.regexMatch = initRegexMatch();
        this.regexParse = initRegexParse();
    }

    private String[] initTokens() {
        Matcher matcher = VARIABLE.matcher(pattern);
        List<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            String token = matcher.group();
            if (!tokens.contains(token))
                tokens.add(token);
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private String[] initVariables() {
        List<String> list = new ArrayList<String>();
        for (String token : this) {
            String var = var(token);
            if (!list.contains(var))
                list.add(var);
        }
        String[] vars = list.toArray(new String[list.size()]);
        Arrays.sort(vars);
        return vars;
    }

    private Pattern initRegexMatch() {
        StringBuffer match = new StringBuffer();
        int cnt = 0;
        for (String part : VARIABLE.split(pattern)) {
            match.append(Pattern.quote(part));
            if (cnt++ < tokens.length) {
                match.append(VARIABLE_CONTENT_MATCH);
            }
        }
        return Pattern.compile(match.toString());
    }

    private Pattern initRegexParse() {
        StringBuffer parse = new StringBuffer();
        int cnt = 0;
        for (String part : VARIABLE.split(pattern)) {
            parse.append(Pattern.quote(part));
            if (cnt++ < tokens.length) {
                parse.append(VARIABLE_CONTENT_PARSE);
            }
        }
        return Pattern.compile(parse.toString());
    }

    /**
     * Returns true if the given uri matches the route pattern
     */
    public boolean match(String uri) {
        return regexMatch.matcher(uri).matches() && matchRequirements(uri);
    }

    /**
     * Parses the given uri using the route pattern
     */
    public Map<String, String> parse(String uri) {
        HashMap<String, String> vars = new HashMap<String, String>();
        Matcher matcher = regexParse.matcher(uri);
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                vars.put(var(tokens[i]), matcher.group(i + 1).length() > 0 ? matcher.group(i + 1) : null);
            }
        }

        return vars;
    }

    /**
     * Expand the route pattern given the specified context
     */
    public String expand(Context context) {
        String pattern = this.pattern;
        if (this.defaultValues != null) {
          context = new DefaultingContext(context,this.defaultValues);
        }
        for (String token : this) {
            String var = var(token);
            Expression exp = new Expression(var);
            String val = exp.evaluate(context);
            pattern = replace(pattern, token, val!=null?val:getDefaultValue(var));
        }
        StringBuffer buf = new StringBuffer(pattern);
        boolean qs = false;
        for (String var : context) {
            if (Arrays.binarySearch(variables, var) < 0) {
                if (!qs) {
                    buf.append("?");
                    qs = true;
                } else {
                    buf.append("&");
                }
                Expression exp = new Expression(var);
                String val = exp.evaluate(context);
                if (val != null)
                  buf.append(var).append("=").append(val);
            }
        }

        return buf.toString();
    }

    public String getDefaultValue(String var) {
        if (defaultValues == null)
            return null;

        return defaultValues.resolve(var);
    }

    public String getRequirement(String var) {
        if (requirements == null)
            return null;

        return requirements.get(var);
    }

    private String var(String token) {
        token = token.substring(1);
        if (token.startsWith("("))
            token = token.substring(1);
        if (token.endsWith(")"))
            token = token.substring(0, token.length() - 1);
        return token;
    }

    /**
     * Expand the route pattern given the specified context object
     **/
    public String expand(Object object) {
        return expand(object, false);
    }

    /**
     * Expand the route pattern using IRI escaping rules
     */
    @SuppressWarnings("rawtypes")
    public String expand(Object object, boolean isiri) {
        return expand(object instanceof Context ? (Context)object : object instanceof Map
            ? new MapContext((Map)object, isiri) : new ObjectContext(object, isiri));
    }

    private String replace(String pattern, String token, String value) {
        return pattern.replace(token, value);
    }

    public T getKey() {
        return key;
    }

    public String getPattern() {
        return pattern;
    }

    public Iterator<String> iterator() {
        return Arrays.asList(tokens).iterator();
    }

    public String[] getVariables() {
        return variables;
    }

    public Map<String, Object> getDefaultValues() {
        return defaultValues;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public Route<T> clone() {
        try {
            return (Route<T>)super.clone();
        } catch (Throwable e) {
            return new Route<T>(key, pattern); // not going to happen, but just in case
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Route<T> other = (Route<T>)obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (pattern == null) {
            if (other.pattern != null)
                return false;
        } else if (!pattern.equals(other.pattern))
            return false;
        return true;
    }

    public String toString() {
        return pattern;
    }

    @SuppressWarnings("rawtypes")
    public static String expand(String pattern, Context context) {
        if (context == null || pattern == null)
            throw new IllegalArgumentException();
        Route<?> route = new Route(null, pattern);
        return route.expand(context);
    }

    public static String expand(String pattern, Object object) {
        return expand(pattern, object, false);
    }

    @SuppressWarnings("rawtypes")
    public static String expand(String pattern, Object object, boolean isiri) {
        if (object == null || pattern == null)
            throw new IllegalArgumentException();
        Route<?> route = new Route(null, pattern);
        return route.expand(object, isiri);
    }

    public static String expandAnnotated(Object object) {
        if (object == null)
            throw new IllegalArgumentException();
        Class<?> _class = object.getClass();
        URIRoute uriroute = (URIRoute)_class.getAnnotation(URIRoute.class);
        if (uriroute != null) {
            return expand(uriroute.value(), object, uriroute.isiri());
        } else {
            throw new IllegalArgumentException("No Route provided");
        }
    }

    private boolean matchRequirements(String uri) {
        if (requirements != null && !requirements.isEmpty()) {
            Map<String, String> parsedUri = parse(uri);
            for (Map.Entry<String, String> requirement : requirements.entrySet()) {
                Pattern patt = Pattern.compile(requirement.getValue());
                if (parsedUri.containsKey(requirement.getKey()) && !patt.matcher(parsedUri.get(requirement.getKey()))
                    .matches()) {
                    return false;
                }
            }
        }
        return true;
    }

}
