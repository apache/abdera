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

import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.util.ServiceUtil;
import org.apache.abdera.writer.StreamWriter;

@SuppressWarnings("unchecked")
public class DefaultSerializationContext 
  extends AbstractSerializationContext {

  private static final long serialVersionUID = 740460842415905883L;

  private final List<SerializerProvider> providers;
  
  public DefaultSerializationContext(StreamWriter streamWriter) {
    super(streamWriter);
    initSerializers();
    providers = loadConverterProviders();
  }
  
  public DefaultSerializationContext(Abdera abdera, StreamWriter streamWriter) {
    super(abdera, streamWriter);
    initSerializers();
    providers = loadConverterProviders();
  }
   
  private void initSerializers() {
    SerializerProvider[] providers = getConverterProviders();
    for (SerializerProvider provider : providers) {
      for (Map.Entry<Class,Serializer> entry : provider) {
        setSerializer(entry.getKey(), entry.getValue());
      }
    }
  }
  
  /**
   * Returns the listing of registered ConverterProvider implementations
   */
  public SerializerProvider[] getConverterProviders() {
    return providers != null ? 
      providers.toArray(
        new SerializerProvider[providers.size()]) : 
          new SerializerProvider[0];
  }
  
  protected static synchronized List<SerializerProvider> loadConverterProviders() {
    List<SerializerProvider> providers =
      ServiceUtil.loadimpls(
        "META-INF/services/org.apache.abdera.converter.ConverterProvider");
    return providers;    
  }
  
}
