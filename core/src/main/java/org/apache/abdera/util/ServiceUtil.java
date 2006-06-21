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
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.xpath.XPath;


/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public final class ServiceUtil 
  implements Constants {

  ServiceUtil() {}

  static private ClassLoader classLoader = null;
  
  public static Object newInstance(String id, String _default) {
    return ServiceUtil.locate(id, _default);
  }

  public static XPath newXPathInstance() {
    return (XPath) newInstance(
      CONFIG_XPATH,
      ConfigProperties.getDefaultXPath());
  }
  
  public static Parser newParserInstance() {
    return (Parser) newInstance(
      CONFIG_PARSER, 
      ConfigProperties.getDefaultParser());
  }

  public static Factory newFactoryInstance() {
    return (Factory) newInstance(
      CONFIG_FACTORY, 
      ConfigProperties.getDefaultFactory());
  }
  
  public static ClassLoader getClassLoader() {
    if (classLoader == null) {
      classLoader = Thread.currentThread().getContextClassLoader();
    }
    return classLoader;
  }

  public static void setClassLoader(ClassLoader classLoader) {
    ServiceUtil.classLoader = classLoader;
  }
  
  public static Object locate(String id, String _default) {
    Object object = locate(id);
    if (object == null && _default != null) {
      try {
        object = getClassLoader().loadClass(_default).newInstance();
      } catch (Exception e) {}
    }
    return object;
  }
  
  public static Object locate(String id) {
    Object service = checkConfigProperties(id);
    return ((service != null) ? service : checkMetaInfServices(id));
  }
  
  static Object checkConfigProperties(String id) {
    Object object = null;
    String s = ConfigProperties.getConfigurationOption(id);
    if (s != null) {
      try {
        object = getClassLoader().loadClass(s).newInstance();
      } catch (Exception e) {}
    } 
    return object;
  }
  
  static Object checkMetaInfServices(String id) {
    Object object = null;
    String sid = "META-INF/services/" + id;
    ClassLoader loader = getClassLoader();
    try {
      InputStream is = loader.getResourceAsStream(sid);
      if (is != null) {
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        if (line != null) {
          String s = line.split("#",2)[0].trim();
          object = loader.loadClass(s).newInstance();
        }
      }
    } catch (Exception e) {}
    return object;
  }

  private static List<ExtensionFactory> factories = null;
  
  public static List<ExtensionFactory> loadExtensionFactories() {
    if (factories == null) {
      factories = new ArrayList<ExtensionFactory>();
      String sid = "META-INF/services/org.apache.abdera.factory.ExtensionFactory";
      ClassLoader loader = getClassLoader();
      try {
        Enumeration e = loader.getResources(sid);
        for (;e.hasMoreElements();) {
          try {
            URL url = (URL) e.nextElement();
            InputStream is = url.openStream();
            if (is != null) {
              BufferedReader buf = 
                new BufferedReader(
                  new InputStreamReader(is));
              String line = null;
              while ((line = buf.readLine()) != null) {
                String s = line.split("#",2)[0].trim();
                if (!"".equals(s)) { 
                  ExtensionFactory factory = 
                    (ExtensionFactory) loader.loadClass(s).newInstance();
                  factories.add(factory);
                }
              }
            }
          } catch (Exception ex) {}
        }
      } catch (Exception e) {}
    }
    return factories;
  }
  
}
