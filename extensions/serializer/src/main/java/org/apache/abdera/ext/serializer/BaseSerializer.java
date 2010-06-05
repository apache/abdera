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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.ext.serializer.annotation.Attribute;
import org.apache.abdera.ext.serializer.annotation.BaseURI;
import org.apache.abdera.ext.serializer.annotation.Extension;
import org.apache.abdera.ext.serializer.annotation.Language;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.ext.serializer.impl.ExtensionSerializer;
import org.apache.abdera.ext.serializer.impl.SimpleElementSerializer;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.writer.StreamWriter;

@SuppressWarnings("unchecked")
public abstract class BaseSerializer extends Serializer {

    public static final String DEFAULT = "##default";

    protected abstract void init(Object source,
                                 ObjectContext objectContext,
                                 SerializationContext context,
                                 Conventions conventions);

    protected abstract void finish(Object source,
                                   ObjectContext objectContext,
                                   SerializationContext context,
                                   Conventions conventions);

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {
    }

    public void serialize(Object source, ObjectContext objectContext, SerializationContext context) {
        Conventions conventions = ((ConventionSerializationContext)context).getConventions();
        init(source, objectContext, context, conventions);
        process(source, objectContext, context, conventions);
        finish(source, objectContext, context, conventions);
    }

    protected void writeTextValue(Object source,
                                  ObjectContext objectContext,
                                  SerializationContext context,
                                  Conventions conventions) {
        AccessibleObject accessor = objectContext.getAccessor(Value.class, conventions);
        Object value = null;
        if (accessor != null) {
            value = eval(accessor, source);
        }
        context.getStreamWriter().writeElementText(value != null ? toString(value) : toString(source));
    }

    protected void writeExtensions(Object source,
                                   ObjectContext objectContext,
                                   SerializationContext context,
                                   Conventions conventions) {
        AccessibleObject[] accessors = objectContext.getAccessors(Extension.class, conventions);
        for (AccessibleObject accessor : accessors) {
            Object value = eval(accessor, source);
            ObjectContext valueContext = new ObjectContext(value, source, accessor);
            Extension extension = valueContext.getAnnotation(Extension.class);
            boolean simple = extension != null ? extension.simple() : false;
            Serializer ser = context.getSerializer(valueContext);
            if (ser == null) {
                if (simple) {
                    QName qname = getQName(accessor);
                    ser = new SimpleElementSerializer(qname);
                } else {
                    ser = context.getSerializer(valueContext);
                    if (ser == null) {
                        QName qname = getQName(accessor);
                        ser = new ExtensionSerializer(qname);
                    }
                }
            }
            ser.serialize(value, valueContext, context);
        }
    }

    protected void writeAttributes(Object source,
                                   ObjectContext objectContext,
                                   SerializationContext context,
                                   Conventions conventions) {
        writeCommon(source, objectContext, context, conventions);
        StreamWriter sw = context.getStreamWriter();
        AccessibleObject[] accessors = objectContext.getAccessors(Attribute.class, conventions);
        for (AccessibleObject accessor : accessors) {
            QName qname = getQName(accessor);
            Object value = eval(accessor, source);
            if (value != null)
                sw.writeAttribute(qname, toString(value));
        }
    }

    protected boolean writeElement(Class<? extends Annotation> annotation,
                                   Serializer serializer,
                                   Object source,
                                   ObjectContext objectContext,
                                   SerializationContext context,
                                   Conventions conventions) {
        AccessibleObject accessor = objectContext.getAccessor(annotation, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            ObjectContext valueContext = new ObjectContext(value, source, accessor);
            serializer.serialize(value, valueContext, context);
            return true;
        }
        return false;
    }

    protected void writeElements(Class<? extends Annotation> annotation,
                                 Serializer serializer,
                                 Object source,
                                 ObjectContext objectContext,
                                 SerializationContext context,
                                 Conventions conventions) {
        AccessibleObject[] accessors = objectContext.getAccessors(annotation, conventions);
        for (AccessibleObject accessor : accessors) {
            if (accessor != null) {
                Object value = eval(accessor, source);
                Object[] values = toArray(value);
                for (Object val : values) {
                    ObjectContext valueContext = new ObjectContext(val, source, accessor);
                    serializer.serialize(val, valueContext, context);
                }
            }
        }
    }

    public static Object eval(AccessibleObject accessor, Object parent) {
        try {
            if (accessor instanceof Field)
                return ((Field)accessor).get(parent);
            else if (accessor instanceof Method)
                return ((Method)accessor).invoke(parent, new Object[0]);
            else
                return null;
        } catch (Throwable t) {
            throw new SerializationException(t);
        }
    }

