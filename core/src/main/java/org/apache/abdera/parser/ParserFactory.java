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

/**
 * The ParserFactory is used a acquire instances of alternative parsers registered with Abdera.
 * 
 * @see org.apache.abdera.parser.NamedParser
 */
public interface ParserFactory {

    /**
     * Get the default parser. This is equivalent to calling Abdera.getParser()
     * 
     * @return The default parser implementation
     */
    <T extends Parser> T getParser();

    /**
     * Get the named parser
     * 
     * @param name The name of the parser instance to retrieve
     * @return The Named parser instance
     */
    <T extends Parser> T getParser(String name);

}
