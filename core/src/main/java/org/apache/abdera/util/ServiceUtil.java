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

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.ExtensionFactory;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;
import org.apache.abdera.xpath.XPath;

/**
 * Core utility methods that support Abdera's internal operation
 * 
 * @deprecated
 */
public final class ServiceUtil implements Constants {

    ServiceUtil() {
    }

    /**
     * Utility method for returning an instance of the default Abdera XPath instance
     * 
     * @deprecated
     */
    public static XPath newXPathInstance(Abdera abdera) {
        return (XPath)Discover.locate(CONFIG_XPATH,
                                      abdera.getConfiguration().getConfigurationOption(CONFIG_XPATH, DEFAULT_XPATH),
                                      abdera);
    }

    /**
     * Utility method for returning an instance of the default Abdera Parser instance
     * 
     * @deprecated
     */
    public static Parser newParserInstance(Abdera abdera) {
        return (Parser)Discover.locate(CONFIG_PARSER,
                                       abdera.getConfiguration().getConfigurationOption(CONFIG_PARSER, DEFAULT_PARSER),
                                       abdera);
    }

    /**
     * Utility method for returning an instance of the defaul Abdera Factory instance
     * 
     * @deprecated
     */
    public static Factory newFactoryInstance(Abdera abdera) {
        return (Factory)Discover.locate(CONFIG_FACTORY, abdera.getConfiguration()
            .getConfigurationOption(CONFIG_FACTORY, DEFAULT_FACTORY), abdera);
    }

    /**
     * @deprecated
     */
    public static ParserFactory newParserFactoryInstance(Abdera abdera) {
        return (ParserFactory)Discover.locate(CONFIG_PARSERFACTORY, abdera.getConfiguration()
            .getConfigurationOption(CONFIG_PARSERFACTORY, DEFAULT_PARSERFACTORY), abdera);
    }

    /**
     * @deprecated
     */
    public static WriterFactory newWriterFactoryInstance(Abdera abdera) {
        return (WriterFactory)Discover.locate(CONFIG_WRITERFACTORY, abdera.getConfiguration()
            .getConfigurationOption(CONFIG_WRITERFACTORY, DEFAULT_WRITERFACTORY), abdera);
    }

    /**
     * @deprecated
     */
    public static Writer newWriterInstance(Abdera abdera) {
        return (Writer)Discover.locate(CONFIG_WRITER,
                                       abdera.getConfiguration().getConfigurationOption(CONFIG_WRITER, DEFAULT_WRITER),
                                       abdera);
    }

    /**
     * @deprecated
     */
    public static StreamWriter newStreamWriterInstance(Abdera abdera) {
        return (StreamWriter)Discover.locate(CONFIG_STREAMWRITER, abdera.getConfiguration()
            .getConfigurationOption(CONFIG_STREAMWRITER, DEFAULT_STREAMWRITER), abdera);
    }

    /**
     * @deprecated
     */
    protected static synchronized List<ExtensionFactory> loadExtensionFactories() {
        List<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
        Iterable<ExtensionFactory> factories = Discover.locate("org.apache.abdera.factory.ExtensionFactory");
        for (ExtensionFactory factory : factories)
            list.add(factory);
        return list;
    }

    /**
     * @deprecated
     */
    public static synchronized <T> Iterable<T> loadimpls(String sid) {
        return Discover.locate(sid);
    }

    /**
     * @deprecated
     */
    public static synchronized <T> Iterable<T> loadimpls(String sid, boolean classesonly) {
        return Discover.locate(sid, classesonly);
    }

    /**
     * Returns a new instance of the identified object class. This will use the Abdera configuration mechanism to look
     * up the implementation class for the specified id. Several places will be checked: the abdera.properties file, the
     * /META-INF/services directory, and the System properties. If no instance is configured, the default class name
     * will be used. Returns null if no instance can be created.
     * 
     * @deprecated
     */
    public static Object newInstance(String id, String _default, Abdera abdera) {
        return Discover.locate(id, _default, abdera);
    }

    /**
     * Returns a new instance of the identified object class. This will use the Abdera configuration mechanism to look
     * up the implementation class for the specified id. Several places will be checked: the abdera.properties file, the
     * /META-INF/services directory, and the System properties. If no instance is configured, the default class name
     * will be used. Returns null if no instance can be created.
     * 
     * @deprecated
     */
    public static Object newInstance(String id, String _default, Object... args) {
        return Discover.locate(id, _default, args);
    }

}
