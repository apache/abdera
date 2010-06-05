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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.AccessibleObject;

import javax.activation.DataHandler;

import org.apache.abdera.ext.serializer.Conventions;
import org.apache.abdera.ext.serializer.ObjectContext;
import org.apache.abdera.ext.serializer.SerializationContext;
import org.apache.abdera.ext.serializer.SerializationException;
import org.apache.abdera.ext.serializer.annotation.Content;
import org.apache.abdera.ext.serializer.annotation.MediaType;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

public class ContentSerializer extends ElementSerializer {

    public ContentSerializer() {
        super(Constants.CONTENT);
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
            Content _content = valueContext.getAnnotation(Content.class);
            type = _content != null ? _content.type() : type;
        } else {
            Content _content = objectContext.getAnnotation(Content.class);
            type = _content != null ? _content.type() : type;
            contentValue = source;
            valueContext = objectContext;
        }

        StreamWriter sw = context.getStreamWriter();
        sw.startContent(type);
        writeAttributes(source, objectContext, context, conventions);

        if (type == Type.MEDIA || type == Type.XML) {
            String mediatype = null;
            AccessibleObject mtaccessor = valueContext.getAccessor(MediaType.class, conventions);
            if (mtaccessor != null) {
                Object mtvalue = eval(mtaccessor, contentValue);
                mediatype = mtvalue != null ? toString(mtvalue) : null;
            }
            if (mediatype == null) {
                MediaType mt = valueContext.getAnnotation(MediaType.class);
                mediatype = mt != null && !mt.value().equals(DEFAULT) ? mt.value() : null;
            }
            if (mediatype != null)
                sw.writeAttribute("type", mediatype);
        }

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
            case XML:
                Element el = null;
                if (contentValue instanceof Element)
                    el = (Element)contentValue;
                else {
                    StringReader sr = new StringReader(toString(contentValue));
                    Document<Element> doc = context.getAbdera().getParser().parse(sr);
                    el = doc.getRoot();
                }
                context.serialize(el, new ObjectContext(el));
                break;
            case MEDIA:
                try {
                    if (contentValue instanceof DataHandler)
                        sw.writeElementText((DataHandler)contentValue);
                    else if (contentValue instanceof InputStream)
                        sw.writeElementText((InputStream)contentValue);
                    else
                        sw.writeElementText(toString(contentValue));
                } catch (IOException e) {
                    throw new SerializationException(e);
                }
        }
    }

}
