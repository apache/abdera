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
package org.apache.abdera2.activities.model.objects;

import org.apache.abdera2.activities.model.ASObject;
import org.apache.abdera2.common.anno.Name;

@Name("place")
public class PlaceObject 
  extends ASObject {

  private static final long serialVersionUID = -8556845441567764050L;
  public static final String POSITION = "position";
  public static final String ADDRESS = "address";
  
  public PlaceObject() {}
  
  public PlaceObject(String displayName) {
    setDisplayName(displayName);
  }
  
  public String getPosition() {
    return getProperty(POSITION);
  }
  
  public void setPosition(String position) {
    setProperty(POSITION, position);
  }
  
  public Address getAddress() {
    return getProperty(ADDRESS);
  }
  
  public void setAddress(Address address) {
    setProperty(ADDRESS, address);
  }

}
