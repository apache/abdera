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
import java.io.PushbackReader;
import java.io.Reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class FOMXmlVersionReader extends PushbackReader {

    private String version = null;

    public FOMXmlVersionReader(Reader in) {
        super(in, 200);
        try {
            version = detectVersion();
        } catch (IOException e) {
        }
    }

    public String getVersion() {
        return version;
    }

    private String detectVersion() throws IOException {
        String version = "1.0";
        try {
            char[] p = new char[200];
            int r = read(p);
            XMLStreamReader xmlreader =
                XMLInputFactory.newInstance().createXMLStreamReader(new java.io.CharArrayReader(p));
            String v = xmlreader.getVersion();
            if (v != null)
                version = v;
            unread(p, 0, r);
        } catch (Exception e) {
        }
        return version;
    }
}
