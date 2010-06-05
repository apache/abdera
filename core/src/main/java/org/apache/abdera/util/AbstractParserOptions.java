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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.i18n.text.io.CompressionUtil.CompressionCodec;
import org.apache.abdera.parser.ParserOptions;

/**
 * Non thread-safe abstract implementation of ParserOptions
 */
public abstract class AbstractParserOptions implements ParserOptions, Cloneable {

    protected Factory factory = null;
    protected String charset = null;
    protected ParseFilter parseFilter = null;
    protected boolean detect = false;
    protected boolean preserve = true;
    protected boolean filterreserved = false;
    protected char replacement = 0;
    protected CompressionCodec[] codecs = null;
    protected boolean resolveentities = true;
    protected Map<String, String> entities = new HashMap<String, String>();

    protected boolean qnamealiasing = false;
    protected Map<QName, QName> aliases = null;

    protected abstract void initFactory();

    protected abstract void checkFactory(Factory factory);

    protected AbstractParserOptions() {
        initDefaultEntities();
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractParserOptions copy = (AbstractParserOptions)super.clone();

        // Object's clone implementation takes care of the rest, we just need
        // deep copies of the two filters, in case they're carrying around some
        // state with them.

        if (parseFilter != null)
            copy.parseFilter = (ParseFilter)parseFilter.clone();

        return copy;
    }

    public Factory getFactory() {
        if (factory == null)
            initFactory();
        return factory;
    }

