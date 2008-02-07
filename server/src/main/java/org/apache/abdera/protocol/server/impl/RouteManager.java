package org.apache.abdera.protocol.server.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.abdera.i18n.templates.CachingContext;
import org.apache.abdera.i18n.templates.Context;
import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.ObjectContext;
import org.apache.abdera.i18n.templates.Route;
import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.TargetType;

/**
 * This is a largely experimental implementation of a Target Resolver and
 * Target Builder based on URL patterns similar (but not identical) to 
 * Ruby on Rails style routes.  
 * 
 * For instance:
 * <pre>
 *   RouteManager rm = 
 *     new RouteManager()
 *       .addRoute("entry",":collection/:entry", TargetType.TYPE_ENTRY)
 *       .addRoute("feed", ":collection", TargetType.TYPE_COLLECTION);
 * </pre>
 *
 * The RouteManager can be used by Provider implementations as the target
 * resolver and target builder
 *
 */
public class RouteManager
  implements Resolver<Target>,
             TargetBuilder {

  protected Map<Route,TargetType> targets = 
    new HashMap<Route,TargetType>();
  
  protected Map<String,Route> routes = 
    new HashMap<String,Route>();

  protected Map<Route,CollectionAdapter> route2CA = 
    new HashMap<Route,CollectionAdapter>();
  
  public RouteManager addRoute(
    Route route) {
      return addRoute(route,null);
  }
  
  public RouteManager addRoute(
    String name, 
    String pattern) {
      return addRoute(
        name,
        pattern,
        null);
  }
  
  public RouteManager addRoute(
    Route route, 
    TargetType type) {
      routes.put(route.getName(), route);
      if (type != null) 
        targets.put(route, type);
      return this;
  }
  
  public RouteManager addRoute(
    String name, 
    String pattern, 
    TargetType type) {
      return addRoute(
        new Route(
          name, 
          pattern), 
        type);
  }
  
  public RouteManager addRoute(
    String name, 
    String pattern, 
    TargetType type,
    CollectionAdapter collectionAdapter) {
    
    Route route = new Route(name,  pattern);
    route2CA.put(route, collectionAdapter);
    return addRoute(route, type);
  }
  
  public Target resolve(Request request) {
    RequestContext context = (RequestContext) request;
    String uri = context.getTargetPath();
    int idx = uri.indexOf('?');
    if (idx != -1) {
      uri = uri.substring(0, idx);
    }
    for(Map.Entry<Route, TargetType> entry : targets.entrySet()) {
      if (entry.getKey().match(uri)) {
        CollectionAdapter ca = route2CA.get(entry.getKey());
        if (ca != null) {
          context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, ca);
        }
        return getTarget(context, entry.getKey(), uri, entry.getValue());
      }
    }
    return null;
  }

  private Target getTarget(
    RequestContext context,
    Route route, 
    String uri, 
    TargetType type) {
      return new RouteTarget(type, context, route, uri);
  }
  
  public String urlFor(
    RequestContext context, 
    Object key, 
    Object param) {
    Route route = routes.get(key);
    if (route != null) {
      Map<String, Object> map = new HashMap<String,Object>();
      Context ctx = getContext(param);
      for (String var : ctx) {
        map.put(var, ctx.resolve(var));
      }
      for (String var : route.getVariables()) {
        Object value = context.getTarget().getParameter(var);
        if (!map.containsKey(var) && value != null) {
          map.put(var, value);
        }
      }
      return context.getTargetBasePath() + route.expand(getContext(map));
    } else {
      return null;
    }
  }
  
  @SuppressWarnings("unchecked")
  private Context getContext(Object param) {
    Context context = null;
    if (param != null) {
      if (param instanceof Map) {
        context = new HashMapContext((Map<String,Object>)param, true);
      } else if (param instanceof Context) {
        context = (Context)param;
      } else {
        context = new ObjectContext(param,true);
      }
    } else context = new EmptyContext();
    return context;
  }

  private static class EmptyContext 
    extends CachingContext {
    private static final long serialVersionUID = 4681906592987534451L;
    protected <T> T resolveActual(String var) {
      return null;
    }
    public Iterator<String> iterator() {
      List<String> list = Arrays.asList(new String[0]);
      return list.iterator();
    }
  }
  
  public static class RouteTarget 
    extends AbstractTarget {
      private final Map<String,String> params;
      private final Route route;      
      public RouteTarget(
        TargetType type, 
        RequestContext context,
        Route route,
        String uri) {
          super(type, context);
          this.route = route;
          this.params = route.parse(uri);
      }
      public Route getRoute() {
        return route;
      }
      public String getParameter(String name) {
        return params.containsKey(name) ?
          params.get(name) :
          super.getParameter(name);
      }
      @SuppressWarnings("unchecked")
      public String[] getParameterNames() {
        List<String> names = new ArrayList(Arrays.asList(super.getParameterNames()));
        for (String name : params.keySet()) {
          if (!names.contains(name))
            names.add(name);
        }
        return names.toArray(new String[names.size()]);
      }
  }
}