    protected boolean hasAnnotation(AnnotatedElement item, Class<? extends Annotation> annotation) {
        return item.isAnnotationPresent(annotation);
    }

    protected void writeElement(StreamWriter sw, QName qname, String value) {
        sw.startElement(qname).writeElementText(value).endElement();
    }

    public static String toString(Object value) {
        if (value == null)
            return null;
        Object[] values = toArray(value);
        StringBuilder buf = new StringBuilder();
        for (int n = 0; n < values.length; n++) {
            if (n > 0)
                buf.append(", ");
            buf.append(values[n].toString());
        }
        return buf.toString();
    }

    public static Object[] toArray(Object value) {
        if (value == null)
            return new Object[0];
        if (value.getClass().isArray()) {
            return (Object[])value;
        } else if (value instanceof Collection) {
            return ((Collection)value).toArray();
        } else if (value instanceof Map) {
            return ((Map)value).values().toArray();
        } else if (value instanceof Dictionary) {
            List<Object> list = new ArrayList<Object>();
            Enumeration e = ((Dictionary)value).elements();
            while (e.hasMoreElements())
                list.add(e.nextElement());
            return list.toArray();
        } else if (value instanceof Iterator) {
            List<Object> list = new ArrayList<Object>();
            Iterator i = (Iterator)value;
            while (i.hasNext())
                list.add(i.next());
            return list.toArray();
        } else if (value instanceof Enumeration) {
            List<Object> list = new ArrayList<Object>();
            Enumeration e = (Enumeration)value;
            while (e.hasMoreElements())
                list.add(e.nextElement());
            return list.toArray();
        } else if (value instanceof Iterable) {
            List<Object> list = new ArrayList<Object>();
            Iterable v = (Iterable)value;
            Iterator i = v.iterator();
            while (i.hasNext())
                list.add(i.next());
            return list.toArray();
        } else {
            return new Object[] {value};
        }
    }

    protected static boolean isUndefined(String value) {
        return value == null || DEFAULT.equals(value);
    }

    protected static QName getQName(AccessibleObject accessor) {
        Extension ext = accessor.getAnnotation(Extension.class);
        if (ext != null)
            return getQName(ext);
        Attribute attr = accessor.getAnnotation(Attribute.class);
        if (attr != null)
            return getQName(attr);
        return new QName(accessor instanceof Method ? ((Method)accessor).getName() : ((Field)accessor).getName());
    }

    protected static QName getQName(Extension extension) {
        QName qname = null;
        if (extension != null) {
            if (isUndefined(extension.prefix()) && isUndefined(extension.ns()) && isUndefined(extension.name())) {
                qname = new QName(extension.ns(), extension.name(), extension.prefix());
            } else if (isUndefined(extension.prefix()) && !isUndefined(extension.ns())
                && !isUndefined(extension.name())) {
                qname = new QName(extension.ns(), extension.name());
            } else if (!isUndefined(extension.name())) {
                qname = new QName(extension.name());
            }
        }
        return qname;
    }

    protected static QName getQName(Attribute attribute) {
        QName qname = null;
        if (attribute != null) {
            if (isUndefined(attribute.prefix()) && isUndefined(attribute.ns()) && isUndefined(attribute.name())) {
                qname = new QName(attribute.ns(), attribute.name(), attribute.prefix());
            } else if (isUndefined(attribute.prefix()) && !isUndefined(attribute.ns())
                && !isUndefined(attribute.name())) {
                qname = new QName(attribute.ns(), attribute.name());
            } else if (!isUndefined(attribute.name())) {
                qname = new QName(attribute.name());
            }
        }
        return qname;
    }

    @SuppressWarnings("deprecation")
    protected void writeCommon(Object source,
                               ObjectContext objectContext,
                               SerializationContext context,
                               Conventions conventions) {
        StreamWriter sw = context.getStreamWriter();
        String lang = null;
        AccessibleObject accessor = objectContext.getAccessor(Language.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null) {
                if (value instanceof Lang || value instanceof org.apache.abdera.i18n.lang.Lang) {
                    lang = value.toString();
                } else {
                    lang = toString(value);
                }
            }
        }
        if (lang == null) {
            Language _lang = objectContext.getAnnotation(Language.class);
            if (_lang != null && !_lang.value().equals(DEFAULT)) {
                lang = _lang.value();
            }
        }
        if (lang != null)
            sw.writeLanguage(lang);

        String base = null;
        accessor = objectContext.getAccessor(BaseURI.class, conventions);
        if (accessor != null) {
            Object value = eval(accessor, source);
            if (value != null)
                base = toString(value);
        }
        if (base != null)
            sw.writeBase(base);
    }
}
