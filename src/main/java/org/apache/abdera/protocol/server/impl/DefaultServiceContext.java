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
package org.apache.abdera.protocol.server.impl;

import javax.security.auth.Subject;

import org.apache.abdera.protocol.ItemManager;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.util.ServerConstants;
import org.apache.abdera.util.ServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultServiceContext 
  extends AbstractServiceContext
  implements ServerConstants {

  private static final Log logger = LogFactory.getLog(DefaultServiceContext.class);
  
  protected String defaultrequesthandlermanager = DefaultRequestHandlerManager.class.getName();
  protected String defaultsubjectresolver = SimpleSubjectResolver.class.getName();
  protected String defaulttargetresolver = RegexTargetResolver.class.getName();
  protected String defaultprovidermanager = null;
  
  protected DefaultServiceContext() {}
  
  private Object instance(String id, String _default) {
    String instance = getProperty(id);
    Object obj =  ServiceUtil.newInstance( id, (instance!=null)?instance:_default, abdera);
    logger.debug("Returning " + obj + " as instance of '" + id + "'.");
    return obj;
  }
  
  private Object instance(String id, String _default, Object... args) {
    String instance = getProperty(id);
    Object obj =  ServiceUtil.newInstance( id, (instance!=null)?instance:_default, abdera, args);
    logger.debug("Returning " + obj + " as instance of '" + id + "'.");
    return obj;
  }
  
  @SuppressWarnings("unchecked")
  public synchronized ItemManager<Provider> getProviderManager() {
    if (providerManager == null) {
      providerManager = (ItemManager<Provider>) instance(
        PROVIDER_MANAGER, getDefaultProviderManager());
    }
    return providerManager;
  }
  
  protected String getDefaultProviderManager() {
    return defaultprovidermanager;
  }

  @SuppressWarnings("unchecked")
  public synchronized ItemManager<RequestHandler> getRequestHandlerManager() {
    if (handlerManager == null) {
      handlerManager = (ItemManager<RequestHandler>) instance(
        REQUEST_HANDLER_MANAGER, getDefaultRequestHandlerManager());
    }
    return handlerManager;
  }
  
  protected String getDefaultRequestHandlerManager() {
    return defaultrequesthandlermanager;
  }

  @SuppressWarnings("unchecked")
  public Resolver<Subject> getSubjectResolver() {
    if (subjectResolver == null) {
      subjectResolver = (Resolver<Subject>) instance(
        SUBJECT_RESOLVER, getDefaultSubjectResolver());
    }
    return subjectResolver;
  }
  
  protected String getDefaultSubjectResolver() {
    return defaultsubjectresolver;
  }

  @SuppressWarnings("unchecked")
  public Resolver<Target> getTargetResolver(String contextPath) {
    if (targetResolver == null) {
      targetResolver = (Resolver<Target>) instance(
        TARGET_RESOLVER, getDefaultTargetResolver(), 
        contextPath);
    }
    return targetResolver;
  }
  
  protected String getDefaultTargetResolver() {
    return defaulttargetresolver;
  }

}
