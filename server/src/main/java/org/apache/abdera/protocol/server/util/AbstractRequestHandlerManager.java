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
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.RequestHandlerManager;
import org.apache.abdera.protocol.server.exceptions.AbderaServerException;

public abstract class AbstractRequestHandlerManager 
  extends PoolManager<RequestHandler>
  implements RequestHandlerManager {
  
  public RequestHandler newRequestHandler(
    AbderaServer abderaServer) 
      throws AbderaServerException {
    RequestHandler rh = getRequestHandler(abderaServer);
    return rh;
  }
  
  private synchronized RequestHandler getRequestHandler(
    AbderaServer abderaServer) throws AbderaServerException {
      return getInstance(abderaServer);
  }

  public synchronized void releaseRequestHandler(
    RequestHandler handler) {
      release(handler);
  }
  
  protected abstract RequestHandler newRequestHandlerInstance( 
    AbderaServer abderaServer) throws AbderaServerException;
  
  protected RequestHandler internalNewInstance( 
    AbderaServer abderaServer) throws AbderaServerException {
      return newRequestHandlerInstance(abderaServer);
  }
}
