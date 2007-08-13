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
package org.apache.abdera.protocol.error;

import org.apache.abdera.Abdera;

public class ProtocolException
    extends RuntimeException {

  private static final long serialVersionUID = 1017447143200419489L;
  protected final Error error;
  
  public ProtocolException(Error error) {
    super(error.getCode() + "::" + error.getMessage());
    this.error = error;
  }
  
  public ProtocolException(Abdera abdera, int code, String message) {
    super(code + "::" + message);
    this.error = Error.create(abdera, code, message);
  }
  
  public Error getError() {
    return error;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((error == null) ? 0 : error.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final ProtocolException other = (ProtocolException) obj;
    if (error == null) {
      if (other.error != null) return false;
    } else if (!error.equals(other.error)) return false;
    return true;
  }
  
}
