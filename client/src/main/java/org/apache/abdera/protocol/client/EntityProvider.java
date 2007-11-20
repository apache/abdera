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
package org.apache.abdera.protocol.client;

import org.apache.abdera.writer.StreamWriter;

/**
 * An EntityProvider is used to serialize client requests using the StreamWriter
 * interface.  The EntityProvider interface can be implemented by client applications
 * to provide an efficient means of serializing non-FOM objects to Atom/XML. 
 */
public interface EntityProvider {

  /**
   * Write to the specified StreamWriter
   */
  void writeTo(StreamWriter sw);
 
  /**
   * True if the serialization is repeatable. 
   */
  boolean isRepeatable();
  
  /**
   * Return the mime content type of the serialized entity
   */
  String getContentType();
  
}
