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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.abdera.converter.ConverterProvider;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;

public interface Configuration 
  extends Cloneable, Serializable {
  
  /**
   * Retrieve the value of the specified configuration option
   * @return The configuration option value or null
   */
  public abstract String getConfigurationOption(String id);
  
  /**
   * Retrieve the value of the specified configuration option or _default
   * if the value is null
   * @return The configuration option value of _default
   */
  public abstract String getConfigurationOption(String id, String _default);
  
  /**
   * Returns the Java classname of the default Abdera XPath implementation
   */
  public abstract String getDefaultXPath();
  
  /**
   * Returns the Java classname of the default Abdera Parser implementation
   */
  public abstract String getDefaultParser();
  
  /**
   * Returns the Java classname of the default Abdera Factory implementation
   */
  public abstract String getDefaultFactory();
  
  /**
   * Returns the Java classname of the default ParserFactory implementation
   */
  public abstract String getDefaultParserFactory();
  
  /**
   * Returns the Java classname of the default WriterFactory implementation
   */
  public abstract String getDefaultWriterFactory();
  
  /**
   * Returns the Java classname of the default Writer implementation
   */
  public abstract String getDefaultWriter();
  
  /**
   * Returns the Java classname of the default StreamWriter implementation
   */
  public abstract String getDefaultStreamWriter();
  
  /**
   * Registers an ExtensionFactory implementation.
   */
  public abstract void addExtensionFactory(ExtensionFactory factory);
  
  /**
   * Returns the listing of registered ExtensionFactory implementations
   */
  public abstract List<ExtensionFactory> getExtensionFactories();
  
  /**
   * Returns the listing of registered ConverterProvider implementations
   */
  public abstract List<ConverterProvider> getConverterProviders();
  
  /**
   * Registers a NamedWriter implementation
   */
  public abstract void addNamedWriter(NamedWriter writer);
  
  /**
   * Returns the collection of NamedWriters
   */
  public abstract Map<String, NamedWriter> getNamedWriters();
  
  /**
   * Returns the collection of NamedWriters
   */
  public abstract Map<String, Class<? extends StreamWriter>> getStreamWriters();
  
  /**
   * Registers a NamedParser implementation
   */
  public abstract void addNamedParser(NamedParser parser);
  
  /**
   * Returns the collection of Named Parsers
   */
  public abstract Map<String, NamedParser> getNamedParsers();
  
  public abstract Object clone();
  
}