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
package org.apache.abdera.util;

import org.apache.abdera.i18n.ChainableBitSet;

public class XmlUtil {

  public enum XMLVersion { XML10, XML11 };
  
  private static final ChainableBitSet restrictedchar10 =
    new ChainableBitSet().set2(0, 8)
                         .set2(11, 12)
                         .set2(14, 31)
                         .set2(55296, 57343)
                         .set2(65534, 65535);

  private static final ChainableBitSet restrictedchar11 = 
    new ChainableBitSet().set2(0, 8)
                         .set2(11, 12)
                         .set2(14, 31)
                         .set2(127, 159)
                         .set2(55296, 57343)
                         .set2(65534, 65535);
  
  public static boolean restricted(XMLVersion version, char c) {
    return restricted(version,(int)c);
  }
  
  public static boolean restricted(XMLVersion version, int c) {
    switch(version) {
      case XML11:
        return restrictedchar11.get(c);
      default:
        return restrictedchar10.get(c);
    }
  }
  
  public static XMLVersion getVersion(String version) {
    return version == null ? XMLVersion.XML10 :
           version.equals("1.0") ? XMLVersion.XML10 :
           version.equals("1.1") ? XMLVersion.XML11 : 
           XMLVersion.XML10;
  }
}
