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
package org.apache.abdera.ext.serializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.StreamWriter;

public class ConventionSerializationContext extends DefaultSerializationContext {

    private static final long serialVersionUID = 7504071837124132972L;

    protected Conventions conventions;

    public ConventionSerializationContext(StreamWriter streamWriter) {
        this(new Abdera(), streamWriter);
    }

    public ConventionSerializationContext(Conventions conventions, StreamWriter streamWriter) {
        this(null, conventions, streamWriter);
    }

    public ConventionSerializationContext(Abdera abdera, StreamWriter streamWriter) {
        super(abdera, streamWriter);
        this.conventions = new DefaultConventions();
        initSerializers();
    }

    public ConventionSerializationContext(Abdera abdera, Conventions conventions, StreamWriter streamWriter) {
        super(abdera, streamWriter);
        this.conventions = conventions;
        initSerializers();
    }

    public Conventions getConventions() {
        return conventions;
    }

    public void setConventions(Conventions conventions) {
        this.conventions = conventions;
    }

    public boolean hasSerializer(AccessibleObject accessor) {
        boolean answer = super.hasSerializer(accessor);
        if (answer)
            return true;
        Class<? extends Annotation> annotation = getConventions().matchConvention(accessor);
        return annotation != null && hasSerializer(annotation);
    }

    private <T> void initSerializers() {
        for (Class<? extends Annotation> type : conventions) {
            org.apache.abdera.ext.serializer.annotation.Serializer serializer =
                type.getAnnotation(org.apache.abdera.ext.serializer.annotation.Serializer.class);
            if (serializer != null) {
                try {
                    Serializer ser = serializer.value().newInstance();
                    this.setSerializer(type, ser);
                } catch (Throwable t) {
                    throw new SerializationException(t);
                }
            }
        }
    }

}
