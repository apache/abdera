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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.abdera.security.util.DHContext;

/**
 * A Servlet Filter that uses Diffie-Hellman Key Exchange to encrypt 
 * Atom documents.  The HTTP request must include a X-DH header in the form:
 * 
 * X-DH: p={dh_p}, g={dh_g}, l={dh_l}, k={base64_pubkey}
 * 
 * Example Client Code:
 * <pre>
 *   DHContext context = new DHContext();
 *   Abdera abdera = new Abdera();
 *   CommonsClient client = new CommonsClient(abdera);
 *   RequestOptions options = client.getDefaultRequestOptions();
 *   options.setHeader("X-DH", context.getRequestString());
 *   
 *   ClientResponse response = client.get("http://localhost:8080/TestWeb/test",options);
 *   Document<Element> doc = response.getDocument();
 *   
 *   String dh_ret = response.getHeader("X-DH");
 *   if (dh_ret != null) {
 *     context.setPublicKey(dh_ret);
 *     AbderaSecurity absec = new AbderaSecurity(abdera);
 *     Encryption enc = absec.getEncryption();
 *     EncryptionOptions encoptions = context.getEncryptionOptions(enc);
 *     doc = enc.decrypt(doc, encoptions);
 *   }
 *   
 *   doc.writeTo(System.out);
 * </pre>
 * 
 * Webapp Deployment:
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
public class DHEncryptedResponseFilter 
  extends BCEncryptedResponseFilter {

  public static final String DH = "X-DH";
    
  protected boolean doEncryption(ServletRequest request, Object arg) {
    return arg != null;
  }
  
  protected Object initArg(ServletRequest request) {
    return getDHContext((HttpServletRequest)request);
  }
  
  protected EncryptionOptions initEncryptionOptions(
      ServletRequest request, 
      ServletResponse response,
      Encryption enc,
      Object arg) {
    EncryptionOptions options = null;
    try {
      DHContext context = (DHContext) arg;
      options = context.getEncryptionOptions(enc);
      returnPublicKey((HttpServletResponse)response,context);
    } catch (Exception e) {}
    return options;
    
  }
  
  private void returnPublicKey(HttpServletResponse response, DHContext context) {
    response.setHeader(DH,context.getResponseString());
  }
  
  private DHContext getDHContext(HttpServletRequest request) {
    try {
      String dh_req = request.getHeader(DH);
      if (dh_req == null || dh_req.length() == 0) return null;
      return new DHContext(dh_req);
    } catch (Exception e) {
      return null;
    }
  }
  
}


