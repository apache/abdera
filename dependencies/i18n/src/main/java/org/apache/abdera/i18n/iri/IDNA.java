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
package org.apache.abdera.i18n.iri;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.i18n.text.Nameprep;
import org.apache.abdera.i18n.text.Punycode;

/**
 * Provides an Internationized Domain Name implementation
 */
public final class IDNA implements Serializable, Cloneable {

    private static final long serialVersionUID = -617056657751424334L;
    private final String regname;

    public IDNA(java.net.InetAddress addr) {
        this(addr.getHostName());
    }

    public IDNA(String regname) {
        this.regname = toUnicode(regname);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toASCII() {
        return toASCII(regname);
    }

    public String toUnicode() {
        return toUnicode(regname);
    }

    public java.net.InetAddress getInetAddress() throws UnknownHostException {
        return java.net.InetAddress.getByName(toASCII());
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((regname == null) ? 0 : regname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IDNA other = (IDNA)obj;
        if (regname == null) {
            if (other.regname != null)
                return false;
        } else if (!regname.equals(other.regname))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return toUnicode();
    }

    public static boolean equals(String idn1, String idn2) {
        return toUnicode(idn1).equals(toUnicode(idn2));
    }

    public static String toASCII(String regname) {
        try {
            if (regname == null){
                return null;
            }
            if (regname.length() == 0){
                return regname;
            }
            String[] labels = regname.split("\\\u002E");
            StringBuilder buf = new StringBuilder();
            for (String label : labels) {
                label = Nameprep.prep(label);
                char[] chars = label.toCharArray();
                CharUtils.verifyNot(chars, Profile.STD3ASCIIRULES);
                if (chars[0] == '\u002D' || chars[chars.length - 1] == '\u002D')
                    throw new IOException("ToASCII violation");
                if (!CharUtils.inRange(chars, (char)0x000, (char)0x007F)) {
                    if (label.startsWith("xn--"))
                        throw new IOException("ToASCII violation");
                    String pc = "xn--" + Punycode.encode(chars, null);
                    chars = pc.toCharArray();
                }
                if (chars.length > 63)
                    throw new IOException("ToASCII violation");
                if (buf.length() > 0)
                    buf.append('\u002E');
                buf.append(chars);
            }
            return buf.toString();
        } catch (IOException e) {
            return regname;
        }
    }

    public static String toUnicode(String regname) {
        if (regname == null)
            return null;
        if (regname.length() == 0)
            return regname;
        String[] labels = regname.split("\\\u002E");
        StringBuilder buf = new StringBuilder();
        for (String label : labels) {
            char[] chars = label.toCharArray();
            if (!CharUtils.inRange(chars, (char)0x000, (char)0x007F)) {
                label = Nameprep.prep(label);
                chars = label.toCharArray();
            }
            if (label.startsWith("xn--")) {
                label = Punycode.decode(label.substring(4));
                chars = label.toCharArray();
            }
            if (buf.length() > 0)
                buf.append('\u002E');
            buf.append(chars);
        }
        String check = toASCII(buf.toString());
        if (check.equalsIgnoreCase(regname))
            return buf.toString();
        else
            return regname;
    }

}
