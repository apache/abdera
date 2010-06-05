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
package org.apache.abdera.i18n.templates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.text.CharUtils;

/**
 * A type of URI Template loosely based on Ruby on Rails style Routes. Example: Route feed_route = new
 * Route("feed",":feed/:entry");
 */
@SuppressWarnings("unchecked")
public class Route implements Iterable<String>, Cloneable, Serializable {

    private static final long serialVersionUID = -8979172281494208841L;

    private static final Evaluator EVALUATOR = new Evaluator();
    private static final Pattern VARIABLE = Pattern.compile("[\\*\\:](?:\\()?[0-9a-zA-Z]+(?:\\))?");
    private static final String VARIABLE_CONTENT_MATCH = "([^:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;\\=]+)";
    private static final String VARIABLE_CONTENT_PARSE = "([^:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;\\=]*)";
    private final String name;
    private final String pattern;
    private final String[] tokens;
    private final String[] variables;
    private final Pattern regexMatch;
    private final Pattern regexParse;

    private Map<String, String> requirements;

    private Map<String, String> defaultValues;

    public Route(String name, String pattern) {
        this(name, pattern, null, null);
    }

    public Route(String name, String pattern, Map<String, String> defaultValues, Map<String, String> requirements) {
        this.name = name;
        this.pattern = CharUtils.stripBidiInternal(pattern);
        this.tokens = initTokens();
        this.variables = initVariables();
        this.defaultValues = defaultValues;
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
        for (String token : this) {
            String var = var(token);
            pattern = replace(pattern, token, EVALUATOR.evaluate(var, getDefaultValue(var), context));
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
                buf.append(var).append("=").append(EVALUATOR.evaluate(var, context));
            }
        }

        return buf.toString();
    }

    public String getDefaultValue(String var) {
        if (defaultValues == null)
            return null;

        return defaultValues.get(var);
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
    public String expand(Object object, boolean isiri) {
        return expand(object instanceof Context ? (Context)object : object instanceof Map
            ? new HashMapContext((Map)object, isiri) : new ObjectContext(object, isiri));
    }

    private String replace(String pattern, String token, String value) {
        return pattern.replace(token, value);
    }

    public String getName() {
        return name;
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

    public Map<String, String> getDefaultValues() {
        return defaultValues;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public Route clone() {
        try {
            return (Route)super.clone();
        } catch (Throwable e) {
            return new Route(name, pattern); // not going to happen, but just in case
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final Route other = (Route)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
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

    public static String expand(String pattern, Context context) {
        if (context == null || pattern == null)
            throw new IllegalArgumentException();
        Route route = new Route(null, pattern);
        return route.expand(context);
    }

    public static String expand(String pattern, Object object) {
        return expand(pattern, object, false);
    }

    public static String expand(String pattern, Object object, boolean isiri) {
        if (object == null || pattern == null)
            throw new IllegalArgumentException();
        Route route = new Route(null, pattern);
        return route.expand(object, isiri);
    }

    public static String expandAnnotated(Object object) {
        if (object == null)
            throw new IllegalArgumentException();
        Class _class = object.getClass();
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
