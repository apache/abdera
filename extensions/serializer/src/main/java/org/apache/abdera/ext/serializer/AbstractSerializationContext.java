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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.serializer.impl.FOMSerializer;
import org.apache.abdera.model.Element;
import org.apache.abdera.writer.StreamWriter;

@SuppressWarnings("unchecked")
public abstract class AbstractSerializationContext implements SerializationContext {

    private final Abdera abdera;
    private final StreamWriter streamWriter;
    private final Map<Class, Serializer> serializers = new HashMap<Class, Serializer>();

    protected AbstractSerializationContext(StreamWriter streamWriter) {
        this(new Abdera(), streamWriter);
    }

    protected AbstractSerializationContext(Abdera abdera, StreamWriter streamWriter) {
        this.abdera = abdera;
        this.streamWriter = streamWriter;
        setSerializer(Element.class, new FOMSerializer());
    }

    public StreamWriter getStreamWriter() {
        return this.streamWriter;
    }

    public Abdera getAbdera() {
        return abdera;
    }

    public Serializer getSerializer(ObjectContext objectContext) {
        try {
            Class type = objectContext.getObjectType();
            Serializer serializer = serializers.get(type);
            if (serializer == null)
                serializer = objectContext.getSerializer();
            if (serializer == null) {
                for (Annotation annotation : objectContext.getAnnotations()) {
                    serializer = serializers.get(annotation.annotationType());
                    if (serializer != null)
                        return serializer;
                }
            }
            if (serializer == null && !type.isAnnotation()) {
                for (Class knownType : serializers.keySet()) {
                    if (!knownType.isAnnotation() && knownType.isAssignableFrom(type)) {
                        return serializers.get(knownType);
                    }
                }
            }
            return serializer;
        } catch (Throwable t) {
            throw new SerializationException(t);
        }
    }

    public boolean hasSerializer(ObjectContext objectContext) {
        return getSerializer(objectContext) != null;
    }

    public boolean hasSerializer(Object object) {
        return hasSerializer(new ObjectContext(object));
    }

    public boolean hasSerializer(Object object, Object parent, AccessibleObject accessor) {
        return hasSerializer(new ObjectContext(object, parent, accessor));
    }

    public boolean hasSerializer(Class<?> type) {
        if (serializers.containsKey(type))
            return true;
        if (!type.isAnnotation()) {
            for (Class<?> t : serializers.keySet()) {
                if (!t.isAnnotation() && t.isAssignableFrom(type))
                    return true;
            }
        }
        return false;
    }

    public boolean hasSerializer(AccessibleObject accessor) {
        Class<? extends Object> returnType = getReturnType(accessor);
        org.apache.abdera.ext.serializer.annotation.Serializer serializer =
            accessor.getAnnotation(org.apache.abdera.ext.serializer.annotation.Serializer.class);
        if (serializer != null && hasSerializer(serializer.value()))
            return true;
        if (returnType != null && hasSerializer(returnType))
            return true;
        return false;
    }

    public void setSerializer(Class type, Serializer Serializer) {
        serializers.put(type, Serializer);
    }

    public void serialize(Object object) {
        serialize(object, new ObjectContext(object));
    }

    public void serialize(Object object, ObjectContext objectContext) {
        if (objectContext == null)
            objectContext = new ObjectContext(object);
        Serializer serializer = getSerializer(objectContext);
        if (serializer != null)
            serialize(object, objectContext, serializer);
        else
            throw new SerializationException("No Serializer available for " + objectContext.getObjectType());
    }

    public void serialize(Object object, Serializer serializer) {
        serialize(object, new ObjectContext(object), serializer);
    }

    public void serialize(Object object, ObjectContext objectContext, Serializer serializer) {
        if (objectContext == null)
            objectContext = new ObjectContext(object);
        Serializer overrideSerializer = getSerializer(objectContext);
        if (overrideSerializer != null)
            serializer = overrideSerializer;
        if (serializer != null) {
            serializer.serialize(object, objectContext, this);
        } else {
            throw new SerializationException("No Serializer available for " + objectContext.getObjectType());
        }
    }

    public static Class<? extends Object> getReturnType(AccessibleObject accessor) {
        if (accessor instanceof Field)
            return ((Field)accessor).getType();
        else if (accessor instanceof Method)
            return ((Method)accessor).getReturnType();
        else
            return null;
    }

}
