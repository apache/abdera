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

import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.protocol.server.impl.DefaultServiceContext;
import org.apache.abdera.protocol.server.util.ServerConstants;
import org.apache.abdera.util.ServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceManager 
  implements ServerConstants {
  
  private final static Log log = LogFactory.getLog(ServiceManager.class);
  
  private static ServiceManager INSTANCE = null;
  private static Abdera abdera = null;
  
  ServiceManager() {}
  
  public static synchronized ServiceManager getInstance() {
    if (INSTANCE == null) {
      log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE","ServiceManager"));
      INSTANCE = new ServiceManager();
    }
    return INSTANCE;
  }
  
  public static synchronized Abdera getAbdera() {
    if (abdera == null) {
      log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE","Abdera"));
      abdera = new Abdera();
    }
    return abdera;
  }
  
  public ServiceContext newServiceContext(
    Map<String,String> properties) {
    Abdera abdera = getAbdera();
    String instance = properties.get(SERVICE_CONTEXT);
    log.debug(Localizer.sprintf("CREATING.NEW.INSTANCE","ServiceContext"));
    ServiceContext context = 
      (ServiceContext) ServiceUtil.newInstance(
        SERVICE_CONTEXT, 
        (instance != null) ? 
          instance : 
          DefaultServiceContext.class.getName(),
        abdera);
    log.debug(Localizer.sprintf("INITIALIZING.INSTANCE", "ServiceContext"));
    context.init(abdera, properties);
    return context;
  }
  
}
