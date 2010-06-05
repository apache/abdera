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

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.Calendar;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.serializer.annotation.EntityTag;
import org.apache.abdera.ext.serializer.annotation.LastModified;
import org.apache.abdera.ext.serializer.annotation.MediaType;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.writer.StreamWriter;

public class ObjectResponseContext extends StreamWriterResponseContext {

    private final Object object;
    private final ObjectContext objectContext;
    private final Conventions conventions;
    private final Class<? extends SerializationContext> context;

    public ObjectResponseContext(Object object, Abdera abdera, String encoding, String sw) {
        this(object, null, null, abdera, encoding, sw);
    }

    public ObjectResponseContext(Object object,
                                 Class<? extends SerializationContext> context,
                                 Conventions conventions,
                                 Abdera abdera,
                                 String encoding,
                                 String sw) {
        super(abdera, encoding, sw);
        this.object = object;
        this.objectContext = new ObjectContext(object);
        this.conventions = conventions != null ? conventions : new DefaultConventions();
        this.context = context != null ? context : ConventionSerializationContext.class;
        init();
    }

    public ObjectResponseContext(Object object, Abdera abdera, String encoding) {
        this(object, null, null, abdera, encoding, null);
    }

    public ObjectResponseContext(Object object,
                                 Class<? extends SerializationContext> context,
                                 Conventions conventions,
                                 Abdera abdera,
                                 String encoding) {
        this(object, context, conventions, abdera, encoding, null);
    }

    public ObjectResponseContext(Object object, Abdera abdera) {
        this(object, null, null, abdera, null, null);
    }

    public ObjectResponseContext(Object object,
                                 Class<? extends SerializationContext> context,
                                 Conventions conventions,
                                 Abdera abdera) {
        this(object, context, null, abdera, null, null);
    }

    private void init() {
        setContentType(getObjectContentType());
        setEntityTag(getObjectEntityTag());
        setLastModified(getObjectLastModified());
    }

    private Date getObjectLastModified() {
        Date date = null;
        AccessibleObject accessor = objectContext.getAccessor(LastModified.class, conventions);
        if (accessor != null) {
            Object value = BaseSerializer.eval(accessor, object);
            date = getDate(value);
        }
        return date;
    }

    private Date getDate(Object value) {
        Date date = null;
        if (value == null)
            return null;
        if (value instanceof Date) {
            date = (Date)value;
        } else if (value instanceof Calendar) {
            date = ((Calendar)value).getTime();
        } else if (value instanceof Long) {
            date = new Date(((Long)value).longValue());
        } else if (value instanceof String) {
            date = AtomDate.parse((String)value);
        } else {
            date = AtomDate.parse(value.toString());
        }
        return date;
    }

    private String getObjectEntityTag() {
        String etag = null;
        AccessibleObject accessor = objectContext.getAccessor(EntityTag.class, conventions);
        if (accessor != null) {
            Object value = BaseSerializer.eval(accessor, object);
            etag = value != null ? BaseSerializer.toString(value) : null;
        }
        return etag;
    }

    private String getObjectContentType() {
        String ctype = null;
        AccessibleObject accessor = objectContext.getAccessor(MediaType.class, conventions);
        if (accessor != null) {
            Object value = BaseSerializer.eval(accessor, object);
            ctype = value != null ? BaseSerializer.toString(value) : null;
        }
        if (ctype == null) {
            MediaType content_type = objectContext.getAnnotation(MediaType.class);
            if (content_type != null && !content_type.value().equals(BaseSerializer.DEFAULT)) {
                ctype = content_type.value();
            }
        }
        return ctype;
    }

    @Override
    protected void writeTo(StreamWriter sw) throws IOException {
        SerializationContext context = newSerializationContext(getAbdera(), conventions, sw);
        sw.startDocument();
        context.serialize(object, objectContext);
        sw.endDocument();
    }

    private SerializationContext newSerializationContext(Abdera abdera, Conventions conventions, StreamWriter sw) {
        try {
            return context.getConstructor(Abdera.class, Conventions.class, StreamWriter.class).newInstance(abdera,
                                                                                                           conventions,
                                                                                                           sw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
