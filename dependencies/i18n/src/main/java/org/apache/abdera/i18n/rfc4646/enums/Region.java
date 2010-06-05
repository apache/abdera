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
public enum Region {

    AA(null, null, "PRIVATE USE"), AD(null, null, "Andorra"), AE(null, null, "United Arab Emirates"), AF(null, null,
        "Afghanistan"), AG(null, null, "Antigua and Barbuda"), AI(null, null, "Anguilla"), AL(null, null, "Albania"), AM(
        null, null, "Armenia"), AN(null, null, "Netherlands Antilles"), AO(null, null, "Angola"), AQ(null, null,
        "Antarctica"), AR(null, null, "Argentina"), AS(null, null, "American Samoa"), AT(null, null, "Austria"), AU(
        null, null, "Australia"), AW(null, null, "Aruba"), AX(null, null, "&#xC5;land Islands"), AZ(null, null,
        "Azerbaijan"), BA(null, null, "Bosnia and Herzegovina"), BB(null, null, "Barbados"), BD(null, null,
        "Bangladesh"), BE(null, null, "Belgium"), BF(null, null, "Burkina Faso"), BG(null, null, "Bulgaria"), BH(null,
        null, "Bahrain"), BI(null, null, "Burundi"), BJ(null, null, "Benin"), BL(null, null, "Saint Barth&#xE9;lemy"), BM(
        null, null, "Bermuda"), BN(null, null, "Brunei Darussalam"), BO(null, null, "Bolivia"), BR(null, null, "Brazil"), BS(
        null, null, "Bahamas"), BT(null, null, "Bhutan"), BU("1989-12-05", "MM", "Burma"), BV(null, null,
        "Bouvet Island"), BW(null, null, "Botswana"), BY(null, null, "Belarus"), BZ(null, null, "Belize"), CA(null,
        null, "Canada"), CC(null, null, "Cocos (Keeling) Islands"), CD(null, null,
        "Congo, The Democratic Republic of the"), CF(null, null, "Central African Republic"), CG(null, null, "Congo"), CH(
        null, null, "Switzerland"), CI(null, null, "C&#xF4;te d'Ivoire"), CK(null, null, "Cook Islands"), CL(null,
        null, "Chile"), CM(null, null, "Cameroon"), CN(null, null, "China"), CO(null, null, "Colombia"), CR(null, null,
        "Costa Rica"), CS("2006-10-05", null, "Serbia and Montenegro"), CU(null, null, "Cuba"), CV(null, null,
        "Cape Verde"), CX(null, null, "Christmas Island"), CY(null, null, "Cyprus"), CZ(null, null, "Czech Republic"), DD(
        "1990-10-30", "DE", "German Democratic Republic"), DE(null, null, "Germany"), DJ(null, null, "Djibouti"), DK(
        null, null, "Denmark"), DM(null, null, "Dominica"), DO(null, null, "Dominican Republic"), DZ(null, null,
        "Algeria"), EC(null, null, "Ecuador"), EE(null, null, "Estonia"), EG(null, null, "Egypt"), EH(null, null,
        "Western Sahara"), ER(null, null, "Eritrea"), ES(null, null, "Spain"), ET(null, null, "Ethiopia"), FI(null,
        null, "Finland"), FJ(null, null, "Fiji"), FK(null, null, "Falkland Islands (Malvinas)"), FM(null, null,
        "Micronesia, Federated States of"), FO(null, null, "Faroe Islands"), FR(null, null, "France"), FX("1997-07-14",
        "FR", "Metropolitan France"), GA(null, null, "Gabon"), GB(null, null, "United Kingdom"), GD(null, null,
        "Grenada"), GE(null, null, "Georgia"), GF(null, null, "French Guiana"), GG(null, null, "Guernsey"), GH(null,
        null, "Ghana"), GI(null, null, "Gibraltar"), GL(null, null, "Greenland"), GM(null, null, "Gambia"), GN(null,
        null, "Guinea"), GP(null, null, "Guadeloupe"), GQ(null, null, "Equatorial Guinea"), GR(null, null, "Greece"), GS(
        null, null, "South Georgia and the South Sandwich Islands"), GT(null, null, "Guatemala"), GU(null, null, "Guam"), GW(
        null, null, "Guinea-Bissau"), GY(null, null, "Guyana"), HK(null, null, "Hong Kong"), HM(null, null,
        "Heard Island and McDonald Islands"), HN(null, null, "Honduras"), HR(null, null, "Croatia"), HT(null, null,
        "Haiti"), HU(null, null, "Hungary"), ID(null, null, "Indonesia"), IE(null, null, "Ireland"), IL(null, null,
        "Israel"), IM(null, null, "Isle of Man"), IN(null, null, "India"), IO(null, null,
        "British Indian Ocean Territory"), IQ(null, null, "Iraq"), IR(null, null, "Iran, Islamic Republic of"), IS(
        null, null, "Iceland"), IT(null, null, "Italy"), JE(null, null, "Jersey"), JM(null, null, "Jamaica"), JO(null,
        null, "Jordan"), JP(null, null, "Japan"), KE(null, null, "Kenya"), KG(null, null, "Kyrgyzstan"), KH(null, null,
        "Cambodia"), KI(null, null, "Kiribati"), KM(null, null, "Comoros"), KN(null, null, "Saint Kitts and Nevis"), KP(
        null, null, "Korea, Democratic People's Republic of"), KR(null, null, "Korea, Republic of"), KW(null, null,
        "Kuwait"), KY(null, null, "Cayman Islands"), KZ(null, null, "Kazakhstan"), LA(null, null,
        "Lao People's Democratic Republic"), LB(null, null, "Lebanon"), LC(null, null, "Saint Lucia"), LI(null, null,
        "Liechtenstein"), LK(null, null, "Sri Lanka"), LR(null, null, "Liberia"), LS(null, null, "Lesotho"), LT(null,
        null, "Lithuania"), LU(null, null, "Luxembourg"), LV(null, null, "Latvia"), LY(null, null,
        "Libyan Arab Jamahiriya"), MA(null, null, "Morocco"), MC(null, null, "Monaco"), MD(null, null,
        "Moldova, Republic of"), ME(null, null, "Montenegro"), MF(null, null, "Saint Martin"), MG(null, null,
        "Madagascar"), MH(null, null, "Marshall Islands"), MK(null, null, "Macedonia, The Former Yugoslav Republic of"), ML(
        null, null, "Mali"), MM(null, null, "Myanmar"), MN(null, null, "Mongolia"), MO(null, null, "Macao"), MP(null,
        null, "Northern Mariana Islands"), MQ(null, null, "Martinique"), MR(null, null, "Mauritania"), MS(null, null,
        "Montserrat"), MT(null, null, "Malta"), MU(null, null, "Mauritius"), MV(null, null, "Maldives"), MW(null, null,
        "Malawi"), MX(null, null, "Mexico"), MY(null, null, "Malaysia"), MZ(null, null, "Mozambique"), NA(null, null,
        "Namibia"), NC(null, null, "New Caledonia"), NE(null, null, "Niger"), NF(null, null, "Norfolk Island"), NG(
        null, null, "Nigeria"), NI(null, null, "Nicaragua"), NL(null, null, "Netherlands"), NO(null, null, "Norway"), NP(
        null, null, "Nepal"), NR(null, null, "Nauru"), NT("1993-07-12", null, "Neutral Zone"), NU(null, null, "Niue"), NZ(
        null, null, "New Zealand"), OM(null, null, "Oman"), PA(null, null, "Panama"), PE(null, null, "Peru"), PF(null,
        null, "French Polynesia"), PG(null, null, "Papua New Guinea"), PH(null, null, "Philippines"), PK(null, null,
        "Pakistan"), PL(null, null, "Poland"), PM(null, null, "Saint Pierre and Miquelon"), PN(null, null, "Pitcairn"), PR(
        null, null, "Puerto Rico"), PS(null, null, "Palestinian Territory, Occupied"), PT(null, null, "Portugal"), PW(
        null, null, "Palau"), PY(null, null, "Paraguay"), QA(null, null, "Qatar"), QM(null, null, "PRIVATE USE"), QN(
        null, null, "PRIVATE USE"), QO(null, null, "PRIVATE USE"), QP(null, null, "PRIVATE USE"), QQ(null, null,
        "PRIVATE USE"), QR(null, null, "PRIVATE USE"), QS(null, null, "PRIVATE USE"), QT(null, null, "PRIVATE USE"), QU(
        null, null, "PRIVATE USE"), QV(null, null, "PRIVATE USE"), QW(null, null, "PRIVATE USE"), QX(null, null,
        "PRIVATE USE"), QY(null, null, "PRIVATE USE"), QZ(null, null, "PRIVATE USE"), RE(null, null, "R&#xE9;union"), RO(
        null, null, "Romania"), RS(null, null, "Serbia"), RU(null, null, "Russian Federation"), RW(null, null, "Rwanda"), SA(
        null, null, "Saudi Arabia"), SB(null, null, "Solomon Islands"), SC(null, null, "Seychelles"), SD(null, null,
        "Sudan"), SE(null, null, "Sweden"), SG(null, null, "Singapore"), SH(null, null, "Saint Helena"), SI(null, null,
        "Slovenia"), SJ(null, null, "Svalbard and Jan Mayen"), SK(null, null, "Slovakia"), SL(null, null,
        "Sierra Leone"), SM(null, null, "San Marino"), SN(null, null, "Senegal"), SO(null, null, "Somalia"), SR(null,
        null, "Suriname"), ST(null, null, "Sao Tome and Principe"), SU("1992-08-30", null,
        "Union of Soviet Socialist Republics"), SV(null, null, "El Salvador"), SY(null, null, "Syrian Arab Republic"), SZ(
        null, null, "Swaziland"), TC(null, null, "Turks and Caicos Islands"), TD(null, null, "Chad"), TF(null, null,
        "French Southern Territories"), TG(null, null, "Togo"), TH(null, null, "Thailand"), TJ(null, null, "Tajikistan"), TK(
        null, null, "Tokelau"), TL(null, null, "Timor-Leste"), TM(null, null, "Turkmenistan"), TN(null, null, "Tunisia"), TO(
        null, null, "Tonga"), TP("2002-11-15", "TL", "East Timor"), TR(null, null, "Turkey"), TT(null, null,
        "Trinidad and Tobago"), TV(null, null, "Tuvalu"), TW(null, null, "Taiwan, Province of China"), TZ(null, null,
        "Tanzania, United Republic of"), UA(null, null, "Ukraine"), UG(null, null, "Uganda"), UM(null, null,
        "United States Minor Outlying Islands"), US(null, null, "United States"), UY(null, null, "Uruguay"), UZ(null,
        null, "Uzbekistan"), VA(null, null, "Holy See (Vatican City State)"), VC(null, null,
        "Saint Vincent and the Grenadines"), VE(null, null, "Venezuela"), VG(null, null, "Virgin Islands, British"), VI(
        null, null, "Virgin Islands, U.S."), VN(null, null, "Viet Nam"), VU(null, null, "Vanuatu"), WF(null, null,
        "Wallis and Futuna"), WS(null, null, "Samoa"), XA(null, null, "PRIVATE USE"), XB(null, null, "PRIVATE USE"), XC(
        null, null, "PRIVATE USE"), XD(null, null, "PRIVATE USE"), XE(null, null, "PRIVATE USE"), XF(null, null,
        "PRIVATE USE"), XG(null, null, "PRIVATE USE"), XH(null, null, "PRIVATE USE"), XI(null, null, "PRIVATE USE"), XJ(
        null, null, "PRIVATE USE"), XK(null, null, "PRIVATE USE"), XL(null, null, "PRIVATE USE"), XM(null, null,
        "PRIVATE USE"), XN(null, null, "PRIVATE USE"), XO(null, null, "PRIVATE USE"), XP(null, null, "PRIVATE USE"), XQ(
        null, null, "PRIVATE USE"), XR(null, null, "PRIVATE USE"), XS(null, null, "PRIVATE USE"), XT(null, null,
        "PRIVATE USE"), XU(null, null, "PRIVATE USE"), XV(null, null, "PRIVATE USE"), XW(null, null, "PRIVATE USE"), XX(
        null, null, "PRIVATE USE"), XY(null, null, "PRIVATE USE"), XZ(null, null, "PRIVATE USE"), YD("1990-08-14",
        "YE", "Yemen, Democratic"), YE(null, null, "Yemen"), YT(null, null, "Mayotte"), YU("2003-07-23", "CS",
        "Yugoslavia"), ZA(null, null, "South Africa"), ZM(null, null, "Zambia"), ZR("1997-07-14", "CD", "Zaire"), ZW(
        null, null, "Zimbabwe"), ZZ(null, null, "PRIVATE USE"), UN001(null, null, "World"), UN002(null, null, "Africa"), UN005(
        null, null, "South America"), UN009(null, null, "Oceania"), UN011(null, null, "Western Africa"), UN013(null,
        null, "Central America"), UN014(null, null, "Eastern Africa"), UN015(null, null, "Northern Africa"), UN017(
        null, null, "Middle Africa"), UN018(null, null, "Southern Africa"), UN019(null, null, "Americas"), UN021(null,
        null, "Northern America"), UN029(null, null, "Caribbean"), UN030(null, null, "Eastern Asia"), UN034(null, null,
        "Southern Asia"), UN035(null, null, "South-Eastern Asia"), UN039(null, null, "Southern Europe"), UN053(null,
        null, "Australia and New Zealand"), UN054(null, null, "Melanesia"), UN057(null, null, "Micronesia"), UN061(
        null, null, "Polynesia"), UN142(null, null, "Asia"), UN143(null, null, "Central Asia"), UN145(null, null,
        "Western Asia"), UN150(null, null, "Europe"), UN151(null, null, "Eastern Europe"), UN154(null, null,
        "Northern Europe"), UN155(null, null, "Western Europe"), UN419(null, null, "Latin America and the Caribbean");

    private final String deprecated;
    private final String preferred;
    private final String[] descriptions;

    private Region(String dep, String pref, String... desc) {
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

    public Region getPreferred() {
        return preferred != null ? valueOf(preferred.toUpperCase(Locale.US)) : this;
    }

    public String getPreferredValue() {
        return preferred;
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

    public static Region valueOf(Subtag subtag) {
        if (subtag == null)
            return null;
        if (subtag.getType() == Subtag.Type.REGION) {
            String name = subtag.getName();
            if (name.length() == 3)
                name = "UN" + name;
            else
                name = name.toUpperCase(Locale.US);
            return valueOf(name);
        } else
            throw new IllegalArgumentException("Wrong subtag type");
    }

}
