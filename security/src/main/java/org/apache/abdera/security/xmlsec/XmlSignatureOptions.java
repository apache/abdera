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
import org.apache.abdera.security.SignatureOptions;

public class XmlSignatureOptions 
  extends XmlSecurityOptions 
  implements SignatureOptions {

  private PrivateKey signingKey = null;
  private PublicKey publickey = null;
  private X509Certificate cert = null;
  private String[] linkrels = null;
  private boolean signlinks = false;
  private List<String> references = null;
  private String algo = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
  
  public String getSigningAlgorithm() {
    return algo;
  }
  
  public void setSigningAlgorithm(String algorithm) {
    this.algo = algorithm;
  }
  
  protected XmlSignatureOptions(Abdera abdera) {
    super(abdera);
    references = new ArrayList<String>();
  }

  public PrivateKey getSigningKey() {
    return signingKey;
  }

  public void setSigningKey(PrivateKey privateKey) {
    this.signingKey = privateKey;
  }

  public X509Certificate getCertificate() {
    return cert;
  }

  public void setCertificate(X509Certificate cert) {
    this.cert = cert;
  }

  public void addReference(String href) {
    if (!references.contains(href)) references.add(href);
  }

  public String[] getReferences() {
    return references.toArray(new String[references.size()]);
  }

  public PublicKey getPublicKey() {
    return publickey;
  }

  public void setPublicKey(PublicKey publickey) {
    this.publickey = publickey;
  }

  public boolean isSignLinks() {
    return signlinks;
  }

  public void setSignLinks(boolean signlinks) {
    this.signlinks = signlinks;
  }

  public String[] getSignLinkRels() {
    return this.linkrels;
  }

  public void setSignedLinkRels(String... rel) {
    this.linkrels = rel;
  }

}
