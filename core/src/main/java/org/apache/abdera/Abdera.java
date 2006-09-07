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
 * Alternatively, new instances may be retrieved using the appropriate 
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
    return ServiceUtil.newXPathInstance(this);
  }
    
  /**
   * Return a new instance of org.apache.abdera.parser.ParserFactory
   */
  private ParserFactory newParserFactory() {
    return
      (ParserFactory) ServiceUtil.newInstance(
      "org.apache.abdera.parser.ParserFactory",
      "org.apache.abdera.parser.stax.FOMParserFactory", 
      this);
  }
    
  /**
   * Return a new instance of org.apache.abdera.writer.WriterFactory
   */
  private WriterFactory newWriterFactory() {
    return
      (WriterFactory) ServiceUtil.newInstance(
        "org.apache.abdera.writer.WriterFactory",
        "org.apache.abdera.parser.stax.FOMWriterFactory", 
        this);
  }
    
  /**
   * Return a new instance of org.apache.abdera.writer.Writer
   */
  private Writer newWriter() {
    return 
      (Writer) ServiceUtil.newInstance(
      "org.apache.abdera.writer.Writer",
      "org.apache.abdera.parser.stax.FOMWriter", 
      this);
  }
  
  // Static convenience methods //
  
  public static Factory getNewFactory() {
    return (new Abdera()).newFactory();
  }

  public static Parser getNewParser() {
    return (new Abdera()).newParser();
  }
  
  public static XPath getNewXPath() {
    return (new Abdera()).newXPath();
  }

  public static ParserFactory getNewParserFactory() {
    return (new Abdera()).newParserFactory();
  }
  
  public static WriterFactory getNewWriterFactory() {
    return (new Abdera()).newWriterFactory();
  }

  public static Writer getNewWriter() {
    return (new Abdera()).newWriter();
  }
}
