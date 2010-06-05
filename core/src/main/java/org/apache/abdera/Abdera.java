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
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.AbderaConfiguration;
import org.apache.abdera.util.Configuration;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

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
    private Factory factory;
    private Parser parser;
    private XPath xpath;
    private ParserFactory parserFactory;
    private WriterFactory writerFactory;
    private Writer writer;

    /**
     * Initialize using the default Abdera Configuration
     */
    public Abdera() {
        this(AbderaConfiguration.getDefault());
    }

    /**
     * Initialize using the specified Abdera Configuration
     * 
     * @param config The Abdera Configuration to use
     */
    public Abdera(Configuration config) {
        this.config = config;
        IRI.preinit(); // initializes the IRI stuff to improve performance later
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
    public synchronized Factory getFactory() {
        if (factory == null)
            factory = newFactory();
        return factory;
    }

    /**
     * Return the singleton instance of org.apache.abdera.parser.Parser
     * 
     * @return The parser instance
     */
    public synchronized Parser getParser() {
        if (parser == null)
            parser = newParser();
        return parser;
    }

    /**
     * Return the singleton instance of org.apache.abdera.xpath.XPath
     * 
     * @return The XPath instance
     */
    public synchronized XPath getXPath() {
        if (xpath == null)
            xpath = newXPath();
        return xpath;
    }

    /**
     * Return the singleton instance of org.apache.abdera.parser.ParserFactory. The Parser Factory is used to acquire
     * alternative parser implementation instances.
     * 
     * @return The ParserFactory instance
     */
    public synchronized ParserFactory getParserFactory() {
        if (parserFactory == null)
            parserFactory = newParserFactory();
        return parserFactory;
    }

    /**
     * Return the singleton instance of org.apache.abdera.writer.WriterFactory. The Writer Factory is used to acquire
     * alternative writer implementation instances.
     * 
     * @return The WriterFactory instance
     */
    public synchronized WriterFactory getWriterFactory() {
        if (writerFactory == null)
            writerFactory = newWriterFactory();
        return writerFactory;
    }

    /**
     * Return the singleton instance of the default org.apache.abdera.writer.Writer implementation.
     * 
     * @return The default writer implementation
     */
    public synchronized Writer getWriter() {
        if (writer == null)
            writer = newWriter();
        return writer;
    }

    /**
     * Return a new instance of org.apache.abdera.factory.Factory
     * 
     * @return A new factory instance
     */
    private Factory newFactory() {
        return config.newFactoryInstance(this);
    }

    /**
     * Return a new instance of org.apache.abdera.parser.Parser
     * 
     * @return A new parser instance
     */
    private Parser newParser() {
        return config.newParserInstance(this);
    }

    /**
     * Return a new instance of org.apache.abdera.xpath.XPath
     * 
     * @return A new XPath instance
     */
    private XPath newXPath() {
        return config.newXPathInstance(this);
    }

    /**
     * Return a new instance of org.apache.abdera.parser.ParserFactory
     * 
     * @return A new ParserFactory instance
     */
    private ParserFactory newParserFactory() {
        return config.newParserFactoryInstance(this);
    }

    /**
     * Return a new instance of org.apache.abdera.writer.WriterFactory
     * 
     * @return A new WriterFactory instance
     */
    private WriterFactory newWriterFactory() {
        return config.newWriterFactoryInstance(this);
    }

    /**
     * Return a new instance of the default org.apache.abdera.writer.Writer
     * 
     * @return A new default writer implementation instance
     */
    private Writer newWriter() {
        return config.newWriterInstance(this);
    }

    /**
     * Return a new instance of the default org.apache.abdera.writer.Writer
     * 
     * @return A new default writer implementation instance
     */
    public StreamWriter newStreamWriter() {
        return config.newStreamWriterInstance(this);
    }

    // Static convenience methods //

    /**
     * Return a new Factory instance using a non-shared Abdera object
     * 
     * @return A new factory instance
     */
    public static Factory getNewFactory() {
        return (new Abdera()).newFactory();
    }

    /**
     * Return a new Parser instance using a non-shared Abdera object
     * 
     * @return A new parser instance
     */
    public static Parser getNewParser() {
        return (new Abdera()).newParser();
    }

    /**
     * Return a new XPath instance using a non-shared Abdera object
     * 
     * @return A new XPath instance
     */
    public static XPath getNewXPath() {
        return (new Abdera()).newXPath();
    }

    /**
     * Return a new ParserFactory instance using a non-shared Abdera object
     * 
     * @return A new ParserFactory instance
     */
    public static ParserFactory getNewParserFactory() {
        return (new Abdera()).newParserFactory();
    }

    /**
     * Return a new WriterFactory instance using a non-shared Abdera object
     * 
     * @return A new WriterFactory instance
     */
    public static WriterFactory getNewWriterFactory() {
        return (new Abdera()).newWriterFactory();
    }

    /**
     * Return a new instance of the default Writer using a non-shared Abdera object
     * 
     * @return A new default writer implementation instance
     */
    public static Writer getNewWriter() {
        return (new Abdera()).newWriter();
    }

    /**
     * Return a new instance of the default StreamWriter using a non-shared Abdera object
     * 
     * @return A new default stream writer implementation instance
     */
    public static StreamWriter getNewStreamWriter() {
        return (new Abdera()).newStreamWriter();
    }
}
