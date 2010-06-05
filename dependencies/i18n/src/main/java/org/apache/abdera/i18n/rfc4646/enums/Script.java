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
package org.apache.abdera.i18n.rfc4646.enums;

import java.util.Locale;

import org.apache.abdera.i18n.rfc4646.Subtag;

/**
 * Enum constants used to validate language tags
 */
public enum Script {

    ARAB(null, null, "Arabic"), ARMI(null, null, "Imperial Aramaic"), ARMN(null, null, "Armenian"), AVST(null, null,
        "Avestan"), BALI(null, null, "Balinese"), BATK(null, null, "Batak"), BENG(null, null, "Bengali"), BLIS(null,
        null, "Blissymbols"), BOPO(null, null, "Bopomofo"), BRAH(null, null, "Brahmi"), BRAI(null, null, "Braille"), BUGI(
        null, null, "Buginese"), BUHD(null, null, "Buhid"), CAKM(null, null, "Chakma"), CANS(null, null,
        "Unified Canadian Aboriginal Syllabics"), CARI(null, null, "Carian"), CHAM(null, null, "Cham"), CHER(null,
        null, "Cherokee"), CIRT(null, null, "Cirth"), COPT(null, null, "Coptic"), CPRT(null, null, "Cypriot"), CYRL(
        null, null, "Cyrillic"), CYRS(null, null, "Cyrillic (Old Church Slavonic variant)"), DEVA(null, null,
        "Devanagari (Nagari)"), DSRT(null, null, "Deseret (Mormon)"), EGYD(null, null, "Egyptian demotic"), EGYH(null,
        null, "Egyptian hieratic"), EGYP(null, null, "Egyptian hieroglyphs"), ETHI(null, null,
        "Ethiopic (Ge&#x2BB;ez)", "Ethiopic (Ge'ez)"), GEOK(null, null, "Khutsuri (Asomtavruli and Nuskhuri)"), GEOR(
        null, null, "Georgian (Mkhedruli)"), GLAG(null, null, "Glagolitic"), GOTH(null, null, "Gothic"), GREK(null,
        null, "Greek"), GUJR(null, null, "Gujarati"), GURU(null, null, "Gurmukhi"), HANG(null, null,
        "Hangul (Hang&#x16D;l, Hangeul)"), HANI(null, null, "Han (Hanzi, Kanji, Hanja)"), HANO(null, null,
        "Hanunoo (Hanun&#xF3;o)"), HANS(null, null, "Han (Simplified variant)"), HANT(null, null,
        "Han (Traditional variant)"), HEBR(null, null, "Hebrew"), HIRA(null, null, "Hiragana"), HMNG(null, null,
        "Pahawh Hmong"), HRKT(null, null, "(alias for Hiragana + Katakana)"), HUNG(null, null, "Old Hungarian"), INDS(
        null, null, "Indus (Harappan)"), ITAL(null, null, "Old Italic (Etruscan, Oscan, etc.)"), JAVA(null, null,
        "Javanese"), JPAN(null, null, "Japanese (alias for Han + Hiragana + Katakana)"), KALI(null, null, "Kayah Li"), KANA(
        null, null, "Katakana"), KHAR(null, null, "Kharoshthi"), KHMR(null, null, "Khmer"), KNDA(null, null, "Kannada"), KORE(
        null, null, "Korean (alias for Hangul + Han)"), KTHI(null, null, "Kaithi"), LANA(null, null, "Lanna",
        "Tai Tham"), LAOO(null, null, "Lao"), LATF(null, null, "Latin (Fraktur variant)"), LATG(null, null,
        "Latin (Gaelic variant)"), LATN(null, null, "Latin"), LEPC(null, null, "Lepcha (R&#xF3;ng)"), LIMB(null, null,
        "Limbu"), LINA(null, null, "Linear A"), LINB(null, null, "Linear B"), LYCI(null, null, "Lycian"), LYDI(null,
        null, "Lydian"), MAND(null, null, "Mandaic", "Mandaean"), MANI(null, null, "Manichaean"), MAYA(null, null,
        "Mayan hieroglyphs"), MERO(null, null, "Meroitic"), MLYM(null, null, "Malayalam"), MONG(null, null, "Mongolian"), MOON(
        null, null, "Moon", "Moon code", "Moon script", "Moon type"), MTEI(null, null, "Meitei Mayek", "Meithei",
        "Meetei"), MYMR(null, null, "Myanmar (Burmese)"), NKOO(null, null, "N&#x2019;Ko"), OGAM(null, null, "Ogham"), OLCK(
        null, null, "Ol Chiki (Ol Cemet', Ol, Santali)"), ORKH(null, null, "Orkhon"), ORYA(null, null, "Oriya"), OSMA(
        null, null, "Osmanya"), PERM(null, null, "Old Permic"), PHAG(null, null, "Phags-pa"), PHLI(null, null,
        "Inscriptional Pahlavi"), PHLP(null, null, "Psalter Pahlavi"), PHLV(null, null, "Book Pahlavi"), PHNX(null,
        null, "Phoenician"), PLRD(null, null, "Pollard Phonetic"), PRTI(null, null, "Inscriptional Parthian"), QAAA(
        null, null, "PRIVATE USE"), QAAB(null, null, "PRIVATE USE"), QAAC(null, null, "PRIVATE USE"), QAAD(null, null,
        "PRIVATE USE"), QAAE(null, null, "PRIVATE USE"), QAAF(null, null, "PRIVATE USE"), QAAG(null, null,
        "PRIVATE USE"), QAAH(null, null, "PRIVATE USE"), QAAI(null, null, "PRIVATE USE"), QAAJ(null, null,
        "PRIVATE USE"), QAAK(null, null, "PRIVATE USE"), QAAL(null, null, "PRIVATE USE"), QAAM(null, null,
        "PRIVATE USE"), QAAN(null, null, "PRIVATE USE"), QAAO(null, null, "PRIVATE USE"), QAAP(null, null,
        "PRIVATE USE"), QAAQ(null, null, "PRIVATE USE"), QAAR(null, null, "PRIVATE USE"), QAAS(null, null,
        "PRIVATE USE"), QAAT(null, null, "PRIVATE USE"), QAAU(null, null, "PRIVATE USE"), QAAV(null, null,
        "PRIVATE USE"), QAAW(null, null, "PRIVATE USE"), QAAX(null, null, "PRIVATE USE"), QABA(null, null,
        "PRIVATE USE"), QABB(null, null, "PRIVATE USE"), QABC(null, null, "PRIVATE USE"), QABD(null, null,
        "PRIVATE USE"), QABE(null, null, "PRIVATE USE"), QABF(null, null, "PRIVATE USE"), QABG(null, null,
        "PRIVATE USE"), QABH(null, null, "PRIVATE USE"), QABI(null, null, "PRIVATE USE"), QABJ(null, null,
        "PRIVATE USE"), QABK(null, null, "PRIVATE USE"), QABL(null, null, "PRIVATE USE"), QABM(null, null,
        "PRIVATE USE"), QABN(null, null, "PRIVATE USE"), QABO(null, null, "PRIVATE USE"), QABP(null, null,
        "PRIVATE USE"), QABQ(null, null, "PRIVATE USE"), QABR(null, null, "PRIVATE USE"), QABS(null, null,
        "PRIVATE USE"), QABT(null, null, "PRIVATE USE"), QABU(null, null, "PRIVATE USE"), QABV(null, null,
        "PRIVATE USE"), QABW(null, null, "PRIVATE USE"), QABX(null, null, "PRIVATE USE"), RJNG(null, null, "Rejang",
        "Redjang", "Kaganga"), RORO(null, null, "Rongorongo"), RUNR(null, null, "Runic"), SAMR(null, null, "Samaritan"), SARA(
        null, null, "Sarati"), SAUR(null, null, "Saurashtra"), SGNW(null, null, "SignWriting"), SHAW(null, null,
        "Shavian (Shaw)"), SINH(null, null, "Sinhala"), SUND(null, null, "Sundanese"), SYLO(null, null, "Syloti Nagri"), SYRC(
        null, null, "Syriac"), SYRE(null, null, "Syriac (Estrangelo variant)"), SYRJ(null, null,
        "Syriac (Western variant)"), SYRN(null, null, "Syriac (Eastern variant)"), TAGB(null, null, "Tagbanwa"), TALE(
        null, null, "Tai Le"), TALU(null, null, "New Tai Lue"), TAML(null, null, "Tamil"), TAVT(null, null, "Tai Viet"), TELU(
        null, null, "Telugu"), TENG(null, null, "Tengwar"), TFNG(null, null, "Tifinagh (Berber)"), TGLG(null, null,
        "Tagalog"), THAA(null, null, "Thaana"), THAI(null, null, "Thai"), TIBT(null, null, "Tibetan"), UGAR(null, null,
        "Ugaritic"), VAII(null, null, "Vai"), VISP(null, null, "Visible Speech"), XPEO(null, null, "Old Persian"), XSUX(
        null, null, "Cuneiform, Sumero-Akkadian"), YIII(null, null, "Yi"), ZMTH(null, null, "Mathematical notation"), ZSYM(
        null, null, "Symbols"), ZXXX(null, null, "Code for unwritten documents"), ZYYY(null, null,
        "Code for undetermined script"), ZZZZ(null, null, "Code for uncoded script");

    private final String deprecated;
    private final String preferred;
    private final String[] descriptions;

    private Script(String dep, String pref, String... desc) {
        this.deprecated = dep;
        this.preferred = pref;
        this.descriptions = desc;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public boolean isDeprecated() {
        return deprecated != null;
    }

    public String getPreferredValue() {
        return preferred;
    }

    public Script getPreferred() {
        return preferred != null ? valueOf(preferred.toUpperCase(Locale.US)) : this;
    }

    public String getDescription() {
        return descriptions.length > 0 ? descriptions[0] : null;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public Subtag newSubtag() {
        return new Subtag(this);
    }

    public static Script valueOf(Subtag subtag) {
        if (subtag == null)
            return null;
        if (subtag.getType() == Subtag.Type.SCRIPT)
            return valueOf(subtag.getName().toUpperCase(Locale.US));
        else
            throw new IllegalArgumentException("Wrong subtag type");
    }

}
