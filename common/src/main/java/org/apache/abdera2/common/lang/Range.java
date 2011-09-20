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
package org.apache.abdera2.common.lang;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera2.common.lang.Subtag.Type;

/**
 * A language range used for matching language tags
 */
public final class Range 
  extends SubtagSet {

    private static final long serialVersionUID = -6397227794306856431L;
    private final boolean extended;

    public Range(String range, boolean extended) {
        super(parse(range, extended).root);
        this.extended = extended;
    }

    public Range(String range) {
        this(parse(range).root);
    }

    public Range(Lang lang) {
        this(lang.toString());
    }

    public Range(Lang lang, boolean extended) {
        this(lang.toString(), extended);
    }

    Range(Subtag primary) {
        super(primary);
        this.extended = !checkBasic();
    }

    public Range append(Subtag subtag) {
        Subtag last = null;
        for (Subtag tag : this)
            last = tag;
        last.setNext(subtag);
        return this;
    }

    public Range appendWildcard() {
        return append(Subtag.newWildcard());
    }

    public Range toBasicRange() {
        if (root.type() == Subtag.Type.WILDCARD) {
            return new Range("*");
        } else {
            List<Subtag> list = new LinkedList<Subtag>();
            for (Subtag tag : this) {
                if (tag.type() != Subtag.Type.WILDCARD)
                    list.add(new Subtag(tag.type(),tag.name(),null));
            }
            Subtag primary = null, current = null;
            for (Subtag tag : list) {
                tag.setNext(null);
                tag.setPrevious(null);
                if (primary == null) {
                    primary = tag;
                    current = primary;
                } else {
                    current.setNext(tag);
                    current = tag;
                }
            }
            return new Range(primary);
        }
    }

    public boolean isBasic() {
        return !extended;
    }

    private boolean checkBasic() {
        Subtag current = root.next();
        while (current != null) {
            if (current.type() == Subtag.Type.WILDCARD)
                return false;
            current = current.next();
        }
        return true;
    }

    public boolean matches(String lang) {
        return matches(new Lang(lang), extended);
    }

    public boolean matches(String lang, boolean extended) {
        return matches(new Lang(lang), extended);
    }

    public boolean matches(Lang lang) {
        return matches(lang, false);
    }

    public boolean matches(Lang lang, boolean extended) {
        Iterator<Subtag> i = iterator();
        Iterator<Subtag> e = lang.iterator();
        if (isBasic() && !extended) {
            if (root.type() == Subtag.Type.WILDCARD)
                return true;
            for (; i.hasNext() && e.hasNext();) {
                Subtag in = i.next();
                Subtag en = e.next();
                if (!in.equals(en))
                    return false;
            }
            return true;
        } else {
            Subtag icurrent = i.next();
            Subtag ecurrent = e.next();
            if (!icurrent.equals(ecurrent))
                return false;

            while (i.hasNext()) {
                icurrent = i.next();
                while (icurrent.type() == Subtag.Type.WILDCARD && i.hasNext())
                    icurrent = i.next();
                // the range ends in a wildcard so it will match everything beyond this point
                if (icurrent.type() == Subtag.Type.WILDCARD)
                    return true;
                boolean matched = false;
                while (e.hasNext()) {
                    ecurrent = e.next();
                    if (extended && (ecurrent.type().ordinal() < icurrent.type().ordinal()))
                        continue;
                    if (!ecurrent.equals(icurrent))
                        break;
                    else {
                        matched = true;
                        break;
                    }
                }
                if (!matched)
                    return false;
            }
            return true;
        }
    }

    public Lang[] filter(Lang... lang) {
        List<Lang> langs = new LinkedList<Lang>();
        for (Lang l : lang)
            if (matches(l))
                langs.add(l);
        return langs.toArray(new Lang[langs.size()]);
    }

    public String[] filter(String... lang) {
        List<String> langs = new LinkedList<String>();
        for (String l : lang)
            if (matches(l))
                langs.add(l);
        return langs.toArray(new String[langs.size()]);
    }

    public static Lang[] filter(String range, Lang... lang) {
        return new Range(range).filter(lang);
    }

    public static String[] filter(String range, String... lang) {
        return new Range(range).filter(lang);
    }

    public static boolean matches(String range, Lang lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    public static boolean matches(String range, Lang lang) {
        return new Range(range).matches(lang);
    }

    public static boolean matches(String range, String lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    public static boolean matches(String range, String lang) {
        return new Range(range).matches(lang);
    }

    // Parsing logic //

    private static final String SEP = "\\s*[-_]\\s*";
    private static final String range = "((?:[a-zA-Z]{1,8}|\\*))((?:[-_](?:[a-zA-Z0-9]{1,8}|\\*))*)";
    private static final String range_component = "[-_]((?:[a-zA-Z0-9]{1,8}|\\*))";
    private static final Pattern p_range = Pattern.compile(range);
    private static final Pattern p_range_component = Pattern.compile(range_component);

    private static final String language =
        "((?:[a-zA-Z]{2,3}(?:[-_](?:[a-zA-Z]{3}|\\*)){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8}|\\*)";
    private static final String script = "((?:[-_](?:[a-zA-Z]{4}|\\*))?)";
    private static final String region = "((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})|\\*))?)";
    private static final String variant = "((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})|\\*))*)";
    private static final String extension = "((?:[-_](?:(?:[a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)|\\*))*)";
    private static final String privateuse = "[xX](?:[-_][a-zA-Z0-9]{2,8})+";
    private static final String _privateuse = "((?:[-_](?:" + privateuse + ")+|\\*)?)";
    private static final String langtag = "^" + language + script + region + variant + extension + _privateuse + "$";
    private static final String grandfathered =
        "^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$";
    private static final Pattern p_privateuse = Pattern.compile("^" + privateuse + "$");
    private static final Pattern p_grandfathered = Pattern.compile(grandfathered);
    private static final Pattern p_extended_range = Pattern.compile(langtag);

    /**
     * Parse the language-range
     */
    public static Range parse(String range) {
        return parse(range, false);
    }

    /**
     * Parse the language-range
     * 
     * @param range The language-range
     * @param extended true to use extended language rules
     */
    public static Range parse(String range, boolean extended) {
        if (!extended) {
            Subtag primary = null, current = null;
            Matcher m = p_range.matcher(range);
            if (m.find()) {
                String first = m.group(1);
                String therest = m.group(2);
                current = primary =
                    new Subtag(first.equals("*") ? 
                        Subtag.Type.WILDCARD : 
                        Subtag.Type.SIMPLE, first
                        .toLowerCase(Locale.US));
                Matcher n = p_range_component.matcher(therest);
                while (n.find()) {
                    String name = n.group(1).toLowerCase(Locale.US);
                    current = new Subtag(
                        name.equals("*") ? 
                            Subtag.Type.WILDCARD : 
                            Subtag.Type.SIMPLE, 
                        name, 
                        current);
                }
            }
            return new Range(primary);
        } else {

            Subtag primary = null;
            Matcher m = p_grandfathered.matcher(range);
            if (m.find()) {
                String[] tags = range.split(SEP);
                Subtag current = null;
                for (String tag : tags)
                    current = current == null ?
                        primary = new Subtag(Type.GRANDFATHERED, tag) :
                        new Subtag(Type.GRANDFATHERED,tag,current);
                return new Range(primary);
            }
            m = p_privateuse.matcher(range);
            if (m.find()) {
                String[] tags = range.split(SEP);
                Subtag current = null;
                for (String tag : tags)
                    current = current == null ?
                        primary = new Subtag(tag.equals("*") ? Type.WILDCARD : Type.SINGLETON, tag) :
                        new Subtag(tag.equals("*") ? Type.WILDCARD : Type.PRIVATEUSE, tag, current);
                return new Range(primary);
            }
            m = p_extended_range.matcher(range);
            if (m.find()) {
                String langtag = m.group(1);
                String script = m.group(2);
                String region = m.group(3);
                String variant = m.group(4);
                String extension = m.group(5);
                String privateuse = m.group(6);
                Subtag current = null;
                String[] tags = langtag.split(SEP);
                for (String tag : tags)
                    current = current == null ?
                        primary = new Subtag(tag.equals("*") ? Type.WILDCARD : Type.LANGUAGE, tag) :
                        new Subtag(tag.equals("*") ? Type.WILDCARD : Type.EXTLANG, tag, current);
                if (script != null && script.length() > 0)
                    current =
                        new Subtag(
                            script.substring(1).equals("*") ? 
                                Type.WILDCARD : 
                                Type.SCRIPT, 
                            script.substring(1),
                            current);
                if (region != null && region.length() > 0)
                    current =
                        new Subtag(
                            region.substring(1).equals("*") ? 
                                Type.WILDCARD : 
                                Type.REGION, 
                            region.substring(1),
                            current);
                if (variant != null && variant.length() > 0) {
                    variant = variant.substring(1);
                    tags = variant.split(SEP);
                    for (String tag : tags)
                        current = new Subtag(tag.equals("*") ? Type.WILDCARD : Type.VARIANT, tag, current);
                }
                if (extension != null && extension.length() > 0) {
                    extension = extension.substring(1);
                    tags = extension.split(SEP);
                    current = new Subtag(tags[0].equals("*") ? Type.WILDCARD : Type.SINGLETON, tags[0], current);
                    for (int i = 1; i < tags.length; i++) {
                        String tag = tags[i];
                        current =
                            new Subtag(tag.equals("*") ? Type.WILDCARD : tag.length() == 1 ? Type.SINGLETON
                                : Type.EXTENSION, tag, current);
                    }
                }
                if (privateuse != null && privateuse.length() > 0) {
                    privateuse = privateuse.substring(1);
                    tags = privateuse.split(SEP);
                    current = new Subtag(tags[0].equals("*") ? Type.WILDCARD : Type.SINGLETON, tags[0], current);
                    for (int i = 1; i < tags.length; i++) {
                        current = new Subtag(tags[i].equals("*") ? Type.WILDCARD : Type.PRIVATEUSE, tags[i], current);
                    }
                }
                return new Range(primary);
            }
        }
        throw new IllegalArgumentException("Invalid range");
    }

}
