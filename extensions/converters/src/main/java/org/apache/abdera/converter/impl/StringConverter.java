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

public class StringConverter extends BaseConverter<StringBuffer> {
  
  @Override 
  protected StringBuffer create(
    ConversionContext context) {
      return new StringBuffer();
  }

  @Override 
  protected void finish(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context,
    StringBuffer dest) {
      if (dest.length() == 0) {
        dest.append(source.toString());
      }
  }

  @Override 
  protected void process(
    Object source, 
    ObjectContext objectContext,
    ConversionContext context, 
    Conventions conventions, 
    StringBuffer dest,
    AccessibleObject accessor) {
      
  }
  
  
  
}
