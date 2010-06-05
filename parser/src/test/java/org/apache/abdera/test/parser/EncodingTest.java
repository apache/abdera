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
package org.apache.abdera.test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.i18n.text.io.CompressionUtil.CompressionCodec;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.ParserOptions;
import org.apache.abdera.writer.WriterOptions;
import org.junit.Test;

public class EncodingTest {

    @Test
    public void testContentEncoding() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId("http://example.com/entry/1");
        entry.setTitle("Whatever");
        entry.setUpdated(new Date());
        Content content = entry.getFactory().newContent(Content.Type.XML);
        String s = "<x>" + new Character((char)224) + "</x>";
        content.setValue(s);
        content.setMimeType("application/xml+whatever");
        entry.setContentElement(content);
        assertNotNull(entry.getContent());
        assertEquals(s, entry.getContent());
    }

    /**
     * Passes if the test does not throw a parse exception
     */
    @Test
    public void testCompressionCodec() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream cout = CompressionUtil.getEncodedOutputStream(out, CompressionCodec.GZIP);
        entry.writeTo(cout);
        cout.close();
        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        options.setCompressionCodecs(CompressionCodec.GZIP);
        Document<Entry> doc = abdera.getParser().parse(in, null, options);
        entry = doc.getRoot();
    }

    /**
     * Passes if the test does not throw a parse exception
     */
    @Test
    public void testXMLRestrictedChar() throws Exception {
        String s = "<?xml version='1.1'?><t t='\u007f' />";
        Abdera abdera = new Abdera();
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        options.setFilterRestrictedCharacters(true);
        Document<Element> doc = parser.parse(new StringReader(s), null, options);
        doc.getRoot().toString();
    }

    /**
     * Passes if the test does not throw a parse exception
     */
    @Test
    public void testXMLRestrictedChar2() throws Exception {
        String s = "<?xml version='1.0'?><t t='\u0002' />";
        Abdera abdera = new Abdera();
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        options.setFilterRestrictedCharacters(true);
        options.setCharset("UTF-8");
        Document<Element> doc = parser.parse(new ByteArrayInputStream(s.getBytes("UTF-8")), null, options);
        doc.getRoot().toString();
    }

    /**
     * Passes if the test does not throw any exceptions
     */
    @Test
    public void testWriterOptions() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setTitle("1");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WriterOptions writeoptions = entry.getDefaultWriterOptions();
        writeoptions.setCompressionCodecs(CompressionCodec.DEFLATE);
        writeoptions.setCharset("UTF-16");
        writeoptions.setAutoClose(true);
        entry.getDocument().writeTo(out, writeoptions);
        out.close();

        byte[] bytes = out.toByteArray();

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Parser parser = abdera.getParser();
        ParserOptions options = parser.getDefaultParserOptions();
        options.setCompressionCodecs(CompressionCodec.DEFLATE);
        Document<Entry> doc = abdera.getParser().parse(in, null, options);

        doc.getRoot().toString();
    }
}
