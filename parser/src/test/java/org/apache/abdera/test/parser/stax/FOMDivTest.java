package org.apache.abdera.test.parser.stax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Text;
import org.junit.Test;

public class FOMDivTest {
    
    @Test
    public void getInternalValueWithUtf8Characters (){
        Abdera abdera = new Abdera ();
        InputStream in = FOMTest.class.getResourceAsStream("/utf8characters.xml");
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();

        assertEquals("Item", entry.getTitle());
        assertEquals(Text.Type.TEXT, entry.getTitleType());
        String value = entry.getContentElement().getValue();
        assertTrue(value.contains("Ȁȁ"));
    }
}
