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

import org.apache.abdera.ext.serializer.annotation.Accept;
import org.apache.abdera.ext.serializer.annotation.Author;
import org.apache.abdera.ext.serializer.annotation.BaseURI;
import org.apache.abdera.ext.serializer.annotation.Categories;
import org.apache.abdera.ext.serializer.annotation.Category;
import org.apache.abdera.ext.serializer.annotation.Collection;
import org.apache.abdera.ext.serializer.annotation.Content;
import org.apache.abdera.ext.serializer.annotation.Contributor;
import org.apache.abdera.ext.serializer.annotation.Control;
import org.apache.abdera.ext.serializer.annotation.DateTime;
import org.apache.abdera.ext.serializer.annotation.Draft;
import org.apache.abdera.ext.serializer.annotation.Edited;
import org.apache.abdera.ext.serializer.annotation.Email;
import org.apache.abdera.ext.serializer.annotation.Entry;
import org.apache.abdera.ext.serializer.annotation.Extension;
import org.apache.abdera.ext.serializer.annotation.Generator;
import org.apache.abdera.ext.serializer.annotation.HrefLanguage;
import org.apache.abdera.ext.serializer.annotation.ID;
import org.apache.abdera.ext.serializer.annotation.IRI;
import org.apache.abdera.ext.serializer.annotation.Icon;
import org.apache.abdera.ext.serializer.annotation.Label;
import org.apache.abdera.ext.serializer.annotation.Language;
import org.apache.abdera.ext.serializer.annotation.Length;
import org.apache.abdera.ext.serializer.annotation.Link;
import org.apache.abdera.ext.serializer.annotation.Logo;
import org.apache.abdera.ext.serializer.annotation.MediaType;
import org.apache.abdera.ext.serializer.annotation.Person;
import org.apache.abdera.ext.serializer.annotation.Published;
import org.apache.abdera.ext.serializer.annotation.Rel;
import org.apache.abdera.ext.serializer.annotation.Rights;
import org.apache.abdera.ext.serializer.annotation.Scheme;
import org.apache.abdera.ext.serializer.annotation.Source;
import org.apache.abdera.ext.serializer.annotation.Src;
import org.apache.abdera.ext.serializer.annotation.Subtitle;
import org.apache.abdera.ext.serializer.annotation.Summary;
import org.apache.abdera.ext.serializer.annotation.Text;
import org.apache.abdera.ext.serializer.annotation.Title;
import org.apache.abdera.ext.serializer.annotation.URI;
import org.apache.abdera.ext.serializer.annotation.Updated;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.ext.serializer.annotation.Version;
import org.apache.abdera.ext.serializer.annotation.Workspace;

public class DefaultConventions extends AbstractConventions {

    private static final long serialVersionUID = 6797950641657672805L;

    private static final Class<?>[] annotations =
        {Accept.class, Author.class, BaseURI.class, Categories.class, Category.class, Collection.class, Content.class,
         Contributor.class, Control.class, Draft.class, Edited.class, Email.class, Entry.class, Extension.class,
         Generator.class, HrefLanguage.class, Icon.class, ID.class, Label.class, Language.class, Length.class,
         Link.class, Logo.class, MediaType.class, Published.class, Rel.class, Rights.class, Scheme.class, Source.class,
         Src.class, Subtitle.class, Summary.class, Title.class, Updated.class, URI.class, Value.class, Version.class,
         Workspace.class, Text.class, DateTime.class, Person.class, IRI.class};

    @SuppressWarnings("unchecked")
    public DefaultConventions() {
        super(false);
        for (Class<?> type : annotations)
            setConvention((Class<? extends Annotation>)type);
    }

}
