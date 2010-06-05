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
package org.apache.abdera.i18n.rfc4646;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.rfc4646.Subtag.Type;

/**
 * Implementation of RFC 4646 Language Tags
 */
public final class Lang extends SubtagSet {

    private static final long serialVersionUID = -7095560018906537331L;
    private final Locale locale;

    /**
     * Create a Lang object using the default locale
     */
    public Lang() {
        this(init(Locale.getDefault()));
    }

    /**
     * Create a Lang object using the specified locale
     */
    public Lang(Locale locale) {
        this(init(locale));
    }

    private static Subtag init(Locale locale) {
        try {
            return parse(locale.toString()).primary;
        } catch (Exception e) {
            Subtag c = null, primary = new Subtag(Type.PRIMARY, locale.getLanguage());
            String country = locale.getCountry();
            String variant = locale.getVariant();
            if (country != null)
                c = new Subtag(Type.REGION, country, primary);
            if (variant != null)
                new Subtag(Type.VARIANT, variant, c);
            return primary;
        }
    }

    /**
     * Create a lang object
     */
    public Lang(String lang) {
        this(parse(lang).primary);
    }

    Lang(Subtag primary) {
        super(primary);
        this.locale = initLocale();
    }

    private Locale initLocale() {
        Subtag primary = getLanguage();
        Subtag region = getRegion();
        Subtag variant = getVariant();
        if (variant != null && region != null)
            return new Locale(primary.toString(), region.toString(), variant.toString());
        else if (region != null)
            return new Locale(primary.toString(), region.toString());
        else
            return new Locale(primary.toString());
    }

    /**
     * Get the Language subtag
     */
    public Subtag getLanguage() {
        return primary;
    }

