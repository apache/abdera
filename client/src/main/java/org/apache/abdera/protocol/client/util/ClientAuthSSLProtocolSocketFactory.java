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
package org.apache.abdera.protocol.client.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.abdera.i18n.text.Localizer;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

public class ClientAuthSSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    private static final String DEFAULT_PROTOCOL = "TLS";
    private static final String DEFAULT_KMF_FACTORY = "ibmX509";

    private final String protocol;
    private final String kmfFactory;
    private final TrustManager tm;
    private final String keyStorePass;
    private final KeyStore ks;

    public ClientAuthSSLProtocolSocketFactory(KeyStore ks, String keyStorePass) {
        this(ks, keyStorePass, DEFAULT_PROTOCOL, DEFAULT_KMF_FACTORY, null);
    }

    public ClientAuthSSLProtocolSocketFactory(KeyStore ks,
                                              String keyStorePass,
                                              String protocol,
                                              String kmfFactory,
                                              TrustManager tm) {
        if (ks == null)
            throw new IllegalArgumentException(Localizer.get("INVALID.KEYSTORE"));
        this.ks = ks;
        this.keyStorePass = keyStorePass;
        this.protocol = protocol != null ? protocol : DEFAULT_PROTOCOL;
        this.kmfFactory = kmfFactory != null ? kmfFactory : DEFAULT_KMF_FACTORY;
        this.tm = tm;
    }

    public ClientAuthSSLProtocolSocketFactory(String keyStore,
                                              String keyStoreType,
                                              String keyStorePass,
                                              String protocol,
                                              String kmfFactory,
                                              TrustManager tm) {
        this(initKeyStore(keyStore, keyStoreType, keyStorePass), keyStorePass, protocol, kmfFactory, tm);
    }

    private static KeyStore initKeyStore(String keyStore, String keyStoreType, String keyPass) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(keyStoreType);
            ks.load(new FileInputStream(keyStore), keyPass.toCharArray());
        } catch (Exception e) {
        }
        return ks;
    }

    public Socket createSocket(Socket socket, String host, int port, boolean close) throws IOException,
        UnknownHostException {
        return createSocket(host, port, null, 0, null);
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return createSocket(host, port, null, 0, null);
    }

    public Socket createSocket(String host, int port, InetAddress chost, int cport) throws IOException,
        UnknownHostException {
        return createSocket(host, port, chost, cport, null);
    }

    public Socket createSocket(String host, int port, InetAddress chost, int cport, HttpConnectionParams params)
        throws IOException, UnknownHostException, ConnectTimeoutException {

        SSLContext context;
        SSLSocketFactory factory = null;
        SSLSocket socket = null;
        try {
            KeyManagerFactory kmf;
            context = SSLContext.getInstance(protocol);
            kmf = KeyManagerFactory.getInstance(kmfFactory);
            TrustManager tm = (this.tm != null) ? this.tm : new NonOpTrustManager();
            kmf.init(ks, keyStorePass.toCharArray());
            context.init(kmf.getKeyManagers(), new TrustManager[] {tm}, null);
            factory = context.getSocketFactory();
            socket = (SSLSocket)factory.createSocket(host, port);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
