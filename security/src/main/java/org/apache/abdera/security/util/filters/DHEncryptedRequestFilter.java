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
package org.apache.abdera.security.util.filters;

import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.abdera.security.util.Constants;
import org.apache.abdera.security.util.DHContext;

/**
 * A filter implementation that allows requests to be encrypted using Diffie-Hellman key negotiation. A client first
 * uses GET/HEAD/OPTIONS to get the servers DH information, then sends an encrypted request containing it's DH
 * information. The server can then decrypt and process the request. Note: this is currently untested.
 */
public class DHEncryptedRequestFilter extends AbstractEncryptedRequestFilter {

    public DHEncryptedRequestFilter() {
        super();
    }

    public DHEncryptedRequestFilter(String... methods) {
        super(methods);
    }

    public void bootstrap(RequestContext request) {
    }

    public ResponseContext filter(RequestContext request, FilterChain chain) {
        ResponseContext response = super.filter(request, chain);
        String method = request.getMethod();
        // include a Accept-Encryption header in the response to GET, HEAD and OPTIONS requests
        // the header will specify all the information the client needs to construct
        // it's own DH context and encrypt the request
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            DHContext context = (DHContext)request.getAttribute(Scope.SESSION, "dhcontext");
            if (context == null) {
                context = new DHContext();
                request.setAttribute(Scope.SESSION, "dhcontext", context);
            }
            response.setHeader(Constants.ACCEPT_ENCRYPTION, context.getRequestString());
        }
        return response;
    }

    protected Object initArg(RequestContext request) {
        DHContext context = (DHContext)request.getAttribute(Scope.SESSION, "dhcontext");
        String dh = request.getHeader(Constants.CONTENT_ENCRYPTED);
        if (context != null && dh != null && dh.length() > 0) {
            try {
                context.setPublicKey(dh);
            } catch (Exception e) {
            }
        }
        return context;
    }

    protected EncryptionOptions initEncryptionOptions(RequestContext request, Encryption encryption, Object arg) {
        EncryptionOptions options = null;
        if (arg != null && arg instanceof DHContext) {
            try {
                options = ((DHContext)arg).getEncryptionOptions(encryption);
            } catch (Exception e) {
            }
        }
        return options;
    }

}
