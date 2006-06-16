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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public class MimeTypeHelper {

  public static boolean isMatch(String a, String b) {
    boolean answer = false;
    try {
      MimeType mta = new MimeType(a);
      answer = mta.match(b);
    } catch (Exception e) {}
    return answer;
  }
  
  public static boolean isXml(String a) {
    boolean answer = isMatch("application/xml", a) || isMatch("text/xml", a);
    if (!answer) {
      try { 
        MimeType mta = new MimeType(a);
        answer = 
          (("application".equalsIgnoreCase(mta.getPrimaryType()) ||
            "text".equalsIgnoreCase(mta.getPrimaryType())) && 
            mta.getSubType().equals("xml") || 
            mta.getSubType().endsWith("+xml"));
      } catch (Exception e) {}
    }
    return answer;
  }
  
  public static boolean isText(String a) {
    boolean answer = isMatch("text/*", a);
    return answer;
  }
  
  public static boolean isMimeType(String a) {
    boolean answer = false;
    try {
      new MimeType(a);
      answer = true;
    } catch (MimeTypeParseException e) {
      answer = false;
    }
    return answer;
  }
}
