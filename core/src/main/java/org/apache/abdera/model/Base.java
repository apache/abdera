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
package org.apache.abdera.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.abdera.factory.Factory;

/**
 * The Base interface provides the basis for the Feed Object Model API and 
 * defines the operations common to both the Element and Document APIs.
 * 
 * Classes implementing Base MUST NOT be assumed to be thread safe.  Developers
 * wishing to allow multiple threads to perform concurrent modifications to a
 * Base MUST provide their own synchronization. 
 */
public interface Base 
  extends Cloneable {

  /**
   * Serializes the model component out to the specified stream
   * @param out The java.io.OutputStream to use when serializing the Base. The charset encoding specified for the document will be used
   */
  void writeTo(OutputStream out) throws IOException;
  
  /**
   * Serializes the model component out to the specified writer
   * @param writer The java.io.Writer to use when serializing the Base
   */
  void writeTo(Writer writer) throws IOException;
  
  /**
   * Clone this Base
   */
  Object clone();
  
  /**
   * Get the Factory used to create this Base
   * @return The Factory used to create this Base
   */
  Factory getFactory();
  
  /**
   * Add an XML comment to this Base
   * @param value The text value of the comment
   */
  void addComment(String value);
  
}
