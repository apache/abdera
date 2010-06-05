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

import org.apache.abdera.ext.serializer.Conventions;
import org.apache.abdera.ext.serializer.ObjectContext;
import org.apache.abdera.ext.serializer.SerializationContext;
import org.apache.abdera.ext.serializer.annotation.HrefLanguage;
import org.apache.abdera.ext.serializer.annotation.Link;
import org.apache.abdera.ext.serializer.annotation.MediaType;
import org.apache.abdera.ext.serializer.annotation.Rel;
import org.apache.abdera.ext.serializer.annotation.Title;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

public class LinkSerializer extends ElementSerializer {

    public LinkSerializer() {
        super(Constants.LINK);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {

        StreamWriter sw = context.getStreamWriter();
        Link _link = objectContext.getAnnotation(Link.class);

        String rel = null;
        AccessibleObject accessor = objectContext.getAccessor(Rel.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                rel = toString(value);
        }
        if (rel == null) {
            Rel _rel = objectContext.getAnnotation(Rel.class);
            if (_rel != null && !_rel.value().equals(DEFAULT)) {
                rel = _rel.value();
            }
        }
        if (rel == null && _link != null && !_link.rel().equals(DEFAULT)) {
            rel = _link.rel();
        }
        if (rel != null)
            sw.writeAttribute("rel", rel);

        String type = null;
        accessor = objectContext.getAccessor(MediaType.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                type = toString(value);
        }
        if (type == null) {
            MediaType _type = objectContext.getAnnotation(MediaType.class);
            if (_type != null && !_type.value().equals(DEFAULT)) {
                type = _type.value();
            }
        }
        if (type == null && _link != null && !_link.type().equals(DEFAULT)) {
            type = _link.type();
        }
        if (type != null)
            sw.writeAttribute("type", type);

        String title = null;
        accessor = objectContext.getAccessor(Title.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                title = toString(value);
        }
        if (title == null && _link != null && !_link.title().equals(DEFAULT)) {
            title = _link.title();
        }
        if (title != null)
            sw.writeAttribute("title", title);

        String hreflang = null;
        accessor = objectContext.getAccessor(HrefLanguage.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                hreflang = toString(value);
        }
        if (hreflang == null) {
            HrefLanguage _hreflang = objectContext.getAnnotation(HrefLanguage.class);
            if (_hreflang != null && !_hreflang.value().equals(DEFAULT)) {
                hreflang = _hreflang.value();
            }
        }
        if (hreflang == null && _link != null && !_link.hreflang().equals(DEFAULT)) {
            hreflang = _link.hreflang();
        }
        if (hreflang != null)
            sw.writeAttribute("hreflang", hreflang);

        String href = null;
        accessor = objectContext.getAccessor(Value.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                href = toString(value);
        }
        if (href == null)
            href = toString(source);
        if (href != null)
            sw.writeAttribute("href", href);

        writeAttributes(source, objectContext, context, conventions);
        writeExtensions(source, objectContext, context, conventions);
    }
}
