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

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.Signature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SecurityFilterTest 
  extends Assert {

  private static JettyServer server;
  private static Abdera abdera = Abdera.getInstance();
  private static AbderaClient client = new AbderaClient();
  
  @BeforeClass
  public static void setUp() throws Exception {
    try {
      server = new JettyServer();
      server.start(CustomProvider.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDown() throws Exception {
    server.stop();
  }

  @Test
  public void testSignedResponseFilter() throws Exception {
    ClientResponse resp = client.get("http://localhost:9002/");
    Document<Element> doc = resp.getDocument();
    Element root = doc.getRoot();
    AbderaSecurity security = new AbderaSecurity(abdera);
    Signature sig = security.getSignature();
    assertTrue(sig.isSigned(root));
    assertTrue(sig.verify(root, sig.getDefaultSignatureOptions()));
  }
  
}
