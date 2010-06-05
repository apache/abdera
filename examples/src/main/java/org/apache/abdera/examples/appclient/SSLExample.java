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
package org.apache.abdera.examples.appclient;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.util.ClientAuthSSLProtocolSocketFactory;

public class SSLExample {

    /**
     * Abdera's default trust manager is registered on port 443 and accepts all server certificates and issuers.
     */
    public static void defaultTrustManager() throws Exception {

        Abdera abdera = new Abdera();
        AbderaClient client = new AbderaClient(abdera);

        // Default trust manager provider registered for port 443
        AbderaClient.registerTrustManager();

        client.get("https://localhost:9080/foo");

    }

    /**
     * The default trust manager can be registered for additional ports
     */
    public static void defaultTrustManager2() throws Exception {

        Abdera abdera = new Abdera();
        AbderaClient client = new AbderaClient(abdera);

        // Default trust manager provider registered for port 9443
        AbderaClient.registerTrustManager(9443);

        client.get("https://localhost:9080/foo");

    }

    /**
     * You can provide your own X509TrustManager implementation
     */
    public static void customTrustManager() throws Exception {

        Abdera abdera = new Abdera();
        AbderaClient client = new AbderaClient(abdera);

        // Default trust manager provider registered for port 9443
        AbderaClient.registerTrustManager(new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] certs, String arg1) throws CertificateException {
                // ignore this one for now
            }

            public void checkServerTrusted(X509Certificate[] certs, String arg1) throws CertificateException {
                // logic to determine if the cert is acceptable
                // throw a CertificateException if it's not
            }

            public X509Certificate[] getAcceptedIssuers() {
                List<X509Certificate> certs = new ArrayList<X509Certificate>();
                // prepare list of accepted issuer certs
                return certs.toArray(new X509Certificate[certs.size()]);
            }

        });

        client.get("https://localhost:9080/foo");

    }

    /**
     * You can provide your own secure socket factory to support additional use cases, such as using client certs for
     * auth
     */
    public static void clientAuth() throws Exception {

        Abdera abdera = new Abdera();
        AbderaClient client = new AbderaClient(abdera);

        KeyStore keystore = null;
        ClientAuthSSLProtocolSocketFactory factory =
            new ClientAuthSSLProtocolSocketFactory(keystore, "keystorepassword");

        AbderaClient.registerFactory(factory, 443);

        // DO NOT register a trust manager after this point

        client.get("https://localhost:9080/foo");
    }

}
