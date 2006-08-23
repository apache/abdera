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

public final class Abdera {
  
  private AbderaConfiguration config = null;
  private Factory factory = null;
  private Parser parser = null;
  private XPath xpath = null;
  private ParserFactory parserFactory = null;
  private WriterFactory writerFactory = null;
  private Writer writer = null;
  
  public Abdera() {
    config = AbderaConfiguration.getDefault();
  }
  
  public Abdera(AbderaConfiguration config) {
    this.config = config;
  }
  
  public AbderaConfiguration getConfiguration() {
    return config;
  }
  
  public synchronized Factory getFactory() {
    if (factory == null)
      factory = newFactory();
    return factory;
  }
  
  public static Factory getNewFactory() {
    return (new Abdera()).newFactory();
  }

  public Parser getParser() {
    if (parser == null)
      parser = newParser();
    return parser;
  }
  
  public static Parser getNewParser() {
    return (new Abdera()).newParser();
  }
  
  public XPath getXPath() {
    if (xpath == null)
      xpath = newXPath();
    return xpath;
  }
  
  public static XPath getNewXPath() {
    return (new Abdera()).newXPath();
  }

  public ParserFactory getParserFactory() {
    if (parserFactory == null)
      parserFactory = newParserFactory();
    return parserFactory;
  }
  
  public static ParserFactory getNewParserFactory() {
    return (new Abdera()).newParserFactory();
  }

  public WriterFactory getWriterFactory() {
    if (writerFactory == null)
      writerFactory = newWriterFactory();
    return writerFactory;
  }
  
  public static WriterFactory getNewWriterFactory() {
    return (new Abdera()).newWriterFactory();
  }

  public Writer getWriter() {
    if (writer == null)
      writer = newWriter();
    return writer;
  }
  
  public static Writer getNewWriter() {
    return (new Abdera()).newWriter();
  }

  private Factory newFactory() {
    return ServiceUtil.newFactoryInstance(this);
  }
    
  private Parser newParser() {
    return ServiceUtil.newParserInstance(this);
  }
    
  private XPath newXPath() {
    return ServiceUtil.newXPathInstance(this);
  }
    
  private ParserFactory newParserFactory() {
    return
      (ParserFactory) ServiceUtil.newInstance(
      "org.apache.abdera.parser.ParserFactory",
      "org.apache.abdera.parser.stax.FOMParserFactory", 
      this);
  }
    
  private WriterFactory newWriterFactory() {
    return
      (WriterFactory) ServiceUtil.newInstance(
        "org.apache.abdera.writer.WriterFactory",
        "org.apache.abdera.parser.stax.FOMWriterFactory", 
        this);
  }
    
  private Writer newWriter() {
    return 
      (Writer) ServiceUtil.newInstance(
      "org.apache.abdera.writer.Writer",
      "org.apache.abdera.parser.stax.FOMWriter", 
      this);
  }
  
}
