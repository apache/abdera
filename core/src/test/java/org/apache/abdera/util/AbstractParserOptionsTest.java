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

import static org.junit.Assert.assertEquals;

import org.apache.abdera.factory.Factory;
import org.junit.Test;

public class AbstractParserOptionsTest {

    private static final class TestParserOptions extends AbstractParserOptions {

        @Override
        protected void checkFactory(Factory factory) {
        }

        @Override
        protected void initFactory() {
        }
    }

    @Test
    public void checkAllEntities() throws Exception {
        TestParserOptions fomParserOptions = new TestParserOptions();

        assertEquals("00A1", getHexValue("iexcl", fomParserOptions));
        assertEquals("00A2", getHexValue("cent", fomParserOptions));
        assertEquals("00A3", getHexValue("pound", fomParserOptions));
        assertEquals("00A4", getHexValue("curren", fomParserOptions));
        assertEquals("00A5", getHexValue("yen", fomParserOptions));
        assertEquals("00A6", getHexValue("brvbar", fomParserOptions));
        assertEquals("00A7", getHexValue("sect", fomParserOptions));
        assertEquals("00A8", getHexValue("uml", fomParserOptions));
        assertEquals("00A9", getHexValue("copy", fomParserOptions));
        assertEquals("00AA", getHexValue("ordf", fomParserOptions));
        assertEquals("00AB", getHexValue("laquo", fomParserOptions));
        assertEquals("00AC", getHexValue("not", fomParserOptions));
        assertEquals("00AD", getHexValue("shy", fomParserOptions));
        assertEquals("00AE", getHexValue("reg", fomParserOptions));
        assertEquals("00AF", getHexValue("macr", fomParserOptions));
        assertEquals("00B0", getHexValue("deg", fomParserOptions));
        assertEquals("00B1", getHexValue("plusmn", fomParserOptions));
        assertEquals("00B2", getHexValue("sup2", fomParserOptions));
        assertEquals("00B3", getHexValue("sup3", fomParserOptions));
        assertEquals("00B4", getHexValue("acute", fomParserOptions));
        assertEquals("00B5", getHexValue("micro", fomParserOptions));
        assertEquals("00B6", getHexValue("para", fomParserOptions));
        assertEquals("00B7", getHexValue("middot", fomParserOptions));
        assertEquals("00B8", getHexValue("cedil", fomParserOptions));
        assertEquals("00B9", getHexValue("sup1", fomParserOptions));
        assertEquals("00BA", getHexValue("ordm", fomParserOptions));
        assertEquals("00BB", getHexValue("raquo", fomParserOptions));
        assertEquals("00BC", getHexValue("frac14", fomParserOptions));
        assertEquals("00BD", getHexValue("frac12", fomParserOptions));
        assertEquals("00BE", getHexValue("frac34", fomParserOptions));
        assertEquals("00BF", getHexValue("iquest", fomParserOptions));
        assertEquals("00C0", getHexValue("Agrave", fomParserOptions));
        assertEquals("00C1", getHexValue("Aacute", fomParserOptions));
        assertEquals("00C2", getHexValue("Acirc", fomParserOptions));
        assertEquals("00C3", getHexValue("Atilde", fomParserOptions));
        assertEquals("00C4", getHexValue("Auml", fomParserOptions));
        assertEquals("00C5", getHexValue("Aring", fomParserOptions));
        assertEquals("00C6", getHexValue("AElig", fomParserOptions));
        assertEquals("00C7", getHexValue("Ccedil", fomParserOptions));
        assertEquals("00C8", getHexValue("Egrave", fomParserOptions));
        assertEquals("00C9", getHexValue("Eacute", fomParserOptions));
        assertEquals("00CA", getHexValue("Ecirc", fomParserOptions));
        assertEquals("00CB", getHexValue("Euml", fomParserOptions));
        assertEquals("00CC", getHexValue("Igrave", fomParserOptions));
        assertEquals("00CD", getHexValue("Iacute", fomParserOptions));
        assertEquals("00CE", getHexValue("Icirc", fomParserOptions));
        assertEquals("00CF", getHexValue("Iuml", fomParserOptions));
        assertEquals("00D0", getHexValue("ETH", fomParserOptions));
        assertEquals("00D1", getHexValue("Ntilde", fomParserOptions));
        assertEquals("00D2", getHexValue("Ograve", fomParserOptions));
        assertEquals("00D3", getHexValue("Oacute", fomParserOptions));
        assertEquals("00D4", getHexValue("Ocirc", fomParserOptions));
        assertEquals("00D5", getHexValue("Otilde", fomParserOptions));
        assertEquals("00D6", getHexValue("Ouml", fomParserOptions));
        assertEquals("00D7", getHexValue("times", fomParserOptions));
        assertEquals("00D8", getHexValue("Oslash", fomParserOptions));
        assertEquals("00D9", getHexValue("Ugrave", fomParserOptions));
        assertEquals("00DA", getHexValue("Uacute", fomParserOptions));
        assertEquals("00DB", getHexValue("Ucirc", fomParserOptions));
        assertEquals("00DC", getHexValue("Uuml", fomParserOptions));
        assertEquals("00DD", getHexValue("Yacute", fomParserOptions));
        assertEquals("00DE", getHexValue("THORN", fomParserOptions));
        assertEquals("00DF", getHexValue("szlig", fomParserOptions));
        assertEquals("00E0", getHexValue("agrave", fomParserOptions));
        assertEquals("00E1", getHexValue("aacute", fomParserOptions));
        assertEquals("00E2", getHexValue("acirc", fomParserOptions));
        assertEquals("00E3", getHexValue("atilde", fomParserOptions));
        assertEquals("00E4", getHexValue("auml", fomParserOptions));
        assertEquals("00E5", getHexValue("aring", fomParserOptions));
        assertEquals("00E6", getHexValue("aelig", fomParserOptions));
        assertEquals("00E7", getHexValue("ccedil", fomParserOptions));
        assertEquals("00E8", getHexValue("egrave", fomParserOptions));
        assertEquals("00E9", getHexValue("eacute", fomParserOptions));
        assertEquals("00EA", getHexValue("ecirc", fomParserOptions));
        assertEquals("00EB", getHexValue("euml", fomParserOptions));
        assertEquals("00EC", getHexValue("igrave", fomParserOptions));
        assertEquals("00ED", getHexValue("iacute", fomParserOptions));
        assertEquals("00EE", getHexValue("icirc", fomParserOptions));
        assertEquals("00EF", getHexValue("iuml", fomParserOptions));
        assertEquals("00F0", getHexValue("eth", fomParserOptions));
        assertEquals("00F1", getHexValue("ntilde", fomParserOptions));
        assertEquals("00F2", getHexValue("ograve", fomParserOptions));
        assertEquals("00F3", getHexValue("oacute", fomParserOptions));
        assertEquals("00F4", getHexValue("ocirc", fomParserOptions));
        assertEquals("00F5", getHexValue("otilde", fomParserOptions));
        assertEquals("00F6", getHexValue("ouml", fomParserOptions));
        assertEquals("00F7", getHexValue("divide", fomParserOptions));
        assertEquals("00F8", getHexValue("oslash", fomParserOptions));
        assertEquals("00F9", getHexValue("ugrave", fomParserOptions));
        assertEquals("00FA", getHexValue("uacute", fomParserOptions));
        assertEquals("00FB", getHexValue("ucirc", fomParserOptions));
        assertEquals("00FC", getHexValue("uuml", fomParserOptions));
        assertEquals("00FD", getHexValue("yacute", fomParserOptions));
        assertEquals("00FE", getHexValue("thorn", fomParserOptions));
        assertEquals("00FF", getHexValue("yuml", fomParserOptions));
        assertEquals("0192", getHexValue("fnof", fomParserOptions));
        assertEquals("0391", getHexValue("Alpha", fomParserOptions));
        assertEquals("0392", getHexValue("Beta", fomParserOptions));
        assertEquals("0393", getHexValue("Gamma", fomParserOptions));
        assertEquals("0394", getHexValue("Delta", fomParserOptions));
        assertEquals("0395", getHexValue("Epsilon", fomParserOptions));
        assertEquals("0396", getHexValue("Zeta", fomParserOptions));
        assertEquals("0397", getHexValue("Eta", fomParserOptions));
        assertEquals("0398", getHexValue("Theta", fomParserOptions));
        assertEquals("0399", getHexValue("Iota", fomParserOptions));
        assertEquals("039A", getHexValue("Kappa", fomParserOptions));
        assertEquals("039B", getHexValue("Lambda", fomParserOptions));
        assertEquals("039C", getHexValue("Mu", fomParserOptions));
        assertEquals("039D", getHexValue("Nu", fomParserOptions));
        assertEquals("039E", getHexValue("Xi", fomParserOptions));
        assertEquals("039F", getHexValue("Omicron", fomParserOptions));
        assertEquals("03A0", getHexValue("Pi", fomParserOptions));
        assertEquals("03A1", getHexValue("Rho", fomParserOptions));
        assertEquals("03A3", getHexValue("Sigma", fomParserOptions));
        assertEquals("03A4", getHexValue("Tau", fomParserOptions));
        assertEquals("03A5", getHexValue("Upsilon", fomParserOptions));
        assertEquals("03A6", getHexValue("Phi", fomParserOptions));
        assertEquals("03A7", getHexValue("Chi", fomParserOptions));
        assertEquals("03A8", getHexValue("Psi", fomParserOptions));
        assertEquals("03A9", getHexValue("Omega", fomParserOptions));
        assertEquals("03B1", getHexValue("alpha", fomParserOptions));
        assertEquals("03B2", getHexValue("beta", fomParserOptions));
        assertEquals("03B3", getHexValue("gamma", fomParserOptions));
        assertEquals("03B4", getHexValue("delta", fomParserOptions));
        assertEquals("03B5", getHexValue("epsilon", fomParserOptions));
        assertEquals("03B6", getHexValue("zeta", fomParserOptions));
        assertEquals("03B7", getHexValue("eta", fomParserOptions));
        assertEquals("03B8", getHexValue("theta", fomParserOptions));
        assertEquals("03B9", getHexValue("iota", fomParserOptions));
        assertEquals("03BA", getHexValue("kappa", fomParserOptions));
        assertEquals("03BB", getHexValue("lambda", fomParserOptions));
        assertEquals("03BC", getHexValue("mu", fomParserOptions));
        assertEquals("03BD", getHexValue("nu", fomParserOptions));
        assertEquals("03BE", getHexValue("xi", fomParserOptions));
        assertEquals("03BF", getHexValue("omicron", fomParserOptions));
        assertEquals("03C0", getHexValue("pi", fomParserOptions));
        assertEquals("03C1", getHexValue("rho", fomParserOptions));
        assertEquals("03C2", getHexValue("sigmaf", fomParserOptions));
        assertEquals("03C3", getHexValue("sigma", fomParserOptions));
        assertEquals("03C4", getHexValue("tau", fomParserOptions));
        assertEquals("03C5", getHexValue("upsilon", fomParserOptions));
        assertEquals("03C6", getHexValue("phi", fomParserOptions));
        assertEquals("03C7", getHexValue("chi", fomParserOptions));
        assertEquals("03C8", getHexValue("psi", fomParserOptions));
        assertEquals("03C9", getHexValue("omega", fomParserOptions));
        assertEquals("03D1", getHexValue("thetasym", fomParserOptions));
        assertEquals("03D2", getHexValue("upsih", fomParserOptions));
        assertEquals("03D6", getHexValue("piv", fomParserOptions));
        assertEquals("2022", getHexValue("bull", fomParserOptions));
        assertEquals("2026", getHexValue("hellip", fomParserOptions));
        assertEquals("2032", getHexValue("prime", fomParserOptions));
        assertEquals("2033", getHexValue("Prime", fomParserOptions));
        assertEquals("203E", getHexValue("oline", fomParserOptions));
        assertEquals("2044", getHexValue("frasl", fomParserOptions));
        assertEquals("2118", getHexValue("weierp", fomParserOptions));
        assertEquals("2111", getHexValue("image", fomParserOptions));
        assertEquals("211C", getHexValue("real", fomParserOptions));
        assertEquals("2122", getHexValue("trade", fomParserOptions));
        assertEquals("2135", getHexValue("alefsym", fomParserOptions));
        assertEquals("2190", getHexValue("larr", fomParserOptions));
        assertEquals("2191", getHexValue("uarr", fomParserOptions));
        assertEquals("2192", getHexValue("rarr", fomParserOptions));
        assertEquals("2193", getHexValue("darr", fomParserOptions));
        assertEquals("2194", getHexValue("harr", fomParserOptions));
        assertEquals("21B5", getHexValue("crarr", fomParserOptions));
        assertEquals("21D0", getHexValue("lArr", fomParserOptions));
        assertEquals("21D1", getHexValue("uArr", fomParserOptions));
        assertEquals("21D2", getHexValue("rArr", fomParserOptions));
        assertEquals("21D3", getHexValue("dArr", fomParserOptions));
        assertEquals("21D4", getHexValue("hArr", fomParserOptions));
        assertEquals("2200", getHexValue("forall", fomParserOptions));
        assertEquals("2202", getHexValue("part", fomParserOptions));
        assertEquals("2203", getHexValue("exist", fomParserOptions));
        assertEquals("2205", getHexValue("empty", fomParserOptions));
        assertEquals("2207", getHexValue("nabla", fomParserOptions));
        assertEquals("2208", getHexValue("isin", fomParserOptions));
        assertEquals("2209", getHexValue("notin", fomParserOptions));
        assertEquals("220B", getHexValue("ni", fomParserOptions));
        assertEquals("220F", getHexValue("prod", fomParserOptions));
        assertEquals("2211", getHexValue("sum", fomParserOptions));
        assertEquals("2212", getHexValue("minus", fomParserOptions));
        assertEquals("2217", getHexValue("lowast", fomParserOptions));
        assertEquals("221A", getHexValue("radic", fomParserOptions));
        assertEquals("221D", getHexValue("prop", fomParserOptions));
        assertEquals("221E", getHexValue("infin", fomParserOptions));
        assertEquals("2220", getHexValue("ang", fomParserOptions));
        assertEquals("2227", getHexValue("and", fomParserOptions));
        assertEquals("2228", getHexValue("or", fomParserOptions));
        assertEquals("2229", getHexValue("cap", fomParserOptions));
        assertEquals("222A", getHexValue("cup", fomParserOptions));
        assertEquals("222B", getHexValue("int", fomParserOptions));
        assertEquals("2234", getHexValue("there4", fomParserOptions));
        assertEquals("223C", getHexValue("sim", fomParserOptions));
        assertEquals("2245", getHexValue("cong", fomParserOptions));
        assertEquals("2248", getHexValue("asymp", fomParserOptions));
        assertEquals("2260", getHexValue("ne", fomParserOptions));
        assertEquals("2261", getHexValue("equiv", fomParserOptions));
        assertEquals("2264", getHexValue("le", fomParserOptions));
        assertEquals("2265", getHexValue("ge", fomParserOptions));
        assertEquals("2282", getHexValue("sub", fomParserOptions));
        assertEquals("2283", getHexValue("sup", fomParserOptions));
        assertEquals("2284", getHexValue("nsub", fomParserOptions));
        assertEquals("2286", getHexValue("sube", fomParserOptions));
        assertEquals("2287", getHexValue("supe", fomParserOptions));
        assertEquals("2295", getHexValue("oplus", fomParserOptions));
        assertEquals("2297", getHexValue("otimes", fomParserOptions));
        assertEquals("22A5", getHexValue("perp", fomParserOptions));
        assertEquals("22C5", getHexValue("sdot", fomParserOptions));
        assertEquals("2308", getHexValue("lceil", fomParserOptions));
        assertEquals("2309", getHexValue("rceil", fomParserOptions));
        assertEquals("230A", getHexValue("lfloor", fomParserOptions));
        assertEquals("230B", getHexValue("rfloor", fomParserOptions));
        assertEquals("2329", getHexValue("lang", fomParserOptions));
        assertEquals("232A", getHexValue("rang", fomParserOptions));
        assertEquals("25CA", getHexValue("loz", fomParserOptions));
        assertEquals("2660", getHexValue("spades", fomParserOptions));
        assertEquals("2663", getHexValue("clubs", fomParserOptions));
        assertEquals("2665", getHexValue("hearts", fomParserOptions));
        assertEquals("2666", getHexValue("diams", fomParserOptions));
        assertEquals("0022", getHexValue("quot", fomParserOptions));
        assertEquals("0026", getHexValue("amp", fomParserOptions));
        assertEquals("003C", getHexValue("lt", fomParserOptions));
        assertEquals("003E", getHexValue("gt", fomParserOptions));
        assertEquals("0152", getHexValue("OElig", fomParserOptions));
        assertEquals("0153", getHexValue("oelig", fomParserOptions));
        assertEquals("0160", getHexValue("Scaron", fomParserOptions));
        assertEquals("0161", getHexValue("scaron", fomParserOptions));
        assertEquals("0178", getHexValue("Yuml", fomParserOptions));
        assertEquals("02C6", getHexValue("circ", fomParserOptions));
        assertEquals("02DC", getHexValue("tilde", fomParserOptions));
        assertEquals("2002", getHexValue("ensp", fomParserOptions));
        assertEquals("2003", getHexValue("emsp", fomParserOptions));
        assertEquals("2009", getHexValue("thinsp", fomParserOptions));
        assertEquals("200C", getHexValue("zwnj", fomParserOptions));
        assertEquals("200D", getHexValue("zwj", fomParserOptions));
        assertEquals("200E", getHexValue("lrm", fomParserOptions));
        assertEquals("200F", getHexValue("rlm", fomParserOptions));
        assertEquals("2013", getHexValue("ndash", fomParserOptions));
        assertEquals("2014", getHexValue("mdash", fomParserOptions));
        assertEquals("2018", getHexValue("lsquo", fomParserOptions));
        assertEquals("2019", getHexValue("rsquo", fomParserOptions));
        assertEquals("201A", getHexValue("sbquo", fomParserOptions));
        assertEquals("201C", getHexValue("ldquo", fomParserOptions));
        assertEquals("201D", getHexValue("rdquo", fomParserOptions));
        assertEquals("201E", getHexValue("bdquo", fomParserOptions));
        assertEquals("2020", getHexValue("dagger", fomParserOptions));
        assertEquals("2021", getHexValue("Dagger", fomParserOptions));
        assertEquals("2030", getHexValue("permil", fomParserOptions));
        assertEquals("20AC", getHexValue("euro", fomParserOptions));
    }

    private String getHexValue(String entity, AbstractParserOptions fomParserOptions) {
        String hexValue = fomParserOptions.resolveEntity(entity);
        char hexChar = hexValue.charAt(0);
        StringBuilder result = new StringBuilder(Integer.toHexString(hexChar));
        if (result.length() == 2) {
            result.insert(0, "00");
        } else if (result.length() == 3) {
            result.insert(0, "0");
        }
        return result.toString().toUpperCase();
    }

}
