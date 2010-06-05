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
import org.apache.xml.security.keys.KeyInfo;

/**
 * Interface used for digitally signing and verifying Abdera elements
 */
public interface Signature {

    /**
     * Return true if the element has been digitally signed
     */
    <T extends Element> boolean isSigned(T element) throws SecurityException;

    /**
     * Adds a digital signature to the specified element
     */
    <T extends Element> T sign(T element, SignatureOptions options) throws SecurityException;

    /**
     * Verifies that the digitally signed element is valid
     */
    <T extends Element> boolean verify(T element, SignatureOptions options) throws SecurityException;

    /**
     * Returns a listing of X.509 certificates of valid digital signatures in the element
     */
    <T extends Element> X509Certificate[] getValidSignatureCertificates(T element, SignatureOptions options)
        throws SecurityException;

    <T extends Element> KeyInfo getSignatureKeyInfo(T element, SignatureOptions options) throws SecurityException;

    /**
     * Returns the default signing options
     * 
     * @see org.apache.abdera.security.SignatureOptions
     */
    SignatureOptions getDefaultSignatureOptions() throws SecurityException;

    <T extends Element> T removeInvalidSignatures(T element, SignatureOptions options) throws SecurityException;

}
