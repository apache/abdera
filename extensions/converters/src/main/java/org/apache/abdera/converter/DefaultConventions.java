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
package org.apache.abdera.converter;

import java.lang.annotation.Annotation;

import org.apache.abdera.converter.annotation.Accept;
import org.apache.abdera.converter.annotation.Author;
import org.apache.abdera.converter.annotation.BaseURI;
import org.apache.abdera.converter.annotation.Categories;
import org.apache.abdera.converter.annotation.Category;
import org.apache.abdera.converter.annotation.Collection;
import org.apache.abdera.converter.annotation.Content;
import org.apache.abdera.converter.annotation.ContentType;
import org.apache.abdera.converter.annotation.Contributor;
import org.apache.abdera.converter.annotation.Control;
import org.apache.abdera.converter.annotation.Direction;
import org.apache.abdera.converter.annotation.Draft;
import org.apache.abdera.converter.annotation.Edited;
import org.apache.abdera.converter.annotation.Email;
import org.apache.abdera.converter.annotation.Entry;
import org.apache.abdera.converter.annotation.Extension;
import org.apache.abdera.converter.annotation.Generator;
import org.apache.abdera.converter.annotation.Href;
import org.apache.abdera.converter.annotation.HrefLanguage;
import org.apache.abdera.converter.annotation.ID;
import org.apache.abdera.converter.annotation.Icon;
import org.apache.abdera.converter.annotation.Label;
import org.apache.abdera.converter.annotation.Language;
import org.apache.abdera.converter.annotation.Length;
import org.apache.abdera.converter.annotation.Link;
import org.apache.abdera.converter.annotation.Logo;
import org.apache.abdera.converter.annotation.MediaType;
import org.apache.abdera.converter.annotation.Name;
import org.apache.abdera.converter.annotation.Published;
import org.apache.abdera.converter.annotation.Rel;
import org.apache.abdera.converter.annotation.Rights;
import org.apache.abdera.converter.annotation.Scheme;
import org.apache.abdera.converter.annotation.Source;
import org.apache.abdera.converter.annotation.Src;
import org.apache.abdera.converter.annotation.Subtitle;
import org.apache.abdera.converter.annotation.Term;
import org.apache.abdera.converter.annotation.TextType;
import org.apache.abdera.converter.annotation.Title;
import org.apache.abdera.converter.annotation.URI;
import org.apache.abdera.converter.annotation.Updated;
import org.apache.abdera.converter.annotation.Value;
import org.apache.abdera.converter.annotation.Version;
import org.apache.abdera.converter.annotation.Workspace;

public class DefaultConventions
    extends AbstractConventions {

  private static final long serialVersionUID = 6797950641657672805L;

  @SuppressWarnings("unchecked") private static final Class[] annotations = 
   {Accept.class, 
    Author.class, 
    BaseURI.class, 
    Categories.class, 
    Category.class, 
    Collection.class, 
    Content.class, 
    ContentType.class, 
    Contributor.class, 
    Control.class, 
    Direction.class, 
    Draft.class, 
    Edited.class, 
    Email.class, 
    Entry.class, 
    Extension.class, 
    Generator.class, 
    Href.class, 
    HrefLanguage.class, 
    Icon.class, 
    ID.class, 
    Label.class, 
    Language.class, 
    Length.class, 
    Link.class, 
    Logo.class, 
    MediaType.class, 
    Name.class, 
    Published.class, 
    Rel.class, 
    Rights.class, 
    Scheme.class, 
    Source.class, 
    Src.class, 
    Subtitle.class, 
    Term.class, 
    TextType.class, 
    Title.class, 
    Updated.class, 
    URI.class, 
    Value.class, 
    Version.class, 
    Workspace.class
   };
  
  public DefaultConventions() {
    super(false);
    for (Class<Annotation> type : annotations) setConvention(type);
  }
  
}
