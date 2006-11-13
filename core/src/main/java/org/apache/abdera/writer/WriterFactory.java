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
package org.apache.abdera.writer;

/**
 * The WriterFactory is used a acquire instances of alternative 
 * writers registered with Abdera.  
 * @see org.apache.abdera.writer.Writer
 */
public interface WriterFactory {
  
  /**
   * Get the default writer. This is equivalent to calling 
   * abdera.getWriter();
   * @return The default writer
   */
  Writer getWriter();
  
  /**
   * Get the named writer.
   * @param name The name of the writer
   * @return The specified writer
   */
  Writer getWriter(String name);
  
  /**
   * Return a writer capable of outputting the given MIME media type
   * @param mediatype A MIME media type
   * @return A matching writer
   */
  Writer getWriterByMediaType(String mediatype);
}
