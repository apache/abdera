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
import org.apache.abdera.ext.serializer.annotation.Author;
import org.apache.abdera.ext.serializer.annotation.Category;
import org.apache.abdera.ext.serializer.annotation.Contributor;
import org.apache.abdera.ext.serializer.annotation.ID;
import org.apache.abdera.ext.serializer.annotation.Icon;
import org.apache.abdera.ext.serializer.annotation.Link;
import org.apache.abdera.ext.serializer.annotation.Logo;
import org.apache.abdera.ext.serializer.annotation.Rights;
import org.apache.abdera.ext.serializer.annotation.Subtitle;
import org.apache.abdera.ext.serializer.annotation.Title;
import org.apache.abdera.ext.serializer.annotation.Updated;
import org.apache.abdera.util.Constants;

public class SourceSerializer extends ElementSerializer {

    public SourceSerializer() {
        super(Constants.SOURCE);
    }

    protected SourceSerializer(QName qname) {
        super(qname);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {
        writeAttributes(source, objectContext, context, conventions);
        writeElement(ID.class, new IRISerializer(Constants.ID), source, objectContext, context, conventions);
        writeElement(Icon.class, new IRISerializer(Constants.ICON), source, objectContext, context, conventions);
        writeElement(Logo.class, new IRISerializer(Constants.LOGO), source, objectContext, context, conventions);
        writeElement(Title.class, new TextSerializer(Constants.TITLE), source, objectContext, context, conventions);
        writeElement(Subtitle.class,
                     new TextSerializer(Constants.SUBTITLE),
                     source,
                     objectContext,
                     context,
                     conventions);
        writeElement(Rights.class, new TextSerializer(Constants.RIGHTS), source, objectContext, context, conventions);
        writeElement(Updated.class,
                     new DateTimeSerializer(Constants.UPDATED),
                     source,
                     objectContext,
                     context,
                     conventions);
        writeElements(Link.class, new LinkSerializer(), source, objectContext, context, conventions);
        writeElements(Category.class, new CategorySerializer(), source, objectContext, context, conventions);
        writeElements(Author.class, new PersonSerializer(Constants.AUTHOR), source, objectContext, context, conventions);
        writeElements(Contributor.class,
                      new PersonSerializer(Constants.CONTRIBUTOR),
                      source,
                      objectContext,
                      context,
                      conventions);
        writeExtensions(source, objectContext, context, conventions);
    }
}
