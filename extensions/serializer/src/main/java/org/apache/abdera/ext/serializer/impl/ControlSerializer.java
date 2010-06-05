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
import org.apache.abdera.ext.serializer.Serializer;
import org.apache.abdera.ext.serializer.annotation.Draft;
import org.apache.abdera.util.Constants;

public class ControlSerializer extends ElementSerializer {

    public ControlSerializer() {
        super(Constants.CONTROL);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {
        writeAttributes(source, objectContext, context, conventions);
        AccessibleObject accessor = objectContext.getAccessor(Draft.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value instanceof Boolean) {
                String val = ((Boolean)value).booleanValue() ? "yes" : "no";
                ObjectContext valueContext = new ObjectContext(val);
                Serializer ser = new SimpleElementSerializer(Constants.DRAFT);
                ser.serialize(val, valueContext, context);
            } else {
                ObjectContext valueContext = new ObjectContext(value);
                context.serialize(value, valueContext);
            }
        }
        writeExtensions(source, objectContext, context, conventions);
    }

}
