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
package org.apache.abdera.i18n.test.iri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Nameprep;

public class TestNameprep extends TestBase {

    enum Test {

        A("Map to nothing", string('f',
                                   'o',
                                   'o',
                                   0xC2,
                                   0xAD,
                                   0xCD,
                                   0x8F,
                                   0xE1,
                                   0xA0,
                                   0x86,
                                   0xE1,
                                   0xA0,
                                   0x8B,
                                   'b',
                                   'a',
                                   'r',
                                   0xE2,
                                   0x80,
                                   0x8B,
                                   0xE2,
                                   0x81,
                                   0xA0,
                                   'b',
                                   'a',
                                   'z',
                                   0xEF,
                                   0xB8,
                                   0x80,
                                   0xEF,
                                   0xB8,
                                   0x88,
                                   0xEF,
                                   0xB8,
                                   0x8F,
                                   0xEF,
                                   0xBB,
                                   0xBF), "foobarbaz"), B("Case folding ASCII U+0043 U+0041 U+0046 U+0045", "CAFE",
            "cafe"), C("Case folding 8bit U+00DF (german sharp s)", string(0xC3, 0x9F), "ss"), D(
            "Case folding U+0130 (turkish capital I with dot)", string(0xC4, 0xB0), string('i', 0xCC, 0x87)), E(
            "Case folding multibyte U+0143 U+037A", string(0xC5, 0x83, 0xCD, 0xBA),
            string(0xC5, 0x84, 0x20, 0xCE, 0xB9)), F("Case folding U+2121 U+33C6 U+1D7BB", string(0xE2,
                                                                                                  0x84,
                                                                                                  0xA1,
                                                                                                  0xE3,
                                                                                                  0x8F,
                                                                                                  0x86,
                                                                                                  0xF0,
                                                                                                  0x9D,
                                                                                                  0x9E,
                                                                                                  0xBB), string('t',
                                                                                                                'e',
                                                                                                                'l',
                                                                                                                'c',
                                                                                                                0xE2,
                                                                                                                0x88,
                                                                                                                0x95,
                                                                                                                'k',
                                                                                                                'g',
                                                                                                                0xCF,
                                                                                                                0x83)), G(
            "Normalization of U+006a U+030c U+00A0 U+00AA", string(0x6A, 0xCC, 0x8C, 0xC2, 0xA0, 0xC2, 0xAA),
            string(0xC7, 0xB0, 'a')), H("Case folding U+1FB7 and normalization", string(0xE1, 0xBE, 0xB7), string(0xE1,
                                                                                                                  0xBE,
                                                                                                                  0xB6,
                                                                                                                  0xCE,
                                                                                                                  0xB9)), I(
            "Self-reverting case folding U+01F0 and normalization", string(0xC7, 0xB0), string(0xC7, 0xB0)), J(
            "Self-reverting case folding U+0390 and normalization", string(0xCE, 0x90), string(0xCE, 0x90)), K(
            "Self-reverting case folding U+03B0 and normalization", string(0xCE, 0xB0), string(0xCE, 0xB0)), L(
            "Self-reverting case folding U+1E96 and normalization", string(0xE1, 0xBA, 0x96), string(0xE1, 0xBA, 0x96)), M(
            "Self-reverting case folding U+1F56 and normalization", string(0xE1, 0xBD, 0x96), string(0xE1, 0xBD, 0x96)), N(
            "ASCII space character U+0020", "\u0020", "\u0020"), O("Non-ASCII 8bit space character U+00A0", "\u00A0",
            ""), P("Non-ASCII multibyte space character U+1680", "\u1680", null, -1), Q(
            "Non-ASCII multibyte space character U+2000", "\u2000", "\u0020", -1), R("Zero Width Space U+200b",
            "\u200B", ""), S("Non-ASCII multibyte space character U+3000", "\u3000", "\u0020", -1), T(
            "ASCII control characters U+0010 U+007F", "\u0010\u007F", "\u0010\u007F"), U(
            "Non-ASCII 8bit control character U+0085", "\u0085", null, -1), V(
            "Non-ASCII multibyte control character U+180E", "\u180E", null, -1), W("Zero Width No-Break Space U+FEFF",
            "\uFEFF", ""), X("Non-ASCII control character U+1D175",
            new String(new char[] {CharUtils.getHighSurrogate(0x1D175), CharUtils.getLowSurrogate(0x1D175)}), null, -1), Y(
            "Plane 0 private use character U+F123", "\uF123", null, -1), Z("Plane 15 private use character U+F1234",
            string(0xF3, 0xB1, 0x88, 0xB4), null, -1), AA("Plane 16 private use character U+10F234", string(0xF4,
                                                                                                            0x8F,
                                                                                                            0x88,
                                                                                                            0xB4),
            null, -1), AB("Non-character code point U+8FFFE", "\ud9ff\udffe", null, -1), AC(
            "Non-character code point U+10FFFF", string(0xF4, 0x8F, 0x8F, 0x8F), null, -1), AD("Surrogate code U+DF42",
            string(0xED, 0xBD, 0x82), null, -1), AE("Non-plain text character U+FFFD", string(0xEF, 0xBF, 0xBD), null,
            -1), AF("Ideographic description character U+2FF5", string(0xE2, 0xBF, 0xB5), null, -1), AG(
            "Display property character U+0341", string(0xCD, 0x81), string(0xCC, 0x81), -1), AH(
            "Left-to-right mark U+200E", string(0xE2, 0x80, 0x8E), null, -1), AI("Deprecated U+202A", string(0xE2,
                                                                                                             0x80,
                                                                                                             0xAA),
            null, -1), AJ("Language tagging character U+E0001", string(0xF3, 0xA0, 0x80, 0x81), null, -1), AK(
            "Language tagging character U+E0042", string(0xF3, 0xA0, 0x81, 0x82), null, -1), AL(
            "Bidi: RandALCat character U+05BE and LCat characters", string('f', 'o', 'o', 0xD6, 0xBE), null, -1), AM(
            "Bidi: RandALCat character U+FD50 and LCat characters", string('f', 'o', 'o', 0xEF, 0xB5, 0x90), null, -1), AN(
            "Bidi: RandALCat character U+FB38 and LCat characters", string('f', 'o', 'o', 0xEF, 0xB9, 0xB6), null, -1), AO(
            "Bidi: RandALCat without trailing RandALCat U+0627 U+0031", string(0xD8, 0xA7, 0x31), null, -1), AP(
            "Bidi: RandALCat character U+0627 U+0031 U+0628", string(0xD8, 0xA7, 0x31, 0xD8, 0xA8), string(0xD8,
                                                                                                           0xA7,
                                                                                                           0x31,
                                                                                                           0xD8,
                                                                                                           0xA8)),
        // AQ("Unassigned code point U+E0002",string(0xF3,0xA0,0x80,0x82),null,true,-1),
        // AR("Larger test (shrinking)", //
        // {"Larger test (shrinking)","X\xC2\xAD\xC3\x9F\xC4\xB0\xE2\x84\xA1\x6a\xcc\x8c\xc2\xa0\xc2""\xaa\xce\xb0\xe2\x80\x80",
        // "xssi\xcc\x87" "tel\xc7\xb0 a\xce\xb0 ","Nameprep"},
        // string('X',0xC2,0xAD,0xC3,0x9F,0xC4,0xB0,0xE2,0x84,0xA1,0x6a,0xc,'c',0x8c,0xc2,0xa0,0xc2),
        // string('x','s','s','i',0xcc,0x87,'t','e','l',0xc7,0xb0,'a',0xce,0xb0), false, 0),
        AS("Larger test (expanding)", string('X',
                                             0xC3,
                                             0x9F,
                                             0xe3,
                                             0x8c,
                                             0x96,
                                             0xC4,
                                             0xB0,
                                             0xE2,
                                             0x84,
                                             0xA1,
                                             0xE2,
                                             0x92,
                                             0x9F,
                                             0xE3,
                                             0x8c,
                                             0x80), string('x',
                                                           's',
                                                           's',
                                                           0xE3,
                                                           0x82,
                                                           0xAD,
                                                           0xE3,
                                                           0x83,
                                                           0xAD,
                                                           0xE3,
                                                           0x83,
                                                           0xA1,
                                                           0xE3,
                                                           0x83,
                                                           0xBC,
                                                           0xE3,
                                                           0x83,
                                                           0x88,
                                                           0xE3,
                                                           0x83,
                                                           0xAB,
                                                           'i',
                                                           0xCC,
                                                           0x87,
                                                           't',
                                                           'e',
                                                           'l',
                                                           0x28,
                                                           'd',
                                                           0x29,
                                                           0xE3,
                                                           0x82,
                                                           0xA2,
                                                           0xE3,
                                                           0x83,
                                                           0x91,
                                                           0xE3,
                                                           0x83,
                                                           0xBC,
                                                           0xE3,
                                                           0x83,
                                                           0x88)), AT("Case map + normalization", string(0xC2, 0xB5),
            string(0xCE, 0xBC)), AU("NFKC test", string(0xC2, 0xAA), string(0x61)), AV(
            "nameprep, exposed a bug in libstringprep 0.0.5", string(0xC2, 0xAA, 0x0A), string(0x61, 0x0A)),
        // AW("unassigned code point U+0221", "\u0221", "\u0221", true, 0),
        AX("unassigned code point U+0221", "\u0221", "\u0221", -1),
        // AY("Unassigned code point U+0236", "\u0236", "\u0236", true, 0),
        AZ("unassigned code point U+0236", "\u0236", "\u0236", -1), BA(
            "bidi both RandALCat and LCat  U+0627 U+00AA U+0628", string(0xD8, 0xA7, 0xC2, 0xAA, 0xD8, 0xA8), null, -1);

        String comment;
        String in;
        String out;
        int rc;

        Test(String comment, String in, String out) {
            this.comment = comment;
            this.in = in;
            this.out = out;
        }

        Test(String comment, String in, String out, int rc) {
            this.comment = comment;
            this.in = in;
            this.out = out;
            this.rc = rc;
        }

    }

    @org.junit.Test
    public void testNameprep() throws Exception {
        for (Test test : Test.values()) {
            try {
                String out = Nameprep.prep(test.in);
                assertEquals(out, test.out);
            } catch (Exception e) {
                if (test.rc != -1)
                    fail("Failure in Test #" + test + " not expected");
            }
        }
    }

}
