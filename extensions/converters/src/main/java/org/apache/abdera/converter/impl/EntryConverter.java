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

import org.apache.abdera.converter.BaseConverter;
import org.apache.abdera.converter.Conventions;
import org.apache.abdera.converter.ConversionContext;
import org.apache.abdera.converter.ObjectContext;
import org.apache.abdera.converter.annotation.Author;
import org.apache.abdera.converter.annotation.Category;
import org.apache.abdera.converter.annotation.Content;
import org.apache.abdera.converter.annotation.Contributor;
import org.apache.abdera.converter.annotation.Edited;
import org.apache.abdera.converter.annotation.ID;
import org.apache.abdera.converter.annotation.Link;
import org.apache.abdera.converter.annotation.Published;
import org.apache.abdera.converter.annotation.Rights;
import org.apache.abdera.converter.annotation.Summary;
import org.apache.abdera.converter.annotation.Title;
import org.apache.abdera.converter.annotation.Updated;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.Constants;

public class EntryConverter 
  extends BaseConverter<Entry> {
  
  @Override 
  protected Entry create(
    ConversionContext context) {
      return context.getAbdera().newEntry();
  }

  @Override protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context,
    Conventions conventions,
    Entry entry, 
    AccessibleObject accessor) {
      
      if (accessor.isAnnotationPresent(ID.class) || 
          ID.class.equals(conventions.matchConvention(accessor))) {
        IRIConverter c = new IRIConverter(Constants.ID);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        IRIElement iri = c.convert(value, valueContext, context);
        entry.setIdElement(iri);
      }
      
      else if (accessor.isAnnotationPresent(Title.class) || 
          Title.class.equals(conventions.matchConvention(accessor))) {
        TextConverter c = new TextConverter(Constants.TITLE);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        Text text = c.convert(value, valueContext, context);
        entry.setTitleElement(text);
      }
    
      else if (accessor.isAnnotationPresent(Summary.class) || 
          Summary.class.equals(conventions.matchConvention(accessor))) {
        TextConverter c = new TextConverter(Constants.SUBTITLE);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        Text text = c.convert(value, valueContext, context);
        entry.setSummaryElement(text);
      }
      
      else if (accessor.isAnnotationPresent(Rights.class) || 
          Rights.class.equals(conventions.matchConvention(accessor))) {
        TextConverter c = new TextConverter(Constants.RIGHTS);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        Text text = c.convert(value, valueContext, context);
        entry.setRightsElement(text);
      }
      
      else if (accessor.isAnnotationPresent(Updated.class) || 
          Updated.class.equals(conventions.matchConvention(accessor))) {
        DateTimeConverter c = new DateTimeConverter(Constants.UPDATED);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        DateTime dt = c.convert(value, valueContext, context);
        entry.setUpdatedElement(dt);        
      }

      else if (accessor.isAnnotationPresent(Published.class) || 
          Published.class.equals(conventions.matchConvention(accessor))) {
        DateTimeConverter c = new DateTimeConverter(Constants.PUBLISHED);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        DateTime dt = c.convert(value, valueContext, context);
        entry.setPublishedElement(dt);        
      }
      
      else if (accessor.isAnnotationPresent(Edited.class) || 
          Edited.class.equals(conventions.matchConvention(accessor))) {
        DateTimeConverter c = new DateTimeConverter(Constants.EDITED);
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        DateTime dt = c.convert(value, valueContext, context);
        entry.setEditedElement(dt);        
      }
      
      else if (accessor.isAnnotationPresent(Link.class) || 
          Link.class.equals(conventions.matchConvention(accessor))) {
        LinkConverter c = new LinkConverter();
        Object val = eval(accessor, source);
        Object[] values = toArray(val);
        for (Object value : values) {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          org.apache.abdera.model.Link link = c.convert(value, valueContext, context);
          if (link != null) entry.addLink(link);
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
          if (category != null) entry.addCategory(category);
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
          if (person != null) entry.addAuthor(person);
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
          if (person != null) entry.addContributor(person);
        }
      }
      
      else if (accessor.isAnnotationPresent(Content.class) || 
          Content.class.equals(conventions.matchConvention(accessor))) {
        ContentConverter c = new ContentConverter();
        Object value = eval(accessor, source);
        ObjectContext valueContext = new ObjectContext(value,source,accessor);
        org.apache.abdera.model.Content content = c.convert(value, valueContext, context);
        entry.setContentElement(content);
      }
      
  }
    
}
