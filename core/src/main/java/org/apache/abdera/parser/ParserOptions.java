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
package org.apache.abdera.parser;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.i18n.text.io.CompressionUtil.CompressionCodec;

/**
 * Parser options are used to modify the behavior of the parser.
 */
public interface ParserOptions extends Cloneable {

    Object clone() throws CloneNotSupportedException;

    /**
     * Returns the factory the parser should use
     */
    Factory getFactory();

    /**
     * Sets the factory the parser should use
     */
    ParserOptions setFactory(Factory factory);

    /**
     * Returns the default character set to use for the parsed document
     */
    String getCharset();

    /**
     * Sets the character set to use for the parsed document
     */
    ParserOptions setCharset(String charset);

    /**
     * Returns the Parse Filter. The parse filter is a set of XML QNames that the parse should watch out for. If the
     * filter is null, the parser will parse all elements in the document. I the filter is not null, the parser will
     * only pay attention to elements whose QName's appear in the filter list.
     */
    ParseFilter getParseFilter();

    /**
     * Sets the Parse Filter. The parse filter is a set of XML QNames that the parse should watch out for. If the filter
     * is null, the parser will parse all elements in the document. I the filter is not null, the parser will only pay
     * attention to elements whose QName's appear in the filter list.
     */
    ParserOptions setParseFilter(ParseFilter parseFilter);

    /**
     * Returns true if the parser should attempt to automatically detect the character encoding from the stream
     */
    boolean getAutodetectCharset();

    /**
     * If true, the parser will attempt to automatically detect the character encoding from the stream by checking for
     * the byte order mark or checking the XML prolog.
     */
    ParserOptions setAutodetectCharset(boolean detect);

    /**
     * If false, the parser will trim leading and trailing whitespace in element and attribute values unless there is an
     * in-scope xml:space="preserve".
     */
    boolean getMustPreserveWhitespace();

    /**
     * If false, the parser will trim leading and trailing whitespace in element and attribute values unless there is an
     * in-scope xml:space="preserve".
     */
    ParserOptions setMustPreserveWhitespace(boolean preserve);

    /**
     * If true, the parser will attempt to silently filter out invalid XML characters appearing within the XML document.
     */
    boolean getFilterRestrictedCharacters();

    /**
     * If true, the parser will attempt to silently filter out invalid XML characters appearing within the XML document
     */
    ParserOptions setFilterRestrictedCharacters(boolean filter);

    /**
     * If getFilterRestrictedCharacters is true, restricted characters will be replaced with the specified character
     */
    char getFilterRestrictedCharacterReplacement();

    /**
     * If getFilterRestrictedCharacters is true, restricted characters will be replaced with the specified character
     */
    ParserOptions setFilterRestrictedCharacterReplacement(char replacement);

    /**
     * When parsing an InputStream that contains compressed data, use these codecs to decompress the stream. Only used
     * when parsing an InputStream. Ignored when parsing a Reader
     */
    CompressionCodec[] getCompressionCodecs();

    /**
     * When parsing an InputStream that contains compressed data, use these codecs to decompress the stream. Only used
     * when parsing an InputStream. Ignored when parsing a Reader
     */
    ParserOptions setCompressionCodecs(CompressionCodec... codecs);

    /**
     * Register a named entity. This provides an escape clause for when feeds use entities that are not supported in XML
     * without a DTD decl. By default, all of the (X)HTML entities are preregistered
     */
    ParserOptions registerEntity(String name, String value);

    /**
     * Resolves a value for a named entity. This provides an escape clause for when feeds use entities that are not
     * supported in XML without a DTD decl. By default, all of the (X)HTML entities are preregistered
     */
    String resolveEntity(String name);

    /**
     * True if undeclared named entities should be resolved.
     */
    ParserOptions setResolveEntities(boolean resolve);

    /**
     * True if undeclared named entities should be resolved.
     */
    boolean getResolveEntities();

    /**
     * True if QName-Alias mapping is enabled
     */
    ParserOptions setQNameAliasMappingEnabled(boolean enabled);

    /**
     * True if QName-Alias mapping is enabled (default is false)
     */
    boolean isQNameAliasMappingEnabled();

    /**
     * Get the QName-Alias Mapping (default null)
     */
    Map<QName, QName> getQNameAliasMap();

    /**
     * Set the QName-Alias Mapping
     */
    ParserOptions setQNameAliasMap(Map<QName, QName> map);

}
