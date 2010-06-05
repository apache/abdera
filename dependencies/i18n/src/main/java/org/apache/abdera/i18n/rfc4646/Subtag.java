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

import java.io.Serializable;
import java.util.Locale;

import org.apache.abdera.i18n.rfc4646.enums.Extlang;
import org.apache.abdera.i18n.rfc4646.enums.Language;
import org.apache.abdera.i18n.rfc4646.enums.Region;
import org.apache.abdera.i18n.rfc4646.enums.Script;
import org.apache.abdera.i18n.rfc4646.enums.Singleton;
import org.apache.abdera.i18n.rfc4646.enums.Variant;

/**
 * A Lang tag subtag
 */
public final class Subtag implements Serializable, Cloneable, Comparable<Subtag> {

    private static final long serialVersionUID = -4496128268514329138L;

    public enum Type {
        /** Primary language subtag **/
        PRIMARY,
        /** Extended Language subtag **/
        EXTLANG,
        /** Script subtag **/
        SCRIPT,
        /** Region subtag **/
        REGION,
        /** Variant subtag **/
        VARIANT,
        /** Singleton subtag **/
        SINGLETON,
        /** Extension subtag **/
        EXTENSION,
        /** Primary-use subtag **/
        PRIVATEUSE,
        /** Grandfathered subtag **/
        GRANDFATHERED,
        /** Wildcard subtag ("*") **/
        WILDCARD,
        /** Simple subtag (ranges) **/
        SIMPLE
    }

    private final Type type;
    private final String name;
    private Subtag prev;
    private Subtag next;

    /**
     * Create a Subtag
     */
    public Subtag(Language language) {
        this(Type.PRIMARY, language.name().toLowerCase(Locale.US));
    }

    /**
     * Create a Subtag
     */
    public Subtag(Script script) {
        this(Type.SCRIPT, toTitleCase(script.name()));
    }

    /**
     * Create a Subtag
     */
    public Subtag(Region region) {
        this(Type.REGION, getRegionName(region.name()));
    }

    private static String getRegionName(String name) {
        return name.startsWith("UN") && name.length() == 5 ? name.substring(2) : name;
    }

    /**
     * Create a Subtag
     */
    public Subtag(Variant variant) {
        this(Type.VARIANT, getVariantName(variant.name().toLowerCase(Locale.US)));
    }

    private static String getVariantName(String name) {
        return name.startsWith("_") ? name.substring(1) : name;
    }

    /**
     * Create a Subtag
     */
    public Subtag(Extlang extlang) {
        this(Type.EXTLANG, extlang.name().toLowerCase(Locale.US));
    }

    /**
     * Create a Subtag
     */
    public Subtag(Singleton singleton) {
        this(Type.SINGLETON, singleton.name().toLowerCase(Locale.US));
    }

    /**
     * Create a Subtag
     */
    public Subtag(Type type, String name) {
        this(type, name, null);
    }

    Subtag() {
        this(Type.WILDCARD, "*");
    }

