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
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;

@SuppressWarnings("unchecked")
public class FOMWriterFactory 
  implements WriterFactory {

  private final Abdera abdera;
  private final Map<String,NamedWriter> writers;
  
  public FOMWriterFactory() {
    this(new Abdera());
  }
  
  public FOMWriterFactory(Abdera abdera) {
    this.abdera = abdera;
    Map<String,NamedWriter>  w = getAbdera().getConfiguration().getNamedWriters();
    writers = (w != null) ? w : new HashMap<String,NamedWriter>();
  }
  
  protected Abdera getAbdera() {
    return abdera;
  }
  
  public <T extends Writer>T getWriter() {
    return (T)getAbdera().getWriter();
  }

  public <T extends Writer>T getWriter(String name) {
    return (T)((name != null) ? 
      getWriters().get(name.toLowerCase()) : getWriter());
  }

  public <T extends Writer>T getWriterByMediaType(String mediatype) {
    Map<String,NamedWriter> writers = getWriters();
    for (NamedWriter writer : writers.values()) {
      if (writer.outputsFormat(mediatype)) return (T)writer;
    }
    return null;
  }
  
  private Map<String,NamedWriter> getWriters() {
    return writers;
  }
  
}
