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
package org.apache.abdera.test.security.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.Signature;
import org.apache.abdera.security.SignatureOptions;
import org.apache.abdera.test.security.DigitalSignatureTest;
import org.apache.axiom.testutils.PortAllocator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SecurityFilterTest {

    private static int port;
    private static JettyServer server;
    private static Abdera abdera = Abdera.getInstance();
    private static AbderaClient client = new AbderaClient();

    @BeforeClass
    public static void setUp() throws Exception {
        port = PortAllocator.allocatePort();
        server = new JettyServer(port);
        server.start(CustomProvider.class);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testSignedResponseFilter() throws Exception {
        ClientResponse resp = client.get("http://localhost:" + port + "/");
        Document<Element> doc = resp.getDocument();
        Element root = doc.getRoot();
        AbderaSecurity security = new AbderaSecurity(abdera);
        Signature sig = security.getSignature();
        assertTrue(sig.isSigned(root));
        assertTrue(sig.verify(root, sig.getDefaultSignatureOptions()));
    }

    private static final String keystoreFile = "/key.jks";
    private static final String keystoreType = "JKS";
    private static final String keystorePass = "testing";
    private static final String privateKeyAlias = "James";
    private static final String privateKeyPass = "testing";
    private static final String certificateAlias = "James";

    @Test
    public void testSignedRequestFilter() throws Exception {
        Entry entry = abdera.newEntry();
        entry.setId("http://localhost:" + port + "/feed/entries/1");
        entry.setTitle("test entry");
        entry.setContent("Test Content");
        entry.addLink("http://example.org");
        entry.setUpdated(new Date());
        entry.addAuthor("James");
        ClientResponse resp = client.post("http://localhost:" + port + "/feed", entry);
        assertNotNull(resp);
        assertEquals(ResponseType.CLIENT_ERROR, resp.getType());

        // Initialize the keystore
        AbderaSecurity security = new AbderaSecurity(abdera);
        KeyStore ks = KeyStore.getInstance(keystoreType);
        assertNotNull(ks);

        InputStream in = DigitalSignatureTest.class.getResourceAsStream(keystoreFile);
        assertNotNull(in);

        ks.load(in, keystorePass.toCharArray());
        PrivateKey signingKey = (PrivateKey)ks.getKey(privateKeyAlias, privateKeyPass.toCharArray());
        X509Certificate cert = (X509Certificate)ks.getCertificate(certificateAlias);
        assertNotNull(signingKey);
        assertNotNull(cert);

        Signature sig = security.getSignature();
        SignatureOptions options = sig.getDefaultSignatureOptions();
        options.setCertificate(cert);
        options.setSigningKey(signingKey);

        // Sign the entry
        entry = sig.sign(entry, options);

        resp = client.post("http://localhost:" + port + "/feed", entry);
        assertNotNull(resp);
        assertEquals(ResponseType.SUCCESS, resp.getType());
    }
}
