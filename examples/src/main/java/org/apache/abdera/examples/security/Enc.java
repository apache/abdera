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
package org.apache.abdera.examples.security;

import java.security.Provider;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.security.AbderaSecurity;
import org.apache.abdera.security.Encryption;
import org.apache.abdera.security.EncryptionOptions;

public class Enc {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

        Abdera abdera = new Abdera();

        // Prepare the crypto provider
        try {
            String jce =
                abdera.getConfiguration().getConfigurationOption("jce.provider",
                                                                 "org.bouncycastle.jce.provider.BouncyCastleProvider");
            Class provider = Class.forName(jce);
            Provider p = (Provider)provider.newInstance();
            Security.addProvider(p);
        } catch (Exception e) {
            // The configured JCE Provider is not available, try to proceed anyway
        }

        // Generate Encryption Key
        String jceAlgorithmName = "AES";
        KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();

        // Create the entry to encrypt
        AbderaSecurity absec = new AbderaSecurity(abdera);
        Factory factory = abdera.getFactory();

        Entry entry = factory.newEntry();
        entry.setId("http://example.org/foo/entry");
        entry.setUpdated(new java.util.Date());
        entry.setTitle("This is an entry");
        entry.setContentAsXhtml("This <b>is</b> <i>markup</i>");
        entry.addAuthor("James");
        entry.addLink("http://www.example.org");

        // Prepare the encryption options
        Encryption enc = absec.getEncryption();
        EncryptionOptions options = enc.getDefaultEncryptionOptions();
        options.setDataEncryptionKey(key);

        // Encrypt the document using the generated key
        Document enc_doc = enc.encrypt(entry.getDocument(), options);

        enc_doc.writeTo(System.out);

        System.out.println("\n\n");

        // Decrypt the document using the generated key
        Document<Entry> entry_doc = enc.decrypt(enc_doc, options);

        entry_doc.writeTo(System.out);

    }

}
