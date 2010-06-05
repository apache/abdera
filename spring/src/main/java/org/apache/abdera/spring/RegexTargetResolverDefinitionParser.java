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
package org.apache.abdera.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RegexTargetResolverDefinitionParser extends org.apache.abdera.spring.AbstractSingleBeanDefinitionParser {

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

        List<String> collection = new ArrayList<String>();
        List<String> category = new ArrayList<String>();
        List<String> entry = new ArrayList<String>();
        List<String> service = new ArrayList<String>();
        List<String> media = new ArrayList<String>();

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String name = n.getLocalName();

                if (name.equals("collection")) {
                    collection.add(getText(n));
                } else if (name.equals("category")) {
                    category.add(getText(n));
                } else if (name.equals("entry")) {
                    entry.add(getText(n));
                } else if (name.equals("media")) {
                    media.add(getText(n));
                } else if (name.equals("service")) {
                    service.add(getText(n));
                }
            }
        }

        bean.addPropertyValue("categories", category);
        bean.addPropertyValue("collections", collection);
        bean.addPropertyValue("entries", entry);
        bean.addPropertyValue("media", media);
        bean.addPropertyValue("services", service);
    }

    private String getText(Node n) {
        if (n == null) {
            return null;
        }

        Node n1 = getChild(n, Node.TEXT_NODE);

        if (n1 == null) {
            return null;
        }

        return n1.getNodeValue().trim();
    }

    public static Node getChild(Node parent, int type) {
        Node n = parent.getFirstChild();
        while (n != null && type != n.getNodeType()) {
            n = n.getNextSibling();
        }
        if (n == null) {
            return null;
        }
        return n;
    }

    @Override
    protected String getBeanClassName(Element arg0) {
        String cls = super.getBeanClassName(arg0);

        if (cls == null) {
            cls = RegexTargetResolverFactoryBean.class.getName();
        }

        return cls;
    }

    @Override
    protected String resolveId(Element arg0, AbstractBeanDefinition arg1, ParserContext arg2)
        throws BeanDefinitionStoreException {
        String id = super.resolveId(arg0, arg1, arg2);

        if (id == null) {
            id = RegexTargetResolverFactoryBean.class.getName() + new Object().hashCode();
        }
        return id;
    }

}
