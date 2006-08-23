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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.writer.NamedWriter;

public final class AbderaConfiguration 
  implements Constants, Cloneable {
  
  private static AbderaConfiguration instance = null;
  
  public static AbderaConfiguration getDefault() {
    if (instance == null) {
      try {
        ResourceBundle bundle = ResourceBundle.getBundle("abdera");
        instance = new AbderaConfiguration(bundle);
      } catch (Exception e) {
        instance = new AbderaConfiguration();
      }
    } 
    return instance;
  }
  
  private static ResourceBundle getBundle(
    ClassLoader loader, 
    Locale locale) {
      try {
        ResourceBundle bundle = 
          ResourceBundle.getBundle(
            "abdera", 
            locale, 
            loader);
        return bundle;
      } catch (Exception e) {
        // Nothing
      }
      return null;
  }
  
  private ResourceBundle bundle = null;
  private List<ExtensionFactory> factories = null;
  private Map<String,NamedWriter> writers = null;
  private Map<String,NamedParser> parsers = null;
  private String xpath = null;
  private String parser = null;
  private String factory = null;
  
  public AbderaConfiguration() {}
  
  protected AbderaConfiguration(ResourceBundle bundle) {
    this.bundle = bundle;
  }  
  
  private synchronized ResourceBundle getBundle() {
    if (bundle == null) {
      bundle = AbderaConfiguration.getBundle(
        ServiceUtil.getClassLoader(), 
        Locale.getDefault());
    } 
    return bundle;
  }

  public String getConfigurationOption(String id) {
    String option = System.getProperty(id);
    try {
      ResourceBundle bundle = getBundle();
      if (option == null && bundle != null)
        option = bundle.getString(id);
    } catch (Exception e) {}
    return option;
  }
  
  public String getConfigurationOption(String id, String _default) {
    String value = getConfigurationOption(id);
    return (value != null) ? value : _default;
  }
  
  public String getDefaultXPath() {
    return (xpath == null) ? 
      getConfigurationOption(CONFIG_XPATH, DEFAULT_XPATH) : xpath;
  }
  
  public String getDefaultParser() {
    return (parser == null) ? 
      getConfigurationOption(CONFIG_PARSER, DEFAULT_PARSER) : parser;
  }
  
  public String getDefaultFactory() {
    return (factory == null) ? 
      getConfigurationOption(CONFIG_FACTORY, DEFAULT_FACTORY) : factory;
  }
  
  public synchronized void addExtensionFactory(ExtensionFactory factory) {
    List<ExtensionFactory> factories = getExtensionFactories();
    if (!factories.contains(factory))
      factories.add(factory);
  }
  
  public synchronized List<ExtensionFactory> getExtensionFactories() {
    if (factories == null) factories = ServiceUtil.loadExtensionFactories();
    return factories;
  }
  
  public synchronized void addNamedWriter(NamedWriter writer) {
    Map<String,NamedWriter> writers = getNamedWriters();
    writers.put(writer.getName(), writer);
  }
  
  public synchronized Map<String,NamedWriter> getNamedWriters() {
    if (writers == null) {
      List<NamedWriter> _writers = 
        ServiceUtil._loadimpls(
          "META-INF/services/org.apache.abdera.writer.NamedWriter");
      writers = new HashMap<String,NamedWriter>();
      for (NamedWriter writer : _writers) {
        writers.put(writer.getName(), writer);
      }
    }
    return writers;
  }
  
  public synchronized void addNamedParser(NamedParser parser) {
    Map<String,NamedParser> parsers = getNamedParsers();
    parsers.put(parser.getName(), parser);
  }
  
  public synchronized Map<String,NamedParser> getNamedParsers() {
    if (parsers == null) {
      List<NamedParser> _parsers = 
        ServiceUtil._loadimpls(
          "META-INF/services/org.apache.abdera.parser.NamedParser");
      parsers = new HashMap<String,NamedParser>();
      for (NamedParser parser : _parsers) {
        parsers.put(parser.getName(), parser);
      }
    }
    return parsers;
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
