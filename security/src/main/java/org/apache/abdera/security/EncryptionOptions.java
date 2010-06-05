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

import java.security.Key;

/**
 * Provides access to the information necessary to encrypt or decrypt a document
 */
public interface EncryptionOptions extends SecurityOptions {

    /**
     * Return the secret key used to encrypt/decrypt the document content
     */
    Key getDataEncryptionKey();

    /**
     * Set the secret key used to encrypt/decrypt the document content
     */
    EncryptionOptions setDataEncryptionKey(Key key);

    /**
     * Return the secret key used to encrypt/decrypt the data encryption key
     */
    Key getKeyEncryptionKey();

    /**
     * Set the secret key used to encrypt/decrypt the data encryption key
     */
    EncryptionOptions setKeyEncryptionKey(Key key);

    /**
     * Return the cipher algorithm used to decrypt/encrypt the data encryption key The default is
     * "http://www.w3.org/2001/04/xmlenc#kw-aes128"
     */
    String getKeyCipherAlgorithm();

    /**
     * Set the cipher algorithm used to decrypt/encrypt the data encryption key The default is
     * "http://www.w3.org/2001/04/xmlenc#kw-aes128"
     */
    EncryptionOptions setKeyCipherAlgorithm(String alg);

    /**
     * Return the cipher algorithm used to decrypt/encrypt the document content The default is
     * "http://www.w3.org/2001/04/xmlenc#aes128-cbc"
     */
    String getDataCipherAlgorithm();

    /**
     * Set the cipher algorithm used to decyrpt/encrypt the document content The default is
     * "http://www.w3.org/2001/04/xmlenc#aes128-cbc"
     */
    EncryptionOptions setDataCipherAlgorithm(String alg);

    /**
     * Return true if the encryption should include information about the key The default is false
     */
    boolean includeKeyInfo();

    /**
     * Set whether the encryption should include information about the key The default is false
     */
    EncryptionOptions setIncludeKeyInfo(boolean includeKeyInfo);

}
