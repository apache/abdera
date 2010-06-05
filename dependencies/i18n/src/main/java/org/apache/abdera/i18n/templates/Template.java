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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.text.CharUtils;

/**
 * Used to evaluate a URI Template. Instances are immutable, cloneable, serializable and threadsafe.
 */
@SuppressWarnings("unchecked")
public final class Template implements Iterable<String>, Cloneable, Serializable {

    private static final long serialVersionUID = -613907262632631896L;

    private static final Evaluator EVALUATOR = new Evaluator();
    private static final Pattern VARIABLE = Pattern.compile("\\{[^{}]+\\}");
    private static final String TOKEN_START = "\\{";
    private static final String TOKEN_STOP = "\\}";

    private final String pattern;
    private final String[] tokens;
    private final String[] variables;

    /**
     * @param pattern A URI Template
     */
    public Template(String pattern) {
        this.pattern = CharUtils.stripBidiInternal(pattern);
        this.tokens = initTokens();
        this.variables = initVariables();
    }

    /**
     * Return the URI Template pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * IRI Templates that contain bidirectional characters will typically not display properly in unicode enabled
     * environments. This method return the Template with appropriate bidi control characters to ensure that the
     * Template can be rendered properly for display
     */
    public String getPatternForDisplay() {
        String pattern = this.pattern;
        for (String token : this) {
            pattern = replace(pattern, token, forDisplay(token));
        }
        return CharUtils.wrapBidi(pattern, CharUtils.LRO);
    }

