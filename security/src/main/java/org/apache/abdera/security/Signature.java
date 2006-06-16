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

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.util.ServiceUtil;


public interface Signature {

  public static final Signature INSTANCE = 
    (Signature) ServiceUtil.newInstance(
      "org.apache.abdera.security.Signature", 
      "org.apache.abdera.security.xmlsec.XmlSignature");
  
  boolean isSigned(Entry entry) throws SecurityException;
  
  boolean isSigned(Feed feed) throws SecurityException;
  
  Entry sign(Entry entry, SignatureOptions options) throws SecurityException;
  
  Feed sign(Feed feed, SignatureOptions options) throws SecurityException;
  
  boolean verify(Entry entry, SignatureOptions options) throws SecurityException;
  
  boolean verify(Feed feed, SignatureOptions options) throws SecurityException;
  
  SignatureOptions getDefaultSignatureOptions() throws SecurityException;
  
}
