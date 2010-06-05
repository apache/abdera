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
package org.apache.abdera.parser.stax.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.i18n.text.io.CharsetSniffingInputStream;
import org.apache.abdera.i18n.text.io.PeekAheadInputStream;

/**
 * Will attempt to autodetect the character encoding from the stream This will preserve the BOM if it exists.
 */
public class FOMSniffingInputStream extends CharsetSniffingInputStream {

    public FOMSniffingInputStream(InputStream in) {
        super(in);
    }

    protected String detectEncoding() throws IOException {
        String charset = super.detectEncoding();
        PeekAheadInputStream pin = getInternal();
        try {
            byte[] p = new byte[200];
            pin.peek(p);
            XMLStreamReader xmlreader =
                XMLInputFactory.newInstance().createXMLStreamReader(new java.io.ByteArrayInputStream(p));
            String cs = xmlreader.getCharacterEncodingScheme();
            if (cs != null)
                charset = cs;
        } catch (Exception e) {
        }
        return charset;
    }

}
