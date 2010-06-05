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
package org.apache.abdera.i18n.lang;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.InvalidCharacterException;
import org.apache.abdera.i18n.text.CharUtils.Profile;

/**
 * rfc3066 implementation. This has been deprecated. Use org.apache.abdera.i18n.rfc4646.Lang instead
 * 
 * @deprecated
 * @see org.apache.abdera.i18n.rfc4646.Lang
 */
public class Lang implements Iterable<String>, Serializable, Cloneable {

    public static final Lang ANY = new Lang();

    private static final long serialVersionUID = -4620499451615533855L;
    protected final String[] tags;
    protected final Locale locale;

    private Lang() {
        tags = new String[] {"*"};
        locale = null;
    }

    public Lang(Locale locale) {
        this.tags = locale.toString().replace("\u005F", "\u002D").split("\u002D");
        this.locale = locale;
    }

    public Lang(String tag) {
        this(parse(tag));
    }

    public Lang(String... tags) {
        verify(tags);
        this.tags = tags;
        this.locale = initLocale();
    }

    private Locale initLocale() {
        Locale locale = null;
        switch (tags.length) {
            case 0:
                break;
            case 1:
                locale = new Locale(tags[0]);
                break;
            case 2:
                locale = new Locale(tags[0], tags[1]);
                break;
            default:
                locale = new Locale(tags[0], tags[1], tags[2]);
                break;
        }
        return locale;
    }

    public String getPrimary() {
        return tags[0];
    }

    public String getSubtag(int n) {
        if (n + 1 > tags.length)
            throw new ArrayIndexOutOfBoundsException(n);
        return tags[n + 1];
    }

    public int getSubtagCount() {
        return tags.length - 1;
    }

    public Locale getLocale() {
        return locale;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String s : tags) {
            if (buf.length() > 0)
                buf.append('\u002D');
            buf.append(s);
        }
        return buf.toString();
    }

    public static boolean matches(Lang lang, String range) {
        if (range.equals("*"))
            return true;
        return matches(lang, new Lang(range));
    }

    public static boolean matches(Lang lang, Lang range) {
        if (range.equals("*"))
            return true;
        if (lang.equals(range))
            return true;
        if (lang.tags.length <= range.tags.length)
            return false;
        for (int n = 0; n < range.tags.length; n++) {
            if (!lang.tags[n].equalsIgnoreCase(range.tags[n]))
                return false;
        }
        return true;
    }

    public boolean matches(String range) {
        return matches(this, range);
    }

    public boolean matches(Lang range) {
        return matches(this, range);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((locale == null) ? 0 : locale.hashCode());
        for (String tag : tags) {
            result = PRIME * result + tag.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof String) {
            String s = (String)obj;
            if (s.equals("*"))
                obj = ANY;
            else {
                try {
                    obj = new Lang(s);
                } catch (Exception e) {
                }
            }
        }
        if (getClass() != obj.getClass())
            return false;
        final Lang other = (Lang)obj;
        if (tags.length != other.tags.length)
            return false;
        for (int n = 0; n < tags.length; n++) {
            if (!tags[n].equalsIgnoreCase(other.tags[n]))
                return false;
        }
        return true;
    }

    private static void verify(String[] tags) {
        if (tags.length == 0)
            throw new InvalidLangTagSyntax();
        String primary = tags[0];
        try {
            CharUtils.verify(primary, Profile.ALPHA);
        } catch (InvalidCharacterException e) {
            throw new InvalidLangTagSyntax();
        }
        for (int n = 1; n < tags.length; n++) {
            try {
                CharUtils.verify(tags[n], Profile.ALPHANUM);
            } catch (InvalidCharacterException e) {
                throw new InvalidLangTagSyntax();
            }
        }
    }

    private static String[] parse(String tag) {
        String[] tags = tag.split("\u002D");
        verify(tags);
        return tags;
    }

    public Iterator<String> iterator() {
        return java.util.Arrays.asList(tags).iterator();
    }
}
