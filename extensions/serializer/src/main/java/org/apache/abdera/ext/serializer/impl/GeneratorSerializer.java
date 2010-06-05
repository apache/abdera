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
import org.apache.abdera.ext.serializer.annotation.Generator;
import org.apache.abdera.ext.serializer.annotation.URI;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

public class GeneratorSerializer extends ElementSerializer {

    public GeneratorSerializer() {
        super(Constants.GENERATOR);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {
        writeAttributes(source, objectContext, context, conventions);
        StreamWriter sw = context.getStreamWriter();
        Generator _generator = objectContext.getAnnotation(Generator.class);

        String uri = null;
        AccessibleObject accessor = objectContext.getAccessor(URI.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                uri = toString(value);
        }
        if (uri == null) {
            URI _uri = objectContext.getAnnotation(URI.class);
            if (_uri != null && !_uri.value().equals(DEFAULT)) {
                uri = _uri.value();
            }
        }
        if (uri == null && _generator != null && !_generator.uri().equals(DEFAULT)) {
            uri = _generator.uri();
        }
        if (uri != null)
            sw.writeAttribute("uri", uri);

        String version = null;
        accessor = objectContext.getAccessor(URI.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                version = toString(value);
        }
        if (version == null) {
            URI _version = objectContext.getAnnotation(URI.class);
            if (_version != null && !_version.value().equals(DEFAULT)) {
                version = _version.value();
            }
        }
        if (version == null && _generator != null && !_generator.version().equals(DEFAULT)) {
            version = _generator.version();
        }
        if (version != null)
            sw.writeAttribute("version", version);

        writeTextValue(source, objectContext, context, conventions);
    }

}
