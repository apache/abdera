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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.CollectionAdapter;

public class CollectionAdapterManager {
  
  public static Logger logger =
    Logger.getLogger(CollectionAdapterManager.class.getName());

  protected static final String PROP_NAME_ADAPTER_CLASS = "adapterClassName";
  protected static final String PROPERTIES_PATH = "abdera/adapter/";
  protected static final String PROPERTIES_FILE_SUFFIX = ".properties";

  // maps a feed id to an adapter instance
  protected Map<String, CollectionAdapter> adapterInstanceMap =
      new HashMap<String, CollectionAdapter>();

  protected final Abdera abdera;

  public CollectionAdapterManager(Abdera abdera) {
    this.abdera = abdera;
  }
  
  public CollectionAdapter getAdapter(
    String feedId) 
      throws Exception {
    CollectionAdapter adapter = 
      adapterInstanceMap.get(feedId);
    if (adapter != null)
      return adapter;

    // load the feed properties file
    Properties properties = loadFeedInfo(feedId);
    String className = properties.getProperty(PROP_NAME_ADAPTER_CLASS);
    if (className == null) {
      logger.warning("property '" + PROP_NAME_ADAPTER_CLASS +
          "' not found for feed '" + feedId + "'");
      throw new RuntimeException();
    }

    return createAdapterInstance(feedId, className, properties);
   }

  public Map<String,Properties> listAdapters() throws Exception {
    Map<String,Properties> results = new HashMap<String,Properties>();
    Enumeration<URL> e = 
      Thread.currentThread()
        .getContextClassLoader()
        .getResources(PROPERTIES_PATH);
    while (e.hasMoreElements()) {
      URL url = e.nextElement();
      File file = new File(url.toURI());
      if (!file.exists()) {
        throw new RuntimeException("Could not convert properties path to a File! \"" + file.getAbsolutePath() + "\" does not exist.");
      }
      File[] files = 
        file.listFiles(
          new FileFilter() {
            public boolean accept(File file) {
              return !file.isDirectory();
            }
          }
        );
      if (files != null) {
        for (File _file : files) {
          String name = _file.getName();
          int i = name.indexOf(PROPERTIES_FILE_SUFFIX);
          String id = i > -1 ? name.substring(0,i) : null;
          if (id != null) {
            Properties properties = loadFeedInfo(id);
            if (properties != null)
              results.put(id,properties);
          }
        }
      }
    }
    return results;
  }
  
  protected Properties loadFeedInfo(String feedId)
    throws Exception {
    String fileName = PROPERTIES_PATH + feedId + PROPERTIES_FILE_SUFFIX;
    InputStream in =       
      Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream(fileName);
    if (in == null) {
      throw new FileNotFoundException();
    }
    Properties props = new Properties();
    props.load(in);
    in.close();
    return props;
  }

  protected synchronized CollectionAdapter createAdapterInstance(
    String feedId,
    String className, 
    Properties properties) 
      throws Exception {
    CollectionAdapter basicAdapter = adapterInstanceMap.get(feedId);
    if (basicAdapter != null) {
      return basicAdapter;
    }
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Class<?> adapterClass = cl.loadClass(className);
    Constructor<?>[] ctors = adapterClass.getConstructors();
    for (Constructor<?> element : ctors) {
      logger.finest("Public constructor found: " +
        element.toString());
    }
    Constructor<?> c = 
      adapterClass.getConstructor(
        new Class[] {
          Abdera.class,
          Properties.class, 
          String.class});
    c.setAccessible(true);
    CollectionAdapter adapterInstance = 
      (CollectionAdapter) c.newInstance(
        abdera, 
        properties,
        feedId);

    // put this adapter instance in adapterInstanceMap
    adapterInstanceMap.put(feedId, adapterInstance);
    return adapterInstance;
  }

}