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

import org.apache.abdera.model.Document;

/**
 * Interface used for encrypting/decrypting Abdera documents.
 */
@SuppressWarnings("unchecked")
public interface Encryption {

    /**
     * Encrypt the document using the specified options
     * 
     * @param doc The document to encrypt
     * @param options The encryption options
     * @return The encrypted document
     * @throws org.apache.abdera.security.SecurityException if the encryption failed
     */
    Document encrypt(Document doc, EncryptionOptions options) throws SecurityException;

    /**
     * Decrypt the document using the specified options
     * 
     * @param doc The document to decrypt
     * @param options The decryption options
     * @return The decrypted document
     * @throws org.apache.abdera.security.SecurityException if the decryption failed
     */
    Document decrypt(Document doc, EncryptionOptions options) throws SecurityException;

    /**
     * Returns true if this specified document has been encrypted
     */
    boolean isEncrypted(Document doc) throws SecurityException;

    /**
     * Returns the default encryption/decryption options
     * 
     * @see org.apache.abdera.security.EncryptionOptions
     */
    EncryptionOptions getDefaultEncryptionOptions();

}
