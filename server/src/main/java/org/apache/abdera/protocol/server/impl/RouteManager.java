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
 * This is a largely experimental implementation of a Target Resolver and Target Builder based on URL patterns similar
 * (but not identical) to Ruby on Rails style routes. For instance:
 * 
 * <pre>
 * RouteManager rm =
 *     new RouteManager().addRoute(&quot;entry&quot;, &quot;:collection/:entry&quot;, TargetType.TYPE_ENTRY)
 *         .addRoute(&quot;feed&quot;, &quot;:collection&quot;, TargetType.TYPE_COLLECTION);
 * </pre>
 * 
 * The RouteManager can be used by Provider implementations as the target resolver and target builder
 */
public class RouteManager implements Resolver<Target>, TargetBuilder {

    protected class RouteTargetType {
        protected Route route;
        protected TargetType targetType;

        RouteTargetType(Route route, TargetType targetType) {
            this.route = route;
            this.targetType = targetType;
        }

        public Route getRoute() {
            return route;
        }

        public TargetType getTargetType() {
            return targetType;
        }
    }

    protected List<RouteTargetType> targets = new ArrayList<RouteTargetType>();

    protected Map<String, Route> routes = new HashMap<String, Route>();

    protected Map<Route, CollectionAdapter> route2CA = new HashMap<Route, CollectionAdapter>();

    public RouteManager addRoute(Route route) {
        return addRoute(route, null);
    }

    public RouteManager addRoute(String name, String pattern) {
        return addRoute(name, pattern, null);
    }

    public RouteManager addRoute(Route route, TargetType type) {
        routes.put(route.getName(), route);
        if (type != null)
            targets.add(new RouteTargetType(route, type));
        return this;
    }

    public RouteManager addRoute(String name, String pattern, TargetType type) {
        return addRoute(new Route(name, pattern), type);
    }

    public RouteManager addRoute(String name, String pattern, TargetType type, CollectionAdapter collectionAdapter) {

        Route route = new Route(name, pattern);
        route2CA.put(route, collectionAdapter);
        return addRoute(route, type);
    }

    public Target resolve(Request request) {
        RequestContext context = (RequestContext)request;
        String uri = context.getTargetPath();
        int idx = uri.indexOf('?');
        if (idx != -1) {
            uri = uri.substring(0, idx);
        }

        RouteTargetType target = get(uri);
        if (target == null) {
            target = match(uri);
        }

        if (target != null) {
            return getTarget(context, target, uri);
        }

        return null;
    }

    private RouteTargetType get(String uri) {
        for (RouteTargetType target : targets) {
            if (target.route.getPattern().equals(uri)) {
                return target;
            }
        }
        return null;
    }

    private RouteTargetType match(String uri) {
        for (RouteTargetType target : targets) {
            if (target.route.match(uri)) {
                return target;
            }
        }
        return null;
    }

    private Target getTarget(RequestContext context, RouteTargetType target, String uri) {
        CollectionAdapter ca = route2CA.get(target.route);
        if (ca != null) {
            context.setAttribute(DefaultWorkspaceManager.COLLECTION_ADAPTER_ATTRIBUTE, ca);
        }
        return getTarget(context, target.route, uri, target.targetType);
    }

    private Target getTarget(RequestContext context, Route route, String uri, TargetType type) {
        return new RouteTarget(type, context, route, uri);
    }

    public String urlFor(RequestContext context, Object key, Object param) {
        Route route = routes.get(key);
        return route != null ? context.getContextPath() + route.expand(getContext(param)) : null;
    }

    @SuppressWarnings("unchecked")
    private Context getContext(Object param) {
        Context context = new EmptyContext();
        if (param != null) {
            if (param instanceof Map) {
                context = new HashMapContext(cleanMapCtx(param), true);
            } else if (param instanceof Context) {
                context = (Context)param;
            } else {
                context = new ObjectContext(param, true);
            }
        }
        return context;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cleanMapCtx(Object param) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : ((Map<String, Object>)param).entrySet()) {
            map.put(entry.getKey().replaceFirst("^:", ""), entry.getValue());
        }
        ((Map<String, Object>)param).clear();
        ((Map<String, Object>)param).putAll(map);
        return (Map<String, Object>)param;
    }

    private static class EmptyContext extends CachingContext {
        private static final long serialVersionUID = 4681906592987534451L;

        protected <T> T resolveActual(String var) {
            return null;
        }

        public Iterator<String> iterator() {
            List<String> list = Arrays.asList(new String[0]);
            return list.iterator();
        }
    }

    public static class RouteTarget extends SimpleTarget {
        private final Map<String, String> params;
        private final Route route;

        public RouteTarget(TargetType type, RequestContext context, Route route, String uri) {
            super(type, context);
            this.route = route;
            this.params = route.parse(uri);
        }

        public Route getRoute() {
            return route;
        }

        @Override
        public <T> T getMatcher() {
            return (T)getRoute();
        }

        public String getParameter(String name) {
            return params.containsKey(name) ? params.get(name) : super.getParameter(name);
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
