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

import org.apache.commons.codec.binary.Hex;

/**
 * Implements an EntityTag.
 */
public class EntityTag implements Cloneable, Serializable {

  private static final long serialVersionUID = 1559972888659121461L;

  private static final String INVALID_ENTITY_TAG = Messages
      .get("INVALID.ENTITY.TAG");

  public static EntityTag parse(String entity_tag) {
    if (entity_tag == null || entity_tag.length() == 0)
      throw new IllegalArgumentException(INVALID_ENTITY_TAG);
    boolean weak = entity_tag.startsWith("W/");
    if (!weak && !entity_tag.startsWith("\""))
      throw new IllegalArgumentException(INVALID_ENTITY_TAG);
    String tag = entity_tag.substring((weak) ? 3 : 1, entity_tag.length() - 1);
    return new EntityTag(tag, weak);
  }

  public static EntityTag[] parseTags(String entity_tags) {
    if (entity_tags == null || entity_tags.length() == 0)
      return new EntityTag[0];
    String[] tags = entity_tags.split("((?<=\")\\s*,\\s*(?=(W/)?\"))");
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

  public static boolean matchesAny(EntityTag tag1, EntityTag[] tags,
      boolean weak) {
    if (tags == null)
      return (tag1 == null) ? true : false;
    for (EntityTag tag : tags) {
      if (tag1.equals(tag))
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
    return tag1.equals(tag2);
  }

  private final String tag;

  private final boolean weak;

  public EntityTag(String tag) {
    this(tag, false);
  }

  public EntityTag(String tag, boolean weak) {
    checkTag(tag);
    this.tag = tag;
    this.weak = weak;
  }

  private void checkTag(String tag) {
    if (tag.startsWith("\"") || 
        (tag.endsWith("\"") && !tag.endsWith("\\\"")) )
      throw new IllegalArgumentException("Invalid Entity Tag");
  }

  public String getTag() {
    return tag;
  }

  public boolean isWeak() {
    return weak;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    if (weak)
      buf.append("W/");
    buf.append('"');
    buf.append(tag);
    buf.append('"');
    return buf.toString();
  }

  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((tag == null) ? 0 : tag.hashCode());
    result = PRIME * result + (weak ? 1231 : 1237);
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof String)
      obj = new EntityTag((String) obj);
    if (getClass() != obj.getClass())
      return false;
    final EntityTag other = (EntityTag) obj;
    if (tag == null) {
      if (other.tag != null)
        return false;
    } else if (!tag.equals(other.tag))
      return false;
    if (weak != other.weak)
      return false;
    return true;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Utility method for generating ETags. Works by concatenating the UTF-8 bytes
   * of the provided strings then generating an MD5 hash of the result.
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
      throw new UnsupportedOperationException(Messages
          .get("HASHING.NOT.AVAILABLE"));
    } catch (UnsupportedEncodingException e) {
      // should not happen
      throw new RuntimeException(Messages.get("UTF8.NOT.SUPPORTED"), e);
    }
    return new EntityTag(etag);
  }

  /**
   * Checks that the passed in ETag matches the ETag generated by the generate
   * method
   */
  public static boolean matches(EntityTag etag, String... material) {
    EntityTag etag2 = generate(material);
    return EntityTag.matches(etag, etag2);
  }

  public static String toString(EntityTag... tags) {
    StringBuffer buf = new StringBuffer();
    for (EntityTag tag : tags) {
      if (buf.length() > 0) buf.append(", ");
      buf.append(tag.toString());
    }
    return buf.toString();
  }
  
  public static String toString(String ... tags) {
    StringBuffer buf = new StringBuffer();
    for (String tag : tags) {
      if (buf.length() > 0) buf.append(", ");
      EntityTag etag = new EntityTag(tag);
      buf.append(etag.toString());
    }
    return buf.toString();
  }
}
