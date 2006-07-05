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
package org.apache.abdera.parser.stax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.abdera.util.ServiceUtil;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;

public class FOMWriterFactory 
  implements WriterFactory {

  public Writer getWriter() {
    return Writer.INSTANCE;
  }

  public Writer getWriter(String name) {
    return (name != null) ? 
      loadWriters().get(name) : getWriter();
  }

  private static Map<String,NamedWriter> writers = null;
  
  public static Map<String,NamedWriter> loadWriters() {
    if (writers == null) {
      List<NamedWriter> _writers = 
        ServiceUtil._loadimpls(
          "META-INF/services/org.apache.abdera.writer.NamedWriter");
      writers = new HashMap<String,NamedWriter>();
      for (NamedWriter writer : _writers) {
        writers.put(writer.getName(), writer);
      }
    }
    return writers;
  }
}
