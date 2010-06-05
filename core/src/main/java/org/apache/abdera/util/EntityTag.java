/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. For additional
 * information regarding copyright in this work, please see the NOTICE file in
 * the top level directory of this distribution.
 */
package org.apache.abdera.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.i18n.text.Localizer;
import org.apache.commons.codec.binary.Hex;

/**
 * Implements an EntityTag.
 */
public class EntityTag implements Cloneable, Serializable, Comparable<EntityTag> {

    private static final long serialVersionUID = 1559972888659121461L;

    private static final Pattern PATTERN = Pattern.compile("(\\*)|([wW]/)?\"([^\"]*)\"");

    private static final String INVALID_ENTITY_TAG = Localizer.get("INVALID.ENTITY.TAG");

    public static final EntityTag WILD = new EntityTag("*");

    public static EntityTag parse(String entity_tag) {
        if (entity_tag == null || entity_tag.length() == 0)
            throw new IllegalArgumentException(INVALID_ENTITY_TAG);
        Matcher m = PATTERN.matcher(entity_tag);
        if (m.find()) {
            boolean wild = m.group(1) != null;
            boolean weak = m.group(2) != null;
            String tag = wild ? "*" : m.group(3);
            return new EntityTag(tag, weak, wild);
        } else {
            throw new IllegalArgumentException(INVALID_ENTITY_TAG);
        }
    }

    public static EntityTag[] parseTags(String entity_tags) {
        if (entity_tags == null || entity_tags.length() == 0)
            return new EntityTag[0];
        String[] tags = entity_tags.split("((?<=\")\\s*,\\s*(?=([wW]/)?\"|\\*))");
        List<EntityTag> etags = new ArrayList<EntityTag>();
        for (String tag : tags) {
            etags.add(EntityTag.parse(tag.trim()));
        }
        return etags.toArray(new EntityTag[etags.size()]);
    }

    public static boolean matchesAny(EntityTag tag1, String tags) {
        return matchesAny(tag1, parseTags(tags), false);
    }

    public static boolean matchesAny(EntityTag tag1, String tags, boolean weak) {
        return matchesAny(tag1, parseTags(tags), weak);
    }

    public static boolean matchesAny(String tag1, String tags) {
        return matchesAny(parse(tag1), parseTags(tags), false);
    }

    public static boolean matchesAny(String tag1, String tags, boolean weak) {
        return matchesAny(parse(tag1), parseTags(tags), weak);
    }

    public static boolean matchesAny(EntityTag tag1, EntityTag[] tags) {
        return matchesAny(tag1, tags, false);
    }

    public static boolean matchesAny(EntityTag tag1, EntityTag[] tags, boolean weak) {
        if (tags == null)
            return (tag1 == null) ? true : false;
        if (tag1.isWild() && tags != null && tags.length > 0)
            return true;
        for (EntityTag tag : tags) {
            if (tag1.equals(tag) || tag.isWild())
                return true;
        }
        return false;
    }

    public static boolean matches(EntityTag tag1, EntityTag tag2) {
        return tag1.equals(tag2);
    }

    public static boolean matches(String tag1, String tag2) {
        EntityTag etag1 = parse(tag1);
        EntityTag etag2 = parse(tag2);
        return etag1.equals(etag2);
    }

    public static boolean matches(EntityTag tag1, String tag2) {
        return tag1.equals(parse(tag2));
    }

    private final String tag;

    private final boolean weak;
    private final boolean wild;

    public EntityTag(String tag) {
        this(tag, false);
    }

    public EntityTag(String tag, boolean weak) {
        EntityTag etag = attemptParse(tag);
        if (etag == null) {
            if (tag.indexOf('"') > -1)
                throw new IllegalArgumentException(INVALID_ENTITY_TAG);
            this.tag = tag;
            this.weak = weak;
            this.wild = tag.equals("*");
        } else {
            this.tag = etag.tag;
            this.weak = etag.weak;
            this.wild = etag.wild;
        }
    }

    private EntityTag(String tag, boolean weak, boolean wild) {
        this.tag = tag;
        this.weak = weak;
        this.wild = wild;
    }

    private EntityTag attemptParse(String tag) {
        try {
            return parse(tag);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isWild() {
        return wild;
    }

    public String getTag() {
        return tag;
    }

    public boolean isWeak() {
        return weak;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (wild) {
            buf.append("*");
        } else {
            if (weak)
                buf.append("W/");
            buf.append('"');
            buf.append(tag);
            buf.append('"');
        }
        return buf.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        result = prime * result + (weak ? 1231 : 1237);
        result = prime * result + (wild ? 1231 : 1237);
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
        final EntityTag other = (EntityTag)obj;
        if (isWild() || other.isWild())
            return true;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        if (weak != other.weak)
            return false;
        if (wild != other.wild)
            return false;
        return true;
    }

    @Override
    protected EntityTag clone() {
        try {
            return (EntityTag)super.clone();
        } catch (CloneNotSupportedException e) {
            return new EntityTag(tag, weak, wild); // not going to happen
        }
    }

    /**
     * Utility method for generating ETags. Works by concatenating the UTF-8 bytes of the provided strings then
     * generating an MD5 hash of the result.
     */
    public static EntityTag generate(String... material) {
        String etag = null;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            for (String s : material) {
                if (s != null)
                    md.update(s.getBytes("utf-8"));
            }
            byte[] digest = md.digest();
            etag = new String(Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(Localizer.get("HASHING.NOT.AVAILABLE"));
        } catch (UnsupportedEncodingException e) {
            // should not happen
            throw new RuntimeException(Localizer.get("UTF8.NOT.SUPPORTED"), e);
        }
        return new EntityTag(etag);
    }

    /**
     * Checks that the passed in ETag matches the ETag generated by the generate method
     */
    public static boolean matches(EntityTag etag, String... material) {
        EntityTag etag2 = generate(material);
        return EntityTag.matches(etag, etag2);
    }

    public static String toString(EntityTag... tags) {
        StringBuilder buf = new StringBuilder();
        for (EntityTag tag : tags) {
            if (buf.length() > 0)
                buf.append(", ");
            buf.append(tag.toString());
        }
        return buf.toString();
    }

    public static String toString(String... tags) {
        StringBuilder buf = new StringBuilder();
        for (String tag : tags) {
            if (buf.length() > 0)
                buf.append(", ");
            EntityTag etag = new EntityTag(tag);
            buf.append(etag.toString());
        }
        return buf.toString();
    }

    public int compareTo(EntityTag o) {
        if (o.isWild() && !isWild())
            return 1;
        if (isWild() && !o.isWild())
            return -1;
        if (o.isWeak() && !isWeak())
            return -1;
        if (isWeak() && !o.isWeak())
            return 1;
        return tag.compareTo(o.tag);
    }
}
