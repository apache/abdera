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
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;

public class AbderaDataSource 
  implements DataSource {

  private Base base = null;
  
  public AbderaDataSource(Base base) {
    this.base = base;
  }
  
  public String getContentType() {
    String type = null;
    if (base instanceof Document) {
      Document doc = (Document) base;
      if (doc.getContentType() != null) {
        type = doc.getContentType().toString();
      } else {
        if (doc.getRoot() instanceof Feed ||
            doc.getRoot() instanceof Entry) {
          type = "application/atom+xml";
        } else if (doc.getRoot() instanceof Service) {
          type = "application/atomserv+xml";
        } else {
          type = "application/xml";
        }
      }
    } else if (base instanceof Feed || base instanceof Entry) {
      Document doc = ((Element)base).getDocument();
      if (doc != null && doc.getContentType() != null)
        type = doc.getContentType().toString();
      if (type == null)
        type = "application/atom+xml";
    } else if (base instanceof Service) {
      Document doc = ((Element)base).getDocument();
      if (doc != null)
        type = doc.getContentType().toString();
      if (type == null)
        type = "application/atomserv+xml";      
    }
    return (type != null) ? type : "application/xml";
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
