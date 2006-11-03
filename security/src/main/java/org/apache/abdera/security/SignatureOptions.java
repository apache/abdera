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

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Provides access to the information necessary to signed an Abdera element
 */
public interface SignatureOptions 
  extends SecurityOptions {

  /**
   * Return the private key with which to sign the element
   */
  PrivateKey getSigningKey();
  
  /**
   * Set the private key with which to sign the element
   */
  void setSigningKey(PrivateKey privateKey);
  
  /**
   * Return the X.509 cert to associated with the signature
   */
  X509Certificate getCertificate();
  
  /**
   * Set the X.509 cert to associate with the signature
   */
  void setCertificate(X509Certificate cert);

  void addReference(String href);
  
  String[] getReferences();
  
}
