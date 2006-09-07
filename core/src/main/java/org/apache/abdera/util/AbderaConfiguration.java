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

import java.util.Collections;
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
  
  public static synchronized AbderaConfiguration getDefault() {
    AbderaConfiguration instance = null;
    try {
      ResourceBundle bundle = ResourceBundle.getBundle("abdera");
      instance = new AbderaConfiguration(bundle);
    } catch (Exception e) {
      instance = new AbderaConfiguration();
    }
    return instance; 
  }
  
  private static ResourceBundle getBundle(
    ClassLoader loader, 
    Locale locale) {
      ResourceBundle bundle = null;
      try {
        bundle = 
          ResourceBundle.getBundle(
            "abdera", 
            locale, 
            loader);
      } catch (Exception e) {
        // Do nothing
      }
      return bundle;
  }
  
  private final ResourceBundle bundle;
  private final String xpath;
  private final String parser;
  private final String factory;
  private final String parserFactory;
  private final String writerFactory;
  private final String writer;
  private final List<ExtensionFactory> factories;
  private final Map<String,NamedWriter> writers;
  private final Map<String,NamedParser> parsers;
  
  public AbderaConfiguration() {
    this(null);
  }
  
  protected AbderaConfiguration(ResourceBundle bundle) {
    this.bundle = (bundle != null) ? bundle : 
      AbderaConfiguration.getBundle(
        ServiceUtil.getClassLoader(), 
        Locale.getDefault());
    xpath = getConfigurationOption(CONFIG_XPATH, DEFAULT_XPATH);
    parser = getConfigurationOption(CONFIG_PARSER, DEFAULT_PARSER);
    factory = getConfigurationOption(CONFIG_FACTORY, DEFAULT_FACTORY);
    parserFactory = getConfigurationOption(CONFIG_PARSERFACTORY, DEFAULT_PARSERFACTORY);
    writerFactory = getConfigurationOption(CONFIG_WRITERFACTORY, DEFAULT_WRITERFACTORY);
    writer = getConfigurationOption(CONFIG_WRITER, DEFAULT_WRITER);
    factories = ServiceUtil.loadExtensionFactories();
    writers = initNamedWriters();
    parsers = initNamedParsers();
  }  
  
  private ResourceBundle getBundle() {
    return bundle;
  }

  public String getConfigurationOption(String id) {
    String option = System.getProperty(id);
    if (option == null) {
      try {
        ResourceBundle bundle = getBundle();
        if (bundle != null) option = bundle.getString(id);
      } catch (Exception e) {
        // Do Nothing
      }
    }
    return option;
  }
  
  public String getConfigurationOption(String id, String _default) {
    String value = getConfigurationOption(id);
    return (value != null) ? value : _default;
  }
  
  public String getDefaultXPath() {
    return xpath;
  }
  
  public String getDefaultParser() {
    return parser;
  }
  
  public String getDefaultFactory() {
    return factory;
  }
  
  public String getDefaultParserFactory() {
    return parserFactory; 
  }
  
  public String getDefaultWriterFactory() {
    return writerFactory;
  }
  
  public String getDefaultWriter() {
    return writer;
  }
  
  public void addExtensionFactory(ExtensionFactory factory) {
    List<ExtensionFactory> factories = getExtensionFactories();
    if (!factories.contains(factory))
      factories.add(factory);
  }
  
  public List<ExtensionFactory> getExtensionFactories() {
    return factories;
  }
  
  public void addNamedWriter(NamedWriter writer) {
    Map<String,NamedWriter> writers = getNamedWriters();
    writers.put(writer.getName(), writer);
  }
  
  private Map<String,NamedWriter> initNamedWriters() {
    Map<String,NamedWriter> writers = null;
    List<NamedWriter> _writers = 
      ServiceUtil._loadimpls(NAMED_WRITER);
    writers = Collections.synchronizedMap(new HashMap<String,NamedWriter>());
    for (NamedWriter writer : _writers) {
      writers.put(writer.getName().toLowerCase(), writer);
    }
    return writers;
  }
  
  public Map<String,NamedWriter> getNamedWriters() {
    return writers;
  }
  
  public void addNamedParser(NamedParser parser) {
    Map<String,NamedParser> parsers = getNamedParsers();
    parsers.put(parser.getName(), parser);
  }
  
  private Map<String,NamedParser> initNamedParsers() {
    Map<String,NamedParser> parsers = null;
    List<NamedParser> _parsers = 
      ServiceUtil._loadimpls(NAMED_PARSER);
    parsers = Collections.synchronizedMap(new HashMap<String,NamedParser>());
    for (NamedParser parser : _parsers) {
      parsers.put(parser.getName().toLowerCase(), parser);
    }
    return parsers;
  }
  
  public Map<String,NamedParser> getNamedParsers() {
    return parsers;
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
