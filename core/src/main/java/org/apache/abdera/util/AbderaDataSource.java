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
package org.apache.abdera.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.abdera.model.Base;

/**
 * Utility implementation of javax.activation.DataSource that wraps Abdera Base
 */
public class AbderaDataSource 
  implements DataSource {

  private Base base = null;
  
  public AbderaDataSource(Base base) {
    this.base = base;
  }
  
  public String getContentType() {
    return MimeTypeHelper.getMimeType(base);
  }

  public InputStream getInputStream() throws IOException {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      base.writeTo(out);
      ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
      return in;
    } catch (IOException io) {
      throw io;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String getName() {
    return "Abdera Data Source::" + base.getClass().getName();
  }

  public OutputStream getOutputStream() throws IOException {
    throw new UnsupportedOperationException();
  }

}
