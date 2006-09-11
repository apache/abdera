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
package org.apache.abdera.protocol.server.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResourceType 
  implements Comparable<ResourceType>, 
             Serializable {

  private static final long serialVersionUID = -4325229702865059923L;
  public static final int UNKNOWN_ORDINAL    = 0;
  public static final int SERVICE_ORDINAL    = 1;
  public static final int COLLECTION_ORDINAL = 2;
  public static final int ENTRY_ORDINAL      = 3;
  public static final int ENTRY_EDIT_ORDINAL = 4;
  public static final int MEDIA_ORDINAL      = 5;
  public static final int MEDIA_EDIT_ORDINAL = 6;
  
  public static final ResourceType UNKNOWN    = new ResourceType("UNKNOWN", UNKNOWN_ORDINAL); 
  public static final ResourceType SERVICE    = new ResourceType("SERVICE", SERVICE_ORDINAL);
  public static final ResourceType COLLECTION = new ResourceType("COLLECTION", COLLECTION_ORDINAL); 
  public static final ResourceType ENTRY      = new ResourceType("ENTRY", ENTRY_ORDINAL);
  public static final ResourceType ENTRY_EDIT = new ResourceType("ENTRY_EDIT", ENTRY_EDIT_ORDINAL);
  public static final ResourceType MEDIA      = new ResourceType("MEDIA", MEDIA_ORDINAL);
  public static final ResourceType MEDIA_EDIT = new ResourceType("MEDIA_EDIT", MEDIA_EDIT_ORDINAL);
  
  private static List<ResourceType> values;
  
  private static synchronized List<ResourceType> get_values() {
    if (values == null) {
      values = Collections.synchronizedList(
        new ArrayList<ResourceType>());
    }
    return values;
  }
  
  private static synchronized void add(ResourceType type) {
    List<ResourceType> values = get_values();
    if (type.ordinal() < values().length || 
        contains(type) || 
        contains(type.name())) 
          throw new IllegalArgumentException();
    values.add(type);
  }
  
  public static boolean contains(ResourceType type) {
    for (ResourceType rt : values()) {
      if (rt.equals(type)) return true;
    }
    return false;
  }
  
  public static boolean contains(String name) {
    for (ResourceType rt : values()) {
      if (rt.name.equals(name)) return true;
    }
    return false;
  }
  
  public static int nextOrdinal() {
    return values.size();
  }
  
  public static ResourceType[] values() {
    List<ResourceType> values = get_values();
    return values.toArray(new ResourceType[values.size()]);
  }
  
  public static ResourceType valueOf(String string) {
    for (ResourceType type: values()) {
      if (type.name() == string.intern()) return type;
    }
    return null;
  }
  
  public static ResourceType getOrCreate(String string) {
    ResourceType type = valueOf(string);
    return (type != null) ? type : new ResourceType(string);
  }
  
  public static ResourceType valueOf(int ordinal) {
    return values()[ordinal];
  }
  
  private final int i;
  private final String name;
  
  public ResourceType(String name) {
    this(name, nextOrdinal());
  }
  
  public ResourceType(String name, int ordinal) {
    if (name == null) throw new IllegalArgumentException("Name cannot be null");
    this.i = ordinal;
    this.name = name.intern().toUpperCase();
    ResourceType.add(this);
  }
  
  public String name() {
    return name;
  }
  
  public int ordinal() {
    return i;
  }
  
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + i;
    result = PRIME * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final ResourceType other = (ResourceType) obj;
    if (i != other.i) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  public int compareTo(ResourceType o) {
    if (o.ordinal() > ordinal()) return -1;
    if (o.ordinal() < ordinal()) return 1;
    return 0;
  }
  
  public String toString() {
    return name();
  }

}
