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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import javax.activation.MimeType;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.easymock.EasyMock;
import org.junit.Test;

public class MimeTypeHelperTest {

    @Test
    public void testCondense() {
        String[] types = MimeTypeHelper.condense("image/png", "image/gif", "image/png", "image/*");
        assertEquals(1, types.length);
        assertEquals("image/*", types[0]);
    }

    @Test
    public void testGetCharset() {
        String charsetIso = MimeTypeHelper.getCharset("text/html; charset=utf-8");
        assertEquals("utf-8", charsetIso);

        String charsetEmpty = MimeTypeHelper.getCharset("text/plain");
        assertNull(charsetEmpty);
    }

    @Test
    public void testGetMimeType() {
        String mimeType1 = MimeTypeHelper.getMimeType(EasyMock.createMock(Feed.class));
        assertEquals("application/atom+xml;type=feed", mimeType1);
        String mimeType2 = MimeTypeHelper.getMimeType(EasyMock.createMock(Entry.class));
        assertEquals("application/atom+xml;type=entry", mimeType2);
        String mimeType3 = MimeTypeHelper.getMimeType(EasyMock.createMock(Service.class));
        assertEquals("application/atomsvc+xml", mimeType3);
    }

    @Test
    public void testIsApp() {
        assertTrue(MimeTypeHelper.isApp("application/atomsvc+xml"));
        assertFalse(MimeTypeHelper.isApp("application/atomserv+xml"));
    }

    @Test
    public void testIsAtom() {
        assertTrue(MimeTypeHelper.isAtom("application/atom+xml"));
        assertTrue(MimeTypeHelper.isAtom("application/atom+xml;type=\"entry\""));
        assertTrue(MimeTypeHelper.isAtom("application/atom+xml;type=\"feed\""));
    }

    @Test
    public void testIsEntry() {
        assertTrue(MimeTypeHelper.isEntry("application/atom+xml;type=\"entry\""));
    }

    @Test
    public void testIsFeed() {
        assertTrue(MimeTypeHelper.isFeed("application/atom+xml;type=\"feed\""));
    }

    @Test
    public void testIsMatch() throws Exception {
        assertTrue(MimeTypeHelper.isMatch("application/atom+xml;type=entry", "application/atom+xml;type=feed"));
        assertTrue(MimeTypeHelper.isMatch("*/*", "application/atom+xml;type=feed"));
        assertTrue(MimeTypeHelper.isMatch("application/atom+xml;type=entry", "*/*"));
        assertFalse(MimeTypeHelper.isMatch(null, "application/atom+xml;type=feed"));
        assertFalse(MimeTypeHelper.isMatch("blafasel", "application/atom+xml;type=feed"));

        MimeType mimeType1 = new MimeType("application/atom+xml;type=entry");
        MimeType mimeType2 = new MimeType("application/atom+xml;type=feed");
        assertFalse(MimeTypeHelper.isMatch(mimeType1, mimeType2, true));
    }

    @Test
    public void testIsMimeType() {
        assertTrue(MimeTypeHelper.isMimeType("text/html"));
        assertTrue(MimeTypeHelper.isMimeType("*/*"));
        assertTrue(MimeTypeHelper.isMimeType("blafasel/pdf"));
        assertFalse(MimeTypeHelper.isMimeType("text"));
    }

    @Test
    public void testIsMultipart() {
        assertTrue(MimeTypeHelper.isMultipart("multipart/related"));
    }

    @Test
    public void testIsText() {
        assertTrue(MimeTypeHelper.isText("text/plain"));
    }

    @Test
    public void testIsXml() {
        assertTrue(MimeTypeHelper.isXml("application/xml"));
    }
}
