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

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.StreamWriter;

public interface SerializationContext extends Serializable {

    Abdera getAbdera();

    StreamWriter getStreamWriter();

    void serialize(Object object);

    void serialize(Object object, ObjectContext objectContext);

    void serialize(Object object, Serializer serializer);

    void serialize(Object object, ObjectContext objectContext, Serializer serializer);

    void setSerializer(Class<?> type, Serializer serializer);

    Serializer getSerializer(ObjectContext objectContext);

    boolean hasSerializer(ObjectContext objectContext);

    boolean hasSerializer(Object object);

    boolean hasSerializer(Object object, Object parent, AccessibleObject accessor);

    boolean hasSerializer(Class<?> type);

    boolean hasSerializer(AccessibleObject accessor);

}
