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
package org.apache.abdera.ext.wsse;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.RFC2617Scheme;

/**
 * WSSE Auth Scheme implementation for use with HTTP Commons AbderaClient Some APP implementations use WSSE for
 * authentication
 * 
 * @see http://www.xml.com/pub/a/2003/12/17/dive.html
 */
public class WSSEAuthScheme extends RFC2617Scheme implements AuthScheme {

    private final int NONCE_LENGTH = 16;

    public static void register(AbderaClient abderaClient, boolean exclusive) {
        AbderaClient.registerScheme("WSSE", WSSEAuthScheme.class);
        if (exclusive)
            ((AbderaClient)abderaClient).setAuthenticationSchemePriority("WSSE");
        else
            ((AbderaClient)abderaClient).setAuthenticationSchemeDefaults();
    }

    public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
        if (credentials instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentials creds = (UsernamePasswordCredentials)credentials;
            AtomDate now = new AtomDate(new Date());
            String nonce = generateNonce();
            String digest = generatePasswordDigest(creds.getPassword(), nonce, now);
            String username = creds.getUserName();

            String wsse =
                "UsernameToken Username=\"" + username
                    + "\", "
                    + "PasswordDigest=\""
                    + digest
                    + "\", "
                    + "Nonce=\""
                    + nonce
                    + "\", "
                    + "Created=\""
                    + now.getValue()
                    + "\"";
            if (method != null)
                method.addRequestHeader("X-WSSE", wsse);
            return "WSSE profile=\"UsernameToken\"";
        } else {
            return null;
        }
    }

    private String generatePasswordDigest(String password, String nonce, AtomDate date) throws AuthenticationException {
        String temp = nonce + date.getValue() + password;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            return new String(Base64.encodeBase64(md.digest(temp.getBytes())));
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage(), e);
        }
    }

    private String generateNonce() throws AuthenticationException {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] temp = new byte[NONCE_LENGTH];
            sr.nextBytes(temp);
            String n = new String(Hex.encodeHex(temp));
            return n;
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage(), e);
        }
    }

    public String authenticate(Credentials credentials, String method, String uri) throws AuthenticationException {
        return authenticate(credentials, null);
    }

    public String getSchemeName() {
        return "WSSE";
    }

    public boolean isComplete() {
        return true;
    }

    public boolean isConnectionBased() {
        return false;
    }

}
