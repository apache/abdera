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

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.ItemManager;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.RequestHandler;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.Target;

public abstract class AbstractServiceContext 
  implements ServiceContext {

  protected Abdera abdera;
  protected Map<String,String> config;
  protected ItemManager<Provider> providerManager;
  protected ItemManager<RequestHandler> handlerManager;
  protected Resolver<Subject> subjectResolver;
  protected Resolver<Target> targetResolver;
  
  public synchronized void init(
    Abdera abdera, 
    Map<String,String> config) {
     this.abdera = abdera;
     this.config = (config != null) ? config : new HashMap<String,String>();
  }
  
  public Abdera getAbdera() {
    return abdera;
  }

  public String getProperty(String name) {
    return config.get(name);
  }

  public String[] getPropertyNames() {
    return config.keySet().toArray(new String[config.size()]);
  }

}
