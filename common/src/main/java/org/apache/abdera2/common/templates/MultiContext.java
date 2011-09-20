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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MultiContext 
  extends CachingContext {

  private static final long serialVersionUID = 1691294411780004133L;
  private final Set<Context> contexts = 
    new HashSet<Context>();
  
  public MultiContext(Context... contexts) {
    for (Context context : contexts)
      this.contexts.add(context);
  }
  
  public MultiContext(Iterable<Context> contexts) {
    for (Context context : contexts)
      this.contexts.add(context);
  }
  
  public MultiContext(Collection<Context> contexts) {
    if (contexts == null)
      throw new IllegalArgumentException();
    this.contexts.addAll(contexts);
  }
  
  public void add(Context context) {
    if (context == null)
      throw new IllegalArgumentException();
    this.contexts.add(context);
  }
  
  @SuppressWarnings("unchecked")
  public void add(Object object) {
    this.contexts.add(
      object instanceof Context ? 
        (Context)object :
        object instanceof Map ?
          new MapContext((Map<String,Object>)object) :
          new ObjectContext(object)
    );
  }
  
  public boolean contains(String var) {
    for (Context context : contexts)
      if (context.contains(var))
        return true;
    return false;
  }

  public Iterator<String> iterator() {
    Set<String> names = new HashSet<String>();
    for (Context context : contexts) 
      for (String name : context)
        names.add(name);
    return names.iterator();
  }

  @Override
  protected <T> T resolveActual(String var) {
    for (Context context : contexts)
      if (context.contains(var))
        return context.resolve(var);
    return null;
  }


}
