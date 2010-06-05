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
package org.apache.abdera.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.abdera.i18n.text.io.FilteredCharReader;
import org.apache.abdera.util.XmlUtil.XMLVersion;

/**
 * A reader implementation that filters out characters that are not allowed in XML 1.0 or XML 1.1 documents. The default
 * xMLVersion is to assume XML 1.0. By default, invalid characters are simply removed from the stream. Alternatively, a
 * replacement character can be provided so long as it is a valid XML character itself.
 */
public class XmlRestrictedCharReader extends FilteredCharReader {

    /**
     * The XMLVersion determines which set of restrictions to apply depending on the XML version being parsed
     */
    public XmlRestrictedCharReader(InputStream in) {
        this(new InputStreamReader(in));
    }

    public XmlRestrictedCharReader(InputStream in, String charset) throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset));
    }

    public XmlRestrictedCharReader(InputStream in, XMLVersion version) {
        this(new InputStreamReader(in), version);
    }

    public XmlRestrictedCharReader(InputStream in, String charset, XMLVersion version)
        throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset), version);
    }

    public XmlRestrictedCharReader(InputStream in, char replacement) {
        this(new InputStreamReader(in), replacement);
    }

    public XmlRestrictedCharReader(InputStream in, String charset, char replacement)
        throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset), replacement);
    }

    public XmlRestrictedCharReader(InputStream in, XMLVersion version, char replacement) {
        this(new InputStreamReader(in), version, replacement);
    }

    public XmlRestrictedCharReader(InputStream in, String charset, XMLVersion version, char replacement)
        throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset), version, replacement);
    }

    public XmlRestrictedCharReader(Reader in) {
        this(in, XMLVersion.XML10, (char)0);
    }

    public XmlRestrictedCharReader(Reader in, XMLVersion version) {
        this(in, version, (char)0);
    }

    public XmlRestrictedCharReader(Reader in, char replacement) {
        this(in, XMLVersion.XML10, replacement);
    }

    public XmlRestrictedCharReader(Reader in, XMLVersion version, char replacement) {
        super(in, version.filter(), replacement);
    }

}
