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

import java.io.Serializable;
import java.util.Locale;

/**
 * A Language Tab Subtag. Instances are immutable and safe to use by
 * multiple threads.
 */
public final class Subtag 
  implements Serializable, Comparable<Subtag> {

    private static final long serialVersionUID = -4496128268514329138L;

    public enum Type {
        LANGUAGE,
        EXTLANG,
        SCRIPT,
        REGION,
        VARIANT,
        SINGLETON,
        EXTENSION,
        PRIVATEUSE,
        GRANDFATHERED,
        WILDCARD,
        SIMPLE
    }

    private final Type type;
    private final String name;
    private Subtag prev;
    private Subtag next;
    private Subtag root;

    public Subtag(
        Type type, 
        String name) {
        this(
            type, 
            name, 
            null);
    }

    Subtag() {
        this(
            Type.WILDCARD, 
            "*");
    }

    /**
     * Create a Subtag
     */
    public Subtag(
        Type type, 
        String name, 
        Subtag prev) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        if (prev != null) {
            prev.setNext(this);
            this.root = prev.root();
        } else this.root = null;
    }

    Subtag(Subtag copy, Subtag parent) {
      this(copy.type(),copy.name(),parent);
    }
    
    Subtag(
        Type type, 
        String name, 
        Subtag prev, 
        Subtag next,
        Subtag root) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        this.next = next;
        this.root = root;
    }

    public Subtag root() {
      return root != null ? root : this;
    }
    
    public Type type() {
        return type;
    }

    public String name() {
        return toString();
    }

    void setPrevious(Subtag prev) {
        this.prev = prev;
    }

    public Subtag previous() {
        return prev;
    }

    void setNext(Subtag next) {
        this.next = next;
        if (next != null)
            next.setPrevious(this);
    }

    public Subtag next() {
        return next;
    }

    public String toString() {
        switch (type) {
            case LANGUAGE:
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
        if (string == null || string.length() == 0)
            return string;
        char[] chars = string.toLowerCase(Locale.US).toCharArray();
        chars[0] = Character.toTitleCase(chars[0]);
        return new String(chars);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : 
          name.toLowerCase(Locale.US).hashCode());
        result = prime * result + ((type == null) ? 0 : 
          type.hashCode());
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
        if (other.type() == Type.WILDCARD || type() == Type.WILDCARD)
            return true;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equalsIgnoreCase(other.name))
            return false;
        if (other.type() == Type.SIMPLE || type() == Type.SIMPLE)
            return true;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    private boolean isX() {
      return "x".equalsIgnoreCase(name());
    }
    
    public boolean isSingleton() {
      return type == Type.SINGLETON;
    }
    
    public boolean isExtension() {
      return type == Type.EXTENSION;
    }
    
    public boolean isPrivateUse() {
      return type == Type.PRIVATEUSE;
    }
    
    boolean isExtensionOrPrivateUse() {
      return isExtension() || isPrivateUse();
    }
    
    public boolean isExtensionSingleton() {
      return isSingleton() && !isX();
    }
    
    public boolean isPrivateSingleton() {
      return isSingleton() && isX();
    }
        
    public SubtagSet extractExtensionGroup() {
      if (!isSingleton()) return null;
      Subtag c = this, p = root = new Subtag(this,null);
      while((c = c.next()) != null && c.isExtensionOrPrivateUse())
        p = new Subtag(c,p);
      return new SubtagSet(root) {
        private static final long serialVersionUID = 7508549925367514365L;        
      };
    }
    
    public static Subtag newWildcard() {
        return new Subtag();
    }
    
    public int compareTo(Subtag o) {
      int c = o.type.compareTo(type);
      return c != 0 ? c : o.name.compareTo(name);
  }
}
