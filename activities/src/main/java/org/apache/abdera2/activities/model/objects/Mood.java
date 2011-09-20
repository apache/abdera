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

import org.apache.abdera2.activities.model.ASBase;
import org.apache.abdera2.activities.model.MediaLink;

public class Mood extends ASBase {

  private static final long serialVersionUID = -3361108212621494744L;
  public static final String IMAGE = "image";
  public static final String DISPLAYNAME = "displayName";
  
  public Mood() {}
  
  public Mood(String displayName) {
    setDisplayName(displayName);
  }
  
  public String getDisplayName() {
    return getProperty(DISPLAYNAME);
  }
  
  public void setDisplayName(String displayName) {
    setProperty(DISPLAYNAME, displayName);
  }
  
  public MediaLink getImage() {
    return getProperty(IMAGE);
  }
  
  public void setImage(MediaLink image) {
    setProperty(IMAGE, image);
  }
  
  public String toString() {
    return getDisplayName();
  }
}
