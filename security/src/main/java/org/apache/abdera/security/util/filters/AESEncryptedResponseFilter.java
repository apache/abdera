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

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.abdera.security.util.KeyHelper;
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
public class AESEncryptedResponseFilter extends AbstractEncryptedResponseFilter {

    public static final String PUBLICKEY = "X-PublicKey";

    protected X509Certificate[] getCerts(RequestContext request) {
        return (X509Certificate[])request.getAttribute(Scope.REQUEST, "javax.servlet.request.X509Certificate");
    }

    protected PublicKey getPublicKey(RequestContext request) {
        String header = request.getHeader(PUBLICKEY);
        PublicKey pkey = KeyHelper.generatePublicKey(header);
        if (pkey == null)
            pkey = retrievePublicKey(request);
        return pkey;
    }

    protected boolean doEncryption(RequestContext request, Object arg) {
        return arg != null && arg instanceof RSAPublicKey;
    }

    protected Object initArg(RequestContext request) {
        return getPublicKey(request);
    }

    protected PublicKey retrievePublicKey(RequestContext request) {
        X509Certificate[] cert = getCerts(request);
        return cert != null ? cert[0].getPublicKey() : null;
    }

    protected EncryptionOptions initEncryptionOptions(RequestContext request,
                                                      ResponseContext response,
                                                      Encryption enc,
                                                      Object arg) {
        try {
            EncryptionOptions options = enc.getDefaultEncryptionOptions();
            options.setDataEncryptionKey(KeyHelper.generateKey("AES"));
            options.setKeyEncryptionKey((PublicKey)arg);
            options.setKeyCipherAlgorithm(XMLCipher.RSA_v1dot5);
            options.setIncludeKeyInfo(true);
            return options;
        } catch (Exception e) {
            return null;
        }
    }
}
