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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ObjectContext {

    private final Class objectType;
    private final Object parent;
    private final AccessibleObject accessor;
    private final Annotation[] annotations;
    private final Serializer serializer;
    private final Field[] fields;
    private final Method[] methods;

    public ObjectContext(Object object) {
        this(object, null, null);
    }

    public ObjectContext(Object object, Object parent, AccessibleObject accessor) {
        this.objectType =
            object != null ? object.getClass() : accessor != null ? AbstractSerializationContext
                .getReturnType(accessor) : null;
        this.parent = parent;
        this.accessor = accessor;
        this.annotations = initAnnotations();
        this.serializer = initSerializer();
        this.fields = initFields();
        this.methods = initMethods();
    }

    private Field[] initFields() {
        Field[] fields = objectType.getFields();
        List<Field> list = new ArrayList<Field>();
        for (Field field : fields) {
            int mods = field.getModifiers();
            // ignore static fields
            if (!Modifier.isStatic(mods)) {
                list.add(field);
            }
        }
        return list.toArray(new Field[list.size()]);
    }

    private Method[] initMethods() {
        Method[] methods = objectType.getMethods();
        List<Method> list = new ArrayList<Method>();
        for (Method method : methods) {
            // only methods that have no parameters, return a value, are not
            // abstract and are not static
            int mods = method.getModifiers();
            if (!Modifier.isStatic(mods) && !Modifier.isAbstract(mods)
                && method.getParameterTypes().length == 0
                && method.getReturnType() != Void.class) {
                list.add(method);
            }
        }
        return list.toArray(new Method[list.size()]);
    }

    private Annotation[] initAnnotations() {
        Map<Class<? extends Annotation>, Annotation> annotations =
            new HashMap<Class<? extends Annotation>, Annotation>();
        if (objectType != null) {
            for (Annotation annotation : objectType.getAnnotations()) {
                annotations.put(annotation.annotationType(), annotation);
            }
        }
        if (accessor != null) {
            for (Annotation annotation : accessor.getAnnotations()) {
                annotations.put(annotation.annotationType(), annotation);
            }
        }
        return annotations.values().toArray(new Annotation[annotations.size()]);
    }

    private Serializer initSerializer() {
        try {
            org.apache.abdera.ext.serializer.annotation.Serializer ser =
                getAnnotation(org.apache.abdera.ext.serializer.annotation.Serializer.class);
            if (ser != null) {
                Class<? extends Serializer> serclass = ser.value();
                return serclass.newInstance();
            }
            return null;
        } catch (Throwable t) {
            throw new SerializationException(t);
        }
    }

    public AccessibleObject getAccessor() {
        return accessor;
    }

    public Object getParent() {
        return parent;
    }

    public Class getObjectType() {
        return objectType;
    }

    public <X extends Annotation> X getAnnotation(Class<X> annotationType) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationType)
                return (X)annotation;
        }
        return null;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public Field[] getFields() {
        return fields;
    }

    public Method[] getMethods() {
        return methods;
    }

    public AccessibleObject[] getAccessors() {
        List<AccessibleObject> list = new ArrayList<AccessibleObject>();
        for (Method method : methods)
            list.add(method);
        for (Field field : fields)
            list.add(field);
        return list.toArray(new AccessibleObject[list.size()]);
    }

    public AccessibleObject[] getAccessors(Class<? extends Annotation> annotation, Conventions conventions) {
        List<AccessibleObject> accessors = new ArrayList<AccessibleObject>();
        for (AccessibleObject accessor : getAccessors()) {
            if (accessor.isAnnotationPresent(annotation) || annotation.equals(conventions.matchConvention(accessor))) {
                accessors.add(accessor);
            }
        }
        return accessors.toArray(new AccessibleObject[accessors.size()]);
    }

    public AccessibleObject getAccessor(Class<? extends Annotation> annotation, Conventions conventions) {
        for (AccessibleObject accessor : getAccessors()) {
            if (accessor.isAnnotationPresent(annotation) || annotation.equals(conventions.matchConvention(accessor))) {
                return accessor;
            }
        }
        return null;
    }

}
