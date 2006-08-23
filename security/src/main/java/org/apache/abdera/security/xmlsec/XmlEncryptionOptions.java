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

import java.security.Key;

import org.apache.abdera.Abdera;
import org.apache.abdera.security.EncryptionOptions;

public class XmlEncryptionOptions 
  extends XmlSecurityOptions
  implements EncryptionOptions {

  private Key dek = null;
  private Key kek = null;
  private String kca = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
  private String dca = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
  private boolean setki = false;
  
  protected XmlEncryptionOptions(Abdera abdera) {
    super(abdera);
  }
  
  public Key getDataEncryptionKey() {
    return dek;
  }

  public void setDataEncryptionKey(Key key) {
    this.dek = key;
  }

  public Key getKeyEncryptionKey() {
    return kek;
  }

  public void setKeyEncryptionKey(Key key) {
    this.kek = key;
  }

  public String getKeyCipherAlgorithm() {
    return kca;
  }

  public void setKeyCipherAlgorithm(String alg) {
    this.kca = alg;
  }

  public String getDataCipherAlgorithm() {
    return dca;
  }

  public void setDataCipherAlgorithm(String alg) {
    this.dca = alg;
  }

  public boolean includeKeyInfo() {
    return setki;
  }

  public void setIncludeKeyInfo(boolean includeKeyInfo) {
    this.setki = includeKeyInfo;
  }
}
