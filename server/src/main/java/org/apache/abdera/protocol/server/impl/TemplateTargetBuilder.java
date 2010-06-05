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

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.templates.Context;
import org.apache.abdera.i18n.templates.DelegatingContext;
import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.ObjectContext;
import org.apache.abdera.i18n.templates.Template;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.RequestContext.Scope;

/**
 * A TargetBuilder implementation that uses URI/IRI Templates to construct URIs/IRIs. The input parameter can be a
 * java.util.Map<String,Object>, an instance of the org.apache.abdera.i18n.templates.Context interface, or any Java
 * object with public getter methods.
 */
@SuppressWarnings("unchecked")
public class TemplateTargetBuilder implements TargetBuilder {

    protected final Map<Object, Template> templates = new HashMap<Object, Template>();

    public TemplateTargetBuilder() {
    }

    public TemplateTargetBuilder(Map<Object, Template> templates) {
        for (Map.Entry<Object, Template> entry : templates.entrySet()) {
            setTemplate(entry.getKey(), entry.getValue());
        }
    }

    public TemplateTargetBuilder setTemplate(Object key, String template) {
        return setTemplate(key, new Template(template));
    }

    public TemplateTargetBuilder setTemplate(Object key, Template template) {
        template = new Template(doReplacements(template.getPattern()));
        templates.put(key, template);
        return this;
    }

    private String doReplacements(String template) {
        for (Variable var : Variable.values()) {
            String rep = var.getReplacement();
            if (rep != null) {
                template = template.replaceAll("\\Q{" + var.name().toLowerCase() + "}\\E", rep);
            }
        }
        return template;
    }

    public static Context getContext(RequestContext request, Object param) {
        Context context = null;
        if (param != null) {
            if (param instanceof Map) {
                context = new HashMapContext((Map<String, Object>)param, true);
            } else if (param instanceof Context) {
                context = (Context)param;
            } else {
                context = new ObjectContext(param, true);
            }
        }
        return new TemplateContext(request, context);
    }

    public static <T> List<T> asList(Iterator<T> i) {
        List<T> list = new ArrayList<T>();
        while (i.hasNext())
            list.add(i.next());
        return list;
    }

    public String urlFor(RequestContext request, Object key, Object param) {
        Template template = templates.get(key);
        return template != null ? template.expand(getContext(request, param)) : null;
    }

    public static class TemplateContext extends DelegatingContext {

        private static final long serialVersionUID = 4332356546022014897L;

        private final RequestContext request;

        public TemplateContext(RequestContext request, Context subcontext) {
            super(subcontext);
            this.request = request;
        }

        private String[] split(String val) {
            if (val.equals(""))
                return null;
            String[] segments = val.split("/");
            return segments.length > 0 ? segments : null;
        }

        protected <T> T resolveActual(String var) {
            Variable variable = Variable.get(var);
            if (variable == null)
                return subcontext != null ? (T)subcontext.resolve(var) : null;
            switch (variable) {
                case REQUEST_URI:
                    return (T)request.getUri().toString();
                case REQUEST_RESOLVED_URI:
                    return (T)request.getResolvedUri().toString();
                case REQUEST_CONTENT_TYPE:
                    return (T)request.getContentType().toString();
                case REQUEST_CONTEXT_PATH:
                    return (T)split(request.getContextPath());
                case REQUEST_PARAMETER:
                    String name = Variable.REQUEST_PARAMETER.label(var);
                    return (T)request.getParameter(name);
                case REQUEST_LANGUAGE:
                    return (T)request.getAcceptLanguage();
                case REQUEST_CHARSET:
                    return (T)request.getAcceptCharset();
                case REQUEST_USER:
                    Principal p = request.getPrincipal();
                    return p != null ? (T)p.getName() : null;
                case SESSION_ATTRIBUTE:
                    name = Variable.SESSION_ATTRIBUTE.label(var);
                    return (T)request.getAttribute(Scope.SESSION, name);
                case REQUEST_ATTRIBUTE:
                    name = Variable.REQUEST_ATTRIBUTE.label(var);
                    return (T)request.getAttribute(Scope.REQUEST, name);
                case REQUEST_HEADER:
                    name = Variable.REQUEST_HEADER.label(var);
                    return (T)request.getHeader(name);
                case TARGET_PARAMETER:
                    name = Variable.TARGET_PARAMETER.label(var);
                    return (T)request.getTarget().getParameter(name);
                case TARGET_IDENTITY:
                    return (T)request.getTarget().getIdentity();
                case TARGET_PATH:
                    return (T)split(request.getTargetPath());
                case TARGET_BASE:
                    return (T)split(request.getTargetBasePath());
                default:
                    return subcontext != null ? (T)subcontext.resolve(var) : null;
            }
        }

