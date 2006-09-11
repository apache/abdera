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
package org.apache.abdera.protocol.server;

import java.io.Serializable;

import org.apache.abdera.protocol.server.util.ResourceType;

public interface Target 
  extends Iterable<String>, 
          Serializable,
          Cloneable {

  ResourceType getResourceType();
  
  /**
   * Requests a target token value.  index=0 should always return the complete
   * identifier of the target.
   */
  String getValue(int index);
  
  /**
   * Determines whether or not the Target specifies the specified token value.
   * MUST return true if index=0
   */
  boolean hasValue(int index);
  
  Object clone() throws CloneNotSupportedException;
}
