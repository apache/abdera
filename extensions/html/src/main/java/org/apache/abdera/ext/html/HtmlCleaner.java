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
package org.apache.abdera.ext.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Arrays;

import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlSerializer;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlCleaner {

    private HtmlCleaner() {
    }

    public static String parse(String value) {
        return parse(new StringReader(value), true);
    }

    public static String parse(InputStream in) {
        return parse(in, "UTF-8");
    }

    public static String parse(InputStream in, String charset) {
        try {
            return parse(new InputStreamReader(in, charset), true);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String parse(Reader in, boolean fragment) {
        try {
            nu.validator.htmlparser.sax.HtmlParser htmlParser = new nu.validator.htmlparser.sax.HtmlParser();
            htmlParser.setBogusXmlnsPolicy(XmlViolationPolicy.ALTER_INFOSET);
            htmlParser.setMappingLangToXmlLang(true);
            htmlParser.setReportingDoctype(false);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(out, "UTF-8");
            HtmlSerializer ser = new VoidElementFixHtmlSerializer(w);
            htmlParser.setContentHandler(ser);
            htmlParser.setLexicalHandler(ser);
            if (!fragment)
                htmlParser.parse(new InputSource(in));
            else
                htmlParser.parseFragment(new InputSource(in), "div");
            try {
                w.flush();
            } catch (IOException e) {
            }
            return new String(out.toByteArray(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static class VoidElementFixHtmlSerializer extends HtmlSerializer {
        private static final String[] VOID_ELEMENTS =
            {"area", "base", "basefont", "bgsound", "br", "col", "embed", "frame", "hr", "img", "input", "link",
             "meta", "param", "spacer", "wbr"};
        private final Writer writer;

        public VoidElementFixHtmlSerializer(Writer out) {
            super(out);
            this.writer = out;
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if (Arrays.binarySearch(VOID_ELEMENTS, localName) > -1) {
                try {
                    writer.write('<');
                    writer.write('/');
                    writer.write(localName);
                    writer.write('>');
                } catch (IOException e) {
                    throw new SAXException(e);
                }
            }
            super.endElement(uri, localName, name);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            StringBuilder buf = new StringBuilder();
            for (int n = start; n < (start + length); n++) {
                if (ch[n] == '<')
                    buf.append("&lt;");
                else if (ch[n] == '>')
                    buf.append("&gt;");
                else if (ch[n] == '&') {
                    boolean isentity = false;
                    int i = n;
                    String ent = null;
                    for (; i < (start + length); i++) {
                        if (ch[i] == ';') {
                            ent = new String(ch, n, i - n + 1);
                            isentity = ent.matches("\\&[\\w]*\\;");
                            break;
                        }
                    }
                    if (isentity) {
                        buf.append(ent);
                        n = i;
                    } else {
                        buf.append("&amp;");
                    }
                } else
                    buf.append(ch[n]);
            }
            super.characters(buf.toString().toCharArray(), 0, buf.length());
        }
    }
}
