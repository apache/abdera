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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera2.common.iri.IRI;

/**
 * Constructs a mutable Template Context based on an existing IRI/URI 
 * Query String. This can be used to construct new IRI's based on an
 * existing IRI -- for instance, when needing to construct an IRI that 
 * contains all of the same querystring parameters as the original 
 * IRI or when modifying querystring parameter values.
 */
public class QueryContext extends MapContext  {

  private static final long serialVersionUID = -3083469437683051678L;

  public QueryContext(IRI iri) {
    super(parse(iri));
  }
  
  public QueryContext(String iri) {
    super(parse(new IRI(iri)));
  }
  
  public Template getTemplate(boolean fragment, Context additionalParams) {
    Context context = this;
    if (additionalParams != null)
      context = new DefaultingContext(context,additionalParams);
    return QueryContext.templateFromContext(context, fragment);
  }
  
  public Template getTemplate(boolean fragment) {
    return QueryContext.templateFromContext(this, fragment);
  }
  
  public Template getTemplate() {
    return QueryContext.templateFromContext(this, false);
  }
  
  public String expand(boolean fragment) {
    return getTemplate(fragment).expand(this);
  }
  
  public String expand() {
    return getTemplate(false).expand(this);
  }
  
  public String expand(boolean fragment, Context additionalParams) {
    Context context = this;
    if (additionalParams != null)
      context = new DefaultingContext(context,additionalParams);
    return getTemplate(fragment,additionalParams).expand(context);
  }
  
  public String expand(Context additionalParams) {
    return expand(false,additionalParams);
  }
  
  private static Map<String,Object> parse(IRI iri) {
    Map<String,Object> map = new HashMap<String,Object>();
    if (iri != null) {
      String query = iri.getQuery();
      if (query != null) {
        String[] params = query.split("\\s*&\\s*");
        for (String param : params) {
          String[] pair = param.split("\\s*=\\s*",2);
          setval(map,pair[0],pair.length==1?null:pair[1]);
        }
      }
    }
    return map;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static void setval(Map<String,Object> map, String key, String val) {
    if (map.containsKey(key)) {
      Object value = map.get(key);
      if (value instanceof Collection) {
        ((Collection)value).add(val);
      } else {
        List<Object> l = new ArrayList<Object>();
        l.add(value);
        l.add(val);
        map.put(key, l);
      }
    } else map.put(key,val);
  }
  
  public static Template templateFromContext(Context context, boolean fragment) {
    StringBuilder buf = new StringBuilder();
    buf.append('{').append(fragment?'&':'?');
    boolean first = true;
    for (String name : context) {
      if (!first) buf.append(',');
      else first = false;
      buf.append(name);
      Object val = context.resolve(name);
      if (val == null)
        buf.append('^');
      else if (val instanceof List)
        buf.append('*');
    }
    buf.append('}');
    return new Template(buf.toString());
  }
  
  public static Template templateFromQuery(String query, boolean fragment, Context additionalParams) {
    Context context = new QueryContext(query);
    if (additionalParams != null)
      context = new DefaultingContext(context,additionalParams);
    StringBuilder buf = new StringBuilder(baseFromQuery(query));
    buf.append(templateFromContext(context,fragment));
    return new Template(buf.toString());
  }
  
  public static String baseFromQuery(String query) {
    IRI iri = new IRI(query);
    String s = iri.resolve(iri.getPath()).toString();
    return s.equals(query) ? "" : s;
  }
  
  public static String expandQuery(String query, Context context) {
    return expandQuery(query,context,(Template)null);
  }
  
  public static String expandQuery(String query, Context context, String extender) {
    return expandQuery(query,context,new Template(extender));
  }
  
  public static String expandQuery(String query, Context context, Template extender) {
    QueryContext qc = new QueryContext(query);
    DefaultingContext dc = new DefaultingContext(context,qc);
    Template temp = QueryContext.templateFromQuery(query, false, qc);
    if (extender != null)
      temp = temp.extend(extender);
    return temp.expand(dc);  
  }
}
