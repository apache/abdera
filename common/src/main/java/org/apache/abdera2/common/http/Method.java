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
package org.apache.abdera2.common.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Method {
  
  private static final Map<String, Method> methods = 
    new ConcurrentHashMap<String, Method>();
  
  public static final Method GET = 
    new Method("GET", true, true);
  public static final Method POST = 
    new Method("POST", false, false);
  public static final Method PUT = 
    new Method("PUT", true, false);
  public static final Method DELETE = 
    new Method("DELETE", true, false);
  public static final Method OPTIONS = 
    new Method("OPTIONS", true, true);
  public static final Method PATCH = 
    new Method("PATCH", false, false);
  public static final Method HEAD = 
    new Method("HEAD", true, true);
  public static final Method TRACE = 
    new Method("TRACE", true, true);
  
  static {
    methods.put(GET.name,GET);
    methods.put(POST.name,POST);
    methods.put(PUT.name, PUT);
    methods.put(DELETE.name,DELETE);
    methods.put(OPTIONS.name,OPTIONS);
    methods.put(PATCH.name, PATCH);
    methods.put(HEAD.name, HEAD);
    methods.put(TRACE.name, TRACE);
  }
  
  static void add(Method method) {
    methods.put(method.name(),method);
  }
  
  public static boolean isIdempotent(String method) {
    Method m = get(method);
    return m != null ? m.idempotent() : false;
  }
  
  public static boolean isSafe(String method) {
    Method m = get(method);
    return m != null ? m.safe() : false;
  }
  
  public static Method add(String name, boolean idempotent, boolean safe) {
    if (name == null)
      throw new IllegalArgumentException();
    if (!methods.containsKey(name)) {
      name = name.toUpperCase();
      Method method = new Method(name,idempotent,safe);
      add(method);
      return method;
    } else return null;
  }
  
  public static Method get(String name) {
    if (name == null) 
      throw new IllegalArgumentException();
    return methods.get(name.toUpperCase());
  }
  
  public static Method get(String name, boolean create) {
    Method method = get(name);
    if (method == null && create) {
      method = add(name,false,false);
    }
    return method;
  }
  
  private final String name;
  private final boolean idempotent;
  private final boolean safe;
  
  Method(String name) {
    this(name,false,false);
  }
  
  Method(String name, boolean idempotent, boolean safe) {
    this.name = name.toUpperCase();
    this.idempotent = idempotent;
    this.safe = safe;
  }
  
  public String name() {
    return name;
  }
  
  public boolean idempotent() {
    return idempotent;
  }
  
  public boolean safe() {
    return safe;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (idempotent ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + (safe ? 1231 : 1237);
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
    Method other = (Method) obj;
    if (idempotent != other.idempotent)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (safe != other.safe)
      return false;
    return true;
  }
  
  public String toString() {
    return name;
  }
}
