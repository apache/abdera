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

import javax.xml.namespace.QName;

import org.apache.abdera.ext.serializer.Conventions;
import org.apache.abdera.ext.serializer.ObjectContext;
import org.apache.abdera.ext.serializer.SerializationContext;
import org.apache.abdera.ext.serializer.annotation.Email;
import org.apache.abdera.ext.serializer.annotation.URI;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;

public class PersonSerializer extends ElementSerializer {

    public PersonSerializer(QName qname) {
        super(qname);
    }

    public PersonSerializer() {
        super(null);
    }

    protected void init(Object source,
                        ObjectContext objectContext,
                        SerializationContext context,
                        Conventions conventions) {
        QName qname = this.qname != null ? this.qname : getQName(objectContext.getAccessor());
        StreamWriter sw = context.getStreamWriter();
        sw.startElement(qname);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {
        writeAttributes(source, objectContext, context, conventions);
        if (!writeElement(Value.class,
                          new SimpleElementSerializer(Constants.NAME),
                          source,
                          objectContext,
                          context,
                          conventions)) {
            context.getStreamWriter().startElement(Constants.NAME).writeElementText(toString(source)).endElement();
        }
        writeElement(Email.class,
                     new SimpleElementSerializer(Constants.EMAIL),
                     source,
                     objectContext,
                     context,
                     conventions);
        writeElement(URI.class, new SimpleElementSerializer(Constants.URI), source, objectContext, context, conventions);
        writeExtensions(source, objectContext, context, conventions);
    }

}
