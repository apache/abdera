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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.junit.Test;

public class XhtmlTest {

    @Test
    public void testXhtml() throws Exception {

        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setContentAsXhtml("<p>Test</p>");
        assertNotNull(entry.getContent());
        assertEquals(Content.Type.XHTML, entry.getContentType());
        Element el = entry.getContentElement().getValueElement();
        assertTrue(el instanceof Div);

        entry = abdera.newEntry();
        entry.setContent("<a><b><c/></b></a>", Content.Type.XML);
        assertNotNull(entry.getContent());
        assertEquals(Content.Type.XML, entry.getContentType());
        assertNotNull(entry.getContentElement().getValueElement());

    }

    @Test
    public void testSpecialCharacters() {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setContentAsXhtml("<p>&Auml;sthetik</p>");
        assertNotNull(entry.getContent());
        assertEquals(Content.Type.XHTML, entry.getContentType());
        Element el = entry.getContentElement().getValueElement();
        char umlaut = ((Element)el.getFirstChild()).getText().charAt(0);
        // hexadecimal value of &Auml; is U+00C4
        assertEquals("c4", Integer.toHexString(umlaut));
    }

}
