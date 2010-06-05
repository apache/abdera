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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.factory.StreamBuilder;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides the basic configuration for the Abdera default implementation. This class should not be accessed by
 * applications directly without very good reason.
 */
public final class AbderaConfiguration implements Constants, Configuration {

    private static final long serialVersionUID = 7460203853824337559L;
    private final static Log log = LogFactory.getLog(AbderaConfiguration.class);

    /**
     * Returns the default configuration. Every call to this method returns a new AbderaConfiguration instance using
     * abdera.properties
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

    private static ResourceBundle getBundle(Locale locale) {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle("abdera", locale, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            // Do nothing
        }
        return bundle;
    }

    private final ResourceBundle bundle;
    private final List<ExtensionFactory> factories;
    private final Map<String, NamedWriter> writers;
    private final Map<String, Class<? extends StreamWriter>> streamwriters;
    private final Map<String, NamedParser> parsers;

    public AbderaConfiguration() {
        this(null);
    }

    protected AbderaConfiguration(ResourceBundle bundle) {
        this.bundle = (bundle != null) ? bundle : AbderaConfiguration.getBundle(Locale.getDefault());
        factories = loadExtensionFactories();
        writers = initNamedWriters();
        parsers = initNamedParsers();
        streamwriters = initStreamWriters();
    }

    private static synchronized List<ExtensionFactory> loadExtensionFactories() {
        List<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
        Iterable<ExtensionFactory> factories = Discover.locate("org.apache.abdera.factory.ExtensionFactory");
        for (ExtensionFactory factory : factories)
            list.add(factory);
        return list;
    }

    private ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Retrieve the value of the specified configuration option
     * 
     * @return The configuration option value or null
     */
    public String getConfigurationOption(String id) {
        String option = System.getProperty(id);
        if (option == null) {
            try {
                ResourceBundle bundle = getBundle();
                if (bundle != null)
                    option = bundle.getString(id);
            } catch (Exception e) {
                // Do Nothing
            }
        }
        return option;
    }

    /**
     * Retrieve the value of the specified configuration option or _default if the value is null
     * 
     * @return The configuration option value of _default
     */
    public String getConfigurationOption(String id, String _default) {
        String value = getConfigurationOption(id);
        return (value != null) ? value : _default;
    }

    /**
     * Registers an ExtensionFactory implementation.
     */
    public AbderaConfiguration addExtensionFactory(ExtensionFactory factory) {
        List<ExtensionFactory> factories = getExtensionFactories();
        if (!factories.contains(factory)) {
            factories.add(factory);
        } else {
            log.warn("These extensions are already registered: " + factory.getNamespaces());
        }
        return this;
    }

    /**
     * Returns the listing of registered ExtensionFactory implementations
     */
    public List<ExtensionFactory> getExtensionFactories() {
        return factories;
    }

    /**
     * Registers a NamedWriter implementation
     */
    public AbderaConfiguration addNamedWriter(NamedWriter writer) {
        Map<String, NamedWriter> writers = getNamedWriters();
        if (!writers.containsKey(writer.getName())) {
            writers.put(writer.getName(), writer);
        } else {
            log.warn("The NamedWriter is already registered: " + writer.getName());
        }
        return this;
    }

    /**
     * Registers NamedWriter implementations using the /META-INF/services/org.apache.abdera.writer.NamedWriter file
     */
    private Map<String, NamedWriter> initNamedWriters() {
        Map<String, NamedWriter> writers = null;
        Iterable<NamedWriter> _writers = Discover.locate(NAMED_WRITER);
        writers = Collections.synchronizedMap(new HashMap<String, NamedWriter>());
        for (NamedWriter writer : _writers) {
            writers.put(writer.getName().toLowerCase(), writer);
        }
        return writers;
    }

    /**
     * Registers StreamWriter implementations using the /META-INF/services/org.apache.abdera.writer.StreamWriter file
     */
    private Map<String, Class<? extends StreamWriter>> initStreamWriters() {
        Map<String, Class<? extends StreamWriter>> writers = null;
        Iterable<Class<? extends StreamWriter>> _writers = Discover.locate(STREAM_WRITER, true);
        writers = Collections.synchronizedMap(new HashMap<String, Class<? extends StreamWriter>>());
        for (Class<? extends StreamWriter> writer : _writers) {
            String name = getName(writer);
            if (name != null)
                writers.put(name.toLowerCase(), writer);
        }
        writers.put("fom", StreamBuilder.class);
        return writers;
    }

    private static String getName(Class<? extends StreamWriter> sw) {
        String name = null;
        try {
            Field field = sw.getField("NAME");
            if (Modifier.isStatic(field.getModifiers())) {
                name = (String)field.get(null);
            }
        } catch (Exception e) {
        }
        return name;
    }

    /**
     * Returns the collection of NamedWriters
     */
    public Map<String, NamedWriter> getNamedWriters() {
        return writers;
    }

    /**
     * Returns the collection of NamedWriters
     */
    public Map<String, Class<? extends StreamWriter>> getStreamWriters() {
        return streamwriters;
    }

