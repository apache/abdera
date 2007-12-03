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
package org.apache.abdera.protocol;

import java.util.Date;

import org.apache.abdera.util.EntityTag;
import org.apache.abdera.writer.StreamWriter;

/**
 * An EntityProvider is used to serialize entities using the StreamWriter
 * interface.  The EntityProvider interface can be implemented by applications
 * to provide an efficient means of serializing non-FOM objects to Atom/XML. 
 */
public abstract class EntityProvider {

  /**
   * Write to the specified StreamWriter
   */
  public abstract void writeTo(StreamWriter sw);
 
  /**
   * True if the serialization is repeatable. 
   */
  public abstract boolean isRepeatable();
  
  /**
   * Return the mime content type of the serialized entity
   */
  public abstract String getContentType();
  
  /**
   * Return the EntityTag of the entity,
   */
  public EntityTag getEntityTag() { return null; }
  
  /**
   * Return the Last-Modified date of the entity
   */
  public Date getLastModified() { return null; }
  
}
