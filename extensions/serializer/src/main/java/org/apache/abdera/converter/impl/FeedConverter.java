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
import org.apache.abdera.converter.annotation.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Source;

public class FeedConverter 
  extends SourceConverter {
  
  @Override 
  protected Source create(
    ConversionContext context) {
      return context.getAbdera().newFeed();
  }

  @Override protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context,
    Conventions conventions,
    Source feed, 
    AccessibleObject accessor) {
      
      if (accessor.isAnnotationPresent(Entry.class) || 
          Entry.class.equals(conventions.matchConvention(accessor))) {
        EntryConverter c = new EntryConverter();
        Object val = eval(accessor, source);
        Object[] values = toArray(val);
        for (Object value : values) {
          ObjectContext valueContext = new ObjectContext(value,source,accessor);
          org.apache.abdera.model.Entry entry = c.convert(value, valueContext, context);
          if (entry != null) ((Feed)feed).addEntry(entry);
        }
      }
       
      else super.process(
        source, 
        objectContext, 
        context, 
        conventions, 
        feed, 
        accessor);
      
  }
    
}
