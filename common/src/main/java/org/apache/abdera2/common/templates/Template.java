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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.anno.URITemplate;
import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.Expression;
import org.apache.abdera2.common.templates.MapContext;
import org.apache.abdera2.common.templates.ObjectContext;
import org.apache.abdera2.common.templates.Template;
import org.apache.abdera2.common.templates.Expression.VarSpec;

@SuppressWarnings("unchecked")
public final class Template implements Iterable<Expression>, Serializable {

    private static final long serialVersionUID = -613907262632631896L;

    private static final Pattern EXPRESSION = Pattern.compile("\\{[^{}]+\\}");
    private static final String EXP_START = "\\{";
    private static final String EXP_STOP = "\\}";

    private final String pattern;
    private final Set<Expression> expressions = new HashSet<Expression>();
    private final Set<String> variables = new HashSet<String>();

    /**
     * @param pattern A URI Template
     */
    public Template(String pattern) {
        if (pattern == null)
          throw new IllegalArgumentException("Template pattern must not be null");
        this.pattern = pattern;
        initExpressions();
    }
    
    public Template(Object object) {
      this(extractPattern(object));
    }

    private static String extractPattern(Object object) {
      if (object == null)
        return null;
      if (object instanceof String)
        return (String)object;
      else if (object instanceof Template)
        return ((Template)object).pattern;
      Class<?> _class = object instanceof Class ? (Class<?>)object : object.getClass();
      URITemplate uriTemplate = (URITemplate)_class.getAnnotation(URITemplate.class);
      String pattern = 
        uriTemplate != null ?
           uriTemplate.value() :
           object instanceof TemplateProvider ? 
             ((TemplateProvider)object).getTemplate() : 
             null;
      if (pattern == null)
        throw new IllegalArgumentException();
      return pattern;
    }

    /**
     * Iterate the template expressions
     */
    public Iterator<Expression> iterator() {
        return expressions.iterator();
    }

    /**
     * Return the array of template variables
     */
    private void initExpressions() {
        Matcher matcher = EXPRESSION.matcher(pattern);
        while (matcher.find()) {
            String token = matcher.group();
            token = token.substring(1, token.length() - 1);
            Expression exp = new Expression(token);
            for (VarSpec varspec : exp)
              variables.add(varspec.getName());
            expressions.add(exp);
        }
    }

    /**
     * Return the array of template variables
     */
    public Iterable<String> getVariables() {
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
        for (Expression exp : this)
            pattern = 
              replace(
                pattern, 
                exp, 
                exp.evaluate(context));
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
        return expand(asContext(object,isiri));
    }

    private String replace(String pattern, Expression exp, String value) {
        return pattern.replaceAll(EXP_START + Pattern.quote(exp.toString()) + EXP_STOP, value);
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
        return pattern;
    }

    public static String expand(String pattern, Context context) {
        if (context == null || pattern == null)
            throw new IllegalArgumentException();
        return new Template(pattern).expand(context);
    }

    public static String expand(String pattern, Object object) {
        return expand(pattern, object, false);
    }

    public static String expand(String pattern, Object object, boolean isiri) {
        if (object == null || pattern == null)
            throw new IllegalArgumentException();
        return new Template(pattern).expand(object, isiri);
    }

    public static String expandAnnotated(Object object) {
      return expandAnnotated(object,null);
    }
    
    @SuppressWarnings("rawtypes")
    private static Context asContext(Object obj, boolean isiri) {
      return 
        obj instanceof Context ? 
          (Context)obj : 
         obj instanceof Map ? 
           new MapContext((Map)obj, isiri) : 
           new ObjectContext(obj, isiri);
    }
    
    /**
     * Use an Object annotated with the URITemplate annotation to expand a template
     */
    public static String expandAnnotated(Object object, Object additional) {
        if (object == null)
            throw new IllegalArgumentException();
        Object contextObject = null;
        Class<?> _class = null;
        if (object instanceof Class<?>) {
          _class = (Class<?>)object;
          contextObject = new AnnotationContext(_class);
        } else {
          _class = object.getClass();
          contextObject = object;
          if (_class.isAnnotationPresent(org.apache.abdera2.common.anno.Context.class)) {
            additional = new AnnotationContext(_class);
          }
        }
        URITemplate uritemplate = (URITemplate)_class.getAnnotation(URITemplate.class);
        if (uritemplate != null) {
            if (additional != null) {
              Context add = asContext(additional, uritemplate.isiri());
              Context main = asContext(contextObject, uritemplate.isiri());
              contextObject = new DefaultingContext(add,main);
            }
            return expand(
              uritemplate.value(), 
              contextObject, 
              uritemplate.isiri());
        } else {
            throw new IllegalArgumentException("No URI Template provided");
        }

    }
    
    public static Context getAnnotatedContext(Object object) {
      return new AnnotationContext(object);
    }

    /**
     * Create a new Template by appending the given template to this
     */
    public Template extend(Template template) {
      StringBuilder buf = new StringBuilder(pattern);
      if (template != null)
        buf.append(template.pattern);
      return new Template(buf.toString());
    }
    
    public Template extend(String template) {
      StringBuilder buf = new StringBuilder(pattern);
      if (template != null)
        buf.append(template);
      return new Template(buf.toString());
    }
}
