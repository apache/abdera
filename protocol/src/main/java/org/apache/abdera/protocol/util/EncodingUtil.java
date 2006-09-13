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
package org.apache.abdera.protocol.util;

import org.apache.commons.codec.net.QCodec;

public class EncodingUtil {

  public static String encode(String value, String charset) {
    try {
      return (new QCodec(charset)).encode(value);
    } catch (Exception e) {
      return value;
    }
  }
  
  public static String decode(String value) {
    try {
      return (new QCodec()).decode(value);
    } catch (Exception e) {
      return value;
    }
  }
}
