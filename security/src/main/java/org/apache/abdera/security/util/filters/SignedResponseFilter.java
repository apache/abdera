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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextWrapper;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.SecurityException;
import org.apache.abdera.security.Signature;
import org.apache.abdera.security.SignatureOptions;
import org.apache.abdera.writer.Writer;

/**
 * <p>
 * This HTTP Servlet Filter will add an XML Digital Signature to Abdera documents
 * </p>
 * 
 * <pre>
 * &lt;filter>
 *   &lt;filter-name>signing filter&lt;/filter-name>
 *   &lt;filter-class>org.apache.abdera.security.util.servlet.SignedResponseFilter&lt;/filter-class>
 *   &lt;init-param>
 *     &lt;param-name>org.apache.abdera.security.util.servlet.Keystore&lt;/param-name>
 *     &lt;param-value>/key.jks&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>org.apache.abdera.security.util.servlet.KeystorePassword&lt;/param-name>
 *     &lt;param-value>testing&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>org.apache.abdera.security.util.servlet.PrivateKeyAlias&lt;/param-name>
 *     &lt;param-value>James&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>org.apache.abdera.security.util.servlet.PrivateKeyPassword&lt;/param-name>
 *     &lt;param-value>testing&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>org.apache.abdera.security.util.servlet.CertificateAlias&lt;/param-name>
 *     &lt;param-value>James&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>org.apache.abdera.security.util.servlet.SigningAlgorithm&lt;/param-name>
 *     &lt;param-value>http://www.w3.org/2000/09/xmldsig#rsa-sha1&lt;/param-value>
 *   &lt;/init-param>
 * &lt;/filter>
 * &lt;filter-mapping id="signing-filter">
 *   &lt;filter-name>signing filter&lt;/filter-name>
 *   &lt;servlet-name>Abdera&lt;/servlet-name>
 * &lt;/filter-mapping>
 * </pre>
 */
public class SignedResponseFilter implements Filter {

    private static final String keystoreType = "JKS";

    private String keystoreFile = null;
    private String keystorePass = null;
    private String privateKeyAlias = null;
    private String privateKeyPass = null;
    private String certificateAlias = null;
    private String algorithm = null;
    private PrivateKey signingKey = null;
    private X509Certificate cert = null;

    public SignedResponseFilter(String keystoreFile,
                                String keystorePass,
                                String privateKeyAlias,
                                String privateKeyPass,
                                String certificateAlias,
                                String algorithm) {
        this.keystoreFile = keystoreFile;
        this.keystorePass = keystorePass;
        this.privateKeyAlias = privateKeyAlias;
        this.privateKeyPass = privateKeyPass;
        this.certificateAlias = certificateAlias;
        this.algorithm = algorithm;
        try {
            KeyStore ks = KeyStore.getInstance(keystoreType);
            InputStream in = SignedResponseFilter.class.getResourceAsStream(keystoreFile);
            ks.load(in, keystorePass.toCharArray());
            signingKey = (PrivateKey)ks.getKey(privateKeyAlias, privateKeyPass.toCharArray());
            cert = (X509Certificate)ks.getCertificate(certificateAlias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResponseContext filter(RequestContext request, FilterChain chain) {
        return new SigningResponseContextWrapper(request.getAbdera(), chain.next(request));
    }

    private Document<Element> signDocument(Abdera abdera, Document<Element> doc) throws SecurityException {
        AbderaSecurity security = new AbderaSecurity(abdera);
        if (signingKey == null || cert == null)
            return doc; // pass through
        Signature sig = security.getSignature();
        SignatureOptions options = sig.getDefaultSignatureOptions();
        options.setCertificate(cert);
        options.setSigningKey(signingKey);
        if (algorithm != null)
            options.setSigningAlgorithm(algorithm);
        Element element = doc.getRoot();
        element = sig.sign(element, options);
        return element.getDocument();
    }

    private class SigningResponseContextWrapper extends ResponseContextWrapper {

        private final Abdera abdera;

        public SigningResponseContextWrapper(Abdera abdera, ResponseContext response) {
            super(response);
            this.abdera = abdera;
        }

        public void writeTo(OutputStream out, Writer writer) throws IOException {
            try {
                sign(out, null);
            } catch (Exception se) {
                throw new RuntimeException(se);
            }
        }

        public void writeTo(OutputStream out) throws IOException {
            try {
                sign(out, null);
            } catch (Exception se) {
                throw new RuntimeException(se);
            }
        }

        private void sign(OutputStream aout, Writer writer) throws Exception {
            Document<Element> doc = null;
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (writer == null)
                    super.writeTo(out);
                else
                    super.writeTo(out, writer);
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                doc = abdera.getParser().parse(in);
            } catch (Exception e) {
            }
            if (doc != null) {
                doc = signDocument(abdera, doc);
                doc.writeTo(aout);
            } else {
                super.writeTo(aout);
            }
        }
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    public void setPrivateKeyAlias(String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }

    public String getPrivateKeyPass() {
        return privateKeyPass;
    }

    public void setPrivateKeyPass(String privateKeyPass) {
        this.privateKeyPass = privateKeyPass;
    }

    public String getCertificateAlias() {
        return certificateAlias;
    }

    public void setCertificateAlias(String certificateAlias) {
        this.certificateAlias = certificateAlias;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public PrivateKey getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(PrivateKey signingKey) {
        this.signingKey = signingKey;
    }

    public X509Certificate getCert() {
        return cert;
    }

    public void setCert(X509Certificate cert) {
        this.cert = cert;
    }

    public static String getKeystoreType() {
        return keystoreType;
    }
}