    /**
     * Create a Subtag
     */
    public Subtag(Type type, String name, Subtag prev) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        if (prev != null)
            prev.setNext(this);
    }

    Subtag(Type type, String name, Subtag prev, Subtag next) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        this.next = next;
    }

    /**
     * Get the subtag type
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the subtag value
     */
    public String getName() {
        return toString();
    }

    void setPrevious(Subtag prev) {
        this.prev = prev;
    }

    /**
     * Get the previous subtag
     */
    public Subtag getPrevious() {
        return prev;
    }

    void setNext(Subtag next) {
        this.next = next;
        if (next != null)
            next.setPrevious(this);
    }

    /**
     * Get the next subtag
     */
    public Subtag getNext() {
        return next;
    }

    public String toString() {
        switch (type) {
            case PRIMARY:
                return name.toLowerCase(Locale.US);
            case REGION:
                return name.toUpperCase(Locale.US);
            case SCRIPT:
                return toTitleCase(name);
            default:
                return name.toLowerCase(Locale.US);
        }
    }

    private static String toTitleCase(String string) {
        if (string == null)
            return null;
        if (string.length() == 0)
            return string;
        char[] chars = string.toLowerCase(Locale.US).toCharArray();
        chars[0] = (char)(chars[0] - 32);
        return new String(chars);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.toLowerCase(Locale.US).hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Subtag other = (Subtag)obj;
        if (other.getType() == Type.WILDCARD || getType() == Type.WILDCARD)
            return true;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equalsIgnoreCase(other.name))
            return false;
        if (other.getType() == Type.SIMPLE || getType() == Type.SIMPLE)
            return true;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    public Subtag clone() {
        try {
            Subtag tag = (Subtag)super.clone();
            if (getNext() != null)
                tag.setNext(getNext().clone());
            return tag;
        } catch (CloneNotSupportedException e) {
            return new Subtag(type, name, prev != null ? prev.clone() : null, next != null ? next.clone() : null); // not
                                                                                                                   // going
                                                                                                                   // to
                                                                                                                   // happen
        }
    }

    /**
     * True if this subtag has been deprecated
     */
    public boolean isDeprecated() {
        switch (type) {
            case PRIMARY: {
                Language e = getEnum();
                return e.isDeprecated();
            }
            case SCRIPT: {
                Script e = getEnum();
                return e.isDeprecated();
            }
            case REGION: {
                Region e = getEnum();
                return e.isDeprecated();
            }
            case VARIANT: {
                Variant e = getEnum();
                return e.isDeprecated();
            }
            case EXTLANG: {
                Extlang e = getEnum();
                return e.isDeprecated();
            }
            case EXTENSION: {
                Singleton e = getEnum();
                return e.isDeprecated();
            }
            default:
                return false;
        }
    }

    /**
     * Get this subtags Enum, allowing the subtag to be verified
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> T getEnum() {
        switch (type) {
            case PRIMARY:
                return (T)Language.valueOf(this);
            case SCRIPT:
                return (T)Script.valueOf(this);
            case REGION:
                return (T)Region.valueOf(this);
            case VARIANT:
                return (T)Variant.valueOf(this);
            case EXTLANG:
                return (T)Extlang.valueOf(this);
            case EXTENSION:
                return (T)Singleton.valueOf(this);
            default:
                return null;
        }
    }

    /**
     * True if this subtag is valid
     */
    public boolean isValid() {
        switch (type) {
            case PRIMARY:
            case SCRIPT:
            case REGION:
            case VARIANT:
            case EXTLANG:
                try {
                    getEnum();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            case EXTENSION:
                return name.matches("[A-Za-z0-9]{2,8}");
            case GRANDFATHERED:
                return name.matches("[A-Za-z]{1,3}(?:-[A-Za-z0-9]{2,8}){1,2}");
            case PRIVATEUSE:
                return name.matches("[A-Za-z0-9]{1,8}");
            case SINGLETON:
                return name.matches("[A-Za-z]");
            case WILDCARD:
                return name.equals("*");
            case SIMPLE:
                return name.matches("[A-Za-z0-9]{1,8}");
            default:
                return false;
        }
    }

    /**
     * Return the canonicalized version of this subtag
     */
    public Subtag canonicalize() {
        switch (type) {
            case REGION:
                Region region = getEnum();
                return region.getPreferred().newSubtag();
            case PRIMARY:
                Language language = getEnum();
                return language.getPreferred().newSubtag();
            case SCRIPT:
                Script script = getEnum();
                return script.getPreferred().newSubtag();
            case VARIANT:
                Variant variant = getEnum();
                return variant.getPreferred().newSubtag();
            case EXTLANG:
                Extlang extlang = getEnum();
                return extlang.getPreferred().newSubtag();
            case EXTENSION:
            case GRANDFATHERED:
            case PRIVATEUSE:
            case SINGLETON:
            default:
                return this;
        }
    }

    /**
     * Create a new wildcard subtag
     */
    public static Subtag newWildcard() {
        return new Subtag();
    }

    public int compareTo(Subtag o) {
        int c = o.type.compareTo(type);
        return c != 0 ? c : o.name.compareTo(name);
    }
}
