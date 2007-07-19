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
package org.apache.abdera;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.AbderaConfiguration;
import org.apache.abdera.util.ServiceUtil;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

/**
 * A top level entry point for Abdera that provides access to various
 * subcomponents.  Upon creation, this class will attempt to create 
 * singleton instances of each of the various subcomponents components.
 * These instances may be retrieved using the appropriate get___ methods.
 * Alternatively, new instances may be created using the appropriate 
 * new___ methods. 
 */
public final class Abdera {

  private final AbderaConfiguration config;
  private final Factory factory;
  private final Parser parser;
  private final XPath xpath;
  private final ParserFactory parserFactory;
  private final WriterFactory writerFactory;
  private final Writer writer;
  
  /**
   * Initialize using the default Abdera Configuration
   */
  public Abdera() {
    this(AbderaConfiguration.getDefault());
  }
  
  /**
   * Initialize using the specified Abdera Configuration
   * @param config The Abdera Configuration to use
   */
  public Abdera(AbderaConfiguration config) {
    this.config = config;
    factory = newFactory();
    parser = newParser();
    xpath = newXPath();
    parserFactory = newParserFactory();
    writerFactory = newWriterFactory();
    writer = newWriter();
  }
  
  /**
   * Create a new Feed instance.  This is a convenience shortcut for
   * <code>abdera.getFactory().newFeed()</code>
   * @return A newly created feed element
   */
  public Feed newFeed() {
    return getFactory().newFeed();
  }
  
  /**
   * Create a new Entry instance.  This is a convenience shortcut for
   * <code>abdera.getFactory().newEntry()</code>
   */
  public Entry newEntry() {
    return getFactory().newEntry();
  }
  
  /**
   * Create a new Service instance.  This is a convenience shortcut for
   * <code>abdera.getFactory().newService()</code>
   */
  public Service newService() {
    return getFactory().newService();
  }
  
  /**
   * Create a new Categories instance.  This is a convenience shortcut
   * for <code>abdera.getFactory().newCategories()</code>
   */
  public Categories newCategories() {
    return getFactory().newCategories();
  }
  
  /**
   * Return the Abdera Configuration used to initialize this instance
   */
  public AbderaConfiguration getConfiguration() {
    return config;
  }
  
  /**
   * Return the singleton instance of org.apache.abdera.factory.Factory
   */
  public Factory getFactory() {
    return factory;
  }
  
  /**
   * Return the singleton instance of org.apache.abdera.parser.Parser
   */
  public Parser getParser() {
    return parser;
  }
  
  /**
   * Return the singleton instance of org.apache.abdera.xpath.XPath
   */
  public XPath getXPath() {
    return xpath;
  }
  
  /**
   * Return the singleton instance of org.apache.abdera.parser.ParserFactory
   */
  public ParserFactory getParserFactory() {
    return parserFactory;
  }
  
  /**
   * Return the singleton instance of org.apache.abdera.writer.WriterFactory
   */
  public WriterFactory getWriterFactory() {
    return writerFactory;
  }
  
  /**
   * Return the singleton instance of org.apache.abdera.writer.Writer
   */
  public Writer getWriter() {
    return writer;
  }
  
  /**
   * Return a new instance of org.apache.abdera.factory.Factory
   */
  private Factory newFactory() {
    return ServiceUtil.newFactoryInstance(this);
  }
    
  /**
   * Return a new instance of org.apache.abdera.parser.Parser
   */
  private Parser newParser() {
    return ServiceUtil.newParserInstance(this);
  }
    
  /**
   * Return a new instance of org.apache.abdera.xpath.XPath
   */
  private XPath newXPath() {
    try {
      return ServiceUtil.newXPathInstance(this);
    } catch (NoClassDefFoundError n) {
      throw new RuntimeException("An Abdera XPath implementation is not available",n);
    }
  }
    
  /**
   * Return a new instance of org.apache.abdera.parser.ParserFactory
   */
  private ParserFactory newParserFactory() {
    try {
      return ServiceUtil.newParserFactoryInstance(this);
    } catch (NoClassDefFoundError n) {
      throw new RuntimeException("An Abdera Parser implementation is not available",n);
    }
  }
    
  /**
   * Return a new instance of org.apache.abdera.writer.WriterFactory
   */
  private WriterFactory newWriterFactory() {
    try {
      return ServiceUtil.newWriterFactoryInstance(this);
    } catch (NoClassDefFoundError n) {
      throw new RuntimeException("An Abdera WriterFactory implementation is not available",n);
    }
  }
    
  /**
   * Return a new instance of org.apache.abdera.writer.Writer
   */
  private Writer newWriter() {
    try {
      return ServiceUtil.newWriterInstance(this);
    } catch (NoClassDefFoundError n) {
      throw new RuntimeException("An Abdera Writer implementation is not available",n);
    }
  }
  
  // Static convenience methods //
  
  /**
   * Return a new Factory instance using a non-shared Abdera object
   */
  public static Factory getNewFactory() {
    return (new Abdera()).newFactory();
  }

  /**
   * Return a new Parser instance using a non-shared Abdera object
   */
  public static Parser getNewParser() {
    return (new Abdera()).newParser();
  }
  
  /**
   * Return a new XPath instance using a non-shared Abdera object
   */
  public static XPath getNewXPath() {
    return (new Abdera()).newXPath();
  }

  /**
   * Return a new ParserFactory instance using a non-shared Abdera object
   */
  public static ParserFactory getNewParserFactory() {
    return (new Abdera()).newParserFactory();
  }
  
  /**
   * Return a new WriterFactory instance using a non-shared Abdera object
   */
  public static WriterFactory getNewWriterFactory() {
    return (new Abdera()).newWriterFactory();
  }

  /**
   * Return a new instance of the default Writer using a non-shared Abdera object 
   */
  public static Writer getNewWriter() {
    return (new Abdera()).newWriter();
  }
}
