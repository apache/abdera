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

import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.util.ServiceUtil;

public class DefaultConversionContext 
  extends AbstractConversionContext {

  private static final long serialVersionUID = 740460842415905883L;

  private final List<ConverterProvider> providers;
  
  public DefaultConversionContext() {
    super();   
    initConverters();
    providers = loadConverterProviders();
  }
  
  public DefaultConversionContext(Abdera abdera) {
    super(abdera);
    initConverters();
    providers = loadConverterProviders();
  }
  
  private void initConverters() {
    ConverterProvider[] providers = getConverterProviders();
    for (ConverterProvider provider : providers) {
      for (Map.Entry<Class<?>,Converter<?>> entry : provider) {
        setConverter(entry.getKey(), entry.getValue());
      }
    }
  }
  
  /**
   * Returns the listing of registered ConverterProvider implementations
   */
  public ConverterProvider[] getConverterProviders() {
    return providers != null ? 
      providers.toArray(
        new ConverterProvider[providers.size()]) : 
          new ConverterProvider[0];
  }
  
  protected static synchronized List<ConverterProvider> loadConverterProviders() {
    List<ConverterProvider> providers =
      ServiceUtil.loadimpls(
        "META-INF/services/org.apache.abdera.converter.ConverterProvider");
    return providers;    
  }
  
}
