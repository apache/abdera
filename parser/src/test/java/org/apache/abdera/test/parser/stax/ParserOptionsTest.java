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
package org.apache.abdera.test.parser.stax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.util.filter.AbstractParseFilter;
import org.junit.Test;

public class ParserOptionsTest {
    private static final Abdera abdera = new Abdera();
    
    @Test
    public void testIgnoreComments() {
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        ParseFilter filter = new SimpleParseFilter();
        filter.setIgnoreComments(true);
        options.setParseFilter(filter);
        Document<Feed> doc = parser.parse(ParserOptionsTest.class.getResourceAsStream(
                "/parseroptionstest.xml"), options);
        assertTrue(abdera.getXPath().selectNodes("//comment()", doc).isEmpty());
    }
    
    @Test
    public void testIgnoreProcessingInstructions() {
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        ParseFilter filter = new SimpleParseFilter();
        filter.setIgnoreProcessingInstructions(true);
        options.setParseFilter(filter);
        Document<Feed> doc = parser.parse(ParserOptionsTest.class.getResourceAsStream(
                "/parseroptionstest.xml"), options);
        assertTrue(abdera.getXPath().selectNodes("//processing-instruction()", doc).isEmpty());
    }
    
    @Test
    public void testIgnoreWhitespace() {
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        ParseFilter filter = new SimpleParseFilter();
        filter.setIgnoreWhitespace(true);
        options.setParseFilter(filter);
        Document<Feed> doc = parser.parse(ParserOptionsTest.class.getResourceAsStream(
                "/parseroptionstest.xml"), options);
        assertEquals("", doc.getRoot().getEntries().get(0).getSummary());
    }
    
    @Test
    public void testAttributeFiltering() {
        final QName filteredAttribute = new QName("urn:test", "attr");
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        options.setParseFilter(new AbstractParseFilter() {
            public boolean acceptable(QName qname) {
                return true;
            }

            public boolean acceptable(QName qname, QName attribute) {
                return !filteredAttribute.equals(attribute);
            }
        });
        Document<Feed> doc = parser.parse(ParserOptionsTest.class.getResourceAsStream(
                "/parseroptionstest.xml"), options);
        Entry entry = doc.getRoot().getEntries().get(0);
        assertNull(entry.getAttributeValue(filteredAttribute));
    }
    
    @Test
    public void testQNameAliasMapping() {
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        Map<QName,QName> qnameAliases = new HashMap<QName,QName>();
        qnameAliases.put(new QName("urn:test", "mylink"), new QName("http://www.w3.org/2005/Atom", "link"));
        options.setQNameAliasMap(qnameAliases);
        options.setQNameAliasMappingEnabled(true);
        Document<Feed> doc = parser.parse(ParserOptionsTest.class.getResourceAsStream(
                "/parseroptionstest.xml"), options);
        assertFalse(doc.getRoot().getEntries().get(0).getLinks().isEmpty());
    }
}
