package org.apache.abdera2.common.protocol;

import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.abdera2.common.protocol.RequestContext.Scope;
import org.apache.abdera2.common.templates.Context;
import org.apache.abdera2.common.templates.DelegatingContext;

public class RequestTemplateContext extends DelegatingContext {

  private static final long serialVersionUID = 4332356546022014897L;

  private final RequestContext request;

  public RequestTemplateContext(RequestContext request, Context subcontext) {
      super(subcontext);
      this.request = request;
  }
  
  private String[] split(String val) {
      if (val.equals(""))
          return null;
      String[] segments = val.split("/");
      return segments.length > 0 ? segments : null;
  }

  @Override
  public boolean contains(String var) {
    if (super.contains(var)) return true;
    return resolveActual(var) != null;
  }
  
  @Override
  @SuppressWarnings("unchecked")
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

  @Override
  public Iterator<String> iterator() {
      Set<String> vars = new HashSet<String>();
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
  
  private static String toVar(Variable variable, String label) {
    return String.format("%s_%s", variable.name().toLowerCase(), label);
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
    SESSION_ATTRIBUTE, 
    REQUEST_ATTRIBUTE, 
    REQUEST_HEADER, 
    TARGET_PARAMETER, 
    TARGET_IDENTITY, 
    TARGET_PATH, 
    TARGET_BASE;

    static Variable get(String var) {
      if (var == null) return null;
      var = var.toUpperCase(Locale.US);
      for (Variable variable : Variable.values())
        if (var.startsWith(variable.name()))
          return variable;
      return null;
    }
    
    boolean match(String var) {
      if (var == null) return false;
      var = var.toUpperCase(Locale.US);
      return var.startsWith(name());
    }

    String label(String var) {
        return var.substring(name().length() + 1);
    }
  }

}
