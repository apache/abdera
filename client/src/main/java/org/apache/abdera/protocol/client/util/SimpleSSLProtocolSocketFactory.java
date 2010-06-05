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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

public class SimpleSSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    private SSLContext context = null;

    public SimpleSSLProtocolSocketFactory(TrustManager trustManager) {
        init(trustManager);
    }

    public SimpleSSLProtocolSocketFactory() {
        this(new NonOpTrustManager());
    }

    private void init(TrustManager trustManager) {
        try {
            context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] {trustManager}, null);
        } catch (Exception e) {
        }
    }

    public Socket createSocket(Socket socket, String host, int port, boolean close) throws IOException,
        UnknownHostException {
        return context.getSocketFactory().createSocket(socket, host, port, close);
    }

    public Socket createSocket(String host, int port, InetAddress chost, int cport) throws IOException,
        UnknownHostException {
        return context.getSocketFactory().createSocket(host, port, chost, cport);
    }

    public Socket createSocket(String host, int port, InetAddress chost, int cport, HttpConnectionParams params)
        throws IOException, UnknownHostException, ConnectTimeoutException {
        return context.getSocketFactory().createSocket(host, port, chost, cport);
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return context.getSocketFactory().createSocket(host, port);
    }

}
