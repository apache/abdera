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

import java.lang.reflect.AccessibleObject;

import javax.xml.namespace.QName;

import org.apache.abdera.ext.serializer.Conventions;
import org.apache.abdera.ext.serializer.ObjectContext;
import org.apache.abdera.ext.serializer.SerializationContext;
import org.apache.abdera.ext.serializer.annotation.Text;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.writer.StreamWriter;

public class TextSerializer extends ElementSerializer {

    public TextSerializer(QName qname) {
        super(qname);
    }

    public TextSerializer() {
        super(null);
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

        Type type = Type.TEXT;
        Object contentValue = null;
        ObjectContext valueContext = null;
        AccessibleObject accessor = objectContext.getAccessor(Value.class, conventions);
        if (accessor != null && !(source instanceof Element)) {
            contentValue = eval(accessor, source);
            valueContext = new ObjectContext(contentValue, source, accessor);
            Text _text = valueContext.getAnnotation(Text.class);
            type = _text != null ? _text.type() : type;
        } else {
            Text _text = objectContext.getAnnotation(Text.class);
            type = _text != null ? _text.type() : type;
            contentValue = source;
            valueContext = objectContext;
        }
        QName qname = this.qname != null ? this.qname : getQName(objectContext.getAccessor());
        StreamWriter sw = context.getStreamWriter();
        sw.startText(qname, type);
        writeAttributes(source, objectContext, context, conventions);

        switch (type) {
            case TEXT:
            case HTML:
                sw.writeElementText(toString(contentValue));
                break;
            case XHTML:
                Div div = null;
                if (contentValue instanceof Div)
                    div = (Div)contentValue;
                else {
                    div = context.getAbdera().getFactory().newDiv();
                    div.setValue(toString(contentValue));
                }
                context.serialize(div, new ObjectContext(div));
                break;
        }
    }

}
