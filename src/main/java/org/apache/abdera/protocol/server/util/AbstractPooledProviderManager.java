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
package org.apache.abdera.protocol.server.util;

import org.apache.abdera.protocol.server.AbderaServer;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.ProviderManager;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException;

public abstract class AbstractPooledProviderManager 
  extends PoolManager<Provider>
  implements ProviderManager {
  
  public Provider newProvider(
    AbderaServer abderaServer) 
      throws AbderaServerException {
    Provider prov = getProvider(abderaServer);
    return prov;
  }
  
  private synchronized Provider getProvider(
    AbderaServer abderaServer) throws AbderaServerException {
      return getInstance(abderaServer);
  }

  public synchronized void releaseProvider(
    Provider provider) {
      release(provider);
  }
  
  protected abstract Provider newProviderInstance( 
    AbderaServer abderaServer) throws AbderaServerException;
  
  protected Provider internalNewInstance( 
    AbderaServer abderaServer) throws AbderaServerException {
      return newProviderInstance(abderaServer);
  }
}
