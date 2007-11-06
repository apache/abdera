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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.abdera.converter.ConverterProvider;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;

/**
 * Provides the basic configuration for the Abdera default implementation.  This
 * class should not be accessed by applications directly without very good reason.
 */
public final class AbderaConfiguration 
  implements Constants, Configuration {
  
  private static final long serialVersionUID = 7460203853824337559L;

  /**
   * Returns the default configuration. Every call to this method returns
   * a new AbderaConfiguration instance using abdera.properties
   */
  public static synchronized Configuration getDefault() {
    Configuration instance = null;
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
  private final String streamwriter;
  private final List<ExtensionFactory> factories;
  private final List<ConverterProvider> providers;
  private final Map<String,NamedWriter> writers;
  private final Map<String,Class<? extends StreamWriter>> streamwriters;
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
    streamwriter = getConfigurationOption(CONFIG_STREAMWRITER, DEFAULT_STREAMWRITER);
    factories = ServiceUtil.loadExtensionFactories();
    providers = ServiceUtil.loadConverterProviders();
    writers = initNamedWriters();
    parsers = initNamedParsers();
    streamwriters = initStreamWriters();
  }  
  
  private ResourceBundle getBundle() {
    return bundle;
  }

  /**
   * Retrieve the value of the specified configuration option
   * @return The configuration option value or null
   */
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
  
  /**
   * Retrieve the value of the specified configuration option or _default
   * if the value is null
   * @return The configuration option value of _default
   */
  public String getConfigurationOption(String id, String _default) {
    String value = getConfigurationOption(id);
    return (value != null) ? value : _default;
  }
  
  /**
   * Returns the Java classname of the default Abdera XPath implementation
   */
  public String getDefaultXPath() {
    return xpath;
  }
  
  /**
   * Returns the Java classname of the default Abdera Parser implementation
   */
  public String getDefaultParser() {
    return parser;
  }
  
  /**
   * Returns the Java classname of the default Abdera Factory implementation
   */
  public String getDefaultFactory() {
    return factory;
  }
  
  /**
   * Returns the Java classname of the default ParserFactory implementation
   */
  public String getDefaultParserFactory() {
    return parserFactory; 
  }
  
  /**
   * Returns the Java classname of the default WriterFactory implementation
   */
  public String getDefaultWriterFactory() {
    return writerFactory;
  }
  
  /**
   * Returns the Java classname of the default Writer implementation
   */
  public String getDefaultWriter() {
    return writer;
  }

  /**
   * Returns the Java classname of the default StreamWriter implementation
   */
  public String getDefaultStreamWriter() {
    return streamwriter;
  }
  
  /**
   * Registers an ExtensionFactory implementation.
   */
  public void addExtensionFactory(ExtensionFactory factory) {
    List<ExtensionFactory> factories = getExtensionFactories();
    if (!factories.contains(factory))
      factories.add(factory);
  }
  
  /**
   * Returns the listing of registered ExtensionFactory implementations
   */
  public List<ExtensionFactory> getExtensionFactories() {
    return factories;
  }
  
  /**
   * Returns the listing of registered ConverterProvider implementations
   */
  public List<ConverterProvider> getConverterProviders() {
    return providers;
  }
  
  /**
   * Registers a NamedWriter implementation
   */
  public void addNamedWriter(NamedWriter writer) {
    Map<String,NamedWriter> writers = getNamedWriters();
    writers.put(writer.getName(), writer);
  }
  
  /**
   * Registers NamedWriter implementations using 
   * the /META-INF/services/org.apache.abdera.writer.NamedWriter file
   */
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

  /**
   * Registers StreamWriter implementations using 
   * the /META-INF/services/org.apache.abdera.writer.StreamWriter file
   */
  private Map<String,Class<? extends StreamWriter>> initStreamWriters() {
    Map<String,Class<? extends StreamWriter>> writers = null;
    List<Class<? extends StreamWriter>> _writers = 
      ServiceUtil._loadimpls(STREAM_WRITER,true);
    writers = Collections.synchronizedMap(new HashMap<String,Class<? extends StreamWriter>>());
    for (Class<? extends StreamWriter> writer : _writers) {
      try {
        Field field = writer.getField("NAME");
        if (Modifier.isStatic(field.getModifiers())) {
          String name = (String)field.get(null);
          if (name != null)
            writers.put(name.toLowerCase(), writer);
        }
      } catch (Exception e) {}
    }
    return writers;
  }
  
  /**
   * Returns the collection of NamedWriters
   */
  public Map<String,NamedWriter> getNamedWriters() {
    return writers;
  }

  /**
   * Returns the collection of NamedWriters
   */
  public Map<String,Class<? extends StreamWriter>> getStreamWriters() {
    return streamwriters;
  }
  
  /**
   * Registers a NamedParser implementation
   */
  public void addNamedParser(NamedParser parser) {
    Map<String,NamedParser> parsers = getNamedParsers();
    parsers.put(parser.getName(), parser);
  }
  
  /**
   * Registers NamedParser implementations using 
   * the /META-INF/services/org.apache.abdera.writer.NamedParser file 
   */
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
  
  /**
   * Returns the collection of Named Parsers
   */
  public Map<String,NamedParser> getNamedParsers() {
    return parsers;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
  
}
