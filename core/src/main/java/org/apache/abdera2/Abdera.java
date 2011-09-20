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
package org.apache.abdera2;

import org.apache.abdera2.common.Discover;
import org.apache.abdera2.common.anno.Version;
import org.apache.abdera2.factory.Factory;
import org.apache.abdera2.model.Base;
import org.apache.abdera2.model.Categories;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.Service;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.ParserFactory;
import org.apache.abdera2.protocol.error.ErrorExtensionFactory;
import org.apache.abdera2.util.AbderaConfiguration;
import org.apache.abdera2.util.Configuration;
import org.apache.abdera2.writer.Writer;
import org.apache.abdera2.writer.WriterFactory;
import org.apache.abdera2.xpath.XPath;

/**
 * The top level entry point for Abdera that provides access to various subcomponents. Upon creation, this class will
 * attempt to create singleton instances of each of the various subcomponents components. These instances may be
 * retrieved using the appropriate get___ methods. Alternatively, new instances may be created using the appropriate
 * new___ methods. Instances of the Abdera object, and it's direct children (Parser, Factory, XPath, etc) are
 * Threadsafe. Because of the dynamic configuration model Abdera uses, creating a new instance of the Abdera object can
 * be time consuming. It is, therefore, a good idea for applications to create only a single static instance of the
 * Abdera object (see the Abdera.getInstance() method). Abdera's configuration model depends heavily on the context
 * classloader. Extension Factories, custom writers, custom parsers, etc are all discovered automatically by searching
 * the classpath. This means that care needs to be taken when using Abdera in environments that utilize multiple
 * classloaders (such as Web application servers).
 */
@Version(value="v2.0-SNAPSHOT",
         name="Abdera",
         uri="http://abdera.apache.org")
public class Abdera {

    /** A static instance of Abdera **/
    private static Abdera instance;

    /**
     * Get a static instance of the Abdera object.
     */
    public static synchronized Abdera getInstance() {
        if (instance == null)
            instance = new Abdera();
        return instance;
    }
    
    private final Configuration config;
    private final Factory factory;
    private final Parser parser;
    private final XPath xpath;
    private final ParserFactory parserFactory;
    private final WriterFactory writerFactory;
    private final Writer writer;

    private Abdera() {
        this(null);
    }

    /**
     * Init the configuration. This will search the classpath for
     * a configured Configuration instance or will use the 
     * default AbderaConfiguration. 
     */
    private static Configuration initConfig(Abdera abdera) {
      return Discover.locate(
        Configuration.class, 
        AbderaConfiguration.class.getName(), abdera);
    }
    
    /**
     * Initialize using the specified Abdera Configuration
     * 
     * @param config The Abdera Configuration to use
     */
    public Abdera(Configuration config) {
        this.config = config!=null?config:initConfig(this);
        factory = create(Factory.class);
        xpath = create(XPath.class);
        parserFactory = create(ParserFactory.class);
        writerFactory = create(WriterFactory.class);
        writer = create(Writer.class);
        parser = create(Parser.class);
    }

    public org.apache.abdera2.protocol.error.Error newError() {
      return getFactory().newExtensionElement(ErrorExtensionFactory.ERROR);
    }
    
    /**
     * Create a new Feed instance. This is a convenience shortcut for <code>abdera.getFactory().newFeed()</code>
     * 
     * @return A newly created feed element
     */
    public Feed newFeed() {
        return getFactory().newFeed();
    }

    /**
     * Create a new Entry instance. This is a convenience shortcut for <code>abdera.getFactory().newEntry()</code>
     * 
     * @return A newly created entry element
     */
    public Entry newEntry() {
        return getFactory().newEntry();
    }

    /**
     * Create a new Service instance. This is a convenience shortcut for <code>abdera.getFactory().newService()</code>
     * 
     * @return A newly created service element
     */
    public Service newService() {
        return getFactory().newService();
    }

    /**
     * Create a new Categories instance. This is a convenience shortcut for
     * <code>abdera.getFactory().newCategories()</code>
     * 
     * @return A newly created categories element
     */
    public Categories newCategories() {
        return getFactory().newCategories();
    }
    
    /**
     * Return the Abdera Configuration used to initialize this instance
     * 
     * @return The Abdera configuration
     */
    public Configuration getConfiguration() {
        return config;
    }

    /**
     * Return the singleton instance of org.apache.abdera.factory.Factory
     * 
     * @return The factory instance
     */
    public Factory getFactory() {
        return factory;
    }

    /**
     * Return the singleton instance of org.apache.abdera.parser.Parser
     * 
     * @return The parser instance
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * Return the singleton instance of org.apache.abdera.xpath.XPath
     * 
     * @return The XPath instance
     */
    public XPath getXPath() {
        return xpath;
    }

    /**
     * Return the singleton instance of org.apache.abdera.parser.ParserFactory. The Parser Factory is used to acquire
     * alternative parser implementation instances.
     * 
     * @return The ParserFactory instance
     */
    public ParserFactory getParserFactory() {
        return parserFactory;
    }

    /**
     * Return the singleton instance of org.apache.abdera.writer.WriterFactory. The Writer Factory is used to acquire
     * alternative writer implementation instances.
     * 
     * @return The WriterFactory instance
     */
    public WriterFactory getWriterFactory() {
        return writerFactory;
    }

    /**
     * Return the singleton instance of the default org.apache.abdera.writer.Writer implementation.
     * 
     * @return The default writer implementation
     */
    public Writer getWriter() {
        return writer;
    }

    /**
     * Create an instance of the specified object using the Abdera Configuration
     */
    @SuppressWarnings("unchecked")
    public <T>T create(Class<T> _class) {
      if (Base.class.isAssignableFrom(_class)) {
        return (T)getFactory().newElement(_class);
      } else 
        return config.newInstance(this, _class);
    }
    
}