    /**
     * Registers a NamedParser implementation
     */
    public AbderaConfiguration addNamedParser(NamedParser parser) {
        Map<String, NamedParser> parsers = getNamedParsers();
        if (!parsers.containsKey(parser.getName())) {
            parsers.put(parser.getName(), parser);
        } else {
            log.warn("The NamedParser is already registered: " + parser.getName());
        }
        return this;
    }

    /**
     * Registers a StreamWriter implementation
     */
    public AbderaConfiguration addStreamWriter(Class<? extends StreamWriter> sw) {
        Map<String, Class<? extends StreamWriter>> streamWriters = getStreamWriters();
        String swName = getName(sw);
        if (!streamWriters.containsKey(swName)) {
            streamWriters.put(swName, sw);
        } else {
            log.warn("The StreamWriter is already registered: " + swName);
        }
        return this;
    }

    /**
     * Registers NamedParser implementations using the /META-INF/services/org.apache.abdera.writer.NamedParser file
     */
    private Map<String, NamedParser> initNamedParsers() {
        Map<String, NamedParser> parsers = null;
        Iterable<NamedParser> _parsers = Discover.locate(NAMED_PARSER);
        parsers = Collections.synchronizedMap(new HashMap<String, NamedParser>());
        for (NamedParser parser : _parsers) {
            parsers.put(parser.getName().toLowerCase(), parser);
        }
        return parsers;
    }

    /**
     * Returns the collection of Named Parsers
     */
    public Map<String, NamedParser> getNamedParsers() {
        return parsers;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return a new instance of org.apache.abdera.factory.Factory
     * 
     * @return A new factory instance
     */
    public Factory newFactoryInstance(Abdera abdera) {
        return (Factory)Discover.locate(CONFIG_FACTORY, abdera.getConfiguration()
            .getConfigurationOption(CONFIG_FACTORY, DEFAULT_FACTORY), abdera);
    }

    /**
     * Return a new instance of org.apache.abdera.parser.Parser
     * 
     * @return A new parser instance
     */
    public Parser newParserInstance(Abdera abdera) {
        return (Parser)Discover.locate(CONFIG_PARSER,
                                       abdera.getConfiguration().getConfigurationOption(CONFIG_PARSER, DEFAULT_PARSER),
                                       abdera);
    }

    /**
     * Return a new instance of org.apache.abdera.xpath.XPath
     * 
     * @return A new XPath instance
     */
    public XPath newXPathInstance(Abdera abdera) {
        try {
            return (XPath)Discover
                .locate(CONFIG_XPATH,
                        abdera.getConfiguration().getConfigurationOption(CONFIG_XPATH, DEFAULT_XPATH),
                        abdera);
        } catch (Throwable n) {
            throw throwex("IMPLEMENTATION.NOT.AVAILABLE", "XPath", n);
        }
    }

    /**
     * Return a new instance of org.apache.abdera.parser.ParserFactory
     * 
     * @return A new ParserFactory instance
     */
    public ParserFactory newParserFactoryInstance(Abdera abdera) {
        try {
            return (ParserFactory)Discover.locate(CONFIG_PARSERFACTORY, abdera.getConfiguration()
                .getConfigurationOption(CONFIG_PARSERFACTORY, DEFAULT_PARSERFACTORY), abdera);
        } catch (Throwable n) {
            throw throwex("IMPLEMENTATION.NOT.AVAILABLE", "Parser", n);
        }
    }

    /**
     * Return a new instance of org.apache.abdera.writer.WriterFactory
     * 
     * @return A new WriterFactory instance
     */
    public WriterFactory newWriterFactoryInstance(Abdera abdera) {
        try {
            return (WriterFactory)Discover.locate(CONFIG_WRITERFACTORY, abdera.getConfiguration()
                .getConfigurationOption(CONFIG_WRITERFACTORY, DEFAULT_WRITERFACTORY), abdera);
        } catch (Throwable n) {
            throw throwex("IMPLEMENTATION.NOT.AVAILABLE", "WriterFactory", n);
        }
    }

    /**
     * Return a new instance of the default org.apache.abdera.writer.Writer
     * 
     * @return A new default writer implementation instance
     */
    public Writer newWriterInstance(Abdera abdera) {
        try {
            return (Writer)Discover.locate(CONFIG_WRITER, abdera.getConfiguration()
                .getConfigurationOption(CONFIG_WRITER, DEFAULT_WRITER), abdera);
        } catch (Throwable n) {
            throw throwex("IMPLEMENTATION.NOT.AVAILABLE", "Writer", n);
        }
    }

    /**
     * Return a new instance of the default org.apache.abdera.writer.Writer
     * 
     * @return A new default writer implementation instance
     */
    public StreamWriter newStreamWriterInstance(Abdera abdera) {
        try {
            return (StreamWriter)Discover.locate(CONFIG_STREAMWRITER, abdera.getConfiguration()
                .getConfigurationOption(CONFIG_STREAMWRITER, DEFAULT_STREAMWRITER), abdera);
        } catch (Throwable n) {
            throw throwex("IMPLEMENTATION.NOT.AVAILABLE", "StreamWriter", n);
        }
    }

    private RuntimeException throwex(String id, String arg, Throwable t) {
        return new RuntimeException(Localizer.sprintf(id, arg), t);
    }

}
