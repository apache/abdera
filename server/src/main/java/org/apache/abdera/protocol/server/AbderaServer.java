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
import org.apache.abdera.protocol.server.util.ServerConstants;
import org.apache.abdera.util.ServiceUtil;

public class AbderaServer implements ServerConstants {
  
  private final Abdera abdera;
  private final RequestHandlerManager handlerManager;
  private final TargetResolver targetResolver;
  private final SubjectResolver subjectResolver;
  private final ProviderManager providerManager;
  private final String defaultHandlerManager;
  private final String defaultProviderManager;
  private final String defaultTargetResolver;
  private final String defaultSubjectResolver;
  
  public AbderaServer() {
    this(new Abdera());
  }
  
  public AbderaServer(
    String defaultTargetResolver, 
    String defaultHandlerManager,
    String defaultSubjectResolver,
    String defaultProviderManager) {
      this(new Abdera(), 
           defaultTargetResolver, 
           defaultHandlerManager, 
           defaultSubjectResolver,
           defaultProviderManager);
  }
  
  public AbderaServer(Abdera abdera) {
    this(abdera,"","",DEFAULT_SUBJECT_RESOLVER, "");
  }
  
  public AbderaServer(
    Abdera abdera, 
    String defaultTargetResolver, 
    String defaultHandlerManager,
    String defaultSubjectResolver,
    String defaultProviderFactory) {
      this.abdera = abdera;
      this.handlerManager = newRequestHandlerManager(defaultHandlerManager);
      this.targetResolver = newTargetResolver(defaultTargetResolver);
      this.subjectResolver = newSubjectResolver(defaultSubjectResolver);
      this.providerManager = newProviderManager(defaultProviderFactory);
      this.defaultHandlerManager = defaultHandlerManager;
      this.defaultTargetResolver = defaultTargetResolver;
      this.defaultSubjectResolver = defaultSubjectResolver;
      this.defaultProviderManager = defaultProviderFactory;
  }
  
  public Abdera getAbdera() {
    return abdera;
  }
  
  public ProviderManager newProviderManager() {
    return newProviderManager(defaultProviderManager);
  }
  
  public ProviderManager newProviderManager(String _default) {
    return (ProviderManager) ServiceUtil.newInstance(
      PROVIDER_MANAGER, (_default != null) ? _default : "", abdera);
  }

  public ProviderManager getProviderManager() {
    return providerManager;
  }
  
  public RequestHandlerManager newRequestHandlerManager() {
    return newRequestHandlerManager(defaultHandlerManager);
  }
  
  public RequestHandlerManager newRequestHandlerManager(String _default) {
    return (RequestHandlerManager) ServiceUtil.newInstance(
      HANDLER_MANAGER, (_default != null) ? _default : "", abdera);
  }
  
  public RequestHandlerManager getRequestHandlerManager() {
    return handlerManager;
  }
  
  public TargetResolver newTargetResolver() {
    return newTargetResolver(defaultTargetResolver);
  }
  
  public TargetResolver newTargetResolver(String _default) {
    return (TargetResolver) ServiceUtil.newInstance(
      TARGET_RESOLVER, (_default != null) ? _default : "", abdera);
  }
  
  public TargetResolver getTargetResolver() {
    return targetResolver;
  }
  
  public SubjectResolver newSubjectResolver() {
    return newSubjectResolver(defaultSubjectResolver);
  }
  
  public SubjectResolver newSubjectResolver(String _default) {
    return (SubjectResolver) ServiceUtil.newInstance(
        SUBJECT_RESOLVER, (_default != null) ? _default : DEFAULT_SUBJECT_RESOLVER, abdera);
  }
  
  public SubjectResolver getSubjectResolver() {
    return subjectResolver;
  }
}
