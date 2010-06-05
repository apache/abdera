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
import org.apache.abdera.ext.serializer.annotation.Category;
import org.apache.abdera.ext.serializer.annotation.Label;
import org.apache.abdera.ext.serializer.annotation.Scheme;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

public class CategorySerializer extends ElementSerializer {

    public CategorySerializer() {
        super(Constants.CATEGORY);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {

        StreamWriter sw = context.getStreamWriter();
        Category _category = objectContext.getAnnotation(Category.class);

        String scheme = null;
        AccessibleObject accessor = objectContext.getAccessor(Scheme.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                scheme = toString(value);
        }
        if (scheme == null) {
            Scheme _scheme = objectContext.getAnnotation(Scheme.class);
            if (_scheme != null && !_scheme.value().equals(DEFAULT)) {
                scheme = _scheme.value();
            }
        }
        if (scheme == null && _category != null && !_category.scheme().equals(DEFAULT)) {
            scheme = _category.scheme();
        }
        if (scheme != null)
            sw.writeAttribute("scheme", scheme);

        String label = null;
        accessor = objectContext.getAccessor(Label.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                label = toString(value);
        }
        if (label == null) {
            Label _label = objectContext.getAnnotation(Label.class);
            if (_label != null && !_label.value().equals(DEFAULT)) {
                label = _label.value();
            }
        }
        if (label == null && _category != null && !_category.label().equals(DEFAULT)) {
            label = _category.label();
        }
        if (label != null)
            sw.writeAttribute("label", label);

        String term = null;
        accessor = objectContext.getAccessor(Value.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                term = toString(value);
        }
        if (term == null)
            term = toString(source);
        if (term != null)
            sw.writeAttribute("term", term);

        writeAttributes(source, objectContext, context, conventions);
        writeExtensions(source, objectContext, context, conventions);
    }
}
