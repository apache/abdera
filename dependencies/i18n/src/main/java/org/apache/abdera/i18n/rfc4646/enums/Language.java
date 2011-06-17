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
    AA(null, null, "Afar"), AB(null, null, "Abkhazian"), AE(null, null, "Avestan"), AF(null, null, "Afrikaans"), AK(
        null, null, "Akan"), AM(null, null, "Amharic"), AN(null, null, "Aragonese"), AR(null, null, "Arabic"), AS(null,
        null, "Assamese"), AV(null, null, "Avaric"), AY(null, null, "Aymara"), AZ(null, null, "Azerbaijani"), BA(null,
        null, "Bashkir"), BE(null, null, "Belarusian"), BG(null, null, "Bulgarian"), BH(null, null, "Bihari"), BI(null,
        null, "Bislama"), BM(null, null, "Bambara"), BN(null, null, "Bengali"), BO(null, null, "Tibetan"), BR(null,
        null, "Breton"), BS(null, null, "Bosnian"), CA(null, null, "Catalan", "Valencian"), CE(null, null, "Chechen"), CH(
        null, null, "Chamorro"), CO(null, null, "Corsican"), CR(null, null, "Cree"), CS(null, null, "Czech"), CU(null,
        null, "Church Slavic", "Old Slavonic", "Church Slavonic", "Old Bulgarian", "Old Church Slavonic"), CV(null,
        null, "Chuvash"), CY(null, null, "Welsh"), DA(null, null, "Danish"), DE(null, null, "German"), DV(null, null,
        "Divehi", "Dhivehi", "Maldivian"), DZ(null, null, "Dzongkha"), EE(null, null, "Ewe"), EL(null, null,
        "Greek, Modern (1453-)"), EN(null, null, "English"), EO(null, null, "Esperanto"), ES(null, null, "Spanish",
        "Castilian"), ET(null, null, "Estonian"), EU(null, null, "Basque"), FA(null, null, "Persian"), FF(null, null,
        "Fulah"), FI(null, null, "Finnish"), FJ(null, null, "Fijian"), FO(null, null, "Faroese"), FR(null, null,
        "French"), FY(null, null, "Western Frisian"), GA(null, null, "Irish"), GD(null, null, "Gaelic",
        "Scottish Gaelic"), GL(null, null, "Galician"), GN(null, null, "Guarani"), GU(null, null, "Gujarati"), GV(null,
        null, "Manx"), HA(null, null, "Hausa"), HE(null, null, "Hebrew"), HI(null, null, "Hindi"), HO(null, null,
        "Hiri Motu"), HR(null, null, "Croatian"), HT(null, null, "Haitian", "Haitian Creole"), HU(null, null,
        "Hungarian"), HY(null, null, "Armenian"), HZ(null, null, "Herero"), IA(null, null,
        "Interlingua (International Auxiliary Language Association)"), ID(null, null, "Indonesian"), IE(null, null,
        "Interlingue", "Occidental"), IG(null, null, "Igbo"), II(null, null, "Sichuan Yi", "Nuosu"), IK(null, null,
        "Inupiaq"), IN("1989-01-01", "id", "Indonesian"), IO(null, null, "Ido"), IS(null, null, "Icelandic"), IT(null,
        null, "Italian"), IU(null, null, "Inuktitut"), IW("1989-01-01", "he", null, "Hebrew"), JA(null, null,
        "Japanese"), JI("1989-01-01", "yi", "Yiddish"), JV(null, null, "Javanese"), JW("2001-08-13", "jv", "Javanese"), KA(
        null, null, "Georgian"), KG(null, null, "Kongo"), KI(null, null, "Kikuyu", "Gikuyu"), KJ(null, null,
        "Kuanyama", "Kwanyama"), KK(null, null, "Kazakh"), KL(null, null, "Kalaallisut", "Greenlandic"), KM(null, null,
        "Central Khmer"), KN(null, null, "Kannada"), KO(null, null, "Korean"), KR(null, null, "Kanuri"), KS(null, null,
        "Kashmiri"), KU(null, null, "Kurdish"), KV(null, null, "Komi"), KW(null, null, "Cornish"), KY(null, null,
        "Kyrgyz", "Kirghiz"), LA(null, null, "Latin"), LB(null, null, "Luxembourgish", "Letzeburgesch"), LG(null, null,
        "Ganda"), LI(null, null, "Limburgan", "Limburger", "Limburgish"), LN(null, null, "Lingala"), LO(null, null,
        "Lao"), LT(null, null, "Lithuanian"), LU(null, null, "Luba-Katanga"), LV(null, null, "Latvian"), MG(null, null,
        "Malagasy"), MH(null, null, "Marshallese"), MI(null, null, "Maori"), MK(null, null, "Macedonian"), ML(null,
        null, "Malayalam"), MN(null, null, "Mongolian"), MO("2008-11-03", null, null, "Moldavian"), MR(null, null,
        "Marathi"), MS(null, null, "Malay"), MT(null, null, "Maltese"), MY(null, null, "Burmese"), NA(null, null,
        "Nauru"), NB(null, null, "Norwegian Bokm&#xE5;l"), ND(null, null, "Ndebele, North", "North Ndebele"), NE(null,
        null, "Nepali"), NG(null, null, "Ndonga"), NL(null, null, "Dutch", "Flemish"), NN(null, null,
        "Norwegian Nynorsk"), NO(null, null, "Norwegian"), NR(null, null, "Ndebele, South", "South Ndebele"), NV(null,
        null, "Navajo", "Navaho"), NY(null, null, "Chichewa", "Chewa", "Nyanja"), OC(null, null, "Occitan (post 1500)",
        "Proven&#xE7;al"), OJ(null, null, "Ojibwa"), OM(null, null, "Oromo"), OR(null, null, "Oriya"), OS(null, null,
        "Ossetian", "Ossetic"), PA(null, null, "Panjabi", "Punjabi"), PI(null, null, "Pali"), PL(null, null, "Polish"), PS(
        null, null, "Pushto"), PT(null, null, "Portuguese"), QU(null, null, "Quechua"), RM(null, null, "Romansh"), RN(
        null, null, "Rundi"), RO(null, null, "Romanian"), RU(null, null, "Russian"), RW(null, null, "Kinyarwanda"), SA(
        null, null, "Sanskrit"), SC(null, null, "Sardinian"), SD(null, null, "Sindhi"), SE(null, null, "Northern Sami"), SG(
        null, null, "Sango"), SH("2000-02-18", null, null, "Serbo-Croatian"), SI(null, null, "Sinhala", "Sinhalese"), SK(
        null, null, "Slovak"), SL(null, null, "Slovenian"), SM(null, null, "Samoan"), SN(null, null, "Shona"), SO(null,
        null, "Somali"), SQ(null, null, "Albanian"), SR(null, null, "Serbian"), SS(null, null, "Swati"), ST(null, null,
        "Sotho, Southern"), SU(null, null, "Sundanese"), SV(null, null, "Swedish"), SW(null, null, "Swahili"), TA(null,
        null, "Tamil"), TE(null, null, "Telugu"), TG(null, null, "Tajik"), TH(null, null, "Thai"), TI(null, null,
        "Tigrinya"), TK(null, null, "Turkmen"), TL(null, null, "Tagalog"), TN(null, null, "Tswana"), TO(null, null,
        "Tonga (Tonga Islands)"), TR(null, null, "Turkish"), TS(null, null, "Tsonga"), TT(null, null, "Tatar"), TW(
        null, null, "Twi"), TY(null, null, "Tahitian"), UG(null, null, "Uighur", "Uyghur"), UK(null, null, "Ukrainian"), UR(
        null, null, "Urdu"), UZ(null, null, "Uzbek"), VE(null, null, "Venda"), VI(null, null, "Vietnamese"), VO(null,
        null, "Volap&#xFC;k"), WA(null, null, "Walloon"), WO(null, null, "Wolof"), XH(null, null, "Xhosa"), YI(null,
        null, "Yiddish"), YO(null, null, "Yoruba"), ZA(null, null, "Zhuang", "Chuang"), ZH(null, null, "Chinese"), ZU(
        null, null, "Zulu"),
    // ISO 639-2
    // A
    AAR(null, null, "Afar"), ABK(null, null, "Abkhazian"), ACE(null, null, "Achinese"), AFR(null, null, "Afrikaans"), ANP(
        null, null, "Angika"), ACH(null, null, "Acoli"), ADA(null, null, "Adangme"), ADY(null, null, "Adyghe", "Adygei"), AFA(
        null, null, "Afro-Asiatic (Other)"), AFH(null, null, "Afrihili"), AIN(null, null, "Ainu"), AKA(null, null,
        "Akan"), AKK(null, null, "Akkadian"), ALB(null, null, "Albanian"), ALE(null, null, "Aleut"), ALG(null, null,
        "Algonquian languages"), ALT(null, null, "Southern Altai"), AMH(null, null, "Amharic"), ANG(null, null,
        "English, Old (ca. 450-1100)"), APA(null, null, "Apache languages"), ARA(null, null, "Arabic"), ARC(null, null,
        "Official Aramaic (700-300 BCE)", "Imperial Aramaic (700-300 BCE)"), ARG(null, null, "Aragonese"), ARM(null,
        null, "Armenian"), ARN(null, null, "Mapudungun", "Mapuche"), ARP(null, null, "Arapaho"), ART(null, null,
        "Artificial (Other)"), ARW(null, null, "Arawak"), ASM(null, null, "Assamese"), AST(null, null, "Asturian",
        "Bable", "Leonese", "Asturleonese"), ATH(null, null, "Athapascan languages"), AUS(null, null,
        "Australian languages"), AVA(null, null, "Avaric"), AVE(null, null, "Avestan"), AWA(null, null, "Awadhi"), AYM(
        null, null, "Aymara"), AZE(null, null, "Azerbaijani"),
    // B
    BAD(null, null, "Banda languages"), BAI(null, null, "Bamileke languages"), BAK(null, null, "Bashkir"), BAL(null,
        null, "Baluchi"), BAM(null, null, "Bambara"), BAN(null, null, "Balinese"), BAQ(null, null, "Basque"), BAS(null,
        null, "Basa"), BAT(null, null, "Baltic (Other)"), BEJ(null, null, "Beja", "Bedawiyet"), BEM(null, null, "Bemba"), BEN(
        null, null, "Bengali"), BER(null, null, "Berber (Other)"), BHO(null, null, "Bhojpuri"), BIH(null, null,
        "Bihari languages"), BIK(null, null, "Bikol"), BIN(null, null, "Bini", "Edo"), BIS(null, null, "Bislama"), BLA(
        null, null, "Siksika"), BNT(null, null, "Bantu (Other)"), BOD(null, null, "Tibetan"), BOS(null, null, "Bosnian"), BRA(
        null, null, "Braj"), BRE(null, null, "Breton"), BTK(null, null, "Batak languages"), BUA(null, null, "Buriat"), BUG(
        null, null, "Buginese"), BUL(null, null, "Bulgarian"), BUR(null, null, "Burmese"), BYN(null, null, "Blin",
        "Bilin"),
    // C
    CAD(null, null, "Caddo"), CAI(null, null, "Central American Indian (Other)"), CAR(null, null, "Galibi Carib"), CAT(
        null, null, "Catalan", "Valencian"), CAU(null, null, "Caucasian (Other)"), CEB(null, null, "Cebuano"), CEL(
        null, null, "Celtic (Other)"), CES(null, null, "Czech"), CHA(null, null, "Chamorro"), CHB(null, null, "Chibcha"), CHE(
        null, null, "Chechen"), CHG(null, null, "Chagatai"), CHI(null, null, "Chinese"), CHK(null, null, "Chuukese"), CHM(
        null, null, "Mari"), CHN(null, null, "Chinook jargon"), CHO(null, null, "Choctaw"), CHP(null, null,
        "Chipewyan", "Dene Suline"), CHR(null, null, "Cherokee"), CHU(null, null, "Church Slavic", "Old Slavonic",
        "Church Slavonic", "Old Bulgarian", "Old Church Slavonic"), CHV(null, null, "Chuvash"), CHY(null, null,
        "Cheyenne"), CMC(null, null, "Chamic languages"), COP(null, null, "Coptic"), COR(null, null, "Cornish"), COS(
        null, null, "Corsican"), CPE(null, null, "Creoles and pidgins, English-based (Other)"), CPF(null, null,
        "Creoles and pidgins, French-based (Other)"), CPP(null, null, "Creoles and pidgins, Portuguese-based (Other)"), CRH(
        null, null, "Crimean Tatar", "Crimean Turkish"), CRE(null, null, "Cree"), CRP(null, null,
        "Creoles and pidgins (Other)"), CSB(null, null, "Kashubian"), CUS(null, null, "Cushitic (Other)"), CYM(null,
        null, "Welsh"), CZE(null, null, "Czech"),
    // D
    DAK(null, null, "Dakota"), DAN(null, null, "Danish"), DAR(null, null, "Dargwa"), DAY(null, null,
        "Land Dayak languages"), DEL(null, null, "Delaware"), DEN(null, null, "Slave (Athapascan)"), DEU(null, null,
        "German"), DGR(null, null, "Dogrib"), DIN(null, null, "Dinka"), DIV(null, null, "Divehi", "Dhivehi",
        "Maldivian"), DOI(null, null, "Dogri"), DRA(null, null, "Dravidian (Other)"), DSB(null, null, "Lower Sorbian"), DUA(
        null, null, "Duala"), DUM(null, null, "Dutch, Middle (ca. 1050-1350)"), DUT(null, null, "Dutch", "Flemish"), DYU(
        null, null, "Dyula"), DZO(null, null, "Dzongkha"),
    // E
    EFI(null, null, "Efik"), EGY(null, null, "Egyptian (Ancient)"), EKA(null, null, "Ekajuk"), ELL(null, null,
        "Greek, Modern (1453-)"), ELX(null, null, "Elamite"), ENG(null, null, "English"), ENM(null, null,
        "English, Middle (1100-1500)"), EPO(null, null, "Esperanto"), EST(null, null, "Estonian"), EUS(null, null,
        "Basque"), EWE(null, null, "Ewe"), EWO(null, null, "Ewondo"),
    // F
    FAN(null, null, "Fang"), FAO(null, null, "Faroese"), FAS(null, null, "Persian"), FAT(null, null, "Fanti"), FIJ(
        null, null, "Fijian"), FIL(null, null, "Filipino", "Pilipino"), FIN(null, null, "Finnish"), FIU(null, null,
        "Finno-Ugrian (Other)"), FON(null, null, "Fon"), FRA(null, null, "French"), FRE(null, null, "French"), FRM(
        null, null, "French, Middle (ca. 1400-1600)"), FRO(null, null, "French, Old (842-ca. 1400)"), FRR(null, null,
        "Northern Frisian"), FRS(null, null, "Eastern Frisian"), FRY(null, null, "Western Frisian"), FUL(null, null,
        "Fulah"), FUR(null, null, "Friulian"),
    // G
    GAA(null, null, "Ga"), GAY(null, null, "Gayo"), GBA(null, null, "Gbaya"), GEM(null, null, "Germanic (Other)"), GEO(
        null, null, "Georgian"), GER(null, null, "German"), GEZ(null, null, "Geez"), GIL(null, null, "Gilbertese"), GLA(
        null, null, "Gaelic", "Scottish Gaelic"), GLE(null, null, "Irish"), GLG(null, null, "Galician"), GLV(null,
        null, "Manx"), GMH(null, null, "German, Middle High (ca. 1050-1500)"), GOH(null, null,
        "German, Old High (ca. 750-1050)"), GON(null, null, "Gondi"), GOR(null, null, "Gorontalo"), GOT(null, null,
        "Gothic"), GRB(null, null, "Grebo"), GRC(null, null, "Greek, Ancient (to 1453)"), GRE(null, null,
        "Greek, Modern (1453-)"), GRN(null, null, "Guarani"), GSW(null, null, "Swiss German", "Alemannic"), GUJ(null,
        null, "Gujarati"), GWI(null, null, "Gwich&#xB4;in"),
    // H
    HAI(null, null, "Haida"), HAU(null, null, "Hausa"), HAW(null, null, "Hawaiian"), HAT(null, null, "Haitian",
        "Haitian Creole"), HEB(null, null, "Hebrew"), HER(null, null, "Herero"), HIL(null, null, "Hiligaynon"), HIM(
        null, null, "Himachali"), HIN(null, null, "Hindi"), HIT(null, null, "Hittite"), HMN(null, null, "Hmong"), HMO(
        null, null, "Hiri Motu"), HRV(null, null, "Croatian"), HSB(null, null, "Upper Sorbian"), HUN(null, null,
        "Hungarian"), HUP(null, null, "Hupa"), HYE(null, null, "Armenian"),
    // I
    IBA(null, null, "Iban"), IBO(null, null, "Igbo"), III(null, null, "Sichuan Yi", "Nuosu"), IJO(null, null,
        "Ijo languages"), ILE(null, null, "Interlingue", "Occidental"), ILO(null, null, "Iloko"), INC(null, null,
        "Indic (Other)"), INE(null, null, "Indo-European (Other)"), INH(null, null, "Ingush"), IRA(null, null,
        "Iranian (Other)"), IRO(null, null, "Iroquoian languages"), ITA(null, null, "Italian"),
    // J
    JAV(null, null, "Javanese"), JBO(null, null, "Lojban"), JPN(null, null, "Japanese"), JPR(null, null,
        "Judeo-Persian"), JRB(null, null, "Judeo-Arabic"),
    // K
    KAA(null, null, "Kara-Kalpak"), KAB(null, null, "Kabyle"), KAC(null, null, "Kachin", "Jingpho"), KAL(null, null,
        "Kalaallisut", "Greenlandic"), KAM(null, null, "Kamba"), KAR(null, null, "Karen languages"), KAW(null, null,
        "Kawi"), KBD(null, null, "Kabardian"), KHA(null, null, "Khasi"), KHI(null, null, "Khoisan (Other)"), KHO(null,
        null, "Khotanese"), KIK(null, null, "Kikuyu", "Gikuyu"), KIR(null, null, "Kirghiz", "Kyrgyz"), KMB(null, null,
        "Kimbundu"), KOK(null, null, "Konkani"), KOS(null, null, "Kosraean"), KPE(null, null, "Kpelle"), KRC(null,
        null, "Karachay-Balkar"), KRL(null, null, "Karelian"), KRO(null, null, "Kru languages"), KRU(null, null,
        "Kurukh"), KUA(null, null, "Kuanyama", "Kwanyama"), KUM(null, null, "Kumyk"), KUT(null, null, "Kutenai"),
    // L
    LAD(null, null, "Ladino"), LAH(null, null, "Lahnda"), LAM(null, null, "Lamba"), LEZ(null, null, "Lezghian"), LIM(
        null, null, "Limburgan", "Limburger", "Limburgish"), LOL(null, null, "Mongo"), LOZ(null, null, "Lozi"), LUA(
        null, null, "Luba-Lulua"), LUI(null, null, "Luiseno"), LUN(null, null, "Lunda"), LUO(null, null,
        "Luo (Kenya and Tanzania)"), LUS(null, null, "Lushai"),
    // M
    MAD(null, null, "Madurese"), MAG(null, null, "Magahi"), MAI(null, null, "Maithili"), MAK(null, null, "Makasar"), MAN(
        null, null, "Mandingo"), MAP(null, null, "Austronesian (Other)"), MAS(null, null, "Masai"), MDF(null, null,
        "Moksha"), MDR(null, null, "Mandar"), MEN(null, null, "Mende"), MGA(null, null, "Irish, Middle (900-1200)"), MIC(
        null, null, "Mi'kmaq", "Micmac"), MIN(null, null, "Minangkabau"), MIS(null, null, "Uncoded languages"), MKH(
        null, null, "Mon-Khmer (Other)"), MNC(null, null, "Manchu"), MNI(null, null, "Manipuri"), MNO(null, null,
        "Manobo languages"), MOH(null, null, "Mohawk"), MOS(null, null, "Mossi"), MUL(null, null, "Multiple languages"), MUN(
        null, null, "Munda languages"), MUS(null, null, "Creek"), MWL(null, null, "Mirandese"), MWR(null, null,
        "Marwari"), MYN(null, null, "Mayan languages"), MYV(null, null, "Erzya"),
    // N
    NAH(null, null, "Nahuatl languages"), NAI(null, null, "North American Indian"), NAP(null, null, "Neapolitan"), NAU(
        null, null, "Nauru"), NAV(null, null, "Navajo", "Navaho"), NBL(null, null, "Ndebele, South", "South Ndebele"), NDE(
        null, null, "Ndebele, North", "North Ndebele"), NDS(null, null, "Low German", "Low Saxon", "German, Low",
        "Saxon, Low"), NEW(null, null, "Nepal Bhasa", "Newari"), NIA(null, null, "Nias"), NIC(null, null,
        "Niger-Kordofanian (Other)"), NIU(null, null, "Niuean"), NNO(null, null, "Norwegian Nynorsk",
        "Nynorsk, Norwegian"), NOG(null, null, "Nogai"), NON(null, null, "Norse, Old"), NQO(null, null, "N&#x2019;Ko"), NSO(
        null, null, "Northern Sotho", "Pedi", "Sepedi"), NUB(null, null, "Nubian languages"), NWC(null, null,
        "Classical Newari", "Old Newari", "Classical Nepal Bhasa"), NYA(null, null, "Chichewa", "Chewa", "Nyanja"), NYM(
        null, null, "Nyamwezi"), NYN(null, null, "Nyankole"), NYO(null, null, "Nyoro"), NZI(null, null, "Nzima"),
    // O
    OCI(null, null, "Occitan"), OJI(null, null, "Ojibwa"), ORI(null, null, "Oriya"), ORM(null, null, "Oromo"), OSA(
        null, null, "Osage"), OSS(null, null, "Ossetian", "Ossetic"), OTA(null, null, "Turkish, Ottoman (1500-1928)"), OTO(
        null, null, "Otomian languages"),
    // P
    PAA(null, null, "Papuan (Other)"), PAG(null, null, "Pangasinan"), PAL(null, null, "Pahlavi"), PAM(null, null,
        "Pampanga", "Kapampangan"), PAN(null, null, "Panjabi", "Punjabi"), PAP(null, null, "Papiamento"), PAU(null,
        null, "Palauan"), PEO(null, null, "Persian, Old (ca. 600-400 B.C.)"), PHI(null, null, "Philippine (Other)"), PHN(
        null, null, "Phoenician"), PON(null, null, "Pohnpeian"), POR(null, null, "Portuguese"), PRA(null, null,
        "Prakrit languages"), PRO(null, null, "Proven&#xE7;al, Old (to 1500)"), PUS(null, null, "Pushto", "Pashto"),
    // Q
    QAA(null, null, "PRIVATE USE"), QAB(null, null, "PRIVATE USE"), QAC(null, null, "PRIVATE USE"), QAD(null, null,
        "PRIVATE USE"), QAE(null, null, "PRIVATE USE"), QAF(null, null, "PRIVATE USE"), QAG(null, null, "PRIVATE USE"), QAH(
        null, null, "PRIVATE USE"), QAI(null, null, "PRIVATE USE"), QAJ(null, null, "PRIVATE USE"), QAK(null, null,
        "PRIVATE USE"), QAL(null, null, "PRIVATE USE"), QAM(null, null, "PRIVATE USE"), QAN(null, null, "PRIVATE USE"), QAO(
        null, null, "PRIVATE USE"), QAP(null, null, "PRIVATE USE"), QAQ(null, null, "PRIVATE USE"), QAR(null, null,
        "PRIVATE USE"), QAS(null, null, "PRIVATE USE"), QAT(null, null, "PRIVATE USE"), QAU(null, null, "PRIVATE USE"), QAV(
        null, null, "PRIVATE USE"), QAW(null, null, "PRIVATE USE"), QAX(null, null, "PRIVATE USE"), QAY(null, null,
        "PRIVATE USE"), QAZ(null, null, "PRIVATE USE"), QBA(null, null, "PRIVATE USE"), QBB(null, null, "PRIVATE USE"), QBC(
        null, null, "PRIVATE USE"), QBD(null, null, "PRIVATE USE"), QBE(null, null, "PRIVATE USE"), QBF(null, null,
        "PRIVATE USE"), QBG(null, null, "PRIVATE USE"), QBH(null, null, "PRIVATE USE"), QBI(null, null, "PRIVATE USE"), QBJ(
        null, null, "PRIVATE USE"), QBK(null, null, "PRIVATE USE"), QBL(null, null, "PRIVATE USE"), QBM(null, null,
        "PRIVATE USE"), QBN(null, null, "PRIVATE USE"), QBO(null, null, "PRIVATE USE"), QBP(null, null, "PRIVATE USE"), QBQ(
        null, null, "PRIVATE USE"), QBR(null, null, "PRIVATE USE"), QBS(null, null, "PRIVATE USE"), QBT(null, null,
        "PRIVATE USE"), QBU(null, null, "PRIVATE USE"), QBV(null, null, "PRIVATE USE"), QBW(null, null, "PRIVATE USE"), QBX(
        null, null, "PRIVATE USE"), QBY(null, null, "PRIVATE USE"), QBZ(null, null, "PRIVATE USE"), QCA(null, null,
        "PRIVATE USE"), QCB(null, null, "PRIVATE USE"), QCC(null, null, "PRIVATE USE"), QCD(null, null, "PRIVATE USE"), QCE(
        null, null, "PRIVATE USE"), QCF(null, null, "PRIVATE USE"), QCG(null, null, "PRIVATE USE"), QCH(null, null,
        "PRIVATE USE"), QCI(null, null, "PRIVATE USE"), QCJ(null, null, "PRIVATE USE"), QCK(null, null, "PRIVATE USE"), QCL(
        null, null, "PRIVATE USE"), QCM(null, null, "PRIVATE USE"), QCN(null, null, "PRIVATE USE"), QCO(null, null,
        "PRIVATE USE"), QCP(null, null, "PRIVATE USE"), QCQ(null, null, "PRIVATE USE"), QCR(null, null, "PRIVATE USE"), QCS(
        null, null, "PRIVATE USE"), QCT(null, null, "PRIVATE USE"), QCU(null, null, "PRIVATE USE"), QCV(null, null,
        "PRIVATE USE"), QCW(null, null, "PRIVATE USE"), QCX(null, null, "PRIVATE USE"), QCY(null, null, "PRIVATE USE"), QCZ(
        null, null, "PRIVATE USE"), QDA(null, null, "PRIVATE USE"), QDB(null, null, "PRIVATE USE"), QDC(null, null,
        "PRIVATE USE"), QDD(null, null, "PRIVATE USE"), QDE(null, null, "PRIVATE USE"), QDF(null, null, "PRIVATE USE"), QDG(
        null, null, "PRIVATE USE"), QDH(null, null, "PRIVATE USE"), QDI(null, null, "PRIVATE USE"), QDJ(null, null,
        "PRIVATE USE"), QDK(null, null, "PRIVATE USE"), QDL(null, null, "PRIVATE USE"), QDM(null, null, "PRIVATE USE"), QDN(
        null, null, "PRIVATE USE"), QDO(null, null, "PRIVATE USE"), QDP(null, null, "PRIVATE USE"), QDQ(null, null,
        "PRIVATE USE"), QDR(null, null, "PRIVATE USE"), QDS(null, null, "PRIVATE USE"), QDT(null, null, "PRIVATE USE"), QDU(
        null, null, "PRIVATE USE"), QDV(null, null, "PRIVATE USE"), QDW(null, null, "PRIVATE USE"), QDX(null, null,
        "PRIVATE USE"), QDY(null, null, "PRIVATE USE"), QDZ(null, null, "PRIVATE USE"), QEA(null, null, "PRIVATE USE"), QEB(
        null, null, "PRIVATE USE"), QEC(null, null, "PRIVATE USE"), QED(null, null, "PRIVATE USE"), QEE(null, null,
        "PRIVATE USE"), QEF(null, null, "PRIVATE USE"), QEG(null, null, "PRIVATE USE"), QEH(null, null, "PRIVATE USE"), QEI(
        null, null, "PRIVATE USE"), QEJ(null, null, "PRIVATE USE"), QEK(null, null, "PRIVATE USE"), QEL(null, null,
        "PRIVATE USE"), QEM(null, null, "PRIVATE USE"), QEN(null, null, "PRIVATE USE"), QEO(null, null, "PRIVATE USE"), QEP(
        null, null, "PRIVATE USE"), QEQ(null, null, "PRIVATE USE"), QER(null, null, "PRIVATE USE"), QES(null, null,
        "PRIVATE USE"), QET(null, null, "PRIVATE USE"), QEU(null, null, "PRIVATE USE"), QEV(null, null, "PRIVATE USE"), QEW(
        null, null, "PRIVATE USE"), QEX(null, null, "PRIVATE USE"), QEY(null, null, "PRIVATE USE"), QEZ(null, null,
        "PRIVATE USE"), QFA(null, null, "PRIVATE USE"), QFB(null, null, "PRIVATE USE"), QFC(null, null, "PRIVATE USE"), QFD(
        null, null, "PRIVATE USE"), QFE(null, null, "PRIVATE USE"), QFF(null, null, "PRIVATE USE"), QFG(null, null,
        "PRIVATE USE"), QFH(null, null, "PRIVATE USE"), QFI(null, null, "PRIVATE USE"), QFJ(null, null, "PRIVATE USE"), QFK(
        null, null, "PRIVATE USE"), QFL(null, null, "PRIVATE USE"), QFM(null, null, "PRIVATE USE"), QFN(null, null,
        "PRIVATE USE"), QFO(null, null, "PRIVATE USE"), QFP(null, null, "PRIVATE USE"), QFQ(null, null, "PRIVATE USE"), QFR(
        null, null, "PRIVATE USE"), QFS(null, null, "PRIVATE USE"), QFT(null, null, "PRIVATE USE"), QFU(null, null,
        "PRIVATE USE"), QFV(null, null, "PRIVATE USE"), QFW(null, null, "PRIVATE USE"), QFX(null, null, "PRIVATE USE"), QFY(
        null, null, "PRIVATE USE"), QFZ(null, null, "PRIVATE USE"), QGA(null, null, "PRIVATE USE"), QGB(null, null,
        "PRIVATE USE"), QGC(null, null, "PRIVATE USE"), QGD(null, null, "PRIVATE USE"), QGE(null, null, "PRIVATE USE"), QGF(
        null, null, "PRIVATE USE"), QGG(null, null, "PRIVATE USE"), QGH(null, null, "PRIVATE USE"), QGI(null, null,
        "PRIVATE USE"), QGJ(null, null, "PRIVATE USE"), QGK(null, null, "PRIVATE USE"), QGL(null, null, "PRIVATE USE"), QGM(
        null, null, "PRIVATE USE"), QGN(null, null, "PRIVATE USE"), QGO(null, null, "PRIVATE USE"), QGP(null, null,
        "PRIVATE USE"), QGQ(null, null, "PRIVATE USE"), QGR(null, null, "PRIVATE USE"), QGS(null, null, "PRIVATE USE"), QGT(
        null, null, "PRIVATE USE"), QGU(null, null, "PRIVATE USE"), QGV(null, null, "PRIVATE USE"), QGW(null, null,
        "PRIVATE USE"), QGX(null, null, "PRIVATE USE"), QGY(null, null, "PRIVATE USE"), QGZ(null, null, "PRIVATE USE"), QHA(
        null, null, "PRIVATE USE"), QHB(null, null, "PRIVATE USE"), QHC(null, null, "PRIVATE USE"), QHD(null, null,
        "PRIVATE USE"), QHE(null, null, "PRIVATE USE"), QHF(null, null, "PRIVATE USE"), QHG(null, null, "PRIVATE USE"), QHH(
        null, null, "PRIVATE USE"), QHI(null, null, "PRIVATE USE"), QHJ(null, null, "PRIVATE USE"), QHK(null, null,
        "PRIVATE USE"), QHL(null, null, "PRIVATE USE"), QHM(null, null, "PRIVATE USE"), QHN(null, null, "PRIVATE USE"), QHO(
        null, null, "PRIVATE USE"), QHP(null, null, "PRIVATE USE"), QHQ(null, null, "PRIVATE USE"), QHR(null, null,
        "PRIVATE USE"), QHS(null, null, "PRIVATE USE"), QHT(null, null, "PRIVATE USE"), QHU(null, null, "PRIVATE USE"), QHV(
        null, null, "PRIVATE USE"), QHW(null, null, "PRIVATE USE"), QHX(null, null, "PRIVATE USE"), QHY(null, null,
        "PRIVATE USE"), QHZ(null, null, "PRIVATE USE"), QIA(null, null, "PRIVATE USE"), QIB(null, null, "PRIVATE USE"), QIC(
        null, null, "PRIVATE USE"), QID(null, null, "PRIVATE USE"), QIE(null, null, "PRIVATE USE"), QIF(null, null,
        "PRIVATE USE"), QIG(null, null, "PRIVATE USE"), QIH(null, null, "PRIVATE USE"), QII(null, null, "PRIVATE USE"), QIJ(
        null, null, "PRIVATE USE"), QIK(null, null, "PRIVATE USE"), QIL(null, null, "PRIVATE USE"), QIM(null, null,
        "PRIVATE USE"), QIN(null, null, "PRIVATE USE"), QIO(null, null, "PRIVATE USE"), QIP(null, null, "PRIVATE USE"), QIQ(
        null, null, "PRIVATE USE"), QIR(null, null, "PRIVATE USE"), QIS(null, null, "PRIVATE USE"), QIT(null, null,
        "PRIVATE USE"), QIU(null, null, "PRIVATE USE"), QIV(null, null, "PRIVATE USE"), QIW(null, null, "PRIVATE USE"), QIX(
        null, null, "PRIVATE USE"), QIY(null, null, "PRIVATE USE"), QIZ(null, null, "PRIVATE USE"), QJA(null, null,
        "PRIVATE USE"), QJB(null, null, "PRIVATE USE"), QJC(null, null, "PRIVATE USE"), QJD(null, null, "PRIVATE USE"), QJE(
        null, null, "PRIVATE USE"), QJF(null, null, "PRIVATE USE"), QJG(null, null, "PRIVATE USE"), QJH(null, null,
        "PRIVATE USE"), QJI(null, null, "PRIVATE USE"), QJJ(null, null, "PRIVATE USE"), QJK(null, null, "PRIVATE USE"), QJL(
        null, null, "PRIVATE USE"), QJM(null, null, "PRIVATE USE"), QJN(null, null, "PRIVATE USE"), QJO(null, null,
        "PRIVATE USE"), QJP(null, null, "PRIVATE USE"), QJQ(null, null, "PRIVATE USE"), QJR(null, null, "PRIVATE USE"), QJS(
        null, null, "PRIVATE USE"), QJT(null, null, "PRIVATE USE"), QJU(null, null, "PRIVATE USE"), QJV(null, null,
        "PRIVATE USE"), QJW(null, null, "PRIVATE USE"), QJX(null, null, "PRIVATE USE"), QJY(null, null, "PRIVATE USE"), QJZ(
        null, null, "PRIVATE USE"), QKA(null, null, "PRIVATE USE"), QKB(null, null, "PRIVATE USE"), QKC(null, null,
        "PRIVATE USE"), QKD(null, null, "PRIVATE USE"), QKE(null, null, "PRIVATE USE"), QKF(null, null, "PRIVATE USE"), QKG(
        null, null, "PRIVATE USE"), QKH(null, null, "PRIVATE USE"), QKI(null, null, "PRIVATE USE"), QKJ(null, null,
        "PRIVATE USE"), QKK(null, null, "PRIVATE USE"), QKL(null, null, "PRIVATE USE"), QKM(null, null, "PRIVATE USE"), QKN(
        null, null, "PRIVATE USE"), QKO(null, null, "PRIVATE USE"), QKP(null, null, "PRIVATE USE"), QKQ(null, null,
        "PRIVATE USE"), QKR(null, null, "PRIVATE USE"), QKS(null, null, "PRIVATE USE"), QKT(null, null, "PRIVATE USE"), QKU(
        null, null, "PRIVATE USE"), QKV(null, null, "PRIVATE USE"), QKW(null, null, "PRIVATE USE"), QKX(null, null,
        "PRIVATE USE"), QKY(null, null, "PRIVATE USE"), QKZ(null, null, "PRIVATE USE"), QLA(null, null, "PRIVATE USE"), QLB(
        null, null, "PRIVATE USE"), QLC(null, null, "PRIVATE USE"), QLD(null, null, "PRIVATE USE"), QLE(null, null,
        "PRIVATE USE"), QLF(null, null, "PRIVATE USE"), QLG(null, null, "PRIVATE USE"), QLH(null, null, "PRIVATE USE"), QLI(
        null, null, "PRIVATE USE"), QLJ(null, null, "PRIVATE USE"), QLK(null, null, "PRIVATE USE"), QLL(null, null,
        "PRIVATE USE"), QLM(null, null, "PRIVATE USE"), QLN(null, null, "PRIVATE USE"), QLO(null, null, "PRIVATE USE"), QLP(
        null, null, "PRIVATE USE"), QLQ(null, null, "PRIVATE USE"), QLR(null, null, "PRIVATE USE"), QLS(null, null,
        "PRIVATE USE"), QLT(null, null, "PRIVATE USE"), QLU(null, null, "PRIVATE USE"), QLV(null, null, "PRIVATE USE"), QLW(
        null, null, "PRIVATE USE"), QLX(null, null, "PRIVATE USE"), QLY(null, null, "PRIVATE USE"), QLZ(null, null,
        "PRIVATE USE"), QMA(null, null, "PRIVATE USE"), QMB(null, null, "PRIVATE USE"), QMC(null, null, "PRIVATE USE"), QMD(
        null, null, "PRIVATE USE"), QME(null, null, "PRIVATE USE"), QMF(null, null, "PRIVATE USE"), QMG(null, null,
        "PRIVATE USE"), QMH(null, null, "PRIVATE USE"), QMI(null, null, "PRIVATE USE"), QMJ(null, null, "PRIVATE USE"), QMK(
        null, null, "PRIVATE USE"), QML(null, null, "PRIVATE USE"), QMM(null, null, "PRIVATE USE"), QMN(null, null,
        "PRIVATE USE"), QMO(null, null, "PRIVATE USE"), QMP(null, null, "PRIVATE USE"), QMQ(null, null, "PRIVATE USE"), QMR(
        null, null, "PRIVATE USE"), QMS(null, null, "PRIVATE USE"), QMT(null, null, "PRIVATE USE"), QMU(null, null,
        "PRIVATE USE"), QMV(null, null, "PRIVATE USE"), QMW(null, null, "PRIVATE USE"), QMX(null, null, "PRIVATE USE"), QMY(
        null, null, "PRIVATE USE"), QMZ(null, null, "PRIVATE USE"), QNA(null, null, "PRIVATE USE"), QNB(null, null,
        "PRIVATE USE"), QNC(null, null, "PRIVATE USE"), QND(null, null, "PRIVATE USE"), QNE(null, null, "PRIVATE USE"), QNF(
        null, null, "PRIVATE USE"), QNG(null, null, "PRIVATE USE"), QNH(null, null, "PRIVATE USE"), QNI(null, null,
        "PRIVATE USE"), QNJ(null, null, "PRIVATE USE"), QNK(null, null, "PRIVATE USE"), QNL(null, null, "PRIVATE USE"), QNM(
        null, null, "PRIVATE USE"), QNN(null, null, "PRIVATE USE"), QNO(null, null, "PRIVATE USE"), QNP(null, null,
        "PRIVATE USE"), QNQ(null, null, "PRIVATE USE"), QNR(null, null, "PRIVATE USE"), QNS(null, null, "PRIVATE USE"), QNT(
        null, null, "PRIVATE USE"), QNU(null, null, "PRIVATE USE"), QNV(null, null, "PRIVATE USE"), QNW(null, null,
        "PRIVATE USE"), QNX(null, null, "PRIVATE USE"), QNY(null, null, "PRIVATE USE"), QNZ(null, null, "PRIVATE USE"), QOA(
        null, null, "PRIVATE USE"), QOB(null, null, "PRIVATE USE"), QOC(null, null, "PRIVATE USE"), QOD(null, null,
        "PRIVATE USE"), QOE(null, null, "PRIVATE USE"), QOF(null, null, "PRIVATE USE"), QOG(null, null, "PRIVATE USE"), QOH(
        null, null, "PRIVATE USE"), QOI(null, null, "PRIVATE USE"), QOJ(null, null, "PRIVATE USE"), QOK(null, null,
        "PRIVATE USE"), QOL(null, null, "PRIVATE USE"), QOM(null, null, "PRIVATE USE"), QON(null, null, "PRIVATE USE"), QOO(
        null, null, "PRIVATE USE"), QOP(null, null, "PRIVATE USE"), QOQ(null, null, "PRIVATE USE"), QOR(null, null,
        "PRIVATE USE"), QOS(null, null, "PRIVATE USE"), QOT(null, null, "PRIVATE USE"), QOU(null, null, "PRIVATE USE"), QOV(
        null, null, "PRIVATE USE"), QOW(null, null, "PRIVATE USE"), QOX(null, null, "PRIVATE USE"), QOY(null, null,
        "PRIVATE USE"), QOZ(null, null, "PRIVATE USE"), QPA(null, null, "PRIVATE USE"), QPB(null, null, "PRIVATE USE"), QPC(
        null, null, "PRIVATE USE"), QPD(null, null, "PRIVATE USE"), QPE(null, null, "PRIVATE USE"), QPF(null, null,
        "PRIVATE USE"), QPG(null, null, "PRIVATE USE"), QPH(null, null, "PRIVATE USE"), QPI(null, null, "PRIVATE USE"), QPJ(
        null, null, "PRIVATE USE"), QPK(null, null, "PRIVATE USE"), QPL(null, null, "PRIVATE USE"), QPM(null, null,
        "PRIVATE USE"), QPN(null, null, "PRIVATE USE"), QPO(null, null, "PRIVATE USE"), QPP(null, null, "PRIVATE USE"), QPQ(
        null, null, "PRIVATE USE"), QPR(null, null, "PRIVATE USE"), QPS(null, null, "PRIVATE USE"), QPT(null, null,
        "PRIVATE USE"), QPU(null, null, "PRIVATE USE"), QPV(null, null, "PRIVATE USE"), QPW(null, null, "PRIVATE USE"), QPX(
        null, null, "PRIVATE USE"), QPY(null, null, "PRIVATE USE"), QPZ(null, null, "PRIVATE USE"), QQA(null, null,
        "PRIVATE USE"), QQB(null, null, "PRIVATE USE"), QQC(null, null, "PRIVATE USE"), QQD(null, null, "PRIVATE USE"), QQE(
        null, null, "PRIVATE USE"), QQF(null, null, "PRIVATE USE"), QQG(null, null, "PRIVATE USE"), QQH(null, null,
        "PRIVATE USE"), QQI(null, null, "PRIVATE USE"), QQJ(null, null, "PRIVATE USE"), QQK(null, null, "PRIVATE USE"), QQL(
        null, null, "PRIVATE USE"), QQM(null, null, "PRIVATE USE"), QQN(null, null, "PRIVATE USE"), QQO(null, null,
        "PRIVATE USE"), QQP(null, null, "PRIVATE USE"), QQQ(null, null, "PRIVATE USE"), QQR(null, null, "PRIVATE USE"), QQS(
        null, null, "PRIVATE USE"), QQT(null, null, "PRIVATE USE"), QQU(null, null, "PRIVATE USE"), QQV(null, null,
        "PRIVATE USE"), QQW(null, null, "PRIVATE USE"), QQX(null, null, "PRIVATE USE"), QQY(null, null, "PRIVATE USE"), QQZ(
        null, null, "PRIVATE USE"), QRA(null, null, "PRIVATE USE"), QRB(null, null, "PRIVATE USE"), QRC(null, null,
        "PRIVATE USE"), QRD(null, null, "PRIVATE USE"), QRE(null, null, "PRIVATE USE"), QRF(null, null, "PRIVATE USE"), QRG(
        null, null, "PRIVATE USE"), QRH(null, null, "PRIVATE USE"), QRI(null, null, "PRIVATE USE"), QRJ(null, null,
        "PRIVATE USE"), QRK(null, null, "PRIVATE USE"), QRL(null, null, "PRIVATE USE"), QRM(null, null, "PRIVATE USE"), QRN(
        null, null, "PRIVATE USE"), QRO(null, null, "PRIVATE USE"), QRP(null, null, "PRIVATE USE"), QRQ(null, null,
        "PRIVATE USE"), QRR(null, null, "PRIVATE USE"), QRS(null, null, "PRIVATE USE"), QRT(null, null, "PRIVATE USE"), QRU(
        null, null, "PRIVATE USE"), QRV(null, null, "PRIVATE USE"), QRW(null, null, "PRIVATE USE"), QRX(null, null,
        "PRIVATE USE"), QRY(null, null, "PRIVATE USE"), QRZ(null, null, "PRIVATE USE"), QSA(null, null, "PRIVATE USE"), QSB(
        null, null, "PRIVATE USE"), QSC(null, null, "PRIVATE USE"), QSD(null, null, "PRIVATE USE"), QSE(null, null,
        "PRIVATE USE"), QSF(null, null, "PRIVATE USE"), QSG(null, null, "PRIVATE USE"), QSH(null, null, "PRIVATE USE"), QSI(
        null, null, "PRIVATE USE"), QSJ(null, null, "PRIVATE USE"), QSK(null, null, "PRIVATE USE"), QSL(null, null,
        "PRIVATE USE"), QSM(null, null, "PRIVATE USE"), QSN(null, null, "PRIVATE USE"), QSO(null, null, "PRIVATE USE"), QSP(
        null, null, "PRIVATE USE"), QSQ(null, null, "PRIVATE USE"), QSR(null, null, "PRIVATE USE"), QSS(null, null,
        "PRIVATE USE"), QST(null, null, "PRIVATE USE"), QSU(null, null, "PRIVATE USE"), QSV(null, null, "PRIVATE USE"), QSW(
        null, null, "PRIVATE USE"), QSX(null, null, "PRIVATE USE"), QSY(null, null, "PRIVATE USE"), QSZ(null, null,
        "PRIVATE USE"), QTA(null, null, "PRIVATE USE"), QTB(null, null, "PRIVATE USE"), QTC(null, null, "PRIVATE USE"), QTD(
        null, null, "PRIVATE USE"), QTE(null, null, "PRIVATE USE"), QTF(null, null, "PRIVATE USE"), QTG(null, null,
        "PRIVATE USE"), QTH(null, null, "PRIVATE USE"), QTI(null, null, "PRIVATE USE"), QTJ(null, null, "PRIVATE USE"), QTK(
        null, null, "PRIVATE USE"), QTL(null, null, "PRIVATE USE"), QTM(null, null, "PRIVATE USE"), QTN(null, null,
        "PRIVATE USE"), QTO(null, null, "PRIVATE USE"), QTP(null, null, "PRIVATE USE"), QTQ(null, null, "PRIVATE USE"), QTR(
        null, null, "PRIVATE USE"), QTS(null, null, "PRIVATE USE"), QTT(null, null, "PRIVATE USE"), QTU(null, null,
        "PRIVATE USE"), QTV(null, null, "PRIVATE USE"), QTW(null, null, "PRIVATE USE"), QTX(null, null, "PRIVATE USE"), QTY(
        null, null, "PRIVATE USE"), QTZ(null, null, "PRIVATE USE"),
    // R
    RAJ(null, null, "Rajasthani"), RAP(null, null, "Rapanui"), RAR(null, null, "Rarotongan", "Cook Islands Maori"), ROA(
        null, null, "Romance (Other)"), ROH(null, null, "Romansh"), ROM(null, null, "Romany"), RON(null, null,
        "Romanian", "Moldavian", "Moldovan"), RUM(null, null, "Romanian", "Moldavian", "Moldovan"), RUN(null, null,
        "Rundi"), RUP(null, null, "Aromanian", "Arumanian", "Macedo-Romanian"), RUS(null, null, "Russian"),
    // S
    SAD(null, null, "Sandawe"), SAH(null, null, "Yakut"), SAI(null, null, "South American Indian (Other)"), SAL(null,
        null, "Salishan languages"), SAM(null, null, "Samaritan Aramaic"), SAS(null, null, "Sasak"), SAT(null, null,
        "Santali"), SCN(null, null, "Sicilian"), SCO(null, null, "Scots"), SEL(null, null, "Selkup"), SEM(null, null,
        "Semitic (Other)"), SGA(null, null, "Irish, Old (to 900)"), SGN(null, null, "Sign Languages"), SHN(null, null,
        "Shan"), SID(null, null, "Sidamo"), SIN(null, null, "Sinhala", "Sinhalese"), SIO(null, null, "Siouan languages"), SIT(
        null, null, "Sino-Tibetan (Other)"), SLA(null, null, "Slavic (Other)"), SMA(null, null, "Southern Sami"), SMI(
        null, null, "Sami languages (Other)"), SMJ(null, null, "Lule Sami"), SMN(null, null, "Inari Sami"), SMS(null,
        null, "Skolt Sami"), SNK(null, null, "Soninke"), SOG(null, null, "Sogdian"), SON(null, null,
        "Songhai languages"), SPA(null, null, "Spanish", "Castilian"), SRN(null, null, "Sranan Tongo"), SRR(null, null,
        "Serer"), SSA(null, null, "Nilo-Saharan (Other)"), SUK(null, null, "Sukuma"), SUS(null, null, "Susu"), SUX(
        null, null, "Sumerian"), SYC(null, null, "Classical Syriac"), SYR(null, null, "Syriac"),
    // T
    TAH(null, null, "Tahitian"), TAI(null, null, "Tai (Other)"), TAM(null, null, "Tamil"), TAT(null, null, "Tatar"), TEM(
        null, null, "Timne"), TER(null, null, "Tereno"), TET(null, null, "Tetum"), TIG(null, null, "Tigre"), TIV(null,
        null, "Tiv"), TKL(null, null, "Tokelau"), TLH(null, null, "Klingon", "tlhIngan-Hol"), TLI(null, null, "Tlingit"), TMH(
        null, null, "Tamashek"), TOG(null, null, "Tonga (Nyasa)"), TPI(null, null, "Tok Pisin"), TSI(null, null,
        "Tsimshian"), TUK(null, null, "Turkmen"), TUM(null, null, "Tumbuka"), TUP(null, null, "Tupi languages"), TUR(
        null, null, "Turkish"), TSO(null, null, "Tsonga"), TUT(null, null, "Altaic (Other)"), TVL(null, null, "Tuvalu"), TWI(
        null, null, "Twi"), TYV(null, null, "Tuvinian"),
    // U
    UDM(null, null, "Udmurt"), UGA(null, null, "Ugaritic"), UIG(null, null, "Uighur", "Uyghur"), UKR(null, null,
        "Ukrainian"), UMB(null, null, "Umbundu"), UND(null, null, "Undetermined"), URD(null, null, "Urdu"), UZB(null,
        null, "Uzbek"),
    // V
    VAI(null, null, "Vai"), VEN(null, null, "Venda"), VIE(null, null, "Vietnamese"), VOT(null, null, "Votic"),
    // W
    WAK(null, null, "Wakashan languages"), WAL(null, null, "Walamo"), WAR(null, null, "Waray"), WAS(null, null, "Washo"), WEL(
        null, null, "Welsh"), WEN(null, null, "Sorbian languages"), WLN(null, null, "Walloon"), WOL(null, null, "Wolof"),
    // X
    XAL(null, null, "Kalmyk", "Oirat"), XHO(null, null, "Xhosa"),
    // Y
    YAO(null, null, "Yao"), YAP(null, null, "Yapese"), YID(null, null, "Yiddish"), YOR(null, null, "Yoruba"), YPK(null,
        null, "Yupik languages"),
    // Z
    ZAP(null, null, "Zapotec"), ZBL(null, null, "Blissymbols", "Blissymbolics", "Bliss"), ZEN(null, null, "Zenaga"), ZHA(
        null, null, "Zhuang", "Chuang"), ZHO(null, null, "Chinese"), ZND(null, null, "Zande languages"), ZUL(null,
        null, "Zulu"), ZUN(null, null, "Zuni"), ZXX(null, null, "No linguistic content"), ZZA(null, null, "Zaza",
        "Dimili", "Dimli", "Kirdki", "Kirmanjki", "Zazaki");

    private final String deprecated;
    private final String preferred;
    private final String[] descriptions;

    private Language(String dep, String pref, String... desc) {
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

    public Language getPreferred() {
        return preferred != null ? valueOf(preferred.toUpperCase(Locale.US)) : this;
    }

    public String getSuppressScript() {
        return null;
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
