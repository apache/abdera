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
package org.apache.abdera.security.util.servlet;

import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.axiom.om.util.Base64;
import org.apache.xml.security.encryption.XMLCipher;

/**
 * Implements the Diffie-Hellman Key Exchange details for both parties
 * 
 * Party A:
 * 
 * DHContext context_a = new DHContext();
 * String req = context_a.getRequestString();
 * 
 * Party B:
 * 
 * DHContext context_b = new DHContext(req);
 * EncryptionOptions options = context_b.getEncryptionOptions(enc);
 * // encrypt
 * String ret = context_b.getResponseString();
 * 
 * Party A:
 * 
 * context_a.setPublicKey(ret);
 * EncryptionOptions options = context_a.getEncryptionOptions(enc);
 * // decrypt
 * 
 */
public class DHContext {

  BigInteger p = null, g = null;
  int l = 0;
  private KeyPair keyPair;
  private Key publicKey;
  
  public DHContext() {
    try {
      init();
    } catch (Exception e) {}
  }
  
  public DHContext(String dh) {
    try {
      init(dh);
    } catch (Exception e) {}
  }
  
  public String getRequestString() {
    StringBuffer buf = new StringBuffer();
    buf.append("p=");
    buf.append(p.toString());
    buf.append(", ");
    buf.append("g=");
    buf.append(g.toString());
    buf.append(", ");
    buf.append("l=");
    buf.append(l);
    buf.append(", ");
    buf.append("k=");
    buf.append(Base64.encode(keyPair.getPublic().getEncoded()));
    return buf.toString();
  }
  
  public String getResponseString() {
    StringBuffer buf = new StringBuffer();
    buf.append("k=");
    buf.append(Base64.encode(keyPair.getPublic().getEncoded()));
    return buf.toString();
  }
  
  private void init() 
    throws NoSuchAlgorithmException, 
           InvalidAlgorithmParameterException, 
           InvalidParameterSpecException,
           InvalidKeySpecException  {
    AlgorithmParameterGenerator pgen = AlgorithmParameterGenerator.getInstance("DH");
    pgen.init(512);
    AlgorithmParameters params = pgen.generateParameters();
    DHParameterSpec dhspec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
    KeyPairGenerator keypairgen = KeyPairGenerator.getInstance("DH");
    keypairgen.initialize(dhspec);   
    keyPair = keypairgen.generateKeyPair();
    p = dhspec.getP();
    g = dhspec.getG();
    l = dhspec.getL();
  }
  
  private void init(
    String dh) 
      throws NoSuchAlgorithmException, 
             InvalidAlgorithmParameterException, 
             InvalidKeySpecException {
    String[] params = dh.split("\\s*,\\s*");
    byte[] key = null;
    for (String param : params) {
      String name = param.substring(0,param.indexOf("="));
      String value = param.substring(param.indexOf("=") + 1);
      if (name.equalsIgnoreCase("p"))
        p = new BigInteger(value);
      else if (name.equalsIgnoreCase("g"))
        g = new BigInteger(value);
      else if (name.equalsIgnoreCase("l"))
        l = Integer.parseInt(value);
      else if (name.equalsIgnoreCase("k"))
        key = Base64.decode(value);
    }
    init(p,g,l,key);
  }
  
  private void init(
    BigInteger p, 
    BigInteger g, 
    int l, 
    byte[] key) 
      throws NoSuchAlgorithmException, 
             InvalidAlgorithmParameterException, 
             InvalidKeySpecException {
    DHParameterSpec spec = new DHParameterSpec(p,g,l);
    KeyPairGenerator keypairgen = KeyPairGenerator.getInstance("DH");
    keypairgen.initialize(spec);   
    keyPair = keypairgen.generateKeyPair();
    publicKey = decode(key);
  }
  
  public KeyPair getKeyPair() {
    return keyPair;
  }
  
  public Key getPublicKey() {
    return publicKey;
  }
  
  private Key decode(
    byte[] key)      
      throws NoSuchAlgorithmException, 
             InvalidKeySpecException {
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
    KeyFactory keyFact = KeyFactory.getInstance("DH");
    return keyFact.generatePublic(x509KeySpec);
  }
  
  public void setPublicKey(
    String dh) 
      throws NoSuchAlgorithmException, 
             InvalidKeySpecException {
    String[] tokens = dh.split("\\s*,\\s*");
    byte[] key = null;
    for (String token : tokens) {
      String name = token.substring(0,token.indexOf("="));
      String value = token.substring(token.indexOf("=") + 1);
      if (name.equalsIgnoreCase("k"))
        key = Base64.decode(value);
    }
    publicKey = decode(key);
  }
  
  public Key generateSecret() 
    throws NoSuchAlgorithmException, 
           InvalidKeyException {
    KeyAgreement ka = KeyAgreement.getInstance("DH");
    ka.init(keyPair.getPrivate());
    ka.doPhase(publicKey, true);
    return ka.generateSecret("DESede");
  }
  
  public EncryptionOptions getEncryptionOptions(
    Encryption enc) 
      throws InvalidKeyException, 
             NoSuchAlgorithmException {
    EncryptionOptions options = enc.getDefaultEncryptionOptions();
    options.setDataEncryptionKey(generateSecret());
    options.setDataCipherAlgorithm(XMLCipher.TRIPLEDES);
    return options;
  }
}
