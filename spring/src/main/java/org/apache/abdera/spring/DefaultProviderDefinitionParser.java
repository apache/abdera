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

import java.util.List;

import org.apache.abdera.protocol.server.Provider;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultProviderDefinitionParser extends org.apache.abdera.spring.AbstractSingleBeanDefinitionParser {

    @Override
    protected void mapAttribute(BeanDefinitionBuilder bean, Element element, String name, String val) {
        if (name.equals("base")) {
            bean.addPropertyValue(name, val);
        } else if (name.equals("class")) {
            bean.addPropertyValue("providerClass", val);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void mapElement(ParserContext ctx, BeanDefinitionBuilder bean, Element element, String name) {
        if (name.equals("workspaceManager")) {
            setFirstChildAsProperty(element, ctx, bean, "workspaceManager");
        } else if (name.equals("targetResolver")) {
            setFirstChildAsProperty(element, ctx, bean, "targetResolver");
        } else if (name.equals("subjectResolver")) {
            setFirstChildAsProperty(element, ctx, bean, name);
        } else if (name.equals("filter")) {
            MutablePropertyValues values = bean.getBeanDefinition().getPropertyValues();
            PropertyValue pv = values.getPropertyValue("filters");
            List filters = pv != null ? (List)pv.getValue() : new ManagedList();
            NodeList nodes = element.getChildNodes();
            Object child = null;
            if (element.hasAttribute("ref")) {
                if (!StringUtils.hasText(element.getAttribute("ref"))) {
                    ctx.getReaderContext().error(name + " contains empty 'ref' attribute", element);
                }
                child = new RuntimeBeanReference(element.getAttribute("ref"));
                ((RuntimeBeanReference)child).setSource(ctx.extractSource(element));
            } else if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node n = nodes.item(i);
                    if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element childElement = (Element)n;
                        child = ctx.getDelegate().parsePropertySubElement(childElement, bean.getRawBeanDefinition());
                    }
                }
            }
            if (child != null) {
                filters.add(child);
            }
            bean.addPropertyValue("filters", filters);
        } else if (name.equals("workspace")) {
            String id = getAndRegister(ctx, bean, element);
            bean.addPropertyReference("workspaces", id);
        } else if (name.equals("property")) {
            ctx.getDelegate().parsePropertyElement(element, bean.getRawBeanDefinition());
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
    protected String resolveId(Element element, AbstractBeanDefinition arg1, ParserContext arg2)
        throws BeanDefinitionStoreException {
        String id = element.getAttribute("id");

        if (id == null || "".equals(id)) {
            id = Provider.class.getName();
        }

        return id;
    }

}
