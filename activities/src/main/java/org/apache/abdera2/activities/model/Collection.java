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
package org.apache.abdera2.activities.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.abdera2.common.anno.Name;
import org.apache.abdera2.common.iri.IRI;

@Name("collection")
public class Collection<T extends ASObject> extends ASObject {

  private static final long serialVersionUID = 1530068180553259077L;
  public static final String TOTAL_ITEMS = "totalItems";
  public static final String URL = "url";
  public static final String ITEMS = "items";
  public static final String OBJECT_TYPES = "objectTypes";

  public int getTotalItems() {
    return (Integer)getProperty(TOTAL_ITEMS);
  }
  
  public Collection<T> setTotalItems(int totalItems) {
    setProperty(TOTAL_ITEMS, totalItems);
    return this;
  }
  
  public IRI getUrl() {
    return getProperty(URL);
  }
  
  public void setUrl(IRI url) {
    setProperty(URL, url);
  }

  public Iterable<String> getObjectTypes() {
    return getProperty(OBJECT_TYPES);
  }
  
  public void setObjectTypes(Set<String> types) {
    setProperty(OBJECT_TYPES,types);
  }
  
  public void addObjectType(String... objectTypes) {
    Set<String> list = getProperty(OBJECT_TYPES);
    if (list == null) {
      list = new LinkedHashSet<String>();
      setProperty(OBJECT_TYPES, list);
    }
    for (String objectType : objectTypes)
      list.add(objectType);
  }
  
  public Iterable<T> getItems() {
    return getProperty(ITEMS);
  }
  
  public void setItems(Set<T> items) {
    setProperty(ITEMS, new LinkedHashSet<T>(items));
    setTotalItems(items.size());
  }
  
  public void addItem(T item) {
    Set<T> list = getProperty(ITEMS);
    if (list == null) {
      list = new LinkedHashSet<T>();
      setProperty(ITEMS, list);
    }
    list.add(item);
    setTotalItems(list.size());
  }
  
}
