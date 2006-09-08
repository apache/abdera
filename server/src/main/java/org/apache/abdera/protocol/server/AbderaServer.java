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
import org.apache.abdera.protocol.server.util.ServerConstants;
import org.apache.abdera.util.ServiceUtil;

public class AbderaServer implements ServerConstants {
  
  private final Abdera abdera;
  private final RequestHandlerFactory handlerFactory;
  private final TargetResolver targetResolver;
  private final String defaultHandlerFactory;
  private final String defaultTargetResolver;
  
  public AbderaServer() {
    this(new Abdera(),"","");
  }
  
  public AbderaServer(
    String defaultTargetResolver, 
    String defaultHandlerFactory) {
      this(new Abdera(), defaultTargetResolver, defaultHandlerFactory);
  }
  
  public AbderaServer(Abdera abdera) {
    this(abdera,"","");
  }
  
  public AbderaServer(
    Abdera abdera, 
    String defaultTargetResolver, 
    String defaultHandlerFactory) {
      this.abdera = abdera;
      this.handlerFactory = newRequestHandlerFactory(defaultHandlerFactory);
      this.targetResolver = newTargetResolver(defaultTargetResolver);
      this.defaultHandlerFactory = defaultHandlerFactory;
      this.defaultTargetResolver = defaultTargetResolver;
  }
  
  public Abdera getAbdera() {
    return abdera;
  }
  
  public RequestHandlerFactory newRequestHandlerFactory() {
    return newRequestHandlerFactory(defaultHandlerFactory);
  }
  
  public RequestHandlerFactory newRequestHandlerFactory(String _default) {
    return (RequestHandlerFactory) ServiceUtil.newInstance(
      HANDLER_FACTORY, (_default != null) ? _default : "", abdera);
  }
  
  public RequestHandlerFactory getRequestHandlerFactory(String _default) {
    return handlerFactory;
  }
  
  public TargetResolver newTargetResolver() {
    return newTargetResolver(defaultTargetResolver);
  }
  
  public TargetResolver newTargetResolver(String _default) {
    return (TargetResolver) ServiceUtil.newInstance(
      TARGET_RESOLVER, (_default != null) ? _default : "", abdera);
  }
  
  public TargetResolver getTargetResolver(String _default) {
    return targetResolver;
  }
}
