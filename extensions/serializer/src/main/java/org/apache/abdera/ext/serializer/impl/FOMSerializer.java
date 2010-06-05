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
package org.apache.abdera.ext.serializer.impl;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.abdera.ext.serializer.BaseSerializer;
import org.apache.abdera.ext.serializer.Conventions;
import org.apache.abdera.ext.serializer.ObjectContext;
import org.apache.abdera.ext.serializer.SerializationContext;
import org.apache.abdera.model.Comment;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ProcessingInstruction;
import org.apache.abdera.model.TextValue;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.xpath.XPath;

public class FOMSerializer extends BaseSerializer {

    public FOMSerializer() {
    }

    protected void finish(Object source,
                          ObjectContext objectContext,
                          SerializationContext context,
                          Conventions conventions) {
    }

    protected void init(Object source,
                        ObjectContext objectContext,
                        SerializationContext context,
                        Conventions conventions) {
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {
        StreamWriter sw = context.getStreamWriter();
        if (!(source instanceof Element))
            return;
        Element element = (Element)source;
        sw.startElement(element.getQName());
        for (QName attr : element.getAttributes())
            sw.writeAttribute(attr, element.getAttributeValue(attr));
        XPath xpath = context.getAbdera().getXPath();
        List<?> children = xpath.selectNodes("node()", element);
        for (Object child : children) {
            if (child instanceof Element) {
                process(child, new ObjectContext(child), context, conventions);
            } else if (child instanceof Comment) {
                Comment comment = (Comment)child;
                sw.writeComment(comment.getText());
            } else if (child instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction)child;
                sw.writePI(pi.getText(), pi.getTarget());
            } else if (child instanceof TextValue) {
                TextValue tv = (TextValue)child;
                sw.writeElementText(tv.getText());
            }
        }
        sw.endElement();
    }

}
