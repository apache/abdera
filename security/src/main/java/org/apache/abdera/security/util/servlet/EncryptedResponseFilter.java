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

import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.KeyGenerator;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.xml.security.encryption.XMLCipher;

/**
 * <pre>
 * &lt;filter>
 *   &lt;filter-name>enc filter&lt;/filter-name>
 *   &lt;filter-class>com.test.EncryptedResponseFilter&lt;/filter-class>
 * &lt;/filter>
 * &lt;filter-mapping>
 *   &lt;filter-name>enc filter&lt;/filter-name>
 *   &lt;servlet-name>TestServlet&lt;/servlet-name>
 * &lt;/filter-mapping>
 * </pre>
 */
public class EncryptedResponseFilter 
  extends SecurityFilter {

  public void init(
    FilterConfig config) 
      throws ServletException {
    try {
      Class.forName("org.bouncycastle.LICENSE");
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    } catch (Exception e) {}
  }
  
  @SuppressWarnings("unchecked")
  public void doFilter(
    ServletRequest request, 
    ServletResponse response,
    FilterChain chain) 
      throws IOException, 
             ServletException {
    try {
      X509Certificate[] cert = (X509Certificate[]) request.getAttribute(
        "javax.servlet.request.X509Certificate");
      PublicKey pkey = (cert != null) ? cert[0].getPublicKey() : null;
      if (pkey != null && pkey instanceof RSAPublicKey) {
        BufferingResponseWrapper wrapper = 
          new BufferingResponseWrapper(
            (HttpServletResponse)response);
        chain.doFilter(request, wrapper);
        Document<Element> doc = getDocument(wrapper);
        if (doc != null) {  
          KeyGenerator keygen = KeyGenerator.getInstance("AES");
          keygen.init(new SecureRandom());
          Key key = keygen.generateKey();
          Encryption enc = security.getEncryption(); 
          EncryptionOptions options = enc.getDefaultEncryptionOptions();
          options.setDataEncryptionKey(key);
          options.setKeyEncryptionKey(pkey);
          options.setKeyCipherAlgorithm(XMLCipher.RSA_v1dot5);
          options.setIncludeKeyInfo(true);
          Document<Element> enc_doc = enc.encrypt(doc, options);
          enc_doc.writeTo(response.getOutputStream());
        }
      } else {
        chain.doFilter(request, response);
      }
    } catch (Exception e) {}
  } 
}
