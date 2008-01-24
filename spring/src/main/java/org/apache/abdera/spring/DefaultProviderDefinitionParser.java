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

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Element;

public class DefaultProviderDefinitionParser 
    extends org.apache.abdera.spring.AbstractSingleBeanDefinitionParser {

    @Override
    protected void mapAttribute(BeanDefinitionBuilder bean, Element element, String name, String val) {
        if (name.equals("servicesPattern")) {
            bean.addPropertyValue(name, val);
        }
    }

    @Override
    protected void mapElement(ParserContext ctx, BeanDefinitionBuilder bean, Element element, String name) {
        if (name.equals("workspaceManager")) {
            setFirstChildAsProperty(element, ctx, bean, "workspaceManager");
        } else if (name.equals("targetResolver")) {
            setFirstChildAsProperty(element, ctx, bean, "targetResolver");
        } else if (name.equals("subjectResolver")) {
            setFirstChildAsProperty(element, ctx, bean, name);
        } else if (name.equals("workspace")) {
            String id = getAndRegister(ctx, bean, element);
            
            bean.addPropertyReference("workspaces", id);
        }
    }

    @Override
    protected String getBeanClassName(Element arg0) {
        String cls = super.getBeanClassName(arg0);
        
        if (cls == null) {
            cls = ProviderFactoryBean.class.getName();
        }
        
        return cls;
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition arg1, ParserContext arg2) throws BeanDefinitionStoreException {
        String id = element.getAttribute("id");
        
        if (id == null || "".equals(id)) {
            id = Provider.class.getName();
        }
        
        
        return id;
    }
    
    
}
 