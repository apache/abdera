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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Codepoint;
import org.apache.abdera.i18n.text.CodepointIterator;
import org.apache.abdera.i18n.text.Normalizer;
import org.apache.abdera.i18n.text.Sanitizer;
import org.junit.Test;

public class TestText extends TestBase {

    @Test
    public void testCodepoints() throws Exception {
        StringBuilder buf = new StringBuilder();
        CharUtils.append(buf, 0x10000);
        assertEquals(2, buf.length());
        assertEquals(CharUtils.getHighSurrogate(0x10000), buf.charAt(0));
        assertEquals(CharUtils.getLowSurrogate(0x10000), buf.charAt(1));
        assertTrue(CharUtils.isSurrogatePair(buf.charAt(0), buf.charAt(1)));
        Codepoint codepoint = CharUtils.codepointAt(buf, 0);
        assertEquals(0x10000, codepoint.getValue());
        assertEquals(2, codepoint.getCharCount());
        assertTrue(codepoint.isSupplementary());
        CharUtils.insert(buf, 0, codepoint.next());
        assertEquals(4, buf.length());
        assertEquals(CharUtils.codepointAt(buf, 0), codepoint.next());
    }

    @Test
    public void testSetChecks() throws Exception {
        assertTrue(CharUtils.inRange(new char[] {'a', 'b', 'c'}, 'a', 'c'));
        assertFalse(CharUtils.inRange(new char[] {'a', 'b', 'c'}, 'm', 'z'));

        assertTrue(CharUtils.inRange(new char[] {'a', 'b', 'c'}, (int)'a', (int)'c'));
        assertFalse(CharUtils.inRange(new char[] {'a', 'b', 'c'}, (int)'m', (int)'z'));

        assertTrue(CharUtils.isBidi(CharUtils.LRE));
        assertTrue(CharUtils.isBidi(CharUtils.LRM));
        assertTrue(CharUtils.isBidi(CharUtils.LRO));
        assertTrue(CharUtils.isBidi(CharUtils.PDF));
        assertTrue(CharUtils.isBidi(CharUtils.RLE));
        assertTrue(CharUtils.isBidi(CharUtils.RLM));
        assertTrue(CharUtils.isBidi(CharUtils.RLO));
    }

    @Test
    public void testBidi() throws Exception {
        String s = "abc";
        String lre = CharUtils.wrapBidi(s, CharUtils.LRE);
        String lro = CharUtils.wrapBidi(s, CharUtils.LRO);
        String lrm = CharUtils.wrapBidi(s, CharUtils.LRM);
        String rle = CharUtils.wrapBidi(s, CharUtils.RLE);
        String rlo = CharUtils.wrapBidi(s, CharUtils.RLO);
        String rlm = CharUtils.wrapBidi(s, CharUtils.RLM);

        assertEquals(CharUtils.LRE, lre.charAt(0));
        assertEquals(CharUtils.PDF, lre.charAt(lre.length() - 1));

        assertEquals(CharUtils.LRO, lro.charAt(0));
        assertEquals(CharUtils.PDF, lro.charAt(lro.length() - 1));

        assertEquals(CharUtils.LRM, lrm.charAt(0));
        assertEquals(CharUtils.LRM, lrm.charAt(lrm.length() - 1));

        assertEquals(CharUtils.RLE, rle.charAt(0));
        assertEquals(CharUtils.PDF, rle.charAt(rle.length() - 1));

        assertEquals(CharUtils.RLO, rlo.charAt(0));
        assertEquals(CharUtils.PDF, rlo.charAt(rlo.length() - 1));

        assertEquals(CharUtils.RLM, rlm.charAt(0));
        assertEquals(CharUtils.RLM, rlm.charAt(rlm.length() - 1));

        assertEquals(s, CharUtils.stripBidi(lre));
        assertEquals(s, CharUtils.stripBidi(lro));
        assertEquals(s, CharUtils.stripBidi(lrm));
        assertEquals(s, CharUtils.stripBidi(rle));
        assertEquals(s, CharUtils.stripBidi(rlo));
        assertEquals(s, CharUtils.stripBidi(rlm));

        s = new String(new char[] {'a', CharUtils.LRM, 'b', CharUtils.LRM, 'c'});
        assertEquals(5, s.length());
        s = CharUtils.stripBidiInternal(s);
        assertEquals(3, s.length());
        assertEquals("abc", s);
    }

    @Test
    public void testVerify() throws Exception {
        CharUtils.verify(new char[] {'a', 'b', 'c', 'd'}, CharUtils.Profile.ALPHA);
        CharUtils.verifyNot(new char[] {'1', '2', '3', '4', '-'}, CharUtils.Profile.ALPHA);
    }

    @Test
    public void testCodepointIterator() throws Exception {
        String s = "abcdefghijklmnop";
        CodepointIterator ci = CodepointIterator.forCharSequence(s);
        while (ci.hasNext())
            ci.next();
    }

    @Test
    public void testSanitizer() throws Exception {
        String s = "\u0074\u0068\u00ed\u0073\u0020\u00ed\u0073\u0020\u00e0\u0020\u0074\u00e9\u0073\u0074";
        String t = Sanitizer.sanitize(s);
        assertEquals("th%C3%ADs_%C3%ADs_%C3%A0_t%C3%A9st", t);
        t = Sanitizer.sanitize(s, "", true, Normalizer.Form.D);
        assertEquals("this_is_a_test", t);

    }
}
