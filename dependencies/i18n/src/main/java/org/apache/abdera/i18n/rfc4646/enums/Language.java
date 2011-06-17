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
public enum Language {
    // ISO 639-1
    AA(null, null, null, "Afar"), AB(null, null, null, "Abkhazian"), AE(null, null, null, "Avestan"), AF(null, null,
        null, "Afrikaans"), AK(null, null, null, "Akan"), AM(null, null, null, "Amharic"), AN(null, null, null,
        "Aragonese"), AR(null, null, null, "Arabic"), AS(null, null, null, "Assamese"), AV(null, null, null, "Avaric"), AY(
        null, null, null, "Aymara"), AZ(null, null, null, "Azerbaijani"), BA(null, null, null, "Bashkir"), BE(null,
        null, null, "Belarusian"), BG(null, null, null, "Bulgarian"), BH(null, null, null, "Bihari"), BI(null, null,
        null, "Bislama"), BM(null, null, null, "Bambara"), BN(null, null, null, "Bengali"), BO(null, null, null,
        "Tibetan"), BR(null, null, null, "Breton"), BS(null, null, null, "Bosnian"), CA(null, null, null, "Catalan",
        "Valencian"), CE(null, null, null, "Chechen"), CH(null, null, null, "Chamorro"), CO(null, null, null,
        "Corsican"), CR(null, null, null, "Cree"), CS(null, null, null, "Czech"), CU(null, null, null, "Church Slavic",
        "Old Slavonic", "Church Slavonic", "Old Bulgarian", "Old Church Slavonic"), CV(null, null, null, "Chuvash"), CY(
        null, null, null, "Welsh"), DA(null, null, null, "Danish"), DE(null, null, null, "German"), DV(null, null,
        null, "Divehi", "Dhivehi", "Maldivian"), DZ(null, null, null, "Dzongkha"), EE(null, null, null, "Ewe"), EL(
        null, null, null, "Greek, Modern (1453-)"), EN(null, null, null, "English"), EO(null, null, null, "Esperanto"), ES(
        null, null, null, "Spanish", "Castilian"), ET(null, null, null, "Estonian"), EU(null, null, null, "Basque"), FA(
        null, null, null, "Persian"), FF(null, null, null, "Fulah"), FI(null, null, null, "Finnish"), FJ(null, null,
        null, "Fijian"), FO(null, null, null, "Faroese"), FR(null, null, null, "French"), FY(null, null, null,
        "Western Frisian"), GA(null, null, null, "Irish"), GD(null, null, null, "Gaelic", "Scottish Gaelic"), GL(null,
        null, null, "Galician"), GN(null, null, null, "Guarani"), GU(null, null, null, "Gujarati"), GV(null, null,
        null, "Manx"), HA(null, null, null, "Hausa"), HE(null, null, null, "Hebrew"), HI(null, null, null, "Hindi"), HO(
        null, null, null, "Hiri Motu"), HR(null, null, null, "Croatian"), HT(null, null, null, "Haitian",
        "Haitian Creole"), HU(null, null, null, "Hungarian"), HY(null, null, null, "Armenian"), HZ(null, null, null,
        "Herero"), IA(null, null, null, "Interlingua (International Auxiliary Language Association)"), ID(null, null,
        null, "Indonesian"), IE(null, null, null, "Interlingue", "Occidental"), IG(null, null, null, "Igbo"), II(null,
        null, null, "Sichuan Yi", "Nuosu"), IK(null, null, null, "Inupiaq"), IN("1989-01-01", "id", null, "Indonesian"), IO(
        null, null, null, "Ido"), IS(null, null, null, "Icelandic"), IT(null, null, null, "Italian"), IU(null, null,
        null, "Inuktitut"), IW("1989-01-01", "he", null, "Hebrew"), JA(null, null, null, "Japanese"), JI("1989-01-01",
        "yi", null, "Yiddish"), JV(null, null, null, "Javanese"), JW("2001-08-13", "jv", null, "Javanese"), KA(null,
        null, null, "Georgian"), KG(null, null, null, "Kongo"), KI(null, null, null, "Kikuyu", "Gikuyu"), KJ(null,
        null, null, "Kuanyama", "Kwanyama"), KK(null, null, null, "Kazakh"), KL(null, null, null, "Kalaallisut",
        "Greenlandic"), KM(null, null, null, "Central Khmer"), KN(null, null, null, "Kannada"), KO(null, null, null,
        "Korean"), KR(null, null, null, "Kanuri"), KS(null, null, null, "Kashmiri"), KU(null, null, null, "Kurdish"), KV(
        null, null, null, "Komi"), KW(null, null, null, "Cornish"), KY(null, null, null, "Kyrgyz", "Kirghiz"), LA(null,
        null, null, "Latin"), LB(null, null, null, "Luxembourgish", "Letzeburgesch"), LG(null, null, null, "Ganda"), LI(
        null, null, null, "Limburgan", "Limburger", "Limburgish"), LN(null, null, null, "Lingala"), LO(null, null,
        null, "Lao"), LT(null, null, null, "Lithuanian"), LU(null, null, null, "Luba-Katanga"), LV(null, null, null,
        "Latvian"), MG(null, null, null, "Malagasy"), MH(null, null, null, "Marshallese"), MI(null, null, null, "Maori"), MK(
        null, null, null, "Macedonian"), ML(null, null, null, "Malayalam"), MN(null, null, null, "Mongolian"), MO(
        "2008-11-03", null, null, "Moldavian"), MR(null, null, null, "Marathi"), MS(null, null, null, "Malay"), MT(
        null, null, null, "Maltese"), MY(null, null, null, "Burmese"), NA(null, null, null, "Nauru"), NB(null, null,
        null, "Norwegian Bokm&#xE5;l"), ND(null, null, null, "Ndebele, North", "North Ndebele"), NE(null, null, null,
        "Nepali"), NG(null, null, null, "Ndonga"), NL(null, null, null, "Dutch", "Flemish"), NN(null, null, null,
        "Norwegian Nynorsk"), NO(null, null, null, "Norwegian"), NR(null, null, null, "Ndebele, South", "South Ndebele"), NV(
        null, null, null, "Navajo", "Navaho"), NY(null, null, null, "Chichewa", "Chewa", "Nyanja"), OC(null, null,
        null, "Occitan (post 1500)", "Proven&#xE7;al"), OJ(null, null, null, "Ojibwa"), OM(null, null, null, "Oromo"), OR(
        null, null, null, "Oriya"), OS(null, null, null, "Ossetian", "Ossetic"), PA(null, null, null, "Panjabi",
        "Punjabi"), PI(null, null, null, "Pali"), PL(null, null, null, "Polish"), PS(null, null, null, "Pushto"), PT(
        null, null, null, "Portuguese"), QU(null, null, null, "Quechua"), RM(null, null, null, "Romansh"), RN(null,
        null, null, "Rundi"), RO(null, null, null, "Romanian"), RU(null, null, null, "Russian"), RW(null, null, null,
        "Kinyarwanda"), SA(null, null, null, "Sanskrit"), SC(null, null, null, "Sardinian"), SD(null, null, null,
        "Sindhi"), SE(null, null, null, "Northern Sami"), SG(null, null, null, "Sango"), SH("2000-02-18", null, null,
        "Serbo-Croatian"), SI(null, null, null, "Sinhala", "Sinhalese"), SK(null, null, null, "Slovak"), SL(null, null,
        null, "Slovenian"), SM(null, null, null, "Samoan"), SN(null, null, null, "Shona"), SO(null, null, null,
        "Somali"), SQ(null, null, null, "Albanian"), SR(null, null, null, "Serbian"), SS(null, null, null, "Swati"), ST(
        null, null, null, "Sotho, Southern"), SU(null, null, null, "Sundanese"), SV(null, null, null, "Swedish"), SW(
        null, null, null, "Swahili"), TA(null, null, null, "Tamil"), TE(null, null, null, "Telugu"), TG(null, null,
        null, "Tajik"), TH(null, null, null, "Thai"), TI(null, null, null, "Tigrinya"), TK(null, null, null, "Turkmen"), TL(
        null, null, null, "Tagalog"), TN(null, null, null, "Tswana"), TO(null, null, null, "Tonga (Tonga Islands)"), TR(
        null, null, null, "Turkish"), TS(null, null, null, "Tsonga"), TT(null, null, null, "Tatar"), TW(null, null,
        null, "Twi"), TY(null, null, null, "Tahitian"), UG(null, null, null, "Uighur", "Uyghur"), UK(null, null, null,
        "Ukrainian"), UR(null, null, null, "Urdu"), UZ(null, null, null, "Uzbek"), VE(null, null, null, "Venda"), VI(
        null, null, null, "Vietnamese"), VO(null, null, null, "Volap&#xFC;k"), WA(null, null, null, "Walloon"), WO(
        null, null, null, "Wolof"), XH(null, null, null, "Xhosa"), YI(null, null, null, "Yiddish"), YO(null, null,
        null, "Yoruba"), ZA(null, null, null, "Zhuang", "Chuang"), ZH(null, null, null, "Chinese"), ZU(null, null,
        null, "Zulu"),
    // ISO 639-2
    // A
    AAR(null, null, null, "Afar"), ABK(null, null, null, "Abkhazian"), ACE(null, null, null, "Achinese"), AFR(null,
        null, null, "Afrikaans"), ANP(null, null, null, "Angika"), ACH(null, null, null, "Acoli"), ADA(null, null,
        null, "Adangme"), ADY(null, null, null, "Adyghe", "Adygei"), AFA(null, null, null, "Afro-Asiatic (Other)"), AFH(
        null, null, null, "Afrihili"), AIN(null, null, null, "Ainu"), AKA(null, null, null, "Akan"), AKK(null, null,
        null, "Akkadian"), ALB(null, null, null, "Albanian"), ALE(null, null, null, "Aleut"), ALG(null, null, null,
        "Algonquian languages"), ALT(null, null, null, "Southern Altai"), AMH(null, null, null, "Amharic"), ANG(null,
        null, null, "English, Old (ca. 450-1100)"), APA(null, null, null, "Apache languages"), ARA(null, null, null,
        "Arabic"), ARC(null, null, null, "Official Aramaic (700-300 BCE)", "Imperial Aramaic (700-300 BCE)"), ARG(null,
        null, null, "Aragonese"), ARM(null, null, null, "Armenian"), ARN(null, null, null, "Mapudungun", "Mapuche"), ARP(
        null, null, null, "Arapaho"), ART(null, null, null, "Artificial (Other)"), ARW(null, null, null, "Arawak"), ASM(
        null, null, null, "Assamese"), AST(null, null, null, "Asturian", "Bable", "Leonese", "Asturleonese"), ATH(null,
        null, null, "Athapascan languages"), AUS(null, null, null, "Australian languages"), AVA(null, null, null,
        "Avaric"), AVE(null, null, null, "Avestan"), AWA(null, null, null, "Awadhi"), AYM(null, null, null, "Aymara"), AZE(
        null, null, null, "Azerbaijani"),
    // B
    BAD(null, null, null, "Banda languages"), BAI(null, null, null, "Bamileke languages"), BAK(null, null, null,
        "Bashkir"), BAL(null, null, null, "Baluchi"), BAM(null, null, null, "Bambara"), BAN(null, null, null,
        "Balinese"), BAQ(null, null, null, "Basque"), BAS(null, null, null, "Basa"), BAT(null, null, null,
        "Baltic (Other)"), BEJ(null, null, null, "Beja", "Bedawiyet"), BEM(null, null, null, "Bemba"), BEN(null, null,
        null, "Bengali"), BER(null, null, null, "Berber (Other)"), BHO(null, null, null, "Bhojpuri"), BIH(null, null,
        null, "Bihari languages"), BIK(null, null, null, "Bikol"), BIN(null, null, null, "Bini", "Edo"), BIS(null,
        null, null, "Bislama"), BLA(null, null, null, "Siksika"), BNT(null, null, null, "Bantu (Other)"), BOD(null,
        null, null, "Tibetan"), BOS(null, null, null, "Bosnian"), BRA(null, null, null, "Braj"), BRE(null, null, null,
        "Breton"), BTK(null, null, null, "Batak languages"), BUA(null, null, null, "Buriat"), BUG(null, null, null,
        "Buginese"), BUL(null, null, null, "Bulgarian"), BUR(null, null, null, "Burmese"), BYN(null, null, null,
        "Blin", "Bilin"),
    // C
    CAD(null, null, null, "Caddo"), CAI(null, null, null, "Central American Indian (Other)"), CAR(null, null, null,
        "Galibi Carib"), CAT(null, null, null, "Catalan", "Valencian"), CAU(null, null, null, "Caucasian (Other)"), CEB(
        null, null, null, "Cebuano"), CEL(null, null, null, "Celtic (Other)"), CHB(null, null, null, "Chibcha"), CHG(
        null, null, null, "Chagatai"), CHK(null, null, null, "Chuukese"), CHM(null, null, null, "Mari"), CHN(null,
        null, null, "Chinook jargon"), CHO(null, null, null, "Choctaw"), CHP(null, null, null, "Chipewyan",
        "Dene Suline"), CHR(null, null, null, "Cherokee"), CHY(null, null, null, "Cheyenne"), CMC(null, null, null,
        "Chamic languages"), COP(null, null, null, "Coptic"), CPE(null, null, null,
        "Creoles and pidgins, English-based (Other)"), CPF(null, null, null,
        "Creoles and pidgins, French-based (Other)"), CPP(null, null, null,
        "Creoles and pidgins, Portuguese-based (Other)"), CRH(null, null, null, "Crimean Tatar", "Crimean Turkish"), CRP(
        null, null, null, "Creoles and pidgins (Other)"), CSB(null, null, null, "Kashubian"), CUS(null, null, null,
        "Cushitic (Other)"),
    // D
    DAK(null, null, null, "Dakota"), DAR(null, null, null, "Dargwa"), DAY(null, null, null, "Land Dayak languages"), DEL(
        null, null, null, "Delaware"), DEN(null, null, null, "Slave (Athapascan)"), DGR(null, null, null, "Dogrib"), DIN(
        null, null, null, "Dinka"), DOI(null, null, null, "Dogri"), DRA(null, null, null, "Dravidian (Other)"), DSB(
        null, null, null, "Lower Sorbian"), DUA(null, null, null, "Duala"), DUM(null, null, null,
        "Dutch, Middle (ca. 1050-1350)"), DYU(null, null, null, "Dyula"),
    // E
    EFI(null, null, null, "Efik"), EGY(null, null, null, "Egyptian (Ancient)"), EKA(null, null, null, "Ekajuk"), ELX(
        null, null, null, "Elamite"), ENM(null, null, null, "English, Middle (1100-1500)"), EWE(null, null, null,"Ewe"), EWO(
        null, null, null, "Ewondo"),
    // F
    FAN(null, null, null, "Fang"), FAT(null, null, null, "Fanti"), FIL(null, null, null, "Filipino", "Pilipino"), FIU(
        null, null, null, "Finno-Ugrian (Other)"), FON(null, null, null, "Fon"), FRM(null, null, null,
        "French, Middle (ca. 1400-1600)"), FRO(null, null, null, "French, Old (842-ca. 1400)"), FRR(null, null, null,
        "Northern Frisian"), FRS(null, null, null, "Eastern Frisian"), FUR(null, null, null, "Friulian"),
    // G
    GAA(null, null, null, "Ga"), GAY(null, null, null, "Gayo"), GBA(null, null, null, "Gbaya"), GEM(null, null, null,
        "Germanic (Other)"), GEZ(null, null, null, "Geez"), GIL(null, null, null, "Gilbertese"), GMH(null, null, null,
        "German, Middle High (ca. 1050-1500)"), GOH(null, null, null, "German, Old High (ca. 750-1050)"), GON(null,
        null, null, "Gondi"), GOR(null, null, null, "Gorontalo"), GOT(null, null, null, "Gothic"), GRB(null, null,
        null, "Grebo"), GRC(null, null, null, "Greek, Ancient (to 1453)"), GSW(null, null, null, "Swiss German",
        "Alemannic"), GWI(null, null, null, "Gwich&#xB4;in"),
    // H
    HAI(null, null, null, "Haida"), HAW(null, null, null, "Hawaiian"), HIL(null, null, null, "Hiligaynon"), HIM(null,
        null, null, "Himachali"), HIT(null, null, null, "Hittite"), HMN(null, null, null, "Hmong"), HSB(null, null,
        null, "Upper Sorbian"), HUP(null, null, null, "Hupa"),
    // I
    IBA(null, null, null, "Iban"), IJO(null, null, null, "Ijo languages"), ILO(null, null, null, "Iloko"), INC(null,
        null, null, "Indic (Other)"), INE(null, null, null, "Indo-European (Other)"), INH(null, null, null, "Ingush"), IRA(
        null, null, null, "Iranian (Other)"), IRO(null, null, null, "Iroquoian languages"),
    // J
    JBO(null, null, null, "Lojban"), JPR(null, null, null, "Judeo-Persian"), JRB(null, null, null, "Judeo-Arabic"),
    // K
    KAA(null, null, null, "Kara-Kalpak"), KAB(null, null, null, "Kabyle"), KAC(null, null, null, "Kachin", "Jingpho"), KAM(
        null, null, null, "Kamba"), KAR(null, null, null, "Karen languages"), KAW(null, null, null, "Kawi"), KBD(null,
        null, null, "Kabardian"), KHA(null, null, null, "Khasi"), KHI(null, null, null, "Khoisan (Other)"), KHO(null,
        null, null, "Khotanese"), KMB(null, null, null, "Kimbundu"), KOK(null, null, null, "Konkani"), KOS(null, null,
        null, "Kosraean"), KPE(null, null, null, "Kpelle"), KRC(null, null, null, "Karachay-Balkar"), KRL(null, null,
        null, "Karelian"), KRO(null, null, null, "Kru languages"), KRU(null, null, null, "Kurukh"), KUM(null, null,
        null, "Kumyk"), KUT(null, null, null, "Kutenai"),
    // L
    LAD(null, null, null, "Ladino"), LAH(null, null, null, "Lahnda"), LAM(null, null, null, "Lamba"), LEZ(null, null,
        null, "Lezghian"), LOL(null, null, null, "Mongo"), LOZ(null, null, null, "Lozi"), LUA(null, null, null,
        "Luba-Lulua"), LUI(null, null, null, "Luiseno"), LUN(null, null, null, "Lunda"), LUO(null, null, null,
        "Luo (Kenya and Tanzania)"), LUS(null, null, null, "Lushai"),
    // M
    MAD(null, null, null, "Madurese"), MAG(null, null, null, "Magahi"), MAI(null, null, null, "Maithili"), MAK(null,
        null, null, "Makasar"), MAN(null, null, null, "Mandingo"), MAP(null, null, null, "Austronesian (Other)"), MAS(
        null, null, null, "Masai"), MDF(null, null, null, "Moksha"), MDR(null, null, null, "Mandar"), MEN(null, null,
        null, "Mende"), MGA(null, null, null, "Irish, Middle (900-1200)"), MIC(null, null, null, "Mi'kmaq", "Micmac"), MIN(
        null, null, null, "Minangkabau"), MIS(null, null, null, "Uncoded languages"), MKH(null, null, null,
        "Mon-Khmer (Other)"), MNC(null, null, null, "Manchu"), MNI(null, null, null, "Manipuri"), MNO(null, null, null,
        "Manobo languages"), MOH(null, null, null, "Mohawk"), MOS(null, null, null, "Mossi"), MUL(null, null, null,
        "Multiple languages"), MUN(null, null, null, "Munda languages"), MUS(null, null, null, "Creek"), MWL(null,
        null, null, "Mirandese"), MWR(null, null, null, "Marwari"), MYN(null, null, null, "Mayan languages"), MYV(null,
        null, null, "Erzya"),
    // N
    NAH(null, null, null, "Nahuatl languages"), NAI(null, null, null, "North American Indian"), NAP(null, null, null,
        "Neapolitan"), NDS(null, null, null, "Low German", "Low Saxon", "German, Low", "Saxon, Low"), NEW(null, null,
        null, "Nepal Bhasa", "Newari"), NIA(null, null, null, "Nias"), NIC(null, null, null,
        "Niger-Kordofanian (Other)"), NIU(null, null, null, "Niuean"), NOG(null, null, null, "Nogai"), NON(null, null,
        null, "Norse, Old"), NQO(null, null, null, "N&#x2019;Ko"), NSO(null, null, null, "Northern Sotho", "Pedi",
        "Sepedi"), NUB(null, null, null, "Nubian languages"), NWC(null, null, null, "Classical Newari", "Old Newari",
        "Classical Nepal Bhasa"), NYM(null, null, null, "Nyamwezi"), NYN(null, null, null, "Nyankole"), NYO(null, null,
        null, "Nyoro"), NZI(null, null, null, "Nzima"),
    // O
    OSA(null, null, null, "Osage"), OTA(null, null, null, "Turkish, Ottoman (1500-1928)"), OTO(null, null, null,
        "Otomian languages"),
    // P
    PAA(null, null, null, "Papuan (Other)"), PAG(null, null, null, "Pangasinan"), PAL(null, null, null, "Pahlavi"), PAM(
        null, null, null, "Pampanga", "Kapampangan"), PAP(null, null, null, "Papiamento"), PAU(null, null, null,
        "Palauan"), PEO(null, null, null, "Persian, Old (ca. 600-400 B.C.)"), PHI(null, null, null,
        "Philippine (Other)"), PHN(null, null, null, "Phoenician"), PON(null, null, null, "Pohnpeian"), PRA(null, null,
        null, "Prakrit languages"), PRO(null, null, null, "Proven&#xE7;al, Old (to 1500)"),
    // Q
    QAA(null, null, null, "PRIVATE USE"), QAB(null, null, null, "PRIVATE USE"), QAC(null, null, null, "PRIVATE USE"), QAD(
        null, null, null, "PRIVATE USE"), QAE(null, null, null, "PRIVATE USE"), QAF(null, null, null, "PRIVATE USE"), QAG(
        null, null, null, "PRIVATE USE"), QAH(null, null, null, "PRIVATE USE"), QAI(null, null, null, "PRIVATE USE"), QAJ(
        null, null, null, "PRIVATE USE"), QAK(null, null, null, "PRIVATE USE"), QAL(null, null, null, "PRIVATE USE"), QAM(
        null, null, null, "PRIVATE USE"), QAN(null, null, null, "PRIVATE USE"), QAO(null, null, null, "PRIVATE USE"), QAP(
        null, null, null, "PRIVATE USE"), QAQ(null, null, null, "PRIVATE USE"), QAR(null, null, null, "PRIVATE USE"), QAS(
        null, null, null, "PRIVATE USE"), QAT(null, null, null, "PRIVATE USE"), QAU(null, null, null, "PRIVATE USE"), QAV(
        null, null, null, "PRIVATE USE"), QAW(null, null, null, "PRIVATE USE"), QAX(null, null, null, "PRIVATE USE"), QAY(
        null, null, null, "PRIVATE USE"), QAZ(null, null, null, "PRIVATE USE"), QBA(null, null, null, "PRIVATE USE"), QBB(
        null, null, null, "PRIVATE USE"), QBC(null, null, null, "PRIVATE USE"), QBD(null, null, null, "PRIVATE USE"), QBE(
        null, null, null, "PRIVATE USE"), QBF(null, null, null, "PRIVATE USE"), QBG(null, null, null, "PRIVATE USE"), QBH(
        null, null, null, "PRIVATE USE"), QBI(null, null, null, "PRIVATE USE"), QBJ(null, null, null, "PRIVATE USE"), QBK(
        null, null, null, "PRIVATE USE"), QBL(null, null, null, "PRIVATE USE"), QBM(null, null, null, "PRIVATE USE"), QBN(
        null, null, null, "PRIVATE USE"), QBO(null, null, null, "PRIVATE USE"), QBP(null, null, null, "PRIVATE USE"), QBQ(
        null, null, null, "PRIVATE USE"), QBR(null, null, null, "PRIVATE USE"), QBS(null, null, null, "PRIVATE USE"), QBT(
        null, null, null, "PRIVATE USE"), QBU(null, null, null, "PRIVATE USE"), QBV(null, null, null, "PRIVATE USE"), QBW(
        null, null, null, "PRIVATE USE"), QBX(null, null, null, "PRIVATE USE"), QBY(null, null, null, "PRIVATE USE"), QBZ(
        null, null, null, "PRIVATE USE"), QCA(null, null, null, "PRIVATE USE"), QCB(null, null, null, "PRIVATE USE"), QCC(
        null, null, null, "PRIVATE USE"), QCD(null, null, null, "PRIVATE USE"), QCE(null, null, null, "PRIVATE USE"), QCF(
        null, null, null, "PRIVATE USE"), QCG(null, null, null, "PRIVATE USE"), QCH(null, null, null, "PRIVATE USE"), QCI(
        null, null, null, "PRIVATE USE"), QCJ(null, null, null, "PRIVATE USE"), QCK(null, null, null, "PRIVATE USE"), QCL(
        null, null, null, "PRIVATE USE"), QCM(null, null, null, "PRIVATE USE"), QCN(null, null, null, "PRIVATE USE"), QCO(
        null, null, null, "PRIVATE USE"), QCP(null, null, null, "PRIVATE USE"), QCQ(null, null, null, "PRIVATE USE"), QCR(
        null, null, null, "PRIVATE USE"), QCS(null, null, null, "PRIVATE USE"), QCT(null, null, null, "PRIVATE USE"), QCU(
        null, null, null, "PRIVATE USE"), QCV(null, null, null, "PRIVATE USE"), QCW(null, null, null, "PRIVATE USE"), QCX(
        null, null, null, "PRIVATE USE"), QCY(null, null, null, "PRIVATE USE"), QCZ(null, null, null, "PRIVATE USE"), QDA(
        null, null, null, "PRIVATE USE"), QDB(null, null, null, "PRIVATE USE"), QDC(null, null, null, "PRIVATE USE"), QDD(
        null, null, null, "PRIVATE USE"), QDE(null, null, null, "PRIVATE USE"), QDF(null, null, null, "PRIVATE USE"), QDG(
        null, null, null, "PRIVATE USE"), QDH(null, null, null, "PRIVATE USE"), QDI(null, null, null, "PRIVATE USE"), QDJ(
        null, null, null, "PRIVATE USE"), QDK(null, null, null, "PRIVATE USE"), QDL(null, null, null, "PRIVATE USE"), QDM(
        null, null, null, "PRIVATE USE"), QDN(null, null, null, "PRIVATE USE"), QDO(null, null, null, "PRIVATE USE"), QDP(
        null, null, null, "PRIVATE USE"), QDQ(null, null, null, "PRIVATE USE"), QDR(null, null, null, "PRIVATE USE"), QDS(
        null, null, null, "PRIVATE USE"), QDT(null, null, null, "PRIVATE USE"), QDU(null, null, null, "PRIVATE USE"), QDV(
        null, null, null, "PRIVATE USE"), QDW(null, null, null, "PRIVATE USE"), QDX(null, null, null, "PRIVATE USE"), QDY(
        null, null, null, "PRIVATE USE"), QDZ(null, null, null, "PRIVATE USE"), QEA(null, null, null, "PRIVATE USE"), QEB(
        null, null, null, "PRIVATE USE"), QEC(null, null, null, "PRIVATE USE"), QED(null, null, null, "PRIVATE USE"), QEE(
        null, null, null, "PRIVATE USE"), QEF(null, null, null, "PRIVATE USE"), QEG(null, null, null, "PRIVATE USE"), QEH(
        null, null, null, "PRIVATE USE"), QEI(null, null, null, "PRIVATE USE"), QEJ(null, null, null, "PRIVATE USE"), QEK(
        null, null, null, "PRIVATE USE"), QEL(null, null, null, "PRIVATE USE"), QEM(null, null, null, "PRIVATE USE"), QEN(
        null, null, null, "PRIVATE USE"), QEO(null, null, null, "PRIVATE USE"), QEP(null, null, null, "PRIVATE USE"), QEQ(
        null, null, null, "PRIVATE USE"), QER(null, null, null, "PRIVATE USE"), QES(null, null, null, "PRIVATE USE"), QET(
        null, null, null, "PRIVATE USE"), QEU(null, null, null, "PRIVATE USE"), QEV(null, null, null, "PRIVATE USE"), QEW(
        null, null, null, "PRIVATE USE"), QEX(null, null, null, "PRIVATE USE"), QEY(null, null, null, "PRIVATE USE"), QEZ(
        null, null, null, "PRIVATE USE"), QFA(null, null, null, "PRIVATE USE"), QFB(null, null, null, "PRIVATE USE"), QFC(
        null, null, null, "PRIVATE USE"), QFD(null, null, null, "PRIVATE USE"), QFE(null, null, null, "PRIVATE USE"), QFF(
        null, null, null, "PRIVATE USE"), QFG(null, null, null, "PRIVATE USE"), QFH(null, null, null, "PRIVATE USE"), QFI(
        null, null, null, "PRIVATE USE"), QFJ(null, null, null, "PRIVATE USE"), QFK(null, null, null, "PRIVATE USE"), QFL(
        null, null, null, "PRIVATE USE"), QFM(null, null, null, "PRIVATE USE"), QFN(null, null, null, "PRIVATE USE"), QFO(
        null, null, null, "PRIVATE USE"), QFP(null, null, null, "PRIVATE USE"), QFQ(null, null, null, "PRIVATE USE"), QFR(
        null, null, null, "PRIVATE USE"), QFS(null, null, null, "PRIVATE USE"), QFT(null, null, null, "PRIVATE USE"), QFU(
        null, null, null, "PRIVATE USE"), QFV(null, null, null, "PRIVATE USE"), QFW(null, null, null, "PRIVATE USE"), QFX(
        null, null, null, "PRIVATE USE"), QFY(null, null, null, "PRIVATE USE"), QFZ(null, null, null, "PRIVATE USE"), QGA(
        null, null, null, "PRIVATE USE"), QGB(null, null, null, "PRIVATE USE"), QGC(null, null, null, "PRIVATE USE"), QGD(
        null, null, null, "PRIVATE USE"), QGE(null, null, null, "PRIVATE USE"), QGF(null, null, null, "PRIVATE USE"), QGG(
        null, null, null, "PRIVATE USE"), QGH(null, null, null, "PRIVATE USE"), QGI(null, null, null, "PRIVATE USE"), QGJ(
        null, null, null, "PRIVATE USE"), QGK(null, null, null, "PRIVATE USE"), QGL(null, null, null, "PRIVATE USE"), QGM(
        null, null, null, "PRIVATE USE"), QGN(null, null, null, "PRIVATE USE"), QGO(null, null, null, "PRIVATE USE"), QGP(
        null, null, null, "PRIVATE USE"), QGQ(null, null, null, "PRIVATE USE"), QGR(null, null, null, "PRIVATE USE"), QGS(
        null, null, null, "PRIVATE USE"), QGT(null, null, null, "PRIVATE USE"), QGU(null, null, null, "PRIVATE USE"), QGV(
        null, null, null, "PRIVATE USE"), QGW(null, null, null, "PRIVATE USE"), QGX(null, null, null, "PRIVATE USE"), QGY(
        null, null, null, "PRIVATE USE"), QGZ(null, null, null, "PRIVATE USE"), QHA(null, null, null, "PRIVATE USE"), QHB(
        null, null, null, "PRIVATE USE"), QHC(null, null, null, "PRIVATE USE"), QHD(null, null, null, "PRIVATE USE"), QHE(
        null, null, null, "PRIVATE USE"), QHF(null, null, null, "PRIVATE USE"), QHG(null, null, null, "PRIVATE USE"), QHH(
        null, null, null, "PRIVATE USE"), QHI(null, null, null, "PRIVATE USE"), QHJ(null, null, null, "PRIVATE USE"), QHK(
        null, null, null, "PRIVATE USE"), QHL(null, null, null, "PRIVATE USE"), QHM(null, null, null, "PRIVATE USE"), QHN(
        null, null, null, "PRIVATE USE"), QHO(null, null, null, "PRIVATE USE"), QHP(null, null, null, "PRIVATE USE"), QHQ(
        null, null, null, "PRIVATE USE"), QHR(null, null, null, "PRIVATE USE"), QHS(null, null, null, "PRIVATE USE"), QHT(
        null, null, null, "PRIVATE USE"), QHU(null, null, null, "PRIVATE USE"), QHV(null, null, null, "PRIVATE USE"), QHW(
        null, null, null, "PRIVATE USE"), QHX(null, null, null, "PRIVATE USE"), QHY(null, null, null, "PRIVATE USE"), QHZ(
        null, null, null, "PRIVATE USE"), QIA(null, null, null, "PRIVATE USE"), QIB(null, null, null, "PRIVATE USE"), QIC(
        null, null, null, "PRIVATE USE"), QID(null, null, null, "PRIVATE USE"), QIE(null, null, null, "PRIVATE USE"), QIF(
        null, null, null, "PRIVATE USE"), QIG(null, null, null, "PRIVATE USE"), QIH(null, null, null, "PRIVATE USE"), QII(
        null, null, null, "PRIVATE USE"), QIJ(null, null, null, "PRIVATE USE"), QIK(null, null, null, "PRIVATE USE"), QIL(
        null, null, null, "PRIVATE USE"), QIM(null, null, null, "PRIVATE USE"), QIN(null, null, null, "PRIVATE USE"), QIO(
        null, null, null, "PRIVATE USE"), QIP(null, null, null, "PRIVATE USE"), QIQ(null, null, null, "PRIVATE USE"), QIR(
        null, null, null, "PRIVATE USE"), QIS(null, null, null, "PRIVATE USE"), QIT(null, null, null, "PRIVATE USE"), QIU(
        null, null, null, "PRIVATE USE"), QIV(null, null, null, "PRIVATE USE"), QIW(null, null, null, "PRIVATE USE"), QIX(
        null, null, null, "PRIVATE USE"), QIY(null, null, null, "PRIVATE USE"), QIZ(null, null, null, "PRIVATE USE"), QJA(
        null, null, null, "PRIVATE USE"), QJB(null, null, null, "PRIVATE USE"), QJC(null, null, null, "PRIVATE USE"), QJD(
        null, null, null, "PRIVATE USE"), QJE(null, null, null, "PRIVATE USE"), QJF(null, null, null, "PRIVATE USE"), QJG(
        null, null, null, "PRIVATE USE"), QJH(null, null, null, "PRIVATE USE"), QJI(null, null, null, "PRIVATE USE"), QJJ(
        null, null, null, "PRIVATE USE"), QJK(null, null, null, "PRIVATE USE"), QJL(null, null, null, "PRIVATE USE"), QJM(
        null, null, null, "PRIVATE USE"), QJN(null, null, null, "PRIVATE USE"), QJO(null, null, null, "PRIVATE USE"), QJP(
        null, null, null, "PRIVATE USE"), QJQ(null, null, null, "PRIVATE USE"), QJR(null, null, null, "PRIVATE USE"), QJS(
        null, null, null, "PRIVATE USE"), QJT(null, null, null, "PRIVATE USE"), QJU(null, null, null, "PRIVATE USE"), QJV(
        null, null, null, "PRIVATE USE"), QJW(null, null, null, "PRIVATE USE"), QJX(null, null, null, "PRIVATE USE"), QJY(
        null, null, null, "PRIVATE USE"), QJZ(null, null, null, "PRIVATE USE"), QKA(null, null, null, "PRIVATE USE"), QKB(
        null, null, null, "PRIVATE USE"), QKC(null, null, null, "PRIVATE USE"), QKD(null, null, null, "PRIVATE USE"), QKE(
        null, null, null, "PRIVATE USE"), QKF(null, null, null, "PRIVATE USE"), QKG(null, null, null, "PRIVATE USE"), QKH(
        null, null, null, "PRIVATE USE"), QKI(null, null, null, "PRIVATE USE"), QKJ(null, null, null, "PRIVATE USE"), QKK(
        null, null, null, "PRIVATE USE"), QKL(null, null, null, "PRIVATE USE"), QKM(null, null, null, "PRIVATE USE"), QKN(
        null, null, null, "PRIVATE USE"), QKO(null, null, null, "PRIVATE USE"), QKP(null, null, null, "PRIVATE USE"), QKQ(
        null, null, null, "PRIVATE USE"), QKR(null, null, null, "PRIVATE USE"), QKS(null, null, null, "PRIVATE USE"), QKT(
        null, null, null, "PRIVATE USE"), QKU(null, null, null, "PRIVATE USE"), QKV(null, null, null, "PRIVATE USE"), QKW(
        null, null, null, "PRIVATE USE"), QKX(null, null, null, "PRIVATE USE"), QKY(null, null, null, "PRIVATE USE"), QKZ(
        null, null, null, "PRIVATE USE"), QLA(null, null, null, "PRIVATE USE"), QLB(null, null, null, "PRIVATE USE"), QLC(
        null, null, null, "PRIVATE USE"), QLD(null, null, null, "PRIVATE USE"), QLE(null, null, null, "PRIVATE USE"), QLF(
        null, null, null, "PRIVATE USE"), QLG(null, null, null, "PRIVATE USE"), QLH(null, null, null, "PRIVATE USE"), QLI(
        null, null, null, "PRIVATE USE"), QLJ(null, null, null, "PRIVATE USE"), QLK(null, null, null, "PRIVATE USE"), QLL(
        null, null, null, "PRIVATE USE"), QLM(null, null, null, "PRIVATE USE"), QLN(null, null, null, "PRIVATE USE"), QLO(
        null, null, null, "PRIVATE USE"), QLP(null, null, null, "PRIVATE USE"), QLQ(null, null, null, "PRIVATE USE"), QLR(
        null, null, null, "PRIVATE USE"), QLS(null, null, null, "PRIVATE USE"), QLT(null, null, null, "PRIVATE USE"), QLU(
        null, null, null, "PRIVATE USE"), QLV(null, null, null, "PRIVATE USE"), QLW(null, null, null, "PRIVATE USE"), QLX(
        null, null, null, "PRIVATE USE"), QLY(null, null, null, "PRIVATE USE"), QLZ(null, null, null, "PRIVATE USE"), QMA(
        null, null, null, "PRIVATE USE"), QMB(null, null, null, "PRIVATE USE"), QMC(null, null, null, "PRIVATE USE"), QMD(
        null, null, null, "PRIVATE USE"), QME(null, null, null, "PRIVATE USE"), QMF(null, null, null, "PRIVATE USE"), QMG(
        null, null, null, "PRIVATE USE"), QMH(null, null, null, "PRIVATE USE"), QMI(null, null, null, "PRIVATE USE"), QMJ(
        null, null, null, "PRIVATE USE"), QMK(null, null, null, "PRIVATE USE"), QML(null, null, null, "PRIVATE USE"), QMM(
        null, null, null, "PRIVATE USE"), QMN(null, null, null, "PRIVATE USE"), QMO(null, null, null, "PRIVATE USE"), QMP(
        null, null, null, "PRIVATE USE"), QMQ(null, null, null, "PRIVATE USE"), QMR(null, null, null, "PRIVATE USE"), QMS(
        null, null, null, "PRIVATE USE"), QMT(null, null, null, "PRIVATE USE"), QMU(null, null, null, "PRIVATE USE"), QMV(
        null, null, null, "PRIVATE USE"), QMW(null, null, null, "PRIVATE USE"), QMX(null, null, null, "PRIVATE USE"), QMY(
        null, null, null, "PRIVATE USE"), QMZ(null, null, null, "PRIVATE USE"), QNA(null, null, null, "PRIVATE USE"), QNB(
        null, null, null, "PRIVATE USE"), QNC(null, null, null, "PRIVATE USE"), QND(null, null, null, "PRIVATE USE"), QNE(
        null, null, null, "PRIVATE USE"), QNF(null, null, null, "PRIVATE USE"), QNG(null, null, null, "PRIVATE USE"), QNH(
        null, null, null, "PRIVATE USE"), QNI(null, null, null, "PRIVATE USE"), QNJ(null, null, null, "PRIVATE USE"), QNK(
        null, null, null, "PRIVATE USE"), QNL(null, null, null, "PRIVATE USE"), QNM(null, null, null, "PRIVATE USE"), QNN(
        null, null, null, "PRIVATE USE"), QNO(null, null, null, "PRIVATE USE"), QNP(null, null, null, "PRIVATE USE"), QNQ(
        null, null, null, "PRIVATE USE"), QNR(null, null, null, "PRIVATE USE"), QNS(null, null, null, "PRIVATE USE"), QNT(
        null, null, null, "PRIVATE USE"), QNU(null, null, null, "PRIVATE USE"), QNV(null, null, null, "PRIVATE USE"), QNW(
        null, null, null, "PRIVATE USE"), QNX(null, null, null, "PRIVATE USE"), QNY(null, null, null, "PRIVATE USE"), QNZ(
        null, null, null, "PRIVATE USE"), QOA(null, null, null, "PRIVATE USE"), QOB(null, null, null, "PRIVATE USE"), QOC(
        null, null, null, "PRIVATE USE"), QOD(null, null, null, "PRIVATE USE"), QOE(null, null, null, "PRIVATE USE"), QOF(
        null, null, null, "PRIVATE USE"), QOG(null, null, null, "PRIVATE USE"), QOH(null, null, null, "PRIVATE USE"), QOI(
        null, null, null, "PRIVATE USE"), QOJ(null, null, null, "PRIVATE USE"), QOK(null, null, null, "PRIVATE USE"), QOL(
        null, null, null, "PRIVATE USE"), QOM(null, null, null, "PRIVATE USE"), QON(null, null, null, "PRIVATE USE"), QOO(
        null, null, null, "PRIVATE USE"), QOP(null, null, null, "PRIVATE USE"), QOQ(null, null, null, "PRIVATE USE"), QOR(
        null, null, null, "PRIVATE USE"), QOS(null, null, null, "PRIVATE USE"), QOT(null, null, null, "PRIVATE USE"), QOU(
        null, null, null, "PRIVATE USE"), QOV(null, null, null, "PRIVATE USE"), QOW(null, null, null, "PRIVATE USE"), QOX(
        null, null, null, "PRIVATE USE"), QOY(null, null, null, "PRIVATE USE"), QOZ(null, null, null, "PRIVATE USE"), QPA(
        null, null, null, "PRIVATE USE"), QPB(null, null, null, "PRIVATE USE"), QPC(null, null, null, "PRIVATE USE"), QPD(
        null, null, null, "PRIVATE USE"), QPE(null, null, null, "PRIVATE USE"), QPF(null, null, null, "PRIVATE USE"), QPG(
        null, null, null, "PRIVATE USE"), QPH(null, null, null, "PRIVATE USE"), QPI(null, null, null, "PRIVATE USE"), QPJ(
        null, null, null, "PRIVATE USE"), QPK(null, null, null, "PRIVATE USE"), QPL(null, null, null, "PRIVATE USE"), QPM(
        null, null, null, "PRIVATE USE"), QPN(null, null, null, "PRIVATE USE"), QPO(null, null, null, "PRIVATE USE"), QPP(
        null, null, null, "PRIVATE USE"), QPQ(null, null, null, "PRIVATE USE"), QPR(null, null, null, "PRIVATE USE"), QPS(
        null, null, null, "PRIVATE USE"), QPT(null, null, null, "PRIVATE USE"), QPU(null, null, null, "PRIVATE USE"), QPV(
        null, null, null, "PRIVATE USE"), QPW(null, null, null, "PRIVATE USE"), QPX(null, null, null, "PRIVATE USE"), QPY(
        null, null, null, "PRIVATE USE"), QPZ(null, null, null, "PRIVATE USE"), QQA(null, null, null, "PRIVATE USE"), QQB(
        null, null, null, "PRIVATE USE"), QQC(null, null, null, "PRIVATE USE"), QQD(null, null, null, "PRIVATE USE"), QQE(
        null, null, null, "PRIVATE USE"), QQF(null, null, null, "PRIVATE USE"), QQG(null, null, null, "PRIVATE USE"), QQH(
        null, null, null, "PRIVATE USE"), QQI(null, null, null, "PRIVATE USE"), QQJ(null, null, null, "PRIVATE USE"), QQK(
        null, null, null, "PRIVATE USE"), QQL(null, null, null, "PRIVATE USE"), QQM(null, null, null, "PRIVATE USE"), QQN(
        null, null, null, "PRIVATE USE"), QQO(null, null, null, "PRIVATE USE"), QQP(null, null, null, "PRIVATE USE"), QQQ(
        null, null, null, "PRIVATE USE"), QQR(null, null, null, "PRIVATE USE"), QQS(null, null, null, "PRIVATE USE"), QQT(
        null, null, null, "PRIVATE USE"), QQU(null, null, null, "PRIVATE USE"), QQV(null, null, null, "PRIVATE USE"), QQW(
        null, null, null, "PRIVATE USE"), QQX(null, null, null, "PRIVATE USE"), QQY(null, null, null, "PRIVATE USE"), QQZ(
        null, null, null, "PRIVATE USE"), QRA(null, null, null, "PRIVATE USE"), QRB(null, null, null, "PRIVATE USE"), QRC(
        null, null, null, "PRIVATE USE"), QRD(null, null, null, "PRIVATE USE"), QRE(null, null, null, "PRIVATE USE"), QRF(
        null, null, null, "PRIVATE USE"), QRG(null, null, null, "PRIVATE USE"), QRH(null, null, null, "PRIVATE USE"), QRI(
        null, null, null, "PRIVATE USE"), QRJ(null, null, null, "PRIVATE USE"), QRK(null, null, null, "PRIVATE USE"), QRL(
        null, null, null, "PRIVATE USE"), QRM(null, null, null, "PRIVATE USE"), QRN(null, null, null, "PRIVATE USE"), QRO(
        null, null, null, "PRIVATE USE"), QRP(null, null, null, "PRIVATE USE"), QRQ(null, null, null, "PRIVATE USE"), QRR(
        null, null, null, "PRIVATE USE"), QRS(null, null, null, "PRIVATE USE"), QRT(null, null, null, "PRIVATE USE"), QRU(
        null, null, null, "PRIVATE USE"), QRV(null, null, null, "PRIVATE USE"), QRW(null, null, null, "PRIVATE USE"), QRX(
        null, null, null, "PRIVATE USE"), QRY(null, null, null, "PRIVATE USE"), QRZ(null, null, null, "PRIVATE USE"), QSA(
        null, null, null, "PRIVATE USE"), QSB(null, null, null, "PRIVATE USE"), QSC(null, null, null, "PRIVATE USE"), QSD(
        null, null, null, "PRIVATE USE"), QSE(null, null, null, "PRIVATE USE"), QSF(null, null, null, "PRIVATE USE"), QSG(
        null, null, null, "PRIVATE USE"), QSH(null, null, null, "PRIVATE USE"), QSI(null, null, null, "PRIVATE USE"), QSJ(
        null, null, null, "PRIVATE USE"), QSK(null, null, null, "PRIVATE USE"), QSL(null, null, null, "PRIVATE USE"), QSM(
        null, null, null, "PRIVATE USE"), QSN(null, null, null, "PRIVATE USE"), QSO(null, null, null, "PRIVATE USE"), QSP(
        null, null, null, "PRIVATE USE"), QSQ(null, null, null, "PRIVATE USE"), QSR(null, null, null, "PRIVATE USE"), QSS(
        null, null, null, "PRIVATE USE"), QST(null, null, null, "PRIVATE USE"), QSU(null, null, null, "PRIVATE USE"), QSV(
        null, null, null, "PRIVATE USE"), QSW(null, null, null, "PRIVATE USE"), QSX(null, null, null, "PRIVATE USE"), QSY(
        null, null, null, "PRIVATE USE"), QSZ(null, null, null, "PRIVATE USE"), QTA(null, null, null, "PRIVATE USE"), QTB(
        null, null, null, "PRIVATE USE"), QTC(null, null, null, "PRIVATE USE"), QTD(null, null, null, "PRIVATE USE"), QTE(
        null, null, null, "PRIVATE USE"), QTF(null, null, null, "PRIVATE USE"), QTG(null, null, null, "PRIVATE USE"), QTH(
        null, null, null, "PRIVATE USE"), QTI(null, null, null, "PRIVATE USE"), QTJ(null, null, null, "PRIVATE USE"), QTK(
        null, null, null, "PRIVATE USE"), QTL(null, null, null, "PRIVATE USE"), QTM(null, null, null, "PRIVATE USE"), QTN(
        null, null, null, "PRIVATE USE"), QTO(null, null, null, "PRIVATE USE"), QTP(null, null, null, "PRIVATE USE"), QTQ(
        null, null, null, "PRIVATE USE"), QTR(null, null, null, "PRIVATE USE"), QTS(null, null, null, "PRIVATE USE"), QTT(
        null, null, null, "PRIVATE USE"), QTU(null, null, null, "PRIVATE USE"), QTV(null, null, null, "PRIVATE USE"), QTW(
        null, null, null, "PRIVATE USE"), QTX(null, null, null, "PRIVATE USE"), QTY(null, null, null, "PRIVATE USE"), QTZ(
        null, null, null, "PRIVATE USE"),
    // R
    RAJ(null, null, null, "Rajasthani"), RAP(null, null, null, "Rapanui"), RAR(null, null, null, "Rarotongan",
        "Cook Islands Maori"), ROA(null, null, null, "Romance (Other)"), ROM(null, null, null, "Romany"), RUM(null,
        null, null, "Romanian", "Moldavian", "Moldovan"), RUP(null, null, null, "Aromanian", "Arumanian",
        "Macedo-Romanian"),
    // S
    SAD(null, null, null, "Sandawe"), SAH(null, null, null, "Yakut"), SAI(null, null, null,
        "South American Indian (Other)"), SAL(null, null, null, "Salishan languages"), SAM(null, null, null,
        "Samaritan Aramaic"), SAS(null, null, null, "Sasak"), SAT(null, null, null, "Santali"), SCN(null, null, null,
        "Sicilian"), SCO(null, null, null, "Scots"), SEL(null, null, null, "Selkup"), SEM(null, null, null,
        "Semitic (Other)"), SGA(null, null, null, "Irish, Old (to 900)"), SGN(null, null, null, "Sign Languages"), SHN(
        null, null, null, "Shan"), SID(null, null, null, "Sidamo"), SIO(null, null, null, "Siouan languages"), SIT(
        null, null, null, "Sino-Tibetan (Other)"), SLA(null, null, null, "Slavic (Other)"), SMA(null, null, null,
        "Southern Sami"), SMI(null, null, null, "Sami languages (Other)"), SMJ(null, null, null, "Lule Sami"), SMN(
        null, null, null, "Inari Sami"), SMS(null, null, null, "Skolt Sami"), SNK(null, null, null, "Soninke"), SOG(
        null, null, null, "Sogdian"), SON(null, null, null, "Songhai languages"), SRN(null, null, null, "Sranan Tongo"), SRR(
        null, null, null, "Serer"), SSA(null, null, null, "Nilo-Saharan (Other)"), SUK(null, null, null, "Sukuma"), SUS(
        null, null, null, "Susu"), SUX(null, null, null, "Sumerian"), SYC(null, null, null, "Classical Syriac"), SYR(
        null, null, null, "Syriac"),
    // T
    TAI(null, null, null, "Tai (Other)"), TEM(null, null, null, "Timne"), TER(null, null, null, "Tereno"), TET(null,
        null, null, "Tetum"), TIG(null, null, null, "Tigre"), TIV(null, null, null, "Tiv"), TKL(null, null, null,
        "Tokelau"), TLH(null, null, null, "Klingon", "tlhIngan-Hol"), TLI(null, null, null, "Tlingit"), TMH(null, null,
        null, "Tamashek"), TOG(null, null, null, "Tonga (Nyasa)"), TPI(null, null, null, "Tok Pisin"), TSI(null, null,
        null, "Tsimshian"), TUM(null, null, null, "Tumbuka"), TUP(null, null, null, "Tupi languages"), TUT(null, null,
        null, "Altaic (Other)"), TVL(null, null, null, "Tuvalu"), TYV(null, null, null, "Tuvinian"),
    // U
    UDM(null, null, null, "Udmurt"), UGA(null, null, null, "Ugaritic"), UIG(null, null, null, "Uighur", "Uyghur"), UMB(
        null, null, null, "Umbundu"), UND(null, null, null, "Undetermined"),
    // V
    VAI(null, null, null, "Vai"), VOT(null, null, null, "Votic"),
    // W
    WAK(null, null, null, "Wakashan languages"), WAL(null, null, null, "Walamo"), WAR(null, null, null, "Waray"), WAS(
        null, null, null, "Washo"), WEL(null, null, null, "Welsh"), WEN(null, null, null, "Sorbian languages"), WLN(
        null, null, null, "Walloon"), WOL(null, null, null, "Wolof"),
    // X
    XAL(null, null, null, "Kalmyk", "Oirat"), XHO(null, null, null, "Xhosa"),
    // Y
    YAO(null, null, null, "Yao"), YAP(null, null, null, "Yapese"), YID(null, null, null, "Yiddish"), YOR(null, null,
        null, "Yoruba"), YPK(null, null, null, "Yupik languages"),
    // Z
    ZAP(null, null, null, "Zapotec"), ZBL(null, null, null, "Blissymbols", "Blissymbolics", "Bliss"), ZEN(null, null,
        null, "Zenaga"), ZHA(null, null, null, "Zhuang", "Chuang"), ZHO(null, null, null, "Chinese"), ZND(null, null,
        null, "Zande languages"), ZUL(null, null, null, "Zulu"), ZUN(null, null, null, "Zuni"), ZXX(null, null, null,
        "No linguistic content"), ZZA(null, null, null, "Zaza", "Dimili", "Dimli", "Kirdki", "Kirmanjki", "Zazaki");

    private final String deprecated;
    private final String preferred;
    private final String suppressscript;
    private final String[] descriptions;

    private Language(String dep, String pref, String ss, String... desc) {
        this.deprecated = dep;
        this.preferred = pref;
        this.suppressscript = ss;
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

    public Language getPreferred() {
        return preferred != null ? valueOf(preferred.toUpperCase(Locale.US)) : this;
    }

    public String getSuppressScript() {
        return suppressscript;
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

    public static Language valueOf(Subtag subtag) {
        if (subtag == null)
            return null;
        if (subtag.getType() == Subtag.Type.PRIMARY)
            return valueOf(subtag.getName().toUpperCase(Locale.US));
        else
            throw new IllegalArgumentException("Wrong subtag type");
    }

}
