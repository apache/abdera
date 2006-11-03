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

import org.apache.abdera.protocol.server.auth.SubjectResolver;
import org.apache.abdera.protocol.server.provider.ProviderManager;
import org.apache.abdera.protocol.server.provider.TargetResolver;
import org.apache.abdera.protocol.server.servlet.DefaultRequestHandlerManager;
import org.apache.abdera.protocol.server.servlet.RequestHandlerManager;
import org.apache.abdera.protocol.server.util.RegexTargetResolver;
import org.apache.abdera.protocol.server.util.ServerConstants;
import org.apache.abdera.protocol.server.util.SimpleSubjectResolver;
import org.apache.abdera.util.ServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultServiceContext 
  extends AbstractServiceContext
  implements ServerConstants {

  private static final Log logger = LogFactory.getLog(DefaultServiceContext.class);
  
  private Object instance(String id, String _default) {
    String instance = getProperty(id);
    Object obj =  ServiceUtil.newInstance( id, (instance!=null)?instance:_default, abdera);
    if (logger.isDebugEnabled()) {
      logger.debug("Returning " + obj + " as instance of '" + id + "'.");
    }
    return obj;
  }
  
  public synchronized ProviderManager getProviderManager() {
    if (providerManager == null) {
      providerManager = (ProviderManager) instance(
        PROVIDER_MANAGER, getDefaultProviderManager());
    }
    return providerManager;
  }
  
  protected String getDefaultProviderManager() {
    return null;
  }

  public synchronized RequestHandlerManager getRequestHandlerManager() {
    if (handlerManager == null) {
      handlerManager = (RequestHandlerManager) instance(
        REQUEST_HANDLER_MANAGER, getDefaultRequestHandlerManager());
    }
    return handlerManager;
  }
  
  protected String getDefaultRequestHandlerManager() {
    return DefaultRequestHandlerManager.class.getName();
  }

  public SubjectResolver getSubjectResolver() {
    if (subjectResolver == null) {
      subjectResolver = (SubjectResolver) instance(
        SUBJECT_RESOLVER, getDefaultSubjectResolver());
    }
    return subjectResolver;
  }
  
  protected String getDefaultSubjectResolver() {
    return SimpleSubjectResolver.class.getName();
  }

  public TargetResolver getTargetResolver() {
    if (targetResolver == null) {
      targetResolver = (TargetResolver) instance(
        TARGET_RESOLVER, getDefaultTargetResolver());
    }
    return targetResolver;
  }
  
  protected String getDefaultTargetResolver() {
    return RegexTargetResolver.class.getName();
  }

}
