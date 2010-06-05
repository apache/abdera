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
package org.apache.abdera.test.core;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.junit.Test;

public class CoreTest {

    @Test
    public void testUriNormalization() {
        assertEquals("http://www.example.org/Bar/%3F/foo/", IRI
            .normalizeString("HTTP://www.EXAMPLE.org:80/foo/../Bar/%3f/./foo/."));
        assertEquals("https://www.example.org/Bar/%3F/foo/", IRI
            .normalizeString("HTTPs://www.EXAMPLE.org:443/foo/../Bar/%3f/./foo/."));
        assertEquals("http://www.example.org:81/Bar/%3F/foo/", IRI
            .normalizeString("HTTP://www.EXAMPLE.org:81/foo/../Bar/%3f/./foo/."));
        assertEquals("https://www.example.org:444/Bar/%3F/foo/", IRI
            .normalizeString("HTTPs://www.EXAMPLE.org:444/foo/../Bar/%3f/./foo/."));
    }

    @Test
    public void testCompressionUtil() throws Exception {
        String s = "abcdefg";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream cout = CompressionUtil.getEncodedOutputStream(out, CompressionUtil.CompressionCodec.GZIP);
        cout.write(s.getBytes("UTF-8"));
        cout.flush();
        cout.close();
        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        InputStream cin = CompressionUtil.getDecodingInputStream(in, CompressionUtil.CompressionCodec.GZIP);
        byte[] buffer = new byte[20];
        int r = cin.read(buffer);
        String t = new String(buffer, 0, r, "UTF-8");
        assertEquals(s, t);
    }

}
