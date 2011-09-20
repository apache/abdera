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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.abdera2.common.iri.IRI;


public class TemplateManager<T>
  implements Iterable<T> { 

  private final Map<T,Template> templates = 
    new HashMap<T,Template>();
  private final boolean isiri;
  private final IRI base;
  private final Context contextDefaults;

  public TemplateManager(String base) {
    this(new IRI(base));
  }
  
  public TemplateManager(IRI base) {
    this.isiri = true;
    this.base = base;
    this.contextDefaults = null;
  }
  
  public TemplateManager() {
    this.isiri = true;
    this.base = null;
    this.contextDefaults = null;
  }
  
  public TemplateManager(String base, boolean iri) {
    this(new IRI(base),iri);
  }
  
  public TemplateManager(IRI base, boolean iri) {
    this.isiri = iri;
    this.base = base;
    this.contextDefaults = null;
  }
  
  public TemplateManager(boolean iri) {
    this.isiri = iri;
    this.base = null;
    this.contextDefaults = null;
  }

  public TemplateManager(String base, Context defaults) {
    this(new IRI(base),defaults);
  }
  
  public TemplateManager(IRI base, Context defaults) {
    if (defaults == null)
      throw new IllegalArgumentException();
    this.isiri = defaults.isIri();
    this.contextDefaults = defaults;
    this.base = base;
  }
  
  public TemplateManager(Context defaults) {
    this((IRI)null,defaults);
  }
  
  public TemplateManager(String base, Object defaults) {
    this(new IRI(base),defaults);
  }
  
  public TemplateManager(IRI base, Object defaults) {
    this(base,defaults,true);
  }
 
  public TemplateManager(Object defaults) {
    this((IRI)null,defaults,true);
  }
  
  public TemplateManager(Object defaults, boolean isiri) {
    this(_innerContext(defaults,isiri));
  }
  
  public TemplateManager(String base, Object defaults, boolean isiri) {
    this(new IRI(base),defaults,isiri);
  }
  
  public TemplateManager(IRI base, Object defaults, boolean isiri) {
    this(base,_innerContext(defaults,isiri));
  }
  
  public Context getDefaultContext() {
    return this.contextDefaults;
  }
  
  public void add(T key, Template template) {
    this.templates.put(key,template);
  }
  
  public void add(T key, String template) {
    add(key, new Template(template));
  }
  
  public void add(T key, Object template) {
    add(key, new Template(template));
  }
  
  public void add(Map<T,Object> templates) {
    TemplateManager<T> tm = fromMap(templates);
    this.templates.putAll(tm.templates);
  }
  
  public String expandAndResolve(T key, Object object, String base) {
    IRI iri = expandAndResolve(key,object,new IRI(base));
    return iri != null ? iri.toString() : null;
  }
  
  public IRI expandAndResolve(T key, Object object, IRI base) {
    String ex = expand(key,object);
    return ex != null ? 
        base == null ? 
            new IRI(ex) : 
            base.resolve(ex) : 
        null;    
  }
  
  public IRI expandAndResolve(T key, Object object) {
    return expandAndResolve(key,object,base);
  }
 
  public String expandAndResolve(T key, Context context, String base) {
    IRI iri = expandAndResolve(key,context,new IRI(base));
    return iri != null ? iri.normalize().toString() : null;
  }
  
  public IRI expandAndResolve(T key, Context context, IRI base) {
    String ex = expand(key,context);
    return ex != null ? 
        base == null ? 
            new IRI(ex) : 
            base.resolve(ex) : 
        null;    
  }
  
  public IRI expandAndResolve(T key, Context context) {
    return expandAndResolve(key,context,base);    
  }
  
  public String expand(T key, Object object) {
    if (!templates.containsKey(key))
      return null;
    Template template = templates.get(key);
    return template.expand(_wrap(_innerContext(object,isiri),contextDefaults));
  }
  
  public String expand(T key) {
    if (contextDefaults == null)
      throw new IllegalArgumentException();
    return expand(key,contextDefaults);
  }
  
  public String expand(T key, Context context) {
    if (!templates.containsKey(key))
      return null;
    Template template = templates.get(key);
    return template.expand(_wrap(context,contextDefaults));
  }
  
  @SuppressWarnings("unchecked")
  private static Context _innerContext(Object object, boolean isiri) {
    return object instanceof Context ? (Context)object : object instanceof Map
        ? new MapContext((Map<String,Object>)object, isiri) : new ObjectContext(object, isiri);
  }
  
  private static Context _wrap(Context context, Context contextDefaults) {
    return contextDefaults != null ? 
      new DefaultingContext(context,contextDefaults) : context;
  }

  public Iterator<T> iterator() {
    return templates.keySet().iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((contextDefaults == null) ? 0 : contextDefaults.hashCode());
    result = prime * result + (isiri ? 1231 : 1237);
    result = prime * result + ((templates == null) ? 0 : templates.hashCode());
    return result;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TemplateManager other = (TemplateManager) obj;
    if (contextDefaults == null) {
      if (other.contextDefaults != null)
        return false;
    } else if (!contextDefaults.equals(other.contextDefaults))
      return false;
    if (isiri != other.isiri)
      return false;
    if (templates == null) {
      if (other.templates != null)
        return false;
    } else if (!templates.equals(other.templates))
      return false;
    return true;
  }
 
  public static <T>TemplateManager<T> fromMap(Map<T,Object> map) {
    TemplateManager<T> tm = new TemplateManager<T>();
    for (Map.Entry<T, Object> entry : map.entrySet()) {
      tm.add(entry.getKey(),entry.getValue());
    }
    return tm;
  }
}
