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
package org.apache.abdera.protocol.server;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Identifies the type of resource being requests.
 */
public final class TargetType {

    public static final String UNKNOWN = "UNKNOWN";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String SERVICE = "SERVICE";
    public static final String COLLECTION = "COLLECTION";
    public static final String ENTRY = "ENTRY";
    public static final String MEDIA = "MEDIA";
    public static final String CATEGORIES = "CATEGORIES";

    private static final Map<String, TargetType> types = new WeakHashMap<String, TargetType>();

    /**
     * An unknown target type
     */
    public static final TargetType TYPE_UNKNOWN = new TargetType(UNKNOWN);
    /**
     * A not found target type
     */
    public static final TargetType TYPE_NOT_FOUND = new TargetType(NOT_FOUND);
    /**
     * An Atompub Service Document
     */
    public static final TargetType TYPE_SERVICE = new TargetType(SERVICE);
    /**
     * An Atom Feed Document representing an Atompub Collection
     */
    public static final TargetType TYPE_COLLECTION = new TargetType(COLLECTION);
    /**
     * An Atompub Collection member entry
     */
    public static final TargetType TYPE_ENTRY = new TargetType(ENTRY);
    /**
     * An Atompub Collection media resource
     */
    public static final TargetType TYPE_MEDIA = new TargetType(MEDIA);
    /**
     * An Atompub Categories Document
     */
    public static final TargetType TYPE_CATEGORIES = new TargetType(CATEGORIES);

    /**
     * Return a listing of TargetTypes
     */
    public static Iterable<TargetType> values() {
        return types.values();
    }

    /**
     * Get the specified target type
     */
    public static TargetType get(String name) {
        return types.get(name.toUpperCase());
    }

    /**
     * Get the specified target type. If the target type does not currently exist, and create = true, a new type will be
     * created.
     */
    public static TargetType get(String name, boolean create) {
        if (name == null)
            return null;
        TargetType type = get(name);
        return type != null ? type : create ? create(name) : null;
    }

    private static synchronized TargetType create(String name) {
        TargetType type = new TargetType(name.toUpperCase());
        types.put(type.name(), type);
        return type;
    }

    private final String name;

    private TargetType(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException();
        if (get(name) != null)
            throw new IllegalArgumentException();
        this.name = name.toUpperCase();
        types.put(name, this);
    }

    /**
     * Return the target name
     */
    public String name() {
        return name;
    }

    public String toString() {
        return name;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TargetType other = (TargetType)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
