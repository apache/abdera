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

import org.apache.abdera.util.NamedItem;

/**
 * Abdera's abstract parsing model allows developers to implement parsers capable of translating non-Atom formats into
 * Abdera objects. For instance, a developer could create an RDF, RSS, JSON or hAtom microformat parser that
 * automatically converted to Atom. Alternative parsers are made available via the ParserFactory interface.
 * 
 * <pre>
 *   Parser parser = abdera.getParserFactory().getParser("json");
 *   Document&lt;Feed> doc = parser.parse(...);
 *   
 *   Parser parser = abdera.getParserFactory().getParser("hatom");
 *   Document&lt;Feed> doc = parser.parse(...);
 * </pre>
 */
public interface NamedParser extends Parser, NamedItem {

    /**
     * Returns a listing of media type of the format consumed by this parser
     * 
     * @return An array of MIME Media Types
     */
    String[] getInputFormats();

    /**
     * Returns true if this parser is capable of consuming the specified media type
     * 
     * @param mediatype The MIME media type to check
     * @return True if the media type is supported
     */
    boolean parsesFormat(String mediatype);
}