    private static String forDisplay(String token) {
        String[] splits = token.split("\\|");
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        if (splits.length == 1) {
            String[] pair = splits[0].split("\\s*=\\s*");
            buf.append(CharUtils.wrapBidi(pair[0], CharUtils.LRE));
            if (pair.length > 1) {
                buf.append('=');
                buf.append(pair[1]);
            }
        } else {
            buf.append(splits[0]);
            buf.append('|');
            buf.append(splits[1]);
            buf.append('|');
            String[] vars = splits[2].split("\\s*,\\s*");
            int i = 0;
            for (String var : vars) {
                if (i++ > 0)
                    buf.append(",");
                String[] pair = var.split("\\s*=\\s*");
                buf.append(CharUtils.wrapBidi(pair[0], CharUtils.LRE));
                if (pair.length > 1) {
                    buf.append('=');
                    buf.append(pair[1]);
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }

    /**
     * Iterate the template tokens
     */
    public Iterator<String> iterator() {
        return Arrays.asList(tokens).iterator();
    }

    /**
     * Return the array of template variables
     */
    private String[] initTokens() {
        Matcher matcher = VARIABLE.matcher(pattern);
        List<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            String token = matcher.group();
            token = token.substring(1, token.length() - 1);
            if (!tokens.contains(token))
                tokens.add(token);
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private String[] initVariables() {
        List<String> list = new ArrayList<String>();
        for (String token : this) {
            String[] vars = EVALUATOR.getVariables(token);
            for (String var : vars) {
                if (!list.contains(var))
                    list.add(var);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Return the array of template variables
     */
    public String[] getVariables() {
        return variables;
    }

    /**
     * Expand the URI Template using the specified Context.
     * 
     * @param context The Context impl used to resolve variable values
     * @return An expanded URI
     */
    public String expand(Context context) {
        String pattern = this.pattern;
        for (String token : this) {
            pattern = replace(pattern, token, EVALUATOR.evaluate(token, context));
        }
        return pattern;
    }

    /**
     * Expand the URI Template using the non-private fields and methods of the specified object to resolve the template
     * tokens
     */
    public String expand(Object object) {
        return expand(object, false);
    }

    /**
     * Expand the template using the non-private fields and methods of the specified object to resolve the template
     * tokens. If isiri is true, IRI escaping rules will be used.
     */
    public String expand(Object object, boolean isiri) {
        return expand(object instanceof Context ? (Context)object : object instanceof Map
            ? new HashMapContext((Map)object, isiri) : new ObjectContext(object, isiri));
    }

    private String replace(String pattern, String token, String value) {
        return pattern.replaceAll(TOKEN_START + Pattern.quote(token) + TOKEN_STOP, value);
    }

    /**
     * Clone this Template instance
     */
    public Template clone() {
        try {
            return (Template)super.clone();
        } catch (Throwable e) {
            return new Template(pattern); // not going to happen, but just in case
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
        final Template other = (Template)obj;
        if (pattern == null) {
            if (other.pattern != null)
                return false;
        } else if (!pattern.equals(other.pattern))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("V:" + getPatternForDisplay());
        buf.append('\n');
        buf.append("L:" + CharUtils.wrapBidi(getPattern(), CharUtils.LRO));
        buf.append('\n');
        return buf.toString();
    }

    public String explain() {
        StringBuilder buf = new StringBuilder();
        try {
            explain(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buf.toString();
    }

    public void explain(Appendable buf) throws IOException {
        buf.append("Template:");
        buf.append('\n');
        buf.append("\t" + getPatternForDisplay());
        buf.append('\n');
        buf.append('\n');
        buf.append(" Variables:");
        buf.append('\n');
        String[] vars = getVariables();
        for (String var : vars) {
            buf.append('\t');
            buf.append(var);
            buf.append('\n');
        }
        buf.append('\n');
        buf.append(" Tokens:");
        buf.append('\n');
        for (String token : this) {
            buf.append('\t');
            buf.append(forDisplay(token) + " \n\t\t ");
            EVALUATOR.explain(token, buf);
            buf.append('\n');
        }
        buf.append('\n');
        buf.append(" Example:");
        buf.append('\n');

        HashMapContext c = new HashMapContext();
        for (String var : vars) {
            c.put(var, "foo");
            buf.append("\t" + var + " = " + "foo");
            buf.append('\n');
        }
        buf.append('\n');
        buf.append("\t" + expand(c));

        buf.append('\n');
        buf.append('\n');

        c.clear();
        for (int i = 0; i < vars.length; i++) {
            String var = vars[i];
            if (i % 2 == 1) {
                c.put(var, "foo");
                buf.append("\t" + var + " = " + "foo");
                buf.append('\n');
            } else {
                buf.append("\t" + var + " = null");
                buf.append('\n');
            }
        }
        buf.append('\n');
        buf.append("\t" + expand(c));

        buf.append('\n');
        buf.append('\n');

        c.clear();
        for (int i = 0; i < vars.length; i++) {
            String var = vars[i];
            if (i % 2 == 0) {
                c.put(var, "foo");
                buf.append("\t" + var + " = " + "foo");
                buf.append('\n');
            } else {
                buf.append("\t" + var + " = null");
                buf.append('\n');
            }
        }
        buf.append('\n');
        buf.append("\t" + expand(c));
    }

    public static String expand(String pattern, Context context) {
        if (context == null || pattern == null)
            throw new IllegalArgumentException();
        Template template = new Template(pattern);
        return template.expand(context);
    }

    public static String expand(String pattern, Object object) {
        return expand(pattern, object, false);
    }

    public static String expand(String pattern, Object object, boolean isiri) {
        if (object == null || pattern == null)
            throw new IllegalArgumentException();
        Template template = new Template(pattern);
        return template.expand(object, isiri);
    }

    /**
     * Use an Object annotated with the URITemplate annotation to expand a template
     */
    public static String expandAnnotated(Object object) {
        if (object == null)
            throw new IllegalArgumentException();
        Class _class = object.getClass();
        URITemplate uritemplate = (URITemplate)_class.getAnnotation(URITemplate.class);
        if (uritemplate != null) {
            return expand(uritemplate.value(), object, uritemplate.isiri());
        } else {
            throw new IllegalArgumentException("No URI Template provided");
        }
    }

    public static String explain(String pattern) {
        return new Template(pattern).explain();
    }

    public static void explain(String pattern, Appendable buf) throws IOException {
        new Template(pattern).explain(buf);
    }
}
