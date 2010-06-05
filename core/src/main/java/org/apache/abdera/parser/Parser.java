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

import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;

import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

public interface Parser {

    /**
     * Parse the input stream using the default character set encoding (UTF-8)
     * 
     * @param in The input stream to parse
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(InputStream in) throws ParseException;

    /**
     * Parse the input stream using the default character set encoding (UTF-8)
     * 
     * @param reader The XMLStreamReader to use to parse
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(XMLStreamReader reader) throws ParseException;

    /**
     * Parse the input stream using the default character set encoding (UTF-8). The specified Base URI is used to
     * resolve relative references contained in the document
     * 
     * @param in The input stream to parse
     * @param base The Base URI of the document
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(InputStream in, String base) throws ParseException;

    /**
     * Parse the input stream using the default character set encoding (UTF-8). The specified Base URI is used to
     * resolve relative references contained in the document
     * 
     * @param in The input stream to parse
     * @param options The Parse options
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(InputStream in, ParserOptions options) throws ParseException;

    /**
     * Parse the input stream using using the specified Parse options. The parse options can be used to control various
     * aspects of the parsing process such as the character set encoding to use and whether certain elements should be
     * ignored. The specified Base URI is used to resolve relative references contained in the document.
     * 
     * @param in The input stream to parse
     * @param base The Base URI of the document
     * @param options The Parse Options
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(InputStream in, String base, ParserOptions options) throws ParseException;

    /**
     * Parse the reader using the default Base URI and options
     * 
     * @param in The Reader to parse
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(Reader in) throws ParseException;

    /**
     * Parse the reader using the specified Base URI
     * 
     * @param in The Reader to parse
     * @param base The Base URI
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(Reader in, String base) throws ParseException;

    /**
     * Parse the reader using the specified Base URI
     * 
     * @param in The Reader to parse
     * @param options The Parse Options
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(Reader in, ParserOptions options) throws ParseException;

    /**
     * Parse the reader using using the specified Parse options. The parse options can be used to control various
     * aspects of the parsing process such as the character set encoding to use and whether certain elements should be
     * ignored. The specified Base URI is used to resolve relative references contained in the document.
     * 
     * @param in The reader to parse
     * @param base The Base URI of the document
     * @param options The Parse Options
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(Reader in, String base, ParserOptions options) throws ParseException;

    /**
     * Parse the channel using using the specified Parse options. The parse options can be used to control various
     * aspects of the parsing process such as the character set encoding to use and whether certain elements should be
     * ignored. The specified Base URI is used to resolve relative references contained in the document.
     * 
     * @param in The ReadableByteChannel to parse
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(ReadableByteChannel buf) throws ParseException;

    /**
     * Parse the channel using using the specified Parse options. The parse options can be used to control various
     * aspects of the parsing process such as the character set encoding to use and whether certain elements should be
     * ignored. The specified Base URI is used to resolve relative references contained in the document.
     * 
     * @param in The ReadableByteChannel to parse
     * @param base The Base URI of the document
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(ReadableByteChannel buf, String base) throws ParseException;

    /**
     * Parse the channel using using the specified Parse options. The parse options can be used to control various
     * aspects of the parsing process such as the character set encoding to use and whether certain elements should be
     * ignored. The specified Base URI is used to resolve relative references contained in the document.
     * 
     * @param in The ReadableByteChannel to parse
     * @param base The Base URI of the document
     * @param options The Parse Options
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(ReadableByteChannel buf, String base, ParserOptions options)
        throws ParseException;

    /**
     * Parse the channel using using the specified Parse options. The parse options can be used to control various
     * aspects of the parsing process such as the character set encoding to use and whether certain elements should be
     * ignored. The specified Base URI is used to resolve relative references contained in the document.
     * 
     * @param reader the XMLStreamReader parser to use to parse
     * @param base The Base URI of the document
     * @param options The Parse Options
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(XMLStreamReader reader, String base, ParserOptions options)
        throws ParseException;

    /**
     * Parse the channel using using the specified Parse options. The parse options can be used to control various
     * aspects of the parsing process such as the character set encoding to use and whether certain elements should be
     * ignored. The specified Base URI is used to resolve relative references contained in the document.
     * 
     * @param in The ReadableByteChannel to parse
     * @param options The Parse Options
     * @return The parsed Abdera Document
     * @throws ParseException if the parse failed
     */
    <T extends Element> Document<T> parse(ReadableByteChannel buf, ParserOptions options) throws ParseException;

    /**
     * Return the default parser options for this Parser. This method returns a copy of the default options. Changes to
     * this instance will not affect the defaults returned by subsequent requests.
     * 
     * @return The default ParserOptions
     */
    ParserOptions getDefaultParserOptions();

    /**
     * Set the default parser options for this Parser. This method copies the specified options.
     * 
     * @param options The Parser Options to use as the default
     */
    Parser setDefaultParserOptions(ParserOptions options);

}
