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

import org.apache.abdera.ext.serializer.Conventions;
import org.apache.abdera.ext.serializer.ObjectContext;
import org.apache.abdera.ext.serializer.SerializationContext;
import org.apache.abdera.ext.serializer.annotation.Author;
import org.apache.abdera.ext.serializer.annotation.Category;
import org.apache.abdera.ext.serializer.annotation.Content;
import org.apache.abdera.ext.serializer.annotation.Contributor;
import org.apache.abdera.ext.serializer.annotation.Control;
import org.apache.abdera.ext.serializer.annotation.Edited;
import org.apache.abdera.ext.serializer.annotation.ID;
import org.apache.abdera.ext.serializer.annotation.Link;
import org.apache.abdera.ext.serializer.annotation.Published;
import org.apache.abdera.ext.serializer.annotation.Rights;
import org.apache.abdera.ext.serializer.annotation.Summary;
import org.apache.abdera.ext.serializer.annotation.Title;
import org.apache.abdera.ext.serializer.annotation.Updated;
import org.apache.abdera.util.Constants;

public class EntrySerializer extends ElementSerializer {

    public EntrySerializer() {
        super(Constants.ENTRY);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {
        writeAttributes(source, objectContext, context, conventions);
        writeElement(ID.class, new IRISerializer(Constants.ID), source, objectContext, context, conventions);
        writeElement(Title.class, new TextSerializer(Constants.TITLE), source, objectContext, context, conventions);
        writeElement(Summary.class, new TextSerializer(Constants.SUMMARY), source, objectContext, context, conventions);
        writeElement(Rights.class, new TextSerializer(Constants.RIGHTS), source, objectContext, context, conventions);
        writeElement(Updated.class,
                     new DateTimeSerializer(Constants.UPDATED),
                     source,
                     objectContext,
                     context,
                     conventions);
        writeElement(Published.class,
                     new DateTimeSerializer(Constants.PUBLISHED),
                     source,
                     objectContext,
                     context,
                     conventions);
        writeElement(Edited.class,
                     new DateTimeSerializer(Constants.EDITED),
                     source,
                     objectContext,
                     context,
                     conventions);
        writeElement(Content.class, new ContentSerializer(), source, objectContext, context, conventions);
        writeElement(Control.class, new ControlSerializer(), source, objectContext, context, conventions);
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
