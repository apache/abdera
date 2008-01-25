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
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.RequestContext.Scope;

@SuppressWarnings("unchecked") 
/**
 * A TargetBuilder implementation that uses URI/IRI Templates to construct
 * URIs/IRIs.  The input parameter can be a java.util.Map<String,Object>, 
 * an instance of the org.apache.abdera.i18n.templates.Context interface,
 * or any Java object with public getter methods. 
 */
public class TemplateTargetBuilder 
  implements TargetBuilder {

  protected final Map<Object,Template> templates 
    = new HashMap<Object,Template>();
  
  public TemplateTargetBuilder() {}
  
  public TemplateTargetBuilder(Map<TargetType,Template> templates) {
    this.templates.putAll(templates);
  }
  
  public TemplateTargetBuilder setTemplate(Object key, String template) {
    return setTemplate(key, new Template(template));
  }
  
  public TemplateTargetBuilder setTemplate(Object key, Template template) {
    templates.put(key,template);
    return this;
  }

  public static Context getContext(RequestContext request, Object param) {
    Context context = null;
    if (param != null) {
      if (param instanceof Map) {
        context = new HashMapContext((Map<String,Object>)param, true);
      } else if (param instanceof Context) {
        context = (Context)param;
      } else {
        context = new ObjectContext(param,true);
      }
    }
    return new TemplateContext(request, context);
  }
  
  public static <T>List<T> asList(Iterator<T> i) {
    List<T> list = new ArrayList<T>();
    while(i.hasNext()) 
      list.add(i.next());
    return list;
  }
  
  public String resolveIri(
    RequestContext request, 
    Object key, 
    Object param) {
      Template template = templates.get(key);
      return template != null ? template.expand(getContext(request,param)) : null;
  }

  public static class TemplateContext 
    extends DelegatingContext {

    private static final long serialVersionUID = 4332356546022014897L;
    
    private final RequestContext request;
    
    public TemplateContext(
      RequestContext request,
      Context subcontext) {
        super(subcontext);
        this.request = request;
    }

    protected <T> T resolveActual(
      String var) {
        Variable variable = Variable.get(var);
        if (variable == null) 
          return (T)subcontext.resolve(var);
        switch(variable) {
          case REQUEST_URI:
            return (T) request.getUri().toString();
          case REQUEST_RESOLVED_URI:
            return (T) request.getResolvedUri().toString();
          case REQUEST_CONTENT_TYPE:
            return (T) request.getContentType().toString();
          case REQUEST_CONTEXT_PATH:
            return (T) request.getContextPath();
          case REQUEST_PARAMETER:
            String name = Variable.REQUEST_PARAMETER.label(var);
            return (T) request.getParameter(name);
          case REQUEST_LANGUAGE:
            return (T) request.getAcceptLanguage();
          case REQUEST_CHARSET:
            return (T) request.getAcceptCharset();
          case REQUEST_USER: 
            Principal p = request.getPrincipal();
            return p != null ? (T)p.getName() : null;
          case REQUEST_ATTRIBUTE_SESSION:
            name = Variable.REQUEST_ATTRIBUTE_SESSION.label(var);
            return (T) request.getAttribute(Scope.SESSION, name);
          case REQUEST_ATTRIBUTE_REQUEST: 
            name = Variable.REQUEST_ATTRIBUTE_REQUEST.label(var);
            return (T) request.getAttribute(Scope.REQUEST, name);
          case REQUEST_HEADER:
            name = Variable.REQUEST_HEADER.label(var);
            return (T) request.getHeader(name);
          case TARGET_PARAMETER:
            name = Variable.TARGET_PARAMETER.label(var);
            return (T) request.getTarget().getParameter(name);
          case TARGET_IDENTITY:
            return (T) request.getTarget().getIdentity();
          case TARGET_PATH:
            return (T) request.getTargetPath();
          case TARGET_BASE:
            return (T) request.getTargetBasePath();
          default: 
            return (T)subcontext.resolve(var);
        }
    }

    public Iterator<String> iterator() {
      List<String> vars = new ArrayList<String>();
      for (String var : subcontext)
        vars.add(var);      
      for (String var : request.getParameterNames()) 
        vars.add(toVar(Variable.REQUEST_PARAMETER,var));      
      for (String var : request.getAttributeNames(Scope.SESSION))
        vars.add(toVar(Variable.REQUEST_ATTRIBUTE_SESSION,var));      
      for (String var : request.getAttributeNames(Scope.REQUEST))
        vars.add(toVar(Variable.REQUEST_ATTRIBUTE_REQUEST,var));      
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
    REQUEST_CONTEXT_PATH,
    REQUEST_CONTENT_TYPE,
    REQUEST_URI,
    REQUEST_RESOLVED_URI,
    REQUEST_PARAMETER,
    REQUEST_LANGUAGE,
    REQUEST_CHARSET,
    REQUEST_USER,
    REQUEST_ATTRIBUTE_SESSION,
    REQUEST_ATTRIBUTE_REQUEST,
    REQUEST_HEADER,
    TARGET_PARAMETER,
    TARGET_IDENTITY,
    TARGET_PATH,
    TARGET_BASE,
    ;
    
    static Variable get(String var) {
      if (REQUEST_PARAMETER.match(var)) {
        return REQUEST_PARAMETER;
      } else if (REQUEST_ATTRIBUTE_SESSION.match(var)) {
        return REQUEST_ATTRIBUTE_SESSION;
      } else if (REQUEST_ATTRIBUTE_REQUEST.match(var)) {
        return REQUEST_ATTRIBUTE_REQUEST;
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
      
    Variable() {
      p = Pattern.compile("\\Q" + name() + "_\\E.*", Pattern.CASE_INSENSITIVE);
    }
    
    boolean match(String var) {
      return p.matcher(var).matches();
    }
    
    String label(String var) {
      return var.substring(name().length() + 1);
    }
  }
  
}
