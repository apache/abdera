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

import java.security.cert.X509Certificate;

import org.apache.abdera.model.Element;

public interface Signature {

  <T extends Element>boolean isSigned(T element) throws SecurityException;
  
  <T extends Element>T sign(T element, SignatureOptions options) throws SecurityException;
  
  <T extends Element>boolean verify(T element, SignatureOptions options) throws SecurityException;
  
  <T extends Element>X509Certificate[] getValidSignatureCertificates(T element, SignatureOptions options) throws SecurityException;
  
  SignatureOptions getDefaultSignatureOptions() throws SecurityException;
  
}
