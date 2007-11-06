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

import org.apache.abdera.Abdera;
import org.apache.abdera.converter.ConverterProvider;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

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
   * Get a new instance of the default Factory impl
   */
  public Factory newFactoryInstance(Abdera abdera);
  
  /**
   * Get a new instance of the default Parser impl
   */
  public Parser newParserInstance(Abdera abdera);
  
  /**
   * Get a new instance of the default XPath impl
   */
  public XPath newXPathInstance(Abdera abdera);
  
  /**
   * Get a new instance of the default ParserFactory impl
   */
  public ParserFactory newParserFactoryInstance(Abdera abdera);
  
  /**
   * Get a new instance of the default WriterFactory impl
   */
  public WriterFactory newWriterFactoryInstance(Abdera abdera);
  
  /**
   * Get a new instance of the default Writer impl
   */
  public Writer newWriterInstance(Abdera abdera);
  
  /**
   * Get a new instance of the default StreamWriter impl
   */
  public StreamWriter newStreamWriterInstance(Abdera abdera);
  
  /**
   * Get a listing of ConverterProviders
   */
  public ConverterProvider[] getConverterProviders();
  
  /**
   * Get the collection of NamedParsers;
   */
  public Map<String,NamedParser> getNamedParsers();
  
  /**
   * Get the collection of NamedWriters
   */
  public Map<String,NamedWriter> getNamedWriters();
  
  /**
   * Get the collection of Named StreamWriters
   */
  public Map<String,Class<? extends StreamWriter>> getStreamWriters();
  
  /**
   * Get the collection of ExtensionFactory impls
   */
  public List<ExtensionFactory> getExtensionFactories();
  
  
  public abstract Object clone();
}