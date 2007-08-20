/**
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.abdera.spring;

import org.apache.abdera.protocol.ItemManager;
import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SingletonProviderManager;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class ServiceContextDefinitionParserTest 
    extends AbstractDependencyInjectionSpringContextTests {

    public void testParser() throws Exception {
        ServiceContext ctx = (ServiceContext) applicationContext.getBean(ServiceContext.class.getName());
        
        ItemManager<Provider> pm = ctx.getProviderManager();
        assertTrue(pm instanceof SingletonProviderManager);
        
        Provider provider = pm.get(null);
        assertTrue(provider instanceof TestProvider);
        
        Resolver<Target> tresolver = ctx.getTargetResolver("/path");
        assertTrue(tresolver instanceof RegexTargetResolver);
    }
    
    @Override
    protected String getConfigPath() {
        return "/org/apache/abdera/spring/beans.xml";
    }
    
}
 