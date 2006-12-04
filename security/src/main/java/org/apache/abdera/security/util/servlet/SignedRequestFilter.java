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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.Signature;

/**
 * Servlet Filter that verifies that an Atom document received by the server
 * via PUT or POST contains a valid XML Digital Signature.  
 */
public class SignedRequestFilter 
  extends SecurityFilter {

  public static final String VALID = "org.apache.abdera.security.util.servlet.SignedRequestFilter.valid";
  public static final String CERTS = "org.apache.abdera.security.util.servlet.SignedRequestFilter.certs";
  
  public void doFilter(
    ServletRequest request, 
    ServletResponse response,
    FilterChain filter) 
      throws IOException, ServletException {
    
    HttpServletRequest req = (HttpServletRequest) request;
    String method = req.getMethod();
    if (method.equals("POST") || method.equals("PUT")) {
      BufferedRequestWrapper wrapper = 
        new BufferedRequestWrapper((HttpServletRequest) request);
      try {
        Abdera abdera = new Abdera();
        AbderaSecurity absec = new AbderaSecurity(abdera);
        Signature sig = absec.getSignature();
        Document<Element> doc = abdera.getParser().parse(wrapper.getInputStream());
        boolean valid = sig.verify(doc.getRoot(), null);
        if (!valid) {
          ((HttpServletResponse)response).sendError(
            400, "A Valid Signature is required");
          return;
        }
        wrapper.setAttribute(VALID, Boolean.valueOf(valid));
        wrapper.setAttribute(CERTS, sig.getValidSignatureCertificates(doc.getRoot(), null));
      } catch (Exception e) {
        e.printStackTrace();
      } 
      wrapper.reset();
      filter.doFilter(wrapper, response);
    } else {
      filter.doFilter(request, response);
    }
  }

}
