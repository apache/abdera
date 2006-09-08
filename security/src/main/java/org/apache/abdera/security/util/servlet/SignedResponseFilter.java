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
package org.apache.abdera.security.util.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.SecurityException;
import org.apache.abdera.security.Signature;
import org.apache.abdera.security.SignatureOptions;

/**
 * <p>This HTTP Servlet Filter will add an XML Digital Signature to Abdera documents</p>
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
 * &lt;/filter>
 * &lt;filter-mapping id="signing-filter">
 *   &lt;filter-name>signing filter&lt;/filter-name>
 *   &lt;servlet-name>Abdera&lt;/servlet-name>
 * &lt;/filter-mapping>
 * </pre>
 */
public class SignedResponseFilter 
  implements Filter {

  private static final String KEYSTORE  = "org.apache.abdera.security.util.servlet.Keystore";
  private static final String STOREPASS = "org.apache.abdera.security.util.servlet.KeystorePassword";
  private static final String KEY       = "org.apache.abdera.security.util.servlet.PrivateKeyAlias";
  private static final String KEYPASS   = "org.apache.abdera.security.util.servlet.PrivateKeyPassword";
  private static final String CERT      = "org.apache.abdera.security.util.servlet.CertificateAlias";
  
  private static final String keystoreType = "JKS";
  
  private final Abdera abdera;
  private final AbderaSecurity security;
  private String keystoreFile = null;
  private String keystorePass = null;
  private String privateKeyAlias = null;
  private String privateKeyPass = null;
  private String certificateAlias = null;
  private PrivateKey signingKey = null;
  private X509Certificate cert = null;

  public SignedResponseFilter() {
    this.abdera = new Abdera();
    this.security = new AbderaSecurity(abdera);
  }
  
  public void init(
    FilterConfig config) 
      throws ServletException {
    keystoreFile = config.getInitParameter(KEYSTORE);
    keystorePass = config.getInitParameter(STOREPASS);
    privateKeyAlias = config.getInitParameter(KEY);
    privateKeyPass = config.getInitParameter(KEYPASS);
    certificateAlias = config.getInitParameter(CERT);
    
    try {
      KeyStore ks = KeyStore.getInstance(keystoreType);    
      InputStream in = SignedResponseFilter.class.getResourceAsStream(keystoreFile);
      ks.load(in, keystorePass.toCharArray());
      signingKey = 
        (PrivateKey) ks.getKey(
          privateKeyAlias,
          privateKeyPass.toCharArray());
      cert = 
        (X509Certificate) ks.getCertificate(
          certificateAlias);
    } catch (Exception e) {}
  }
  
  public void destroy() {}

  public void doFilter(
    ServletRequest request, 
    ServletResponse response,
    FilterChain chain) 
      throws IOException, 
             ServletException {

    BufferingResponseWrapper wrapper = 
      new BufferingResponseWrapper(
        (HttpServletResponse)response);
    
    chain.doFilter(request, wrapper);
    
    try {
      Document<Element> doc = getDocument(wrapper);
      if (doc != null) {
        doc = sign(doc);
        doc.writeTo(response.getOutputStream());
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private Document<Element> sign(Document<Element> doc) throws SecurityException  {
    if (signingKey == null || cert == null) return doc; // pass through
    Signature sig = security.getSignature();
    SignatureOptions options = sig.getDefaultSignatureOptions();    
    options.setCertificate(cert);
    options.setSigningKey(signingKey);
    Element element = doc.getRoot();
    element = sig.sign(element, options);
    return element.getDocument();
  }
  
  private Document<Element> getDocument(BufferingResponseWrapper wrapper) {
    Reader rdr = wrapper.getReader();
    InputStream in = wrapper.getInputStream();
    Parser parser = abdera.getParser();
    try {
      if (rdr != null) {
        return parser.parse(rdr);
      }
      if (in != null) {
        return parser.parse(in);
      }
    } catch (Exception e) {}
    return null;
  }
  
  static class BufferingResponseWrapper 
    extends HttpServletResponseWrapper {
    
    CharArrayWriter output = null;
    ByteArrayOutputStream outStream = null;
    
    BufferingResponseWrapper(HttpServletResponse response) {
      super(response);
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
      if (outStream != null) throw new IllegalStateException();
      if (output == null) output = new CharArrayWriter();
      return new PrintWriter(output);
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      if (output != null) throw new IllegalStateException();
      if (outStream == null) outStream = new ByteArrayOutputStream();
      return new BufferingServletOutputStream(outStream);
    }
    
    public Reader getReader() {
      if (output == null) return null;
      return new CharArrayReader(output.toCharArray());
    }
    
    public InputStream getInputStream() {
      if (outStream == null) return null;
      return new ByteArrayInputStream(outStream.toByteArray());
    }
  }
  
  static class BufferingServletOutputStream 
    extends ServletOutputStream {

    ByteArrayOutputStream out = null;
    
    BufferingServletOutputStream(ByteArrayOutputStream out) {
      this.out = out;
    }
    
    public void write(int b) throws IOException {
      out.write(b);
    }
    
    public void write(byte[] b) throws IOException {
      out.write(b);
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
      out.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
      out.close();
      super.close();
    }

    @Override
    public void flush() throws IOException {
      out.flush();
      super.flush();
    }
    
  }
}
