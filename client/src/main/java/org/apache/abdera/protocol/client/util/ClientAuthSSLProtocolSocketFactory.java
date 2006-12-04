package org.apache.abdera.protocol.client.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

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

public class ClientAuthSSLProtocolSocketFactory 
  implements SecureProtocolSocketFactory {

  private String keyStore = null;
  private String keyStoreType = null;
  private String keyPass = null;
  private String protocol = "TLS";
  private String kmfFactory = "ibmX509";
  private TrustManager tm = null;
  
  public ClientAuthSSLProtocolSocketFactory(
    String keyStore, 
    String keyStoreType,
    String keyPass,
    String protocol,
    String kmfFactory,
    TrustManager tm) {
      this.keyStore = keyStore;
      this.keyStoreType = keyStoreType;
      this.keyPass = keyPass;
      if (protocol != null) this.protocol = protocol;
      if (kmfFactory != null) this.kmfFactory = kmfFactory;
      if (tm != null) this.tm = tm;
  }
  
  public Socket createSocket(
    Socket socket, 
    String host, 
    int port, 
    boolean close) 
      throws IOException, 
             UnknownHostException {
    return createSocket(host,port,null,0,null);
  }

  public Socket createSocket(
    String host, 
    int port) 
      throws IOException, 
             UnknownHostException {
    return createSocket(host,port,null,0,null);
  }

  public Socket createSocket(
    String host, 
    int port, 
    InetAddress chost, 
    int cport) 
      throws IOException, 
             UnknownHostException {
    return createSocket(host,port,chost,cport,null);
  }

  public Socket createSocket(
    String host, 
    int port, 
    InetAddress chost, 
    int cport, 
    HttpConnectionParams params) 
      throws IOException, 
             UnknownHostException, 
             ConnectTimeoutException {
    
    SSLContext context;
    SSLSocketFactory factory = null;
    SSLSocket socket = null;
    try {
      KeyManagerFactory kmf;
      KeyStore ks;
      char[] passphrase = keyPass.toCharArray();

      context = SSLContext.getInstance(protocol);
      kmf = KeyManagerFactory.getInstance(kmfFactory);
      ks = KeyStore.getInstance(keyStoreType);
      ks.load(new FileInputStream(keyStore), passphrase);

      TrustManager tm = (this.tm != null) ? this.tm : new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) 
          throws CertificateException {}
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) 
          throws CertificateException {}
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      };
      
      kmf.init(ks, passphrase);
      context.init(kmf.getKeyManagers(), new TrustManager[] {tm}, null);
      
      factory = context.getSocketFactory();

      socket = (SSLSocket) factory.createSocket(host, port);
      return socket;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}