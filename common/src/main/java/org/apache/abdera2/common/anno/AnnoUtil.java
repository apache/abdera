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
package org.apache.abdera2.common.anno;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;


public class AnnoUtil {

  /**
   * Retrieves the value of the Name attribute from the specified 
   * item. If the item is an instance object, the name is pulled from it's
   * Class.
   */
  public static String getName(Object item) {
    if (item == null) return null;
    Class<?> _class = 
      item instanceof Class ? (Class<?>)item :
      item.getClass();
    return _class.isAnnotationPresent(Name.class) ?
      _class.getAnnotation(Name.class).value() : 
      _class.getSimpleName().toLowerCase();
  }

  /**
   * Retrieve the default implementation for the specified Class.
   */
  public static String getDefaultImplementation(Class<?> _class) {
    String _default = null;
    if (_class.isAnnotationPresent(DefaultImplementation.class)) {
      DefaultImplementation di = 
        _class.getAnnotation(DefaultImplementation.class);
      _default = di.value();
    }
    return _default;
  }

  /**
   * Retrieve the Version annotation from the specified item
   */
  public static Version getVersion(Object item) {
    if (item == null) return null;
    Class<?> _class = 
      item instanceof Class ? (Class<?>)item :
      item.getClass();
    return _class.isAnnotationPresent(Version.class) ?
      _class.getAnnotation(Version.class) :
      null;
  }

  /**
   * Returns the Namespace URIs handled by this Extension Factory
   * 
   * @return A List of Namespace URIs Supported by this Extension
   */
  public static Set<String> getNamespaces(Object obj) {
    if (obj == null) return Collections.emptySet();
    Class<?> _class = 
      obj instanceof Class ? (Class<?>)obj :
      obj.getClass();
    Set<String> ns = new HashSet<String>();
    if (_class.isAnnotationPresent(Namespace.class)) {
      Namespace nsa = _class.getAnnotation(Namespace.class);
      for (String n : nsa.value())
        ns.add(n);
    }
    return ns;
  }

  /**
   * Returns true if the given object "handles" the given namespace based
   * on values specified using the Namespace annotation
   */
  public static boolean handlesNamespace(String namespace, Object obj) {
    Set<String> set = getNamespaces(obj);
    return set.contains(namespace);
  }
  
  /**
   * Retrieve a javax.xml.namespace.QName from a class using the QName annotation.
   */
  public static QName qNameFromAnno(org.apache.abdera2.common.anno.QName impl) {
    QName result = null;
    String name = impl.value();
      String ns = impl.ns();
      String pfx = impl.pfx();
      if (pfx != null && pfx.length() > 0) {
        result = new QName(ns,name,pfx);
      } else if (ns != null && ns.length() > 0) {
        result = new QName(ns,name);
      } else if (name != null && name.length() > 0) {
        result = new QName(name);
      };
    return result;
  }
  
  /**
   * Retrieve a javax.xml.namespace.QName from an instance object using the 
   * QName annotation
   */
  public static QName getQName(Object obj) {
    if (obj == null) return null;
    Class<?> _class = obj instanceof Class ? (Class<?>)obj : obj.getClass();
    if (_class.isAnnotationPresent(org.apache.abdera2.common.anno.QName.class)) {
      return qNameFromAnno(_class.getAnnotation(org.apache.abdera2.common.anno.QName.class));
    }
    return null;
  }
}
