/*
 * (c) 2007 Joost Technologies B.V. All rights reserved. This code contains
 * trade secrets of Joost Technologies B.V. and any unauthorized use or
 * disclosure is strictly prohibited.
 * 
 * $Id$
 */
package org.apache.abdera.test.parser;

import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;

import junit.framework.TestCase;


public class EncodingTest extends TestCase {
    
    public void testContentEncoding() throws Exception {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId("http://example.com/entry/1");
        entry.setTitle("Whatever");
        entry.setUpdated(new Date());
        Content content = entry.getFactory().newContent(Content.Type.XML);
        String s = "<x>" + new Character((char) 224) + "</x>";
        content.setValue(s);
        content.setMimeType("application/xml+whatever");
        entry.setContentElement(content);
        assertNotNull(entry.getContent());
        assertEquals(s, entry.getContent());
    }

}
