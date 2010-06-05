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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSingleBeanDefinitionParser extends
    org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser {

    public AbstractSingleBeanDefinitionParser() {
        super();
    }

    @Override
    protected void doParse(Element element, ParserContext ctx, BeanDefinitionBuilder bean) {
        NamedNodeMap atts = element.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            Attr node = (Attr)atts.item(i);
            String val = node.getValue();
            String name = node.getLocalName();

            if ("abstract".equals(name)) {
                bean.setAbstract(true);
            } else if (!"id".equals(name) && !"name".equals(name)) {
                mapAttribute(bean, element, name, val);
            }
        }

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String name = n.getLocalName();

                mapElement(ctx, bean, (Element)n, name);
            }
        }
    }

    protected void mapElement(ParserContext ctx, BeanDefinitionBuilder bean, Element element, String name) {

    }

    protected void mapAttribute(BeanDefinitionBuilder bean, Element element, String name, String val) {

    }

    protected void setFirstChildAsProperty(Element element,
                                           ParserContext ctx,
                                           BeanDefinitionBuilder bean,
                                           String propertyName) {
        String id = getAndRegisterFirstChild(element, ctx, bean, propertyName);
        bean.addPropertyReference(propertyName, id);

    }

    protected String getAndRegisterFirstChild(Element element,
                                              ParserContext ctx,
                                              BeanDefinitionBuilder bean,
                                              String propertyName) {
        Element first = getFirstChild(element);

        if (first == null) {
            throw new IllegalStateException(propertyName + " property must have child elements!");
        }

        return getAndRegister(ctx, bean, first);
    }

    protected String getAndRegister(ParserContext ctx, BeanDefinitionBuilder bean, Element el) {
        // Seems odd that we have to do the registration, I wonder if there is a better way
        String id;
        BeanDefinition child;
        if (el.getNamespaceURI().equals(BeanDefinitionParserDelegate.BEANS_NAMESPACE_URI)) {
            String name = el.getLocalName();
            if ("ref".equals(name)) {
                id = el.getAttribute("bean");
                if (id == null) {
                    throw new IllegalStateException("<ref> elements must have a \"bean\" attribute!");
                }
                return id;
            } else if ("bean".equals(name)) {
                BeanDefinitionHolder bdh = ctx.getDelegate().parseBeanDefinitionElement(el);
                child = bdh.getBeanDefinition();
                id = bdh.getBeanName();
            } else {
                throw new UnsupportedOperationException("Elements with the name " + name
                    + " are not currently "
                    + "supported as sub elements of "
                    + el.getParentNode().getLocalName());
            }

        } else {
            child = ctx.getDelegate().parseCustomElement(el, bean.getBeanDefinition());
            id = child.toString();
        }

        ctx.getRegistry().registerBeanDefinition(id, child);
        return id;
    }

    protected Element getFirstChild(Element element) {
        Element first = null;
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                first = (Element)n;
            }
        }
        return first;
    }

}
