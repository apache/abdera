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
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

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
   * Utility method for returning an instance of the default Abdera XPath instance
   */
  public static XPath newXPathInstance(Abdera abdera) {
    return (XPath) newInstance(
      CONFIG_XPATH,
      abdera.getConfiguration().getDefaultXPath(), 
      abdera);
  }
  
  /**
   * Utility method for returning an instance of the default Abdera Parser instance
   */
  public static Parser newParserInstance(Abdera abdera) {
    return (Parser) newInstance(
      CONFIG_PARSER, 
      abdera.getConfiguration().getDefaultParser(),
      abdera);
  }

  /**
   * Utility method for returning an instance of the defaul Abdera Factory instance
   */
  public static Factory newFactoryInstance(Abdera abdera) {
    return (Factory) newInstance(
      CONFIG_FACTORY, 
      abdera.getConfiguration().getDefaultFactory(),
      abdera);
  }
  
  public static ParserFactory newParserFactoryInstance(Abdera abdera) {
    return (ParserFactory) newInstance(
      CONFIG_PARSERFACTORY,
      abdera.getConfiguration().getDefaultParserFactory(),
      abdera);
  }
  
  public static WriterFactory newWriterFactoryInstance(Abdera abdera) {
    return (WriterFactory) newInstance(
      CONFIG_WRITERFACTORY,
      abdera.getConfiguration().getDefaultWriterFactory(),
      abdera) ;
  }
  
  public static Writer newWriterInstance(Abdera abdera) {
    return (Writer) newInstance(
      CONFIG_WRITER,
      abdera.getConfiguration().getDefaultWriter(),
      abdera);
  }
  
  /**
   * Get the context class loader for this thread
   */
  public static ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  public static Object locate(
    String id, 
    String _default, 
    Abdera abdera) {
      Object object = locate(id, abdera);
      if (object == null && _default != null) {
        object = locateInstance(getClassLoader(), _default, abdera);
      }
      return object;
  }

  /**
   * Locate a class instance for the given id
   */
  @SuppressWarnings("unchecked")
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
  public static Object locateInstance(ClassLoader loader, String id, Abdera abdera) {
    try {
      Class _class = loader.loadClass(id);
      return _create(_class, abdera);
    } catch (Exception e) {
      // Nothing
    }
    try {
      Class _class = ClassLoader.getSystemClassLoader().loadClass(id);
      return _create(_class, abdera);
    } catch (Exception e) {
      // Nothing
    }
    return null;
  }
  
  public static InputStream locateStream(ClassLoader loader, String id) {
    InputStream in = loader.getResourceAsStream(id);
    return (in != null) ? in : ClassLoader.getSystemResourceAsStream(id);
  }
  
  public static Enumeration<URL> locateResources(ClassLoader loader, String id) {
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
  
  @SuppressWarnings("unchecked")
  private static Object checkAbderaConfiguration(String id, Abdera abdera) {
    String s = abdera.getConfiguration().getConfigurationOption(id);
    return (s != null) ? locateInstance(getClassLoader(), id, abdera) : null;
  }
  
  private static Object checkMetaInfServices(String id, Abdera abdera) {
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
          object = locateInstance(loader,s, abdera);
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
  
  @SuppressWarnings("unchecked")
  protected static <T>List<T> _loadimpls(String sid) {
    List<T> impls = Collections.synchronizedList(new ArrayList<T>());
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
                T impl = (T) locateInstance(loader,s, null);
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
