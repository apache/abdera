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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;

public class FOMWriter 
  implements org.apache.abdera.writer.Writer {

  public FOMWriter() {}
  
  public FOMWriter(Abdera abdera) {}
  
  public void writeTo(
    Base base, 
    OutputStream out) 
      throws IOException {
        base.writeTo(out);
  }

  public void writeTo(
    Base base, 
    Writer out) 
      throws IOException {
        base.writeTo(out);
  }

  public Object write(
    Base base) 
      throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writeTo(base, out);
    return out.toString();
  }

}
