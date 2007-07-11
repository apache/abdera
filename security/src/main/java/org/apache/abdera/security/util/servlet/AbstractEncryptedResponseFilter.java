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
import java.security.Provider;
import java.security.Security;

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

public abstract class AbstractEncryptedResponseFilter 
  extends SecurityFilter {
    
  public void init(
    FilterConfig config) 
      throws ServletException {
    initProvider();
  }
  
  protected void initProvider() {}
  
  protected void addProvider(Provider provider) {
    if (Security.getProvider(provider.getName()) == null) 
      Security.addProvider(provider);
  }
  
  @SuppressWarnings("unchecked")
  public void doFilter(
    ServletRequest request, 
    ServletResponse response,
    FilterChain chain) 
      throws IOException, 
             ServletException {
    try {
      Object arg = initArg(request);
      if (doEncryption(request, arg)) {
        BufferingResponseWrapper wrapper = 
          new BufferingResponseWrapper(
            (HttpServletResponse)response);
        chain.doFilter(request, wrapper);
        Document<Element> doc = getDocument(wrapper);
        if (doc != null) {  
          Encryption enc = security.getEncryption(); 
          EncryptionOptions options = initEncryptionOptions(request,response,enc,arg);
          Document<Element> enc_doc = enc.encrypt(doc, options);
          enc_doc.writeTo(response.getOutputStream());
        }
      } else {
        chain.doFilter(request, response);
      }
    } catch (Exception e) {}
  } 

  protected abstract boolean doEncryption(ServletRequest request, Object arg);
  
  protected abstract EncryptionOptions initEncryptionOptions(
      ServletRequest request, 
      ServletResponse response,
      Encryption enc,
      Object arg);
  
  protected abstract Object initArg(ServletRequest request);
}
