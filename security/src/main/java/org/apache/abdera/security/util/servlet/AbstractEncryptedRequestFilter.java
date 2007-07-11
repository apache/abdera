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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.abdera.security.SecurityException;

public abstract class AbstractEncryptedRequestFilter 
  extends SecurityFilter {

  // The methods that allow encrypted bodies
  protected final List<String> methods = new ArrayList<String>(); 
  
  protected AbstractEncryptedRequestFilter() {
    this("POST","PUT");
  }
  
  protected AbstractEncryptedRequestFilter(String... methods) {
    for (String method: methods) this.methods.add(method);
  }

  @Override
  public void init(FilterConfig config) throws ServletException {
    super.init(config);
    String s = config.getInitParameter("methods");
    if (s != null && s.length() > 0) {
      this.methods.clear();
      String[] methods = s.split("\\s*,\\s*");
      for (String method : methods) this.methods.add(method);
    }
  }
  
  @SuppressWarnings("unchecked")
  public void doFilter(
    ServletRequest request, 
    ServletResponse response,
    FilterChain chain) 
      throws IOException, 
             ServletException {
    
    bootstrap(request,response);
    HttpServletRequest req = (HttpServletRequest) request;
    String method = req.getMethod();
    if (methods.contains(method.toUpperCase())) {
      BufferedRequestWrapper wrapper = 
        new BufferedRequestWrapper((HttpServletRequest) request);
      try {
        Document<Element> doc = getDocument(wrapper);
        if (doc != null) {
          Encryption enc = security.getEncryption();
          if (enc.isEncrypted(doc)) {
            Object arg = initArg(request);
            EncryptionOptions options = initEncryptionOptions(request,response,enc,arg);
            doDecryption(doc, options, enc, wrapper);
          }
        }
      } catch (Exception e) { 
      } finally {wrapper.reset();}
      request = wrapper;
    }
    chain.doFilter(request, response);
  }

  protected void doDecryption(
    Document doc, 
    EncryptionOptions options, 
    Encryption enc, 
    BufferedRequestWrapper wrapper) 
      throws SecurityException, IOException {
    doc = enc.decrypt(doc, options);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    doc.writeTo(out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    wrapper.setInputStream(in);
  }
  
  protected abstract void bootstrap(ServletRequest request, ServletResponse response);
  
  protected abstract Object initArg(ServletRequest request);
  
  protected abstract EncryptionOptions initEncryptionOptions(
    ServletRequest request, 
    ServletResponse response, 
    Encryption encryption,
    Object arg);
}
