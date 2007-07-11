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

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.abdera.security.util.DHContext;

/**
 * A filter implementation that allows requests to be encrypted using Diffie-Hellman
 * key negotiation.  A client first uses GET/HEAD/OPTIONS to get the servers DH
 * information, then sends an encrypted request containing it's DH information.
 * The server can then decrypt and process the request.
 * 
 * Note: this is currently untested.
 */
public class DHEncryptedRequestFilter 
  extends AbstractEncryptedRequestFilter {

  private DHContext context;
  
  @Override
  public void init(FilterConfig config) throws ServletException {
    super.init(config);
    context = new DHContext();
  }
  
  @Override
  public void bootstrap(
    ServletRequest request, 
    ServletResponse response ) {
    String method = ((HttpServletRequest)request).getMethod();
    // include a X-DH header in the response to GET, HEAD and OPTIONS requests
    // the header will specify all the information the client needs to construct
    // it's own DH context and encrypt the request
    if ("GET".equalsIgnoreCase(method) || 
        "HEAD".equalsIgnoreCase(method) || 
        "OPTIONS".equalsIgnoreCase(method)) {
      ((HttpServletResponse)response).setHeader(
        DHEncryptedResponseFilter.DH, 
        this.context.getRequestString());
    } 
  }

  @Override
  protected Object initArg(ServletRequest request) {
    DHContext context = null;
    String dh = ((HttpServletRequest)request).getHeader(DHEncryptedResponseFilter.DH);
    if (dh != null && dh.length() > 0) {
      try {
        context = (DHContext) this.context.clone();
        context.setPublicKey(dh);
      } catch (Exception e) {}
    }
    return context;
  }

  @Override
  protected EncryptionOptions initEncryptionOptions(
    ServletRequest request,
    ServletResponse response, 
    Encryption encryption, 
    Object arg) {
      EncryptionOptions options = null;
      if (arg != null && arg instanceof DHContext) {
        try {
          options = ((DHContext)arg).getEncryptionOptions(encryption);
        } catch (Exception e) {}
      }
      return options;
  }

}
