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
package org.apache.abdera.server.exceptions;

import org.apache.abdera.server.RequestContext;


public class MethodNotAllowedException 
  extends AbderaServerException {

  private static final long serialVersionUID = -633052744794889086L;

  public MethodNotAllowedException() {
    super(405, null);
  }

  public MethodNotAllowedException(String text) {
    super(405, text);
  }
  
  public void setAllow(RequestContext.Method[] methods) {
    if(methods == null || methods.length == 0) {
      throw new IllegalArgumentException("Methods argument must not be empty or null.");
    }
    boolean first = true;
    StringBuffer value = new StringBuffer();
    for(RequestContext.Method method : methods) {
      if(first) {
        value.append(method.toString());
        first = false;
        continue;
      }
      value.append(", " + method.toString());
    }
    setHeader("Allow", value.toString());
  }
}