        public Iterator<String> iterator() {
            List<String> vars = new ArrayList<String>();
            for (String var : subcontext)
                vars.add(var);
            for (String var : request.getParameterNames())
                vars.add(toVar(Variable.REQUEST_PARAMETER, var));
            for (String var : request.getAttributeNames(Scope.SESSION))
                vars.add(toVar(Variable.SESSION_ATTRIBUTE, var));
            for (String var : request.getAttributeNames(Scope.REQUEST))
                vars.add(toVar(Variable.REQUEST_ATTRIBUTE, var));
            for (String var : request.getHeaderNames())
                vars.add(toVar(Variable.REQUEST_HEADER, var));
            Target target = request.getTarget();
            for (String var : target.getParameterNames())
                vars.add(toVar(Variable.TARGET_PARAMETER, var));
            vars.add(Variable.REQUEST_CONTEXT_PATH.name().toLowerCase());
            vars.add(Variable.REQUEST_CONTENT_TYPE.name().toLowerCase());
            vars.add(Variable.REQUEST_URI.name().toLowerCase());
            vars.add(Variable.REQUEST_RESOLVED_URI.name().toLowerCase());
            vars.add(Variable.REQUEST_LANGUAGE.name().toLowerCase());
            vars.add(Variable.REQUEST_CHARSET.name().toLowerCase());
            vars.add(Variable.REQUEST_USER.name().toLowerCase());
            vars.add(Variable.TARGET_IDENTITY.name().toLowerCase());
            vars.add(Variable.TARGET_PATH.name().toLowerCase());
            vars.add(Variable.TARGET_BASE.name().toLowerCase());
            return vars.iterator();
        }
    }

    private static String toVar(Variable variable, String label) {
        return variable.name().toLowerCase() + "_" + label;
    }

    public static enum Variable {
        REQUEST_CONTEXT_PATH("{-opt|/|request_context_path}{-listjoin|/|request_context_path}"), REQUEST_CONTENT_TYPE, REQUEST_URI, REQUEST_RESOLVED_URI, REQUEST_PARAMETER, REQUEST_LANGUAGE, REQUEST_CHARSET, REQUEST_USER, SESSION_ATTRIBUTE, REQUEST_ATTRIBUTE, REQUEST_HEADER, TARGET_PARAMETER, TARGET_IDENTITY, TARGET_PATH(
            "{-opt|/|target_path}{-listjoin|/|target_path}"), TARGET_BASE(
            "{-opt|/|target_base}{-listjoin|/|target_base}"), ;

        static Variable get(String var) {
            if (REQUEST_PARAMETER.match(var)) {
                return REQUEST_PARAMETER;
            } else if (SESSION_ATTRIBUTE.match(var)) {
                return SESSION_ATTRIBUTE;
            } else if (REQUEST_ATTRIBUTE.match(var)) {
                return REQUEST_ATTRIBUTE;
            } else if (REQUEST_HEADER.match(var)) {
                return REQUEST_HEADER;
            } else if (TARGET_PARAMETER.match(var)) {
                return TARGET_PARAMETER;
            } else {
                try {
                    return Variable.valueOf(var.toUpperCase());
                } catch (Exception e) {
                    return null;
                }
            }
        }

        private final Pattern p;
        private final String replacement;

        Variable() {
            this(null);
        }

        Variable(String replacement) {
            this.p = Pattern.compile("\\Q" + name() + "_\\E.*", Pattern.CASE_INSENSITIVE);
            this.replacement = replacement;
        }

        String getReplacement() {
            return replacement;
        }

        boolean match(String var) {
            return p.matcher(var).matches();
        }

        String label(String var) {
            return var.substring(name().length() + 1);
        }
    }

}
