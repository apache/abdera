package org.apache.abdera.test.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.junit.Test;

public class HeadTest {

    @Test
    public void testHead() {
        AbderaClient abderaClient = new AbderaClient();
        ClientResponse response = abderaClient.head("http://www.heise.de/newsticker/heise-atom.xml");
        assertNotNull(response);
        assertEquals("HEAD", response.getMethod());
        assertEquals(-1, response.getContentLength());
    }

}
