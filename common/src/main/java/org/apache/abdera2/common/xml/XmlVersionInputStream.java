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
package org.apache.abdera2.common.xml;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera2.common.io.PeekAheadInputStream;


/**
 * Will attempt to autodetect the character encoding from the stream This will preserve the BOM if it exists
 */
public class XmlVersionInputStream extends FilterInputStream {

    private XMLVersion version = null;
    private final int peek_ahead;
    private static final int DEFAULT_PEEK_AHEAD = 200;

    public XmlVersionInputStream(InputStream in, int peek_ahead) {
      super(new PeekAheadInputStream(in, peek_ahead));
      this.peek_ahead = peek_ahead;
      try {
        version = detectVersion();
      } catch (IOException e) {}
    }
    
    public XmlVersionInputStream(InputStream in) {
        this(in,DEFAULT_PEEK_AHEAD);
    }

    public XMLVersion getVersion() {
        return version;
    }

    private XMLVersion detectVersion() throws IOException {
        String version = "1.0";
        PeekAheadInputStream pin = (PeekAheadInputStream)this.in;
        try {
            byte[] p = new byte[peek_ahead];
            pin.peek(p);
            XMLStreamReader xmlreader =
                XMLInputFactory.newInstance().createXMLStreamReader(new java.io.ByteArrayInputStream(p));
            String v = xmlreader.getVersion();
            if (v != null)
                version = v;
        } catch (Exception e) {
        }
        return XMLVersion.get(version);
    }

}
