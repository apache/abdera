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

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.target.TargetResolver;
import org.apache.abdera.util.ServiceUtil;

public class AbderaServer implements ServerConstants {
  
  private Abdera abdera = null;
  private RequestHandlerFactory handlerFactory = null;
  private TargetResolver targetResolver = null;
  
  public AbderaServer() {
    abdera = new Abdera();
  }
  
  public AbderaServer(Abdera abdera) {
    this.abdera = abdera;
  }
  
  public Abdera getAbdera() {
    return abdera;
  }
  
  public RequestHandlerFactory newRequestHandlerFactory(String _default) {
    return (RequestHandlerFactory) ServiceUtil.newInstance(
      HANDLER_FACTORY, (_default != null) ? _default : "", abdera);
  }
  
  public RequestHandlerFactory getRequestHandlerFactory(String _default) {
    if (handlerFactory == null)
      handlerFactory = newRequestHandlerFactory(_default);
    return handlerFactory;
  }
  
  public TargetResolver newTargetResolver(String _default) {
    return (TargetResolver) ServiceUtil.newInstance(
      TARGET_RESOLVER, (_default != null) ? _default : "", abdera);
  }
  
  public TargetResolver getTargetResolver(String _default) {
    if (targetResolver == null)
      targetResolver = newTargetResolver(_default);
    return targetResolver;
  }
}
