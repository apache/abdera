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
package org.apache.abdera.parser.stax;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserFactory;
import org.apache.abdera.util.AbstractParser;

@SuppressWarnings("unchecked")
public class FOMParserFactory implements ParserFactory {

    private final Abdera abdera;
    private final Map<String, NamedParser> parsers;

    public FOMParserFactory() {
        this(new Abdera());
    }

    public FOMParserFactory(Abdera abdera) {
        this.abdera = abdera;
        Map<String, NamedParser> p = getAbdera().getConfiguration().getNamedParsers();
        this.parsers = (p != null) ? p : new HashMap<String, NamedParser>();
    }

    protected Abdera getAbdera() {
        return abdera;
    }

    public <T extends Parser> T getParser() {
        return (T)getAbdera().getParser();
    }

    public <T extends Parser> T getParser(String name) {
        Parser parser = (T)((name != null) ? getParsers().get(name.toLowerCase()) : getParser());
        if (parser instanceof AbstractParser) {
            ((AbstractParser)parser).setAbdera(abdera);
        }
        return (T)parser;
    }

    private Map<String, NamedParser> getParsers() {
        return parsers;
    }

}