    public ParserOptions setFactory(Factory factory) {
        checkFactory(factory);
        this.factory = factory;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public ParserOptions setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public ParseFilter getParseFilter() {
        return parseFilter;
    }

    public ParserOptions setParseFilter(ParseFilter parseFilter) {
        this.parseFilter = parseFilter;
        return this;
    }

    public boolean getAutodetectCharset() {
        return this.detect;
    }

    public ParserOptions setAutodetectCharset(boolean detect) {
        this.detect = detect;
        return this;
    }

    public boolean getMustPreserveWhitespace() {
        return preserve;
    }

    public ParserOptions setMustPreserveWhitespace(boolean preserve) {
        this.preserve = preserve;
        return this;
    }

    public boolean getFilterRestrictedCharacters() {
        return filterreserved;
    }

    public ParserOptions setFilterRestrictedCharacters(boolean filter) {
        this.filterreserved = filter;
        return this;
    }

    public char getFilterRestrictedCharacterReplacement() {
        return replacement;
    }

    public ParserOptions setFilterRestrictedCharacterReplacement(char replacement) {
        this.replacement = replacement;
        return this;
    }

    public CompressionCodec[] getCompressionCodecs() {
        return codecs;
    }

    public ParserOptions setCompressionCodecs(CompressionCodec... codecs) {
        this.codecs = codecs;
        return this;
    }

    public ParserOptions registerEntity(String name, String value) {
        entities.put(name, value);
        return this;
    }

    private void initDefaultEntities() {
        registerEntity("quot", "\"");
        registerEntity("amp", "\u0026");
        registerEntity("lt", "\u003C");
        registerEntity("gt", "\u003E");
        registerEntity("nbsp", " ");
        registerEntity("iexcl", "\u00A1");
        registerEntity("cent", "\u00A2");
        registerEntity("pound", "\u00A3");
        registerEntity("curren", "\u00A4");
        registerEntity("yen", "\u00A5");
        registerEntity("brvbar", "\u00A6");
        registerEntity("sect", "\u00A7");
        registerEntity("uml", "\u00A8");
        registerEntity("copy", "\u00A9");
        registerEntity("ordf", "\u00AA");
        registerEntity("laquo", "\u00AB");
        registerEntity("not", "\u00AC");
        registerEntity("shy", "\u00AD");
        registerEntity("reg", "\u00AE");
        registerEntity("macr", "\u00AF");
        registerEntity("deg", "\u00B0");
        registerEntity("plusmn", "\u00B1");
        registerEntity("sup2", "\u00B2");
        registerEntity("sup3", "\u00B3");
        registerEntity("acute", "\u00B4");
        registerEntity("micro", "\u00B5");
        registerEntity("para", "\u00B6");
        registerEntity("middot", "\u00B7");
        registerEntity("cedil", "\u00B8");
        registerEntity("sup1", "\u00B9");
        registerEntity("ordm", "\u00BA");
        registerEntity("raquo", "\u00BB");
        registerEntity("frac14", "\u00BC");
        registerEntity("frac12", "\u00BD");
        registerEntity("frac34", "\u00BE");
        registerEntity("iquest", "\u00BF");
        registerEntity("Agrave", "\u00C0");
        registerEntity("Aacute", "\u00C1");
        registerEntity("Acirc", "\u00C2");
        registerEntity("Atilde", "\u00C3");
        registerEntity("Auml", "\u00C4");
        registerEntity("Aring", "\u00C5");
        registerEntity("AElig", "\u00C6");
        registerEntity("Ccedil", "\u00C7");
        registerEntity("Egrave", "\u00C8");
        registerEntity("Eacute", "\u00C9");
        registerEntity("Ecirc", "\u00CA");
        registerEntity("Euml", "\u00CB");
        registerEntity("Igrave", "\u00CC");
        registerEntity("Iacute", "\u00CD");
        registerEntity("Icirc", "\u00CE");
        registerEntity("Iuml", "\u00CF");
        registerEntity("ETH", "\u00D0");
        registerEntity("Ntilde", "\u00D1");
        registerEntity("Ograve", "\u00D2");
        registerEntity("Oacute", "\u00D3");
        registerEntity("Ocirc", "\u00D4");
        registerEntity("Otilde", "\u00D5");
        registerEntity("Ouml", "\u00D6");
        registerEntity("times", "\u00D7");
        registerEntity("Oslash", "\u00D8");
        registerEntity("Ugrave", "\u00D9");
        registerEntity("Uacute", "\u00DA");
        registerEntity("Ucirc", "\u00DB");
        registerEntity("Uuml", "\u00DC");
        registerEntity("Yacute", "\u00DD");
        registerEntity("THORN", "\u00DE");
        registerEntity("szlig", "\u00DF");
        registerEntity("agrave", "\u00E0");
        registerEntity("aacute", "\u00E1");
        registerEntity("acirc", "\u00E2");
        registerEntity("atilde", "\u00E3");
        registerEntity("auml", "\u00E4");
        registerEntity("aring", "\u00E5");
        registerEntity("aelig", "\u00E6");
        registerEntity("ccedil", "\u00E7");
        registerEntity("egrave", "\u00E8");
        registerEntity("eacute", "\u00E9");
        registerEntity("ecirc", "\u00EA");
        registerEntity("euml", "\u00EB");
        registerEntity("igrave", "\u00EC");
        registerEntity("iacute", "\u00ED");
        registerEntity("icirc", "\u00EE");
        registerEntity("iuml", "\u00EF");
        registerEntity("eth", "\u00F0");
        registerEntity("ntilde", "\u00F1");
        registerEntity("ograve", "\u00F2");
        registerEntity("oacute", "\u00F3");
        registerEntity("ocirc", "\u00F4");
        registerEntity("otilde", "\u00F5");
        registerEntity("ouml", "\u00F6");
        registerEntity("divide", "\u00F7");
        registerEntity("oslash", "\u00F8");
        registerEntity("ugrave", "\u00F9");
        registerEntity("uacute", "\u00FA");
        registerEntity("ucirc", "\u00FB");
        registerEntity("uuml", "\u00FC");
        registerEntity("yacute", "\u00FD");
        registerEntity("thorn", "\u00FE");
        registerEntity("yuml", "\u00FF");
        registerEntity("OElig", "\u0152");
        registerEntity("oelig", "\u0153");
        registerEntity("Scaron", "\u0160");
        registerEntity("scaron", "\u0161");
        registerEntity("Yuml", "\u0178");
        registerEntity("fnof", "\u0192");
        registerEntity("circ", "\u02C6");
        registerEntity("tilde", "\u02DC");
        registerEntity("Alpha", "\u0391");
        registerEntity("Beta", "\u0392");
        registerEntity("Gamma", "\u0393");
        registerEntity("Delta", "\u0394");
        registerEntity("Epsilon", "\u0395");
        registerEntity("Zeta", "\u0396");
        registerEntity("Eta", "\u0397");
        registerEntity("Theta", "\u0398");
        registerEntity("Iota", "\u0399");
        registerEntity("Kappa", "\u039A");
        registerEntity("Lambda", "\u039B");
        registerEntity("Mu", "\u039C");
        registerEntity("Nu", "\u039D");
        registerEntity("Xi", "\u039E");
        registerEntity("Omicron", "\u039F");
        registerEntity("Pi", "\u03A0");
        registerEntity("Rho", "\u03A1");
        registerEntity("Sigma", "\u03A3");
        registerEntity("Tau", "\u03A4");
        registerEntity("Upsilon", "\u03A5");
        registerEntity("Phi", "\u03A6");
        registerEntity("Chi", "\u03A7");
        registerEntity("Psi", "\u03A8");
        registerEntity("Omega", "\u03A9");
        registerEntity("alpha", "\u03B1");
        registerEntity("beta", "\u03B2");
        registerEntity("gamma", "\u03B3");
        registerEntity("delta", "\u03B4");
        registerEntity("epsilon", "\u03B5");
        registerEntity("zeta", "\u03B6");
        registerEntity("eta", "\u03B7");
        registerEntity("theta", "\u03B8");
        registerEntity("iota", "\u03B9");
        registerEntity("kappa", "\u03BA");
        registerEntity("lambda", "\u03BB");
        registerEntity("mu", "\u03BC");
        registerEntity("nu", "\u03BD");
        registerEntity("xi", "\u03BE");
        registerEntity("omicron", "\u03BF");
        registerEntity("pi", "\u03C0");
        registerEntity("rho", "\u03C1");
        registerEntity("sigmaf", "\u03C2");
        registerEntity("sigma", "\u03C3");
        registerEntity("tau", "\u03C4");
        registerEntity("upsilon", "\u03C5");
        registerEntity("phi", "\u03C6");
        registerEntity("chi", "\u03C7");
        registerEntity("psi", "\u03C8");
        registerEntity("omega", "\u03C9");
        registerEntity("thetasym", "\u03D1");
        registerEntity("upsih", "\u03D2");
        registerEntity("piv", "\u03D6");
        registerEntity("ensp", "\u2002");
        registerEntity("emsp", "\u2003");
        registerEntity("thinsp", "\u2009");
        registerEntity("zwnj", "\u200C");
        registerEntity("zwj", "\u200D");
        registerEntity("lrm", "\u200E");
        registerEntity("rlm", "\u200F");
        registerEntity("ndash", "\u2013");
        registerEntity("mdash", "\u2014");
        registerEntity("lsquo", "\u2018");
        registerEntity("rsquo", "\u2019");
        registerEntity("sbquo", "\u201A");
        registerEntity("ldquo", "\u201C");
        registerEntity("rdquo", "\u201D");
        registerEntity("bdquo", "\u201E");
        registerEntity("dagger", "\u2020");
        registerEntity("Dagger", "\u2021");
        registerEntity("bull", "\u2022");
        registerEntity("hellip", "\u2026");
        registerEntity("permil", "\u2030");
        registerEntity("prime", "\u2032");
        registerEntity("Prime", "\u2033");
        registerEntity("lsaquo", "\u2039");
        registerEntity("rsaquo", "\u203A");
        registerEntity("oline", "\u203E");
        registerEntity("frasl", "\u2044");
        registerEntity("euro", "\u20AC");
        registerEntity("image", "\u2111");
        registerEntity("weierp", "\u2118");
        registerEntity("real", "\u211C");
        registerEntity("trade", "\u2122");
        registerEntity("alefsym", "\u2135");
        registerEntity("larr", "\u2190");
        registerEntity("uarr", "\u2191");
        registerEntity("rarr", "\u2192");
        registerEntity("darr", "\u2193");
        registerEntity("harr", "\u2194");
        registerEntity("crarr", "\u21B5");
        registerEntity("lArr", "\u21D0");
        registerEntity("uArr", "\u21D1");
        registerEntity("rArr", "\u21D2");
        registerEntity("dArr", "\u21D3");
        registerEntity("hArr", "\u21D4");
        registerEntity("forall", "\u2200");
        registerEntity("part", "\u2202");
        registerEntity("exist", "\u2203");
        registerEntity("empty", "\u2205");
        registerEntity("nabla", "\u2207");
        registerEntity("isin", "\u2208");
        registerEntity("notin", "\u2209");
        registerEntity("ni", "\u220B");
        registerEntity("prod", "\u220F");
        registerEntity("sum", "\u2211");
        registerEntity("minus", "\u2212");
        registerEntity("lowast", "\u2217");
        registerEntity("radic", "\u221A");
        registerEntity("prop", "\u221D");
        registerEntity("infin", "\u221E");
        registerEntity("ang", "\u2220");
        registerEntity("and", "\u2227");
        registerEntity("or", "\u2228");
        registerEntity("cap", "\u2229");
        registerEntity("cup", "\u222A");
        registerEntity("int", "\u222B");
        registerEntity("there4", "\u2234");
        registerEntity("sim", "\u223C");
        registerEntity("cong", "\u2245");
        registerEntity("asymp", "\u2248");
        registerEntity("ne", "\u2260");
        registerEntity("equiv", "\u2261");
        registerEntity("le", "\u2264");
        registerEntity("ge", "\u2265");
        registerEntity("sub", "\u2282");
        registerEntity("sup", "\u2283");
        registerEntity("nsub", "\u2284");
        registerEntity("sube", "\u2286");
        registerEntity("supe", "\u2287");
        registerEntity("oplus", "\u2295");
        registerEntity("otimes", "\u2297");
        registerEntity("perp", "\u22A5");
        registerEntity("sdot", "\u22C5");
        registerEntity("lceil", "\u2308");
        registerEntity("rceil", "\u2309");
        registerEntity("lfloor", "\u230A");
        registerEntity("rfloor", "\u230B");
        registerEntity("lang", "\u2329");
        registerEntity("rang", "\u232A");
        registerEntity("loz", "\u25CA");
        registerEntity("spades", "\u2660");
        registerEntity("clubs", "\u2663");
        registerEntity("hearts", "\u2665");
        registerEntity("diams", "\u2666");
    }

    public String resolveEntity(String name) {
        return resolveentities ? entities.get(name) : null;
    }

    public ParserOptions setResolveEntities(boolean resolve) {
        this.resolveentities = resolve;
        return this;
    }

    public boolean getResolveEntities() {
        return this.resolveentities;
    }

    public Map<QName, QName> getQNameAliasMap() {
        return aliases;
    }

    public ParserOptions setQNameAliasMap(Map<QName, QName> map) {
        this.aliases = map;
        return this;
    }

    public boolean isQNameAliasMappingEnabled() {
        return qnamealiasing;
    }

    public ParserOptions setQNameAliasMappingEnabled(boolean enabled) {
        this.qnamealiasing = enabled;
        return this;
    }
}
