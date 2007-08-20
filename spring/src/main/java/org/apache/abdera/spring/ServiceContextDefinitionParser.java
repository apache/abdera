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

import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.impl.DefaultServiceContext;
import org.apache.abdera.protocol.server.impl.SingletonProviderManager;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ServiceContextDefinitionParser 
    extends org.apache.abdera.spring.AbstractSingleBeanDefinitionParser {

    @Override
    protected void mapElement(ParserContext ctx, BeanDefinitionBuilder bean, Element element, String name) {
        if (name.equals("provider")) {
            String id = getAndRegisterFirstChild(element, ctx, bean, "provider");
            
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SingletonProviderManager.class);
            builder.addPropertyReference("provider", id);
            AbstractBeanDefinition singletonBean = builder.getBeanDefinition();
            ctx.getRegistry().registerBeanDefinition(SingletonProviderManager.class.getName() + hashCode(), 
                                                     singletonBean);
            
            bean.addPropertyValue("providerManager", singletonBean);
        } else if (name.equals("providerManager")) {
            setFirstChildAsProperty(element, ctx, bean, "providerManager");
        } else if (name.equals("targetResolver")) {
            setFirstChildAsProperty(element, ctx, bean, "targetResolver");
        } else if (name.equals("subjectResolver")) {
            setFirstChildAsProperty(element, ctx, bean, "subjectResolver");
        }
    }

    @Override
    protected String getBeanClassName(Element arg0) {
        String cls = super.getBeanClassName(arg0);
        
        if (cls == null) {
            cls = DefaultServiceContext.class.getName();
        }
        
        return cls;
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition arg1, ParserContext arg2) throws BeanDefinitionStoreException {
        String id = element.getAttribute("id");
        
        if (id == null || "".equals(id)) {
            id = ServiceContext.class.getName();
        }
        
        
        return id;
    }
    
}
 