    /**
     * Get a Locale object derived from this language tag
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get the Extlang tag. If there are multiple extlang tags, this will return the first one. The rest can be
     * retrieved by following Subtag.getNext()
     */
    public Subtag getExtLang() {
        for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY:
                    break;
                case EXTLANG:
                    return subtag;
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Get the Script subtag
     */
    public Subtag getScript() {
        for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY:
                case EXTLANG:
                    break;
                case SCRIPT:
                    return subtag;
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Get the Region subtag
     */
    public Subtag getRegion() {
        for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY:
                case EXTLANG:
                case SCRIPT:
                    break;
                case REGION:
                    return subtag;
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Get the Variant subtag
     */
    public Subtag getVariant() {
        for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY:
                case EXTLANG:
                case SCRIPT:
                case REGION:
                    break;
                case VARIANT:
                    return subtag;
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Get the beginning of the extension section. This will return the first prefix subtag of the first set of
     * extension subtags.
     */
    public Subtag getExtension() {
        for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY:
                case EXTLANG:
                case SCRIPT:
                case REGION:
                case VARIANT:
                    break;
                case EXTENSION:
                    return subtag.getPrevious();
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Get the beginning of the private-use section. This will return the x prefix subtag
     */
    public Subtag getPrivateUse() {
        for (Subtag subtag : this) {
            switch (subtag.getType()) {
                case PRIMARY:
                case EXTLANG:
                case SCRIPT:
                case VARIANT:
                case REGION:
                case EXTENSION:
                    break;
                case PRIVATEUSE:
                    return subtag.getPrevious();
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Get this Lang as a Language-Range for use with matching
     */
    public Range asRange() {
        return new Range(toString());
    }

    /**
     * Clone this Language tag
     */
    public Lang clone() {
        return new Lang(primary.clone());
    }

    /**
     * Produce a canonicalized copy of this lang tag
     */
    public Lang canonicalize() {
        Subtag primary = null, current = null;
        int p = -1, t = -1;
        List<Subtag> tags = new LinkedList<Subtag>();
        for (Subtag tag : this) {
            tags.add(tag);
        }
        List<Subtag> ext = new LinkedList<Subtag>();
        for (Subtag tag : tags) {
            if (tag.getType() == Subtag.Type.SINGLETON) {
                if (!tag.getName().equalsIgnoreCase("x")) {
                    ext.add(tag);
                }
            }
        }
        if (ext.size() > 0) {
            p = tags.indexOf(ext.get(0));
            t = tags.indexOf(ext.get(ext.size() - 1));
        }
        Collections.sort(ext, new Comparator<Subtag>() {
            public int compare(Subtag o1, Subtag o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        List<Subtag> extchain = new LinkedList<Subtag>();
        for (Subtag tag : ext) {
            extchain.add(tag);
            current = tag.getNext();
            while (current != null && current.getType() == Subtag.Type.EXTENSION) {
                extchain.add(current);
                current = current.getNext();
            }
        }
        List<Subtag> result = new LinkedList<Subtag>();
        result.addAll(tags.subList(0, p));
        result.addAll(extchain);
        result.addAll(tags.subList(t + 2, tags.size()));

        current = null;
        for (Subtag tag : result) {
            tag = tag.canonicalize();
            if (primary == null) {
                primary = tag;
                current = primary;
            } else {
                current.setNext(tag);
                current = tag;
            }
        }
        return new Lang(primary);
    }

    /**
     * Return true if this lang tag contains any deprecated subtags
     */
    public boolean isDeprecated() {
        for (Subtag tag : this)
            if (tag.isDeprecated())
                return true;
        return false;
    }

    /**
     * Get a Lang tag that drops the last subtag
     */
    public Lang getParent() {
        Lang lang = clone();
        Subtag last = null;
        for (Subtag tag : lang)
            last = tag;
        if (last.getPrevious() == null)
            return null;
        last.getPrevious().setNext(null);
        return lang;
    }

    /**
     * Return true if the specified lang tag is the parent of this one
     */
    public boolean isChildOf(Lang lang) {
        Range range = new Range(lang).appendWildcard();
        return range.matches(this);
    }

    /**
     * Return true if the specified lang tag is the child of this one
     */
    public boolean isParentOf(Lang lang) {
        return lang.isChildOf(this);
    }

    // Parsing Logic

    private static final String language = "((?:[a-zA-Z]{2,3}(?:[-_][a-zA-Z]{3}){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8})";
    private static final String script = "((?:[-_][a-zA-Z]{4})?)";
    private static final String region = "((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})))?)";
    private static final String variant = "((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})))*)";
    private static final String extension = "((?:[-_][a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)*)";
    private static final String privateuse = "[xX](?:[-_][a-zA-Z0-9]{2,8})+";
    private static final String _privateuse = "((?:[-_]" + privateuse + ")?)";
    private static final String grandfathered =
        "^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]wuu|zh[-_]xiang|zh[-_]yue)$";
    private static final String langtag = "^" + language + script + region + variant + extension + _privateuse + "$";

    private static final Pattern p_langtag = Pattern.compile(langtag);
    private static final Pattern p_privateuse = Pattern.compile("^" + privateuse + "$");
    private static final Pattern p_grandfathered = Pattern.compile(grandfathered);

    /**
     * Parse a Lang tag
     */
    public static Lang parse(String lang) {
        Subtag primary = null;
        Matcher m = p_grandfathered.matcher(lang);
        if (m.find()) {
            String[] tags = lang.split("[-_]");
            Subtag current = null;
            for (String tag : tags) {
                if (current == null) {
                    primary = new Subtag(Type.GRANDFATHERED, tag, null);
                    current = primary;
                } else {
                    current = new Subtag(Type.GRANDFATHERED, tag, current);
                }
            }
            return new Lang(primary);
        }
        m = p_privateuse.matcher(lang);
        if (m.find()) {
            String[] tags = lang.split("[-_]");
            Subtag current = null;
            for (String tag : tags) {
                if (current == null) {
                    primary = new Subtag(Type.SINGLETON, tag, null);
                    current = primary;
                } else {
                    current = new Subtag(Type.PRIVATEUSE, tag, current);
                }
            }
            return new Lang(primary);
        }
        m = p_langtag.matcher(lang);
        if (m.find()) {
            String langtag = m.group(1);
            String script = m.group(2);
            String region = m.group(3);
            String variant = m.group(4);
            String extension = m.group(5);
            String privateuse = m.group(6);
            Subtag current = null;
            String[] tags = langtag.split("[-_]");
            for (String tag : tags) {
                if (current == null) {
                    primary = new Subtag(Type.PRIMARY, tag);
                    current = primary;
                } else {
                    current = new Subtag(Type.EXTLANG, tag, current);
                }
            }
            if (script != null && script.length() > 0)
                current = new Subtag(Type.SCRIPT, script.substring(1), current);
            if (region != null && region.length() > 0)
                current = new Subtag(Type.REGION, region.substring(1), current);
            if (variant != null && variant.length() > 0) {
                variant = variant.substring(1);
                tags = variant.split("-");
                for (String tag : tags)
                    current = new Subtag(Type.VARIANT, tag, current);
            }
            if (extension != null && extension.length() > 0) {
                extension = extension.substring(1);
                tags = extension.split("-");
                current = new Subtag(Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; i++) {
                    String tag = tags[i];
                    current = new Subtag(tag.length() == 1 ? Type.SINGLETON : Type.EXTENSION, tag, current);
                }
            }
            if (privateuse != null && privateuse.length() > 0) {
                privateuse = privateuse.substring(1);
                tags = privateuse.split("-");
                current = new Subtag(Type.SINGLETON, tags[0], current);
                for (int i = 1; i < tags.length; i++) {
                    current = new Subtag(Type.PRIVATEUSE, tags[i], current);
                }
            }
            return new Lang(primary);
        }
        throw new IllegalArgumentException();
    }

    public static String fromLocale(Locale locale) {
        return new Lang(locale).toString();
    }
}
