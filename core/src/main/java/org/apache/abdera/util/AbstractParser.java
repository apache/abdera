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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;

/**
 * Abstract base implementation of Abdera Parser.
 */
public abstract class AbstractParser implements Parser {

    protected Abdera abdera;
    protected ParserOptions options;

    protected AbstractParser() {
        this(new Abdera());
    }

    protected AbstractParser(Abdera abdera) {
        this.abdera = abdera;
    }

    public Abdera getAbdera() {
        return abdera;
    }

    public void setAbdera(Abdera abdera) {
        this.abdera = abdera;
    }

    public Factory getFactory() {
        return getAbdera().getFactory();
    }

    public <T extends Element> Document<T> parse(InputStream in) throws ParseException {
        return parse(in, null, getDefaultParserOptions());
    }

    public <T extends Element> Document<T> parse(XMLStreamReader reader) throws ParseException {
        return parse(reader, null, getDefaultParserOptions());
    }

    public <T extends Element> Document<T> parse(InputStream in, String base) throws ParseException {
        return parse(in, base, getDefaultParserOptions());
    }

    public <T extends Element> Document<T> parse(InputStream in, ParserOptions options) throws ParseException {
        return parse(in, null, options);
    }

    public <T extends Element> Document<T> parse(InputStream in, String base, ParserOptions options)
        throws ParseException {
        return parse(new InputStreamReader(in), base, options);
    }

    public <T extends Element> Document<T> parse(Reader in) throws ParseException {
        return parse(in, null, getDefaultParserOptions());
    }

    public <T extends Element> Document<T> parse(Reader in, String base) throws ParseException {
        return parse(in, base, getDefaultParserOptions());
    }

    public <T extends Element> Document<T> parse(Reader in, ParserOptions options) throws ParseException {
        return parse(in, null, options);
    }

    public <T extends Element> Document<T> parse(ReadableByteChannel buf, ParserOptions options) throws ParseException {
        return parse(buf, null, options);
    }

    public <T extends Element> Document<T> parse(ReadableByteChannel buf, String base, ParserOptions options)
        throws ParseException {
        String charset = options.getCharset();
        return parse(Channels.newReader(buf, charset != null ? charset : "utf-8"), base, options);
    }

    public <T extends Element> Document<T> parse(ReadableByteChannel buf, String base) throws ParseException {
        return parse(buf, base, getDefaultParserOptions());
    }

    public <T extends Element> Document<T> parse(ReadableByteChannel buf) throws ParseException {
        return parse(buf, null, getDefaultParserOptions());
    }

    public synchronized ParserOptions getDefaultParserOptions() {
        if (options == null)
            options = initDefaultParserOptions();

        // Make a copy of the options, so that changes to it don't result in
        // changes to the Parser's defaults. Also, this allows us to remain
        // thread safe without having to make ParseOptions implementations
        // synchronized.

        try {
            return (ParserOptions)options.clone();
        } catch (CloneNotSupportedException cnse) {
            // This shouldn't actually happen
            throw new RuntimeException(cnse);
        }
    }

    protected abstract ParserOptions initDefaultParserOptions();

    public synchronized Parser setDefaultParserOptions(ParserOptions options) {
        // Ok, we need to make a defensive copy of the options, since otherwise
        // the caller still has access to the object, which means our access to
        // it isn't certain to be thread safe.

        try {
            this.options = (options != null) ? (ParserOptions)options.clone() : initDefaultParserOptions();
            return this;
        } catch (CloneNotSupportedException cnse) {
            // This shouldn't actually happen
            throw new RuntimeException(cnse);
        }
    }

}
