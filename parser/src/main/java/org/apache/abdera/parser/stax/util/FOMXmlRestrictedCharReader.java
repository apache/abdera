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

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.abdera.util.XmlRestrictedCharReader;
import org.apache.abdera.util.XmlUtil;

public final class FOMXmlRestrictedCharReader extends XmlRestrictedCharReader {

    public FOMXmlRestrictedCharReader(Reader in) {
        this(new FOMXmlVersionReader(in));
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionReader in) {
        super(in, XmlUtil.getVersion(in.getVersion()));
    }

    public FOMXmlRestrictedCharReader(Reader in, char replacement) {
        this(new FOMXmlVersionReader(in), replacement);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionReader in, char replacement) {
        super(in, XmlUtil.getVersion(in.getVersion()), replacement);
    }

    public FOMXmlRestrictedCharReader(InputStream in) {
        this(new FOMXmlVersionInputStream(in));
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in) {
        super(in, XmlUtil.getVersion(in.getVersion()));
    }

    public FOMXmlRestrictedCharReader(InputStream in, char replacement) {
        this(new FOMXmlVersionInputStream(in), replacement);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in, char replacement) {
        super(in, XmlUtil.getVersion(in.getVersion()), replacement);
    }

    public FOMXmlRestrictedCharReader(InputStream in, String charset) throws UnsupportedEncodingException {
        this(new FOMXmlVersionInputStream(in), charset);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in, String charset) throws UnsupportedEncodingException {
        super(in, charset, XmlUtil.getVersion(in.getVersion()));
    }

    public FOMXmlRestrictedCharReader(InputStream in, String charset, char replacement)
        throws UnsupportedEncodingException {
        this(new FOMXmlVersionInputStream(in), charset, replacement);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in, String charset, char replacement)
        throws UnsupportedEncodingException {
        super(in, charset, XmlUtil.getVersion(in.getVersion()), replacement);
    }

}
