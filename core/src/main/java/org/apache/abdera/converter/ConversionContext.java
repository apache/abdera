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

import java.io.Serializable;

import org.apache.abdera.Abdera;

public interface ConversionContext
  extends Cloneable, 
          Serializable {

  Abdera getAbdera();
  
  /**
   * Convert using the appropriate detected converter
   */
  <T>T convert(Object object);
  
  /**
   * Convert using the appropriate detected converter
   */
  <T>T convert(Object object, ObjectContext objectContext);
  
  /**
   * Convert using the specified converter
   */
  <T>T convert(Object object, Converter<T> converter);
  
  /**
   * Convert using the specified converter
   */
  <T>T convert(Object object, ObjectContext objectContext, Converter<T> converter);
  
  /**
   * Specify the appropriate converter for the given object or annotation type
   */
  void setConverter(Class<?> type, Converter<?> converter);
  
  /**
   * Get the appropriate converter for the given object or annotation type
   */
  <T>Converter<T> getConverter(ObjectContext objectContext);
  
  /**
   * True if a converter for the specified object or annotation type has been set
   */
  boolean hasConverter(ObjectContext objectContext);

  Object clone();
}
