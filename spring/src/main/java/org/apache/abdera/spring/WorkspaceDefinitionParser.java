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

import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("unchecked")
public class WorkspaceDefinitionParser extends org.apache.abdera.spring.AbstractSingleBeanDefinitionParser {

    @Override
    protected void doParse(Element element, ParserContext ctx, BeanDefinitionBuilder bean) {
        ManagedList collections = new ManagedList();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE
            // &&
            // n.getNodeName().equals("bean")) {
            // Element childElement = (Element)n;
            // BeanDefinitionHolder child = ctx.getDelegate().parseBeanDefinitionElement(childElement);
            // collections.add(child);
            // } else if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE &&
            // n.getNodeName().equals("ref")
            ) {
                Element childElement = (Element)n;
                Object child = ctx.getDelegate().parsePropertySubElement(childElement, bean.getRawBeanDefinition());
                collections.add(child);
            }

        }
        bean.addPropertyValue("collections", collections);

        NamedNodeMap atts = element.getAttributes();
        Node title = atts.getNamedItem("title");
        if (title != null) {
            bean.addPropertyValue("title", title.getNodeValue());
        }
    }

    @Override
    protected String getBeanClassName(Element arg0) {
        String cls = super.getBeanClassName(arg0);

        if (cls == null) {
            cls = SimpleWorkspaceInfo.class.getName();
        }

        return cls;
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

}
