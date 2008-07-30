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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

/**
 * Core utility methods that support Abdera's internal operation
 */
public final class ServiceUtil 
  implements Constants {
  
  ServiceUtil() {}
  
  /**
   * Returns a new instance of the identified object class.  This will use
   * the Abdera configuration mechanism to look up the implementation class
   * for the specified id.  Several places will be checked: the abdera.properties
   * file, the /META-INF/services directory, and the System properties.  If 
   * no instance is configured, the default class name will be used.  Returns
   * null if no instance can be created.
   */
  public static Object newInstance(String id, String _default, Abdera abdera) {
    return locate(id, _default, abdera);
  }
  
  /**
   * Returns a new instance of the identified object class.  This will use
   * the Abdera configuration mechanism to look up the implementation class
   * for the specified id.  Several places will be checked: the abdera.properties
   * file, the /META-INF/services directory, and the System properties.  If 
   * no instance is configured, the default class name will be used.  Returns
   * null if no instance can be created.
   */
  public static Object newInstance(String id, String _default, Abdera abdera, Object... args) {
    return locate(id, _default, abdera, args);
  }

  /**
   * Utility method for returning an instance of the default Abdera XPath instance
   */
  public static XPath newXPathInstance(Abdera abdera) {
    return (XPath) newInstance(
      CONFIG_XPATH,
      abdera.getConfiguration().getConfigurationOption(CONFIG_XPATH, DEFAULT_XPATH), 
      abdera);
  }
  
  /**
   * Utility method for returning an instance of the default Abdera Parser instance
   */
  public static Parser newParserInstance(Abdera abdera) {
    return (Parser) newInstance(
      CONFIG_PARSER, 
      abdera.getConfiguration().getConfigurationOption(CONFIG_PARSER, DEFAULT_PARSER),
      abdera);
  }

  /**
   * Utility method for returning an instance of the defaul Abdera Factory instance
   */
  public static Factory newFactoryInstance(Abdera abdera) {
    return (Factory) newInstance(
      CONFIG_FACTORY, 
      abdera.getConfiguration().getConfigurationOption(CONFIG_FACTORY, DEFAULT_FACTORY),
      abdera);
  }
  
  public static ParserFactory newParserFactoryInstance(Abdera abdera) {
    return (ParserFactory) newInstance(
      CONFIG_PARSERFACTORY,
      abdera.getConfiguration().getConfigurationOption(CONFIG_PARSERFACTORY, DEFAULT_PARSERFACTORY),
      abdera);
  }
  
  public static WriterFactory newWriterFactoryInstance(Abdera abdera) {
    return (WriterFactory) newInstance(
      CONFIG_WRITERFACTORY,
      abdera.getConfiguration().getConfigurationOption(CONFIG_WRITERFACTORY, DEFAULT_WRITERFACTORY),
      abdera) ;
  }
  
  public static Writer newWriterInstance(Abdera abdera) {
    return (Writer) newInstance(
      CONFIG_WRITER,
      abdera.getConfiguration().getConfigurationOption(CONFIG_WRITER, DEFAULT_WRITER),
      abdera);
  }
  
  public static StreamWriter newStreamWriterInstance(Abdera abdera) {
    return (StreamWriter) newInstance(
      CONFIG_STREAMWRITER,
      abdera.getConfiguration().getConfigurationOption(CONFIG_STREAMWRITER, DEFAULT_STREAMWRITER),
      abdera);    
  }
  
  public static Object locate(
    String id, 
    String _default, 
    Abdera abdera) {
      Object object = locate(id, abdera);
      if (object == null && _default != null) {
        object = locateInstance(_default, abdera);
      }
      return object;
  }
  
  public static Object locate(
    String id, 
    String _default, 
    Abdera abdera,
    Object... args) {
      Object object = locate(id, abdera);
      if (object == null && _default != null) {
        object = locateInstance(_default, abdera, args);
      }
      return object;
  }

  /**
   * Locate a class instance for the given id
   */
  public static Object locate(String id, Abdera abdera) {
    Object service = checkAbderaConfiguration(id, abdera);
    return ((service != null) ? service : checkMetaInfServices(id, abdera));
  }
  
  @SuppressWarnings("unchecked")
  private static Object _create(Class _class, Abdera abdera) {
    if (_class == null) return null;
    try {
      if (abdera != null) {
        Constructor c = _class.getConstructor(new Class[] {Abdera.class});
        return c.newInstance(new Object[] {abdera});
      }
    } catch (Exception e) {
      // Nothing
    }
    try {
      return _class.newInstance();
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  private static Object _create(Class _class, Abdera abdera, Object... args) {
    Class[] types = null;
    Object[] values = null;
    if (_class == null) return null;
    try {
      if (abdera != null) {
        types = new Class[args.length + 1];
        values = new Object[args.length + 1];
        types[0] = Abdera.class;
        values[0] = abdera;
        for (int n = 0; n < args.length; n++) {
          types[n+1] = args[n].getClass();
          values[n+1] = args[n];
        }
        Constructor c = _class.getConstructor(types);
        return c.newInstance(values);
      }
    } catch (Exception e) {
      // Nothing
    }
    try {
      types = new Class[args.length];
      for (int n = 0; n < args.length; n++)
        types[n] = args[n].getClass();
      return _class.getConstructor(types).newInstance(args);
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  public static Object locateInstance(String id, Abdera abdera) {
    return locateInstance(id,abdera,false);
  }
  
  @SuppressWarnings("unchecked")
  public static Object locateInstance(String id, Abdera abdera, boolean classesonly) {
    try {
      Class _class = loadClass(id, ServiceUtil.class);
      return classesonly ? _class : _create(_class, abdera);
    } catch (Exception e) {
      // Nothing
    }
    try {
      Class _class = ClassLoader.getSystemClassLoader().loadClass(id);
      return classesonly ? _class : _create(_class, abdera);
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public static Object locateInstance(String id, Abdera abdera, Object... args) {
    try {
      Class _class = loadClass(id, ServiceUtil.class);
      return _create(_class, abdera, args);
    } catch (Exception e) {
      // Nothing
    }
    try {
      Class _class = ClassLoader.getSystemClassLoader().loadClass(id);
      return _create(_class, abdera, args);
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  public static InputStream locateStream(String id) {
    InputStream in = getResourceAsStream(id, ServiceUtil.class);
    return (in != null) ? in : ClassLoader.getSystemResourceAsStream(id);
  }
  
  public static Enumeration<URL> locateResources(String id) {
    try {
      return getResources(id,ServiceUtil.class);
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
  
  private static Object checkAbderaConfiguration(String id, Abdera abdera) {
    String s = abdera.getConfiguration().getConfigurationOption(id);
    return (s != null) ? locateInstance(id, abdera) : null;
  }
  
  private static Object checkMetaInfServices(String id, Abdera abdera) {
    Object object = null;
    String sid = "META-INF/services/" + id;
    BufferedReader buf = null;
    try {
      InputStream is = locateStream(sid);
      if (is != null) {
        buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        if (line != null) {
          String s = line.split("#",2)[0].trim();
          object = locateInstance(s, abdera);
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
  
  protected static synchronized List<ExtensionFactory> loadExtensionFactories() {
      List<ExtensionFactory> factories =
        _loadimpls(
          "META-INF/services/org.apache.abdera.factory.ExtensionFactory");
      return factories;
  }
  
  public static synchronized <T>List<T> loadimpls(String sid) {
    return loadimpls(sid,false);
  }
  
  public static synchronized <T>List<T> loadimpls(String sid, boolean classesonly) {
    return _loadimpls(sid,classesonly);
  }
  
  @SuppressWarnings("unchecked")
  protected static <T>List<T> _loadimpls(String sid, boolean classesonly) {
    List<T> impls = Collections.synchronizedList(new ArrayList<T>());
    try {
      Enumeration<URL> e = locateResources(sid);
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
                T impl = (T) locateInstance(s, null);
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
 
  protected static <T>List<T> _loadimpls(String sid) {
    return _loadimpls(sid,false);
  }
  
  
  // The following class loader functions were adopted from 
  // http://svn.apache.org/repos/asf/cxf/trunk/common/common/src/main/java/org/apache/cxf/common/classloader/ClassLoaderUtils.java
  // CXF is an Apache project licensed under the ASF License 2.0
  // License statement from the file:
  /**
   * Licensed to the Apache Software Foundation (ASF) under one
   * or more contributor license agreements. See the NOTICE file
   * distributed with this work for additional information
   * regarding copyright ownership. The ASF licenses this file
   * to you under the Apache License, Version 2.0 (the
   * "License"); you may not use this file except in compliance
   * with the License. You may obtain a copy of the License at
   *
   * http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing,
   * software distributed under the License is distributed on an
   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   * KIND, either express or implied. See the License for the
   * specific language governing permissions and limitations
   * under the License.
   */

  
  /**
   * Load a given resource. <p/> This method will try to load the resource
   * using the following methods (in order):
   * <ul>
   * <li>From Thread.currentThread().getContextClassLoader()
   * <li>From ClassLoaderUtil.class.getClassLoader()
   * <li>callingClass.getClassLoader()
   * </ul>
   * 
   * @param resourceName The name of the resource to load
   * @param callingClass The Class object of the calling object
   */
  public static URL getResource(
    String resourceName, 
    Class<?> callingClass) {
      URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
      if (url == null && resourceName.startsWith("/")) {
        //certain classloaders need it without the leading /
        url = Thread.currentThread().getContextClassLoader()
            .getResource(resourceName.substring(1));
      }
      if (url == null) {
        url = ServiceUtil.class.getClassLoader().getResource(resourceName);
      }
      if (url == null && resourceName.startsWith("/")) {
        //certain classloaders need it without the leading /
        url = ServiceUtil.class.getClassLoader()
            .getResource(resourceName.substring(1));
      }
      if (url == null) {
        ClassLoader cl = callingClass.getClassLoader();
        if (cl != null) {
          url = cl.getResource(resourceName);
        }
      }
      if (url == null) {
        url = callingClass.getResource(resourceName);
      }      
      if ((url == null) && (resourceName != null) && (resourceName.charAt(0) != '/')) {
        return getResource('/' + resourceName, callingClass);
      }
      return url;
  }

  public static Enumeration<URL> getResources(
    String resourceName, 
    Class<?> callingClass) 
      throws IOException {
    Enumeration<URL> url = Thread.currentThread().getContextClassLoader().getResources(resourceName);
    if (url == null && resourceName.startsWith("/")) {
      //certain classloaders need it without the leading /
      url = Thread.currentThread().getContextClassLoader()
          .getResources(resourceName.substring(1));
    }
    if (url == null) {
      url = ServiceUtil.class.getClassLoader().getResources(resourceName);
    }
    if (url == null && resourceName.startsWith("/")) {
      //certain classloaders need it without the leading /
      url = ServiceUtil.class.getClassLoader()
          .getResources(resourceName.substring(1));
    }
    if (url == null) {
      ClassLoader cl = callingClass.getClassLoader();
      if (cl != null) {
        url = cl.getResources(resourceName);
      }
    }      
    if ((url == null) && (resourceName != null) && (resourceName.charAt(0) != '/')) {
      return getResources('/' + resourceName, callingClass);
    }
    return url;
  }
  
  /**
   * This is a convenience method to load a resource as a stream. <p/> The
   * algorithm used to find the resource is given in getResource()
   * 
   * @param resourceName The name of the resource to load
   * @param callingClass The Class object of the calling object
   */
  public static InputStream getResourceAsStream(
    String resourceName, 
    Class<?> callingClass) {
      URL url = getResource(resourceName, callingClass);
      try {
        return (url != null) ? url.openStream() : null;
      } catch (IOException e) {
        return null;
      }
  }

  /**
   * Load a class with a given name. <p/> It will try to load the class in the
   * following order:
   * <ul>
   * <li>From Thread.currentThread().getContextClassLoader()
   * <li>Using the basic Class.forName()
   * <li>From ClassLoaderUtil.class.getClassLoader()
   * <li>From the callingClass.getClassLoader()
   * </ul>
   * 
   * @param className The name of the class to load
   * @param callingClass The Class object of the calling object
   * @throws ClassNotFoundException If the class cannot be found anywhere.
   */
  public static Class<?> loadClass(
    String className, 
    Class<?> callingClass) 
      throws ClassNotFoundException {
    try {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl != null) {
        return cl.loadClass(className);
      }      
      return loadClass2(className, callingClass);
    } catch (ClassNotFoundException e) {
      return loadClass2(className, callingClass);
    }
  }

  private static Class<?> loadClass2(
    String className, 
    Class<?> callingClass) 
      throws ClassNotFoundException {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException ex) {
      try {
        return ServiceUtil.class.getClassLoader().loadClass(className);
      } catch (ClassNotFoundException exc) {
        return callingClass.getClassLoader().loadClass(className);
      }
    }
  }

}
