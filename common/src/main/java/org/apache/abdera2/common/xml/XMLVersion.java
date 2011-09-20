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
package org.apache.abdera2.common.xml;

import org.apache.abdera2.common.text.CharUtils;
import org.apache.abdera2.common.text.CharUtils.Profile;

public enum XMLVersion {
    XML10("1.0",CharUtils.Profile.XML1RESTRICTED), 
    XML11("1.1",CharUtils.Profile.XML11RESTRICTED);
    
    private final Profile profile;
    private final String label;
    
    XMLVersion(String label, Profile profile) {
      this.profile = profile;
      this.label = label;
    }
    
    public Profile profile() {
      return profile;
    }
    
    public String label() {
      return label;
    }
    
    public static XMLVersion get(String version) {
      return version == null ? XMLVersion.XML10 : version.equals("1.0") ? XMLVersion.XML10 : version.equals("1.1")
          ? XMLVersion.XML11 : XMLVersion.XML10;
  }
}