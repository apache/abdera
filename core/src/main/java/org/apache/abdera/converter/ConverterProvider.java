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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ConverterProvider 
  implements Iterable<Map.Entry<Class<?>,Converter<?>>>{

  protected Map<Class<?>,Converter<?>> converters = 
    new HashMap<Class<?>,Converter<?>>();
  
  protected void setConverter(
    Class<?> type, 
    Converter<?> converter) {
      converters.put(type, converter);
  }

  public Iterator<Entry<Class<?>, Converter<?>>> iterator() {
    return converters.entrySet().iterator();
  }
  
}
