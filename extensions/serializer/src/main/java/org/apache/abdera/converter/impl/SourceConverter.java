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
package org.apache.abdera.converter.impl;

import java.lang.reflect.AccessibleObject;

import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.Author;
import org.apache.abdera.converter.annotation.Category;
import org.apache.abdera.converter.annotation.Contributor;
import org.apache.abdera.converter.annotation.Generator;
import org.apache.abdera.converter.annotation.ID;
import org.apache.abdera.converter.annotation.Icon;
import org.apache.abdera.converter.annotation.Link;
import org.apache.abdera.converter.annotation.Logo;
import org.apache.abdera.converter.annotation.Rights;
import org.apache.abdera.converter.annotation.Subtitle;
import org.apache.abdera.converter.annotation.Title;
import org.apache.abdera.converter.annotation.Updated;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Source;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.Constants;

public class SourceConverter 
  extends ExtensionConverter<Source> {
  
  @Override 
  protected Source create(
    ConversionContext context) {
      return context.getAbdera().getFactory().newSource();
  }

  @Override protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context,
    Conventions conventions,
    Source feed, 
    AccessibleObject accessor) {
      
      if (accessor.isAnnotationPresent(ID.class) || 
          ID.class.equals(conventions.matchConvention(accessor))) {
        IRIConverter c = new IRIConverter(Constants.ID);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        IRIElement iri = c.convert(value, valueContext, context);
        feed.setIdElement(iri);
      }
      
      else if (accessor.isAnnotationPresent(Title.class) || 
          Title.class.equals(conventions.matchConvention(accessor))) {
        TextConverter c = new TextConverter(Constants.TITLE);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        Text text = c.convert(value, valueContext, context);
        feed.setTitleElement(text);
      }
    
      else if (accessor.isAnnotationPresent(Subtitle.class) || 
          Subtitle.class.equals(conventions.matchConvention(accessor))) {
        TextConverter c = new TextConverter(Constants.SUBTITLE);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        Text text = c.convert(value, valueContext, context);
        feed.setSubtitleElement(text);
      }
      
      else if (accessor.isAnnotationPresent(Rights.class) || 
          Rights.class.equals(conventions.matchConvention(accessor))) {
        TextConverter c = new TextConverter(Constants.RIGHTS);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        Text text = c.convert(value, valueContext, context);
        feed.setRightsElement(text);
      }
      
      else if (accessor.isAnnotationPresent(Updated.class) || 
          Updated.class.equals(conventions.matchConvention(accessor))) {
        DateTimeConverter c = new DateTimeConverter(Constants.UPDATED);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        DateTime dt = c.convert(value, valueContext, context);
        feed.setUpdatedElement(dt);        
      }
      
      else if (accessor.isAnnotationPresent(Link.class) || 
          Link.class.equals(conventions.matchConvention(accessor))) {
        LinkConverter c = new LinkConverter();
        Object val = eval(accessor, source);
        Object[] values = toArray(val);
        for (Object value : values) {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          org.apache.abdera.model.Link link = c.convert(value, valueContext, context);
          if (link != null) feed.addLink(link);
        }
      }
      
      else if (accessor.isAnnotationPresent(Category.class) || 
          Category.class.equals(conventions.matchConvention(accessor))) {
        CategoryConverter c = new CategoryConverter();
        Object val = eval(accessor, source);
        Object[] values = toArray(val);
        for (Object value : values) {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          org.apache.abdera.model.Category category = c.convert(value, valueContext, context);
          if (category != null) feed.addCategory(category);
        }
      }
      
      else if (accessor.isAnnotationPresent(Author.class) || 
          Author.class.equals(conventions.matchConvention(accessor))) {
        PersonConverter c = new PersonConverter(Constants.AUTHOR);
        Object val = eval(accessor, source);
        Object[] values = toArray(val);
        for (Object value : values) {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          Person person = c.convert(value, valueContext, context);
          if (person != null) feed.addAuthor(person);
        }
      }
      
      else if (accessor.isAnnotationPresent(Contributor.class) || 
          Contributor.class.equals(conventions.matchConvention(accessor))) {
        PersonConverter c = new PersonConverter(Constants.CONTRIBUTOR);
        Object val = eval(accessor, source);
        Object[] values = toArray(val);
        for (Object value : values) {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          Person person = c.convert(value, valueContext, context);
          if (person != null) feed.addContributor(person);
        }
      }
      
      else if (accessor.isAnnotationPresent(Generator.class) || 
          Generator.class.equals(conventions.matchConvention(accessor))) {
        GeneratorConverter c = new GeneratorConverter();
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        org.apache.abdera.model.Generator generator = c.convert(value, valueContext, context);
        feed.setGenerator(generator);
      }
      
      else if (accessor.isAnnotationPresent(Icon.class) || 
          Icon.class.equals(conventions.matchConvention(accessor))) {
        IRIConverter c = new IRIConverter(Constants.ICON);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        IRIElement iri = c.convert(value, valueContext, context);
        feed.setIconElement(iri);
      }
      
      else if (accessor.isAnnotationPresent(Logo.class) || 
          Logo.class.equals(conventions.matchConvention(accessor))) {
        IRIConverter c = new IRIConverter(Constants.LOGO);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        IRIElement iri = c.convert(value, valueContext, context);
        feed.setLogoElement(iri);
      }
      
      else {
        super.process(
          source, 
          objectContext, 
          context, 
          conventions, 
          feed, 
          accessor);
      }
      
  }
    
}
