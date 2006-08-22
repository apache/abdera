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
package org.apache.abdera.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.xpath.XPath;

public final class ServiceUtil 
  implements Constants {

  ServiceUtil() {}
  
  private static ThreadLocal cache = new ThreadLocal();
  
  @SuppressWarnings("unchecked")
  private static Map<String,Class> getCache() {
    Map<String,Class> map;
    if (cache.get() == null) {
      map = new HashMap<String,Class>();
      cache.set(map);
    } else {
      map = (Map<String, Class>) cache.get();
    }
    return map;
  }
  
  /**
   * Get the cached Class for a given id.  The cache is contained
   * in Thread Local storage
   */
  private static Class getClass(String id) {
    return getCache().get(id);
  }
  
  /**
   * Cache the class resolved for a particular ID so we don't have to 
   * go looking for it again later.  It's highly unlikely that the 
   * configured class will change within the context of a given thread
   */
  private static void setClass(String id, Class _class) {
    getCache().put(id,_class);
  }
  
  /**
   * Returns a new instance of the identified object class.  This will use
   * the Abdera configuration mechanism to look up the implementation class
   * for the specified id.  Several places will be checked: the abdera.properties
   * file, the /META-INF/services directory, and the System properties.  If 
   * no instance is configured, the default class name will be used.  Returns
   * null if no instance can be created.
   */
  public static Object newInstance(String id, String _default) {
    return ServiceUtil.locate(id, _default);
  }

  /**
   * Utility method for returning an instance of the default Abdera XPath instance
   */
  public static XPath newXPathInstance() {
    return (XPath) newInstance(
      CONFIG_XPATH,
      ConfigProperties.getDefaultXPath());
  }
  
  /**
   * Utility method for returning an instance of the default Abdera Parser instance
   */
  public static Parser newParserInstance() {
    return (Parser) newInstance(
      CONFIG_PARSER, 
      ConfigProperties.getDefaultParser());
  }

  /**
   * Utility method for returning an instance of the defaul Abdera Factory instance
   */
  public static Factory newFactoryInstance() {
    return (Factory) newInstance(
      CONFIG_FACTORY, 
      ConfigProperties.getDefaultFactory());
  }
  
  /**
   * Get the context class loader for this thread
   */
  public static ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  public static Object locate(String id, String _default) {
    Object object = locate(id);
    if (object == null && _default != null) {
      object = locateInstance(getClassLoader(), _default);
    }
    return object;
  }

  /**
   * Locate a class instance for the given id
   */
  public static Object locate(String id) {
    Object service = checkCache(id);
    if (service == null) service = checkConfigProperties(id);
    return ((service != null) ? service : checkMetaInfServices(id));
  }
  
  @SuppressWarnings("unchecked")
  private static <T>T locateInstance(ClassLoader loader, String id) {
    try {
      Class _class = loader.loadClass(id);
      setClass(id, _class);
      return (T) _class.newInstance();
    } catch (Exception e) {
      // Nothing
    }
    try {
      Class _class = ClassLoader.getSystemClassLoader().loadClass(id);
      setClass(id, _class);
      return (T) _class.newInstance();
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  private static InputStream locateStream(ClassLoader loader, String id) {
    InputStream in = loader.getResourceAsStream(id);
    return (in != null) ? in : ClassLoader.getSystemResourceAsStream(id);
  }
  
  private static Enumeration<URL> locateResources(ClassLoader loader, String id) {
    try {
      return loader.getResources(id);
    } catch (Exception e) {
      // Nothing
    }
    try {
      return ClassLoader.getSystemResources(id);
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  private static Object checkCache(String id) {
    try {
      Class _class = getClass(id);
      return (_class != null) ? _class.newInstance() : null;
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  private static Object checkConfigProperties(String id) {
    String s = ConfigProperties.getConfigurationOption(id);
    return (s != null) ? locateInstance(getClassLoader(), id) : null;
  }
  
  private static Object checkMetaInfServices(String id) {
    Object object = null;
    String sid = "META-INF/services/" + id;
    ClassLoader loader = getClassLoader();
    BufferedReader buf = null;
    try {
      InputStream is = locateStream(loader,sid);
      if (is != null) {
        buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        if (line != null) {
          String s = line.split("#",2)[0].trim();
          object = locateInstance(loader,s);
        }
      }
    } catch (Exception e) {
      // Nothing
    } finally {
      if (buf != null) {
        try {
          buf.close();
        } catch (IOException ioe) {
          // Nothing
        }
      }
    }
    return object;
  }

  private static List<ExtensionFactory> factories = null;
  
  public static List<ExtensionFactory> loadExtensionFactories() {
    if (factories == null) {
      factories = _loadimpls(
        "META-INF/services/org.apache.abdera.factory.ExtensionFactory");
    }
    return factories;
  }
  
  @SuppressWarnings("unchecked")
  public static <T>List<T> _loadimpls(String sid) {
    List<T> impls = new ArrayList<T>();
    ClassLoader loader = getClassLoader();
    try {
      Enumeration<URL> e = locateResources(loader,sid);
      for (;e.hasMoreElements();) {
        BufferedReader buf = null;
        try {
          URL url = (URL) e.nextElement();
          InputStream is = url.openStream();
          if (is != null) {
            buf = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = buf.readLine()) != null) {
              String s = line.split("#",2)[0].trim();
              if (!"".equals(s)) { 
                T impl = (T) locateInstance(loader,s);
                if (impl != null)
                  impls.add(impl);
              }
            }
          }
        } catch (Exception ex) {
          // Nothing
        } finally {
          if (buf != null) {
            try {
              buf.close();
            } catch (IOException ioe) {
              // Nothing
            }
          }
        }
      }
    } catch (Exception e) {
      // Nothing
    }
    
    return impls;
  }
}
