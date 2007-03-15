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
package org.apache.abdera.security.xmlsec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.security.SecurityException;
import org.apache.abdera.security.SignatureOptions;
import org.apache.abdera.security.util.Constants;
import org.apache.abdera.security.util.SignatureBase;
import org.apache.abdera.g14n.iri.IRI;
import org.apache.abdera.g14n.iri.IRISyntaxException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlSignature 
  extends SignatureBase {

  static {
    if (!org.apache.xml.security.Init.isInitialized())
      org.apache.xml.security.Init.init();
  }

  public XmlSignature() {
    super(new Abdera());
  }
  
  public XmlSignature(Abdera abdera) {
    super(abdera);
  }
  
  @SuppressWarnings("unchecked")
  private <T extends Element>T _sign(
    T element, 
    SignatureOptions options) 
      throws XMLSecurityException, 
             IRISyntaxException {    
    element.setBaseUri(element.getResolvedBaseUri());
    org.w3c.dom.Element dom = fomToDom((Element)element.clone(), options);
    org.w3c.dom.Document domdoc = dom.getOwnerDocument();
    PrivateKey signingKey = options.getSigningKey();
    X509Certificate cert = options.getCertificate();
    IRI baseUri = element.getResolvedBaseUri();
    XMLSignature sig = new XMLSignature(
      domdoc, 
      (baseUri != null) ? baseUri.toString() : "", 
      options.getSigningAlgorithm());
    dom.appendChild(sig.getElement());
    Transforms transforms = new Transforms(domdoc);
    transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
    transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
    sig.addDocument("", transforms, org.apache.xml.security.utils.Constants.ALGO_ID_DIGEST_SHA1);
    String[] refs = options.getReferences();
    for (String ref : refs) sig.addDocument(ref);
    sig.addKeyInfo(cert);
    sig.sign(signingKey);    
    return (T)domToFom(dom, options);
  }
  
  public <T extends Element>T sign(
    T entry, 
    SignatureOptions options) 
      throws SecurityException {
    try {
      return (T) _sign(entry, options);
    } catch (Exception e) {
      throw new SecurityException(e);
    }
  }
  
  private boolean is_valid_signature(
    XMLSignature sig) 
      throws XMLSignatureException, 
             XMLSecurityException, 
             IRISyntaxException {
    boolean answer = false;
    KeyInfo ki = sig.getKeyInfo();
    if (ki != null) {
      X509Certificate cert = ki.getX509Certificate();
      if (cert != null) {
        answer = sig.checkSignatureValue(cert);
      } else {
        PublicKey key = ki.getPublicKey();
        if (key != null) {
          answer = sig.checkSignatureValue(key);
        }
      }
    }
    return answer;
  }
  
  private <T extends Element>X509Certificate[] _getcerts(
    T element, 
    SignatureOptions options)
      throws XMLSignatureException, 
             XMLSecurityException, 
             IRISyntaxException {
    List<X509Certificate> certs = new ArrayList<X509Certificate>();
    org.w3c.dom.Element dom = fomToDom((Element)element, options);
    NodeList children = dom.getChildNodes();
    for (int n = 0; n < children.getLength(); n++) {
      try {
        Node node = children.item(n);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          org.w3c.dom.Element el = (org.w3c.dom.Element) node;
          if (Constants.DSIG_NS.equals(el.getNamespaceURI()) &&
              Constants.LN_SIGNATURE.equals(el.getLocalName())) {
            IRI baseUri = element.getResolvedBaseUri();
            XMLSignature sig = 
              new XMLSignature(
                el, (baseUri != null) ? baseUri.toString() : "");
            if (is_valid_signature(sig)) {
              KeyInfo ki = sig.getKeyInfo();
              if (ki != null) {
                X509Certificate cert = ki.getX509Certificate();
                if (cert != null) certs.add(cert);
              }
            }
          }
        }
      } catch (Exception e) {}
    }
    return certs.toArray(new X509Certificate[certs.size()]);
  }
  
  public <T extends Element>X509Certificate[] getValidSignatureCertificates(
    T element, 
    SignatureOptions options) 
      throws SecurityException {
    try {
      return _getcerts(element, options);
    } catch (Exception e) {}
    return null;
  }
  
  private boolean _verify(
    Element element, 
    SignatureOptions options) 
      throws XMLSignatureException, 
             XMLSecurityException, 
             IRISyntaxException {
    boolean answer = false;
    org.w3c.dom.Element dom = fomToDom((Element)element, options);
    NodeList children = dom.getChildNodes();
    for (int n = 0; n < children.getLength(); n++) {
      Node node = children.item(n);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        org.w3c.dom.Element el = (org.w3c.dom.Element) node;
        if (Constants.DSIG_NS.equals(el.getNamespaceURI()) &&
            Constants.LN_SIGNATURE.equals(el.getLocalName())) {
          IRI baseUri = element.getResolvedBaseUri();
          XMLSignature sig = 
            new XMLSignature(
              el, (baseUri != null) ? baseUri.toString() : "");
          answer = is_valid_signature(sig);
        }
      }
    }
    
    return answer;
  }
  
  public <T extends Element>boolean verify(
    T entry, 
    SignatureOptions options) throws SecurityException {
      if (!isSigned(entry)) return false;
      try {
        return _verify(entry,options);
      } catch (Exception e) {
        throw new SecurityException(e);
      }
  }

  public SignatureOptions getDefaultSignatureOptions() throws SecurityException {
    return new XmlSignatureOptions(getAbdera());
  }

}
