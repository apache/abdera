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
package org.apache.abdera.security;

import org.apache.abdera.Abdera;
import org.apache.abdera.util.AbderaConfiguration;
import org.apache.abdera.util.ServiceUtil;

public class AbderaSecurity {

  private Abdera abdera = null;
  private Encryption encryption = null;
  private Signature signature = null;
  
  public AbderaSecurity() {
    abdera = new Abdera();
  }
  
  public AbderaSecurity(Abdera abdera) {
    this.abdera = abdera;
  }
  
  public AbderaSecurity(AbderaConfiguration config) {
    this.abdera = new Abdera(config);
  }
  
  private Abdera getAbdera() {
    return abdera;
  }
  
  public Encryption newEncryption() {
    return
      (Encryption) ServiceUtil.newInstance(
          "org.apache.abdera.security.Encryption", 
          "org.apache.abdera.security.xmlsec.XmlEncryption",
          getAbdera());
  }
  
  public Encryption getEncryption() {
    if (encryption == null)
      encryption = newEncryption();
    return encryption;
  }
  
  public Signature newSignature() {
    return
      (Signature) ServiceUtil.newInstance(
        "org.apache.abdera.security.Signature", 
        "org.apache.abdera.security.xmlsec.XmlSignature",
        getAbdera());
  }
  
  public Signature getSignature() {
    if (signature == null)
      signature = newSignature();
    return signature;
  }
  
}
