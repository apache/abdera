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
package org.apache.abdera.protocol.server.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.abdera.Abdera;
import org.apache.abdera.writer.StreamWriter;

/**
 * Abstract base class for creating ResponseContext implementations that use
 * the StreamWriter interface.  Using the StreamWriter to write out documents
 * is significantly faster than using the object model but requires developers
 * to know more about proper Atom syntax
 */
public abstract class StreamWriterResponseContext 
  extends AbstractResponseContext {
  
  private final Abdera abdera;
  private final String sw;
  
  protected StreamWriterResponseContext(
    Abdera abdera) {
      this(abdera,null);
  }

  protected StreamWriterResponseContext(
    Abdera abdera, 
    String sw) {
      this.abdera = abdera;
      this.sw = sw;
  }
  
  protected final Abdera getAbdera() {
    return abdera;
  }
  
  protected StreamWriter newStreamWriter() {
    return sw == null ? 
      abdera.newStreamWriter() : 
      abdera.getWriterFactory().newStreamWriter(sw);
  }
  
  public void writeTo(
    OutputStream out) 
      throws IOException {
    writeTo(new OutputStreamWriter(out,"UTF-8"));
  }
  
  public final void writeTo(
    OutputStream out, 
    org.apache.abdera.writer.Writer writer)
      throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public final void writeTo(
    Writer javaWriter,
    org.apache.abdera.writer.Writer abderaWriter) 
      throws IOException {
    throw new UnsupportedOperationException();
  }
  
}
