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
package org.apache.abdera.util.filter;

import org.apache.abdera.filter.ParseFilter;

public abstract class AbstractParseFilter 
  implements ParseFilter {

  protected byte flags = 0;
  
  public void setIgnoreComments(boolean ignore) {
    if (getIgnoreComments()) {
      if (!ignore) flags ^= 1;
    } else {
      if (ignore) flags |= 1;
    }
  }

  public void setIgnoreWhitespace(boolean ignore) {
    if (getIgnoreWhitespace()) {
      if (!ignore) flags ^= 2;
    } else {
      if (ignore) flags |= 2;
    }
  }
  
  public void setIgnoreProcessingInstructions(boolean ignore) {
    if (getIgnoreProcessingInstructions()) {
      if (!ignore) flags ^= 4;
    } else {
      if (ignore) flags |= 4;
    }
  }
  
  public boolean getIgnoreComments() {
    return (flags & 1) == 1;
  }

  public boolean getIgnoreProcessingInstructions() {
    return (flags & 4) == 4;
  }

  public boolean getIgnoreWhitespace() {
    return (flags & 2) == 2;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
