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
package org.apache.abdera.test.security;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.Client;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.CommonsClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.protocol.client.util.BaseRequestEntity;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.Signature;
import org.apache.abdera.security.SignatureOptions;
import org.apache.abdera.xpath.XPath;

import junit.framework.TestCase;

public class DSigThirdPartyVerifyTest extends TestCase {

  private static final String keystoreFile = "/key.jks";
  private static final String keystoreType = "JKS";
  private static final String keystorePass = "testing";
  private static final String privateKeyAlias = "James";
  private static final String privateKeyPass = "testing";
  private static final String certificateAlias = "James";
  
  public static void testThirdPartyVerification() throws Exception {
      
    // Initialize the keystore
    KeyStore ks = KeyStore.getInstance(keystoreType);
    assertNotNull(ks);
    
    InputStream in = DigitalSignatureTest.class.getResourceAsStream(keystoreFile);
    assertNotNull(in);
    
    ks.load(in, keystorePass.toCharArray());
    PrivateKey signingKey = 
      (PrivateKey) ks.getKey(
        privateKeyAlias,
        privateKeyPass.toCharArray());
    X509Certificate cert = 
      (X509Certificate) ks.getCertificate(
        certificateAlias);
    assertNotNull(signingKey);
    assertNotNull(cert);
    
    // Create the entry to sign
    Abdera abdera = new Abdera();
    AbderaSecurity absec = new AbderaSecurity(abdera);
    Factory factory = abdera.getFactory();
    
    Entry entry = factory.newEntry();
    entry.setId("http://example.org/foo/entry");  
    entry.setUpdated(new java.util.Date());
    entry.setTitle("This is an entry");
    entry.setContentAsXhtml("This <b>is</b> <i>markup</i>");
    entry.addAuthor("James");
    entry.addLink("http://www.example.org");
    
    // Prepare the digital signature options
    Signature sig = absec.getSignature();
    SignatureOptions options = sig.getDefaultSignatureOptions();    
    options.setCertificate(cert);
    options.setSigningKey(signingKey);

    // Sign the entry
    entry = sig.sign(entry, options);
    assertNotNull(
      entry.getFirstChild(
        new QName(
          "http://www.w3.org/2000/09/xmldsig#", 
          "Signature")));

    // Verify the signature with Verisign's "Signed Ping" interop endpoint
    Client client = new CommonsClient();
    RequestOptions reqoptions = client.getDefaultRequestOptions();
    reqoptions.setContentType("application/xml");
    BaseRequestEntity bre = new BaseRequestEntity(entry,false);
    ClientResponse response = client.post(
      "http://verisignlabs.com/tg/verify", 
      bre, reqoptions);
    assertEquals(200, response.getStatus());
    Document<Element> result = response.getDocument();
    
    XPath xpath = abdera.getXPath();
    assertTrue(
      xpath.booleanValueOf(
        "/Result/SignatureVerifies[text()='true']", 
        result));

  }
  
}
