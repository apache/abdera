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
package org.apache.abdera.i18n.text;

import java.io.IOException;

import org.apache.abdera.i18n.text.data.UnicodeCharacterDatabase;

/**
 * Performs Unicode Normalization (Form D,C,KD and KC)
 */
public final class Normalizer {

    private enum Mask {
        NONE, COMPATIBILITY, COMPOSITION
    }

    public enum Form {
        D, C(Mask.COMPOSITION), KD(Mask.COMPATIBILITY), KC(Mask.COMPATIBILITY, Mask.COMPOSITION);

        private int mask = 0;

        Form(Mask... masks) {
            for (Mask mask : masks) {
                this.mask |= (mask.ordinal());
            }
        }

        public boolean isCompatibility() {
            return (mask & (Mask.COMPATIBILITY.ordinal())) != 0;
        }

        public boolean isCanonical() {
            return !isCompatibility();
        }

        public boolean isComposition() {
            return (mask & (Mask.COMPOSITION.ordinal())) != 0;
        }
    }

    private Normalizer() {
    }

    /**
     * Normalize the string using NFKC
     */
    public static String normalize(CharSequence source) {
        return normalize(source, Form.KC);
    }

    /**
     * Normalize the string using the specified Form
     */
    public static String normalize(CharSequence source, Form form) {
        return normalize(source, form, new StringBuilder());
    }

    /**
     * Normalize the string into the given StringBuilder using the given Form
     */
    public static String normalize(CharSequence source, Form form, StringBuilder buf) {
        if (source.length() != 0) {
            try {
                decompose(source, form, buf);
                compose(form, buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return buf.toString();
    }

    private static void decompose(CharSequence source, Form form, StringBuilder buf) throws IOException {
        StringBuilder internal = new StringBuilder();
        CodepointIterator ci = CodepointIterator.forCharSequence(source);
        boolean canonical = form.isCanonical();
        while (ci.hasNext()) {
            Codepoint c = ci.next();
            internal.setLength(0);
            UnicodeCharacterDatabase.decompose(c.getValue(), canonical, internal);
            CodepointIterator ii = CodepointIterator.forCharSequence(internal);
            while (ii.hasNext()) {
                Codepoint ch = ii.next();
                int i = findInsertionPoint(buf, ch.getValue());
                buf.insert(i, CharUtils.toString(ch.getValue()));
            }
        }

    }

    private static int findInsertionPoint(StringBuilder buf, int c) {
        int cc = UnicodeCharacterDatabase.getCanonicalClass(c);
        int i = buf.length();
        if (cc != 0) {
            int ch;
            for (; i > 0; i -= CharUtils.length(c)) {
                ch = CharUtils.codepointAt(buf, i - 1).getValue();
                if (UnicodeCharacterDatabase.getCanonicalClass(ch) <= cc)
                    break;
            }
        }
        return i;
    }

    private static void compose(Form form, StringBuilder buf) throws IOException {
        if (!form.isComposition())
            return;
        int pos = 0;
        int lc = CharUtils.codepointAt(buf, pos).getValue();
        int cpos = CharUtils.length(lc);
        int lcc = UnicodeCharacterDatabase.getCanonicalClass(lc);
        if (lcc != 0)
            lcc = 256;
        int len = buf.length();
        int c;
        for (int dpos = cpos; dpos < buf.length(); dpos += CharUtils.length(c)) {
            c = CharUtils.codepointAt(buf, dpos).getValue();
            int cc = UnicodeCharacterDatabase.getCanonicalClass(c);
            int composite = UnicodeCharacterDatabase.getPairComposition(lc, c);
            if (composite != '\uFFFF' && (lcc < cc || lcc == 0)) {
                CharUtils.setChar(buf, pos, composite);
                lc = composite;
            } else {
                if (cc == 0) {
                    pos = cpos;
                    lc = c;
                }
                lcc = cc;
                CharUtils.setChar(buf, cpos, c);
                if (buf.length() != len) {
                    dpos += buf.length() - len;
                    len = buf.length();
                }
                cpos += CharUtils.length(c);
            }
        }
        buf.setLength(cpos);
    }

}
