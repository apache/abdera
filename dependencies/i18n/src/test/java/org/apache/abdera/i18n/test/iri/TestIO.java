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
package org.apache.abdera.i18n.test.iri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import org.apache.abdera.i18n.text.Filter;
import org.apache.abdera.i18n.text.io.CharsetSniffingInputStream;
import org.apache.abdera.i18n.text.io.DynamicPushbackInputStream;
import org.apache.abdera.i18n.text.io.FilteredCharReader;
import org.apache.abdera.i18n.text.io.PeekAheadInputStream;
import org.apache.abdera.i18n.text.io.PipeChannel;
import org.apache.abdera.i18n.text.io.RewindableInputStream;
import org.junit.Test;

public class TestIO extends TestBase {

    @Test
    public void testCharsetDetection() throws Exception {

        byte[] utf32be = {0x00, 0x00, 0xFFFFFFFE, 0xFFFFFFFF, 0x01, 0x02, 0x03};
        byte[] utf32le = {0xFFFFFFFF, 0xFFFFFFFE, 0x00, 0x00, 0x01, 0x02, 0x03};
        byte[] utf16be = {0xFFFFFFFE, 0xFFFFFFFF, 0x01, 0x02, 0x03};
        byte[] utf16le = {0xFFFFFFFF, 0xFFFFFFFE, 0x01, 0x02, 0x03};
        byte[] utf8 = {0xFFFFFFEF, 0xFFFFFFBB, 0xFFFFFFBF, 0x01, 0x02, 0x03};

        byte[] nobom_utf32be = {0x00, 0x00, 0x00, 0x3C, 0x01, 0x02, 0x03};
        byte[] nobom_utf32le = {0x3C, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03};
        byte[] nobom_utf16be = {0x00, 0x3C, 0x00, 0x3F, 0x01, 0x02, 0x03};
        byte[] nobom_utf16le = {0x3C, 0x00, 0x3F, 0x00, 0x01, 0x02, 0x03};

        ByteArrayInputStream in = new ByteArrayInputStream(utf32be);
        CharsetSniffingInputStream csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-32", csis.getEncoding());
        assertTrue(csis.isBomSet());

        in = new ByteArrayInputStream(utf32le);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-32", csis.getEncoding());
        assertTrue(csis.isBomSet());

        in = new ByteArrayInputStream(utf16be);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-16", csis.getEncoding());
        assertTrue(csis.isBomSet());

        in = new ByteArrayInputStream(utf16le);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-16", csis.getEncoding());
        assertTrue(csis.isBomSet());

        in = new ByteArrayInputStream(utf8);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-8", csis.getEncoding());
        assertTrue(csis.isBomSet());

        in = new ByteArrayInputStream(nobom_utf32be);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-32be", csis.getEncoding());
        assertFalse(csis.isBomSet());

        in = new ByteArrayInputStream(nobom_utf32le);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-32le", csis.getEncoding());
        assertFalse(csis.isBomSet());

        in = new ByteArrayInputStream(nobom_utf16be);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-16be", csis.getEncoding());
        assertFalse(csis.isBomSet());

        in = new ByteArrayInputStream(nobom_utf16le);
        csis = new CharsetSniffingInputStream(in);
        assertEquals("UTF-16le", csis.getEncoding());
        assertFalse(csis.isBomSet());
    }

    @Test
    public void testDynamicPushbackInputStream() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {0x01, 0x02, 0x03, 0x04});
        DynamicPushbackInputStream dpis = new DynamicPushbackInputStream(in);
        int r = dpis.read();
        dpis.unread(r);
        int e = dpis.read();
        assertEquals(r, e);
        dpis.unread(e);
        dpis.clear();
        r = dpis.read();
        assertFalse(e == r);
    }

    @Test
    public void testFilteredCharReader() throws Exception {
        String string = "abcdefg";
        FilteredCharReader fcr = new FilteredCharReader(new StringReader(string), new Filter() {
            public boolean accept(int c) {
                return c != 'c' && c != 'd' && c != 'e';
            }
        });
        char[] buf = new char[7];
        int r = fcr.read(buf);
        assertEquals(4, r);
        assertEquals("abfg", new String(buf, 0, r));
    }

    @Test
    public void testPeekAheadInputStream() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {0x01, 0x02, 0x03, 0x04});
        PeekAheadInputStream pais = new PeekAheadInputStream(in);
        byte[] peek = new byte[2];
        byte[] read = new byte[2];
        pais.peek(peek);
        pais.read(read);
        assertEquals(read[0], peek[0]);
        assertEquals(read[1], peek[1]);
        byte[] newread = new byte[2];
        assertFalse(read[0] == newread[0]);
        assertFalse(read[1] == newread[1]);
    }

    @Test
    public void testRewindableInputStream() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {0x01, 0x02, 0x03, 0x04});
        RewindableInputStream ris = new RewindableInputStream(in);
        byte[] buf1 = new byte[4];
        byte[] buf2 = new byte[4];
        ris.read(buf1);
        ris.rewind();
        ris.read(buf2);
        for (int n = 0; n < 4; n++)
            assertEquals(buf2[n], buf1[n]);
    }

    @Test
    public void testPipeChannel() throws Exception {
        PipeChannel pc = new PipeChannel();
        assertTrue(pc.isWritable());
        assertFalse(pc.isReadable());
        OutputStream out = pc.getOutputStream();
        out.write(0x01);
        out.write(0x02);
        out.write(0x03);
        out.write(0x04);
        out.close();
        assertFalse(pc.isWritable());
        assertTrue(pc.isReadable());
        InputStream in = pc.getInputStream();
        byte[] buf = new byte[4];
        in.read(buf);
        assertEquals(0x1, buf[0]);
        assertEquals(0x2, buf[1]);
        assertEquals(0x3, buf[2]);
        assertEquals(0x4, buf[3]);
        in.close();
        assertTrue(pc.isWritable());
        assertFalse(pc.isReadable());
    }
}
