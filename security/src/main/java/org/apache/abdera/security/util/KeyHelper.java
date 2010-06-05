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
package org.apache.abdera.security.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;

public class KeyHelper {

    public static void saveKeystore(KeyStore ks, String file, String password) throws KeyStoreException,
        NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
        ks.store(new FileOutputStream(file), password.toCharArray());
    }

    public static KeyStore loadKeystore(String file, String pass) throws KeyStoreException, NoSuchAlgorithmException,
        CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (in == null)
            in = new FileInputStream(file);
        ks.load(in, pass.toCharArray());
        return ks;
    }

    public static KeyStore loadKeystore(String type, String file, String pass) throws KeyStoreException,
        NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance(type);
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (in == null)
            in = new FileInputStream(file);
        ks.load(in, pass.toCharArray());
        return ks;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Key> T getKey(KeyStore ks, String alias, String pass) throws KeyStoreException,
        NoSuchAlgorithmException, UnrecoverableKeyException {
        return (T)ks.getKey(alias, pass.toCharArray());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Certificate> T getCertificate(KeyStore ks, String alias) throws KeyStoreException {
        return (T)ks.getCertificate(alias);
    }

    public static KeyPair generateKeyPair(String type, int size) throws NoSuchAlgorithmException,
        NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(type);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(size, random);
        random.setSeed(System.currentTimeMillis());
        return keyGen.generateKeyPair();
    }

    public static KeyPair generateKeyPair(String type, int size, String provider) throws NoSuchAlgorithmException,
        NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(type, provider);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
        keyGen.initialize(size, random);
        random.setSeed(System.currentTimeMillis());
        return keyGen.generateKeyPair();
    }

    public static SecretKey generateSecretKey(String type, int size) throws NoSuchAlgorithmException,
        NoSuchProviderException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(type);
        keyGenerator.init(size);
        return keyGenerator.generateKey();
    }

    public static Key generateKey(String type) throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(type);
        keygen.init(new SecureRandom());
        return keygen.generateKey();
    }

    public static SecretKey generateSecretKey(String type, int size, String provider) throws NoSuchAlgorithmException,
        NoSuchProviderException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(type, provider);
        keyGenerator.init(size);
        return keyGenerator.generateKey();
    }

    public static PublicKey generatePublicKey(String hex) {
        try {
            if (hex == null || hex.trim().length() == 0)
                return null;
            byte[] data = Hex.decodeHex(hex.toCharArray());
            X509EncodedKeySpec keyspec = new X509EncodedKeySpec(data);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(keyspec);
        } catch (Exception e) {
            return null;
        }
    }

}
