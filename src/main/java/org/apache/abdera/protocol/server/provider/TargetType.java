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
package org.apache.abdera.protocol.server.provider;

public final class TargetType {

  public static final String UNKNOWN = "UNKNOWN";
  public static final String SERVICE = "SERVICE";
  public static final String COLLECTION = "COLLECTION";
  public static final String ENTRY = "ENTRY";
  public static final String MEDIA = "MEDIA";
  public static final String CATEGORIES = "CATEGORIES";
  
  public static final TargetType TYPE_UNKNOWN = new TargetType(UNKNOWN);
  public static final TargetType TYPE_SERVICE = new TargetType(SERVICE);
  public static final TargetType TYPE_COLLECTION = new TargetType(COLLECTION);
  public static final TargetType TYPE_ENTRY = new TargetType(ENTRY);
  public static final TargetType TYPE_MEDIA = new TargetType(MEDIA);
  public static final TargetType TYPE_CATEGORIES = new TargetType(CATEGORIES);
  
  public static TargetType get(String name) {
    if (name == null) return null;
    name = name.toUpperCase().intern();
    if (name == UNKNOWN) return TYPE_UNKNOWN;
    if (name == COLLECTION) return TYPE_COLLECTION;
    if (name == ENTRY) return TYPE_ENTRY;
    if (name == MEDIA) return TYPE_MEDIA;
    if (name == CATEGORIES) return TYPE_CATEGORIES;
    return null;
  }
  
  public static TargetType get(String name, boolean create) {
    TargetType type = get(name);
    return (type != null) ? type : (create) ? new TargetType(name) : null;
  }
  
  private final String name;
  
  public TargetType(String name) {
    if (name == null || name.length() == 0) throw new IllegalArgumentException();
    this.name = name.toUpperCase();
  }
  
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
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final TargetType other = (TargetType) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
  
}
