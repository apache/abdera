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
package org.apache.abdera.ext.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.util.MethodHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.RFC2617Scheme;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * OAuth Scheme implementation for use with HTTP Commons AbderaClient
 * 
 * @see http://oauth.org
 * @see http://oauth.googlecode.com/svn/spec/branches/1.0/drafts/7/spec.html
 * @author David Calavera
 */
public class OAuthScheme extends RFC2617Scheme implements AuthScheme {

    private enum OAUTH_KEYS {
        OAUTH_CONSUMER_KEY, OAUTH_TOKEN, OAUTH_SIGNATURE_METHOD, OAUTH_TIMESTAMP, OAUTH_NONCE, OAUTH_VERSION, OAUTH_SIGNATURE;

        public String toLowerCase() {
            return this.toString().toLowerCase();
        }
    }

    private final int NONCE_LENGTH = 16;

    public static void register(AbderaClient abderaClient, boolean exclusive) {
        AbderaClient.registerScheme("OAuth", OAuthScheme.class);
        if (exclusive)
            ((AbderaClient)abderaClient).setAuthenticationSchemePriority("OAuth");
        else
            ((AbderaClient)abderaClient).setAuthenticationSchemeDefaults();
    }

    public String authenticate(Credentials credentials, String method, String uri) throws AuthenticationException {
        return authenticate(credentials, resolveMethod(method, uri));
    }

    public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
        if (credentials instanceof OAuthCredentials) {

            OAuthCredentials oauthCredentials = (OAuthCredentials)credentials;
            String nonce = generateNonce();
            long timestamp = new Date().getTime() / 1000;

            String signature = generateSignature(oauthCredentials, method, nonce, timestamp);

            return "OAuth realm=\"" + oauthCredentials.getRealm()
                + "\", "
                + OAUTH_KEYS.OAUTH_CONSUMER_KEY.toLowerCase()
                + "=\""
                + oauthCredentials.getConsumerKey()
                + "\", "
                + OAUTH_KEYS.OAUTH_TOKEN.toLowerCase()
                + "=\""
                + oauthCredentials.getToken()
                + "\", "
                + OAUTH_KEYS.OAUTH_SIGNATURE_METHOD.toLowerCase()
                + "=\""
                + oauthCredentials.getSignatureMethod()
                + "\", "
                + OAUTH_KEYS.OAUTH_SIGNATURE.toLowerCase()
                + "=\""
                + signature
                + "\", "
                + OAUTH_KEYS.OAUTH_TIMESTAMP.toLowerCase()
                + "=\""
                + timestamp
                + "\", "
                + OAUTH_KEYS.OAUTH_NONCE.toLowerCase()
                + "=\""
                + nonce
                + "\", "
                + OAUTH_KEYS.OAUTH_VERSION.toLowerCase()
                + "=\""
                + oauthCredentials.getVersion()
                + "\"";
        } else {
            return null;
        }
    }

    private HttpMethod resolveMethod(String method, String uri) throws AuthenticationException {
        if (method.equalsIgnoreCase("get")) {
            return new GetMethod(uri);
        } else if (method.equalsIgnoreCase("post")) {
            return new PostMethod(uri);
        } else if (method.equalsIgnoreCase("put")) {
            return new PutMethod(uri);
        } else if (method.equalsIgnoreCase("delete")) {
            return new DeleteMethod(uri);
        } else if (method.equalsIgnoreCase("head")) {
            return new HeadMethod(uri);
        } else if (method.equalsIgnoreCase("options")) {
            return new OptionsMethod(uri);
        } else {
            // throw new AuthenticationException("unsupported http method : " + method);
            return new MethodHelper.ExtensionMethod(method, uri);
        }
    }

    private String generateSignature(OAuthCredentials credentials, HttpMethod method, String nonce, long timestamp)
        throws AuthenticationException {
        try {
            String baseString =
                method.getName().toUpperCase() + method.getURI().toString()
                    + OAUTH_KEYS.OAUTH_CONSUMER_KEY.toLowerCase()
                    + "="
                    + credentials.getConsumerKey()
                    + OAUTH_KEYS.OAUTH_TOKEN.toLowerCase()
                    + "="
                    + credentials.getToken()
                    + OAUTH_KEYS.OAUTH_SIGNATURE_METHOD.toLowerCase()
                    + "="
                    + credentials.getSignatureMethod()
                    + OAUTH_KEYS.OAUTH_TIMESTAMP.toLowerCase()
                    + "="
                    + timestamp
                    + OAUTH_KEYS.OAUTH_NONCE.toLowerCase()
                    + "="
                    + nonce
                    + OAUTH_KEYS.OAUTH_VERSION.toLowerCase()
                    + "="
                    + credentials.getVersion();
            return sign(credentials.getSignatureMethod(), URLEncoder.encode(baseString, "UTF-8"), credentials.getCert());
        } catch (URIException e) {
            throw new AuthenticationException(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
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

    private String sign(String method, String baseString, Certificate cert) throws AuthenticationException {
        if (method.equalsIgnoreCase("HMAC-MD5") || method.equalsIgnoreCase("HMAC-SHA1")) {
            try {
                String[] tokens = method.split("-");
                String methodName =
                    tokens[0].substring(0, 1).toUpperCase() + tokens[0].substring(1).toLowerCase() + tokens[1];
                KeyGenerator kg = KeyGenerator.getInstance(methodName);

                Mac mac = Mac.getInstance(kg.getAlgorithm());
                mac.init(kg.generateKey());
                byte[] result = mac.doFinal(baseString.getBytes());

                return new String(Base64.encodeBase64(result));
            } catch (Exception e) {
                throw new AuthenticationException(e.getMessage(), e);
            }
        } else if (method.equalsIgnoreCase("md5")) {
            return new String(Base64.encodeBase64(DigestUtils.md5(baseString)));
        } else if (method.equalsIgnoreCase("sha1")) {
            return new String(Base64.encodeBase64(DigestUtils.sha(baseString)));
        } else if (method.equalsIgnoreCase("RSA-SHA1")) {
            if (cert == null) {
                throw new AuthenticationException("a cert is mandatory to use SHA1 with RSA");
            }
            try {
                Cipher cipher = Cipher.getInstance("SHA1withRSA");
                cipher.init(Cipher.ENCRYPT_MODE, cert);
                byte[] result = cipher.doFinal(baseString.getBytes());
                return new String(Base64.encodeBase64(result));
            } catch (Exception e) {
                throw new AuthenticationException(e.getMessage(), e);
            }
        } else {
            throw new AuthenticationException("unsupported algorithm method: " + method);
        }
    }

    public String getSchemeName() {
        return "OAuth";
    }

    public boolean isComplete() {
        return true;
    }

    public boolean isConnectionBased() {
        return true;
    }

}
