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
import org.apache.abdera.model.Document;
import org.apache.abdera.security.EncryptionOptions;
import org.apache.abdera.security.SecurityException;
import org.apache.abdera.security.util.EncryptionBase;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;

@SuppressWarnings("unchecked")
public class XmlEncryption extends EncryptionBase {

    static {
        if (!org.apache.xml.security.Init.isInitialized())
            org.apache.xml.security.Init.init();
    }

    public XmlEncryption() {
        super(new Abdera());
    }

    public XmlEncryption(Abdera abdera) {
        super(abdera);
    }

    public Document encrypt(Document doc, EncryptionOptions options) throws SecurityException {
        try {
            org.w3c.dom.Document dom = fomToDom(doc, options);
            Key dek = options.getDataEncryptionKey();
            Key kek = options.getKeyEncryptionKey();
            String dalg = options.getDataCipherAlgorithm();
            String kalg = options.getKeyCipherAlgorithm();
            boolean includeki = options.includeKeyInfo();
            EncryptedKey enckey = null;
            XMLCipher xmlCipher = XMLCipher.getInstance(dalg);
            xmlCipher.init(XMLCipher.ENCRYPT_MODE, dek);
            if (includeki && kek != null && dek != null) {
                XMLCipher keyCipher = XMLCipher.getInstance(kalg);
                keyCipher.init(XMLCipher.WRAP_MODE, kek);
                enckey = keyCipher.encryptKey(dom, dek);
                EncryptedData encdata = xmlCipher.getEncryptedData();
                KeyInfo keyInfo = new KeyInfo(dom);
                keyInfo.add(enckey);
                encdata.setKeyInfo(keyInfo);
            }
            dom = xmlCipher.doFinal(dom, dom.getDocumentElement(), false);
            return domToFom(dom, options);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public Document decrypt(Document doc, EncryptionOptions options) throws SecurityException {
        if (!isEncrypted(doc))
            return null;
        try {
            org.w3c.dom.Document dom = fomToDom(doc, options);
            Key kek = options.getKeyEncryptionKey();
            Key dek = options.getDataEncryptionKey();
            org.w3c.dom.Element element = dom.getDocumentElement();
            XMLCipher xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, dek);
            xmlCipher.setKEK(kek);
            dom = xmlCipher.doFinal(dom, element);
            return domToFom(dom, options);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public EncryptionOptions getDefaultEncryptionOptions() {
        return new XmlEncryptionOptions(getAbdera());
    }

}
