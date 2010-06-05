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

import java.security.cert.Certificate;

import org.apache.commons.httpclient.Credentials;

/**
 * OAuth credentials
 * 
 * @see http://oauth.org
 * @see http://oauth.googlecode.com/svn/spec/branches/1.0/drafts/7/spec.html
 * @author David Calavera
 */
public class OAuthCredentials implements Credentials {

    private String consumerKey;
    private String token;
    private String signatureMethod;
    private String realm;
    private String version;
    private Certificate cert;

    public OAuthCredentials() {
        super();
    }

    public OAuthCredentials(String consumerKey, String token, String signatureMethod, String realm) {
        this(consumerKey, token, signatureMethod, realm, "1.0");
    }

    public OAuthCredentials(String consumerKey, String token, String signatureMethod, String realm, String version) {
        this(consumerKey, token, signatureMethod, realm, version, null);
    }

    public OAuthCredentials(String consumerKey,
                            String token,
                            String signatureMethod,
                            String realm,
                            String version,
                            Certificate cert) {
        super();
        this.consumerKey = consumerKey;
        this.token = token;
        this.signatureMethod = signatureMethod;
        this.realm = realm;
        this.version = version;
        this.cert = cert;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getSignatureMethod() {
        return signatureMethod;
    }

    public void setSignatureMethod(String signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Certificate getCert() {
        return cert;
    }

    public void setCert(Certificate cert) {
        this.cert = cert;
    }
}
